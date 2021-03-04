BEGIN TRANSACTION;
-- Sobre:
--
-- Function utilizada pela integração da Piccolotur para resolver itens pendentes de uma Ordem de Serviço no ProLog.
--
-- Essa function recebe os dados de um item resolvido e salva no banco de dados. Caso a O.S cujo item foi resolvido
-- não possuir mais nenhum item pendente, ela também é fechada por esta function.
--
-- Se a function receber itens que já encontram-se resolvidos, irá retornar sucesso e não mais erro como era a
-- abordagem anterior.
--
-- Precondições:
-- 1) O código do item sendo resolvido deve estar contido na tabela de itens enviaos, ou seja, só é possível fechar
-- pela integração um item que foi aberto pela integração.
-- 2) A function assume 1 como um código de retorno indicando sucesso da operação.
--
-- Histórico:
-- 2019-08-07 -> Function criada (diogenesvanzella - PL-2021).
-- 2019-10-07 -> Retorna sucesso caso item já esteja resolvido (diogenesvanzella - PLI-36).
CREATE OR REPLACE FUNCTION PICCOLOTUR.FUNC_CHECK_OS_RESOLVE_ITEM_PENDENTE(
  F_COD_UNIDADE_ITEM_OS           BIGINT,
  F_COD_OS_GLOBUS                 BIGINT,
  F_COD_ITEM_RESOLVIDO_GLOBUS     BIGINT,
  F_CPF_COLABORADOR_RESOLUCAO     BIGINT,
  F_PLACA_VEICULO_ITEM_OS         TEXT,
  F_KM_COLETADO_RESOLUCAO         BIGINT,
  F_DURACAO_RESOLUCAO_MS          BIGINT,
  F_FEEDBACK_RESOLUCAO            TEXT,
  F_DATA_HORA_RESOLVIDO_PROLOG    TIMESTAMP WITH TIME ZONE,
  F_DATA_HORA_INICIO_RESOLUCAO    TIMESTAMP WITH TIME ZONE,
  F_DATA_HORA_FIM_RESOLUCAO       TIMESTAMP WITH TIME ZONE,
  F_TOKEN_INTEGRACAO              TEXT,
  F_DATA_HORA_SINCRONIA_RESOLUCAO TIMESTAMP WITH TIME ZONE)
  RETURNS BIGINT
LANGUAGE PLPGSQL
AS $$
DECLARE
  STATUS_ITEM_OS_PENDENTE       TEXT := 'P';
  COD_ITEM_RESOLVIDO_PROLOG     BIGINT;
  COD_RETORNO_SUCESSO           BIGINT := 1;
  F_QTD_ROWS_ITENS_OS           BIGINT;
  F_QTD_ROWS_VINCULOS_ALTERADOS BIGINT;
