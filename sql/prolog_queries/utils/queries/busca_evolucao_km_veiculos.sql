WITH PLACA AS (
    SELECT ARRAY['QHW1797', 'FGK4051'] AS PLACA
),

    DADOS AS (
    (select
       'MOVIMENTAÇÃO'                                       as processo,
       mp.data_hora at time zone tz_unidade(mp.cod_unidade) as data_hora,
       coalesce(o.placa, d.placa)                           as placa,
       coalesce(o.km_veiculo, d.km_veiculo)                 as km_coletado
     from movimentacao_processo mp
       join movimentacao m on mp.codigo = m.cod_movimentacao_processo and mp.cod_unidade = m.cod_unidade
       join movimentacao_destino d on m.codigo = d.cod_movimentacao
       join movimentacao_origem o on m.codigo = o.cod_movimentacao
     where (select coalesce(o.placa::text, d.placa::text) = any (p.placa) from placa p)
     group by mp.cod_unidade, mp.codigo, o.placa, d.placa, o.km_veiculo, d.km_veiculo
     order by mp.data_hora asc)
    UNION ALL
    (select
       'CHECKLIST'                                        as processo,
       c.data_hora at time zone tz_unidade(c.cod_unidade) as data_hora,
       c.placa_veiculo                                    as placa,
       c.km_veiculo                                       as km_coletado
     from checklist c
     where (select c.placa_veiculo::text = any (p.placa) from placa p)
     order by c.data_hora asc)
    UNION ALL
    (select
       'AFERIÇÃO'                                         as processo,
       a.data_hora at time zone tz_unidade(a.cod_unidade) as data_hora,
       a.placa_veiculo                                    as placa,
       a.km_veiculo                                       as km_coletado
     from afericao a
     where (select a.placa_veiculo::text = any (p.placa) from placa p)
     order by a.data_hora asc)
    UNION ALL
    (select
       'FECHAMENTO SERVIÇO PNEU'                          as processo,
       a.data_hora at time zone tz_unidade(a.cod_unidade) as data_hora,
       a.placa_veiculo                                    as placa,
       am.km_momento_conserto                             as km_coletado
     from afericao a
       join afericao_manutencao am on am.cod_afericao = a.codigo
     where (select a.placa_veiculo::text = any (p.placa) from placa p) and data_hora_resolucao is not null
     order by a.data_hora asc)
    UNION ALL
    (select
       'FECHAMENTO ITEM CHECKLIST'                        as processo,
       c.data_hora at time zone tz_unidade(c.cod_unidade) as data_hora,
       c.placa_veiculo                                    as placa,
       cosi.km                                            as km_coletado
     from checklist c
       join checklist_ordem_servico cos on cos.cod_checklist = c.codigo
       join checklist_ordem_servico_itens cosi on cosi.cod_os = cos.codigo and cosi.cod_unidade = cos.cod_unidade
     where (select c.placa_veiculo::text = any (p.placa) from placa p) and cosi.status_resolucao = 'R'
     order by c.data_hora asc)
    UNION ALL
    (select
       'TRANSFERÊNCIA DE VEÍCULOS'                                                               as processo,
       vtp.data_hora_transferencia_processo at time zone tz_unidade(vtp.cod_unidade_colaborador) as data_hora,
       v.placa                                                                                   as placa,
       vti.km_veiculo_momento_transferencia                                                      as km_coletado
     from veiculo_transferencia_processo vtp
       join veiculo_transferencia_informacoes vti on vtp.codigo = vti.cod_processo_transferencia
       join veiculo v on vti.cod_veiculo = v.codigo
     where (select v.placa::text = any(p.placa) from placa p))
  )

select
  d.*,
  v.km                 as km_atual,
  v.km - d.km_coletado as diferenca_atual_coletado
from dados d
join veiculo v on v.placa = d.placa
order by d.placa asc, d.data_hora asc;