-- Altera types.origem_acao_type para separar o type PROLOG em PROLOG_WEB e PROLOG_ANDROID.
alter table veiculo_edicao_historico
    drop constraint fk_origem_edicao;
alter table checklist_delecao
    drop constraint fk_origem_delecao;

update veiculo_edicao_historico
set origem_edicao = 'PROLOG_WEB'
where origem_edicao = 'PROLOG';
update checklist_delecao
set origem_delecao = 'PROLOG_WEB'
where origem_delecao = 'PROLOG';

update types.origem_acao_type
set origem_acao = 'PROLOG_WEB'
where origem_acao = 'PROLOG';
insert into types.origem_acao_type (origem_acao, origem_acao_legivel_pt_br, origem_acao_legivel_es, ativo)
values ('PROLOG_ANDROID', 'Prolog', 'Prolog', true);

alter table veiculo_edicao_historico
    add constraint fk_origem_edicao foreign key (origem_edicao) references types.origem_acao_type;
alter table checklist_delecao
    add constraint fk_origem_delecao foreign key (origem_delecao) references types.origem_acao_type;

-- Altera NPS para salvar qual origem da resposta e do bloqueio.
alter table cs.nps_respostas
    add column origem_resposta text not null default 'PROLOG_WEB';
alter table cs.nps_respostas
    add constraint fk_origem_resposta foreign key (origem_resposta) references types.origem_acao_type;
alter table cs.nps_respostas
    alter column origem_resposta drop default;

alter table cs.nps_bloqueio_pesquisa_colaborador
    add column origem_bloqueio text not null default 'PROLOG_WEB';
alter table cs.nps_bloqueio_pesquisa_colaborador
    add constraint fk_origem_resposta foreign key (origem_bloqueio) references types.origem_acao_type;
alter table cs.nps_bloqueio_pesquisa_colaborador
    alter column origem_bloqueio drop default;

-- Altera as functions.
create or replace function cs.func_nps_insere_respostas_pesquisa(f_cod_pesquisa_nps bigint,
                                                                 f_cod_colaborador_realizacao bigint,
                                                                 f_data_hora_realizacao_pesquisa timestamp with time zone,
                                                                 f_resposta_pergunta_escala smallint,
                                                                 f_resposta_pergunta_descritiva text,
                                                                 f_origem_resposta text)
    returns bigint
    language plpgsql
as
$$
declare
    cod_respostas_pesquisa_nps bigint;
begin
    -- Propositalmente, não tratamos a constraint de UNIQUE aqui. O front deve tratar para não enviar duplicados.
    insert into cs.nps_respostas (cod_nps_pesquisa,
                                  cod_colaborador_respostas,
                                  data_hora_realizacao_pesquisa,
                                  resposta_pergunta_escala,
                                  resposta_pergunta_descritiva,
                                  origem_resposta)
    values (f_cod_pesquisa_nps,
            f_cod_colaborador_realizacao,
            f_data_hora_realizacao_pesquisa,
            f_resposta_pergunta_escala,
            f_resposta_pergunta_descritiva,
            f_origem_resposta) returning codigo into cod_respostas_pesquisa_nps;

    if not FOUND
    then
        raise exception 'Erro ao inserir respostas da pesquisa de NPS % para colaborador %.',
            f_cod_pesquisa_nps,
            f_cod_colaborador_realizacao;
    end if;

    return cod_respostas_pesquisa_nps;
end;
$$;

create or replace function cs.func_nps_bloqueia_pesquisa(f_cod_pesquisa_nps bigint,
                                                         f_cod_colaborador_bloqueio bigint,
                                                         f_data_hora_bloqueio_pesquisa timestamp with time zone,
                                                         f_origem_bloqueio_pesquisa text)
    returns void
    language plpgsql
as
$$
begin
    -- Propositalmente, não tratamos a constraint de UNIQUE aqui. O front deve tratar para não enviar duplicados.
    insert into cs.nps_bloqueio_pesquisa_colaborador (cod_nps_pesquisa,
                                                      cod_colaborador_bloqueio,
                                                      data_hora_bloqueio_pesquisa,
                                                      origem_bloqueio)
    values (f_cod_pesquisa_nps,
            f_cod_colaborador_bloqueio,
            f_data_hora_bloqueio_pesquisa,
            f_origem_bloqueio_pesquisa);

    if not found
    then
        raise exception 'Erro ao bloquear pesquisa de NPS % para colaborador %.',
            f_cod_pesquisa_nps,
            f_cod_colaborador_bloqueio;
    end if;
end;
$$;