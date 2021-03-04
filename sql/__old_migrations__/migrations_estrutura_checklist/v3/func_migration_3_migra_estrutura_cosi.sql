create or replace function migration_checklist.func_migration_3_migra_estrutura_cosi()
    returns void
    language plpgsql
as
$func$
begin
    --######################################################################################################################
    -- Renomeia as colunas atuais.
    alter table checklist_ordem_servico_itens_data
        rename column cod_pergunta to cod_pergunta_primeiro_apontamento;
    alter table checklist_ordem_servico_itens_data
        rename column cod_alternativa to cod_alternativa_primeiro_apontamento;

    -- Cria colunas para referenciarmos os códigos de contexto das perguntas e alternativas.
    -- Isso vai nos ajudar a criar a unique que impedirá dois itens iguais, em aberto, da mesma alternativa.
    alter table checklist_ordem_servico_itens_data
        add column cod_contexto_pergunta bigint;
    alter table checklist_ordem_servico_itens_data
        add column cod_contexto_alternativa bigint;

    -- Remove constraints para o update funcionar.
    alter table checklist_ordem_servico_itens_data
        drop constraint check_data_hora_inicio_resolucao_not_null;
    alter table checklist_ordem_servico_itens_data
        drop constraint check_data_hora_fim_resolucao_not_null;

    update checklist_ordem_servico_itens_data
    set cod_contexto_pergunta = (select cpd.codigo_contexto
                                 from checklist_perguntas_data cpd
                                 where cpd.codigo = cod_pergunta_primeiro_apontamento);
    update checklist_ordem_servico_itens_data
    set cod_contexto_alternativa = (select capd.codigo_contexto
                                    from checklist_alternativa_pergunta_data capd
                                    where capd.codigo = cod_alternativa_primeiro_apontamento);
    --######################################################################################################################
    -- PL-2438
    -- Corrige problema da COSI.
    -- Na migração da versão dos modelos, o estado atual de cada modelo virou a última versão. Essa última versão nunca
    -- teve checks realizados e não tem entradas na CHECKLIST_RESPOSTAS_NOK (CRN). Porém, checks antigos já abriram itens de
    -- OS dessa versão. Pois antes só existia ela. Então, chegamos em um cenário onde um código de
    -- alternativa/pergunta estava na COSI mas não na CRN. Os updates abaixo corrigem isso tanto para pergguntas quanto
    -- para alternativas.
    -- Corrige alternativas.
    create table migration_checklist.migra_cosi_aux_alternativa (
        codigo_cosi bigint,
        codigo_alternativa_novo bigint
    );

    create index idx_migra_cosi_aux_codigo_cosi_alternativa1 on migration_checklist.migra_cosi_aux_alternativa (codigo_cosi);
    create index idx_migra_cosi_aux_codigo_alternativa_novo1 on migration_checklist.migra_cosi_aux_alternativa (codigo_alternativa_novo);

    insert into migration_checklist.migra_cosi_aux_alternativa (codigo_cosi, codigo_alternativa_novo)
    select cosid.codigo as codigo_cosid,
           aux.cod_alternativa_novo
    from migration_checklist.check_alternativas_aux aux
             join checklist_ordem_servico_itens_data cosid
                  on aux.cod_alternativa_antigo = cosid.cod_alternativa_primeiro_apontamento
                      and aux.cod_modelo_versao = (select cd.cod_versao_checklist_modelo
                                                   from checklist_ordem_servico_data cosd
                                                            join checklist_data cd on cosd.cod_checklist = cd.codigo
                                                   where cosd.codigo = cosid.cod_os
                                                     and cosd.cod_unidade = cosid.cod_unidade);

    update checklist_ordem_servico_itens_data
    set cod_alternativa_primeiro_apontamento = (select codigo_alternativa_novo
                                                from migration_checklist.migra_cosi_aux_alternativa
                                                where codigo_cosi = codigo);
    -- Fim alternativas.


    -- Corrige perguntas.
    create table migration_checklist.migra_cosi_aux_pergunta (
        codigo_cosi bigint,
        codigo_pergunta_novo bigint
    );

    create index idx_migra_cosi_aux_codigo_cosi_pergunta1 on migration_checklist.migra_cosi_aux_pergunta (codigo_cosi);
    create index idx_migra_cosi_aux_codigo_pergunta_novo1 on migration_checklist.migra_cosi_aux_pergunta (codigo_pergunta_novo);

    insert into migration_checklist.migra_cosi_aux_pergunta (codigo_cosi, codigo_pergunta_novo)
    select cosid.codigo as codigo_cosid,
           aux.cod_pergunta_novo
    from migration_checklist.check_perguntas_aux aux
             join checklist_ordem_servico_itens_data cosid
                  on aux.cod_pergunta_antigo = cosid.cod_pergunta_primeiro_apontamento
                      and aux.cod_modelo_versao = (select cd.cod_versao_checklist_modelo
                                                   from checklist_ordem_servico_data cosd
                                                            join checklist_data cd on cosd.cod_checklist = cd.codigo
                                                   where cosd.codigo = cosid.cod_os
                                                     and cosd.cod_unidade = cosid.cod_unidade);


    update checklist_ordem_servico_itens_data
    set cod_pergunta_primeiro_apontamento = (select codigo_pergunta_novo
                                                from migration_checklist.migra_cosi_aux_pergunta
                                                where codigo_cosi = codigo);
    -- Fim perguntas.
    --######################################################################################################################

    -- Recria as constraints após o update.
    alter table checklist_ordem_servico_itens_data
        add constraint check_data_hora_inicio_resolucao_not_null check (
                deletado or ((data_hora_conserto is not null and data_hora_inicio_resolucao is not null)
                or (data_hora_conserto is null and data_hora_inicio_resolucao is null))) not valid;
    alter table checklist_ordem_servico_itens_data
        add constraint check_data_hora_fim_resolucao_not_null check (
                deletado or ((data_hora_conserto is not null and data_hora_fim_resolucao is not null)
                or (data_hora_conserto is null and data_hora_fim_resolucao is null))) not valid;
    comment on constraint check_data_hora_inicio_resolucao_not_null
        on checklist_ordem_servico_itens_data
        is 'Constraint para impedir que novas linhas adicionadas tenham a DATA_HORA_INICIO_RESOLUCAO nula.
    Ela foi criada usando NOT VALID para pular a verificação das linhas já existentes.
    Além disso, a verificação é ignorada para linhas deletadas, desse modo podemos deletar itens antigos que não
    têm essa informação salva.';
    comment on constraint check_data_hora_fim_resolucao_not_null
        on checklist_ordem_servico_itens_data
        is 'Constraint para impedir que novas linhas adicionadas tenham a DATA_HORA_FIM_RESOLUCAO nula.
    Ela foi criada usando NOT VALID para pular a verificação das linhas já existentes.
    Além disso, a verificação é ignorada para linhas deletadas, desse modo podemos deletar itens antigos que não
    têm essa informação salva.';


    alter table checklist_ordem_servico_itens_data
        drop constraint fk_checklist_ordem_servico_itens_perguntas;
    alter table checklist_ordem_servico_itens_data
        drop constraint fk_checklist_ordem_servico_itens_alternativa_pergunta;

    alter table checklist_ordem_servico_itens_data
        add constraint fk_checklist_ordem_servico_itens_perguntas
            foreign key (cod_contexto_pergunta, cod_pergunta_primeiro_apontamento)
                references checklist_perguntas_data (codigo_contexto, codigo);
    alter table checklist_ordem_servico_itens_data
        add constraint fk_checklist_ordem_servico_itens_alternativa_pergunta
            foreign key (cod_contexto_alternativa, cod_alternativa_primeiro_apontamento)
                references checklist_alternativa_pergunta_data (codigo_contexto, codigo);
    --######################################################################################################################
    -- Cria a tabela que armazena quais checklists abriram itens de O.S.
    CREATE TABLE IF NOT EXISTS CHECKLIST_ORDEM_SERVICO_ITENS_APONTAMENTOS
    (
        CODIGO BIGSERIAL NOT NULL
            CONSTRAINT PK_CHECKLIST_APONTAMENTOS
                PRIMARY KEY,
        COD_ITEM_ORDEM_SERVICO BIGINT NOT NULL
            CONSTRAINT FK_CHECKLIST_APONTAMENTOS_ITEM_ORDEM_SERVICO
                REFERENCES CHECKLIST_ORDEM_SERVICO_ITENS_DATA (CODIGO),
        COD_CHECKLIST_REALIZADO BIGINT NOT NULL
            CONSTRAINT FK_CHECKLIST_APONTAMENTOS_CHECKLIST
                REFERENCES CHECKLIST_DATA (CODIGO),
        COD_ALTERNATIVA BIGINT NOT NULL
            CONSTRAINT FK_CHECKLIST_ALTERNATIVA
                REFERENCES CHECKLIST_ALTERNATIVA_PERGUNTA_DATA (CODIGO),
        NOVA_QTD_APONTAMENTOS INTEGER NOT NULL,
        CONSTRAINT UNICA_QTD_APONTAMENTOS_POR_ITEM_ORDEM_SERVICO
            UNIQUE (COD_ITEM_ORDEM_SERVICO, NOVA_QTD_APONTAMENTOS),
        CONSTRAINT UNICA_ALTERNATIVA_POR_CHECKLIST
            UNIQUE (COD_CHECKLIST_REALIZADO, COD_ALTERNATIVA),
        CONSTRAINT UNICO_ITEM_ORDEM_SERVICO_POR_CHECKLIST
            UNIQUE (COD_CHECKLIST_REALIZADO, COD_ITEM_ORDEM_SERVICO)
    );

    CREATE INDEX IDX_CHECK_ITENS_APONTAMENTOS_COD_ITEM_OS ON CHECKLIST_ORDEM_SERVICO_ITENS_APONTAMENTOS (COD_ITEM_ORDEM_SERVICO);

    COMMENT ON TABLE CHECKLIST_ORDEM_SERVICO_ITENS_APONTAMENTOS IS 'Salva os apontamentos que houveram nos itens abertos de OS.
        Cada checklist é responsável por incrementar um apontamento de um item já em aberto, com essa tabela conseguiremos
        saber quais checklists foram responsáveis por cada apontamento do item de OS.';
    --######################################################################################################################

    --######################################################################################################################
    -- Alterado colunas COD_ALTERNATIVA e COD_PERGUNTA para COD_ALTERNATIVA_PRIMEIRO_APONTAMENTO e
    -- COD_PERGUNTA_PRIMEIRO_APONTAMENTO, respectivamente.
    drop view checklist_ordem_servico_itens;
    CREATE OR REPLACE VIEW CHECKLIST_ORDEM_SERVICO_ITENS AS
      SELECT
        COSI.COD_UNIDADE,
        COSI.CODIGO,
        COSI.COD_OS,
        COSI.CPF_MECANICO,
        COSI.COD_PERGUNTA_PRIMEIRO_APONTAMENTO,
        COSI.COD_CONTEXTO_PERGUNTA,
        COSI.COD_CONTEXTO_ALTERNATIVA,
        COSI.COD_ALTERNATIVA_PRIMEIRO_APONTAMENTO,
        COSI.STATUS_RESOLUCAO,
        COSI.QT_APONTAMENTOS,
        COSI.KM,
        COSI.DATA_HORA_CONSERTO,
        COSI.DATA_HORA_INICIO_RESOLUCAO,
        COSI.DATA_HORA_FIM_RESOLUCAO,
        COSI.TEMPO_REALIZACAO,
        COSI.FEEDBACK_CONSERTO
      FROM CHECKLIST_ORDEM_SERVICO_ITENS_DATA COSI
      WHERE COSI.DELETADO = FALSE;
    --######################################################################################################################

    --######################################################################################################################
    -- Altera JOIN para utilizar colunas de novo nome da tabela COSI e passa a usar CHECKLIST_RESPOSTAS_NOK ao invés
    -- de CHECKLIST_RESPOSTAS.
    -- Também altera um F_IF para um CASE por questões de otimização.
    CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_OS_GET_ORDEM_SERVICO_RESOLUCAO(
      F_COD_UNIDADE         BIGINT,
      F_COD_OS              BIGINT,
      F_DATA_HORA_ATUAL_UTC TIMESTAMP WITH TIME ZONE)
      RETURNS TABLE(
        PLACA_VEICULO                         TEXT,
        KM_ATUAL_VEICULO                      BIGINT,
        COD_OS                                BIGINT,
        COD_UNIDADE_OS                        BIGINT,
        STATUS_OS                             TEXT,
        DATA_HORA_ABERTURA_OS                 TIMESTAMP WITHOUT TIME ZONE,
        DATA_HORA_FECHAMENTO_OS               TIMESTAMP WITHOUT TIME ZONE,
        COD_ITEM_OS                           BIGINT,
        COD_UNIDADE_ITEM_OS                   BIGINT,
        DATA_HORA_PRIMEIRO_APONTAMENTO_ITEM   TIMESTAMP WITHOUT TIME ZONE,
        STATUS_ITEM_OS                        TEXT,
        PRAZO_RESOLUCAO_ITEM_HORAS            INTEGER,
        PRAZO_RESTANTE_RESOLUCAO_ITEM_MINUTOS BIGINT,
        QTD_APONTAMENTOS                      INTEGER,
        COD_COLABORADOR_RESOLUCAO             BIGINT,
        NOME_COLABORADOR_RESOLUCAO            TEXT,
        DATA_HORA_RESOLUCAO                   TIMESTAMP WITHOUT TIME ZONE,
        DATA_HORA_INICIO_RESOLUCAO            TIMESTAMP WITHOUT TIME ZONE,
        DATA_HORA_FIM_RESOLUCAO               TIMESTAMP WITHOUT TIME ZONE,
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
    as $$
    BEGIN
      RETURN QUERY
      SELECT
        C.PLACA_VEICULO :: TEXT                                                AS PLACA_VEICULO,
        V.KM                                                                   AS KM_ATUAL_VEICULO,
        COS.CODIGO                                                             AS COD_OS,
        COS.COD_UNIDADE                                                        AS COD_UNIDADE_OS,
        COS.STATUS :: TEXT                                                     AS STATUS_OS,
        C.DATA_HORA_REALIZACAO_TZ_APLICADO                                     AS DATA_HORA_ABERTURA_OS,
        COS.DATA_HORA_FECHAMENTO AT TIME ZONE TZ_UNIDADE(F_COD_UNIDADE)        AS DATA_HORA_FECHAMENTO_OS,
        COSI.CODIGO                                                            AS COD_ITEM_OS,
        COS.COD_UNIDADE                                                        AS COD_UNIDADE_ITEM_OS,
        C.DATA_HORA_REALIZACAO_TZ_APLICADO                                     AS DATA_HORA_PRIMEIRO_APONTAMENTO_ITEM,
        COSI.STATUS_RESOLUCAO                                                  AS STATUS_ITEM_OS,
        PRIO.PRAZO                                                             AS PRAZO_RESOLUCAO_ITEM_HORAS,
        TO_MINUTES_TRUNC((C.DATA_HORA
                          + (PRIO.PRAZO || ' HOURS') :: INTERVAL)
                         - F_DATA_HORA_ATUAL_UTC)                              AS PRAZO_RESTANTE_RESOLUCAO_ITEM_MINUTOS,
        COSI.QT_APONTAMENTOS                                                   AS QTD_APONTAMENTOS,
        CO.CODIGO                                                              AS COD_COLABORADOR_RESOLUCAO,
        CO.NOME :: TEXT                                                        AS NOME_COLABORADOR_RESOLUCAO,
        COSI.DATA_HORA_CONSERTO AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)         AS DATA_HORA_RESOLUCAO,
        COSI.DATA_HORA_INICIO_RESOLUCAO AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE) AS DATA_HORA_INICIO_RESOLUCAO,
        COSI.DATA_HORA_FIM_RESOLUCAO AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)    AS DATA_HORA_FIM_RESOLUCAO,
        COSI.FEEDBACK_CONSERTO                                                 AS FEEDBACK_RESOLUCAO,
        MILLIS_TO_MINUTES(COSI.TEMPO_REALIZACAO)                               AS DURACAO_RESOLUCAO_MINUTOS,
        COSI.KM                                                                AS KM_VEICULO_COLETADO_RESOLUCAO,
        CP.CODIGO                                                              AS COD_PERGUNTA,
        CP.PERGUNTA                                                            AS DESCRICAO_PERGUNTA,
        CAP.CODIGO                                                             AS COD_ALTERNATIVA,
        CAP.ALTERNATIVA                                                        AS DESCRICAO_ALTERNATIVA,
        CAP.ALTERNATIVA_TIPO_OUTROS                                            AS ALTERNATIVA_TIPO_OUTROS,
        CASE
            WHEN CAP.ALTERNATIVA_TIPO_OUTROS
                THEN
                (SELECT CRN.RESPOSTA_OUTROS
                 FROM CHECKLIST_RESPOSTAS_NOK CRN
                 WHERE CRN.COD_CHECKLIST = C.CODIGO
                   AND CRN.COD_ALTERNATIVA = CAP.CODIGO) :: TEXT
            ELSE NULL
        END                                                                    AS DESCRICAO_TIPO_OUTROS,
        CAP.PRIORIDADE :: TEXT                                                 AS PRIORIDADE_ALTERNATIVA
      FROM CHECKLIST C
        JOIN CHECKLIST_ORDEM_SERVICO COS
          ON C.CODIGO = COS.COD_CHECKLIST
        JOIN CHECKLIST_ORDEM_SERVICO_ITENS COSI
          ON COS.CODIGO = COSI.COD_OS
                 AND COS.COD_UNIDADE = COSI.COD_UNIDADE
        JOIN CHECKLIST_PERGUNTAS CP
          ON COSI.COD_PERGUNTA_PRIMEIRO_APONTAMENTO = CP.CODIGO
        JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
          ON COSI.COD_ALTERNATIVA_PRIMEIRO_APONTAMENTO = CAP.CODIGO
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
    --######################################################################################################################

    --######################################################################################################################
    -- Altera JOIN para utilizar colunas de novo nome da tabela COSI e passa a usar CHECKLIST_RESPOSTAS_NOK ao invés
    -- de CHECKLIST_RESPOSTAS.
    -- Também altera um F_IF para um CASE por questões de otimização.
    CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_OS_GET_ITENS_RESOLUCAO(
      F_COD_UNIDADE            BIGINT,
      F_COD_OS                 BIGINT,
      F_PLACA_VEICULO          TEXT,
      F_PRIORIDADE_ALTERNATIVA TEXT,
      F_STATUS_ITENS           TEXT,
      F_DATA_HORA_ATUAL_UTC    TIMESTAMP WITH TIME ZONE,
      F_LIMIT                  INTEGER,
      F_OFFSET                 INTEGER)
      RETURNS TABLE(
        PLACA_VEICULO                         TEXT,
        KM_ATUAL_VEICULO                      BIGINT,
        COD_OS                                BIGINT,
        COD_UNIDADE_ITEM_OS                   BIGINT,
        COD_ITEM_OS                           BIGINT,
        DATA_HORA_PRIMEIRO_APONTAMENTO_ITEM   TIMESTAMP WITHOUT TIME ZONE,
        STATUS_ITEM_OS                        TEXT,
        PRAZO_RESOLUCAO_ITEM_HORAS            INTEGER,
        PRAZO_RESTANTE_RESOLUCAO_ITEM_MINUTOS BIGINT,
        QTD_APONTAMENTOS                      INTEGER,
        COD_COLABORADOR_RESOLUCAO             BIGINT,
        NOME_COLABORADOR_RESOLUCAO            TEXT,
        DATA_HORA_RESOLUCAO                   TIMESTAMP WITHOUT TIME ZONE,
        DATA_HORA_INICIO_RESOLUCAO            TIMESTAMP WITHOUT TIME ZONE,
        DATA_HORA_FIM_RESOLUCAO               TIMESTAMP WITHOUT TIME ZONE,
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
            C.PLACA_VEICULO :: TEXT                                                AS PLACA_VEICULO,
            V.KM                                                                   AS KM_ATUAL_VEICULO,
            COS.CODIGO                                                             AS COD_OS,
            COS.COD_UNIDADE                                                        AS COD_UNIDADE_ITEM_OS,
            COSI.CODIGO                                                            AS COD_ITEM_OS,
            C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)                     AS DATA_HORA_PRIMEIRO_APONTAMENTO_ITEM,
            COSI.STATUS_RESOLUCAO                                                  AS STATUS_ITEM_OS,
            PRIO.PRAZO                                                             AS PRAZO_RESOLUCAO_ITEM_HORAS,
            TO_MINUTES_TRUNC((C.DATA_HORA
                              + (PRIO.PRAZO || ' HOURS') :: INTERVAL)
                             - F_DATA_HORA_ATUAL_UTC)                              AS PRAZO_RESTANTE_RESOLUCAO_ITEM_MINUTOS,
            COSI.QT_APONTAMENTOS                                                   AS QTD_APONTAMENTOS,
            CO.CODIGO                                                              AS COD_COLABORADOR_RESOLUCAO,
            CO.NOME :: TEXT                                                        AS NOME_COLABORADOR_RESOLUCAO,
            COSI.DATA_HORA_CONSERTO AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)         AS DATA_HORA_RESOLUCAO,
            COSI.DATA_HORA_INICIO_RESOLUCAO AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE) AS DATA_HORA_INICIO_RESOLUCAO,
            COSI.DATA_HORA_FIM_RESOLUCAO AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)    AS DATA_HORA_FIM_RESOLUCAO,
            COSI.FEEDBACK_CONSERTO                                                 AS FEEDBACK_RESOLUCAO,
            MILLIS_TO_MINUTES(COSI.TEMPO_REALIZACAO)                               AS DURACAO_RESOLUCAO_MINUTOS,
            COSI.KM                                                                AS KM_VEICULO_COLETADO_RESOLUCAO,
            CP.CODIGO                                                              AS COD_PERGUNTA,
            CP.PERGUNTA                                                            AS DESCRICAO_PERGUNTA,
            CAP.CODIGO                                                             AS COD_ALTERNATIVA,
            CAP.ALTERNATIVA                                                        AS DESCRICAO_ALTERNATIVA,
            CAP.ALTERNATIVA_TIPO_OUTROS                                            AS ALTERNATIVA_TIPO_OUTROS,
            CASE
                WHEN CAP.ALTERNATIVA_TIPO_OUTROS
                    THEN
                    (SELECT CRN.RESPOSTA_OUTROS
                     FROM CHECKLIST_RESPOSTAS_NOK CRN
                     WHERE CRN.COD_CHECKLIST = C.CODIGO
                       AND CRN.COD_ALTERNATIVA = CAP.CODIGO) :: TEXT
                ELSE NULL
            END                                                                    AS DESCRICAO_TIPO_OUTROS,
            CAP.PRIORIDADE :: TEXT                                                 AS PRIORIDADE_ALTERNATIVA
          FROM CHECKLIST C
            JOIN CHECKLIST_ORDEM_SERVICO COS
              ON C.CODIGO = COS.COD_CHECKLIST
            JOIN CHECKLIST_ORDEM_SERVICO_ITENS COSI
              ON COS.CODIGO = COSI.COD_OS
                 AND COS.COD_UNIDADE = COSI.COD_UNIDADE
            JOIN CHECKLIST_PERGUNTAS CP
              ON COSI.COD_PERGUNTA_PRIMEIRO_APONTAMENTO = CP.CODIGO
            JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
              ON COSI.COD_ALTERNATIVA_PRIMEIRO_APONTAMENTO = CAP.CODIGO
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
        D.DATA_HORA_INICIO_RESOLUCAO                                              AS DATA_HORA_INICIO_RESOLUCAO,
        D.DATA_HORA_FIM_RESOLUCAO                                                 AS DATA_HORA_FIM_RESOLUCAO,
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
    -- Altera JOIN para utilizar colunas de novo nome da tabela COSI.
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
        V.PLACA :: TEXT       AS PLACA_VEICULO,
        COUNT(CASE WHEN CAP.PRIORIDADE = TIPO_ITEM_PRIORIDADE_CRITICA
          THEN 1 END)         AS QTD_ITENS_PRIORIDADE_CRITICA,
        COUNT(CASE WHEN CAP.PRIORIDADE = TIPO_ITEM_PRIORIDADE_ALTA
          THEN 1 END)         AS QTD_ITENS_PRIORIDADE_ALTA,
        COUNT(CASE WHEN CAP.PRIORIDADE = TIPO_ITEM_PRIORIDADE_BAIXA
          THEN 1 END)         AS QTD_ITENS_PRIORIDADE_BAIXA,
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
          ON CAP.CODIGO = COSI.COD_ALTERNATIVA_PRIMEIRO_APONTAMENTO
        JOIN VEICULO_TIPO VT
          ON V.COD_TIPO = VT.CODIGO
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
    --######################################################################################################################
end;
$func$;