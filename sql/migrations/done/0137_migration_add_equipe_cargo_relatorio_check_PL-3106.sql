-- PL-3106
DROP FUNCTION FUNC_CHECKLIST_RELATORIO_RESUMO_REALIZADOS(F_COD_UNIDADES BIGINT[],
    F_PLACA_VEICULO TEXT,
    F_DATA_INICIAL DATE,
    F_DATA_FINAL DATE);

-- 2020-08-27 -> Adiciona equipe e cargo do colaborador (luiz_fp - PL-3106).
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
                "EQUIPE"                      TEXT,
                "CARGO"                       TEXT,
                "PLACA"                       TEXT,
                "TIPO DE VEÍCULO"             TEXT,
                "KM"                          BIGINT,
                "TEMPO REALIZAÇÃO (SEGUNDOS)" BIGINT,
                "TIPO"                        TEXT,
                "TOTAL DE PERGUNTAS"          SMALLINT,
                "TOTAL NOK"                   BIGINT,
                "TOTAL IMAGENS PERGUNTAS"     SMALLINT,
                "TOTAL IMAGENS ALTERNATIVAS"  SMALLINT,
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
       E.NOME                                                 AS EQUIPE_COLABORADOR,
       F.NOME                                                 AS CARGO_COLABORADOR,
       C.PLACA_VEICULO                                        AS PLACA_VEICULO,
       VT.NOME                                                AS TIPO_VEICULO,
       C.KM_VEICULO                                           AS KM_VEICULO,
       C.TEMPO_REALIZACAO / 1000                              AS TEMPO_REALIZACAO_SEGUNDOS,
       F_IF(C.TIPO = 'S', 'Saída' :: TEXT, 'Retorno' :: TEXT) AS TIPO_CHECKLIST,
       C.TOTAL_PERGUNTAS_OK + C.TOTAL_PERGUNTAS_NOK           AS TOTAL_PERGUNTAS,
       (SELECT COUNT(*)
        FROM CHECKLIST_RESPOSTAS_NOK CRN
        WHERE CRN.COD_CHECKLIST = C.CODIGO)                   AS TOTAL_NOK,
       COALESCE(C.TOTAL_MIDIAS_PERGUNTAS_OK, 0)::SMALLINT     AS TOTAL_MIDIAS_PERGUNTAS,
       COALESCE(C.TOTAL_MIDIAS_ALTERNATIVAS_NOK, 0)::SMALLINT AS TOTAL_MIDIAS_ALTERNATIVAS,
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
              ON CP.COD_VERSAO_CHECKLIST_MODELO = C.COD_VERSAO_CHECKLIST_MODELO
         JOIN COLABORADOR CO
              ON C.CPF_COLABORADOR = CO.CPF
         JOIN EQUIPE E
              ON CO.COD_EQUIPE = E.CODIGO
        JOIN FUNCAO F
              ON CO.COD_FUNCAO = F.CODIGO
         JOIN UNIDADE U
              ON C.COD_UNIDADE = U.CODIGO
         JOIN CHECKLIST_MODELO CM ON CM.CODIGO = C.COD_CHECKLIST_MODELO
         JOIN VEICULO V ON V.PLACA = C.PLACA_VEICULO
         JOIN VEICULO_TIPO VT ON VT.CODIGO = V.COD_TIPO
WHERE C.COD_UNIDADE = ANY (F_COD_UNIDADES)
  AND C.DATA_HORA_REALIZACAO_TZ_APLICADO :: DATE >= F_DATA_INICIAL
  AND C.DATA_HORA_REALIZACAO_TZ_APLICADO :: DATE <= F_DATA_FINAL
  AND (F_PLACA_VEICULO = '%' OR C.PLACA_VEICULO LIKE F_PLACA_VEICULO)
GROUP BY C.CODIGO,
         CM.NOME,
         C.TOTAL_PERGUNTAS_OK,
         C.TOTAL_MIDIAS_PERGUNTAS_OK,
         C.TOTAL_MIDIAS_ALTERNATIVAS_NOK,
         C.TOTAL_PERGUNTAS_NOK,
         U.CODIGO,
         U.NOME,
         CO.NOME,
         CO.CPF,
         E.NOME,
         F.NOME,
         C.DATA_HORA,
         C.DATA_HORA_REALIZACAO_TZ_APLICADO,
         C.DATA_HORA_IMPORTADO_PROLOG,
         C.DATA_HORA_SINCRONIZACAO,
         C.COD_UNIDADE,
         C.PLACA_VEICULO,
         VT.NOME,
         C.KM_VEICULO,
         C.TEMPO_REALIZACAO,
         C.TIPO
ORDER BY U.NOME,
         C.DATA_HORA_SINCRONIZACAO DESC;
$$;