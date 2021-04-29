CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_PNEU_VINCULA_PNEU_POSICAO_PLACA(F_COD_VEICULO_PROLOG BIGINT,
                                                                           F_PLACA_VEICULO_PNEU_APLICADO TEXT,
                                                                           F_COD_PNEU_PROLOG BIGINT,
                                                                           F_CODIGO_PNEU_CLIENTE TEXT,
                                                                           F_COD_UNIDADE_PNEU BIGINT,
                                                                           F_POSICAO_VEICULO_PNEU_APLICADO INTEGER,
                                                                           F_IS_POSICAO_ESTEPE BOOLEAN)
    RETURNS BOOLEAN
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    F_QTD_ROWS_ALTERADAS BIGINT;
BEGIN
    -- Validamos se a placa existe no ProLog.
    IF (F_COD_VEICULO_PROLOG IS NULL OR F_COD_VEICULO_PROLOG <= 0)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('A placa informada %s não está presente no Sistema ProLog',
                                                  F_PLACA_VEICULO_PNEU_APLICADO));
    END IF;

    -- Validamos se o placa e o pneu pertencem a mesma unidade.
    IF ((SELECT V.COD_UNIDADE FROM PUBLIC.VEICULO V WHERE V.CODIGO = F_COD_VEICULO_PROLOG) <> F_COD_UNIDADE_PNEU)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                        FORMAT('A placa informada %s está em uma Unidade diferente do pneu informado %s,
               unidade da placa %s, unidade do pneu %s',
                               F_PLACA_VEICULO_PNEU_APLICADO,
                               F_CODIGO_PNEU_CLIENTE,
                               (SELECT V.COD_UNIDADE FROM PUBLIC.VEICULO V WHERE V.CODIGO = F_COD_VEICULO_PROLOG),
                               F_COD_UNIDADE_PNEU));
    END IF;

    -- Validamos se a posição repassada é uma posição válida no ProLog.
    IF (NOT IS_PLACA_POSICAO_PNEU_VALIDA(F_COD_VEICULO_PROLOG, F_POSICAO_VEICULO_PNEU_APLICADO, F_IS_POSICAO_ESTEPE))
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(
                        FORMAT('A posição informada %s para o pneu, não é uma posição válida para a placa %s',
                               F_POSICAO_VEICULO_PNEU_APLICADO,
                               F_PLACA_VEICULO_PNEU_APLICADO));
    END IF;

    -- Validamos se a placa possui algum outro pneu aplicado na posição.
    IF (SELECT EXISTS(SELECT *
                      FROM PUBLIC.VEICULO_PNEU VP
                      WHERE VP.PLACA = F_PLACA_VEICULO_PNEU_APLICADO
                        AND VP.COD_UNIDADE = F_COD_UNIDADE_PNEU
                        AND VP.POSICAO = F_POSICAO_VEICULO_PNEU_APLICADO))
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('Já existe um pneu na placa %s, posição %s',
                                                  F_PLACA_VEICULO_PNEU_APLICADO,
                                                  F_POSICAO_VEICULO_PNEU_APLICADO));
    END IF;

    -- Vincula pneu a placa.
    INSERT INTO PUBLIC.VEICULO_PNEU(PLACA,
                                    COD_PNEU,
                                    COD_UNIDADE,
                                    POSICAO,
                                    COD_DIAGRAMA)
    VALUES (F_PLACA_VEICULO_PNEU_APLICADO,
            F_COD_PNEU_PROLOG,
            F_COD_UNIDADE_PNEU,
            F_POSICAO_VEICULO_PNEU_APLICADO,
            (SELECT VT.COD_DIAGRAMA
             FROM VEICULO_TIPO VT
             WHERE VT.CODIGO = (SELECT V.COD_TIPO FROM VEICULO V WHERE V.CODIGO = F_COD_VEICULO_PROLOG)));

    GET DIAGNOSTICS F_QTD_ROWS_ALTERADAS = ROW_COUNT;

    -- Verificamos se o update ocorreu como deveria
    IF (F_QTD_ROWS_ALTERADAS <= 0)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('Não foi possível aplicar o pneu %s na placa %s',
                                                  F_CODIGO_PNEU_CLIENTE,
                                                  F_PLACA_VEICULO_PNEU_APLICADO));
    END IF;

    -- Retornamos sucesso se o pneu estiver aplicado na placa e posição que deveria estar.
    IF (SELECT EXISTS(SELECT VP.POSICAO
                      FROM PUBLIC.VEICULO_PNEU VP
                      WHERE VP.PLACA = F_PLACA_VEICULO_PNEU_APLICADO
                        AND VP.COD_PNEU = F_COD_PNEU_PROLOG
                        AND VP.POSICAO = F_POSICAO_VEICULO_PNEU_APLICADO
                        AND VP.COD_UNIDADE = F_COD_UNIDADE_PNEU))
    THEN
        RETURN TRUE;
    ELSE
        PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('Não foi possível aplicar o pneu %s na placa %s',
                                                  F_CODIGO_PNEU_CLIENTE,
                                                  F_PLACA_VEICULO_PNEU_APLICADO));
    END IF;
END ;
$$;