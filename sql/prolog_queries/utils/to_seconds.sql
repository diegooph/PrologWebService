-- Sobre:
--
-- Converte um interval em um número de segundos.
-- atualizar para interval_to_seconts?
create or replace function to_seconds(t interval)
    returns bigint
    language sql
as
$$
select (extract(epoch from t))::bigint;
$$;