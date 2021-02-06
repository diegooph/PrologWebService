BEGIN TRANSACTION ;

--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--###########################################  INÍCIO MIGRAÇÃO CONTROLE JORNADA  #######################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################

-- ########################################################################################################
-- ########################################################################################################
-- ################################ MIGRA DADOS DA TABELA INTERVALO ATUAL #################################
-- ########################################################################################################
-- ########################################################################################################
ALTER TABLE INTERVALO DROP CONSTRAINT PK_INTERVALO;
ALTER TABLE INTERVALO ADD CONSTRAINT PK_INTERVALO PRIMARY KEY (CODIGO);

-- CRIA TABELAS PARA ARMAZENAR SEPARADAMENTE INÍCIOS E FINS
CREATE TABLE IF NOT EXISTS MARCACAO_INICIO (
  COD_MARCACAO_INICIO BIGINT NOT NULL,
  CONSTRAINT FK_MARCACAO_INICIO_INTERVALO FOREIGN KEY (COD_MARCACAO_INICIO) REFERENCES INTERVALO(CODIGO),
  CONSTRAINT UNIQUE_COD_MARCACAO_INICIO UNIQUE (COD_MARCACAO_INICIO)
);
COMMENT ON TABLE MARCACAO_INICIO IS 'Armazena apenas marcações de início';

CREATE TABLE IF NOT EXISTS MARCACAO_FIM (
  COD_MARCACAO_FIM BIGINT NOT NULL,
  CONSTRAINT FK_MARCACAO_FIM_INTERVALO FOREIGN KEY (COD_MARCACAO_FIM) REFERENCES INTERVALO(CODIGO),
  CONSTRAINT UNIQUE_COD_MARCACAO_FIM UNIQUE (COD_MARCACAO_FIM)
);
COMMENT ON TABLE MARCACAO_FIM IS 'Armazena apenas marcações de fim';

-- CRIA TABELA PARA ARMAZENAR O VÍNCULO ENTRE AS MARCAÇÕES DE INÍCIO E FIM
CREATE TABLE IF NOT EXISTS MARCACAO_VINCULO_INICIO_FIM (
  CODIGO BIGSERIAL NOT NULL,
  COD_MARCACAO_INICIO BIGINT NOT NULL,
  COD_MARCACAO_FIM BIGINT NOT NULL,
  POSSUI_INCONSISTENCIA BOOLEAN DEFAULT FALSE,
  CONSTRAINT PK_MARCACAO_VINCULO_INICIO_FIM PRIMARY KEY (CODIGO),
  CONSTRAINT FK_MARCACAO_VINCULO_MARCACAO_INICIO FOREIGN KEY (COD_MARCACAO_INICIO)
  REFERENCES MARCACAO_INICIO(COD_MARCACAO_INICIO),
  CONSTRAINT FK_MARCACAO_VINCULO_MARCACAO_FIM FOREIGN KEY (COD_MARCACAO_FIM)
  REFERENCES MARCACAO_FIM(COD_MARCACAO_FIM),
  CONSTRAINT UNICO_CODIGO_INICIO UNIQUE (COD_MARCACAO_INICIO),
  CONSTRAINT UNICO_CODIGO_FIM UNIQUE (COD_MARCACAO_FIM),
  CONSTRAINT UNICO_VINCULO_INICIO_FIM UNIQUE (COD_MARCACAO_INICIO, COD_MARCACAO_FIM)
);
COMMENT ON TABLE MARCACAO_VINCULO_INICIO_FIM
IS 'Armazena o código da marcação de início vinculando a uma marcação de fim';

-- PROCESSO DE MIGRAÇAO DE DADOS
ALTER TABLE MARCACAO_INICIO ADD COLUMN COD_MARCACAO_FIM_TEMP BIGINT;
ALTER TABLE MARCACAO_FIM ADD COLUMN COD_MARCACAO_INICIO_TEMP BIGINT;

WITH MARCACOES_AGRUPADAS AS (
    SELECT
      MA.COD_UNIDADE,
      MA.COD_TIPO_INTERVALO,
      MA.CPF_COLABORADOR,
      MA.COD_MARCACAO_INICIO,
      MA.COD_MARCACAO_FIM
    FROM FUNC_INTERVALOS_AGRUPADOS(NULL, NULL, NULL) AS MA
)
-- MOVE MARCAÇÕES DE INÍCIO APENAS
INSERT INTO MARCACAO_INICIO (COD_MARCACAO_INICIO, COD_MARCACAO_FIM_TEMP)
  SELECT MA.COD_MARCACAO_INICIO, MA.COD_MARCACAO_FIM FROM MARCACOES_AGRUPADAS MA
  WHERE MA.COD_MARCACAO_INICIO IS NOT NULL;

WITH MARCACOES_AGRUPADAS AS (
    SELECT
      MA.COD_UNIDADE,
      MA.COD_TIPO_INTERVALO,
      MA.CPF_COLABORADOR,
      MA.COD_MARCACAO_INICIO,
      MA.COD_MARCACAO_FIM
    FROM FUNC_INTERVALOS_AGRUPADOS(NULL, NULL, NULL) AS MA
)
-- MOVE MARCAÇÕES DE FIM APENAS
INSERT INTO MARCACAO_FIM (COD_MARCACAO_FIM, COD_MARCACAO_INICIO_TEMP)
  SELECT COD_MARCACAO_FIM, COD_MARCACAO_INICIO FROM MARCACOES_AGRUPADAS MA
  WHERE MA.COD_MARCACAO_FIM IS NOT NULL;

-- CRIA VINCULOS NAS MARCAÇÕES QUE POSSUEM INÍCIO E FIM
INSERT INTO MARCACAO_VINCULO_INICIO_FIM (COD_MARCACAO_INICIO, COD_MARCACAO_FIM)
  SELECT
    MI.COD_MARCACAO_INICIO,
    MF.COD_MARCACAO_FIM
  FROM MARCACAO_INICIO MI
    LEFT JOIN MARCACAO_FIM MF
      ON MI.COD_MARCACAO_FIM_TEMP = MF.COD_MARCACAO_FIM
  WHERE MI.COD_MARCACAO_INICIO IS NOT NULL
        AND MF.COD_MARCACAO_FIM IS NOT NULL;

-- DELETA COLUNAS AUXILIARES PARA O PROCESSO DE MIGRAÇÃO
ALTER TABLE MARCACAO_INICIO DROP COLUMN COD_MARCACAO_FIM_TEMP;
ALTER TABLE MARCACAO_FIM DROP COLUMN COD_MARCACAO_INICIO_TEMP;

-- INSERE DATA_HORA_SINCRONIZAÇÃO = DATA_HORA + 1 MINUTO, PARA MARCAÇÕES QUE NÃO POSSUEM DATA_HORA_SINCRONIZAÇÃO
UPDATE INTERVALO SET DATA_HORA_SINCRONIZACAO = (DATA_HORA + INTERVAL '1 minute') WHERE DATA_HORA_SINCRONIZACAO IS NULL;

-- CRIA NOVAS COLUNAS NA TABELA INTERVALO
ALTER TABLE INTERVALO ADD COLUMN VERSAO_APP_MOMENTO_MARCACAO INTEGER;
ALTER TABLE INTERVALO ADD COLUMN VERSAO_APP_MOMENTO_SINCRONIZACAO INTEGER;
ALTER TABLE INTERVALO ALTER COLUMN DATA_HORA SET NOT NULL;
ALTER TABLE INTERVALO ADD COLUMN FOI_AJUSTADO BOOLEAN DEFAULT FALSE;
ALTER TABLE INTERVALO ADD COLUMN COD_COLABORADOR_INSERCAO BIGINT DEFAULT NULL;
ALTER TABLE INTERVALO ADD COLUMN STATUS_ATIVO BOOLEAN DEFAULT TRUE;
ALTER TABLE INTERVALO ADD CONSTRAINT FK_INTERVALO_COLABORADOR_INSERCAO FOREIGN KEY (COD_COLABORADOR_INSERCAO)
REFERENCES COLABORADOR(CODIGO);
COMMENT ON COLUMN INTERVALO.COD_COLABORADOR_INSERCAO
IS 'Esta coluna estará preenchida apenas se a marcação tiver sido inserida através do processo de Ajuste de Marcação';
COMMENT ON COLUMN INTERVALO.STATUS_ATIVO
IS 'Está flag serva para identificar se a marcação encontra-se ativa ou intavida. Somente será setada como inativa caso for inativada por algum supervisor';



-- ########################################################################################################
-- ########################################################################################################
-- ################ CRIAÇÃO DAS TABELAS NECESSÁRIAS PARA A EDIÇÃO DE MARCAÇÕES  ###########################
-- ########################################################################################################
-- ########################################################################################################

-- TABELA PARA SALVAR AS POSSÍVEIS JUSTIFICATIVAS QUE AS EDIÇÕES PODE TER
CREATE TABLE IF NOT EXISTS MARCACAO_JUSTIFICATIVA_AJUSTE (
  CODIGO BIGSERIAL NOT NULL,
  NOME TEXT NOT NULL,
  COD_EMPRESA BIGINT,
  OBRIGA_OBSERVACAO BOOLEAN NOT NULL DEFAULT TRUE,
  STATUS_ATIVO BOOLEAN NOT NULL DEFAULT TRUE,
  EDITAVEL BOOLEAN NOT NULL DEFAULT TRUE,
  COD_COLABORADOR_CRIACAO BIGINT,
  DATA_HORA_CRIACAO TIMESTAMP,
  COD_COLABORADOR_ULTIMA_EDICAO BIGINT,
  DATA_HORA_ULTIMA_EDICAO TIMESTAMP,
  CONSTRAINT PK_MARCACAO_JUSTIFICATIVA_AJUSTE PRIMARY KEY (CODIGO),
  CONSTRAINT FK_MARCACAO_JUSTIFICATIVA_AJUSTE_EMPRESA FOREIGN KEY (COD_EMPRESA) REFERENCES EMPRESA(CODIGO),
  CONSTRAINT FK_MARCACAO_JUSTIFICATIVA_AJUSTE_COLABORADOR_CRIACAO FOREIGN KEY (COD_COLABORADOR_CRIACAO)
  REFERENCES COLABORADOR(CODIGO),
  CONSTRAINT FK_MARCACAO_JUSTIFICATIVA_AJUSTE_COLABORADOR_EDICAO FOREIGN KEY (COD_COLABORADOR_ULTIMA_EDICAO)
  REFERENCES COLABORADOR(CODIGO),
  CONSTRAINT UNIQUE_NOME_JUSTIFICATIVA_EDICAO UNIQUE (COD_EMPRESA, NOME),
  CONSTRAINT CHECK_EDITAVEL_EMPRESA CHECK (NOT(COD_EMPRESA IS NULL AND EDITAVEL IS TRUE))
);
COMMENT ON TABLE MARCACAO_JUSTIFICATIVA_AJUSTE IS
  'Contém as possíveis justificativas que os ajustes de marcações poderão ter';

COMMENT ON COLUMN MARCACAO_JUSTIFICATIVA_AJUSTE.COD_EMPRESA IS
  'Se o COD_EMPRESA for null, então essa justificativa é a nível ProLog, disponível para todas as empresas';

INSERT INTO MARCACAO_JUSTIFICATIVA_AJUSTE (NOME, COD_EMPRESA, OBRIGA_OBSERVACAO, STATUS_ATIVO, EDITAVEL, DATA_HORA_CRIACAO)
VALUES ('ESQUECIMENTO', NULL, FALSE, TRUE, FALSE, NOW());
INSERT INTO MARCACAO_JUSTIFICATIVA_AJUSTE (NOME, COD_EMPRESA, OBRIGA_OBSERVACAO, STATUS_ATIVO, EDITAVEL, DATA_HORA_CRIACAO)
VALUES ('OUTROS - ESPECIFIQUE', NULL, TRUE, TRUE, FALSE, NOW());

-- FUNCTION PARA BUSCA DAS JUSTIFICATIVAS DE AJUSTES.
CREATE OR REPLACE FUNCTION FUNC_MARCACAO_BUSCA_JUSTIFICATIVAS_AJUSTES(
  F_COD_EMPRESA BIGINT,
  F_ATIVAS      BOOLEAN)
  RETURNS TABLE(
    CODIGO            BIGINT,
    COD_EMPRESA       BIGINT,
    NOME              TEXT,
    OBRIGA_OBSERVACAO BOOLEAN,
    STATUS_ATIVO      BOOLEAN)
LANGUAGE SQL
AS $$
SELECT
  JA.CODIGO,
  JA.COD_EMPRESA,
  JA.NOME,
  JA.OBRIGA_OBSERVACAO,
  JA.STATUS_ATIVO
FROM MARCACAO_JUSTIFICATIVA_AJUSTE JA
WHERE (JA.COD_EMPRESA = F_COD_EMPRESA AND F_IF(F_ATIVAS IS NULL, TRUE, F_ATIVAS = JA.STATUS_ATIVO))
      OR (JA.COD_EMPRESA IS NULL AND JA.STATUS_ATIVO = TRUE);
$$;

-- CRIA TRIGGER PARA GARANTIR QUE JUSTIFICATIVAS QUE EXIJAM OBSERVAÇÃO, TENHAM, DE FATO, UMA OBSERVAÇÃO NÃO NULA.
CREATE OR REPLACE FUNCTION TG_MARCACAO_VERIFICA_OBSERVACAO_JUSTIFICATIVA()
  RETURNS TRIGGER AS $$
DECLARE
  OBRIGA_OBSERVACAO BOOLEAN := (SELECT OBRIGA_OBSERVACAO
                                FROM MARCACAO_JUSTIFICATIVA_AJUSTE
                                WHERE CODIGO = NEW.COD_JUSTIFICATIVA_AJUSTE);
BEGIN
  IF OBRIGA_OBSERVACAO AND (NEW.OBSERVACAO_AJUSTE IS NULL OR LENGTH(NEW.OBSERVACAO_AJUSTE) = 0)
  THEN RAISE EXCEPTION 'Justificativa de código % exige observação', NEW.COD_JUSTIFICATIVA_AJUSTE;
  END IF;
  RETURN NEW;
END;
$$
LANGUAGE PLPGSQL;

-- TABELA PARA SALVAR OS DADOS DE AJUSTE DE UMA MARCAÇÃO
CREATE TABLE IF NOT EXISTS MARCACAO_AJUSTE (
  CODIGO BIGSERIAL NOT NULL,
  COD_JUSTIFICATIVA_AJUSTE BIGINT NOT NULL,
  OBSERVACAO_AJUSTE TEXT,
  ACAO_AJUSTE VARCHAR(25) NOT NULL,
  COD_UNIDADE_AJUSTE BIGINT NOT NULL,
  COD_COLABORADOR_AJUSTE BIGINT NOT NULL,
  DATA_HORA_AJUSTE TIMESTAMP WITH TIME ZONE NOT NULL,
  CONSTRAINT PK_MARCACAO_AJUSTE PRIMARY KEY (CODIGO),
  CONSTRAINT FK_MARCACAO_AJUSTE_MARCACAO_JUSTIFICATIVA_AJUSTE FOREIGN KEY (COD_JUSTIFICATIVA_AJUSTE)
  REFERENCES MARCACAO_JUSTIFICATIVA_AJUSTE(CODIGO),
  CONSTRAINT FK_MARCACAO_AJUSTE_UNIDADE FOREIGN KEY (COD_UNIDADE_AJUSTE) REFERENCES UNIDADE(CODIGO),
  CONSTRAINT FK_MARCACAO_AJUSTE_COLABORADOR FOREIGN KEY (COD_COLABORADOR_AJUSTE) REFERENCES COLABORADOR(CODIGO),
  CONSTRAINT CHECK_ACHAO_EDICAO CHECK (ACAO_AJUSTE IN ('ADICAO', 'EDICAO', 'ATIVACAO', 'INATIVACAO', 'ADICAO_INICIO_FIM'))
);
COMMENT ON TABLE MARCACAO_AJUSTE IS 'Salva metadados sobre o ajuste da marcação';
COMMENT ON COLUMN MARCACAO_AJUSTE.COD_UNIDADE_AJUSTE IS 'Salva o código da unidade onde em que o colaborador que fez o ajuste estava alocado ao realizar a ação. Também é útil para sabermos qual Time Zone aplicar nos timestamps.';

CREATE CONSTRAINT TRIGGER MARCACAO_VERIFICA_OBSERVACAO_JUSTIFICATIVA_TRIGGER
  AFTER INSERT OR UPDATE
  ON MARCACAO_AJUSTE
  DEFERRABLE
  FOR EACH ROW
EXECUTE PROCEDURE TG_MARCACAO_VERIFICA_OBSERVACAO_JUSTIFICATIVA();

-- TABELA PARA SALVAR AS VERSÕES QUE UMA MARCAÇÃO POSSUÍ,
-- CADA EDIÇÃO REALIZADA DEVERÁ GERAR UMA ENTRADA NESSA TABELA
CREATE TABLE IF NOT EXISTS MARCACAO_HISTORICO (
  CODIGO BIGSERIAL NOT NULL,
  COD_MARCACAO BIGINT NOT NULL,
  COD_AJUSTE BIGINT NOT NULL,
  DATA_HORA_ANTIGA TIMESTAMP WITH TIME ZONE NOT NULL,
  CONSTRAINT PK_MARCACAO_HISTORICO PRIMARY KEY (CODIGO),
  CONSTRAINT FK_MARCACAO_HISTORICO_MARCACAO_AJUSTE FOREIGN KEY (COD_AJUSTE) REFERENCES MARCACAO_AJUSTE(CODIGO),
  CONSTRAINT FK_MARCACAO_HISTORICO_MARCACAO FOREIGN KEY (COD_MARCACAO) REFERENCES INTERVALO(CODIGO)
);
COMMENT ON TABLE MARCACAO_HISTORICO IS
  'Salva os históricos de uma marcação, cada ajuste realizado deverá gerar uma nova entrada nessa tabela';

CREATE TABLE IF NOT EXISTS MARCACAO_INCONSISTENCIA (
  CODIGO BIGSERIAL NOT NULL,
  COD_MARCACAO_VINCULO_INICIO_FIM BIGINT NOT NULL,
  COD_MARCACAO_INCONSISTENTE BIGINT NOT NULL,
  CONSTRAINT PK_MARCACAO_INCONSISTENCIA PRIMARY KEY (CODIGO),
  CONSTRAINT FK_MARCACAO_VINCULO_INICIO_FIM FOREIGN KEY (COD_MARCACAO_VINCULO_INICIO_FIM)
  REFERENCES MARCACAO_VINCULO_INICIO_FIM(CODIGO),
  CONSTRAINT FK_MARCACAO_INCONSISTENCIA_MARCACAO FOREIGN KEY (COD_MARCACAO_INCONSISTENTE) REFERENCES INTERVALO(CODIGO)
);
COMMENT ON TABLE MARCACAO_INCONSISTENCIA IS 'Vincula os códigos das marcações que foram sincronizadas para o mesmo início';
-- ########################################################################################################
-- ########################################################################################################

-- ########################################################################################################
-- ##################################            WARNING           ########################################
-- ########## PARA EXECUTAR ESSA PARTE É NECESSÁRIO QUE A MIGRATION 'migration_intervalos_antigos' ########
-- ##########                           JÁ TENHA SIDO EXECUTADA SEM NENHUM ERRO                    ########
-- ########################################################################################################
-- ########################################################################################################
-- AGORA PRECISAMOS RECRIAR A FUNÇÃO QUE BUSCA OS INTERVALOS AGRUPADOS
DROP VIEW VIEW_INTERVALO_MAPA_COLABORADOR;
DROP VIEW VIEW_EXTRATO_MAPAS_VERSUS_INTERVALOS;
DROP FUNCTION FUNC_INTERVALOS_AGRUPADOS(BIGINT, BIGINT, BIGINT);
CREATE OR REPLACE FUNCTION FUNC_INTERVALOS_AGRUPADOS(
  F_COD_UNIDADE        BIGINT,
  F_CPF_COLABORADOR    BIGINT,
  F_COD_TIPO_INTERVALO BIGINT)
  RETURNS TABLE(
    FONTE_DATA_HORA_FIM             TEXT,
    FONTE_DATA_HORA_INICIO          TEXT,
    JUSTIFICATIVA_ESTOURO           TEXT,
    JUSTIFICATIVA_TEMPO_RECOMENDADO TEXT,
    LATITUDE_MARCACAO_INICIO        TEXT,
    LONGITUDE_MARCACAO_INICIO       TEXT,
    LATITUDE_MARCACAO_FIM           TEXT,
    LONGITUDE_MARCACAO_FIM          TEXT,
    COD_UNIDADE                     BIGINT,
    CPF_COLABORADOR                 BIGINT,
    COD_TIPO_INTERVALO              BIGINT,
    COD_TIPO_INTERVALO_POR_UNIDADE  BIGINT,
    DATA_HORA_INICIO                TIMESTAMP WITH TIME ZONE,
    DATA_HORA_FIM                   TIMESTAMP WITH TIME ZONE,
    COD_MARCACAO_INICIO             BIGINT,
    COD_MARCACAO_FIM                BIGINT,
    STATUS_ATIVO_INICIO             BOOLEAN,
    STATUS_ATIVO_FIM                BOOLEAN,
    FOI_AJUSTADO_INICIO             BOOLEAN,
    FOI_AJUSTADO_FIM                BOOLEAN,
    DATA_HORA_SINCRONIZACAO_INICIO  TIMESTAMP WITH TIME ZONE,
    DATA_HORA_SINCRONIZACAO_FIM     TIMESTAMP WITH TIME ZONE)
LANGUAGE SQL
AS $$
WITH INICIOS AS (
    SELECT
      MI.COD_MARCACAO_INICIO,
      MV.COD_MARCACAO_FIM                   AS COD_MARCACAO_VINCULO,
      I.FONTE_DATA_HORA                     AS FONTE_DATA_HORA_INICIO,
      I.LATITUDE_MARCACAO                   AS LATITUDE_MARCACAO_INICIO,
      I.LONGITUDE_MARCACAO                  AS LONGITUDE_MARCACAO_INICIO,
      I.COD_UNIDADE                         AS COD_UNIDADE,
      I.CPF_COLABORADOR                     AS CPF_COLABORADOR,
      I.COD_TIPO_INTERVALO                  AS COD_TIPO_INTERVALO,
      I.DATA_HORA                           AS DATA_HORA_INICIO,
      I.CODIGO                              AS CODIGO_INICIO,
      I.STATUS_ATIVO                        AS STATUS_ATIVO_INICIO,
      I.FOI_AJUSTADO                        AS FOI_AJUSTADO_INICIO,
      I.DATA_HORA_SINCRONIZACAO             AS DATA_HORA_SINCRONIZACAO_INICIO,
      VIT.CODIGO_TIPO_INTERVALO_POR_UNIDADE AS COD_TIPO_INTERVALO_POR_UNIDADE
    FROM MARCACAO_INICIO MI
      LEFT JOIN MARCACAO_VINCULO_INICIO_FIM MV
        ON MI.COD_MARCACAO_INICIO = MV.COD_MARCACAO_INICIO
      JOIN INTERVALO I
        ON MI.COD_MARCACAO_INICIO = I.CODIGO
      JOIN VIEW_INTERVALO_TIPO VIT
        ON I.COD_TIPO_INTERVALO = VIT.CODIGO
    WHERE I.VALIDO = TRUE
          AND CASE WHEN F_COD_UNIDADE IS NULL THEN TRUE ELSE I.COD_UNIDADE = F_COD_UNIDADE END
          AND CASE WHEN F_CPF_COLABORADOR IS NULL THEN TRUE ELSE I.CPF_COLABORADOR = F_CPF_COLABORADOR END
          AND CASE WHEN F_COD_TIPO_INTERVALO IS NULL THEN TRUE ELSE I.COD_TIPO_INTERVALO = F_COD_TIPO_INTERVALO END
),

    FINS AS (
      SELECT
        MF.COD_MARCACAO_FIM,
        MV.COD_MARCACAO_INICIO                AS COD_MARCACAO_VINCULO,
        F.FONTE_DATA_HORA                     AS FONTE_DATA_HORA_FIM,
        F.JUSTIFICATIVA_ESTOURO               AS JUSTIFICATIVA_ESTOURO,
        F.JUSTIFICATIVA_TEMPO_RECOMENDADO     AS JUSTIFICATIVA_TEMPO_RECOMENDADO,
        F.LATITUDE_MARCACAO                   AS LATITUDE_MARCACAO_FIM,
        F.LONGITUDE_MARCACAO                  AS LONGITUDE_MARCACAO_FIM,
        F.COD_UNIDADE                         AS COD_UNIDADE,
        F.CPF_COLABORADOR                     AS CPF_COLABORADOR,
        F.COD_TIPO_INTERVALO                  AS COD_TIPO_INTERVALO,
        F.DATA_HORA                           AS DATA_HORA_FIM,
        F.CODIGO                              AS CODIGO_FIM,
        F.STATUS_ATIVO                        AS STATUS_ATIVO_FIM,
        F.FOI_AJUSTADO                        AS FOI_AJUSTADO_FIM,
        F.DATA_HORA_SINCRONIZACAO             AS DATA_HORA_SINCRONIZACAO_FIM,
        VIT.CODIGO_TIPO_INTERVALO_POR_UNIDADE AS COD_TIPO_INTERVALO_POR_UNIDADE
      FROM MARCACAO_FIM MF
        LEFT JOIN MARCACAO_VINCULO_INICIO_FIM MV
          ON MF.COD_MARCACAO_FIM = MV.COD_MARCACAO_FIM
        JOIN INTERVALO F
          ON MF.COD_MARCACAO_FIM = F.CODIGO
        JOIN VIEW_INTERVALO_TIPO VIT
          ON F.COD_TIPO_INTERVALO = VIT.CODIGO
      WHERE F.VALIDO = TRUE
            AND CASE WHEN F_COD_UNIDADE IS NULL THEN TRUE ELSE F.COD_UNIDADE = F_COD_UNIDADE END
            AND CASE WHEN F_CPF_COLABORADOR IS NULL THEN TRUE ELSE F.CPF_COLABORADOR = F_CPF_COLABORADOR END
            AND CASE WHEN F_COD_TIPO_INTERVALO IS NULL THEN TRUE ELSE F.COD_TIPO_INTERVALO = F_COD_TIPO_INTERVALO END
  )

