create or replace function func_pneu_relatorio_km_rodado_por_vida_base(f_cod_unidades bigint[])
    returns table
            (
                unidade_alocado       text,
                cod_pneu              bigint,
                cod_cliente_pneu      text,
                marca                 text,
                modelo                text,
                dimensao              text,
                vida_pneu             integer,
                valor_vida            numeric,
                km_rodado_vida        numeric,
                valor_por_km_vida     text,
                km_rodado_todas_vidas numeric
            )
    language sql
as
$$
with pneu_valor_todas_vidas as (
    select distinct on (p.codigo, vida_pneu) p.cod_unidade                                         as cod_unidade_pneu,
                                             p.codigo                                              as cod_pneu,
                                             p.codigo_cliente                                      as cod_cliente_pneu,
                                             p.cod_dimensao                                        as cod_dimensao,
                                             p.cod_modelo                                          as cod_modelo_pneu,
                                             coalesce(pvv.cod_modelo_banda, p.cod_modelo_banda)    as cod_modelo_banda,
                                             -- O generate series só serve pra gerar a vida 1. Então, se for a vida 1,
                                             -- o dado da vida vem dele, se não, vem da pvv.
                                             f_if(g.vida_pneu = 1, g.vida_pneu, pvv.vida::integer) as vida_pneu,
                                             -- A pvv só tem acima da primeira vida. O generate series só serve pra
                                             -- gerar a vida 1. Então, se for a vida 1, o dado da vida vem da pneu
                                             -- (a vida 1 não existe na pvv), se não, vem da pvv.
                                             f_if(g.vida_pneu = 1, round(p.valor::numeric, 2),
                                                  round(pvv.valor::numeric, 2))                    as valor_vida
    from pneu p
             left join pneu_valor_vida pvv
                       on pvv.cod_pneu = p.codigo
        -- O generate series só serve pra gerar a vida 1, porque a pvv não tem ela. Sendo assim, o valor final
        -- para gerar o generate series será sempre a menor vida existente na pvv.
        -- Para o valor inicial, é buscado na propria pvv a menor vida que seja fonte_cadastro. Se encontrar, vai
        -- ser ela. Se não, se for movimento, será ela - 1 (porque a fonte_cadastro guarda a vida que iniciou, a
        -- fonte_movimento guarda para que vida irá, diminuindo 1, será a que iniciou).
             cross join generate_series(
            f_if((select pnvv.fonte_servico_realizado
                  from pneu_valor_vida pnvv
                  where pnvv.cod_pneu = p.codigo
                  order by vida
                  limit 1) is not null,
                 f_if(
                             (select pnvv.fonte_servico_realizado
                              from pneu_valor_vida pnvv
                              where pnvv.cod_pneu = p.codigo
                              order by vida
                              limit 1) = 'FONTE_CADASTRO',
                             (select min(vida) from pneu_valor_vida pnvv where pnvv.cod_pneu = p.codigo)::integer,
                             (select min(vida) from pneu_valor_vida pnvv where pnvv.cod_pneu = p.codigo) - 1),
                 1::integer),
            (f_if((select pnvv.fonte_servico_realizado
                   from pneu_valor_vida pnvv
                   where pnvv.cod_pneu = p.codigo
                   order by vida
                   limit 1) is not null,
                  (select min(pnvv.vida)
                   from pneu_valor_vida pnvv
                   where pnvv.cod_pneu = p.codigo),
                  1::smallint))
        ) g(vida_pneu)
    where p.cod_unidade = any (f_cod_unidades)
)
select u.nome                                                    as unidade_alocado,
       pvtv.cod_pneu                                             as cod_pneu,
       pvtv.cod_cliente_pneu                                     as cod_cliente_pneu,
       f_if(pvtv.vida_pneu = 1, mrp.nome, mrb.nome)              as marca,
       f_if(pvtv.vida_pneu = 1, mp.nome, mb.nome)                as modelo,
       func_pneu_format_dimensao(dp.largura, dp.largura, dp.aro) as dimensao,
       pvtv.vida_pneu                                            as vida_pneu,
       pvtv.valor_vida                                           as valor_vida,
       coalesce(vp.km_rodado_vida, 0)                            as km_rodado_vida,
       -- O nullif() nesse case serve para impedir erro de divisão por zero.
       coalesce(
               round((case
                          when vp.vida_pneu = 1
                              then pvtv.valor_vida / nullif(vp.km_rodado_vida, 0)
                          else
                              coalesce(pvtv.valor_vida, 0) / nullif(vp.km_rodado_vida, 0)
                   end)::numeric, 3)::text, '-')                 as valor_por_km_vida,
       coalesce(vp.total_km_rodado_todas_vidas, 0)               as km_rodado_todas_vidas
from pneu_valor_todas_vidas pvtv
         inner join modelo_pneu mp
                    on mp.codigo = pvtv.cod_modelo_pneu
         inner join marca_pneu mrp
                    on mrp.codigo = mp.cod_marca
         inner join dimensao_pneu dp
                    on dp.codigo = pvtv.cod_dimensao
         inner join unidade u
                    on u.codigo = pvtv.cod_unidade_pneu
         left join modelo_banda mb
                   on mb.codigo = pvtv.cod_modelo_banda
         left join marca_banda mrb
                   on mrb.codigo = mb.cod_marca
         left join view_pneu_km_rodado_total vp
                   on vp.cod_pneu = pvtv.cod_pneu and vp.vida_pneu = pvtv.vida_pneu
order by u.codigo, pvtv.cod_cliente_pneu, pvtv.cod_modelo_pneu, pvtv.vida_pneu;
$$;