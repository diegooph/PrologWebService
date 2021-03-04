-- Sobre:
--
-- Function que gera o relatório de quantidade de itens abertos ou fechados (via F_STATUS_ITENS) por prioridade.
--
-- Histórico:
-- 2020-03-17 -> Atualização de arquivo e documentação (luizfp - PL-2494).
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_OS_RELATORIO_QTD_ITENS_POR_PRIORIDADE(F_COD_UNIDADES BIGINT[],
                                                                                F_STATUS_ITENS TEXT)
    RETURNS TABLE
            (
                PRIORIDADE TEXT,
                QUANTIDADE BIGINT
            )
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    RETURN QUERY
        WITH PRIORIDADES AS (
            SELECT CAP.PRIORIDADE :: TEXT AS PRIORIDADE
            FROM CHECKLIST_ALTERNATIVA_PRIORIDADE CAP
            ORDER BY CAP.PRAZO ASC
        )

        SELECT P.PRIORIDADE       AS PRIORIDADE,
               COUNT(COSI.CODIGO) AS QUANTIDADE
        FROM CHECKLIST_ALTERNATIVA_PERGUNTA CAP
                 JOIN CHECKLIST_ORDEM_SERVICO_ITENS COSI
                      ON CAP.CODIGO = COSI.COD_ALTERNATIVA_PRIMEIRO_APONTAMENTO
                          AND COSI.COD_UNIDADE = ANY (F_COD_UNIDADES)
                          AND COSI.STATUS_RESOLUCAO = F_STATUS_ITENS
                 RIGHT JOIN PRIORIDADES P
                            ON CAP.PRIORIDADE = P.PRIORIDADE
        GROUP BY P.PRIORIDADE
        ORDER BY CASE P.PRIORIDADE
                     WHEN 'CRITICA'
                         THEN 1
                     WHEN 'ALTA'
                         THEN 2
                     WHEN 'BAIXA'
                         THEN 3
                     END;
END;
$$;