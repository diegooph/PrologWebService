-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- ao receber os dados da aferição através do servidor, a function realiza a inserção de uma nova aferição.
-- Para suprir a nova demanda da tabela afericao_data possuir a coluna cod_diagrama, a function realiza a busca
-- do cod_diagrama desse veículo para que seja inserido.
-- Após inserir a nova aferição, é retornando o cod_afericao_inserida.

-- Précondições:
--
-- Histórico:
-- 2019-11-29 -> Function criada (natanrotta - pl-1899).
-- 2020-04-29 -> Adiciona campo forma_coleta_dados no insert (gustavocnp95 - pl-2686)
-- 2020-09-23 -> Adiciona campo cod_veiculo (thaisksf - PL-3170).
-- 2020-11-23 -> Modifica update de km na function (gustavocnp95 - PL-3290).
-- 2020-12-16 -> Corrige propagação de km na function (gustavocnp95|thaisksf - PL-3367).
create or replace function func_afericao_insert_afericao(f_cod_unidade bigint,
                                                         f_data_hora timestamp with time zone,
                                                         f_cpf_aferidor bigint,
                                                         f_tempo_realizacao bigint,
                                                         f_tipo_medicao_coletada varchar(255),
                                                         f_tipo_processo_coleta varchar(255),
                                                         f_forma_coleta_dados text,
                                                         f_placa_veiculo varchar(255),
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
                                       where v.placa = f_placa_veiculo);
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
                                                         true,
                                                         f_data_hora));
    end if;

    -- realiza inserção da aferição.
    insert into afericao_data(codigo,
                              data_hora,
                              placa_veiculo,
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
            f_placa_veiculo,
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