-- PL-3147.

-- Utilizado para detectar usos do CPF como FK:
-- select r.table_name
-- from information_schema.constraint_column_usage u
-- inner join information_schema.referential_constraints fk
--     on u.constraint_catalog = fk.unique_constraint_catalog
--     and u.constraint_schema = fk.unique_constraint_schema
--     and u.constraint_name = fk.unique_constraint_name
-- inner join information_schema.key_column_usage r
--     on r.constraint_catalog = fk.constraint_catalog
--     and r.constraint_schema = fk.constraint_schema
--     and r.constraint_name = fk.constraint_name
-- where u.column_name = 'cpf'
--   and u.table_schema = 'public'
--   and u.table_name = 'colaborador_data';

-- Tabelas do GSD podem ser removidas, funcionalidade não existe mais.
drop table gsd_respostas;
drop table gsd_perguntas;
drop table pdv_gsd;
drop table gsd;

-- Tabela pode ser dropada, não é mais utilizada.
drop table veiculo_pneu_inconsistencia;

-- Tabela pode ser dropada, não chegou a ser utilizada.
drop table dashboard_componente_personalizacao;

-- Alteramos as tabelas que usam CPF como FK para terem 'update cascade'.
-- Na tabela de 'token_autenticacao' não será adicionado o cascade pois as entradas devem ser deletadas para forçar um
-- logout.
alter table fale_conosco
    drop constraint fk_fale_conosco_colaborador,
    add constraint fk_fale_conosco_colaborador
        foreign key (cpf_colaborador) references colaborador_data (cpf) on update cascade;
alter table fale_conosco
    drop constraint fk_fale_conosco_colaborador_feedback,
    add constraint fk_fale_conosco_colaborador_feedback
        foreign key (cpf_feedback) references colaborador_data (cpf) on update cascade;

alter table acessos_produtividade
    drop constraint fk_acessos_produtividade_colaborador,
    add constraint fk_acessos_produtividade_colaborador
        foreign key (cpf_colaborador) references colaborador_data (cpf) on update cascade;

alter table quiz
    drop constraint fk_quiz_colaborador,
    add constraint fk_quiz_colaborador
        foreign key (cpf_colaborador) references colaborador_data (cpf) on update cascade;

alter table movimentacao_processo
    drop constraint fk_movimentacao_processo_colaborador,
    add constraint fk_movimentacao_processo_colaborador
        foreign key (cpf_responsavel) references colaborador_data (cpf) on update cascade;

alter table afericao_data
    drop constraint fk_afericao_colaborador,
    add constraint fk_afericao_colaborador
        foreign key (cpf_aferidor) references colaborador_data (cpf) on update cascade;
alter table afericao_manutencao_data
    drop constraint fk_afericao_manutencao,
    add constraint fk_afericao_manutencao_colaborador
        foreign key (cpf_mecanico) references colaborador_data (cpf) on update cascade;

alter table intervalo
    drop constraint fk_intervalo_colaborador,
    add constraint fk_intervalo_colaborador
        foreign key (cpf_colaborador) references colaborador_data (cpf) on update cascade;

alter table checklist_data
    drop constraint fk_checklist_colaborador,
    add constraint fk_checklist_colaborador
        foreign key (cpf_colaborador) references colaborador_data (cpf) on update cascade;
alter table checklist_ordem_servico_itens_data
    drop constraint fk_checklist_ordem_servico_itens,
    add constraint fk_checklist_ordem_servico_itens_colaborador
        foreign key (cpf_mecanico) references colaborador_data (cpf) on update cascade;

alter table relato
    drop constraint fk_relato_colaborador,
    add constraint fk_relato_colaborador
        foreign key (cpf_colaborador) references colaborador_data (cpf) on update cascade;
alter table relato
    drop constraint fk_relato_colaborador_classificacao,
    add constraint fk_relato_colaborador_classificacao
        foreign key (cpf_colaborador) references colaborador_data (cpf) on update cascade;
alter table relato
    drop constraint fk_relato_colaborador_fechamento,
    add constraint fk_relato_colaborador_fechamento
        foreign key (cpf_colaborador) references colaborador_data (cpf) on update cascade;

alter table solicitacao_folga
    drop constraint fk_solicitacao_folga_colaborador,
    add constraint fk_solicitacao_folga_colaborador
        foreign key (cpf_colaborador) references colaborador_data (cpf) on update cascade;
alter table solicitacao_folga
    drop constraint fk_solicitacao_folga_colaborador_feedback,
    add constraint fk_solicitacao_folga_colaborador_feedback
        foreign key (cpf_feedback) references colaborador_data (cpf) on update cascade;

alter table treinamento_colaborador
    drop constraint fk_treinamento_colaborador_colaborador,
    add constraint fk_treinamento_colaborador_colaborador
        foreign key (cpf_colaborador) references colaborador_data (cpf) on update cascade;


-- Melhora comentário da coluna para sabermos que é em milissegundos.
comment on column checklist_ordem_servico_itens_data.tempo_realizacao is 'Tempo de duração do conserto em milissegundos.';

-- Em março de 2019 nós adicionamos as colunas 'data_hora_inicio_resolucao' e 'data_hora_fim_resolucao' aos itens de OS.
-- Como os itens antigos não tinham essa informação, que é fornecida pelo usuário, nós criamos a constraint de check
-- usando 'not valid', para ignorar entradas já existentes na tabela.
-- Porém, essa constraint de check falha se tentamos atualizar uma entrada antiga na tabela de
-- 'checklist_ordem_servico_itens_data' e isso vai ocorrer quando a FK de colaborador for realizar o update cascade em
-- uma mudança de CPF.
-- É por isso que estamos setando valor para esas duas colunas no update abaixo.
update checklist_ordem_servico_itens_data
set data_hora_inicio_resolucao = (data_hora_conserto - (tempo_realizacao * interval '1 milliseconds')),
    data_hora_fim_resolucao    = data_hora_conserto
where data_hora_conserto is not null
  -- Apenas os resolvidos.
  and status_resolucao = 'R'
  and data_hora_inicio_resolucao is null;

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