drop function func_veiculo_update_km_atual(f_cod_unidade bigint,
                                           f_cod_veiculo bigint,
                                           f_km_coletado bigint,
                                           f_cod_processo bigint,
                                           f_tipo_processo text,
                                           f_deve_propagar_km boolean,
                                           f_data_hora timestamp with time zone);
create or replace function func_veiculo_update_km_atual(f_cod_unidade bigint,
                                                        f_cod_veiculo bigint,
                                                        f_km_coletado bigint,
                                                        f_cod_processo bigint,
                                                        f_tipo_processo text,
                                                        f_data_hora timestamp with time zone)
    returns bigint
    language plpgsql
as
$$
declare
    v_km_atual                           bigint;
    v_diferenca_km                       bigint;
    v_km_motorizado                      bigint;
    v_possui_hubodometro                 boolean;
    v_motorizado                         boolean;
    v_cod_processo_acoplamento           bigint;
    v_cod_historico_processo_acoplamento bigint[];
    v_cod_veiculos_acoplados             bigint[];
    v_km_veiculos_acoplados              bigint[];
    v_veiculos_motorizados               boolean[];
    v_cod_empresa                        bigint;
begin
    select v.km, v.possui_hubodometro, v.motorizado, vaa.cod_processo, v.cod_empresa
    from veiculo v
             left join veiculo_acoplamento_atual vaa on v.codigo = vaa.cod_veiculo
    where v.codigo = f_cod_veiculo
    into strict v_km_atual, v_possui_hubodometro, v_motorizado, v_cod_processo_acoplamento, v_cod_empresa;

    case when (f_km_coletado is not null) then
        case when ((v_motorizado is true or v_possui_hubodometro is true) and v_km_atual > f_km_coletado)
            then
                return f_km_coletado;
            else
                if (v_cod_processo_acoplamento is not null)
                then
                    select array_agg(vaa.cod_veiculo), array_agg(v.motorizado), array_agg(v.km), array_agg(vah.codigo)
                    from veiculo_acoplamento_atual vaa
                             join veiculo v
                                  on vaa.cod_unidade = v.cod_unidade
                                      and vaa.cod_veiculo = v.codigo
                             inner join veiculo_acoplamento_historico vah on vaa.cod_processo = vah.cod_processo
                        and vaa.cod_veiculo = vah.cod_veiculo
                    where vaa.cod_unidade = f_cod_unidade
                      and vaa.cod_processo = v_cod_processo_acoplamento
                      and v.possui_hubodometro is false
                    into v_cod_veiculos_acoplados,
                        v_veiculos_motorizados,
                        v_km_veiculos_acoplados,
                        v_cod_historico_processo_acoplamento;
                end if;
                case when (v_possui_hubodometro is false and v_motorizado is false and
                           v_cod_processo_acoplamento is null)
                    then
                        perform func_veiculo_salva_historico_km_propagacao(
                                f_cod_unidade,
                                null,
                                v_cod_processo_acoplamento,
                                f_cod_veiculo,
                                v_motorizado,
                                true,
                                v_km_atual,
                                v_km_atual,
                                f_km_coletado,
                                f_tipo_processo,
                                f_cod_processo,
                                f_data_hora);
                        return v_km_atual;
                    else
                        case when (v_possui_hubodometro is true or
                                   (v_motorizado is true and v_cod_processo_acoplamento is null))
                            then
                                update veiculo set km = f_km_coletado where codigo = f_cod_veiculo;
                                return f_km_coletado;
                            else
                                case when (v_possui_hubodometro is false and v_cod_processo_acoplamento is not null)
                                    then
                                        case when (v_motorizado is true)
                                            then
                                                v_diferenca_km = f_km_coletado - v_km_atual;
                                            else
                                                v_km_motorizado = (select v.km
                                                                   from veiculo v
                                                                   where v.cod_unidade = f_cod_unidade
                                                                     and v.codigo = any (v_cod_veiculos_acoplados)
                                                                     and v.motorizado is true);
                                                case when (v_km_motorizado > f_km_coletado)
                                                    then
                                                        perform func_veiculo_salva_historico_km_propagacao(
                                                                f_cod_unidade,
                                                                unnest(v_cod_historico_processo_acoplamento),
                                                                v_cod_processo_acoplamento,
                                                                unnest(v_cod_veiculos_acoplados),
                                                                unnest(v_veiculos_motorizados),
                                                                (unnest(v_cod_veiculos_acoplados) = f_cod_veiculo),
                                                                unnest(v_km_veiculos_acoplados),
                                                                unnest(v_km_veiculos_acoplados),
                                                                f_km_coletado,
                                                                f_tipo_processo,
                                                                f_cod_processo,
                                                                f_data_hora);
                                                        return v_km_atual;
                                                    else
                                                        v_diferenca_km = f_km_coletado - v_km_motorizado;
                                                    end case;
                                            end case;
                                        case when (v_diferenca_km is not null)
                                            then
                                                update veiculo v
                                                set km = km + v_diferenca_km
                                                where v.codigo = any (v_cod_veiculos_acoplados);
                                                perform func_veiculo_salva_historico_km_propagacao(
                                                        f_cod_unidade,
                                                        unnest(v_cod_historico_processo_acoplamento),
                                                        v_cod_processo_acoplamento,
                                                        unnest(v_cod_veiculos_acoplados),
                                                        unnest(v_veiculos_motorizados),
                                                        (unnest(v_cod_veiculos_acoplados) = f_cod_veiculo),
                                                        unnest(v_km_veiculos_acoplados),
                                                        unnest(v_km_veiculos_acoplados) + v_diferenca_km,
                                                        f_km_coletado,
                                                        f_tipo_processo,
                                                        f_cod_processo,
                                                        f_data_hora);
                                                return v_km_atual + v_diferenca_km;
                                            else
                                                return v_km_atual;
                                            end case;
                                    end case;
                            end case;
                    end case;
            end case;
        else
            return v_km_atual;
        end case;
