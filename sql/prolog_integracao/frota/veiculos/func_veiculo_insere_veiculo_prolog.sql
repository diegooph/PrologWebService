create or replace function
    integracao.func_veiculo_insere_veiculo_prolog(f_cod_unidade_veiculo_alocado bigint,
                                                  f_placa_veiculo_cadastrado text,
                                                  f_km_atual_veiculo_cadastrado bigint,
                                                  f_cod_modelo_veiculo_cadastrado bigint,
                                                  f_cod_tipo_veiculo_cadastrado bigint,
                                                  f_data_hora_veiculo_cadastro timestamp with time zone,
                                                  f_token_integracao text,
                                                  f_origem_cadastro text,
                                                  f_possui_hubodometro boolean default false)
    returns bigint
    language plpgsql
as
$$
declare
    cod_empresa_veiculo       constant bigint  := (select u.cod_empresa
                                                   from public.unidade u
                                                   where u.codigo = f_cod_unidade_veiculo_alocado);
    deve_sobrescrever_veiculo constant boolean := (select *
                                                   from integracao.func_empresa_get_config_sobrescreve_veiculos(
                                                           cod_empresa_veiculo));
    veiculo_esta_no_prolog    constant boolean := (select exists(select v.codigo
                                                                 from public.veiculo_data v
                                                                 where v.placa::text = f_placa_veiculo_cadastrado));
    status_ativo_veiculo      constant boolean := true;
    cod_veiculo_prolog                 bigint;
    f_qtd_rows_alteradas               bigint;
    v_insert_cod_diagrama              bigint;
    v_insert_motorizado                boolean;