SELECT
  I.FONTE_DATA_HORA_INICIO,
  F.FONTE_DATA_HORA_FIM,
  F.JUSTIFICATIVA_ESTOURO,
  F.JUSTIFICATIVA_TEMPO_RECOMENDADO,
  I.LATITUDE_MARCACAO_INICIO,
  I.LONGITUDE_MARCACAO_INICIO,
  F.LATITUDE_MARCACAO_FIM,
  F.LONGITUDE_MARCACAO_FIM,
  COALESCE(I.COD_UNIDADE, F.COD_UNIDADE) AS COD_UNIDADE,
  COALESCE(I.CPF_COLABORADOR, F.CPF_COLABORADOR) AS CPF_COLABORADOR,
  COALESCE(I.COD_TIPO_INTERVALO, F.COD_TIPO_INTERVALO) AS COD_TIPO_INTERVALO,
  COALESCE(I.COD_TIPO_INTERVALO_POR_UNIDADE, F.COD_TIPO_INTERVALO_POR_UNIDADE) AS COD_TIPO_INTERVALO_POR_UNIDADE,
  I.DATA_HORA_INICIO,
  F.DATA_HORA_FIM,
  I.CODIGO_INICIO,
  F.CODIGO_FIM,
  I.STATUS_ATIVO_INICIO,
  F.STATUS_ATIVO_FIM,
  I.FOI_AJUSTADO_INICIO,
  F.FOI_AJUSTADO_FIM,
  I.DATA_HORA_SINCRONIZACAO_INICIO,
  F.DATA_HORA_SINCRONIZACAO_FIM
FROM INICIOS I
  FULL OUTER JOIN FINS F
    ON I.COD_MARCACAO_VINCULO = F.COD_MARCACAO_FIM
ORDER BY
  CPF_COLABORADOR,
  COD_TIPO_INTERVALO,
  COALESCE(DATA_HORA_INICIO, DATA_HORA_FIM)
$$;

create view view_extrato_mapas_versus_intervalos as
  SELECT m.data,
    m.mapa,
    m.cod_unidade,
    (m.fator + (1)::double precision) AS intervalos_previstos,
    ((
        CASE
            WHEN (COALESCE((trunc((date_part('epoch'::text, (int_aj1.data_hora_fim - int_aj1.data_hora_inicio)) / (60)::double precision)))::text, '-'::text) <> '-'::text) THEN 1
            ELSE 0
        END +
        CASE
            WHEN (COALESCE((trunc((date_part('epoch'::text, (int_aj2.data_hora_fim - int_aj2.data_hora_inicio)) / (60)::double precision)))::text, '-'::text) <> '-'::text) THEN 1
            ELSE 0
        END) +
        CASE
            WHEN (COALESCE((trunc((date_part('epoch'::text, (int_mot.data_hora_fim - int_mot.data_hora_inicio)) / (60)::double precision)))::text, '-'::text) <> '-'::text) THEN 1
            ELSE 0
        END) AS intervalos_realizados,
    mot.cpf AS cpf_motorista,
    mot.nome AS nome_motorista,
    COALESCE(to_char(((int_mot.data_hora_inicio)::time without time zone)::interval, 'HH24:MI'::text), '-'::text) AS inicio_intervalo_mot,
    COALESCE(to_char(((int_mot.data_hora_fim)::time without time zone)::interval, 'HH24:MI'::text), '-'::text) AS fim_intervalo_mot,
    COALESCE((trunc((date_part('epoch'::text, (int_mot.data_hora_fim - int_mot.data_hora_inicio)) / (60)::double precision)))::text, '-'::text) AS tempo_decorrido_minutos_mot,
        CASE
            WHEN (int_mot.data_hora_fim IS NULL) THEN '-'::text
            WHEN ((60)::double precision > (date_part('epoch'::text, (int_mot.data_hora_fim - int_mot.data_hora_inicio)) / (60)::double precision)) THEN 'NÃO'::text
            ELSE 'SIM'::text
        END AS mot_cumpriu_tempo_minimo,
    aj1.cpf AS cpf_aj1,
    COALESCE(aj1.nome, '-'::character varying) AS nome_aj1,
    COALESCE(to_char(((int_aj1.data_hora_inicio)::time without time zone)::interval, 'HH24:MI'::text), '-'::text) AS inicio_intervalo_aj1,
    COALESCE(to_char(((int_aj1.data_hora_fim)::time without time zone)::interval, 'HH24:MI'::text), '-'::text) AS fim_intervalo_aj1,
    COALESCE((trunc((date_part('epoch'::text, (int_aj1.data_hora_fim - int_aj1.data_hora_inicio)) / (60)::double precision)))::text, '-'::text) AS tempo_decorrido_minutos_aj1,
        CASE
            WHEN (int_aj1.data_hora_fim IS NULL) THEN '-'::text
            WHEN ((60)::double precision > (date_part('epoch'::text, (int_aj1.data_hora_fim - int_aj1.data_hora_inicio)) / (60)::double precision)) THEN 'NÃO'::text
            ELSE 'SIM'::text
        END AS aj1_cumpriu_tempo_minimo,
    aj2.cpf AS cpf_aj2,
    COALESCE(aj2.nome, '-'::character varying) AS nome_aj2,
    COALESCE(to_char(((int_aj2.data_hora_inicio)::time without time zone)::interval, 'HH24:MI'::text), '-'::text) AS inicio_intervalo_aj2,
    COALESCE(to_char(((int_aj2.data_hora_fim)::time without time zone)::interval, 'HH24:MI'::text), '-'::text) AS fim_intervalo_aj2,
    COALESCE((trunc((date_part('epoch'::text, (int_aj2.data_hora_fim - int_aj2.data_hora_inicio)) / (60)::double precision)))::text, '-'::text) AS tempo_decorrido_minutos_aj2,
        CASE
            WHEN (int_aj2.data_hora_fim IS NULL) THEN '-'::text
            WHEN ((60)::double precision > (date_part('epoch'::text, (int_aj2.data_hora_fim - int_aj2.data_hora_inicio)) / (60)::double precision)) THEN 'NÃO'::text
            ELSE 'SIM'::text
        END AS aj2_cumpriu_tempo_minimo
   FROM (((((((mapa m
     JOIN unidade_funcao_produtividade ufp ON ((ufp.cod_unidade = m.cod_unidade)))
     JOIN colaborador mot ON (((mot.cod_unidade = m.cod_unidade) AND (mot.cod_funcao = ufp.cod_funcao_motorista) AND (mot.matricula_ambev = m.matricmotorista))))
     LEFT JOIN colaborador aj1 ON (((aj1.cod_unidade = m.cod_unidade) AND (aj1.cod_funcao = ufp.cod_funcao_ajudante) AND (aj1.matricula_ambev = m.matricajud1))))
     LEFT JOIN colaborador aj2 ON (((aj2.cod_unidade = m.cod_unidade) AND (aj2.cod_funcao = ufp.cod_funcao_ajudante) AND (aj2.matricula_ambev = m.matricajud2))))
     LEFT JOIN func_intervalos_agrupados(NULL::bigint, NULL::bigint, NULL::bigint) int_mot(fonte_data_hora_fim, fonte_data_hora_inicio, justificativa_estouro, justificativa_tempo_recomendado, latitude_marcacao_inicio, longitude_marcacao_inicio, latitude_marcacao_fim, longitude_marcacao_fim, cod_unidade, cpf_colaborador, cod_tipo_intervalo, cod_tipo_intervalo_por_unidade, data_hora_inicio, data_hora_fim, cod_marcacao_inicio, cod_marcacao_fim) ON (((int_mot.cpf_colaborador = mot.cpf) AND ((int_mot.data_hora_inicio)::date = m.data))))
     LEFT JOIN func_intervalos_agrupados(NULL::bigint, NULL::bigint, NULL::bigint) int_aj1(fonte_data_hora_fim, fonte_data_hora_inicio, justificativa_estouro, justificativa_tempo_recomendado, latitude_marcacao_inicio, longitude_marcacao_inicio, latitude_marcacao_fim, longitude_marcacao_fim, cod_unidade, cpf_colaborador, cod_tipo_intervalo, cod_tipo_intervalo_por_unidade, data_hora_inicio, data_hora_fim, cod_marcacao_inicio, cod_marcacao_fim) ON (((int_aj1.cpf_colaborador = aj1.cpf) AND ((int_aj1.data_hora_inicio)::date = m.data))))
     LEFT JOIN func_intervalos_agrupados(NULL::bigint, NULL::bigint, NULL::bigint) int_aj2(fonte_data_hora_fim, fonte_data_hora_inicio, justificativa_estouro, justificativa_tempo_recomendado, latitude_marcacao_inicio, longitude_marcacao_inicio, latitude_marcacao_fim, longitude_marcacao_fim, cod_unidade, cpf_colaborador, cod_tipo_intervalo, cod_tipo_intervalo_por_unidade, data_hora_inicio, data_hora_fim, cod_marcacao_inicio, cod_marcacao_fim) ON (((int_aj2.cpf_colaborador = aj2.cpf) AND ((int_aj2.data_hora_inicio)::date = m.data))))
  ORDER BY m.mapa DESC;

create view view_intervalo_mapa_colaborador as
  SELECT view_extrato_mapas_versus_intervalos.data,
    view_extrato_mapas_versus_intervalos.mapa,
    view_extrato_mapas_versus_intervalos.cod_unidade,
    view_extrato_mapas_versus_intervalos.cpf_motorista AS cpf,
    view_extrato_mapas_versus_intervalos.inicio_intervalo_mot AS inicio_intervalo,
    view_extrato_mapas_versus_intervalos.fim_intervalo_mot AS fim_intervalo,
    view_extrato_mapas_versus_intervalos.tempo_decorrido_minutos_mot AS tempo_decorrido_minutos,
    view_extrato_mapas_versus_intervalos.mot_cumpriu_tempo_minimo AS cumpriu_tempo_minimo
   FROM view_extrato_mapas_versus_intervalos
UNION
 SELECT view_extrato_mapas_versus_intervalos.data,
    view_extrato_mapas_versus_intervalos.mapa,
    view_extrato_mapas_versus_intervalos.cod_unidade,
    view_extrato_mapas_versus_intervalos.cpf_aj1 AS cpf,
    view_extrato_mapas_versus_intervalos.inicio_intervalo_aj1 AS inicio_intervalo,
    view_extrato_mapas_versus_intervalos.fim_intervalo_aj1 AS fim_intervalo,
    view_extrato_mapas_versus_intervalos.tempo_decorrido_minutos_aj1 AS tempo_decorrido_minutos,
    view_extrato_mapas_versus_intervalos.aj1_cumpriu_tempo_minimo AS cumpriu_tempo_minimo
   FROM view_extrato_mapas_versus_intervalos
UNION
 SELECT view_extrato_mapas_versus_intervalos.data,
    view_extrato_mapas_versus_intervalos.mapa,
    view_extrato_mapas_versus_intervalos.cod_unidade,
    view_extrato_mapas_versus_intervalos.cpf_aj2 AS cpf,
    view_extrato_mapas_versus_intervalos.inicio_intervalo_aj2 AS inicio_intervalo,
    view_extrato_mapas_versus_intervalos.fim_intervalo_aj2 AS fim_intervalo,
    view_extrato_mapas_versus_intervalos.tempo_decorrido_minutos_aj2 AS tempo_decorrido_minutos,
    view_extrato_mapas_versus_intervalos.aj2_cumpriu_tempo_minimo AS cumpriu_tempo_minimo
   FROM view_extrato_mapas_versus_intervalos;


-- ########################################################################################################
-- ########################################################################################################

-- ########################################################################################################
-- ########################################################################################################
-- ######################### FUNTIONS PARA REALIZAR AJUSTES NAS MARCAÇÕES DE JORNADA ######################
-- ########################################################################################################
-- ########################################################################################################
CREATE OR REPLACE FUNCTION FUNC_MARCACAO_INSERT_INFORMACOES_AJUSTE(
  F_COD_MARCACAO_AJUSTADA    BIGINT,
  F_DATA_HORA_INSERIDA       TIMESTAMP WITH TIME ZONE,
  F_COD_JUSTIFICATIVA        BIGINT,
  F_OBSERVACAO_AJUSTE        TEXT,
  F_ACAO_AJUSTE              VARCHAR(25),
  F_TOKEN_RESPONSAVEL_AJUSTE TEXT,
  F_DATA_HORA_AJUSTE         TIMESTAMP WITH TIME ZONE)
  RETURNS BIGINT
LANGUAGE PLPGSQL
AS $$
DECLARE
  DATA_HORA_ATUAL_MARCACAO TIMESTAMP WITH TIME ZONE := (SELECT I.DATA_HORA
                                                        FROM INTERVALO I
                                                        WHERE I.CODIGO = F_COD_MARCACAO_AJUSTADA);
  COD_UNIDADE_AJUSTE       BIGINT;
  COD_COLABORADOR_AJUSTE   BIGINT;
  COD_AJUSTE_REALIZADO     BIGINT;
  COD_HISTORICO_MARCACAO   BIGINT;
BEGIN
  SELECT INTO COD_UNIDADE_AJUSTE, COD_COLABORADOR_AJUSTE
    COD_UNIDADE,
    CODIGO
  FROM COLABORADOR C
  WHERE CPF = (SELECT CPF_COLABORADOR
               FROM TOKEN_AUTENTICACAO
               WHERE TOKEN = F_TOKEN_RESPONSAVEL_AJUSTE);

  -- APENAS PARA O CASO DE EDIÇÃO, TEREMOS UM DATA/HORA INSERIDA E PRECISAREMOS ATUALIZAR NA TABELA INTERVALO.
  IF (F_ACAO_AJUSTE = 'EDICAO')
  THEN
    UPDATE INTERVALO
    SET DATA_HORA = F_DATA_HORA_INSERIDA
    WHERE CODIGO = F_COD_MARCACAO_AJUSTADA;

    IF NOT FOUND
    THEN RAISE EXCEPTION 'Erro ao atualizar a data/hora da marcação: %', F_COD_MARCACAO_AJUSTADA;
    END IF;
  END IF;

  INSERT INTO MARCACAO_AJUSTE (COD_JUSTIFICATIVA_AJUSTE,
                               OBSERVACAO_AJUSTE,
                               ACAO_AJUSTE,
                               COD_UNIDADE_AJUSTE,
                               COD_COLABORADOR_AJUSTE,
                               DATA_HORA_AJUSTE)
  VALUES (F_COD_JUSTIFICATIVA,
          F_OBSERVACAO_AJUSTE,
          F_ACAO_AJUSTE,
          COD_UNIDADE_AJUSTE,
          COD_COLABORADOR_AJUSTE,
          F_DATA_HORA_AJUSTE)
  RETURNING CODIGO
    INTO COD_AJUSTE_REALIZADO;

  IF NOT FOUND OR COD_AJUSTE_REALIZADO IS NULL OR COD_AJUSTE_REALIZADO <= 0
  THEN RAISE EXCEPTION 'Erro ao salvar informações na tabela MARCACAO_AJUSTE: %', COD_AJUSTE_REALIZADO;
  END IF;

  INSERT INTO MARCACAO_HISTORICO (COD_MARCACAO, COD_AJUSTE, DATA_HORA_ANTIGA)
  VALUES (
    F_COD_MARCACAO_AJUSTADA,
    COD_AJUSTE_REALIZADO,
    DATA_HORA_ATUAL_MARCACAO)
  RETURNING CODIGO
    INTO COD_HISTORICO_MARCACAO;

  IF NOT FOUND OR COD_HISTORICO_MARCACAO IS NULL OR COD_HISTORICO_MARCACAO <= 0
  THEN RAISE EXCEPTION 'Erro ao salvar informações na tabela MARCACAO_HISTORICO: %', COD_HISTORICO_MARCACAO;
  END IF;

  RETURN COD_HISTORICO_MARCACAO;
END;
$$;

-- VAMOS CRIAR UMA TRIGGER PARA QUE SEMPRE QUE UMA MARCAÇÃO SEJA AJUSTADA,
-- IRÁ SETAR FOI_AJUSTADO = true NA TABELA INTERVALO
CREATE OR REPLACE FUNCTION TG_FUNC_MARCACAO_JORNADA_EDITADA()
  RETURNS TRIGGER AS $MARCACAO_JORNADA_EDITADA_TRIGGER$
BEGIN
  UPDATE INTERVALO SET FOI_AJUSTADO = TRUE
  WHERE FOI_AJUSTADO = FALSE AND CODIGO = NEW.COD_MARCACAO;
  RETURN NEW;
END;
$MARCACAO_JORNADA_EDITADA_TRIGGER$
LANGUAGE plpgsql;

CREATE CONSTRAINT TRIGGER MARCACAO_JORNADA_AJUSTADA_TRIGGER
  AFTER INSERT OR UPDATE ON MARCACAO_HISTORICO
  DEFERRABLE
  FOR EACH ROW
EXECUTE PROCEDURE TG_FUNC_MARCACAO_JORNADA_EDITADA();
-- ########################################################################################################
-- ########################################################################################################


-- ########################################################################################################
-- ########################################################################################################
-- ######################### CRIA FUNCTION PARA BUSCAR AS MARCAÇÕES CONSOLIDADAS ##########################
-- ########################################################################################################
-- ########################################################################################################
CREATE OR REPLACE FUNCTION FUNC_MARCACAO_GET_MARCACOES_CONSOLIDADAS_AJUSTE(
  F_COD_UNIDADE       BIGINT,
  F_COD_TIPO_MARCACAO BIGINT,
  F_COD_COLABORADOR   BIGINT,
  F_DATA_INICIAL      DATE,
  F_DATA_FINAL        DATE)
  RETURNS TABLE(
    DIA                             DATE,
    COD_COLABORADOR                 BIGINT,
    NOME_COLABORADOR                VARCHAR,
    TOTAL_MARCACOES_COLABORADOR_DIA BIGINT,
    TOTAL_MARCACOES_GERAL_DIA       NUMERIC
  )
LANGUAGE PLPGSQL
AS $$
DECLARE
  CPF_COLABORADOR_FILTRO BIGINT := CASE WHEN F_COD_COLABORADOR IS NOT NULL
    THEN (SELECT C.CPF
          FROM COLABORADOR C
          WHERE C.CODIGO = F_COD_COLABORADOR) END;
BEGIN
  RETURN QUERY
  WITH MARCACOES_DIA AS (
      SELECT
        (I.DATA_HORA AT TIME ZONE TZ_UNIDADE(I.COD_UNIDADE)) :: DATE AS DIA,
        I.CPF_COLABORADOR                                            AS CPF_COLABORADOR,
        COUNT(I.CPF_COLABORADOR)                                     AS TOTAL_MARCACOES_COLABORADOR_DIA
      FROM INTERVALO I
        JOIN COLABORADOR C
          ON I.CPF_COLABORADOR = C.CPF
      WHERE I.COD_UNIDADE = F_COD_UNIDADE
            AND F_IF(F_COD_COLABORADOR IS NULL, TRUE, I.CPF_COLABORADOR = CPF_COLABORADOR_FILTRO)
            AND F_IF(F_COD_TIPO_MARCACAO IS NULL, TRUE, I.COD_TIPO_INTERVALO = F_COD_TIPO_MARCACAO)
            AND (I.DATA_HORA AT TIME ZONE TZ_UNIDADE(I.COD_UNIDADE)) :: DATE BETWEEN F_DATA_INICIAL AND F_DATA_FINAL
      GROUP BY I.CPF_COLABORADOR, DIA
  )

  SELECT
    MD.DIA                             AS DIA,
    C.CODIGO                           AS COD_COLABORADOR,
    C.NOME                             AS NOME_COLABORADOR,
    MD.TOTAL_MARCACOES_COLABORADOR_DIA AS TOTAL_MARCACOES_COLABORADOR_DIA,
    SUM(MD.TOTAL_MARCACOES_COLABORADOR_DIA)
    OVER (PARTITION BY MD.DIA)         AS TOTAL_MARCACOES_GERAL_DIA
  FROM MARCACOES_DIA MD
    JOIN COLABORADOR C ON MD.CPF_COLABORADOR = C.CPF
  ORDER BY MD.DIA DESC, C.NOME ASC;
END;
$$;
-- ########################################################################################################
-- ########################################################################################################

-- ########################################################################################################
-- ########################################################################################################
-- ######################### CRIA FUNCTION PARA BUSCAR AS MARCAÇÕES DE UM COLABORADOR #####################
-- ########################################################################################################
-- ########################################################################################################
CREATE OR REPLACE FUNCTION FUNC_MARCACAO_GET_MARCACOES_COLABORADOR_AJUSTE(
  F_COD_TIPO_MARCACAO BIGINT,
  F_COD_COLABORADOR   BIGINT,
  F_DIA               DATE)
  RETURNS TABLE(
    COD_MARCACAO_INICIO BIGINT,
    COD_MARCACAO_FIM    BIGINT,
    DATA_HORA_INICIO    TIMESTAMP WITHOUT TIME ZONE,
    DATA_HORA_FIM       TIMESTAMP WITHOUT TIME ZONE,
    STATUS_ATIVO_INICIO BOOLEAN,
    STATUS_ATIVO_FIM    BOOLEAN,
    FOI_AJUSTADO_INICIO BOOLEAN,
    FOI_AJUSTADO_FIM    BOOLEAN,
    COD_TIPO_MARCACAO   BIGINT,
    NOME_TIPO_MARCACAO  VARCHAR)
LANGUAGE PLPGSQL
AS $$
BEGIN
  RETURN QUERY
  SELECT
    F.COD_MARCACAO_INICIO                                       AS COD_MARCACAO_INICIO,
    F.COD_MARCACAO_FIM                                          AS COD_MARCACAO_FIM,
    (F.DATA_HORA_INICIO AT TIME ZONE TZ_UNIDADE(F.COD_UNIDADE)) AS DATA_HORA_INICIO,
    (F.DATA_HORA_FIM AT TIME ZONE TZ_UNIDADE(F.COD_UNIDADE))    AS DATA_HORA_FIM,
    F.STATUS_ATIVO_INICIO                                       AS STATUS_ATIVO_INICIO,
    F.STATUS_ATIVO_FIM                                          AS STATUS_ATIVO_FIM,
    F.FOI_AJUSTADO_INICIO                                       AS FOI_AJUSTADO_INICIO,
    F.FOI_AJUSTADO_FIM                                          AS FOI_AJUSTADO_FIM,
    F.COD_TIPO_INTERVALO                                        AS COD_TIPO_MARCACAO,
    IT.NOME                                                     AS NOME_TIPO_MARCACAO
  FROM FUNC_INTERVALOS_AGRUPADOS(NULL,
                                 (SELECT C.CPF
                                  FROM COLABORADOR C
                                  WHERE C.CODIGO = F_COD_COLABORADOR),
                                 F_COD_TIPO_MARCACAO) F
    JOIN INTERVALO_TIPO IT
      ON F.COD_TIPO_INTERVALO = IT.CODIGO
  WHERE ((F.DATA_HORA_INICIO AT TIME ZONE TZ_UNIDADE(F.COD_UNIDADE))::DATE = F_DIA
       OR (F.DATA_HORA_FIM AT TIME ZONE TZ_UNIDADE(F.COD_UNIDADE))::DATE = F_DIA)
      AND NOT EXISTS(SELECT MI.CODIGO FROM MARCACAO_INCONSISTENCIA MI
                      WHERE MI.COD_MARCACAO_INCONSISTENTE = F.COD_MARCACAO_FIM)
  ORDER BY COALESCE(F.DATA_HORA_INICIO, F.DATA_HORA_FIM);
END;
$$;
-- ########################################################################################################
-- ########################################################################################################

-- ########################################################################################################
-- ########################################################################################################
-- ######################### FUNCTION PARA INSERIR UMA MARCAÇÃO AVULSA DE INÍCIO OU FIM ###################
-- ########################################################################################################
-- ########################################################################################################
CREATE OR REPLACE FUNCTION FUNC_MARCACAO_INSERT_MARCACAO_AVULSA_AJUSTE(
  F_DATA_HORA_INSERIDA         TIMESTAMP WITH TIME ZONE,
  F_COD_MARCACAO_VINCULO       BIGINT,
  F_DATA_HORA_ATUAL            TIMESTAMP WITH TIME ZONE,
  F_TOKEN_RESPONSAVEL_INSERCAO TEXT)
  RETURNS BIGINT
LANGUAGE SQL
AS $$
INSERT INTO INTERVALO (COD_UNIDADE,
                       COD_TIPO_INTERVALO,
                       CPF_COLABORADOR,
                       DATA_HORA,
                       TIPO_MARCACAO,
                       FONTE_DATA_HORA,
                       FOI_AJUSTADO,
                       DATA_HORA_SINCRONIZACAO,
                       COD_COLABORADOR_INSERCAO)
  SELECT
    I.COD_UNIDADE,
    I.COD_TIPO_INTERVALO,
    I.CPF_COLABORADOR,
    F_DATA_HORA_INSERIDA,
    F_IF(I.TIPO_MARCACAO = 'MARCACAO_INICIO', 'MARCACAO_FIM'::TEXT, 'MARCACAO_INICIO'::TEXT),
    I.FONTE_DATA_HORA,
    TRUE,
    F_DATA_HORA_ATUAL,
    (SELECT C.CODIGO
     FROM COLABORADOR C
     WHERE CPF = (SELECT CPF_COLABORADOR
                  FROM TOKEN_AUTENTICACAO
                  WHERE TOKEN = F_TOKEN_RESPONSAVEL_INSERCAO))
  FROM INTERVALO I
  WHERE I.CODIGO = F_COD_MARCACAO_VINCULO
RETURNING CODIGO AS NEW_COD_MARCACAO;
$$;
-- ########################################################################################################
-- ########################################################################################################

-- ########################################################################################################
-- ########################################################################################################
-- ######################### FUNCTION PARA INSERIR MARCAÇÕES DE INÍCIO E FIM ##############################
-- ########################################################################################################
-- ########################################################################################################
CREATE OR REPLACE FUNCTION FUNC_MARCACAO_INSERT_MARCACAO_INICIO_FIM(
  F_COD_COLABORADOR_MARCACAO   BIGINT,
  F_COD_TIPO_MARCACAO          BIGINT,
  F_DATA_HORA_INICIO           TIMESTAMP WITH TIME ZONE,
  F_DATA_HORA_FIM              TIMESTAMP WITH TIME ZONE,
  F_DATA_HORA_ATUAL            TIMESTAMP WITH TIME ZONE,
  F_TOKEN_RESPONSAVEL_INSERCAO TEXT)
  RETURNS TABLE(
    COD_MARCACAO_INICIO BIGINT,
    COD_MARCACAO_FIM    BIGINT)
