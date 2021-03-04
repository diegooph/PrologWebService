BEGIN TRANSACTION;

--######################################################################################################################
--######################################################################################################################
--########## FUNC QUE RETORNA OS DADOS NECESSÁRIOS PARA A CONSTRUÇÃO DO RELATÓRIO DE FAROL DE AFERIÇÕES ################
--######################################################################################################################
--######################################################################################################################
--PL-2268
CREATE OR REPLACE FUNCTION FUNC_PNEU_RELATORIO_FAROL_AFERICAO(F_COD_UNIDADES BIGINT[], F_DATA_INICIAL DATE, F_DATA_FINAL DATE)
    RETURNS TABLE
            (
                "UNIDADE"                             TEXT,
                "QTD DE FROTAS"                       TEXT,
                "QTD DE PNEUS"                        TEXT,
                "QTD DE PNEUS AFERIDOS - PRESSÃO"     TEXT,
                "PERCENTUAL PNEUS AFERIDOS - PRESSÃO" TEXT,
                "QTD DE PNEUS AFERIDOS - SULCO"       TEXT,
                "PERCENTUAL PNEUS AFERIDOS - SULCO"   TEXT
            )
    LANGUAGE SQL
AS
$$
WITH FAROL_AFERICAO AS (
    SELECT U.NOME                                                AS NOME_UNIDADE,
           COUNT(DISTINCT V.PLACA)                               AS QTD_VEICULOS,
           COUNT(DISTINCT VP.*)                                  AS QTD_PNEUS,
           COUNT(DISTINCT VP.COD_PNEU) FILTER (
               WHERE A.TIPO_MEDICAO_COLETADA = 'PRESSAO'
                   OR A.TIPO_MEDICAO_COLETADA = 'SULCO_PRESSAO') AS TOTAL_PRESSAO,
           COUNT(DISTINCT VP.COD_PNEU) FILTER (
               WHERE A.TIPO_MEDICAO_COLETADA = 'SULCO'
                   OR A.TIPO_MEDICAO_COLETADA = 'SULCO_PRESSAO') AS TOTAL_SULCO
    FROM VEICULO_DATA V
             JOIN UNIDADE U ON V.COD_UNIDADE = U.CODIGO
             JOIN VEICULO_PNEU VP ON V.PLACA = VP.PLACA AND V.COD_UNIDADE = VP.COD_UNIDADE
             LEFT JOIN AFERICAO_VALORES AV ON AV.COD_PNEU = VP.COD_PNEU
             LEFT JOIN AFERICAO A ON V.PLACA = A.PLACA_VEICULO AND A.CODIGO = AV.COD_AFERICAO
    WHERE V.COD_UNIDADE = ANY (F_COD_UNIDADES)
      AND (A.DATA_HORA AT TIME ZONE TZ_UNIDADE(A.COD_UNIDADE))::DATE >= (F_DATA_INICIAL)
      AND (A.DATA_HORA AT TIME ZONE TZ_UNIDADE(A.COD_UNIDADE))::DATE <= (F_DATA_FINAL)
    GROUP BY V.COD_UNIDADE, U.NOME
)
SELECT NOME_UNIDADE :: TEXT,
       QTD_VEICULOS :: TEXT,
       QTD_PNEUS :: TEXT,
       TOTAL_PRESSAO :: TEXT,
       COALESCE_PERCENTAGE(TOTAL_PRESSAO, QTD_PNEUS) :: TEXT AS PERCENTUAL_PRESSAO,
       TOTAL_SULCO :: TEXT,
       COALESCE_PERCENTAGE(TOTAL_SULCO, QTD_PNEUS) :: TEXT   AS PERCENTUAL_SULCO
FROM FAROL_AFERICAO
ORDER BY (TOTAL_PRESSAO :: REAL / NULLIF(QTD_PNEUS, 0) :: REAL) ASC NULLS LAST;
$$;
--######################################################################################################################
--######################################################################################################################


--######################################################################################################################
--######################################################################################################################
-- PL-2232
DROP TABLE META_UNIDADE;
COMMENT ON COLUMN UNIDADE_METAS.META_RAIO_TRACKING IS 'A meta de raio do tracking, um valor em KM.';
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--######################## ESTRUTURA PARA A CONFIGURAÇÃO DE RESTRIÇÕES DE ABERTURA DE SERVIÇOS #########################
--######################################################################################################################
--######################################################################################################################
-- PL-1989 / PL-2011

-- ATUALIZA TODAS AS UNIDADES PARA QUE TENHAM DATA DE CADASTRO.
UPDATE UNIDADE SET DATA_HORA_CADASTRO = '2019-01-01 10:00:00'
WHERE DATA_HORA_CADASTRO IS NULL;
ALTER TABLE UNIDADE ALTER COLUMN DATA_HORA_CADASTRO SET NOT NULL;

