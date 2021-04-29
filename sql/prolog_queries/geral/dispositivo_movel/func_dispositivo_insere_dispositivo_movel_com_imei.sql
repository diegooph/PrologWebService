CREATE OR REPLACE FUNCTION FUNC_DISPOSITIVO_INSERE_DISPOSITIVO_MOVEL_COM_IMEI(
  F_COD_EMPRESA BIGINT,
  F_COD_MARCA   BIGINT,
  F_MODELO      TEXT,
  F_DESCRICAO   TEXT,
  F_IMEI        TEXT [])
  RETURNS BIGINT
LANGUAGE PLPGSQL
AS $$
DECLARE
  COD_DISPOSITIVO_INSERIDO BIGINT;
  IMEI                     TEXT;
BEGIN
  -- Verifica se a empresa existe
  PERFORM FUNC_GARANTE_EMPRESA_EXISTE(F_COD_EMPRESA);

  -- Insere o dispositivo móvel
  COD_DISPOSITIVO_INSERIDO := FUNC_DISPOSITIVO_INSERE_DISPOSITIVO_MOVEL(F_COD_EMPRESA,
                                                                        F_COD_MARCA,
                                                                        F_MODELO,
                                                                        F_DESCRICAO);

  -- Verificamos se o insert funcionou.
  IF COD_DISPOSITIVO_INSERIDO <= 0
  THEN
    PERFORM THROW_GENERIC_ERROR(
              'Não foi possível inserir o dispositivo móvel, tente novamente');
  END IF;

  -- Insere os números de IMEI
  FOREACH IMEI IN ARRAY F_IMEI
  LOOP
    PERFORM FUNC_DISPOSITIVO_INSERE_IMEI(F_COD_EMPRESA, COD_DISPOSITIVO_INSERIDO, IMEI);
  END LOOP;

  RETURN COD_DISPOSITIVO_INSERIDO;
END;
$$;