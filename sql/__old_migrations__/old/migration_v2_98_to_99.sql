BEGIN TRANSACTION ;
CREATE OR REPLACE FUNCTION FUNC_GARANTE_EMPRESA_EXISTE(F_COD_EMPRESA BIGINT)
  RETURNS VOID
LANGUAGE PLPGSQL
AS $$
BEGIN
  IF NOT EXISTS(SELECT E.CODIGO
                FROM EMPRESA E
                WHERE E.CODIGO = F_COD_EMPRESA)
  THEN RAISE EXCEPTION 'Empresa de código % não existe!', F_COD_EMPRESA;
  END IF;
END;
$$;

CREATE OR REPLACE FUNCTION FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE BIGINT)
  RETURNS VOID
LANGUAGE PLPGSQL
AS $$
BEGIN
  IF NOT EXISTS(SELECT U.CODIGO
                FROM UNIDADE U
                WHERE U.CODIGO = F_COD_UNIDADE)
  THEN RAISE EXCEPTION 'Unidade de código % não existe!', F_COD_UNIDADE;
  END IF;
END;
$$;

CREATE OR REPLACE FUNCTION FUNC_GARANTE_EMPRESA_POSSUI_UNIDADE(F_COD_EMPRESA BIGINT, F_COD_UNIDADE BIGINT)
  RETURNS VOID
LANGUAGE PLPGSQL
AS $$
BEGIN
  -- Verifica se a unidade é da empresa informada.
  IF NOT EXISTS(SELECT U.CODIGO
                FROM UNIDADE U
                WHERE U.CODIGO = F_COD_UNIDADE AND U.COD_EMPRESA = F_COD_EMPRESA)
  THEN RAISE EXCEPTION 'A unidade % não pertence a empresa %!', F_COD_UNIDADE, F_COD_EMPRESA;
  END IF;
END;
$$;

CREATE OR REPLACE FUNCTION FUNC_GARANTE_INTEGRIDADE_EMPRESA_UNIDADE(F_COD_EMPRESA BIGINT, F_COD_UNIDADE BIGINT)
  RETURNS VOID
LANGUAGE PLPGSQL
AS $$
BEGIN
  PERFORM FUNC_GARANTE_EMPRESA_EXISTE(F_COD_EMPRESA);
  PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE);
  PERFORM FUNC_GARANTE_EMPRESA_POSSUI_UNIDADE(F_COD_EMPRESA, F_COD_UNIDADE);
END;
$$;

CREATE OR REPLACE FUNCTION FUNC_GARANTE_VEICULO_EXISTE(F_COD_UNIDADE_VEICULO BIGINT, F_PLACA_VEICULO TEXT)
  RETURNS VOID
LANGUAGE PLPGSQL
AS $$
BEGIN
  IF NOT EXISTS(SELECT VD.CODIGO
                FROM VEICULO_DATA VD
                WHERE VD.PLACA = F_PLACA_VEICULO
                      AND VD.COD_UNIDADE = F_COD_UNIDADE_VEICULO)
  THEN
    RAISE EXCEPTION
    'Não foi possível encontrar o veículo com estes parâmetros: Unidade %, Placa %',
    F_COD_UNIDADE_VEICULO,
    F_PLACA_VEICULO;
  END IF;
END;
$$;

CREATE OR REPLACE FUNCTION FUNC_GARANTE_NOVO_KM_MENOR_QUE_ATUAL_VEICULO(
  F_COD_UNIDADE_VEICULO BIGINT,
  F_PLACA_VEICULO       TEXT,
  F_NOVO_KM             BIGINT)
  RETURNS VOID
LANGUAGE PLPGSQL
AS $$
DECLARE
  F_KM_ATUAL_VEICULO     BIGINT;
BEGIN
  SELECT VD.KM
  FROM VEICULO_DATA VD
  WHERE VD.PLACA = F_PLACA_VEICULO
        AND VD.COD_UNIDADE = F_COD_UNIDADE_VEICULO
  INTO F_KM_ATUAL_VEICULO;

  IF (F_KM_ATUAL_VEICULO IS NOT NULL AND F_NOVO_KM > F_KM_ATUAL_VEICULO)
  THEN
    RAISE EXCEPTION 'O Km enviado não pode ser maior que o Km atual do veículo : Km enviado %, Km atual %',
    F_NOVO_KM,
    F_KM_ATUAL_VEICULO;
  END IF;
END;
$$;

CREATE OR REPLACE FUNCTION FUNC_GARANTE_PNEU_EXISTE(
  F_COD_EMPRESA BIGINT,
  F_COD_UNIDADE BIGINT,
  F_COD_PNEU    BIGINT,
  F_NUMERO_FOGO TEXT)
  RETURNS VOID
LANGUAGE PLPGSQL
AS $$
BEGIN
  IF NOT EXISTS(SELECT P.CODIGO
                FROM PNEU P
                WHERE P.CODIGO_CLIENTE = F_NUMERO_FOGO AND P.CODIGO = F_COD_PNEU AND
                      P.COD_UNIDADE = F_COD_UNIDADE AND P.COD_EMPRESA = F_COD_EMPRESA)
  THEN RAISE EXCEPTION 'Pneu com número de fogo %, código %, não existe na unidade % - %', F_NUMERO_FOGO, F_COD_PNEU,
  F_COD_UNIDADE, (SELECT U.NOME
                  FROM UNIDADE U
                  WHERE U.CODIGO = F_COD_UNIDADE);
  END IF;
END;
$$;

-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Verifica se o setor existe na unidade informada.
--
-- Precondições:
-- 1) Necessário o código da unidade e o código do setor para a verificação da integridade entre unidade-setor.
--
-- Histórico:
-- 2019-07-23 -> Function criada (thaisksf).
CREATE OR REPLACE FUNCTION FUNC_GARANTE_SETOR_EXISTE(
  F_COD_UNIDADE BIGINT,
  F_COD_SETOR BIGINT)
  RETURNS VOID
LANGUAGE PLPGSQL
AS $$
BEGIN
  -- VERIFICA SE O SETOR EXISTE
  IF NOT EXISTS (SELECT S.CODIGO FROM SETOR S WHERE S.CODIGO = F_COD_SETOR AND S.COD_UNIDADE = F_COD_UNIDADE)
  THEN RAISE EXCEPTION 'O setor % não existe na unidade %.', F_COD_SETOR, (SELECT U.NOME FROM UNIDADE U WHERE U.CODIGO = F_COD_UNIDADE);
  END IF;
END;
$$;

-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Verifica se o as empresas são iguais.
--
-- Precondições:
-- 1) Necessário o código das empresas para verificar se são iguais.
--
-- Histórico:
-- 2019-07-23 -> Function criada (thaisksf).
CREATE OR REPLACE FUNCTION FUNC_GARANTE_EMPRESAS_DISTINTAS(
  F_COD_EMPRESA_1 BIGINT,
  F_COD_EMPRESA_2 BIGINT)
  RETURNS VOID
LANGUAGE PLPGSQL
AS $$
BEGIN
  -- Verifica se as empresas são iguais.
  IF (F_COD_EMPRESA_1 = F_COD_EMPRESA_2)
  THEN RAISE EXCEPTION 'As empresas são iguais';
  END IF;
END;
$$;

-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Verifica se a equipe existe na unidade informada.
--
-- Precondições:
-- 1) Necessário o código da unidade e o código da equipe para a verificação da integridade entre unidade-equipe.
--
-- Histórico:
-- 2019-07-26 -> Function criada (thaisksf).
CREATE OR REPLACE FUNCTION FUNC_GARANTE_EQUIPE_EXISTE(
  F_COD_UNIDADE BIGINT,
  F_COD_EQUIPE BIGINT)
  RETURNS VOID
LANGUAGE PLPGSQL
AS $$
BEGIN
  -- VERIFICA SE A EQUIPE EXISTE
  IF NOT EXISTS (SELECT E.CODIGO FROM EQUIPE E WHERE E.CODIGO = F_COD_EQUIPE AND E.COD_UNIDADE = F_COD_UNIDADE)
  THEN RAISE EXCEPTION 'A equipe de código: % não existe na unidade: %.', F_COD_EQUIPE,
  (SELECT U.NOME FROM UNIDADE U WHERE U.CODIGO = F_COD_UNIDADE);
  END IF;
END;
$$;

-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Verifica se colaborador existe a partir do cpf informado.
--
-- Precondições:
-- 1) Necessário o CPF do colaborador para a verificação.
--
-- Histórico:
-- 2019-07-30 -> Function criada (thaisksf).
CREATE OR REPLACE FUNCTION FUNC_GARANTE_COLABORADOR_EXISTE(
  F_CPF_COLABORADOR BIGINT)
  RETURNS VOID
LANGUAGE PLPGSQL
AS $$
BEGIN
  -- VERIFICA SE O COLABORADOR EXISTE
  IF NOT EXISTS(SELECT C.CPF
                FROM COLABORADOR C
                WHERE C.CPF = F_CPF_COLABORADOR)
  THEN RAISE EXCEPTION 'O colaborador com CPF: % não está cadastrado.', F_CPF_COLABORADOR;
  END IF;
END;
$$;

-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Verifica se colaborador está na unidade informada.
--
-- Precondições:
-- 1) Necessário o CPF do colaborador e o código da undiade para a verificar a integração.
--
-- Histórico:
-- 2019-07-30 -> Function criada (thaisksf).
CREATE OR REPLACE FUNCTION FUNC_GARANTE_INTEGRIDADE_UNIDADE_COLABORADOR(
  F_COD_UNIDADE BIGINT,
  F_CPF_COLABORADOR BIGINT)
  RETURNS VOID
LANGUAGE PLPGSQL
AS $$
BEGIN
  --VERIFICA SE COLABORADOR EXISTE
  PERFORM FUNC_GARANTE_COLABORADOR_EXISTE(F_CPF_COLABORADOR);

  --VERIFICA SE UNIDADE EXISTE
  PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE);

  -- VERIFICA SE O COLABORADOR PERTENCE A UNIDADE.
  IF NOT EXISTS(SELECT C.CPF
                FROM COLABORADOR C
                WHERE C.CPF = F_CPF_COLABORADOR AND C.COD_UNIDADE = F_COD_UNIDADE)
  THEN RAISE EXCEPTION 'O colaborador com CPF: %, nome: %, não pertence a unidade: % - %!',
  F_CPF_COLABORADOR,
  (SELECT C.NOME FROM COLABORADOR C WHERE C.CPF = F_CPF_COLABORADOR),
  F_COD_UNIDADE,
  (SELECT U.NOME FROM UNIDADE U WHERE U.CODIGO = F_COD_UNIDADE);
  END IF;
END;
$$;
--######################################################################################################################
--######################################################################################################################
--######################################### FUNC - RETORNO PNEU DO DESCARTE ############################################
--######################################################################################################################
--######################################################################################################################
--PL-2090
-- A lógica aplicada nessa function é a seguinte:
-- Se o pneu veio do ESTOQUE:
-- Pode deletar a movimentação e mover pneu para ESTOQUE.
--
-- Se o pneu veio da ANÁLISE:
-- Pode deletar a movimentação e mover pneu para ANÁLISE.
-- Não podemos mover diretamente para ESTOQUE pois uma mov de ANÁLISE -> ESTOQUE implica informações de incremento de
-- vida ou de outros serviços simples, que nós não temos.
--
-- Se o pneu veio do VEÍCULO:
-- Não podemos deletar a movimentação, pois não podemos voltar o pneu para o veículo, já que pode ter outro pneu lá.
-- Teremos que alterar essa movimentação para ser VEÍCULO -> ESTOQUE ao invés de VEÍCULO -> DESCARTE. As informações
-- específicas de uma mov VEÍCULO -> DESCARTE (fotos e motivo) devem ser removidas, mas não a mov como um todo.
--
-- Lembrando sempre que nos casos de deleção de movs, temos que deletar o processo, se necessário.
CREATE OR REPLACE FUNCTION FUNC_PNEU_RETORNA_PNEU_DO_DESCARTE(
      F_COD_EMPRESA               BIGINT,
      F_COD_UNIDADE               BIGINT,
      F_COD_PNEU                  BIGINT,
      F_NUMERO_FOGO_PNEU          VARCHAR,
  OUT AVISO_PNEU_RETORNADO        TEXT)
  RETURNS TEXT
LANGUAGE PLPGSQL
SECURITY DEFINER
AS $$
DECLARE
    F_STATUS_PNEU_ESTOQUE       VARCHAR := 'ESTOQUE';
    F_STATUS_PNEU_DESCARTE      VARCHAR := 'DESCARTE';
    F_STATUS_PNEU_EM_USO        VARCHAR := 'EM_USO';
    F_STATUS_ORIGEM_PNEU        VARCHAR;
    F_STATUS_DESTINO_PNEU       VARCHAR;
    F_COD_MOVIMENTACAO_PROCESSO BIGINT;
    F_COD_MOVIMENTACAO          BIGINT;
BEGIN
  -- VERIFICA SE UNIDADE EXISTE, SE EMPRESA EXISTE E SE UNIDADE PERTENCE A EMPRESA.
  PERFORM FUNC_GARANTE_INTEGRIDADE_EMPRESA_UNIDADE(F_COD_EMPRESA, F_COD_UNIDADE);

  -- VERIFICA SE PNEU EXISTE NA UNIDADE.
  PERFORM FUNC_GARANTE_PNEU_EXISTE(F_COD_EMPRESA, F_COD_UNIDADE, F_COD_PNEU, F_NUMERO_FOGO_PNEU);

  -- VERIFICA SE O STATUS DO PNEU ESTÁ COMO 'DESCARTE'.
  IF (SELECT P.STATUS FROM PNEU P WHERE P.CODIGO_CLIENTE = F_NUMERO_FOGO_PNEU AND
                                        P.CODIGO = F_COD_PNEU AND
                                        P.COD_UNIDADE = F_COD_UNIDADE) != F_STATUS_PNEU_DESCARTE
  THEN RAISE EXCEPTION 'Pneu de número de fogo %, com código % da unidade %, não está com status = %!',
  F_NUMERO_FOGO_PNEU, F_COD_PNEU, F_COD_UNIDADE, F_STATUS_PNEU_DESCARTE;
  END IF;

  -- VAI PEGAR AS INFORMAÇÕES DA ÚLTIMA MOVIMENTAÇÃO DO PNEU ASSUMINDO QUE FOI PARA DESCARTE.
  -- NA SEQUÊNCIA ISSO SERÁ VALIDADO.
  SELECT M.CODIGO                    AS COD_MOVIMENTACAO,
         M.COD_MOVIMENTACAO_PROCESSO AS COD_MOVIMENTACAO_PROCESSO,
         MO.TIPO_ORIGEM              AS TIPO_ORIGEM,
         MD.TIPO_DESTINO             AS TIPO_DESTINO
  FROM MOVIMENTACAO M
           JOIN MOVIMENTACAO_DESTINO MD ON M.CODIGO = MD.COD_MOVIMENTACAO
           JOIN MOVIMENTACAO_ORIGEM MO ON M.CODIGO = MO.COD_MOVIMENTACAO
  WHERE M.COD_PNEU = F_COD_PNEU
    AND M.COD_UNIDADE = F_COD_UNIDADE
  ORDER BY M.CODIGO DESC
  LIMIT 1
  INTO F_COD_MOVIMENTACAO, F_COD_MOVIMENTACAO_PROCESSO, F_STATUS_ORIGEM_PNEU, F_STATUS_DESTINO_PNEU;

  -- GARANTE QUE A ÚLTIMA MOVIMENTAÇÃO DO PNEU TENHA SIDO PARA DESCARTE.
  IF F_STATUS_DESTINO_PNEU != F_STATUS_PNEU_DESCARTE
  THEN RAISE EXCEPTION '[INCONSISTÊNCIA] A ultima movimentação do pneu de número de fogo %, com código % da unidade %,
      não foi para %!',
  F_NUMERO_FOGO_PNEU, F_COD_PNEU, F_COD_UNIDADE, F_STATUS_PNEU_DESCARTE;
  END IF;

  -- DELETA A MOVIMENTAÇÃO QUE MOVEU O PNEU PARA O DESCARTE SE A ORIGEM NÃO FOR 'EM_USO', CASO SEJA, MODIFICA
  -- DESTINO PARA 'ESTOQUE'.
  -- ISSO É FEITO PORQUE UM PNEU QUE ESTAVA APLICADO, NÃO PODE VOLTAR AO VEÍCULO, JÁ QUE PODERÍAMOS ESBARRAR NO CASO
  -- ONDE JÁ EXISTE OUTRO PNEU NA POSIÇÃO QUE ELE ESTAVA ANTES.
  IF F_STATUS_ORIGEM_PNEU != F_STATUS_PNEU_EM_USO
  THEN
    DELETE FROM MOVIMENTACAO M WHERE M.CODIGO = F_COD_MOVIMENTACAO AND
                                     M.COD_MOVIMENTACAO_PROCESSO = F_COD_MOVIMENTACAO_PROCESSO AND
                                     M.COD_PNEU = F_COD_PNEU AND
                                     M.COD_UNIDADE = F_COD_UNIDADE;
      -- VERIFICA SE MOVIMENTACAO ERA A UNICA EXISTENTE NO PROCESSO.
    IF NOT EXISTS(SELECT M.CODIGO FROM MOVIMENTACAO M WHERE M.COD_MOVIMENTACAO_PROCESSO = F_COD_MOVIMENTACAO_PROCESSO)
    THEN
      -- DELETA PROCESSO DE MOVIMENTACAO.
      DELETE FROM MOVIMENTACAO_PROCESSO WHERE CODIGO = F_COD_MOVIMENTACAO_PROCESSO;
    END IF;
  ELSE
    UPDATE MOVIMENTACAO_DESTINO SET TIPO_DESTINO = F_STATUS_PNEU_ESTOQUE,
                                    COD_MOTIVO_DESCARTE = NULL,
                                    URL_IMAGEM_DESCARTE_1 = NULL,
                                    URL_IMAGEM_DESCARTE_2 = NULL,
                                    URL_IMAGEM_DESCARTE_3 = NULL WHERE COD_MOVIMENTACAO = F_COD_MOVIMENTACAO;
  END IF;

  -- ALTERA STATUS DO PNEU.
  IF F_STATUS_ORIGEM_PNEU != F_STATUS_PNEU_EM_USO
    THEN
      UPDATE PNEU SET STATUS = F_STATUS_ORIGEM_PNEU WHERE CODIGO = F_COD_PNEU AND
                                                          CODIGO_CLIENTE = F_NUMERO_FOGO_PNEU AND
                                                          COD_UNIDADE = F_COD_UNIDADE AND
                                                          COD_EMPRESA = F_COD_EMPRESA;
  ELSE
    UPDATE PNEU SET STATUS = F_STATUS_PNEU_ESTOQUE WHERE CODIGO = F_COD_PNEU AND
                                                         CODIGO_CLIENTE = F_NUMERO_FOGO_PNEU AND
                                                         COD_UNIDADE = F_COD_UNIDADE AND
                                                         COD_EMPRESA = F_COD_EMPRESA;
  END IF;

  SELECT CONCAT('Pneu retornado para ', (SELECT P.STATUS FROM PNEU P WHERE P.CODIGO = F_COD_PNEU),
                ', Código: ', F_COD_PNEU,
                ', Número de fogo: ', F_NUMERO_FOGO_PNEU,
                ', Unidade: ', F_COD_UNIDADE, ' - ', (SELECT U.NOME FROM UNIDADE U WHERE U.CODIGO = F_COD_UNIDADE),
                ', Empresa: ', F_COD_EMPRESA, ' - ', (SELECT E.NOME FROM EMPRESA E WHERE E.CODIGO = F_COD_EMPRESA))
  INTO AVISO_PNEU_RETORNADO;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--########################################### FUNC - DELETAR SERVIÇO AFERICAO ##########################################
--######################################################################################################################
--######################################################################################################################
--PL-2064
CREATE OR REPLACE FUNCTION FUNC_AFERICAO_DELETA_SERVICO_AFERICAO(
      F_COD_EMPRESA                   BIGINT,
      F_COD_UNIDADE                   BIGINT,
      F_COD_PNEU                      BIGINT,
      F_NUMERO_FOGO                   TEXT,
      F_CODIGO_AFERICAO               BIGINT,
      F_COD_SERVICO_AFERICAO          BIGINT,
      F_TIPO_SERVICO_AFERICAO         TEXT,
  OUT AVISO_SERVICO_AFERICAO_DELETADO TEXT)
  RETURNS TEXT