ALTER TABLE PNEU_RESTRICAO_UNIDADE
	ADD CODIGO BIGSERIAL NOT NULL;

ALTER TABLE PNEU_RESTRICAO_UNIDADE DROP CONSTRAINT PK_EMPRESA_RESTRICAO;

ALTER TABLE PNEU_RESTRICAO_UNIDADE
	ADD CONSTRAINT PK_PNEU_RESTRICAO_UNIDADE
		PRIMARY KEY (CODIGO);

ALTER TABLE PNEU_RESTRICAO_UNIDADE
	ADD CONSTRAINT UNIQUE_RESTRICAO_UNIDADE_EMPRESA
		UNIQUE (COD_EMPRESA, COD_UNIDADE);

ALTER TABLE PNEU_RESTRICAO_UNIDADE
	ADD COD_COLABORADOR_ULTIMA_ATUALIZACAO BIGINT;

ALTER TABLE PNEU_RESTRICAO_UNIDADE
	ADD DATA_HORA_ULTIMA_ATUALIZACAO TIMESTAMP WITH TIME ZONE;

ALTER TABLE PNEU_RESTRICAO_UNIDADE
	ADD CONSTRAINT FK_PNEU_RESTRICAO_UNIDADE_COLABORADOR_CODIGO
		FOREIGN KEY (COD_COLABORADOR_ULTIMA_ATUALIZACAO) REFERENCES COLABORADOR_DATA (CODIGO);

-- ATUALIZA TODAS AS CONFIGURAÇÕES DE RESTRIÇÃO PARA QUE TENHAM DATA DE ATUALIZAÇÃO.
UPDATE PNEU_RESTRICAO_UNIDADE PRU
SET DATA_HORA_ULTIMA_ATUALIZACAO =
    (SELECT U.DATA_HORA_CADASTRO
    FROM UNIDADE U
    WHERE U.CODIGO = PRU.COD_UNIDADE)
WHERE DATA_HORA_ULTIMA_ATUALIZACAO IS NULL;

ALTER TABLE PNEU_RESTRICAO_UNIDADE ALTER COLUMN SULCO_MINIMO_RECAPAGEM SET NOT NULL;

ALTER TABLE PNEU_RESTRICAO_UNIDADE ALTER COLUMN SULCO_MINIMO_DESCARTE SET NOT NULL;

ALTER TABLE PNEU_RESTRICAO_UNIDADE ALTER COLUMN TOLERANCIA_INSPECAO SET NOT NULL;

ALTER TABLE PNEU_RESTRICAO_UNIDADE ALTER COLUMN PERIODO_AFERICAO_PRESSAO SET NOT NULL;

ALTER TABLE PNEU_RESTRICAO_UNIDADE ALTER COLUMN PERIODO_AFERICAO_SULCO SET NOT NULL;

ALTER TABLE PNEU_RESTRICAO_UNIDADE ALTER COLUMN DATA_HORA_ULTIMA_ATUALIZACAO SET NOT NULL;

CREATE TABLE PNEU_RESTRICAO_UNIDADE_HISTORICO
(
    CODIGO                     BIGSERIAL                NOT NULL
        CONSTRAINT PK_PNEU_RESTRICAO_UNIDADE_HISTORICO
            PRIMARY KEY,
    COD_RESTRICAO_UNIDADE_PNEU BIGINT                   NOT NULL
        CONSTRAINT FK_PNEU_RESTRICAO_UNIDADE
            REFERENCES PNEU_RESTRICAO_UNIDADE(CODIGO),
    COD_EMPRESA                BIGINT                   NOT NULL
        CONSTRAINT FK_PNEU_RESTRICAO_UNIDADE_EMPRESA
            REFERENCES EMPRESA(CODIGO),
    COD_UNIDADE                BIGINT                   NOT NULL
        CONSTRAINT FK_PNEU_RESTRICAO_UNIDADE_UNIDADE
            REFERENCES UNIDADE(CODIGO),
    COD_COLABORADOR            BIGINT
        CONSTRAINT FK_PNEU_RESTRICAO_UNIDADE_COLABORADOR
            REFERENCES COLABORADOR_DATA (CODIGO),
    DATA_HORA_ALTERACAO        TIMESTAMP WITH TIME ZONE NOT NULL,
    TOLERANCIA_CALIBRAGEM      REAL                     NOT NULL,
    TOLERANCIA_INSPECAO        REAL                     NOT NULL,
    SULCO_MINIMO_RECAPAGEM     REAL                     NOT NULL,
    SULCO_MINIMO_DESCARTE      REAL                     NOT NULL,
    PERIODO_AFERICAO_PRESSAO   INTEGER                  NOT NULL,
    PERIODO_AFERICAO_SULCO     INTEGER                  NOT NULL
);

