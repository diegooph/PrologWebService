-- Sobre:
-- Essa function salva o histórico de uma propagação de km.
create function func_veiculo_salva_historico_km_propagacao(f_cod_unidade bigint,
                                                           f_cod_historico_processo_acoplamento bigint,
                                                           f_cod_processo_acoplamento bigint,
                                                           f_cod_veiculo_propagado bigint,
                                                           f_motorizado boolean,
                                                           f_veiculo_fonte_processo boolean,
                                                           f_km_antigo bigint,
                                                           f_km_final bigint,
                                                           f_km_coletado bigint,
                                                           f_tipo_processo text,
                                                           f_cod_processo bigint,
                                                           f_data_hora timestamp with time zone)
    returns bigint
    language plpgsql
as
$$
declare
    v_cod_historico_propagacao bigint;
begin
    insert into veiculo_processo_km_historico (cod_unidade,
                                               cod_historico_processo_acoplamento,
                                               cod_processo_acoplamento,
                                               cod_processo_veiculo,
                                               tipo_processo_veiculo,
                                               cod_veiculo,
                                               motorizado,
                                               veiculo_fonte_processo,
                                               km_antigo,
                                               km_final,
                                               km_coletado_processo,
                                               data_hora_processo)
    values (f_cod_unidade,
            f_cod_historico_processo_acoplamento,
            f_cod_processo_acoplamento,
            f_cod_processo,
            f_tipo_processo,
            f_cod_veiculo_propagado,
            f_motorizado,
            f_veiculo_fonte_processo,
            f_km_antigo,
            f_km_final,
            f_km_coletado,
            f_data_hora)
    returning codigo into v_cod_historico_propagacao;
    return v_cod_historico_propagacao;
end;
$$;