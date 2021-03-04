--REALIZA TODA A LOGICA DE CRIAÇÃO DOS TYPES
CREATE SCHEMA TYPES;

CREATE TABLE TYPES.AFERICAO_FORMA_COLETA_DADOS_TYPE
(
    FORMA_COLETA_DADOS               TEXT     NOT NULL
        CONSTRAINT PK_AFERICAO_FORMA_COLETA_DADOS_TYPE
            PRIMARY KEY,
    FORMA_COLETA_DADOS_LEGIVEL_PT_BR TEXT     NOT NULL,
    FORMA_COLETA_DADOS_LEGIVEL_ES    TEXT     NOT NULL,
    ORDEM_EXIBICAO_RELATORIOS        SMALLINT NOT NULL,
    ORDEM_EXIBICAO_LISTAGEM          SMALLINT NOT NULL,
    ATIVO                            BOOLEAN  NOT NULL
);

INSERT INTO TYPES.AFERICAO_FORMA_COLETA_DADOS_TYPE
VALUES ('BLOQUEADO', 'Bloqueado', 'Bloqueado', 0, 0, true);
INSERT INTO TYPES.AFERICAO_FORMA_COLETA_DADOS_TYPE
VALUES ('EQUIPAMENTO', 'Com Aferidor', 'Con Aparato de Medición', 1, 1, true);
INSERT INTO TYPES.AFERICAO_FORMA_COLETA_DADOS_TYPE
VALUES ('MANUAL', 'Manualmente', 'Manualmente', 2, 2, true);
INSERT INTO TYPES.AFERICAO_FORMA_COLETA_DADOS_TYPE
VALUES ('EQUIPAMENTO_MANUAL', 'Com Aferidor ou Manualmente', 'Con Aparato de Medición o Manualmente', 3, 3, true);

CREATE OR REPLACE VIEW TYPES.AFERICAO_FORMA_COLETA_DADOS AS
SELECT FORMA_COLETA_DADOS                     AS FORMA_COLETA_DADOS,
       F_IF((SELECT current_setting('lc_messages') = 'es_es.UTF-8'),
            FORMA_COLETA_DADOS_LEGIVEL_ES,
            FORMA_COLETA_DADOS_LEGIVEL_PT_BR) AS STATUS_LEGIVEL,
       ORDEM_EXIBICAO_RELATORIOS              AS ORDEM_EXIBICAO_RELATORIOS,
       ORDEM_EXIBICAO_LISTAGEM                AS ORDEM_EXIBICAO_LISTAGEM
FROM TYPES.AFERICAO_FORMA_COLETA_DADOS_TYPE
WHERE ATIVO = TRUE;

-- ADICIONA AS COLUNAS DE FORMA DE COLETA
ALTER TABLE AFERICAO_CONFIGURACAO_TIPO_AFERICAO_VEICULO
    add COLUMN FORMA_COLETA_DADOS_PRESSAO TEXT NOT NULL DEFAULT 'EQUIPAMENTO'
        constraint fk_afericao_configuracao_forma_coleta_dados_pressao
            references types.AFERICAO_FORMA_COLETA_DADOS_TYPE;

ALTER TABLE AFERICAO_CONFIGURACAO_TIPO_AFERICAO_VEICULO
    ADD COLUMN FORMA_COLETA_DADOS_SULCO TEXT NOT NULL DEFAULT 'EQUIPAMENTO'
        constraint fk_afericao_configuracao_forma_coleta_dados_sulco
            references types.AFERICAO_FORMA_COLETA_DADOS_TYPE;

ALTER TABLE AFERICAO_CONFIGURACAO_TIPO_AFERICAO_VEICULO
    ADD COLUMN FORMA_COLETA_DADOS_SULCO_PRESSAO TEXT NOT NULL DEFAULT 'EQUIPAMENTO'
        constraint fk_afericao_configuracao_forma_coleta_dados_sulco_pressao
            references types.AFERICAO_FORMA_COLETA_DADOS_TYPE;

ALTER TABLE AFERICAO_CONFIGURACAO_TIPO_AFERICAO_VEICULO
    ADD COLUMN FORMA_COLETA_DADOS_FECHAMENTO_SERVICO TEXT NOT NULL DEFAULT 'EQUIPAMENTO'
        constraint fk_afericao_configuracao_forma_coleta_dados_fechamento_servico
            references types.AFERICAO_FORMA_COLETA_DADOS_TYPE;

-- REMOVE OS DEFAULTS:
ALTER TABLE AFERICAO_CONFIGURACAO_TIPO_AFERICAO_VEICULO
    ALTER COLUMN FORMA_COLETA_DADOS_PRESSAO DROP DEFAULT;
ALTER TABLE AFERICAO_CONFIGURACAO_TIPO_AFERICAO_VEICULO
    ALTER COLUMN FORMA_COLETA_DADOS_SULCO DROP DEFAULT;
ALTER TABLE AFERICAO_CONFIGURACAO_TIPO_AFERICAO_VEICULO
    ALTER COLUMN FORMA_COLETA_DADOS_SULCO_PRESSAO DROP DEFAULT;
ALTER TABLE AFERICAO_CONFIGURACAO_TIPO_AFERICAO_VEICULO
    ALTER COLUMN FORMA_COLETA_DADOS_FECHAMENTO_SERVICO DROP DEFAULT;

CREATE OR REPLACE FUNCTION FUNC_AFERICAO_CONFIGURACOES_VEICULO_AFERIVEL_INSERE(F_COD_CONFIGURACAO BIGINT,
                                                                               F_COD_UNIDADE BIGINT,
                                                                               F_COD_TIPO_VEICULO BIGINT,
                                                                               F_PODE_AFERIR_ESTEPE BOOLEAN,
                                                                               F_FORMA_COLETA_DADOS_PRESSAO TEXT,
                                                                               F_FORMA_COLETA_DADOS_SULCO TEXT,
                                                                               F_FORMA_COLETA_DADOS_SULCO_PRESSAO TEXT,
                                                                               F_FORMA_COLETA_DADOS_FECHAMENTO_SERVICO TEXT)
    RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_COD_CONFIGURACAO                      BIGINT;
    V_PODE_AFERIR_ESTEPE                    BOOLEAN;
    V_FORMA_COLETA_DADOS_PRESSAO            TEXT;
    V_FORMA_COLETA_DADOS_SULCO              TEXT;
    V_FORMA_COLETA_DADOS_SULCO_PRESSAO      TEXT;
    V_FORMA_COLETA_DADOS_FECHAMENTO_SERVICO TEXT;
BEGIN

    IF F_COD_CONFIGURACAO IS NULL THEN
        INSERT INTO AFERICAO_CONFIGURACAO_TIPO_AFERICAO_VEICULO (COD_UNIDADE,
                                                                 COD_TIPO_VEICULO,
                                                                 PODE_AFERIR_ESTEPE,
                                                                 FORMA_COLETA_DADOS_PRESSAO,
                                                                 FORMA_COLETA_DADOS_SULCO,
                                                                 FORMA_COLETA_DADOS_SULCO_PRESSAO,
                                                                 FORMA_COLETA_DADOS_FECHAMENTO_SERVICO)
        VALUES (F_COD_UNIDADE,
                F_COD_TIPO_VEICULO,
                F_PODE_AFERIR_ESTEPE,
                F_FORMA_COLETA_DADOS_PRESSAO,
                F_FORMA_COLETA_DADOS_SULCO,
                F_FORMA_COLETA_DADOS_SULCO_PRESSAO,
                F_FORMA_COLETA_DADOS_FECHAMENTO_SERVICO)
        RETURNING CODIGO INTO V_COD_CONFIGURACAO;
    ELSE
        SELECT PODE_AFERIR_ESTEPE,
               FORMA_COLETA_DADOS_PRESSAO,
               FORMA_COLETA_DADOS_SULCO,
               FORMA_COLETA_DADOS_SULCO_PRESSAO,
               FORMA_COLETA_DADOS_FECHAMENTO_SERVICO
        INTO V_PODE_AFERIR_ESTEPE,
            V_FORMA_COLETA_DADOS_PRESSAO,
            V_FORMA_COLETA_DADOS_SULCO,
            V_FORMA_COLETA_DADOS_SULCO_PRESSAO,
            V_FORMA_COLETA_DADOS_FECHAMENTO_SERVICO
        FROM AFERICAO_CONFIGURACAO_TIPO_AFERICAO_VEICULO
        WHERE CODIGO = F_COD_CONFIGURACAO;

        IF V_PODE_AFERIR_ESTEPE != F_PODE_AFERIR_ESTEPE
            OR V_FORMA_COLETA_DADOS_PRESSAO != F_FORMA_COLETA_DADOS_PRESSAO
            OR V_FORMA_COLETA_DADOS_SULCO != F_FORMA_COLETA_DADOS_SULCO
            OR V_FORMA_COLETA_DADOS_SULCO_PRESSAO != F_FORMA_COLETA_DADOS_SULCO_PRESSAO
            OR V_FORMA_COLETA_DADOS_FECHAMENTO_SERVICO != F_FORMA_COLETA_DADOS_FECHAMENTO_SERVICO THEN

            UPDATE AFERICAO_CONFIGURACAO_TIPO_AFERICAO_VEICULO
            SET PODE_AFERIR_ESTEPE                    = F_PODE_AFERIR_ESTEPE,
                FORMA_COLETA_DADOS_PRESSAO            = F_FORMA_COLETA_DADOS_PRESSAO,
                FORMA_COLETA_DADOS_SULCO              = F_FORMA_COLETA_DADOS_SULCO,
                FORMA_COLETA_DADOS_SULCO_PRESSAO      = F_FORMA_COLETA_DADOS_SULCO_PRESSAO,
                FORMA_COLETA_DADOS_FECHAMENTO_SERVICO = F_FORMA_COLETA_DADOS_FECHAMENTO_SERVICO
            WHERE CODIGO = F_COD_CONFIGURACAO
            RETURNING CODIGO INTO V_COD_CONFIGURACAO;
        END IF;
    END IF;

    RETURN V_COD_CONFIGURACAO;
END
$$;

DROP FUNCTION FUNC_AFERICAO_GET_CONFIG_TIPO_AFERICAO_VEICULO(
    F_COD_UNIDADE BIGINT);

