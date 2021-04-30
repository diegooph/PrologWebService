create or replace function suporte.func_colaborador_busca_historico_edicoes(f_cod_colaborador bigint)
    returns table
            (
                cpf                                bigint,
                matricula_ambev                    integer,
                matricula_trans                    integer,
                data_nascimento                    date,
                data_admissao                      date,
                data_demissao                      date,
                status_ativo                       boolean,
                nome                               varchar(255),
                cod_equipe                         bigint,
                cod_funcao                         integer,
                cod_unidade                        integer,
                cod_permissao                      bigint,
                cod_empresa                        bigint,
                cod_setor                          bigint,
                pis                                varchar(11),
                data_hora_cadastro                 timestamp with time zone,
                codigo                             bigint,
                cod_unidade_cadastro               integer,
                deletado                           boolean,
                data_hora_deletado                 timestamp with time zone,
                pg_username_delecao                text,
                motivo_delecao                     text,
                cod_colaborador_cadastro           bigint,
                cod_colaborador_ultima_atualizacao bigint
            )
    language plpgsql
as
$$
begin
    return query
    select (jsonb_populate_record(NULL::colaborador_data, cd.row_log)).*
    from audit.colaborador_data_audit cd
    where cd.row_log ->> 'codigo' = f_cod_colaborador::text
    order by cd.row_log -> 'codigo', cd.data_hora_utc;
end
$$;