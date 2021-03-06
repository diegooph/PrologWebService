-- 2020-09-24 -> Corrige function para considerar código da marca no total de edições (luiz_fp).
create or replace function func_veiculo_atualiza_veiculo(f_cod_veiculo bigint,
                                                         f_nova_placa text,
                                                         f_novo_identificador_frota text,
                                                         f_novo_km bigint,
                                                         f_novo_cod_tipo bigint,
                                                         f_novo_cod_modelo bigint,
                                                         f_novo_status boolean,
                                                         f_cod_colaborador_edicao bigint,
                                                         f_origem_edicao text,
                                                         f_data_hora_edicao timestamp with time zone,
                                                         f_informacoes_extras_edicao text)
    returns table
            (
                cod_edicao_historico       bigint,
                total_edicoes              smallint,
                antiga_placa               text,
                antigo_identificador_frota text,
                antigo_km                  bigint,
                antigo_cod_diagrama        bigint,
                antigo_cod_tipo            bigint,
                antigo_cod_modelo          bigint,
                antigo_status              boolean
            )
    language plpgsql
as
$$
declare
    v_cod_empresa       constant bigint not null := (select v.cod_empresa
                                                     from veiculo v
                                                     where v.codigo = f_cod_veiculo);
    v_novo_cod_diagrama constant bigint not null := (select vt.cod_diagrama
                                                     from veiculo_tipo vt
                                                     where vt.codigo = f_novo_cod_tipo
                                                       and vt.cod_empresa = v_cod_empresa);
    v_novo_cod_marca    constant bigint not null := (select mv.cod_marca
                                                     from modelo_veiculo mv
                                                     where mv.codigo = f_novo_cod_modelo);
    v_cod_edicao_historico       bigint;
    v_total_edicoes              smallint;
    v_cod_unidade                bigint;
    v_antiga_placa               text;
    v_antigo_identificador_frota text;
    v_antigo_km                  bigint;
    v_antigo_cod_diagrama        bigint;
    v_antigo_cod_tipo            bigint;
    v_antigo_cod_marca           bigint;
    v_antigo_cod_modelo          bigint;
    v_antigo_status              boolean;
begin
    select v.cod_unidade,
           v.placa,
           v.identificador_frota,
           v.km,
           v.cod_diagrama,
           v.cod_tipo,
           mv.cod_marca,
           v.cod_modelo,
           v.status_ativo
    into strict
        v_cod_unidade,
        v_antiga_placa,
        v_antigo_identificador_frota,
        v_antigo_km,
        v_antigo_cod_diagrama,
        v_antigo_cod_tipo,
        v_antigo_cod_marca,
        v_antigo_cod_modelo,
        v_antigo_status
    from veiculo v
             join modelo_veiculo mv on v.cod_modelo = mv.codigo
    where v.codigo = f_cod_veiculo;

    -- Validamos se o km foi inputado corretamente.
    if (f_novo_km < 0)
    then
        perform throw_generic_error(
                'A quilometragem do veículo não pode ser um número negativo.');
    end if;

    -- Validamos se o tipo foi alterado mesmo com o veículo contendo pneus aplicados.
    if ((v_antigo_cod_tipo <> f_novo_cod_tipo)
        and (select count(vp.*)
             from veiculo_pneu vp
             where vp.placa = (select v.placa from veiculo v where v.codigo = f_cod_veiculo)) > 0)
    then
        perform throw_generic_error(
                'O tipo do veículo não pode ser alterado se a placa contém pneus aplicados.');
    end if;

    -- Agora que passou nas verificações, calcula quantas alterações foram feitas:
    -- hstore é uma estrutura que salva os dados como chave => valor. Fazendo hstore(novo) - hstore(antigo) irá
    -- sobrar apenas as entradas (chave => valor) que mudaram. Depois, aplicamos um akeys(hstore), que retorna um
    -- array das chaves (apenas as que mudaram) (poderia ser um avalues(hstore) também). Por fim, fazemos um
    -- f_size_array para saber o tamanho desse array: isso nos dá o número de edições realizadas.
    -- IMPORTANTE: como a placa não é atualiza no update abaixo, também ignoramos ela na contagem de total de edições.
    v_total_edicoes := f_size_array(akeys(hstore((f_novo_identificador_frota,
                                                  f_novo_km,
                                                  v_novo_cod_diagrama,
                                                  f_novo_cod_tipo,
                                                  v_novo_cod_marca,
                                                  f_novo_cod_modelo,
                                                  f_novo_status)) - hstore((v_antigo_identificador_frota,
                                                                            v_antigo_km,
                                                                            v_antigo_cod_diagrama,
                                                                            v_antigo_cod_tipo,
                                                                            v_antigo_cod_marca,
                                                                            v_antigo_cod_modelo,
                                                                            v_antigo_status))));

    -- O update no veículo só será feito se algo de fato mudou. E algo só mudou se o total de edições for maior que 0.
    if (v_total_edicoes is not null and v_total_edicoes > 0)
    then
        v_cod_edicao_historico := func_veiculo_gera_historico_atualizacao(v_cod_empresa,
                                                                          v_cod_unidade,
                                                                          f_cod_veiculo,
                                                                          v_antiga_placa,
                                                                          v_antigo_identificador_frota,
                                                                          v_antigo_km,
                                                                          v_antigo_cod_diagrama,
                                                                          v_antigo_cod_tipo,
                                                                          v_antigo_cod_modelo,
                                                                          v_antigo_status,
                                                                          v_total_edicoes,
                                                                          f_cod_colaborador_edicao,
                                                                          f_origem_edicao,
                                                                          f_data_hora_edicao,
                                                                          f_informacoes_extras_edicao);

        update veiculo
        set identificador_frota = f_novo_identificador_frota,
            km                  = f_novo_km,
            cod_modelo          = f_novo_cod_modelo,
            cod_tipo            = f_novo_cod_tipo,
            cod_diagrama        = v_novo_cod_diagrama,
            status_ativo        = f_novo_status,
            foi_editado         = true
        where codigo = f_cod_veiculo
          and cod_empresa = v_cod_empresa;

        -- Verificamos se o update na tabela de veículos ocorreu com êxito.
        if (not found)
        then
            perform throw_generic_error('Não foi possível atualizar o veículo, tente novamente.');
        end if;
    end if;

    return query
        select v_cod_edicao_historico,
               v_total_edicoes,
               v_antiga_placa,
               v_antigo_identificador_frota,
               v_antigo_km,
               v_antigo_cod_diagrama,
               v_antigo_cod_tipo,
               v_antigo_cod_modelo,
               v_antigo_status;
end;
$$;