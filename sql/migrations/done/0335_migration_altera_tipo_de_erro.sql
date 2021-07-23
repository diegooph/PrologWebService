create or replace function func_checklist_os_resolver_itens(f_cod_unidade bigint,
                                                            f_cod_veiculo bigint,
                                                            f_cod_itens bigint[],
                                                            f_cpf bigint,
                                                            f_tempo_realizacao bigint,
                                                            f_km bigint,
                                                            f_status_resolucao text,
                                                            f_data_hora_conserto timestamp with time zone,
                                                            f_data_hora_inicio_resolucao timestamp with time zone,
                                                            f_data_hora_fim_resolucao timestamp with time zone,
                                                            f_feedback_conserto text) returns bigint
    language plpgsql
as
$$
declare
v_cod_item                                   bigint;
    v_data_realizacao_checklist                  timestamp with time zone;
    v_alternativa_item                           text;
    v_error_message                              text            := E'Erro! A data de resolução "%s" não pode ser anterior a data de abertura "%s" do item "%s".';
    v_qtd_linhas_atualizadas                     bigint;
    v_total_linhas_atualizadas                   bigint          := 0;
    v_cod_agrupamento_resolucao_em_lote constant bigint not null := (select nextval('CODIGO_RESOLUCAO_ITEM_OS'));
    v_tipo_processo                     constant text not null   := 'FECHAMENTO_ITEM_CHECKLIST';
    v_km_real                                    bigint;
begin
    v_km_real := (select *
                  from func_veiculo_update_km_atual(f_cod_unidade,
                                                    f_cod_veiculo,
                                                    f_km,
                                                    v_cod_agrupamento_resolucao_em_lote,
                                                    v_tipo_processo,
                                                    true,
                                                    CURRENT_TIMESTAMP));

    foreach v_cod_item in array f_cod_itens
        loop
            -- Busca a data de realização do check e a pergunta que originou o item de O.S.
select c.data_hora, capd.alternativa
from checklist_ordem_servico_itens cosi
         join checklist_ordem_servico cos
              on cosi.cod_os = cos.codigo and cosi.cod_unidade = cos.cod_unidade
         join checklist c on cos.cod_checklist = c.codigo
         join checklist_alternativa_pergunta_data capd
              on capd.codigo = cosi.cod_alternativa_primeiro_apontamento
where cosi.codigo = v_cod_item
    into v_data_realizacao_checklist, v_alternativa_item;

-- Bloqueia caso a data de resolução seja menor ou igual que a data de realização do checklist
if v_data_realizacao_checklist is not null and v_data_realizacao_checklist >= f_data_hora_inicio_resolucao
            then
                perform throw_client_side_error (format(
                        v_error_message,
                        format_with_tz(f_data_hora_inicio_resolucao, tz_unidade(f_cod_unidade), 'DD/MM/YYYY HH24:MI'),
                        format_with_tz(v_data_realizacao_checklist, tz_unidade(f_cod_unidade), 'DD/MM/YYYY HH24:MI'),
                        v_alternativa_item));
end if;

            -- Atualiza os itens
update checklist_ordem_servico_itens
set cpf_mecanico                      = f_cpf,
    tempo_realizacao                  = f_tempo_realizacao,
    km                                = v_km_real,
    status_resolucao                  = f_status_resolucao,
    data_hora_conserto                = f_data_hora_conserto,
    data_hora_inicio_resolucao        = f_data_hora_inicio_resolucao,
    data_hora_fim_resolucao           = f_data_hora_fim_resolucao,
    feedback_conserto                 = f_feedback_conserto,
    cod_agrupamento_resolucao_em_lote = v_cod_agrupamento_resolucao_em_lote
where cod_unidade = f_cod_unidade
  and codigo = v_cod_item
  and data_hora_conserto is null;

get diagnostics v_qtd_linhas_atualizadas = row_count;

-- Verificamos se o update funcionou.
if v_qtd_linhas_atualizadas is null or v_qtd_linhas_atualizadas <= 0
            then
                perform throw_generic_error('Erro ao marcar os itens como resolvidos.');
end if;
            v_total_linhas_atualizadas := v_total_linhas_atualizadas + v_qtd_linhas_atualizadas;
end loop;
return v_total_linhas_atualizadas;
end;
$$;