CREATE OR REPLACE FUNCTION FUNC_AFERICAO_GET_CONFIG_TIPO_AFERICAO_VEICULO(
    F_COD_UNIDADE BIGINT)
    RETURNS TABLE
            (
                COD_CONFIGURACAO                              BIGINT,
                COD_UNIDADE_CONFIGURACAO                      BIGINT,
                COD_TIPO_VEICULO                              BIGINT,
                NOME_TIPO_VEICULO                             TEXT,
                COD_EMPRESA_TIPO_VEICULO                      BIGINT,
                STATUS_ATIVO_TIPO_VEICULO                     BOOLEAN,
                PODE_AFERIR_ESTEPE                            BOOLEAN,
                FORMA_COLETA_DADOS_PRESSAO                    TEXT,
                FORMA_COLETA_DADOS_PRESSAO_LEGIVEL            TEXT,
                FORMA_COLETA_DADOS_SULCO                      TEXT,
                FORMA_COLETA_DADOS_SULCO_LEGIVEL              TEXT,
                FORMA_COLETA_DADOS_SULCO_PRESSAO              TEXT,
                FORMA_COLETA_DADOS_SULCO_PRESSAO_LEGIVEL      TEXT,
                FORMA_COLETA_DADOS_FECHAMENTO_SERVICO         TEXT,
                FORMA_COLETA_DADOS_FECHAMENTO_SERVICO_LEGIVEL TEXT
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_COD_EMPRESA                CONSTANT BIGINT := (SELECT U.COD_EMPRESA
                                                     FROM UNIDADE U
                                                     WHERE U.CODIGO = F_COD_UNIDADE);
    V_STATUS_LEGIVEL_EQUIPAMENTO CONSTANT TEXT   = (SELECT STATUS_LEGIVEL
                                                    FROM TYPES.AFERICAO_FORMA_COLETA_DADOS
                                                    WHERE FORMA_COLETA_DADOS = 'EQUIPAMENTO');
BEGIN
    RETURN QUERY
        SELECT CONFIG.CODIGO                                                AS COD_CONFIGURACAO,
               -- Usamos o código da unidade recebido por parâmetro pois se um tipo não tiver configurado para a unidade buscada
               -- o código da tabela será null.
               F_COD_UNIDADE                                                AS COD_UNIDADE_CONFIGURACAO,
               VT.CODIGO                                                    AS COD_TIPO_VEICULO,
               VT.NOME :: TEXT                                              AS NOME_TIPO_VEICULO,
               VT.COD_EMPRESA                                               AS COD_EMPRESA_TIPO_VEICULO,
               VT.STATUS_ATIVO                                              AS STATUS_ATIVO_TIPO_VEICULO,
               -- Essas verificações servem para o caso do tipo de veículo não ter configuracão criada,
               -- assim retornamos um default que libera tudo.
               F_IF(CONFIG.CODIGO IS NULL, TRUE, CONFIG.PODE_AFERIR_ESTEPE) AS PODE_AFERIR_ESTEPE,
               -- Aqui é tratada tanto a primary key quando a forma legivel (br ou es).
               F_IF(CONFIG.FORMA_COLETA_DADOS_PRESSAO IS NULL,
                    'EQUIPAMENTO',
                    CONFIG.FORMA_COLETA_DADOS_PRESSAO)                      AS FORMA_COLETA_DADOS_PRESSAO,
               F_IF(CONFIG.FORMA_COLETA_DADOS_PRESSAO IS NULL,
                    V_STATUS_LEGIVEL_EQUIPAMENTO,
                    FCDTPRESSAO.STATUS_LEGIVEL)                             AS FORMA_COLETA_DADOS_PRESSAO_LEGIVEL,
               F_IF(CONFIG.FORMA_COLETA_DADOS_SULCO IS NULL,
                    'EQUIPAMENTO',
                    CONFIG.FORMA_COLETA_DADOS_SULCO)                        AS FORMA_COLETA_DADOS_SULCO,
               F_IF(CONFIG.FORMA_COLETA_DADOS_SULCO IS NULL,
                    V_STATUS_LEGIVEL_EQUIPAMENTO,
                    FCDTSULCO.STATUS_LEGIVEL)                               AS FORMA_COLETA_DADOS_SULCO_LEGIVEL,
               F_IF(CONFIG.FORMA_COLETA_DADOS_SULCO_PRESSAO IS NULL,
                    'EQUIPAMENTO',
                    CONFIG.FORMA_COLETA_DADOS_SULCO_PRESSAO)                AS FORMA_COLETA_DADOS_SULCO_PRESSAO,
               F_IF(CONFIG.FORMA_COLETA_DADOS_SULCO_PRESSAO IS NULL,
                    V_STATUS_LEGIVEL_EQUIPAMENTO,
                    FCDTSULCOPRESSAO.STATUS_LEGIVEL)                        AS FORMA_COLETA_DADOS_SULCO_PRESSAO_LEGIVEL,
               F_IF(CONFIG.FORMA_COLETA_DADOS_FECHAMENTO_SERVICO IS NULL,
                    'EQUIPAMENTO',
                    CONFIG.FORMA_COLETA_DADOS_FECHAMENTO_SERVICO)           AS FORMA_COLETA_DADOS_FECHAMENTO_SERVICO,
               F_IF(CONFIG.FORMA_COLETA_DADOS_FECHAMENTO_SERVICO IS NULL,
                    V_STATUS_LEGIVEL_EQUIPAMENTO,
                    FCDTFECHAMENTOSERVICO.STATUS_LEGIVEL)                   AS FORMA_COLETA_DADOS_FECHGAMENTO_SERVICO_LEGIVEL
        FROM VEICULO_TIPO VT
                 LEFT JOIN AFERICAO_CONFIGURACAO_TIPO_AFERICAO_VEICULO CONFIG
                           ON CONFIG.COD_TIPO_VEICULO = VT.CODIGO
                               AND CONFIG.COD_UNIDADE = F_COD_UNIDADE
                 LEFT JOIN TYPES.AFERICAO_FORMA_COLETA_DADOS FCDTSULCO
                           ON FCDTSULCO.FORMA_COLETA_DADOS = CONFIG.FORMA_COLETA_DADOS_SULCO
                 LEFT JOIN TYPES.AFERICAO_FORMA_COLETA_DADOS FCDTPRESSAO
                           ON FCDTPRESSAO.FORMA_COLETA_DADOS = CONFIG.FORMA_COLETA_DADOS_PRESSAO
                 LEFT JOIN TYPES.AFERICAO_FORMA_COLETA_DADOS FCDTSULCOPRESSAO
                           ON FCDTSULCOPRESSAO.FORMA_COLETA_DADOS = CONFIG.FORMA_COLETA_DADOS_SULCO_PRESSAO
                 LEFT JOIN TYPES.AFERICAO_FORMA_COLETA_DADOS FCDTFECHAMENTOSERVICO
                           ON FCDTFECHAMENTOSERVICO.FORMA_COLETA_DADOS = CONFIG.FORMA_COLETA_DADOS_FECHAMENTO_SERVICO
        WHERE VT.COD_EMPRESA = V_COD_EMPRESA
          AND (CONFIG.COD_UNIDADE = F_COD_UNIDADE OR CONFIG.COD_UNIDADE IS NULL)
        ORDER BY VT.NOME ASC;
END ;
$$;

UPDATE afericao_configuracao_tipo_afericao_veiculo
SET forma_coleta_dados_sulco = 'EQUIPAMENTO'
WHERE pode_aferir_sulco = true;

UPDATE afericao_configuracao_tipo_afericao_veiculo
SET forma_coleta_dados_sulco = 'BLOQUEADO'
WHERE pode_aferir_sulco = false;

alter table afericao_configuracao_tipo_afericao_veiculo
    drop column IF EXISTS pode_aferir_sulco;

UPDATE afericao_configuracao_tipo_afericao_veiculo
SET forma_coleta_dados_pressao = 'EQUIPAMENTO'
WHERE pode_aferir_pressao = true;

UPDATE afericao_configuracao_tipo_afericao_veiculo
SET forma_coleta_dados_pressao = 'BLOQUEADO'
WHERE pode_aferir_pressao = false;

alter table afericao_configuracao_tipo_afericao_veiculo
    drop column IF EXISTS pode_aferir_pressao;

UPDATE afericao_configuracao_tipo_afericao_veiculo
SET forma_coleta_dados_sulco_pressao = 'EQUIPAMENTO'
WHERE pode_aferir_sulco_pressao = true;

UPDATE afericao_configuracao_tipo_afericao_veiculo
SET forma_coleta_dados_sulco_pressao = 'BLOQUEADO'
WHERE pode_aferir_sulco_pressao = false;

alter table afericao_configuracao_tipo_afericao_veiculo
    drop column IF EXISTS pode_aferir_sulco_pressao;

ALTER TABLE AFERICAO_DATA
ADD COLUMN FORMA_COLETA_DADOS TEXT NOT NULL DEFAULT 'EQUIPAMENTO'
        constraint fk_forma_coleta_dados
            references types.AFERICAO_FORMA_COLETA_DADOS_TYPE;

--Remove default;
ALTER TABLE AFERICAO_DATA
    ALTER COLUMN FORMA_COLETA_DADOS DROP DEFAULT;

ALTER TABLE AFERICAO_DATA
ADD CONSTRAINT CHECK_FORMA_COLETA_DADOS CHECK (
    FORMA_COLETA_DADOS = 'EQUIPAMENTO'
    OR FORMA_COLETA_DADOS = 'MANUAL');

DROP FUNCTION IF EXISTS FUNC_AFERICAO_INSERT_AFERICAO(F_COD_UNIDADE BIGINT,
                                                         F_DATA_HORA TIMESTAMP WITH TIME ZONE,
                                                         F_CPF_AFERIDOR BIGINT,
                                                         F_TEMPO_REALIZACAO BIGINT,
                                                         F_TIPO_MEDICAO_COLETADA VARCHAR(255),
                                                         F_TIPO_PROCESSO_COLETA VARCHAR(255),
                                                         F_PLACA_VEICULO VARCHAR(255),
                                                         F_KM_VEICULO BIGINT);

CREATE OR REPLACE FUNCTION FUNC_AFERICAO_INSERT_AFERICAO(F_COD_UNIDADE BIGINT,
                                                         F_DATA_HORA TIMESTAMP WITH TIME ZONE,
                                                         F_CPF_AFERIDOR BIGINT,
                                                         F_TEMPO_REALIZACAO BIGINT,
                                                         F_TIPO_MEDICAO_COLETADA VARCHAR(255),
                                                         F_TIPO_PROCESSO_COLETA VARCHAR(255),
                                                         F_FORMA_COLETA_DADOS TEXT,
                                                         F_PLACA_VEICULO VARCHAR(255),
                                                         F_KM_VEICULO BIGINT)
    RETURNS BIGINT

    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    F_COD_TIPO_VEICULO      BIGINT := (SELECT V.COD_TIPO
                                       FROM VEICULO_DATA V
                                       WHERE V.PLACA = F_PLACA_VEICULO);
    F_COD_DIAGRAMA_VEICULO  BIGINT := (SELECT VT.COD_DIAGRAMA
                                       FROM VEICULO_TIPO VT
                                       WHERE VT.CODIGO = F_COD_TIPO_VEICULO);
    F_COD_AFERICAO_INSERIDA BIGINT;
BEGIN
    --REALIZA INSERÇÃO DA AFERIÇÃO
    INSERT INTO AFERICAO_DATA(DATA_HORA, PLACA_VEICULO, CPF_AFERIDOR, KM_VEICULO, TEMPO_REALIZACAO,
                              TIPO_MEDICAO_COLETADA, COD_UNIDADE, TIPO_PROCESSO_COLETA, DELETADO, DATA_HORA_DELETADO,
                              PG_USERNAME_DELECAO, COD_DIAGRAMA, FORMA_COLETA_DADOS)
    VALUES (F_DATA_HORA, F_PLACA_VEICULO, F_CPF_AFERIDOR, F_KM_VEICULO, F_TEMPO_REALIZACAO, F_TIPO_MEDICAO_COLETADA,
            F_COD_UNIDADE, F_TIPO_PROCESSO_COLETA, FALSE, NULL, NULL,
            F_COD_DIAGRAMA_VEICULO, F_FORMA_COLETA_DADOS) RETURNING CODIGO INTO F_COD_AFERICAO_INSERIDA;

    RETURN F_COD_AFERICAO_INSERIDA;
END
$$;


CREATE OR REPLACE VIEW AFERICAO AS
SELECT AD.CODIGO,
       AD.DATA_HORA,
       AD.PLACA_VEICULO,
       AD.CPF_AFERIDOR,
       AD.KM_VEICULO,
       AD.TEMPO_REALIZACAO,
       AD.TIPO_MEDICAO_COLETADA,
       AD.COD_UNIDADE,
       AD.TIPO_PROCESSO_COLETA,
       AD.FORMA_COLETA_DADOS
FROM AFERICAO_DATA AD
WHERE AD.DELETADO = FALSE;


DROP FUNCTION IF EXISTS FUNC_AFERICAO_GET_AFERICOES_PLACAS_PAGINADA(F_COD_UNIDADE BIGINT, F_COD_TIPO_VEICULO BIGINT,
    F_PLACA_VEICULO TEXT, F_DATA_INICIAL DATE,
    F_DATA_FINAL DATE, F_LIMIT BIGINT, F_OFFSET BIGINT,
    F_TZ_UNIDADE TEXT);
CREATE OR REPLACE FUNCTION FUNC_AFERICAO_GET_AFERICOES_PLACAS_PAGINADA(F_COD_UNIDADE BIGINT, F_COD_TIPO_VEICULO BIGINT,
                                                                       F_PLACA_VEICULO TEXT, F_DATA_INICIAL DATE,
                                                                       F_DATA_FINAL DATE, F_LIMIT BIGINT,
                                                                       F_OFFSET BIGINT,
                                                                       F_TZ_UNIDADE TEXT)
    RETURNS TABLE
            (
                KM_VEICULO            BIGINT,
                COD_AFERICAO          BIGINT,
                COD_UNIDADE           BIGINT,
                DATA_HORA             TIMESTAMP WITHOUT TIME ZONE,
                PLACA_VEICULO         TEXT,
                TIPO_MEDICAO_COLETADA TEXT,
                TIPO_PROCESSO_COLETA  TEXT,
                FORMA_COLETA_DADOS    TEXT,
                CPF                   TEXT,
                NOME                  TEXT,
                TEMPO_REALIZACAO      BIGINT
            )
    LANGUAGE SQL
AS
$$
SELECT A.KM_VEICULO,
       A.CODIGO                              AS COD_AFERICAO,
       A.COD_UNIDADE                         AS COD_UNIDADE,
       A.DATA_HORA AT TIME ZONE F_TZ_UNIDADE AS DATA_HORA,
       A.PLACA_VEICULO::TEXT                 AS PLACA_VEICULO,
       A.TIPO_MEDICAO_COLETADA::TEXT         AS TIPO_MEDICAO_COLETADA,
       A.TIPO_PROCESSO_COLETA::TEXT          AS TIPO_PROCESSO_COLETA,
       A.FORMA_COLETA_DADOS::TEXT            AS FORMA_COLETA_DADOS,
       C.CPF::TEXT                           AS CPF,
       C.NOME::TEXT                          AS NOME,
       A.TEMPO_REALIZACAO                    AS TEMPO_REALIZACAO
FROM AFERICAO A
         JOIN VEICULO V ON V.PLACA = A.PLACA_VEICULO
         JOIN COLABORADOR C ON C.CPF = A.CPF_AFERIDOR
WHERE A.COD_UNIDADE = F_COD_UNIDADE
  AND CASE
          WHEN F_COD_TIPO_VEICULO IS NOT NULL
              THEN V.COD_TIPO = F_COD_TIPO_VEICULO
          ELSE TRUE END
  AND CASE
          WHEN F_PLACA_VEICULO IS NOT NULL
              THEN V.PLACA = F_PLACA_VEICULO
          ELSE TRUE END
  AND (A.DATA_HORA AT TIME ZONE F_TZ_UNIDADE)::DATE BETWEEN F_DATA_INICIAL AND F_DATA_FINAL
ORDER BY A.DATA_HORA DESC
LIMIT F_LIMIT
OFFSET
F_OFFSET;
$$;


DROP FUNCTION IF EXISTS FUNC_AFERICAO_GET_AFERICOES_AVULSAS_PAGINADA(F_COD_UNIDADE BIGINT, F_DATA_INICIAL DATE,
    F_DATA_FINAL DATE, F_LIMIT BIGINT, F_OFFSET BIGINT,
    F_TZ_UNIDADE TEXT);
CREATE OR REPLACE FUNCTION FUNC_AFERICAO_GET_AFERICOES_AVULSAS_PAGINADA(F_COD_UNIDADE BIGINT, F_DATA_INICIAL DATE,
                                                                        F_DATA_FINAL DATE, F_LIMIT BIGINT,
                                                                        F_OFFSET BIGINT,
                                                                        F_TZ_UNIDADE TEXT)
    RETURNS TABLE
            (
                KM_VEICULO            BIGINT,
                COD_AFERICAO          BIGINT,
                COD_UNIDADE           BIGINT,
                DATA_HORA             TIMESTAMP WITHOUT TIME ZONE,
                PLACA_VEICULO         TEXT,
                TIPO_MEDICAO_COLETADA TEXT,
                TIPO_PROCESSO_COLETA  TEXT,
                FORMA_COLETA_DADOS    TEXT,
                CPF                   TEXT,
                NOME                  TEXT,
                TEMPO_REALIZACAO      BIGINT
            )
    LANGUAGE SQL
AS
$$
SELECT A.KM_VEICULO,
       A.CODIGO                              AS COD_AFERICAO,
       A.COD_UNIDADE                         AS COD_UNIDADE,
       A.DATA_HORA AT TIME ZONE F_TZ_UNIDADE AS DATA_HORA,
       A.PLACA_VEICULO::TEXT                 as PLACA_VEICULO,
       A.TIPO_MEDICAO_COLETADA::TEXT         AS TIPO_MEDICAO_COLETADA,
       A.TIPO_PROCESSO_COLETA::TEXT          AS TIPO_PROCESSO_COLETA,
       A.FORMA_COLETA_DADOS::TEXT            AS FORMA_COLETA_DADOS,
       C.CPF::TEXT                           AS CPF,
       C.NOME::TEXT                          AS NOME,
       A.TEMPO_REALIZACAO                    AS TEMPO_REALIZACAO
FROM AFERICAO A
         JOIN VEICULO V ON V.PLACA = A.PLACA_VEICULO
         JOIN COLABORADOR C ON C.CPF = A.CPF_AFERIDOR
WHERE A.COD_UNIDADE = F_COD_UNIDADE
  AND (A.DATA_HORA AT TIME ZONE F_TZ_UNIDADE)::DATE BETWEEN F_DATA_INICIAL AND F_DATA_FINAL
ORDER BY A.DATA_HORA DESC
LIMIT F_LIMIT
OFFSET
F_OFFSET;
$$;


DROP FUNCTION IF EXISTS FUNC_AFERICAO_GET_AFERICAO_BY_CODIGO(F_COD_UNIDADE BIGINT,
    F_COD_AFERICAO BIGINT,
    F_TZ_UNIDADE TEXT);
CREATE OR REPLACE FUNCTION FUNC_AFERICAO_GET_AFERICAO_BY_CODIGO(F_COD_UNIDADE BIGINT,
                                                                F_COD_AFERICAO BIGINT,
                                                                F_TZ_UNIDADE TEXT)
    RETURNS TABLE
            (
                COD_AFERICAO                 BIGINT,
                COD_UNIDADE                  BIGINT,
                DATA_HORA                    TIMESTAMP WITHOUT TIME ZONE,
                PLACA_VEICULO                TEXT,
                KM_VEICULO                   BIGINT,
                TEMPO_REALIZACAO             BIGINT,
                TIPO_PROCESSO_COLETA         TEXT,
                TIPO_MEDICAO_COLETADA        TEXT,
                FORMA_COLETA_DADOS           TEXT,
                CPF                          TEXT,
                NOME                         TEXT,
                ALTURA_SULCO_CENTRAL_INTERNO REAL,
                ALTURA_SULCO_CENTRAL_EXTERNO REAL,
                ALTURA_SULCO_EXTERNO         REAL,
                ALTURA_SULCO_INTERNO         REAL,
                PRESSAO_PNEU                 INTEGER,
                POSICAO_PNEU                 INTEGER,
                VIDA_PNEU_MOMENTO_AFERICAO   INTEGER,
                VIDAS_TOTAL_PNEU             INTEGER,
                CODIGO_PNEU                  BIGINT,
                CODIGO_PNEU_CLIENTE          TEXT,
                PRESSAO_RECOMENDADA          REAL
            )
    LANGUAGE SQL
AS
$$
SELECT A.CODIGO                              AS COD_AFERICAO,
       A.COD_UNIDADE                         AS COD_UNIDADE,
       A.DATA_HORA AT TIME ZONE F_TZ_UNIDADE AS DATA_HORA,
       A.PLACA_VEICULO::TEXT                 AS PLACA_VEICULO,
       A.KM_VEICULO                          AS KM_VEICULO,
       A.TEMPO_REALIZACAO                    AS TEMPO_REALIZACAO,
       A.TIPO_PROCESSO_COLETA::TEXT          AS TIPO_PROCESSO_COLETA,
       A.TIPO_MEDICAO_COLETADA::TEXT         AS TIPO_MEDICAO_COLETADA,
       A.FORMA_COLETA_DADOS::TEXT            AS FORMA_COLETA_DADOS,
       C.CPF::TEXT                           AS CPF,
       C.NOME::TEXT                          AS NOME,
       AV.ALTURA_SULCO_CENTRAL_INTERNO       AS ALTURA_SULCO_CENTRAL_INTERNO,
       AV.ALTURA_SULCO_CENTRAL_EXTERNO       AS ALTURA_SULCO_CENTRAL_EXTERNO,
       AV.ALTURA_SULCO_EXTERNO               AS ALTURA_SULCO_EXTERNO,
       AV.ALTURA_SULCO_INTERNO               AS ALTURA_SULCO_INTERNO,
       AV.PSI::INT                           AS PRESSAO_PNEU,
       AV.POSICAO                            AS POSICAO_PNEU,
       AV.VIDA_MOMENTO_AFERICAO              AS VIDA_PNEU_MOMENTO_AFERICAO,
       P.VIDA_TOTAL                          AS VIDAS_TOTAL_PNEU,
       P.CODIGO                              AS CODIGO_PNEU,
       P.CODIGO_CLIENTE::TEXT                AS CODIGO_PNEU_CLIENTE,
       P.PRESSAO_RECOMENDADA                 AS PRESSAO_RECOMENDADA
FROM AFERICAO A
         JOIN AFERICAO_VALORES AV
              ON A.CODIGO = AV.COD_AFERICAO
         JOIN PNEU_ORDEM PO
              ON AV.POSICAO = PO.POSICAO_PROLOG
         JOIN PNEU P
              ON P.CODIGO = AV.COD_PNEU
         JOIN MODELO_PNEU MO
              ON MO.CODIGO = P.COD_MODELO
         JOIN MARCA_PNEU MP
              ON MP.CODIGO = MO.COD_MARCA
         JOIN COLABORADOR C
              ON C.CPF = A.CPF_AFERIDOR
WHERE AV.COD_AFERICAO = F_COD_AFERICAO
  AND AV.COD_UNIDADE = F_COD_UNIDADE
ORDER BY PO.ORDEM_EXIBICAO ASC;
$$;


DROP FUNCTION IF EXISTS FUNC_RELATORIO_PNEU_AFERICOES_AVULSAS(F_COD_UNIDADES BIGINT[],
    F_DATA_INICIAL DATE, F_DATA_FINAL DATE);
CREATE OR REPLACE FUNCTION FUNC_RELATORIO_PNEU_AFERICOES_AVULSAS(F_COD_UNIDADES BIGINT[], F_DATA_INICIAL DATE, F_DATA_FINAL DATE)
    RETURNS TABLE
            (
                "DATA/HORA AFERIÇÃO"        TEXT,
                "QUEM AFERIU?"              CHARACTER VARYING,
                "UNIDADE ALOCADO"           CHARACTER VARYING,
                "PNEU"                      CHARACTER VARYING,
                "MARCA"                     CHARACTER VARYING,
                "MODELO"                    CHARACTER VARYING,
                "MEDIDAS"                   TEXT,
                "SULCO INTERNO"             TEXT,
                "SULCO CENTRAL INTERNO"     TEXT,
                "SULCO CENTRAL EXTERNO"     TEXT,
                "SULCO EXTERNO"             TEXT,
                "MENOR SULCO"               TEXT,
                "PRESSÃO"                   TEXT,
                "TIPO DE MEDIÇÃO"           TEXT,
                "VIDA"                      TEXT,
                "DOT"                       CHARACTER VARYING,
                "FORMA DE COLETA DOS DADOS" TEXT
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
                        PNEU_NUNCA_AFERIDO)                                                  AS ULTIMA_AFERICAO,
               C.NOME,
               U.NOME                                                                        AS UNIDADE_ALOCADO,
               P.CODIGO_CLIENTE                                                              AS COD_PNEU,
               MAP.NOME                                                                      AS NOME_MARCA,
               MP.NOME                                                                       AS NOME_MODELO,
               ((((DP.LARGURA || '/'::TEXT) || DP.ALTURA) || ' R'::TEXT) || DP.ARO)          AS MEDIDAS,
               FUNC_PNEU_FORMAT_SULCO(AV.ALTURA_SULCO_INTERNO)                               AS SULCO_INTERNO,
               FUNC_PNEU_FORMAT_SULCO(AV.ALTURA_SULCO_CENTRAL_INTERNO)                       AS SULCO_CENTRAL_INTERNO,
               FUNC_PNEU_FORMAT_SULCO(AV.ALTURA_SULCO_CENTRAL_EXTERNO)                       AS SULCO_CENTRAL_EXTERNO,
               FUNC_PNEU_FORMAT_SULCO(AV.ALTURA_SULCO_EXTERNO)                               AS SULCO_EXTERNO,
               FUNC_PNEU_FORMAT_SULCO(LEAST(AV.ALTURA_SULCO_EXTERNO, AV.ALTURA_SULCO_CENTRAL_EXTERNO,
                                            AV.ALTURA_SULCO_CENTRAL_INTERNO,
                                            AV.ALTURA_SULCO_INTERNO))                        AS MENOR_SULCO,
               REPLACE(COALESCE(TRUNC(AV.PSI :: NUMERIC, 1) :: TEXT, '-'), '.', ',') :: TEXT AS PRESSAO,
               A.TIPO_MEDICAO_COLETADA :: TEXT                                               AS TIPO_MEDICAO,
               P.VIDA_ATUAL::TEXT                                                            AS VIDA_ATUAL,
               COALESCE(P.DOT, '-')                                                          AS DOT,
               COALESCE(TAFCD.STATUS_LEGIVEL::TEXT, '-'::TEXT)                               AS FORMA_COLETA_DADOS
        FROM PNEU P
                 JOIN DIMENSAO_PNEU DP ON DP.CODIGO = P.COD_DIMENSAO
                 JOIN UNIDADE U ON U.CODIGO = P.COD_UNIDADE
                 JOIN MODELO_PNEU MP ON MP.CODIGO = P.COD_MODELO AND MP.COD_EMPRESA = U.COD_EMPRESA
                 JOIN MARCA_PNEU MAP ON MAP.CODIGO = MP.COD_MARCA
                 JOIN AFERICAO_VALORES AV ON AV.COD_PNEU = P.CODIGO
                 JOIN AFERICAO A ON A.CODIGO = AV.COD_AFERICAO
                 JOIN COLABORADOR C ON A.CPF_AFERIDOR = C.CPF
                 LEFT JOIN TYPES.AFERICAO_FORMA_COLETA_DADOS TAFCD
                           ON TAFCD.FORMA_COLETA_DADOS::TEXT = A.FORMA_COLETA_DADOS::TEXT
        WHERE P.COD_UNIDADE = ANY (F_COD_UNIDADES)
          AND A.TIPO_PROCESSO_COLETA = PROCESSO_AFERICAO_PNEU_AVULSO
          AND (A.DATA_HORA AT TIME ZONE TZ_UNIDADE(A.COD_UNIDADE))::DATE BETWEEN F_DATA_INICIAL AND F_DATA_FINAL
        ORDER BY U.NOME ASC, ULTIMA_AFERICAO DESC NULLS LAST;
END;
$$;


DROP FUNCTION IF EXISTS FUNC_RELATORIO_PNEU_AFERICOES_AVULSAS_BY_COLABORADOR(F_COD_UNIDADE BIGINT, F_COD_COLABORADOR BIGINT,
    F_DATA_INICIAL DATE, F_DATA_FINAL DATE);
CREATE OR REPLACE FUNCTION FUNC_RELATORIO_PNEU_AFERICOES_AVULSAS_BY_COLABORADOR(F_COD_UNIDADE BIGINT,
                                                                                F_COD_COLABORADOR BIGINT,
                                                                                F_DATA_INICIAL DATE, F_DATA_FINAL DATE)
    RETURNS TABLE
            (
                "DATA/HORA AFERIÇÃO"       TEXT,
                "QUEM AFERIU?"             CHARACTER VARYING,
                "UNIDADE ALOCADO"          CHARACTER VARYING,
                "PNEU"                     CHARACTER VARYING,
                "MARCA"                    CHARACTER VARYING,
                "MODELO"                   CHARACTER VARYING,
                "MEDIDAS"                  TEXT,
                "SULCO INTERNO"            TEXT,
                "SULCO CENTRAL INTERNO"    TEXT,
                "SULCO CENTRAL EXTERNO"    TEXT,
                "SULCO EXTERNO"            TEXT,
                "VIDA"                     TEXT,
                "DOT"                      CHARACTER VARYING,
                "FORMA DE COLETA DE DADOS" TEXT
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    DATE_FORMAT                   TEXT := 'DD/MM/YYYY HH24:MI';
    PNEU_NUNCA_AFERIDO            TEXT := 'NUNCA AFERIDO';
    PROCESSO_AFERICAO_PNEU_AVULSO TEXT := 'PNEU_AVULSO';
BEGIN
    RETURN QUERY
        SELECT COALESCE(TO_CHAR(A.DATA_HORA AT TIME ZONE TZ_UNIDADE(A.COD_UNIDADE), DATE_FORMAT),
                        PNEU_NUNCA_AFERIDO)                                         AS ULTIMA_AFERICAO,
               C.NOME,
               U.NOME                                                               AS UNIDADE_ALOCADO,
               P.CODIGO_CLIENTE                                                     AS COD_PNEU,
               MAP.NOME                                                             AS NOME_MARCA,
               MP.NOME                                                              AS NOME_MODELO,
               ((((DP.LARGURA || '/'::TEXT) || DP.ALTURA) || ' R'::TEXT) || DP.ARO) AS MEDIDAS,
               REPLACE(COALESCE(TRUNC(AV.ALTURA_SULCO_INTERNO::NUMERIC, 2)::TEXT, '-'), '.',
                       ',')                                                         AS SULCO_INTERNO,
               REPLACE(COALESCE(TRUNC(AV.ALTURA_SULCO_CENTRAL_INTERNO::NUMERIC, 2)::TEXT, '-'), '.',
                       ',')                                                         AS SULCO_CENTRAL_INTERNO,
               REPLACE(COALESCE(TRUNC(AV.ALTURA_SULCO_CENTRAL_EXTERNO::NUMERIC, 2)::TEXT, '-'), '.',
                       ',')                                                         AS SULCO_CENTRAL_EXTERNO,
               REPLACE(COALESCE(TRUNC(AV.ALTURA_SULCO_EXTERNO::NUMERIC, 2)::TEXT, '-'), '.',
                       ',')                                                         AS SULCO_EXTERNO,
               P.VIDA_ATUAL::TEXT                                                   AS VIDA_ATUAL,
               COALESCE(P.DOT, '-')                                                 AS DOT,
               COALESCE(TAFCD.STATUS_LEGIVEL::TEXT, '-'::TEXT)                      AS FORMA_COLETA_DADOS
        FROM PNEU P
                 JOIN DIMENSAO_PNEU DP ON DP.CODIGO = P.COD_DIMENSAO
                 JOIN UNIDADE U ON U.CODIGO = P.COD_UNIDADE
                 JOIN MODELO_PNEU MP ON MP.CODIGO = P.COD_MODELO AND MP.COD_EMPRESA = U.COD_EMPRESA
                 JOIN MARCA_PNEU MAP ON MAP.CODIGO = MP.COD_MARCA
                 JOIN AFERICAO_VALORES AV ON AV.COD_PNEU = P.CODIGO
                 JOIN AFERICAO A ON A.CODIGO = AV.COD_AFERICAO
                 LEFT JOIN TYPES.AFERICAO_FORMA_COLETA_DADOS TAFCD
                           ON TAFCD.FORMA_COLETA_DADOS::TEXT = A.FORMA_COLETA_DADOS::TEXT
                 JOIN COLABORADOR C
                      ON A.CPF_AFERIDOR = (SELECT CO.CPF FROM COLABORADOR CO WHERE CODIGO = F_COD_COLABORADOR)
        WHERE C.CODIGO = F_COD_COLABORADOR
          AND P.COD_UNIDADE = F_COD_UNIDADE
          AND A.TIPO_PROCESSO_COLETA = PROCESSO_AFERICAO_PNEU_AVULSO
          AND (A.DATA_HORA AT TIME ZONE TZ_UNIDADE(A.COD_UNIDADE))::DATE BETWEEN F_DATA_INICIAL AND F_DATA_FINAL
        ORDER BY U.NOME ASC, ULTIMA_AFERICAO DESC NULLS LAST;
END;
$$;


DROP FUNCTION IF EXISTS FUNC_AFERICAO_RELATORIO_DADOS_GERAIS(F_COD_UNIDADES BIGINT[],
    F_DATA_INICIAL DATE,
    F_DATA_FINAL DATE);
CREATE OR REPLACE FUNCTION FUNC_AFERICAO_RELATORIO_DADOS_GERAIS(F_COD_UNIDADES BIGINT[],
                                                                F_DATA_INICIAL DATE,
                                                                F_DATA_FINAL DATE)
    RETURNS TABLE
            (
                "CÓDIGO AFERIÇÃO"           TEXT,
                "UNIDADE"                   TEXT,
                "DATA E HORA"               TEXT,
                "CPF DO RESPONSÁVEL"        TEXT,
                "NOME COLABORADOR"          TEXT,
                "PNEU"                      TEXT,
                "STATUS ATUAL"              TEXT,
                "VALOR COMPRA"              TEXT,
                "MARCA DO PNEU"             TEXT,
                "MODELO DO PNEU"            TEXT,
                "QTD SULCOS MODELO"         TEXT,
                "VIDA ATUAL"                TEXT,
                "VALOR VIDA ATUAL"          TEXT,
                "BANDA APLICADA"            TEXT,
                "QTD SULCOS BANDA"          TEXT,
                "DIMENSÃO"                  TEXT,
                "DOT"                       TEXT,
                "DATA E HORA CADASTRO"      TEXT,
                "POSIÇÃO PNEU"              TEXT,
                "PLACA"                     TEXT,
                "VIDA MOMENTO AFERIÇÃO"     TEXT,
                "KM NO MOMENTO DA AFERIÇÃO" TEXT,
                "KM ATUAL"                  TEXT,
                "MARCA DO VEÍCULO"          TEXT,
                "MODELO DO VEÍCULO"         TEXT,
                "TIPO DE MEDIÇÃO COLETADA"  TEXT,
                "TIPO DA AFERIÇÃO"          TEXT,
                "TEMPO REALIZAÇÃO (MM:SS)"  TEXT,
                "SULCO INTERNO"             TEXT,
                "SULCO CENTRAL INTERNO"     TEXT,
                "SULCO CENTRAL EXTERNO"     TEXT,
                "SULCO EXTERNO"             TEXT,
                "MENOR SULCO"               TEXT,
                "PRESSÃO"                   TEXT,
                "FORMA DE COLETA DOS DADOS" TEXT
            )
    LANGUAGE SQL
AS
$$
SELECT A.CODIGO :: TEXT                                                                 AS COD_AFERICAO,
       U.NOME                                                                           AS UNIDADE,
       TO_CHAR((A.DATA_HORA AT TIME ZONE TZ_UNIDADE(A.COD_UNIDADE)),
               'DD/MM/YYYY HH24:MI')                                                    AS DATA_HORA_AFERICAO,
       LPAD(C.CPF :: TEXT, 11, '0')                                                     AS CPF_COLABORADOR,
       C.NOME                                                                           AS NOME_COLABORADOR,
       P.CODIGO_CLIENTE                                                                 AS CODIGO_CLIENTE_PNEU,
       P.STATUS                                                                         AS STATUS_ATUAL_PNEU,
       ROUND(P.VALOR :: NUMERIC, 2) :: TEXT                                             AS VALOR_COMPRA,
       MAP.NOME                                                                         AS MARCA_PNEU,
       MP.NOME                                                                          AS MODELO_PNEU,
       MP.QT_SULCOS :: TEXT                                                             AS QTD_SULCOS_MODELO,
       (SELECT PVN.NOME
        FROM PNEU_VIDA_NOMENCLATURA PVN
        WHERE PVN.COD_VIDA = P.VIDA_ATUAL)                                              AS VIDA_ATUAL,
       COALESCE(ROUND(PVV.VALOR :: NUMERIC, 2) :: TEXT, '-')                            AS VALOR_VIDA_ATUAL,
       F_IF(MARB.CODIGO IS NOT NULL, MARB.NOME || ' - ' || MODB.NOME, 'Nunca Recapado') AS BANDA_APLICADA,
       COALESCE(MODB.QT_SULCOS :: TEXT, '-')                                            AS QTD_SULCOS_BANDA,
       DP.LARGURA || '-' || DP.ALTURA || '/' || DP.ARO                                  AS DIMENSAO,
       P.DOT                                                                            AS DOT,
       COALESCE(TO_CHAR(P.DATA_HORA_CADASTRO AT TIME ZONE TZ_UNIDADE(P.COD_UNIDADE_CADASTRO),
                        'DD/MM/YYYY HH24:MI'),
                '-')                                                                    AS DATA_HORA_CADASTRO,
       COALESCE(PPNE.NOMENCLATURA, '-')                                                 AS POSICAO,
       COALESCE(A.PLACA_VEICULO, '-')                                                   AS PLACA,
       (SELECT PVN.NOME
        FROM PNEU_VIDA_NOMENCLATURA PVN
        WHERE PVN.COD_VIDA = AV.VIDA_MOMENTO_AFERICAO)                                  AS VIDA_MOMENTO_AFERICAO,
       COALESCE(A.KM_VEICULO :: TEXT, '-')                                              AS KM_MOMENTO_AFERICAO,
       COALESCE(V.KM :: TEXT, '-')                                                      AS KM_ATUAL,
       COALESCE(M2.NOME, '-')                                                           AS MARCA_VEICULO,
       COALESCE(MV.NOME, '-')                                                           AS MODELO_VEICULO,
       A.TIPO_MEDICAO_COLETADA,
       A.TIPO_PROCESSO_COLETA,
       TO_CHAR((A.TEMPO_REALIZACAO || ' milliseconds') :: INTERVAL, 'MI:SS')            AS TEMPO_REALIZACAO_MINUTOS,
       FUNC_PNEU_FORMAT_SULCO(AV.ALTURA_SULCO_INTERNO)                                  AS SULCO_INTERNO,
       FUNC_PNEU_FORMAT_SULCO(AV.ALTURA_SULCO_CENTRAL_INTERNO)                          AS SULCO_CENTRAL_INTERNO,
       FUNC_PNEU_FORMAT_SULCO(AV.ALTURA_SULCO_CENTRAL_EXTERNO)                          AS SULCO_CENTRAL_EXTERNO,
       FUNC_PNEU_FORMAT_SULCO(AV.ALTURA_SULCO_EXTERNO)                                  AS SULCO_EXTERNO,
       FUNC_PNEU_FORMAT_SULCO(LEAST(AV.ALTURA_SULCO_EXTERNO, AV.ALTURA_SULCO_CENTRAL_EXTERNO,
                                    AV.ALTURA_SULCO_CENTRAL_INTERNO,
                                    AV.ALTURA_SULCO_INTERNO))                           AS MENOR_SULCO,
       REPLACE(COALESCE(TRUNC(AV.PSI :: NUMERIC, 1) :: TEXT, '-'), '.', ',')            AS PRESSAO,
       COALESCE(TAFCD.STATUS_LEGIVEL::TEXT, '-'::TEXT)                                  AS FORMA_COLETA_DADOS
FROM AFERICAO A
         JOIN AFERICAO_VALORES AV ON A.CODIGO = AV.COD_AFERICAO AND A.COD_UNIDADE = AV.COD_UNIDADE
         JOIN UNIDADE U ON U.CODIGO = A.COD_UNIDADE
         JOIN COLABORADOR C ON C.CPF = A.CPF_AFERIDOR
         JOIN PNEU P ON P.CODIGO = AV.COD_PNEU AND P.COD_UNIDADE = AV.COD_UNIDADE
         JOIN MODELO_PNEU MP ON P.COD_MODELO = MP.CODIGO AND MP.COD_EMPRESA = P.COD_EMPRESA
         JOIN MARCA_PNEU MAP ON MAP.CODIGO = MP.COD_MARCA
         JOIN DIMENSAO_PNEU DP ON P.COD_DIMENSAO = DP.CODIGO
         LEFT JOIN PNEU_VALOR_VIDA PVV ON P.CODIGO = PVV.COD_PNEU AND P.VIDA_ATUAL = PVV.VIDA
         LEFT JOIN TYPES.AFERICAO_FORMA_COLETA_DADOS TAFCD ON TAFCD.FORMA_COLETA_DADOS = A.FORMA_COLETA_DADOS::TEXT

    -- Pode não possuir banda.
         LEFT JOIN MODELO_BANDA MODB ON MODB.CODIGO = P.COD_MODELO_BANDA
         LEFT JOIN MARCA_BANDA MARB ON MARB.CODIGO = MODB.COD_MARCA

    -- Se foi aferição de pneu avulso, pode não possuir placa.
         LEFT JOIN VEICULO V ON V.PLACA = A.PLACA_VEICULO

         LEFT JOIN EMPRESA E ON U.COD_EMPRESA = E.CODIGO
         LEFT JOIN VEICULO_TIPO VT ON E.CODIGO = VT.COD_EMPRESA AND VT.CODIGO = V.COD_TIPO
         LEFT JOIN PNEU_POSICAO_NOMENCLATURA_EMPRESA PPNE
                   ON PPNE.COD_EMPRESA = P.COD_EMPRESA
                       AND PPNE.COD_DIAGRAMA = VT.COD_DIAGRAMA
                       AND PPNE.POSICAO_PROLOG = AV.POSICAO
         LEFT JOIN MODELO_VEICULO MV
                   ON MV.CODIGO = V.COD_MODELO
         LEFT JOIN MARCA_VEICULO M2
                   ON MV.COD_MARCA = M2.CODIGO
WHERE A.COD_UNIDADE = ANY (F_COD_UNIDADES)
  AND (A.DATA_HORA AT TIME ZONE TZ_UNIDADE(A.COD_UNIDADE)) :: DATE BETWEEN F_DATA_INICIAL AND F_DATA_FINAL
ORDER BY U.CODIGO, A.DATA_HORA DESC;
$$;


drop FUNCTION IF EXISTS FUNC_AFERICAO_RELATORIO_CRONOGRAMA_AFERICOES_PLACAS(F_COD_UNIDADES BIGINT[],
    F_DATA_HORA_ATUAL_UTC TIMESTAMP WITH TIME ZONE,
    F_DATA_HORA_GERACAO_RELATORIO TIMESTAMP WITH TIME ZONE);
CREATE OR REPLACE FUNCTION FUNC_AFERICAO_RELATORIO_CRONOGRAMA_AFERICOES_PLACAS(F_COD_UNIDADES BIGINT[],
                                                                               F_DATA_HORA_ATUAL_UTC TIMESTAMP WITH TIME ZONE,
                                                                               F_DATA_HORA_GERACAO_RELATORIO TIMESTAMP WITH TIME ZONE)
    RETURNS TABLE
            (
                UNIDADE                              TEXT,
                PLACA                                TEXT,
                "QTD PNEUS APLICADOS"                TEXT,
                "MODELO VEÍCULO"                     TEXT,
                "TIPO VEÍCULO"                       TEXT,
                "STATUS SULCO"                       TEXT,
                "STATUS PRESSÃO"                     TEXT,
                "DATA VENCIMENTO SULCO"              TEXT,
                "DATA VENCIMENTO PRESSÃO"            TEXT,
                "DIAS VENCIMENTO SULCO"              TEXT,
                "DIAS VENCIMENTO PRESSÃO"            TEXT,
                "DIAS DESDE ÚLTIMA AFERIÇÃO SULCO"   TEXT,
                "DATA/HORA ÚLTIMA AFERIÇÃO SULCO"    TEXT,
                "DIAS DESDE ÚLTIMA AFERIÇÃO PRESSÃO" TEXT,
                "DATA/HORA ÚLTIMA AFERIÇÃO PRESSÃO"  TEXT,
                "DATA/HORA GERAÇÃO RELATÓRIO"        TEXT
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
BEGIN
    RETURN QUERY
        WITH DADOS AS (SELECT U.NOME :: TEXT                                                       AS NOME_UNIDADE,
                              V.PLACA :: TEXT                                                      AS PLACA_VEICULO,
                              (SELECT COUNT(VP.COD_PNEU)
                               FROM VEICULO_PNEU VP
                               WHERE VP.PLACA = V.PLACA
                               GROUP BY VP.PLACA) :: TEXT                                          AS QTD_PNEUS_APLICADOS,
                              MV.NOME :: TEXT                                                      AS NOME_MODELO_VEICULO,
                              VT.NOME :: TEXT                                                      AS NOME_TIPO_VEICULO,
                              TO_CHAR(SULCO.DATA_HORA_ULTIMA_AFERICAO_SULCO, 'DD/MM/YYYY HH24:MI') AS DATA_HORA_ULTIMA_AFERICAO_SULCO,
                              TO_CHAR(PRESSAO.DATA_HORA_ULTIMA_AFERICAO_PRESSAO,
                                      'DD/MM/YYYY HH24:MI')                                        AS DATA_HORA_ULTIMA_AFERICAO_PRESSAO,
                              TO_CHAR(SULCO.DATA_ULTIMA_AFERICAO_SULCO + (PRU.PERIODO_AFERICAO_SULCO ||
                                                                          ' DAYS') :: INTERVAL,
                                      'DD/MM/YYYY')                                                AS DATA_VENCIMENTO_SULCO,
                              TO_CHAR(PRESSAO.DATA_ULTIMA_AFERICAO_PRESSAO + (PRU.PERIODO_AFERICAO_PRESSAO ||
                                                                              ' DAYS') :: INTERVAL,
                                      'DD/MM/YYYY')                                                AS DATA_VENCIMENTO_PRESSAO,
                              (PRU.PERIODO_AFERICAO_SULCO -
                               SULCO.DIAS) :: TEXT                                                 AS DIAS_VENCIMENTO_SULCO,
                              (PRU.PERIODO_AFERICAO_PRESSAO - PRESSAO.DIAS) :: TEXT
                                                                                                   AS DIAS_VENCIMENTO_PRESSAO,
                              SULCO.DIAS :: TEXT                                                   AS DIAS_DESDE_ULTIMA_AFERICAO_SULCO,
                              PRESSAO.DIAS :: TEXT                                                 AS DIAS_DESDE_ULTIMA_AFERICAO_PRESSAO,
                              F_IF(CONFIG.FORMA_COLETA_DADOS_SULCO IN ('EQUIPAMENTO', 'MANUAL')
                                       OR CONFIG.FORMA_COLETA_DADOS_SULCO_PRESSAO IN ('EQUIPAMENTO', 'MANUAL'), TRUE,
                                   FALSE)                                                          AS PODE_AFERIR_SULCO,
                              F_IF(CONFIG.FORMA_COLETA_DADOS_PRESSAO IN ('EQUIPAMENTO', 'MANUAL')
                                       OR CONFIG.FORMA_COLETA_DADOS_SULCO_PRESSAO IN ('EQUIPAMENTO', 'MANUAL'), TRUE,
                                   FALSE)                                                          AS PODE_AFERIR_PRESSAO,
                              F_IF(SULCO.DIAS IS NULL, TRUE,
                                   FALSE)                                                          AS SULCO_NUNCA_AFERIDO,
                              F_IF(PRESSAO.DIAS IS NULL, TRUE,
                                   FALSE)                                                          AS PRESSAO_NUNCA_AFERIDA,
                              F_IF(SULCO.DIAS > PRU.PERIODO_AFERICAO_SULCO, TRUE,
                                   FALSE)                                                          AS AFERICAO_SULCO_VENCIDA,
                              F_IF(PRESSAO.DIAS > PRU.PERIODO_AFERICAO_PRESSAO, TRUE,
                                   FALSE)                                                          AS AFERICAO_PRESSAO_VENCIDA
                       FROM VEICULO V
                                JOIN MODELO_VEICULO MV
                                     ON MV.CODIGO = V.COD_MODELO
                                JOIN VEICULO_TIPO VT
                                     ON VT.CODIGO = V.COD_TIPO
                                JOIN FUNC_AFERICAO_GET_CONFIG_TIPO_AFERICAO_VEICULO(V.COD_UNIDADE) CONFIG
                                     ON CONFIG.COD_TIPO_VEICULO = V.COD_TIPO
                                LEFT JOIN
                            (SELECT A.PLACA_VEICULO                                               AS PLACA_INTERVALO,
                                    MAX(A.DATA_HORA AT TIME ZONE
                                        TZ_UNIDADE(A.COD_UNIDADE)) :: DATE                        AS DATA_ULTIMA_AFERICAO_PRESSAO,
                                    MAX(A.DATA_HORA AT TIME ZONE
                                        TZ_UNIDADE(A.COD_UNIDADE))                                AS DATA_HORA_ULTIMA_AFERICAO_PRESSAO,
                                    EXTRACT(DAYS FROM (F_DATA_HORA_ATUAL_UTC) - MAX(A.DATA_HORA)) AS DIAS
                             FROM AFERICAO A
                             WHERE A.TIPO_MEDICAO_COLETADA = 'PRESSAO'
                                OR A.TIPO_MEDICAO_COLETADA = 'SULCO_PRESSAO'
                             GROUP BY A.PLACA_VEICULO) AS PRESSAO ON PRESSAO.PLACA_INTERVALO = V.PLACA
                                LEFT JOIN
                            (SELECT A.PLACA_VEICULO                                             AS PLACA_INTERVALO,
                                    MAX(A.DATA_HORA AT TIME ZONE
                                        TZ_UNIDADE(A.COD_UNIDADE)) :: DATE                      AS DATA_ULTIMA_AFERICAO_SULCO,
                                    MAX(A.DATA_HORA AT TIME ZONE
                                        TZ_UNIDADE(A.COD_UNIDADE))                              AS DATA_HORA_ULTIMA_AFERICAO_SULCO,
                                    EXTRACT(DAYS FROM F_DATA_HORA_ATUAL_UTC - MAX(A.DATA_HORA)) AS DIAS
                             FROM AFERICAO A
                             WHERE A.TIPO_MEDICAO_COLETADA = 'SULCO'
                                OR A.TIPO_MEDICAO_COLETADA = 'SULCO_PRESSAO'
                             GROUP BY A.PLACA_VEICULO) AS SULCO ON SULCO.PLACA_INTERVALO = V.PLACA
                                JOIN PNEU_RESTRICAO_UNIDADE PRU
                                     ON PRU.COD_UNIDADE = V.COD_UNIDADE
                                JOIN UNIDADE U
                                     ON U.CODIGO = V.COD_UNIDADE
                       WHERE V.STATUS_ATIVO = TRUE
                         AND V.COD_UNIDADE = ANY (F_COD_UNIDADES)
                       ORDER BY U.CODIGO ASC, V.PLACA ASC)

             -- TODOS OS COALESCE FICAM AQUI.
        SELECT D.NOME_UNIDADE                                               AS NOME_UNIDADE,
               D.PLACA_VEICULO                                              AS PLACA_VEICULO,
               COALESCE(D.QTD_PNEUS_APLICADOS, '-')                         AS QTD_PNEUS_APLICADOS,
               D.NOME_MODELO_VEICULO                                        AS NOME_MODELO_VEICULO,
               D.NOME_TIPO_VEICULO                                          AS NOME_TIPO_VEICULO,
               CASE
                   WHEN D.SULCO_NUNCA_AFERIDO
                       THEN 'SULCO NUNCA AFERIDO'
                   WHEN NOT D.PODE_AFERIR_SULCO
                       THEN 'BLOQUEADO AFERIÇÃO'
                   WHEN D.AFERICAO_SULCO_VENCIDA
                       THEN 'VENCIDO'
                   ELSE 'NO PRAZO'
                   END                                                      AS STATUS_SULCO,
               CASE
                   WHEN D.PRESSAO_NUNCA_AFERIDA
                       THEN 'PRESSÃO NUNCA AFERIDA'
                   WHEN NOT D.PODE_AFERIR_PRESSAO
                       THEN 'BLOQUEADO AFERIÇÃO'
                   WHEN D.AFERICAO_PRESSAO_VENCIDA
                       THEN 'VENCIDO'
                   ELSE 'NO PRAZO'
                   END                                                      AS STATUS_PRESSAO,
               F_IF(NOT D.PODE_AFERIR_SULCO OR D.SULCO_NUNCA_AFERIDO,
                    '-',
                    D.DATA_VENCIMENTO_SULCO)                                AS DATA_VENCIMENTO_SULCO,
               F_IF(NOT D.PODE_AFERIR_PRESSAO OR D.PRESSAO_NUNCA_AFERIDA,
                    '-',
                    D.DATA_VENCIMENTO_PRESSAO)                              AS DATA_VENCIMENTO_PRESSAO,
               F_IF(NOT D.PODE_AFERIR_SULCO OR D.SULCO_NUNCA_AFERIDO,
                    '-',
                    D.DIAS_VENCIMENTO_SULCO)                                AS DIAS_VENCIMENTO_SULCO,
               F_IF(NOT D.PODE_AFERIR_PRESSAO OR D.PRESSAO_NUNCA_AFERIDA,
                    '-',
                    D.DIAS_VENCIMENTO_PRESSAO)                              AS DIAS_VENCIMENTO_PRESSAO,
               F_IF(NOT D.PODE_AFERIR_SULCO OR D.SULCO_NUNCA_AFERIDO,
                    '-',
                    D.DIAS_DESDE_ULTIMA_AFERICAO_SULCO)                     AS DIAS_DESDE_ULTIMA_AFERICAO_SULCO,
               D.DATA_HORA_ULTIMA_AFERICAO_SULCO                            AS DATA_HORA_ULTIMA_AFERICAO_SULCO,
               F_IF(NOT D.PODE_AFERIR_PRESSAO OR D.PRESSAO_NUNCA_AFERIDA,
                    '-',
                    D.DIAS_DESDE_ULTIMA_AFERICAO_PRESSAO)                   AS DIAS_DESDE_ULTIMA_AFERICAO_PRESSAO,
               D.DATA_HORA_ULTIMA_AFERICAO_PRESSAO                          AS DATA_HORA_ULTIMA_AFERICAO_PRESSAO,
               TO_CHAR(F_DATA_HORA_GERACAO_RELATORIO, 'DD/MM/YYYY HH24:MI') AS DATA_HORA_GERACAO_RELATORIO
        FROM DADOS D;
END;
$$;


DROP FUNCTION IF EXISTS FUNC_RELATORIO_DADOS_ULTIMA_AFERICAO_PNEU(F_COD_UNIDADES TEXT[]);
CREATE OR REPLACE FUNCTION FUNC_RELATORIO_DADOS_ULTIMA_AFERICAO_PNEU(F_COD_UNIDADES TEXT[])
    RETURNS TABLE
            (
                "UNIDADE ALOCADO"               TEXT,
                "PNEU"                          TEXT,
                "STATUS ATUAL"                  TEXT,
                "MARCA PNEU"                    TEXT,
                "MODELO PNEU"                   TEXT,
                "MEDIDAS"                       TEXT,
                "PLACA APLICADO"                TEXT,
                "MARCA VEÍCULO"                 TEXT,
                "MODELO VEÍCULO"                TEXT,
                "TIPO VEÍCULO"                  TEXT,
                "POSIÇÃO APLICADO"              TEXT,
                "SULCO INTERNO"                 TEXT,
                "SULCO CENTRAL INTERNO"         TEXT,
                "SULCO CENTRAL EXTERNO"         TEXT,
                "SULCO EXTERNO"                 TEXT,
                "MENOR SULCO"                   TEXT,
                "PRESSÃO (PSI)"                 TEXT,
                "VIDA ATUAL"                    TEXT,
                "DOT"                           TEXT,
                "ÚLTIMA AFERIÇÃO"               TEXT,
                "TIPO PROCESSO ÚLTIMA AFERIÇÃO" TEXT,
                "FORMA DE COLETA DOS DADOS"     TEXT
            )
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    -- Essa CTE busca o código da última aferição de cada pneu.
    -- Com o código nós conseguimos buscar depois qualquer outra informação da aferição.
    RETURN QUERY
        WITH CODS_AFERICOES AS (
            SELECT AV.COD_PNEU   AS COD_PNEU_AFERIDO,
                   MAX(A.CODIGO) AS COD_AFERICAO
            FROM AFERICAO A
                     JOIN AFERICAO_VALORES AV
                          ON AV.COD_AFERICAO = A.CODIGO
                     JOIN PNEU P ON P.CODIGO = AV.COD_PNEU
            WHERE P.COD_UNIDADE :: TEXT = ANY (F_COD_UNIDADES)
            GROUP BY AV.COD_PNEU
        ),

             ULTIMAS_AFERICOES AS (
                 SELECT CA.COD_PNEU_AFERIDO    AS COD_PNEU_AFERIDO,
                        A.DATA_HORA            AS DATA_HORA_AFERICAO,
                        A.COD_UNIDADE          AS COD_UNIDADE_AFERICAO,
                        A.TIPO_PROCESSO_COLETA AS TIPO_PROCESSO_COLETA,
                        A.FORMA_COLETA_DADOS   AS FORMA_COLETA_DADOS
                 FROM CODS_AFERICOES CA
                          JOIN AFERICAO A ON A.CODIGO = CA.COD_AFERICAO)

        SELECT U.NOME :: TEXT                                                   AS UNIDADE_ALOCADO,
               P.CODIGO_CLIENTE :: TEXT                                         AS COD_PNEU,
               P.STATUS :: TEXT                                                 AS STATUS_ATUAL,
               MAP.NOME :: TEXT                                                 AS NOME_MARCA,
               MP.NOME :: TEXT                                                  AS NOME_MODELO,
               ((((DP.LARGURA || '/' :: TEXT) || DP.ALTURA) || ' R' :: TEXT) ||
                DP.ARO)                                                         AS MEDIDAS,
               COALESCE(VP.PLACA, '-') :: TEXT                                  AS PLACA,
               COALESCE(MARV.NOME, '-') :: TEXT                                 AS MARCA_VEICULO,
               COALESCE(MODV.NOME, '-') :: TEXT                                 AS MODELO_VEICULO,
               COALESCE(VT.NOME, '-') :: TEXT                                   AS TIPO_VEICULO,
               COALESCE(PPNE.NOMENCLATURA:: TEXT, '-')                          AS POSICAO_PNEU,
               FUNC_PNEU_FORMAT_SULCO(P.ALTURA_SULCO_INTERNO)                   AS SULCO_INTERNO,
               FUNC_PNEU_FORMAT_SULCO(P.ALTURA_SULCO_CENTRAL_INTERNO)           AS SULCO_CENTRAL_INTERNO,
               FUNC_PNEU_FORMAT_SULCO(P.ALTURA_SULCO_CENTRAL_EXTERNO)           AS SULCO_CENTRAL_EXTERNO,
               FUNC_PNEU_FORMAT_SULCO(P.ALTURA_SULCO_EXTERNO)                   AS SULCO_EXTERNO,
               FUNC_PNEU_FORMAT_SULCO(LEAST(P.ALTURA_SULCO_EXTERNO, P.ALTURA_SULCO_CENTRAL_EXTERNO,
                                            P.ALTURA_SULCO_CENTRAL_INTERNO,
                                            P.ALTURA_SULCO_INTERNO))            AS MENOR_SULCO,
               REPLACE(COALESCE(TRUNC(P.PRESSAO_ATUAL) :: TEXT, '-'), '.', ',') AS PRESSAO_ATUAL,
               P.VIDA_ATUAL :: TEXT                                             AS VIDA_ATUAL,
               COALESCE(P.DOT, '-') :: TEXT                                     AS DOT,
               COALESCE(TO_CHAR(UA.DATA_HORA_AFERICAO AT TIME ZONE
                                tz_unidade(UA.COD_UNIDADE_AFERICAO),
                                'DD/MM/YYYY HH24:MI'),
                        'Nunca Aferido')                                        AS ULTIMA_AFERICAO,
               CASE
                   WHEN UA.TIPO_PROCESSO_COLETA IS NULL
                       THEN 'Nunca Aferido'
                   WHEN UA.TIPO_PROCESSO_COLETA = 'PLACA'
                       THEN 'Aferido em uma placa'
                   ELSE 'Aferido Avulso (em estoque)' END                       AS TIPO_PROCESSO_ULTIMA_AFERICAO,
               COALESCE(TAFCD.STATUS_LEGIVEL, '-')::TEXT                        AS FORMA_COLETA_DADOS
        FROM PNEU P
                 JOIN DIMENSAO_PNEU DP ON DP.CODIGO = P.COD_DIMENSAO
                 JOIN UNIDADE U ON U.CODIGO = P.COD_UNIDADE
                 JOIN EMPRESA E ON U.COD_EMPRESA = E.CODIGO
                 JOIN MODELO_PNEU MP ON MP.CODIGO = P.COD_MODELO AND MP.COD_EMPRESA = U.COD_EMPRESA
                 JOIN MARCA_PNEU MAP ON MAP.CODIGO = MP.COD_MARCA
                 LEFT JOIN VEICULO_PNEU VP
                           ON P.CODIGO = VP.COD_PNEU
                               AND P.COD_UNIDADE = VP.COD_UNIDADE
                 LEFT JOIN VEICULO V
                           ON VP.PLACA = V.PLACA
                               AND VP.COD_UNIDADE = V.COD_UNIDADE
                 LEFT JOIN VEICULO_TIPO VT
                           ON V.COD_TIPO = VT.CODIGO
                 LEFT JOIN VEICULO_DIAGRAMA VD ON VT.COD_DIAGRAMA = VD.CODIGO
                 LEFT JOIN PNEU_POSICAO_NOMENCLATURA_EMPRESA PPNE ON PPNE.COD_EMPRESA = E.CODIGO
            AND PPNE.COD_DIAGRAMA = VD.CODIGO
            AND PPNE.POSICAO_PROLOG = VP.POSICAO
                 LEFT JOIN MODELO_VEICULO MODV
                           ON MODV.CODIGO = V.COD_MODELO
                 LEFT JOIN MARCA_VEICULO MARV
                           ON MARV.CODIGO = MODV.COD_MARCA
                 LEFT JOIN ULTIMAS_AFERICOES UA
                           ON UA.COD_PNEU_AFERIDO = P.CODIGO
                 LEFT JOIN TYPES.AFERICAO_FORMA_COLETA_DADOS TAFCD
                           ON TAFCD.FORMA_COLETA_DADOS::TEXT = UA.FORMA_COLETA_DADOS::TEXT
        WHERE P.COD_UNIDADE :: TEXT = ANY (F_COD_UNIDADES)
        ORDER BY U.NOME, P.CODIGO_CLIENTE;
END;
$$;


ALTER TABLE AFERICAO_CONFIGURACAO_TIPO_AFERICAO_VEICULO
    ADD CONSTRAINT CHECK_FECHAMENTO_SERVICO_AFERICAO_CONFIGURACAO CHECK (
        FORMA_COLETA_DADOS_FECHAMENTO_SERVICO <> 'BLOQUEADO'
        );


DROP FUNCTION IF EXISTS FUNC_AFERICAO_GET_CONFIGURACOES_NOVA_AFERICAO_PLACA(F_PLACA_VEICULO TEXT);
CREATE OR REPLACE FUNCTION FUNC_AFERICAO_GET_CONFIGURACOES_NOVA_AFERICAO_PLACA(F_PLACA_VEICULO TEXT)
    RETURNS TABLE
            (
                SULCO_MINIMO_DESCARTE                  REAL,
                SULCO_MINIMO_RECAPAGEM                 REAL,
                TOLERANCIA_CALIBRAGEM                  REAL,
                TOLERANCIA_INSPECAO                    REAL,
                PERIODO_AFERICAO_SULCO                 INTEGER,
                PERIODO_AFERICAO_PRESSAO               INTEGER,
                FORMA_COLETA_DADOS_SULCO               TEXT,
                FORMA_COLETA_DADOS_PRESSAO             TEXT,
                FORMA_COLETA_DADOS_SULCO_PRESSAO       TEXT,
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
               CONFIG_PODE_AFERIR.FORMA_COLETA_DADOS_SULCO,
               CONFIG_PODE_AFERIR.FORMA_COLETA_DADOS_PRESSAO,
               CONFIG_PODE_AFERIR.FORMA_COLETA_DADOS_SULCO_PRESSAO,
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


DROP function IF EXISTS
    integracao.func_pneu_afericao_get_config_nova_afericao_placa(f_cod_unidade bigint,
    f_cod_auxiliar_tipo_veiculo text);
CREATE OR REPLACE FUNCTION
    INTEGRACAO.FUNC_PNEU_AFERICAO_GET_CONFIG_NOVA_AFERICAO_PLACA(F_COD_UNIDADE BIGINT,
                                                                 F_COD_AUXILIAR_TIPO_VEICULO TEXT)
    RETURNS TABLE
            (
                SULCO_MINIMO_DESCARTE                  REAL,
                SULCO_MINIMO_RECAPAGEM                 REAL,
                TOLERANCIA_INSPECAO                    REAL,
                TOLERANCIA_CALIBRAGEM                  REAL,
                PERIODO_AFERICAO_SULCO                 INTEGER,
                PERIODO_AFERICAO_PRESSAO               INTEGER,
                FORMA_COLETA_DADOS_SULCO               TEXT,
                FORMA_COLETA_DADOS_PRESSAO             TEXT,
                FORMA_COLETA_DADOS_SULCO_PRESSAO       TEXT,
                PODE_AFERIR_ESTEPE                     BOOLEAN,
                VARIACAO_ACEITA_SULCO_MENOR_MILIMETROS DOUBLE PRECISION,
                VARIACAO_ACEITA_SULCO_MAIOR_MILIMETROS DOUBLE PRECISION,
                BLOQUEAR_VALORES_MENORES               BOOLEAN,
                BLOQUEAR_VALORES_MAIORES               BOOLEAN,
                VARIACOES_SULCO_DEFAULT_PROLOG         BOOLEAN
            )
    LANGUAGE SQL
AS
$$
WITH COD_AUXILIARES AS (
    SELECT VT.CODIGO                                   AS COD_TIPO_VEICULO,
           REGEXP_SPLIT_TO_TABLE(VT.COD_AUXILIAR, ',') AS COD_AUXILIAR
    FROM VEICULO_TIPO VT
    WHERE VT.COD_EMPRESA = (SELECT U.COD_EMPRESA FROM UNIDADE U WHERE U.CODIGO = F_COD_UNIDADE)
)

SELECT PRU.SULCO_MINIMO_DESCARTE                                  AS SULCO_MINIMO_DESCARTE,
       PRU.SULCO_MINIMO_RECAPAGEM                                 AS SULCO_MINIMO_RECAPAGEM,
       PRU.TOLERANCIA_INSPECAO                                    AS TOLERANCIA_INSPECAO,
       PRU.TOLERANCIA_CALIBRAGEM                                  AS TOLERANCIA_CALIBRAGEM,
       PRU.PERIODO_AFERICAO_SULCO                                 AS PERIODO_AFERICAO_SULCO,
       PRU.PERIODO_AFERICAO_PRESSAO                               AS PERIODO_AFERICAO_PRESSAO,
       CONFIG_PODE_AFERIR.FORMA_COLETA_DADOS_SULCO                AS PODE_AFERIR_SULCO,
       CONFIG_PODE_AFERIR.FORMA_COLETA_DADOS_PRESSAO              AS PODE_AFERIR_PRESSAO,
       CONFIG_PODE_AFERIR.FORMA_COLETA_DADOS_SULCO_PRESSAO        AS PODE_AFERIR_SULCO_PRESSAO,
       CONFIG_PODE_AFERIR.PODE_AFERIR_ESTEPE                      AS PODE_AFERIR_ESTEPE,
       CONFIG_ALERTA_SULCO.VARIACAO_ACEITA_SULCO_MENOR_MILIMETROS AS VARIACAO_ACEITA_SULCO_MENOR_MILIMETROS,
       CONFIG_ALERTA_SULCO.VARIACAO_ACEITA_SULCO_MAIOR_MILIMETROS AS VARIACAO_ACEITA_SULCO_MAIOR_MILIMETROS,
       CONFIG_ALERTA_SULCO.BLOQUEAR_VALORES_MENORES               AS BLOQUEAR_VALORES_MENORES,
       CONFIG_ALERTA_SULCO.BLOQUEAR_VALORES_MAIORES               AS BLOQUEAR_VALORES_MAIORES,
       CONFIG_ALERTA_SULCO.USA_DEFAULT_PROLOG                     AS VARIACOES_SULCO_DEFAULT_PROLOG
FROM FUNC_AFERICAO_GET_CONFIG_TIPO_AFERICAO_VEICULO(F_COD_UNIDADE) AS CONFIG_PODE_AFERIR
         JOIN VIEW_AFERICAO_CONFIGURACAO_ALERTA_SULCO AS CONFIG_ALERTA_SULCO
              ON CONFIG_PODE_AFERIR.COD_UNIDADE_CONFIGURACAO = CONFIG_ALERTA_SULCO.COD_UNIDADE
         JOIN PNEU_RESTRICAO_UNIDADE PRU
              ON PRU.COD_UNIDADE = CONFIG_PODE_AFERIR.COD_UNIDADE_CONFIGURACAO
         JOIN COD_AUXILIARES CA ON CA.COD_AUXILIAR = F_COD_AUXILIAR_TIPO_VEICULO
WHERE CONFIG_PODE_AFERIR.COD_UNIDADE_CONFIGURACAO = F_COD_UNIDADE
  AND CONFIG_PODE_AFERIR.COD_TIPO_VEICULO = CA.COD_TIPO_VEICULO
  AND PRU.COD_UNIDADE = F_COD_UNIDADE;
$$;


DROP FUNCTION IF EXISTS FUNC_AFERICAO_GET_CRONOGRAMA_AFERICOES_PLACAS(F_COD_UNIDADES BIGINT[],
    F_DATA_HORA_ATUAL TIMESTAMP WITH TIME ZONE);
CREATE OR REPLACE FUNCTION FUNC_AFERICAO_GET_CRONOGRAMA_AFERICOES_PLACAS(F_COD_UNIDADES BIGINT[],
                                                                         F_DATA_HORA_ATUAL TIMESTAMP WITH TIME ZONE)
    RETURNS TABLE
            (
                PLACA                            TEXT,
                COD_UNIDADE_PLACA                BIGINT,
                NOME_MODELO                      TEXT,
                INTERVALO_PRESSAO                INTEGER,
                INTERVALO_SULCO                  INTEGER,
                PERIODO_AFERICAO_SULCO           INTEGER,
                PERIODO_AFERICAO_PRESSAO         INTEGER,
                PNEUS_APLICADOS                  INTEGER,
                STATUS_ATIVO_TIPO_VEICULO        BOOLEAN,
                FORMA_COLETA_DADOS_SULCO         TEXT,
                FORMA_COLETA_DADOS_PRESSAO       TEXT,
                FORMA_COLETA_DADOS_SULCO_PRESSAO TEXT,
                PODE_AFERIR_ESTEPE               BOOLEAN
            )
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    RETURN QUERY
        SELECT V.PLACA :: TEXT                                              AS PLACA,
               V.COD_UNIDADE :: BIGINT                                      AS COD_UNIDADE_PLACA,
               MV.NOME :: TEXT                                              AS NOME_MODELO,
               COALESCE(INTERVALO_PRESSAO.INTERVALO, -1) :: INTEGER         AS INTERVALO_PRESSAO,
               COALESCE(INTERVALO_SULCO.INTERVALO, -1) :: INTEGER           AS INTERVALO_SULCO,
               PRU.PERIODO_AFERICAO_SULCO                                   AS PERIODO_AFERICAO_SULCO,
               PRU.PERIODO_AFERICAO_PRESSAO                                 AS PERIODO_AFERICAO_PRESSAO,
               COALESCE(NUMERO_PNEUS.TOTAL, 0) :: INTEGER                   AS PNEUS_APLICADOS,
               VT.STATUS_ATIVO                                              AS STATUS_ATIVO_TIPO_VEICULO,
               F_IF(CONFIG.CODIGO IS NULL, 'EQUIPAMENTO',
                    CONFIG.FORMA_COLETA_DADOS_SULCO)                        AS FORMA_COLETA_DADOS_SULCO,
               F_IF(CONFIG.CODIGO IS NULL, 'EQUIPAMENTO',
                    CONFIG.FORMA_COLETA_DADOS_PRESSAO)                      AS FORMA_COLETA_DADOS_PRESSAO,
               F_IF(CONFIG.CODIGO IS NULL, 'EQUIPAMENTO',
                    CONFIG.FORMA_COLETA_DADOS_SULCO_PRESSAO)                AS FORMA_COLETA_DADOS_SULCO_PRESSAO,
               F_IF(CONFIG.CODIGO IS NULL, TRUE, CONFIG.PODE_AFERIR_ESTEPE) AS PODE_AFERIR_ESTEPE
        FROM VEICULO V
                 JOIN PNEU_RESTRICAO_UNIDADE PRU ON PRU.COD_UNIDADE = V.COD_UNIDADE
                 JOIN VEICULO_TIPO VT ON VT.CODIGO = V.COD_TIPO
                 JOIN MODELO_VEICULO MV ON MV.CODIGO = V.COD_MODELO
                 LEFT JOIN AFERICAO_CONFIGURACAO_TIPO_AFERICAO_VEICULO CONFIG
                           ON CONFIG.COD_TIPO_VEICULO = VT.CODIGO
                               AND CONFIG.COD_UNIDADE = V.COD_UNIDADE
                 LEFT JOIN (SELECT A.PLACA_VEICULO                                                            AS PLACA_INTERVALO,
                                   EXTRACT(DAYS FROM (F_DATA_HORA_ATUAL) -
                                                     MAX(A.DATA_HORA AT TIME ZONE TZ_UNIDADE(A.COD_UNIDADE))) AS INTERVALO
                            FROM AFERICAO A
                            WHERE A.TIPO_MEDICAO_COLETADA = 'PRESSAO'
                               OR A.TIPO_MEDICAO_COLETADA = 'SULCO_PRESSAO'
                            GROUP BY A.PLACA_VEICULO) AS INTERVALO_PRESSAO
                           ON INTERVALO_PRESSAO.PLACA_INTERVALO = V.PLACA
                 LEFT JOIN (SELECT A.PLACA_VEICULO                                                            AS PLACA_INTERVALO,
                                   EXTRACT(DAYS FROM (F_DATA_HORA_ATUAL) -
                                                     MAX(A.DATA_HORA AT TIME ZONE TZ_UNIDADE(A.COD_UNIDADE))) AS INTERVALO
                            FROM AFERICAO A
                            WHERE A.TIPO_MEDICAO_COLETADA = 'SULCO'
                               OR A.TIPO_MEDICAO_COLETADA = 'SULCO_PRESSAO'
                            GROUP BY A.PLACA_VEICULO) AS INTERVALO_SULCO
                           ON INTERVALO_SULCO.PLACA_INTERVALO = V.PLACA
                 LEFT JOIN (SELECT VP.PLACA           AS PLACA_PNEUS,
                                   COUNT(VP.COD_PNEU) AS TOTAL
                            FROM VEICULO_PNEU VP
                            WHERE VP.COD_UNIDADE = ANY (F_COD_UNIDADES)
                            GROUP BY VP.PLACA) AS NUMERO_PNEUS ON PLACA_PNEUS = V.PLACA
        WHERE V.STATUS_ATIVO = TRUE
          AND V.COD_UNIDADE = ANY (F_COD_UNIDADES)
        ORDER BY MV.NOME, INTERVALO_PRESSAO DESC, INTERVALO_SULCO DESC, PNEUS_APLICADOS DESC;
END;
$$;


DROP FUNCTION IF EXISTS INTEGRACAO.FUNC_PNEU_AFERICAO_GET_INFOS_CONFIGURACAO_AFERICAO(F_COD_UNIDADES BIGINT[]);
CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_PNEU_AFERICAO_GET_INFOS_CONFIGURACAO_AFERICAO(F_COD_UNIDADES BIGINT[])
    RETURNS TABLE
            (
                COD_AUXILIAR                     TEXT,
                COD_UNIDADE                      BIGINT,
                COD_TIPO_VEICULO                 BIGINT,
                FORMA_COLETA_DADOS_SULCO         TEXT,
                FORMA_COLETA_DADOS_PRESSAO       TEXT,
                FORMA_COLETA_DADOS_SULCO_PRESSAO TEXT,
                PODE_AFERIR_ESTEPE               BOOLEAN
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_COD_EMPRESA BIGINT := (SELECT U.COD_EMPRESA
                             FROM PUBLIC.UNIDADE U
                             WHERE U.CODIGO = ANY (F_COD_UNIDADES)
                             LIMIT 1);
BEGIN
    RETURN QUERY
        WITH COD_AUXILIARES AS (
            SELECT VT.CODIGO                                   AS COD_TIPO_VEICULO,
                   REGEXP_SPLIT_TO_TABLE(VT.COD_AUXILIAR, ',') AS COD_AUXILIAR
            FROM VEICULO_TIPO VT
            WHERE VT.COD_EMPRESA = V_COD_EMPRESA
        )

        SELECT CA.COD_AUXILIAR                        AS COD_AUXILIAR,
               ACTAV.COD_UNIDADE                      AS COD_UNIDADE,
               ACTAV.COD_TIPO_VEICULO                 AS COD_TIPO_VEICULO,
               ACTAV.FORMA_COLETA_DADOS_SULCO         AS FORMA_COLETA_DADOS_SULCO,
               ACTAV.FORMA_COLETA_DADOS_PRESSAO       AS FORMA_COLETA_DADOS_PRESSAO,
               ACTAV.FORMA_COLETA_DADOS_SULCO_PRESSAO AS FORMA_COLETA_DADOS_SULCO_PRESSAO,
               ACTAV.PODE_AFERIR_ESTEPE               AS PODE_AFERIR_ESTEPE
        FROM AFERICAO_CONFIGURACAO_TIPO_AFERICAO_VEICULO ACTAV
                 JOIN COD_AUXILIARES CA ON ACTAV.COD_TIPO_VEICULO = CA.COD_TIPO_VEICULO
        WHERE ACTAV.COD_UNIDADE = ANY (F_COD_UNIDADES)
          AND CA.COD_AUXILIAR IS NOT NULL;
END;
$$;


DROP FUNCTION IF EXISTS FUNC_AFERICAO_RELATORIO_QTD_DIAS_PLACAS_VENCIDAS(F_COD_UNIDADES BIGINT[],
    F_DATA_HOJE_UTC TIMESTAMP WITH TIME ZONE);
CREATE OR REPLACE FUNCTION FUNC_AFERICAO_RELATORIO_QTD_DIAS_PLACAS_VENCIDAS(F_COD_UNIDADES BIGINT[],
                                                                            F_DATA_HOJE_UTC TIMESTAMP WITH TIME ZONE)
    RETURNS TABLE
            (
                UNIDADE                           TEXT,
                PLACA                             TEXT,
                PODE_AFERIR_SULCO                 BOOLEAN,
                PODE_AFERIR_PRESSAO               BOOLEAN,
                QTD_DIAS_AFERICAO_SULCO_VENCIDA   INTEGER,
                QTD_DIAS_AFERICAO_PRESSAO_VENCIDA INTEGER
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    AFERICAO_SULCO         VARCHAR := 'SULCO';
    AFERICAO_PRESSAO       VARCHAR := 'PRESSAO';
    AFERICAO_SULCO_PRESSAO VARCHAR := 'SULCO_PRESSAO';
BEGIN
    RETURN QUERY
        WITH VEICULOS_ATIVOS_UNIDADES AS (
            SELECT V.PLACA
            FROM VEICULO V
            WHERE V.COD_UNIDADE = ANY (F_COD_UNIDADES)
              AND V.STATUS_ATIVO
        ),
             -- As CTEs ULTIMA_AFERICAO_SULCO e ULTIMA_AFERICAO_PRESSAO retornam a placa de cada veículo e a quantidade de dias
             -- que a aferição de sulco e pressão, respectivamente, estão vencidas. Um número negativo será retornado caso ainda
             -- esteja com a aferição no prazo e ele indicará quantos dias faltam para vencer. Um -20, por exemplo, significa
             -- que a placa vai vencer em 20 dias.
             ULTIMA_AFERICAO_SULCO AS (
                 SELECT DISTINCT ON (A.PLACA_VEICULO) A.COD_UNIDADE,
                                                      A.PLACA_VEICULO              AS PLACA,
                                                      DATE_PART('DAY', F_DATA_HOJE_UTC - MAX(DATA_HORA))
                                                          -
                                                      (PRU.PERIODO_AFERICAO_SULCO) AS QTD_DIAS_ENTRE_ULTIMA_AFERICAO_SULCO_E_HOJE
                 FROM AFERICAO A
                          JOIN PNEU_RESTRICAO_UNIDADE PRU
                               ON (SELECT V.COD_UNIDADE
                                   FROM VEICULO V
                                   WHERE V.PLACA = A.PLACA_VEICULO) = PRU.COD_UNIDADE
                 WHERE A.TIPO_MEDICAO_COLETADA IN (AFERICAO_SULCO, AFERICAO_SULCO_PRESSAO)
                   -- Desse modo nós buscamos a última aferição de cada placa que está ativa nas unidades filtradas, independente
                   -- de onde foram foram aferidas.
                   AND PLACA_VEICULO = ANY (SELECT VAU.PLACA
                                            FROM VEICULOS_ATIVOS_UNIDADES VAU)
                 GROUP BY A.DATA_HORA,
                          A.COD_UNIDADE,
                          A.PLACA_VEICULO,
                          PRU.PERIODO_AFERICAO_SULCO
                 ORDER BY A.PLACA_VEICULO, A.DATA_HORA DESC
             ),
             ULTIMA_AFERICAO_PRESSAO AS (
                 SELECT DISTINCT ON (A.PLACA_VEICULO) A.COD_UNIDADE,
                                                      A.PLACA_VEICULO                AS PLACA,
                                                      DATE_PART('DAY', F_DATA_HOJE_UTC - MAX(DATA_HORA))
                                                          -
                                                      (PRU.PERIODO_AFERICAO_PRESSAO) AS QTD_DIAS_ENTRE_ULTIMA_AFERICAO_PRESSAO_E_HOJE
                 FROM AFERICAO A
                          JOIN PNEU_RESTRICAO_UNIDADE PRU
                               ON (SELECT V.COD_UNIDADE
                                   FROM VEICULO V
                                   WHERE V.PLACA = A.PLACA_VEICULO) = PRU.COD_UNIDADE
                 WHERE A.COD_UNIDADE = ANY (F_COD_UNIDADES)
                   AND A.TIPO_MEDICAO_COLETADA IN (AFERICAO_PRESSAO, AFERICAO_SULCO_PRESSAO)
                   AND PLACA_VEICULO = ANY (SELECT VAU.PLACA
                                            FROM VEICULOS_ATIVOS_UNIDADES VAU)
                 GROUP BY A.DATA_HORA,
                          A.COD_UNIDADE,
                          A.PLACA_VEICULO,
                          PRU.PERIODO_AFERICAO_PRESSAO
                 ORDER BY A.PLACA_VEICULO, A.DATA_HORA DESC
             ),

             PRE_SELECT AS (
                 SELECT U.NOME                                            AS NOME_UNIDADE,
                        V.PLACA                                           AS PLACA_VEICULO,
                        COALESCE((
                                     SELECT (FA.FORMA_COLETA_DADOS_SULCO IN ('EQUIPAMENTO', 'MANUAL') OR
                                             FA.FORMA_COLETA_DADOS_SULCO_PRESSAO IN ('EQUIPAMENTO', 'MANUAL'))
                                     FROM FUNC_AFERICAO_GET_CONFIG_TIPO_AFERICAO_VEICULO(V.COD_UNIDADE) FA
                                     WHERE FA.COD_TIPO_VEICULO = V.COD_TIPO), FALSE)
                                                                          AS PODE_AFERIR_SULCO,
                        COALESCE((
                                     SELECT (FA.FORMA_COLETA_DADOS_PRESSAO IN ('EQUIPAMENTO', 'MANUAL') OR
                                             FA.FORMA_COLETA_DADOS_SULCO_PRESSAO IN ('EQUIPAMENTO', 'MANUAL'))
                                     FROM FUNC_AFERICAO_GET_CONFIG_TIPO_AFERICAO_VEICULO(V.COD_UNIDADE) FA
                                     WHERE FA.COD_TIPO_VEICULO = V.COD_TIPO), FALSE)
                                                                          AS PODE_AFERIR_PRESSAO,
                        -- Por conta do filtro no where, agora não é mais a diferença de dias e sim somente as vencidas (ou ainda
                        -- nunca aferidas).
                        UAS.QTD_DIAS_ENTRE_ULTIMA_AFERICAO_SULCO_E_HOJE   AS QTD_DIAS_AFERICAO_SULCO_VENCIDA,
                        UAP.QTD_DIAS_ENTRE_ULTIMA_AFERICAO_PRESSAO_E_HOJE AS QTD_DIAS_AFERICAO_PRESSAO_VENCIDA
                 FROM UNIDADE U
                          JOIN VEICULO V
                               ON V.COD_UNIDADE = U.CODIGO
                          LEFT JOIN ULTIMA_AFERICAO_SULCO UAS
                                    ON UAS.PLACA = V.PLACA
                          LEFT JOIN ULTIMA_AFERICAO_PRESSAO UAP
                                    ON UAP.PLACA = V.PLACA
                 WHERE
                     -- Se algum dos dois tipos de aferição estiver vencido, retornamos a linha.
                     (UAS.QTD_DIAS_ENTRE_ULTIMA_AFERICAO_SULCO_E_HOJE > 0 OR
                      UAP.QTD_DIAS_ENTRE_ULTIMA_AFERICAO_PRESSAO_E_HOJE > 0)
                 GROUP BY U.NOME,
                          V.PLACA,
                          V.COD_TIPO,
                          V.COD_UNIDADE,
                          UAS.QTD_DIAS_ENTRE_ULTIMA_AFERICAO_SULCO_E_HOJE,
                          UAP.QTD_DIAS_ENTRE_ULTIMA_AFERICAO_PRESSAO_E_HOJE
             )
        SELECT PS.NOME_UNIDADE::TEXT                         AS NOME_UNIDADE,
               PS.PLACA_VEICULO::TEXT                        AS PLACA_VEICULO,
               PS.PODE_AFERIR_SULCO                          AS PODE_AFERIR_SULCO,
               PS.PODE_AFERIR_PRESSAO                        AS PODE_AFERIR_PRESSAO,
               PS.QTD_DIAS_AFERICAO_SULCO_VENCIDA::INTEGER   AS QTD_DIAS_AFERICAO_SULCO_VENCIDA,
               PS.QTD_DIAS_AFERICAO_PRESSAO_VENCIDA::INTEGER AS QTD_DIAS_AFERICAO_PRESSAO_VENCIDA
        FROM PRE_SELECT PS
             -- Para a placa ser exibida, ao menos um dos tipos de aferições, de sulco ou pressão, devem estar habilitadas.
        WHERE PS.PODE_AFERIR_SULCO <> FALSE
           OR PS.PODE_AFERIR_PRESSAO <> FALSE
        ORDER BY PS.QTD_DIAS_AFERICAO_SULCO_VENCIDA DESC,
                 PS.QTD_DIAS_AFERICAO_PRESSAO_VENCIDA DESC;
END;
$$;


-- Cria nova coluna.
ALTER TABLE AFERICAO_MANUTENCAO_DATA
    ADD COLUMN FORMA_COLETA_DADOS_FECHAMENTO TEXT
        CONSTRAINT FK_FORMA_COLETA_DADOS_FECHAMENTO
            REFERENCES TYPES.AFERICAO_FORMA_COLETA_DADOS_TYPE
        CONSTRAINT CHECK_FORMAS_COLETA_DADOS_VALIDAS
            CHECK (FORMA_COLETA_DADOS_FECHAMENTO IS NULL OR FORMA_COLETA_DADOS_FECHAMENTO IN ('EQUIPAMENTO', 'MANUAL'));

-- Atualiza a constraint de check de estados dos serviços.
ALTER TABLE AFERICAO_MANUTENCAO_DATA DROP CONSTRAINT CHECK_ESTADOS_SERVICOS;
ALTER TABLE AFERICAO_MANUTENCAO_DATA ADD CONSTRAINT CHECK_ESTADOS_SERVICOS CHECK(
  CASE
  WHEN (TIPO_SERVICO::TEXT = 'MOVIMENTACAO'::TEXT)
    THEN ((ROW(
           DATA_HORA_RESOLUCAO,
           CPF_MECANICO,
           PSI_APOS_CONSERTO,
           KM_MOMENTO_CONSERTO,
           COD_PNEU_INSERIDO,
           COD_PROCESSO_MOVIMENTACAO,
           TEMPO_REALIZACAO_MILLIS) IS NULL
           AND
           FECHADO_AUTOMATICAMENTE_MOVIMENTACAO IS FALSE
           AND
           FECHADO_AUTOMATICAMENTE_INTEGRACAO IS FALSE
           AND
           FORMA_COLETA_DADOS_FECHAMENTO IS NULL) -- Verificamos se está pendente.
          OR
          (ROW(
           DATA_HORA_RESOLUCAO,
           CPF_MECANICO,
           PSI_APOS_CONSERTO,
           KM_MOMENTO_CONSERTO,
           COD_PNEU_INSERIDO,
           COD_PROCESSO_MOVIMENTACAO,
           TEMPO_REALIZACAO_MILLIS) IS NOT NULL
           AND
           FECHADO_AUTOMATICAMENTE_MOVIMENTACAO IS FALSE
           AND
           FECHADO_AUTOMATICAMENTE_INTEGRACAO IS FALSE
           AND
           FORMA_COLETA_DADOS_FECHAMENTO IS NOT NULL) -- Verificamos se está resolvido por mecânico.
          OR
          (ROW(
           DATA_HORA_RESOLUCAO,
           COD_PROCESSO_MOVIMENTACAO) IS NOT NULL
           AND
           FECHADO_AUTOMATICAMENTE_MOVIMENTACAO IS TRUE
           AND
           FECHADO_AUTOMATICAMENTE_INTEGRACAO IS FALSE
           AND
           FORMA_COLETA_DADOS_FECHAMENTO IS NULL) -- Verificamos se está resolvido por movimentação.
          OR
          (DATA_HORA_RESOLUCAO IS NOT NULL
           AND
           COD_PROCESSO_MOVIMENTACAO IS NULL
           AND
           FECHADO_AUTOMATICAMENTE_MOVIMENTACAO IS FALSE
           AND
           FECHADO_AUTOMATICAMENTE_INTEGRACAO IS TRUE
           AND
           FORMA_COLETA_DADOS_FECHAMENTO IS NULL)) -- Verificamos se está resolvido por integração.
  WHEN (TIPO_SERVICO::TEXT = 'CALIBRAGEM'::TEXT)
    THEN ((ROW(
           DATA_HORA_RESOLUCAO,
           CPF_MECANICO,
           PSI_APOS_CONSERTO,
           KM_MOMENTO_CONSERTO,
           COD_PNEU_INSERIDO,
           COD_PROCESSO_MOVIMENTACAO,
           TEMPO_REALIZACAO_MILLIS) IS NULL
           AND
           FECHADO_AUTOMATICAMENTE_MOVIMENTACAO IS FALSE
           AND
           FECHADO_AUTOMATICAMENTE_INTEGRACAO IS FALSE
           AND
           FORMA_COLETA_DADOS_FECHAMENTO IS NULL) -- Verificamos se está pendente.
          OR
          (ROW(
           DATA_HORA_RESOLUCAO,
           CPF_MECANICO,
           PSI_APOS_CONSERTO,
           KM_MOMENTO_CONSERTO,
           TEMPO_REALIZACAO_MILLIS) IS NOT NULL
           AND
           FECHADO_AUTOMATICAMENTE_MOVIMENTACAO IS FALSE
           AND
           FECHADO_AUTOMATICAMENTE_INTEGRACAO IS FALSE
           AND
           FORMA_COLETA_DADOS_FECHAMENTO IS NOT NULL) -- Verificamos se está resolvido por mecânico.
          OR
          (ROW(
           DATA_HORA_RESOLUCAO,
           COD_PROCESSO_MOVIMENTACAO) IS NOT NULL
           AND
           FECHADO_AUTOMATICAMENTE_MOVIMENTACAO IS TRUE
           AND
           FECHADO_AUTOMATICAMENTE_INTEGRACAO IS FALSE
           AND
           FORMA_COLETA_DADOS_FECHAMENTO IS NULL) -- Verificamos se está resolvido por movimentação.
          OR
          (DATA_HORA_RESOLUCAO IS NOT NULL
           AND
           COD_PROCESSO_MOVIMENTACAO IS NULL
           AND
           FECHADO_AUTOMATICAMENTE_MOVIMENTACAO IS FALSE
           AND
           FECHADO_AUTOMATICAMENTE_INTEGRACAO IS TRUE
           AND
           FORMA_COLETA_DADOS_FECHAMENTO IS NULL)) -- Verificamos se está resolvido por integração.
  WHEN (TIPO_SERVICO::TEXT = 'INSPECAO'::TEXT)
    THEN ((ROW(
           DATA_HORA_RESOLUCAO,
           CPF_MECANICO,
           PSI_APOS_CONSERTO,
           KM_MOMENTO_CONSERTO,
           COD_PNEU_INSERIDO,
           COD_PROCESSO_MOVIMENTACAO,
           TEMPO_REALIZACAO_MILLIS) IS NULL
           AND
           FECHADO_AUTOMATICAMENTE_MOVIMENTACAO IS FALSE
           AND
           FECHADO_AUTOMATICAMENTE_INTEGRACAO IS FALSE
           AND
           FORMA_COLETA_DADOS_FECHAMENTO IS NULL) -- Verificamos se está pendente.
          OR
          (ROW(
           DATA_HORA_RESOLUCAO,
           CPF_MECANICO,
           PSI_APOS_CONSERTO,
           KM_MOMENTO_CONSERTO,
           COD_ALTERNATIVA,
           TEMPO_REALIZACAO_MILLIS) IS NOT NULL
           AND
           FECHADO_AUTOMATICAMENTE_MOVIMENTACAO IS FALSE
           AND
           FECHADO_AUTOMATICAMENTE_INTEGRACAO IS FALSE
           AND
           FORMA_COLETA_DADOS_FECHAMENTO IS NOT NULL) -- Verificamos se está resolvido por mecânico.
          OR
          (ROW(
           DATA_HORA_RESOLUCAO,
           COD_PROCESSO_MOVIMENTACAO) IS NOT NULL
           AND
           FECHADO_AUTOMATICAMENTE_MOVIMENTACAO IS TRUE
           AND
           FECHADO_AUTOMATICAMENTE_INTEGRACAO IS FALSE
           AND
           FORMA_COLETA_DADOS_FECHAMENTO IS NULL) -- Verificamos se está resolvido por movimentação.
          OR
          (DATA_HORA_RESOLUCAO IS NOT NULL
           AND
           COD_PROCESSO_MOVIMENTACAO IS NULL
           AND
           FECHADO_AUTOMATICAMENTE_MOVIMENTACAO IS FALSE
           AND
           FECHADO_AUTOMATICAMENTE_INTEGRACAO IS TRUE
           AND
           FORMA_COLETA_DADOS_FECHAMENTO IS NULL)) -- Verificamos se está resolvido por integração.
  END
);

-- O que já foi fechado e não foi feito de forma automática setamos a forma de coleta como 'EQUIPAMENTO'.
-- Os demais (abertos e fechados automaticamente) ficam com forma de coleta NULL.
UPDATE AFERICAO_MANUTENCAO_DATA
SET FORMA_COLETA_DADOS_FECHAMENTO = 'EQUIPAMENTO'
WHERE DATA_HORA_RESOLUCAO IS NOT NULL
  AND (FECHADO_AUTOMATICAMENTE_INTEGRACAO IS NULL OR FECHADO_AUTOMATICAMENTE_INTEGRACAO IS FALSE)
  AND (FECHADO_AUTOMATICAMENTE_MOVIMENTACAO IS NULL OR FECHADO_AUTOMATICAMENTE_MOVIMENTACAO IS FALSE);

-- Altera a view para conter a nova coluna.
DROP VIEW AFERICAO_MANUTENCAO;
CREATE OR REPLACE VIEW AFERICAO_MANUTENCAO AS
SELECT AM.COD_AFERICAO,
       AM.COD_PNEU,
       AM.COD_UNIDADE,
       AM.TIPO_SERVICO,
       AM.DATA_HORA_RESOLUCAO,
       AM.CPF_MECANICO,
       AM.QT_APONTAMENTOS,
       AM.PSI_APOS_CONSERTO,
       AM.KM_MOMENTO_CONSERTO,
       AM.COD_ALTERNATIVA,
       AM.COD_PNEU_INSERIDO,
       AM.CODIGO,
       AM.COD_PROCESSO_MOVIMENTACAO,
       AM.TEMPO_REALIZACAO_MILLIS,
       AM.FECHADO_AUTOMATICAMENTE_MOVIMENTACAO,
       AM.FECHADO_AUTOMATICAMENTE_INTEGRACAO,
       AM.FORMA_COLETA_DADOS_FECHAMENTO
FROM AFERICAO_MANUTENCAO_DATA AM
WHERE (AM.DELETADO = FALSE);


-- Esse relatório teve apenas o nome alterado para ficar no padrão do outro de serviços.
DROP FUNCTION FUNC_RELATORIO_PNEU_EXTRATO_SERVICOS_ABERTOS(TEXT[], DATE, DATE, DATE);
CREATE OR REPLACE FUNCTION FUNC_PNEU_RELATORIO_EXTRATO_SERVICOS_ABERTOS(F_COD_UNIDADES BIGINT[],
                                                                        F_DATA_INICIAL DATE,
                                                                        F_DATA_FINAL DATE,
                                                                        F_DATA_ATUAL DATE)
    RETURNS TABLE
            (
                "UNIDADE DO SERVIÇO"            TEXT,
                "CÓDIGO DO SERVIÇO"             TEXT,
                "TIPO DO SERVIÇO"               TEXT,
                "QTD APONTAMENTOS"              TEXT,
                "DATA HORA ABERTURA"            TEXT,
                "QTD DIAS EM ABERTO"            TEXT,
                "NOME DO COLABORADOR"           TEXT,
                "PLACA"                         TEXT,
                "PNEU"                          TEXT,
                "POSIÇÃO PNEU ABERTURA SERVIÇO" TEXT,
                "MEDIDAS"                       TEXT,
                "COD AFERIÇÃO"                  TEXT,
                "SULCO INTERNO"                 TEXT,
                "SULCO CENTRAL INTERNO"         TEXT,
                "SULCO CENTRAL EXTERNO"         TEXT,
                "SULCO EXTERNO"                 TEXT,
                "MENOR SULCO"                   TEXT,
                "PRESSÃO (PSI)"                 TEXT,
                "PRESSÃO RECOMENDADA (PSI)"     TEXT,
                "ESTADO ATUAL"                  TEXT,
                "MÁXIMO DE RECAPAGENS"          TEXT
            )
    LANGUAGE SQL
AS
$$
SELECT U.NOME                                                                                   AS UNIDADE_SERVICO,
       AM.CODIGO :: TEXT                                                                        AS CODIGO_SERVICO,
       AM.TIPO_SERVICO                                                                          AS TIPO_SERVICO,
       AM.QT_APONTAMENTOS :: TEXT                                                               AS QT_APONTAMENTOS,
       TO_CHAR((A.DATA_HORA AT TIME ZONE TZ_UNIDADE(AM.COD_UNIDADE)),
               'DD/MM/YYYY HH24:MI') :: TEXT                                                    AS DATA_HORA_ABERTURA,
       (F_DATA_ATUAL - ((A.DATA_HORA AT TIME ZONE TZ_UNIDADE(AM.COD_UNIDADE)) :: DATE)) :: TEXT AS DIAS_EM_ABERTO,
       C.NOME                                                                                   AS NOME_COLABORADOR,
       A.PLACA_VEICULO                                                                          AS PLACA_VEICULO,
       P.CODIGO_CLIENTE                                                                         AS COD_PNEU_PROBLEMA,
       COALESCE(PPNE.NOMENCLATURA :: TEXT, '-')                                                 AS POSICAO_PNEU_PROBLEMA,
       DP.LARGURA || '/' :: TEXT || DP.ALTURA || ' R' :: TEXT || DP.ARO                         AS MEDIDAS,
       A.CODIGO :: TEXT                                                                         AS COD_AFERICAO,
       FUNC_PNEU_FORMAT_SULCO(AV.ALTURA_SULCO_INTERNO)                                          AS SULCO_INTERNO,
       FUNC_PNEU_FORMAT_SULCO(AV.ALTURA_SULCO_CENTRAL_INTERNO)                                  AS SULCO_CENTRAL_INTERNO,
       FUNC_PNEU_FORMAT_SULCO(AV.ALTURA_SULCO_CENTRAL_EXTERNO)                                  AS SULCO_CENTRAL_EXTERNO,
       FUNC_PNEU_FORMAT_SULCO(AV.ALTURA_SULCO_EXTERNO)                                          AS SULCO_EXTERNO,
       FUNC_PNEU_FORMAT_SULCO(LEAST(AV.ALTURA_SULCO_EXTERNO, AV.ALTURA_SULCO_CENTRAL_EXTERNO,
                                    AV.ALTURA_SULCO_CENTRAL_INTERNO,
                                    AV.ALTURA_SULCO_INTERNO))                                   AS MENOR_SULCO,
       REPLACE(COALESCE(TRUNC(AV.PSI) :: TEXT, '-'), '.', ',')                                  AS PRESSAO_PNEU_PROBLEMA,
       REPLACE(COALESCE(TRUNC(P.PRESSAO_RECOMENDADA) :: TEXT, '-'), '.',
               ',')                                                                             AS PRESSAO_RECOMENDADA,
       PVN.NOME                                                                                 AS VIDA_PNEU_PROBLEMA,
       PRN.NOME                                                                                 AS TOTAL_RECAPAGENS
FROM AFERICAO_MANUTENCAO AM
         JOIN PNEU P
              ON AM.COD_PNEU = P.CODIGO
         JOIN DIMENSAO_PNEU DP
              ON DP.CODIGO = P.COD_DIMENSAO
         JOIN AFERICAO A
              ON A.CODIGO = AM.COD_AFERICAO
         JOIN COLABORADOR C
              ON A.CPF_AFERIDOR = C.CPF
         JOIN AFERICAO_VALORES AV
              ON AV.COD_AFERICAO = AM.COD_AFERICAO
                  AND AV.COD_PNEU = AM.COD_PNEU
         JOIN UNIDADE U
              ON U.CODIGO = AM.COD_UNIDADE
         JOIN EMPRESA E
              ON U.COD_EMPRESA = E.CODIGO
         JOIN PNEU_VIDA_NOMENCLATURA PVN
              ON PVN.COD_VIDA = AV.VIDA_MOMENTO_AFERICAO
         JOIN PNEU_RECAPAGEM_NOMENCLATURA PRN
              ON PRN.COD_TOTAL_VIDA = P.VIDA_TOTAL
         JOIN VEICULO V
              ON A.PLACA_VEICULO = V.PLACA
                  AND V.COD_UNIDADE = A.COD_UNIDADE
         LEFT JOIN VEICULO_TIPO VT
                   ON V.COD_TIPO = VT.CODIGO
         LEFT JOIN VEICULO_DIAGRAMA VD ON VT.COD_DIAGRAMA = VD.CODIGO
         LEFT JOIN PNEU_POSICAO_NOMENCLATURA_EMPRESA PPNE ON PPNE.COD_EMPRESA = E.CODIGO
    AND PPNE.COD_DIAGRAMA = VD.CODIGO
    AND AV.POSICAO = PPNE.POSICAO_PROLOG
WHERE AM.COD_UNIDADE = ANY (F_COD_UNIDADES)
  AND (A.DATA_HORA AT TIME ZONE TZ_UNIDADE(AM.COD_UNIDADE)) :: DATE >= F_DATA_INICIAL
  AND (A.DATA_HORA AT TIME ZONE TZ_UNIDADE(AM.COD_UNIDADE)) :: DATE <= F_DATA_FINAL
  AND AM.DATA_HORA_RESOLUCAO IS NULL
  AND (AM.FECHADO_AUTOMATICAMENTE_MOVIMENTACAO = FALSE OR AM.FECHADO_AUTOMATICAMENTE_MOVIMENTACAO IS NULL)
  AND (AM.FECHADO_AUTOMATICAMENTE_INTEGRACAO = FALSE OR AM.FECHADO_AUTOMATICAMENTE_INTEGRACAO IS NULL)
ORDER BY U.NOME, A.DATA_HORA;
$$;

-- Nome alterado e adicionado ao retorno a forma de coleta dos dados no fechamento.
DROP FUNCTION FUNC_RELATORIO_PNEU_EXTRATO_SERVICOS_FECHADOS(TEXT[], DATE, DATE);
CREATE OR REPLACE FUNCTION FUNC_PNEU_RELATORIO_EXTRATO_SERVICOS_FECHADOS(F_COD_UNIDADES BIGINT[],
                                                                         F_DATA_INICIAL DATE,
                                                                         F_DATA_FINAL DATE)
    RETURNS TABLE
            (
                "UNIDADE DO SERVIÇO"               TEXT,
                "DATA AFERIÇÃO"                    TEXT,
                "DATA RESOLUÇÃO"                   TEXT,
                "HORAS PARA RESOLVER"              DOUBLE PRECISION,
                "MINUTOS PARA RESOLVER"            DOUBLE PRECISION,
                "PLACA"                            TEXT,
                "KM AFERIÇÃO"                      BIGINT,
                "KM CONSERTO"                      BIGINT,
                "KM PERCORRIDO"                    BIGINT,
                "COD PNEU"                         CHARACTER VARYING,
                "PRESSÃO RECOMENDADA"              REAL,
                "PRESSÃO AFERIÇÃO"                 TEXT,
                "DISPERSÃO RECOMENDADA X AFERIÇÃO" TEXT,
                "PRESSÃO INSERIDA"                 TEXT,
                "DISPERSÃO RECOMENDADA X INSERIDA" TEXT,
                "POSIÇÃO PNEU ABERTURA SERVIÇO"    TEXT,
                "SERVIÇO"                          TEXT,
                "MECÂNICO"                         TEXT,
                "PROBLEMA APONTADO (INSPEÇÃO)"     TEXT,
                "FECHADO AUTOMATICAMENTE"          TEXT,
                "FORMA DE COLETA DOS DADOS"        TEXT
            )
    LANGUAGE SQL
AS
$$
SELECT U.NOME                                                                       AS UNIDADE_SERVICO,
       TO_CHAR((A.DATA_HORA AT TIME ZONE TZ_UNIDADE(AM.COD_UNIDADE)),
               'DD/MM/YYYY HH24:MI:SS')                                             AS DATA_HORA_AFERICAO,
       TO_CHAR((AM.DATA_HORA_RESOLUCAO AT TIME ZONE TZ_UNIDADE(AM.COD_UNIDADE)),
               'DD/MM/YYYY HH24:MI:SS')                                             AS DATA_HORA_RESOLUCAO,
       TRUNC(EXTRACT(EPOCH FROM ((AM.DATA_HORA_RESOLUCAO) - (A.DATA_HORA))) /
             3600)                                                                  AS HORAS_RESOLUCAO,
       TRUNC(
               EXTRACT(EPOCH FROM ((AM.DATA_HORA_RESOLUCAO) - (A.DATA_HORA))) / 60) AS MINUTOS_RESOLUCAO,
       A.PLACA_VEICULO                                                              AS PLACA_VEICULO,
       A.KM_VEICULO                                                                 AS KM_AFERICAO,
       AM.KM_MOMENTO_CONSERTO                                                       AS KM_MOMENTO_CONSERTO,
       AM.KM_MOMENTO_CONSERTO - A.KM_VEICULO                                        AS KM_PERCORRIDO,
       P.CODIGO_CLIENTE                                                             AS CODIGO_CLIENTE_PNEU,
       P.PRESSAO_RECOMENDADA                                                        AS PRESSAO_RECOMENDADA_PNEU,
       COALESCE(REPLACE(ROUND(AV.PSI :: NUMERIC, 2) :: TEXT, '.', ','),
                '-')                                                                AS PSI_AFERICAO,
       COALESCE(REPLACE(ROUND((((AV.PSI / P.PRESSAO_RECOMENDADA) - 1) * 100) :: NUMERIC, 2) || '%', '.', ','),
                '-')                                                                AS DISPERSAO_PRESSAO_ANTES,
       COALESCE(REPLACE(ROUND(AM.PSI_APOS_CONSERTO :: NUMERIC, 2) :: TEXT, '.', ','),
                '-')                                                                AS PSI_POS_CONSERTO,
       COALESCE(REPLACE(ROUND((((AM.PSI_APOS_CONSERTO / P.PRESSAO_RECOMENDADA) - 1) * 100) :: NUMERIC, 2) || '%', '.',
                        ','),
                '-')                                                                AS DISPERSAO_PRESSAO_DEPOIS,
       COALESCE(PPNE.NOMENCLATURA, '-')                                             AS POSICAO,
       AM.TIPO_SERVICO                                                              AS TIPO_SERVICO,
       COALESCE(INITCAP(C.NOME), '-')                                               AS NOME_MECANICO,
       COALESCE(AA.ALTERNATIVA, '-')                                                AS PROBLEMA_APONTADO,
       F_IF(AM.FECHADO_AUTOMATICAMENTE_MOVIMENTACAO OR AM.FECHADO_AUTOMATICAMENTE_INTEGRACAO, 'Sim' :: TEXT,
            'Não')                                                                  AS TIPO_FECHAMENTO,
       COALESCE(AFCD.STATUS_LEGIVEL, '-')                                           AS FORMA_COLETA_DADOS_FECHAMENTO
FROM AFERICAO_MANUTENCAO AM
         JOIN UNIDADE U
              ON AM.COD_UNIDADE = U.CODIGO
         JOIN AFERICAO_VALORES AV
              ON AM.COD_UNIDADE = AV.COD_UNIDADE
                  AND AM.COD_AFERICAO = AV.COD_AFERICAO
                  AND AM.COD_PNEU = AV.COD_PNEU
         JOIN AFERICAO A
              ON A.CODIGO = AV.COD_AFERICAO
         LEFT JOIN COLABORADOR C
                   ON AM.CPF_MECANICO = C.CPF
         JOIN PNEU P
              ON P.CODIGO = AV.COD_PNEU
         LEFT JOIN VEICULO_PNEU VP
                   ON VP.COD_PNEU = P.CODIGO
                       AND VP.COD_UNIDADE = P.COD_UNIDADE
         LEFT JOIN AFERICAO_ALTERNATIVA_MANUTENCAO_INSPECAO AA
                   ON AA.CODIGO = AM.COD_ALTERNATIVA
         LEFT JOIN VEICULO V
                   ON V.PLACA = VP.PLACA
         LEFT JOIN EMPRESA E
                   ON U.COD_EMPRESA = E.CODIGO
         LEFT JOIN VEICULO_TIPO VT
                   ON E.CODIGO = VT.COD_EMPRESA
                       AND VT.CODIGO = V.COD_TIPO
         LEFT JOIN PNEU_POSICAO_NOMENCLATURA_EMPRESA PPNE
                   ON PPNE.COD_EMPRESA = P.COD_EMPRESA
                       AND PPNE.COD_DIAGRAMA = VT.COD_DIAGRAMA
                       AND PPNE.POSICAO_PROLOG = AV.POSICAO
         LEFT JOIN TYPES.AFERICAO_FORMA_COLETA_DADOS AFCD
                   ON AFCD.FORMA_COLETA_DADOS = AM.FORMA_COLETA_DADOS_FECHAMENTO
WHERE AV.COD_UNIDADE = ANY (F_COD_UNIDADES)
  AND AM.DATA_HORA_RESOLUCAO IS NOT NULL
  AND (AM.DATA_HORA_RESOLUCAO AT TIME ZONE TZ_UNIDADE(AM.COD_UNIDADE)) :: DATE >= F_DATA_INICIAL
  AND (AM.DATA_HORA_RESOLUCAO AT TIME ZONE TZ_UNIDADE(AM.COD_UNIDADE)) :: DATE <= F_DATA_FINAL
ORDER BY U.NOME, A.DATA_HORA DESC
$$;