LANGUAGE PLPGSQL
AS $$
DECLARE
  CPF_COLABORADOR_MARCACAO         BIGINT;
  COD_UNIDADE_COLABORADOR_MARCACAO BIGINT;
  COD_COLABORADOR_INSERCAO         BIGINT := (SELECT CODIGO
                                              FROM COLABORADOR
                                              WHERE CPF = (SELECT CPF_COLABORADOR
                                                           FROM TOKEN_AUTENTICACAO
                                                           WHERE TOKEN = F_TOKEN_RESPONSAVEL_INSERCAO));
  TIPO_INICIO                      TEXT := 'MARCACAO_INICIO';
  TIPO_FIM                         TEXT := 'MARCACAO_FIM';
  FONTE_DATA_HORA_SERVIDOR         TEXT := 'SERVIDOR';
  CODIGO_INICIO                    BIGINT;
  CODIGO_FIM                       BIGINT;
BEGIN

  SELECT INTO CPF_COLABORADOR_MARCACAO, COD_UNIDADE_COLABORADOR_MARCACAO
    CPF,
    COD_UNIDADE
  FROM COLABORADOR C
  WHERE C.CODIGO = F_COD_COLABORADOR_MARCACAO;

  INSERT INTO INTERVALO (COD_UNIDADE,
                         COD_TIPO_INTERVALO,
                         CPF_COLABORADOR,
                         DATA_HORA,
                         TIPO_MARCACAO,
                         FONTE_DATA_HORA,
                         FOI_AJUSTADO,
                         DATA_HORA_SINCRONIZACAO,
                         COD_COLABORADOR_INSERCAO)
  VALUES (
    COD_UNIDADE_COLABORADOR_MARCACAO,
    F_COD_TIPO_MARCACAO,
    CPF_COLABORADOR_MARCACAO,
    F_DATA_HORA_INICIO,
    TIPO_INICIO,
    FONTE_DATA_HORA_SERVIDOR,
    TRUE,
    F_DATA_HORA_ATUAL,
    COD_COLABORADOR_INSERCAO)
  RETURNING CODIGO
    INTO CODIGO_INICIO;

  IF CODIGO_INICIO IS NULL OR CODIGO_INICIO <= 0
  THEN RAISE EXCEPTION 'Erro ao inserir marcação de início';
  END IF;

  INSERT INTO INTERVALO (COD_UNIDADE,
                         COD_TIPO_INTERVALO,
                         CPF_COLABORADOR,
                         DATA_HORA,
                         TIPO_MARCACAO,
                         FONTE_DATA_HORA,
                         FOI_AJUSTADO,
                         DATA_HORA_SINCRONIZACAO,
                         COD_COLABORADOR_INSERCAO)
  VALUES (
    COD_UNIDADE_COLABORADOR_MARCACAO,
    F_COD_TIPO_MARCACAO,
    CPF_COLABORADOR_MARCACAO,
    F_DATA_HORA_FIM,
    TIPO_FIM,
    FONTE_DATA_HORA_SERVIDOR,
    TRUE,
    F_DATA_HORA_ATUAL,
    COD_COLABORADOR_INSERCAO)
  RETURNING CODIGO
    INTO CODIGO_FIM;

  IF CODIGO_FIM IS NULL OR CODIGO_FIM <= 0
  THEN RAISE EXCEPTION 'Erro ao inserir marcação de fim';
  END IF;

  RETURN QUERY SELECT
                 CODIGO_INICIO,
                 CODIGO_FIM;
END;
$$;
-- ########################################################################################################
-- ########################################################################################################

-- ########################################################################################################
-- ########################################################################################################
-- ###################### FUNCTION PARA INSERIR UMA MARCAÇÃO DE JORNADA DO COLABORADOR  ###################
-- ########################################################################################################
-- ########################################################################################################
CREATE OR REPLACE FUNCTION FUNC_MARCACAO_INSERT_MARCACAO_JORNADA(
  F_COD_UNIDADE                      BIGINT,
  F_COD_TIPO_INTERVALO               BIGINT,
  F_CPF_COLABORADOR                  BIGINT,
  F_DATA_HORA                        TIMESTAMP WITH TIME ZONE,
  F_TIPO_MARCACAO                    TEXT,
  F_FONTE_DATA_HORA                  TEXT,
  F_JUSTIFICATIVA_TEMPO_RECOMENDADO  TEXT,
  F_JUSTIFICATIVA_ESTOURO            TEXT,
  F_LATITUDE_MARCACAO                TEXT,
  F_LONGITUDE_MARCACAO               TEXT,
  F_DATA_HORA_SINCRONIZACAO          TIMESTAMP WITH TIME ZONE,
  F_VERSAO_APP_MOMENTO_MARCACAO      INTEGER,
  F_VERSAO_APP_MOMENTO_SINCRONIZACAO INTEGER)
  RETURNS BIGINT
LANGUAGE SQL
AS $$
INSERT INTO INTERVALO(
  COD_UNIDADE,
  COD_TIPO_INTERVALO,
  CPF_COLABORADOR,
  DATA_HORA,
  TIPO_MARCACAO,
  FONTE_DATA_HORA,
  JUSTIFICATIVA_TEMPO_RECOMENDADO,
  JUSTIFICATIVA_ESTOURO,
  LATITUDE_MARCACAO,
  LONGITUDE_MARCACAO,
  DATA_HORA_SINCRONIZACAO,
  VERSAO_APP_MOMENTO_MARCACAO,
  VERSAO_APP_MOMENTO_SINCRONIZACAO)
VALUES (
  F_COD_UNIDADE,
  F_COD_TIPO_INTERVALO,
  F_CPF_COLABORADOR,
  F_DATA_HORA,
  F_TIPO_MARCACAO,
  F_FONTE_DATA_HORA,
  F_JUSTIFICATIVA_TEMPO_RECOMENDADO,
  F_JUSTIFICATIVA_ESTOURO,
  F_LATITUDE_MARCACAO,
  F_LONGITUDE_MARCACAO,
  F_DATA_HORA_SINCRONIZACAO,
  F_VERSAO_APP_MOMENTO_MARCACAO,
  F_VERSAO_APP_MOMENTO_SINCRONIZACAO)
RETURNING CODIGO;
$$;
-- #####################################################################################################################
-- #####################################################################################################################

-- #####################################################################################################################
-- #####################################################################################################################
-- ############################### FUNCTION PARA INSERIR MARCAÇÕES COM INCONSISTENCIA ##################################
-- #####################################################################################################################
-- #####################################################################################################################
CREATE OR REPLACE FUNCTION FUNC_MARCACAO_INSERT_MARCACAO_INCONSISTENCIA(
  F_COD_MARCACAO_INICIO       BIGINT,
  F_COD_MARCACAO_SINCRONIZADA BIGINT)
  RETURNS BOOLEAN
LANGUAGE PLPGSQL
AS $$
DECLARE
  COD_VINCULO_INICIO_FIM          BIGINT := (SELECT CODIGO
                                             FROM MARCACAO_VINCULO_INICIO_FIM
                                             WHERE COD_MARCACAO_INICIO = F_COD_MARCACAO_INICIO);
  COD_INCONSISTENCIA_INSERIDA     BIGINT;
  COD_VINCULO_INICIO_FIM_ALTERADA BIGINT;
BEGIN
  IF COD_VINCULO_INICIO_FIM IS NULL
  THEN RAISE EXCEPTION 'Erro ao buscar código de vínculo início/fim para mapear inconsistência';
  END IF;

  -- INSERIR INCONSISTÊNCIA
  INSERT INTO MARCACAO_INCONSISTENCIA(COD_MARCACAO_VINCULO_INICIO_FIM, COD_MARCACAO_INCONSISTENTE)
  VALUES (COD_VINCULO_INICIO_FIM, F_COD_MARCACAO_SINCRONIZADA)
  RETURNING CODIGO INTO COD_INCONSISTENCIA_INSERIDA;

  IF COD_INCONSISTENCIA_INSERIDA IS NULL
  THEN RAISE EXCEPTION 'Erro ao inserir marcação inconsistencia para a marcação: %', F_COD_MARCACAO_SINCRONIZADA;
  END IF;

  -- ALTERAR FLAG NA TABELA MARCACAO_VINCULO_INICIO_FIM - POSSUI_INCONSISTENCIA
  UPDATE MARCACAO_VINCULO_INICIO_FIM SET POSSUI_INCONSISTENCIA = TRUE
  WHERE CODIGO = COD_VINCULO_INICIO_FIM RETURNING CODIGO INTO COD_VINCULO_INICIO_FIM_ALTERADA;

  IF COD_VINCULO_INICIO_FIM_ALTERADA IS NULL
  THEN RAISE EXCEPTION
  'Erro ao setar flag possui_inconsistencia para a marcação: %', F_COD_MARCACAO_SINCRONIZADA;
  END IF;

  RETURN TRUE;
END;
$$;
-- ########################################################################################################
-- ########################################################################################################

-- ########################################################################################################
-- ########################################################################################################
-- ######################## FUNCTION PARA BUSCAR O CÓDIGO DE MATCH DE UMA MARCAÇÃO  #######################
-- ########################################################################################################
-- ########################################################################################################
CREATE OR REPLACE FUNCTION FUNC_MARCACAO_BUSCA_MARCACAO_VINCULO_BY_MARCACAO(
  F_COD_UNIDADE                     BIGINT,
  F_COD_TIPO_INTERVALO              BIGINT,
  F_CPF_COLABORADOR                 BIGINT)
  RETURNS BIGINT
LANGUAGE SQL
AS $$
WITH INTERVALOS AS (
    SELECT
      I.*,
      (CASE
       WHEN ((SELECT CODIGO FROM MARCACAO_VINCULO_INICIO_FIM MVIF
       WHERE MVIF.COD_MARCACAO_INICIO = I.CODIGO OR MVIF.COD_MARCACAO_FIM = I.CODIGO) IS NULL)
         THEN FALSE
       ELSE TRUE END) AS POSSUI_VINCULO
    FROM INTERVALO I
    WHERE I.COD_UNIDADE = F_COD_UNIDADE
          AND I.COD_TIPO_INTERVALO = F_COD_TIPO_INTERVALO
          AND I.CPF_COLABORADOR = F_CPF_COLABORADOR
          AND I.STATUS_ATIVO = TRUE
          AND I.FOI_AJUSTADO = FALSE
    ORDER BY I.DATA_HORA DESC
),

    MARCACAO AS (
      SELECT I.CODIGO,
        I.TIPO_MARCACAO,
        I.POSSUI_VINCULO FROM INTERVALOS I
      LIMIT 1
  )

SELECT (CASE
        WHEN ((SELECT M.TIPO_MARCACAO FROM MARCACAO M) = 'MARCACAO_INICIO'
              AND NOT (SELECT POSSUI_VINCULO FROM MARCACAO))
          THEN (SELECT CODIGO FROM MARCACAO)
        ELSE NULL END) AS CODIGO;
$$;
-- ########################################################################################################
-- ########################################################################################################

-- ########################################################################################################
-- ########################################################################################################
-- ##################### FUNCTION PARA BUSCAR O CÓDIGO DA MARCAÇÃO, SE ELA JÁ EXISTE  #####################
-- ########################################################################################################
-- ########################################################################################################
CREATE OR REPLACE FUNCTION FUNC_MARCACAO_BUSCA_COD_MARCACAO(
  F_COD_UNIDADE     BIGINT,
  F_CPF_COLABORADOR BIGINT,
  F_TIPO_MARCACAO   TEXT,
  F_DATA_HORA       TIMESTAMP WITH TIME ZONE)
  RETURNS BIGINT
LANGUAGE SQL
AS $$
SELECT I.CODIGO AS CODIGO
FROM INTERVALO I
WHERE I.COD_UNIDADE = F_COD_UNIDADE
      AND I.CPF_COLABORADOR = F_CPF_COLABORADOR
      AND I.DATA_HORA = F_DATA_HORA
      AND I.TIPO_MARCACAO = F_TIPO_MARCACAO;
$$;
-- ########################################################################################################
-- ########################################################################################################

-- ########################################################################################################
-- ########################################################################################################
-- #####################   RECRIA VIEW_INTERVALO COM NOVOS ATRIBUTOS DOS INTERVALOS   #####################
-- ########################################################################################################
-- ########################################################################################################
CREATE OR REPLACE VIEW VIEW_INTERVALO AS
  SELECT ROW_NUMBER() OVER (PARTITION BY I.COD_UNIDADE ORDER BY I.CODIGO) AS CODIGO_MARCACAO_POR_UNIDADE,
    I.CODIGO,
    I.COD_UNIDADE,
    I.COD_TIPO_INTERVALO,
    I.CPF_COLABORADOR,
    I.DATA_HORA,
    I.TIPO_MARCACAO,
    I.FONTE_DATA_HORA,
    I.JUSTIFICATIVA_TEMPO_RECOMENDADO,
    I.JUSTIFICATIVA_ESTOURO,
    I.LATITUDE_MARCACAO,
    I.LONGITUDE_MARCACAO,
    I.VALIDO,
    I.FOI_AJUSTADO,
    I.COD_COLABORADOR_INSERCAO,
    I.STATUS_ATIVO,
    I.DATA_HORA_SINCRONIZACAO
  FROM INTERVALO I;
-- ########################################################################################################
-- ########################################################################################################

-- ########################################################################################################
-- ########################################################################################################
-- #####################   FUNCTION PARA BUSCAR SE EXISTE UMA MARCAÇÃO EM ANDAMENTO   #####################
-- ########################################################################################################
-- ########################################################################################################
CREATE OR REPLACE FUNCTION FUNC_MARCACAO_BUSCA_MARCACAO_EM_ANDAMENTO(
  F_COD_UNIDADE                     BIGINT,
  F_COD_TIPO_INTERVALO              BIGINT,
  F_CPF_COLABORADOR                 BIGINT)
  RETURNS TABLE(
    COD_MARCACAO_POR_UNIDADE        BIGINT,
    CODIGO                          BIGINT,
    COD_UNIDADE                     BIGINT,
    COD_TIPO_INTERVALO              BIGINT,
    CPF_COLABORADOR                 BIGINT,
    DATA_NASCIMENTO_COLABORADOR     DATE,
    DATA_HORA                       TIMESTAMP WITHOUT TIME ZONE,
    TIPO_MARCACAO                   TEXT,
    FONTE_DATA_HORA                 TEXT,
    JUSTIFICATIVA_TEMPO_RECOMENDADO TEXT,
    JUSTIFICATIVA_ESTOURO           TEXT,
    LATITUDE_MARCACAO               TEXT,
    LONGITUDE_MARCACAO              TEXT,
    VALIDO                          BOOLEAN,
    FOI_EDITADO                     BOOLEAN,
    COD_COLABORADOR_INSERCAO        BIGINT,
    STATUS_ATIVO                    BOOLEAN,
    DATA_HORA_SINCRONIZACAO         TIMESTAMP WITHOUT TIME ZONE)
LANGUAGE SQL
AS $$
WITH INTERVALO_MARCACAO AS (
    SELECT
      I.CODIGO_MARCACAO_POR_UNIDADE,
      I.CODIGO,
      I.COD_UNIDADE,
      I.COD_TIPO_INTERVALO,
      I.CPF_COLABORADOR,
      C.DATA_NASCIMENTO AS DATA_NASCIMENTO_COLABORADOR,
      I.DATA_HORA AT TIME ZONE TZ_UNIDADE(I.COD_UNIDADE) AS DATA_HORA,
      I.TIPO_MARCACAO,
      I.FONTE_DATA_HORA,
      I.JUSTIFICATIVA_TEMPO_RECOMENDADO,
      I.JUSTIFICATIVA_ESTOURO,
      I.LATITUDE_MARCACAO,
      I.LONGITUDE_MARCACAO,
      I.VALIDO,
      I.FOI_AJUSTADO,
      I.COD_COLABORADOR_INSERCAO,
      I.STATUS_ATIVO,
      I.DATA_HORA_SINCRONIZACAO AT TIME ZONE TZ_UNIDADE(I.COD_UNIDADE) AS DATA_HORA_SINCRONIZACAO
    FROM VIEW_INTERVALO I
      JOIN COLABORADOR C
        ON I.CPF_COLABORADOR = C.CPF
    WHERE I.COD_UNIDADE = F_COD_UNIDADE
          AND I.COD_TIPO_INTERVALO = F_COD_TIPO_INTERVALO
          AND I.CPF_COLABORADOR = F_CPF_COLABORADOR
    ORDER BY I.CODIGO DESC
    LIMIT 1
)

SELECT * FROM INTERVALO_MARCACAO IM
WHERE NOT EXISTS(SELECT MVIF.COD_MARCACAO_INICIO
                 FROM MARCACAO_VINCULO_INICIO_FIM MVIF
                 WHERE MVIF.COD_MARCACAO_INICIO = IM.CODIGO)
      AND IM.TIPO_MARCACAO = 'MARCACAO_INICIO'
      AND IM.VALIDO = TRUE
      AND IM.STATUS_ATIVO = TRUE;
$$;
-- ########################################################################################################
-- ########################################################################################################

-- ########################################################################################################
-- ########################################################################################################
-- #####################   FUNCTION PARA BUSCAR O HISTÓRICO DE AJUSTES DAS MARCAÇÕES   ####################
-- ########################################################################################################
-- ########################################################################################################
CREATE OR REPLACE FUNCTION FUNC_MARCACAO_GET_HISTORICO_AJUSTES(F_COD_MARCACOES BIGINT [])
  RETURNS TABLE(
    COD_MARCACAO            BIGINT,
    NOME_RESPONSAVEL_AJUSTE TEXT,
    DATA_HORA_AJUSTE        TIMESTAMP WITHOUT TIME ZONE,
    JUSTIFICATIVA_AJUSTE    TEXT,
    OBSERVACAO_AJUSTE       TEXT,
    ACAO_AJUSTE             TEXT,
    TIPO_MARCACAO           TEXT,
    DATA_HORA_ANTIGA        TIMESTAMP WITHOUT TIME ZONE,
    DATA_HORA_NOVA          TIMESTAMP WITHOUT TIME ZONE
  )
LANGUAGE SQL
AS $$
SELECT
  MH.COD_MARCACAO                                                           AS COD_MARCACAO,
  C.NOME                                                                    AS NOME_RESPONSAVEL_AJUSTE,
  MA.DATA_HORA_AJUSTE AT TIME ZONE (TZ_UNIDADE(MA.COD_UNIDADE_AJUSTE))      AS DATA_HORA_AJUSTE,
  MJA.NOME                                                                  AS JUSTIFICATIVA_AJUSTE,
  MA.OBSERVACAO_AJUSTE                                                      AS OBSERVACAO_AJUSTE,
  MA.ACAO_AJUSTE                                                            AS ACAO_AJUSTE,
  I.TIPO_MARCACAO                                                           AS TIPO_MARCACAO,
  MH.DATA_HORA_ANTIGA AT TIME ZONE (TZ_UNIDADE(I.COD_UNIDADE))              AS DATA_HORA_ANTIGA,
  -- Esse select interno provavelmente atrasa a query, mas é uma solução muito simples.
  LEAD(MH.DATA_HORA_ANTIGA, 1, (SELECT DATA_HORA
                                FROM INTERVALO
                                WHERE CODIGO = MH.COD_MARCACAO))
  OVER (
    PARTITION BY MH.COD_MARCACAO ) AT TIME ZONE (TZ_UNIDADE(I.COD_UNIDADE)) AS DATA_HORA_NOVA
FROM MARCACAO_HISTORICO MH
  JOIN MARCACAO_AJUSTE MA ON MH.COD_AJUSTE = MA.CODIGO
  JOIN MARCACAO_JUSTIFICATIVA_AJUSTE MJA ON MA.COD_JUSTIFICATIVA_AJUSTE = MJA.CODIGO
  JOIN INTERVALO I ON MH.COD_MARCACAO = I.CODIGO
  JOIN COLABORADOR C ON MA.COD_COLABORADOR_AJUSTE = C.CODIGO
WHERE MH.COD_MARCACAO = ANY (F_COD_MARCACOES)
ORDER BY MA.DATA_HORA_AJUSTE ASC;
$$;
-- ########################################################################################################
-- ########################################################################################################

-- ########################################################################################################
-- ########################################################################################################
-- #####################   FUNCTION PARA BUSCAR AS INCONSISTENCIAS DO TIPO SEM VÍNCULO   ##################
-- ########################################################################################################
-- ########################################################################################################
CREATE OR REPLACE FUNCTION FUNC_MARCACAO_GET_INCONSISTENCIAS_TIPO_SEM_VINCULO(
  F_COD_COLABORADOR BIGINT,
  F_DIA             DATE)
  RETURNS TABLE(
    COD_MARCACAO_SEM_VINCULO BIGINT,
    TIPO_INICIO_FIM          TEXT,
    NOME_COLABORADOR         TEXT,
    DATA_HORA_MARCACAO       TIMESTAMP WITHOUT TIME ZONE
  )
LANGUAGE PLPGSQL
AS $$
DECLARE
  CPF_COLABORADOR_FILTRO BIGINT := (SELECT C.CPF
                                    FROM COLABORADOR C
                                    WHERE C.CODIGO = F_COD_COLABORADOR);
  TIPO_MARCACAO_INICIO   TEXT := 'MARCACAO_INICIO';
  TIPO_MARCACAO_FIM      TEXT := 'MARCACAO_FIM';
BEGIN
  RETURN QUERY
  WITH MARCACOES_DIA AS (
      SELECT
        I.CODIGO        AS COD_MARCACAO,
        I.TIPO_MARCACAO AS TIPO_MARCACAO,
        I.DATA_HORA     AS DATA_HORA_MARCACAO,
        I.COD_UNIDADE   AS COD_UNIDADE_MARCACAO,
        C.NOME          AS NOME_COLABORADOR
      FROM INTERVALO I
        JOIN COLABORADOR C ON I.CPF_COLABORADOR = C.CPF
      WHERE
        (I.DATA_HORA AT TIME ZONE TZ_UNIDADE(I.COD_UNIDADE)) :: DATE = F_DIA
        AND I.CPF_COLABORADOR = CPF_COLABORADOR_FILTRO
        AND I.STATUS_ATIVO = TRUE
  ),

      INICIOS AS (
        SELECT *
        FROM MARCACOES_DIA MD
        WHERE MD.TIPO_MARCACAO = TIPO_MARCACAO_INICIO
    ),

      FINS AS (
        SELECT *
        FROM MARCACOES_DIA MD
        WHERE MD.TIPO_MARCACAO = TIPO_MARCACAO_FIM
    )

  SELECT
    I.COD_MARCACAO                                                         AS COD_MARCACAO_SEM_VINCULO,
    I.TIPO_MARCACAO :: TEXT                                                AS TIPO_INICIO_FIM,
    I.NOME_COLABORADOR :: TEXT                                             AS NOME_COLABORADOR,
    (I.DATA_HORA_MARCACAO AT TIME ZONE TZ_UNIDADE(I.COD_UNIDADE_MARCACAO)) AS DATA_HORA_MARCACAO
  FROM INICIOS I
    LEFT JOIN MARCACAO_VINCULO_INICIO_FIM MV
      ON I.COD_MARCACAO = MV.COD_MARCACAO_INICIO
  WHERE MV.CODIGO IS NULL
  UNION ALL
  SELECT
    F.COD_MARCACAO                                                         AS COD_MARCACAO_SEM_VINCULO,
    F.TIPO_MARCACAO :: TEXT                                                AS TIPO_INICIO_FIM,
    F.NOME_COLABORADOR :: TEXT                                             AS NOME_COLABORADOR,
    (F.DATA_HORA_MARCACAO AT TIME ZONE TZ_UNIDADE(F.COD_UNIDADE_MARCACAO)) AS DATA_HORA_MARCACAO
  FROM FINS F
    LEFT JOIN MARCACAO_VINCULO_INICIO_FIM MV
      ON F.COD_MARCACAO = MV.COD_MARCACAO_FIM
  WHERE MV.CODIGO IS NULL;
END;
$$;
-- ########################################################################################################
-- ########################################################################################################

-- ########################################################################################################
-- ########################################################################################################
-- #####################   FUNCTION PARA BUSCAR AS INCONSISTENCIAS DO TIPO FIM ANTES INÍCIO   #############
-- ########################################################################################################
-- ########################################################################################################
CREATE OR REPLACE FUNCTION FUNC_MARCACAO_GET_INCONSISTENCIAS_TIPO_FIM_ANTES_INICIO(
  F_COD_COLABORADOR BIGINT,
  F_DIA             DATE)
  RETURNS TABLE(
    COD_MARCACAO_INICIO       BIGINT,
    DATA_HORA_MARCACAO_INICIO TIMESTAMP WITHOUT TIME ZONE,
    COD_MARCACAO_FIM          BIGINT,
    DATA_HORA_MARCACAO_FIM    TIMESTAMP WITHOUT TIME ZONE,
    NOME_COLABORADOR          TEXT
  )
LANGUAGE PLPGSQL
AS $$
DECLARE
  CPF_COLABORADOR_FILTRO BIGINT := (SELECT C.CPF
                                    FROM COLABORADOR C
                                    WHERE C.CODIGO = F_COD_COLABORADOR);
BEGIN
  RETURN QUERY
  SELECT
    F.COD_MARCACAO_INICIO                                     AS COD_MARCACAO_INICIO,
    F.DATA_HORA_INICIO AT TIME ZONE TZ_UNIDADE(F.COD_UNIDADE) AS DATA_HORA_INICIO,
    F.COD_MARCACAO_FIM                                        AS COD_MARCACAO_FIM,
    F.DATA_HORA_FIM AT TIME ZONE TZ_UNIDADE(F.COD_UNIDADE)    AS DATA_HORA_FIM,
    C.NOME :: TEXT                                            AS NOME_COLABORADOR
  FROM FUNC_INTERVALOS_AGRUPADOS(NULL, CPF_COLABORADOR_FILTRO, NULL) F
    JOIN COLABORADOR C ON F.CPF_COLABORADOR = C.CPF
  WHERE F.DATA_HORA_FIM < F.DATA_HORA_INICIO
        AND F.DATA_HORA_INICIO IS NOT NULL
        AND F.DATA_HORA_FIM IS NOT NULL
        AND ((DATA_HORA_INICIO AT TIME ZONE TZ_UNIDADE(F.COD_UNIDADE)) :: DATE = F_DIA
             OR (DATA_HORA_FIM AT TIME ZONE TZ_UNIDADE(F.COD_UNIDADE)) :: DATE = F_DIA);
