-- PL-3210.
create or replace function func_veiculo_remove_acoplamento_atual(f_cod_processo_acoplamento bigint)
    returns void
    language plpgsql
as
$$
begin
    delete
    from veiculo_acoplamento_atual
    where cod_processo = f_cod_processo_acoplamento;

    if not found
    then
        perform throw_server_side_error(format('Erro ao deletar estado atual de acoplamento para o process:
                                               %s.', f_cod_processo_acoplamento));
    end if;
end;
$$;

create or replace function func_veiculo_insert_processo_acoplamento(f_cod_unidade bigint,
                                                                    f_cod_colaborador_realizacao bigint,
                                                                    f_data_hora_atual timestamp with time zone,
                                                                    f_observacao text)
    returns bigint
    language sql
as
$$
insert into veiculo_acoplamento_processo(cod_unidade,
                                         cod_colaborador,
                                         data_hora,
                                         observacao)
values (f_cod_unidade,
        f_cod_colaborador_realizacao,
        f_data_hora_atual,
        f_observacao)
returning codigo as codigo;
$$;

create or replace function func_veiculo_insert_historico_acoplamento(f_cod_processo_acoplamento bigint,
                                                                     f_cod_veiculo bigint,
                                                                     f_cod_diagrama_veiculo bigint,
                                                                     f_posicao_acao_realizada smallint,
                                                                     f_veiculo_motorizado boolean,
                                                                     f_km_coletado bigint,
                                                                     f_acao_realizada text)
    returns void
    language plpgsql
as
$$
begin
    insert into veiculo_acoplamento_historico(cod_processo,
                                              cod_veiculo,
                                              cod_diagrama,
                                              cod_posicao,
                                              motorizado,
                                              km_coletado,
                                              acao)
    values (f_cod_processo_acoplamento,
            f_cod_veiculo,
            f_cod_diagrama_veiculo,
            f_posicao_acao_realizada,
            f_veiculo_motorizado,
            f_km_coletado,
            f_acao_realizada::types.veiculo_acoplamento_acao_type);

    if not found
    then
        perform throw_server_side_error('Erro ao inserir histórico de acoplamento para o veículo: %s.', f_cod_veiculo);
    end if;
end;
$$;

create or replace function func_veiculo_insert_estado_atual_acoplamentos(f_cod_processo_acoplamento bigint,
                                                                         f_cod_unidade bigint,
                                                                         f_cod_veiculo bigint,
                                                                         f_cod_diagrama_veiculo bigint,
                                                                         f_posicao_acoplamento smallint,
                                                                         f_veiculo_motorizado boolean)
    returns void
    language plpgsql
as
$$
begin
    insert into veiculo_acoplamento_atual(cod_processo,
                                          cod_unidade,
                                          cod_veiculo,
                                          cod_diagrama,
                                          cod_posicao,
                                          motorizado)
    values (f_cod_processo_acoplamento,
            f_cod_unidade,
            f_cod_veiculo,
            f_cod_diagrama_veiculo,
            f_posicao_acoplamento,
            f_veiculo_motorizado);

    if not found
    then
        perform throw_server_side_error('Erro ao inserir estado atual de acoplamento para o veículo: %s.',
                                        f_cod_veiculo);
    end if;
end;
$$;