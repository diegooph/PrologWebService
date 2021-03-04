-- Sobre:
-- Esta função insere um dispositivo móvel e retorna o respectivo código.
--
-- Parâmetros:
-- F_COD_EMPRESA: Código da empresa.
-- F_COD_MARCA: Código da marca (MARCA_DISPOSITIVO_MOVEL_PROLOG).
-- F_MODELO: Texto de nome do modelo inserido pelo usuário (Ex: Galaxy S10).
-- F_DESCRICAO: Texto de descrição do dispositivo inserido pelo usuário.
--
-- Atenção: Esta função é utilizada dentro da function FUNC_DISPOSITIVO_INSERE_DISPOSITIVO_MOVEL_COM_IMEI.
--
-- Histórico:
-- 2019-07-25 -> Function criada (wvinim - PL-2150).
CREATE OR REPLACE FUNCTION FUNC_DISPOSITIVO_INSERE_DISPOSITIVO_MOVEL(
  F_COD_EMPRESA BIGINT,
  F_COD_MARCA   BIGINT,
  F_MODELO      TEXT,
  F_DESCRICAO   TEXT)
  RETURNS BIGINT
LANGUAGE PLPGSQL
AS $$
DECLARE
  COD_DISPOSITIVO_INSERIDO BIGINT;
BEGIN
  -- Verifica se a empresa existe
  PERFORM FUNC_GARANTE_EMPRESA_EXISTE(F_COD_EMPRESA);

  -- Verifica se a marca do dispositivo móvel existe no banco.
  IF ((F_COD_MARCA IS NOT NULL) AND (SELECT NOT EXISTS(
                   SELECT MDP.CODIGO AS COD_MARCA, MDP.NOME :: TEXT AS NOME_MARCA
                   FROM DISPOSITIVO_MOVEL_MARCA_PROLOG MDP
                   WHERE MDP.CODIGO = F_COD_MARCA)))
  THEN
    PERFORM THROW_GENERIC_ERROR(
              'Não foi possível encontrar esta marca do dispositivo móvel, confira a marca e tente novamente');
  END IF;

  -- Insere o registro de IMEI.
  INSERT INTO DISPOSITIVO_MOVEL (COD_EMPRESA, COD_MARCA, MODELO, DESCRICAO)
  VALUES (F_COD_EMPRESA, F_COD_MARCA, TRIM_AND_REMOVE_EXTRA_SPACES(F_MODELO), TRIM_AND_REMOVE_EXTRA_SPACES(F_DESCRICAO))
      RETURNING CODIGO
        INTO COD_DISPOSITIVO_INSERIDO;

  -- Verificamos se o insert funcionou.
  IF COD_DISPOSITIVO_INSERIDO <= 0
  THEN
    PERFORM THROW_GENERIC_ERROR(
              'Não foi possível inserir o dispositivo móvel, tente novamente');
  END IF;

  RETURN COD_DISPOSITIVO_INSERIDO;
END;
$$;