end;
$$;

create or replace function func_checklist_insert_checklist_infos(f_cod_unidade_checklist bigint,
                                                                 f_cod_modelo_checklist bigint,
                                                                 f_cod_versao_modelo_checklist bigint,
                                                                 f_data_hora_realizacao timestamp with time zone,
                                                                 f_cod_colaborador bigint,
                                                                 f_cod_veiculo bigint,
                                                                 f_tipo_checklist char,
                                                                 f_km_coletado bigint,
                                                                 f_observacao text,
                                                                 f_tempo_realizacao bigint,
                                                                 f_data_hora_sincronizacao timestamp with time zone,
                                                                 f_fonte_data_hora_realizacao text,
                                                                 f_versao_app_momento_realizacao integer,
                                                                 f_versao_app_momento_sincronizacao integer,
                                                                 f_device_id text,
                                                                 f_device_imei text,
                                                                 f_device_uptime_realizacao_millis bigint,
                                                                 f_device_uptime_sincronizacao_millis bigint,
                                                                 f_foi_offline boolean,
                                                                 f_total_perguntas_ok integer,
                                                                 f_total_perguntas_nok integer,
                                                                 f_total_alternativas_ok integer,
                                                                 f_total_alternativas_nok integer,
                                                                 f_total_midias_perguntas_ok integer,
                                                                 f_total_midias_alternativas_nok integer)
    returns table
            (
                cod_checklist_inserido bigint,
                checklist_ja_existia   boolean
            )
    language plpgsql
as
$$
declare
    -- Iremos pegar a placa com base no veículo, para evitar a impossibilidade de sincronização caso ela tenha sido
    -- alterada e o check realizado offiline.
    v_placa_atual_do_veiculo text    := (select vd.placa
                                         from veiculo_data vd
                                         where vd.codigo = f_cod_veiculo);
    v_cod_novo_checklist     bigint;
    v_cod_checklist_inserido bigint;
    v_checklist_ja_existia   boolean := false;
    v_km_final               bigint;
