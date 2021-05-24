create or replace function suporte.func_colaborador_busca_historico_edicoes(f_cod_colaborador bigint)
    returns table
            (
                cod_log                               bigint,
                data_hora_ultima_atualizacao          timestamp without time zone,
                cpf_colaborador_ultima_atualizacao    bigint,
                nome_colaborador_ultima_atualizacao   text,
                status_colaborador_ultima_atualizacao boolean,
                cpf                                   bigint,
                matricula_ambev                       integer,
                matricula_trans                       integer,
                data_nascimento                       date,
                data_admissao                         date,
                data_demissao                         date,
                status_ativo                          boolean,
                nome                                  varchar(255),
                cod_equipe                            bigint,
                cod_funcao                            integer,
                cod_unidade                           integer,
                cod_permissao                         bigint,
                cod_empresa                           bigint,
                cod_setor                             bigint,
                pis                                   varchar(11),
                data_hora_cadastro                    timestamp with time zone,
                codigo                                bigint,
                cod_unidade_cadastro                  integer,
                deletado                              boolean,
                data_hora_deletado                    timestamp with time zone,
                pg_username_delecao                   text,
                motivo_delecao                        text,
                cod_colaborador_cadastro              bigint,
                cod_colaborador_ultima_atualizacao    bigint
            )
    language plpgsql
    security definer
as
$$
begin
    return query
        select cd.codigo::bigint                                       as cod_log,
               cd.data_hora_utc at time zone tz_unidade(c.cod_unidade) as data_hora_ultima_atualizacao,
               c.cpf                                                   as cpf_colaborador_ultima_atualizacao,
               c.nome::text                                            as nome_colaborador_ultima_atualizacao,
               c.status_ativo                                          as status_colaborador_ultima_atualizacao,
               (jsonb_populate_record(NULL::colaborador_data, cd.row_log)).*
        from audit.colaborador_data_audit cd
                 left join colaborador_data c
                           on c.codigo = (cd.row_log ->> 'cod_colaborador_ultima_atualizacao')::bigint
        where (cd.row_log ->> 'codigo')::bigint = f_cod_colaborador
        order by cd.codigo desc;
end
$$;