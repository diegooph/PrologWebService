-- Sobre:
--
-- Function que realiza a busca dos checklists realizados por colaborador.
--
-- Histórico:
-- 2020-06-03 -> Atualização do arquivo específico (wvinim - PL-2773).
-- 2020-07-07 -> Altera nome da function antiga para manter a compatibilidade (wvinim - PL-2824).
-- 2020-07-13 -> Criação de arquivo e documentação (wvinim - PL-2824).
-- 2020-10-14 -> Adiciona observação ao retorno (luizfp).
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_GET_REALIZADOS_BY_COLABORADOR_DEPRECATED(F_CPF_COLABORADOR BIGINT,
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
                TIPO_CHECKLIST                CHARACTER,
                NOME_COLABORADOR              TEXT,
                TOTAL_ITENS_OK                SMALLINT,
                TOTAL_ITENS_NOK               SMALLINT,
                OBSERVACAO                    TEXT
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    F_HAS_DATA_INICIAL INTEGER := CASE WHEN F_DATA_INICIAL IS NULL THEN 1 ELSE 0 END;
    F_HAS_DATA_FINAL   INTEGER := CASE WHEN F_DATA_FINAL IS NULL THEN 1 ELSE 0 END;
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
               C.TOTAL_PERGUNTAS_NOK                                AS TOTAL_ITENS_NOK,
               C.OBSERVACAO                                         AS OBSERVACAO
        FROM CHECKLIST C
                 JOIN COLABORADOR CO
                      ON CO.CPF = C.CPF_COLABORADOR
        WHERE C.CPF_COLABORADOR = F_CPF_COLABORADOR
          AND (F_HAS_DATA_INICIAL = 1 OR (C.DATA_HORA AT TIME ZONE F_TIMEZONE) :: DATE >= F_DATA_INICIAL)
          AND (F_HAS_DATA_FINAL = 1 OR (C.DATA_HORA AT TIME ZONE F_TIMEZONE) :: DATE <= F_DATA_FINAL)
        ORDER BY C.DATA_HORA DESC
        LIMIT F_LIMIT OFFSET F_OFFSET;
END;
$$;