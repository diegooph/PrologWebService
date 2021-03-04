-- Sobre:
--
-- Function utilizada para alterar o CPF de um colaborador.
-- Além da alteração do CPF, todos os tokens do colaborador são removidos, forçando que ele faça um novo login no
-- sistema, dessa vez já com o novo CPF.
--
-- Histórico:
-- 2020-09-22 -> Cria function de alterar CPF (luiz_fp - PL-3147).
create or replace function suporte.func_colaborador_alterar_cpf(f_cod_colaborador bigint,
                                                                f_cpf_atual bigint,
                                                                f_cpf_novo bigint,
                                                                f_informacoes_extras_suporte text,
                                                                out f_aviso_associar_treinamento text)
    returns text
    language plpgsql
    security definer
as
$$
begin
    perform suporte.func_historico_salva_execucao(f_informacoes_extras_suporte);

    perform func_garante_cod_colaborador_existe(f_cod_colaborador);
    perform func_garante_colaborador_existe(f_cpf_atual);

    if f_cpf_novo is null
    then
        raise exception 'Fornceça um CPF novo diferente de NULL.';
    end if;

    if f_cpf_novo = f_cpf_atual
    then
        raise exception 'O CPF novo não pode ser igual ao atual.';
    end if;

    -- Verifica se o novo CPF já está sendo usado.
    if exists(select cd.codigo
              from colaborador_data cd
              where cd.cpf = f_cpf_novo)
    then
        raise exception
            'O novo CPF informado (%) já está em uso pelo colaborador %.',
            f_cpf_novo,
            (select cd.nome
             from colaborador_data cd
             where cd.cpf = f_cpf_novo);
    end if;

    -- Antes de alterarmos o CPF deletamos todos os tokens do CPF atual. Assim o usuário será obrigado a realizar um
    -- novo login.
    delete from token_autenticacao where cod_colaborador = f_cod_colaborador and cpf_colaborador = f_cpf_atual;

    update colaborador_data
    set cpf = f_cpf_novo
    where codigo = f_cod_colaborador
      and cpf = f_cpf_atual;

    if not found
    then
        raise exception 'Erro ao atualizar o CPF, tente novamente.';
    end if;

    select 'O CPF foi alterado de '
               || f_cpf_atual ||
           ' para '
               || f_cpf_novo ||
           ' com sucesso!'
    into f_aviso_associar_treinamento;
end ;
$$;