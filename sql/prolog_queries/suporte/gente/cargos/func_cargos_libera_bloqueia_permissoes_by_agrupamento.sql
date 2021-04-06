CREATE OR REPLACE FUNCTION
    SUPORTE.FUNC_CARGOS_LIBERA_BLOQUEIA_PERMISSOES_BY_AGRUPAMENTO(F_LIBERAR_BLOQUEAR TEXT,
                                                                  F_COD_AGRUPAMENTOS BIGINT[],
                                                                  F_COD_EMPRESAS BIGINT[],
                                                                  F_COD_MOTIVO_BLOQUEIO BIGINT,
                                                                  F_OBSERVACAO_BLOQUEIO TEXT DEFAULT NULL,
                                                                  F_COD_UNIDADES BIGINT[] DEFAULT NULL)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    V_COD_AGRUPAMENTOS_NAO_MAPEADOS BIGINT[] := (SELECT ARRAY_AGG(AGRUPAMENTO.COD_AGRUPAMENTO)
                                                 FROM (SELECT UNNEST(F_COD_AGRUPAMENTOS) AS COD_AGRUPAMENTO) AGRUPAMENTO
                                                 WHERE AGRUPAMENTO.COD_AGRUPAMENTO NOT IN
                                                       (SELECT FPA.CODIGO FROM FUNCAO_PROLOG_AGRUPAMENTO FPA));
    V_COD_PERMISSOES                BIGINT[] := (SELECT ARRAY_AGG(FP.CODIGO)
                                                 FROM FUNCAO_PROLOG_V11 FP
                                                 WHERE FP.COD_AGRUPAMENTO = ANY (F_COD_AGRUPAMENTOS));
BEGIN
    PERFORM SUPORTE.FUNC_HISTORICO_SALVA_EXECUCAO();
    -- Validamos apenas os agrupamentos não mapeados, demais validações são feitas pela function interna.
    IF (F_SIZE_ARRAY(V_COD_AGRUPAMENTOS_NAO_MAPEADOS) > 0)
    THEN
        PERFORM THROW_GENERIC_ERROR(
                FORMAT('Códigos de agrupamentos inválidos (%s). ' ||
                       'Verifique os códigos na tabela funcao_prolog_agrupamento.',
                       V_COD_AGRUPAMENTOS_NAO_MAPEADOS));
    END IF;

    PERFORM SUPORTE.FUNC_CARGOS_LIBERA_BLOQUEIA_PERMISSOES_BY_CODIGO(F_LIBERAR_BLOQUEAR,
                                                                     V_COD_PERMISSOES,
                                                                     F_COD_EMPRESAS,
                                                                     F_COD_MOTIVO_BLOQUEIO,
                                                                     F_OBSERVACAO_BLOQUEIO,
                                                                     F_COD_UNIDADES);

    RETURN (SELECT FORMAT('A operação de %s foi realizada com sucesso para as permissões dos agrupamentos (%s)',
                          F_LIBERAR_BLOQUEAR, F_COD_AGRUPAMENTOS));
END;
$$;