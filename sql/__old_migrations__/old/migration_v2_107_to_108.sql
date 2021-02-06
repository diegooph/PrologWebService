BEGIN TRANSACTION;

--######################################################################################################################
--######################################################################################################################
-- PL-2269
ALTER TABLE PRONTUARIO_CONDUTOR_CONSOLIDADO ADD COLUMN FADIGAS_CELULAR INTEGER;
ALTER TABLE PRONTUARIO_CONDUTOR_CONSOLIDADO ADD COLUMN FADIGAS_CONSUMO_ALIMENTO INTEGER;
ALTER TABLE PRONTUARIO_CONDUTOR_CONSOLIDADO ADD COLUMN FADIGAS_FUMANDO INTEGER;
ALTER TABLE PRONTUARIO_CONDUTOR_CONSOLIDADO ADD COLUMN FADIGAS_OCLUSAO INTEGER;
ALTER TABLE PRONTUARIO_CONDUTOR_CONSOLIDADO ADD COLUMN FADIGAS_SEM_CINTO INTEGER;

UPDATE PRONTUARIO_CONDUTOR_CONSOLIDADO
SET FADIGAS_CELULAR          = 0,
    FADIGAS_CONSUMO_ALIMENTO = 0,
    FADIGAS_FUMANDO          = 0,
    FADIGAS_OCLUSAO          = 0,
    FADIGAS_SEM_CINTO        = 0;

ALTER TABLE PRONTUARIO_CONDUTOR_CONSOLIDADO ALTER COLUMN FADIGAS_CELULAR SET NOT NULL;
ALTER TABLE PRONTUARIO_CONDUTOR_CONSOLIDADO ALTER COLUMN FADIGAS_CONSUMO_ALIMENTO SET NOT NULL;
ALTER TABLE PRONTUARIO_CONDUTOR_CONSOLIDADO ALTER COLUMN FADIGAS_FUMANDO SET NOT NULL;
ALTER TABLE PRONTUARIO_CONDUTOR_CONSOLIDADO ALTER COLUMN FADIGAS_OCLUSAO SET NOT NULL;
ALTER TABLE PRONTUARIO_CONDUTOR_CONSOLIDADO ALTER COLUMN FADIGAS_SEM_CINTO SET NOT NULL;

CREATE OR REPLACE FUNCTION FUNC_PRONTUARIO_INSERT_OR_UPDATE(F_CPF_COLABORADOR BIGINT,
                                                            F_STATUS TEXT,
                                                            F_MOTIVO TEXT,
                                                            F_PONTUACAO INTEGER,
                                                            F_VENCIMENTO_CNH DATE,
                                                            F_DOCUMENTOS_RS TEXT,
                                                            F_DOCUMENTOS_EC TEXT,
                                                            F_DOCUMENTOS_IT TEXT,
                                                            F_PONTUACAO_PONDERADA REAL,
                                                            F_ACIDENTES_FAI INTEGER,
                                                            F_ACIDENTES_LTI INTEGER,
                                                            F_ACIDENTES_MDI INTEGER,
                                                            F_ACIDENTES_MTI INTEGER,
                                                            F_CAPOTAMENTOS INTEGER,
                                                            F_COLISOES INTEGER,
                                                            F_TOMBAMENTOS INTEGER,
                                                            F_FADIGAS_CELULAR INTEGER,
                                                            F_FADIGAS_CONSUMO_ALIMENTO INTEGER,
                                                            F_FADIGAS_FUMANDO INTEGER,
                                                            F_FADIGAS_OCLUSAO INTEGER,
                                                            F_FADIGAS_SEM_CINTO INTEGER,
                                                            F_MULTAS_LEVE INTEGER,
                                                            F_MULTAS_MEDIA INTEGER,
                                                            F_MULTAS_GRAVE INTEGER,
                                                            F_MULTAS_GRAVISSIMA INTEGER,
                                                            F_SAC_IMPERICIA INTEGER,
                                                            F_SAC_IMPRUDENCIA INTEGER,
                                                            F_SAV_IMPERICIA INTEGER,
                                                            F_SAV_IMPRUDENCIA INTEGER,
                                                            F_ADVERTENCIAS INTEGER,
                                                            F_SUSPENSOES INTEGER,
                                                            F_EXCESSO_VELOCIDADE_1 INTEGER,
                                                            F_EXCESSO_VELOCIDADE_2 INTEGER,
                                                            F_EXCESSO_VELOCIDADE_3 INTEGER,
                                                            F_FORCA_G INTEGER,
                                                            F_FRENAGEM_BRUSCA INTEGER,
                                                            F_POWER_ON INTEGER,
                                                            F_DATA_ATUALIZACAO TIMESTAMP WITH TIME ZONE)
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    INSERT INTO PRONTUARIO_CONDUTOR_CONSOLIDADO (CPF_COLABORADOR,
                                                 STATUS,
                                                 MOTIVO,
                                                 PONTUACAO,
                                                 VENCIMENTO_CNH,
                                                 DOCUMENTOS_RS,
                                                 DOCUMENTOS_EC,
                                                 DOCUMENTOS_IT,
                                                 PONTUACAO_PONDERADA,
                                                 ACIDENTES_FAI,
                                                 ACIDENTES_LTI,
                                                 ACIDENTES_MDI,
                                                 ACIDENTES_MTI,
                                                 CAPOTAMENTOS,
                                                 COLISOES,
                                                 TOMBAMENTOS,
                                                 FADIGAS_CELULAR,
                                                 FADIGAS_CONSUMO_ALIMENTO,
                                                 FADIGAS_FUMANDO,
                                                 FADIGAS_OCLUSAO,
                                                 FADIGAS_SEM_CINTO,
                                                 MULTAS_LEVE,
                                                 MULTAS_MEDIA,
                                                 MULTAS_GRAVE,
                                                 MULTAS_GRAVISSIMA,
                                                 SAC_IMPERICIA,
                                                 SAC_IMPRUDENCIA,
                                                 SAV_IMPERICIA,
                                                 SAV_IMPRUDENCIA,
                                                 ADVERTENCIAS,
                                                 SUSPENSOES,
                                                 EXCESSO_VELOCIDADE_1,
                                                 EXCESSO_VELOCIDADE_2,
                                                 EXCESSO_VELOCIDADE_3,
                                                 FORCA_G,
                                                 FRENAGEM_BRUSCA,
                                                 POWER_ON,
                                                 DATA_ATUALIZACAO)
    VALUES (F_CPF_COLABORADOR,
            F_STATUS,
            F_MOTIVO,
            F_PONTUACAO,
            F_VENCIMENTO_CNH,
            F_DOCUMENTOS_RS,
            F_DOCUMENTOS_EC,
            F_DOCUMENTOS_IT,
            F_PONTUACAO_PONDERADA,
            F_ACIDENTES_FAI,
            F_ACIDENTES_LTI,
            F_ACIDENTES_MDI,
            F_ACIDENTES_MTI,
            F_CAPOTAMENTOS,
            F_COLISOES,
            F_TOMBAMENTOS,
            F_FADIGAS_CELULAR,
            F_FADIGAS_CONSUMO_ALIMENTO,
            F_FADIGAS_FUMANDO,
            F_FADIGAS_OCLUSAO,
            F_FADIGAS_SEM_CINTO,
            F_MULTAS_LEVE,
            F_MULTAS_MEDIA,
            F_MULTAS_GRAVE,
            F_MULTAS_GRAVISSIMA,
            F_SAC_IMPERICIA,
            F_SAC_IMPRUDENCIA,
            F_SAV_IMPERICIA,
            F_SAV_IMPRUDENCIA,
            F_ADVERTENCIAS,
            F_SUSPENSOES,
            F_EXCESSO_VELOCIDADE_1,
            F_EXCESSO_VELOCIDADE_2,
            F_EXCESSO_VELOCIDADE_3,
            F_FORCA_G,
            F_FRENAGEM_BRUSCA,
            F_POWER_ON,
            F_DATA_ATUALIZACAO)
    ON CONFLICT ON CONSTRAINT PK_PRONTUARIO_CONDUTOR_CONSOLIDADO
        DO UPDATE SET CPF_COLABORADOR          = F_CPF_COLABORADOR,
                      STATUS                   = F_STATUS,
                      MOTIVO                   = F_MOTIVO,
                      PONTUACAO                = F_PONTUACAO,
                      VENCIMENTO_CNH           = F_VENCIMENTO_CNH,
                      DOCUMENTOS_RS            = F_DOCUMENTOS_RS,
                      DOCUMENTOS_EC            = F_DOCUMENTOS_EC,
                      DOCUMENTOS_IT            = F_DOCUMENTOS_IT,
                      PONTUACAO_PONDERADA      = F_PONTUACAO_PONDERADA,
                      ACIDENTES_FAI            = F_ACIDENTES_FAI,
                      ACIDENTES_LTI            = F_ACIDENTES_LTI,
                      ACIDENTES_MDI            = F_ACIDENTES_MDI,
                      ACIDENTES_MTI            = F_ACIDENTES_MTI,
                      CAPOTAMENTOS             = F_CAPOTAMENTOS,
                      COLISOES                 = F_COLISOES,
                      TOMBAMENTOS              = F_TOMBAMENTOS,
                      FADIGAS_CELULAR          = F_FADIGAS_CELULAR,
                      FADIGAS_CONSUMO_ALIMENTO = F_FADIGAS_CONSUMO_ALIMENTO,
                      FADIGAS_FUMANDO          = F_FADIGAS_FUMANDO,
                      FADIGAS_OCLUSAO          = F_FADIGAS_OCLUSAO,
                      FADIGAS_SEM_CINTO        = F_FADIGAS_SEM_CINTO,
                      MULTAS_LEVE              = F_MULTAS_LEVE,
                      MULTAS_MEDIA             = F_MULTAS_MEDIA,
                      MULTAS_GRAVE             = F_MULTAS_GRAVE,
                      MULTAS_GRAVISSIMA        = F_MULTAS_GRAVISSIMA,
                      SAC_IMPERICIA            = F_SAC_IMPERICIA,
                      SAC_IMPRUDENCIA          = F_SAC_IMPRUDENCIA,
                      SAV_IMPERICIA            = F_SAV_IMPERICIA,
                      SAV_IMPRUDENCIA          = F_SAV_IMPRUDENCIA,
                      ADVERTENCIAS             = F_ADVERTENCIAS,
                      SUSPENSOES               = F_SUSPENSOES,
                      EXCESSO_VELOCIDADE_1     = F_EXCESSO_VELOCIDADE_1,
                      EXCESSO_VELOCIDADE_2     = F_EXCESSO_VELOCIDADE_2,
                      EXCESSO_VELOCIDADE_3     = F_EXCESSO_VELOCIDADE_3,
                      FORCA_G                  = F_FORCA_G,
                      FRENAGEM_BRUSCA          = F_FRENAGEM_BRUSCA,
                      POWER_ON                 = F_POWER_ON,
                      DATA_ATUALIZACAO         = F_DATA_ATUALIZACAO;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--####################################  FUNC_COLABORADOR_TRANSFERE_ENTRE_UNIDADE   #####################################