CREATE OR REPLACE FUNCTION FUNC_AFERICAO_UPSERT_CONFIGURACAO_CRONOGRAMA_SERVICO(F_CODIGO_EMPRESA BIGINT,
                                                                                F_CODIGO_UNIDADE BIGINT,
                                                                                F_TOLERANCIA_CALIBRAGEM NUMERIC,
                                                                                F_TOLERANCIA_INSPECAO NUMERIC,
                                                                                F_SULCO_MINIMO_RECAPAGEM NUMERIC,
                                                                                F_SULCO_MINIMO_DESCARTE NUMERIC,
                                                                                F_PERIODO_AFERICAO_PRESSAO INTEGER,
                                                                                F_PERIODO_AFERICAO_SULCO INTEGER,
                                                                                F_COD_COLABORADOR BIGINT,
                                                                                F_DATA_HORA_ATUAL_UTC TIMESTAMP WITH TIME ZONE)
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    CODIGO_CONFIG BIGINT;
    OLD_COD_COLABORADOR_ULTIMA_ATUALIZACAO BIGINT;
    OLD_DATA_HORA_ULTIMA_ATUALIZACAO TIMESTAMP WITH TIME ZONE;
    OLD_TOLERANCIA_CALIBRAGEM NUMERIC;
    OLD_TOLERANCIA_INSPECAO NUMERIC;
    OLD_SULCO_MINIMO_RECAPAGEM NUMERIC;
    OLD_SULCO_MINIMO_DESCARTE NUMERIC;
    OLD_PERIODO_AFERICAO_PRESSAO INTEGER;
    OLD_PERIODO_AFERICAO_SULCO INTEGER;
