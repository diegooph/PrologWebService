BEGIN TRANSACTION;

-- PL-2413
--######################################################################################################################
--######################           ADIÇÃO DE COLUNAS NO RELATÓRIO DE AFERIÇÕES AVULSAS         #########################
--######################################################################################################################

-- Deleta a function antiga
DROP FUNCTION FUNC_RELATORIO_PNEU_AFERICOES_AVULSAS(BIGINT[],DATE,DATE);
-- Recria com as novas colunas
CREATE OR REPLACE FUNCTION FUNC_RELATORIO_PNEU_AFERICOES_AVULSAS(F_COD_UNIDADES BIGINT[], F_DATA_INICIAL DATE, F_DATA_FINAL DATE)
    RETURNS TABLE
            (
                "DATA/HORA AFERIÇÃO"    TEXT,
                "QUEM AFERIU?"          CHARACTER VARYING,
                "UNIDADE ALOCADO"       CHARACTER VARYING,
                "PNEU"                  CHARACTER VARYING,
                "MARCA"                 CHARACTER VARYING,
                "MODELO"                CHARACTER VARYING,
                "MEDIDAS"               TEXT,
                "SULCO INTERNO"         TEXT,
                "SULCO CENTRAL INTERNO" TEXT,
                "SULCO CENTRAL EXTERNO" TEXT,
                "SULCO EXTERNO"         TEXT,
                "MENOR SULCO"           TEXT,
                "PRESSÃO"               TEXT,
                "TIPO DE MEDIÇÃO"       TEXT,
                "VIDA"                  TEXT,
                "DOT"                   CHARACTER VARYING
            )
    LANGUAGE plpgsql
AS
$$
DECLARE
    DATE_FORMAT                   TEXT := 'DD/MM/YYYY HH24:MI';
    PNEU_NUNCA_AFERIDO            TEXT := 'Nunca Aferido';
    PROCESSO_AFERICAO_PNEU_AVULSO TEXT := 'PNEU_AVULSO';
BEGIN
    RETURN QUERY
        SELECT COALESCE(TO_CHAR(A.DATA_HORA AT TIME ZONE TZ_UNIDADE(A.COD_UNIDADE), DATE_FORMAT),
                        PNEU_NUNCA_AFERIDO)                                                               AS ULTIMA_AFERICAO,
               C.NOME,
               U.NOME                                                                                     AS UNIDADE_ALOCADO,
               P.CODIGO_CLIENTE                                                                           AS COD_PNEU,
               MAP.NOME                                                                                   AS NOME_MARCA,
               MP.NOME                                                                                    AS NOME_MODELO,
               ((((DP.LARGURA || '/'::TEXT) || DP.ALTURA) || ' R'::TEXT) || DP.ARO)                       AS MEDIDAS,
               FUNC_PNEU_FORMAT_SULCO(AV.ALTURA_SULCO_INTERNO)                                            AS SULCO_INTERNO,
               FUNC_PNEU_FORMAT_SULCO(AV.ALTURA_SULCO_CENTRAL_INTERNO)                                    AS SULCO_CENTRAL_INTERNO,
               FUNC_PNEU_FORMAT_SULCO(AV.ALTURA_SULCO_CENTRAL_EXTERNO)                                    AS SULCO_CENTRAL_EXTERNO,
               FUNC_PNEU_FORMAT_SULCO(AV.ALTURA_SULCO_EXTERNO)                                            AS SULCO_EXTERNO,
               FUNC_PNEU_FORMAT_SULCO(LEAST(AV.ALTURA_SULCO_EXTERNO, AV.ALTURA_SULCO_CENTRAL_EXTERNO,
                                      AV.ALTURA_SULCO_CENTRAL_INTERNO,
                                      AV.ALTURA_SULCO_INTERNO))                                           AS MENOR_SULCO,
               REPLACE(COALESCE(TRUNC(AV.PSI :: NUMERIC, 1) :: TEXT, '-'), '.', ',') :: TEXT              AS PRESSAO,
               A.TIPO_MEDICAO_COLETADA :: TEXT                                                            AS TIPO_MEDICAO,
               P.VIDA_ATUAL::TEXT                                                                         AS VIDA_ATUAL,
               COALESCE(P.DOT, '-')                                                                       AS DOT
        FROM PNEU P
                 JOIN DIMENSAO_PNEU DP ON DP.CODIGO = P.COD_DIMENSAO
                 JOIN UNIDADE U ON U.CODIGO = P.COD_UNIDADE
                 JOIN MODELO_PNEU MP ON MP.CODIGO = P.COD_MODELO AND MP.COD_EMPRESA = U.COD_EMPRESA
                 JOIN MARCA_PNEU MAP ON MAP.CODIGO = MP.COD_MARCA
                 JOIN AFERICAO_VALORES AV ON AV.COD_PNEU = P.CODIGO
                 JOIN AFERICAO A ON A.CODIGO = AV.COD_AFERICAO
                 JOIN COLABORADOR C ON A.CPF_AFERIDOR = C.CPF
        WHERE P.COD_UNIDADE = ANY (F_COD_UNIDADES)
          AND A.TIPO_PROCESSO_COLETA = PROCESSO_AFERICAO_PNEU_AVULSO
          AND (A.DATA_HORA AT TIME ZONE TZ_UNIDADE(A.COD_UNIDADE))::DATE BETWEEN F_DATA_INICIAL AND F_DATA_FINAL
        ORDER BY U.NOME ASC, ULTIMA_AFERICAO DESC NULLS LAST;