--######################################################################################################################
--######################################################################################################################
-- PL-2377
CREATE OR REPLACE FUNCTION SUPORTE.FUNC_COLABORADOR_TRANSFERE_ENTRE_UNIDADES(F_COD_EMPRESA_ORIGEM BIGINT,
                                                                             F_COD_UNIDADE_ORIGEM BIGINT,
                                                                             F_COD_UNIDADE_DESTINO BIGINT,
                                                                             F_CPF_COLABORADOR BIGINT,
                                                                             F_COD_SETOR_DESTINO BIGINT,
                                                                             F_COD_EQUIPE_DESTINO BIGINT,
                                                                             F_COD_FUNCAO_DESTINO INTEGER,
                                                                             OUT COLABORADOR_TRANSFERIDO TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
BEGIN
    -- VERIFICA SE EMPRESA ORIGEM POSSUI UNIDADE ORIGEM.
    PERFORM FUNC_GARANTE_EMPRESA_POSSUI_UNIDADE(F_COD_EMPRESA_ORIGEM, F_COD_UNIDADE_ORIGEM);

    -- VERIFICA SE EMPRESA POSSUI UNIDADE DESTINO.
    PERFORM FUNC_GARANTE_EMPRESA_POSSUI_UNIDADE(F_COD_EMPRESA_ORIGEM, F_COD_UNIDADE_DESTINO);

    -- VERIFICA SE COLABORADOR EXISTE NA UNIDADE ORIGEM.
    PERFORM FUNC_GARANTE_INTEGRIDADE_UNIDADE_COLABORADOR(F_COD_UNIDADE_ORIGEM, F_CPF_COLABORADOR);

    -- VERIFICA SE O SETOR EXISTE NA UNIDADE DESTINO.
    PERFORM FUNC_GARANTE_SETOR_EXISTE(F_COD_UNIDADE_DESTINO, F_COD_SETOR_DESTINO);

    -- VERIFICA SE A EQUIPE EXISTE NA UNIDADE DESTINO.
    PERFORM FUNC_GARANTE_EQUIPE_EXISTE(F_COD_UNIDADE_DESTINO, F_COD_EQUIPE_DESTINO);

    -- VERIFICA SE A FUNÇÃO EXISTE NA EMPRESA DESTINO.
    PERFORM FUNC_GARANTE_CARGO_EXISTE(F_COD_EMPRESA_ORIGEM, F_COD_FUNCAO_DESTINO);

    -- TRANSFERE COLABORADOR.
    UPDATE COLABORADOR_DATA
    SET COD_UNIDADE  = F_COD_UNIDADE_DESTINO,
        COD_SETOR    = F_COD_SETOR_DESTINO,
        COD_EQUIPE   = F_COD_EQUIPE_DESTINO,
        COD_FUNCAO   = F_COD_FUNCAO_DESTINO,
        -- Também ativa o colaborador ao transferir.
        STATUS_ATIVO = TRUE
    WHERE CPF = F_CPF_COLABORADOR
      AND COD_UNIDADE = F_COD_UNIDADE_ORIGEM;

    -- MENSAGEM DE SUCESSO.
    SELECT ('COLABORADOR COM CPF: '
                || (SELECT C.CPF
                    FROM COLABORADOR C
                    WHERE C.CPF = F_CPF_COLABORADOR
                      AND COD_UNIDADE = F_COD_UNIDADE_DESTINO)
                || ', FOI TRANSFERIDO PARA A UNIDADE: '
        || (SELECT U.NOME FROM UNIDADE U WHERE U.CODIGO = F_COD_UNIDADE_DESTINO))
    INTO COLABORADOR_TRANSFERIDO;
END
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
-- PL-2390
DROP FUNCTION FUNC_PNEU_GET_MODELOS_PNEU_LISTAGEM(F_COD_EMPRESA BIGINT, F_COD_MARCA BIGINT);
CREATE OR REPLACE FUNCTION FUNC_PNEU_GET_MODELOS_PNEU_LISTAGEM(F_COD_EMPRESA BIGINT,
                                                               F_COD_MARCA BIGINT,
                                                               F_INCLUIR_MARCAS_NAO_UTILIZADAS BOOLEAN)
    RETURNS TABLE
            (
                COD_MARCA_PNEU   BIGINT,
                NOME_MARCA_PNEU  TEXT,
                COD_MODELO_PNEU  BIGINT,
                NOME_MODELO_PNEU TEXT,
                QTD_SULCOS       SMALLINT,
                ALTURA_SULCOS    NUMERIC
            )
    LANGUAGE SQL
AS
$$
SELECT MAP.CODIGO                             AS COD_MARCA_PNEU,
       MAP.NOME :: TEXT                       AS NOME_MARCA_PNEU,
       MOP.CODIGO                             AS COD_MODELO_PNEU,
       MOP.NOME :: TEXT                       AS NOME_MODELO_PNEU,
       MOP.QT_SULCOS                          AS QTD_SULCOS,
       TRUNC(MOP.ALTURA_SULCOS :: NUMERIC, 2) AS ALTURA_SULCOS
FROM MARCA_PNEU MAP
         LEFT JOIN MODELO_PNEU MOP
                   ON MAP.CODIGO = MOP.COD_MARCA
                       AND MOP.COD_EMPRESA = F_COD_EMPRESA
WHERE F_IF(F_COD_MARCA IS NULL, TRUE, MAP.CODIGO = F_COD_MARCA)
  AND F_IF(F_INCLUIR_MARCAS_NAO_UTILIZADAS IS TRUE, TRUE, MOP.CODIGO IS NOT NULL)
ORDER BY NOME_MARCA_PNEU, NOME_MODELO_PNEU
$$;

DROP FUNCTION FUNC_PNEU_GET_MARCAS_PNEU_LISTAGEM();
CREATE OR REPLACE FUNCTION FUNC_PNEU_GET_MARCAS_PNEU_LISTAGEM(F_COD_EMPRESA BIGINT,
                                                              F_INCLUIR_MARCAS_NAO_UTILIZADAS BOOLEAN)
    RETURNS TABLE
            (
                COD_MARCA_PNEU  BIGINT,
                NOME_MARCA_PNEU TEXT
            )
    LANGUAGE SQL
AS
$$
SELECT MP.CODIGO       AS COD_MARCA_PNEU,
       MP.NOME :: TEXT AS NOME_MARCA_PNEU
FROM MARCA_PNEU MP
WHERE CASE
          WHEN F_INCLUIR_MARCAS_NAO_UTILIZADAS IS TRUE
              THEN TRUE
          ELSE (SELECT EXISTS(SELECT MOP.COD_MARCA
                              FROM MODELO_PNEU MOP
                              WHERE MOP.COD_EMPRESA = F_COD_EMPRESA
                                AND MOP.COD_MARCA = MP.CODIGO))
          END
ORDER BY MP.NOME;
$$;

DROP FUNCTION FUNC_PNEU_GET_MODELOS_BANDA_LISTAGEM(F_COD_EMPRESA BIGINT, F_COD_MARCA BIGINT);
CREATE OR REPLACE FUNCTION FUNC_PNEU_GET_MODELOS_BANDA_LISTAGEM(F_COD_EMPRESA BIGINT,
                                                                F_COD_MARCA BIGINT,
                                                                F_INCLUIR_MARCAS_NAO_UTILIZADAS BOOLEAN)
    RETURNS TABLE
            (
                COD_MARCA_BANDA   BIGINT,
                NOME_MARCA_BANDA  TEXT,
                COD_MODELO_BANDA  BIGINT,
                NOME_MODELO_BANDA TEXT,
                QTD_SULCOS        SMALLINT,
                ALTURA_SULCOS     NUMERIC
            )
    LANGUAGE SQL
AS
$$
SELECT MAB.CODIGO                             AS COD_MARCA_BANDA,
       MAB.NOME :: TEXT                       AS NOME_MARCA_BANDA,
       MOB.CODIGO                             AS COD_MODELO_BANDA,
       MOB.NOME :: TEXT                       AS NOME_MODELO_BANDA,
       MOB.QT_SULCOS                          AS QTD_SULCOS,
       TRUNC(MOB.ALTURA_SULCOS :: NUMERIC, 2) AS ALTURA_SULCOS
FROM MARCA_BANDA MAB
         LEFT JOIN MODELO_BANDA MOB
                   ON MAB.CODIGO = MOB.COD_MARCA
WHERE MAB.COD_EMPRESA = F_COD_EMPRESA
  AND F_IF(F_COD_MARCA IS NULL, TRUE, MAB.CODIGO = F_COD_MARCA)
  AND F_IF(F_INCLUIR_MARCAS_NAO_UTILIZADAS IS TRUE, TRUE, MOB.CODIGO IS NOT NULL)
ORDER BY NOME_MARCA_BANDA, NOME_MODELO_BANDA
$$;

DROP FUNCTION FUNC_PNEU_GET_MARCAS_BANDA_LISTAGEM(F_COD_EMPRESA BIGINT);
CREATE OR REPLACE FUNCTION FUNC_PNEU_GET_MARCAS_BANDA_LISTAGEM(F_COD_EMPRESA BIGINT)
    RETURNS TABLE
            (
                COD_MARCA_BANDA  BIGINT,
                NOME_MARCA_BANDA TEXT
            )
    LANGUAGE SQL
AS
$$
SELECT MB.CODIGO       AS COD_MARCA_PNEU,
       MB.NOME :: TEXT AS NOME_MARCA_PNEU
FROM MARCA_BANDA MB
WHERE MB.COD_EMPRESA = F_COD_EMPRESA
ORDER BY MB.NOME;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--########################################### IMPORT DE PLANILHA DE VEÍCULOS ###########################################
--###############################################        PL-2318         ###############################################
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--############################### TABELA DE MAPEAMENTO DE DIAGRAMAS USUÁRIO-PROLOG #####################################
--######################################################################################################################
--######################################################################################################################
--PL-2318
CREATE TABLE IF NOT EXISTS IMPLANTACAO.VEICULO_DIAGRAMA_USUARIO_PROLOG
(
    CODIGO               BIGSERIAL,
    COD_VEICULO_DIAGRAMA BIGINT,
    NOME                 VARCHAR(255),
    QTD_EIXOS            INTEGER,
    PRIMARY KEY (CODIGO),
    FOREIGN KEY (COD_VEICULO_DIAGRAMA) REFERENCES VEICULO_DIAGRAMA (CODIGO)
);
--######################################################################################################################
--INSERE DADOS MAPEADOS NA TABELA IMPLANTACAO.VEICULO_DIAGRAMA_USUARIO_PROLOG
INSERT INTO IMPLANTACAO.VEICULO_DIAGRAMA_USUARIO_PROLOG(COD_VEICULO_DIAGRAMA, NOME, QTD_EIXOS)
VALUES (3, 'CARRETA', 3),
       (3, 'CARRETA S/ REBOQUE', 3),
       (3, 'REBOQUE', 3),
       (3, 'Semi-reboque', 3),
       (3, 'PRANCHA', 3),
       (3, 'SLIDER 28 PLT', 3),
       (3, 'BATE VOLTA', 3),
       (3, 'DEDICADO', 3),
       (3, 'SEMI REBOQUE 3 EIXO', 3),
       (2, '3/4 truck', 3),
       (2, 'CAMINHÃO', 3),
       (2, 'CAVALO MECANICO', 3),
       (2, 'CAVALO TRUCADO', 3),
       (2, 'TRUCK', 3),
       (2, 'TRUCK TRUCADO', 3),
       (2, 'VUC', 3),
       (2, 'VUC SIDER', 3),
       (2, 'BATE VOLTA', 3),
       (2, 'DEDICADO', 3),
       (2, 'VULC', 3),
       (1, '3X4', 2),
       (1, 'CAMINHÃO', 2),
       (1, 'CAVALO SIMPLES', 2),
       (1, 'SIMPLES', 2),
       (1, 'TOCO', 2),
       (1, 'VUC', 2),
       (1, 'VULC', 2),
       (5, 'BITREM', 2),
       (5, 'REBOQUE', 2),
       (5, 'SEMI REBOQUE 2 EIXO', 2),
       (5, 'SEMI REBOQUE', 2),
       (8, 'CARRO', 2),
       (8, 'VAN', 2),
       (8, 'LEVE', 2),
       (8, 'FROTA LEVE', 2),
       (6, 'EMPILHADEIRA', 2),
       (4, 'BITRUCK', 4),
       (10, 'TRUCK ELÉTRICO', 3),
       (5, 'CARRETA 2 EIXOS', 2),
       (12, 'Moto', 2),
       (5, 'REBOQUE', 1);
--######################################################################################################################
--######################################################################################################################
--################################## TABELA COM DADOS DE QUEM REALIZOU O IMPORT ########################################
--######################################################################################################################
--######################################################################################################################
--PL-2318
CREATE TABLE IF NOT EXISTS IMPLANTACAO.DADOS_AUTOR_IMPORT
(
    CODIGO      BIGSERIAL,
    COD_EMPRESA BIGINT,
    COD_UNIDADE BIGINT,
    USUARIO     VARCHAR(255),
    DATA_HORA   TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    PRIMARY KEY (CODIGO)
);
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--###############################  CRIA REGEX PARA REMOVER TODOS OS ESPAÇOS EM BRANCO ##################################
--######################################################################################################################
--######################################################################################################################
--PL-2318
CREATE OR REPLACE FUNCTION REMOVE_ALL_SPACES(F_TEXTO TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    RETURN REGEXP_REPLACE(F_TEXTO, '\s', '', 'g');
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--####################################### CRIA SCHEMA PARA AUDIT DAS IMPLANTAÇÕES ######################################
--######################################################################################################################
--######################################################################################################################
CREATE SCHEMA IF NOT EXISTS AUDIT_IMPLANTACAO;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--############################################ CRIA FUNC AUDIT PARA IMPLANTAÇÃO ########################################
--######################################################################################################################
--######################################################################################################################
--PL-2318
CREATE OR REPLACE FUNCTION AUDIT_IMPLANTACAO.FUNC_AUDIT_IMPLANTACAO() RETURNS trigger
    SECURITY DEFINER
    LANGUAGE plpgsql
AS
$$
DECLARE
  F_TABLE_NAME_AUDIT   TEXT := TG_RELNAME || '_audit';
  F_TG_OP              TEXT := SUBSTRING(TG_OP, 1, 1);
  F_JSON               TEXT := CASE
                               WHEN F_TG_OP = 'D'
                                 THEN ROW_TO_JSON(OLD)
                               ELSE ROW_TO_JSON(NEW)
                               END;
  IS_NEW_ROW        BOOLEAN := CASE WHEN F_TG_OP = 'D' THEN FALSE ELSE TRUE END;
BEGIN
  EXECUTE FORMAT(
      'CREATE TABLE IF NOT EXISTS audit_implantacao.%I (
        CODIGO                  SERIAL,
        DATA_HORA_UTC           TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
        OPERACAO                VARCHAR(1),
        PG_USERNAME             TEXT,
        PG_APPLICATION_NAME     TEXT,
        ROW_LOG                 JSONB,
        IS_NEW_ROW              BOOLEAN
      );', F_TABLE_NAME_AUDIT);

  EXECUTE FORMAT(
      'INSERT INTO audit_implantacao.%I (operacao, row_log, is_new_row, pg_username, pg_application_name)
       VALUES (%L, %L, %L, %L, %L);', F_TABLE_NAME_AUDIT, F_TG_OP, F_JSON, IS_NEW_ROW, SESSION_USER,
      (SELECT CURRENT_SETTING('application_name')));
  RETURN NULL;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--######################################## FUNC QUE CRIA TABELA PRÉ-IMPORT #############################################
--######################################################################################################################
--######################################################################################################################
--PL-2318
CREATE OR REPLACE FUNCTION IMPLANTACAO.FUNC_VEICULO_IMPORT_CRIA_TABELA_IMPORT(F_COD_EMPRESA BIGINT,
                                                                              F_COD_UNIDADE BIGINT,
                                                                              F_USUARIO TEXT,
                                                                              F_DATA DATE,
                                                                              OUT NOME_TABELA_CRIADA TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    NOME_EMPRESA       TEXT := (SELECT E.NOME
                                FROM EMPRESA E
                                WHERE E.CODIGO = F_COD_EMPRESA);
    NOME_UNIDADE       TEXT := (SELECT U.NOME
                                FROM UNIDADE U
                                WHERE U.CODIGO = F_COD_UNIDADE);
    DIA                TEXT := (SELECT EXTRACT(DAY FROM F_DATA));
    MES                TEXT := (SELECT EXTRACT(MONTH FROM F_DATA));
    ANO                TEXT := (SELECT EXTRACT(YEAR FROM F_DATA)) ;
    NOME_TABELA_IMPORT TEXT := lower(remove_all_spaces(
                        'V_' || NOME_EMPRESA || '_' || NOME_UNIDADE || '_' || ANO || '_' || MES || '_' || DIA || '_' ||
                        F_USUARIO));
BEGIN
    EXECUTE FORMAT(
            'CREATE TABLE IF NOT EXISTS IMPLANTACAO.%I (
            CODIGO                  BIGSERIAL,
            COD_DADOS_AUTOR_IMPORT  BIGINT,
            COD_UNIDADE_EDITAVEL    BIGINT,
            PLACA_EDITAVEL          VARCHAR(255),
            KM_EDITAVEL             BIGINT,
            MARCA_EDITAVEL          VARCHAR(255),
            MODELO_EDITAVEL         VARCHAR(255),
            TIPO_EDITAVEL           VARCHAR(255),
            QTD_EIXOS_EDITAVEL      VARCHAR(255),
            PLACA_FORMATADA_IMPORT  VARCHAR(255),
            MARCA_FORMATADA_IMPORT  VARCHAR(255),
            MODELO_FORMATADO_IMPORT VARCHAR(255),
            TIPO_FORMATADO_IMPORT   VARCHAR(255),
            STATUS_IMPORT_REALIZADO BOOLEAN,
            ERROS_ENCONTRADOS       VARCHAR(255),
            USUARIO_UPDATE          VARCHAR(255)
        );', NOME_TABELA_IMPORT);

    --TRIGGER PARA VERIFICAR PLANILHA E REALIZAR O IMPORT DE VEÍCULOS
    EXECUTE format('DROP TRIGGER IF EXISTS TG_FUNC_IMPORT_VEICULO ON IMPLANTACAO.%I;
                   CREATE TRIGGER TG_FUNC_IMPORT_VEICULO
                    BEFORE INSERT OR UPDATE
                        ON IMPLANTACAO.%I
                    FOR EACH ROW
                   EXECUTE PROCEDURE IMPLANTACAO.TG_FUNC_VEICULO_CONFERE_PLANILHA_IMPORTA_VEICULO()',
                   NOME_TABELA_IMPORT,
                   NOME_TABELA_IMPORT);

    --CRIA AUDIT PARA A TABELA
    EXECUTE format('DROP TRIGGER IF EXISTS TG_FUNC_AUDIT_IMPORT_VEICULO ON IMPLANTACAO.%I;
                    CREATE TRIGGER TG_FUNC_AUDIT_IMPORT_VEICULO
                    AFTER UPDATE OR DELETE
                    ON IMPLANTACAO.%I
                    FOR EACH ROW
                    EXECUTE PROCEDURE AUDIT_IMPLANTACAO.FUNC_AUDIT_IMPLANTACAO();',
                    NOME_TABELA_IMPORT,
                    NOME_TABELA_IMPORT);

    --RETORNA NOME DA TABELA
    SELECT NOME_TABELA_IMPORT INTO NOME_TABELA_CRIADA;
END ;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--##################################### FUNC PARA INSERIR DADOS DO AUTOR DO IMPORT #####################################
--######################################################################################################################
--######################################################################################################################
--PL-2318
CREATE OR REPLACE FUNCTION IMPLANTACAO.FUNC_VEICULO_IMPORT_INSERE_DADOS_AUTOR(F_COD_EMPRESA BIGINT,
                                                                              F_COD_UNIDADE BIGINT,
                                                                              F_USUARIO TEXT)
    RETURNS TABLE
            (
                COD_DADOS_AUTOR_IMPORT BIGINT,
                NOME_TABELA_IMPORT     TEXT
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    DATA_HORA_IMPORT       TIMESTAMP WITH TIME ZONE := NOW();
    DATA_IMPORT            DATE                     := CURRENT_DATE;
    COD_DADOS_AUTOR_IMPORT BIGINT;
    NOME_TABELA_CRIADA     TEXT;
BEGIN
    INSERT INTO IMPLANTACAO.DADOS_AUTOR_IMPORT (COD_EMPRESA, COD_UNIDADE, USUARIO, DATA_HORA)
    VALUES (F_COD_EMPRESA, F_COD_UNIDADE, F_USUARIO, DATA_HORA_IMPORT) RETURNING CODIGO
        INTO COD_DADOS_AUTOR_IMPORT;

    -- Verificamos se o insert funcionou.
    IF COD_DADOS_AUTOR_IMPORT > 0
    THEN
        SELECT *
        FROM IMPLANTACAO.FUNC_VEICULO_IMPORT_CRIA_TABELA_IMPORT(
                     F_COD_EMPRESA,
                     F_COD_UNIDADE,
                     F_USUARIO,
                     DATA_IMPORT)
        INTO NOME_TABELA_CRIADA;
    ELSE
        PERFORM THROW_GENERIC_ERROR('Não foi possível inserir os dados do autor de import, tente novamente');
    END IF;

    RETURN QUERY SELECT COD_DADOS_AUTOR_IMPORT, NOME_TABELA_CRIADA;
END ;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--################################ FUNC PARA INSERIR NA TABELA CRIADA PARA O IMPORT ####################################
--######################################################################################################################
--######################################################################################################################
--PL-2318
CREATE OR REPLACE FUNCTION IMPLANTACAO.FUNC_VEICULO_INSERE_PLANILHA_IMPORTACAO(F_COD_DADOS_AUTOR_IMPORT BIGINT,
                                                                               F_NOME_TABELA_IMPORT TEXT,
                                                                               F_COD_UNIDADE BIGINT,
                                                                               F_JSON_VEICULOS JSONB)
    RETURNS VOID
    LANGUAGE plpgsql
AS
$$
BEGIN
    EXECUTE FORMAT('INSERT INTO implantacao.%I (COD_DADOS_AUTOR_IMPORT,
                                                COD_UNIDADE_EDITAVEL,
                                                PLACA_EDITAVEL,
                                                PLACA_FORMATADA_IMPORT,
                                                KM_EDITAVEL,
                                                MARCA_EDITAVEL,
                                                MARCA_FORMATADA_IMPORT,
                                                MODELO_EDITAVEL,
                                                MODELO_FORMATADO_IMPORT,
                                                TIPO_EDITAVEL,
                                                TIPO_FORMATADO_IMPORT,
                                                QTD_EIXOS_EDITAVEL)
                   SELECT %s AS COD_DADOS_AUTOR_IMPORT,
                          %s AS COD_UNIDADE,
                          (SRC ->> ''placa'') :: TEXT                                         AS PLACA,
                          REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS((SRC ->> ''placa'')) :: TEXT  AS PLACA_FORMATADA_IMPORT,
                          (SRC ->> ''km'') :: BIGINT                                          AS KM,
                          (SRC ->> ''marca'') :: TEXT                                         AS MARCA,
                          REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(SRC ->> (''marca'')) :: TEXT  AS MARCA_FORMATADA_IMPORT,
                          (SRC ->> ''modelo'') :: TEXT                                        AS MODELO,
                          REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(SRC ->> (''modelo'')) :: TEXT AS MODELO_FORMATADO_IMPORT,
                          (SRC ->> ''tipo'') :: TEXT                                          AS TIPO,
                          REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(SRC ->> (''tipo'')) :: TEXT   AS TIPO_FORMATADO_IMPORT,
                          (SRC ->> ''qtdEixos'') :: TEXT                                      AS QTD_EIXOS
                   FROM JSONB_ARRAY_ELEMENTS(%L) AS SRC',
                   F_NOME_TABELA_IMPORT,
                   F_COD_DADOS_AUTOR_IMPORT,
                   F_COD_UNIDADE,
                   F_JSON_VEICULOS);
END
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--################################ DROPA TABELA DE TOKEN QUE NÃO SERÁ MAIS UTILIZADA ###################################
--######################################################################################################################
DROP TABLE IF EXISTS IMPLANTACAO.TOKEN_IMPLANTACAO;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--###################################### CRIA TABELA PARA AUTENTICAÇÃO DO USUÁRIO ######################################
--######################################################################################################################
--######################################################################################################################
--PL-2318
CREATE TABLE IF NOT EXISTS IMPLANTACAO.PROLOG_USER
(
    CODIGO   BIGSERIAL NOT NULL PRIMARY KEY,
    USERNAME TEXT      NOT NULL UNIQUE,
    PASSWORD TEXT      NOT NULL
);
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--###################################### FUNCTION PARA VERIFICAR USUÁRIO E SENHA #######################################
--######################################################################################################################
--######################################################################################################################
--PL-2318
CREATE OR REPLACE FUNCTION IMPLANTACAO.FUNC_VERIFICA_LOGIN_SENHA(F_USERNAME TEXT, F_PASSWORD TEXT)
    RETURNS TABLE
            (
                USERNAME TEXT
            )
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    RETURN QUERY
        SELECT PU.USERNAME
        FROM IMPLANTACAO.PROLOG_USER PU
        WHERE PU.USERNAME = F_USERNAME
          AND PU.PASSWORD = F_PASSWORD;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--######################## FUNC PARA CONFERIR PLANILHA E IMPORTAR OS DADOS CORRETOS PARA O BANCO #######################
--######################################################################################################################
--######################################################################################################################
--PL-2318
CREATE OR REPLACE FUNCTION IMPLANTACAO.TG_FUNC_VEICULO_CONFERE_PLANILHA_IMPORTA_VEICULO()
    RETURNS TRIGGER
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    F_COD_EMPRESA CONSTANT                 BIGINT := (SELECT U.COD_EMPRESA
                                                      FROM UNIDADE U
                                                      WHERE U.CODIGO = NEW.COD_UNIDADE_EDITAVEL);
    F_VALOR_SIMILARIDADE CONSTANT          REAL     := 0.4;
    F_VALOR_SIMILARIDADE_DIAGRAMA CONSTANT REAL     := 0.5;
    F_SEM_SIMILARIDADE CONSTANT            REAL     := 0.0;
    F_QTD_ERROS                            SMALLINT := 0;
    F_MSGS_ERROS                           TEXT;
    F_QUEBRA_LINHA                         TEXT   := CHR(10);
    F_COD_MARCA_BANCO                      BIGINT;
    F_SIMILARIDADE_MARCA                   REAL;
    F_MARCA_MODELO                         TEXT;
    F_COD_MODELO_BANCO                     BIGINT;
    F_SIMILARIDADE_MODELO                  REAL;
    F_COD_DIAGRAMA_BANCO                   BIGINT;
    F_NOME_DIAGRAMA_BANCO                  TEXT;
    F_SIMILARIDADE_DIAGRAMA                REAL;
    F_DIAGRAMA_TIPO                        TEXT;
    F_EIXOS_DIAGRAMA                       TEXT;
    F_COD_TIPO_BANCO                       BIGINT;
    F_SIMILARIDADE_TIPO                    REAL;
BEGIN
    IF (TG_OP = 'UPDATE' AND OLD.STATUS_IMPORT_REALIZADO IS TRUE)
    THEN
        NEW.ERROS_ENCONTRADOS := OLD.ERROS_ENCONTRADOS;
        NEW.COD_UNIDADE_EDITAVEL := OLD.COD_UNIDADE_EDITAVEL;
        NEW.PLACA_EDITAVEL := OLD.PLACA_EDITAVEL;
        NEW.PLACA_FORMATADA_IMPORT := OLD.PLACA_FORMATADA_IMPORT;
        NEW.KM_EDITAVEL := OLD.KM_EDITAVEL;
        NEW.MARCA_EDITAVEL := OLD.MARCA_EDITAVEL;
        NEW.MARCA_FORMATADA_IMPORT := OLD.MARCA_FORMATADA_IMPORT;
        NEW.MODELO_EDITAVEL := OLD.MODELO_EDITAVEL;
        NEW.MODELO_FORMATADO_IMPORT := OLD.MODELO_FORMATADO_IMPORT;
        NEW.TIPO_EDITAVEL := OLD.TIPO_EDITAVEL;
        NEW.TIPO_FORMATADO_IMPORT := OLD.TIPO_FORMATADO_IMPORT;
        NEW.QTD_EIXOS_EDITAVEL := OLD.QTD_EIXOS_EDITAVEL;
        NEW.STATUS_IMPORT_REALIZADO := OLD.STATUS_IMPORT_REALIZADO;
        NEW.USUARIO_UPDATE := OLD.USUARIO_UPDATE;
    ELSE
        NEW.PLACA_FORMATADA_IMPORT := REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(NEW.PLACA_EDITAVEL);
        NEW.MARCA_FORMATADA_IMPORT := REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(NEW.MARCA_EDITAVEL);
        NEW.MODELO_FORMATADO_IMPORT := REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(NEW.MODELO_EDITAVEL);
        NEW.TIPO_FORMATADO_IMPORT := REMOVE_ESPACOS_E_CARACTERES_ESPECIAIS(NEW.TIPO_EDITAVEL);
        NEW.USUARIO_UPDATE := SESSION_USER;

        -- VERIFICAÇÕES PLACA.
        -- Placa sem 7 dígitos: Erro.
        -- Pĺaca cadastrada em outra empresa: Erro.
        -- Pĺaca cadastrada em outra unidade da mesma empresa: Erro.
        -- Pĺaca cadastrada na mesma unidade: Atualiza informações.
        IF (NEW.PLACA_FORMATADA_IMPORT IS NOT NULL) THEN
            IF LENGTH(NEW.PLACA_FORMATADA_IMPORT) <> 7
            THEN
                F_QTD_ERROS = F_QTD_ERROS + 1;
                F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS, '- A PLACA NÃO POSSUI 7 CARACTERES', F_QUEBRA_LINHA);
            ELSE
                IF EXISTS(SELECT V.PLACA
                          FROM VEICULO V
                          WHERE V.PLACA = NEW.PLACA_FORMATADA_IMPORT
                            AND V.COD_EMPRESA != F_COD_EMPRESA)
                THEN
                    F_QTD_ERROS = F_QTD_ERROS + 1;
                    F_MSGS_ERROS =
                            concat(F_MSGS_ERROS, F_QTD_ERROS, '- A PLACA JÁ ESTÁ CADASTRADA E PERTENCE A OUTRA EMPRESA',
                                   F_QUEBRA_LINHA);
                ELSE
                    IF EXISTS(SELECT V.PLACA
                              FROM VEICULO V
                              WHERE V.PLACA = NEW.PLACA_FORMATADA_IMPORT
                                AND V.COD_EMPRESA = F_COD_EMPRESA
                                AND COD_UNIDADE != NEW.COD_UNIDADE_EDITAVEL)
                    THEN
                        F_QTD_ERROS = F_QTD_ERROS + 1;
                        F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS,
                                              '- A PLACA JÁ ESTÁ CADASTRADA E PERTENCE A OUTRA UNIDADE',
                                              F_QUEBRA_LINHA);
                    END IF;
                END IF;
            END IF;
        ELSE
            F_QTD_ERROS = F_QTD_ERROS + 1;
            F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS, '- A PLACA NÃO PODE SER NULA', F_QUEBRA_LINHA);
        END IF;

        -- VERIFICAÇÕES MARCA: Procura marca similar no banco.
        F_MARCA_MODELO := CONCAT(F_COD_MARCA_BANCO, NEW.MODELO_FORMATADO_IMPORT);
        SELECT DISTINCT ON (NEW.MARCA_FORMATADA_IMPORT) MAV.CODIGO                                                        AS COD_MARCA_BANCO,
                                                        MAX(FUNC_GERA_SIMILARIDADE(NEW.MARCA_FORMATADA_IMPORT, MAV.NOME)) AS SIMILARIEDADE_MARCA
        INTO F_COD_MARCA_BANCO, F_SIMILARIDADE_MARCA
        FROM MARCA_VEICULO MAV
        GROUP BY NEW.MARCA_FORMATADA_IMPORT, NEW.MARCA_EDITAVEL, MAV.NOME, MAV.CODIGO
        ORDER BY NEW.MARCA_FORMATADA_IMPORT, SIMILARIEDADE_MARCA DESC;

        -- Se a similaridade da marca for maior ou igual ao exigido: procura modelo.
        -- Se não for: Mostra erro de marca não encontrada.
        IF (F_SIMILARIDADE_MARCA >= F_VALOR_SIMILARIDADE)
        THEN
            -- VERIFICAÇÕES DE MODELO: Procura modelo similar no banco.
            SELECT DISTINCT ON (F_MARCA_MODELO) MOV.CODIGO AS COD_MODELO_VEICULO,
                                                CASE
                                                    WHEN F_COD_MARCA_BANCO = MOV.COD_MARCA
                                                        THEN
                                                        MAX(FUNC_GERA_SIMILARIDADE(F_MARCA_MODELO,
                                                                                   CONCAT(MOV.COD_MARCA, MOV.NOME)))
                                                    ELSE F_SEM_SIMILARIDADE
                                                    END    AS SIMILARIEDADE_MODELO
            INTO F_COD_MODELO_BANCO, F_SIMILARIDADE_MODELO
            FROM MODELO_VEICULO MOV
            WHERE MOV.COD_EMPRESA = F_COD_EMPRESA
            GROUP BY F_MARCA_MODELO, MOV.NOME, MOV.CODIGO
            ORDER BY F_MARCA_MODELO, SIMILARIEDADE_MODELO DESC;
            -- Se a similaridade do modelo for menor do que o exigido: cadastra novo modelo.

            IF (F_SIMILARIDADE_MODELO < F_VALOR_SIMILARIDADE)
            THEN
                INSERT INTO MODELO_VEICULO (NOME, COD_MARCA, COD_EMPRESA)
                VALUES (NEW.MODELO_EDITAVEL, F_COD_MARCA_BANCO, F_COD_EMPRESA) RETURNING CODIGO INTO F_COD_MODELO_BANCO;
            END IF;
        ELSE
            F_QTD_ERROS = F_QTD_ERROS + 1;
            F_MSGS_ERROS = concat(F_MSGS_ERROS, F_QTD_ERROS, '- A MARCA NÃO FOI ENCONTRADA', F_QUEBRA_LINHA);
        END IF;

        -- VERIFICAÇÕES DE DIAGRAMA.
        -- O diagrama é obtido através do preenchimento do campo "tipo" da planilha de import.
        F_DIAGRAMA_TIPO := CONCAT(F_NOME_DIAGRAMA_BANCO, NEW.TIPO_FORMATADO_IMPORT);
        F_EIXOS_DIAGRAMA := CONCAT(NEW.QTD_EIXOS_EDITAVEL, NEW.TIPO_FORMATADO_IMPORT);
        -- Procura diagrama no banco:
        WITH INFO_DIAGRAMAS AS (
            SELECT COUNT(VDE.POSICAO) AS QTD_EIXOS, VDE.COD_DIAGRAMA AS CODIGO, VD.NOME AS NOME
            FROM VEICULO_DIAGRAMA_EIXOS VDE
                     JOIN
                 VEICULO_DIAGRAMA VD ON VDE.COD_DIAGRAMA = VD.CODIGO
            GROUP BY VDE.COD_DIAGRAMA, VD.NOME),

             DIAGRAMAS AS (
                 SELECT VDUP.COD_VEICULO_DIAGRAMA AS COD_DIAGRAMA,
                        VDUP.NOME                 AS NOME_DIAGRAMA,
                        VDUP.QTD_EIXOS            AS QTD_EIXOS
                 FROM IMPLANTACAO.VEICULO_DIAGRAMA_USUARIO_PROLOG VDUP
                 UNION ALL
                 SELECT ID.CODIGO AS COD_DIAGRAMA, ID.NOME AS NOME_DIAGRAMA, ID.QTD_EIXOS
                 FROM INFO_DIAGRAMAS ID)

             -- F_EIXOS_DIAGRAMA: Foi necessário concatenar a quantidade de eixos ao nome do diagrama para evitar
             -- similaridades ambiguas.
        SELECT DISTINCT ON (F_EIXOS_DIAGRAMA) D.NOME_DIAGRAMA AS NOME_DIAGRAMA,
                                              D.COD_DIAGRAMA  AS DIAGRAMA_BANCO,
                                              CASE
                                                  WHEN D.QTD_EIXOS ::TEXT = NEW.QTD_EIXOS_EDITAVEL
                                                      THEN
                                                      MAX(FUNC_GERA_SIMILARIDADE(F_EIXOS_DIAGRAMA,
                                                                                 CONCAT(D.QTD_EIXOS, D.NOME_DIAGRAMA)))
                                                  ELSE F_SEM_SIMILARIDADE
                                                  END         AS SIMILARIEDADE_DIAGRAMA
        INTO F_NOME_DIAGRAMA_BANCO, F_COD_DIAGRAMA_BANCO,
            F_SIMILARIDADE_DIAGRAMA
        FROM DIAGRAMAS D
        GROUP BY F_EIXOS_DIAGRAMA, D.NOME_DIAGRAMA, D.COD_DIAGRAMA, D.QTD_EIXOS
        ORDER BY F_EIXOS_DIAGRAMA, SIMILARIEDADE_DIAGRAMA DESC;

        -- Se a similaridade do diagrama for maior ou igual ao exigido: procura tipo.
        -- Se não for: Mostra erro de diagrama não encontrado.
        CASE WHEN (F_SIMILARIDADE_DIAGRAMA >= F_VALOR_SIMILARIDADE_DIAGRAMA)
            THEN
                SELECT DISTINCT ON (F_DIAGRAMA_TIPO) VT.CODIGO AS COD_TIPO_VEICULO,
                                                     CASE
                                                         WHEN F_COD_DIAGRAMA_BANCO = VT.COD_DIAGRAMA
                                                             THEN MAX(FUNC_GERA_SIMILARIDADE(NEW.TIPO_FORMATADO_IMPORT, VT.NOME))
                                                         ELSE F_SEM_SIMILARIDADE
                                                         END   AS SIMILARIEDADE_TIPO_DIAGRAMA
                INTO F_COD_TIPO_BANCO, F_SIMILARIDADE_TIPO
                FROM VEICULO_TIPO VT
                WHERE VT.COD_EMPRESA = F_COD_EMPRESA
                GROUP BY F_DIAGRAMA_TIPO,
                         VT.CODIGO
                ORDER BY F_DIAGRAMA_TIPO, SIMILARIEDADE_TIPO_DIAGRAMA DESC;
                -- Se a similaridade do tipo for menor do que o exigido: cadastra novo modelo.
                IF (F_SIMILARIDADE_TIPO < F_VALOR_SIMILARIDADE)
                THEN
                    INSERT INTO VEICULO_TIPO (NOME, STATUS_ATIVO, COD_DIAGRAMA, COD_EMPRESA)
                    VALUES (NEW.TIPO_EDITAVEL, TRUE, F_COD_DIAGRAMA_BANCO, F_COD_EMPRESA) RETURNING CODIGO INTO F_COD_TIPO_BANCO;
                END IF;
            ELSE
                F_QTD_ERROS = F_QTD_ERROS + 1;
                F_MSGS_ERROS =
                        concat(F_MSGS_ERROS, F_QTD_ERROS, '- O DIAGRAMA (TIPO) NÃO FOI ENCONTRADO', F_QUEBRA_LINHA);
            END CASE;
        -- VERIFICA QTD DE ERROS
        IF (F_QTD_ERROS > 0)
        THEN
            NEW.STATUS_IMPORT_REALIZADO = FALSE;
            NEW.ERROS_ENCONTRADOS = F_MSGS_ERROS;
        ELSE
            IF (F_QTD_ERROS = 0 AND EXISTS(SELECT V.PLACA
                                           FROM VEICULO V
                                           WHERE V.PLACA = NEW.PLACA_FORMATADA_IMPORT
                                             AND V.COD_EMPRESA = F_COD_EMPRESA
                                             AND COD_UNIDADE = NEW.COD_UNIDADE_EDITAVEL))
            THEN
                -- ATUALIZA INFORMAÇÕES DO VEÍCULO.
                UPDATE VEICULO_DATA
                SET COD_MODELO = F_COD_MODELO_BANCO,
                    COD_TIPO   = F_COD_TIPO_BANCO,
                    KM         = NEW.KM_EDITAVEL
                WHERE PLACA = NEW.PLACA_FORMATADA_IMPORT
                  AND COD_EMPRESA = F_COD_EMPRESA
                  AND COD_UNIDADE = NEW.COD_UNIDADE_EDITAVEL;
                NEW.STATUS_IMPORT_REALIZADO = NULL;
                NEW.ERROS_ENCONTRADOS = 'A PLACA JÁ ESTAVA CADASTRADA NA MESMA UNIDADE - INFORMAÇÕES FORAM ATUALIZADAS.';
            ELSE
                IF (F_QTD_ERROS = 0 AND NOT EXISTS(SELECT V.PLACA
                                                   FROM VEICULO V
                                                   WHERE V.PLACA = NEW.PLACA_FORMATADA_IMPORT))
                THEN
                    -- CADASTRA VEÍCULO.
                    INSERT INTO VEICULO_DATA (PLACA,
                                              COD_UNIDADE,
                                              KM,
                                              STATUS_ATIVO,
                                              COD_TIPO,
                                              COD_MODELO,
                                              COD_EIXOS,
                                              DATA_HORA_CADASTRO,
                                              COD_UNIDADE_CADASTRO,
                                              COD_EMPRESA)
                    VALUES (NEW.PLACA_FORMATADA_IMPORT,
                            NEW.COD_UNIDADE_EDITAVEL,
                            NEW.KM_EDITAVEL,
                            TRUE,
                            F_COD_TIPO_BANCO,
                            F_COD_MODELO_BANCO,
                            1,
                            NOW(),
                            NEW.COD_UNIDADE_EDITAVEL,
                            F_COD_EMPRESA);
                    NEW.STATUS_IMPORT_REALIZADO = TRUE;
                    NEW.ERROS_ENCONTRADOS = '-';
                END IF;
            END IF;
        END IF;
    END IF;
    RETURN NEW;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
