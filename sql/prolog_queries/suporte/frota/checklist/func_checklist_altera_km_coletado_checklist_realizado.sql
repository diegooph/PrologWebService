CREATE OR REPLACE FUNCTION SUPORTE.FUNC_CHECKLIST_ALTERA_KM_COLETADO_CHECKLIST_REALIZADO(F_COD_UNIDADE BIGINT,
                                                                                         F_PLACA TEXT,
                                                                                         F_COD_CHECKLIST_REALIZADO BIGINT,
                                                                                         F_NOVO_KM BIGINT,
                                                                                         OUT AVISO_KM_CHECKLIST_ALTERADO TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    QTD_LINHAS_ATUALIZADAS BIGINT;
BEGIN
    PERFORM SUPORTE.FUNC_HISTORICO_SALVA_EXECUCAO();
    PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE);
    PERFORM FUNC_GARANTE_VEICULO_EXISTE(F_COD_UNIDADE, F_PLACA);
    PERFORM FUNC_GARANTE_NOVO_KM_MENOR_QUE_ATUAL_VEICULO(F_COD_UNIDADE, F_PLACA, F_NOVO_KM);

    -- Verifica se o checklist existe.
    IF NOT EXISTS(SELECT CD.CODIGO
                  FROM CHECKLIST CD
                  WHERE CD.CODIGO = F_COD_CHECKLIST_REALIZADO
                    AND CD.COD_UNIDADE = F_COD_UNIDADE
                    AND CD.PLACA_VEICULO = F_PLACA)
    THEN
        RAISE EXCEPTION 'Não foi possível encontrar o checklist realizado com estes parâmetros: Unidade %, Placa %,
                     Código da realização do checklist %', F_COD_UNIDADE, F_PLACA, F_COD_CHECKLIST_REALIZADO;
    END IF;

    UPDATE CHECKLIST_DATA
    SET KM_VEICULO = F_NOVO_KM
    WHERE CODIGO = F_COD_CHECKLIST_REALIZADO
      AND COD_UNIDADE = F_COD_UNIDADE
      AND PLACA_VEICULO = F_PLACA;

    GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

    IF (QTD_LINHAS_ATUALIZADAS <= 0)
    THEN
        RAISE EXCEPTION 'Erro ao atualizar o km do checklist realizado com estes parâemtros: Unidade %, Placa %,
                     Código da realização do checklist %', F_COD_UNIDADE, F_PLACA, F_COD_CHECKLIST_REALIZADO;
    END IF;

    SELECT 'O KM DO VEÍCULO NO CHECKLIST REALIZADO FOI ALTERADO COM SUCESSO, UNIDADE: '
               || F_COD_UNIDADE
               || ', PLACA: '
               || F_PLACA
               || ', CÓDIGO DO CHECKLIST REALIZADO: '
               || F_COD_CHECKLIST_REALIZADO
    INTO AVISO_KM_CHECKLIST_ALTERADO;
END;
$$;