END;
$$;
--######################################################################################################################
--######################################################################################################################

-- PL-1934
--######################################################################################################################
--#####################################           CRIA NOVAS COLUNAS         ###########################################
--######################################################################################################################

-- Adiciona a coluna booleana para bloquear aferições com valores menores
ALTER TABLE AFERICAO_CONFIGURACAO_ALERTA_SULCO
    ADD BLOQUEAR_VALORES_MENORES BOOLEAN NOT NULL DEFAULT FALSE;

-- Adiciona a coluna booleana para bloquear aferições com valores maiores
ALTER TABLE AFERICAO_CONFIGURACAO_ALERTA_SULCO
    ADD BLOQUEAR_VALORES_MAIORES BOOLEAN NOT NULL DEFAULT FALSE;

-- Adiciona a coluna booleana para bloquear aferições com valores menores
ALTER TABLE AFERICAO_CONFIGURACAO_PROLOG
    ADD BLOQUEAR_VALORES_MENORES BOOLEAN NOT NULL DEFAULT FALSE;

-- Adiciona a coluna booleana para bloquear aferições com valores maiores
ALTER TABLE AFERICAO_CONFIGURACAO_PROLOG
    ADD BLOQUEAR_VALORES_MAIORES BOOLEAN NOT NULL DEFAULT FALSE;

--######################################################################################################################
--#########################         ALTERA A VIEW DE CONFIGURAÇÕES DE AFERIÇÃO       ###################################
--######################################################################################################################

-- Dropa a view antiga
DROP VIEW VIEW_AFERICAO_CONFIGURACAO_ALERTA_SULCO;

