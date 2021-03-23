CREATE OR REPLACE FUNCTION FUNC_GARANTE_NOVO_KM_MENOR_QUE_ATUAL_VEICULO(F_COD_UNIDADE_VEICULO BIGINT,
                                                                        F_PLACA_VEICULO TEXT,
                                                                        F_NOVO_KM BIGINT)
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    F_COD_EMPRESA CONSTANT      BIGINT := (SELECT U.COD_EMPRESA
                                           FROM UNIDADE U
                                           WHERE U.CODIGO = F_COD_UNIDADE_VEICULO);
    F_KM_ATUAL_VEICULO CONSTANT BIGINT := (SELECT VD.KM
                                           FROM VEICULO_DATA VD
                                           WHERE VD.PLACA = F_PLACA_VEICULO
                                             AND VD.COD_UNIDADE IN
                                                 (SELECT U.CODIGO FROM UNIDADE U WHERE U.COD_EMPRESA = F_COD_EMPRESA));
BEGIN
    IF (F_KM_ATUAL_VEICULO IS NOT NULL AND F_NOVO_KM > F_KM_ATUAL_VEICULO)
    THEN
        RAISE EXCEPTION 'O Km enviado não pode ser maior que o Km atual do veículo : Km enviado %, Km atual %',
            F_NOVO_KM,
            F_KM_ATUAL_VEICULO;
    END IF;
END;
$$;