END;
$$;
-- ########################################################################################################
-- ########################################################################################################


-- ########################################################################################################
-- ########################################################################################################
-- #####################   DEFINE PERMISSÃO UTILIZADA PARA A PARTE DE GESTÃO DE MARCAÇÕES   ###############
-- ########################################################################################################
-- ########################################################################################################
-- 1 - Renomear Controle de Jornada - Editar Marcações para Controle de Jornada - Ajuste de Marcações (criar, editar, inativar)
UPDATE FUNCAO_PROLOG_V11 SET FUNCAO = 'Controle de Jornada - Ajuste de Marcações (criar, editar, inativar)' WHERE CODIGO = 338;

-- 2 - Conceder a permissão renomeada para qualquer cargo que possua Controle de Jornada - Invalidar marcações
WITH CARGOS_INATIVAR AS (
    SELECT *
    FROM CARGO_FUNCAO_PROLOG_V11 CFP
    WHERE CFP.COD_FUNCAO_PROLOG = 339
)

INSERT INTO CARGO_FUNCAO_PROLOG_V11 (
  COD_UNIDADE,
  COD_FUNCAO_COLABORADOR,
  COD_FUNCAO_PROLOG,
  COD_PILAR_PROLOG)
  SELECT
    CI.COD_UNIDADE,
    CI.COD_FUNCAO_COLABORADOR,
    338,
    CI.COD_PILAR_PROLOG
  FROM CARGOS_INATIVAR CI
  WHERE CI.COD_FUNCAO_COLABORADOR NOT IN (SELECT CFP.COD_FUNCAO_COLABORADOR
                                          FROM CARGO_FUNCAO_PROLOG_V11 CFP
                                          WHERE CFP.COD_FUNCAO_PROLOG = 338
                                                AND CI.COD_UNIDADE = CFP.COD_UNIDADE)
  ORDER BY CI.COD_FUNCAO_COLABORADOR, CI.COD_UNIDADE;

-- 3 - Deslogar do sistema todos os usuários que possuam a permissão Controle de Jornada - Invalidar marcações
WITH CARGOS_FUNCAO_DELETAR AS (
    SELECT CFP.COD_FUNCAO_COLABORADOR
    FROM CARGO_FUNCAO_PROLOG_V11 CFP
    WHERE CFP.COD_FUNCAO_PROLOG = 339
),
    CPFS_DELETAR AS (
      SELECT
        C.CPF
      FROM COLABORADOR C
      WHERE C.COD_FUNCAO IN (SELECT CFD.COD_FUNCAO_COLABORADOR
                             FROM CARGOS_FUNCAO_DELETAR CFD)
  )

SELECT
  *
FROM CPFS_DELETAR CD
  JOIN TOKEN_AUTENTICACAO TA
    ON TA.CPF_COLABORADOR = CD.CPF;

-- 4 - Desvincular de todos os cargos as permissões Controle de Jornada - Invalidar marcações
DELETE FROM CARGO_FUNCAO_PROLOG_V11 WHERE COD_FUNCAO_PROLOG = 339;

-- 5 - Remover a permissão Controle de Jornada - Invalidar marcações do banco, do servidor e do android
DELETE FROM FUNCAO_PROLOG_V11 WHERE CODIGO = 339;
DELETE FROM FUNCAO_PROLOG WHERE CODIGO = 339;
-- ########################################################################################################
-- ########################################################################################################


--######################################################################################################################
--######################################################################################################################
--############################ ADICIONA DATA/HORA SINCRONIA AO RELATÓRIO ###############################################
--######################################################################################################################
--######################################################################################################################
 DROP FUNCTION FUNC_MARCACAO_RELATORIO_MARCACOES_DIARIAS(
   F_COD_UNIDADE  BIGINT,
   F_DATA_INICIAL DATE,
   F_DATA_FINAL   DATE,
   F_CPF          TEXT);


 CREATE OR REPLACE FUNCTION FUNC_MARCACAO_RELATORIO_MARCACOES_DIARIAS(
   F_COD_UNIDADE  BIGINT,
   F_DATA_INICIAL DATE,
   F_DATA_FINAL   DATE,
   F_CPF          TEXT)
   RETURNS TABLE(
     "NOME"                                         TEXT,
     "MATRÍCULA TRANSPORTADORA"                     TEXT,
     "MATRÍCULA AMBEV"                              TEXT,
     "CARGO"                                        TEXT,
     "SETOR"                                        TEXT,
     "INTERVALO"                                    TEXT,
     "INICIO INTERVALO"                             TEXT,
     "LATITUDE INÍCIO"                              TEXT,
     "LONGITUDE INÍCIO"                             TEXT,
     "FONTE DATA/HORA INÍCIO"                       TEXT,
     "DATA/HORA SINCRONIZAÇÃO INÍCIO"               TEXT,
     "FIM INTERVALO"                                TEXT,
     "LATITUDE FIM"                                 TEXT,
     "LONGITUDE FIM"                                TEXT,
     "FONTE DATA/HORA FIM"                          TEXT,
     "DATA/HORA SINCRONIZAÇÃO FIM"                  TEXT,
     "TEMPO DECORRIDO (MINUTOS)"                    TEXT,
     "TEMPO RECOMENDADO (MINUTOS)"                  BIGINT,
     "CUMPRIU TEMPO MÍNIMO"                         TEXT,
     "JUSTIFICATIVA NÃO CUMPRIMENTO TEMPO MÍNIMO"   TEXT,
     "JUSTIFICATIVA ESTOURO TEMPO MÁXIMO PERMITIDO" TEXT,
     "DISTANCIA ENTRE INÍCIO E FIM (METROS)"        TEXT)
 LANGUAGE SQL
 AS $$
 SELECT
   C.NOME                                   AS NOME_COLABORADOR,
   COALESCE(C.MATRICULA_TRANS :: TEXT, '-') AS MATRICULA_TRANS,
   COALESCE(C.MATRICULA_AMBEV :: TEXT, '-') AS MATRICULA_AMBEV,
   F.NOME                                   AS CARGO,
   S.NOME                                   AS SETOR,
   IT.NOME                                  AS INTERVALO,
   COALESCE(TO_CHAR(I.DATA_HORA_INICIO AT TIME ZONE (SELECT TIMEZONE
                                                     FROM FUNC_GET_TIME_ZONE_UNIDADE(F_COD_UNIDADE)),
                    'DD/MM/YYYY HH24:MI:SS'),
            '')                             AS DATA_HORA_INICIO,

   I.LATITUDE_MARCACAO_INICIO :: TEXT       AS LATITUDE_INICIO,
   I.LONGITUDE_MARCACAO_INICIO :: TEXT      AS LONGITUDE_INICIO,
   I.FONTE_DATA_HORA_INICIO,
   COALESCE (TO_CHAR (I.DATA_HORA_SINCRONIZACAO_INICIO AT TIME ZONE (SELECT TIMEZONE
                                                                     FROM FUNC_GET_TIME_ZONE_UNIDADE(F_COD_UNIDADE)),
                   'DD/MM/YYYY HH24:MI:SS'),
           ''),

   COALESCE(TO_CHAR(I.DATA_HORA_FIM AT TIME ZONE (SELECT TIMEZONE
                                                  FROM FUNC_GET_TIME_ZONE_UNIDADE(F_COD_UNIDADE)),
                    'DD/MM/YYYY HH24:MI:SS'),
            '')                             AS DATA_HORA_FIM,

   I.LATITUDE_MARCACAO_FIM :: TEXT          AS LATITUDE_FIM,
   I.LONGITUDE_MARCACAO_FIM :: TEXT         AS LONGITUDE_FIM,
   I.FONTE_DATA_HORA_FIM,
   COALESCE(TO_CHAR(I.DATA_HORA_SINCRONIZACAO_FIM AT TIME ZONE (SELECT TIMEZONE
                                                     FROM FUNC_GET_TIME_ZONE_UNIDADE(F_COD_UNIDADE)),
                    'DD/MM/YYYY HH24:MI:SS'),
            ''),

   COALESCE(TRUNC(EXTRACT(EPOCH FROM I.DATA_HORA_FIM - I.DATA_HORA_INICIO) / 60) :: TEXT,
            '')                             AS TEMPO_DECORRIDO_MINUTOS,
   IT.TEMPO_RECOMENDADO_MINUTOS,
   CASE WHEN I.DATA_HORA_FIM IS NULL OR I.DATA_HORA_INICIO IS NULL
     THEN ''
   WHEN IT.TEMPO_RECOMENDADO_MINUTOS > (EXTRACT(EPOCH FROM I.DATA_HORA_FIM - I.DATA_HORA_INICIO) / 60)
     THEN
       'NÃO'
   ELSE 'SIM' END                           AS CUMPRIU_TEMPO_MINIMO,
   I.JUSTIFICATIVA_TEMPO_RECOMENDADO,
   I.JUSTIFICATIVA_ESTOURO,

   COALESCE(TRUNC((ST_DISTANCE(
       ST_POINT(I.LONGITUDE_MARCACAO_INICIO :: FLOAT, I.LATITUDE_MARCACAO_INICIO :: FLOAT) :: GEOGRAPHY,
       ST_POINT(I.LONGITUDE_MARCACAO_FIM :: FLOAT, I.LATITUDE_MARCACAO_FIM :: FLOAT) :: GEOGRAPHY))) :: TEXT,
            '-')                            AS DISTANCIA
 FROM
       FUNC_INTERVALOS_AGRUPADOS(F_COD_UNIDADE, CASE WHEN F_CPF = '%'
         THEN NULL
                                                ELSE F_CPF :: BIGINT END, NULL) I
   JOIN COLABORADOR C ON C.CPF = I.CPF_COLABORADOR
   JOIN INTERVALO_TIPO IT ON IT.COD_UNIDADE = I.COD_UNIDADE AND IT.CODIGO = I.COD_TIPO_INTERVALO
   JOIN UNIDADE U ON U.CODIGO = C.COD_UNIDADE AND C.COD_EMPRESA = U.COD_EMPRESA
   JOIN FUNCAO F ON F.COD_EMPRESA = U.COD_EMPRESA AND F.CODIGO = C.COD_FUNCAO
   JOIN SETOR S ON S.COD_UNIDADE = C.COD_UNIDADE AND S.CODIGO = C.COD_SETOR
 WHERE ((I.DATA_HORA_INICIO AT TIME ZONE (SELECT TIMEZONE
                                          FROM FUNC_GET_TIME_ZONE_UNIDADE(F_COD_UNIDADE))) :: DATE
        BETWEEN (F_DATA_INICIAL AT TIME ZONE (SELECT TIMEZONE
                                              FROM FUNC_GET_TIME_ZONE_UNIDADE(F_COD_UNIDADE)))
        AND (F_DATA_FINAL AT TIME ZONE (SELECT TIMEZONE
                                        FROM FUNC_GET_TIME_ZONE_UNIDADE(F_COD_UNIDADE)))
        OR (I.DATA_HORA_FIM AT TIME ZONE (SELECT TIMEZONE
                                          FROM FUNC_GET_TIME_ZONE_UNIDADE(F_COD_UNIDADE))) :: DATE
        BETWEEN (F_DATA_INICIAL AT TIME ZONE (SELECT TIMEZONE
                                              FROM FUNC_GET_TIME_ZONE_UNIDADE(F_COD_UNIDADE)))
        AND (F_DATA_FINAL AT TIME ZONE (SELECT TIMEZONE
                                        FROM FUNC_GET_TIME_ZONE_UNIDADE(F_COD_UNIDADE))))
 ORDER BY I.DATA_HORA_INICIO, C.NOME
 $$;
--######################################################################################################################
--######################################################################################################################

-- #####################################################################################################################
-- #####################################################################################################################
-- ###############################  CRIAÇÃO DE TOKEN PARA A SINCRONIZAÇÃO DE MARCAÇÕES  ################################
-- #####################################################################################################################
-- #####################################################################################################################

-- CRIA COLUNA PARA SALVAR O TOKEN DE SINCRONIZAÇÃO DE MARCAÇÃO DE JORNADA DAS UNIDADES
ALTER TABLE INTERVALO_UNIDADE ADD COLUMN TOKEN_SINCRONIZACAO_MARCACAO VARCHAR(255);
ALTER TABLE INTERVALO_UNIDADE ADD CONSTRAINT UNIQUE_TOKEN_SINCRONIZACAO_MARCACAO UNIQUE(TOKEN_SINCRONIZACAO_MARCACAO);
--######################################################################################################################
--######################################################################################################################

-- PROCESSO DE CRIAÇÃO DE TOKENS PARA AS UNIDADES JÁ PRESENTES NA TABELA
UPDATE INTERVALO_UNIDADE SET TOKEN_SINCRONIZACAO_MARCACAO = 'GJE55LRKFEQ3F52G44TJ8H9U2S' WHERE COD_UNIDADE =	1;
UPDATE INTERVALO_UNIDADE SET TOKEN_SINCRONIZACAO_MARCACAO = '6LEK80ODEUUD0L5EG0TGCB7GGN' WHERE COD_UNIDADE =	2;
UPDATE INTERVALO_UNIDADE SET TOKEN_SINCRONIZACAO_MARCACAO = 'R5RAUREHKKRIF5B20LC0SKURS' WHERE COD_UNIDADE =	3;
UPDATE INTERVALO_UNIDADE SET TOKEN_SINCRONIZACAO_MARCACAO = 'ULHLTI34CJKA0CEPSREM4FVH3R' WHERE COD_UNIDADE =	4;
UPDATE INTERVALO_UNIDADE SET TOKEN_SINCRONIZACAO_MARCACAO = 'R43VIDK2G3CJ9DU68454G8SG0A' WHERE COD_UNIDADE =	5;
UPDATE INTERVALO_UNIDADE SET TOKEN_SINCRONIZACAO_MARCACAO = 'G2VPJJ751KAT7KJONJ7BF12DNI' WHERE COD_UNIDADE =	6;
UPDATE INTERVALO_UNIDADE SET TOKEN_SINCRONIZACAO_MARCACAO = 'N1PB5HP9G2310F9SCAF9RPTSR3' WHERE COD_UNIDADE =	7;
UPDATE INTERVALO_UNIDADE SET TOKEN_SINCRONIZACAO_MARCACAO = '7PKH73UGOLCE380N797N9BA34D' WHERE COD_UNIDADE =	9;
UPDATE INTERVALO_UNIDADE SET TOKEN_SINCRONIZACAO_MARCACAO = 'V0HGCSHJHMVJJMOQJ142T8K787' WHERE COD_UNIDADE =	10;
UPDATE INTERVALO_UNIDADE SET TOKEN_SINCRONIZACAO_MARCACAO = 'HCCH895UJK14F2588SJ9PD3P45' WHERE COD_UNIDADE =	11;
UPDATE INTERVALO_UNIDADE SET TOKEN_SINCRONIZACAO_MARCACAO = 'RP6E8QL0UAVSJAKFPPL4L5AQ34' WHERE COD_UNIDADE =	15;
UPDATE INTERVALO_UNIDADE SET TOKEN_SINCRONIZACAO_MARCACAO = 'VKKD76UJE4QM7E65RVL45RAQ64' WHERE COD_UNIDADE =	16;
UPDATE INTERVALO_UNIDADE SET TOKEN_SINCRONIZACAO_MARCACAO = 'V5120BJF7A415C9QCK41DFGJG5' WHERE COD_UNIDADE =	17;
UPDATE INTERVALO_UNIDADE SET TOKEN_SINCRONIZACAO_MARCACAO = 'JBBTJ4O5KTLNBKGGCJ5EESUA7I' WHERE COD_UNIDADE =	19;
UPDATE INTERVALO_UNIDADE SET TOKEN_SINCRONIZACAO_MARCACAO = 'MKEIF7CH3VH6MJTJKU6HMS0BFC' WHERE COD_UNIDADE =	21;
UPDATE INTERVALO_UNIDADE SET TOKEN_SINCRONIZACAO_MARCACAO = '46RQLA16NV3MQPOSAK5V9Q3TN2' WHERE COD_UNIDADE =	26;
UPDATE INTERVALO_UNIDADE SET TOKEN_SINCRONIZACAO_MARCACAO = 'QL5059DLAL45S63PMA66IHJM7T' WHERE COD_UNIDADE =	29;
UPDATE INTERVALO_UNIDADE SET TOKEN_SINCRONIZACAO_MARCACAO = 'PKAKVKT7IHPKL1DDMGJB1HTADU' WHERE COD_UNIDADE =	33;
UPDATE INTERVALO_UNIDADE SET TOKEN_SINCRONIZACAO_MARCACAO = 'M0L3LIA966MIC1JBQ9T9NDD7N5' WHERE COD_UNIDADE =	38;
UPDATE INTERVALO_UNIDADE SET TOKEN_SINCRONIZACAO_MARCACAO = 'AJ3V86IM38T7KLR9LE1V0DRG45' WHERE COD_UNIDADE =	39;
UPDATE INTERVALO_UNIDADE SET TOKEN_SINCRONIZACAO_MARCACAO = 'KRSVAD7EK9J7QLGFLF0GL5RV4B' WHERE COD_UNIDADE =	43;
UPDATE INTERVALO_UNIDADE SET TOKEN_SINCRONIZACAO_MARCACAO = 'TRN6GFMRQB4SSEC9MFOIN4N3V1' WHERE COD_UNIDADE =	44;
UPDATE INTERVALO_UNIDADE SET TOKEN_SINCRONIZACAO_MARCACAO = 'JL9K6C78UCGNJSTHSJ7GA7F3B4' WHERE COD_UNIDADE =	45;
UPDATE INTERVALO_UNIDADE SET TOKEN_SINCRONIZACAO_MARCACAO = '66FMUHQ5DNROLVB0I8L19H4VPS' WHERE COD_UNIDADE =	46;
UPDATE INTERVALO_UNIDADE SET TOKEN_SINCRONIZACAO_MARCACAO = 'PANHT28I5DMBAJ3EFSCJQ0PU8A' WHERE COD_UNIDADE =	48;
UPDATE INTERVALO_UNIDADE SET TOKEN_SINCRONIZACAO_MARCACAO = 'OC9ECG6J8DL1UOP3DNVOBCSE2O' WHERE COD_UNIDADE =	52;
UPDATE INTERVALO_UNIDADE SET TOKEN_SINCRONIZACAO_MARCACAO = 'IGDO59K4OQQKI4LGDG7BSV1HP8' WHERE COD_UNIDADE =	53;
UPDATE INTERVALO_UNIDADE SET TOKEN_SINCRONIZACAO_MARCACAO = 'JLC7NMM6HUQUM47QSFLDTEKBND' WHERE COD_UNIDADE =	54;
UPDATE INTERVALO_UNIDADE SET TOKEN_SINCRONIZACAO_MARCACAO = 'SRVTS1OOTSLNR2CFLCP4F7F3UI' WHERE COD_UNIDADE =	56;
UPDATE INTERVALO_UNIDADE SET TOKEN_SINCRONIZACAO_MARCACAO = '5CMP1QTPNR5VUOC7E01D2Q5HT6' WHERE COD_UNIDADE =	62;
UPDATE INTERVALO_UNIDADE SET TOKEN_SINCRONIZACAO_MARCACAO = 'FDSOPDRU3BGEDRHRATPB2KT6DB' WHERE COD_UNIDADE =	63;
UPDATE INTERVALO_UNIDADE SET TOKEN_SINCRONIZACAO_MARCACAO = 'UHMVH2P9LR1K56IP2U8E3DLVNE' WHERE COD_UNIDADE =	65;
UPDATE INTERVALO_UNIDADE SET TOKEN_SINCRONIZACAO_MARCACAO = '3A62JIEEQ96F57G2KU6TSIKHRR' WHERE COD_UNIDADE =	67;
UPDATE INTERVALO_UNIDADE SET TOKEN_SINCRONIZACAO_MARCACAO = 'GE2IND2IDVS6DTGLNHIMRI5DGO' WHERE COD_UNIDADE =	68;
UPDATE INTERVALO_UNIDADE SET TOKEN_SINCRONIZACAO_MARCACAO = 'O2C36IDF606VNO2OJ058RR8JS' WHERE COD_UNIDADE =	69;
UPDATE INTERVALO_UNIDADE SET TOKEN_SINCRONIZACAO_MARCACAO = '2NSUO1INJP8FQ5K1FC9PAQ76EG' WHERE COD_UNIDADE =	70;
UPDATE INTERVALO_UNIDADE SET TOKEN_SINCRONIZACAO_MARCACAO = '56L27MU58OI0ERRRLR18RC4D3N' WHERE COD_UNIDADE =	71;
UPDATE INTERVALO_UNIDADE SET TOKEN_SINCRONIZACAO_MARCACAO = 'FPB1QE6PQ831D945M5DO50OLFO' WHERE COD_UNIDADE =	72;
UPDATE INTERVALO_UNIDADE SET TOKEN_SINCRONIZACAO_MARCACAO = 'JJG8OOQ2GE6Q61TQ1EL44QQPPN' WHERE COD_UNIDADE =	73;
UPDATE INTERVALO_UNIDADE SET TOKEN_SINCRONIZACAO_MARCACAO = 'BR8L05PIQNFD4VI4PHKRKK2D2' WHERE COD_UNIDADE =	75;
UPDATE INTERVALO_UNIDADE SET TOKEN_SINCRONIZACAO_MARCACAO = 'OA9V6S9L1INSMU2M3DMMBUTBQF' WHERE COD_UNIDADE =	76;
UPDATE INTERVALO_UNIDADE SET TOKEN_SINCRONIZACAO_MARCACAO = 'SF89PJRSPV6V09NCIC1B3HU5HK' WHERE COD_UNIDADE =	78;
UPDATE INTERVALO_UNIDADE SET TOKEN_SINCRONIZACAO_MARCACAO = 'GTMSNT4R56PBLFEMJ81QJN0TNB' WHERE COD_UNIDADE =	79;
UPDATE INTERVALO_UNIDADE SET TOKEN_SINCRONIZACAO_MARCACAO = '4I6FJURP6RNOSO9H6D2OJVKQ6P' WHERE COD_UNIDADE =	80;
UPDATE INTERVALO_UNIDADE SET TOKEN_SINCRONIZACAO_MARCACAO = '8NEBGNV2RE0OVBLRBE2UI7JJUT' WHERE COD_UNIDADE =	81;
UPDATE INTERVALO_UNIDADE SET TOKEN_SINCRONIZACAO_MARCACAO = 'G5F7O2GFVO5FP34655U0MEMTHD' WHERE COD_UNIDADE =	86;
UPDATE INTERVALO_UNIDADE SET TOKEN_SINCRONIZACAO_MARCACAO = 'JBBCE6T4H3NQ1IKSI77SD2PO90' WHERE COD_UNIDADE =	87;
UPDATE INTERVALO_UNIDADE SET TOKEN_SINCRONIZACAO_MARCACAO = '8HLTBL6ABB0ROB82NHHAD1LUNA' WHERE COD_UNIDADE =	88;
UPDATE INTERVALO_UNIDADE SET TOKEN_SINCRONIZACAO_MARCACAO = '3B6FOP9QIE5729EK7FMQRV92N8' WHERE COD_UNIDADE =	89;
UPDATE INTERVALO_UNIDADE SET TOKEN_SINCRONIZACAO_MARCACAO = 'JNO23P73C2DS1I9TDRLSR2S62F' WHERE COD_UNIDADE =	90;
UPDATE INTERVALO_UNIDADE SET TOKEN_SINCRONIZACAO_MARCACAO = 'HAUMU35T9GTO7MFTC6THKU3T4O' WHERE COD_UNIDADE =	91;
UPDATE INTERVALO_UNIDADE SET TOKEN_SINCRONIZACAO_MARCACAO = 'LH7VPCU2UVH8HB60185PFOTDJI' WHERE COD_UNIDADE =	93;
UPDATE INTERVALO_UNIDADE SET TOKEN_SINCRONIZACAO_MARCACAO = 'UJI5O10NHVA2OOGOB8G5GF0NGB' WHERE COD_UNIDADE =	94;
UPDATE INTERVALO_UNIDADE SET TOKEN_SINCRONIZACAO_MARCACAO = 'UC7CPI3V64D8UKKTJJ0ECVIN1S' WHERE COD_UNIDADE =	97;
UPDATE INTERVALO_UNIDADE SET TOKEN_SINCRONIZACAO_MARCACAO = 'CNVH7NE0J2CM308EEO41UREDCV' WHERE COD_UNIDADE =	99;
--######################################################################################################################
--######################################################################################################################

-- CRIA CONSTRAINT NOT NULL PARA O TOKEN
ALTER TABLE INTERVALO_UNIDADE ALTER COLUMN TOKEN_SINCRONIZACAO_MARCACAO SET NOT NULL;
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--###########################################  FIM MIGRAÇÃO CONTROLE JORNADA  ##########################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################



--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--###########################################  INÍCIO MIGRAÇÃO CHECK OS  ###############################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
---
---
--- DROPA DEPENDÊNCIAS COM A COLUNA PRIORIDADE DE CHECKLIST_PERGUNTAS.
---
---
DROP VIEW ESTRATIFICACAO_OS;

ALTER TABLE PRIORIDADE_PERGUNTA_CHECKLIST
  RENAME TO CHECKLIST_ALTERNATIVA_PRIORIDADE;
---
---
--- FIM!
---
---


---
---
--- MOVE PRIORIDADE DA PERGUNTA PARA A ALTERNATIVA.
---
---
-- PRIORIDADE PODE SER: CRITICA, ALTA OU BAIXA.
-- ADICIONA COLUNA PRIORIDADE NA TABELA DE ALTERNATIVAS.
ALTER TABLE CHECKLIST_ALTERNATIVA_PERGUNTA
  ADD COLUMN PRIORIDADE VARCHAR(7);

