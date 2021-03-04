-- Sobre:
--
-- Function utilizada para liberar ou bloquear permissões específicas para empresas ou mesmo unidades específicas
-- das empresas. A function pode ser utilizada para liberar ou bloquear permissões e deve-se utilizar EXATAMENTE o
-- texto liberar/bloquear ou LIBERAR/BLOQUEAR para executar cada ação.
-- Para realizar o bloqueio de todas as unidades das empresas, basta não enviar nenhuma unidade no parâmetro
-- 'f_cod_unidades'.
-- Pode-se ainda informar o motivo pelo qual está sendo realizado o bloqueio. O parâmentro 'f_observacao_bloqueio' é
-- opcional e se informado será replicado para todas as permissões bloqueadas pela function.
-- Caso a operação realizada for LIBERAR, então 'f_cod_motivo_bloqueio' e 'f_observacao_bloqueio' serão ignorados.
-- IMPORTANTE: Ao realizar o bloqueio de uma permissão ela é automaticamente retirada de todos os cargos que a possuem.
--
-- Histórico:
-- 2020-06-26 -> Function criada (diogenesvanzella - PL-2671).
-- 2020-07-08 -> Passa a remover a permissão do cargo quando bloqueia (diogenesvanzella - PL-2671).
-- 2020-08-14 -> Adiciona chamada para logar execução da function (gustavocnp95 - PL-3066).
create or replace function
    suporte.func_cargos_libera_bloqueia_permissoes_by_codigo(f_liberar_bloquear text,
                                                             f_cod_permissoes bigint[],
                                                             f_cod_empresas bigint[],
                                                             f_cod_motivo_bloqueio bigint,
                                                             f_observacao_bloqueio text default null,
                                                             f_cod_unidades bigint[] default null)
    returns text
    language plpgsql
    security definer
as
$$
declare
    v_cod_permissoes_nao_mapeados bigint[] := (select array_agg(permissoes.cod_permissao)
                                               from (select unnest(f_cod_permissoes) as cod_permissao) permissoes
                                               where permissoes.cod_permissao not in
                                                     (select fp.codigo from funcao_prolog_v11 fp));
    v_cod_empresas_nao_mapeadas   bigint[] := (select array_agg(empresas.cod_empresa)
                                               from (select unnest(f_cod_empresas) as cod_empresa) empresas
                                               where empresas.cod_empresa not in (select e.codigo from empresa e));
    v_cod_unidades_nao_mapeadas   bigint[] := (select array_agg(unidades.cod_unidade)
                                               from (select unnest(f_cod_unidades) as cod_unidade) unidades
                                               where unidades.cod_unidade not in
                                                     (select u.codigo
                                                      from unidade u
                                                      where u.cod_empresa = any (f_cod_empresas)));
    -- Caso o usuário informar as unidades, utilizaremos elas para liberar ou bloquear as permissões. Caso o usuário
    -- forneça somente a empresa, utilizamos todas as unidades de empresa para liberar ou bloquear as permissões.
    v_cod_unidades_mapeadas       bigint[] := (f_if(f_cod_unidades is not null,
                                                    f_cod_unidades,
                                                    (select array_agg(u.codigo)
                                                     from unidade u
                                                     where u.cod_empresa = any (f_cod_empresas))));
begin
    perform suporte.func_historico_salva_execucao();
    if (upper(f_liberar_bloquear) != 'LIBERAR' and upper(f_liberar_bloquear) != 'BLOQUEAR')
    then
        perform throw_generic_error('Deve-se informar o tipo correto da operação: LIBERAR ou BLOQUEAR.');
    end if;

    if (f_size_array(v_cod_permissoes_nao_mapeados) > 0)
    then
        perform throw_generic_error(
                format('Códigos de permissões inválidos (%s). ' ||
                       'Verifique os códigos na tabela funcao_prolog_v11.',
                       v_cod_permissoes_nao_mapeados));
    end if;

    if (f_size_array(v_cod_empresas_nao_mapeadas) > 0)
    then
        perform throw_generic_error(
                format('Nenhuma empresa encontrada para os códigos (%s)', v_cod_empresas_nao_mapeadas));
    end if;

    if (f_size_array(v_cod_unidades_nao_mapeadas) > 0)
    then
        perform throw_generic_error(
                format('As unidades (%s) não pertencem as empresas (%s)',
                       v_cod_unidades_nao_mapeadas,
                       f_cod_empresas));
    end if;

    -- Depois de validar os atributos necessários, fazemos o bloqueio ou liberação.
    if (upper(f_liberar_bloquear) = 'LIBERAR')
    then
        -- Devemos deletar da tabela de bloqueio as permissões com códigos e unidades mapeadas.
        delete
        from funcao_prolog_bloqueada
        where cod_funcao_prolog = any (f_cod_permissoes)
          and cod_unidade = any (v_cod_unidades_mapeadas);
    else
        if (select not exists(select codigo from funcao_prolog_motivo_bloqueio where codigo = f_cod_motivo_bloqueio))
        then
            perform throw_generic_error(
                    format('O motivo do bloqueio informado (%s) não é válido', f_cod_motivo_bloqueio));
        end if;

        -- Devemos inserir na tabela de bloqueio as permissões com códigos e unidades mapeadas.
        insert into funcao_prolog_bloqueada (cod_unidade,
                                             cod_pilar_funcao,
                                             cod_funcao_prolog,
                                             cod_motivo_bloqueio,
                                             observacao_bloqueio)
        select unnest(v_cod_unidades_mapeadas) as cod_unidade,
               fp.cod_pilar,
               fp.codigo,
               f_cod_motivo_bloqueio,
               f_observacao_bloqueio
        from funcao_prolog_v11 as fp
        where fp.codigo = any (f_cod_permissoes)
        on conflict
            on constraint pk_funcao_prolog_bloqueada
            do update
            set cod_motivo_bloqueio = f_cod_motivo_bloqueio,
                observacao_bloqueio = f_observacao_bloqueio;

        -- Devemos também remover essas permissões dos cargos associados nas unidades.
        delete
        from cargo_funcao_prolog_v11
        where cod_unidade = any (v_cod_unidades_mapeadas)
          and cod_funcao_prolog = any (f_cod_permissoes);
    end if;

    return (select format('A operação de %s foi realizada com sucesso para as permissões (%s)',
                          f_liberar_bloquear, f_cod_permissoes));
end;
$$;