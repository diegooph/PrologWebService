create or replace function func_pneu_relatorio_km_rodado_por_vida_linhas(f_cod_unidades bigint[])
    returns table
            (
                "UNIDADE ALOCADO"          text,
                "PNEU"                     text,
                "MARCA"                    text,
                "MODELO"                   text,
                "DIMENSÃO"                 text,
                "VIDA"                     integer,
                "VALOR VIDA"               numeric,
                "KM RODADO VIDA"           numeric,
                "CPK VIDA"                 text,
                "KM RODADO TODAS AS VIDAS" numeric
            )
    language sql
as
$$
select f.unidade_alocado       as unidade_alocado,
       f.cod_cliente_pneu      as cod_cliente_pneu,
       f.marca                 as marca,
       f.modelo                as modelo,
       f.dimensao              as dimensao,
       f.vida_pneu             as vida,
       f.valor_vida            as valor_vida,
       f.km_rodado_vida        as km_rodado_vida,
       f.valor_por_km_vida     as valor_por_km_vida,
       f.km_rodado_todas_vidas as km_rodado_todas_vidas
from func_pneu_relatorio_km_rodado_por_vida_base(f_cod_unidades) f;
$$;