-- Recria a view com as novas colunas de bloqueio
CREATE OR REPLACE VIEW VIEW_AFERICAO_CONFIGURACAO_ALERTA_SULCO AS
WITH CONFIGURACAO_PROLOG AS (
    SELECT AP.VARIACAO_ACEITA_SULCO_MAIOR_MILIMETROS,
           AP.VARIACAO_ACEITA_SULCO_MENOR_MILIMETROS,
           AP.BLOQUEAR_VALORES_MENORES,
           AP.BLOQUEAR_VALORES_MAIORES
    FROM AFERICAO_CONFIGURACAO_PROLOG AP
)
SELECT U.CODIGO                                                               AS COD_UNIDADE,
       CONFIG.CODIGO                                                          AS COD_CONFIG,
       COALESCE(CONFIG.VARIACAO_ACEITA_SULCO_MENOR_MILIMETROS,
                CP.VARIACAO_ACEITA_SULCO_MENOR_MILIMETROS)                    AS VARIACAO_ACEITA_SULCO_MENOR_MILIMETROS,
       COALESCE(CONFIG.VARIACAO_ACEITA_SULCO_MAIOR_MILIMETROS,
                CP.VARIACAO_ACEITA_SULCO_MAIOR_MILIMETROS)                    AS VARIACAO_ACEITA_SULCO_MAIOR_MILIMETROS,
       F_IF(CONFIG.CODIGO IS NULL, TRUE, FALSE)                               AS USA_DEFAULT_PROLOG,
       COALESCE(CONFIG.BLOQUEAR_VALORES_MENORES, CP.BLOQUEAR_VALORES_MENORES) AS BLOQUEAR_VALORES_MENORES,
       COALESCE(CONFIG.BLOQUEAR_VALORES_MAIORES, CP.BLOQUEAR_VALORES_MAIORES) AS BLOQUEAR_VALORES_MAIORES
FROM UNIDADE U
         LEFT JOIN AFERICAO_CONFIGURACAO_ALERTA_SULCO CONFIG ON U.CODIGO = CONFIG.COD_UNIDADE
         FULL JOIN CONFIGURACAO_PROLOG CP ON TRUE;


--######################################################################################################################
--#################################         ALTERA A FUNCTION DE UPSERT       ##########################################
--######################################################################################################################

-- Dropa a function antiga
DROP FUNCTION FUNC_AFERICAO_UPSERT_CONFIG_ALERTA_SULCO(BIGINT, BIGINT, DOUBLE PRECISION, DOUBLE PRECISION);

-- Recria a function com os parâmetros de bloqueio
CREATE OR REPLACE FUNCTION FUNC_AFERICAO_UPSERT_CONFIG_ALERTA_SULCO(F_CODIGO BIGINT,
                                                                    F_COD_UNIDADE BIGINT,
                                                                    F_VARIACAO_SULCO_MENOR DOUBLE PRECISION,
                                                                    F_VARIACAO_SULCO_MAIOR DOUBLE PRECISION,
                                                                    F_BLOQUEAR_VALORES_MENORES BOOLEAN,
                                                                    F_BLOQUEAR_VALORES_MAIORES BOOLEAN)
    RETURNS BOOLEAN
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    IF F_CODIGO IS NULL
    THEN
        INSERT INTO AFERICAO_CONFIGURACAO_ALERTA_SULCO (COD_UNIDADE,
                                                        VARIACAO_ACEITA_SULCO_MENOR_MILIMETROS,
                                                        VARIACAO_ACEITA_SULCO_MAIOR_MILIMETROS,
                                                        BLOQUEAR_VALORES_MENORES,
                                                        BLOQUEAR_VALORES_MAIORES)
        VALUES (F_COD_UNIDADE,
                F_VARIACAO_SULCO_MENOR,
                F_VARIACAO_SULCO_MAIOR,
                F_BLOQUEAR_VALORES_MENORES,
                F_BLOQUEAR_VALORES_MAIORES);
    ELSE
        UPDATE AFERICAO_CONFIGURACAO_ALERTA_SULCO
        SET VARIACAO_ACEITA_SULCO_MENOR_MILIMETROS = F_VARIACAO_SULCO_MENOR,
            VARIACAO_ACEITA_SULCO_MAIOR_MILIMETROS = F_VARIACAO_SULCO_MAIOR,
            BLOQUEAR_VALORES_MENORES               = F_BLOQUEAR_VALORES_MENORES,
            BLOQUEAR_VALORES_MAIORES               = F_BLOQUEAR_VALORES_MAIORES
        WHERE CODIGO = F_CODIGO;
    END IF;

    -- Validamos se houve alguma inserção ou atualização dos valores.
    IF NOT FOUND
    THEN
        PERFORM THROW_GENERIC_ERROR(FORMAT('Erro ao atualizar configurações da unidade %s', F_COD_UNIDADE));
    END IF;

    RETURN FOUND;
END;
$$;

