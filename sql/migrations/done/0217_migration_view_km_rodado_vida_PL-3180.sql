create or replace view view_pneu_km_rodado_vida as
select p.codigo                                                                      as cod_pneu,
       coalesce(q.vida_pneu, p.vida_atual)                                           as vida_pneu,
       (coalesce(sum(q.km_rodado), 0)
           +
        (select func_pneu_calcula_km_aplicacao_remocao_pneu(p.codigo, q.vida_pneu))) as km_rodado_vida
from (select av.cod_pneu,
             av.vida_momento_afericao                as vida_pneu,
             (max(a.km_veiculo) - min(a.km_veiculo)) as km_rodado
      from (afericao_valores av
               join afericao a on ((a.codigo = av.cod_afericao)))
      where ((a.tipo_processo_coleta)::text = 'PLACA'::text)
      group by av.cod_pneu, a.placa_veiculo, av.vida_momento_afericao
      order by av.cod_pneu) q
         right join pneu_data p on p.codigo = q.cod_pneu
group by p.codigo, q.vida_pneu
order by p.codigo, q.vida_pneu;