LANGUAGE PLPGSQL
SECURITY DEFINER
AS $$
DECLARE
F_TIPO_SERVICO_AFERICAO TEXT := LOWER(F_TIPO_SERVICO_AFERICAO);
BEGIN
  --Garante integridade entre unidade e empresa
  PERFORM FUNC_GARANTE_INTEGRIDADE_EMPRESA_UNIDADE(F_COD_EMPRESA, F_COD_UNIDADE);

  --Verifica se o pneu existe
  PERFORM FUNC_GARANTE_PNEU_EXISTE(F_COD_EMPRESA, F_COD_UNIDADE, F_COD_PNEU, F_NUMERO_FOGO);

  --Verifica se existe afericao
  IF NOT EXISTS(SELECT A.CODIGO
                FROM AFERICAO A
                WHERE A.CODIGO = F_CODIGO_AFERICAO AND
                      A.COD_UNIDADE = F_COD_UNIDADE)
  THEN RAISE EXCEPTION 'Aferição de código % não existe para a unidade % - %', F_CODIGO_AFERICAO, F_COD_UNIDADE,
  (SELECT NOME
   FROM UNIDADE
   WHERE CODIGO = F_COD_UNIDADE);
  END IF;

  --Verifica se existe serviço de afericao
  IF NOT EXISTS(SELECT AM.CODIGO
                FROM AFERICAO_MANUTENCAO AM
                WHERE AM.CODIGO = F_COD_SERVICO_AFERICAO  AND
                      AM.COD_AFERICAO = F_CODIGO_AFERICAO AND
                      AM.COD_PNEU = F_COD_PNEU            AND
                      AM.COD_UNIDADE = F_COD_UNIDADE      AND
                      AM.TIPO_SERVICO = F_TIPO_SERVICO_AFERICAO)
  THEN RAISE EXCEPTION 'Não existe serviço de aferição com código: %, do tipo: "%", código de aferição: %, e codigo de pneus: %
                       para a unidade % - %', F_COD_SERVICO_AFERICAO, F_TIPO_SERVICO_AFERICAO,
  F_CODIGO_AFERICAO, F_COD_PNEU, F_COD_UNIDADE, (SELECT NOME
                                                 FROM UNIDADE
                                                 WHERE CODIGO = F_COD_UNIDADE);
  END IF;

  -- Deleta aferição manutenção.
  UPDATE AFERICAO_MANUTENCAO_DATA
  SET DELETADO = TRUE, DATA_HORA_DELETADO = NOW()
  WHERE
    CODIGO = F_COD_SERVICO_AFERICAO AND
    COD_AFERICAO = F_CODIGO_AFERICAO AND
    COD_UNIDADE = F_COD_UNIDADE AND
    COD_PNEU = F_COD_PNEU;

  SELECT 'SERVIÇO DE AFERIÇÃO DELETADO: '
         || F_COD_SERVICO_AFERICAO
         || ', DO TIPO: '
         || F_TIPO_SERVICO_AFERICAO
         || ', CODIGO DE AFERIÇÃO: '
         || F_CODIGO_AFERICAO
         || ', CÓDIGO PNEU: '
         || F_COD_PNEU
         || ', UNIDADE: '
         || F_COD_UNIDADE
         || ' - '
         || (SELECT U.NOME
             FROM UNIDADE U
             WHERE U.CODIGO = F_COD_UNIDADE)
  INTO AVISO_SERVICO_AFERICAO_DELETADO;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--#################### CRIAR FUNCTION PARA VISUALIZAR COLABORADORES COM DETERMINADA PERMISSÃO  #########################
--######################################################################################################################
--######################################################################################################################
--PL-2093

-- Função genérica que busca os colaboradores com uma permissão específica de acordo com a empresa
CREATE OR REPLACE FUNCTION FUNC_COLABORADOR_BUSCA_POR_PERMISSAO_EMPRESA(
  F_COD_EMPRESA BIGINT, F_COD_PERMISSAO BIGINT)
  RETURNS TABLE(
    FUNCIONALIDADE  TEXT,
    PERMISSAO       TEXT,
    COD_EMPRESA     BIGINT,
    EMPRESA         TEXT,
    COD_UNIDADE     BIGINT,
    UNIDADE         TEXT,
    COD_COLABORADOR BIGINT,
    COLABORADOR     TEXT,
    CPF             BIGINT,
    DATA_NASCIMENTO DATE,
    CARGO           TEXT)
LANGUAGE PLPGSQL
AS $$
BEGIN
  RETURN QUERY
  SELECT FPA.NOME :: TEXT  AS FUNCIONALIDADE,
         FP.FUNCAO :: TEXT AS PERMISSAO,
         E.CODIGO          AS COD_EMPRESA,
         E.NOME :: TEXT    AS EMPRESA,
         U.CODIGO          AS COD_UNIDADE,
         U.NOME :: TEXT    AS UNIDADE,
         C.CODIGO          AS COD_COLABORADOR,
         C.NOME :: TEXT    AS COLABORADOR,
         C.CPF,
         C.DATA_NASCIMENTO,
         F.NOME :: TEXT    AS CARGO
  FROM COLABORADOR C
         LEFT JOIN CARGO_FUNCAO_PROLOG_V11 CFP ON CFP.COD_FUNCAO_COLABORADOR = C.COD_FUNCAO
                                                    AND CFP.COD_UNIDADE = C.COD_UNIDADE
         LEFT JOIN UNIDADE U ON U.CODIGO = C.COD_UNIDADE
         LEFT JOIN EMPRESA E ON E.CODIGO = C.COD_EMPRESA
         LEFT JOIN FUNCAO F ON F.CODIGO = C.COD_FUNCAO
         LEFT JOIN FUNCAO_PROLOG_V11 FP ON FP.CODIGO = CFP.COD_FUNCAO_PROLOG
         LEFT JOIN FUNCAO_PROLOG_AGRUPAMENTO FPA ON FPA.CODIGO = FP.COD_AGRUPAMENTO
  WHERE C.COD_EMPRESA = F_COD_EMPRESA
    AND C.STATUS_ATIVO = TRUE
    AND CFP.COD_FUNCAO_PROLOG = F_COD_PERMISSAO
  ORDER BY UNIDADE, COLABORADOR;
END;
$$;

-- Função que busca os colaboradores com a permissão de vincular permissões de acordo com a empresa
CREATE OR REPLACE FUNCTION FUNC_COLABORADOR_BUSCA_VINCULAR_PERMISSAO(
  F_COD_EMPRESA BIGINT)
  RETURNS TABLE(
    FUNCIONALIDADE  TEXT,
    PERMISSAO       TEXT,
    COD_EMPRESA     BIGINT,
    EMPRESA         TEXT,
    COD_UNIDADE     BIGINT,
    UNIDADE         TEXT,
    COD_COLABORADOR BIGINT,
    COLABORADOR     TEXT,
    CPF             BIGINT,
    DATA_NASCIMENTO DATE,
    CARGO           TEXT)
LANGUAGE PLPGSQL
AS $$
DECLARE F_COD_FUNCAO BIGINT := 329;
BEGIN
  RETURN QUERY
  SELECT *
  FROM FUNC_COLABORADOR_BUSCA_POR_PERMISSAO_EMPRESA(F_COD_EMPRESA, F_COD_FUNCAO);
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--#################### CRIA FUNCTION PARA ALTERAR O KM COLETADO NA REALIZAÇÃO DE UM CHECKLIST ##########################
--######################################################################################################################
--######################################################################################################################
--PL-2098

CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_ALTERA_KM_COLETADO_CHECKLIST_REALIZADO(
      F_COD_UNIDADE               BIGINT,
      F_PLACA                     TEXT,
      F_COD_CHECKLIST_REALIZADO   BIGINT,
      F_NOVO_KM                   BIGINT,
  OUT AVISO_KM_CHECKLIST_ALTERADO TEXT)
  RETURNS TEXT
LANGUAGE PLPGSQL SECURITY DEFINER
AS $$
DECLARE
  QTD_LINHAS_ATUALIZADAS BIGINT;
BEGIN
  PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE);
  PERFORM FUNC_GARANTE_VEICULO_EXISTE(F_COD_UNIDADE, F_PLACA);
  PERFORM FUNC_GARANTE_NOVO_KM_MENOR_QUE_ATUAL_VEICULO(F_COD_UNIDADE, F_PLACA, F_NOVO_KM);

  -- Verifica se o checklist existe.
  IF NOT EXISTS(SELECT CD.CODIGO
                FROM CHECKLIST CD
                WHERE CD.CODIGO = F_COD_CHECKLIST_REALIZADO
                      AND CD.COD_UNIDADE = F_COD_UNIDADE
                      AND CD.PLACA_VEICULO = F_PLACA)
  THEN
    RAISE EXCEPTION 'Não foi possível encontrar o checklist realizado com estes parâmetros: Unidade %, Placa %,
                     Código da realização do checklist %', F_COD_UNIDADE, F_PLACA, F_COD_CHECKLIST_REALIZADO;
  END IF;

  UPDATE CHECKLIST_DATA
  SET KM_VEICULO = F_NOVO_KM
  WHERE CODIGO = F_COD_CHECKLIST_REALIZADO
        AND COD_UNIDADE = F_COD_UNIDADE
        AND PLACA_VEICULO = F_PLACA;

  GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

  IF (QTD_LINHAS_ATUALIZADAS <= 0)
  THEN
    RAISE EXCEPTION 'Erro ao atualizar o km do checklist realizado com estes parâemtros: Unidade %, Placa %,
                     Código da realização do checklist %', F_COD_UNIDADE, F_PLACA, F_COD_CHECKLIST_REALIZADO;
  END IF;

  SELECT 'O KM DO VEÍCULO NO CHECKLIST REALIZADO FOI ALTERADO COM SUCESSO, UNIDADE: '
         || F_COD_UNIDADE
         || ', PLACA: '
         || F_PLACA
         || ', CÓDIGO DO CHECKLIST REALIZADO: '
         || F_COD_CHECKLIST_REALIZADO
  INTO AVISO_KM_CHECKLIST_ALTERADO;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--#################### CRIA FUNCTION PARA ALTERAR O KM COLETADO NA REALIZAÇÃO DE UMA AFERIÇÃO ##########################
--######################################################################################################################
--######################################################################################################################
--PL-2097
CREATE OR REPLACE FUNCTION FUNC_AFERICAO_ALTERA_KM_COLETADO_AFERICAO(
      F_COD_UNIDADE              BIGINT,
      F_PLACA                    TEXT,
      F_COD_AFERICAO             BIGINT,
      F_NOVO_KM                  BIGINT,
  OUT AVISO_KM_AFERICAO_ALTERADO TEXT)
  RETURNS TEXT
LANGUAGE PLPGSQL SECURITY DEFINER
AS $$
DECLARE
  QTD_LINHAS_ATUALIZADAS BIGINT;
BEGIN
  PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE);
  PERFORM FUNC_GARANTE_VEICULO_EXISTE(F_COD_UNIDADE, F_PLACA);
  PERFORM FUNC_GARANTE_NOVO_KM_MENOR_QUE_ATUAL_VEICULO(F_COD_UNIDADE, F_PLACA, F_NOVO_KM);

  -- Verifica se aferição existe.
  IF NOT EXISTS(SELECT AF.CODIGO
                FROM AFERICAO AF
                WHERE AF.PLACA_VEICULO IS NOT NULL
                      AND AF.PLACA_VEICULO = F_PLACA
                      AND AF.COD_UNIDADE = F_COD_UNIDADE
                      AND AF.CODIGO = F_COD_AFERICAO)
  THEN
    RAISE EXCEPTION 'Não foi possível encontrar a aferição realizada com estes parâmetros: Unidade %, Placa %,
                     Código da aferição %', F_COD_UNIDADE, F_PLACA, F_COD_AFERICAO;
  END IF;

  UPDATE AFERICAO
  SET KM_VEICULO = F_NOVO_KM
  WHERE CODIGO = F_COD_AFERICAO
        AND COD_UNIDADE = F_COD_UNIDADE
        AND PLACA_VEICULO = F_PLACA;

  GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

  IF (QTD_LINHAS_ATUALIZADAS <= 0)
  THEN
    RAISE EXCEPTION 'Erro ao atualizar o km da aferição com estes parâemtros: Unidade %, Placa %, Código da aferição %',
    F_COD_UNIDADE,
    F_PLACA,
    F_COD_AFERICAO;
  END IF;

  SELECT 'O KM DO VEÍCULO NA AFERIÇÃO FOI ALTERADO COM SUCESSO, UNIDADE: '
         || F_COD_UNIDADE
         || ', PLACA: '
         || F_PLACA
         || ', CÓDIGO DA AFERIÇÃO: '
         || F_COD_AFERICAO
  INTO AVISO_KM_AFERICAO_ALTERADO;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--################# CRIA FUNCTION PARA ALTERAR A PRESSÃO RECOMENDADA DO PNEU PELO NÚMERO DE FOGO #######################
--######################################################################################################################
--######################################################################################################################
--PL-2094
CREATE OR REPLACE FUNCTION FUNC_PNEU_ALTERA_PRESSAO_IDEAL_BY_NUMERO_FOGO(
      F_COD_EMPRESA              BIGINT,
      F_COD_UNIDADE              BIGINT,
      F_NUMERO_FOGO              TEXT,
      F_NOVA_PRESSAO_RECOMENDADA BIGINT,
  OUT AVISO_PRESSAO_ALTERADA     TEXT)
  RETURNS TEXT
LANGUAGE PLPGSQL
SECURITY DEFINER
AS $$
DECLARE
  QTD_LINHAS_ATUALIZADAS  BIGINT;
  PRESSAO_MINIMA_RECOMENDADA BIGINT := 25;
  PRESSAO_MAXIMA_RECOMENDADA BIGINT := 150;
BEGIN
   PERFORM FUNC_GARANTE_INTEGRIDADE_EMPRESA_UNIDADE(F_COD_EMPRESA, F_COD_UNIDADE);

  --Verifica se a pressao informada está dentro das recomendadas.
  IF (F_NOVA_PRESSAO_RECOMENDADA NOT BETWEEN PRESSAO_MINIMA_RECOMENDADA AND PRESSAO_MAXIMA_RECOMENDADA)
  THEN RAISE EXCEPTION 'Pressão recomendada não está dentro dos valores pré-estabelecidos.
                        Mínima Recomendada: % ---- Máxima Recomendada: %', PRESSAO_MINIMA_RECOMENDADA,
  PRESSAO_MAXIMA_RECOMENDADA;
  END IF;

  -- Verifica se existe o número de fogo informado.
  IF NOT EXISTS(SELECT PD.CODIGO
                FROM PNEU PD
                WHERE PD.CODIGO_CLIENTE = F_NUMERO_FOGO AND PD.COD_EMPRESA = F_COD_EMPRESA)
  THEN RAISE EXCEPTION 'Número de fogo % não está cadastrado na empresa %!', F_NUMERO_FOGO, F_COD_EMPRESA;
  END IF;

  UPDATE PNEU
  SET PRESSAO_RECOMENDADA = F_NOVA_PRESSAO_RECOMENDADA
  WHERE CODIGO_CLIENTE = F_NUMERO_FOGO
        AND COD_UNIDADE = F_COD_UNIDADE
        AND COD_EMPRESA = F_COD_EMPRESA;

  GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

  IF (QTD_LINHAS_ATUALIZADAS <= 0)
  THEN
    RAISE EXCEPTION 'Erro ao atualizar a pressão recomendada com estes parâemtros:
                     Empresa %, Unidade %, Número de fogo %, Nova pressão %',
    F_COD_EMPRESA,
    F_COD_UNIDADE,
    F_NUMERO_FOGO,
    F_NOVA_PRESSAO_RECOMENDADA;
  END IF;

  SELECT CONCAT('Pressão recomendada do pneu com número de fogo ',
                F_NUMERO_FOGO,
                ' da empresa ',
                F_COD_EMPRESA,
                ' da unidade ',
                F_COD_UNIDADE,
                ' alterada para ',
                F_NOVA_PRESSAO_RECOMENDADA,
                ' psi')
  INTO AVISO_PRESSAO_ALTERADA;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--######################### CRIA FUNCTION PARA DELETAR LOGICAMENTE VALORES DE AFERIÇÃO #################################
--######################################################################################################################
--######################################################################################################################
--PL-2099
-- ALTERA PARA DELETAR APENAS NÃO DELETADOS.
CREATE OR REPLACE FUNCTION FUNC_AFERICAO_DELETA_AFERICAO(
      F_COD_UNIDADE       BIGINT,
      F_PLACA             TEXT,
      F_CODIGO_AFERICAO   BIGINT,
  OUT AVISO_AFERICAO_DELETADA TEXT)
  RETURNS TEXT
LANGUAGE PLPGSQL SECURITY DEFINER
AS $$
DECLARE
  QTD_LINHAS_ATUALIZADAS BIGINT;
BEGIN
  IF ((SELECT COUNT(codigo) FROM AFERICAO_DATA WHERE CODIGO = F_CODIGO_AFERICAO
        AND COD_UNIDADE = F_COD_UNIDADE
        AND PLACA_VEICULO = F_PLACA) <= 0)
  THEN
    RAISE EXCEPTION 'Nenhuma aferição encontrada com estes parâmetros: Unidade %, Placa % e Código %', F_COD_UNIDADE, F_PLACA, F_CODIGO_AFERICAO;
  END IF;

  -- DELETA AFERIÇÃO.
  UPDATE AFERICAO_DATA
  SET DELETADO = TRUE, DATA_HORA_DELETADO = NOW()
  WHERE COD_UNIDADE = F_COD_UNIDADE
        AND PLACA_VEICULO = F_PLACA
        AND CODIGO = F_CODIGO_AFERICAO
        AND DELETADO = FALSE;

  GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

  IF (QTD_LINHAS_ATUALIZADAS <= 0)
  THEN
    RAISE EXCEPTION 'Erro ao deletar o aferição de unidade: %, placa: % e código: %', F_COD_UNIDADE, F_PLACA, F_CODIGO_AFERICAO;
  END IF;
  --
  --

  -- DELETA AFERIÇÃO VALORES.
  UPDATE AFERICAO_VALORES_DATA
  SET DELETADO = TRUE, DATA_HORA_DELETADO = NOW()
WHERE COD_UNIDADE = F_COD_UNIDADE
        AND COD_AFERICAO = F_CODIGO_AFERICAO
        AND DELETADO = FALSE;

  GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

  -- SE TEM AFERIÇÃO, TAMBÉM DEVERÁ CONTER VALORES DE AFERIÇÃO, ENTÃO DEVE-SE VERIFICAR.
  IF ((QTD_LINHAS_ATUALIZADAS <= 0) AND ((SELECT COUNT(*)
                                          FROM AFERICAO_VALORES_DATA AVD
                                            WHERE AVD.COD_UNIDADE = F_COD_UNIDADE
                                                  AND AVD.COD_AFERICAO = F_CODIGO_AFERICAO) > 0))
  THEN
    RAISE EXCEPTION 'Erro ao deletar os valores de  aferição de unidade: %, placa: % e código: %', F_COD_UNIDADE, F_PLACA, F_CODIGO_AFERICAO;
  END IF;
  --
  --

  -- DELETA AFERIÇÃO MANUTENÇÃO.
  -- Não verificamos quantas linhas atualizadas pois aferição pode não ter manutenções.
  UPDATE AFERICAO_MANUTENCAO_DATA
  SET DELETADO = TRUE, DATA_HORA_DELETADO = NOW()
  WHERE COD_UNIDADE = F_COD_UNIDADE
        AND COD_AFERICAO = F_CODIGO_AFERICAO;

  SELECT 'AFERIÇÃO DELETADA: '
         || F_CODIGO_AFERICAO
         || ', PLACA: '
         || F_PLACA
         || ', CÓDIGO DA UNIDADE: '
         || F_COD_UNIDADE
  INTO AVISO_AFERICAO_DELETADA;
END;
$$;

CREATE OR REPLACE FUNCTION FUNC_AFERICAO_DELETA_AFERICAO_VALORES(
      F_COD_UNIDADE                 BIGINT,
      F_PLACA                       TEXT,
      F_CODIGO_PNEU                 BIGINT,
      F_CODIGO_AFERICAO             BIGINT,
  OUT AVISO_AFERICAO_VALOR_DELETADA TEXT)
  RETURNS TEXT
LANGUAGE PLPGSQL
SECURITY DEFINER
AS $$
DECLARE
  QTD_LINHAS_ATUALIZADAS   BIGINT;
  -- Busca a quantidade de valores aferidos estão ativos nesta aferição
  QTD_VALORES_AFERICAO     BIGINT := (SELECT COUNT(*)
                                      FROM AFERICAO_VALORES
                                      WHERE COD_AFERICAO = F_CODIGO_AFERICAO
                                            AND COD_UNIDADE = F_COD_UNIDADE);

  -- Variável utilizada para melhorar o feedback da function de acordo com o fluxo
  PREFIXO_MENSAGEM_RETORNO TEXT;
BEGIN
  PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE);

  IF NOT EXISTS(SELECT *
                FROM AFERICAO_VALORES
                WHERE COD_AFERICAO = F_CODIGO_AFERICAO
                      AND COD_UNIDADE = F_COD_UNIDADE
                      AND COD_PNEU = F_CODIGO_PNEU)
  THEN
    RAISE EXCEPTION 'Nenhum valor de aferição encontrado com estes parâmetros: Unidade %, Placa %, Pneu % e Código %',
    F_COD_UNIDADE,
    F_PLACA,
    F_CODIGO_PNEU,
    F_CODIGO_AFERICAO;
  END IF;

  -- Define qual fluxo executar de acordo com a quantidade de valores de aferição encontrados
  CASE QTD_VALORES_AFERICAO
    WHEN 1
    THEN
      -- Somente um valor de aferição foi encontrado, deletar toda a aferição, manutenção e valores
      PERFORM FUNC_AFERICAO_DELETA_AFERICAO(F_COD_UNIDADE, F_PLACA, F_CODIGO_AFERICAO);
      PREFIXO_MENSAGEM_RETORNO := 'AFERIÇÃO, MANUTENÇÃO E VALOR DE AFERIÇÃO DELETADO ';
  ELSE
    -- Existe mais de um valor de aferição, deletar exclusivamente por COD_PNEU
    -- DELETA AFERIÇÃO.
    UPDATE AFERICAO_VALORES_DATA
    SET DELETADO         = TRUE,
      DATA_HORA_DELETADO = NOW()
    WHERE COD_UNIDADE = F_COD_UNIDADE
          AND COD_PNEU = F_CODIGO_PNEU
          AND COD_AFERICAO = F_CODIGO_AFERICAO;

    GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

    IF (QTD_LINHAS_ATUALIZADAS <= 0)
    THEN
      RAISE EXCEPTION 'Erro ao deletar os valores de aferição com estes parâmetros Unidade %, Placa %, Pneu % e Código %',
      F_COD_UNIDADE,
      F_PLACA,
      F_CODIGO_PNEU,
      F_CODIGO_AFERICAO;
    END IF;

    -- DELETA AFERIÇÃO MANUTENÇÃO.
    -- Não verificamos quantas linhas atualizadas pois aferição pode não ter manutenções.
    UPDATE AFERICAO_MANUTENCAO_DATA
    SET DELETADO         = TRUE,
      DATA_HORA_DELETADO = NOW()
    WHERE COD_UNIDADE = F_COD_UNIDADE
          AND COD_PNEU = F_CODIGO_PNEU
          AND COD_AFERICAO = F_CODIGO_AFERICAO;

    PREFIXO_MENSAGEM_RETORNO := 'VALOR DE AFERIÇÃO DELETADO ';
  END CASE;

  SELECT PREFIXO_MENSAGEM_RETORNO
         || ', CÓDIGO DA UNIDADE: '
         || F_COD_UNIDADE
         || ', PLACA: '
         || F_PLACA
         || ', CÓDIGO DO PNEU: '
         || F_CODIGO_PNEU
         || ', CÓDIGO DA AFERIÇÃO: '
         || F_CODIGO_AFERICAO
  INTO AVISO_AFERICAO_VALOR_DELETADA;
