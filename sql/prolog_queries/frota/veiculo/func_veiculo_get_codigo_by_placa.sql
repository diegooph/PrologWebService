-- Sobre:
-- Retorna uma lista de códigos de veículo com base no código do colaborador e uma lista de placas.
--
-- Observação:
-- O código do colaborador é utilizado para verificar se a placa pertence à empresa de quem fez a requisição.
-- Caso uma placa recebida não exista na empresa, retorna uma mensagem de erro específica com a placa inconsistente.
--
-- Histórico:
-- 2020-07-07 -> Function criada (wvinim - PL-2621).
CREATE OR REPLACE FUNCTION FUNC_VEICULO_GET_CODIGO_BY_PLACA(F_COD_COLABORADOR BIGINT, F_PLACAS TEXT[])
    RETURNS BIGINT[]
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_COD_VEICULOS  BIGINT[];
    V_PLACA         TEXT;
    V_ERROR_MESSAGE TEXT := E'Erro! Não foi possível encontrar a placa "%s" em sua empresa.';
    V_COD_VEICULO   BIGINT;
BEGIN
    FOREACH V_PLACA IN ARRAY F_PLACAS
        LOOP
            SELECT V.CODIGO
            INTO V_COD_VEICULO
            FROM VEICULO V
                     JOIN COLABORADOR C ON C.CODIGO = F_COD_COLABORADOR AND C.COD_EMPRESA = V.COD_EMPRESA
            WHERE V.PLACA = V_PLACA;
            IF V_COD_VEICULO IS NULL
            THEN
                PERFORM THROW_GENERIC_ERROR(FORMAT(V_ERROR_MESSAGE, V_PLACA));
            END IF;
            V_COD_VEICULOS = ARRAY_APPEND(V_COD_VEICULOS, V_COD_VEICULO);
        END LOOP;
    RETURN V_COD_VEICULOS;
END;
$$;
