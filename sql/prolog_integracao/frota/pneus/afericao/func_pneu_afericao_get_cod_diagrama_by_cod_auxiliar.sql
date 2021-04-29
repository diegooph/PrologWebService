create or replace function
    integracao.func_pneu_afericao_get_cod_diagrama_by_cod_auxiliar(f_cod_empresa bigint,
                                                                   f_cod_auxiliar_tipo_veiculo text)
    returns table
            (
                cod_diagrama smallint
            )
    language sql
as
$$
select vt.cod_diagrama as cod_diagrama
from (select vt.cod_diagrama                             as cod_diagrama,
             regexp_split_to_table(vt.cod_auxiliar, ',') as cod_auxiliar
      from veiculo_tipo vt
      where vt.cod_empresa = f_cod_empresa) as vt
where vt.cod_auxiliar = f_cod_auxiliar_tipo_veiculo;
$$;