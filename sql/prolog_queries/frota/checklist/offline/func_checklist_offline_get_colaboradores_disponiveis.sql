--######################################################################################################################
--######################################################################################################################
--#######################    FUNCTION PARA BUSCAR OS COLABORADORES COM          ########################################
--#######################           ACESSO A REALIZAÇÃO DE CHECKLIST            ########################################
--######################################################################################################################
--######################################################################################################################
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_OFFLINE_GET_COLABORADORES_DISPONIVEIS(F_COD_UNIDADE BIGINT)
  RETURNS TABLE(
    COD_UNIDADE_COLABORADOR   BIGINT,
    COD_COLABORADOR           BIGINT,
    NOME_COLABORADOR          TEXT,
    CPF_COLABORADOR           TEXT,
    DATA_NASCIMENTO           DATE,
    COD_CARGO_COLABORADOR     INTEGER,
    COD_PERMISSAO_COLABORADOR INTEGER)
LANGUAGE PLPGSQL
AS $$
BEGIN
  RETURN QUERY
  SELECT
    C.COD_UNIDADE :: BIGINT      AS COD_UNIDADE_COLABORADOR,
    C.CODIGO                     AS COD_COLABORADOR,
    C.NOME :: TEXT               AS NOME_COLABORADOR,
    LPAD(C.CPF :: TEXT, 11, '0') AS CPF_COLABORADOR,
    C.DATA_NASCIMENTO :: DATE    AS DATA_NASCIMENTO,
    C.COD_FUNCAO                 AS COD_CARGO_COLABORADOR,
    C.COD_PERMISSAO :: INTEGER   AS COD_PERMISSAO_COLABORADOR
  FROM COLABORADOR C
  WHERE C.COD_UNIDADE = F_COD_UNIDADE
        AND C.STATUS_ATIVO
        -- Apenas colaboradores que possuem funções associadas a modelos de checklist ativos.
        AND C.COD_FUNCAO IN (SELECT CMF.COD_FUNCAO
                             FROM CHECKLIST_MODELO CM
                               JOIN CHECKLIST_MODELO_FUNCAO CMF
                                 ON CM.CODIGO = CMF.COD_CHECKLIST_MODELO
                             WHERE CM.COD_UNIDADE = F_COD_UNIDADE
                                   AND CM.STATUS_ATIVO = TRUE);
END;
$$;