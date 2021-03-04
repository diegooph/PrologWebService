do
$$
    declare
        v_observacao constant text not null := 'Deleção criada anteriormente a inclusão desta tabela. Faz parte da migração de dados';
        v_origem_acao constant text not null := 'SUPORTE';
        v_acao_executada constant text not null := 'DELETADO';
    begin
       insert into checklist_delecao(cod_checklist,
                                      data_hora,
                                      acao_executada,
                                      origem_delecao,
                                      observacao,
                                      pg_username_delecao)
        select
            cd.codigo                                       as cod_checklist,
            case
                when cd.data_hora_deletado is null then
                now()
                else cd.data_hora_deletado
            end                                             as data_hora,
            v_acao_executada                                as acao_executada,
            v_origem_acao                                   as origem_delecao,
            v_observacao                                    as observacao,
            case
                when cd.pg_username_delecao is null then
                session_user
                else cd.pg_username_delecao
                end                                         as pg_username_delecao
        from checklist_data cd
        where cd.deletado = true;
    end
$$
language 'plpgsql';

alter table checklist_data drop column if exists pg_username_delecao;
alter table checklist_data drop column if exists data_hora_deletado;
alter table checklist_data drop column if exists motivo_delecao;