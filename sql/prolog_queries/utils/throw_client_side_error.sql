-- Sobre:
-- Esta função permite estourar um erro que indica que algum problema ocorreu com as informações enviadas pelo cliente
-- (do lado do cliente).
--
-- Histórico:
-- 2020-10-20 -> Function criada (gustavocnp95 - PL-2939).
create or replace function throw_client_side_error(f_message text) returns void
    language plpgsql
as
$$
begin
  raise exception '%', f_message
  using errcode = (select sql_error_code
                   from prolog_sql_error_code
                   where prolog_error_code = 'CLIENT_SIDE_ERROR');
end;
$$;