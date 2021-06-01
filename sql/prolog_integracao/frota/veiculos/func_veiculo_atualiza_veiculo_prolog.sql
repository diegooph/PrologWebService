-- Sobre:
-- Function para atualizar um veículo integrado no Prolog, alterando tanto na tabela VEICULO_DATA quanto na
-- INTEGRACAO.VEICULO_CADASTRADO.
--
-- Histórico:
-- 2020-02-26 -> Adiciona a manipulação do código de diagrama (wvinim - PL-1965).
-- 2020-08-05 -> Adapta function para token duplicado (diogenesvanzella - PLI-175).
-- 2020-09-10 -> Altera function para utilizar function de update de veículo padrão (luiz_fp - PL-3097).
-- 2020-11-09 -> Adiciona parametro default false para hubodometro (steinert999 - PL-3223).
create or replace function
    integracao.func_veiculo_atualiza_veiculo_prolog(f_cod_unidade_original_alocado bigint,
                                                    f_placa_original_veiculo text,
                                                    f_novo_cod_unidade_alocado bigint,
                                                    f_nova_placa_veiculo text,
                                                    f_novo_km_veiculo bigint,
                                                    f_novo_cod_modelo_veiculo bigint,
                                                    f_novo_cod_tipo_veiculo bigint,
                                                    f_data_hora_edicao_veiculo timestamp with time zone,
                                                    f_token_integracao text,
                                                    f_novo_possui_hubodometro boolean default false)
    returns bigint
    language plpgsql
as
$$
declare
    v_cod_empresa_veiculo constant bigint not null := (select u.cod_empresa
                                                       from public.unidade u
                                                       where u.codigo = f_cod_unidade_original_alocado);
    v_cod_veiculo_prolog           bigint;
    v_identificador_frota          text;
    v_status_ativo                 boolean;
begin
    -- Validamos se o usuário trocou a unidade alocada do veículo.
    if (f_cod_unidade_original_alocado <> f_novo_cod_unidade_alocado)
    then
        perform public.throw_generic_error(
                '[ERRO DE OPERAÇÃO] Para mudar a Unidade do veículo, utilize a transferência de veículo');
    end if;

    -- Validamos se a Unidade do veículo trocou.
    if ((select v.cod_unidade
         from public.veiculo_data v
         where v.placa = f_placa_original_veiculo) <> f_cod_unidade_original_alocado)
    then
        perform public.throw_generic_error(
                '[ERRO DE OPERAÇÃO] Para mudar a Unidade do veículo, utilize a transferência de veículo');
    end if;

    -- Validamos se a Unidade pertence a mesma empresa do token.
    if ((select u.cod_empresa from public.unidade u where u.codigo = f_novo_cod_unidade_alocado)
        not in (select ti.cod_empresa
                from integracao.token_integracao ti
                where ti.token_integracao = f_token_integracao))
    then
        perform public.throw_generic_error(
                format(
                        '[ERRO DE VÍNCULO] O token "%s" não está autorizado a atualizar dados da unidade "%s", verificar vínculos',
                        f_token_integracao,
                        f_novo_cod_unidade_alocado));
    end if;

    -- Validamos se a placa já existe no ProLog.
    if (select not exists(select v.codigo from public.veiculo_data v where v.placa::text = f_nova_placa_veiculo))
    then
        perform public.throw_generic_error(
                format('[ERRO DE DADOS] A placa "%s" não existe no Sistema ProLog', f_nova_placa_veiculo));
    end if;

    -- Validamos se o modelo do veículo está mapeado.
    if (select not exists(select codigo
                          from public.modelo_veiculo mv
                          where mv.cod_empresa = v_cod_empresa_veiculo
                            and mv.codigo = f_novo_cod_modelo_veiculo))
    then
        perform public.throw_generic_error(
                '[ERRO DE VINCULO] O modelo do veículo não está mapeado corretamente, verificar vínculos');
    end if;

    -- Validamos se o tipo do veículo está mapeado.
    if (select not exists(select codigo
                          from public.veiculo_tipo vt
                          where vt.codigo = f_novo_cod_tipo_veiculo
                            and vt.cod_empresa = v_cod_empresa_veiculo))
    then
        perform public.throw_generic_error(
                '[ERRO DE VINCULO] O tipo do veículo não está mapeado corretamente, verificar vínculos');
    end if;

    select vd.codigo,
           vd.identificador_frota,
           vd.status_ativo
    into strict
        v_cod_veiculo_prolog,
        v_identificador_frota,
        v_status_ativo
    from veiculo_data vd
    where vd.placa = f_placa_original_veiculo
      and vd.cod_unidade = f_cod_unidade_original_alocado;

    perform func_veiculo_atualiza_veiculo(v_cod_veiculo_prolog,
                                          f_nova_placa_veiculo,
                                          v_identificador_frota,
                                          f_novo_km_veiculo,
                                          f_novo_cod_tipo_veiculo,
                                          f_novo_cod_modelo_veiculo,
                                          v_status_ativo,
                                          f_novo_possui_hubodometro,
                                          null,
                                          'API',
                                          f_data_hora_edicao_veiculo,
                                          f_token_integracao);

    update integracao.veiculo_cadastrado
    set data_hora_ultima_edicao = f_data_hora_edicao_veiculo
    where cod_empresa_cadastro = v_cod_empresa_veiculo
      and placa_veiculo_cadastro = f_placa_original_veiculo;

    -- Verificamos se o update na tabela de mapeamento de veículos cadastrados na integração ocorreu com êxito.
    if not found
    then
        perform throw_generic_error(
                format('Não foi possível atualizar a placa "%s" na tabela de mapeamento', F_PLACA_ORIGINAL_VEICULO));
    end if;

    return v_cod_veiculo_prolog;
end;
$$;