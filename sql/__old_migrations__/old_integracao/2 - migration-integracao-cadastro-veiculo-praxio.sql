BEGIN TRANSACTION;

--######################################################################################################################
--######################################################################################################################
-- PL-2132
CREATE TABLE IF NOT EXISTS INTEGRACAO.VEICULO_CADASTRADO(
  COD_EMPRESA_CADASTRO        BIGINT NOT NULL,
  COD_UNIDADE_CADASTRO        BIGINT NOT NULL,
  COD_VEICULO_CADASTRO_PROLOG BIGINT NOT NULL,
  PLACA_VEICULO_CADASTRO      TEXT NOT NULL,
  DATA_HORA_CADASTRO_PROLOG   TIMESTAMP WITH TIME ZONE NOT NULL,
  DATA_HORA_ULTIMA_EDICAO     TIMESTAMP WITH TIME ZONE,
  CONSTRAINT UNIQUE_PLACA_CADASTRADA_EMPRESA_INTEGRACAO UNIQUE (COD_EMPRESA_CADASTRO, PLACA_VEICULO_CADASTRO),
  CONSTRAINT FK_VEICULO_CADASTRO_EMPRESA FOREIGN KEY (COD_EMPRESA_CADASTRO) REFERENCES EMPRESA(CODIGO),
  CONSTRAINT FK_VEICULO_CADASTRO_UNIDADE FOREIGN KEY (COD_UNIDADE_CADASTRO) REFERENCES UNIDADE(CODIGO),
  CONSTRAINT FK_VEICULO_CADASTRO_VEICULO FOREIGN KEY (COD_VEICULO_CADASTRO_PROLOG) REFERENCES VEICULO_DATA(CODIGO)
);
COMMENT ON TABLE INTEGRACAO.VEICULO_CADASTRADO
IS 'Tabela utilizada para salvar os veículos que foram cadastrados no ProLog a partir de integrações';


--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
-- FUNCTION PARA SALVAR UM VEÍCULO NO PROLOG
CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_VEICULO_INSERE_VEICULO_PROLOG(
  F_COD_UNIDADE_VEICULO_ALOCADO   BIGINT,
  F_PLACA_VEICULO_CADASTRADO      TEXT,
  F_KM_ATUAL_VEICULO_CADASTRADO   BIGINT,
  F_COD_MODELO_VEICULO_CADASTRADO BIGINT,
  F_COD_TIPO_VEICULO_CADASTRADO   BIGINT,
  F_DATA_HORA_VEICULO_CADASTRO    TIMESTAMP WITH TIME ZONE,
  F_TOKEN_INTEGRACAO              TEXT)
  RETURNS BIGINT
LANGUAGE PLPGSQL
AS $$
DECLARE
  COD_EMPRESA_VEICULO  BIGINT  := (SELECT TI.COD_EMPRESA
                                   FROM INTEGRACAO.TOKEN_INTEGRACAO TI
                                   WHERE TI.TOKEN_INTEGRACAO = F_TOKEN_INTEGRACAO);
  STATUS_VEICULO       BOOLEAN := TRUE;
  COD_VEICULO_PROLOG   BIGINT;
  F_QTD_ROWS_ALTERADAS BIGINT;
