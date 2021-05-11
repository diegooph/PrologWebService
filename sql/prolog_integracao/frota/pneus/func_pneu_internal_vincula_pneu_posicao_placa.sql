CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_PNEU_INTERNAL_VINCULA_PNEU_POSICAO_PLACA(F_PLACA TEXT,
                                                                                    F_COD_PNEU BIGINT,
                                                                                    F_POSICAO INTEGER)
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_COD_VEICULO  BIGINT;
    V_COD_UNIDADE  BIGINT;
    V_COD_DIAGRAMA BIGINT := (SELECT COD_DIAGRAMA
                              FROM VEICULO_TIPO
                              WHERE CODIGO = (SELECT COD_TIPO FROM VEICULO_DATA WHERE PLACA = F_PLACA));
BEGIN
    SELECT V.CODIGO, V.COD_UNIDADE
    FROM VEICULO_DATA V
    WHERE V.PLACA = F_PLACA
    INTO V_COD_VEICULO, V_COD_UNIDADE;

    -- Valida se posição existe no diagrama.
    IF NOT EXISTS(SELECT VDPP.POSICAO_PROLOG
                  FROM VEICULO_DIAGRAMA_POSICAO_PROLOG VDPP
                  WHERE VDPP.COD_DIAGRAMA = (SELECT V.COD_DIAGRAMA FROM VEICULO_DATA V WHERE V.PLACA = F_PLACA))
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('A posição %s não existe no diagrama do veículo de placa %s',
                                                  F_POSICAO,
                                                  F_PLACA));
    END IF;

    -- Verifica se tem pneu aplicado nessa posição, caso tenha é prq não passou pelo método
    -- do Java de removePneusAplicados;
    IF EXISTS(SELECT VP.COD_PNEU FROM VEICULO_PNEU VP WHERE VP.POSICAO = F_POSICAO AND VP.COD_VEICULO = V_COD_VEICULO)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('Erro! O veículo %s já possui pneu aplicado na posição %s',
                                                  F_PLACA,
                                                  F_POSICAO));
    END IF;

    -- Deleta a posição.
    DELETE FROM VEICULO_PNEU WHERE POSICAO = F_POSICAO AND COD_VEICULO = V_COD_VEICULO;

    -- Não tem pneu aplicado a posição, então eu adiciono.
    INSERT INTO VEICULO_PNEU(COD_PNEU, COD_UNIDADE, POSICAO, COD_DIAGRAMA, COD_VEICULO)
    VALUES (F_COD_PNEU, V_COD_UNIDADE, F_POSICAO, V_COD_DIAGRAMA, V_COD_VEICULO);
END;
$$;