CREATE OR REPLACE FUNCTION SUPORTE.FUNC_AFERICAO_ALTERA_KM_COLETADO_AFERICAO(F_PLACA TEXT,
                                                                             F_COD_AFERICAO BIGINT,
                                                                             F_NOVO_KM BIGINT,
                                                                             OUT AVISO_KM_AFERICAO_ALTERADO TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    V_QTD_LINHAS_ATUALIZADAS       BIGINT;
    -- Não usa NOT NULL para não quebrar aqui com um erro não significativo para quem usar a function.
    V_COD_UNIDADE_VEICULO CONSTANT BIGINT := (SELECT VD.COD_UNIDADE
                                              FROM VEICULO_DATA VD
                                              WHERE VD.PLACA = F_PLACA);
BEGIN
    PERFORM SUPORTE.FUNC_HISTORICO_SALVA_EXECUCAO();
    PERFORM FUNC_GARANTE_UNIDADE_EXISTE(V_COD_UNIDADE_VEICULO);
    PERFORM FUNC_GARANTE_VEICULO_EXISTE(V_COD_UNIDADE_VEICULO, F_PLACA);
    PERFORM FUNC_GARANTE_NOVO_KM_MENOR_QUE_ATUAL_VEICULO(V_COD_UNIDADE_VEICULO, F_PLACA, F_NOVO_KM);

    -- Verifica se aferição existe.
    IF NOT EXISTS(SELECT AF.CODIGO
                  FROM AFERICAO AF
                  WHERE AF.PLACA_VEICULO IS NOT NULL
                    AND AF.PLACA_VEICULO = F_PLACA
                    AND AF.CODIGO = F_COD_AFERICAO)
    THEN
        RAISE EXCEPTION 'Não foi possível encontrar a aferição realizada com estes parâmetros: Placa %,
                     Código da aferição %', F_PLACA, F_COD_AFERICAO;
    END IF;

    UPDATE AFERICAO
    SET KM_VEICULO = F_NOVO_KM
    WHERE CODIGO = F_COD_AFERICAO
      AND PLACA_VEICULO = F_PLACA;

    GET DIAGNOSTICS V_QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

    IF (V_QTD_LINHAS_ATUALIZADAS <= 0)
    THEN
        RAISE EXCEPTION 'Erro ao atualizar o km da aferição com estes parâemtros: Placa %, Código
            da aferição %', F_PLACA, F_COD_AFERICAO;
    END IF;

    SELECT 'O KM DO VEÍCULO NA AFERIÇÃO FOI ALTERADO COM SUCESSO '
               || ', PLACA: '
               || F_PLACA
               || ', CÓDIGO DA AFERIÇÃO: '
               || F_COD_AFERICAO
    INTO AVISO_KM_AFERICAO_ALTERADO;
END;
$$;