-- Sobre:
--
-- Esta function foi criada para a integração de aferições. Retorna o codigo da unidade prolog e também o código
-- auxiliar mapeado. Caso o codigo do Prolog possuir mais de um código auxiliar, a function retornará mais de uma linha
-- para o código do Prolog usando a função 'regexp_split_to_table'.
-- A function não irá retornar as unidades que não possuirem nenhum codigo auxiliar mapeado.
--
-- Histórico:
-- 2020-06-15 -> Function criada (diogenesvanzella - PLI-165).
-- 2020-07-28 -> Corrige retorno da function (diogenesvanzella - PLI-190).
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