BEGIN
    -- BUSCA E ARMAZENA OS DADOS ANTIGOS
    SELECT CODIGO,
          COD_COLABORADOR_ULTIMA_ATUALIZACAO,
          DATA_HORA_ULTIMA_ATUALIZACAO,
          TOLERANCIA_CALIBRAGEM,
          TOLERANCIA_INSPECAO,
          SULCO_MINIMO_RECAPAGEM,
          SULCO_MINIMO_DESCARTE,
          PERIODO_AFERICAO_PRESSAO,
          PERIODO_AFERICAO_SULCO
    FROM PNEU_RESTRICAO_UNIDADE
    WHERE COD_EMPRESA = F_CODIGO_EMPRESA
     AND COD_UNIDADE = F_CODIGO_UNIDADE
    INTO CODIGO_CONFIG, OLD_COD_COLABORADOR_ULTIMA_ATUALIZACAO, OLD_DATA_HORA_ULTIMA_ATUALIZACAO,
        OLD_TOLERANCIA_CALIBRAGEM,OLD_TOLERANCIA_INSPECAO, OLD_SULCO_MINIMO_RECAPAGEM, OLD_SULCO_MINIMO_DESCARTE,
        OLD_PERIODO_AFERICAO_PRESSAO, OLD_PERIODO_AFERICAO_SULCO;

    -- CASO A CONFIG EXISTA, VERIFICA SE HOUVE MUDANÇAS, ATUALIZA E SALVA O HISTÓRICO.
    IF CODIGO_CONFIG > 0 THEN
        -- VERIFICA SE HOUVE MUDANÇAS
        IF  OLD_TOLERANCIA_CALIBRAGEM != F_TOLERANCIA_CALIBRAGEM OR
            OLD_TOLERANCIA_INSPECAO != F_TOLERANCIA_INSPECAO OR
            OLD_SULCO_MINIMO_RECAPAGEM != F_SULCO_MINIMO_RECAPAGEM OR
            OLD_SULCO_MINIMO_DESCARTE != F_SULCO_MINIMO_DESCARTE OR
            OLD_PERIODO_AFERICAO_PRESSAO != F_PERIODO_AFERICAO_PRESSAO OR
            OLD_PERIODO_AFERICAO_SULCO != F_PERIODO_AFERICAO_SULCO THEN
            -- ATUALIZA.
            UPDATE PNEU_RESTRICAO_UNIDADE
            SET TOLERANCIA_CALIBRAGEM              = F_TOLERANCIA_CALIBRAGEM,
                COD_COLABORADOR_ULTIMA_ATUALIZACAO = F_COD_COLABORADOR,
                DATA_HORA_ULTIMA_ATUALIZACAO       = F_DATA_HORA_ATUAL_UTC,
                TOLERANCIA_INSPECAO                = F_TOLERANCIA_INSPECAO,
                SULCO_MINIMO_RECAPAGEM             = F_SULCO_MINIMO_RECAPAGEM,
                SULCO_MINIMO_DESCARTE              = F_SULCO_MINIMO_DESCARTE,
                PERIODO_AFERICAO_PRESSAO           = F_PERIODO_AFERICAO_PRESSAO,
                PERIODO_AFERICAO_SULCO             = F_PERIODO_AFERICAO_SULCO
            WHERE COD_EMPRESA = F_CODIGO_EMPRESA
              AND COD_UNIDADE = F_CODIGO_UNIDADE;

            -- SALVA O HISTÓRICO.
            INSERT INTO PNEU_RESTRICAO_UNIDADE_HISTORICO (
                                        COD_RESTRICAO_UNIDADE_PNEU,
                                        COD_EMPRESA,
                                        COD_UNIDADE,
                                        COD_COLABORADOR,
                                        DATA_HORA_ALTERACAO,
                                        TOLERANCIA_CALIBRAGEM,
                                        TOLERANCIA_INSPECAO,
                                        SULCO_MINIMO_RECAPAGEM,
                                        SULCO_MINIMO_DESCARTE,
                                        PERIODO_AFERICAO_PRESSAO,
                                        PERIODO_AFERICAO_SULCO)
            VALUES (CODIGO_CONFIG,
                    F_CODIGO_EMPRESA,
                    F_CODIGO_UNIDADE,
                    OLD_COD_COLABORADOR_ULTIMA_ATUALIZACAO,
                    OLD_DATA_HORA_ULTIMA_ATUALIZACAO,
                    OLD_TOLERANCIA_CALIBRAGEM,
                    OLD_TOLERANCIA_INSPECAO,
                    OLD_SULCO_MINIMO_RECAPAGEM,
                    OLD_SULCO_MINIMO_DESCARTE,
                    OLD_PERIODO_AFERICAO_PRESSAO,
                    OLD_PERIODO_AFERICAO_SULCO);
        END IF;
    -- SE NÃO EXISTIR.
    ELSE
        -- INSERE A CONFIG.
        INSERT INTO PNEU_RESTRICAO_UNIDADE (COD_EMPRESA,
                                            COD_UNIDADE,
                                            COD_COLABORADOR_ULTIMA_ATUALIZACAO,
                                            DATA_HORA_ULTIMA_ATUALIZACAO,
                                            TOLERANCIA_CALIBRAGEM,
                                            TOLERANCIA_INSPECAO,
                                            SULCO_MINIMO_RECAPAGEM,
                                            SULCO_MINIMO_DESCARTE,
                                            PERIODO_AFERICAO_PRESSAO,
                                            PERIODO_AFERICAO_SULCO)
        VALUES (F_CODIGO_EMPRESA,
                F_CODIGO_UNIDADE,
                F_COD_COLABORADOR,
                F_DATA_HORA_ATUAL_UTC,
                F_TOLERANCIA_CALIBRAGEM,
                F_TOLERANCIA_INSPECAO,
                F_SULCO_MINIMO_RECAPAGEM,
                F_SULCO_MINIMO_DESCARTE,
                F_PERIODO_AFERICAO_PRESSAO,
                F_PERIODO_AFERICAO_SULCO);
    END IF;
END;
$$;

