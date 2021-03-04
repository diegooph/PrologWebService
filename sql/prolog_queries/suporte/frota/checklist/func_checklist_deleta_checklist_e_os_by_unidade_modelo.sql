-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Ao receber o código da unidade e do modelo de checklist, é realizada a deleção lógica dos checklists juntamente
-- com as suas respectivas OS's.
-- Caso o checklist seja integrado, também é realizada a deleção na tabela de integração.
--
-- Précondições:
--
-- Histórico:
-- 2020-07-07 -> Function criada. (thaisksf - PL-2801).
-- 2020-08-14 -> Adiciona chamada para logar execução da function (gustavocnp95 - PL-3066).
CREATE OR REPLACE FUNCTION SUPORTE.FUNC_CHECKLIST_DELETA_CHECKLIST_E_OS_BY_UNIDADE_MODELO(F_COD_UNIDADE BIGINT,
                                                                                          F_COD_CHECKLIST_MODELO BIGINT,
                                                                                          F_MOTIVO_DELECAO TEXT,
                                                                                          OUT AVISO_CHECKLIST_DELETADO TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_COD_CHECKLISTS   BIGINT[];
    V_COD_OS_DELETADAS BIGINT[];
BEGIN
    PERFORM SUPORTE.FUNC_HISTORICO_SALVA_EXECUCAO();
    IF (SELECT NOT EXISTS(SELECT CODIGO
                          FROM CHECKLIST
                          WHERE COD_UNIDADE = F_COD_UNIDADE
                            AND COD_CHECKLIST_MODELO = F_COD_CHECKLIST_MODELO))
    THEN
        RAISE EXCEPTION 'Nenhum checklist encontrado com as informações fornecidas, verifique.';
    END IF;

    SELECT ARRAY_AGG(CODIGO)
    FROM CHECKLIST
    WHERE COD_UNIDADE = F_COD_UNIDADE
      AND COD_CHECKLIST_MODELO = F_COD_CHECKLIST_MODELO
    INTO V_COD_CHECKLISTS;


    -- Deleta checklist de forma lógica.
    UPDATE
        CHECKLIST_DATA
    SET DELETADO            = TRUE,
        DATA_HORA_DELETADO  = NOW(),
        PG_USERNAME_DELECAO = SESSION_USER,
        MOTIVO_DELECAO      = F_MOTIVO_DELECAO
    WHERE COD_UNIDADE = F_COD_UNIDADE
      AND COD_CHECKLIST_MODELO = F_COD_CHECKLIST_MODELO
      AND DELETADO = FALSE;
    -- Validamos se o checklist realmente foi deletado lógicamente.
    IF (NOT FOUND)
    THEN
        RAISE EXCEPTION 'Erro ao deletar o checklist da Unidade: %, Modelo: %.', F_COD_UNIDADE, F_COD_CHECKLIST_MODELO;
    END IF;

    -- Deleta lógicamente a Ordem de Serviço e os itens vinculada ao checklist, se existir.
    IF (SELECT EXISTS(SELECT CODIGO
                      FROM CHECKLIST_ORDEM_SERVICO
                      WHERE COD_UNIDADE = F_COD_UNIDADE
                        AND COD_CHECKLIST = ANY (V_COD_CHECKLISTS)))
    THEN

        WITH OS_DELETADA(COD_OS) AS (
            UPDATE CHECKLIST_ORDEM_SERVICO_DATA
                SET DELETADO = TRUE,
                    DATA_HORA_DELETADO = NOW(),
                    PG_USERNAME_DELECAO = SESSION_USER,
                    MOTIVO_DELECAO = F_MOTIVO_DELECAO
                WHERE COD_UNIDADE = F_COD_UNIDADE
                    AND COD_CHECKLIST = ANY (V_COD_CHECKLISTS)
                    AND DELETADO = FALSE RETURNING CODIGO
        )
        SELECT ARRAY_AGG(COD_OS)
        FROM OS_DELETADA
        INTO V_COD_OS_DELETADAS;

        -- Deleta lógicamente a O.S.
        IF (NOT FOUND)
        THEN
            RAISE EXCEPTION 'Erro ao deletar O.S. da Unidade: %, Modelo: %.', F_COD_UNIDADE, F_COD_CHECKLIST_MODELO;
        END IF;

        -- Deleta lógicamente os Itens da O.S.
        UPDATE CHECKLIST_ORDEM_SERVICO_ITENS_DATA
        SET DELETADO            = TRUE,
            DATA_HORA_DELETADO  = NOW(),
            PG_USERNAME_DELECAO = SESSION_USER,
            MOTIVO_DELECAO      = F_MOTIVO_DELECAO
        WHERE COD_UNIDADE = F_COD_UNIDADE
          AND COD_OS = ANY (V_COD_OS_DELETADAS)
          AND DELETADO = FALSE;

        IF (NOT FOUND)
        THEN
            RAISE EXCEPTION 'Erro ao deletar Itens da O.S. da Unidade: %, Modelo: %.',
                F_COD_UNIDADE, F_COD_CHECKLIST_MODELO;
        END IF;
    END IF;

    -- Deleta checklist da integração da Piccolotur
    IF (SELECT EXISTS(SELECT COD_CHECKLIST_PARA_SINCRONIZAR
                      FROM PICCOLOTUR.CHECKLIST_PENDENTE_PARA_SINCRONIZAR
                      WHERE COD_CHECKLIST_PARA_SINCRONIZAR = ANY (V_COD_CHECKLISTS)))
    THEN
        -- Deletamos apenas da tabela de pendente para evitar o envio dos checks que ainda não foram sincronizados e
        -- estão sendo deletados.
        DELETE
        FROM PICCOLOTUR.CHECKLIST_PENDENTE_PARA_SINCRONIZAR
        WHERE COD_CHECKLIST_PARA_SINCRONIZAR = ANY (V_COD_CHECKLISTS);
    END IF;

    SELECT 'MODELO DE CHECKLIST DELETADO: '
               || F_COD_CHECKLIST_MODELO
               || ', CÓDIGO DA UNIDADE: '
               || F_COD_UNIDADE
    INTO AVISO_CHECKLIST_DELETADO;
END;
$$;