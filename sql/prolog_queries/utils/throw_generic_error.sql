-- Sobre:
-- Esta função permite estourar um erro que indica um erro genérico, sem especificações.
--
-- Histórico:
create or replace function public.throw_generic_error(f_message text)
  returns void
language plpgsql
as $$
begin
  raise exception '%', f_message
  using errcode = (select sql_error_code
                   from prolog_sql_error_code
                   where prolog_error_code = 'GENERIC_ERROR');
end;
$$;