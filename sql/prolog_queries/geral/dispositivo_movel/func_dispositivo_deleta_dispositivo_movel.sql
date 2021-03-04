-- Sobre:
-- Esta função deleta um dipositivo móvel e seus IMEIs.
--
-- Parâmetros:
-- F_COD_EMPRESA: Código da empresa.
-- F_COD_DISPOSITIVO: Código do dispositivo.
--
-- Histórico:
-- 2019-07-26 -> Function criada (wvinim - PL-2150).
CREATE OR REPLACE FUNCTION FUNC_DISPOSITIVO_DELETA_DISPOSITIVO_MOVEL(F_COD_EMPRESA BIGINT, F_COD_DISPOSITIVO BIGINT)
  RETURNS BIGINT
LANGUAGE PLPGSQL
AS $$
DECLARE
  QTD_LINHAS_DELETADAS BIGINT;
BEGIN
  IF (SELECT NOT EXISTS(SELECT CODIGO FROM DISPOSITIVO_MOVEL WHERE COD_EMPRESA = F_COD_EMPRESA
                                                               AND CODIGO = F_COD_DISPOSITIVO))
  THEN
    PERFORM THROW_GENERIC_ERROR(
              'Erro ao deletar, dispositivo móvel não encontrado');
  END IF;

  -- Deleta todos os números de IMEI anteriores
  DELETE FROM DISPOSITIVO_MOVEL_IMEI WHERE COD_EMPRESA = F_COD_EMPRESA
                                       AND COD_DISPOSITIVO = F_COD_DISPOSITIVO;

  -- Deleta o dispositivo móvel.
  DELETE FROM DISPOSITIVO_MOVEL WHERE COD_EMPRESA = F_COD_EMPRESA
                                  AND CODIGO = F_COD_DISPOSITIVO;

  GET DIAGNOSTICS QTD_LINHAS_DELETADAS = ROW_COUNT;

  IF (QTD_LINHAS_DELETADAS <= 0)
  THEN
    PERFORM THROW_GENERIC_ERROR('Erro ao deletar o dispositivo móvel, tente novamente');
  END IF;
  --
  --
  RETURN QTD_LINHAS_DELETADAS;
END;
$$;