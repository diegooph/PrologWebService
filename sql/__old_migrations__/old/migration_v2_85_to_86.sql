BEGIN TRANSACTION ;

-- Cria function para novas buscas da listagem de cargos.
--######################################################################################################################
--######################################################################################################################
-- Aproveitamos e criamos FK na tabela CARGO_FUNCAO_PROLOG_V11 que não existia.
ALTER TABLE CARGO_FUNCAO_PROLOG_V11
  ADD CONSTRAINT FK_CARGO_FUNCAO_PROLOG_CARGO FOREIGN KEY (COD_FUNCAO_COLABORADOR) REFERENCES FUNCAO (CODIGO);

CREATE OR REPLACE FUNCTION FUNC_CARGOS_GET_CARGOS_EM_USO(
  F_COD_UNIDADE BIGINT)
  RETURNS TABLE(
    COD_CARGO                    BIGINT,
    NOME_CARGO                   TEXT,
    QTD_COLABORADORES_VINCULADOS BIGINT,
    QTD_PERMISSOES_VINCULADAS    BIGINT)
LANGUAGE PLPGSQL
AS $$
DECLARE
  PILARES_LIBERADOS_UNIDADE BIGINT [] := (SELECT ARRAY_AGG(UPP.COD_PILAR)
                                          FROM UNIDADE_PILAR_PROLOG UPP
                                          WHERE UPP.COD_UNIDADE = F_COD_UNIDADE);
BEGIN
  RETURN QUERY
  WITH CARGOS_EM_USO AS (
      SELECT DISTINCT COD_FUNCAO
      FROM COLABORADOR C
      WHERE C.COD_UNIDADE = F_COD_UNIDADE
  )

  SELECT
    F.CODIGO                                                                    AS COD_CARGO,
    F.NOME :: TEXT                                                              AS NOME_CARGO,
    (SELECT COUNT(*)
     FROM COLABORADOR C
     WHERE C.COD_FUNCAO = F.CODIGO
           AND C.COD_UNIDADE = F_COD_UNIDADE)                                   AS QTD_COLABORADORES_VINCULADOS,
    -- Se não tivesse esse FILTER, cargos que não possuem nenhuma permissão vinculada retornariam 1.
    COUNT(*)
      FILTER (WHERE CFP.COD_UNIDADE IS NOT NULL
                    AND CFP.COD_PILAR_PROLOG = ANY (PILARES_LIBERADOS_UNIDADE)) AS QTD_PERMISSOES_VINCULADAS
  FROM FUNCAO F
    LEFT JOIN CARGO_FUNCAO_PROLOG_V11 CFP
      ON F.CODIGO = CFP.COD_FUNCAO_COLABORADOR
         AND CFP.COD_UNIDADE = F_COD_UNIDADE
  -- Não podemos simplesmente filtrar pelo código da unidade presente na tabela CARGO_FUNCAO_PROLOG_V11, pois desse
  -- modo iríamos remover do retorno cargos usados mas sem permissões vinculadas. Por isso utilizamos esse modo de
  -- filtragem com a CTE criada acima.
  WHERE F.CODIGO IN (SELECT *
                     FROM CARGOS_EM_USO)
  GROUP BY F.CODIGO
  ORDER BY F.NOME ASC;
END;
$$;


CREATE OR REPLACE FUNCTION FUNC_CARGOS_GET_CARGOS_NAO_UTILIZADOS(
  F_COD_UNIDADE BIGINT)
  RETURNS TABLE(
    COD_CARGO                 BIGINT,
    NOME_CARGO                TEXT,
    QTD_PERMISSOES_VINCULADAS BIGINT)
LANGUAGE PLPGSQL
AS $$
DECLARE
  PILARES_LIBERADOS_UNIDADE BIGINT [] := (SELECT ARRAY_AGG(UPP.COD_PILAR)
                                          FROM UNIDADE_PILAR_PROLOG UPP
                                          WHERE UPP.COD_UNIDADE = F_COD_UNIDADE);
BEGIN
  RETURN QUERY
  WITH CARGOS_EM_USO AS (
      SELECT DISTINCT COD_FUNCAO
      FROM COLABORADOR C
      WHERE C.COD_UNIDADE = F_COD_UNIDADE
  )

  SELECT
    F.CODIGO                                                                    AS COD_CARGO,
    F.NOME :: TEXT                                                              AS NOME_CARGO,
    COUNT(*)
      FILTER (WHERE CFP.COD_UNIDADE IS NOT NULL
                    AND CFP.COD_PILAR_PROLOG = ANY (PILARES_LIBERADOS_UNIDADE)) AS QTD_PERMISSOES_VINCULADAS
  FROM FUNCAO F
    LEFT JOIN CARGO_FUNCAO_PROLOG_V11 CFP
      ON F.CODIGO = CFP.COD_FUNCAO_COLABORADOR
         AND CFP.COD_UNIDADE = F_COD_UNIDADE
  -- Para buscar os cargos não utilizados, adotamos a lógica de buscar todos os da empresa e depois remover os que
  -- tem colaboradores vinculados, isso é feito nas duas condições abaixo do WHERE.
  WHERE F.COD_EMPRESA = (SELECT U.COD_EMPRESA
                         FROM UNIDADE U
                         WHERE U.CODIGO = F_COD_UNIDADE)
        AND F.CODIGO NOT IN (SELECT *
                             FROM CARGOS_EM_USO)
  GROUP BY F.CODIGO
  ORDER BY F.NOME ASC;
END;
$$;


CREATE OR REPLACE FUNCTION FUNC_CARGOS_GET_TODOS_CARGOS(
  F_COD_UNIDADE BIGINT)
  RETURNS TABLE(
    COD_CARGO  BIGINT,
    NOME_CARGO TEXT)
LANGUAGE PLPGSQL
AS $$
BEGIN
  RETURN QUERY
  SELECT DISTINCT
    F.CODIGO       AS COD_CARGO,
    F.NOME :: TEXT AS NOME_CARGO
  FROM FUNCAO F
    JOIN UNIDADE U
      ON U.COD_EMPRESA = F.COD_EMPRESA
  WHERE U.CODIGO = F_COD_UNIDADE
  ORDER BY 2 ASC;
END;
$$;
--######################################################################################################################
--######################################################################################################################

END TRANSACTION ;