ALTER TABLE CHECKLIST_ALTERNATIVA_PERGUNTA
  ADD CONSTRAINT FK_CHECKLIST_ALTERNATIVA_PRIORIDADE
FOREIGN KEY (PRIORIDADE) REFERENCES CHECKLIST_ALTERNATIVA_PRIORIDADE (PRIORIDADE);

COMMENT ON COLUMN CHECKLIST_ALTERNATIVA_PERGUNTA.PRIORIDADE
IS
  'Salva a prioridade que essa alternativa possui, podendo ser: CRITICA, ALTA ou BAIXA';

-- SETA A PRIORIDADE DE CADA ALTERNATIVA, COMO A PRIORIDADE QUE A PERGUNTA QUE ELA COMPÕE POSSUI.
UPDATE CHECKLIST_ALTERNATIVA_PERGUNTA CAP
SET PRIORIDADE = (SELECT CP.PRIORIDADE
                  FROM CHECKLIST_PERGUNTAS CP
                  WHERE CP.CODIGO = CAP.COD_PERGUNTA);

-- TODAS AS LINHAS DEVEM SER DIFERENTES DE NULL AGORA.
ALTER TABLE CHECKLIST_ALTERNATIVA_PERGUNTA
  ALTER COLUMN PRIORIDADE SET NOT NULL;

ALTER TABLE CHECKLIST_PERGUNTAS
  DROP COLUMN PRIORIDADE;
---
---
--- FIM!
---
---


---
---
--- REPLICA PROCEDIMENTO PARA AS TABELAS DE CHECKLISTS PADRÕES DO PROLOG
---
---
ALTER TABLE CHECKLIST_ALTERNATIVA_PERGUNTA_PROLOG
  ADD COLUMN PRIORIDADE VARCHAR(7);

ALTER TABLE CHECKLIST_ALTERNATIVA_PERGUNTA_PROLOG
  ADD CONSTRAINT FK_CHECKLIST_ALTERNATIVA_PRIORIDADE
FOREIGN KEY (PRIORIDADE) REFERENCES CHECKLIST_ALTERNATIVA_PRIORIDADE (PRIORIDADE);

COMMENT ON COLUMN CHECKLIST_ALTERNATIVA_PERGUNTA_PROLOG.PRIORIDADE
IS
  'Salva a prioridade que essa alternativa possui, podendo ser: CRITICA, ALTA ou BAIXA';

-- SETA A PRIORIDADE DE CADA ALTERNATIVA, COMO A PRIORIDADE QUE A PERGUNTA QUE ELA COMPÕE POSSUI.
UPDATE CHECKLIST_ALTERNATIVA_PERGUNTA_PROLOG CAP
SET PRIORIDADE = (SELECT CP.PRIORIDADE
                  FROM CHECKLIST_PERGUNTAS_PROLOG CP
                  WHERE CP.CODIGO = CAP.COD_PERGUNTA_PROLOG);

-- TODAS AS LINHAS DEVEM SER DIFERENTES DE NULL AGORA.
ALTER TABLE CHECKLIST_ALTERNATIVA_PERGUNTA_PROLOG
  ALTER COLUMN PRIORIDADE SET NOT NULL;

ALTER TABLE CHECKLIST_PERGUNTAS_PROLOG
  DROP COLUMN PRIORIDADE;
---
---
--- FIM!
---
---


---
---
--- RECRIA DEPENDÊNCIAS.
---
---
CREATE VIEW ESTRATIFICACAO_OS AS
  SELECT
    V.COD_TIPO                                                       AS COD_TIPO,
    REALIZADOR.NOME                                                  AS NOME_REALIZADOR_CHECKLIST,
    MECANICO.NOME                                                    AS NOME_MECANICO,
    C.PLACA_VEICULO                                                  AS PLACA_VEICULO,
    C.KM_VEICULO                                                     AS KM,
    C.DATA_HORA AT TIME ZONE TZ_UNIDADE(COS.COD_UNIDADE)             AS DATA_HORA,
    C.TIPO                                                           AS TIPO_CHECKLIST,
    CP.CODIGO                                                        AS COD_PERGUNTA,
    CP.ORDEM                                                         AS ORDEM_PERGUNTA,
    CP.PERGUNTA                                                      AS PERGUNTA,
    CP.SINGLE_CHOICE                                                 AS SINGLE_CHOICE,
    NULL :: UNKNOWN                                                  AS URL_IMAGEM,
    CAP.PRIORIDADE                                                   AS PRIORIDADE,
    CASE CAP.PRIORIDADE
    WHEN 'CRITICA' :: TEXT
      THEN 1
    WHEN 'ALTA' :: TEXT
      THEN 2
    WHEN 'BAIXA' :: TEXT
      THEN 3
    ELSE NULL :: INTEGER
    END                                                              AS PRIORIDADE_ORDEM,
    CAP.CODIGO                                                       AS COD_ALTERNATIVA,
    CAP.ALTERNATIVA                                                  AS ALTERNATIVA,
    PRIO.PRAZO                                                       AS PRAZO,
    CR.RESPOSTA                                                      AS RESPOSTA,
    COS.CODIGO                                                       AS COD_OS,
    COS.COD_UNIDADE                                                  AS COD_UNIDADE,
    COS.STATUS                                                       AS STATUS_OS,
    COS.COD_CHECKLIST                                                AS COD_CHECKLIST,
    TZ_UNIDADE(COS.COD_UNIDADE)                                      AS TIME_ZONE_UNIDADE,
    COSI.STATUS_RESOLUCAO                                            AS STATUS_ITEM,
    COSI.CPF_MECANICO                                                AS CPF_MECANICO,
    COSI.TEMPO_REALIZACAO                                            AS TEMPO_REALIZACAO,
    COSI.DATA_HORA_CONSERTO AT TIME ZONE TZ_UNIDADE(COS.COD_UNIDADE) AS DATA_HORA_CONSERTO,
    COSI.KM                                                          AS KM_FECHAMENTO,
    COSI.QT_APONTAMENTOS                                             AS QT_APONTAMENTOS,
    COSI.FEEDBACK_CONSERTO                                           AS FEEDBACK_CONSERTO,
    COSI.CODIGO                                                      AS CODIGO
  FROM CHECKLIST C
    JOIN COLABORADOR REALIZADOR
      ON REALIZADOR.CPF = C.CPF_COLABORADOR
    JOIN VEICULO V
      ON V.PLACA = C.PLACA_VEICULO
    JOIN CHECKLIST_ORDEM_SERVICO COS
      ON C.CODIGO = COS.COD_CHECKLIST
         AND C.COD_UNIDADE = COS.COD_UNIDADE
    JOIN CHECKLIST_ORDEM_SERVICO_ITENS COSI
      ON COS.CODIGO = COSI.COD_OS
         AND COS.COD_UNIDADE = COSI.COD_UNIDADE
    JOIN CHECKLIST_PERGUNTAS CP
      ON CP.COD_UNIDADE = COS.COD_UNIDADE
         AND CP.CODIGO = COSI.COD_PERGUNTA
         AND CP.COD_CHECKLIST_MODELO = C.COD_CHECKLIST_MODELO
    JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
      ON CAP.COD_UNIDADE = CP.COD_UNIDADE
         AND CAP.COD_CHECKLIST_MODELO = CP.COD_CHECKLIST_MODELO
         AND CAP.COD_PERGUNTA = CP.CODIGO
         AND CAP.CODIGO = COSI.COD_ALTERNATIVA
    JOIN CHECKLIST_ALTERNATIVA_PRIORIDADE PRIO
      ON PRIO.PRIORIDADE = CAP.PRIORIDADE
    JOIN CHECKLIST_RESPOSTAS CR
      ON C.COD_UNIDADE = CR.COD_UNIDADE
         AND CR.COD_CHECKLIST_MODELO = C.COD_CHECKLIST_MODELO
         AND CR.COD_CHECKLIST = C.CODIGO
         AND CR.COD_PERGUNTA = CP.CODIGO
         AND CR.COD_ALTERNATIVA = CAP.CODIGO
    LEFT JOIN COLABORADOR MECANICO
      ON MECANICO.CPF = COSI.CPF_MECANICO;

COMMENT ON VIEW ESTRATIFICACAO_OS
IS 'View que compila as informações das OS e seus itens';
---
---
--- FIM!
---
---

---
---
--- NADA PRECISA SER FEITO, NÃO MEXE COM PRIORIDADE E JÁ TEM ARQUIVO PRÓPRIO
---
---

-- FUNC_CHECKLIST_RELATORIO_CHECKS_REALIZADOS_EM_MENOS_DE_1_30

---
---
--- FIM!
---
---


--======================================================================================================================
--======================================================================================================================
--======================================================================================================================
--======================================================================================================================
--======================================================================================================================
--======================================================================================================================
--======================================================================================================================
--======================================================================================================================
--======================================================================================================================


---
---
--- NÃO FORAM ALTERADOS, SÓ ARQUIVO DA FUNCTION CRIADO
---
---

-- FUNC_CHECKLIST_RELATORIO_AMBEV_EXTRATO_REALIZADOS_DIA
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_RELATORIO_AMBEV_EXTRATO_REALIZADOS_DIA(
  F_COD_UNIDADES BIGINT [],
  F_DATA_INICIAL DATE,
  F_DATA_FINAL   DATE)
  RETURNS TABLE(
    "UNIDADE"        TEXT,
    "DATA"           TEXT,
    "PLACA"          TEXT,
    "CHECKS SAÍDA"   BIGINT,
    "CHECKS RETORNO" BIGINT)
