create or replace function suporte.func_pneu_mescla_modelos_pneu(f_cod_empresa bigint,
                                                                 f_cod_modelo_pneu_destino_mescla bigint,
                                                                 f_cod_modelo_pneu_que_sera_deletado bigint,
                                                                 f_informacoes_extras_suporte text)
    returns text
    security definer
    language plpgsql
as
$$
begin
    perform suporte.func_historico_salva_execucao(f_informacoes_extras_suporte);

    perform func_garante_empresa_existe(f_cod_empresa);
    perform func_garante_modelo_pneu_existe(f_cod_modelo_pneu_destino_mescla);
    perform func_garante_modelo_pneu_existe(f_cod_modelo_pneu_que_sera_deletado);

    if (select count(*)
        from modelo_pneu mp
        where mp.codigo in (f_cod_modelo_pneu_destino_mescla, f_cod_modelo_pneu_que_sera_deletado)
          and mp.cod_empresa = f_cod_empresa) <> 2
    then
        raise exception 'Os modelos fornecidos não fazem parte da mesma empresa!';
    end if;

    -- Usamos o _data pois precisamos remover a referência até mesmo dos pneus deletados.
    update pneu_data
    set cod_modelo = f_cod_modelo_pneu_destino_mescla
    where cod_modelo = f_cod_modelo_pneu_que_sera_deletado
      and cod_empresa = f_cod_empresa;

    delete
    from modelo_pneu
    where codigo = f_cod_modelo_pneu_que_sera_deletado
      and cod_empresa = f_cod_empresa;

    return format('O modelo de pneu de código "%s" foi mesclado ao modelo de código "%s" com sucesso!',
                  f_cod_modelo_pneu_que_sera_deletado,
                  f_cod_modelo_pneu_destino_mescla);
end ;
$$;