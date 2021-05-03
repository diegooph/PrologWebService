create or replace function suporte.func_veiculo_altera_tipo_veiculo(f_placa_veiculo text,
                                                                    f_cod_veiculo_tipo_novo bigint,
                                                                    f_cod_unidade bigint,
                                                                    f_informacoes_extras_suporte text,
                                                                    out aviso_tipo_veiculo_alterado text)
    returns text
    security definer
    language plpgsql
as
$$
declare
    -- Não colocamos 'not null' para deixar que as validações quebrem com mensagens personalizadas.
    v_cod_diagrama_novo constant bigint := (select vt.cod_diagrama
                                            from veiculo_tipo vt
                                            where vt.codigo = f_cod_veiculo_tipo_novo);
    -- Não colocamos 'not null' para deixar que as validações quebrem com mensagens personalizadas.
    V_cod_empresa       constant bigint := (select u.cod_empresa
                                            from unidade u
                                            where u.codigo = f_cod_unidade);
    v_cod_veiculo                bigint;
    v_identificador_frota_antigo text;
    v_km_antigo                  bigint;
    v_cod_diagrama_antigo        bigint;
    v_cod_tipo_antigo            bigint;
    v_cod_modelo_antigo          bigint;
    v_possui_hubodometro_antigo  boolean;
    v_status_antigo              boolean;
begin
    perform suporte.func_historico_salva_execucao();

    -- Garante que unidade/empresa existem.
    perform func_garante_unidade_existe(f_cod_unidade);

    -- Garante que veiculo existe e pertence a unidade sem considerar os deletados.
    perform func_garante_veiculo_existe(f_cod_unidade, f_placa_veiculo, false);

    -- Garante que tipo_veiculo_novo pertence a empresa.
    if not exists(select vt.codigo
                  from veiculo_tipo vt
                  where vt.codigo = f_cod_veiculo_tipo_novo
                    and vt.cod_empresa = V_cod_empresa)
    then
        raise exception
            'O tipo de veículo de código: % não pertence à empresa: %',
            f_cod_veiculo_tipo_novo,
            V_cod_empresa;
    end if;

    -- Verifica se placa tem pneus aplicados.
    if exists(select vp.cod_veiculo from veiculo_pneu vp where vp.cod_veiculo = v_cod_veiculo)
    then
        -- Se existirem pneus, verifica se os pneus que aplicados possuem as mesmas posições do novo tipo.
        if ((select array_agg(vp.posicao)
             from veiculo_pneu vp
             where vp.cod_veiculo = v_cod_veiculo) <@
            (select array_agg(vdpp.posicao_prolog :: integer)
             from veiculo_diagrama_posicao_prolog vdpp
             where cod_diagrama = v_cod_diagrama_novo) = false)
        then
            raise exception
                'Existem pneus aplicados em posições que não fazem parte do tipo de veículo de código: %',
                f_cod_veiculo_tipo_novo;
        end if;
    end if;

    -- Busca os dados necessários para mandarmos para a function de update.
    select v.codigo,
           v.identificador_frota,
           v.km,
           v.cod_diagrama,
           v.cod_tipo,
           v.cod_modelo,
           v.possui_hubodometro,
           v.status_ativo
    into strict
        v_cod_veiculo,
        v_identificador_frota_antigo,
        v_km_antigo,
        v_cod_diagrama_antigo,
        v_cod_tipo_antigo,
        v_cod_modelo_antigo,
        v_possui_hubodometro_antigo,
        v_status_antigo
    from veiculo v
    where v.placa = f_placa_veiculo
      and v.cod_unidade = f_cod_unidade;

    -- Verifica se o tipo_veiculo_novo é o atual.
    if v_cod_tipo_antigo = f_cod_veiculo_tipo_novo
    then
        raise exception
            'O tipo de veículo atual da placa % é igual ao informado. Código tipo de veículo: %',
            f_placa_veiculo,
            f_cod_veiculo_tipo_novo;
    end if;

    if exists(select vp.cod_veiculo from veiculo_pneu vp where vp.cod_veiculo = v_cod_veiculo)
        and v_cod_diagrama_antigo <> v_cod_diagrama_novo
    then
        -- Assim conseguimos alterar o cod_diagrama na VEICULO_PNEU sem ele ainda estar alterado na tabela VEICULO_DATA.
        set constraints all deferred;

        update veiculo_pneu
        set cod_diagrama = v_cod_diagrama_novo
        where cod_veiculo = v_cod_veiculo
          and cod_unidade = f_cod_unidade
          and cod_diagrama = v_cod_diagrama_antigo;

        if (not found)
        then
            raise exception
                'Não foi possível modificar o cod_diagrama para a placa % no vínculo de veículo pneu.', f_placa_veiculo;
        end if;
    end if;

    -- Precisamos gerar o histórico também.
    perform func_veiculo_gera_historico_atualizacao(v_cod_empresa,
                                                    v_cod_veiculo,
                                                    null,
                                                    'SUPORTE',
                                                    now(),
                                                    f_informacoes_extras_suporte,
                                                    f_placa_veiculo,
                                                    v_identificador_frota_antigo,
                                                    v_km_antigo,
                                                    v_cod_diagrama_novo,
                                                    f_cod_veiculo_tipo_novo,
                                                    v_cod_modelo_antigo,
                                                    v_status_antigo,
                                                    v_possui_hubodometro_antigo,
                                                    (f_if(v_cod_tipo_antigo <> f_cod_veiculo_tipo_novo, 1, 0)
                                                        +
                                                     f_if(v_cod_diagrama_antigo <> v_cod_diagrama_novo, 1, 0))::smallint);

    -- Nesta function, fazemos o update diretamente ao invés de chamar a function de atualizar o
    -- veículo. Precisamos fazer assim pois no postgres como cada function roda dentro de uma transaction, se
    -- chamássemos uma nova function para atualizar o veículo, o "set constraints all deferred;" utilizado para
    -- postergar as constraints na tabela VEICULO_PNEU não funcionaria.
    update veiculo
    set cod_tipo     = f_cod_veiculo_tipo_novo,
        cod_diagrama = v_cod_diagrama_novo,
        foi_editado  = true
    where codigo = v_cod_veiculo
      and placa = f_placa_veiculo
      and cod_unidade = f_cod_unidade;

    -- Mensagem de sucesso.
    select 'Tipo do veículo alterado! ' ||
           'Placa: ' || f_placa_veiculo ||
           ', Código da unidade: ' || f_cod_unidade ||
           ', Tipo: ' || (select vt.nome from veiculo_tipo vt where vt.codigo = f_cod_veiculo_tipo_novo) ||
           ', Código do tipo: ' || f_cod_veiculo_tipo_novo || '.'
    into aviso_tipo_veiculo_alterado;
end;
$$;