with estruturas as (
    select vt.codigo                                   as cod_tipo_veiculo,
           regexp_split_to_table(vt.cod_auxiliar, ',') as cod_auxiliar
    from veiculo_tipo vt
    where vt.cod_auxiliar is not null
      and cod_empresa = 15
)
select count(e.cod_auxiliar)
from estruturas e
group by e.cod_auxiliar;