END;
$$;

--######################################################################################################################
--######################################################################################################################

-- PL-2157
DROP FUNCTION PUBLIC.THROW_GENERIC_ERROR(F_MESSAGE TEXT, VARIADIC F_ATTRS ANYARRAY);
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--########################################### COPIA ALTERNATIVAS DE RELATO #############################################
--######################################################################################################################
--######################################################################################################################
-- PL-2100
CREATE EXTENSION UNACCENT;

-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Se as alternativas de relato não existirem na unidade e setor de destino, serão copiadas.
-- Essa function não necessita que setores origem e destino sejam da mesma empresa. A cópia funciona entre empresas.
--
-- Precondições:
-- 1) Para a function funcionar é verificado a integridade entre unidade-setor.
-- 2) Verificado se existem aternativas de origem para serem copiadas.
-- 3) Na hora do insert é realizado um select apenas para copiar as alternativas que não estão no destino, com base na
-- desrição da alternativa.
--
-- Histórico:
-- 2019-07-23 -> Function criada (thaisksf - PL-2100).
CREATE OR REPLACE FUNCTION FUNC_RELATO_COPIA_ALTERNATIVAS_RELATO(
      F_COD_UNIDADE_ORIGEM       BIGINT,
      F_COD_SETOR_ORIGEM         BIGINT,
      F_COD_UNIDADE_DESTINO      BIGINT,
      F_COD_SETOR_DESTINO        BIGINT,
  OUT AVISO_ALTERNATIVA_INSERIDA TEXT)
  RETURNS TEXT
LANGUAGE PLPGSQL
SECURITY DEFINER
AS $$
DECLARE
  QTD_INSERTS BIGINT;
BEGIN
    -- VERIFICA SE O SETOR EXISTE NA UNIDADE DESTINO.
    PERFORM FUNC_GARANTE_SETOR_EXISTE(F_COD_UNIDADE_DESTINO, F_COD_SETOR_DESTINO);

    -- VERIFICA SE O SETOR EXISTE NA UNIDADE ORIGEM.
    PERFORM FUNC_GARANTE_SETOR_EXISTE(F_COD_UNIDADE_ORIGEM, F_COD_SETOR_ORIGEM);

    -- VERIFICA SE EXISTEM ALTERNATIVAS DE RELATO PARA SER COPIADO COM BASE NA UNIDADE E SETOR INFORMADOS.
    IF NOT EXISTS(SELECT RA.CODIGO
                  FROM RELATO_ALTERNATIVA RA
                  WHERE RA.COD_UNIDADE = F_COD_UNIDADE_ORIGEM
                    AND RA.COD_SETOR = F_COD_SETOR_ORIGEM)
    THEN
        RAISE EXCEPTION 'Não existem alternativas de relato para a unidade; %, setor: %!', F_COD_UNIDADE_ORIGEM,
            F_COD_SETOR_ORIGEM;
    END IF;

    -- COPIA ALTERNATIVAS DE RELATO QUE NÃO ESTÃO NA UNIDADE DESTINO.
    INSERT INTO RELATO_ALTERNATIVA (COD_UNIDADE, COD_SETOR, ALTERNATIVA, STATUS_ATIVO)
    SELECT F_COD_UNIDADE_DESTINO,
           F_COD_SETOR_DESTINO,
           TRIM_AND_REMOVE_EXTRA_SPACES(RA.ALTERNATIVA),
           RA.STATUS_ATIVO
    FROM RELATO_ALTERNATIVA RA
    WHERE RA.COD_UNIDADE = F_COD_UNIDADE_ORIGEM
      AND RA.COD_SETOR = F_COD_SETOR_ORIGEM
      AND NOT EXISTS(SELECT *
                     FROM RELATO_ALTERNATIVA R
                     WHERE R.COD_UNIDADE = F_COD_UNIDADE_DESTINO
                       AND R.COD_SETOR = F_COD_SETOR_DESTINO
                       AND (TRIM_AND_REMOVE_EXTRA_SPACES(UNACCENT(RA.ALTERNATIVA)) ILIKE
                            TRIM_AND_REMOVE_EXTRA_SPACES(UNACCENT(R.ALTERNATIVA))));
    GET DIAGNOSTICS QTD_INSERTS = ROW_COUNT;

    IF (QTD_INSERTS > 0)
    THEN
        SELECT ('ALTERNATIVAS DE RELATO COPIADOS COM SUCESSO PARA A UNIDADE: '
                    || (SELECT U.NOME FROM UNIDADE U WHERE U.CODIGO = F_COD_UNIDADE_DESTINO)
                    || ' DE CÓDIGO: '
                    || F_COD_UNIDADE_DESTINO
                    || ' , SETOR: '
                    || F_COD_SETOR_DESTINO
                    || ' , QUANTIDADE DE ALTERNATIVAS INSERIDAS: '
            || QTD_INSERTS)
        INTO AVISO_ALTERNATIVA_INSERIDA;
    ELSE
        SELECT ('NENHUMA ALTERNATIVA INSERIDA - A UNIDADE: '
                    || (SELECT U.NOME FROM UNIDADE U WHERE U.CODIGO = F_COD_UNIDADE_DESTINO)
                    || ' DE CÓDIGO: '
                    || F_COD_UNIDADE_DESTINO
                    || ' - , SETOR: '
                    || F_COD_SETOR_DESTINO
            || ', JÁ POSSUI AS ALTERNATIVAS')
        INTO AVISO_ALTERNATIVA_INSERIDA;
    END IF;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--################## ADICIONA ATRIBUTO "VEICULO COMPLETO" NA FUNCTION RELATORIO LISTAGEM DE VEÍCULOS ###################
--######################################################################################################################
--######################################################################################################################
DROP FUNCTION FUNC_VEICULO_RELATORIO_LISTAGEM_VEICULOS_BY_UNIDADE(F_COD_UNIDADES BIGINT[]);

-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Gera um relatório dos veículos existentes a partir de um array de unidades.
--
-- O relatório contém também a quantidade de pneus vinculados ao veículo, quantidade de posições que possui e se possui
-- diagrama.
--
-- Precondições:
-- 1) Possuir veículos na unidade
--
-- Histórico:
-- 2019-05-16 -> Function criada (thaisksf - PL-1964)
-- 2019-07-30 -> Adicionado coluna "Veículo Completo" (thaisksf - PL-2196).
--
CREATE OR REPLACE FUNCTION FUNC_VEICULO_RELATORIO_LISTAGEM_VEICULOS_BY_UNIDADE(F_COD_UNIDADES BIGINT[])
  RETURNS TABLE(
     UNIDADE TEXT,
     PLACA TEXT,
     MARCA TEXT,
     MODELO TEXT,
     TIPO TEXT,
     "DIAGRAMA VINCULADO?" TEXT,
     "KM ATUAL" TEXT,
     STATUS TEXT,
     "DATA/HORA CADASTRO" TEXT,
     "VEÍCULO COMPLETO" TEXT,
     "QTD PNEUS VINCULADOS" TEXT,
     "QTD POSIÇÕES DIAGRAMA" TEXT,
     "QTD POSIÇÕES SEM PNEUS" TEXT,
     "QTD ESTEPES" TEXT)
language plpgsql
as $$
DECLARE
  ESTEPES            INTEGER := 900;
  POSICOES_SEM_PNEUS INTEGER = 0;
  SIM                TEXT := 'SIM';
  NAO                TEXT := 'NÃO';
BEGIN
  RETURN QUERY
  -- Calcula a quantidade de pneus e estepes que estão vinculados na placa.
  WITH QTD_PNEUS_VINCULADOS_PLACA AS (
      SELECT
        V.PLACA,
        COUNT(VP.PLACA)
          FILTER (WHERE VP.POSICAO < ESTEPES)  AS QTD_PNEUS_VINCULADOS,
        COUNT(VP.PLACA)
          FILTER (WHERE VP.POSICAO >= ESTEPES) AS QTD_ESTEPES_VINCULADOS,
        VT.COD_DIAGRAMA
      FROM VEICULO V
        JOIN VEICULO_TIPO VT ON V.COD_TIPO = VT.CODIGO
        LEFT JOIN VEICULO_PNEU VP ON V.PLACA = VP.PLACA AND V.COD_UNIDADE = VP.COD_UNIDADE
      WHERE V.COD_UNIDADE = ANY(F_COD_UNIDADES)
      GROUP BY
        V.PLACA,
        VT.COD_DIAGRAMA
  ),

  -- Calcula a quantidade de posições nos diagramas que existem no prolog.
      QTD_POSICOES_DIAGRAMA AS (
        SELECT
          VDE.COD_DIAGRAMA,
          SUM(VDE.QT_PNEUS) AS QTD_POSICOES_DIAGRAMA
        FROM VEICULO_DIAGRAMA_EIXOS VDE
        GROUP BY COD_DIAGRAMA
    )

  SELECT
    U.NOME :: TEXT                                                     AS UNIDADE,
    V.PLACA :: TEXT                                                    AS PLACA,
    MA.NOME :: TEXT                                                    AS MARCA,
    MO.NOME :: TEXT                                                    AS MODELO,
    VT.NOME :: TEXT                                                    AS TIPO,
    CASE WHEN QPVP.COD_DIAGRAMA IS NULL
      THEN 'NÃO'
    ELSE 'SIM' END                                                     AS POSSUI_DIAGRAMA,
    V.KM :: TEXT                                                       AS KM_ATUAL,
    F_IF(V.STATUS_ATIVO, 'ATIVO' :: TEXT, 'INATIVO' :: TEXT)           AS STATUS,
    COALESCE(TO_CHAR(V.DATA_HORA_CADASTRO, 'DD/MM/YYYY HH24:MI'), '-') AS DATA_HORA_CADASTRO,
    -- Caso a quantidade de posições sem pneus seja 0 é porque o veículo está com todos os pneus - veículo completo.
    CASE WHEN (QSD.QTD_POSICOES_DIAGRAMA - QPVP.QTD_PNEUS_VINCULADOS) = POSICOES_SEM_PNEUS
      THEN SIM
    ELSE NAO END                                                       AS VEICULO_COMPLETO,
    QPVP.QTD_PNEUS_VINCULADOS :: TEXT                                  AS QTD_PNEUS_VINCULADOS,
    QSD.QTD_POSICOES_DIAGRAMA :: TEXT                                  AS QTD_POSICOES_DIAGRAMA,
    -- Calcula a quantidade de posições sem pneus.
    (QSD.QTD_POSICOES_DIAGRAMA - QPVP.QTD_PNEUS_VINCULADOS) :: TEXT    AS QTD_POSICOES_SEM_PNEUS,
    QPVP.QTD_ESTEPES_VINCULADOS :: TEXT                                AS QTD_ESTEPES_VINCULADOS
  FROM VEICULO V
    JOIN UNIDADE U ON V.COD_UNIDADE = U.CODIGO
    JOIN MODELO_VEICULO MO ON V.COD_MODELO = MO.CODIGO
    JOIN MARCA_VEICULO MA ON MO.COD_MARCA = MA.CODIGO
    JOIN VEICULO_TIPO VT ON V.COD_TIPO = VT.CODIGO
    RIGHT JOIN QTD_PNEUS_VINCULADOS_PLACA QPVP ON QPVP.PLACA = V.PLACA
    LEFT JOIN QTD_POSICOES_DIAGRAMA QSD ON QSD.COD_DIAGRAMA = QPVP.COD_DIAGRAMA
  ORDER BY
    U.NOME ASC,
    STATUS ASC,
    V.PLACA ASC,
    MA.NOME ASC,
    MO.NOME ASC,
    VT.NOME ASC,
    QTD_POSICOES_SEM_PNEUS DESC;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
-- Apenas para histórico:
-- JSON -> JSONB
-- DEFAULT NOW() UTC -> DEFAULT NOW()
-- Ativado audti na tabela de unidade
--
-- alter table audit.funcao_data_audit alter column data_hora_utc drop default ;
-- alter table audit.funcao_data_audit alter column data_hora_utc set default now();
--
-- ALTER TABLE audit.funcao_data_audit
--   ALTER COLUMN linha_anterior
--   SET DATA TYPE jsonb
--   USING linha_anterior::jsonb;
--
-- Sobre:
-- Function genérica para logar as alterações realizadas em qualquer tabela.
--
-- Ativando o log em uma tabela:
-- Para ativar o log de uma tabela, adicione uma trigger apontando para essa function na tabela escolhida.
-- Consulte o arquivo /ProLogDatabase/pilares/audit/audited_tables.sql para verificar como adicionar tal trigger
-- e também para adicionar a trigger criada no arquivo em questão, assim saberemos quais tabelas estão com sistema de
-- log ativo.
--
-- Under the hood:
-- Para cada tabela com sistema de log ativo, sempre que uma alteração for feita (INSERT, UPDATE ou DELETE) essa func
-- irá logar essas alterações. Para cada tabela auditada, uma nova tabela é criada para salvar seus logs. As tabelas
-- auditadas são criadas no schema audit e seguem o seguinte padrão de nomenclatura:
-- Para uma tabela chamada FUNCAO_DATA, a sua tabela de logs será criada no schema audit com o nome FUNCAO_DATA_AUDIT.
-- ATENÇÃO: Você não precisa criar a tabela de audit manualmente, a própria function fará quando tentar salvar o
-- primeiro log e perceber que a tabela ainda não existe.
--
-- O que é salvo nas tabelas de log atualmente:
-- CODIGO              -> Um código BIGSERIAL único para identificar o registro de log.
-- DATA_HORA_UTC       -> A data e hora em utc de quando o log foi salvo.
-- OPERACAO            -> A operação salva (I = Insert, U = Update, D = Delete).
-- PG_USERNAME         -> O nome do usuário do banco que executou tal alteração (ex.: prolog_user, prolog_user_diogenes)
-- PG_APPLICATION_NAME -> O nome da aplicação conectada ao banco que executou tal alteração (ex.: ProLog WS, DBeaver)
-- ROW_LOG             -> O JSON da linha que foi alterada na tabela monitorada.
-- IS_NEW_ROW          -> Indica se o dado JSON salvo é referente a row NEW ou OLD.
--
-- Histórico:
-- 2019-06-26 -> Function criada (wvinim - PL-2115).
-- 2019-07-23 -> (luizfp)
--  • Documentação criada.
--  • Alterado modo de salvamento, sempre salvando a row NEW com exceção de deleções, onde salva OLD.
--  • Adicionado coluna IS_NEW_ROW.
--  • Alterado para JSONB ao invés de JSON.
--  • Alterado data/hora default para não aplicar time zone UTC.
-- CREATE OR REPLACE FUNCTION AUDIT.FUNC_AUDIT()
--   RETURNS TRIGGER
-- LANGUAGE PLPGSQL
-- SECURITY DEFINER
-- AS $$
-- DECLARE
--   F_TABLE_NAME_AUDIT   TEXT := TG_RELNAME || '_audit';
--   F_TG_OP              TEXT := SUBSTRING(TG_OP, 1, 1);
--   F_JSON               TEXT := CASE
--                                WHEN F_TG_OP = 'D'
--                                  THEN ROW_TO_JSON(OLD)
--                                ELSE ROW_TO_JSON(NEW)
--                                END;
--   IS_NEW_ROW        BOOLEAN := CASE WHEN F_TG_OP = 'D' THEN FALSE ELSE TRUE END;
-- BEGIN
--   EXECUTE FORMAT(
--       'CREATE TABLE IF NOT EXISTS audit.%I (
--         CODIGO                  SERIAL,
--         DATA_HORA_UTC           TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
--         OPERACAO                VARCHAR(1),
--         PG_USERNAME             TEXT,
--         PG_APPLICATION_NAME     TEXT,
--         ROW_LOG                 JSONB,
--         IS_NEW_ROW              BOOLEAN
--       );', F_TABLE_NAME_AUDIT);
--
--   EXECUTE FORMAT(
--       'INSERT INTO audit.%I (operacao, row_log, is_new_row, pg_username, pg_application_name)
--        VALUES (%L, %L, %L, %L, %L);', F_TABLE_NAME_AUDIT, F_TG_OP, F_JSON, IS_NEW_ROW, SESSION_USER,
--       (SELECT CURRENT_SETTING('application_name')));
--   RETURN NULL;
-- END;
-- $$;
--
-- CREATE TRIGGER TG_FUNC_AUDIT_UNIDADE
--   AFTER INSERT OR UPDATE OR DELETE
--   ON UNIDADE
--   FOR EACH ROW EXECUTE PROCEDURE AUDIT.FUNC_AUDIT();
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--################ ADICIONA SECURITY DEFINER NA TG_FUNC_CHECKLIST_INSERE_TOKEN_UNIDADE_CHECKLIST_OFFLINE ###############
--######################################################################################################################
--######################################################################################################################
--PL-2189
CREATE OR REPLACE FUNCTION TG_FUNC_CHECKLIST_INSERE_TOKEN_UNIDADE_CHECKLIST_OFFLINE()
  RETURNS TRIGGER AS $TG_UNIDADE_ADICIONA_TOKEN_CHECKLIST_OFFLINE$
BEGIN
  INSERT INTO CHECKLIST_OFFLINE_DADOS_UNIDADE (COD_UNIDADE, TOKEN_SINCRONIZACAO_CHECKLIST)
  VALUES (NEW.CODIGO, F_RANDOM_STRING(64));
  RETURN NEW;
END;
$TG_UNIDADE_ADICIONA_TOKEN_CHECKLIST_OFFLINE$
LANGUAGE plpgsql SECURITY DEFINER;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--######################################## FUNCTION DELETA ORDEM DE SERVIÇO ############################################
--######################################################################################################################
--######################################################################################################################
-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Ao validar os dados, todos os itens da ordem de serviço são deletados, logo é deletado a OS.
--
-- Précondições:
-- É feito a validação da Unidade.
--
-- Histórico:
-- 2019-07-24-> Function criada (natanrotta - PL-2171).
--
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_OS_DELETA_OS_CHECKLIST(F_COD_UNIDADE BIGINT,
                                                              F_COD_OS BIGINT,
                                                              F_COD_CHECKLIST BIGINT,
                                                              OUT AVISO_CHECKLIST_OS_DELETADO TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    QTD_LINHAS_ATUALIZADAS BIGINT;
BEGIN
    --VERIFICA SE EXISTE UNIDADE
    PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE);

    --VERIFICA A EXISTÊNCIA DA ORDEM DE SERVIÇO DE ACORDO COM A CHAVE COMPOSTA RECEBIDA POR PARÂMETRO:
    IF NOT EXISTS(
            SELECT *
            FROM CHECKLIST_ORDEM_SERVICO_DATA
            WHERE CODIGO = F_COD_OS
              AND COD_UNIDADE = F_COD_UNIDADE
              AND COD_CHECKLIST = F_COD_CHECKLIST
        )
    THEN
        RAISE EXCEPTION 'ORDEM DE SERVIÇO COM CÓDIGO: %, UNIDADE: %, CÓDIGO DE CHECKLIST: % NÃO ENCONTRADO',
            F_COD_OS, F_COD_UNIDADE, F_COD_CHECKLIST;
    END IF;

    --DELETA ITEM ORDEM SERVIÇO:
    UPDATE CHECKLIST_ORDEM_SERVICO_ITENS_DATA
    SET DELETADO           = TRUE,
        DATA_HORA_DELETADO = NOW()
    WHERE COD_OS = F_COD_OS
      AND COD_UNIDADE = F_COD_UNIDADE;

    GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

    IF (QTD_LINHAS_ATUALIZADAS <= 0)
    THEN
        RAISE EXCEPTION 'ERRO AO DELETAR ITEM DA ORDEM DE SERVIÇO DA UNIDADE: %, CÓDIGO OS: % E CÓDIGO CHECKLIST: %',
            F_COD_UNIDADE, F_COD_OS, F_COD_CHECKLIST;
    END IF;

    --DELETA OS:
    UPDATE CHECKLIST_ORDEM_SERVICO_DATA
    SET DELETADO           = TRUE,
        DATA_HORA_DELETADO = NOW()
    WHERE CODIGO = F_COD_OS
      AND COD_UNIDADE = F_COD_UNIDADE
      AND COD_CHECKLIST = F_COD_CHECKLIST;

    GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

    IF (QTD_LINHAS_ATUALIZADAS <= 0)
    THEN
        RAISE EXCEPTION 'ERRO AO DELETAR ORDEM DE SERVIÇO DA UNIDADE: %, CÓDIGO OS: %, CÓDIGO CHECKLIST: %',
            F_COD_UNIDADE, F_COD_OS, F_COD_CHECKLIST;
    END IF;

    SELECT 'DELEÇÃO DA OS: '
               || F_COD_OS
               || ', CÓDIGO CHECKLIST'
               || F_COD_CHECKLIST
               || ', CÓDIGO UNIDADE: '
               || F_COD_UNIDADE
               || ' REALIZADO COM SUCESSO.'
    INTO AVISO_CHECKLIST_OS_DELETADO;
END
$$;
--######################################################################################################################
--######################################################################################################################\


--######################################################################################################################
--######################################################################################################################
DROP FUNCTION FUNC_CHECKLIST_RELATORIO_AMBEV_REALIZADOS_DIA(F_COD_UNIDADES BIGINT[],
    F_DATA_INICIAL DATE,
    F_DATA_FINAL DATE);


CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_RELATORIO_AMBEV_REALIZADOS_DIA(F_COD_UNIDADES BIGINT[],
                                                                         F_DATA_INICIAL DATE,
                                                                         F_DATA_FINAL DATE)
    RETURNS TABLE
            (
                "UNIDADE"            TEXT,
                "DATA"               TEXT,
                "QTD CHECKS SAÍDA"   BIGINT,
                "ADERÊNCIA SAÍDA"    TEXT,
                "QTD CHECKS RETORNO" BIGINT,
                "ADERÊNCIA RETORNO"  TEXT,
                "TOTAL DE CHECKS"    BIGINT,
                "TOTAL DE VIAGENS"   BIGINT,
                "ADERÊNCIA DIA"      TEXT
            )
    LANGUAGE SQL
AS
$$
SELECT DADOS.NOME_UNIDADE                AS NOME_UNIDADE,
       TO_CHAR(DADOS.DATA, 'DD/MM/YYYY') AS DATA,
       DADOS.CHECKS_SAIDA                AS QTD_CHECKS_SAIDA,
       TRUNC((DADOS.CHECKS_SAIDA :: FLOAT / DADOS.TOTAL_VIAGENS) * 100) ||
       '%'                               AS ADERENCIA_SAIDA,
       DADOS.CHECKS_RETORNO              AS QTD_CHECKS_RETORNO,
       TRUNC((DADOS.CHECKS_RETORNO :: FLOAT / DADOS.TOTAL_VIAGENS) * 100) ||
       '%'                               AS ADERENCIA_RETORNO,
       DADOS.TOTAL_CHECKS                AS TOTAL_CHECKS,
       DADOS.TOTAL_VIAGENS               AS TOTAL_MAPAS,
       TRUNC(((DADOS.CHECKS_SAIDA + DADOS.CHECKS_RETORNO) :: FLOAT / (DADOS.TOTAL_VIAGENS * 2)) * 100) ||
       '%'                               AS ADERENCIA_DIA