CREATE OR REPLACE FUNCTION FUNC_AFERICAO_GET_CONFIGURACAO_CRONOGRAMA_SERVICO_BY_COLABORADOR(F_COD_COLABORADOR BIGINT)
    RETURNS TABLE
            (
                CODIGO                             BIGINT,
                CODIGO_EMPRESA                     BIGINT,
                CODIGO_REGIONAL                    BIGINT,
                NOME_REGIONAL                      TEXT,
                CODIGO_UNIDADE                     BIGINT,
                NOME_UNIDADE                       TEXT,
                COD_COLABORADOR_ULTIMA_ATUALIZACAO BIGINT,
                DATA_HORA_ULTIMA_ATUALIZACAO       TIMESTAMP WITHOUT TIME ZONE,
                TOLERANCIA_CALIBRAGEM              REAL,
                TOLERANCIA_INSPECAO                REAL,
                SULCO_MINIMO_RECAPAGEM             REAL,
                SULCO_MINIMO_DESCARTE              REAL,
                PERIODO_AFERICAO_PRESSAO           INTEGER,
                PERIODO_AFERICAO_SULCO             INTEGER
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
BEGIN
    RETURN QUERY
        WITH UNIDADES_ACESSO AS (
            SELECT DISTINCT ON (F.CODIGO_UNIDADE) F.CODIGO_UNIDADE,
                                                  F.NOME_UNIDADE,
                                                  F.CODIGO_EMPRESA,
                                                  F.CODIGO_REGIONAL,
                                                  F.NOME_REGIONAL
            FROM FUNC_COLABORADOR_GET_UNIDADES_ACESSO(F_COD_COLABORADOR) F
        )
        SELECT PRU.CODIGO                                                                AS CODIGO,
               UA.CODIGO_EMPRESA                                                         AS CODIGO_EMPRESA,
               UA.CODIGO_REGIONAL                                                        AS CODIGO_REGIONAL,
               UA.NOME_REGIONAL                                                          AS NOME_REGIONAL,
               UA.CODIGO_UNIDADE                                                         AS CODIGO_UNIDADE,
               UA.NOME_UNIDADE                                                           AS NOME_UNIDADE,
               PRU.COD_COLABORADOR_ULTIMA_ATUALIZACAO                                    AS COD_COLABORADOR_ULTIMA_ATUALIZACAO,
               PRU.DATA_HORA_ULTIMA_ATUALIZACAO AT TIME ZONE TZ_UNIDADE(PRU.COD_UNIDADE) AS DATA_HORA_ULTIMA_ATUALIZACAO,
               PRU.TOLERANCIA_CALIBRAGEM                                                 AS TOLERANCIA_CALIBRAGEM,
               PRU.TOLERANCIA_INSPECAO                                                   AS TOLERANCIA_INSPECAO,
               PRU.SULCO_MINIMO_RECAPAGEM                                                AS SULCO_MINIMO_RECAPAGEM,
               PRU.SULCO_MINIMO_DESCARTE                                                 AS SULCO_MINIMO_DESCARTE,
               PRU.PERIODO_AFERICAO_PRESSAO                                              AS PERIODO_AFERICAO_PRESSAO,
               PRU.PERIODO_AFERICAO_SULCO                                                AS PERIODO_AFERICAO_SULCO
        FROM UNIDADES_ACESSO UA
                 LEFT JOIN PNEU_RESTRICAO_UNIDADE PRU ON UA.CODIGO_UNIDADE = PRU.COD_UNIDADE
        ORDER BY UA.NOME_REGIONAL ASC, UA.NOME_UNIDADE ASC;
END;
$$;

CREATE OR REPLACE FUNCTION FUNC_AFERICAO_GET_CONFIGURACAO_CRONOGRAMA_SERVICO_HISTORICO(F_COD_RESTRICAO_UNIDADE_PNEU BIGINT)
    RETURNS TABLE
            (
                NOME_UNIDADE             TEXT,
                NOME_COLABORADOR         TEXT,
                DATA_HORA_ALTERACAO      TIMESTAMP WITHOUT TIME ZONE,
                TOLERANCIA_CALIBRAGEM    REAL,
                TOLERANCIA_INSPECAO      REAL,
                SULCO_MINIMO_RECAPAGEM   REAL,
                SULCO_MINIMO_DESCARTE    REAL,
                PERIODO_AFERICAO_PRESSAO INTEGER,
                PERIODO_AFERICAO_SULCO   INTEGER,
                ATUAL                    BOOLEAN
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
BEGIN
    RETURN QUERY
        SELECT U.NOME :: TEXT                                                            AS NOME_UNIDADE,
               F_IF(CD.NOME IS NULL, 'Cadastrado pelo Prolog', CD.NOME) :: TEXT          AS NOME_COLABORADOR,
               PRU.DATA_HORA_ULTIMA_ATUALIZACAO AT TIME ZONE TZ_UNIDADE(PRU.COD_UNIDADE) AS DATA_HORA_ALTERACAO,
               PRU.TOLERANCIA_CALIBRAGEM                                                 AS TOLERANCIA_CALIBRAGEM,
               PRU.TOLERANCIA_INSPECAO                                                   AS TOLERANCIA_INSPECAO,
               PRU.SULCO_MINIMO_RECAPAGEM                                                AS SULCO_MINIMO_RECAPAGEM,
               PRU.SULCO_MINIMO_DESCARTE                                                 AS SULCO_MINIMO_DESCARTE,
               PRU.PERIODO_AFERICAO_PRESSAO                                              AS PERIODO_AFERICAO_PRESSAO,
               PRU.PERIODO_AFERICAO_SULCO                                                AS PERIODO_AFERICAO_SULCO,
               TRUE                                                                      AS ATUAL
        FROM PNEU_RESTRICAO_UNIDADE PRU
                 JOIN UNIDADE U ON PRU.COD_UNIDADE = U.CODIGO
                 LEFT JOIN COLABORADOR CD ON PRU.COD_COLABORADOR_ULTIMA_ATUALIZACAO = CD.CODIGO
        WHERE PRU.CODIGO = F_COD_RESTRICAO_UNIDADE_PNEU
        UNION ALL
        SELECT U.NOME :: TEXT                                                     AS NOME_UNIDADE,
               F_IF(CD.NOME IS NULL, 'Cadastrado pelo Prolog', CD.NOME) :: TEXT   AS NOME_COLABORADOR,
               PRUH.DATA_HORA_ALTERACAO AT TIME ZONE TZ_UNIDADE(PRUH.COD_UNIDADE) AS DATA_HORA_ALTERACAO,
               PRUH.TOLERANCIA_CALIBRAGEM                                         AS TOLERANCIA_CALIBRAGEM,
               PRUH.TOLERANCIA_INSPECAO                                           AS TOLERANCIA_INSPECAO,
               PRUH.SULCO_MINIMO_RECAPAGEM                                        AS SULCO_MINIMO_RECAPAGEM,
               PRUH.SULCO_MINIMO_DESCARTE                                         AS SULCO_MINIMO_DESCARTE,
               PRUH.PERIODO_AFERICAO_PRESSAO                                      AS PERIODO_AFERICAO_PRESSAO,
               PRUH.PERIODO_AFERICAO_SULCO                                        AS PERIODO_AFERICAO_SULCO,
               FALSE                                                              AS ATUAL
        FROM PNEU_RESTRICAO_UNIDADE_HISTORICO PRUH
                 JOIN UNIDADE U ON PRUH.COD_UNIDADE = U.CODIGO
                 LEFT JOIN COLABORADOR CD ON PRUH.COD_COLABORADOR = CD.CODIGO
        WHERE PRUH.COD_RESTRICAO_UNIDADE_PNEU = F_COD_RESTRICAO_UNIDADE_PNEU
        ORDER BY ATUAL DESC, DATA_HORA_ALTERACAO DESC;
END;
$$;

--######################################################################################################################
--######################################################################################################################


--######################################################################################################################
--######################################################################################################################
-- PL-2295.

--
-- TABELA PNEU_SERVICO_REALIZADO.
-- Renomeia coluna.
ALTER TABLE PNEU_SERVICO_REALIZADO
    RENAME COLUMN COD_PNEU_TIPO_SERVICO TO COD_TIPO_SERVICO;
--

--
-- TABELA PNEU_SERVICO_CADASTRO.
-- Renomeia coluna.
ALTER TABLE PNEU_SERVICO_CADASTRO
    RENAME COLUMN COD_PNEU_SERVICO_REALIZADO TO COD_SERVICO_REALIZADO;
--

--
-- TABELA PNEU_SERVICO_REALIZADO_INCREMENTA_VIDA.
-- Renomeia coluna.
ALTER TABLE PNEU_SERVICO_REALIZADO_INCREMENTA_VIDA
    RENAME COLUMN COD_PNEU_SERVICO_REALIZADO TO COD_SERVICO_REALIZADO;

-- Melhora nome de constraint.
ALTER TABLE PNEU_SERVICO_REALIZADO_INCREMENTA_VIDA
    DROP CONSTRAINT FK_PNEU_SERVICO_REALIZADO_INCREMENTA_VIDA_PNEU_SERVICO_REALIZAD;
ALTER TABLE PNEU_SERVICO_REALIZADO_INCREMENTA_VIDA
    ADD CONSTRAINT FK_SERVICO_REALIZADO_INCREMENTA_VIDA_PNEU
        FOREIGN KEY (COD_SERVICO_REALIZADO, FONTE_SERVICO_REALIZADO)
            REFERENCES PNEU_SERVICO_REALIZADO (CODIGO, FONTE_SERVICO_REALIZADO);
--

--
-- TABELA MOVIMENTACAO_PNEU_SERVICO_REALIZADO.
-- Renomeia coluna.
ALTER TABLE MOVIMENTACAO_PNEU_SERVICO_REALIZADO
    RENAME COLUMN COD_PNEU_SERVICO_REALIZADO TO COD_SERVICO_REALIZADO;
--

--
-- TABELA MOVIMENTACAO_PNEU_SERVICO_REALIZADO_RECAPADORA.
-- Renomeia coluna.
ALTER TABLE MOVIMENTACAO_PNEU_SERVICO_REALIZADO_RECAPADORA
    RENAME COLUMN COD_PNEU_SERVICO_REALIZADO TO COD_SERVICO_REALIZADO_MOVIMENTACAO;

-- Melhora nome de constraint.
ALTER TABLE MOVIMENTACAO_PNEU_SERVICO_REALIZADO_RECAPADORA
    DROP CONSTRAINT FK_MOVIMENTACAO_PNEU_SERVICO_REALIZADO_RECAPADORA_MOVIMENTACAO_;
ALTER TABLE MOVIMENTACAO_PNEU_SERVICO_REALIZADO_RECAPADORA
    ADD CONSTRAINT FK_SERVICO_REALIZADO_RECAPADORA_MOVIMENTACAO_SERVICO
        FOREIGN KEY (COD_MOVIMENTACAO, COD_SERVICO_REALIZADO_MOVIMENTACAO)
            REFERENCES MOVIMENTACAO_PNEU_SERVICO_REALIZADO (COD_MOVIMENTACAO, COD_SERVICO_REALIZADO);
--

--
-- Altera view (nome da coluna foi alterado no JOIN).
create or replace view pneu_valor_vida as
  SELECT srr.cod_unidade,
    srr.cod_pneu,
    srrec.cod_modelo_banda,
    srrec.vida_nova_pneu AS vida,
    srr.custo AS valor
   FROM (pneu_servico_realizado srr
     JOIN pneu_servico_realizado_incrementa_vida srrec
         ON ((srr.codigo = srrec.cod_servico_realizado)));

comment on view pneu_valor_vida
is 'View que contém o valor e a vida associados a um pneu, somente para pneus que já foram recapados.';
--

--
-- Colunas renomeadas na function.
CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_PNEU_REALIZA_INCREMENTO_VIDA_CADASTRO(
  F_COD_UNIDADE_PNEU      BIGINT,
  F_COD_PNEU_PROLOG       BIGINT,
  F_COD_MODELO_BANDA_PNEU BIGINT,
  F_VALOR_BANDA_PNEU      REAL,
  F_VIDA_NOVA_PNEU        INTEGER)
  RETURNS BOOLEAN
LANGUAGE PLPGSQL
AS $$
DECLARE
  COD_TIPO_SERVICO_INCREMENTA_VIDA_CADASTRO BIGINT := (SELECT PTS.CODIGO
                                                       FROM PNEU_TIPO_SERVICO AS PTS
                                                       WHERE PTS.COD_EMPRESA IS NULL
                                                             AND PTS.STATUS_ATIVO = TRUE
                                                             AND PTS.INCREMENTA_VIDA = TRUE
                                                             AND PTS.UTILIZADO_CADASTRO_PNEU = TRUE);
  FONTE_SERVICO_REALIZADO_CADASTRO TEXT := 'FONTE_CADASTRO';
  VIDA_MOMENTO_SERVICO_REALIZADO INTEGER := F_VIDA_NOVA_PNEU - 1;
  IS_PRIMEIRA_VIDA BOOLEAN := F_VIDA_NOVA_PNEU < 2;
  F_COD_SERVICO_REALIZADO BIGINT;
  F_QTD_ROWS_ALTERADAS BIGINT;
BEGIN
  -- Validamos que o pneu pode receber o serviço de incremento de vida.
  IF (IS_PRIMEIRA_VIDA)
  THEN
    PERFORM PUBLIC.THROW_GENERIC_ERROR(
        'Não é possível aplicar um serviço de troca de banda em um pneu na primeira vida');
  END IF;

  --  Inserimos o serviço realizado, retornando o código.
  INSERT INTO PUBLIC.PNEU_SERVICO_REALIZADO(
    COD_TIPO_SERVICO,
    COD_UNIDADE,
    COD_PNEU,
    CUSTO,
    VIDA,
    FONTE_SERVICO_REALIZADO)
  VALUES(
    COD_TIPO_SERVICO_INCREMENTA_VIDA_CADASTRO,
    F_COD_UNIDADE_PNEU,
    F_COD_PNEU_PROLOG,
    F_VALOR_BANDA_PNEU,
    VIDA_MOMENTO_SERVICO_REALIZADO,
    FONTE_SERVICO_REALIZADO_CADASTRO) RETURNING CODIGO INTO F_COD_SERVICO_REALIZADO;

  -- Mapeamos o incremento de vida do serviço realizado acima.
  INSERT INTO PUBLIC.PNEU_SERVICO_REALIZADO_INCREMENTA_VIDA(
    COD_SERVICO_REALIZADO,
    COD_MODELO_BANDA,
    VIDA_NOVA_PNEU,
    FONTE_SERVICO_REALIZADO)
  VALUES (
    F_COD_SERVICO_REALIZADO,
    F_COD_MODELO_BANDA_PNEU,
    F_VIDA_NOVA_PNEU,
    FONTE_SERVICO_REALIZADO_CADASTRO);

  INSERT INTO PUBLIC.PNEU_SERVICO_CADASTRO(
    COD_PNEU,
    COD_SERVICO_REALIZADO)
  VALUES (F_COD_PNEU_PROLOG,
          F_COD_SERVICO_REALIZADO);

  GET DIAGNOSTICS F_QTD_ROWS_ALTERADAS = ROW_COUNT;

  -- Verificamos se a criação do serviço de incremento de vida ocorreu com sucesso.
  IF (F_QTD_ROWS_ALTERADAS <= 0)
  THEN
    PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('Não foi possível incrementar a vida do pneu %s', F_COD_PNEU_PROLOG));
  END IF;

  RETURN TRUE;
END;
$$;

CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_PNEU_REALIZA_INCREMENTO_VIDA_MOVIMENTACAO(
  F_COD_UNIDADE_PNEU                 BIGINT,
  F_COD_PNEU_PROLOG                  BIGINT,
  F_COD_MODELO_BANDA_PNEU            BIGINT,
  F_VALOR_BANDA_PNEU                 REAL,
  F_VIDA_NOVA_PNEU                   INTEGER,
  F_COD_TIPO_SERVICO_INCREMENTA_VIDA BIGINT)
  RETURNS BOOLEAN
LANGUAGE PLPGSQL
AS $$
DECLARE
  FONTE_SERVICO_REALIZADO_MOVIMENTACAO TEXT := 'FONTE_MOVIMENTACAO';
  VIDA_MOMENTO_SERVICO_REALIZADO INTEGER := F_VIDA_NOVA_PNEU - 1;
  F_COD_SERVICO_REALIZADO BIGINT;
  F_QTD_ROWS_ALTERADAS BIGINT;
BEGIN
  --  Inserimos o serviço realizado, retornando o código.
  INSERT INTO PUBLIC.PNEU_SERVICO_REALIZADO(
    COD_TIPO_SERVICO,
    COD_UNIDADE,
    COD_PNEU,
    CUSTO,
    VIDA,
    FONTE_SERVICO_REALIZADO)
  VALUES (
    F_COD_TIPO_SERVICO_INCREMENTA_VIDA,
    F_COD_UNIDADE_PNEU,
    F_COD_PNEU_PROLOG,
    F_VALOR_BANDA_PNEU,
    VIDA_MOMENTO_SERVICO_REALIZADO,
    FONTE_SERVICO_REALIZADO_MOVIMENTACAO) RETURNING CODIGO INTO F_COD_SERVICO_REALIZADO;

  -- Mapeamos o incremento de vida do serviço realizado acima.
  INSERT INTO PUBLIC.PNEU_SERVICO_REALIZADO_INCREMENTA_VIDA(
    COD_SERVICO_REALIZADO,
    COD_MODELO_BANDA,
    VIDA_NOVA_PNEU,
    FONTE_SERVICO_REALIZADO)
  VALUES(
    F_COD_SERVICO_REALIZADO,
    F_COD_MODELO_BANDA_PNEU,
    F_VIDA_NOVA_PNEU,
    FONTE_SERVICO_REALIZADO_MOVIMENTACAO);

  GET DIAGNOSTICS F_QTD_ROWS_ALTERADAS = ROW_COUNT;

  -- Verificamos se a criação do serviço de incremento de vida ocorreu com sucesso.
  IF (F_QTD_ROWS_ALTERADAS <= 0)
  THEN
    PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('Não foi possível incrementar a vida do pneu %s', F_COD_PNEU_PROLOG));
  END IF;

  RETURN TRUE;