begin
    -- Validamos se a Unidade pertence a mesma empresa do token.
    perform integracao.func_garante_token_empresa(
            cod_empresa_veiculo,
            f_token_integracao,
            format('[ERRO DE VÍNCULO] O token "%s" não está autorizado a inserir dados da unidade "%s",
                     confira se está usando o token correto', f_token_integracao, f_cod_unidade_veiculo_alocado));

    -- Validamos se o KM foi inputado corretamente.
    if (f_km_atual_veiculo_cadastrado < 0)
    then
        perform public.throw_generic_error(
                '[ERRO DE DADOS] A quilometragem do veículo não pode ser um número negativo');
    end if;

    -- Validamos se o modelo do veículo está mapeado.
    if (select not exists(select codigo
                          from public.modelo_veiculo mv
                          where mv.cod_empresa = cod_empresa_veiculo
                            and mv.codigo = f_cod_modelo_veiculo_cadastrado))
    then
        perform public.throw_generic_error(
                '[ERRO DE VINCULO] O modelo do veículo não está mapeado corretamente, verificar vinculos');
    end if;

    -- Validamos se o tipo do veículo está mapeado.
    if (select not exists(select codigo
                          from public.veiculo_tipo vt
                          where vt.codigo = f_cod_tipo_veiculo_cadastrado
                            and vt.cod_empresa = cod_empresa_veiculo))
    then
        perform public.throw_generic_error(
                '[ERRO DE VINCULO] O tipo do veículo não está mapeado corretamente, verificar vinculos');
    end if;

    -- Validamos se a placa já existe no ProLog.
    if (veiculo_esta_no_prolog and not deve_sobrescrever_veiculo)
    then
        perform public.throw_generic_error(
                format('[ERRO DE DADOS] A placa "%s" já está cadastrada no Sistema ProLog',
                       f_placa_veiculo_cadastrado));
    end if;

    if (veiculo_esta_no_prolog and deve_sobrescrever_veiculo)
    then
        -- Buscamos o código do veículo que será sobrescrito.
        select v.codigo
        from veiculo v
        where v.placa = f_placa_veiculo_cadastrado
          and v.cod_empresa = cod_empresa_veiculo
        into cod_veiculo_prolog;

        -- Removemos os pneus aplicados na placa, para que ela possa receber novos pneus.
        perform integracao.func_pneu_remove_vinculo_pneu_placa_posicao_by_placa(f_placa_veiculo_cadastrado);

        -- Sebrescrevemos os dados do veículo.
        perform integracao.func_veiculo_sobrescreve_veiculo_cadastrado(
                f_placa_veiculo_cadastrado,
                f_cod_unidade_veiculo_alocado,
                f_km_atual_veiculo_cadastrado,
                f_cod_tipo_veiculo_cadastrado,
                f_cod_modelo_veiculo_cadastrado);

    else
        select vt.cod_diagrama, vd.motorizado
        from public.veiculo_tipo vt
                 join veiculo_diagrama vd
                      on vd.codigo = vt.cod_diagrama
        where vt.codigo = f_cod_tipo_veiculo_cadastrado
          and vt.cod_empresa = cod_empresa_veiculo
        into v_insert_cod_diagrama, v_insert_motorizado;
        -- Aqui devemos apenas inserir o veículo no ProLog.
        insert into public.veiculo(cod_empresa,
                                   cod_unidade,
                                   placa,
                                   km,
                                   status_ativo,
                                   cod_tipo,
                                   cod_diagrama,
                                   motorizado,
                                   cod_modelo,
                                   cod_unidade_cadastro,
                                   possui_hubodometro,
                                   origem_cadastro)
        values (cod_empresa_veiculo,
                f_cod_unidade_veiculo_alocado,
                f_placa_veiculo_cadastrado,
                f_km_atual_veiculo_cadastrado,
                status_ativo_veiculo,
                f_cod_tipo_veiculo_cadastrado,
                v_insert_cod_diagrama,
                v_insert_motorizado,
                f_cod_modelo_veiculo_cadastrado,
                f_cod_unidade_veiculo_alocado,
                f_possui_hubodometro,
                f_origem_cadastro)
        returning codigo into cod_veiculo_prolog;
    end if;

    if (deve_sobrescrever_veiculo)
    then
        -- Se permite sobrescrita de dados, então tentamos inserir, caso a constraint estourar,
        -- apenas atualizamos os dados. Tentamos inserir antes, pois, em cenários onde o veículo já encontra-se no
        -- ProLog, não temos nenhuma entrada para ele na tabela de mapeamento.
        insert into integracao.veiculo_cadastrado(cod_empresa_cadastro,
                                                  cod_unidade_cadastro,
                                                  cod_veiculo_cadastro_prolog,
                                                  placa_veiculo_cadastro,
                                                  data_hora_cadastro_prolog)
        values (cod_empresa_veiculo,
                f_cod_unidade_veiculo_alocado,
                cod_veiculo_prolog,
                f_placa_veiculo_cadastrado,
                f_data_hora_veiculo_cadastro)
        on conflict on constraint unique_placa_cadastrada_empresa_integracao
            do update set cod_veiculo_cadastro_prolog = cod_veiculo_prolog,
                          cod_unidade_cadastro        = f_cod_unidade_veiculo_alocado,
                          data_hora_ultima_edicao     = f_data_hora_veiculo_cadastro;
    else
        -- Se não houve sobrescrita de dados, significa que devemos apenas inserir os dados na tabela de mapeamento.
        insert into integracao.veiculo_cadastrado(cod_empresa_cadastro,
                                                  cod_unidade_cadastro,
                                                  cod_veiculo_cadastro_prolog,
                                                  placa_veiculo_cadastro,
                                                  data_hora_cadastro_prolog)
        values (cod_empresa_veiculo,
                f_cod_unidade_veiculo_alocado,
                cod_veiculo_prolog,
                f_placa_veiculo_cadastrado,
                f_data_hora_veiculo_cadastro);
    end if;

    get diagnostics f_qtd_rows_alteradas = row_count;

    -- VERIFICAMOS SE O INSERT NA TABELA DE MAPEAMENTO DE VEÍCULOS CADASTRADOS NA INTEGRAÇÃO OCORREU COM ÊXITO.
    if (f_qtd_rows_alteradas <= 0)
    then
        raise exception
            'Não foi possível inserir a placa "%" na tabela de mapeamento', f_placa_veiculo_cadastrado;
    end if;

    return cod_veiculo_prolog;
end;
$$;