-- PLI-41
-- Migra tabela que salva os recursos integrados para o schema de integração.
CREATE TABLE IF NOT EXISTS INTEGRACAO.EMPRESA_INTEGRACAO_SISTEMA
(
    CODIGO            BIGSERIAL NOT NULL,
    COD_EMPRESA       BIGINT    NOT NULL,
    CHAVE_SISTEMA     TEXT      NOT NULL,
    RECURSO_INTEGRADO TEXT      NOT NULL,
    CONSTRAINT PK_EMPRESA_INTEGRACAO PRIMARY KEY (CODIGO),
    CONSTRAINT UNIQUE_EMPRESA_INTEGRACAO UNIQUE (COD_EMPRESA, CHAVE_SISTEMA, RECURSO_INTEGRADO),
    CONSTRAINT FK_EMPRESA_INTEGRACAO_EMPRESA FOREIGN KEY (COD_EMPRESA) REFERENCES PUBLIC.EMPRESA (CODIGO)
);

INSERT INTO INTEGRACAO.EMPRESA_INTEGRACAO_SISTEMA(COD_EMPRESA, CHAVE_SISTEMA, RECURSO_INTEGRADO)
SELECT I.COD_EMPRESA, I.CHAVE_SISTEMA, I.RECURSO_INTEGRADO
FROM INTEGRACAO I;

