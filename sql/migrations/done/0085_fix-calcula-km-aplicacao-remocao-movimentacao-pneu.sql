create or replace function func_pneu_calcula_km_aplicacao_remocao_pneu(f_cod_pneu bigint,
                                                                       f_vida_pneu integer)
    returns numeric
    language sql
as
$$
with movimentacoes_vida_pneu as (
    select mp.data_hora    as data_hora_movimentacao,
           mo.tipo_origem  as tipo_origem,
           md.tipo_destino as tipo_destino,
           md.placa        as placa_destino,
           md.km_veiculo   as km_veiculo_destino,
           mo.placa        as placa_origem,
           mo.km_veiculo   as km_veiculo_origem
    from movimentacao_processo mp
             join movimentacao m on mp.codigo = m.cod_movimentacao_processo
             join movimentacao_origem mo on m.codigo = mo.cod_movimentacao
             join movimentacao_destino md on m.codigo = md.cod_movimentacao
    where (mo.tipo_origem = 'EM_USO' or md.tipo_destino = 'EM_USO')
      and m.cod_pneu = f_cod_pneu
      and m.vida = f_vida_pneu
),

     afericoes_vida_pneu as (
         select a.data_hora     as data_hora_afericao,
                a.placa_veiculo as placa_afericao,
                a.km_veiculo    as km_veiculo_afericao
         from afericao a
                  join afericao_valores av on av.cod_afericao = a.codigo
         where a.tipo_processo_coleta = 'PLACA'
           and av.cod_pneu = f_cod_pneu
           and av.vida_momento_afericao = f_vida_pneu
     ),

     kms_primeira_aplicacao_ate_primeira_afericao as (
         select sum((select avp.km_veiculo_afericao
                     from afericoes_vida_pneu avp
                     where avp.placa_afericao = pvp.placa_destino
                       and avp.data_hora_afericao > pvp.data_hora_movimentacao
                     order by avp.data_hora_afericao
                     limit 1) - pvp.km_veiculo_destino) as km_percorrido
         from movimentacoes_vida_pneu pvp
              -- Saiu de qualquer origem e foi aplicado no veículo.
         where pvp.tipo_origem <> 'EM_USO'
           and pvp.tipo_destino = 'EM_USO'
     ),

     kms_ultima_afericao_ate_remocao as (
         select sum(pvp.km_veiculo_origem - (select avp.km_veiculo_afericao
                                             from afericoes_vida_pneu avp
                                             where avp.placa_afericao = pvp.placa_origem
                                               and avp.data_hora_afericao < pvp.data_hora_movimentacao
                                             order by avp.data_hora_afericao desc
                                             limit 1)) as km_percorrido
         from movimentacoes_vida_pneu pvp
              -- Saiu do veículo e foi movido para qualquer outro destino que não veículo.
         where pvp.tipo_origem = 'EM_USO'
           and pvp.tipo_destino <> 'EM_USO'
     )

select coalesce((select aplicacao.km_percorrido
                 from kms_primeira_aplicacao_ate_primeira_afericao aplicacao), 0)
           +
       coalesce((select remocao.km_percorrido
                 from kms_ultima_afericao_ate_remocao remocao), 0) as km_total_aplicacao_remocao;
$$;

create or replace view view_pneu_km_rodado_vida as
SELECT q.cod_pneu,
       q.vida_pneu,
       (coalesce(sum(q.km_rodado), 0)
           +
        (select func_pneu_calcula_km_aplicacao_remocao_pneu(q.cod_pneu, q.vida_pneu))) AS km_rodado_vida
FROM (SELECT av.cod_pneu,
             av.vida_momento_afericao                AS vida_pneu,
             (max(a.km_veiculo) - min(a.km_veiculo)) AS km_rodado
      FROM (afericao_valores av
               JOIN afericao a ON ((a.codigo = av.cod_afericao)))
      WHERE ((a.tipo_processo_coleta)::text = 'PLACA'::text)
      GROUP BY av.cod_pneu, a.placa_veiculo, av.vida_momento_afericao
      ORDER BY av.cod_pneu) q
GROUP BY q.cod_pneu, q.vida_pneu
ORDER BY q.cod_pneu, q.vida_pneu;