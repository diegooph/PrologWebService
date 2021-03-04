-- Sobre:
--
-- Function responsavel por atualizar para deleção ou reversão da deleção as tabelas
-- checklist_data,
-- checklist_ordem_servico_data,
-- checklist_ordem_servico_itens_data,
-- assim realizando a ação solicitada em todas os checklists encaminhadas
--
-- Histórico:
-- 2020-10-28 -> Function criada (steinert999 - PL-3217).
create or replace function func_checklist_deleta_checklist_e_os(f_cod_checklists bigint[],
                                                                f_cod_colaborador bigint,
                                                                f_acao_executada text,
                                                                f_origem_delecao text,
                                                                f_observacao text,
                                                                f_data_hora_atual timestamp with time zone)
    returns void
    language plpgsql
as
$$
declare
    v_cod_empresa_colaborador constant bigint not null  := (select c.cod_empresa
                                                            from colaborador c
                                                            where c.codigo = f_cod_colaborador);
    v_deletado                constant boolean not null := f_if(f_acao_executada = 'DELETADO', true, false);
    v_quantidade_linhas_atualizadas    bigint;
begin
    if not ((select array_agg(cd.cod_unidade)
             from checklist_data cd
             where cd.codigo = any (f_cod_checklists))
        <@
            (select array_agg(u.codigo)
             from unidade u
             where u.cod_empresa = v_cod_empresa_colaborador))
    then
        perform throw_generic_error('Um ou mais checklists não pertencem à empresa! Favor verificar.');
    end if;

    if (v_deletado in (select cd.deletado
                                from checklist_data cd
                                where cd.codigo = any (f_cod_checklists))) then
        case v_deletado
            when true then perform throw_generic_error('Só é possivel deletar se todos os checklists ' ||
                                                       'não estiverem deletados, favor verificar.');
            when false then perform throw_generic_error('Só é possivel desfazer a deleção dos checklists ' ||
                                                        'se todos estiverem deletados, favor verificar.');
            end case;
    end if;

    update checklist_data
    set deletado = v_deletado
    where codigo = any (f_cod_checklists);

    get diagnostics v_quantidade_linhas_atualizadas := row_count;
    if v_quantidade_linhas_atualizadas is null or v_quantidade_linhas_atualizadas != f_size_array(f_cod_checklists)
    then
        perform throw_generic_error('Erro ao atualizar checklists, tente novamente.');
    end if;

    with update_os as (
        update checklist_ordem_servico_data
            set deletado = v_deletado,
                pg_username_delecao = session_user,
                data_hora_deletado = f_data_hora_atual
            where cod_checklist = any (f_cod_checklists)
            returning cod_unidade, codigo
    )

    update checklist_ordem_servico_itens_data
    set deletado = v_deletado,
        pg_username_delecao = session_user,
        data_hora_deletado = f_data_hora_atual
    where (cod_unidade, cod_os) in (select uo.cod_unidade, uo.codigo from update_os uo);

    insert into checklist_delecao(cod_colaborador,
                                  cod_checklist,
                                  data_hora,
                                  acao_executada,
                                  origem_delecao,
                                  observacao)
    select f_cod_colaborador,
           unnest(f_cod_checklists),
           f_data_hora_atual,
           f_acao_executada,
           f_origem_delecao,
           f_observacao;

    if not found
    then
        perform throw_generic_error('Erro ao atualizar checklists, tente novamente.');
    end if;
end
$$