begin

    v_cod_novo_checklist := (select nextval(pg_get_serial_sequence('checklist_data', 'codigo')));


    v_km_final :=
            (select *
             from func_veiculo_update_km_atual(f_cod_unidade_checklist,
                                               f_cod_veiculo,
                                               f_km_coletado,
                                               v_cod_novo_checklist,
                                               'CHECKLIST',
                                               f_data_hora_realizacao));

    insert into checklist_data(codigo,
                               cod_unidade,
                               cod_checklist_modelo,
                               cod_versao_checklist_modelo,
                               data_hora,
                               data_hora_realizacao_tz_aplicado,
                               cpf_colaborador,
                               tipo,
                               tempo_realizacao,
                               km_veiculo,
                               observacao,
                               data_hora_sincronizacao,
                               fonte_data_hora_realizacao,
                               versao_app_momento_realizacao,
                               versao_app_momento_sincronizacao,
                               device_id,
                               device_imei,
                               device_uptime_realizacao_millis,
                               device_uptime_sincronizacao_millis,
                               foi_offline,
                               total_perguntas_ok,
                               total_perguntas_nok,
                               total_alternativas_ok,
                               total_alternativas_nok,
                               total_midias_perguntas_ok,
                               total_midias_alternativas_nok,
                               cod_veiculo)
    values (v_cod_novo_checklist,
            f_cod_unidade_checklist,
            f_cod_modelo_checklist,
            f_cod_versao_modelo_checklist,
            f_data_hora_realizacao,
            (f_data_hora_realizacao at time zone tz_unidade(f_cod_unidade_checklist)),
            (select c.cpf from colaborador c where c.codigo = f_cod_colaborador),
            f_tipo_checklist,
            f_tempo_realizacao,
            v_km_final,
            f_observacao,
            f_data_hora_sincronizacao,
            f_fonte_data_hora_realizacao,
            f_versao_app_momento_realizacao,
            f_versao_app_momento_sincronizacao,
            f_device_id,
            f_device_imei,
            f_device_uptime_realizacao_millis,
            f_device_uptime_sincronizacao_millis,
            f_foi_offline,
            f_total_perguntas_ok,
            f_total_perguntas_nok,
            f_total_alternativas_ok,
            f_total_alternativas_nok,
            nullif(f_total_midias_perguntas_ok, 0),
            nullif(f_total_midias_alternativas_nok, 0),
            f_cod_veiculo)
    on conflict on constraint unique_checklist
        do update set data_hora_sincronizacao = f_data_hora_sincronizacao
                      -- https://stackoverflow.com/a/40880200/4744158
    returning codigo, not (checklist_data.xmax = 0) into v_cod_checklist_inserido, v_checklist_ja_existia;

    -- Verificamos se o insert funcionou.
    if v_cod_checklist_inserido <= 0
    then
        raise exception 'Não foi possível inserir o checklist.';
    end if;

    return query select v_cod_checklist_inserido, v_checklist_ja_existia;
end;
$$;

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

create or replace function func_socorro_rota_abertura(f_cod_unidade bigint,
                                                      f_cod_colaborador_abertura bigint,
                                                      f_cod_veiculo_problema bigint,
                                                      f_km_veiculo_abertura bigint,
                                                      f_cod_problema_socorro_rota bigint,
                                                      f_descricao_problema text,
                                                      f_data_hora_abertura timestamp with time zone,
                                                      f_url_foto_1_abertura text,
                                                      f_url_foto_2_abertura text,
                                                      f_url_foto_3_abertura text,
                                                      f_latitude_abertura text,
                                                      f_longitude_abertura text,
                                                      f_precisao_localizacao_abertura_metros numeric,
                                                      f_endereco_automatico text,
                                                      f_ponto_referencia text,
                                                      f_device_id_abertura text,
                                                      f_device_imei_abertura text,
                                                      f_device_uptime_millis_abertura bigint,
                                                      f_android_api_version_abertura integer,
                                                      f_marca_device_abertura text,
                                                      f_modelo_device_abertura text,
                                                      f_plataforma_origem prolog_plataforma_socorro_rota_type,
                                                      f_versao_plataforma_origem text) returns bigint
    language plpgsql
as
$$
declare
    f_cod_socorro_inserido bigint;
    f_cod_empresa          bigint := (select cod_empresa
                                      from unidade
                                      where codigo = f_cod_unidade);
    f_cod_abertura         bigint;
    v_km_final             bigint;
