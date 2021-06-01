create or replace view view_pneu_analise_vidas as
with dados_afericao as (
    select a.codigo                                                                            as cod_afericao,
           a.cod_unidade                                                                       as cod_unidade_afericao,
           a.data_hora                                                                         as data_hora_afericao,
           a.tipo_processo_coleta                                                              as tipo_processo_coleta_afericao,
           av.cod_pneu                                                                         as cod_pneu,
           av.vida_momento_afericao                                                            as vida_momento_afericao,
           av.altura_sulco_central_interno                                                     as altura_sulco_central_interno,
           av.altura_sulco_central_externo                                                     as altura_sulco_central_externo,
           av.altura_sulco_externo                                                             as altura_sulco_externo,
           av.altura_sulco_interno                                                             as altura_sulco_interno,
           row_number()
           over (partition by av.cod_pneu, av.vida_momento_afericao order by a.data_hora)      as row_number_asc,
           row_number()
           over (partition by av.cod_pneu, av.vida_momento_afericao order by a.data_hora desc) as row_number_desc
    from afericao a
             join afericao_valores av on a.codigo = av.cod_afericao
),
     primeira_afericao as (
         select da.cod_pneu,
                da.vida_momento_afericao,
                da.cod_afericao,
                da.cod_unidade_afericao,
                da.data_hora_afericao
         from dados_afericao da
         where da.row_number_asc = 1
     ),
     ultima_afericao as (
         select da.cod_pneu,
                da.vida_momento_afericao,
                da.cod_afericao,
                da.cod_unidade_afericao,
                da.data_hora_afericao
         from dados_afericao da
         where da.row_number_desc = 1
     ),
     analises_afericoes as (
         select da.cod_pneu                            as cod_pneu,
                da.vida_momento_afericao               as vida_analisada_pneu,
                count(da.cod_pneu)                     as quantidade_afericoes_pneu_vida,
                max(greatest(da.altura_sulco_externo,
                             da.altura_sulco_central_externo,
                             da.altura_sulco_central_interno,
                             da.altura_sulco_interno)) as maior_sulco_aferido_vida,
                min(least(da.altura_sulco_externo,
                          da.altura_sulco_central_externo,
                          da.altura_sulco_central_interno,
                          da.altura_sulco_interno))    as menor_sulco_aferido_vida
         from dados_afericao da
         group by da.cod_pneu, da.vida_momento_afericao
     )
select p.codigo                                                                            as cod_pneu,
       p.status                                                                            as status,
       p.valor                                                                             as valor_pneu,
       coalesce(pvv.valor, 0::real)                                                        as valor_banda,
       pa.data_hora_afericao                                                               as data_hora_primeira_afericao,
       pa.cod_afericao                                                                     as cod_primeira_afericao,
       pa.cod_unidade_afericao                                                             as cod_unidade_primeira_afericao,
       ua.data_hora_afericao                                                               as data_hora_ultima_afericao,
       ua.cod_afericao                                                                     as cod_ultima_afericao,
       ua.cod_unidade_afericao                                                             as cod_unidade_ultima_afericao,
       aa.vida_analisada_pneu                                                              as vida_analisada_pneu,
       aa.quantidade_afericoes_pneu_vida                                                   as quantidade_afericoes_pneu_vida,
       aa.maior_sulco_aferido_vida                                                         as maior_sulco_aferido_vida,
       aa.menor_sulco_aferido_vida                                                         as menor_sulco_aferido_vida,
       (aa.maior_sulco_aferido_vida - aa.menor_sulco_aferido_vida)                         as sulco_gasto,
       (date_part('days'::text, (ua.data_hora_afericao - pa.data_hora_afericao)))::integer as total_dias_ativo,
       km_rodado_pneu.km_rodado_vida                                                       as total_km_rodado_vida,
       func_pneu_calcula_sulco_restante(p.vida_atual,
                                        p.vida_total,
                                        p.altura_sulco_externo,
                                        p.altura_sulco_central_externo,
                                        p.altura_sulco_central_interno,
                                        p.altura_sulco_interno,
                                        pru.sulco_minimo_recapagem,
                                        pru.sulco_minimo_descarte)                         as sulco_restante,
       case
           when ((date_part('days'::text, (ua.data_hora_afericao - pa.data_hora_afericao)) > 0::double precision) and
                 ((aa.maior_sulco_aferido_vida - aa.menor_sulco_aferido_vida) > 0::double precision)) then (
                   (km_rodado_pneu.km_rodado_vida)::double precision /
                   (aa.maior_sulco_aferido_vida - aa.menor_sulco_aferido_vida))
           else 0::double precision
           end                                                                             as km_por_mm_vida,
       case
           when (km_rodado_pneu.km_rodado_vida = 0::numeric) then 0::double precision
           else
               case
                   when (km_rodado_pneu.vida_pneu = 1)
                       then (p.valor / (km_rodado_pneu.km_rodado_vida)::double precision)
                   else (coalesce(pvv.valor, 0::real) / (km_rodado_pneu.km_rodado_vida)::double precision)
                   end
           end                                                                             as valor_por_km_vida
from analises_afericoes aa
         join primeira_afericao pa
              on pa.cod_pneu = aa.cod_pneu
                  and pa.vida_momento_afericao = aa.vida_analisada_pneu
         join ultima_afericao ua
              on ua.cod_pneu = aa.cod_pneu
                  and ua.vida_momento_afericao = aa.vida_analisada_pneu
         join pneu p on aa.cod_pneu = p.codigo
         join pneu_restricao_unidade pru on p.cod_unidade = pru.cod_unidade
         join view_pneu_km_rodado_vida km_rodado_pneu
              on km_rodado_pneu.cod_pneu = aa.cod_pneu
                  and km_rodado_pneu.vida_pneu = aa.vida_analisada_pneu
         left join pneu_valor_vida pvv on p.codigo = pvv.cod_pneu
order by aa.cod_pneu, aa.vida_analisada_pneu;