-- Sobre:
-- Esta função insere um IMEI a um dispositivo móvel.
--
-- Parâmetros:
-- F_COD_EMPRESA: Código da empresa.
-- F_COD_DISPOSITIVO: Código do dispositivo que será vinculado.
-- F_IMEI: Texto com o número do IMEI inserido pelo usuário.
--
-- Atenção: Esta função é utilizada dentro da function FUNC_DISPOSITIVO_INSERE_DISPOSITIVO_MOVEL_COM_IMEI.
--
-- Histórico:
-- 2019-07-25 -> Function criada (wvinim - PL-2150).
CREATE OR REPLACE FUNCTION FUNC_DISPOSITIVO_INSERE_IMEI(
  F_COD_EMPRESA     BIGINT,
  F_COD_DISPOSITIVO BIGINT,
  F_IMEI            TEXT)
  RETURNS VOID
LANGUAGE PLPGSQL
AS $$
DECLARE
  COD_IMEI_INSERIDO BIGINT;
BEGIN
  -- Verifica se o dispositivo móvel existe no banco.
  IF (SELECT NOT EXISTS(
                   SELECT CODIGO FROM DISPOSITIVO_MOVEL WHERE CODIGO = F_COD_DISPOSITIVO
                                                          AND COD_EMPRESA = F_COD_EMPRESA))
  THEN
    PERFORM THROW_GENERIC_ERROR(
              'Não foi possível encontrar este dispositivo móvel, confira o código e tente novamente');
  END IF;

  -- Verifica se o IMEI já existe no banco.
  IF (SELECT EXISTS(SELECT IMEI FROM DISPOSITIVO_MOVEL_IMEI WHERE IMEI = F_IMEI AND COD_EMPRESA = F_COD_EMPRESA))
  THEN
    PERFORM THROW_GENERIC_ERROR(FORMAT('O IMEI %s já está vinculado a outro dispositivo', F_IMEI));
  END IF;

  -- Insere o registro de IMEI
  INSERT INTO DISPOSITIVO_MOVEL_IMEI (COD_EMPRESA, COD_DISPOSITIVO, IMEI)
  VALUES (F_COD_EMPRESA, F_COD_DISPOSITIVO, F_IMEI)
      RETURNING CODIGO
        INTO COD_IMEI_INSERIDO;

  -- Verificamos se o insert funcionou.
  IF COD_IMEI_INSERIDO <= 0
  THEN
    PERFORM THROW_GENERIC_ERROR(
              'Não foi possível inserir o número IMEI, tente novamente');
  END IF;
END;
$$;