begin
    -- Verifica se a funcionalidade está liberada para a empresa.
    perform func_socorro_rota_empresa_liberada(f_cod_empresa);

    -- Assim conseguimos inserir mantendo a referência circular entre a pai e as filhas.
    set constraints all deferred;

    -- Pega o código de abertura da sequence para poder atualizar a tabela pai.
    f_cod_abertura := (select nextval(pg_get_serial_sequence('socorro_rota_abertura', 'codigo')));

    v_km_final := (select *
                   from func_veiculo_update_km_atual(f_cod_unidade,
                                                     f_cod_veiculo_problema,
                                                     f_km_veiculo_abertura,
                                                     f_cod_abertura,
                                                     'SOCORRO_EM_ROTA',
                                                     f_data_hora_abertura));

    -- Insere na tabela pai.
    insert into socorro_rota (cod_unidade, status_atual, cod_abertura)
    values (f_cod_unidade, 'ABERTO', f_cod_abertura)
    returning codigo into f_cod_socorro_inserido;

    -- Exibe erro se não puder inserir.
    if f_cod_socorro_inserido is null or f_cod_socorro_inserido <= 0
    then
        perform throw_generic_error(
                'Não foi possível realizar a abertura desse socorro em rota, tente novamente');
    end if;

    -- Insere na tabela de abertura
    insert into socorro_rota_abertura (codigo,
                                       cod_socorro_rota,
                                       cod_colaborador_abertura,
                                       cod_veiculo_problema,
                                       km_veiculo_abertura,
                                       cod_problema_socorro_rota,
                                       descricao_problema,
                                       data_hora_abertura,
                                       url_foto_1_abertura,
                                       url_foto_2_abertura,
                                       url_foto_3_abertura,
                                       latitude_abertura,
                                       longitude_abertura,
                                       precisao_localizacao_abertura_metros,
                                       endereco_automatico,
                                       ponto_referencia,
                                       device_id_abertura,
                                       device_imei_abertura,
                                       device_uptime_millis_abertura,
                                       android_api_version_abertura,
                                       marca_device_abertura,
                                       modelo_device_abertura,
                                       cod_empresa,
                                       plataforma_origem,
                                       versao_plataforma_origem)
    values (f_cod_abertura,
            f_cod_socorro_inserido,
            f_cod_colaborador_abertura,
            f_cod_veiculo_problema,
            v_km_final,
            f_cod_problema_socorro_rota,
            f_descricao_problema,
            f_data_hora_abertura,
            f_url_foto_1_abertura,
            f_url_foto_2_abertura,
            f_url_foto_3_abertura,
            f_latitude_abertura,
            f_longitude_abertura,
            f_precisao_localizacao_abertura_metros,
            f_endereco_automatico,
            f_ponto_referencia,
            f_device_id_abertura,
            f_device_imei_abertura,
            f_device_uptime_millis_abertura,
            f_android_api_version_abertura,
            f_marca_device_abertura,
            f_modelo_device_abertura,
            f_cod_empresa,
            f_plataforma_origem,
            f_versao_plataforma_origem);

    -- Exibe erro se não puder inserir.
    if f_cod_abertura is null or f_cod_abertura <= 0
    then
        perform throw_generic_error(
                'Não foi possível realizar a abertura desse socorro em rota, tente novamente');
    end if;

    -- Retorna o código do socorro.
    return f_cod_socorro_inserido;
end;
$$;

create or replace function func_afericao_insert_afericao(f_cod_unidade bigint,
                                                         f_data_hora timestamp with time zone,
                                                         f_cpf_aferidor bigint,
                                                         f_tempo_realizacao bigint,
                                                         f_tipo_medicao_coletada varchar(255),
                                                         f_tipo_processo_coleta varchar(255),
                                                         f_forma_coleta_dados text,
                                                         f_cod_veiculo bigint,
                                                         f_km_veiculo bigint)
    returns bigint

    language plpgsql
    security definer
as
$$
declare
    v_cod_tipo_veiculo      bigint := (select v.cod_tipo
                                       from veiculo_data v
                                       where v.codigo = f_cod_veiculo);
    v_cod_diagrama_veiculo  bigint := (select vt.cod_diagrama
                                       from veiculo_tipo vt
                                       where vt.codigo = v_cod_tipo_veiculo);
    v_cod_afericao          bigint;
    v_cod_afericao_inserida bigint;
    v_km_final              bigint;


begin
    v_cod_afericao := (select nextval(pg_get_serial_sequence('afericao_data', 'codigo')));

    if f_cod_veiculo is not null
    then
        v_km_final := (select *
                       from func_veiculo_update_km_atual(f_cod_unidade,
                                                         f_cod_veiculo,
                                                         f_km_veiculo,
                                                         v_cod_afericao,
                                                         'AFERICAO',
                                                         f_data_hora));
    end if;

    -- realiza inserção da aferição.
    insert into afericao_data(codigo,
                              data_hora,
                              cpf_aferidor,
                              km_veiculo,
                              tempo_realizacao,
                              tipo_medicao_coletada,
                              cod_unidade,
                              tipo_processo_coleta,
                              deletado,
                              data_hora_deletado,
                              pg_username_delecao,
                              cod_diagrama,
                              forma_coleta_dados,
                              cod_veiculo)
    values (v_cod_afericao,
            f_data_hora,
            f_cpf_aferidor,
            v_km_final,
            f_tempo_realizacao,
            f_tipo_medicao_coletada,
            f_cod_unidade,
            f_tipo_processo_coleta,
            false,
            null,
            null,
            v_cod_diagrama_veiculo,
            f_forma_coleta_dados,
            f_cod_veiculo)
    returning codigo into v_cod_afericao_inserida;

    if (v_cod_afericao_inserida <= 0)
    then
        perform throw_generic_error('Erro ao inserir aferição');
    end if;

    return v_cod_afericao_inserida;
end
$$;