BEGIN

  -- Validamos se a Unidade pertence a mesma empresa do token
  IF ((SELECT U.COD_EMPRESA FROM PUBLIC.UNIDADE U WHERE U.CODIGO = F_COD_UNIDADE_VEICULO_ALOCADO)
      <>
      (SELECT TI.COD_EMPRESA FROM INTEGRACAO.TOKEN_INTEGRACAO TI
      WHERE TI.TOKEN_INTEGRACAO = F_TOKEN_INTEGRACAO))
  THEN
    PERFORM PUBLIC.THROW_GENERIC_ERROR(
        FORMAT('[ERRO DE VÍNCULO] O token "%s" não está autorizado a inserir dados da unidade "%s", verificar vinculos',
               F_TOKEN_INTEGRACAO,
               F_COD_UNIDADE_VEICULO_ALOCADO));
  END IF;

  -- Validamos se a placa já existe no ProLog
  IF (SELECT EXISTS(SELECT V.CODIGO FROM PUBLIC.VEICULO_DATA V WHERE V.PLACA::TEXT = F_PLACA_VEICULO_CADASTRADO))
  THEN
    PERFORM PUBLIC.THROW_GENERIC_ERROR(
        FORMAT('[ERRO DE DADOS] A placa "%s" já está cadastrada no Sistema ProLog', F_PLACA_VEICULO_CADASTRADO));
  END IF;

  -- Validamos se o KM foi inputado corretamente
  IF (F_KM_ATUAL_VEICULO_CADASTRADO < 0)
  THEN
    PERFORM PUBLIC.THROW_GENERIC_ERROR('[ERRO DE DADOS] A quilometragem do veículo não pode ser um número negativo');
  END IF;

  -- Validamos se o modelo do veículo está mapeado
  IF (SELECT NOT EXISTS(SELECT CODIGO
                        FROM PUBLIC.MODELO_VEICULO MV
                        WHERE MV.COD_EMPRESA = COD_EMPRESA_VEICULO AND MV.CODIGO = F_COD_MODELO_VEICULO_CADASTRADO))
  THEN
    PERFORM PUBLIC.THROW_GENERIC_ERROR(
        '[ERRO DE VINCULO] O modelo do veículo não está mapeado corretamente, verificar vinculos');
  END IF;

  -- Validamos se o tipo do veículo está mapeado
  IF (SELECT NOT EXISTS(SELECT CODIGO
                        FROM PUBLIC.VEICULO_TIPO VT
                        WHERE VT.CODIGO = F_COD_TIPO_VEICULO_CADASTRADO AND VT.COD_EMPRESA = COD_EMPRESA_VEICULO))
  THEN
    PERFORM PUBLIC.THROW_GENERIC_ERROR(
        '[ERRO DE VINCULO] O tipo do veículo não está mapeado corretamente, verificar vinculos');
  END IF;

  INSERT INTO PUBLIC.VEICULO(
    PLACA,
    COD_UNIDADE,
    KM,
    STATUS_ATIVO,
    COD_TIPO,
    COD_MODELO,
    COD_UNIDADE_CADASTRO)
  VALUES (
    F_PLACA_VEICULO_CADASTRADO,
    F_COD_UNIDADE_VEICULO_ALOCADO,
    F_KM_ATUAL_VEICULO_CADASTRADO,
    STATUS_VEICULO,
    F_COD_TIPO_VEICULO_CADASTRADO,
    F_COD_MODELO_VEICULO_CADASTRADO,
    F_COD_UNIDADE_VEICULO_ALOCADO) RETURNING CODIGO INTO COD_VEICULO_PROLOG;

  INSERT INTO INTEGRACAO.VEICULO_CADASTRADO(
    COD_EMPRESA_CADASTRO,
    COD_UNIDADE_CADASTRO,
    COD_VEICULO_CADASTRO_PROLOG,
    PLACA_VEICULO_CADASTRO,
    DATA_HORA_CADASTRO_PROLOG)
  VALUES (
    COD_EMPRESA_VEICULO,
    F_COD_UNIDADE_VEICULO_ALOCADO,
    COD_VEICULO_PROLOG,
    F_PLACA_VEICULO_CADASTRADO,
    F_DATA_HORA_VEICULO_CADASTRO);

  GET DIAGNOSTICS F_QTD_ROWS_ALTERADAS = ROW_COUNT;

  -- VERIFICAMOS SE O INSERT NA TABELA DE MAPEAMENTO DE VEÍCULOS CADASTRADOS NA INTEGRAÇÃO OCORREU COM ÊXITO.
  IF (F_QTD_ROWS_ALTERADAS <= 0)
  THEN
    RAISE EXCEPTION
    'Não foi possível inserir a placa "%" na tabela de mapeamento', F_PLACA_VEICULO_CADASTRADO;
  END IF;

  RETURN COD_VEICULO_PROLOG;