DROP TABLE PUBLIC.INTEGRACAO;

-- Deleta tabela que salva DE-PARA de codigo de unidade que não é mais utilizada
DROP TABLE PUBLIC.INTEGRACAO_UNIDADE;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
-- Cria function para utilizar
CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_GERAL_BUSCA_SISTEMA_KEY(F_USER_TOKEN TEXT,
                                                                   F_RECURSO_INTEGRADO TEXT)
    RETURNS TABLE
            (
                CHAVE_SISTEMA TEXT,
                EXISTE_TOKEN  BOOLEAN
            )
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    RETURN QUERY
        SELECT (SELECT EIS.CHAVE_SISTEMA
                FROM INTEGRACAO.EMPRESA_INTEGRACAO_SISTEMA EIS
                         JOIN TOKEN_AUTENTICACAO TA ON TA.TOKEN = F_USER_TOKEN
                         LEFT JOIN COLABORADOR C ON C.CPF = TA.CPF_COLABORADOR
                WHERE C.COD_EMPRESA = EIS.COD_EMPRESA
                  AND EIS.RECURSO_INTEGRADO = F_RECURSO_INTEGRADO)                              AS CHAVE_SISTEMA,
               (SELECT EXISTS(SELECT TOKEN FROM TOKEN_AUTENTICACAO WHERE TOKEN = F_USER_TOKEN)) AS TOKEN_EXISTE;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
