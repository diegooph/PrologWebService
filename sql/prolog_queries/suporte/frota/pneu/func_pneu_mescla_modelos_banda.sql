-- Sobre:
--
-- Function responsável por mesclar um modelo de banda no outro.
--
-- Ela funciona da seguinte forma: dado dois modelos A e B, todos os vínculos existentes no modelo B são alterados
-- para vincular o modelo A. Depois disso o modelo B é deletado.
--
-- Histórico:
-- 2020-09-08 -> Function criada (luiz_fp - PL-3134).
create or replace function suporte.func_pneu_mescla_modelos_banda(f_cod_empresa bigint,
                                                                  f_cod_modelo_banda_destino_mescla bigint,
                                                                  f_cod_modelo_banda_que_sera_deletado bigint,
                                                                  f_informacoes_extras_suporte text)
    returns text
    security definer
    language plpgsql
as
$$
begin
    perform suporte.func_historico_salva_execucao(f_informacoes_extras_suporte);

    perform func_garante_empresa_existe(f_cod_empresa);
    perform func_garante_modelo_banda_existe(f_cod_modelo_banda_destino_mescla);
    perform func_garante_modelo_banda_existe(f_cod_modelo_banda_que_sera_deletado);

    if (select count(*)
        from modelo_banda mb
        where mb.codigo in (f_cod_modelo_banda_destino_mescla, f_cod_modelo_banda_que_sera_deletado)
          and mb.cod_empresa = f_cod_empresa) <> 2
    then
        raise exception 'Os modelos fornecidos não fazem parte da mesma empresa!';
    end if;

    -- Usamos o _data pois precisamos remover a referência até mesmo dos pneus deletados.
    update pneu_data
    set cod_modelo_banda = f_cod_modelo_banda_destino_mescla
    where cod_modelo_banda = f_cod_modelo_banda_que_sera_deletado
      and cod_empresa = f_cod_empresa;

    -- Usamos o _data pois precisamos remover a referência até mesmo dos serviços deletados.
    update pneu_servico_realizado_incrementa_vida_data
    set cod_modelo_banda = f_cod_modelo_banda_destino_mescla
    where cod_modelo_banda = f_cod_modelo_banda_que_sera_deletado;

    delete
    from modelo_banda
    where codigo = f_cod_modelo_banda_que_sera_deletado
      and cod_empresa = f_cod_empresa;

    return format('O modelo de banda de código "%s" foi mesclado ao modelo de código "%s" com sucesso!',
                  f_cod_modelo_banda_que_sera_deletado,
                  f_cod_modelo_banda_destino_mescla);
end ;
$$;