END;
$$;
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################


--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
-- FUNCTION PARA EDITAR UM VEÍCULO NO PROLOG
CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_VEICULO_ATUALIZA_VEICULO_PROLOG(
  F_COD_UNIDADE_ORIGINAL_ALOCADO BIGINT,
  F_PLACA_ORIGINAL_VEICULO       TEXT,
  F_NOVO_COD_UNIDADE_ALOCADO     BIGINT,
  F_NOVA_PLACA_VEICULO           TEXT,
  F_NOVO_KM_VEICULO              BIGINT,
  F_NOVO_COD_MODELO_VEICULO      BIGINT,
  F_NOVO_COD_TIPO_VEICULO        BIGINT,
  F_DATA_HORA_EDICAO_VEICULO     TIMESTAMP WITH TIME ZONE,
  F_TOKEN_INTEGRACAO             TEXT)
  RETURNS BIGINT
LANGUAGE PLPGSQL
AS $$
DECLARE
  COD_EMPRESA_VEICULO  BIGINT    := (SELECT TI.COD_EMPRESA
                                     FROM INTEGRACAO.TOKEN_INTEGRACAO TI
                                     WHERE TI.TOKEN_INTEGRACAO = F_TOKEN_INTEGRACAO);
  COD_TIPO_VEICULO_ANTIGO BIGINT := (SELECT VD.COD_TIPO
                                     FROM PUBLIC.VEICULO_DATA VD
                                     WHERE VD.PLACA = F_PLACA_ORIGINAL_VEICULO);
  COD_VEICULO_PROLOG   BIGINT;
  F_QTD_ROWS_ALTERADAS BIGINT;
