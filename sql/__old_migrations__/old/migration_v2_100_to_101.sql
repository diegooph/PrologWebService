BEGIN TRANSACTION;

--######################################################################################################################
--######################################################################################################################
--###########################    RECRIA FUNCTION FUNC_CHECKLIST_RELATORIO_RESUMO_REALIZADOS  ###########################
--######################################################################################################################
--######################################################################################################################
-- PL-2290
-- REMOVE FUNC EXISTENTE.
DROP FUNCTION FUNC_CHECKLIST_RELATORIO_RESUMO_REALIZADOS(BIGINT[], TEXT, DATE, DATE);

-- CRIA FUNC FUNC_CHECKLIST_RELATORIO_RESUMO_REALIZADOS.
-- Sobre:
-- Busca um resumo dos checklists que foram realiazados e que se enquadram nos parâmetros de filtro utilizados.
--
-- Parâmetros:
-- F_COD_UNIDADES: Um array com os códigos das unidades das quais queremos buscar os checklists realizados.
-- F_PLACA_VEICULO: A placa do veículo do qual queremos buscar os checklists realizados.
--                  Para não filtrar por nenhuma placa específica, envie '%' (sem as aspas simples).
-- F_DATA_INICIAL: Data inicial pela qual se quer filtrar os dados. Apenas checks realizados nessa data ou após serão
--                 buscados.
-- F_DATA_FINAL: Data final pela qual se quer filtrar os dados. Apenas checks realizados nessa data ou antes serão
--               buscados.
--
-- Todos os parâmetros são obrigatórios.
--
-- Histórico:
-- 2019-08-01 -> Adicionado código do checklist ao retorno da function (luizfp).
-- 2019-09-10 -> Adiciona cpf do colaborador no retorno da function (natan_rotta).
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
                "TOTAL DE PERGUNTAS"          BIGINT,
                "TOTAL NOK"                   BIGINT,
                "PRIORIDADE BAIXA"            BIGINT,
                "PRIORIDADE ALTA"             BIGINT,
                "PRIORIDADE CRÍTICA"          BIGINT
            )
    LANGUAGE SQL