FROM (SELECT U.NOME                                                       AS NOME_UNIDADE,
             (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: DATE AS DATA,
             SUM(CASE
                     WHEN C.TIPO = 'S'
                         THEN 1
                     ELSE 0 END)                                          AS CHECKS_SAIDA,
             SUM(CASE
                     WHEN C.TIPO = 'R'
                         THEN 1
                     ELSE 0 END)                                          AS CHECKS_RETORNO,
             COUNT(C.DATA_HORA :: DATE)                                   AS TOTAL_CHECKS,
             DIA_ESCALA.TOTAL_VIAGENS                                     AS TOTAL_VIAGENS
      FROM CHECKLIST C
               JOIN UNIDADE U ON C.COD_UNIDADE = U.CODIGO
               LEFT JOIN (SELECT ED.DATA         AS DATA_ESCALA,
                                 COUNT(ED.PLACA) AS TOTAL_VIAGENS
                          FROM ESCALA_DIARIA ED
                                   JOIN VEICULO V ON V.PLACA = ED.PLACA
                          WHERE ED.COD_UNIDADE = ANY (ARRAY [F_COD_UNIDADES])
                            AND ED.DATA >= F_DATA_INICIAL
                            AND ED.DATA <= F_DATA_FINAL
                          GROUP BY ED.DATA
                          ORDER BY ED.DATA ASC) AS DIA_ESCALA
                         ON DIA_ESCALA.DATA_ESCALA = (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: DATE
      WHERE C.COD_UNIDADE = ANY (F_COD_UNIDADES)
        AND (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: DATE >= F_DATA_INICIAL
        AND (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: DATE <= F_DATA_FINAL
      GROUP BY U.CODIGO, DATA, DIA_ESCALA.TOTAL_VIAGENS
      ORDER BY U.NOME, (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: DATE) AS DADOS
$$;
--######################################################################################################################
--######################################################################################################################


--######################################################################################################################
--######################################################################################################################
DROP FUNCTION FUNC_CHECKLIST_RELATORIO_RESUMO_REALIZADOS(F_COD_UNIDADES BIGINT[],
    F_PLACA_VEICULO TEXT,
    F_DATA_INICIAL DATE,
    F_DATA_FINAL DATE);

CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_RELATORIO_RESUMO_REALIZADOS(
  F_COD_UNIDADES  BIGINT [],
  F_PLACA_VEICULO TEXT,
  F_DATA_INICIAL  DATE,
  F_DATA_FINAL    DATE)
  RETURNS TABLE(
    "UNIDADE"                     TEXT,
    "CÓDIGO CHECKLIST"            BIGINT,
    "DATA REALIZAÇÃO"             TEXT,
    "DATA IMPORTADO"              TEXT,
    "COLABORADOR"                 TEXT,
    "PLACA"                       TEXT,
    "KM"                          BIGINT,
    "TEMPO REALIZAÇÃO (SEGUNDOS)" BIGINT,
    "TIPO"                        TEXT,
    "TOTAL DE PERGUNTAS"          BIGINT,
    "TOTAL NOK"                   BIGINT,
    "PRIORIDADE BAIXA"            BIGINT,
    "PRIORIDADE ALTA"             BIGINT,
    "PRIORIDADE CRÍTICA"          BIGINT)
LANGUAGE SQL
AS $$
WITH CHECKLITS AS (
    SELECT
      C.CODIGO                                                                          AS COD_CHECKLIST,
      U.CODIGO                                                                          AS COD_UNIDADE,
      U.NOME                                                                            AS NOME_UNIDADE,
      C.DATA_HORA                                                                       AS DATA_HORA_REALIZACAO,
      C.DATA_HORA_SINCRONIZACAO                                                         AS DATA_HORA_SINCRONIZACAO,
      TO_CHAR(C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE), 'DD/MM/YYYY HH24:MI') AS DATA_REALIZACAO_CHECK,
      TO_CHAR(C.DATA_HORA_IMPORTADO_PROLOG AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE),
              'DD/MM/YYYY HH24:MI')                                                     AS DATA_IMPORTADO,
      CO.NOME                                                                           AS NOME_COLABORADOR,
      C.PLACA_VEICULO                                                                   AS PLACA_VEICULO,
      C.KM_VEICULO                                                                      AS KM_VEICULO,
      C.TEMPO_REALIZACAO / 1000                                                         AS TEMPO_REALIZACAO_SEGUNDOS,
      F_IF(C.TIPO = 'S', 'Saída' :: TEXT, 'Retorno' :: TEXT)                            AS TIPO_CHECKLIST,
      COUNT(C.CODIGO)                                                                   AS TOTAL_PERGUNTAS
    FROM CHECKLIST C
      JOIN CHECKLIST_PERGUNTAS CP
        ON CP.COD_CHECKLIST_MODELO = C.COD_CHECKLIST_MODELO
      JOIN COLABORADOR CO
        ON C.CPF_COLABORADOR = CO.CPF
      JOIN UNIDADE U
        ON C.COD_UNIDADE = U.CODIGO
    WHERE
      C.COD_UNIDADE = ANY (F_COD_UNIDADES)
      AND CP.STATUS_ATIVO
      AND C.PLACA_VEICULO LIKE F_PLACA_VEICULO
      AND (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: DATE >= F_DATA_INICIAL
      AND (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: DATE <= F_DATA_FINAL
    GROUP BY
      C.CODIGO,
      U.CODIGO,
      CO.CPF,
      CO.NOME,
      C.DATA_HORA,
      C.DATA_HORA_IMPORTADO_PROLOG,
      C.DATA_HORA_SINCRONIZACAO,
      C.COD_UNIDADE,
      C.PLACA_VEICULO,
      C.KM_VEICULO,
      C.TEMPO_REALIZACAO,
      C.TIPO),

    RESPOSTAS_NOK AS (
      SELECT
        CR.COD_CHECKLIST AS COD_CHECKLIST,
        COUNT(CASE WHEN CR.RESPOSTA <> 'OK'
          THEN 1 END)    AS TOTAL_NOK,
        COUNT(CASE WHEN CAP.PRIORIDADE = 'BAIXA'
          THEN 1 END)    AS TOTAL_BAIXAS,
        COUNT(CASE WHEN CAP.PRIORIDADE = 'ALTA'
          THEN 1 END)    AS TOTAL_ALTAS,
        COUNT(CASE WHEN CAP.PRIORIDADE = 'CRITICA'
          THEN 1 END)    AS TOTAL_CRITICAS
      FROM CHECKLIST_RESPOSTAS CR
        JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
          ON CR.COD_ALTERNATIVA = CAP.CODIGO
        JOIN CHECKLIST C
          ON CR.COD_CHECKLIST = C.CODIGO
      WHERE
        CR.RESPOSTA <> 'OK'
        AND C.COD_UNIDADE = ANY (F_COD_UNIDADES)
        AND CAP.STATUS_ATIVO
        AND C.PLACA_VEICULO LIKE F_PLACA_VEICULO
        AND (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: DATE >= F_DATA_INICIAL
        AND (C.DATA_HORA AT TIME ZONE TZ_UNIDADE(C.COD_UNIDADE)) :: DATE <= F_DATA_FINAL
      GROUP BY CR.COD_CHECKLIST
  )

SELECT
  C.NOME_UNIDADE,
  C.COD_CHECKLIST,
  C.DATA_REALIZACAO_CHECK,
  COALESCE(C.DATA_IMPORTADO, '-'),
  C.NOME_COLABORADOR,
  C.PLACA_VEICULO,
  C.KM_VEICULO,
  C.TEMPO_REALIZACAO_SEGUNDOS,
  C.TIPO_CHECKLIST,
  C.TOTAL_PERGUNTAS,
  COALESCE(RN.TOTAL_NOK, 0),
  COALESCE(RN.TOTAL_BAIXAS, 0),
  COALESCE(RN.TOTAL_ALTAS, 0),
  COALESCE(RN.TOTAL_CRITICAS, 0)
FROM CHECKLITS C
  LEFT JOIN RESPOSTAS_NOK RN
    ON C.COD_CHECKLIST = RN.COD_CHECKLIST
ORDER BY
  C.NOME_UNIDADE,
  C.DATA_HORA_SINCRONIZACAO DESC;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
UPDATE public.funcao_prolog_v11
SET
    descricao = 'Permite ao usuário que tenha acesso ao seu próprio pré-contracheque.',
    impacto = 'BAIXO'
WHERE codigo = 35 AND cod_pilar = 3;
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################









--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
-- PL-2118
-- Deleta checklits que não possuem entrada na CHECKLIST_RESPOSTAS.
-- Após migration em BD local apenas esses dois ficaram sem setar os valores para total de perguntas e/ou
-- alternativas, pois não possuem entradas na tabela de respostas.
DELETE FROM CHECKLIST C WHERE C.CODIGO IN (193729, 669191);

ALTER TABLE CHECKLIST_DATA
    ADD COLUMN TOTAL_PERGUNTAS_OK SMALLINT;
ALTER TABLE CHECKLIST_DATA
    ADD COLUMN TOTAL_PERGUNTAS_NOK SMALLINT;
ALTER TABLE CHECKLIST_DATA
    ADD COLUMN TOTAL_ALTERNATIVAS_OK SMALLINT;
ALTER TABLE CHECKLIST_DATA
    ADD COLUMN TOTAL_ALTERNATIVAS_NOK SMALLINT;

COMMENT ON COLUMN CHECKLIST_DATA.TOTAL_PERGUNTAS_OK IS 'Total de perguntas OK no checklist.
    Uma pergunta é OK quando nenhuma de suas alternatias for selecionada durante a realização do checklist.';
COMMENT ON COLUMN CHECKLIST_DATA.TOTAL_PERGUNTAS_NOK IS 'Total de perguntas NOK no checklist.
    Uma pergunta é NOK quando pelo menos uma de suas alternatias for selecionada durante a realização do checklist.';
COMMENT ON COLUMN CHECKLIST_DATA.TOTAL_ALTERNATIVAS_OK IS 'Total de alternativas OK no checklist, de todas as perguntas.
    Uma alternativa é OK quando não for selecionada durante a realização do checklist.';
COMMENT ON COLUMN CHECKLIST_DATA.TOTAL_ALTERNATIVAS_NOK IS 'Total de alternativas NOK no checklist, de todas as perguntas.
    Uma alternativa é NOK quando for selecionada durante a realização do checklist.';

WITH QTD_POR_PERGUNTA AS (
    SELECT CR.COD_CHECKLIST,
           CR.COD_PERGUNTA,
           COUNT(CASE
                     WHEN CR.RESPOSTA <> 'OK'
                         THEN 1 END) AS ALTERNATIVAS_TOTAL_NOK,
           COUNT(CASE
                     WHEN CR.RESPOSTA = 'OK'
                         THEN 1 END) AS ALTERNATIVAS_TOTAL_OK
    FROM CHECKLIST_RESPOSTAS CR
    GROUP BY CR.COD_CHECKLIST, CR.COD_PERGUNTA),

     QTDS AS (
         SELECT QPP.COD_CHECKLIST                                                  AS COD_CHECKLIST,
                COUNT(*) FILTER ( WHERE QPP.ALTERNATIVAS_TOTAL_NOK > 0)            AS TOTAL_PERGUNTAS_NOK,
                COUNT(*) - COUNT(*) FILTER ( WHERE QPP.ALTERNATIVAS_TOTAL_NOK > 0) AS TOTAL_PERGUNTAS_OK,
                SUM(QPP.ALTERNATIVAS_TOTAL_NOK)                                    AS TOTAL_ALTERNATIVAS_NOK,
                SUM(QPP.ALTERNATIVAS_TOTAL_OK)                                     AS TOTAL_ALTERNATIVAS_OK
         FROM QTD_POR_PERGUNTA QPP
         GROUP BY QPP.COD_CHECKLIST)

UPDATE CHECKLIST_DATA C
SET TOTAL_PERGUNTAS_OK     = QTDS.TOTAL_PERGUNTAS_OK,
    TOTAL_PERGUNTAS_NOK    = QTDS.TOTAL_PERGUNTAS_NOK,
    TOTAL_ALTERNATIVAS_OK  = QTDS.TOTAL_ALTERNATIVAS_OK,
    TOTAL_ALTERNATIVAS_NOK = QTDS.TOTAL_ALTERNATIVAS_NOK
FROM QTDS
WHERE C.CODIGO = QTDS.COD_CHECKLIST;

ALTER TABLE CHECKLIST_DATA ALTER COLUMN TOTAL_PERGUNTAS_OK SET NOT NULL;
ALTER TABLE CHECKLIST_DATA ALTER COLUMN TOTAL_PERGUNTAS_NOK SET NOT NULL;
ALTER TABLE CHECKLIST_DATA ALTER COLUMN TOTAL_ALTERNATIVAS_OK SET NOT NULL;
ALTER TABLE CHECKLIST_DATA ALTER COLUMN TOTAL_ALTERNATIVAS_NOK SET NOT NULL;

CREATE OR REPLACE VIEW CHECKLIST AS
SELECT C.COD_UNIDADE,
       C.COD_CHECKLIST_MODELO,
       C.CODIGO,
       C.DATA_HORA,
       C.DATA_HORA_IMPORTADO_PROLOG,
       C.CPF_COLABORADOR,
       C.PLACA_VEICULO,
       C.TIPO,
       C.TEMPO_REALIZACAO,
       C.KM_VEICULO,
       C.DATA_HORA_SINCRONIZACAO,
       C.FONTE_DATA_HORA_REALIZACAO,
       C.VERSAO_APP_MOMENTO_REALIZACAO,
       C.VERSAO_APP_MOMENTO_SINCRONIZACAO,
       C.DEVICE_ID,
       C.DEVICE_IMEI,
       C.DEVICE_UPTIME_REALIZACAO_MILLIS,
       C.DEVICE_UPTIME_SINCRONIZACAO_MILLIS,
       C.FOI_OFFLINE,
       C.TOTAL_PERGUNTAS_OK,
       C.TOTAL_PERGUNTAS_NOK,
       C.TOTAL_ALTERNATIVAS_OK,
       C.TOTAL_ALTERNATIVAS_NOK
FROM CHECKLIST_DATA C
WHERE C.DELETADO = FALSE;


-- Altera function de insert de um checklist offline.
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_INSERT_CHECKLIST_INFOS(
  F_COD_UNIDADE_CHECKLIST              BIGINT,
  F_COD_MODELO_CHECKLIST               BIGINT,
  F_DATA_HORA_REALIZACAO               TIMESTAMP WITH TIME ZONE,
  F_COD_COLABORADOR                    BIGINT,
  F_COD_VEICULO                        BIGINT,
  F_PLACA_VEICULO                      TEXT,
  F_TIPO_CHECKLIST                     CHAR,
  F_KM_COLETADO                        BIGINT,
  F_TEMPO_REALIZACAO                   BIGINT,
  F_DATA_HORA_SINCRONIZACAO            TIMESTAMP WITH TIME ZONE,
  F_FONTE_DATA_HORA_REALIZACAO         TEXT,
  F_VERSAO_APP_MOMENTO_REALIZACAO      INTEGER,
  F_VERSAO_APP_MOMENTO_SINCRONIZACAO   INTEGER,
  F_DEVICE_ID                          TEXT,
  F_DEVICE_IMEI                        TEXT,
  F_DEVICE_UPTIME_REALIZACAO_MILLIS    BIGINT,
  F_DEVICE_UPTIME_SINCRONIZACAO_MILLIS BIGINT,
  F_TOTAL_PERGUNTAS_OK                 INTEGER,
  F_TOTAL_PERGUNTAS_NOK                INTEGER,
  F_TOTAL_ALTERNATIVAS_OK              INTEGER,
  F_TOTAL_ALTERNATIVAS_NOK             INTEGER)
  RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    -- Iremos atualizar o KM do Veículo somente para o caso em que o KM atual do veículo for menor que o KM coletado.
    DEVE_ATUALIZAR_KM_VEICULO BOOLEAN := (CASE
                                              WHEN (F_KM_COLETADO > (SELECT V.KM
                                                                     FROM VEICULO V
                                                                     WHERE V.CODIGO = F_COD_VEICULO))
                                                  THEN
                                                  TRUE
                                              ELSE FALSE END);
    COD_CHECKLIST_INSERIDO    BIGINT;
    QTD_LINHAS_ATUALIZADAS    BIGINT;
BEGIN

    INSERT INTO CHECKLIST(COD_UNIDADE,
                          COD_CHECKLIST_MODELO,
                          DATA_HORA,
                          CPF_COLABORADOR,
                          PLACA_VEICULO,
                          TIPO,
                          TEMPO_REALIZACAO,
                          KM_VEICULO,
                          DATA_HORA_SINCRONIZACAO,
                          FONTE_DATA_HORA_REALIZACAO,
                          VERSAO_APP_MOMENTO_REALIZACAO,
                          VERSAO_APP_MOMENTO_SINCRONIZACAO,
                          DEVICE_ID,
                          DEVICE_IMEI,
                          DEVICE_UPTIME_REALIZACAO_MILLIS,
                          DEVICE_UPTIME_SINCRONIZACAO_MILLIS,
                          FOI_OFFLINE,
                          TOTAL_PERGUNTAS_OK,
                          TOTAL_PERGUNTAS_NOK,
                          TOTAL_ALTERNATIVAS_OK,
                          TOTAL_ALTERNATIVAS_NOK)
    VALUES (F_COD_UNIDADE_CHECKLIST,
            F_COD_MODELO_CHECKLIST,
            F_DATA_HORA_REALIZACAO,
            (SELECT C.CPF FROM COLABORADOR C WHERE C.CODIGO = F_COD_COLABORADOR),
            F_PLACA_VEICULO,
            F_TIPO_CHECKLIST,
            F_TEMPO_REALIZACAO,
            F_KM_COLETADO,
            F_DATA_HORA_SINCRONIZACAO,
            F_FONTE_DATA_HORA_REALIZACAO,
            F_VERSAO_APP_MOMENTO_REALIZACAO,
            F_VERSAO_APP_MOMENTO_SINCRONIZACAO,
            F_DEVICE_ID,
            F_DEVICE_IMEI,
            F_DEVICE_UPTIME_REALIZACAO_MILLIS,
            F_DEVICE_UPTIME_SINCRONIZACAO_MILLIS,
            TRUE,
            F_TOTAL_PERGUNTAS_OK,
            F_TOTAL_PERGUNTAS_NOK,
            F_TOTAL_ALTERNATIVAS_OK,
            F_TOTAL_ALTERNATIVAS_NOK) RETURNING CODIGO INTO COD_CHECKLIST_INSERIDO;

    -- Verificamos se o insert funcionou.
    IF COD_CHECKLIST_INSERIDO <= 0
    THEN
        RAISE EXCEPTION 'Não foi possível inserir o checklist';
    END IF;

    IF DEVE_ATUALIZAR_KM_VEICULO
    THEN
        UPDATE VEICULO SET KM = F_KM_COLETADO WHERE CODIGO = F_COD_VEICULO;
    END IF;

    GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

    -- Se devemos atualizar o KM mas nenhuma linha foi alterada, então temos um erro.
    IF (DEVE_ATUALIZAR_KM_VEICULO AND QTD_LINHAS_ATUALIZADAS <= 0)
    THEN
        RAISE EXCEPTION 'Não foi possível atualizar o km do veículo';
    END IF;

    RETURN COD_CHECKLIST_INSERIDO;
END;
$$;
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################






--######################################################################################################################
--######################################################################################################################
--######################## CRIAR ESTRUTURA DE CADASTRO DE DISPOSITIVOS MÓVEIS POR EMPRESA ##############################
--######################################################################################################################
--######################################################################################################################
--PL-2150

-- Insere o pilar Geral
INSERT INTO PILAR_PROLOG ("codigo", "pilar")
VALUES (5, 'GERAL');

-- Insere o agrupamento
INSERT INTO funcao_prolog_agrupamento ("codigo", "nome", "cod_pilar")
VALUES (27, 'Dispositivos Móveis', 5);

-- Insere a permissão de gestão de dispositivos móveis
INSERT INTO funcao_prolog_v11 ("codigo", "funcao_old", "cod_pilar", "impacto", "cod_agrupamento", "descricao", "funcao")
VALUES (501,
        'Dispositivos Móveis - Gestão',
        5,
        'MEDIO',
        27,
        'Permite ao usuário fazer a gestão dos dispositivos móveis',
        'Gestão (cadastro, edição e deleção)');

-- Cria tabela para armazenar as marcas dos dispositivos módeis a nível ProLog
CREATE TABLE DISPOSITIVO_MOVEL_MARCA_PROLOG
(
  CODIGO SERIAL NOT NULL,
  NOME   CITEXT NOT NULL,
  CONSTRAINT PK_DISPOSITIVO_MOVEL_MARCA_PROLOG PRIMARY KEY (CODIGO),
  CONSTRAINT UNIQUE_DISPOSITIVO_MOVEL_MARCA_DISPOSITIVO UNIQUE(NOME)
);
COMMENT ON TABLE DISPOSITIVO_MOVEL_MARCA_PROLOG
IS 'Marcas de dispositivos móveis a nível ProLog';

-- Insere as marcas de celular filtradas
INSERT INTO DISPOSITIVO_MOVEL_MARCA_PROLOG ("nome")
VALUES ('Alcatel'),
       ('Acer'),
       ('Asus'),
       ('Blu'),
       ('Ericsson'),
       ('Gigabyte'),
       ('Google'),
       ('Huawei'),
       ('HP'),
       ('HTC'),
       ('Inove'),
       ('Kyocera'),
       ('Lenovo'),
       ('LG'),
       ('Microsoft'),
       ('Motorola'),
       ('Nokia'),
       ('OnePlus'),
       ('Panasonic'),
       ('Philips'),
       ('Positivo'),
       ('Qbex'),
       ('Qualcomm'),
       ('Quantum'),
       ('Razer Phon'),
       ('Samsung'),
       ('Sharp'),
       ('Siemens'),
       ('Sony'),
       ('Sony Ericsson'),
       ('Toshiba'),
       ('Xiaomi'),
       ('ZTE');

-- Cria function de listagem de marcas de dispositivos móveis a nível ProLog
CREATE OR REPLACE FUNCTION FUNC_DISPOSITIVO_GET_MARCAS_DISPOSITIVO_MOVEL()
  RETURNS TABLE(
    COD_MARCA  INT,
    NOME_MARCA TEXT)
LANGUAGE SQL
AS $$
SELECT MDP.CODIGO AS COD_MARCA, MDP.NOME :: TEXT AS NOME_MARCA
FROM DISPOSITIVO_MOVEL_MARCA_PROLOG MDP
ORDER BY MDP.NOME ASC;
$$;

-- Cria tabela para armazenar os dados dos dispositivos móveis
CREATE TABLE DISPOSITIVO_MOVEL
(
  CODIGO      BIGSERIAL NOT NULL,
  COD_EMPRESA BIGINT    NOT NULL,
  COD_MARCA   BIGINT,
  MODELO      VARCHAR(55),
  DESCRICAO   TEXT,
  CONSTRAINT PK_DISPOSITIVO_MOVEL PRIMARY KEY (CODIGO),
  CONSTRAINT FK_DISPOSITIVO_MOVEL_COD_EMPRESA FOREIGN KEY (COD_EMPRESA) REFERENCES EMPRESA (CODIGO),
  CONSTRAINT FK_DISPOSITIVO_MOVEL_COD_MARCA FOREIGN KEY (COD_MARCA) REFERENCES DISPOSITIVO_MOVEL_MARCA_PROLOG (CODIGO)
);
COMMENT ON TABLE DISPOSITIVO_MOVEL
    IS 'Tabela que armazena os dados dos dispositivos móveis';
COMMENT ON COLUMN DISPOSITIVO_MOVEL.COD_MARCA
    IS 'Pode ser null para evitar de o usuário tentar cadastrar um dispositivo de uma marca que não existe no ProLog e isso impedir o cadastro dele';

-- Cria tabela para armazenar os IMEIs dos dispositivos móveis
CREATE TABLE DISPOSITIVO_MOVEL_IMEI
(
  CODIGO          BIGSERIAL PRIMARY KEY NOT NULL,
  COD_EMPRESA     BIGINT                NOT NULL,
  COD_DISPOSITIVO BIGINT                NOT NULL,
  IMEI            VARCHAR(15)           NOT NULL,
  CONSTRAINT FK_DISPOSITIVO_MOVEL_IMEI_EMPRESA FOREIGN KEY (COD_EMPRESA) REFERENCES EMPRESA (CODIGO),
  CONSTRAINT FK_DISPOSITIVO_MOVEL_IMEI_DISPOSITIVO FOREIGN KEY (COD_DISPOSITIVO) REFERENCES DISPOSITIVO_MOVEL (CODIGO)
);
CREATE UNIQUE INDEX DISPOSITIVO_MOVEL_IMEI_UINDEX
  ON DISPOSITIVO_MOVEL_IMEI (COD_EMPRESA, IMEI);
COMMENT ON TABLE DISPOSITIVO_MOVEL_IMEI
IS 'Tabela que armazena os IMEIs dos dispositivos móveis';

-- Cria function de listagem de dispositivos móveis por empresa com os IMEIs
CREATE OR REPLACE FUNCTION FUNC_DISPOSITIVO_GET_DISPOSITIVOS_MOVEIS(F_COD_EMPRESA BIGINT)
  RETURNS TABLE(
    COD_EMPRESA     BIGINT,
    COD_MARCA       BIGINT,
    MARCA           TEXT,
    COD_DISPOSITIVO BIGINT,
    MODELO          TEXT,
    DESCRICAO       TEXT,
    COD_IMEI        BIGINT,
    IMEI            TEXT
  )
LANGUAGE SQL
AS $$
SELECT DM.COD_EMPRESA    AS COD_EMPRESA,
       DM.COD_MARCA      AS COD_MARCA,
       MD.NOME :: TEXT   AS MARCA,
       DM.CODIGO         AS COD_DISPOSITIVO,
       DM.MODELO :: TEXT AS MODELO,
       DM.DESCRICAO      AS DESCRICAO,
       DI.CODIGO         AS COD_IMEI,
       DI.IMEI :: TEXT   AS IMEI
FROM DISPOSITIVO_MOVEL_IMEI DI
       LEFT JOIN DISPOSITIVO_MOVEL DM ON DM.CODIGO = DI.COD_DISPOSITIVO
       LEFT JOIN DISPOSITIVO_MOVEL_MARCA_PROLOG MD ON MD.CODIGO = DM.COD_MARCA
WHERE DI.COD_EMPRESA = F_COD_EMPRESA
ORDER BY DM.CODIGO, DI.CODIGO;
$$;

-- Cria function para buscar um dispositivo móvel específico
CREATE OR REPLACE FUNCTION FUNC_DISPOSITIVO_GET_DISPOSITIVO_MOVEL(F_COD_EMPRESA BIGINT, F_COD_DISPOSITIVO BIGINT)
  RETURNS TABLE(
    COD_EMPRESA     BIGINT,
    COD_MARCA       BIGINT,
    MARCA           TEXT,
    COD_DISPOSITIVO BIGINT,
    MODELO          TEXT,
    DESCRICAO       TEXT,
    COD_IMEI        BIGINT,
    IMEI            TEXT
  )
LANGUAGE SQL
AS $$
SELECT DM.COD_EMPRESA    AS COD_EMPRESA,
       DM.COD_MARCA      AS COD_MARCA,
       MD.NOME :: TEXT   AS MARCA,
       DM.CODIGO         AS COD_DISPOSITIVO,
       DM.MODELO :: TEXT AS MODELO,
       DM.DESCRICAO      AS DESCRICAO,
       DI.CODIGO         AS COD_IMEI,
       DI.IMEI :: TEXT   AS IMEI
FROM DISPOSITIVO_MOVEL_IMEI DI
       LEFT JOIN DISPOSITIVO_MOVEL DM ON DM.CODIGO = DI.COD_DISPOSITIVO
       LEFT JOIN DISPOSITIVO_MOVEL_MARCA_PROLOG MD ON MD.CODIGO = DM.COD_MARCA
WHERE DI.COD_EMPRESA = F_COD_EMPRESA
  AND DM.CODIGO = F_COD_DISPOSITIVO
ORDER BY DM.CODIGO, DI.CODIGO;
$$;

-- Cria function para inserir apenas o dispositivo móvel
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

-- Cria function para inserir um IMEI
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
  -- Verifica se o dispositivo móvel existe no banco
  IF (SELECT NOT EXISTS(
                   SELECT CODIGO FROM DISPOSITIVO_MOVEL WHERE CODIGO = F_COD_DISPOSITIVO
                                                          AND COD_EMPRESA = F_COD_EMPRESA))
  THEN
    PERFORM THROW_GENERIC_ERROR(
              'Não foi possível encontrar este dispositivo móvel, confira o código e tente novamente');
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

-- Cria function que insere o dispositivo móvel com a lista de IMEIs.
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

-- Cria function para editar o dispositivo móvel e a lista de IMEIs
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

-- Cria function para deletar o dispositivo móvel.
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

--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--############################# INCLUIR IMEIS NA ESTRUTURA DE GESTÃO DE MARCAÇÕES ######################################
--######################################################################################################################
--######################################################################################################################
--PL-2152

-- Identificador único do aparelho. No Android, é equivalente ao Android ID.
ALTER TABLE intervalo
  ADD device_id text;

-- IMEI do aparelho.
ALTER TABLE intervalo
  ADD device_imei text;

-- O tempo, em milissegundos, desde que o aparelho foi ligado até o momento da realização da marcação.
ALTER TABLE intervalo
  ADD device_uptime_realizacao_millis bigint;

-- O tempo, em milissegundos, desde que o aparelho foi ligado até o momento da sincronização da marcação.
ALTER TABLE intervalo
  ADD device_uptime_sincronizacao_millis bigint;

-- A versão da API do Android no momento da realização da marcação.
ALTER TABLE intervalo
  ADD android_api_version integer;

-- A marca do aparelho.
ALTER TABLE intervalo
  ADD marca_device text;

-- O modelo do aparelho.
ALTER TABLE intervalo
  ADD modelo_device text;

-- Dropa a function antiga de insert de marcações
DROP FUNCTION FUNC_MARCACAO_INSERT_MARCACAO_JORNADA(BIGINT, BIGINT, BIGINT, TIMESTAMP WITH TIME ZONE, TEXT, TEXT, TEXT,
                                                    TEXT, TEXT, TEXT, TIMESTAMP WITH TIME ZONE, INTEGER, INTEGER);

-- Cria nova function de insert de marcações
CREATE OR REPLACE FUNCTION FUNC_MARCACAO_INSERT_MARCACAO_JORNADA(
                                                      F_COD_UNIDADE                        BIGINT,
                                                      F_COD_TIPO_INTERVALO                 BIGINT,
                                                      F_CPF_COLABORADOR                    BIGINT,
                                                      F_DATA_HORA                          TIMESTAMP WITH TIME ZONE,
                                                      F_TIPO_MARCACAO                      TEXT,
                                                      F_FONTE_DATA_HORA                    TEXT,
                                                      F_JUSTIFICATIVA_TEMPO_RECOMENDADO    TEXT,
                                                      F_JUSTIFICATIVA_ESTOURO              TEXT,
                                                      F_LATITUDE_MARCACAO                  TEXT,
                                                      F_LONGITUDE_MARCACAO                 TEXT,
                                                      F_DATA_HORA_SINCRONIZACAO            TIMESTAMP WITH TIME ZONE,
                                                      F_VERSAO_APP_MOMENTO_MARCACAO        INTEGER,
                                                      F_VERSAO_APP_MOMENTO_SINCRONIZACAO   INTEGER,
                                                      F_DEVICE_ID                          TEXT,
                                                      F_DEVICE_IMEI                        TEXT,
                                                      F_DEVICE_UPTIME_REALIZACAO_MILLIS    BIGINT,
                                                      F_DEVICE_UPTIME_SINCRONIZACAO_MILLIS BIGINT,
                                                      F_ANDROID_API_VERSION                BIGINT,
                                                      F_MARCA_DEVICE                       TEXT,
                                                      F_MODELO_DEVICE                      TEXT)
  RETURNS BIGINT
LANGUAGE SQL
AS $$
INSERT INTO INTERVALO (COD_UNIDADE,
                       COD_TIPO_INTERVALO,
                       CPF_COLABORADOR,
                       DATA_HORA,
                       TIPO_MARCACAO,
                       FONTE_DATA_HORA,
                       JUSTIFICATIVA_TEMPO_RECOMENDADO,
                       JUSTIFICATIVA_ESTOURO,
                       LATITUDE_MARCACAO,
                       LONGITUDE_MARCACAO,
                       DATA_HORA_SINCRONIZACAO,
                       VERSAO_APP_MOMENTO_MARCACAO,
                       VERSAO_APP_MOMENTO_SINCRONIZACAO,
                       DEVICE_ID,
                       DEVICE_IMEI,
                       DEVICE_UPTIME_REALIZACAO_MILLIS,
                       DEVICE_UPTIME_SINCRONIZACAO_MILLIS,
                       ANDROID_API_VERSION,
                       MARCA_DEVICE,
                       MODELO_DEVICE)
VALUES (F_COD_UNIDADE,
        F_COD_TIPO_INTERVALO,
        F_CPF_COLABORADOR,
        F_DATA_HORA,
        F_TIPO_MARCACAO,
        F_FONTE_DATA_HORA,
        F_JUSTIFICATIVA_TEMPO_RECOMENDADO,
        F_JUSTIFICATIVA_ESTOURO,
        F_LATITUDE_MARCACAO,
        F_LONGITUDE_MARCACAO,
        F_DATA_HORA_SINCRONIZACAO,
        F_VERSAO_APP_MOMENTO_MARCACAO,
        F_VERSAO_APP_MOMENTO_SINCRONIZACAO,
        F_DEVICE_ID,
        F_DEVICE_IMEI,
        F_DEVICE_UPTIME_REALIZACAO_MILLIS,
        F_DEVICE_UPTIME_SINCRONIZACAO_MILLIS,
        F_ANDROID_API_VERSION,
        F_MARCA_DEVICE,
        F_MODELO_DEVICE)
    RETURNING CODIGO;
$$;

-- Adiciona os dados de gestão de IMEIs na gestão de marcações

-- Dropa elementos com dependência em intervalos agrupados
-- view_intervalo_mapa_colaborador
DROP VIEW view_intervalo_mapa_colaborador;

-- view_extrato_mapas_versus_intervalos
DROP VIEW view_extrato_mapas_versus_intervalos;

-- func_intervalos_agrupados
DROP FUNCTION FUNC_INTERVALOS_AGRUPADOS(bigint,bigint,bigint);

-- func_marcacao_get_marcacoes_colaborador_ajuste
DROP FUNCTION FUNC_MARCACAO_GET_MARCACOES_COLABORADOR_AJUSTE(BIGINT,BIGINT,DATE);

-- func_marcacao_relatorio_marcacoes_diarias
DROP FUNCTION FUNC_MARCACAO_RELATORIO_MARCACOES_DIARIAS(bigint,date,date,text);

-- func_relatorio_intervalos_mapas
DROP FUNCTION FUNC_RELATORIO_INTERVALOS_MAPAS(bigint,date,date);

-- func_relatorio_intervalo_escala_diaria
DROP FUNCTION FUNC_RELATORIO_INTERVALO_ESCALA_DIARIA(bigint,bigint,date,date,text);

-- Recria os elementos com dependências em intervalos agrupados
-- FUNC_INTERVALOS_AGRUPADOS
CREATE OR REPLACE FUNCTION FUNC_INTERVALOS_AGRUPADOS(
  F_COD_UNIDADE BIGINT,
  F_CPF_COLABORADOR BIGINT,
  F_COD_TIPO_INTERVALO BIGINT)
  RETURNS TABLE(
    FONTE_DATA_HORA_FIM             TEXT,
    FONTE_DATA_HORA_INICIO          TEXT,
    JUSTIFICATIVA_ESTOURO           TEXT,
    JUSTIFICATIVA_TEMPO_RECOMENDADO TEXT,
    LATITUDE_MARCACAO_INICIO        TEXT,
    LONGITUDE_MARCACAO_INICIO       TEXT,
    LATITUDE_MARCACAO_FIM           TEXT,
    LONGITUDE_MARCACAO_FIM          TEXT,
    COD_UNIDADE                     BIGINT,
    CPF_COLABORADOR                 BIGINT,
    COD_TIPO_INTERVALO              BIGINT,
    COD_TIPO_INTERVALO_POR_UNIDADE  BIGINT,
    DATA_HORA_INICIO                TIMESTAMP WITH TIME ZONE,
    DATA_HORA_FIM                   TIMESTAMP WITH TIME ZONE,
    COD_MARCACAO_INICIO             BIGINT,
    COD_MARCACAO_FIM                BIGINT,
    STATUS_ATIVO_INICIO             BOOLEAN,
    STATUS_ATIVO_FIM                BOOLEAN,
    FOI_AJUSTADO_INICIO             BOOLEAN,
    FOI_AJUSTADO_FIM                BOOLEAN,
    DATA_HORA_SINCRONIZACAO_INICIO  TIMESTAMP WITH TIME ZONE,
    DATA_HORA_SINCRONIZACAO_FIM     TIMESTAMP WITH TIME ZONE,
    TIPO_JORNADA                    BOOLEAN,
    DEVICE_IMEI_INICIO              TEXT,
    DEVICE_IMEI_INICIO_RECONHECIDO  BOOLEAN,
    DEVICE_MARCA_INICIO             TEXT,
    DEVICE_MODELO_INICIO            TEXT,
    DEVICE_IMEI_FIM                 TEXT,
    DEVICE_IMEI_FIM_RECONHECIDO     BOOLEAN,
    DEVICE_MARCA_FIM                TEXT,
    DEVICE_MODELO_FIM               TEXT)
LANGUAGE SQL
AS $$
WITH INICIOS AS (
    SELECT
      MI.COD_MARCACAO_INICIO,
      MV.COD_MARCACAO_FIM                   AS COD_MARCACAO_VINCULO,
      I.FONTE_DATA_HORA                     AS FONTE_DATA_HORA_INICIO,
      I.LATITUDE_MARCACAO                   AS LATITUDE_MARCACAO_INICIO,
      I.LONGITUDE_MARCACAO                  AS LONGITUDE_MARCACAO_INICIO,
      I.COD_UNIDADE                         AS COD_UNIDADE,
      I.CPF_COLABORADOR                     AS CPF_COLABORADOR,
      I.COD_TIPO_INTERVALO                  AS COD_TIPO_INTERVALO,
      I.DATA_HORA                           AS DATA_HORA_INICIO,
      I.CODIGO                              AS CODIGO_INICIO,
      I.STATUS_ATIVO                        AS STATUS_ATIVO_INICIO,
      I.FOI_AJUSTADO                        AS FOI_AJUSTADO_INICIO,
      I.DATA_HORA_SINCRONIZACAO             AS DATA_HORA_SINCRONIZACAO_INICIO,
      I.DEVICE_IMEI                         AS DEVICE_IMEI,
      DMI.IMEI IS NOT NULL                  AS DEVICE_RECONHECIDO,
      I.MARCA_DEVICE                        AS DEVICE_MARCA,
      I.MODELO_DEVICE                       AS DEVICE_MODELO,
      VIT.CODIGO_TIPO_INTERVALO_POR_UNIDADE AS COD_TIPO_INTERVALO_POR_UNIDADE,
      VIT.TIPO_JORNADA                      AS TIPO_JORNADA
    FROM MARCACAO_INICIO MI
      LEFT JOIN MARCACAO_VINCULO_INICIO_FIM MV
        ON MI.COD_MARCACAO_INICIO = MV.COD_MARCACAO_INICIO
      JOIN INTERVALO I
        ON MI.COD_MARCACAO_INICIO = I.CODIGO
      JOIN UNIDADE UNI
        ON UNI.CODIGO = I.COD_UNIDADE
      LEFT JOIN DISPOSITIVO_MOVEL_IMEI DMI
        ON DMI.COD_EMPRESA = UNI.COD_EMPRESA AND DMI.IMEI = I.DEVICE_IMEI
      JOIN VIEW_INTERVALO_TIPO VIT
        ON I.COD_TIPO_INTERVALO = VIT.CODIGO
    WHERE I.VALIDO = TRUE
          AND CASE WHEN F_COD_UNIDADE IS NULL THEN TRUE ELSE I.COD_UNIDADE = F_COD_UNIDADE END
          AND CASE WHEN F_CPF_COLABORADOR IS NULL THEN TRUE ELSE I.CPF_COLABORADOR = F_CPF_COLABORADOR END
          AND CASE WHEN F_COD_TIPO_INTERVALO IS NULL THEN TRUE ELSE I.COD_TIPO_INTERVALO = F_COD_TIPO_INTERVALO END
),

    FINS AS (
      SELECT
        MF.COD_MARCACAO_FIM,
        MV.COD_MARCACAO_INICIO                AS COD_MARCACAO_VINCULO,
        F.FONTE_DATA_HORA                     AS FONTE_DATA_HORA_FIM,
        F.JUSTIFICATIVA_ESTOURO               AS JUSTIFICATIVA_ESTOURO,
        F.JUSTIFICATIVA_TEMPO_RECOMENDADO     AS JUSTIFICATIVA_TEMPO_RECOMENDADO,
        F.LATITUDE_MARCACAO                   AS LATITUDE_MARCACAO_FIM,
        F.LONGITUDE_MARCACAO                  AS LONGITUDE_MARCACAO_FIM,
        F.COD_UNIDADE                         AS COD_UNIDADE,
        F.CPF_COLABORADOR                     AS CPF_COLABORADOR,
        F.COD_TIPO_INTERVALO                  AS COD_TIPO_INTERVALO,
        F.DATA_HORA                           AS DATA_HORA_FIM,
        F.CODIGO                              AS CODIGO_FIM,
        F.STATUS_ATIVO                        AS STATUS_ATIVO_FIM,
        F.FOI_AJUSTADO                        AS FOI_AJUSTADO_FIM,
        F.DATA_HORA_SINCRONIZACAO             AS DATA_HORA_SINCRONIZACAO_FIM,
        VIT.CODIGO_TIPO_INTERVALO_POR_UNIDADE AS COD_TIPO_INTERVALO_POR_UNIDADE,
        F.DEVICE_IMEI                         AS DEVICE_IMEI,
        DMI.IMEI IS NOT NULL                  AS DEVICE_RECONHECIDO,
        F.MARCA_DEVICE                        AS DEVICE_MARCA,
        F.MODELO_DEVICE                       AS DEVICE_MODELO,
        VIT.TIPO_JORNADA                      AS TIPO_JORNADA
      FROM MARCACAO_FIM MF
        LEFT JOIN MARCACAO_VINCULO_INICIO_FIM MV
          ON MF.COD_MARCACAO_FIM = MV.COD_MARCACAO_FIM
        JOIN INTERVALO F
          ON MF.COD_MARCACAO_FIM = F.CODIGO
        JOIN UNIDADE UNI
          ON UNI.CODIGO = F.COD_UNIDADE
        LEFT JOIN DISPOSITIVO_MOVEL_IMEI DMI
          ON DMI.COD_EMPRESA = UNI.COD_EMPRESA AND DMI.IMEI = F.DEVICE_IMEI
        JOIN VIEW_INTERVALO_TIPO VIT
          ON F.COD_TIPO_INTERVALO = VIT.CODIGO
      WHERE F.VALIDO = TRUE
            AND CASE WHEN F_COD_UNIDADE IS NULL THEN TRUE ELSE F.COD_UNIDADE = F_COD_UNIDADE END
            AND CASE WHEN F_CPF_COLABORADOR IS NULL THEN TRUE ELSE F.CPF_COLABORADOR = F_CPF_COLABORADOR END
            AND CASE WHEN F_COD_TIPO_INTERVALO IS NULL THEN TRUE ELSE F.COD_TIPO_INTERVALO = F_COD_TIPO_INTERVALO END
  )

SELECT I.FONTE_DATA_HORA_INICIO,
       F.FONTE_DATA_HORA_FIM,
       F.JUSTIFICATIVA_ESTOURO,
       F.JUSTIFICATIVA_TEMPO_RECOMENDADO,
       I.LATITUDE_MARCACAO_INICIO,
       I.LONGITUDE_MARCACAO_INICIO,
       F.LATITUDE_MARCACAO_FIM,
       F.LONGITUDE_MARCACAO_FIM,
       COALESCE(I.COD_UNIDADE, F.COD_UNIDADE)                                       AS COD_UNIDADE,
       COALESCE(I.CPF_COLABORADOR, F.CPF_COLABORADOR)                               AS CPF_COLABORADOR,
       COALESCE(I.COD_TIPO_INTERVALO, F.COD_TIPO_INTERVALO)                         AS COD_TIPO_INTERVALO,
       COALESCE(I.COD_TIPO_INTERVALO_POR_UNIDADE, F.COD_TIPO_INTERVALO_POR_UNIDADE) AS COD_TIPO_INTERVALO_POR_UNIDADE,
       I.DATA_HORA_INICIO,
       F.DATA_HORA_FIM,
       I.CODIGO_INICIO,
       F.CODIGO_FIM,
       I.STATUS_ATIVO_INICIO,
       F.STATUS_ATIVO_FIM,
       I.FOI_AJUSTADO_INICIO,
       F.FOI_AJUSTADO_FIM,
       I.DATA_HORA_SINCRONIZACAO_INICIO,
       F.DATA_HORA_SINCRONIZACAO_FIM,
       (F.TIPO_JORNADA = TRUE OR I.TIPO_JORNADA = TRUE)                             AS TIPO_JORNADA,
       I.DEVICE_IMEI :: TEXT                                                        AS DEVICE_IMEI_INICIO,
       I.DEVICE_RECONHECIDO :: BOOLEAN                                              AS DEVICE_IMEI_INICIO_RECONHECIDO,
       I.DEVICE_MARCA :: TEXT                                                       AS DEVICE_MARCA_INICIO,
       I.DEVICE_MODELO :: TEXT                                                      AS DEVICE_MODELO_INICIO,
       F.DEVICE_IMEI :: TEXT                                                        AS DEVICE_IMEI_FIM,
       F.DEVICE_RECONHECIDO :: BOOLEAN                                              AS DEVICE_IMEI_FIM_RECONHECIDO,
       F.DEVICE_MARCA :: TEXT                                                       AS DEVICE_MARCA_FIM,
       F.DEVICE_MODELO :: TEXT                                                      AS DEVICE_MODELO_FIM
FROM INICIOS I
  FULL OUTER JOIN FINS F
    ON I.COD_MARCACAO_VINCULO = F.COD_MARCACAO_FIM
ORDER BY
  CPF_COLABORADOR,
  COD_TIPO_INTERVALO,
  COALESCE(DATA_HORA_INICIO, DATA_HORA_FIM)
$$;

-- Recria a função que retorna as marcações agrupadas para ajustes.
CREATE OR REPLACE FUNCTION FUNC_MARCACAO_GET_MARCACOES_COLABORADOR_AJUSTE(F_COD_TIPO_MARCACAO BIGINT,
                                                                          F_COD_COLABORADOR   BIGINT,
                                                                          F_DIA DATE)
  RETURNS TABLE(
    COD_MARCACAO_INICIO            BIGINT,
    COD_MARCACAO_FIM               BIGINT,
    DATA_HORA_INICIO               TIMESTAMP WITHOUT TIME ZONE,
    DATA_HORA_FIM                  TIMESTAMP WITHOUT TIME ZONE,
    STATUS_ATIVO_INICIO            BOOLEAN,
    STATUS_ATIVO_FIM               BOOLEAN,
    FOI_AJUSTADO_INICIO            BOOLEAN,
    FOI_AJUSTADO_FIM               BOOLEAN,
    COD_TIPO_MARCACAO              BIGINT,
    NOME_TIPO_MARCACAO             TEXT,
    DEVICE_IMEI_INICIO             TEXT,
    DEVICE_IMEI_INICIO_RECONHECIDO BOOLEAN,
    DEVICE_MARCA_INICIO            TEXT,
    DEVICE_MODELO_INICIO           TEXT,
    DEVICE_IMEI_FIM                TEXT,
    DEVICE_IMEI_FIM_RECONHECIDO    BOOLEAN,
    DEVICE_MARCA_FIM               TEXT,
    DEVICE_MODELO_FIM              TEXT)
LANGUAGE PLPGSQL
AS $$
BEGIN
  RETURN QUERY
  SELECT F.COD_MARCACAO_INICIO                                       AS COD_MARCACAO_INICIO,
         F.COD_MARCACAO_FIM                                          AS COD_MARCACAO_FIM,
         (F.DATA_HORA_INICIO AT TIME ZONE TZ_UNIDADE(F.COD_UNIDADE)) AS DATA_HORA_INICIO,
         (F.DATA_HORA_FIM AT TIME ZONE TZ_UNIDADE(F.COD_UNIDADE))    AS DATA_HORA_FIM,
         F.STATUS_ATIVO_INICIO                                       AS STATUS_ATIVO_INICIO,
         F.STATUS_ATIVO_FIM                                          AS STATUS_ATIVO_FIM,
         F.FOI_AJUSTADO_INICIO                                       AS FOI_AJUSTADO_INICIO,
         F.FOI_AJUSTADO_FIM                                          AS FOI_AJUSTADO_FIM,
         F.COD_TIPO_INTERVALO                                        AS COD_TIPO_MARCACAO,
         IT.NOME :: TEXT                                             AS NOME_TIPO_MARCACAO,
         F.DEVICE_IMEI_INICIO :: TEXT                                AS DEVICE_IMEI_INICIO,
         F.DEVICE_IMEI_INICIO_RECONHECIDO :: BOOLEAN                 AS DEVICE_IMEI_INICIO_RECONHECIDO,
         F.DEVICE_MARCA_INICIO :: TEXT                               AS DEVICE_MARCA_INICIO,
         F.DEVICE_MODELO_INICIO :: TEXT                              AS DEVICE_MODELO_INICIO,
         F.DEVICE_IMEI_FIM :: TEXT                                   AS DEVICE_IMEI_FIM,
         F.DEVICE_IMEI_FIM_RECONHECIDO :: BOOLEAN                    AS DEVICE_IMEI_FIM_RECONHECIDO,
         F.DEVICE_MARCA_FIM :: TEXT                                  AS DEVICE_MARCA_FIM,
         F.DEVICE_MODELO_FIM :: TEXT                                 AS DEVICE_MODELO_FIM
  FROM FUNC_INTERVALOS_AGRUPADOS(NULL,
                                 (SELECT C.CPF FROM COLABORADOR C WHERE C.CODIGO = F_COD_COLABORADOR),
                                 F_COD_TIPO_MARCACAO) F
         JOIN INTERVALO_TIPO IT ON F.COD_TIPO_INTERVALO = IT.CODIGO
  WHERE ((F.DATA_HORA_INICIO AT TIME ZONE TZ_UNIDADE(F.COD_UNIDADE)) :: DATE = F_DIA
           OR (F.DATA_HORA_FIM AT TIME ZONE TZ_UNIDADE(F.COD_UNIDADE)) :: DATE = F_DIA)
  ORDER BY COALESCE(F.DATA_HORA_INICIO, F.DATA_HORA_FIM);
END;
$$;

-- view_extrato_mapas_versus_intervalos
CREATE VIEW VIEW_EXTRATO_MAPAS_VERSUS_INTERVALOS AS
  SELECT m.data,
    m.mapa,
    m.cod_unidade,
    (m.fator + (1)::double precision) AS intervalos_previstos,
    ((
        CASE
            WHEN (COALESCE((trunc((date_part('epoch'::text, (int_aj1.data_hora_fim - int_aj1.data_hora_inicio)) / (60)::double precision)))::text, '-'::text) <> '-'::text) THEN 1
            ELSE 0
        END +
        CASE
            WHEN (COALESCE((trunc((date_part('epoch'::text, (int_aj2.data_hora_fim - int_aj2.data_hora_inicio)) / (60)::double precision)))::text, '-'::text) <> '-'::text) THEN 1
            ELSE 0
        END) +
        CASE
            WHEN (COALESCE((trunc((date_part('epoch'::text, (int_mot.data_hora_fim - int_mot.data_hora_inicio)) / (60)::double precision)))::text, '-'::text) <> '-'::text) THEN 1
            ELSE 0
        END) AS intervalos_realizados,
    mot.cpf AS cpf_motorista,
    mot.nome AS nome_motorista,
    COALESCE(to_char(((int_mot.data_hora_inicio)::time without time zone)::interval, 'HH24:MI'::text), '-'::text) AS inicio_intervalo_mot,
    COALESCE(to_char(((int_mot.data_hora_fim)::time without time zone)::interval, 'HH24:MI'::text), '-'::text) AS fim_intervalo_mot,
    F_IF(int_mot.device_imei_inicio_reconhecido AND int_mot.device_imei_fim_reconhecido, TRUE, FALSE)       AS marcacoes_reconhecidas_mot,
    COALESCE((trunc((date_part('epoch'::text, (int_mot.data_hora_fim - int_mot.data_hora_inicio)) / (60)::double precision)))::text, '-'::text) AS tempo_decorrido_minutos_mot,
        CASE
            WHEN (int_mot.data_hora_fim IS NULL) THEN '-'::text
            WHEN ((60)::double precision > (date_part('epoch'::text, (int_mot.data_hora_fim - int_mot.data_hora_inicio)) / (60)::double precision)) THEN 'NÃO'::text
            ELSE 'SIM'::text
        END AS mot_cumpriu_tempo_minimo,
    aj1.cpf AS cpf_aj1,
    COALESCE(aj1.nome, '-'::character varying) AS nome_aj1,
    COALESCE(to_char(((int_aj1.data_hora_inicio)::time without time zone)::interval, 'HH24:MI'::text), '-'::text) AS inicio_intervalo_aj1,
    COALESCE(to_char(((int_aj1.data_hora_fim)::time without time zone)::interval, 'HH24:MI'::text), '-'::text) AS fim_intervalo_aj1,
    F_IF(int_aj1.device_imei_inicio_reconhecido AND int_aj1.device_imei_fim_reconhecido, TRUE, FALSE)       AS marcacoes_reconhecidas_aj1,
    COALESCE((trunc((date_part('epoch'::text, (int_aj1.data_hora_fim - int_aj1.data_hora_inicio)) / (60)::double precision)))::text, '-'::text) AS tempo_decorrido_minutos_aj1,
        CASE
            WHEN (int_aj1.data_hora_fim IS NULL) THEN '-'::text
            WHEN ((60)::double precision > (date_part('epoch'::text, (int_aj1.data_hora_fim - int_aj1.data_hora_inicio)) / (60)::double precision)) THEN 'NÃO'::text
            ELSE 'SIM'::text
        END AS aj1_cumpriu_tempo_minimo,
    aj2.cpf AS cpf_aj2,
    COALESCE(aj2.nome, '-'::character varying) AS nome_aj2,
    COALESCE(to_char(((int_aj2.data_hora_inicio)::time without time zone)::interval, 'HH24:MI'::text), '-'::text) AS inicio_intervalo_aj2,
    COALESCE(to_char(((int_aj2.data_hora_fim)::time without time zone)::interval, 'HH24:MI'::text), '-'::text) AS fim_intervalo_aj2,
    F_IF(int_aj2.device_imei_inicio_reconhecido AND int_aj2.device_imei_fim_reconhecido, TRUE, FALSE)       AS marcacoes_reconhecidas_aj2,
    COALESCE((trunc((date_part('epoch'::text, (int_aj2.data_hora_fim - int_aj2.data_hora_inicio)) / (60)::double precision)))::text, '-'::text) AS tempo_decorrido_minutos_aj2,
        CASE
            WHEN (int_aj2.data_hora_fim IS NULL) THEN '-'::text
            WHEN ((60)::double precision > (date_part('epoch'::text, (int_aj2.data_hora_fim - int_aj2.data_hora_inicio)) / (60)::double precision)) THEN 'NÃO'::text
            ELSE 'SIM'::text
        END AS aj2_cumpriu_tempo_minimo
   FROM (((((((mapa m
     JOIN unidade_funcao_produtividade ufp ON ((ufp.cod_unidade = m.cod_unidade)))
     JOIN colaborador mot ON ((((mot.cod_unidade = m.cod_unidade) AND (mot.cod_funcao = ufp.cod_funcao_motorista)) AND (mot.matricula_ambev = m.matricmotorista))))
     LEFT JOIN colaborador aj1 ON ((((aj1.cod_unidade = m.cod_unidade) AND (aj1.cod_funcao = ufp.cod_funcao_ajudante)) AND (aj1.matricula_ambev = m.matricajud1))))
     LEFT JOIN colaborador aj2 ON ((((aj2.cod_unidade = m.cod_unidade) AND (aj2.cod_funcao = ufp.cod_funcao_ajudante)) AND (aj2.matricula_ambev = m.matricajud2))))
     LEFT JOIN func_intervalos_agrupados(NULL::bigint, NULL::bigint, NULL::bigint) int_mot(fonte_data_hora_fim, fonte_data_hora_inicio, justificativa_estouro, justificativa_tempo_recomendado, latitude_marcacao_inicio, longitude_marcacao_inicio, latitude_marcacao_fim, longitude_marcacao_fim, cod_unidade, cpf_colaborador, cod_tipo_intervalo, cod_tipo_intervalo_por_unidade, data_hora_inicio, data_hora_fim, cod_marcacao_inicio, cod_marcacao_fim, status_ativo_inicio, status_ativo_fim, foi_ajustado_inicio, foi_ajustado_fim, data_hora_sincronizacao_inicio, data_hora_sincronizacao_fim, tipo_jornada) ON (((int_mot.cpf_colaborador = mot.cpf) AND ((int_mot.data_hora_inicio)::date = m.data))))
     LEFT JOIN func_intervalos_agrupados(NULL::bigint, NULL::bigint, NULL::bigint) int_aj1(fonte_data_hora_fim, fonte_data_hora_inicio, justificativa_estouro, justificativa_tempo_recomendado, latitude_marcacao_inicio, longitude_marcacao_inicio, latitude_marcacao_fim, longitude_marcacao_fim, cod_unidade, cpf_colaborador, cod_tipo_intervalo, cod_tipo_intervalo_por_unidade, data_hora_inicio, data_hora_fim, cod_marcacao_inicio, cod_marcacao_fim, status_ativo_inicio, status_ativo_fim, foi_ajustado_inicio, foi_ajustado_fim, data_hora_sincronizacao_inicio, data_hora_sincronizacao_fim, tipo_jornada) ON (((int_aj1.cpf_colaborador = aj1.cpf) AND ((int_aj1.data_hora_inicio)::date = m.data))))
     LEFT JOIN func_intervalos_agrupados(NULL::bigint, NULL::bigint, NULL::bigint) int_aj2(fonte_data_hora_fim, fonte_data_hora_inicio, justificativa_estouro, justificativa_tempo_recomendado, latitude_marcacao_inicio, longitude_marcacao_inicio, latitude_marcacao_fim, longitude_marcacao_fim, cod_unidade, cpf_colaborador, cod_tipo_intervalo, cod_tipo_intervalo_por_unidade, data_hora_inicio, data_hora_fim, cod_marcacao_inicio, cod_marcacao_fim, status_ativo_inicio, status_ativo_fim, foi_ajustado_inicio, foi_ajustado_fim, data_hora_sincronizacao_inicio, data_hora_sincronizacao_fim, tipo_jornada) ON (((int_aj2.cpf_colaborador = aj2.cpf) AND ((int_aj2.data_hora_inicio)::date = m.data))))
  ORDER BY m.mapa DESC;

-- view_intervalo_mapa_colaborador
CREATE VIEW VIEW_INTERVALO_MAPA_COLABORADOR AS
  SELECT VIEW_EXTRATO_MAPAS_VERSUS_INTERVALOS.DATA,
         VIEW_EXTRATO_MAPAS_VERSUS_INTERVALOS.MAPA,
         VIEW_EXTRATO_MAPAS_VERSUS_INTERVALOS.COD_UNIDADE,
         VIEW_EXTRATO_MAPAS_VERSUS_INTERVALOS.CPF_MOTORISTA               AS CPF,
         VIEW_EXTRATO_MAPAS_VERSUS_INTERVALOS.INICIO_INTERVALO_MOT        AS INICIO_INTERVALO,
         VIEW_EXTRATO_MAPAS_VERSUS_INTERVALOS.FIM_INTERVALO_MOT           AS FIM_INTERVALO,
         VIEW_EXTRATO_MAPAS_VERSUS_INTERVALOS.TEMPO_DECORRIDO_MINUTOS_MOT AS TEMPO_DECORRIDO_MINUTOS,
         VIEW_EXTRATO_MAPAS_VERSUS_INTERVALOS.MOT_CUMPRIU_TEMPO_MINIMO    AS CUMPRIU_TEMPO_MINIMO
  FROM VIEW_EXTRATO_MAPAS_VERSUS_INTERVALOS
  UNION
  SELECT VIEW_EXTRATO_MAPAS_VERSUS_INTERVALOS.DATA,
         VIEW_EXTRATO_MAPAS_VERSUS_INTERVALOS.MAPA,
         VIEW_EXTRATO_MAPAS_VERSUS_INTERVALOS.COD_UNIDADE,
         VIEW_EXTRATO_MAPAS_VERSUS_INTERVALOS.CPF_AJ1                     AS CPF,
         VIEW_EXTRATO_MAPAS_VERSUS_INTERVALOS.INICIO_INTERVALO_AJ1        AS INICIO_INTERVALO,
         VIEW_EXTRATO_MAPAS_VERSUS_INTERVALOS.FIM_INTERVALO_AJ1           AS FIM_INTERVALO,
         VIEW_EXTRATO_MAPAS_VERSUS_INTERVALOS.TEMPO_DECORRIDO_MINUTOS_AJ1 AS TEMPO_DECORRIDO_MINUTOS,
         VIEW_EXTRATO_MAPAS_VERSUS_INTERVALOS.AJ1_CUMPRIU_TEMPO_MINIMO    AS CUMPRIU_TEMPO_MINIMO
  FROM VIEW_EXTRATO_MAPAS_VERSUS_INTERVALOS
  UNION
  SELECT VIEW_EXTRATO_MAPAS_VERSUS_INTERVALOS.DATA,
         VIEW_EXTRATO_MAPAS_VERSUS_INTERVALOS.MAPA,
         VIEW_EXTRATO_MAPAS_VERSUS_INTERVALOS.COD_UNIDADE,
         VIEW_EXTRATO_MAPAS_VERSUS_INTERVALOS.CPF_AJ2                     AS CPF,
         VIEW_EXTRATO_MAPAS_VERSUS_INTERVALOS.INICIO_INTERVALO_AJ2        AS INICIO_INTERVALO,
         VIEW_EXTRATO_MAPAS_VERSUS_INTERVALOS.FIM_INTERVALO_AJ2           AS FIM_INTERVALO,
         VIEW_EXTRATO_MAPAS_VERSUS_INTERVALOS.TEMPO_DECORRIDO_MINUTOS_AJ2 AS TEMPO_DECORRIDO_MINUTOS,
         VIEW_EXTRATO_MAPAS_VERSUS_INTERVALOS.AJ2_CUMPRIU_TEMPO_MINIMO    AS CUMPRIU_TEMPO_MINIMO
  FROM VIEW_EXTRATO_MAPAS_VERSUS_INTERVALOS;

-- Adiciona os dados de gestão de IMEIs na listagem de marcações
-- Dropa a função antiga para a inclusão de novas colunas no retorno.
DROP FUNCTION FUNC_MARCACAO_GET_MARCACOES_ACOMPANHAMENTO(BIGINT, BIGINT, TEXT);

-- Recria a função que retorna as marcações agrupadas para listagem.
-- Sobre:
--
-- Esta função retorna uma lista de marcações agrupadas, filtradas por tipo, colaborador e dia.
--
-- Histórico:
-- 2019-08-01 -> Function alterada para inserir registros de IMEI (wvinim - PL-2152).
-- 2019-08-07 -> Function alterada para inserir registros de marca e modelo do dispositivo (wvinim - PL-2152)
CREATE OR REPLACE FUNCTION FUNC_MARCACAO_GET_MARCACOES_ACOMPANHAMENTO(
  F_COD_INICIO BIGINT,
  F_COD_FIM    BIGINT,
  F_TZ_UNIDADE TEXT)
  RETURNS TABLE(
    FONTE_DATA_HORA_INICIO                    TEXT,
    FONTE_DATA_HORA_FIM                       TEXT,
    JUSTIFICATIVA_ESTOURO                     TEXT,
    JUSTIFICATIVA_TEMPO_RECOMENDADO           TEXT,
    LATITUDE_MARCACAO_INICIO                  TEXT,
    LONGITUDE_MARCACAO_INICIO                 TEXT,
    LATITUDE_MARCACAO_FIM                     TEXT,
    LONGITUDE_MARCACAO_FIM                    TEXT,
    COD_UNIDADE                               BIGINT,
    CPF_COLABORADOR                           TEXT,
    NOME_COLABORADOR                          TEXT,
    NOME_TIPO_MARCACAO                        TEXT,
    DATA_HORA_INICIO                          TIMESTAMP WITHOUT TIME ZONE,
    DATA_HORA_FIM                             TIMESTAMP WITHOUT TIME ZONE,
    TEMPO_DECORRIDO_ENTRE_INICIO_FIM_SEGUNDOS BIGINT,
    TEMPO_RECOMENDADO_TIPO_MARCACAO_SEGUNDOS  BIGINT,
    COD_MARCACAO_INICIO                       BIGINT,
    COD_MARCACAO_FIM                          BIGINT,
    STATUS_ATIVO_INICIO                       BOOLEAN,
    STATUS_ATIVO_FIM                          BOOLEAN,
    FOI_AJUSTADO_INICIO                       BOOLEAN,
    FOI_AJUSTADO_FIM                          BOOLEAN,
    DATA_HORA_SINCRONIZACAO_INICIO            TIMESTAMP WITHOUT TIME ZONE,
    DATA_HORA_SINCRONIZACAO_FIM               TIMESTAMP WITHOUT TIME ZONE,
    VERSAO_APP_MOMENTO_MARCACAO_INICIO        INTEGER,
    VERSAO_APP_MOMENTO_MARCACAO_FIM           INTEGER,
    VERSAO_APP_MOMENTO_SINCRONIZACAO_INICIO   INTEGER,
    VERSAO_APP_MOMENTO_SINCRONIZACAO_FIM      INTEGER,
    TIPO_JORNADA                              BOOLEAN,
    DEVICE_IMEI_INICIO                        TEXT,
    DEVICE_IMEI_INICIO_RECONHECIDO            BOOLEAN,
    DEVICE_MARCA_INICIO                       TEXT,
    DEVICE_MODELO_INICIO                      TEXT,
    DEVICE_IMEI_FIM                           TEXT,
    DEVICE_IMEI_FIM_RECONHECIDO               BOOLEAN,
    DEVICE_MARCA_FIM                          TEXT,
    DEVICE_MODELO_FIM                         TEXT)
LANGUAGE PLPGSQL
AS $$
BEGIN
  -- Se os dois códigos foram fornecidos, garantimos que eles são do mesmo colaborador e também que são marcações
  -- vinculadas.
  IF F_COD_INICIO IS NOT NULL AND F_COD_FIM IS NOT NULL
  THEN
    IF (SELECT I.CPF_COLABORADOR
        FROM INTERVALO I
        WHERE I.CODIGO = F_COD_INICIO) <> (SELECT I.CPF_COLABORADOR
                                           FROM INTERVALO I
                                           WHERE I.CODIGO = F_COD_FIM)
    THEN RAISE EXCEPTION 'As marcações de início e fim buscadas não pertencem ao mesmo colaborador';
    END IF;
    IF (SELECT NOT EXISTS(SELECT MV.CODIGO
                          FROM MARCACAO_VINCULO_INICIO_FIM MV
                          WHERE MV.COD_MARCACAO_INICIO = F_COD_INICIO AND MV.COD_MARCACAO_FIM = F_COD_FIM))
    THEN RAISE EXCEPTION 'As marcações de início e fim buscadas não estão vinculadas';
    END IF;
  END IF;

  RETURN QUERY
  WITH INICIOS AS (
      SELECT MI.COD_MARCACAO_INICIO             AS COD_MARCACAO_INICIO,
             MV.COD_MARCACAO_FIM                AS COD_MARCACAO_VINCULO,
             I.FONTE_DATA_HORA                  AS FONTE_DATA_HORA_INICIO,
             I.LATITUDE_MARCACAO                AS LATITUDE_MARCACAO_INICIO,
             I.LONGITUDE_MARCACAO               AS LONGITUDE_MARCACAO_INICIO,
             I.COD_UNIDADE                      AS COD_UNIDADE,
             I.CPF_COLABORADOR                  AS CPF_COLABORADOR,
             I.DATA_HORA                        AS DATA_HORA_INICIO,
             I.CODIGO                           AS CODIGO_INICIO,
             I.STATUS_ATIVO                     AS STATUS_ATIVO_INICIO,
             I.FOI_AJUSTADO                     AS FOI_AJUSTADO_INICIO,
             I.DATA_HORA_SINCRONIZACAO          AS DATA_HORA_SINCRONIZACAO_INICIO,
             I.VERSAO_APP_MOMENTO_MARCACAO      AS VERSAO_APP_MOMENTO_MARCACAO_INICIO,
             I.VERSAO_APP_MOMENTO_SINCRONIZACAO AS VERSAO_APP_MOMENTO_SINCRONIZACAO_INICIO,
             I.DEVICE_IMEI                      AS DEVICE_IMEI,
             DMI.IMEI IS NOT NULL               AS DEVICE_RECONHECIDO,
             I.MARCA_DEVICE                     AS DEVICE_MARCA,
             I.MODELO_DEVICE                    AS DEVICE_MODELO,
             VIT.NOME                           AS NOME_TIPO_MARCACAO,
             VIT.TEMPO_RECOMENDADO_MINUTOS      AS TEMPO_RECOMENDADO_MINUTOS,
             VIT.TIPO_JORNADA                   AS TIPO_JORNADA
      FROM MARCACAO_INICIO MI
        LEFT JOIN MARCACAO_VINCULO_INICIO_FIM MV
          ON MI.COD_MARCACAO_INICIO = MV.COD_MARCACAO_INICIO
        JOIN INTERVALO I
          ON MI.COD_MARCACAO_INICIO = I.CODIGO
        JOIN UNIDADE UNI
          ON UNI.CODIGO = I.COD_UNIDADE
        LEFT JOIN DISPOSITIVO_MOVEL_IMEI DMI
            ON DMI.COD_EMPRESA = UNI.COD_EMPRESA AND DMI.IMEI = I.DEVICE_IMEI
        JOIN VIEW_INTERVALO_TIPO VIT
          ON I.COD_TIPO_INTERVALO = VIT.CODIGO
      WHERE F_IF(F_COD_INICIO IS NULL, I.CODIGO IS NULL, I.CODIGO = F_COD_INICIO)
  ),

      FINS AS (
        SELECT
          MF.COD_MARCACAO_FIM                AS COD_MARCACAO_FIM,
          MV.COD_MARCACAO_INICIO             AS COD_MARCACAO_VINCULO,
          F.FONTE_DATA_HORA                  AS FONTE_DATA_HORA_FIM,
          F.JUSTIFICATIVA_ESTOURO            AS JUSTIFICATIVA_ESTOURO,
          F.JUSTIFICATIVA_TEMPO_RECOMENDADO  AS JUSTIFICATIVA_TEMPO_RECOMENDADO,
          F.LATITUDE_MARCACAO                AS LATITUDE_MARCACAO_FIM,
          F.LONGITUDE_MARCACAO               AS LONGITUDE_MARCACAO_FIM,
          F.COD_UNIDADE                      AS COD_UNIDADE,
          F.CPF_COLABORADOR                  AS CPF_COLABORADOR,
          F.DATA_HORA                        AS DATA_HORA_FIM,
          F.CODIGO                           AS CODIGO_FIM,
          F.STATUS_ATIVO                     AS STATUS_ATIVO_FIM,
          F.FOI_AJUSTADO                     AS FOI_AJUSTADO_FIM,
          F.DATA_HORA_SINCRONIZACAO          AS DATA_HORA_SINCRONIZACAO_FIM,
          F.VERSAO_APP_MOMENTO_MARCACAO      AS VERSAO_APP_MOMENTO_MARCACAO_FIM,
          F.VERSAO_APP_MOMENTO_SINCRONIZACAO AS VERSAO_APP_MOMENTO_SINCRONIZACAO_FIM,
          F.DEVICE_IMEI                      AS DEVICE_IMEI,
          DMI.IMEI IS NOT NULL               AS DEVICE_RECONHECIDO,
          F.MARCA_DEVICE                     AS DEVICE_MARCA,
          F.MODELO_DEVICE                    AS DEVICE_MODELO,
          VIT.NOME                           AS NOME_TIPO_MARCACAO,
          VIT.TEMPO_RECOMENDADO_MINUTOS      AS TEMPO_RECOMENDADO_MINUTOS,
          VIT.TIPO_JORNADA                   AS TIPO_JORNADA
        FROM MARCACAO_FIM MF
          LEFT JOIN MARCACAO_VINCULO_INICIO_FIM MV
            ON MF.COD_MARCACAO_FIM = MV.COD_MARCACAO_FIM
          JOIN INTERVALO F
            ON MF.COD_MARCACAO_FIM = F.CODIGO
          JOIN UNIDADE UNI
            ON UNI.CODIGO = F.COD_UNIDADE
          LEFT JOIN DISPOSITIVO_MOVEL_IMEI DMI
            ON DMI.COD_EMPRESA = UNI.COD_EMPRESA AND DMI.IMEI = F.DEVICE_IMEI
          JOIN VIEW_INTERVALO_TIPO VIT
            ON F.COD_TIPO_INTERVALO = VIT.CODIGO
        WHERE F_IF(F_COD_FIM IS NULL, F.CODIGO IS NULL, F.CODIGO = F_COD_FIM)
    )

  -- Por algum outro erro que não seja códigos de início e fim de colaboradores diferentes e marcações não vinculadas,
  -- a function poderia também acabar retornando mais de uma linha, preferimos não utilizar limit aqui e deixar esse
  -- erro subir para o servidor tratar.
  SELECT I.FONTE_DATA_HORA_INICIO :: TEXT                                      AS FONTE_DATA_HORA_INICIO,
         F.FONTE_DATA_HORA_FIM :: TEXT                                         AS FONTE_DATA_HORA_FIM,
         F.JUSTIFICATIVA_ESTOURO :: TEXT                                       AS JUSTIFICATIVA_ESTOURO,
         F.JUSTIFICATIVA_TEMPO_RECOMENDADO :: TEXT                             AS JUSTIFICATIVA_TEMPO_RECOMENDADO,
         I.LATITUDE_MARCACAO_INICIO :: TEXT                                    AS LATITUDE_MARCACAO_INICIO,
         I.LONGITUDE_MARCACAO_INICIO :: TEXT                                   AS LONGITUDE_MARCACAO_INICIO,
         F.LATITUDE_MARCACAO_FIM :: TEXT                                       AS LATITUDE_MARCACAO_FIM,
         F.LONGITUDE_MARCACAO_FIM :: TEXT                                      AS LONGITUDE_MARCACAO_FIM,
         COALESCE(I.COD_UNIDADE, F.COD_UNIDADE)                                AS COD_UNIDADE,
         LPAD(COALESCE(I.CPF_COLABORADOR, F.CPF_COLABORADOR) :: TEXT, 11, '0') AS CPF_COLABORADOR,
         INITCAP(C.NOME) :: TEXT                                               AS NOME_COLABORADOR,
         COALESCE(I.NOME_TIPO_MARCACAO, F.NOME_TIPO_MARCACAO) :: TEXT          AS NOME_TIPO_MARCACAO,
         I.DATA_HORA_INICIO AT TIME ZONE F_TZ_UNIDADE                          AS DATA_HORA_INICIO,
         F.DATA_HORA_FIM AT TIME ZONE F_TZ_UNIDADE                             AS DATA_HORA_FIM,
         TO_SECONDS(F.DATA_HORA_FIM - I.DATA_HORA_INICIO)                      AS TEMPO_DECORRIDO_ENTRE_INICIO_FIM_SEGUNDOS,
         COALESCE(I.TEMPO_RECOMENDADO_MINUTOS,
                  F.TEMPO_RECOMENDADO_MINUTOS) *
         60                                                                    AS TEMPO_RECOMENDADO_TIPO_MARCACAO_SEGUNDOS,
         I.CODIGO_INICIO                                                       AS CODIGO_INICIO,
         F.CODIGO_FIM                                                          AS CODIGO_FIM,
         I.STATUS_ATIVO_INICIO                                                 AS STATUS_ATIVO_INICIO,
         F.STATUS_ATIVO_FIM                                                    AS STATUS_ATIVO_FIM,
         I.FOI_AJUSTADO_INICIO                                                 AS FOI_AJUSTADO_INICIO,
         F.FOI_AJUSTADO_FIM                                                    AS FOI_AJUSTADO_FIM,
         I.DATA_HORA_SINCRONIZACAO_INICIO AT TIME ZONE F_TZ_UNIDADE            AS DATA_HORA_SINCRONIZACAO_INICIO,
         F.DATA_HORA_SINCRONIZACAO_FIM AT TIME ZONE F_TZ_UNIDADE               AS DATA_HORA_SINCRONIZACAO_FIM,
         I.VERSAO_APP_MOMENTO_MARCACAO_INICIO                                  AS VERSAO_APP_MOMENTO_MARCACAO_INICIO,
         F.VERSAO_APP_MOMENTO_MARCACAO_FIM                                     AS VERSAO_APP_MOMENTO_MARCACAO_FIM,
         I.VERSAO_APP_MOMENTO_SINCRONIZACAO_INICIO                             AS VERSAO_APP_MOMENTO_SINCRONIZACAO_INICIO,
         F.VERSAO_APP_MOMENTO_SINCRONIZACAO_FIM                                AS VERSAO_APP_MOMENTO_SINCRONIZACAO_FIM,
         (F.TIPO_JORNADA OR I.TIPO_JORNADA)                                    AS TIPO_JORNADA,
         I.DEVICE_IMEI :: TEXT                                                 AS DEVICE_IMEI_INICIO,
         I.DEVICE_RECONHECIDO :: BOOLEAN                                       AS DEVICE_IMEI_INICIO_RECONHECIDO,
         I.DEVICE_MARCA :: TEXT                                                AS DEVICE_MARCA_INICIO,
         I.DEVICE_MODELO :: TEXT                                               AS DEVICE_MODELO_INICIO,
         F.DEVICE_IMEI :: TEXT                                                 AS DEVICE_IMEI_FIM,
         F.DEVICE_RECONHECIDO :: BOOLEAN                                       AS DEVICE_IMEI_FIM_RECONHECIDO,
         F.DEVICE_MARCA :: TEXT                                                AS DEVICE_MARCA_FIM,
         F.DEVICE_MODELO :: TEXT                                               AS DEVICE_MODELO_FIM
  FROM INICIOS I
    FULL OUTER JOIN FINS F
      ON I.COD_MARCACAO_VINCULO = F.COD_MARCACAO_FIM
    JOIN COLABORADOR C
      ON C.CPF = COALESCE(I.CPF_COLABORADOR, F.CPF_COLABORADOR);
END;
$$;

-- Recria a função que retorna o relatório de marcações diárias
CREATE OR REPLACE FUNCTION FUNC_MARCACAO_RELATORIO_MARCACOES_DIARIAS(F_COD_UNIDADE BIGINT,
                                                                     F_DATA_INICIAL DATE,
                                                                     F_DATA_FINAL DATE,
                                                                     F_CPF TEXT)
    RETURNS TABLE
            (
                "NOME"                                         TEXT,
                "MATRÍCULA TRANSPORTADORA"                     TEXT,
                "MATRÍCULA AMBEV"                              TEXT,
                "CARGO"                                        TEXT,
                "SETOR"                                        TEXT,
                "INTERVALO"                                    TEXT,
                "INICIO INTERVALO"                             TEXT,
                "LATITUDE INÍCIO"                              TEXT,
                "LONGITUDE INÍCIO"                             TEXT,
                "FONTE DATA/HORA INÍCIO"                       TEXT,
                "DATA/HORA SINCRONIZAÇÃO INÍCIO"               TEXT,
                "FIM INTERVALO"                                TEXT,
                "LATITUDE FIM"                                 TEXT,
                "LONGITUDE FIM"                                TEXT,
                "FONTE DATA/HORA FIM"                          TEXT,
                "DATA/HORA SINCRONIZAÇÃO FIM"                  TEXT,
                "TEMPO DECORRIDO (MINUTOS)"                    TEXT,
                "TEMPO RECOMENDADO (MINUTOS)"                  BIGINT,
                "CUMPRIU TEMPO MÍNIMO"                         TEXT,
                "JUSTIFICATIVA NÃO CUMPRIMENTO TEMPO MÍNIMO"   TEXT,
                "JUSTIFICATIVA ESTOURO TEMPO MÁXIMO PERMITIDO" TEXT,
                "DISTANCIA ENTRE INÍCIO E FIM (METROS)"        TEXT,
                "DEVICE IMEI INÍCIO"                           TEXT,
                "DEVICE IMEI INÍCIO RECONHECIDO"               TEXT,
                "DEVICE IMEI FIM"                              TEXT,
                "DEVICE IMEI FIM RECONHECIDO"                  TEXT
            )
    LANGUAGE SQL
AS
$$
SELECT C.NOME                                                                  AS NOME_COLABORADOR,
       COALESCE(C.MATRICULA_TRANS :: TEXT, '-')                                AS MATRICULA_TRANS,
       COALESCE(C.MATRICULA_AMBEV :: TEXT, '-')                                AS MATRICULA_AMBEV,
       F.NOME                                                                  AS CARGO,
       S.NOME                                                                  AS SETOR,
       IT.NOME                                                                 AS INTERVALO,
       COALESCE(
               TO_CHAR(I.DATA_HORA_INICIO AT TIME ZONE TZ_UNIDADE(F_COD_UNIDADE),
                       'DD/MM/YYYY HH24:MI:SS'),
               '')                                                             AS DATA_HORA_INICIO,
       I.LATITUDE_MARCACAO_INICIO :: TEXT                                      AS LATITUDE_INICIO,
       I.LONGITUDE_MARCACAO_INICIO :: TEXT                                     AS LONGITUDE_INICIO,
       I.FONTE_DATA_HORA_INICIO,
       COALESCE(TO_CHAR(I.DATA_HORA_SINCRONIZACAO_INICIO AT TIME ZONE TZ_UNIDADE(F_COD_UNIDADE),
                        'DD/MM/YYYY HH24:MI:SS'),
                ''),
       COALESCE(TO_CHAR(I.DATA_HORA_FIM AT TIME ZONE TZ_UNIDADE(F_COD_UNIDADE),
                        'DD/MM/YYYY HH24:MI:SS'),
                '')                                                            AS DATA_HORA_FIM,
       I.LATITUDE_MARCACAO_FIM :: TEXT                                         AS LATITUDE_FIM,
       I.LONGITUDE_MARCACAO_FIM :: TEXT                                        AS LONGITUDE_FIM,
       I.FONTE_DATA_HORA_FIM,
       COALESCE(TO_CHAR(I.DATA_HORA_SINCRONIZACAO_FIM AT TIME ZONE TZ_UNIDADE(F_COD_UNIDADE),
                        'DD/MM/YYYY HH24:MI:SS'),
                ''),
       COALESCE(TRUNC(EXTRACT(EPOCH FROM I.DATA_HORA_FIM - I.DATA_HORA_INICIO) / 60) :: TEXT,
                '')                                                            AS TEMPO_DECORRIDO_MINUTOS,
       IT.TEMPO_RECOMENDADO_MINUTOS,
       CASE
           WHEN I.DATA_HORA_FIM IS NULL OR I.DATA_HORA_INICIO IS NULL
               THEN ''
           WHEN IT.TEMPO_RECOMENDADO_MINUTOS > (EXTRACT(EPOCH FROM I.DATA_HORA_FIM - I.DATA_HORA_INICIO) / 60)
               THEN 'NÃO'
           ELSE 'SIM' END                                                      AS CUMPRIU_TEMPO_MINIMO,
       I.JUSTIFICATIVA_TEMPO_RECOMENDADO,
       I.JUSTIFICATIVA_ESTOURO,
       COALESCE(TRUNC((ST_DISTANCE(
               ST_POINT(I.LONGITUDE_MARCACAO_INICIO :: FLOAT,
                        I.LATITUDE_MARCACAO_INICIO :: FLOAT) :: GEOGRAPHY,
               ST_POINT(I.LONGITUDE_MARCACAO_FIM :: FLOAT,
                        I.LATITUDE_MARCACAO_FIM :: FLOAT) :: GEOGRAPHY))) :: TEXT,
                '-')                                                           AS DISTANCIA,
       COALESCE(I.DEVICE_IMEI_INICIO :: TEXT, '-')                             AS DEVICE_IMEI_INICIO,
       F_IF(
               I.DEVICE_IMEI_INICIO IS NOT NULL,
               F_IF(I.DEVICE_IMEI_INICIO_RECONHECIDO, 'SIM', 'NÃO' :: TEXT),
               '-' :: TEXT)                                                    AS DEVICE_IMEI_INICIO_RECONHECIDO,
       COALESCE(I.DEVICE_IMEI_FIM :: TEXT, '-')                                AS DEVICE_IMEI_FIM,
       F_IF(
               I.DEVICE_IMEI_FIM IS NOT NULL,
               F_IF(I.DEVICE_IMEI_FIM_RECONHECIDO, 'SIM', 'NÃO' :: TEXT), '-') AS DEVICE_IMEI_FIM_RECONHECIDO
FROM FUNC_INTERVALOS_AGRUPADOS(F_COD_UNIDADE, CASE
                                                  WHEN F_CPF = '%'
                                                      THEN NULL
                                                  ELSE F_CPF :: BIGINT END, NULL) I
         JOIN COLABORADOR C ON C.CPF = I.CPF_COLABORADOR
         JOIN INTERVALO_TIPO IT ON IT.COD_UNIDADE = I.COD_UNIDADE AND IT.CODIGO = I.COD_TIPO_INTERVALO
         JOIN UNIDADE U ON U.CODIGO = C.COD_UNIDADE AND C.COD_EMPRESA = U.COD_EMPRESA
         JOIN FUNCAO F ON F.COD_EMPRESA = U.COD_EMPRESA AND F.CODIGO = C.COD_FUNCAO
         JOIN SETOR S ON S.COD_UNIDADE = C.COD_UNIDADE AND S.CODIGO = C.COD_SETOR
WHERE ((I.DATA_HORA_INICIO AT TIME ZONE (SELECT TIMEZONE FROM FUNC_GET_TIME_ZONE_UNIDADE(F_COD_UNIDADE))) :: DATE
           BETWEEN F_DATA_INICIAL
           AND F_DATA_FINAL
    OR (I.DATA_HORA_FIM AT TIME ZONE (SELECT TIMEZONE FROM FUNC_GET_TIME_ZONE_UNIDADE(F_COD_UNIDADE))) :: DATE
           BETWEEN F_DATA_INICIAL
           AND F_DATA_FINAL)
ORDER BY I.DATA_HORA_INICIO, C.NOME
$$;

-- Recria a função que retorna o relatório de mapas e intervalos realizados pelos seus colaboradores
CREATE OR REPLACE FUNCTION FUNC_RELATORIO_INTERVALOS_MAPAS(F_COD_UNIDADE BIGINT, F_DATA_INICIAL DATE, F_DATA_FINAL DATE)
  RETURNS TABLE(
    "DATA"                                 VARCHAR,
    "MAPA"                                 INT,
    "MOTORISTA"                            VARCHAR,
    "INICIO INTERVALO MOTORISTA"           VARCHAR,
    "FIM INTERVALO MOTORISTA"              VARCHAR,
    "MARCAÇÕES RECONHECIDAS MOT"           VARCHAR,
    "TEMPO DECORRIDO EM MINUTOS MOTORISTA" VARCHAR,
    "MOTORISTA CUMPRIU TEMPO MÍNIMO"       VARCHAR,
    "AJUDANTE 1"                           VARCHAR,
    "INICIO INTERVALO AJ 1"                VARCHAR,
    "FIM INTERVALO AJ 1"                   VARCHAR,
    "MARCAÇÕES RECONHECIDAS AJ 1"          VARCHAR,
    "TEMPO DECORRIDO EM MINUTOS AJ 1"      VARCHAR,
    "AJ 1 CUMPRIU TEMPO MÍNIMO"            VARCHAR,
    "AJ 2"                                 VARCHAR,
    "INICIO INTERVALO AJ 2"                VARCHAR,
    "FIM INTERVALO AJ 2"                   VARCHAR,
    "MARCAÇÕES RECONHECIDAS AJ 2"          VARCHAR,
    "TEMPO DECORRIDO EM MINUTOS AJ 2"      VARCHAR,
    "AJ 2 CUMPRIU TEMPO MÍNIMO"            VARCHAR
  ) AS
$func$
SELECT to_char(dados.data, 'DD/MM/YYYY'),
       dados.mapa,
       dados.NOME_MOTORISTA,
       dados.INICIO_INTERVALO_MOT,
       dados.FIM_INTERVALO_MOT,
       F_IF(dados.MARCACOES_RECONHECIDAS_MOT, 'SIM', 'NÃO' :: TEXT),
       dados.TEMPO_DECORRIDO_MINUTOS_MOT,
       dados.MOT_CUMPRIU_TEMPO_MINIMO,
       dados.NOME_aj1,
       dados.INICIO_INTERVALO_aj1,
       dados.FIM_INTERVALO_aj1,
       F_IF(dados.MARCACOES_RECONHECIDAS_AJ1, 'SIM', 'NÃO' :: TEXT),
       dados.TEMPO_DECORRIDO_MINUTOS_aj1,
       dados.aj1_CUMPRIU_TEMPO_MINIMO,
       dados.NOME_aj2,
       dados.INICIO_INTERVALO_aj2,
       dados.FIM_INTERVALO_aj2,
       F_IF(dados.MARCACOES_RECONHECIDAS_AJ2, 'SIM', 'NÃO' :: TEXT),
       dados.TEMPO_DECORRIDO_MINUTOS_aj2,
       dados.aj2_CUMPRIU_TEMPO_MINIMO
FROM view_extrato_mapas_versus_intervalos dados
WHERE dados.cod_unidade = f_cod_unidade
  AND dados.data :: date between f_data_inicial and f_data_final
ORDER BY dados.MAPA desc
$func$
LANGUAGE SQL;

-- Recria a função que retorna o relatório de marcações comparando com escala diária
CREATE OR REPLACE FUNCTION FUNC_RELATORIO_INTERVALO_ESCALA_DIARIA(
  f_cod_unidade        bigint,
  f_cod_tipo_intervalo bigint,
  f_data_inicial       date,
  f_data_final         date,
  f_time_zone_unidade  text)
  returns TABLE(
    "UNIDADE"                     text,
    "PLACA VEÍCULO"               text,
    "CÓDIGO ROTA (MAPA)"          bigint,
    "DATA"                        text,
    "TIPO DE INTERVALO"           text,
    "MOTORISTA"                   text,
    "INÍCIO INTERVALO MOTORISTA"  text,
    "FIM INTERVALO MOTORISTA"     text,
    "MARCAÇÕES RECONHECIDAS MOT"  text,
    "AJUDANTE 1"                  text,
    "INÍCIO INTERVALO AJUDANTE 1" text,
    "FIM INTERVALO AJUDANTE 1"    text,
    "MARCAÇÕES RECONHECIDAS AJ 1" text,
    "AJUDANTE 2"                  text,
    "INÍCIO INTERVALO AJUDANTE 2" text,
    "FIM INTERVALO AJUDANTE 2"    text,
    "MARCAÇÕES RECONHECIDAS AJ 2" text)
language sql
as $$
WITH TABLE_INTERVALOS AS (SELECT *
                          FROM FUNC_INTERVALOS_AGRUPADOS(f_cod_unidade, NULL, f_cod_tipo_intervalo) F
                          WHERE (COALESCE(F.data_hora_inicio, F.data_hora_fim) AT TIME ZONE
                                 f_time_zone_unidade) :: DATE >= f_data_inicial
                            AND (COALESCE(F.data_hora_inicio, F.data_hora_fim) AT TIME ZONE
                                 f_time_zone_unidade) :: DATE <= f_data_final)

SELECT (SELECT U.NOME FROM UNIDADE U WHERE U.CODIGO = f_cod_unidade),
       ED.PLACA,
       ED.MAPA,
       TO_CHAR(ED.DATA, 'DD/MM/YYYY'),
       (SELECT IT.NOME FROM INTERVALO_TIPO IT WHERE IT.CODIGO = f_cod_tipo_intervalo),
       -- MOTORISTA
       F_IF(CM.CPF IS NULL, 'MOTORISTA NÃO CADASTRADO', CM.NOME)    AS NOME_MOTORISTA,
       F_IF(INT_MOT.DATA_HORA_INICIO IS NOT NULL,
            TO_CHAR(INT_MOT.DATA_HORA_INICIO AT TIME ZONE f_time_zone_unidade, 'DD/MM/YYYY HH24:mi:ss'),
            'NÃO MARCADO'),
       F_IF(INT_MOT.DATA_HORA_FIM IS NOT NULL,
            TO_CHAR(INT_MOT.DATA_HORA_FIM AT TIME ZONE f_time_zone_unidade, 'DD/MM/YYYY HH24:mi:ss'),
            'NÃO MARCADO'),
       F_IF(
               F_IF(INT_MOT.DEVICE_IMEI_INICIO_RECONHECIDO AND INT_MOT.DEVICE_IMEI_FIM_RECONHECIDO, TRUE, FALSE),
               'SIM',
               'NÃO' :: TEXT)                                       AS MARCACOES_RECONHECIDAS_MOT,
       -- AJUDANTE 1
       F_IF(CA1.CPF IS NULL, 'AJUDANTE 1 NÃO CADASTRADO', CA1.NOME) AS NOME_AJUDANTE_1,
       F_IF(INT_AJ1.DATA_HORA_INICIO IS NOT NULL,
            TO_CHAR(INT_AJ1.DATA_HORA_INICIO AT TIME ZONE f_time_zone_unidade, 'DD/MM/YYYY HH24:mi:ss'),
            'NÃO MARCADO'),
       F_IF(INT_AJ1.DATA_HORA_FIM IS NOT NULL,
            TO_CHAR(INT_AJ1.DATA_HORA_FIM AT TIME ZONE f_time_zone_unidade, 'DD/MM/YYYY HH24:mi:ss'),
            'NÃO MARCADO'),
       F_IF(
               F_IF(INT_AJ1.DEVICE_IMEI_INICIO_RECONHECIDO AND INT_AJ1.DEVICE_IMEI_FIM_RECONHECIDO, TRUE, FALSE),
               'SIM',
               'NÃO' :: TEXT)                                       AS MARCACOES_RECONHECIDAS_AJ1,
       -- AJUDANTE 2
       F_IF(CA2.CPF IS NULL, 'AJUDANTE 1 NÃO CADASTRADO', CA2.NOME) AS NOME_AJUDANTE_2,
       F_IF(INT_AJ2.DATA_HORA_INICIO IS NOT NULL,
            TO_CHAR(INT_AJ2.DATA_HORA_INICIO AT TIME ZONE f_time_zone_unidade, 'DD/MM/YYYY HH24:mi:ss'),
            'NÃO MARCADO'),
       F_IF(INT_AJ2.DATA_HORA_FIM IS NOT NULL,
            TO_CHAR(INT_AJ2.DATA_HORA_FIM AT TIME ZONE f_time_zone_unidade, 'DD/MM/YYYY HH24:mi:ss'),
            'NÃO MARCADO'),
       F_IF(
               F_IF(INT_AJ2.DEVICE_IMEI_INICIO_RECONHECIDO AND INT_AJ2.DEVICE_IMEI_FIM_RECONHECIDO, TRUE, FALSE),
               'SIM',
               'NÃO' :: TEXT)                                       AS MARCACOES_RECONHECIDAS_AJ2
FROM ESCALA_DIARIA AS ED
       LEFT JOIN COLABORADOR AS CM ON CM.CPF = ED.CPF_MOTORISTA
       LEFT JOIN COLABORADOR AS CA1 ON CA1.CPF = ED.CPF_AJUDANTE_1
       LEFT JOIN COLABORADOR AS CA2 ON CA2.CPF = ED.CPF_AJUDANTE_2
       LEFT JOIN TABLE_INTERVALOS INT_MOT
         ON (COALESCE(INT_MOT.data_hora_inicio, INT_MOT.data_hora_fim) AT TIME ZONE f_time_zone_unidade) :: DATE =
            ED.data
              AND INT_MOT.cpf_colaborador = ED.cpf_motorista
       LEFT JOIN TABLE_INTERVALOS INT_AJ1
         ON (COALESCE(INT_AJ1.data_hora_inicio, INT_AJ1.data_hora_fim) AT TIME ZONE f_time_zone_unidade) :: DATE =
            ED.data
              AND INT_AJ1.cpf_colaborador = ED.cpf_ajudante_1
       LEFT JOIN TABLE_INTERVALOS INT_AJ2
         ON (COALESCE(INT_AJ2.data_hora_inicio, INT_AJ2.data_hora_fim) AT TIME ZONE f_time_zone_unidade) :: DATE =
            ED.data
              AND INT_AJ2.cpf_colaborador = ED.cpf_ajudante_2

WHERE (ED.DATA >= f_data_inicial AND ED.DATA <= f_data_final)
  AND ED.COD_UNIDADE = F_COD_UNIDADE;
$$;

-- Libera pilar 5 para unidades que tem cargos com acesso a gestão de macações.
with cargos_acesso as (
    SELECT cod_unidade, cod_funcao_colaborador
    FROM cargo_funcao_prolog_v11
    GROUP BY cod_unidade, cod_funcao_colaborador
    HAVING '{338}' <@ array_agg(cod_funcao_prolog)
    ORDER BY cod_unidade, cod_funcao_colaborador
)

INSERT
INTO UNIDADE_PILAR_PROLOG (COD_UNIDADE, COD_PILAR)
SELECT DISTINCT CA.COD_UNIDADE, 5
FROM CARGOS_ACESSO CA;

-- Libera permissão de gestão de dispositivos para quem tem acesso a gestão de marcações.
with cargos_acesso as (
    SELECT cod_unidade, cod_funcao_colaborador
    FROM cargo_funcao_prolog_v11
    GROUP BY cod_unidade, cod_funcao_colaborador
    HAVING '{338}' <@ array_agg(cod_funcao_prolog)
    ORDER BY cod_unidade, cod_funcao_colaborador
)

INSERT
INTO CARGO_FUNCAO_PROLOG_V11 (COD_UNIDADE, COD_FUNCAO_COLABORADOR, COD_FUNCAO_PROLOG, COD_PILAR_PROLOG)
SELECT CA.COD_UNIDADE, CA.COD_FUNCAO_COLABORADOR, 501, 5
FROM CARGOS_ACESSO CA;

--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################



--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
-- PL-2200
CREATE OR REPLACE FUNCTION FUNC_GARANTE_REGIONAL_EXISTE(F_COD_REGIONAL BIGINT)
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    IF NOT EXISTS(SELECT R.CODIGO
                  FROM REGIONAL R
                  WHERE R.CODIGO = F_COD_REGIONAL)
    THEN
        RAISE EXCEPTION 'Regional de código % não existe!', F_COD_REGIONAL;
    END IF;
END;
$$;

CREATE OR REPLACE FUNCTION FUNC_GARANTE_NOT_NULL(F_VALUE ANYELEMENT, F_FIELD_NAME TEXT)
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    IF (F_VALUE IS NULL)
    THEN
        RAISE EXCEPTION '% não pode ser nulo', F_FIELD_NAME;
    END IF;
END;
$$;

CREATE OR REPLACE FUNCTION FUNC_GARANTE_PILARES_VALIDOS(F_PILARES_VERIFICACAO INTEGER[])
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    PILARES_VALIDOS INTEGER[] := ARRAY [1, 2, 3, 4, 5];
BEGIN
    IF NOT (F_PILARES_VERIFICACAO <@ PILARES_VALIDOS)
    THEN
        RAISE EXCEPTION 'Apenas os pilares % são válidos', ARRAY_TO_STRING(PILARES_VALIDOS, ', ');
    END IF;
END;
$$;

CREATE OR REPLACE FUNCTION IS_TIMEZONE(TZ TEXT) RETURNS BOOLEAN AS
$$
DECLARE
    DATE TIMESTAMPTZ;
BEGIN
    DATE := NOW() AT TIME ZONE TZ;
    RETURN TRUE;
EXCEPTION
    WHEN OTHERS THEN
        RETURN FALSE;
END;
$$ LANGUAGE PLPGSQL STABLE;

CREATE OR REPLACE FUNCTION ARRAY_DISTINCT(ANYARRAY, BOOLEAN DEFAULT FALSE) RETURNS ANYARRAY
    IMMUTABLE
    LANGUAGE SQL
AS
$$
SELECT ARRAY_AGG(DISTINCT X)
FROM UNNEST($1) T(X)
WHERE CASE WHEN $2 THEN X IS NOT NULL ELSE TRUE END;
$$;

CREATE OR REPLACE FUNCTION FUNC_UNIDADE_CADASTRA_UNIDADE(F_COD_EMPRESA BIGINT,
                                                         F_COD_REGIONAL BIGINT,
                                                         F_NOME_UNIDADE TEXT,
                                                         F_TIMEZONE TEXT,
                                                         F_PILARES_LIBERADOS INTEGER[],
                                                         OUT AVISO_UNIDADE_CADASTRADA TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    QTD_LINHAS_CADASTRADAS BIGINT;
    COD_UNIDADE_CADASTRADA BIGINT;
    MAX_LENGTH_COLUMN      INTEGER   := 40;
    COD_PILAR_GENTE        INTEGER   := 3;
BEGIN
    PERFORM FUNC_GARANTE_EMPRESA_EXISTE(F_COD_EMPRESA);
    PERFORM FUNC_GARANTE_REGIONAL_EXISTE(F_COD_REGIONAL);
    PERFORM FUNC_GARANTE_NOT_NULL(F_NOME_UNIDADE, 'Nome Unidade');
    PERFORM FUNC_GARANTE_NOT_NULL(F_TIMEZONE, 'Timezone');
    PERFORM FUNC_GARANTE_NOT_NULL(F_PILARES_LIBERADOS, 'Pilares Liberados');
    PERFORM FUNC_GARANTE_PILARES_VALIDOS(F_PILARES_LIBERADOS);

    -- Todas as unidades devem ter o pilar GENTE.
    SELECT ARRAY_APPEND(F_PILARES_LIBERADOS, COD_PILAR_GENTE) INTO F_PILARES_LIBERADOS;
    -- Após adicionar o pilar GENTE, removemos do array qualquer valor duplicado.
    SELECT ARRAY_DISTINCT(F_PILARES_LIBERADOS) INTO F_PILARES_LIBERADOS;

    -- Garante que nome unidade não tenha mais do que 40 caracteres.
    IF (LENGTH(TRIM_AND_REMOVE_EXTRA_SPACES(F_NOME_UNIDADE)) > MAX_LENGTH_COLUMN)
    THEN
        RAISE EXCEPTION 'O nome da unidade não pode ter mais do que % caracteres', MAX_LENGTH_COLUMN;
    END IF;

    -- Garante que unidade com mesmo nome não exista para a mesma empresa.
    IF (LOWER(UNACCENT(TRIM_AND_REMOVE_EXTRA_SPACES(F_NOME_UNIDADE))) IN
        (SELECT LOWER(UNACCENT(TRIM_AND_REMOVE_EXTRA_SPACES(U.NOME)))
         FROM UNIDADE U
         WHERE U.COD_EMPRESA = F_COD_EMPRESA))
    THEN
        RAISE EXCEPTION 'Já existe uma unidade com nome % cadastrada para a empresa %',
            TRIM_AND_REMOVE_EXTRA_SPACES(F_NOME_UNIDADE),
            F_COD_EMPRESA;
    END IF;

    -- Garante que o timezone informado exista.
    IF (NOT IS_TIMEZONE(F_TIMEZONE))
    THEN
        RAISE EXCEPTION '% não é um timezone válido', F_TIMEZONE;
    END IF;

    -- Insere a unidade.
    INSERT INTO UNIDADE (NOME, TIMEZONE, COD_REGIONAL, COD_EMPRESA)
    VALUES (F_NOME_UNIDADE, F_TIMEZONE, F_COD_REGIONAL, F_COD_EMPRESA) RETURNING CODIGO INTO COD_UNIDADE_CADASTRADA;

    -- Verifica se insert de unidade funcionou.
    IF (COD_UNIDADE_CADASTRADA <= 0)
    THEN
        RAISE EXCEPTION 'Erro ao cadastrar unidade de nome: %', F_NOME_UNIDADE;
    END IF;

    -- Insere os pilares.
    INSERT INTO UNIDADE_PILAR_PROLOG (COD_UNIDADE, COD_PILAR)
    SELECT COD_UNIDADE_CADASTRADA,
           UNNEST(F_PILARES_LIBERADOS);

    GET DIAGNOSTICS QTD_LINHAS_CADASTRADAS = ROW_COUNT;

    -- Verifica se insert de pilares funcionou.
    IF (QTD_LINHAS_CADASTRADAS <= 0)
    THEN
        RAISE EXCEPTION 'Erro ao liberar pilares para a unidade de nome: %', F_NOME_UNIDADE;
    END IF;

    SELECT 'UNIDADE CADASTRADA: '
               || F_NOME_UNIDADE
               || ', CÓDIGO UNIDADE: '
               || COD_UNIDADE_CADASTRADA
               || ', E PILARES: '
               || ARRAY_TO_STRING(F_PILARES_LIBERADOS, ', ')
    INTO AVISO_UNIDADE_CADASTRADA;
END ;
$$;
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
--######################################################################################################################
-- Essa migration já foi executada
-- -- CRIA CHAVE DO SISTEMA PARA INTEGRAÇÃO DO PROTHEUS - RODALOG
-- INSERT INTO INTEGRACAO(COD_EMPRESA, CHAVE_SISTEMA, RECURSO_INTEGRADO) VALUES (10, 'PROTHEUS_RODALOG', 'AFERICAO');
-- -- CRIA TOKEN PARA A EMPRESA RODALOG
-- INSERT INTO INTEGRACAO.TOKEN_INTEGRACAO VALUES (10, 'tk33g4sbev1vi5l53okcugdsuk0q8lgtu8l14knuroqju9orob2');

END TRANSACTION ;