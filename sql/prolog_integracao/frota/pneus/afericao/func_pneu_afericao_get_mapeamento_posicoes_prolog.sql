create or replace function
    integracao.func_pneu_afericao_get_mapeamento_posicoes_prolog(f_cod_empresa bigint,
                                                                 f_cod_auxiliar_tipo_veiculo text)
    returns table
            (
                posicao_prolog                    integer,
                nomenclatura_cliente              text,
                cod_auxiliar_nomenclatura_cliente text
            )
    language sql
as
$$
with cod_auxiliares as (
    select vt.codigo                                   as cod_tipo_veiculo,
           regexp_split_to_table(vt.cod_auxiliar, ',') as cod_auxiliar
    from veiculo_tipo vt
    where vt.cod_empresa = f_cod_empresa
)
select ppne.posicao_prolog                           as posicao_prolog,
       ppne.nomenclatura                             as nomenclatura_cliente,
       regexp_split_to_table(ppne.cod_auxiliar, ',') as cod_auxiliar_nomenclatura_cliente
from veiculo_tipo vt
         join pneu_posicao_nomenclatura_empresa ppne on vt.cod_diagrama = ppne.cod_diagrama
         join cod_auxiliares ca on ca.cod_tipo_veiculo = vt.codigo
where ca.cod_auxiliar = f_cod_auxiliar_tipo_veiculo
  and ppne.cod_empresa = f_cod_empresa;
$$;