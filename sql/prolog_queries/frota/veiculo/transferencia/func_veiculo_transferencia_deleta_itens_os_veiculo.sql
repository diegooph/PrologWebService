-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
--
-- Précondições:
--
-- Histórico:
-- 2019-09-17 -> Adiciona SESSION_USER (natanrotta - PL-2229).
CREATE OR REPLACE FUNCTION FUNC_VEICULO_TRANSFERENCIA_DELETA_ITENS_OS_VEICULO(F_COD_VEICULO BIGINT,
                                                                              F_COD_TRANSFERENCIA_VEICULO_INFORMACOES BIGINT,
                                                                              F_DATA_HORA_REALIZACAO_TRANSFERENCIA TIMESTAMP WITH TIME ZONE)
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    QTD_INSERTS            BIGINT;
    QTD_UPDATES            BIGINT;
    F_STATUS_OS_ABERTA     TEXT := 'A';
    F_STATUS_OS_FECHADA    TEXT := 'F';
    F_STATUS_ITEM_PENDENTE TEXT := 'P';
    F_PLACA_VEICULO        TEXT := (SELECT V.PLACA
                                    FROM VEICULO V
                                    WHERE V.CODIGO = F_COD_VEICULO);
    F_OS                   CHECKLIST_ORDEM_SERVICO%ROWTYPE;
BEGIN
    FOR F_OS IN
        SELECT COS.CODIGO_PROLOG,
               COS.CODIGO,
               COS.COD_UNIDADE,
               COS.COD_CHECKLIST
            -- Utilizamos propositalmente a view e não a tabela _DATA, para não copiar OSs deletadas e não resolvidos.
        FROM CHECKLIST_ORDEM_SERVICO COS
                 JOIN CHECKLIST C ON C.CODIGO = COS.cod_checklist
        WHERE COS.STATUS = F_STATUS_OS_ABERTA
          AND C.PLACA_VEICULO = F_PLACA_VEICULO
        LOOP
            -- Copia os itens da OS.
            INSERT INTO CHECKLIST_ORDEM_SERVICO_ITEM_DELETADO_TRANSFERENCIA (
                COD_ITEM_OS_PROLOG)
            SELECT COSI.CODIGO
                   -- Utilizamos propositalmente a view e não a tabela _DATA, para não copiar itens já deletados e
                   -- não resolvidos.
            FROM CHECKLIST_ORDEM_SERVICO_ITENS COSI
            WHERE COSI.COD_OS = F_OS.CODIGO
              AND COSI.COD_UNIDADE = F_OS.COD_UNIDADE
              AND COSI.STATUS_RESOLUCAO = F_STATUS_ITEM_PENDENTE;

            GET DIAGNOSTICS QTD_INSERTS = ROW_COUNT;

            -- Deleta os itens da OS.
            UPDATE CHECKLIST_ORDEM_SERVICO_ITENS_DATA
            SET DELETADO            = TRUE,
                PG_USERNAME_DELECAO = SESSION_USER,
                DATA_HORA_DELETADO  = F_DATA_HORA_REALIZACAO_TRANSFERENCIA
            WHERE COD_OS = F_OS.CODIGO
              AND COD_UNIDADE = F_OS.COD_UNIDADE
              AND STATUS_RESOLUCAO = F_STATUS_ITEM_PENDENTE
              AND DELETADO = FALSE;

            GET DIAGNOSTICS QTD_UPDATES = ROW_COUNT;

            IF QTD_INSERTS <> QTD_UPDATES
            THEN
                RAISE EXCEPTION
                    'Erro ao deletar os itens de O.S. de checklist na transferência de veículos. Rollback necessário!
                    __INSERTS: % UPDATES: %__', QTD_INSERTS, QTD_UPDATES;
            END IF;

            IF ((SELECT COUNT(COSI.CODIGO)
                 FROM CHECKLIST_ORDEM_SERVICO_ITENS_DATA COSI
                 WHERE COSI.COD_OS = F_OS.CODIGO
                   AND COSI.COD_UNIDADE = F_OS.COD_UNIDADE
                   AND COSI.DELETADO = FALSE) > 0)
            THEN
                -- Se entrou aqui siginifca que a OS não tem mais itens em aberto, ela possuia alguns fechados e
                -- outros em aberto
                -- mas nós acabamos de deletar os que estavam em aberto.
                -- Por isso, precisamos fechar essa OS.
                UPDATE
                    CHECKLIST_ORDEM_SERVICO_DATA
                SET STATUS               = F_STATUS_OS_FECHADA,
                    PG_USERNAME_DELECAO  = SESSION_USER,
                    DATA_HORA_FECHAMENTO = (SELECT MAX(COSI.DATA_HORA_CONSERTO)
                                            FROM CHECKLIST_ORDEM_SERVICO_ITENS_DATA COSI
                                            WHERE COSI.COD_OS = F_OS.CODIGO
                                              AND COSI.COD_UNIDADE = F_OS.COD_UNIDADE
                                              AND COSI.DELETADO = FALSE)
                WHERE CODIGO_PROLOG = F_OS.CODIGO_PROLOG
                  AND CODIGO = F_OS.CODIGO
                  AND COD_UNIDADE = F_OS.COD_UNIDADE
                  AND COD_CHECKLIST = F_OS.COD_CHECKLIST
                  AND DELETADO = FALSE;
            ELSE
                -- Se entrou aqui siginifica que nós deletamos todos os itens da OS.
                -- Por isso, precisamos copiar a OS para a tabela de vínculo como deletada por transferência e
                -- depois deletá-la.

                -- Copia a OS.
                INSERT INTO CHECKLIST_ORDEM_SERVICO_DELETADA_TRANSFERENCIA (COD_OS_PROLOG,
                                                                            COD_VEICULO_TRANSFERENCIA_INFORMACOES)
                SELECT F_OS.CODIGO_PROLOG,
                       F_COD_TRANSFERENCIA_VEICULO_INFORMACOES;

                GET DIAGNOSTICS QTD_INSERTS = ROW_COUNT;

                -- Deleta a OS copiada.
                UPDATE CHECKLIST_ORDEM_SERVICO_DATA
                SET DELETADO            = TRUE,
                    PG_USERNAME_DELECAO = SESSION_USER,
                    DATA_HORA_DELETADO  = F_DATA_HORA_REALIZACAO_TRANSFERENCIA
                WHERE CODIGO_PROLOG = F_OS.CODIGO_PROLOG
                  AND CODIGO = F_OS.CODIGO
                  AND COD_UNIDADE = F_OS.COD_UNIDADE
                  AND COD_CHECKLIST = F_OS.COD_CHECKLIST
                  AND DELETADO = FALSE;

                GET DIAGNOSTICS QTD_UPDATES = ROW_COUNT;

                IF QTD_INSERTS <> QTD_UPDATES
                THEN
                    RAISE EXCEPTION
                        'Erro ao deletar as OSs de checklist na transferência de veículos. Rollback necessário!
                        __INSERTS: % UPDATES: %__', QTD_INSERTS, QTD_UPDATES;
                END IF;
            END IF;
        END LOOP;
END;
$$;