-- Sobre:
-- Esta função edita um dipositivo móvel e seus IMEIs, utilizando funções auxiliares.
--
-- Atenção: A lista de IMEIs é deletada e gerada novamente toda vez que o dispositivo é editado.
--
-- Parâmetros:
-- F_COD_EMPRESA: Código da empresa.
-- F_COD_DISPOSITIVO: Código do dispositivo.
-- F_COD_MARCA: Código da marca (MARCA_DISPOSITIVO_MOVEL_PROLOG).
-- F_MODELO: Texto de nome do modelo inserido pelo usuário (Ex: Galaxy S10).
-- F_DESCRICAO: Texto de descrição do dispositivo inserido pelo usuário.
-- F_IMEI: Array de texto contendo a lista com os IMEIs inseridos pelo usuário.
--
-- Funções auxiliares:
-- FUNC_DISPOSITIVO_INSERE_IMEI.
--
-- Histórico:
-- 2019-07-26 -> Function criada (wvinim - PL-2150).
CREATE OR REPLACE FUNCTION FUNC_DISPOSITIVO_EDITA_DISPOSITIVO_MOVEL(
  F_COD_EMPRESA     BIGINT,
  F_COD_DISPOSITIVO BIGINT,
  F_COD_MARCA       BIGINT,
  F_MODELO          TEXT,
  F_DESCRICAO       TEXT,
  F_IMEI            TEXT [])
  RETURNS BIGINT
LANGUAGE PLPGSQL
AS $$
DECLARE
  QTD_LINHAS_ATUALIZADAS BIGINT;
  IMEI                   TEXT;
BEGIN
  -- Verifica se a empresa existe.
  PERFORM FUNC_GARANTE_EMPRESA_EXISTE(F_COD_EMPRESA);

  IF (SELECT NOT EXISTS(
                   SELECT DM.CODIGO
                   FROM DISPOSITIVO_MOVEL DM
                   WHERE DM.CODIGO = F_COD_DISPOSITIVO
                     AND DM.COD_EMPRESA = F_COD_EMPRESA))
  THEN
    PERFORM THROW_GENERIC_ERROR(
              'Não foi possível encontrar este dispositivo móvel, confira se ele está na listagem e tente novamente');
  END IF;

  -- Verifica se a marca do dispositivo móvel existe no banco.
  IF ((F_COD_MARCA IS NOT NULL) AND (SELECT NOT EXISTS(
                   SELECT MDP.CODIGO AS COD_MARCA, MDP.NOME :: TEXT AS NOME_MARCA
                   FROM DISPOSITIVO_MOVEL_MARCA_PROLOG MDP
                   WHERE MDP.CODIGO = F_COD_MARCA)))
  THEN
    PERFORM THROW_GENERIC_ERROR(
              'Não foi possível encontrar esta marca do dispositivo móvel, confira a marca e tente novamente');
  END IF;

  -- Edita o registro do dispositivo móvel.
  UPDATE DISPOSITIVO_MOVEL
  SET COD_MARCA = F_COD_MARCA,
      MODELO    = TRIM_AND_REMOVE_EXTRA_SPACES(F_MODELO),
      DESCRICAO = TRIM_AND_REMOVE_EXTRA_SPACES(F_DESCRICAO)
  WHERE COD_EMPRESA = F_COD_EMPRESA
    AND CODIGO = F_COD_DISPOSITIVO;

  GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

  IF (QTD_LINHAS_ATUALIZADAS <= 0)
  THEN
    PERFORM THROW_GENERIC_ERROR(
              'Erro ao editar o dispositivo móvel, tente novamente');
  END IF;

  -- Deleta todos os números de IMEI anteriores
  DELETE FROM DISPOSITIVO_MOVEL_IMEI WHERE COD_EMPRESA = F_COD_EMPRESA
                                       AND COD_DISPOSITIVO = F_COD_DISPOSITIVO;

  -- Insere os números de IMEI
  FOREACH IMEI IN ARRAY F_IMEI
  LOOP
    PERFORM FUNC_DISPOSITIVO_INSERE_IMEI(F_COD_EMPRESA, F_COD_DISPOSITIVO, IMEI);
  END LOOP;

  RETURN QTD_LINHAS_ATUALIZADAS;
END;
$$;