BEGIN
  -- Validamos se o usuário trocou a unidade alocada do veículo.
  IF (F_COD_UNIDADE_ORIGINAL_ALOCADO <> F_NOVO_COD_UNIDADE_ALOCADO)
  THEN
    PERFORM PUBLIC.THROW_GENERIC_ERROR(
        '[ERRO DE OPERAÇÃO] Para mudar a Unidade do veículo, utilize a transferência de veículo');
  END IF;

  -- Validamos se o usuário trocou a placa do veículo.
  IF (F_PLACA_ORIGINAL_VEICULO <> F_NOVA_PLACA_VEICULO)
  THEN
    PERFORM PUBLIC.THROW_GENERIC_ERROR(
        '[ERRO DE OPERAÇÃO] O ProLog não permite a edição da placa do veículo');
  END IF;

  -- Validamos se a Unidade do veículo trocou
  IF ((SELECT V.COD_UNIDADE FROM PUBLIC.VEICULO_DATA V WHERE V.PLACA = F_PLACA_ORIGINAL_VEICULO)
      <> F_COD_UNIDADE_ORIGINAL_ALOCADO)
  THEN
    PERFORM PUBLIC.THROW_GENERIC_ERROR(
        '[ERRO DE OPERAÇÃO] Para mudar a Unidade do veículo, utilize a transferência de veículo');
  END IF;

  -- Validamos se a Unidade pertence a mesma empresa do token.
  IF ((SELECT U.COD_EMPRESA FROM PUBLIC.UNIDADE U WHERE U.CODIGO = F_NOVO_COD_UNIDADE_ALOCADO)
      <>
      (SELECT TI.COD_EMPRESA FROM INTEGRACAO.TOKEN_INTEGRACAO TI
      WHERE TI.TOKEN_INTEGRACAO = F_TOKEN_INTEGRACAO))
  THEN
    PERFORM PUBLIC.THROW_GENERIC_ERROR(
        FORMAT('[ERRO DE VÍNCULO] O token "%s" não está autorizado a atualizar dados da unidade "%s", verificar vínculos',
               F_TOKEN_INTEGRACAO,
               F_NOVO_COD_UNIDADE_ALOCADO));
  END IF;

  -- Validamos se a placa já existe no ProLog.
  IF (SELECT NOT EXISTS(SELECT V.CODIGO FROM PUBLIC.VEICULO_DATA V WHERE V.PLACA::TEXT = F_NOVA_PLACA_VEICULO))
  THEN
    PERFORM PUBLIC.THROW_GENERIC_ERROR(
        FORMAT('[ERRO DE DADOS] A placa "%s" não existe no Sistema ProLog', F_NOVA_PLACA_VEICULO));
  END IF;

  -- Validamos se o KM foi inputado corretamente.
  IF (F_NOVO_KM_VEICULO < 0)
  THEN
    PERFORM PUBLIC.THROW_GENERIC_ERROR('[ERRO DE DADOS] A quilometragem do veículo não pode ser um número negativo');
  END IF;

  -- Validamos se o modelo do veículo está mapeado.
  IF (SELECT NOT EXISTS(SELECT CODIGO
                        FROM PUBLIC.MODELO_VEICULO MV
                        WHERE MV.COD_EMPRESA = COD_EMPRESA_VEICULO AND MV.CODIGO = F_NOVO_COD_MODELO_VEICULO))
  THEN
    PERFORM PUBLIC.THROW_GENERIC_ERROR(
        '[ERRO DE VINCULO] O modelo do veículo não está mapeado corretamente, verificar vínculos');
  END IF;

  -- Validamos se o tipo do veículo está mapeado.
  IF (SELECT NOT EXISTS(SELECT CODIGO
                        FROM PUBLIC.VEICULO_TIPO VT
                        WHERE VT.CODIGO = F_NOVO_COD_TIPO_VEICULO AND VT.COD_EMPRESA = COD_EMPRESA_VEICULO))
  THEN
    PERFORM PUBLIC.THROW_GENERIC_ERROR(
        '[ERRO DE VINCULO] O tipo do veículo não está mapeado corretamente, verificar vínculos');
  END IF;

  -- Validamos se o tipo foi alterado mesmo com o veículo contendo pneus aplicados.
  IF ((COD_TIPO_VEICULO_ANTIGO <> F_NOVO_COD_TIPO_VEICULO)
      AND (SELECT COUNT(VP.*) FROM PUBLIC.VEICULO_PNEU VP WHERE VP.PLACA = F_PLACA_ORIGINAL_VEICULO) > 0)
  THEN
    PERFORM PUBLIC.THROW_GENERIC_ERROR(
        '[ERRO DE OPERAÇÃO] O tipo do veículo não pode ser alterado se a placa contém pneus aplicados');
  END IF;

  UPDATE PUBLIC.VEICULO_DATA
  SET
    KM = F_NOVO_KM_VEICULO,
    COD_MODELO = F_NOVO_COD_MODELO_VEICULO,
    COD_TIPO = F_NOVO_COD_TIPO_VEICULO
  WHERE PLACA = F_PLACA_ORIGINAL_VEICULO
        AND COD_UNIDADE = F_COD_UNIDADE_ORIGINAL_ALOCADO
  RETURNING CODIGO INTO COD_VEICULO_PROLOG;

  UPDATE INTEGRACAO.VEICULO_CADASTRADO
  SET
    DATA_HORA_ULTIMA_EDICAO = F_DATA_HORA_EDICAO_VEICULO
  WHERE COD_EMPRESA_CADASTRO = COD_EMPRESA_VEICULO
        AND PLACA_VEICULO_CADASTRO = F_PLACA_ORIGINAL_VEICULO;

  GET DIAGNOSTICS F_QTD_ROWS_ALTERADAS = ROW_COUNT;

  -- VERIFICAMOS SE O UPDATE NA TABELA DE MAPEAMENTO DE VEÍCULOS CADASTRADOS NA INTEGRAÇÃO OCORREU COM ÊXITO.
  IF (F_QTD_ROWS_ALTERADAS <= 0)
  THEN
    RAISE EXCEPTION
    'Não foi possível atualizar a placa "%" na tabela de mapeamento', F_PLACA_ORIGINAL_VEICULO;
  END IF;

  RETURN COD_VEICULO_PROLOG;
