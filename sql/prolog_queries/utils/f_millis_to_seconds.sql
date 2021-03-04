-- Sobre:
--
-- Function auxiliar para encapsular a conversão de um valor em milissegundos para segundos.
-- A function não valida se o campo é positivo ou negativo, isso é de responsabilidade de quem utiliza a function.
--
-- Histórico:
-- 2020-05-19 -> Function criada (diogenesvanzella - PLI-144).
create or replace function f_millis_to_seconds(bigint) returns bigint
    language plpgsql
as
$$
begin
    -- Valor recebido em milissegundos é retornado em segundos.
    return query select $1 / 1000;
end;
$$;