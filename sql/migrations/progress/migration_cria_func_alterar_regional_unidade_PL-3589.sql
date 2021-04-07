create or replace function suporte.func_unidade_altera_regional(f_cod_unidade bigint,
                                                                f_cod_regional bigint,
                                                                out aviso_regional_alterada text)

    returns text
    language plpgsql
    security definer
as
$$
declare
    v_cod_regional_atual  constant bigint not null := (select u.cod_regional from unidade u
                                                       where u.codigo = f_cod_unidade);
begin
    perform suporte.func_historico_salva_execucao();
    perform func_garante_not_null(f_cod_unidade, 'Código unidade');
    perform func_garante_not_null(f_cod_regional, 'Código regional');
    perform func_garante_unidade_existe(f_cod_unidade);
    perform func_garante_regional_existe(f_cod_regional);
    if(v_cod_regional_atual = f_cod_regional) then
        raise exception 'A regional para alteração é igual a regional já incluida na unidade.';
    end if;
    update unidade
        set cod_regional = f_cod_regional
    where codigo = f_cod_unidade;

    delete from token_autenticacao where cod_colaborador in (select c.codigo from colaborador c
                                                             where c.cod_unidade = f_cod_unidade);

    select concat('Regional da unidade: '
           || f_cod_unidade
           || ' foi alterada de '
           || v_cod_regional_atual
           || ' para '
           || f_cod_regional)
    into aviso_regional_alterada;
end;
$$;
