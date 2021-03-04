-- Sobre:
--
-- Function para buscar um modelo de checklist na versão atual para realização.
--
-- Histórico:
-- 2020-03-03 -> Atualização de arquivo e documentação (wvinim - PL-2494).
-- 2020-07-07 -> Adição das informações de parametrização de fotos (luiz_fp - PL-2705).
-- 2020-07-13 -> Atualização de arquivo e documentação (wvinim - PL-2824).
-- 2020-07-24 -> Insere código auxiliar (gustavocnp95 - PLI-178).
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_GET_MODELO_REALIZACAO(F_COD_MODELO_CHECKLIST BIGINT,
                                                                F_COD_VEICULO_REALIZACAO BIGINT)
    RETURNS TABLE
            (
                COD_UNIDADE_MODELO_CHECKLIST BIGINT,
                COD_MODELO_CHECKLIST         BIGINT,
                COD_VERSAO_MODELO_CHECKLIST  BIGINT,
                NOME_MODELO_CHECKLIST        TEXT,
                COD_PERGUNTA                 BIGINT,
                DESCRICAO_PERGUNTA           TEXT,
                COD_IMAGEM                   BIGINT,
                URL_IMAGEM                   TEXT,
                PERGUNTA_ORDEM_EXIBICAO      INTEGER,
                SINGLE_CHOICE                BOOLEAN,
                ANEXO_MIDIA_RESPOSTA_OK      TEXT,
                COD_ALTERNATIVA              BIGINT,
                DESCRICAO_ALTERNATIVA        TEXT,
                TIPO_OUTROS                  BOOLEAN,
                ALTERNATIVA_ORDEM_EXIBICAO   INTEGER,
                PRIORIDADE_ALTERNATIVA       TEXT,
                KM_ATUAL_VEICULO_REALIZACAO  BIGINT,
                ANEXO_MIDIA                  TEXT
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    KM_ATUAL_VEICULO CONSTANT BIGINT := (SELECT V.KM
                                         FROM VEICULO V
                                         WHERE V.CODIGO = F_COD_VEICULO_REALIZACAO);
BEGIN
    IF KM_ATUAL_VEICULO IS NULL
    THEN
        RAISE EXCEPTION 'Erro ao buscar KM atual do veículo para realização do checklist!';
    END IF;

    RETURN QUERY
        SELECT CM.COD_UNIDADE              AS COD_UNIDADE_MODELO_CHECKLIST,
               CM.CODIGO                   AS COD_MODELO_CHECKLIST,
               CM.COD_VERSAO_ATUAL         AS COD_VERSAO_MODELO_CHECKLIST,
               CM.NOME :: TEXT             AS NOME_MODELO_CHECKLIST,
               CP.CODIGO                   AS COD_PERGUNTA,
               CP.PERGUNTA                 AS DESCRICAO_PERGUNTA,
               CP.COD_IMAGEM               AS COD_IMAGEM,
               CGI.URL_IMAGEM              AS URL_IMAGEM,
               CP.ORDEM                    AS PERGUNTA_ORDEM_EXIBICAO,
               CP.SINGLE_CHOICE            AS SINGLE_CHOICE,
               CP.ANEXO_MIDIA_RESPOSTA_OK  AS ANEXO_MIDIA_RESPOSTA_OK,
               CAP.CODIGO                  AS COD_ALTERNATIVA,
               CAP.ALTERNATIVA             AS DESCRICAO_ALTERNATIVA,
               CAP.ALTERNATIVA_TIPO_OUTROS AS TIPO_OUTROS,
               CAP.ORDEM                   AS ALTERNATIVA_ORDEM_EXIBICAO,
               CAP.PRIORIDADE :: TEXT      AS PRIORIDADE_ALTERNATIVA,
               KM_ATUAL_VEICULO            AS KM_ATUAL_VEICULO_REALIZACAO,
               CAP.ANEXO_MIDIA             AS ANEXO_MIDIA
        FROM CHECKLIST_MODELO CM
                 JOIN CHECKLIST_PERGUNTAS CP
                      ON CM.CODIGO = CP.COD_CHECKLIST_MODELO AND
                         CM.COD_VERSAO_ATUAL = CP.COD_VERSAO_CHECKLIST_MODELO
                 JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
                      ON CP.CODIGO = CAP.COD_PERGUNTA AND CM.COD_VERSAO_ATUAL = CAP.COD_VERSAO_CHECKLIST_MODELO
            -- Precisamos que seja LEFT JOIN para o caso de perguntas sem imagem associada.
                 LEFT JOIN CHECKLIST_GALERIA_IMAGENS CGI
                           ON CP.COD_IMAGEM = CGI.COD_IMAGEM
        WHERE CM.CODIGO = F_COD_MODELO_CHECKLIST
          AND CM.STATUS_ATIVO
        ORDER BY COD_MODELO_CHECKLIST,
                 PERGUNTA_ORDEM_EXIBICAO,
                 COD_PERGUNTA,
                 ALTERNATIVA_ORDEM_EXIBICAO,
                 COD_ALTERNATIVA;
END ;
$$;