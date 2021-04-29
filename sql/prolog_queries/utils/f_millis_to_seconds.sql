create or replace function f_millis_to_seconds(bigint) returns bigint
    language plpgsql
as
$$
begin
    -- Valor recebido em milissegundos é retornado em segundos.
    return query select $1 / 1000;
end;
$$;