END;
$$;

CREATE OR REPLACE FUNCTION FUNC_PNEUS_UPDATE_BANDA_PNEU(F_COD_PNEU BIGINT,
                                                        F_COD_MODELO_BANDA BIGINT,
                                                        F_CUSTO_BANDA REAL)
    RETURNS BOOLEAN
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    F_COD_SERVICO_REALIZADO BIGINT;
BEGIN
    F_COD_SERVICO_REALIZADO = (
        SELECT CODIGO
        FROM PNEU_SERVICO_REALIZADO PSR
                 JOIN PNEU_SERVICO_REALIZADO_INCREMENTA_VIDA PSRIV
                      ON PSR.CODIGO = PSRIV.COD_SERVICO_REALIZADO
                          AND PSR.FONTE_SERVICO_REALIZADO = PSRIV.FONTE_SERVICO_REALIZADO
        WHERE PSR.COD_PNEU = F_COD_PNEU
        ORDER BY CODIGO DESC
        LIMIT 1);
    UPDATE PNEU_SERVICO_REALIZADO
    SET CUSTO = F_CUSTO_BANDA
    WHERE CODIGO = F_COD_SERVICO_REALIZADO;
    UPDATE PNEU_SERVICO_REALIZADO_INCREMENTA_VIDA
    SET COD_MODELO_BANDA = F_COD_MODELO_BANDA
    WHERE COD_SERVICO_REALIZADO = F_COD_SERVICO_REALIZADO;

    -- FOUND será true se alguma linha foi modificada pela query executada.
    IF FOUND THEN
        RETURN TRUE;
    ELSE
        RETURN FALSE;
    END IF;
END;
$$;
--
--######################################################################################################################
--######################################################################################################################
END TRANSACTION;