-- Cria estrutura para salvar URLs de busca/envio de dados
CREATE TABLE IF NOT EXISTS INTEGRACAO.EMPRESA_INTEGRACAO_METODOS
(
    COD_INTEGRACAO_SISTEMA BIGINT NOT NULL,
    METODO_INTEGRADO               TEXT   NOT NULL,
    URL_COMPLETA                   TEXT   NOT NULL,
    CONSTRAINT FK_EMPRESA_INTEGRACAO_SISTEMA_METODOS FOREIGN KEY (COD_INTEGRACAO_SISTEMA)
        REFERENCES INTEGRACAO.EMPRESA_INTEGRACAO_SISTEMA (CODIGO),
    CONSTRAINT UNIQUE_METODO_INTEGRADO UNIQUE (COD_INTEGRACAO_SISTEMA, METODO_INTEGRADO, URL_COMPLETA)
);
--######################################################################################################################
--######################################################################################################################

-- Adiciona método de envio de movimentação para a praxio.
INSERT INTO INTEGRACAO.EMPRESA_INTEGRACAO_METODOS(COD_INTEGRACAO_SISTEMA, METODO_INTEGRADO, URL_COMPLETA)
VALUES (11, 'INSERT_MOVIMENTACAO', 'http://sp.bgmrodotec.com.br:9100/api/api/Troca/Inserir');

