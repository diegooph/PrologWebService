create or replace function suporte.func_veiculo_altera_placa(f_cod_unidade_veiculo bigint,
                                                             f_cod_veiculo bigint,
                                                             f_placa_antiga text,
                                                             f_placa_nova text,
                                                             f_informacoes_extras_suporte text,
                                                             f_forcar_atualizacao_placa_integracao boolean default false,
                                                             out f_aviso_placa_alterada text)
    returns text
    language plpgsql
    security definer
as
$$
declare
    v_cod_empresa         bigint;
    v_identificador_frota text;
    v_km                  bigint;
    v_cod_diagrama        bigint;
    v_cod_tipo            bigint;
    v_cod_modelo          bigint;
    v_status              boolean;
    v_possui_hubodometro  boolean;
begin
    perform suporte.func_historico_salva_execucao(f_informacoes_extras_suporte);

    perform func_garante_unidade_existe(f_cod_unidade_veiculo);
    perform func_garante_veiculo_existe(f_cod_unidade_veiculo, f_placa_antiga);

    -- Verifica se placa nova está disponível.
    if exists(select vd.placa from veiculo_data vd where vd.placa = f_placa_nova)
    then
        raise exception
            'A placa % já existe no banco.', f_placa_nova;
    end if;

    -- Verifica se a placa é de integração.
    if exists(select vc.placa_veiculo_cadastro
              from integracao.veiculo_cadastrado vc
              where vc.placa_veiculo_cadastro = f_placa_antiga)
    then
        -- Verifica se deve alterar placa em integração.
        if (f_forcar_atualizacao_placa_integracao is false)
        then
            raise exception
                'A placa % pertence à integração. para atualizar a mesma, deve-se passar true como parâmetro.',
                f_placa_antiga;
        end if;
    end if;

    -- Agora alteramos a placa.
    select v.cod_empresa,
           v.identificador_frota,
           v.km,
           v.cod_diagrama,
           v.cod_tipo,
           v.cod_modelo,
           v.status_ativo,
           v.possui_hubodometro
    into strict
        v_cod_empresa,
        v_identificador_frota,
        v_km,
        v_cod_diagrama,
        v_cod_tipo,
        v_cod_modelo,
        v_status,
        v_possui_hubodometro
    from veiculo v
    where v.codigo = f_cod_veiculo
      and v.cod_unidade = f_cod_unidade_veiculo;

    -- Precisamos gerar o histórico também.
    perform func_veiculo_gera_historico_atualizacao(v_cod_empresa,
                                                    f_cod_veiculo,
                                                    null,
                                                    'SUPORTE',
                                                    now(),
                                                    f_informacoes_extras_suporte,
                                                    f_placa_nova,
                                                    v_identificador_frota,
                                                    v_km,
                                                    v_cod_diagrama, -- Apenas a placa mudou.
                                                    v_cod_tipo,
                                                    v_cod_modelo,
                                                    v_status,
                                                    v_possui_hubodometro,
                                                    1::smallint);
    -- Nesta function, fazemos o update diretamente ao invés de chamar a function de atualizar o
    -- veículo. Precisamos fazer assim pois no postgres como cada function roda dentro de uma transaction, se
    -- chamássemos uma nova function para atualizar o veículo, o "set constraints all deferred;" utilizado para
    -- postergar as constraints na tabela VEICULO_PNEU não funcionaria.
    update veiculo
    set placa       = f_placa_nova,
        foi_editado = true
    where codigo = f_cod_veiculo
      and placa = f_placa_antiga
      and cod_unidade = f_cod_unidade_veiculo;

    -- Modifica placa na integração.
    if exists(select vc.placa_veiculo_cadastro
              from integracao.veiculo_cadastrado vc
              where vc.placa_veiculo_cadastro = f_placa_antiga)
    then
        update integracao.veiculo_cadastrado
        set placa_veiculo_cadastro = f_placa_nova
        where placa_veiculo_cadastro = f_placa_antiga;

        if (not found)
        then
            raise exception
                'Não foi possível modificar a placa para % na tabela de integração VEICULO_CADASTRADO.', f_placa_nova;
        end if;
    end if;


    select 'A placa foi alterada de '
               || f_placa_antiga ||
           ' para '
               || f_placa_nova || '.'
    into f_aviso_placa_alterada;
end ;
$$;