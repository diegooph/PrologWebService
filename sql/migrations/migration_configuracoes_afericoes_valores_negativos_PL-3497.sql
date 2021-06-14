alter table pneu_restricao_unidade
    add constraint check_periodos_valores_corretos check (periodo_afericao_sulco >= 1 and periodo_afericao_pressao >= 1);