BEGIN
  -- Antes de processarmos a resolução de Itens de O.S., validamos todos os códigos de vínculo possíveis.
  -- Por segurança, verificamos se a integração está fechando os itens de o.s. que pertencem a empresa correta.
  IF (F_COD_UNIDADE_ITEM_OS NOT IN (SELECT CODIGO
                                    FROM PUBLIC.UNIDADE
                                    WHERE COD_EMPRESA = (SELECT TI.COD_EMPRESA
                                                         FROM INTEGRACAO.TOKEN_INTEGRACAO TI
                                                         WHERE TI.TOKEN_INTEGRACAO = F_TOKEN_INTEGRACAO)))
  THEN
    PERFORM PUBLIC.THROW_GENERIC_ERROR('Você está tentando fechar um item de uma O.S. que não pertence à sua empresa');
  END IF;

  -- Validamos se o código do item fechado no Globus, está mapeado no ProLog.
  IF (SELECT EXISTS(SELECT COD_ITEM_OS_PROLOG
                    FROM PICCOLOTUR.CHECKLIST_ORDEM_SERVICO_ITEM_VINCULO
                    WHERE COD_ITEM_OS_GLOBUS = F_COD_ITEM_RESOLVIDO_GLOBUS))
  THEN SELECT COD_ITEM_OS_PROLOG
       FROM PICCOLOTUR.CHECKLIST_ORDEM_SERVICO_ITEM_VINCULO
       WHERE COD_OS_GLOBUS = F_COD_OS_GLOBUS
             AND COD_ITEM_OS_GLOBUS = F_COD_ITEM_RESOLVIDO_GLOBUS INTO COD_ITEM_RESOLVIDO_PROLOG;
  ELSE
    PERFORM PUBLIC.THROW_GENERIC_ERROR(
        FORMAT('[ERRO DE VÍNCULO] O item "%s" da O.S. "%s" não possuí vínculo no ProLog',
               F_COD_ITEM_RESOLVIDO_GLOBUS,
               F_COD_OS_GLOBUS));
  END IF;

  -- Validamos se o item mapeado está pendente no ProLog.
  IF (SELECT COSID.STATUS_RESOLUCAO
      FROM PICCOLOTUR.CHECKLIST_ORDEM_SERVICO_ITEM_VINCULO COSIV
        JOIN CHECKLIST_ORDEM_SERVICO_ITENS_DATA COSID
          ON COSIV.COD_ITEM_OS_PROLOG = COSID.CODIGO
      WHERE COD_OS_GLOBUS = F_COD_OS_GLOBUS
            AND COD_ITEM_OS_GLOBUS = F_COD_ITEM_RESOLVIDO_GLOBUS) != STATUS_ITEM_OS_PENDENTE
  THEN
    RETURN COD_RETORNO_SUCESSO;
  END IF;

  -- Validamos se o usuário está na base de dados do ProLog, em qualquer unidade da empresa integrada.
  IF (SELECT NOT EXISTS(SELECT C.CODIGO
                        FROM PUBLIC.COLABORADOR C
                        WHERE C.CPF = F_CPF_COLABORADOR_RESOLUCAO
                              AND C.COD_EMPRESA = (SELECT U.COD_EMPRESA
                                                   FROM PUBLIC.UNIDADE U WHERE U.CODIGO = F_COD_UNIDADE_ITEM_OS)))
  THEN
    PERFORM
      PUBLIC.THROW_GENERIC_ERROR(FORMAT('O CPF "%s" não encontra-se cadastrado no ProLog',
                                        PUBLIC.FORMAT_CPF(F_CPF_COLABORADOR_RESOLUCAO)));
  END IF;

  -- Validamos se a placa é a mesma do item pendente mapeado no ProLog.
  IF ((SELECT PLACA_VEICULO_OS
       FROM PICCOLOTUR.CHECKLIST_ORDEM_SERVICO_ITEM_VINCULO
       WHERE COD_OS_GLOBUS = F_COD_OS_GLOBUS
             AND COD_ITEM_OS_GLOBUS = F_COD_ITEM_RESOLVIDO_GLOBUS) != F_PLACA_VEICULO_ITEM_OS)
  THEN
    PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('A placa "%s" não bate com a placa do item pendente "%s" do ProLog',
                                              F_PLACA_VEICULO_ITEM_OS,
                                              COD_ITEM_RESOLVIDO_PROLOG));
  END IF;

  -- Depois de validar podemos resolver o item.
  UPDATE CHECKLIST_ORDEM_SERVICO_ITENS_DATA SET
    CPF_MECANICO = F_CPF_COLABORADOR_RESOLUCAO,
    KM = F_KM_COLETADO_RESOLUCAO,
    STATUS_RESOLUCAO = 'R',
    TEMPO_REALIZACAO = F_DURACAO_RESOLUCAO_MS,
    DATA_HORA_CONSERTO = F_DATA_HORA_RESOLVIDO_PROLOG,
    FEEDBACK_CONSERTO = F_FEEDBACK_RESOLUCAO,
    DATA_HORA_INICIO_RESOLUCAO = F_DATA_HORA_INICIO_RESOLUCAO,
    DATA_HORA_FIM_RESOLUCAO = F_DATA_HORA_FIM_RESOLUCAO
  WHERE CODIGO = COD_ITEM_RESOLVIDO_PROLOG;

  -- O ATRIBUTO 'ROW_COUNT' CONTERÁ A QUANTIDADE DE LINHAS QUE FORAM ATUALIZADAS PELO UPDATE ACIMA. A FUNCITON
  -- IRÁ RETORNAR ESSE ATRIBUTO PARA QUE POSSAMOS VALIDAR SE TODOS OS UPDATES ACONTECERAM COMO DEVERIAM.
  GET DIAGNOSTICS F_QTD_ROWS_ITENS_OS = ROW_COUNT;

  -- SE APÓS O UPDATE NENHUMA LINHA FOR ALTERADA, SIGNIFICA QUE O UPDATE NÃO EXECUTOU CORRETAMENTE.
  -- LANÇAMOS AQUI UMA EXCEÇÃO PARA RASTREAR ESSE ERRO.
  IF F_QTD_ROWS_ITENS_OS <= 0
  THEN RAISE EXCEPTION 'Não foi possível resolver o item do ProLog "%s"', COD_ITEM_RESOLVIDO_PROLOG;
  END IF;

  -- Vamos fechar a O.S. caso todos os itens dela já estejam resolvidos.
  UPDATE CHECKLIST_ORDEM_SERVICO_DATA SET
    STATUS = 'F',
    DATA_HORA_FECHAMENTO = F_DATA_HORA_RESOLVIDO_PROLOG
  WHERE CODIGO = F_COD_OS_GLOBUS
        AND COD_UNIDADE = F_COD_UNIDADE_ITEM_OS
        AND (SELECT COUNT(*)
             FROM PUBLIC.CHECKLIST_ORDEM_SERVICO_ITENS COSI
             WHERE COSI.COD_OS = F_COD_OS_GLOBUS
                   AND COSI.COD_UNIDADE = F_COD_UNIDADE_ITEM_OS
                   AND COSI.STATUS_RESOLUCAO = 'P') = 0;

  -- Para finalizar, atualizamos a tabela de vínculo marcando o item como resolvido.
  UPDATE PICCOLOTUR.CHECKLIST_ORDEM_SERVICO_ITEM_VINCULO SET
    DATA_HORA_SINCRONIA_RESOLUCAO = F_DATA_HORA_SINCRONIA_RESOLUCAO
  WHERE COD_ITEM_OS_PROLOG = COD_ITEM_RESOLVIDO_PROLOG AND COD_ITEM_OS_GLOBUS = F_COD_ITEM_RESOLVIDO_GLOBUS;

  GET DIAGNOSTICS F_QTD_ROWS_VINCULOS_ALTERADOS = ROW_COUNT;

  -- VERIFICAMOS SE O INSERT NA TABELA DE MAPEAMENTO DE ITENS RESOLVIDOS NA INTEGRAÇÃO OCORREU COM ÊXITO.
  IF F_QTD_ROWS_VINCULOS_ALTERADOS <= 0
  THEN
    RAISE EXCEPTION
    'Não foi possível inserir o item do ProLog resolvido na tabela de mapeamento, item "%s"', COD_ITEM_RESOLVIDO_PROLOG;
  END IF;

  RETURN F_QTD_ROWS_ITENS_OS;
END;
$$;
END TRANSACTION;