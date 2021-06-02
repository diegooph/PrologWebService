create or replace view view_analise_pneus as
select p.cod_unidade                                                                  as cod_unidade_alocado,
       u.nome                                                                         as nome_unidade_alocado,
       p.codigo                                                                       as cod_pneu,
       p.codigo_cliente                                                               as cod_cliente_pneu,
       p.status                                                                       as status_pneu,
       map.nome                                                                       as nome_marca,
       mp.nome                                                                        as nome_modelo,
       dp.largura || '/' || dp.altura || ' R' || dp.aro                               as medidas,
       dados.qt_afericoes                                                             as qtd_afericoes,
       to_char(dados.primeira_afericao::timestamp with time zone, 'DD/MM/YYYY'::text) as data_primeira_afericao,
       to_char(dados.ultima_afericao::timestamp with time zone, 'DD/MM/YYYY'::text)   as data_ultima_afericao,
       dados.total_dias                                                               as dias_ativo,
       round(case
                 when dados.total_dias > 0 then dados.total_km / dados.total_dias::numeric
                 else null::numeric
           end)                                                                       as media_km_por_dia,
       p.altura_sulco_interno,
       p.altura_sulco_central_interno,
       p.altura_sulco_central_externo,
       p.altura_sulco_externo,
       round(dados.maior_sulco::numeric, 2)                                           as maior_sulco_vida,
       round(dados.menor_sulco::numeric, 2)                                           as menor_sulco_vida,
       round(dados.sulco_gasto::numeric, 2)                                           as milimetros_gastos,
       round(dados.km_por_mm::numeric, 2)                                             as kms_por_milimetro,
       round((dados.km_por_mm * dados.sulco_restante)::numeric)                       as kms_a_percorrer,
       trunc(
               case
                   when dados.total_km > 0::numeric and dados.total_dias > 0 and
                        (dados.total_km / dados.total_dias::numeric) > 0::numeric then
                               dados.km_por_mm * dados.sulco_restante /
                               (dados.total_km / dados.total_dias::numeric)::double precision
                   else 0::double precision
                   end)                                                               as dias_restantes,
       case
           when dados.total_km > 0::numeric and dados.total_dias > 0 and
                (dados.total_km / dados.total_dias::numeric) > 0::numeric
               then (dados.km_por_mm * dados.sulco_restante /
                     (dados.total_km / dados.total_dias::numeric)::double precision)::integer +
                    current_date
           else null::date
           end                                                                        as previsao_troca
from pneu p
         join (select av.cod_pneu,
                      av.cod_unidade,
                      count(av.altura_sulco_central_interno)                                     as qt_afericoes,
                      min(timezone(tz_unidade(a.cod_unidade), a.data_hora))::date                as primeira_afericao,
                      max(timezone(tz_unidade(a.cod_unidade), a.data_hora))::date                as ultima_afericao,
                      max(timezone(tz_unidade(a.cod_unidade), a.data_hora))::date -
                      min(timezone(tz_unidade(a.cod_unidade), a.data_hora))::date                as total_dias,
                      max(total_km.total_km)                                                     as total_km,
                      max(greatest(av.altura_sulco_interno, av.altura_sulco_central_interno,
                                   av.altura_sulco_central_externo, av.altura_sulco_externo))    as maior_sulco,
                      min(least(av.altura_sulco_interno, av.altura_sulco_central_interno,
                                av.altura_sulco_central_externo, av.altura_sulco_externo))       as menor_sulco,
                      max(greatest(av.altura_sulco_interno, av.altura_sulco_central_interno,
                                   av.altura_sulco_central_externo, av.altura_sulco_externo))
                          - min(least(av.altura_sulco_interno, av.altura_sulco_central_interno,
                                      av.altura_sulco_central_externo, av.altura_sulco_externo)) as sulco_gasto,
                      case
                          when
                                  case
                                      when p_1.vida_atual = p_1.vida_total then min(least(av.altura_sulco_interno,
                                                                                          av.altura_sulco_central_interno,
                                                                                          av.altura_sulco_central_externo,
                                                                                          av.altura_sulco_externo)) -
                                                                                pru.sulco_minimo_descarte
                                      when p_1.vida_atual < p_1.vida_total then min(least(av.altura_sulco_interno,
                                                                                          av.altura_sulco_central_interno,
                                                                                          av.altura_sulco_central_externo,
                                                                                          av.altura_sulco_externo)) -
                                                                                pru.sulco_minimo_recapagem
                                      else null::real
                                      end < 0::double precision then 0::real
                          else
                              case
                                  when p_1.vida_atual = p_1.vida_total then min(least(av.altura_sulco_interno,
                                                                                      av.altura_sulco_central_interno,
                                                                                      av.altura_sulco_central_externo,
                                                                                      av.altura_sulco_externo)) -
                                                                            pru.sulco_minimo_descarte
                                  when p_1.vida_atual < p_1.vida_total then min(least(av.altura_sulco_interno,
                                                                                      av.altura_sulco_central_interno,
                                                                                      av.altura_sulco_central_externo,
                                                                                      av.altura_sulco_externo)) -
                                                                            pru.sulco_minimo_recapagem
                                  else null::real
                                  end
                          end                                                                    as sulco_restante,
                      case
                          when (max(timezone(tz_unidade(a.cod_unidade), a.data_hora))::date -
                                min(timezone(tz_unidade(a.cod_unidade), a.data_hora))::date) > 0 then
                                      max(total_km.total_km)::double precision / max(
                                          greatest(av.altura_sulco_interno, av.altura_sulco_central_interno,
                                                   av.altura_sulco_central_externo, av.altura_sulco_externo)) - min(
                                              least(av.altura_sulco_interno, av.altura_sulco_central_interno,
                                                    av.altura_sulco_central_externo, av.altura_sulco_externo))
                          else 0::double precision
                          end                                                                    as km_por_mm
               from afericao_valores av
                        join afericao a on a.codigo = av.cod_afericao
                        join pneu p_1 on p_1.codigo::text = av.cod_pneu::text and p_1.status::text = 'EM_USO'::text
                        join pneu_restricao_unidade pru on pru.cod_unidade = av.cod_unidade
                        join (select total_km_rodado.cod_pneu,
                                     total_km_rodado.cod_unidade,
                                     sum(total_km_rodado.km_rodado) as total_km
                              from (select av_1.cod_pneu,
                                           av_1.cod_unidade,
                                           max(a_1.km_veiculo) - min(a_1.km_veiculo) as km_rodado
                                    from afericao_valores av_1
                                             join afericao a_1 on a_1.codigo = av_1.cod_afericao
                                    group by av_1.cod_pneu, av_1.cod_unidade, a_1.cod_veiculo) total_km_rodado
                              group by total_km_rodado.cod_pneu, total_km_rodado.cod_unidade) total_km
                             on total_km.cod_pneu = av.cod_pneu and total_km.cod_unidade = av.cod_unidade
               group by av.cod_pneu, av.cod_unidade, p_1.vida_atual, p_1.vida_total, pru.sulco_minimo_descarte,
                        pru.sulco_minimo_recapagem) dados on dados.cod_pneu = p.codigo
         join dimensao_pneu dp on dp.codigo = p.cod_dimensao
         join unidade u on u.codigo = p.cod_unidade
         join modelo_pneu mp on mp.codigo = p.cod_modelo and mp.cod_empresa = u.cod_empresa
         join marca_pneu map on map.codigo = mp.cod_marca;