END;
$$;
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################


--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
-- FUNCTION PARA ATIVAR/DESATIVAR UM VEÍCULO NO PROLOG
CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_VEICULO_ATIVA_DESATIVA_VEICULO_PROLOG(
  F_PLACA_VEICULO            TEXT,
  F_ATIVAR_DESATIVAR_VEICULO BOOLEAN,
  F_DATA_HORA_EDICAO_VEICULO TIMESTAMP WITH TIME ZONE,
  F_TOKEN_INTEGRACAO         TEXT)
  RETURNS BIGINT
LANGUAGE PLPGSQL
AS $$
DECLARE
  COD_EMPRESA_VEICULO  BIGINT := (SELECT TI.COD_EMPRESA
                                  FROM INTEGRACAO.TOKEN_INTEGRACAO TI
                                  WHERE TI.TOKEN_INTEGRACAO = F_TOKEN_INTEGRACAO);
  COD_UNIDADE_VEICULO  BIGINT := (SELECT V.COD_UNIDADE
                                  FROM PUBLIC.VEICULO V
                                  WHERE V.PLACA = F_PLACA_VEICULO);
  COD_VEICULO_PROLOG   BIGINT;
  F_QTD_ROWS_ALTERADAS BIGINT;
BEGIN
  -- Validamos se a Unidade pertence a mesma empresa do token.
  IF ((SELECT U.COD_EMPRESA
       FROM PUBLIC.VEICULO V
         JOIN PUBLIC.UNIDADE U ON V.COD_UNIDADE = U.CODIGO
       WHERE V.PLACA = F_PLACA_VEICULO) <> COD_EMPRESA_VEICULO)
  THEN
    PERFORM PUBLIC.THROW_GENERIC_ERROR(
        FORMAT('[ERRO DE VÍNCULO] O token "%s" não está autorizado a atualizar dados da placa "%s", verificar vínculos',
               F_TOKEN_INTEGRACAO,
               F_PLACA_VEICULO));
  END IF;

  -- Validamos se a placa já existe no ProLog.
  IF (SELECT NOT EXISTS(SELECT V.CODIGO FROM PUBLIC.VEICULO_DATA V WHERE V.PLACA::TEXT = F_PLACA_VEICULO))
  THEN
    PERFORM PUBLIC.THROW_GENERIC_ERROR(
        FORMAT('[ERRO DE DADOS] A placa "%s" não existe no Sistema ProLog', F_PLACA_VEICULO));
  END IF;

  UPDATE PUBLIC.VEICULO_DATA
  SET
    STATUS_ATIVO = F_ATIVAR_DESATIVAR_VEICULO
  WHERE PLACA = F_PLACA_VEICULO
        AND COD_UNIDADE = COD_UNIDADE_VEICULO
  RETURNING CODIGO INTO COD_VEICULO_PROLOG;

  UPDATE INTEGRACAO.VEICULO_CADASTRADO
  SET
    DATA_HORA_ULTIMA_EDICAO = F_DATA_HORA_EDICAO_VEICULO
  WHERE COD_EMPRESA_CADASTRO = COD_EMPRESA_VEICULO
        AND PLACA_VEICULO_CADASTRO = F_PLACA_VEICULO;

  GET DIAGNOSTICS F_QTD_ROWS_ALTERADAS = ROW_COUNT;

  -- VERIFICAMOS SE O UPDATE NA TABELA DE MAPEAMENTO DE VEÍCULOS CADASTRADOS NA INTEGRAÇÃO OCORREU COM ÊXITO.
  IF (F_QTD_ROWS_ALTERADAS <= 0)
  THEN
    RAISE EXCEPTION
    'Não foi possível ativar/desativar a placa "%" na tabela de mapeamento', F_PLACA_VEICULO;
  END IF;

  RETURN COD_VEICULO_PROLOG;
END;
$$;
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
END TRANSACTION;