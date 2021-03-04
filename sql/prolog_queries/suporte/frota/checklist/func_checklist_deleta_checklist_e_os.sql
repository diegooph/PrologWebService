-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Ao receber o código do checklist, é realizado a deleção lógica do mesmo junto com as suas respectivas OS.
-- Caso o checklist é integrado, também é realizado a deleção na tabela de integração.
--
-- Précondições:
--
-- Histórico:
-- 2019-09-17 -> Adiciona SESSION_USER. (natanrotta - PL-2229).
-- 2019-09-18 -> Adiciona no schema suporte. (natanrotta - PL-2242).
-- 2020-05-22 -> Adiciona estrutura para deletar correspondentes na integração. (natanrotta - PLI-157).
-- 2020-07-07 -> Adiciona motivo de deleção. (thaisksf - PL-2801).
-- 2020-08-14 -> Adiciona chamada para logar execução da function (gustavocnp95 - PL-3066).
CREATE OR REPLACE FUNCTION SUPORTE.FUNC_CHECKLIST_DELETA_CHECKLIST_E_OS(F_COD_UNIDADE BIGINT,
                                                                        F_COD_CHECKLIST BIGINT,
                                                                        F_PLACA TEXT,
                                                                        F_CPF_COLABORADOR BIGINT,
                                                                        F_MOTIVO_DELECAO TEXT,
                                                                        OUT AVISO_CHECKLIST_DELETADO TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    V_COD_OS_DELETADA BIGINT;
BEGIN
    PERFORM SUPORTE.FUNC_HISTORICO_SALVA_EXECUCAO();
    IF (SELECT NOT EXISTS(SELECT CODIGO
                          FROM CHECKLIST
                          WHERE CODIGO = F_COD_CHECKLIST
                            AND COD_UNIDADE = F_COD_UNIDADE
                            AND PLACA_VEICULO = F_PLACA
                            AND CPF_COLABORADOR = F_CPF_COLABORADOR))
    THEN
        RAISE EXCEPTION 'Nenhum checklist encontrado com as informações fornecidas, verifique!';
    END IF;

    -- Deleta checklist de forma lógica.
    UPDATE CHECKLIST_DATA
    SET DELETADO            = TRUE,
        DATA_HORA_DELETADO  = NOW(),
        PG_USERNAME_DELECAO = SESSION_USER,
        MOTIVO_DELECAO      = F_MOTIVO_DELECAO
    WHERE CODIGO = F_COD_CHECKLIST
      AND COD_UNIDADE = F_COD_UNIDADE
      AND PLACA_VEICULO = F_PLACA
      AND CPF_COLABORADOR = F_CPF_COLABORADOR
      AND DELETADO = FALSE;

    -- Validamos se o checklist realmente foi deletado lógicamente.
    IF (NOT FOUND)
    THEN
        RAISE EXCEPTION 'Erro ao deletar o checklist de código: % da Unidade: %', F_COD_CHECKLIST, F_COD_UNIDADE;
    END IF;

    -- Deleta lógicamente a Ordem de Serviço e os itens vinculada ao checklist, se existir.
    IF (SELECT EXISTS(SELECT CODIGO
                      FROM CHECKLIST_ORDEM_SERVICO
                      WHERE COD_CHECKLIST = F_COD_CHECKLIST
                        AND COD_UNIDADE = F_COD_UNIDADE))
    THEN
        -- Deleta lógicamente a O.S.
        UPDATE CHECKLIST_ORDEM_SERVICO_DATA
        SET DELETADO            = TRUE,
            DATA_HORA_DELETADO  = NOW(),
            PG_USERNAME_DELECAO = SESSION_USER,
            MOTIVO_DELECAO      = F_MOTIVO_DELECAO
        WHERE COD_CHECKLIST = F_COD_CHECKLIST
          AND COD_UNIDADE = F_COD_UNIDADE
          AND DELETADO = FALSE RETURNING CODIGO INTO V_COD_OS_DELETADA;

        IF (NOT FOUND)
        THEN
            RAISE EXCEPTION 'Erro ao deletar O.S. do checklist: % da Unidade: %', F_COD_CHECKLIST, F_COD_UNIDADE;
        END IF;

        -- Deleta lógicamente os Itens da O.S.
        UPDATE CHECKLIST_ORDEM_SERVICO_ITENS_DATA
        SET DELETADO            = TRUE,
            DATA_HORA_DELETADO  = NOW(),
            PG_USERNAME_DELECAO = SESSION_USER,
            MOTIVO_DELECAO      = F_MOTIVO_DELECAO
        WHERE COD_UNIDADE = F_COD_UNIDADE
          AND COD_OS = V_COD_OS_DELETADA
          AND DELETADO = FALSE;

        IF (NOT FOUND)
        THEN
            RAISE EXCEPTION 'Erro ao deletar Itens da O.S. do checklist: % da Unidade: %',
                F_COD_CHECKLIST, F_COD_UNIDADE;
        END IF;
    END IF;

    -- Deleta checklist da integração da Piccolotur
    IF (SELECT EXISTS(SELECT COD_CHECKLIST_PARA_SINCRONIZAR
                      FROM PICCOLOTUR.CHECKLIST_PENDENTE_PARA_SINCRONIZAR
                      WHERE COD_CHECKLIST_PARA_SINCRONIZAR = F_COD_CHECKLIST))
    THEN
        -- Deletamos apenas da tabela de pendente para evitar o envio dos checks que ainda não foram sincronizados e
        -- estão sendo deletados.
        DELETE
        FROM PICCOLOTUR.CHECKLIST_PENDENTE_PARA_SINCRONIZAR
        WHERE COD_CHECKLIST_PARA_SINCRONIZAR = F_COD_CHECKLIST;
    END IF;

    SELECT 'CHECKLIST DELETADO: '
               || F_COD_CHECKLIST
               || ', CÓDIGO DA UNIDADE: '
               || F_COD_UNIDADE
    INTO AVISO_CHECKLIST_DELETADO;
END;
$$;