LANGUAGE SQL
AS $$
WITH MAPAS AS (
    SELECT
      M.DATA AS DATA_MAPA,
      M.MAPA,
      M.PLACA
    FROM MAPA M
      JOIN VEICULO V ON V.PLACA = M.PLACA
    WHERE M.COD_UNIDADE = ANY (F_COD_UNIDADES)
          AND M.DATA >= F_DATA_INICIAL
          AND M.DATA <= F_DATA_FINAL
    ORDER BY M.DATA ASC),

    CHECKS AS (SELECT
                 C.COD_UNIDADE                                                AS COD_UNIDADE,
                 (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: DATE AS DATA,
                 C.PLACA_VEICULO                                              AS PLACA_VEICULO,
                 SUM(CASE WHEN C.TIPO = 'S'
                   THEN 1
                     ELSE 0 END)                                              AS CHECKS_SAIDA,
                 SUM(CASE WHEN C.TIPO = 'R'
                   THEN 1
                     ELSE 0 END)                                              AS CHECKS_RETORNO
               FROM CHECKLIST C
                 JOIN UNIDADE U ON C.COD_UNIDADE = U.CODIGO
                 LEFT JOIN MAPAS AS M
                   ON M.DATA_MAPA = (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: DATE
                      AND M.PLACA = C.PLACA_VEICULO
               WHERE C.COD_UNIDADE = ANY (F_COD_UNIDADES)
                     AND (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: DATE >= F_DATA_INICIAL
                     AND (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: DATE <= F_DATA_FINAL
               GROUP BY COD_UNIDADE, DATA, PLACA_VEICULO
               ORDER BY COD_UNIDADE, DATA, PLACA_VEICULO)

SELECT
  (SELECT NOME
   FROM UNIDADE U
   WHERE U.CODIGO = C.COD_UNIDADE) AS NOME_UNIDADE,
  TO_CHAR(C.DATA, 'DD/MM/YYYY')    AS DATA,
  C.PLACA_VEICULO,
  C.CHECKS_SAIDA,
  C.CHECKS_RETORNO
FROM CHECKS C
ORDER BY NOME_UNIDADE, DATA
$$;

-- FUNC_CHECKLIST_RELATORIO_AMBEV_REALIZADOS_DIA
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_RELATORIO_AMBEV_REALIZADOS_DIA(
  F_COD_UNIDADES BIGINT [],
  F_DATA_INICIAL DATE,
  F_DATA_FINAL   DATE)
  RETURNS TABLE(
    "UNIDADE"            TEXT,
    "DATA"               TEXT,
    "QTD CHECKS SAÍDA"   BIGINT,
    "ADERÊNCIA SAÍDA"    TEXT,
    "QTD CHECKS RETORNO" BIGINT,
    "ADERÊNCIA RETORNO"  TEXT,
    "TOTAL DE CHECKS"    BIGINT,
    "TOTAL DE MAPAS"     BIGINT,
    "ADERÊNCIA DIA"      TEXT)
LANGUAGE SQL
AS $$
SELECT
  DADOS.NOME_UNIDADE                                                                                   AS NOME_UNIDADE,
  TO_CHAR(DADOS.DATA, 'DD/MM/YYYY')                                                                    AS DATA,
  DADOS.CHECKS_SAIDA                                                                                   AS QTD_CHECKS_SAIDA,
  TRUNC((DADOS.CHECKS_SAIDA :: FLOAT / DADOS.TOTAL_MAPAS) * 100) ||
  '%'                                                                                                  AS ADERENCIA_SAIDA,
  DADOS.CHECKS_RETORNO                                                                                 AS QTD_CHECKS_RETORNO,
  TRUNC((DADOS.CHECKS_RETORNO :: FLOAT / DADOS.TOTAL_MAPAS) * 100) ||
  '%'                                                                                                  AS ADERENCIA_RETORNO,
  DADOS.TOTAL_CHECKS                                                                                   AS TOTAL_CHECKS,
  DADOS.TOTAL_MAPAS                                                                                    AS TOTAL_MAPAS,
  TRUNC(((DADOS.CHECKS_SAIDA + DADOS.CHECKS_RETORNO) :: FLOAT / (DADOS.TOTAL_MAPAS * 2)) * 100) || '%' AS ADERENCIA_DIA
FROM (SELECT
        U.NOME                                                       AS NOME_UNIDADE,
        (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: DATE AS DATA,
        SUM(CASE WHEN C.TIPO = 'S'
          THEN 1
            ELSE 0 END)                                              AS CHECKS_SAIDA,
        SUM(CASE WHEN C.TIPO = 'R'
          THEN 1
            ELSE 0 END)                                              AS CHECKS_RETORNO,
        COUNT(C.DATA_HORA :: DATE)                                   AS TOTAL_CHECKS,
        DIA_MAPAS.TOTAL_MAPAS_DIA                                    AS TOTAL_MAPAS
      FROM CHECKLIST C
        JOIN UNIDADE U ON C.COD_UNIDADE = U.CODIGO
        LEFT JOIN (SELECT
                     M.DATA        AS DATA_MAPA,
                     COUNT(M.MAPA) AS TOTAL_MAPAS_DIA
                   FROM MAPA M
                     JOIN VEICULO V ON V.PLACA = M.PLACA
                   WHERE M.COD_UNIDADE = ANY (ARRAY [5])
                         AND M.DATA >= F_DATA_INICIAL
                         AND M.DATA <= F_DATA_FINAL
                   GROUP BY M.DATA
                   ORDER BY M.DATA ASC) AS DIA_MAPAS
          ON DIA_MAPAS.DATA_MAPA = (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: DATE
      WHERE C.COD_UNIDADE = ANY (F_COD_UNIDADES)
            AND (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: DATE >= F_DATA_INICIAL
            AND (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: DATE <= F_DATA_FINAL
      GROUP BY U.CODIGO, DATA, DIA_MAPAS.TOTAL_MAPAS_DIA
      ORDER BY U.NOME, (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: DATE) AS DADOS
$$;

-- FUNC_CHECKLIST_RELATORIO_TEMPO_REALIZACAO_MOTORISTAS
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_RELATORIO_TEMPO_REALIZACAO_MOTORISTAS(
  F_COD_UNIDADES BIGINT [],
  F_DATA_INICIAL DATE,
  F_DATA_FINAL   DATE)
  RETURNS TABLE(
    "UNIDADE"                              TEXT,
    "NOME"                                 TEXT,
    "FUNÇÃO"                               TEXT,
    "CHECKS SAÍDA"                         BIGINT,
    "CHECKS RETORNO"                       BIGINT,
    "TOTAL"                                BIGINT,
    "MÉDIA TEMPO DE REALIZAÇÃO (SEGUNDOS)" NUMERIC)
LANGUAGE SQL
AS $$
SELECT
  U.NOME                                AS NOME_UNIDADE,
  CO.NOME                               AS NOME,
  F.NOME                                AS FUNCAO,
  SUM(CASE WHEN C.TIPO = 'S'
    THEN 1
      ELSE 0 END)                       AS CHECKS_SAIDA,
  SUM(CASE WHEN C.TIPO = 'R'
    THEN 1
      ELSE 0 END)                       AS CHECKS_RETORNO,
  COUNT(C.TIPO)                         AS TOTAL_CHECKS,
  ROUND(AVG(C.TEMPO_REALIZACAO) / 1000) AS MEDIA_SEGUNDOS_REALIZACAO
FROM CHECKLIST C
  JOIN UNIDADE U ON C.COD_UNIDADE = U.CODIGO
  JOIN COLABORADOR CO ON CO.CPF = C.CPF_COLABORADOR
  JOIN FUNCAO F ON F.CODIGO = CO.COD_FUNCAO AND F.COD_EMPRESA = CO.COD_EMPRESA
WHERE C.COD_UNIDADE = ANY (F_COD_UNIDADES)
      AND (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: DATE >= F_DATA_INICIAL
      AND (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: DATE <= F_DATA_FINAL
GROUP BY U.CODIGO, CO.CPF, F.CODIGO
ORDER BY U.NOME, CO.NOME
$$;

-- FUNC_CHECKLIST_RELATORIO_QTD_POR_TIPO
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_RELATORIO_QTD_POR_TIPO(
  F_COD_UNIDADES                 BIGINT [],
  F_DATA_HOJE_UTC                DATE,
  F_DIAS_RETROATIVOS_PARA_BUSCAR INTEGER)
  RETURNS TABLE(DATA DATE, DATA_FORMATADA TEXT, TOTAL_CHECKLISTS_SAIDA BIGINT, TOTAL_CHECKLISTS_RETORNO BIGINT)
LANGUAGE PLPGSQL
AS $$
DECLARE
  DATA_INICIAL       DATE := F_DATA_HOJE_UTC + INTERVAL '1' DAY - (INTERVAL '1' DAY * F_DIAS_RETROATIVOS_PARA_BUSCAR);
  DATA_FINAL         DATE := F_DATA_HOJE_UTC + INTERVAL '1' DAY;
  CHECK_TIPO_SAIDA   CHAR := 'S';
  CHECK_TIPO_RETORNO CHAR := 'R';
BEGIN
  RETURN QUERY

  WITH DIAS AS (
      SELECT G.DAY :: DATE AS DATA
      FROM GENERATE_SERIES(DATA_INICIAL, DATA_FINAL, '1 DAY') G(DAY)
      ORDER BY DATA
  ),

      CHECKS_DIA AS (
        SELECT
          (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: DATE AS DATA,
          SUM(CASE WHEN C.TIPO = CHECK_TIPO_SAIDA
            THEN 1
              ELSE 0 END)                                                 TOTAL_CHECKLISTS_SAIDA,
          SUM(CASE WHEN C.TIPO = CHECK_TIPO_RETORNO
            THEN 1
              ELSE 0 END)                                                 TOTAL_CHECKLISTS_RETORNO
        FROM CHECKLIST C
        WHERE
          C.COD_UNIDADE = ANY (F_COD_UNIDADES)
          AND (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: DATE BETWEEN DATA_INICIAL AND DATA_FINAL
        GROUP BY DATA
        ORDER BY DATA
    )

  SELECT
    D.DATA                      AS DATA,
    TO_CHAR(D.DATA, 'DD/MM')    AS DATA_FORMATADA,
    CD.TOTAL_CHECKLISTS_SAIDA   AS TOTAL_CHECKLISTS_SAIDA,
    CD.TOTAL_CHECKLISTS_RETORNO AS TOTAL_CHECKLISTS_RETORNO
  FROM DIAS D
    LEFT JOIN CHECKS_DIA CD ON D.DATA = CD.DATA
  ORDER BY D.DATA;
END;
$$;

-- FUNC_CHECKLIST_OS_RELATORIO_PRODUTIVIDADE_MECANICOS
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_OS_RELATORIO_PRODUTIVIDADE_MECANICOS(
  F_COD_UNIDADES BIGINT [],
  F_DATA_INICIAL DATE,
  F_DATA_FINAL   DATE)
  RETURNS TABLE(
    UNIDADE              TEXT,
    "MECÂNICO"           TEXT,
    CONSERTOS            BIGINT,
    HORAS                NUMERIC,
    "HORAS POR CONSERTO" NUMERIC)
LANGUAGE SQL
AS $$
SELECT
  Q.NOME_UNIDADE,
  C.NOME AS MECANICO,
  Q.CONSERTOS,
  Q.HORAS,
  Q.HORAS_POR_CONSERTO
FROM (SELECT
        U.NOME                                 AS NOME_UNIDADE,
        CPF_MECANICO                           AS CPF_MECANICO,
        COUNT(CPF_MECANICO)                    AS CONSERTOS,
        SUM(TEMPO_REALIZACAO / 3600000)        AS HORAS,
        ROUND(AVG(TEMPO_REALIZACAO / 3600000)) AS HORAS_POR_CONSERTO
      FROM ESTRATIFICACAO_OS EO
        JOIN UNIDADE U ON EO.COD_UNIDADE = U.CODIGO
      WHERE EO.TEMPO_REALIZACAO IS NOT NULL
            AND EO.TEMPO_REALIZACAO > 0
            AND EO.COD_UNIDADE = ANY (F_COD_UNIDADES)
            AND (EO.DATA_HORA AT TIME ZONE EO.TIME_ZONE_UNIDADE) :: DATE >= F_DATA_INICIAL
            AND (EO.DATA_HORA AT TIME ZONE EO.TIME_ZONE_UNIDADE) :: DATE <= F_DATA_FINAL
      GROUP BY U.CODIGO, EO.CPF_MECANICO) AS Q
  JOIN COLABORADOR C ON Q.CPF_MECANICO = C.CPF
ORDER BY NOME_UNIDADE, MECANICO;
$$;

-- FUNC_CHECKLIST_OS_RELATORIO_ESTRATIFICACAO_OS
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_OS_RELATORIO_ESTRATIFICACAO_OS(
  F_COD_UNIDADES  BIGINT [],
  F_PLACA_VEICULO TEXT,
  F_STATUS_OS     TEXT,
  F_STATUS_ITEM   TEXT,
  F_DATA_INICIAL  DATE,
  F_DATA_FINAL    DATE)
  RETURNS TABLE(
    UNIDADE                        TEXT,
    "CÓDICO OS"                    BIGINT,
    "ABERTURA OS"                  TEXT,
    "DATA LIMITE CONSERTO"         TEXT,
    "STATUS OS"                    TEXT,
    "PLACA"                        TEXT,
    "PERGUNTA"                     TEXT,
    "ALTERNATIVA"                  TEXT,
    "PRIORIDADE"                   TEXT,
    "PRAZO EM HORAS"               INTEGER,
    "DESCRIÇÃO"                    TEXT,
    "STATUS ITEM"                  TEXT,
    "DATA CONSERTO"                TEXT,
    "MECÂNICO"                     TEXT,
    "DESCRIÇÃO CONSERTO"           TEXT,
    "TEMPO DE CONSERTO EM MINUTOS" BIGINT,
    "KM ABERTURA"                  BIGINT,
    "KM FECHAMENTO"                BIGINT,
    "KM PERCORRIDO"                TEXT,
    "MOTORISTA"                    TEXT,
    "TIPO DO CHECKLIST"            TEXT)
LANGUAGE SQL
AS $$
SELECT
  U.NOME                                                                    AS NOME_UNIDADE,
  COD_OS                                                                    AS CODIGO_OS,
  TO_CHAR(DATA_HORA, 'DD/MM/YYYY HH24:MI')                                  AS ABERTURA_OS,
  TO_CHAR(DATA_HORA + (PRAZO || ' HOUR') :: INTERVAL, 'DD/MM/YYYY HH24:MI') AS DATA_LIMITE_CONSERTO,
  (CASE WHEN STATUS_OS = 'A'
    THEN 'ABERTA'
   ELSE 'FECHADA' END)                                                      AS STATUS_OS,
  PLACA_VEICULO                                                             AS PLACA,
  PERGUNTA                                                                  AS PERGUNTA,
  ALTERNATIVA                                                               AS ALTERNATIVA,
  PRIORIDADE                                                                AS PRIORIDADE,
  PRAZO                                                                     AS PRAZO_EM_HORAS,
  RESPOSTA                                                                  AS DESCRICAO,
  CASE WHEN STATUS_ITEM = 'P'
    THEN 'PENDENTE'
  ELSE 'RESOLVIDO' END                                                      AS STATUS_ITEM,
  TO_CHAR(DATA_HORA_CONSERTO, 'DD/MM/YYYY HH24:MI')                         AS DATA_CONSERTO,
  NOME_MECANICO                                                             AS MECANICO,
  FEEDBACK_CONSERTO                                                         AS DESCRICAO_CONSERTO,
  TEMPO_REALIZACAO / 1000 / 60                                              AS TEMPO_CONSERTO_MINUTOS,
  KM                                                                        AS KM_ABERTURA,
  KM_FECHAMENTO                                                             AS KM_FECHAMENTO,
  COALESCE((KM_FECHAMENTO - KM) :: TEXT, '-')                               AS KM_PERCORRIDO,
  NOME_REALIZADOR_CHECKLIST                                                 AS MOTORISTA,
  CASE WHEN TIPO_CHECKLIST = 'S'
    THEN 'SAÍDA'
  ELSE 'RETORNO' END                                                        AS TIPO_CHECKLIST
FROM ESTRATIFICACAO_OS EO
  JOIN UNIDADE U
    ON EO.COD_UNIDADE = U.CODIGO
WHERE EO.COD_UNIDADE = ANY (F_COD_UNIDADES)
      AND EO.PLACA_VEICULO LIKE F_PLACA_VEICULO
      AND EO.STATUS_OS LIKE F_STATUS_OS
      AND EO.STATUS_ITEM LIKE F_STATUS_ITEM
      AND EO.DATA_HORA :: DATE >= F_DATA_INICIAL
      AND EO.DATA_HORA :: DATE <= F_DATA_FINAL
ORDER BY U.NOME, EO.COD_OS, EO.PRAZO;
$$;

---
---
--- FIM!
---
---


--======================================================================================================================
--======================================================================================================================
--======================================================================================================================
--======================================================================================================================
--======================================================================================================================
--======================================================================================================================
--======================================================================================================================
--======================================================================================================================
--======================================================================================================================


---
---
--- PRECISAM SER ALTERADOS
---
---

-- FUNC_CHECKLIST_RELATORIO_RESUMO_REALIZADOS
-- As alterações foram feitas apenas na CTE RESPOSTAS_NOK
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_RELATORIO_RESUMO_REALIZADOS(
  F_COD_UNIDADES  BIGINT [],
  F_PLACA_VEICULO TEXT,
  F_DATA_INICIAL  DATE,
  F_DATA_FINAL    DATE)
  RETURNS TABLE(
    "UNIDADE"                     TEXT,
    "DATA"                        TEXT,
    "HORA"                        TEXT,
    "COLABORADOR"                 TEXT,
    "PLACA"                       TEXT,
    "KM"                          BIGINT,
    "TEMPO REALIZAÇÃO (SEGUNDOS)" BIGINT,
    "TIPO"                        TEXT,
    "TOTAL DE PERGUNTAS"          BIGINT,
    "TOTAL NOK"                   BIGINT,
    "PRIORIDADE BAIXA"            BIGINT,
    "PRIORIDADE ALTA"             BIGINT,
    "PRIORIDADE CRÍTICA"          BIGINT)
LANGUAGE SQL
AS $$
WITH CHECKLITS AS (
    SELECT
      C.CODIGO                                                                            AS COD_CHECKLIST,
      U.CODIGO                                                                            AS COD_UNIDADE,
      U.NOME                                                                              AS NOME_UNIDADE,
      C.DATA_HORA                                                                         AS DATA_HORA_REALIZACAO,
      TO_CHAR((C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: DATE, 'DD/MM/YYYY') AS DATA_REALIZACAO_CHECK,
      TO_CHAR((C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: TIME, 'HH24:MI')    AS HORARIO_REALIZACAO_CHECK,
      CO.NOME                                                                             AS NOME_COLABORADOR,
      C.PLACA_VEICULO                                                                     AS PLACA_VEICULO,
      C.KM_VEICULO                                                                        AS KM_VEICULO,
      C.TEMPO_REALIZACAO / 1000                                                           AS TEMPO_REALIZACAO_SEGUNDOS,
      F_IF(C.TIPO = 'S', 'Saída' :: TEXT, 'Retorno' :: TEXT)                              AS TIPO_CHECKLIST,
      COUNT(C.CODIGO)                                                                     AS TOTAL_PERGUNTAS
    FROM CHECKLIST C
      JOIN CHECKLIST_PERGUNTAS CP
        ON CP.COD_CHECKLIST_MODELO = C.COD_CHECKLIST_MODELO
      JOIN COLABORADOR CO
        ON C.CPF_COLABORADOR = CO.CPF
      JOIN UNIDADE U
        ON C.COD_UNIDADE = U.CODIGO
    WHERE
      C.COD_UNIDADE = ANY (F_COD_UNIDADES)
      AND C.PLACA_VEICULO LIKE F_PLACA_VEICULO
      AND (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: DATE >= F_DATA_INICIAL
      AND (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: DATE <= F_DATA_FINAL
    GROUP BY
      C.CODIGO,
      U.CODIGO,
      CO.CPF),

    RESPOSTAS_NOK AS (
      SELECT
        CR.COD_CHECKLIST AS COD_CHECKLIST,
        COUNT(CASE WHEN CR.RESPOSTA <> 'OK'
          THEN 1 END)    AS TOTAL_NOK,
        COUNT(CASE WHEN CAP.PRIORIDADE = 'BAIXA'
          THEN 1 END)    AS TOTAL_BAIXAS,
        COUNT(CASE WHEN CAP.PRIORIDADE = 'ALTA'
          THEN 1 END)    AS TOTAL_ALTAS,
        COUNT(CASE WHEN CAP.PRIORIDADE = 'CRITICA'
          THEN 1 END)    AS TOTAL_CRITICAS
      FROM CHECKLIST_RESPOSTAS CR
        JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
          ON CR.COD_ALTERNATIVA = CAP.CODIGO
        JOIN CHECKLIST C
          ON CR.COD_CHECKLIST = C.CODIGO
      WHERE
        CR.RESPOSTA <> 'OK'
        AND C.COD_UNIDADE = ANY (F_COD_UNIDADES)
        AND C.PLACA_VEICULO LIKE F_PLACA_VEICULO
        AND (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: DATE >= F_DATA_INICIAL
        AND (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: DATE <= F_DATA_FINAL
      GROUP BY CR.COD_CHECKLIST
  )

SELECT
  C.NOME_UNIDADE,
  C.DATA_REALIZACAO_CHECK,
  C.HORARIO_REALIZACAO_CHECK,
  C.NOME_COLABORADOR,
  C.PLACA_VEICULO,
  C.KM_VEICULO,
  C.TEMPO_REALIZACAO_SEGUNDOS,
  C.TIPO_CHECKLIST,
  C.TOTAL_PERGUNTAS,
  COALESCE(RN.TOTAL_NOK, 0),
  COALESCE(RN.TOTAL_BAIXAS, 0),
  COALESCE(RN.TOTAL_ALTAS, 0),
  COALESCE(RN.TOTAL_CRITICAS, 0)
FROM CHECKLITS C
  LEFT JOIN RESPOSTAS_NOK RN
    ON C.COD_CHECKLIST = RN.COD_CHECKLIST
ORDER BY
  C.NOME_UNIDADE,
  C.DATA_HORA_REALIZACAO DESC;
$$;

-- FUNC_CHECKLIST_RELATORIO_ESTRATIFICACAO_RESPOSTAS_NOK
-- Alterado para utilizar a tabela CHECKLIST_ALTERNATIVA_PRIORIDADE e também onde utiliza CP passou para CPA para
-- acessar a prioridade
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_RELATORIO_ESTRATIFICACAO_RESPOSTAS_NOK(
  F_COD_UNIDADES  BIGINT [],
  F_PLACA_VEICULO CHARACTER VARYING,
  F_DATA_INICIAL  DATE,
  F_DATA_FINAL    DATE)
  RETURNS TABLE(
    "UNIDADE"          TEXT,
    "CODIGO CHECKLIST" BIGINT,
    "DATA"             CHARACTER VARYING,
    "PLACA"            CHARACTER VARYING,
    "TIPO"             TEXT,
    "KM"               BIGINT,
    "NOME"             CHARACTER VARYING,
    "PERGUNTA"         CHARACTER VARYING,
    "ALTERNATIVA"      CHARACTER VARYING,
    "RESPOSTA"         CHARACTER VARYING,
    "PRIORIDADE"       CHARACTER VARYING,
    "PRAZO EM HORAS"   INTEGER)
LANGUAGE SQL
AS $$
SELECT
  U.NOME                                                                              AS NOME_UNIDADE,
  C.CODIGO                                                                            AS COD_CHECKLIST,
  TO_CHAR((C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)), 'DD/MM/YYYY HH24:MI') AS DATA_HORA_CHECK,
  C.PLACA_VEICULO                                                                     AS PLACA_VEICULO,
  CASE WHEN C.TIPO = 'S'
    THEN 'Saída'
  ELSE 'Retorno' END                                                                  AS TIPO_CHECKLIST,
  C.KM_VEICULO                                                                        AS KM_VEICULO,
  CO.NOME                                                                             AS NOME_REALIZADOR_CHECK,
  CP.PERGUNTA                                                                         AS DESCRICAO_PERGUNTA,
  CAP.ALTERNATIVA                                                                     AS DESCRICAO_ALTERNATIVA,
  CR.RESPOSTA                                                                         AS RESPOSTA,
  CAP.PRIORIDADE                                                                      AS PRIORIDADE,
  PRIO.PRAZO                                                                          AS PRAZO
FROM CHECKLIST C
  JOIN VEICULO V
    ON V.PLACA = C.PLACA_VEICULO
  JOIN CHECKLIST_PERGUNTAS CP
    ON CP.COD_CHECKLIST_MODELO = C.COD_CHECKLIST_MODELO
  JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
    ON CAP.COD_PERGUNTA = CP.CODIGO
  JOIN CHECKLIST_ALTERNATIVA_PRIORIDADE PRIO
    ON PRIO.PRIORIDADE :: TEXT = CAP.PRIORIDADE :: TEXT
  JOIN CHECKLIST_RESPOSTAS CR
    ON C.CODIGO = CR.COD_CHECKLIST
       AND CR.COD_ALTERNATIVA = CAP.CODIGO
       AND CR.RESPOSTA <> 'OK'
  JOIN COLABORADOR CO
    ON CO.CPF = C.CPF_COLABORADOR
  JOIN UNIDADE U
    ON C.COD_UNIDADE = U.CODIGO
WHERE C.COD_UNIDADE = ANY (F_COD_UNIDADES)
      AND C.PLACA_VEICULO LIKE F_PLACA_VEICULO
      AND (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: DATE >= F_DATA_INICIAL
      AND (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: DATE <= F_DATA_FINAL
ORDER BY U.NOME, C.DATA_HORA DESC, C.CODIGO ASC
$$;

-- FUNC_CHECKLIST_OS_RELATORIO_PLACAS_MAIOR_QTD_ITENS_ABERTOS
-- Foi feito um JOIN CHECKLIST_ALTERNATIVA_PERGUNTA e também alterado o COUNT com CASE para utilizar a tabela correta.
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_OS_RELATORIO_PLACAS_MAIOR_QTD_ITENS_ABERTOS(
  F_COD_UNIDADES             BIGINT [],
  F_TOTAL_PLACAS_PARA_BUSCAR INTEGER)
  RETURNS TABLE(
    NOME_UNIDADE                      CHARACTER VARYING,
    PLACA                             CHARACTER VARYING,
    QUANTIDADE_ITENS_ABERTOS          BIGINT,
    QUANTIDADE_ITENS_CRITICOS_ABERTOS BIGINT)
LANGUAGE PLPGSQL
AS $$
DECLARE
  STATUS_ITENS_ABERTOS CHAR := 'P';
  PRIORIDADE_CRITICA   VARCHAR := 'CRITICA';
BEGIN
  RETURN QUERY
  WITH PLACAS AS (
      SELECT
        C.PLACA_VEICULO    AS PLACA_VEICULO,
        COUNT(COSI.CODIGO) AS QUANTIDADE_ITENS_ABERTOS,
        COUNT(CASE WHEN CAP.PRIORIDADE = PRIORIDADE_CRITICA
          THEN 1 END)      AS QUANTIDADE_ITENS_CRITICOS_ABERTOS
      FROM CHECKLIST_ORDEM_SERVICO_ITENS COSI
        JOIN CHECKLIST_ORDEM_SERVICO COS
          ON COSI.COD_OS = COS.CODIGO AND COSI.COD_UNIDADE = COS.COD_UNIDADE
        JOIN CHECKLIST_PERGUNTAS CP
          ON COSI.COD_PERGUNTA = CP.CODIGO
        JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
          ON CP.CODIGO = CAP.COD_PERGUNTA
        JOIN CHECKLIST C
          ON C.CODIGO = COS.COD_CHECKLIST
      WHERE C.COD_UNIDADE = ANY (F_COD_UNIDADES)
            AND COSI.STATUS_RESOLUCAO = STATUS_ITENS_ABERTOS
      GROUP BY C.PLACA_VEICULO
      LIMIT F_TOTAL_PLACAS_PARA_BUSCAR
  )

  SELECT
    U.NOME AS NOME_UNIDADE,
    P.PLACA_VEICULO,
    P.QUANTIDADE_ITENS_ABERTOS,
    P.QUANTIDADE_ITENS_CRITICOS_ABERTOS
  FROM PLACAS P
    JOIN VEICULO V ON V.PLACA = P.PLACA_VEICULO
    JOIN UNIDADE U ON V.COD_UNIDADE = U.CODIGO
  ORDER BY P.QUANTIDADE_ITENS_ABERTOS DESC, P.PLACA_VEICULO ASC
  LIMIT F_TOTAL_PLACAS_PARA_BUSCAR;
END;
$$;

-- FUNC_CHECKLIST_OS_RELATORIO_QTD_ITENS_POR_PRIORIDADE
-- Foi feito JOIN com CHECKLIST_ALTERNATIVA_PERGUNTA para termos de onde pegar a prioridade correta. A CTE PRIORIDADES
-- utilizava as prioridades hardcoded, agora ela consulta de uma tabela específica das prioridades do checklist :D
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_OS_RELATORIO_QTD_ITENS_POR_PRIORIDADE(
  F_COD_UNIDADES BIGINT [],
  F_STATUS_ITENS TEXT)
  RETURNS TABLE(
    PRIORIDADE TEXT,
    QUANTIDADE BIGINT)
LANGUAGE PLPGSQL
AS $$
BEGIN
  RETURN QUERY
  WITH PRIORIDADES AS (
      SELECT CAP.PRIORIDADE :: TEXT AS PRIORIDADE
      FROM CHECKLIST_ALTERNATIVA_PRIORIDADE CAP
      ORDER BY CAP.PRAZO ASC
  )

  SELECT
    P.PRIORIDADE       AS PRIORIDADE,
    COUNT(COSI.CODIGO) AS QUANTIDADE
  FROM CHECKLIST_PERGUNTAS CP
    JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
      ON CAP.COD_PERGUNTA = CP.CODIGO
    JOIN CHECKLIST_ORDEM_SERVICO_ITENS COSI
      ON CP.CODIGO = COSI.COD_PERGUNTA
         AND COSI.COD_UNIDADE = ANY (F_COD_UNIDADES)
         AND COSI.STATUS_RESOLUCAO = F_STATUS_ITENS
    RIGHT JOIN PRIORIDADES P
      ON CAP.PRIORIDADE = P.PRIORIDADE
  GROUP BY P.PRIORIDADE
  ORDER BY CASE P.PRIORIDADE
           WHEN 'CRITICA'
             THEN 1
           WHEN 'ALTA'
             THEN 2
           WHEN 'BAIXA'
             THEN 3
           END;
END;
$$;

CREATE OR REPLACE FUNCTION TO_SECONDS_TRUNC(T INTERVAL)
  RETURNS BIGINT
LANGUAGE PLPGSQL
AS $$
DECLARE
  _SECONDS BIGINT;
BEGIN
  SELECT (EXTRACT(EPOCH FROM T)) :: BIGINT
  INTO _SECONDS;
  RETURN _SECONDS;
END;
$$;

CREATE OR REPLACE FUNCTION TO_MINUTES_TRUNC(T INTERVAL)
  RETURNS BIGINT
LANGUAGE PLPGSQL
AS $$
DECLARE
  _MINUTES BIGINT;
BEGIN
  SELECT ((EXTRACT(EPOCH FROM T)) / 60) :: BIGINT
  INTO _MINUTES;
  RETURN _MINUTES;
END;
$$;

-- FUNC_CHECKLIST_OS_RELATORIO_MEDIA_TEMPO_CONSERTO_ITEM
-- Foi corrigido apenas um problema no filtro, a view ESTRAFICACAO_OS já aplica tz na DATA_HORA e esse relatório estava
-- aplicando novamente.
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_OS_RELATORIO_MEDIA_TEMPO_CONSERTO_ITEM(
  F_COD_UNIDADES BIGINT [],
  F_DATA_INICIAL DATE,
  F_DATA_FINAL   DATE)
  RETURNS TABLE(
    UNIDADE                                      TEXT,
    PERGUNTA                                     TEXT,
    ALTERNATIVA                                  TEXT,
    PRIORIDADE                                   TEXT,
    "PRAZO CONSERTO EM HORAS"                    INTEGER,
    "QTD APONTADOS"                              BIGINT,
    "TOTAL ITENS RESOLVIDOS"                     BIGINT,
    "QTD RESOLVIDOS DENTRO PRAZO"                BIGINT,
    "MÉDIA TEMPO CONSERTO EM HORAS / EM MINUTOS" TEXT,
    "PORCENTAGEM"                                TEXT)
LANGUAGE SQL
AS $$
SELECT
  NOME_UNIDADE                                                               AS NOME_UNIDADE,
  PERGUNTA                                                                   AS PERGUNTA,
  ALTERNATIVA                                                                AS ALTERNATIVA,
  PRIORIDADE                                                                 AS PRIORIDADE,
  PRAZO_CONSERTO_EM_HORAS                                                    AS PRAZO_CONSERTO_HORAS,
  QTD_APONTADOS                                                              AS QTD_APONTADOS,
  TOTAL_ITENS_RESOLVIDOS                                                     AS TOTAL_ITENS_RESOLVIDOS,
  QTD_RESOLVIDOS_DENTRO_PRAZO                                                AS QTD_RESOLVIDOS_DENTRO_PRAZO,
  TRUNC(MEDIA_TEMPO_CONSERTO_SEGUNDOS / 3600) || ' / ' ||
  TRUNC(MEDIA_TEMPO_CONSERTO_SEGUNDOS / 60)                                  AS MD_TEMPO_CONSERTO_HORAS_MINUTOS,
  ROUND((QTD_RESOLVIDOS_DENTRO_PRAZO / QTD_APONTADOS :: FLOAT) * 100) || '%' AS PORCENTAGEM
FROM
  (SELECT
     U.NOME                                       AS NOME_UNIDADE,
     EO.PERGUNTA,
     EO.ALTERNATIVA,
     EO.PRIORIDADE,
     EO.PRAZO                                     AS PRAZO_CONSERTO_EM_HORAS,
     COUNT(EO.PERGUNTA)                           AS QTD_APONTADOS,
     SUM(CASE WHEN EO.CPF_MECANICO IS NOT NULL
       THEN 1
         ELSE 0 END)                              AS TOTAL_ITENS_RESOLVIDOS,
     SUM(CASE WHEN (TO_SECONDS_TRUNC(EO.DATA_HORA_CONSERTO - EO.DATA_HORA)) <= EO.PRAZO
       THEN 1
         ELSE 0 END)                              AS QTD_RESOLVIDOS_DENTRO_PRAZO,
     TRUNC(EXTRACT(EPOCH FROM AVG(EO.DATA_HORA_CONSERTO -
                                  EO.DATA_HORA))) AS MEDIA_TEMPO_CONSERTO_SEGUNDOS
   FROM ESTRATIFICACAO_OS EO
     JOIN UNIDADE U ON EO.COD_UNIDADE = U.CODIGO
   WHERE EO.COD_UNIDADE = ANY (F_COD_UNIDADES)
         AND EO.DATA_HORA :: DATE >= F_DATA_INICIAL
         AND EO.DATA_HORA :: DATE <= F_DATA_FINAL
   GROUP BY U.CODIGO, EO.PERGUNTA, EO.ALTERNATIVA, EO.PRIORIDADE, EO.PRAZO) AS DADOS
ORDER BY DADOS.NOME_UNIDADE, ROUND((QTD_RESOLVIDOS_DENTRO_PRAZO / QTD_APONTADOS :: FLOAT) * 100) DESC
$$;

-- FUNC_CHECKLIST_OS_RELATORIO_ITENS_MAIOR_QUANTIDADE_NOK
-- Alterado apenas de qual tabela é utilizado a PRIORIDADE para retorno na function
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_OS_RELATORIO_ITENS_MAIOR_QUANTIDADE_NOK(
  F_COD_UNIDADES BIGINT [],
  F_DATA_INICIAL DATE,
  F_DATA_FINAL   DATE)
  RETURNS TABLE(
    UNIDADE               TEXT,
    PERGUNTA              TEXT,
    ALTERNATIVA           TEXT,
    PRIORIDADE            TEXT,
    "TOTAL MARCAÇÕES NOK" BIGINT,
    "TOTAL REALIZAÇÕES"   BIGINT,
    "PROPORÇÃO"           TEXT)
LANGUAGE SQL
AS $$
SELECT
  U.NOME                                                              AS NOME_UNIDADE,
  CP.PERGUNTA                                                         AS PERGUNTA,
  CAP.ALTERNATIVA                                                     AS ALTERNATIVA,
  CAP.PRIORIDADE                                                      AS PRIORIDADE,
  SUM(CASE WHEN CR.RESPOSTA <> 'OK'
    THEN 1
      ELSE 0 END)                                                     AS TOTAL_MARCACOES_NOK,
  COUNT(CP.PERGUNTA)                                                  AS TOTAL_REALIZACOES,
  TRUNC(((SUM(CASE WHEN CR.RESPOSTA <> 'OK'
    THEN 1
              ELSE 0 END)
          / COUNT(CP.PERGUNTA) :: FLOAT) * 100) :: NUMERIC, 1) || '%' AS PROPORCAO
FROM CHECKLIST C
  JOIN CHECKLIST_PERGUNTAS CP
    ON CP.COD_CHECKLIST_MODELO = C.COD_CHECKLIST_MODELO
  JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
    ON CAP.COD_PERGUNTA = CP.CODIGO
  JOIN CHECKLIST_RESPOSTAS CR
    ON CR.COD_CHECKLIST = C.CODIGO
       AND CR.COD_ALTERNATIVA = CAP.CODIGO
  JOIN UNIDADE U
    ON U.CODIGO = C.COD_UNIDADE
WHERE C.COD_UNIDADE = ANY (F_COD_UNIDADES)
      AND (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: DATE >= F_DATA_INICIAL
      AND (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: DATE <= F_DATA_FINAL
GROUP BY U.CODIGO, CP.CODIGO, CAP.CODIGO, CAP.PRIORIDADE
ORDER BY U.NOME, PROPORCAO DESC
$$;

-- FUNC_CHECKLIST_RELATORIO_LISTAGEM_MODELOS_CHECKLIST
-- Alterado de qual tabela é utilizado a PRIORIDADE.
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_RELATORIO_LISTAGEM_MODELOS_CHECKLIST(F_COD_UNIDADES BIGINT [])
  RETURNS TABLE(
    UNIDADE             TEXT,
    "NOME DO CHECKLIST" TEXT,
    PERGUNTA            TEXT,
    ALTERNATIVA         TEXT,
    "TIPO DE RESPOSTA"  TEXT,
    PRIORIDADE          TEXT
  )
LANGUAGE SQL
AS $$
SELECT
  U.NOME              AS NOME_UNIDADE,
  CM.NOME             AS NOME_MODELO,
  CP.PERGUNTA         AS PERGUNTA,
  CAP.ALTERNATIVA     AS ALTERNATIVA,
  CASE WHEN CP.SINGLE_CHOICE
    THEN 'ÚNICA'
  ELSE 'MÚLTIPLA' END AS TIPO_DE_RESPOSTA,
  CAP.PRIORIDADE      AS PRIORIDADE
FROM CHECKLIST_PERGUNTAS CP
  JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
    ON CP.COD_UNIDADE = CAP.COD_UNIDADE
       AND CP.COD_CHECKLIST_MODELO = CAP.COD_CHECKLIST_MODELO
       AND CP.CODIGO = CAP.COD_PERGUNTA
  JOIN CHECKLIST_MODELO CM
    ON CAP.COD_CHECKLIST_MODELO = CM.CODIGO
  JOIN UNIDADE U
    ON CM.COD_UNIDADE = U.CODIGO
WHERE CM.COD_UNIDADE = ANY (F_COD_UNIDADES)
ORDER BY U.NOME, CM.NOME, CP.PERGUNTA, CAP.ALTERNATIVA;
$$;

-- FUNC_CHECKLIST_RELATORIO_DADOS_GERAIS
-- Alterado de qual tabela é utilizado a PRIORIDADE.
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_RELATORIO_DADOS_GERAIS(
  F_COD_UNIDADES    BIGINT [],
  F_DATA_INICIAL    DATE,
  F_DATA_FINAL      DATE,
  F_PLACA           TEXT,
  F_COD_COLABORADOR BIGINT)
  RETURNS TABLE(
    UNIDADE                TEXT,
    "DATA/HORA REALIZAÇÃO" TEXT,
    "CPF COLABORADOR"      TEXT,
    "NOME COLABORADOR"     TEXT,
    "TIPO VEÍCULO"         TEXT,
    PLACA                  TEXT,
    "TIPO CHECKLIST"       TEXT,
    "CÓDIGO MODELO"        TEXT,
    "NOME MODELO"          TEXT,
    PERGUNTA               TEXT,
    ALTERNATIVA            TEXT,
    RESPOSTA               TEXT,
    PRIORIDADE             TEXT,
    "TIPO RESPOSTA"        TEXT
  )
LANGUAGE SQL
AS $$
SELECT
  U.NOME                                         AS NOME_UNIDADE,
  TO_CHAR(CL.DATA_HORA, 'DD/MM/YYYY HH24:MI:SS') AS DATA_HORA_REALIZACAO,
  LPAD(CL.CPF_COLABORADOR :: TEXT, 11, '0')      AS CPF_COLABORADOR,
  CO.NOME                                        AS NOME_COLABORADOR,
  VT.NOME                                        AS NOME_TIPO_VEICULO,
  CL.PLACA_VEICULO                               AS PLACA_VEICULO,
  CASE WHEN CL.TIPO = 'S'
    THEN 'SAÍDA'
  ELSE 'RETORNO' END                             AS TIPO_CHECKLIST,
  CM.CODIGO :: TEXT                              AS CODIGO_MODELO_CHECKLIST,
  CM.NOME                                        AS NOME_MODELO_CHECKLIST,
  CP.PERGUNTA                                    AS PERGUNTA,
  CAP.ALTERNATIVA                                AS ALTERNATIVA,
  CR.RESPOSTA                                    AS RESPOSTA,
  CAP.PRIORIDADE                                 AS PRIORIDADE,
  CASE WHEN CP.SINGLE_CHOICE
    THEN 'ÚNICA ESCOLHA'
  ELSE 'MÚLTIPLA ESCOLHA' END                    AS TIPO_RESPOSTA
FROM CHECKLIST CL
  JOIN CHECKLIST_MODELO CM ON CL.COD_CHECKLIST_MODELO = CM.CODIGO
  JOIN CHECKLIST_PERGUNTAS CP ON CM.CODIGO = CP.COD_CHECKLIST_MODELO
  JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP ON CP.CODIGO = CAP.COD_PERGUNTA
  JOIN UNIDADE U ON CL.COD_UNIDADE = U.CODIGO
  JOIN CHECKLIST_RESPOSTAS CR ON CAP.CODIGO = CR.COD_ALTERNATIVA
                                 AND CR.COD_CHECKLIST = CL.CODIGO
  JOIN COLABORADOR CO ON CO.CPF = CL.CPF_COLABORADOR
  JOIN VEICULO_TIPO VT ON VT.CODIGO =
                          (SELECT V.COD_TIPO
                           FROM VEICULO V
                           WHERE V.PLACA = CL.PLACA_VEICULO)
WHERE U.CODIGO = ANY (F_COD_UNIDADES)
      AND (CL.DATA_HORA AT TIME ZONE TZ_UNIDADE(CL.COD_UNIDADE)) :: DATE >= F_DATA_INICIAL
      AND (CL.DATA_HORA AT TIME ZONE TZ_UNIDADE(CL.COD_UNIDADE)) :: DATE <= F_DATA_FINAL
      AND F_IF(F_PLACA IS NOT NULL, CL.PLACA_VEICULO = F_PLACA, TRUE)
      AND F_IF(F_COD_COLABORADOR IS NOT NULL, CO.CODIGO = F_COD_COLABORADOR, TRUE)
ORDER BY U.CODIGO, CL.DATA_HORA DESC, CL.PLACA_VEICULO, CM.CODIGO, CP.PERGUNTA, CAP.CODIGO;
$$;
---
---
--- FIM!
---
---


CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_OS_GET_OS_LISTAGEM(
  F_COD_UNIDADE      BIGINT,
  F_COD_TIPO_VEICULO BIGINT,
  F_PLACA_VEICULO    TEXT,
  F_STATUS_OS        TEXT,
  F_LIMIT            INTEGER,
  F_OFFSET           INTEGER)
  RETURNS TABLE(
    PLACA_VEICULO        TEXT,
    COD_OS               BIGINT,
    COD_UNIDADE_OS       BIGINT,
    COD_CHECKLIST        BIGINT,
    DATA_HORA_ABERTURA   TIMESTAMP,
    DATA_HORA_FECHAMENTO TIMESTAMP,
    STATUS_OS            TEXT,
    QTD_ITENS_PENDENTES  INTEGER,
    QTD_ITENS_RESOLVIDOS INTEGER)
LANGUAGE PLPGSQL
AS $$
DECLARE
  STATUS_ITEM_PENDENTE  TEXT = 'P';
  STATUS_ITEM_RESOLVIDO TEXT = 'R';
BEGIN
  RETURN QUERY
  WITH OS AS (
      SELECT
        COS.CODIGO                                                     AS COD_OS,
        COS.COD_UNIDADE                                                AS COD_UNIDADE_OS,
        COUNT(COS.CODIGO)
          FILTER (WHERE COSI.STATUS_RESOLUCAO = STATUS_ITEM_PENDENTE)  AS QTD_ITENS_PENDENTES,
        COUNT(COS.CODIGO)
          FILTER (WHERE COSI.STATUS_RESOLUCAO = STATUS_ITEM_RESOLVIDO) AS QTD_ITENS_RESOLVIDOS
      FROM CHECKLIST_ORDEM_SERVICO COS
        JOIN CHECKLIST_ORDEM_SERVICO_ITENS COSI
          ON COS.CODIGO = COSI.COD_OS
             AND COS.COD_UNIDADE = COSI.COD_UNIDADE
      WHERE COS.COD_UNIDADE = F_COD_UNIDADE
      GROUP BY COS.COD_UNIDADE, COS.CODIGO
  )

  SELECT
    C.PLACA_VEICULO :: TEXT                                         AS PLACA_VEICULO,
    COS.CODIGO                                                      AS COD_OS,
    COS.COD_UNIDADE                                                 AS COD_UNIDADE_OS,
    COS.COD_CHECKLIST                                               AS COD_CHECKLIST,
    -- A DATA/HORA DO CHECK É A ABERTURA DA O.S.
    C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)              AS DATA_HORA_ABERTURA,
    COS.DATA_HORA_FECHAMENTO AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE) AS DATA_HORA_FECHAMENTO,
    COS.STATUS :: TEXT                                              AS STATUS_OS,
    OS.QTD_ITENS_PENDENTES :: INTEGER                               AS QTD_ITENS_PENDENTES,
    OS.QTD_ITENS_RESOLVIDOS :: INTEGER                              AS QTD_ITENS_RESOLVIDOS
  FROM CHECKLIST C
    JOIN CHECKLIST_ORDEM_SERVICO COS
      ON COS.COD_CHECKLIST = C.CODIGO
    JOIN OS
      ON OS.COD_OS = COS.CODIGO
         AND OS.COD_UNIDADE_OS = COS.COD_UNIDADE
    JOIN VEICULO V
      ON V.PLACA = C.PLACA_VEICULO
    JOIN VEICULO_TIPO VT
      ON VT.COD_UNIDADE = C.COD_UNIDADE
         AND V.COD_TIPO = VT.CODIGO
  WHERE
    C.COD_UNIDADE = F_COD_UNIDADE
    AND F_IF(F_COD_TIPO_VEICULO IS NULL, TRUE, F_COD_TIPO_VEICULO = VT.CODIGO)
    AND F_IF(F_PLACA_VEICULO IS NULL, TRUE, F_PLACA_VEICULO = C.PLACA_VEICULO)
    AND F_IF(F_STATUS_OS IS NULL, TRUE, F_STATUS_OS = COS.STATUS)
  ORDER BY COS.CODIGO DESC
  LIMIT F_LIMIT
  OFFSET F_OFFSET;
END;
$$;


DROP FUNCTION FUNC_CHECKLIST_GET_QTD_ITENS_OS(F_COD_UNIDADE BIGINT,
F_COD_TIPO_VEICULO BIGINT,
F_PLACA_VEICULO TEXT,
F_ITENS_OS_ABERTOS BOOLEAN,
F_LIMIT INTEGER,
F_OFFSET INTEGER );

CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_OS_GET_QTD_ITENS_PLACA_LISTAGEM(
  F_COD_UNIDADE      BIGINT,
  F_COD_TIPO_VEICULO BIGINT,
  F_PLACA_VEICULO    TEXT,
  F_STATUS_ITENS_OS  TEXT,
  F_LIMIT            INTEGER,
  F_OFFSET           INTEGER)
  RETURNS TABLE(
    PLACA_VEICULO                TEXT,
    QTD_ITENS_PRIORIDADE_CRITICA BIGINT,
    QTD_ITENS_PRIORIDADE_ALTA    BIGINT,
    QTD_ITENS_PRIORIDADE_BAIXA   BIGINT,
    TOTAL_ITENS                  BIGINT)
LANGUAGE PLPGSQL
AS $$
DECLARE
  TIPO_ITEM_PRIORIDADE_CRITICA TEXT := 'CRITICA';
  TIPO_ITEM_PRIORIDADE_ALTA    TEXT := 'ALTA';
  TIPO_ITEM_PRIORIDADE_BAIXA   TEXT := 'BAIXA';
BEGIN
  RETURN QUERY
  SELECT
    V.PLACA :: TEXT      AS PLACA_VEICULO,
    COUNT(CASE WHEN CAP.PRIORIDADE = TIPO_ITEM_PRIORIDADE_CRITICA
      THEN 1 END)        AS QTD_ITENS_PRIORIDADE_CRITICA,
    COUNT(CASE WHEN CAP.PRIORIDADE = TIPO_ITEM_PRIORIDADE_ALTA
      THEN 1 END)        AS QTD_ITENS_PRIORIDADE_ALTA,
    COUNT(CASE WHEN CAP.PRIORIDADE = TIPO_ITEM_PRIORIDADE_BAIXA
      THEN 1 END)        AS QTD_ITENS_PRIORIDADE_BAIXA,
    COUNT(CAP.PRIORIDADE) AS TOTAL_ITENS
  FROM VEICULO V
    JOIN CHECKLIST C
      ON V.PLACA = C.PLACA_VEICULO
    JOIN CHECKLIST_ORDEM_SERVICO COS
      ON C.CODIGO = COS.COD_CHECKLIST
    JOIN CHECKLIST_ORDEM_SERVICO_ITENS COSI
      ON COS.CODIGO = COSI.COD_OS
         AND COS.COD_UNIDADE = COSI.COD_UNIDADE
    JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
      ON CAP.CODIGO = COSI.COD_ALTERNATIVA
    JOIN VEICULO_TIPO VT
      ON VT.COD_UNIDADE = C.COD_UNIDADE
         AND V.COD_TIPO = VT.CODIGO
  WHERE V.COD_UNIDADE = F_COD_UNIDADE
        AND F_IF(F_COD_TIPO_VEICULO IS NULL, TRUE, VT.CODIGO = F_COD_TIPO_VEICULO)
        AND F_IF(F_PLACA_VEICULO IS NULL, TRUE, V.PLACA = F_PLACA_VEICULO)
        AND F_IF(F_STATUS_ITENS_OS IS NULL, TRUE, COSI.STATUS_RESOLUCAO = F_STATUS_ITENS_OS)
  GROUP BY V.PLACA
  ORDER BY
    QTD_ITENS_PRIORIDADE_CRITICA DESC,
    QTD_ITENS_PRIORIDADE_ALTA DESC,
    QTD_ITENS_PRIORIDADE_BAIXA DESC,
    PLACA_VEICULO ASC
  LIMIT F_LIMIT
  OFFSET F_OFFSET;
END;
$$;


CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_OS_GET_ORDEM_SERVICO_RESOLUCAO(
  F_COD_UNIDADE         BIGINT,
  F_COD_OS              BIGINT,
  F_DATA_HORA_ATUAL_UTC TIMESTAMPTZ)
  RETURNS TABLE(
    PLACA_VEICULO                         TEXT,
    KM_ATUAL_VEICULO                      BIGINT,
    COD_OS                                BIGINT,
    COD_UNIDADE_OS                        BIGINT,
    STATUS_OS                             TEXT,
    DATA_HORA_ABERTURA_OS                 TIMESTAMP,
    DATA_HORA_FECHAMENTO_OS               TIMESTAMP,
    COD_ITEM_OS                           BIGINT,
    COD_UNIDADE_ITEM_OS                   BIGINT,
    DATA_HORA_PRIMEIRO_APONTAMENTO_ITEM   TIMESTAMP,
    STATUS_ITEM_OS                        TEXT,
    PRAZO_RESOLUCAO_ITEM_HORAS            INTEGER,
    PRAZO_RESTANTE_RESOLUCAO_ITEM_MINUTOS BIGINT,
    QTD_APONTAMENTOS                      INTEGER,
    COD_COLABORADOR_RESOLUCAO             BIGINT,
    NOME_COLABORADOR_RESOLUCAO            TEXT,
    DATA_HORA_RESOLUCAO                   TIMESTAMP,
    FEEDBACK_RESOLUCAO                    TEXT,
    DURACAO_RESOLUCAO_MINUTOS             BIGINT,
    KM_VEICULO_COLETADO_RESOLUCAO         BIGINT,
    COD_PERGUNTA                          BIGINT,
    DESCRICAO_PERGUNTA                    TEXT,
    COD_ALTERNATIVA                       BIGINT,
    DESCRICAO_ALTERNATIVA                 TEXT,
    ALTERNATIVA_TIPO_OUTROS               BOOLEAN,
    DESCRICAO_TIPO_OUTROS                 TEXT,
    PRIORIDADE_ALTERNATIVA                TEXT)
LANGUAGE PLPGSQL
AS $$
BEGIN
  RETURN QUERY
  SELECT
    C.PLACA_VEICULO :: TEXT                                         AS PLACA_VEICULO,
    V.KM                                                            AS KM_ATUAL_VEICULO,
    COS.CODIGO                                                      AS COD_OS,
    COS.COD_UNIDADE                                                 AS COD_UNIDADE_OS,
    COS.STATUS :: TEXT                                              AS STATUS_OS,
    C.DATA_HORA AT TIME ZONE TZ_UNIDADE(F_COD_UNIDADE)              AS DATA_HORA_ABERTURA_OS,
    COS.DATA_HORA_FECHAMENTO AT TIME ZONE TZ_UNIDADE(F_COD_UNIDADE) AS DATA_HORA_FECHAMENTO_OS,
    COSI.CODIGO                                                     AS COD_ITEM_OS,
    COS.COD_UNIDADE                                                 AS COD_UNIDADE_ITEM_OS,
    C.DATA_HORA AT TIME ZONE TZ_UNIDADE(F_COD_UNIDADE)              AS DATA_HORA_PRIMEIRO_APONTAMENTO_ITEM,
    COSI.STATUS_RESOLUCAO                                           AS STATUS_ITEM_OS,
    PRIO.PRAZO                                                      AS PRAZO_RESOLUCAO_ITEM_HORAS,
    TO_MINUTES_TRUNC((C.DATA_HORA
                + (PRIO.PRAZO || ' HOURS') :: INTERVAL)
               - F_DATA_HORA_ATUAL_UTC)                             AS PRAZO_RESTANTE_RESOLUCAO_ITEM_MINUTOS,
    COSI.QT_APONTAMENTOS                                            AS QTD_APONTAMENTOS,
    CO.CODIGO                                                       AS COD_COLABORADOR_RESOLUCAO,
    CO.NOME :: TEXT                                                 AS NOME_COLABORADOR_RESOLUCAO,
    COSI.DATA_HORA_CONSERTO AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)  AS DATA_HORA_RESOLUCAO,
    COSI.FEEDBACK_CONSERTO                                          AS FEEDBACK_RESOLUCAO,
    MILLIS_TO_MINUTES(COSI.TEMPO_REALIZACAO)                        AS DURACAO_RESOLUCAO_MINUTOS,
    COSI.KM                                                         AS KM_VEICULO_COLETADO_RESOLUCAO,
    CP.CODIGO                                                       AS COD_PERGUNTA,
    CP.PERGUNTA                                                     AS DESCRICAO_PERGUNTA,
    CAP.CODIGO                                                      AS COD_ALTERNATIVA,
    CAP.ALTERNATIVA                                                 AS DESCRICAO_ALTERNATIVA,
    CAP.ALTERNATIVA_TIPO_OUTROS                                     AS ALTERNATIVA_TIPO_OUTROS,
    F_IF(CAP.ALTERNATIVA_TIPO_OUTROS,
         (SELECT CR.RESPOSTA
          FROM CHECKLIST_RESPOSTAS CR
          WHERE CR.COD_CHECKLIST = C.CODIGO
                AND CR.COD_ALTERNATIVA = CAP.CODIGO) :: TEXT,
         NULL)                                                      AS DESCRICAO_TIPO_OUTROS,
    CAP.PRIORIDADE :: TEXT                                          AS PRIORIDADE_ALTERNATIVA
  FROM CHECKLIST C
    JOIN CHECKLIST_ORDEM_SERVICO COS
      ON C.CODIGO = COS.COD_CHECKLIST
    JOIN CHECKLIST_ORDEM_SERVICO_ITENS COSI
      ON COS.CODIGO = COSI.COD_OS AND COS.COD_UNIDADE = COSI.COD_UNIDADE
    JOIN CHECKLIST_PERGUNTAS CP
      ON COSI.COD_PERGUNTA = CP.CODIGO
    JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
      ON COSI.COD_ALTERNATIVA = CAP.CODIGO
    JOIN CHECKLIST_ALTERNATIVA_PRIORIDADE PRIO
      ON CAP.PRIORIDADE = PRIO.PRIORIDADE
    JOIN VEICULO V
      ON C.PLACA_VEICULO = V.PLACA
    LEFT JOIN COLABORADOR CO
      ON CO.CPF = COSI.CPF_MECANICO
  WHERE COS.CODIGO = F_COD_OS
        AND COS.COD_UNIDADE = F_COD_UNIDADE;
END;
$$;


CREATE OR REPLACE FUNCTION MILLIS_TO_MINUTES(T BIGINT)
  RETURNS BIGINT
LANGUAGE PLPGSQL
AS $$
BEGIN
  RETURN T / 1000 / 60;
END;
$$;

CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_OS_GET_ITENS_RESOLUCAO(
  F_COD_UNIDADE            BIGINT,
  F_COD_OS                 BIGINT,
  F_PLACA_VEICULO          TEXT,
  F_PRIORIDADE_ALTERNATIVA TEXT,
  F_STATUS_ITENS           TEXT,
  F_DATA_HORA_ATUAL_UTC    TIMESTAMPTZ,
  F_LIMIT                  INTEGER,
  F_OFFSET                 INTEGER)
  RETURNS TABLE(
    PLACA_VEICULO                         TEXT,
    KM_ATUAL_VEICULO                      BIGINT,
    COD_OS                                BIGINT,
    COD_UNIDADE_ITEM_OS                   BIGINT,
    COD_ITEM_OS                           BIGINT,
    DATA_HORA_PRIMEIRO_APONTAMENTO_ITEM   TIMESTAMP,
    STATUS_ITEM_OS                        TEXT,
    PRAZO_RESOLUCAO_ITEM_HORAS            INTEGER,
    PRAZO_RESTANTE_RESOLUCAO_ITEM_MINUTOS BIGINT,
    QTD_APONTAMENTOS                      INTEGER,
    COD_COLABORADOR_RESOLUCAO             BIGINT,
    NOME_COLABORADOR_RESOLUCAO            TEXT,
    DATA_HORA_RESOLUCAO                   TIMESTAMP,
    FEEDBACK_RESOLUCAO                    TEXT,
    DURACAO_RESOLUCAO_MINUTOS             BIGINT,
    KM_VEICULO_COLETADO_RESOLUCAO         BIGINT,
    COD_PERGUNTA                          BIGINT,
    DESCRICAO_PERGUNTA                    TEXT,
    COD_ALTERNATIVA                       BIGINT,
    DESCRICAO_ALTERNATIVA                 TEXT,
    ALTERNATIVA_TIPO_OUTROS               BOOLEAN,
    DESCRICAO_TIPO_OUTROS                 TEXT,
    PRIORIDADE_ALTERNATIVA                TEXT)
LANGUAGE PLPGSQL
AS $$
BEGIN
  RETURN QUERY
  WITH DADOS AS (
      SELECT
        C.PLACA_VEICULO :: TEXT                                        AS PLACA_VEICULO,
        V.KM                                                           AS KM_ATUAL_VEICULO,
        COS.CODIGO                                                     AS COD_OS,
        COS.COD_UNIDADE                                                AS COD_UNIDADE_ITEM_OS,
        COSI.CODIGO                                                    AS COD_ITEM_OS,
        C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)             AS DATA_HORA_PRIMEIRO_APONTAMENTO_ITEM,
        COSI.STATUS_RESOLUCAO                                          AS STATUS_ITEM_OS,
        PRIO.PRAZO                                                     AS PRAZO_RESOLUCAO_ITEM_HORAS,
        TO_MINUTES_TRUNC((C.DATA_HORA
                    + (PRIO.PRAZO || ' HOURS') :: INTERVAL)
                   - F_DATA_HORA_ATUAL_UTC)                            AS PRAZO_RESTANTE_RESOLUCAO_ITEM_MINUTOS,
        COSI.QT_APONTAMENTOS                                           AS QTD_APONTAMENTOS,
        CO.CODIGO                                                      AS COD_COLABORADOR_RESOLUCAO,
        CO.NOME :: TEXT                                                AS NOME_COLABORADOR_RESOLUCAO,
        COSI.DATA_HORA_CONSERTO AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE) AS DATA_HORA_RESOLUCAO,
        COSI.FEEDBACK_CONSERTO                                         AS FEEDBACK_RESOLUCAO,
        MILLIS_TO_MINUTES(COSI.TEMPO_REALIZACAO)                       AS DURACAO_RESOLUCAO_MINUTOS,
        COSI.KM                                                        AS KM_VEICULO_COLETADO_RESOLUCAO,
        CP.CODIGO                                                      AS COD_PERGUNTA,
        CP.PERGUNTA                                                    AS DESCRICAO_PERGUNTA,
        CAP.CODIGO                                                     AS COD_ALTERNATIVA,
        CAP.ALTERNATIVA                                                AS DESCRICAO_ALTERNATIVA,
        CAP.ALTERNATIVA_TIPO_OUTROS                                    AS ALTERNATIVA_TIPO_OUTROS,
        F_IF(CAP.ALTERNATIVA_TIPO_OUTROS,
             (SELECT CR.RESPOSTA
              FROM CHECKLIST_RESPOSTAS CR
              WHERE CR.COD_CHECKLIST = C.CODIGO
                    AND CR.COD_ALTERNATIVA = CAP.CODIGO) :: TEXT,
             NULL)                                                     AS DESCRICAO_TIPO_OUTROS,
        CAP.PRIORIDADE :: TEXT                                         AS PRIORIDADE_ALTERNATIVA
      FROM CHECKLIST C
        JOIN CHECKLIST_ORDEM_SERVICO COS
          ON C.CODIGO = COS.COD_CHECKLIST
        JOIN CHECKLIST_ORDEM_SERVICO_ITENS COSI
          ON COS.CODIGO = COSI.COD_OS
             AND COS.COD_UNIDADE = COSI.COD_UNIDADE
        JOIN CHECKLIST_PERGUNTAS CP
          ON COSI.COD_PERGUNTA = CP.CODIGO
        JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
          ON COSI.COD_ALTERNATIVA = CAP.CODIGO
        JOIN CHECKLIST_ALTERNATIVA_PRIORIDADE PRIO
          ON CAP.PRIORIDADE = PRIO.PRIORIDADE
        JOIN VEICULO V
          ON C.PLACA_VEICULO = V.PLACA
        LEFT JOIN COLABORADOR CO
          ON CO.CPF = COSI.CPF_MECANICO
      WHERE F_IF(F_COD_UNIDADE IS NULL, TRUE, COS.COD_UNIDADE = F_COD_UNIDADE)
            AND F_IF(F_COD_OS IS NULL, TRUE, COS.CODIGO = F_COD_OS)
            AND F_IF(F_PLACA_VEICULO IS NULL, TRUE, C.PLACA_VEICULO = F_PLACA_VEICULO)
            AND F_IF(F_PRIORIDADE_ALTERNATIVA IS NULL, TRUE, CAP.PRIORIDADE = F_PRIORIDADE_ALTERNATIVA)
            AND F_IF(F_STATUS_ITENS IS NULL, TRUE, COSI.STATUS_RESOLUCAO = F_STATUS_ITENS)
      LIMIT F_LIMIT
      OFFSET F_OFFSET
  ),
      DADOS_VEICULO AS (
        SELECT
          V.PLACA :: TEXT AS PLACA_VEICULO,
          V.KM            AS KM_ATUAL_VEICULO
        FROM VEICULO V
        WHERE V.PLACA = F_PLACA_VEICULO
    )

  -- NÓS USAMOS ESSE DADOS_VEICULO COM F_IF POIS PODE ACONTECER DE NÃO EXISTIR DADOS PARA OS FILTROS APLICADOS E
  -- DESSE MODO ACABARÍAMOS NÃO RETORNANDO PLACA E KM TAMBÉM, MAS ESSAS SÃO INFORMAÇÕES NECESSÁRIAS POIS O OBJETO
  -- CONSTRUÍDO A PARTIR DESSA FUNCTION USA ELAS.
  SELECT
    F_IF(D.PLACA_VEICULO IS NULL, DV.PLACA_VEICULO, D.PLACA_VEICULO)          AS PLACA_VEICULO,
    F_IF(D.KM_ATUAL_VEICULO IS NULL, DV.KM_ATUAL_VEICULO, D.KM_ATUAL_VEICULO) AS KM_ATUAL_VEICULO,
    D.COD_OS                                                                  AS COD_OS,
    D.COD_UNIDADE_ITEM_OS                                                     AS COD_UNIDADE_ITEM_OS,
    D.COD_ITEM_OS                                                             AS COD_ITEM_OS,
    D.DATA_HORA_PRIMEIRO_APONTAMENTO_ITEM                                     AS DATA_HORA_PRIMEIRO_APONTAMENTO_ITEM,
    D.STATUS_ITEM_OS                                                          AS STATUS_ITEM_OS,
    D.PRAZO_RESOLUCAO_ITEM_HORAS                                              AS PRAZO_RESOLUCAO_ITEM_HORAS,
    D.PRAZO_RESTANTE_RESOLUCAO_ITEM_MINUTOS                                   AS PRAZO_RESTANTE_RESOLUCAO_ITEM_MINUTOS,
    D.QTD_APONTAMENTOS                                                        AS QTD_APONTAMENTOS,
    D.COD_COLABORADOR_RESOLUCAO                                               AS COD_COLABORADOR_RESOLUCAO,
    D.NOME_COLABORADOR_RESOLUCAO                                              AS NOME_COLABORADOR_RESOLUCAO,
    D.DATA_HORA_RESOLUCAO                                                     AS DATA_HORA_RESOLUCAO,
    D.FEEDBACK_RESOLUCAO                                                      AS FEEDBACK_RESOLUCAO,
    D.DURACAO_RESOLUCAO_MINUTOS                                               AS DURACAO_RESOLUCAO_MINUTOS,
    D.KM_VEICULO_COLETADO_RESOLUCAO                                           AS KM_VEICULO_COLETADO_RESOLUCAO,
    D.COD_PERGUNTA                                                            AS COD_PERGUNTA,
    D.DESCRICAO_PERGUNTA                                                      AS DESCRICAO_PERGUNTA,
    D.COD_ALTERNATIVA                                                         AS COD_ALTERNATIVA,
    D.DESCRICAO_ALTERNATIVA                                                   AS DESCRICAO_ALTERNATIVA,
    D.ALTERNATIVA_TIPO_OUTROS                                                 AS ALTERNATIVA_TIPO_OUTROS,
    D.DESCRICAO_TIPO_OUTROS                                                   AS DESCRICAO_TIPO_OUTROS,
    D.PRIORIDADE_ALTERNATIVA                                                  AS PRIORIDADE_ALTERNATIVA
  FROM DADOS D
    RIGHT JOIN DADOS_VEICULO DV
      ON D.PLACA_VEICULO = DV.PLACA_VEICULO;
END;
$$;
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--###########################################  FIM MIGRAÇÃO CHECK OS  ##################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################











--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--########################################### INÍCIO OUTRAS MIGRAÇÕES SIMPLES  #########################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################


--######################################################################################################################
--######################################################################################################################
--################################ INSERE NO ESQUEMA DA AVILAN O CÓDIGO DA FILIAL MATRIZ ###############################
--######################################################################################################################
--######################################################################################################################
INSERT INTO AVILAN.FILIAL (CODIGO, COD_UNIDADE_PROLOG) VALUES (1, 54);
INSERT INTO AVILAN.UNIDADE (CODIGO, COD_FILIAL) VALUES (1, 1);
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--############################################## INSERE NOVO TIPO DE DIAGRAMA ##########################################
--######################################################################################################################
--######################################################################################################################
INSERT INTO VEICULO_DIAGRAMA (CODIGO, NOME, URL_IMAGEM) VALUES (10, 'TRUCK ELÉTRICO', 'WWW.GOOGLE.COM/TRUCK-ELETRICO');
--######################################################################################################################
--######################################################################################################################


--######################################################################################################################
--######################################################################################################################
--################################## RELATÓRIO DE CRONOGRAMA DAS AFERIÇÕES DE PLACAS ###################################
--######################################################################################################################
--######################################################################################################################
CREATE OR REPLACE FUNCTION FUNC_AFERICAO_RELATORIO_CRONOGRAMA_AFERICOES_PLACAS(F_COD_UNIDADES        BIGINT [],
                                                                               F_DATA_HORA_ATUAL_UTC TIMESTAMP WITH TIME ZONE)
  RETURNS TABLE(
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
    "DIAS DESDE ÚLTIMA AFERIÇÃO PRESSÃO" TEXT)
LANGUAGE PLPGSQL
AS $$
DECLARE
BEGIN
  RETURN QUERY
  WITH DADOS AS (SELECT
                   U.NOME :: TEXT        AS NOME_UNIDADE,
                   V.PLACA :: TEXT       AS PLACA_VEICULO,
                   (SELECT COUNT(VP.COD_PNEU)
                    FROM VEICULO_PNEU VP
                    WHERE VP.PLACA = V.PLACA
                    GROUP BY
                      VP.PLACA) :: TEXT  AS QTD_PNEUS_APLICADOS,
                   MV.NOME :: TEXT       AS NOME_MODELO_VEICULO,
                   VT.NOME :: TEXT       AS NOME_TIPO_VEICULO,
                   TO_CHAR(SULCO.DATA_ULTIMA_AFERICAO_SULCO + (PRU.PERIODO_AFERICAO_SULCO ||
                                                               ' DAYS') :: INTERVAL,
                           'DD/MM/YYYY') AS DATA_VENCIMENTO_SULCO,
                   TO_CHAR(PRESSAO.DATA_ULTIMA_AFERICAO_PRESSAO + (PRU.PERIODO_AFERICAO_PRESSAO ||
                                                                   ' DAYS') :: INTERVAL,
                           'DD/MM/YYYY') AS DATA_VENCIMENTO_PRESSAO,
                   (PRU.PERIODO_AFERICAO_SULCO -
                    SULCO.DIAS) :: TEXT  AS DIAS_VENCIMENTO_SULCO,
                   (PRU.PERIODO_AFERICAO_PRESSAO - PRESSAO.DIAS) :: TEXT
                                         AS DIAS_VENCIMENTO_PRESSAO,
                   SULCO.DIAS :: TEXT    AS DIAS_DESDE_ULTIMA_AFERICAO_SULCO,
                   PRESSAO.DIAS :: TEXT  AS DIAS_DESDE_ULTIMA_AFERICAO_PRESSAO,
                   F_IF(VCTA.PODE_AFERIR_SULCO OR VCTA.PODE_AFERIR_SULCO_PRESSAO, TRUE,
                        FALSE)           AS PODE_AFERIR_SULCO,
                   F_IF(VCTA.PODE_AFERIR_PRESSAO OR VCTA.PODE_AFERIR_SULCO_PRESSAO, TRUE,
                        FALSE)           AS PODE_AFERIR_PRESSAO,
                   F_IF(SULCO.DIAS IS NULL, TRUE,
                        FALSE)           AS SULCO_NUNCA_AFERIDO,
                   F_IF(PRESSAO.DIAS IS NULL, TRUE,
                        FALSE)           AS PRESSAO_NUNCA_AFERIDA,
                   F_IF(SULCO.DIAS > PRU.PERIODO_AFERICAO_SULCO, TRUE,
                        FALSE)           AS AFERICAO_SULCO_VENCIDA,
                   F_IF(PRESSAO.DIAS > PRU.PERIODO_AFERICAO_PRESSAO, TRUE,
                        FALSE)           AS AFERICAO_PRESSAO_VENCIDA
                 FROM VEICULO V
                   JOIN MODELO_VEICULO MV
                     ON MV.CODIGO = V.COD_MODELO
                   JOIN VEICULO_TIPO VT
                     ON VT.CODIGO = V.COD_TIPO
                   JOIN VIEW_AFERICAO_CONFIGURACAO_TIPO_AFERICAO VCTA ON VCTA.COD_TIPO_VEICULO = V.COD_TIPO
                   LEFT JOIN
                   (SELECT
                      A.PLACA_VEICULO                                               AS PLACA_INTERVALO,
                      MAX(A.DATA_HORA AT TIME ZONE
                          TZ_UNIDADE(A.COD_UNIDADE)) :: DATE                        AS DATA_ULTIMA_AFERICAO_PRESSAO,
                      EXTRACT(DAYS FROM (F_DATA_HORA_ATUAL_UTC) - MAX(A.DATA_HORA)) AS DIAS
                    FROM AFERICAO A
                    WHERE A.TIPO_MEDICAO_COLETADA = 'PRESSAO' OR A.TIPO_MEDICAO_COLETADA = 'SULCO_PRESSAO'
                    GROUP BY A.PLACA_VEICULO) AS PRESSAO ON PRESSAO.PLACA_INTERVALO = V.PLACA
                   LEFT JOIN
                   (SELECT
                      A.PLACA_VEICULO                                             AS PLACA_INTERVALO,
                      MAX(A.DATA_HORA AT TIME ZONE
                          TZ_UNIDADE(A.COD_UNIDADE)) :: DATE                      AS DATA_ULTIMA_AFERICAO_SULCO,
                      EXTRACT(DAYS FROM F_DATA_HORA_ATUAL_UTC - MAX(A.DATA_HORA)) AS DIAS
                    FROM AFERICAO A
                    WHERE A.TIPO_MEDICAO_COLETADA = 'SULCO' OR A.TIPO_MEDICAO_COLETADA = 'SULCO_PRESSAO'
                    GROUP BY A.PLACA_VEICULO) AS SULCO ON SULCO.PLACA_INTERVALO = V.PLACA
                   JOIN PNEU_RESTRICAO_UNIDADE PRU
                     ON PRU.COD_UNIDADE = V.COD_UNIDADE
                   JOIN UNIDADE U
                     ON U.CODIGO = V.COD_UNIDADE
                 WHERE V.STATUS_ATIVO = TRUE AND V.COD_UNIDADE = ANY (F_COD_UNIDADES)
                 ORDER BY U.CODIGO ASC, V.PLACA ASC)

  -- TODOS OS COALESCE FICAM AQUI.
  SELECT
    D.NOME_UNIDADE                       AS NOME_UNIDADE,
    D.PLACA_VEICULO                      AS PLACA_VEICULO,
    COALESCE(D.QTD_PNEUS_APLICADOS, '-') AS QTD_PNEUS_APLICADOS,
    D.NOME_MODELO_VEICULO                AS NOME_MODELO_VEICULO,
    D.NOME_TIPO_VEICULO                  AS NOME_TIPO_VEICULO,
    CASE
      WHEN D.SULCO_NUNCA_AFERIDO THEN 'SULCO NUNCA AFERIDO'
      WHEN NOT D.PODE_AFERIR_SULCO THEN 'BLOQUEADO AFERIÇÃO'
      WHEN D.AFERICAO_SULCO_VENCIDA THEN 'VENCIDO'
      ELSE 'NO PRAZO'
    END                                      AS STATUS_SULCO,
    CASE
      WHEN D.PRESSAO_NUNCA_AFERIDA THEN 'PRESSÃO NUNCA AFERIDA'
      WHEN NOT D.PODE_AFERIR_PRESSAO THEN 'BLOQUEADO AFERIÇÃO'
      WHEN D.AFERICAO_PRESSAO_VENCIDA THEN 'VENCIDO'
      ELSE 'NO PRAZO'
    END                                      AS STATUS_PRESSAO,
    F_IF(NOT D.PODE_AFERIR_SULCO OR D.SULCO_NUNCA_AFERIDO,
         '-',
         D.DATA_VENCIMENTO_SULCO)              AS DATA_VENCIMENTO_SULCO,
    F_IF(NOT D.PODE_AFERIR_PRESSAO OR D.PRESSAO_NUNCA_AFERIDA,
         '-',
         D.DATA_VENCIMENTO_PRESSAO)            AS DATA_VENCIMENTO_PRESSAO,
    F_IF(NOT D.PODE_AFERIR_SULCO OR D.SULCO_NUNCA_AFERIDO,
         '-',
         D.DIAS_VENCIMENTO_SULCO)              AS DIAS_VENCIMENTO_SULCO,
    F_IF(NOT D.PODE_AFERIR_PRESSAO OR D.PRESSAO_NUNCA_AFERIDA,
         '-',
         D.DIAS_VENCIMENTO_PRESSAO)            AS DIAS_VENCIMENTO_PRESSAO,
    F_IF(NOT D.PODE_AFERIR_SULCO OR D.SULCO_NUNCA_AFERIDO,
         '-',
         D.DIAS_DESDE_ULTIMA_AFERICAO_SULCO)   AS DIAS_DESDE_ULTIMA_AFERICAO_SULCO,
    F_IF(NOT D.PODE_AFERIR_PRESSAO OR D.PRESSAO_NUNCA_AFERIDA,
         '-',
         D.DIAS_DESDE_ULTIMA_AFERICAO_PRESSAO) AS DIAS_DESDE_ULTIMA_AFERICAO_PRESSAO
  FROM DADOS D;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--################################### INSERTS PARA O NOVO COMPONENTE DA DASH ###########################################
--################################## QUANTIDADE DE DIAS VENCIDOS - AFERIÇÕES ###########################################
--######################################################################################################################
--######################################################################################################################
INSERT INTO DASHBOARD_COMPONENTE (
  CODIGO,
  TITULO,
  DESCRICAO,
  QTD_BLOCOS_HORIZONTAIS,
  QTD_BLOCOS_VERTICAIS,
  DATA_HORA_CRIACAO,
  DATA_HORA_ULTIMA_ALTERACAO,
  COD_PILAR_PROLOG_COMPONENTE,
  COD_TIPO_COMPONENTE,
  URL_ENDPOINT_DADOS)
VALUES (
  18,
  'Placas com Aferição Vencida',
  'Mostra as placas com Aferição vencida e quantos dias que está vencida',
  2,
  1,
  NOW(),
  NOW(),
  1,
  5,
  '/dashboards/pneus/quantidade-dias-afericoes-vencidas');

INSERT INTO DASHBOARD_COMPONENTE_FUNCAO_PROLOG (
  COD_COMPONENTE,
  COD_FUNCAO_PROLOG,
  COD_PILAR_PROLOG)
VALUES (
  18,
  110,
  1);
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--###################################### QUANTIDADE DE DIAS VENCIDOS - AFERIÇÕES  ######################################
--######################################################################################################################
--######################################################################################################################
CREATE OR REPLACE FUNCTION FUNC_AFERICAO_RELATORIO_QTD_DIAS_AFERICAO_VENCIDA(
  F_COD_UNIDADES  BIGINT[],
  F_DATA_HOJE_UTC TIMESTAMPTZ)
  RETURNS TABLE(
    UNIDADE                       TEXT,
    PLACA                         VARCHAR,
    "QTD_DIAS_SEM_AFERIR_SULCO"   INTEGER,
    "QTD_DIAS_SEM_AFERIR_PRESSAO" INTEGER
  )
LANGUAGE PLPGSQL
AS $$
BEGIN
  RETURN QUERY
  WITH DIAS_SEM_AFERIR AS(
      SELECT
        U.NOME::TEXT            AS UNIDADE,
        A.PLACA_VEICULO ,
        DATE_PART('DAY', F_DATA_HOJE_UTC - MAX(A.DATA_HORA)) - (PRU.PERIODO_AFERICAO_SULCO)
                                AS QTD_DIAS_SEM_AFERIR_SULCO,
        DATE_PART('DAY', F_DATA_HOJE_UTC - MAX(A.DATA_HORA)) - (PRU.PERIODO_AFERICAO_PRESSAO)
                                AS QTD_DIAS_SEM_AFERIR_PRESSAO
      FROM AFERICAO A
        JOIN UNIDADE U ON A.COD_UNIDADE = U.CODIGO
        JOIN PNEU_RESTRICAO_UNIDADE PRU ON PRU.COD_UNIDADE = A.COD_UNIDADE
        JOIN VEICULO V ON V.COD_UNIDADE = A.COD_UNIDADE AND A.PLACA_VEICULO = V.PLACA
      WHERE V.COD_UNIDADE = ANY(F_COD_UNIDADES)
      GROUP BY U.NOME, A.PLACA_VEICULO, V.COD_UNIDADE, PRU.PERIODO_AFERICAO_SULCO, PRU.PERIODO_AFERICAO_PRESSAO
  )
  SELECT
    DSA.UNIDADE,
    DSA.PLACA_VEICULO,
    DSA.QTD_DIAS_SEM_AFERIR_SULCO  :: INTEGER AS SULCO,
    DSA.QTD_DIAS_SEM_AFERIR_PRESSAO  :: INTEGER AS PRESSAO
  FROM DIAS_SEM_AFERIR DSA
  WHERE
    -- REMOVE PLACAS QUE NÃO ESTÃO COM O PRAZO VENCIDO
    F_IF(DSA.QTD_DIAS_SEM_AFERIR_SULCO > 0, DSA.QTD_DIAS_SEM_AFERIR_SULCO = DSA.QTD_DIAS_SEM_AFERIR_SULCO , FALSE) AND
    F_IF(DSA.QTD_DIAS_SEM_AFERIR_PRESSAO > 0, DSA.QTD_DIAS_SEM_AFERIR_PRESSAO = DSA.QTD_DIAS_SEM_AFERIR_PRESSAO , FALSE)
  ORDER BY DSA.QTD_DIAS_SEM_AFERIR_PRESSAO DESC, DSA.QTD_DIAS_SEM_AFERIR_SULCO DESC;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--################################### INSERTS PARA O NOVO COMPONENTE DA DASH ###########################################
--###################################### CHECKS REALIZADOS EM MENOS DE 1:30 ############################################
--######################################################################################################################
--######################################################################################################################
INSERT INTO DASHBOARD_COMPONENTE (
  CODIGO,
  TITULO,
  DESCRICAO,
  QTD_BLOCOS_HORIZONTAIS,
  QTD_BLOCOS_VERTICAIS,
  DATA_HORA_CRIACAO,
  DATA_HORA_ULTIMA_ALTERACAO,
  COD_PILAR_PROLOG_COMPONENTE,
  COD_TIPO_COMPONENTE,
  URL_ENDPOINT_DADOS)
VALUES (
  17,
  'Checklists realizados em menos de 1 minuto e 30 segundos',
  'Mostra a quantidade de checklists realizados em menos de 1 minuto e 30 segundos',
  2,
  1,
  NOW(),
  NOW(),
  1,
  5,
  '/dashboards/checklists/quantidade-checks-realizados-abaixo-de-1-30');

INSERT INTO DASHBOARD_COMPONENTE_FUNCAO_PROLOG (
  COD_COMPONENTE,
  COD_FUNCAO_PROLOG,
  COD_PILAR_PROLOG)
VALUES (
  17,
  121,
  1);
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--################## RELATÓRIO DE CHECKLISTS REALIZADOS ABAIXO DE UM DETERMINADO TEMPO DE REALIZAÇÃO ###################
--######################################################################################################################
--######################################################################################################################
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_RELATORIO_CHECKS_REALIZADOS_ABAIXO_TEMPO_DEFINIDO(
  F_COD_UNIDADES                  BIGINT[],
  F_TEMPO_REALIZACAO_MILLIS       BIGINT,
  F_DATA_HOJE_UTC                 DATE,
  F_DIAS_RETROATIVOS_PARA_BUSCAR  BIGINT)
  RETURNS TABLE(
    UNIDADE                                                             TEXT,
    NOME                                                                TEXT,
    "QUANTIDADE CHECKLISTS REALIZADOS ABAIXO TEMPO ESPECIFICO"          BIGINT,
    "QUANTIDADE CHECKLISTS REALIZADOS"                                  BIGINT
  )
LANGUAGE PLPGSQL
AS $$
DECLARE
  DATA_INICIAL       DATE := F_DATA_HOJE_UTC + INTERVAL '1' DAY - (INTERVAL '1' DAY * F_DIAS_RETROATIVOS_PARA_BUSCAR);
  DATA_FINAL         DATE := F_DATA_HOJE_UTC + INTERVAL '1' DAY;
BEGIN
  RETURN QUERY

WITH REALIZADOS_DETERMINADO_INTERVALO AS (SELECT
  CL.COD_UNIDADE,
  CL.CPF_COLABORADOR,
  COUNT(CL.CPF_COLABORADOR)
    FILTER (WHERE TEMPO_REALIZACAO < F_TEMPO_REALIZACAO_MILLIS) AS REALIZADOS_ABAIXO_TEMPO_DEFINIDO,
  COUNT(CL.CPF_COLABORADOR) AS REALIZADOS
FROM CHECKLIST CL
WHERE CL.COD_UNIDADE = ANY (F_COD_UNIDADES)
  AND (CL.DATA_HORA AT TIME ZONE TZ_UNIDADE(CL.COD_UNIDADE)) :: DATE BETWEEN DATA_INICIAL AND DATA_FINAL
GROUP BY CL.CPF_COLABORADOR, CL.COD_UNIDADE
)

SELECT
  U.NOME            :: TEXT             AS NOME_UNIDADE,
  CO.NOME           :: TEXT             AS NOME_COLABORADOR,
  RDI.REALIZADOS_ABAIXO_TEMPO_DEFINIDO,
  RDI.REALIZADOS
FROM
  REALIZADOS_DETERMINADO_INTERVALO RDI
  JOIN UNIDADE U ON U.CODIGO = RDI.COD_UNIDADE
  JOIN COLABORADOR CO ON CO.CPF = RDI.CPF_COLABORADOR
  WHERE RDI.REALIZADOS_ABAIXO_TEMPO_DEFINIDO > F_TEMPO_REALIZACAO_MILLIS
ORDER BY RDI.REALIZADOS_ABAIXO_TEMPO_DEFINIDO DESC, CO.NOME ASC;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--################################### INSERTS PARA O NOVO COMPONENTE DA DASH ###########################################
--############################### RELATOS PENDENTES SEPARADOS POR TIPO - RELATOS #######################################
--######################################################################################################################
--######################################################################################################################
INSERT INTO DASHBOARD_COMPONENTE (
  CODIGO,
  TITULO,
  SUBTITULO,
  DESCRICAO,
  QTD_BLOCOS_HORIZONTAIS,
  QTD_BLOCOS_VERTICAIS,
  DATA_HORA_CRIACAO,
  DATA_HORA_ULTIMA_ALTERACAO,
  COD_PILAR_PROLOG_COMPONENTE,
  COD_TIPO_COMPONENTE,
  URL_ENDPOINT_DADOS)
VALUES (
  20,
  'Quantidade de relatos pendentes',
  'Separado por status',
  'Mostra a quantidade de relatos que estão pendentes de classificação ou fechamento',
  1,
  1,
  NOW(),
  NOW(),
  2,
  1,
  '/dashboards/relatos/quantidade-relatos-pendentes-por-status');

INSERT INTO DASHBOARD_COMPONENTE_FUNCAO_PROLOG (
  COD_COMPONENTE,
  COD_FUNCAO_PROLOG,
  COD_PILAR_PROLOG)
VALUES (
  20,
  26,
  2);
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--################################ QUANTIDADE DE RELATOS PENDENTES SEPARADOS POR STATUS ################################
--################################ STATUS: PENDENTE_CLASSIFICACAO / PENDENTE_FECHAMENTO ################################
--######################################################################################################################
--######################################################################################################################
CREATE OR REPLACE FUNCTION FUNC_RELATO_RELATORIO_QTD_RELATOS_PENDENTES_BY_STATUS(
  F_COD_UNIDADES                  BIGINT[])
RETURNS TABLE(
  "QTD_PENDENTES_CLASSIFICACAO"        BIGINT,
  "QTD_PENDENTES_FECHAMENTO"           BIGINT
  )
LANGUAGE PLPGSQL
AS $$
DECLARE
  RELATO_PENDENTE_CLASSIFICACAO     VARCHAR := 'PENDENTE_CLASSIFICACAO';
  RELATO_PENDENTE_FECHAMENTO        VARCHAR := 'PENDENTE_FECHAMENTO';
BEGIN
  RETURN QUERY
SELECT
  COUNT(R.STATUS)
    FILTER (WHERE R.STATUS = RELATO_PENDENTE_CLASSIFICACAO) AS QTD_PENDENTES_CLASSIFICACAO,
  COUNT(R.STATUS)
    FILTER (WHERE R.STATUS = RELATO_PENDENTE_FECHAMENTO) AS QTD_PENDENTES_FECHAMENTO
FROM RELATO R
    WHERE
      R.COD_UNIDADE = ANY (F_COD_UNIDADES);
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--################################### INSERTS PARA O NOVO COMPONENTE DA DASH ###########################################
--############################### QUANTIDADE DE AFERIÇÕES REALIZADAS - AFERIÇÕES #######################################
--######################################################################################################################
--######################################################################################################################
INSERT INTO DASHBOARD_COMPONENTE (
  CODIGO,
  TITULO,
  DESCRICAO,
  QTD_BLOCOS_HORIZONTAIS,
  QTD_BLOCOS_VERTICAIS,
  DATA_HORA_CRIACAO,
  DATA_HORA_ULTIMA_ALTERACAO,
  COD_PILAR_PROLOG_COMPONENTE,
  COD_TIPO_COMPONENTE,
  URL_ENDPOINT_DADOS,
  LABEL_EIXO_X,
  LABEL_EIXO_Y)
VALUES (
  19,
  'Quantidade de aferições realizadas',
  'Mostra a quantidade de aferições realizadas separando por tipo',
  2,
  1,
  NOW(),
  NOW(),
  1,
  7,
  '/dashboards/pneus/quantidade-afericoes-por-tipo-ultimos-30-dias',
  'Dias',
  'Quantidade de Aferições');

INSERT INTO DASHBOARD_COMPONENTE_FUNCAO_PROLOG (
  COD_COMPONENTE,
  COD_FUNCAO_PROLOG,
  COD_PILAR_PROLOG)
VALUES (
  19,
  121,
  1);
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--########################### QUANTIDADE DE AFERIÇÕES REALIZADAS EM UM DETERMINADO PERÍODO #############################
--######################################################################################################################
--######################################################################################################################
CREATE OR REPLACE FUNCTION FUNC_AFERICAO_RELATORIO_QTD_AFERICOES_REALIZADAS_POR_DIA(
  F_COD_UNIDADES                  BIGINT[],
  F_DATA_HOJE_UTC                 TIMESTAMPTZ,
  F_DIAS_RETROATIVOS_PARA_BUSCAR  BIGINT)
RETURNS TABLE(
    DATA                               DATE,
    DATA_FORMATADA                     TEXT,
    QTD_AFERICAO_SULCO                 BIGINT,
    QTD_AFERICAO_PRESSAO               BIGINT,
    QTD_AFERICAO_SULCO_PRESSAO         BIGINT
  )
LANGUAGE PLPGSQL
AS $$
DECLARE
  DATA_INICIAL            DATE := F_DATA_HOJE_UTC + INTERVAL '1' DAY
                                  - (INTERVAL '1' DAY * F_DIAS_RETROATIVOS_PARA_BUSCAR);
  DATA_FINAL              DATE := F_DATA_HOJE_UTC + INTERVAL '1' DAY;
  AFERICAO_SULCO          VARCHAR := 'SULCO';
  AFERICAO_PRESSAO        VARCHAR := 'PRESSAO';
  AFERICAO_SULCO_PRESSAO  VARCHAR := 'SULCO_PRESSAO';
BEGIN
  RETURN QUERY

  WITH DIAS AS (
      SELECT G.DAY :: DATE AS DATA
      FROM GENERATE_SERIES(DATA_INICIAL, DATA_FINAL, '1 DAY') G(DAY)
      ORDER BY DATA
  ),

  AFERICOES_DIA AS (
    SELECT
      (A.DATA_HORA AT TIME ZONE TZ_UNIDADE(A.COD_UNIDADE)) :: DATE AS DATA,
      SUM(CASE WHEN A.TIPO_MEDICAO_COLETADA = AFERICAO_SULCO THEN 1 ELSE 0 END)  QTD_AFERICAO_SULCO,
      SUM(CASE WHEN A.TIPO_MEDICAO_COLETADA = AFERICAO_PRESSAO THEN 1 ELSE 0 END)  QTD_AFERICAO_PRESSAO,
      SUM(CASE WHEN A.TIPO_MEDICAO_COLETADA = AFERICAO_SULCO_PRESSAO THEN 1 ELSE 0 END)  QTD_AFERICAO_SULCO_PRESSAO
    FROM AFERICAO A
    WHERE
      A.COD_UNIDADE = ANY (F_COD_UNIDADES)
      AND (A.DATA_HORA AT TIME ZONE TZ_UNIDADE(A.COD_UNIDADE)) :: DATE BETWEEN DATA_INICIAL AND DATA_FINAL
    GROUP BY DATA
    ORDER BY DATA
  )

  SELECT
    D.DATA                          AS DATA,
    TO_CHAR(D.DATA, 'DD/MM')        AS DATA_FORMATADA,
    AD.QTD_AFERICAO_SULCO           AS QTD_AFERICAO_SULCO,
    AD.QTD_AFERICAO_PRESSAO         AS QTD_AFERICAO_PRESSAO,
    AD.QTD_AFERICAO_SULCO_PRESSAO   AS QTD_AFERICAO_SULCO_PRESSAO
  FROM DIAS D
    LEFT JOIN AFERICOES_DIA AD ON D.DATA = AD.DATA
  ORDER BY D.DATA;
END;
$$;
--######################################################################################################################
--######################################################################################################################


--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--########################################### FIM OUTRAS MIGRAÇÕES SIMPLES  ############################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
END TRANSACTION ;