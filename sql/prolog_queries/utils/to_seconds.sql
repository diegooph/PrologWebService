-- Sobre:
--
-- Converte um interval em um número de segundos.
--
-- Histórico:
-- 2020-10-26 -> Arquivo criado (luizfp - PS-1300).
-- 2020-10-26 -> Corrige problema de número de segundos causar overflow em um INTEGER (luizfp - PS-1300).
create or replace function to_seconds(t interval)
    returns bigint
    language sql
as
$$
select (extract(epoch from t))::bigint;
$$;