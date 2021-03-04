--######################################################################################################################
--######################################################################################################################
--#############################          FUNCTION PARA INSERIR CARGOS           ########################################
--######################################################################################################################
--######################################################################################################################
CREATE OR REPLACE FUNCTION FUNC_CARGOS_INSERE_CARGO(
  F_COD_EMPRESA BIGINT,
  F_NOME_CARGO  TEXT,
  F_TOKEN       TEXT)
  RETURNS BIGINT
LANGUAGE PLPGSQL
AS $$
DECLARE
  F_COD_COLABORADOR_UPDATE BIGINT := (SELECT COD_COLABORADOR
                                      FROM TOKEN_AUTENTICACAO
                                      WHERE TOKEN = F_TOKEN);
  COD_CARGO_INSERIDO       BIGINT;
BEGIN
  IF F_COD_COLABORADOR_UPDATE IS NULL OR F_COD_COLABORADOR_UPDATE <= 0
  THEN
    PERFORM THROW_GENERIC_ERROR(
        'Não foi possível validar sua sessão, por favor, faça login novamente');
  END IF;

  INSERT INTO FUNCAO_DATA (COD_EMPRESA, NOME, COD_COLABORADOR_UPDATE)
  VALUES (F_COD_EMPRESA, TRIM_AND_REMOVE_EXTRA_SPACES(F_NOME_CARGO), F_COD_COLABORADOR_UPDATE)
  RETURNING CODIGO
    INTO COD_CARGO_INSERIDO;

  -- Verificamos se o insert funcionou.
  IF COD_CARGO_INSERIDO <= 0
  THEN
    PERFORM THROW_GENERIC_ERROR(
        'Não foi possível inserir o cargo, tente novamente');
  END IF;

  RETURN COD_CARGO_INSERIDO;
END;
$$;