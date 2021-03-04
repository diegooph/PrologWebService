-- Corrige retorno da function
create or replace function integracao.func_pneu_afericao_get_cod_auxiliar_unidade_prolog(f_cod_unidades bigint[])
    returns table
            (
                cod_unidade_prolog bigint,
                cod_auxiliar       text
            )
    language sql
as
$$
select codigo       as cod_unidade_prolog,
       cod_auxiliar as cod_auxiliar
from public.unidade
where codigo = any (f_cod_unidades)
  and cod_auxiliar is not null;
$$;