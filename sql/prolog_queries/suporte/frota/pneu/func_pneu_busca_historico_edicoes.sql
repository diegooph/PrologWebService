create or replace function suporte.func_pneu_busca_historico_edicoes(f_cod_pneu varchar(255))
    returns table
            (
                data_hora_utc                      timestamp with time zone,
                operacao                           varchar,
                codigo_pneu                        varchar(255),
                cod_modelo                         bigint,
                cod_dimensao                       bigint,
                pressao_recomendada                real,
                pressao_atual                      real,
                altura_sulco_interno               real,
                altura_sulco_central_interno       real,
                altura_sulco_externo               real,
                cod_unidade                        bigint,
                status                             varchar(255),
                vida_atual                         integer,
                vida_total                         integer,
                cod_modelo_banda                   bigint,
                altura_sulco_central_externo       real,
                dot                                varchar(20),
                valor                              real,
                data_hora_cadastro                 timestamp with time zone,
                pneu_novo_nunca_rodado             boolean,
                codigo                             bigint,
                cod_empresa                        bigint,
                cod_unidade_cadastro               integer,
                deletado                           boolean,
                data_hora_deletado                 timestamp with time zone,
                pg_username_delecao                text,
                motivo_delecao                     text,
                origem_cadastro                    text,
                cod_colaborador_cadastro           bigint,
                cod_colaborador_ultima_atualizacao bigint
            )
    language plpgsql
as
$$
begin
return query
select pd.data_hora_utc       as data_hora_log,
       pd.operacao            as operacao,
       (jsonb_populate_record(NULL::pneu_data, pd.row_log)).*
from audit.pneu_data_audit pd
where pd.row_log ->> 'codigo_cliente' = f_cod_pneu
order by pd.row_log -> 'codigo', pd.data_hora_utc asc;
end
$$;