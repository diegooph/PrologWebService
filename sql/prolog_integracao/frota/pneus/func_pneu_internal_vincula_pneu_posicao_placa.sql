-- Sobre:
-- Essa function é responsável por vincular um pneu ao veículo.
--
-- Precondição
-- A posição precisa existir no veículo.
--
-- Histórico:
-- 2020-04-28 -> Function criada (natanrotta - PLI-102).
CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_PNEU_INTERNAL_VINCULA_PNEU_POSICAO_PLACA(F_PLACA TEXT,
                                                                                    F_COD_PNEU BIGINT,
                                                                                    F_POSICAO INTEGER)
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_COD_UNIDADE  BIGINT := (SELECT COD_UNIDADE
                              FROM VEICULO_DATA
                              WHERE PLACA = F_PLACA);
    V_COD_DIAGRAMA BIGINT := (SELECT COD_DIAGRAMA
                              FROM VEICULO_TIPO
                              WHERE CODIGO = (SELECT COD_TIPO FROM VEICULO_DATA WHERE PLACA = F_PLACA));
BEGIN
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
    IF EXISTS(SELECT VP.COD_PNEU FROM VEICULO_PNEU VP WHERE VP.POSICAO = F_POSICAO AND VP.PLACA = F_PLACA)
    THEN
        PERFORM PUBLIC.THROW_GENERIC_ERROR(FORMAT('Erro! O veículo %s já possui pneu aplicado na posição %s',
                                                  F_PLACA,
                                                  F_POSICAO));
    END IF;

    -- Deleta a posição.
    DELETE FROM VEICULO_PNEU WHERE POSICAO = F_POSICAO AND PLACA = F_PLACA;

    -- Não tem pneu aplicado a posição, então eu adiciono.
    INSERT INTO VEICULO_PNEU(PLACA, COD_PNEU, COD_UNIDADE, POSICAO, COD_DIAGRAMA)
    VALUES (F_PLACA, F_COD_PNEU, V_COD_UNIDADE, F_POSICAO, V_COD_DIAGRAMA);
END;
$$;