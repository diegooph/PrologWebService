-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Verifica se a placa recebida está cadastrada no sistema.
--
-- Precondições:
--
-- Histórico:
-- 2019-07-31-> Function criada (natanrotta - PL-2131).
--
CREATE OR REPLACE FUNCTION FUNC_GARANTE_PLACA_CADASTRADA(F_PLACA VARCHAR(7))
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    IF NOT EXISTS(SELECT V.PLACA
                  FROM VEICULO_DATA V
                  WHERE V.PLACA = F_PLACA)
    THEN
        RAISE EXCEPTION 'A PLACA: % NÃO ESTÁ CADASTRADA', F_PLACA;
    END IF;
END;
$$;