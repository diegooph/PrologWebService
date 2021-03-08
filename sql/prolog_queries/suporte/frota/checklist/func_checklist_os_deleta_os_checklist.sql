-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Ao validar os dados, todos os itens da ordem de serviço são deletados, logo é deletado a OS.
-- Caso o checklist seja integrado, também realizamos a deleção.
--
-- Précondições:
-- É feito a validação da Unidade.
--
-- Histórico:
-- 2019-07-24 -> Function criada (natanrotta - PL-2171).
-- 2019-09-17 -> Adiciona SESSION_USER (natanrotta - PL-2229).
-- 2019-09-18 -> Adiciona no schema suporte (natanrotta - PL-2242).
-- 2020-05-25 -> Adiciona estrutura para deletar checklist integrado (natanrotta - PLI-157).
-- 2020-08-14 -> Adiciona chamada para logar execução da function (gustavocnp95 - PL-3066).
CREATE OR REPLACE FUNCTION SUPORTE.FUNC_CHECKLIST_OS_DELETA_OS_CHECKLIST(F_COD_UNIDADE BIGINT,
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
    V_COD_COSI             BIGINT[];
BEGIN
    PERFORM SUPORTE.FUNC_HISTORICO_SALVA_EXECUCAO();
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
    SET DELETADO            = TRUE,
        DATA_HORA_DELETADO  = NOW(),
        PG_USERNAME_DELECAO = SESSION_USER
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
    SET DELETADO            = TRUE,
        DATA_HORA_DELETADO  = NOW(),
        PG_USERNAME_DELECAO = SESSION_USER
    WHERE CODIGO = F_COD_OS
      AND COD_UNIDADE = F_COD_UNIDADE
      AND COD_CHECKLIST = F_COD_CHECKLIST;

    GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

    IF (QTD_LINHAS_ATUALIZADAS <= 0)
    THEN
        RAISE EXCEPTION 'ERRO AO DELETAR ORDEM DE SERVIÇO DA UNIDADE: %, CÓDIGO OS: %, CÓDIGO CHECKLIST: %',
            F_COD_UNIDADE, F_COD_OS, F_COD_CHECKLIST;
    END IF;

    -- Busca código do item
    SELECT ARRAY_AGG(CODIGO)
    FROM CHECKLIST_ORDEM_SERVICO_ITENS_DATA
    WHERE COD_UNIDADE = F_COD_UNIDADE
      AND COD_OS = F_COD_OS
    INTO V_COD_COSI;

    -- Verifica se checklist está integrado, caso esteja é deletado
    IF EXISTS(SELECT ICOSI.COD_EMPRESA
              FROM INTEGRACAO.CHECKLIST_ORDEM_SERVICO_ITEM_RESOLVIDO ICOSI
              WHERE ICOSI.COD_OS = F_COD_OS
                AND ICOSI.COD_ITEM_OS = ANY (V_COD_COSI))
    THEN
        -- Realiza a deleção (Não possuímos deleção lógica).
        DELETE
        FROM INTEGRACAO.CHECKLIST_ORDEM_SERVICO_ITEM_RESOLVIDO
        WHERE COD_UNIDADE = F_COD_UNIDADE
          AND COD_OS = F_COD_OS
          AND COD_ITEM_OS = ANY (V_COD_COSI);
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