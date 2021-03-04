-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
--
-- Précondições:
--
-- Histórico:
-- 2019-09-17 -> Adiciona SESSION_USER. (natanrotta - PL-2229).
-- 2019-09-18 -> Adiciona no schema suporte. (natanrotta - PL-2242).
-- 2020-07-07 -> Adiciona motivo de deleção e corrige bug. (thaisksf - PL-2801).
-- 2020-08-14 -> Adiciona chamada para logar execução da function (gustavocnp95 - PL-3066).
CREATE OR REPLACE FUNCTION SUPORTE.FUNC_CHECKLIST_DELETA_MODELO_CHECKLIST(F_COD_UNIDADE BIGINT,
                                                                          F_COD_MODELO_CHECKLIST BIGINT,
                                                                          F_MOTIVO_DELECAO TEXT,
                                                                          F_DELETAR_CHECKLISTS_REALIZADOS_DESSE_MODELO
                                                                              BOOLEAN DEFAULT FALSE,
                                                                          OUT AVISO_MODELO_CHECKLIST_DELETADO TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    QTD_LINHAS_ATUALIZADAS BIGINT;
BEGIN
    PERFORM SUPORTE.FUNC_HISTORICO_SALVA_EXECUCAO();
    IF F_DELETAR_CHECKLISTS_REALIZADOS_DESSE_MODELO
    THEN
        PERFORM SUPORTE.FUNC_CHECKLIST_DELETA_CHECKLIST_E_OS_BY_UNIDADE_MODELO(
                F_COD_UNIDADE := F_COD_UNIDADE,
                F_COD_CHECKLIST_MODELO := F_COD_MODELO_CHECKLIST,
                F_MOTIVO_DELECAO := F_MOTIVO_DELECAO);
    END IF;

    -- Deleta modelo de checklist.
    UPDATE CHECKLIST_MODELO_DATA
    SET DELETADO            = TRUE,
        DATA_HORA_DELETADO  = NOW(),
        PG_USERNAME_DELECAO = SESSION_USER,
        MOTIVO_DELECAO      = F_MOTIVO_DELECAO
    WHERE CODIGO = F_COD_MODELO_CHECKLIST
      AND COD_UNIDADE = F_COD_UNIDADE;

    GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

    IF (QTD_LINHAS_ATUALIZADAS <= 0)
    THEN
        RAISE EXCEPTION 'Erro ao deletar o modelo de checklist de código: % e Unidade: %',
            F_COD_MODELO_CHECKLIST, F_COD_UNIDADE;
    END IF;

    -- Deleta pergundas do modelo de checklist.
    UPDATE CHECKLIST_PERGUNTAS_DATA
    SET DELETADO            = TRUE,
        DATA_HORA_DELETADO  = NOW(),
        PG_USERNAME_DELECAO = SESSION_USER,
        MOTIVO_DELECAO      = F_MOTIVO_DELECAO
    WHERE COD_CHECKLIST_MODELO = F_COD_MODELO_CHECKLIST
      AND COD_UNIDADE = F_COD_UNIDADE;

    GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

    IF (QTD_LINHAS_ATUALIZADAS <= 0)
    THEN
        RAISE EXCEPTION 'Erro ao deletar as perguntas do modelo de checklist de código: % e Unidade: %',
            F_COD_MODELO_CHECKLIST, F_COD_UNIDADE;
    END IF;

    -- Deleta as alternativas das pergundas do modelo de checklist.
    UPDATE CHECKLIST_ALTERNATIVA_PERGUNTA_DATA
    SET DELETADO            = TRUE,
        DATA_HORA_DELETADO  = NOW(),
        PG_USERNAME_DELECAO = SESSION_USER,
        MOTIVO_DELECAO      = F_MOTIVO_DELECAO
    WHERE COD_CHECKLIST_MODELO = F_COD_MODELO_CHECKLIST
      AND COD_UNIDADE = F_COD_UNIDADE;

    GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

    IF (QTD_LINHAS_ATUALIZADAS <= 0)
    THEN
        RAISE EXCEPTION 'Erro ao deletar as alternativas do modelo de checklist de código: % e Unidade: %',
            F_COD_MODELO_CHECKLIST, F_COD_UNIDADE;
    END IF;

    -- As únicas coisas que deletamos de fato são os vínculos de cargos e tipos de veículos, assim um modelo marcado
    -- como "deletado" não fica com vínculos que podem bloquear outras operações do BD.
    DELETE
    FROM CHECKLIST_MODELO_FUNCAO
    WHERE COD_UNIDADE = F_COD_UNIDADE
      AND COD_CHECKLIST_MODELO = F_COD_MODELO_CHECKLIST;
    DELETE
    FROM CHECKLIST_MODELO_VEICULO_TIPO
    WHERE COD_UNIDADE = F_COD_UNIDADE
      AND COD_MODELO = F_COD_MODELO_CHECKLIST;

    SELECT 'MODELO DE CHECKLIST DELETADO: '
               || F_COD_MODELO_CHECKLIST
               || ', CÓDIGO DA UNIDADE: '
               || F_COD_UNIDADE
               || F_IF(F_DELETAR_CHECKLISTS_REALIZADOS_DESSE_MODELO,
                       '. OS CHECKLISTS REALIZADOS DESSE MODELO TAMBÉM FORAM DELETADOS.' :: TEXT,
                       '. OS CHECKLISTS REALIZADOS DESSE MODELO NÃO FORAM DELETADOS.' :: TEXT)
    INTO AVISO_MODELO_CHECKLIST_DELETADO;
END;
$$;