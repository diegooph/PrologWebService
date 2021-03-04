-- Sobre:
-- Function para atualizar o status de um veículo integrado no Prolog, alterando tanto na tabela VEICULO_DATA quanto na
-- INTEGRACAO.VEICULO_CADASTRADO.
--
-- Histórico:
-- 2020-09-11 -> Altera function para utilizar function de update de veículo padrão (luiz_fp - PL-3097).
-- 2020-11-09 -> Altera function para adequar mudanças na function de atualização (steinert999 - PL-3223).
create or replace function
    integracao.func_veiculo_ativa_desativa_veiculo_prolog(f_placa_veiculo text,
                                                          f_ativar_desativar_veiculo boolean,
                                                          f_data_hora_edicao_veiculo timestamp with time zone,
                                                          f_token_integracao text)
    returns bigint
    language plpgsql
as
$$
declare
    v_cod_empresa_veiculo     bigint;
    v_cod_unidade_veiculo     bigint;
    v_cod_veiculo_prolog      bigint;
    v_identificador_frota     text;
    v_novo_km_veiculo         bigint;
    v_novo_cod_tipo_veiculo   bigint;
    v_novo_cod_modelo_veiculo bigint;
    v_novo_possui_hubodometro boolean;
begin
    -- Não usamos 'strict' propositalmente pois não queremos quebrar no select. Deixamos as próprias validações da
    -- function verificarem e quebrarem.
    select vd.cod_empresa,
           vd.cod_unidade,
           vd.codigo,
           vd.identificador_frota,
           vd.km,
           vd.cod_tipo,
           vd.cod_modelo,
           vd.possui_hubodometro
    into
        v_cod_empresa_veiculo,
        v_cod_unidade_veiculo,
        v_cod_veiculo_prolog,
        v_identificador_frota,
        v_novo_km_veiculo,
        v_novo_cod_tipo_veiculo,
        v_novo_cod_modelo_veiculo,
        v_novo_possui_hubodometro
    from veiculo_data vd
    where vd.placa = f_placa_veiculo;

    -- Validamos se a unidade pertence a mesma empresa do token.
    perform integracao.func_garante_token_empresa(
            v_cod_empresa_veiculo,
            f_token_integracao,
            format('[ERRO DE VÍNCULO] O token "%s" não está autorizado a atualizar dados da unidade "%s",
                   verificar vínculos', f_token_integracao, v_cod_unidade_veiculo));

    -- Validamos se a placa já existe no Prolog.
    if (select not exists(select v.codigo from public.veiculo_data v where v.placa::text = f_placa_veiculo))
    then
        perform public.throw_generic_error(
                format('[ERRO DE DADOS] A placa "%s" não existe no Sistema ProLog', f_placa_veiculo));
    end if;

    perform func_veiculo_atualiza_veiculo(v_cod_veiculo_prolog,
                                          f_placa_veiculo,
                                          v_identificador_frota,
                                          v_novo_km_veiculo,
                                          v_novo_cod_tipo_veiculo,
                                          v_novo_cod_modelo_veiculo,
                                          f_ativar_desativar_veiculo,
                              v_novo_possui_hubodometro,
                                          null,
                                          'API',
                                          f_data_hora_edicao_veiculo,
                                          f_token_integracao);

    update integracao.veiculo_cadastrado
    set data_hora_ultima_edicao = f_data_hora_edicao_veiculo
    where cod_empresa_cadastro = v_cod_empresa_veiculo
      and placa_veiculo_cadastro = f_placa_veiculo;

    -- Verificamos se o update na tabela de mapeamento de veículos cadastrados na integração ocorreu com êxito.
    if not found
    then
        perform throw_generic_error(
                format('Não foi possível atualizar a placa "%s" na tabela de mapeamento', f_placa_veiculo));
    end if;

    return v_cod_veiculo_prolog;
end;
$$;