--######################################################################################################################
--###############         ALTERA A FUNCTION BUSCA DE CONFIGURAÇÕES PARA NOVA AFERIÇÃO AVULSA       #####################
--######################################################################################################################

-- Dropa a function antiga
DROP FUNCTION FUNC_AFERICAO_GET_CONFIGURACOES_NOVA_AFERICAO_AVULSA(BIGINT);

-- Recria a function com as colunas de bloqueio
CREATE OR REPLACE FUNCTION FUNC_AFERICAO_GET_CONFIGURACOES_NOVA_AFERICAO_AVULSA(F_COD_PNEU BIGINT)
    RETURNS TABLE
            (
                SULCO_MINIMO_DESCARTE                  REAL,
                SULCO_MINIMO_RECAPAGEM                 REAL,
                TOLERANCIA_CALIBRAGEM                  REAL,
                TOLERANCIA_INSPECAO                    REAL,
                PERIODO_AFERICAO_SULCO                 INTEGER,
                PERIODO_AFERICAO_PRESSAO               INTEGER,
                VARIACAO_ACEITA_SULCO_MENOR_MILIMETROS DOUBLE PRECISION,
                VARIACAO_ACEITA_SULCO_MAIOR_MILIMETROS DOUBLE PRECISION,
                BLOQUEAR_VALORES_MENORES               BOOLEAN,
                BLOQUEAR_VALORES_MAIORES               BOOLEAN,
                VARIACOES_SULCO_DEFAULT_PROLOG         BOOLEAN
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    F_COD_UNIDADE BIGINT;
BEGIN
    SELECT INTO F_COD_UNIDADE P.COD_UNIDADE
    FROM PNEU P
    WHERE P.CODIGO = F_COD_PNEU;

    RETURN QUERY
        SELECT PRU.SULCO_MINIMO_DESCARTE                                  AS SULCO_MINIMO_DESCARTE,
               PRU.SULCO_MINIMO_RECAPAGEM                                 AS SULCO_MINIMO_RECAPAGEM,
               PRU.TOLERANCIA_INSPECAO                                    AS TOLERANCIA_INSPECAO,
               PRU.TOLERANCIA_CALIBRAGEM                                  AS TOLERANCIA_CALIBRAGEM,
               PRU.PERIODO_AFERICAO_SULCO                                 AS PERIODO_AFERICAO_SULCO,
               PRU.PERIODO_AFERICAO_PRESSAO                               AS PERIODO_AFERICAO_PRESSAO,
               CONFIG_ALERTA_SULCO.VARIACAO_ACEITA_SULCO_MENOR_MILIMETROS AS VARIACAO_ACEITA_SULCO_MENOR_MILIMETROS,
               CONFIG_ALERTA_SULCO.VARIACAO_ACEITA_SULCO_MAIOR_MILIMETROS AS VARIACAO_ACEITA_SULCO_MAIOR_MILIMETROS,
               CONFIG_ALERTA_SULCO.BLOQUEAR_VALORES_MENORES               AS BLOQUEAR_VALORES_MENORES,
               CONFIG_ALERTA_SULCO.BLOQUEAR_VALORES_MAIORES               AS BLOQUEAR_VALORES_MAIORES,
               CONFIG_ALERTA_SULCO.USA_DEFAULT_PROLOG                     AS VARIACOES_SULCO_DEFAULT_PROLOG
        FROM VIEW_AFERICAO_CONFIGURACAO_ALERTA_SULCO CONFIG_ALERTA_SULCO
                 JOIN PNEU_RESTRICAO_UNIDADE PRU
                      ON PRU.COD_UNIDADE = CONFIG_ALERTA_SULCO.COD_UNIDADE
        WHERE CONFIG_ALERTA_SULCO.COD_UNIDADE = F_COD_UNIDADE;
END;
$$;

--######################################################################################################################
--##############         ALTERA A FUNCTION BUSCA DE CONFIGURAÇÕES PARA NOVA AFERIÇÃO DE PLACA       ####################
--######################################################################################################################

-- Dropa a function antiga
DROP FUNCTION FUNC_AFERICAO_GET_CONFIGURACOES_NOVA_AFERICAO_PLACA(TEXT);

-- Recria a function com as colunas de bloqueio
CREATE OR REPLACE FUNCTION FUNC_AFERICAO_GET_CONFIGURACOES_NOVA_AFERICAO_PLACA(F_PLACA_VEICULO TEXT)
    RETURNS TABLE
            (
                SULCO_MINIMO_DESCARTE                  REAL,
                SULCO_MINIMO_RECAPAGEM                 REAL,
                TOLERANCIA_CALIBRAGEM                  REAL,
                TOLERANCIA_INSPECAO                    REAL,
                PERIODO_AFERICAO_SULCO                 INTEGER,
                PERIODO_AFERICAO_PRESSAO               INTEGER,
                PODE_AFERIR_SULCO                      BOOLEAN,
                PODE_AFERIR_PRESSAO                    BOOLEAN,
                PODE_AFERIR_SULCO_PRESSAO              BOOLEAN,
                PODE_AFERIR_ESTEPE                     BOOLEAN,
                VARIACAO_ACEITA_SULCO_MENOR_MILIMETROS DOUBLE PRECISION,
                VARIACAO_ACEITA_SULCO_MAIOR_MILIMETROS DOUBLE PRECISION,
                BLOQUEAR_VALORES_MENORES               BOOLEAN,
                BLOQUEAR_VALORES_MAIORES               BOOLEAN,
                VARIACOES_SULCO_DEFAULT_PROLOG         BOOLEAN
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    F_COD_UNIDADE      BIGINT;
    F_COD_TIPO_VEICULO BIGINT;
BEGIN
    SELECT INTO F_COD_UNIDADE, F_COD_TIPO_VEICULO V.COD_UNIDADE,
                                                  V.COD_TIPO
    FROM VEICULO V
    WHERE V.PLACA = F_PLACA_VEICULO;

    RETURN QUERY
        SELECT PRU.SULCO_MINIMO_DESCARTE,
               PRU.SULCO_MINIMO_RECAPAGEM,
               PRU.TOLERANCIA_INSPECAO,
               PRU.TOLERANCIA_CALIBRAGEM,
               PRU.PERIODO_AFERICAO_SULCO,
               PRU.PERIODO_AFERICAO_PRESSAO,
               CONFIG_PODE_AFERIR.PODE_AFERIR_SULCO,
               CONFIG_PODE_AFERIR.PODE_AFERIR_PRESSAO,
               CONFIG_PODE_AFERIR.PODE_AFERIR_SULCO_PRESSAO,
               CONFIG_PODE_AFERIR.PODE_AFERIR_ESTEPE,
               CONFIG_ALERTA_SULCO.VARIACAO_ACEITA_SULCO_MENOR_MILIMETROS,
               CONFIG_ALERTA_SULCO.VARIACAO_ACEITA_SULCO_MAIOR_MILIMETROS,
               CONFIG_ALERTA_SULCO.BLOQUEAR_VALORES_MENORES,
               CONFIG_ALERTA_SULCO.BLOQUEAR_VALORES_MAIORES,
               CONFIG_ALERTA_SULCO.USA_DEFAULT_PROLOG AS VARIACOES_SULCO_DEFAULT_PROLOG
        FROM FUNC_AFERICAO_GET_CONFIG_TIPO_AFERICAO_VEICULO(F_COD_UNIDADE) AS CONFIG_PODE_AFERIR
                 JOIN VIEW_AFERICAO_CONFIGURACAO_ALERTA_SULCO AS CONFIG_ALERTA_SULCO
                      ON CONFIG_PODE_AFERIR.COD_UNIDADE_CONFIGURACAO = CONFIG_ALERTA_SULCO.COD_UNIDADE
                 JOIN PNEU_RESTRICAO_UNIDADE PRU
                      ON PRU.COD_UNIDADE = CONFIG_PODE_AFERIR.COD_UNIDADE_CONFIGURACAO
        WHERE CONFIG_PODE_AFERIR.COD_UNIDADE_CONFIGURACAO = F_COD_UNIDADE
          AND CONFIG_PODE_AFERIR.COD_TIPO_VEICULO = F_COD_TIPO_VEICULO;
END;
$$;

--######################################################################################################################
--####################         ALTERA A FUNCTION BUSCA DE CONFIGURAÇÕES POR COLABORADOR       ##########################
--######################################################################################################################

-- Dropa a function antiga
DROP FUNCTION FUNC_AFERICAO_GET_CONFIG_ALERTA_COLETA_SULCO(BIGINT);

-- Recria a function com as colunas de bloqueio
CREATE OR REPLACE FUNCTION FUNC_AFERICAO_GET_CONFIG_ALERTA_COLETA_SULCO(F_COD_COLABORADOR BIGINT)
    RETURNS TABLE
            (
                COD_UNIDADE                            BIGINT,
                NOME_UNIDADE                           TEXT,
                CODIGO                                 BIGINT,
                VARIACAO_ACEITA_SULCO_MENOR_MILIMETROS DOUBLE PRECISION,
                VARIACAO_ACEITA_SULCO_MAIOR_MILIMETROS DOUBLE PRECISION,
                BLOQUEAR_VALORES_MENORES               BOOLEAN,
                BLOQUEAR_VALORES_MAIORES               BOOLEAN
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    VARIACAO_MENOR_DEFAULT           DOUBLE PRECISION := (SELECT AP.VARIACAO_ACEITA_SULCO_MENOR_MILIMETROS
                                                          FROM AFERICAO_CONFIGURACAO_PROLOG AP);
    VARIACAO_MAIOR_DEFAULT           DOUBLE PRECISION := (SELECT AP.VARIACAO_ACEITA_SULCO_MAIOR_MILIMETROS
                                                          FROM AFERICAO_CONFIGURACAO_PROLOG AP);
    BLOQUEAR_VALORES_MENORES_DEFAULT BOOLEAN          := (SELECT AP.BLOQUEAR_VALORES_MENORES
                                                          FROM AFERICAO_CONFIGURACAO_PROLOG AP);
    BLOQUEAR_VALORES_MAIORES_DEFAULT BOOLEAN          := (SELECT AP.BLOQUEAR_VALORES_MAIORES
                                                          FROM AFERICAO_CONFIGURACAO_PROLOG AP);
BEGIN
    RETURN QUERY
        WITH UNIDADES_ACESSO AS (
            SELECT DISTINCT ON (F.CODIGO_UNIDADE) F.CODIGO_UNIDADE,
                                                  F.NOME_UNIDADE
            FROM FUNC_COLABORADOR_GET_UNIDADES_ACESSO(F_COD_COLABORADOR) F
        )

        SELECT
            -- Precisamos utilizar o código de unidade de UA pois a unidade pode não possuir config criada e ainda
            -- assim precisamos retornar o código dela.
            UA.CODIGO_UNIDADE,
            UA.NOME_UNIDADE,
            CONFIG.CODIGO,
            COALESCE(CONFIG.VARIACAO_ACEITA_SULCO_MENOR_MILIMETROS, VARIACAO_MENOR_DEFAULT),
            COALESCE(CONFIG.VARIACAO_ACEITA_SULCO_MAIOR_MILIMETROS, VARIACAO_MAIOR_DEFAULT),
            COALESCE(CONFIG.BLOQUEAR_VALORES_MENORES, BLOQUEAR_VALORES_MENORES_DEFAULT),
            COALESCE(CONFIG.BLOQUEAR_VALORES_MAIORES, BLOQUEAR_VALORES_MAIORES_DEFAULT)
        FROM UNIDADES_ACESSO UA
                 LEFT JOIN AFERICAO_CONFIGURACAO_ALERTA_SULCO CONFIG
                           ON UA.CODIGO_UNIDADE = CONFIG.COD_UNIDADE
        ORDER BY UA.NOME_UNIDADE;
END;
$$;
--######################################################################################################################
--######################################################################################################################
END TRANSACTION;