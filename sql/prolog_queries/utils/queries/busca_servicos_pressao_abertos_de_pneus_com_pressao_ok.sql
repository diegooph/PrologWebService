with pneus_pressao_ok as (
    select pd.codigo as codigo
    from pneu_data pd
             join pneu_restricao_unidade pru on pd.cod_unidade = pru.cod_unidade
    where ((pd.pressao_atual < pd.pressao_recomendada * (1 + pru.tolerancia_calibragem))
        and (pd.pressao_atual > pd.pressao_recomendada * (1 - pru.tolerancia_calibragem)))
      and pd.deletado = false
)

select *
from pneus_pressao_ok ppo
where ppo.codigo in (select amd.cod_pneu
                     from afericao_manutencao_data amd
                     where amd.data_hora_resolucao is null
                       and amd.deletado = false
                       and (amd.tipo_servico = 'inspecao'
                         or amd.tipo_servico = 'calibragem'));