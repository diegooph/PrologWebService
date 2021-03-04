create or replace function migration_checklist.func_migration_8_migra_functions_views()
    returns void
    language plpgsql
as
$func$
begin
    --######################################################################################################################
--######################################################################################################################
--#################### MIGRATION DE IMPLEMENTAÇÃO DA NOVA ESTRUTURA DE CHECKLISTS NOS RELATÓRIOS #######################
--######################################################################################################################
--######################################################################################################################
-- PL-2230

    -- Agora a function é STABLE STRICT.
    CREATE OR REPLACE FUNCTION TZ_UNIDADE(F_COD_UNIDADE BIGINT) RETURNS TEXT
        LANGUAGE PLPGSQL
        STABLE STRICT
    AS
    $$
    DECLARE
        TZ TEXT;
    BEGIN
        SELECT TIMEZONE FROM UNIDADE U WHERE U.CODIGO = F_COD_UNIDADE INTO TZ;
        RETURN TZ;
    END;
    $$;

    -- https://dba.stackexchange.com/a/125155/151090
    -- show work_mem;
    -- SET LOCAL work_mem = '4MB';

    -- Agora a function é STABLE STRICT.
    CREATE OR REPLACE FUNCTION TZ_DATE(TIMESTAMP WITH TIME ZONE, TEXT) RETURNS DATE
        LANGUAGE PLPGSQL
        STABLE STRICT
    AS
    $$
    BEGIN
        RETURN ($1 AT TIME ZONE $2) :: DATE;
    END;
    $$;

    CREATE OR REPLACE FUNCTION FORMAT_TIMESTAMP(
      TS_TZ         TIMESTAMP,
      TS_FORTMAT    TEXT,
      VALUE_IF_NULL TEXT DEFAULT NULL)
      RETURNS TEXT
      IMMUTABLE
    LANGUAGE PLPGSQL
    AS $$
    BEGIN
      RETURN COALESCE(TO_CHAR(TS_TZ, TS_FORTMAT), VALUE_IF_NULL);
    END;
    $$;

    -- FUNC_CHECKLIST_RELATORIO_RESUMO_REALIZADOS - Aplica as alterações no relatório de resumo de checklist
    DROP FUNCTION FUNC_CHECKLIST_RELATORIO_RESUMO_REALIZADOS(F_COD_UNIDADES BIGINT[],
        F_PLACA_VEICULO TEXT,
        F_DATA_INICIAL DATE,
        F_DATA_FINAL DATE);
    CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_RELATORIO_RESUMO_REALIZADOS(F_COD_UNIDADES BIGINT[],
                                                                          F_PLACA_VEICULO TEXT,
                                                                          F_DATA_INICIAL DATE,
                                                                          F_DATA_FINAL DATE)
    RETURNS TABLE
            (
                "UNIDADE"                     TEXT,
                "MODELO CHECKLIST"            TEXT,
                "CÓDIGO CHECKLIST"            BIGINT,
                "DATA REALIZAÇÃO"             TEXT,
                "DATA IMPORTADO"              TEXT,
                "COLABORADOR"                 TEXT,
                "CPF"                         TEXT,
                "PLACA"                       TEXT,
                "KM"                          BIGINT,
                "TEMPO REALIZAÇÃO (SEGUNDOS)" BIGINT,
                "TIPO"                        TEXT,
                "TOTAL DE PERGUNTAS"          SMALLINT,
                "TOTAL NOK"                   BIGINT,
                "PRIORIDADE BAIXA"            BIGINT,
                "PRIORIDADE ALTA"             BIGINT,
                "PRIORIDADE CRÍTICA"          BIGINT
            )
    LANGUAGE SQL
    AS
    $$
    SELECT U.NOME                                                 AS NOME_UNIDADE,
           CM.NOME                                                AS NOME_MODELO,
           C.CODIGO                                               AS COD_CHECKLIST,
           FORMAT_TIMESTAMP(
                   C.DATA_HORA_REALIZACAO_TZ_APLICADO,
                   'DD/MM/YYYY HH24:MI')                          AS DATA_HORA_REALIZACAO,
           FORMAT_WITH_TZ(
                   C.DATA_HORA_IMPORTADO_PROLOG,
                   TZ_UNIDADE(C.COD_UNIDADE),
                   'DD/MM/YYYY HH24:MI',
                   '-')                                           AS DATA_HORA_IMPORTADO,
           CO.NOME                                                AS NOME_COLABORADOR,
           LPAD(CO.CPF :: TEXT, 11, '0')                          AS CPF_COLABORADOR,
           C.PLACA_VEICULO                                        AS PLACA_VEICULO,
           C.KM_VEICULO                                           AS KM_VEICULO,
           C.TEMPO_REALIZACAO / 1000                              AS TEMPO_REALIZACAO_SEGUNDOS,
           F_IF(C.TIPO = 'S', 'Saída' :: TEXT, 'Retorno' :: TEXT) AS TIPO_CHECKLIST,
           C.TOTAL_PERGUNTAS_OK + C.TOTAL_PERGUNTAS_NOK           AS TOTAL_PERGUNTAS,
           (SELECT COUNT(*)
            FROM CHECKLIST_RESPOSTAS_NOK CRN
            WHERE CRN.COD_CHECKLIST = C.CODIGO)                   AS TOTAL_NOK,
           (SELECT COUNT(*)
            FROM CHECKLIST_RESPOSTAS_NOK CRN
                     JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
                          ON CRN.COD_ALTERNATIVA = CAP.CODIGO
            WHERE CRN.COD_CHECKLIST = C.CODIGO
              AND CAP.PRIORIDADE = 'BAIXA')                       AS TOTAL_BAIXA,
           (SELECT COUNT(*)
            FROM CHECKLIST_RESPOSTAS_NOK CRN
                     JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
                          ON CRN.COD_ALTERNATIVA = CAP.CODIGO
            WHERE CRN.COD_CHECKLIST = C.CODIGO
              AND CAP.PRIORIDADE = 'ALTA')                        AS TOTAL_ALTA,
           (SELECT COUNT(*)
            FROM CHECKLIST_RESPOSTAS_NOK CRN
                     JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
                          ON CRN.COD_ALTERNATIVA = CAP.CODIGO
            WHERE CRN.COD_CHECKLIST = C.CODIGO
              AND CAP.PRIORIDADE = 'CRITICA')                     AS TOTAL_CRITICA
    FROM CHECKLIST C
             JOIN CHECKLIST_PERGUNTAS CP
                  ON CP.cod_versao_checklist_modelo = C.cod_versao_checklist_modelo
             JOIN COLABORADOR CO
                  ON C.CPF_COLABORADOR = CO.CPF
             JOIN UNIDADE U
                  ON C.COD_UNIDADE = U.CODIGO
             JOIN CHECKLIST_MODELO CM ON CM.CODIGO = C.COD_CHECKLIST_MODELO
    WHERE C.COD_UNIDADE = ANY (F_COD_UNIDADES)
      AND C.DATA_HORA_REALIZACAO_TZ_APLICADO :: DATE >= F_DATA_INICIAL
      AND C.DATA_HORA_REALIZACAO_TZ_APLICADO :: DATE <= F_DATA_FINAL
      AND (F_PLACA_VEICULO = '%' OR C.PLACA_VEICULO LIKE F_PLACA_VEICULO)
    GROUP BY C.CODIGO,
             CM.NOME,
             C.TOTAL_PERGUNTAS_OK,
             C.TOTAL_PERGUNTAS_NOK,
             U.CODIGO,
             CO.CPF,
             CO.NOME,
             CO.CPF,
             C.DATA_HORA,
             C.DATA_HORA_REALIZACAO_TZ_APLICADO,
             C.DATA_HORA_IMPORTADO_PROLOG,
             C.DATA_HORA_SINCRONIZACAO,
             C.COD_UNIDADE,
             C.PLACA_VEICULO,
             C.KM_VEICULO,
             C.TEMPO_REALIZACAO,
             C.TIPO
    ORDER BY U.NOME,
             C.DATA_HORA_SINCRONIZACAO DESC;
    $$;

    -- FUNC_CHECKLIST_OS_RELATORIO_ITENS_MAIOR_QUANTIDADE_NOK - Aplica as alterações no relatório de itens com maior
    -- qtd de nok.
    CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_OS_RELATORIO_ITENS_MAIOR_QUANTIDADE_NOK(F_COD_UNIDADES BIGINT[],
                                                                                      F_DATA_INICIAL DATE,
                                                                                      F_DATA_FINAL DATE)
        RETURNS TABLE
                (
                    UNIDADE               TEXT,
                    "MODELO CHECKLIST"    TEXT,
                    PERGUNTA              TEXT,
                    ALTERNATIVA           TEXT,
                    PRIORIDADE            TEXT,
                    "TOTAL MARCAÇÕES NOK" BIGINT,
                    "TOTAL REALIZAÇÕES"   BIGINT,
                    "PROPORÇÃO"           TEXT
                )
        LANGUAGE SQL
    AS
    $$
    SELECT Q.NOME_UNIDADE          AS NOME_UNIDADE,
           Q.NOME_MODELO_CHECKLIST AS NOME_MODELO_CHECKLIST,
           Q.PERGUNTA              AS PERGUNTA,
           Q.ALTERNATIVA           AS ALTERNATIVA,
           Q.PRIORIDADE            AS PRIORIDADE,
           Q.TOTAL_MARCACOES_NOK   AS TOTAL_MARCACOES_NOK,
           Q.TOTAL_REALIZACOES     AS TOTAL_REALIZACOES,
           Q.PROPORCAO_NUM || '%'  AS PROPORCAO
    FROM (SELECT U.NOME                                                         AS NOME_UNIDADE,
                 CM.NOME                                                        AS NOME_MODELO_CHECKLIST,
                 CP.PERGUNTA                                                    AS PERGUNTA,
                 CAP.ALTERNATIVA                                                AS ALTERNATIVA,
                 CAP.PRIORIDADE                                                 AS PRIORIDADE,
                 COUNT(CRN.CODIGO)                                              AS TOTAL_MARCACOES_NOK,
                 COUNT(DISTINCT C.CODIGO)                                       AS TOTAL_REALIZACOES,
                 TRUNC(((COUNT(CRN.CODIGO)
                     / COUNT(DISTINCT C.CODIGO) :: FLOAT) * 100) :: NUMERIC, 2) AS PROPORCAO_NUM
          FROM CHECKLIST C
                   -- O JOIN é feito pelo código do modelo e não da versão pois queremos saber as proporções de
                   -- respostas NOK em todas as versões de cada modelo.
                   JOIN CHECKLIST_PERGUNTAS CP
                        ON CP.COD_CHECKLIST_MODELO = C.COD_CHECKLIST_MODELO
                   JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
                                   ON CP.CODIGO = CAP.COD_PERGUNTA
                   LEFT JOIN CHECKLIST_RESPOSTAS_NOK CRN
                                   ON C.CODIGO = CRN.COD_CHECKLIST AND CAP.CODIGO = CRN.COD_ALTERNATIVA
                   JOIN CHECKLIST_MODELO CM
                        ON CM.CODIGO = C.COD_CHECKLIST_MODELO
                   JOIN UNIDADE U ON C.COD_UNIDADE = U.CODIGO
          WHERE C.COD_UNIDADE = ANY (F_COD_UNIDADES)
            AND C.DATA_HORA_REALIZACAO_TZ_APLICADO :: DATE >= F_DATA_INICIAL
            AND C.DATA_HORA_REALIZACAO_TZ_APLICADO :: DATE <= F_DATA_FINAL
          GROUP BY U.NOME, CAP.PRIORIDADE, CP.PERGUNTA, CAP.ALTERNATIVA, CM.NOME, C.COD_CHECKLIST_MODELO) Q
    ORDER BY Q.NOME_UNIDADE, Q.PROPORCAO_NUM DESC, Q.TOTAL_MARCACOES_NOK DESC, Q.PERGUNTA;
    $$;

    -- FUNC_CHECKLIST_RELATORIO_LISTAGEM_MODELOS_CHECKLIST - Aplica alterações no relatórios de listagem de modelos
    -- de check.
    CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_RELATORIO_LISTAGEM_MODELOS_CHECKLIST(F_COD_UNIDADES BIGINT[])
        RETURNS TABLE
                (
                    UNIDADE                 TEXT,
                    "CÓDIGO DO CHECKLIST"   TEXT,
                    "NOME DO CHECKLIST"     TEXT,
                    ATIVO                   TEXT,
                    "CÓDIGO DA PERGUNTA"    TEXT,
                    PERGUNTA                TEXT,
                    "CÓDIGO DA ALTERNATIVA" TEXT,
                    ALTERNATIVA             TEXT,
                    "TIPO DE RESPOSTA"      TEXT,
                    PRIORIDADE              TEXT
                )
        LANGUAGE SQL
    AS
    $$
    SELECT U.NOME                                              AS NOME_UNIDADE,
           CM.CODIGO::TEXT                                     AS COD_MODELO_CHECKLIST,
           CM.NOME                                             AS NOME_MODELO,
           F_IF(CM.STATUS_ATIVO, 'SIM' :: TEXT, 'NÃO')         AS ATIVO,
           CP.CODIGO::TEXT                                     AS COD_PERGUNTA,
           CP.PERGUNTA                                         AS PERGUNTA,
           CAP.CODIGO::TEXT                                    AS COD_ALTERNATIVA,
           CAP.ALTERNATIVA                                     AS ALTERNATIVA,
           F_IF(CP.SINGLE_CHOICE, 'ÚNICA' :: TEXT, 'MÚLTIPLA') AS TIPO_DE_RESPOSTA,
           CAP.PRIORIDADE                                      AS PRIORIDADE
    FROM CHECKLIST_PERGUNTAS CP
             JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
                  ON CP.CODIGO = CAP.COD_PERGUNTA
             JOIN CHECKLIST_MODELO CM
                  ON CAP.COD_VERSAO_CHECKLIST_MODELO = CM.COD_VERSAO_ATUAL
             JOIN UNIDADE U
                  ON CM.COD_UNIDADE = U.CODIGO
    WHERE CM.COD_UNIDADE = ANY (F_COD_UNIDADES)
    ORDER BY U.NOME, CM.NOME, CP.PERGUNTA, CAP.ALTERNATIVA;
    $$;

    -- FUNC_CHECKLIST_RELATORIO_ESTRATIFICACAO_RESPOSTAS_NOK
    -- Aplica alterações no relatório de estratificação de respostas não ok
    CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_RELATORIO_ESTRATIFICACAO_RESPOSTAS_NOK(F_COD_UNIDADES BIGINT[],
                                                                                     F_PLACA_VEICULO CHARACTER VARYING,
                                                                                     F_DATA_INICIAL DATE,
                                                                                     F_DATA_FINAL DATE)
        RETURNS TABLE
                (
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
                    "PRAZO EM HORAS"   INTEGER
                )
        LANGUAGE SQL
    AS
    $$
    SELECT U.NOME                                                                     AS NOME_UNIDADE,
           C.CODIGO                                                                   AS COD_CHECKLIST,
           FORMAT_TIMESTAMP(C.DATA_HORA_REALIZACAO_TZ_APLICADO, 'DD/MM/YYYY HH24:MI') AS DATA_HORA_CHECK,
           C.PLACA_VEICULO                                                            AS PLACA_VEICULO,
           CASE
               WHEN C.TIPO = 'S'
                   THEN 'Saída'
               ELSE 'Retorno' END                                                     AS TIPO_CHECKLIST,
           C.KM_VEICULO                                                               AS KM_VEICULO,
           CO.NOME                                                                    AS NOME_REALIZADOR_CHECK,
           CP.PERGUNTA                                                                AS DESCRICAO_PERGUNTA,
           CAP.ALTERNATIVA                                                            AS DESCRICAO_ALTERNATIVA,
           CRN.RESPOSTA_OUTROS                                                        AS RESPOSTA,
           CAP.PRIORIDADE                                                             AS PRIORIDADE,
           PRIO.PRAZO                                                                 AS PRAZO
    FROM CHECKLIST C
             JOIN VEICULO V
                  ON V.PLACA = C.PLACA_VEICULO
             JOIN CHECKLIST_PERGUNTAS CP
                  ON CP.COD_CHECKLIST_MODELO = C.COD_CHECKLIST_MODELO
             JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
                  ON CAP.COD_PERGUNTA = CP.CODIGO
             JOIN CHECKLIST_ALTERNATIVA_PRIORIDADE PRIO
                  ON PRIO.PRIORIDADE :: TEXT = CAP.PRIORIDADE :: TEXT
             JOIN CHECKLIST_RESPOSTAS_NOK CRN
                  ON C.CODIGO = CRN.COD_CHECKLIST
                      AND CRN.COD_ALTERNATIVA = CAP.CODIGO
             JOIN COLABORADOR CO
                  ON CO.CPF = C.CPF_COLABORADOR
             JOIN UNIDADE U
                  ON C.COD_UNIDADE = U.CODIGO
    WHERE C.COD_UNIDADE = ANY (F_COD_UNIDADES)
      AND C.PLACA_VEICULO LIKE F_PLACA_VEICULO
      AND C.DATA_HORA_REALIZACAO_TZ_APLICADO :: DATE >= F_DATA_INICIAL
      AND C.DATA_HORA_REALIZACAO_TZ_APLICADO :: DATE <= F_DATA_FINAL
    ORDER BY U.NOME, C.DATA_HORA_SINCRONIZACAO DESC, C.CODIGO ASC
    $$;

    -- View já foi dropada no migration_checklist.func_migration_1_cria_estrutura_versionamento_modelo.
    CREATE OR REPLACE VIEW ESTRATIFICACAO_OS AS
    SELECT COS.CODIGO                                                       AS COD_OS,
             REALIZADOR.NOME                                                  AS NOME_REALIZADOR_CHECKLIST,
             C.PLACA_VEICULO,
             C.KM_VEICULO                                                     AS KM,
             C.DATA_HORA_REALIZACAO_TZ_APLICADO                               AS DATA_HORA,
             C.TIPO                                                           AS TIPO_CHECKLIST,
             CP.CODIGO                                                        AS COD_PERGUNTA,
             CP.CODIGO_CONTEXTO                                               AS COD_CONTEXTO_PERGUNTA,
             CP.ORDEM                                                         AS ORDEM_PERGUNTA,
             CP.PERGUNTA,
             CP.SINGLE_CHOICE,
             NULL :: UNKNOWN                                                  AS URL_IMAGEM,
             CAP.PRIORIDADE,
             CASE CAP.PRIORIDADE
                 WHEN 'CRITICA' :: TEXT
                     THEN 1
                 WHEN 'ALTA' :: TEXT
                     THEN 2
                 WHEN 'BAIXA' :: TEXT
                     THEN 3
                 ELSE NULL :: INTEGER
                 END                                                          AS PRIORIDADE_ORDEM,
             CAP.CODIGO                                                       AS COD_ALTERNATIVA,
             CAP.CODIGO_CONTEXTO                                              AS COD_CONTEXTO_ALTERNATIVA,
             CAP.ALTERNATIVA,
             PRIO.PRAZO,
             CRN.RESPOSTA_OUTROS,
             V.COD_TIPO,
             COS.COD_UNIDADE,
             COS.STATUS                                                       AS STATUS_OS,
             COS.COD_CHECKLIST,
             TZ_UNIDADE(COS.COD_UNIDADE)                                      AS TIME_ZONE_UNIDADE,
             COSI.STATUS_RESOLUCAO                                            AS STATUS_ITEM,
             MECANICO.NOME                                                    AS NOME_MECANICO,
             COSI.CPF_MECANICO,
             COSI.TEMPO_REALIZACAO,
             COSI.DATA_HORA_CONSERTO AT TIME ZONE TZ_UNIDADE(COS.COD_UNIDADE) AS DATA_HORA_CONSERTO,
             COSI.DATA_HORA_INICIO_RESOLUCAO                                  AS DATA_HORA_INICIO_RESOLUCAO_UTC,
             COSI.DATA_HORA_FIM_RESOLUCAO                                     AS DATA_HORA_FIM_RESOLUCAO_UTC,
             COSI.KM                                                          AS KM_FECHAMENTO,
             COSI.QT_APONTAMENTOS,
             COSI.FEEDBACK_CONSERTO,
             COSI.CODIGO
      FROM CHECKLIST_DATA C
               JOIN COLABORADOR REALIZADOR
                   ON REALIZADOR.CPF = C.CPF_COLABORADOR
               JOIN VEICULO V
                   ON V.PLACA :: TEXT = C.PLACA_VEICULO :: TEXT
               JOIN CHECKLIST_ORDEM_SERVICO COS
                    ON C.CODIGO = COS.COD_CHECKLIST
               JOIN CHECKLIST_ORDEM_SERVICO_ITENS COSI
                    ON COS.CODIGO = COSI.COD_OS
                        AND COS.COD_UNIDADE = COSI.COD_UNIDADE
               JOIN CHECKLIST_PERGUNTAS CP
                    ON CP.COD_VERSAO_CHECKLIST_MODELO = C.COD_VERSAO_CHECKLIST_MODELO
                    AND COSI.COD_CONTEXTO_PERGUNTA = CP.CODIGO_CONTEXTO
               JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
                    ON CAP.COD_PERGUNTA = CP.CODIGO
                    AND COSI.COD_CONTEXTO_ALTERNATIVA= CAP.CODIGO_CONTEXTO
               JOIN CHECKLIST_ALTERNATIVA_PRIORIDADE PRIO
                    ON PRIO.PRIORIDADE :: TEXT = CAP.PRIORIDADE :: TEXT
               JOIN CHECKLIST_RESPOSTAS_NOK CRN
                    ON CRN.COD_CHECKLIST = C.CODIGO
                        AND CRN.COD_ALTERNATIVA = CAP.CODIGO
               LEFT JOIN COLABORADOR MECANICO ON MECANICO.CPF = COSI.CPF_MECANICO;

    -- Dropa e recria a FUNC_CHECKLIST_OS_RELATORIO_ESTRATIFICACAO_OS
    DROP FUNCTION FUNC_CHECKLIST_OS_RELATORIO_ESTRATIFICACAO_OS(BIGINT[], TEXT, TEXT, TEXT, DATE, DATE, DATE, DATE);
    CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_OS_RELATORIO_ESTRATIFICACAO_OS(F_COD_UNIDADES BIGINT[],
                                                                             F_PLACA_VEICULO TEXT,
                                                                             F_STATUS_OS TEXT,
                                                                             F_STATUS_ITEM TEXT,
                                                                             F_DATA_INICIAL_ABERTURA DATE,
                                                                             F_DATA_FINAL_ABERTURA DATE,
                                                                             F_DATA_INICIAL_RESOLUCAO DATE,
                                                                             F_DATA_FINAL_RESOLUCAO DATE)
        RETURNS TABLE
                (
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
                    "DATA INÍCIO RESOLUÇÃO"        TEXT,
                    "DATA FIM RESOLUÇÃO"           TEXT,
                    "DATA RESOLIVDO PROLOG"        TEXT,
                    "MECÂNICO"                     TEXT,
                    "DESCRIÇÃO CONSERTO"           TEXT,
                    "TEMPO DE CONSERTO EM MINUTOS" BIGINT,
                    "KM ABERTURA"                  BIGINT,
                    "KM FECHAMENTO"                BIGINT,
                    "KM PERCORRIDO"                TEXT,
                    "MOTORISTA"                    TEXT,
                    "TIPO DO CHECKLIST"            TEXT
                )
        LANGUAGE SQL
    AS
    $$
    SELECT U.NOME                                                                             AS NOME_UNIDADE,
           EO.COD_OS                                                                          AS CODIGO_OS,
           FORMAT_TIMESTAMP(DATA_HORA, 'DD/MM/YYYY HH24:MI')                                  AS ABERTURA_OS,
           FORMAT_TIMESTAMP(DATA_HORA + (PRAZO || ' HOUR') :: INTERVAL, 'DD/MM/YYYY HH24:MI') AS DATA_LIMITE_CONSERTO,
           (CASE
                WHEN STATUS_OS = 'A'
                    THEN 'ABERTA'
                ELSE 'FECHADA' END)                                                           AS STATUS_OS,
           PLACA_VEICULO                                                                      AS PLACA,
           PERGUNTA                                                                           AS PERGUNTA,
           ALTERNATIVA                                                                        AS ALTERNATIVA,
           PRIORIDADE                                                                         AS PRIORIDADE,
           PRAZO                                                                              AS PRAZO_EM_HORAS,
           RESPOSTA_OUTROS                                                                    AS DESCRICAO,
           CASE
               WHEN STATUS_ITEM = 'P'
                   THEN 'PENDENTE'
               ELSE 'RESOLVIDO' END                                                           AS STATUS_ITEM,
           FORMAT_TIMESTAMP(
                   DATA_HORA_INICIO_RESOLUCAO_UTC AT TIME ZONE TIME_ZONE_UNIDADE,
                   'DD/MM/YYYY HH24:MI',
                   '-')                                                                       AS DATA_INICIO_RESOLUCAO,
           FORMAT_TIMESTAMP(
                   DATA_HORA_FIM_RESOLUCAO_UTC AT TIME ZONE TIME_ZONE_UNIDADE,
                   'DD/MM/YYYY HH24:MI', '-')                                                 AS DATA_FIM_RESOLUCAO,
           FORMAT_TIMESTAMP(DATA_HORA_CONSERTO, 'DD/MM/YYYY HH24:MI')                         AS DATA_RESOLVIDO_PROLOG,
           NOME_MECANICO                                                                      AS MECANICO,
           FEEDBACK_CONSERTO                                                                  AS DESCRICAO_CONSERTO,
           TEMPO_REALIZACAO / 1000 / 60                                                       AS TEMPO_CONSERTO_MINUTOS,
           KM                                                                                 AS KM_ABERTURA,
           KM_FECHAMENTO                                                                      AS KM_FECHAMENTO,
           COALESCE((KM_FECHAMENTO - KM) :: TEXT, '-')                                        AS KM_PERCORRIDO,
           NOME_REALIZADOR_CHECKLIST                                                          AS MOTORISTA,
           CASE
               WHEN TIPO_CHECKLIST = 'S'
                   THEN 'SAÍDA'
               ELSE 'RETORNO' END                                                             AS TIPO_CHECKLIST
    FROM ESTRATIFICACAO_OS EO
             JOIN UNIDADE U
                  ON EO.COD_UNIDADE = U.CODIGO
    WHERE EO.COD_UNIDADE = ANY (F_COD_UNIDADES)
      AND EO.PLACA_VEICULO LIKE F_PLACA_VEICULO
      AND EO.STATUS_OS LIKE F_STATUS_OS
      AND EO.STATUS_ITEM LIKE F_STATUS_ITEM
      AND CASE
              -- O usuário pode filtrar tanto por início e fim de abertura ou por início e fim de resolução ou, ainda,
              -- por ambos.
              WHEN (F_DATA_INICIAL_ABERTURA,
                    F_DATA_FINAL_ABERTURA,
                    F_DATA_INICIAL_RESOLUCAO,
                    F_DATA_FINAL_RESOLUCAO) IS NOT NULL
                  THEN (
                      EO.DATA_HORA :: DATE BETWEEN F_DATA_INICIAL_ABERTURA AND F_DATA_FINAL_ABERTURA
                      AND
                      EO.DATA_HORA_CONSERTO :: DATE BETWEEN F_DATA_INICIAL_RESOLUCAO AND F_DATA_FINAL_RESOLUCAO)
              WHEN (F_DATA_INICIAL_ABERTURA,
                    F_DATA_FINAL_ABERTURA) IS NOT NULL
                  THEN
                  EO.DATA_HORA :: DATE BETWEEN F_DATA_INICIAL_ABERTURA AND F_DATA_FINAL_ABERTURA
              WHEN (F_DATA_INICIAL_RESOLUCAO,
                    F_DATA_FINAL_RESOLUCAO) IS NOT NULL
                  THEN
                  EO.DATA_HORA_CONSERTO :: DATE BETWEEN F_DATA_INICIAL_RESOLUCAO AND F_DATA_FINAL_RESOLUCAO

              -- Se não entrar em nenhuma condição conhecida, retornamos FALSE para o relatório não retornar dado nenhum.
              ELSE FALSE END
    ORDER BY U.NOME, EO.COD_OS, EO.PRAZO;
    $$;
    --######################################################################################################################
    --######################################################################################################################

    --######################################################################################################################
    --######################################################################################################################
    -- Remove usos da CHECKLIST_RESPOSTAS e utiliza nova coluna DATA_HORA_REALIZACAO_TZ_APLICADO.
    DROP FUNCTION FUNC_CHECKLIST_GET_ALL_CHECKLISTS_REALIZADOS(F_COD_UNIDADE BIGINT,
        F_COD_EQUIPE BIGINT,
        F_COD_TIPO_VEICULO BIGINT,
        F_PLACA_VEICULO CHARACTER VARYING,
        F_DATA_INICIAL DATE,
        F_DATA_FINAL DATE,
        F_TIMEZONE TEXT,
        F_LIMIT INTEGER,
        F_OFFSET BIGINT);
    CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_GET_ALL_CHECKLISTS_REALIZADOS(F_COD_UNIDADE BIGINT,
                                                                            F_COD_EQUIPE BIGINT,
                                                                            F_COD_TIPO_VEICULO BIGINT,
                                                                            F_PLACA_VEICULO CHARACTER VARYING,
                                                                            F_DATA_INICIAL DATE,
                                                                            F_DATA_FINAL DATE,
                                                                            F_TIMEZONE TEXT,
                                                                            F_LIMIT INTEGER,
                                                                            F_OFFSET BIGINT)
        RETURNS TABLE
                (
                    COD_CHECKLIST                 BIGINT,
                    COD_CHECKLIST_MODELO          BIGINT,
                    COD_VERSAO_CHECKLIST_MODELO   BIGINT,
                    DATA_HORA_REALIZACAO          TIMESTAMP WITHOUT TIME ZONE,
                    DATA_HORA_IMPORTADO_PROLOG    TIMESTAMP WITHOUT TIME ZONE,
                    KM_VEICULO_MOMENTO_REALIZACAO BIGINT,
                    DURACAO_REALIZACAO_MILLIS     BIGINT,
                    CPF_COLABORADOR               BIGINT,
                    PLACA_VEICULO                 TEXT,
                    TIPO_CHECKLIST                CHAR,
                    NOME_COLABORADOR              TEXT,
                    TOTAL_ITENS_OK                SMALLINT,
                    TOTAL_ITENS_NOK               SMALLINT
                )
        LANGUAGE PLPGSQL
    AS
    $$
    DECLARE
        F_HAS_EQUIPE           INTEGER    := CASE WHEN F_COD_EQUIPE IS NULL THEN 1 ELSE 0 END;
        F_HAS_COD_TIPO_VEICULO INTEGER    := CASE WHEN F_COD_TIPO_VEICULO IS NULL THEN 1 ELSE 0 END;
        F_HAS_PLACA_VEICULO    INTEGER    := CASE WHEN F_PLACA_VEICULO IS NULL THEN 1 ELSE 0 END;
    BEGIN
        RETURN QUERY
            SELECT C.CODIGO                                             AS COD_CHECKLIST,
                   C.COD_CHECKLIST_MODELO                               AS COD_CHECKLIST_MODELO,
                   C.COD_VERSAO_CHECKLIST_MODELO                        AS COD_VERSAO_CHECKLIST_MODELO,
                   C.DATA_HORA AT TIME ZONE F_TIMEZONE                  AS DATA_HORA_REALIZACAO,
                   C.DATA_HORA_IMPORTADO_PROLOG AT TIME ZONE F_TIMEZONE AS DATA_HORA_IMPORTADO_PROLOG,
                   C.KM_VEICULO                                         AS KM_VEICULO_MOMENTO_REALIZACAO,
                   C.TEMPO_REALIZACAO                                   AS DURACAO_REALIZACAO_MILLIS,
                   C.CPF_COLABORADOR                                    AS CPF_COLABORADOR,
                   C.PLACA_VEICULO :: TEXT                              AS PLACA_VEICULO,
                   C.TIPO                                               AS TIPO_CHECKLIST,
                   CO.NOME :: TEXT                                      AS NOME_COLABORADOR,
                   C.TOTAL_PERGUNTAS_OK                                 AS TOTAL_ITENS_OK,
                   C.TOTAL_PERGUNTAS_NOK                                AS TOTAL_ITENS_NOK
            FROM CHECKLIST C
                     JOIN COLABORADOR CO
                          ON CO.CPF = C.CPF_COLABORADOR
                     JOIN EQUIPE E
                          ON E.CODIGO = CO.COD_EQUIPE
                     JOIN VEICULO V
                          ON V.PLACA = C.PLACA_VEICULO
            WHERE C.DATA_HORA_REALIZACAO_TZ_APLICADO :: DATE >= F_DATA_INICIAL
              AND C.DATA_HORA_REALIZACAO_TZ_APLICADO :: DATE <= F_DATA_FINAL
              AND C.COD_UNIDADE = F_COD_UNIDADE
              AND (F_HAS_EQUIPE = 1 OR E.CODIGO = F_COD_EQUIPE)
              AND (F_HAS_COD_TIPO_VEICULO = 1 OR V.COD_TIPO = F_COD_TIPO_VEICULO)
              AND (F_HAS_PLACA_VEICULO = 1 OR C.PLACA_VEICULO = F_PLACA_VEICULO)
            ORDER BY DATA_HORA_SINCRONIZACAO DESC
            LIMIT F_LIMIT
                OFFSET F_OFFSET;
    END;
    $$;
    --######################################################################################################################
    --######################################################################################################################

    --######################################################################################################################
    --######################################################################################################################
    CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_GET_FAROL_CHECKLIST(
      F_COD_UNIDADE                BIGINT,
      F_DATA_INICIAL               DATE,
      F_DATA_FINAL                 DATE,
      F_ITENS_CRITICOS_RETROATIVOS BOOLEAN)
      RETURNS TABLE(
        DATA                               DATE,
        PLACA                              CHARACTER VARYING,
        COD_CHECKLIST_SAIDA                BIGINT,
        DATA_HORA_ULTIMO_CHECKLIST_SAIDA   TIMESTAMP WITHOUT TIME ZONE,
        COD_CHECKLIST_MODELO_SAIDA         BIGINT,
        NOME_COLABORADOR_CHECKLIST_SAIDA   CHARACTER VARYING,
        COD_CHECKLIST_RETORNO              BIGINT,
        DATA_HORA_ULTIMO_CHECKLIST_RETORNO TIMESTAMP WITHOUT TIME ZONE,
        COD_CHECKLIST_MODELO_RETORNO       BIGINT,
        NOME_COLABORADOR_CHECKLIST_RETORNO CHARACTER VARYING,
        CODIGO_PERGUNTA                    BIGINT,
        DESCRICAO_PERGUNTA                 TEXT,
        DESCRICAO_ALTERNATIVA              TEXT,
        ALTERNATIVA_TIPO_OUTROS            BOOLEAN,
        DESCRICAO_ALTERNATIVA_TIPO_OUTROS  TEXT,
        CODIGO_ITEM_CRITICO                BIGINT,
        DATA_HORA_APONTAMENTO_ITEM_CRITICO TIMESTAMP WITHOUT TIME ZONE)
    LANGUAGE PLPGSQL
    AS $$
    DECLARE
      CHECKLIST_TIPO_SAIDA         CHAR := 'S';
      CHECKLIST_TIPO_RETORNO       CHAR := 'R';
      CHECKLIST_PRIORIDADE_CRITICA TEXT := 'CRITICA';
      ORDEM_SERVICO_ABERTA         CHAR := 'A';
      ITEM_ORDEM_SERVICO_PENDENDTE CHAR := 'P';
    BEGIN
      RETURN QUERY
      WITH ULTIMOS_CHECKLISTS_VEICULOS AS (
          SELECT
            INNERTABLE.DATA,
            INNERTABLE.PLACA,
            INNERTABLE.COD_CHECKLIST_SAIDA,
            INNERTABLE.DATA_HORA_ULTIMO_CHECKLIST_SAIDA,
            CS.COD_CHECKLIST_MODELO AS COD_CHECKLIST_MODELO_SAIDA,
            COS.NOME                AS NOME_COLABORADOR_CHECKLIST_SAIDA,
            INNERTABLE.COD_CHECKLIST_RETORNO,
            INNERTABLE.DATA_HORA_ULTIMO_CHECKLIST_RETORNO,
            CR.COD_CHECKLIST_MODELO AS COD_CHECKLIST_MODELO_RETORNO,
            COR.NOME                AS NOME_COLABORADOR_CHECKLIST_RETORNO
          FROM
            (SELECT
               G.DAY :: DATE                                     AS DATA,
               V.PLACA                                           AS PLACA,
               MAX(CASE WHEN C.TIPO = CHECKLIST_TIPO_SAIDA
                 THEN C.CODIGO END)                              AS COD_CHECKLIST_SAIDA,
               MAX(CASE WHEN C.TIPO = CHECKLIST_TIPO_SAIDA
                 THEN C.DATA_HORA_REALIZACAO_TZ_APLICADO END)    AS DATA_HORA_ULTIMO_CHECKLIST_SAIDA,
               MAX(CASE WHEN C.TIPO = CHECKLIST_TIPO_RETORNO
                 THEN C.CODIGO END)                              AS COD_CHECKLIST_RETORNO,
               MAX(CASE WHEN C.TIPO = CHECKLIST_TIPO_RETORNO
                 THEN C.DATA_HORA_REALIZACAO_TZ_APLICADO END)    AS DATA_HORA_ULTIMO_CHECKLIST_RETORNO
             FROM VEICULO V
               CROSS JOIN GENERATE_SERIES(F_DATA_INICIAL, F_DATA_FINAL, '1 DAY') G(DAY)
               LEFT JOIN CHECKLIST C
                 ON C.PLACA_VEICULO = V.PLACA AND G.DAY :: DATE = (C.DATA_HORA_REALIZACAO_TZ_APLICADO) :: DATE
             WHERE V.COD_UNIDADE = F_COD_UNIDADE AND V.STATUS_ATIVO = TRUE
             GROUP BY 1, 2
             ORDER BY 1, 2) AS INNERTABLE
            LEFT JOIN CHECKLIST CS ON CS.CODIGO = INNERTABLE.COD_CHECKLIST_SAIDA
            LEFT JOIN CHECKLIST CR ON CR.CODIGO = INNERTABLE.COD_CHECKLIST_RETORNO
            LEFT JOIN COLABORADOR COS ON COS.CPF = CS.CPF_COLABORADOR
            LEFT JOIN COLABORADOR COR ON COR.CPF = CR.CPF_COLABORADOR
          ORDER BY INNERTABLE.DATA, INNERTABLE.PLACA
      ),

          ITENS_CRITICOS_PENDENTES AS (
            SELECT COSI.COD_ALTERNATIVA_PRIMEIRO_APONTAMENTO AS COD_ALTERNATIVA_PRIMEIRO_APONTAMENTO,
                   COSI.COD_PERGUNTA_PRIMEIRO_APONTAMENTO    AS COD_PERGUNTA_PRIMEIRO_APONTAMENTO,
                   COSI.STATUS_RESOLUCAO                     AS STATUS_RESOLUCAO,
                   COSI.COD_OS                               AS COD_OS,
                   COSI.COD_UNIDADE                          AS COD_UNIDADE,
                   COSI.CODIGO                               AS CODIGO,
                   CAP.PRIORIDADE                            AS PRIORIDADE
            FROM CHECKLIST_ORDEM_SERVICO_ITENS COSI
              JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
                ON CAP.CODIGO = COSI.COD_ALTERNATIVA_PRIMEIRO_APONTAMENTO
              JOIN CHECKLIST_PERGUNTAS CP
                ON CP.CODIGO = CAP.COD_PERGUNTA
            WHERE COSI.COD_UNIDADE = F_COD_UNIDADE
                  AND CAP.PRIORIDADE = CHECKLIST_PRIORIDADE_CRITICA
                  AND COSI.STATUS_RESOLUCAO = ITEM_ORDEM_SERVICO_PENDENDTE
        )

      SELECT
        Q.DATA,
        Q.PLACA,
        Q.COD_CHECKLIST_SAIDA,
        Q.DATA_HORA_ULTIMO_CHECKLIST_SAIDA,
        Q.COD_CHECKLIST_MODELO_SAIDA,
        Q.NOME_COLABORADOR_CHECKLIST_SAIDA,
        Q.COD_CHECKLIST_RETORNO,
        Q.DATA_HORA_ULTIMO_CHECKLIST_RETORNO,
        Q.COD_CHECKLIST_MODELO_RETORNO,
        Q.NOME_COLABORADOR_CHECKLIST_RETORNO,
        Q.CODIGO_PERGUNTA,
        Q.DESCRICAO_PERGUNTA,
        Q.DESCRICAO_ALTERNATIVA,
        CASE
        WHEN Q.ALTERNATIVA_TIPO_OUTROS_CHECKLIST_SAIDA
             OR Q.ALTERNATIVA_TIPO_OUTROS_CHECKLIST_RETORNO
          THEN TRUE
        ELSE FALSE
        END AS ALTERNATIVA_TIPO_OUTROS,
        CASE
        WHEN Q.ITEM_CRITICO_DE_SAIDA_TIPO_OUTROS
          THEN (SELECT CRN.RESPOSTA_OUTROS
                FROM CHECKLIST_RESPOSTAS_NOK CRN
                WHERE CRN.COD_CHECKLIST = Q.COD_CHECKLIST_SAIDA AND CRN.COD_ALTERNATIVA = Q.CODIGO_ALTERNATIVA)
        WHEN Q.ALTERNATIVA_TIPO_OUTROS_CHECKLIST_RETORNO
          THEN (SELECT CRN.RESPOSTA_OUTROS
                FROM CHECKLIST_RESPOSTAS_NOK CRN
                WHERE CRN.COD_CHECKLIST = Q.COD_CHECKLIST_RETORNO AND CRN.COD_ALTERNATIVA = Q.CODIGO_ALTERNATIVA)
        ELSE NULL
        END AS DESCRICAO_ALTERNATIVA_TIPO_OUTROS,
        Q.CODIGO_ITEM_CRITICO,
        Q.DATA_HORA_APONTAMENTO_ITEM_CRITICO
      FROM
        (SELECT
           UCV.DATA,
           UCV.PLACA,
           UCV.COD_CHECKLIST_SAIDA,
           UCV.DATA_HORA_ULTIMO_CHECKLIST_SAIDA,
           UCV.COD_CHECKLIST_MODELO_SAIDA,
           UCV.NOME_COLABORADOR_CHECKLIST_SAIDA,
           UCV.COD_CHECKLIST_RETORNO,
           UCV.DATA_HORA_ULTIMO_CHECKLIST_RETORNO,
           UCV.COD_CHECKLIST_MODELO_RETORNO,
           UCV.NOME_COLABORADOR_CHECKLIST_RETORNO,
           CP.CODIGO       AS CODIGO_PERGUNTA,
           CP.PERGUNTA     AS DESCRICAO_PERGUNTA,
           CAP.ALTERNATIVA AS DESCRICAO_ALTERNATIVA,
           CAP.CODIGO      AS CODIGO_ALTERNATIVA,
           CASE WHEN (COS.COD_CHECKLIST = UCV.COD_CHECKLIST_SAIDA
                      AND CAP.PRIORIDADE = CHECKLIST_PRIORIDADE_CRITICA
                      AND CAP.ALTERNATIVA_TIPO_OUTROS = TRUE
                      AND COSI.COD_ALTERNATIVA_PRIMEIRO_APONTAMENTO = CAP.CODIGO)
             THEN TRUE
           ELSE FALSE
           END             AS ITEM_CRITICO_DE_SAIDA_TIPO_OUTROS,
           CASE WHEN (COS.COD_CHECKLIST = UCV.COD_CHECKLIST_SAIDA
                      AND CAP.PRIORIDADE = CHECKLIST_PRIORIDADE_CRITICA
                      AND CAP.ALTERNATIVA_TIPO_OUTROS = TRUE)
             THEN TRUE
           ELSE FALSE
           END             AS ALTERNATIVA_TIPO_OUTROS_CHECKLIST_SAIDA,
           CASE WHEN (COS.COD_CHECKLIST = UCV.COD_CHECKLIST_RETORNO
                      AND CAP.PRIORIDADE = CHECKLIST_PRIORIDADE_CRITICA
                      AND CAP.ALTERNATIVA_TIPO_OUTROS = TRUE)
             THEN TRUE
           ELSE FALSE
           END             AS ALTERNATIVA_TIPO_OUTROS_CHECKLIST_RETORNO,
           CASE WHEN CAP.PRIORIDADE = CHECKLIST_PRIORIDADE_CRITICA
             THEN COSI.CODIGO
           ELSE NULL
           END             AS CODIGO_ITEM_CRITICO,
           CASE
           WHEN COS.COD_CHECKLIST = UCV.COD_CHECKLIST_SAIDA
                AND CAP.PRIORIDADE = CHECKLIST_PRIORIDADE_CRITICA
             THEN UCV.DATA_HORA_ULTIMO_CHECKLIST_SAIDA
           WHEN COS.COD_CHECKLIST = UCV.COD_CHECKLIST_RETORNO
                AND CAP.PRIORIDADE = CHECKLIST_PRIORIDADE_CRITICA
             THEN UCV.DATA_HORA_ULTIMO_CHECKLIST_RETORNO
           ELSE NULL
           END             AS DATA_HORA_APONTAMENTO_ITEM_CRITICO
         FROM ULTIMOS_CHECKLISTS_VEICULOS UCV
           LEFT JOIN CHECKLIST_ORDEM_SERVICO COS
             ON COS.COD_CHECKLIST IN (UCV.COD_CHECKLIST_SAIDA, UCV.COD_CHECKLIST_RETORNO)
                AND COS.STATUS = ORDEM_SERVICO_ABERTA
                AND COS.CODIGO IS NOT NULL
           -- Utilizamos o join com ITENS_CRITICOS_PENDENTES ao invés de CHECKLIST_ORDEM_SERVICO_ITENS pois desse modo
           -- consideramos apenas os itens abertos críticos, que é o que importa para o farol.
           LEFT JOIN ITENS_CRITICOS_PENDENTES COSI
             ON COS.CODIGO = COSI.COD_OS
                AND COS.COD_UNIDADE = COSI.COD_UNIDADE
                AND COSI.STATUS_RESOLUCAO = ITEM_ORDEM_SERVICO_PENDENDTE
                AND COSI.PRIORIDADE = CHECKLIST_PRIORIDADE_CRITICA
           LEFT JOIN CHECKLIST_PERGUNTAS CP
             ON CP.CODIGO = COSI.COD_PERGUNTA_PRIMEIRO_APONTAMENTO
           LEFT JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
             ON CAP.COD_PERGUNTA = CP.CODIGO
                AND CAP.CODIGO = COSI.COD_ALTERNATIVA_PRIMEIRO_APONTAMENTO
                AND CAP.PRIORIDADE = CHECKLIST_PRIORIDADE_CRITICA) AS Q
      ORDER BY Q.DATA, Q.PLACA, Q.CODIGO_PERGUNTA;
    END;
    $$;
    --######################################################################################################################
    --######################################################################################################################
end;
$func$;