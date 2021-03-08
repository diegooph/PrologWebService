CREATE OR REPLACE FUNCTION INTERNO.FUNC_DELETA_AFERICOES_DEPENDENCIAS(F_COD_UNIDADES BIGINT[],
                                                                      F_COD_AFERICOES BIGINT[])
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    DELETE
    FROM AFERICAO_MANUTENCAO_SERVICO_DELETADO_TRANSFERENCIA AMSDT
    WHERE AMSDT.COD_SERVICO IN (SELECT AMD.CODIGO
                                FROM AFERICAO_MANUTENCAO_DATA AMD
                                WHERE AMD.COD_AFERICAO = ANY
                                      (F_COD_AFERICOES));

    DELETE
    FROM AFERICAO_MANUTENCAO_DATA AMD
    WHERE AMD.COD_AFERICAO = ANY (F_COD_AFERICOES);

    DELETE
    FROM AFERICAO_CONFIGURACAO_TIPO_AFERICAO_VEICULO ACTAV
    WHERE ACTAV.COD_UNIDADE = ANY (F_COD_UNIDADES);

    DELETE
    FROM AFERICAO_VALORES_DATA AVD
    WHERE AVD.COD_AFERICAO = ANY (F_COD_AFERICOES);

    DELETE
    FROM AFERICAO_DATA AD
    WHERE AD.CODIGO = ANY (F_COD_AFERICOES);

    DELETE
    FROM PNEU_RESTRICAO_UNIDADE_HISTORICO PRUH
    WHERE PRUH.COD_UNIDADE = ANY (F_COD_UNIDADES);

    DELETE
    FROM PNEU_RESTRICAO_UNIDADE PRU
    WHERE PRU.COD_UNIDADE = ANY (F_COD_UNIDADES);

    DELETE
    FROM AFERICAO_CONFIGURACAO_TIPO_AFERICAO_VEICULO_BACKUP ACTAVB
    WHERE ACTAVB.COD_UNIDADE = ANY (F_COD_UNIDADES);

    DELETE
    FROM AFERICAO_CONFIGURACAO_ALERTA_SULCO ACAS
    WHERE ACAS.COD_UNIDADE = ANY (F_COD_UNIDADES);
END;
$$;