AS
$$
WITH CHECKLITS AS (
    SELECT C.CODIGO                                                                          AS COD_CHECKLIST,
           C.COD_CHECKLIST_MODELO                                                            AS COD_CHECKLIST_MODELO,
           U.CODIGO                                                                          AS COD_UNIDADE,
           U.NOME                                                                            AS NOME_UNIDADE,
           C.DATA_HORA                                                                       AS DATA_HORA_REALIZACAO,
           C.DATA_HORA_SINCRONIZACAO                                                         AS DATA_HORA_SINCRONIZACAO,
           TO_CHAR(C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE), 'DD/MM/YYYY HH24:MI') AS DATA_REALIZACAO_CHECK,
           TO_CHAR(C.DATA_HORA_IMPORTADO_PROLOG AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE),
                   'DD/MM/YYYY HH24:MI')                                                     AS DATA_IMPORTADO,
           CO.NOME                                                                           AS NOME_COLABORADOR,
           LPAD(CO.CPF :: TEXT, 11, '0')                                                     AS CPF,
           C.PLACA_VEICULO                                                                   AS PLACA_VEICULO,
           C.KM_VEICULO                                                                      AS KM_VEICULO,
           C.TEMPO_REALIZACAO / 1000                                                         AS TEMPO_REALIZACAO_SEGUNDOS,
           F_IF(C.TIPO = 'S', 'Saída' :: TEXT, 'Retorno' :: TEXT)                            AS TIPO_CHECKLIST,
           COUNT(C.CODIGO)                                                                   AS TOTAL_PERGUNTAS
    FROM CHECKLIST C
             JOIN CHECKLIST_PERGUNTAS CP
                  ON CP.COD_CHECKLIST_MODELO = C.COD_CHECKLIST_MODELO
             JOIN COLABORADOR CO
                  ON C.CPF_COLABORADOR = CO.CPF
             JOIN UNIDADE U
                  ON C.COD_UNIDADE = U.CODIGO
    WHERE C.COD_UNIDADE = ANY (F_COD_UNIDADES)
      AND CP.STATUS_ATIVO
      AND C.PLACA_VEICULO LIKE F_PLACA_VEICULO
      AND (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: DATE >= F_DATA_INICIAL
      AND (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: DATE <= F_DATA_FINAL
    GROUP BY C.CODIGO,
             C.COD_CHECKLIST_MODELO,
             U.CODIGO,
             CO.CPF,
             CO.NOME,
             CO.CPF,
             C.DATA_HORA,
             C.DATA_HORA_IMPORTADO_PROLOG,
             C.DATA_HORA_SINCRONIZACAO,
             C.COD_UNIDADE,
             C.PLACA_VEICULO,
             C.KM_VEICULO,
             C.TEMPO_REALIZACAO,
             C.TIPO),

     RESPOSTAS_NOK AS (
         SELECT CR.COD_CHECKLIST          AS COD_CHECKLIST,
                COUNT(CASE
                          WHEN CR.RESPOSTA <> 'OK'
                              THEN 1 END) AS TOTAL_NOK,
                COUNT(CASE
                          WHEN CAP.PRIORIDADE = 'BAIXA'
                              THEN 1 END) AS TOTAL_BAIXAS,
                COUNT(CASE
                          WHEN CAP.PRIORIDADE = 'ALTA'
                              THEN 1 END) AS TOTAL_ALTAS,
                COUNT(CASE
                          WHEN CAP.PRIORIDADE = 'CRITICA'
                              THEN 1 END) AS TOTAL_CRITICAS
         FROM CHECKLIST_RESPOSTAS CR
                  JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
                       ON CR.COD_ALTERNATIVA = CAP.CODIGO
                  JOIN CHECKLIST C
                       ON CR.COD_CHECKLIST = C.CODIGO
         WHERE CR.RESPOSTA <> 'OK'
           AND C.COD_UNIDADE = ANY (F_COD_UNIDADES)
           AND CAP.STATUS_ATIVO
           AND C.PLACA_VEICULO LIKE F_PLACA_VEICULO
           AND (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: DATE >= F_DATA_INICIAL
           AND (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: DATE <= F_DATA_FINAL
         GROUP BY CR.COD_CHECKLIST
     )

SELECT C.NOME_UNIDADE,
       CM.NOME,
       C.COD_CHECKLIST,
       C.DATA_REALIZACAO_CHECK,
       COALESCE(C.DATA_IMPORTADO, '-'),
       C.NOME_COLABORADOR,
       C.CPF,
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
         JOIN CHECKLIST_MODELO CM
              ON C.COD_CHECKLIST_MODELO = CM.CODIGO
         LEFT JOIN RESPOSTAS_NOK RN
                   ON C.COD_CHECKLIST = RN.COD_CHECKLIST
ORDER BY C.NOME_UNIDADE,
         C.DATA_HORA_SINCRONIZACAO DESC;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--########################################### INSERE DIAGRAMA ÔNIBUS BIARTICULADO ######################################
--######################################################################################################################
--######################################################################################################################
--PS-2133
-- Já lançado em prod, aqui apenas para histórico.

-- INSERT INTO VEICULO_DIAGRAMA (CODIGO,
--                               NOME,
--                               URL_IMAGEM)
-- VALUES (14,
--         'BIARTICULADO',
--         'WWW.GOOGLE.COM/BIARTICULADO');
--
-- INSERT INTO VEICULO_DIAGRAMA_EIXOS (COD_DIAGRAMA,
--                                     TIPO_EIXO,
--                                     POSICAO,
--                                     QT_PNEUS,
--                                     EIXO_DIRECIONAL)
-- VALUES (14,
--         'D',
--         1,
--         2,
--         TRUE),
--        (14,
--         'T',
--         2,
--         4,
--         FALSE),
--        (14,
--         'T',
--         3,
--         4,
--         FALSE),
--        (14,
--         'T',
--         4,
--         4,
--         FALSE);
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
-- PL-2291
-- Já foi lançado em prod, aqui apenas para histórico.

-- Renomeia constraint da tabela modelo_banda.
-- ALTER TABLE MODELO_BANDA DROP CONSTRAINT UNIQUE_MODELO_BANDA;
-- ALTER TABLE MODELO_BANDA ADD CONSTRAINT UNIQUE_NOME_MODELO_BANDA_POR_MARCA UNIQUE (COD_EMPRESA, COD_MARCA, NOME);
-- Adiciona nova constraint para poder usar como FK.
-- ALTER TABLE MODELO_BANDA ADD CONSTRAINT UNIQUE_MODELO_BANDA_EMPRESA UNIQUE (COD_EMPRESA, CODIGO);
​
-- Altera constraint de FK para usar cod_empresa.
-- ALTER TABLE PNEU_DATA DROP CONSTRAINT FK_PNEU_MODELO_BANDA;
-- ALTER TABLE PNEU_DATA ADD CONSTRAINT FK_PNEU_MODELO_BANDA FOREIGN KEY (COD_EMPRESA, COD_MODELO_BANDA)
--     REFERENCES MODELO_BANDA (COD_EMPRESA, CODIGO);
--######################################################################################################################
--######################################################################################################################
END TRANSACTION;