create or replace function
    integracao.func_pneu_afericao_insert_afericao_integrada(f_cod_unidade_prolog bigint,
                                                            f_cod_auxiliar_unidade text,
                                                            f_cpf_aferidor text,
                                                            f_cod_veiculo bigint,
                                                            f_placa_veiculo text,
                                                            f_cod_auxiliar_tipo_veiculo_prolog text,
                                                            f_km_veiculo text,
                                                            f_tempo_realizacao bigint,
                                                            f_data_hora timestamp with time zone,
                                                            f_tipo_medicao_coletada text,
                                                            f_tipo_processo_coleta text,
                                                            f_respostas_campos_personalizados jsonb)
    returns bigint
    language plpgsql
as
$$
declare
    v_cod_empresa_prolog              bigint;
    v_cod_empresa_cliente             text;
    v_cod_tipo_veiculo_prolog         bigint;
    v_cod_diagrama_veiculo_prolog     bigint;
    v_cod_afericao_integrada_inserida bigint;
begin
    -- Busca os dados de empresa e unidade para integração
    select e.codigo,
           e.cod_auxiliar
    from unidade u
             join empresa e on u.cod_empresa = e.codigo
    where u.codigo = f_cod_unidade_prolog
    into v_cod_empresa_prolog, v_cod_empresa_cliente;

    -- Busca os dados de tipo de veículo e diagrama para enriquecer o registro de aferição integrada.
    select vt.cod_tipo_veiculo,
           vt.cod_diagrama
    from (select vt.codigo                                   as cod_tipo_veiculo,
                 vt.cod_diagrama                             as cod_diagrama,
                 regexp_split_to_table(vt.cod_auxiliar, ',') as cod_auxiliar
          from veiculo_tipo vt
          where vt.cod_empresa = v_cod_empresa_prolog) as vt
    where vt.cod_auxiliar = f_cod_auxiliar_tipo_veiculo_prolog
    into v_cod_tipo_veiculo_prolog, v_cod_diagrama_veiculo_prolog;

    -- Realiza a inserção do registro de aferição integrada.
    insert into integracao.afericao_integrada(cod_empresa_prolog,
                                              cod_empresa_cliente,
                                              cod_unidade_prolog,
                                              cod_unidade_cliente,
                                              cpf_aferidor,
                                              cod_veiculo,
                                              placa_veiculo,
                                              cod_tipo_veiculo_prolog,
                                              cod_tipo_veiculo_cliente,
                                              cod_diagrama_prolog,
                                              km_veiculo,
                                              tempo_realizacao,
                                              data_hora,
                                              tipo_medicao_coletada,
                                              tipo_processo_coleta,
                                              respostas_campos_personalizados)
    values (v_cod_empresa_prolog,
            v_cod_empresa_cliente,
            f_cod_unidade_prolog,
            f_cod_auxiliar_unidade,
            f_cpf_aferidor,
            f_cod_veiculo,
            f_placa_veiculo,
            v_cod_tipo_veiculo_prolog,
            f_cod_auxiliar_tipo_veiculo_prolog,
            v_cod_diagrama_veiculo_prolog,
            f_km_veiculo,
            f_tempo_realizacao,
            f_data_hora,
            f_tipo_medicao_coletada,
            f_tipo_processo_coleta,
            f_respostas_campos_personalizados)
    returning codigo into v_cod_afericao_integrada_inserida;

    if (v_cod_afericao_integrada_inserida is null or v_cod_afericao_integrada_inserida <= 0)
    then
        raise exception 'Não foi possível inserir a aferição nas tabelas de integração, tente novamente';
    end if;

    return v_cod_afericao_integrada_inserida;
end
$$;