--######################################################################################################################
--######################################################################################################################
-- Function para buscar a URL onde a integração irá se comunicar.
CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_GERAL_BUSCA_URL_SISTEMA_PARCEIRO(F_COD_EMPRESA BIGINT,
                                                                            F_SISTEMA_KEY TEXT,
                                                                            F_METODO_INTEGRADO TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    RETURN (SELECT EIM.URL_COMPLETA AS URL_COMPLETA
            FROM INTEGRACAO.EMPRESA_INTEGRACAO_METODOS EIM
                     JOIN INTEGRACAO.EMPRESA_INTEGRACAO_SISTEMA EIS ON EIM.COD_INTEGRACAO_SISTEMA = EIS.CODIGO
            WHERE EIS.COD_EMPRESA = F_COD_EMPRESA
              AND EIS.CHAVE_SISTEMA = F_SISTEMA_KEY
              AND EIM.METODO_INTEGRADO = F_METODO_INTEGRADO);
END;
$$;
--######################################################################################################################
--######################################################################################################################
-- PLI-51
DROP FUNCTION FUNC_AFERICAO_GET_CRONOGRAMA_AFERICOES_PLACAS(F_COD_UNIDADE BIGINT,
    F_DATA_HORA_ATUAL_TZ_CLIENTE TIMESTAMP WITHOUT TIME ZONE,
    F_TZ_UNIDADE TEXT);
CREATE OR REPLACE FUNCTION FUNC_AFERICAO_GET_CRONOGRAMA_AFERICOES_PLACAS(F_COD_UNIDADES BIGINT[],
                                                                         F_DATA_HORA_ATUAL TIMESTAMP WITH TIME ZONE)
    RETURNS TABLE
            (
                PLACA                     TEXT,
                NOME_MODELO               TEXT,
                INTERVALO_PRESSAO         INTEGER,
                INTERVALO_SULCO           INTEGER,
                PERIODO_AFERICAO_SULCO    INTEGER,
                PERIODO_AFERICAO_PRESSAO  INTEGER,
                PNEUS_APLICADOS           INTEGER,
                STATUS_ATIVO_TIPO_VEICULO BOOLEAN,
                PODE_AFERIR_SULCO         BOOLEAN,
                PODE_AFERIR_PRESSAO       BOOLEAN,
                PODE_AFERIR_SULCO_PRESSAO BOOLEAN,
                PODE_AFERIR_ESTEPE        BOOLEAN
            )
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    RETURN QUERY
        SELECT V.PLACA :: TEXT                                                     AS PLACA,
               MV.NOME :: TEXT                                                     AS NOME_MODELO,
               COALESCE(INTERVALO_PRESSAO.INTERVALO, -1) :: INTEGER                AS INTERVALO_PRESSAO,
               COALESCE(INTERVALO_SULCO.INTERVALO, -1) :: INTEGER                  AS INTERVALO_SULCO,
               PRU.PERIODO_AFERICAO_SULCO                                          AS PERIODO_AFERICAO_SULCO,
               PRU.PERIODO_AFERICAO_PRESSAO                                        AS PERIODO_AFERICAO_PRESSAO,
               COALESCE(NUMERO_PNEUS.TOTAL, 0) :: INTEGER                          AS PNEUS_APLICADOS,
               VT.STATUS_ATIVO                                                     AS STATUS_ATIVO_TIPO_VEICULO,
               F_IF(CONFIG.CODIGO IS NULL, TRUE, CONFIG.PODE_AFERIR_SULCO)         AS PODE_AFERIR_SULCO,
               F_IF(CONFIG.CODIGO IS NULL, TRUE, CONFIG.PODE_AFERIR_PRESSAO)       AS PODE_AFERIR_PRESSAO,
               F_IF(CONFIG.CODIGO IS NULL, TRUE, CONFIG.PODE_AFERIR_SULCO_PRESSAO) AS PODE_AFERIR_SULCO_PRESSAO,
               F_IF(CONFIG.CODIGO IS NULL, TRUE, CONFIG.PODE_AFERIR_ESTEPE)        AS PODE_AFERIR_ESTEPE
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
--######################################################################################################################
--######################################################################################################################
END TRANSACTION;