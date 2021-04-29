CREATE OR REPLACE FUNCTION SUPORTE.FUNC_AFERICAO_DELETA_AFERICAO(F_COD_UNIDADE BIGINT,
                                                                 F_CODIGO_AFERICAO BIGINT,
                                                                 F_MOTIVO_DELECAO TEXT,
                                                                 OUT AVISO_AFERICAO_DELETADA TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    QTD_LINHAS_ATUALIZADAS BIGINT;
BEGIN
    PERFORM SUPORTE.FUNC_HISTORICO_SALVA_EXECUCAO();
    IF ((SELECT COUNT(codigo)
         FROM AFERICAO_DATA
         WHERE CODIGO = F_CODIGO_AFERICAO
           AND COD_UNIDADE = F_COD_UNIDADE) <= 0)
    THEN
        RAISE EXCEPTION 'Nenhuma aferição encontrada com estes parâmetros: Unidade % e Código %',
            F_COD_UNIDADE, F_CODIGO_AFERICAO;
    END IF;

    -- DELETA AFERIÇÃO.
    UPDATE AFERICAO_DATA
    SET DELETADO            = TRUE,
        DATA_HORA_DELETADO  = NOW(),
        PG_USERNAME_DELECAO = SESSION_USER,
        MOTIVO_DELECAO = F_MOTIVO_DELECAO
    WHERE COD_UNIDADE = F_COD_UNIDADE
      AND CODIGO = F_CODIGO_AFERICAO
      AND DELETADO = FALSE;

    GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

    IF (QTD_LINHAS_ATUALIZADAS <= 0)
    THEN
        RAISE EXCEPTION 'Erro ao deletar o aferição de unidade: % e código: %',
            F_COD_UNIDADE, F_CODIGO_AFERICAO;
    END IF;

    -- DELETA AFERIÇÃO VALORES.
    UPDATE AFERICAO_VALORES_DATA
    SET DELETADO            = TRUE,
        DATA_HORA_DELETADO  = NOW(),
        PG_USERNAME_DELECAO = SESSION_USER,
        MOTIVO_DELECAO = F_MOTIVO_DELECAO
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
        RAISE EXCEPTION 'Erro ao deletar os valores de  aferição de unidade: % e código: %',
            F_COD_UNIDADE, F_CODIGO_AFERICAO;
    END IF;

    -- DELETA AFERIÇÃO MANUTENÇÃO.
    -- Não verificamos quantas linhas atualizadas pois aferição pode não ter manutenções.
    UPDATE AFERICAO_MANUTENCAO_DATA
    SET DELETADO            = TRUE,
        DATA_HORA_DELETADO  = NOW(),
        PG_USERNAME_DELECAO = SESSION_USER,
        MOTIVO_DELECAO = F_MOTIVO_DELECAO
    WHERE COD_UNIDADE = F_COD_UNIDADE
      AND COD_AFERICAO = F_CODIGO_AFERICAO;

    SELECT 'AFERIÇÃO DELETADA: '
               || F_CODIGO_AFERICAO
               || ', CÓDIGO DA UNIDADE: '
               || F_COD_UNIDADE
    INTO AVISO_AFERICAO_DELETADA;
END;
$$;