-- PL-3134.

-- Cria funcs de garante.
create or replace function func_garante_modelo_pneu_existe(f_cod_modelo_pneu bigint,
                                                           f_error_message text default null)
    returns void
    language plpgsql
as
$$
declare
    v_error_message constant text not null := f_if(f_error_message is null,
                                                   format('Modelo de pneu de código %s não existe!', f_cod_modelo_pneu),
                                                   f_error_message);
begin
    if not exists(select mp.codigo from modelo_pneu mp where mp.codigo = f_cod_modelo_pneu)
    then
        perform throw_generic_error(v_error_message);
    end if;
end;
$$;

create or replace function func_garante_modelo_banda_existe(f_cod_modelo_banda bigint,
                                                            f_error_message text default null)
    returns void
    language plpgsql
as
$$
declare
    v_error_message constant text not null := f_if(f_error_message is null,
                                                   format('Modelo de banda de código %s não existe!', f_cod_modelo_banda),
                                                   f_error_message);
begin
    if not exists(select mp.codigo from modelo_banda mp where mp.codigo = f_cod_modelo_banda)
    then
        perform throw_generic_error(v_error_message);
    end if;
end;
$$;
--

-- Adiciona coluna de informações extras na tabela de histórico de uso.
alter table suporte.historico_uso_function
    add column informacoes_extras text;

drop function suporte.func_historico_salva_execucao();

create or replace function suporte.func_historico_salva_execucao(f_informacoes_extras text default null)
    returns void
    security definer
    language sql
as
$$
insert into suporte.historico_uso_function (function_query,
                                            data_hora_execucao,
                                            pg_username_execucao,
                                            informacoes_extras)
values (current_query(),
        now(),
        session_user,
        f_informacoes_extras)
$$;
--

-- Ativa o audit no modelo de pneu e banda.
create trigger tg_func_audit_modelo_pneu
    after insert or update or delete
    on modelo_pneu
    for each row
execute procedure audit.func_audit();

create trigger tg_func_audit_modelo_banda
    after insert or update or delete
    on modelo_banda
    for each row
execute procedure audit.func_audit();
--

-- Cria functions de mescla de modelos de pneu e banda.
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