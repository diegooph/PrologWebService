alter table afericao_manutencao_data add column fechado_automaticamente_afericao boolean not null default false;

alter table afericao_manutencao_data add column cod_afericao_fechamento_automatico bigint
    constraint fk_afericao_manutencao_afericao_fechamento_automatico references afericao_data on delete cascade;

alter table afericao_manutencao_data drop constraint check_estados_servicos;

alter table afericao_manutencao_data add constraint check_estados_servicos check(
  case
  when (tipo_servico::text = 'movimentacao'::text)
    then ((row(
           data_hora_resolucao,
           cpf_mecanico,
           psi_apos_conserto,
           km_momento_conserto,
           cod_pneu_inserido,
           cod_processo_movimentacao,
           tempo_realizacao_millis) is null
           and
           fechado_automaticamente_movimentacao is false
           and
           fechado_automaticamente_integracao is false
           and
           fechado_automaticamente_afericao is false) -- verificamos se está pendente.
          or
          (row(
           data_hora_resolucao,
           cpf_mecanico,
           psi_apos_conserto,
           km_momento_conserto,
           cod_pneu_inserido,
           cod_processo_movimentacao,
           tempo_realizacao_millis) is not null
           and
           fechado_automaticamente_movimentacao is false
           and
           fechado_automaticamente_integracao is false
           and
           fechado_automaticamente_afericao is false) -- verificamos se está resolvido por mecânico
          or
          (row(
           data_hora_resolucao,
           cod_processo_movimentacao) is not null
           and
           fechado_automaticamente_movimentacao is true
           and
           fechado_automaticamente_integracao is false
           and
           fechado_automaticamente_afericao is false) -- verificamos se está resolvido automaticamente
          or
          (data_hora_resolucao is not null
           and
           cod_processo_movimentacao is null
           and
           fechado_automaticamente_movimentacao is false
           and
           fechado_automaticamente_integracao is true
           and
           fechado_automaticamente_afericao is false) -- verificamos se está resolvido por integração
          or
          (row(
           data_hora_resolucao,
           cod_afericao_fechamento_automatico) is not null
           and
           cod_processo_movimentacao is null
           and
           fechado_automaticamente_movimentacao is false
           and
           fechado_automaticamente_integracao is false
           and
           fechado_automaticamente_afericao is true)) -- verificamos se está resolvido por aferição
  when (tipo_servico::text = 'calibragem'::text)
    then ((row(
           data_hora_resolucao,
           cpf_mecanico,
           psi_apos_conserto,
           km_momento_conserto,
           cod_pneu_inserido,
           cod_processo_movimentacao,
           tempo_realizacao_millis) is null
           and
           fechado_automaticamente_movimentacao is false
           and
           fechado_automaticamente_integracao is false
           and
           fechado_automaticamente_afericao is false) -- verificamos se está pendente.
          or
          (row(
           data_hora_resolucao,
           cpf_mecanico,
           psi_apos_conserto,
           km_momento_conserto,
           tempo_realizacao_millis) is not null
           and
           fechado_automaticamente_movimentacao is false
           and
           fechado_automaticamente_integracao is false
           and
           fechado_automaticamente_afericao is false) -- verificamos se está resolvido por mecânico
          or
          (row(
           data_hora_resolucao,
           cod_processo_movimentacao) is not null
           and
           fechado_automaticamente_movimentacao is true
           and
           fechado_automaticamente_integracao is false
           and
           fechado_automaticamente_afericao is false) -- verificamos se está resolvido automaticamente
          or
          (data_hora_resolucao is not null
           and
           cod_processo_movimentacao is null
           and
           fechado_automaticamente_movimentacao is false
           and
           fechado_automaticamente_integracao is true
           and
           fechado_automaticamente_afericao is false) -- verificamos se está resolvido por integração
          or
          (row(
           data_hora_resolucao,
           cod_afericao_fechamento_automatico) is not null
           and
           cod_processo_movimentacao is null
           and
           fechado_automaticamente_movimentacao is false
           and
           fechado_automaticamente_integracao is false
           and
           fechado_automaticamente_afericao is true)) -- verificamos se está resolvido por aferição
  when (tipo_servico::text = 'inspecao'::text)
    then ((row(
           data_hora_resolucao,
           cpf_mecanico,
           psi_apos_conserto,
           km_momento_conserto,
           cod_pneu_inserido,
           cod_processo_movimentacao,
           tempo_realizacao_millis) is null
           and
           fechado_automaticamente_movimentacao is false
           and
           fechado_automaticamente_integracao is false
           and
           fechado_automaticamente_afericao is false) -- verificamos se está pendente.
          or
          (row(
           data_hora_resolucao,
           cpf_mecanico,
           psi_apos_conserto,
           km_momento_conserto,
           cod_alternativa,
           tempo_realizacao_millis) is not null
           and
           fechado_automaticamente_movimentacao is false
           and
           fechado_automaticamente_integracao is false
           and
           fechado_automaticamente_afericao is false) -- verificamos se está resolvido por mecânico
          or
          (row(
           data_hora_resolucao,
           cod_processo_movimentacao) is not null
           and
           fechado_automaticamente_movimentacao is true
           and
           fechado_automaticamente_integracao is false
           and
           fechado_automaticamente_afericao is false
           and
           cod_afericao_fechamento_automatico is null
           and
           fechado_automaticamente_afericao is false) -- verificamos se está resolvido automaticamente
          or
          (data_hora_resolucao is not null
           and
           cod_processo_movimentacao is null
           and
           fechado_automaticamente_movimentacao is false
           and
           fechado_automaticamente_integracao is true
           and
           fechado_automaticamente_afericao is false) -- verificamos se está resolvido por integração
          or
          (row(
           data_hora_resolucao,
           cod_afericao_fechamento_automatico) is not null
           and
           cod_processo_movimentacao is null
           and
           fechado_automaticamente_movimentacao is false
           and
           fechado_automaticamente_integracao is false
           and
           fechado_automaticamente_afericao is true)) -- verificamos se está resolvido por aferição
  end
);