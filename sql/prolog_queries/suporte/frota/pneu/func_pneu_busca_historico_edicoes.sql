create or replace function suporte.func_pneu_busca_historico_edicoes(f_cod_pneu bigint)
    returns table
            (
                cod_log                               bigint,
                data_hora_ultima_atualizacao          timestamp without time zone,
                cpf_colaborador_ultima_atualizacao    bigint,
                nome_colaborador_ultima_atualizacao   text,
                status_colaborador_ultima_atualizacao boolean,
                codigo_pneu                           varchar(255),
                cod_modelo                            bigint,
                cod_dimensao                          bigint,
                pressao_recomendada                   real,
                pressao_atual                         real,
                altura_sulco_interno                  real,
                altura_sulco_central_interno          real,
                altura_sulco_externo                  real,
                cod_unidade                           bigint,
                status                                varchar(255),
                vida_atual                            integer,
                vida_total                            integer,
                cod_modelo_banda                      bigint,
                altura_sulco_central_externo          real,
                dot                                   varchar(20),
                valor                                 real,
                data_hora_cadastro                    timestamp with time zone,
                pneu_novo_nunca_rodado                boolean,
                codigo                                bigint,
                cod_empresa                           bigint,
                cod_unidade_cadastro                  integer,
                deletado                              boolean,
                data_hora_deletado                    timestamp with time zone,
                pg_username_delecao                   text,
                motivo_delecao                        text,
                origem_cadastro                       text,
                cod_colaborador_cadastro              bigint,
                cod_colaborador_ultima_atualizacao    bigint
            )
    language plpgsql
as
$$
begin
return query
select pd.codigo::bigint                                       as cod_log,
        pd.data_hora_utc at time zone tz_unidade(c.cod_unidade) as data_hora_ultima_atualizacao,
       c.cpf                                                   as cpf_colaborador_ultima_atualizacao,
       c.nome::text                                            as nome_colaborador_ultima_atualizacao,
        c.status_ativo                                          as status_colaborador_ultima_atualizacao,
       (jsonb_populate_record(NULL::pneu_data, pd.row_log)).*
from audit.pneu_data_audit pd
    left join colaborador_data c
on c.codigo = (pd.row_log ->> 'cod_colaborador_ultima_atualizacao')::bigint
where (pd.row_log ->> 'codigo')::bigint = f_cod_pneu
order by pd.codigo desc;
end
$$;