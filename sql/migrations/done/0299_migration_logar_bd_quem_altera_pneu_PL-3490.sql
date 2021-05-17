alter table pneu_data
    add column cod_colaborador_cadastro bigint;

alter table pneu_data
    add column cod_colaborador_ultima_atualizacao bigint;

create or replace view pneu as
select p.codigo_cliente,
       p.cod_modelo,
       p.cod_dimensao,
       p.pressao_recomendada,
       p.pressao_atual,
       p.altura_sulco_interno,
       p.altura_sulco_central_interno,
       p.altura_sulco_externo,
       p.cod_unidade,
       p.status,
       p.vida_atual,
       p.vida_total,
       p.cod_modelo_banda,
       p.altura_sulco_central_externo,
       p.dot,
       p.valor,
       p.data_hora_cadastro,
       p.pneu_novo_nunca_rodado,
       p.codigo,
       p.cod_empresa,
       p.cod_unidade_cadastro,
       p.origem_cadastro,
       p.cod_colaborador_cadastro,
       p.cod_colaborador_ultima_atualizacao
from pneu_data p
where p.deletado = false;

create or replace function func_pneu_atualiza(f_cod_cliente text,
                                              f_cod_modelo bigint,
                                              f_cod_dimensao bigint,
                                              f_cod_modelo_banda bigint,
                                              f_dot text,
                                              f_valor numeric,
                                              f_vida_total int,
                                              f_pressao_recomendada double precision,
                                              f_cod_original_pneu bigint,
                                              f_cod_unidade bigint,
                                              f_cod_colaborador_responsavel_edicao bigint)
    returns void
    language plpgsql
as
$$
declare
    v_cod_cliente         text;
    v_cod_modelo          bigint;
    v_cod_dimensao        bigint;
    v_cod_modelo_banda    bigint;
    v_dot                 text;
    v_valor               numeric;
    v_vida_total          int;
    v_pressao_recomendada double precision;
    v_cod_unidade         bigint;
begin
    select codigo_cliente,
           cod_modelo,
           cod_dimensao,
           cod_modelo_banda,
           coalesce(dot, ''),
           valor,
           vida_total,
           pressao_recomendada,
           cod_unidade
    into strict v_cod_cliente,
        v_cod_modelo,
        v_cod_dimensao,
        v_cod_modelo_banda,
        v_dot,
        v_valor,
        v_vida_total,
        v_pressao_recomendada,
        v_cod_unidade
    from pneu
    where codigo = f_cod_original_pneu;

    if v_cod_cliente != f_cod_cliente
        or v_cod_modelo != f_cod_modelo
        or v_cod_dimensao != f_cod_dimensao
        or v_cod_modelo_banda != f_cod_modelo_banda
        or v_dot != f_dot
        or v_valor != f_valor
        or v_vida_total != f_vida_total
        or v_pressao_recomendada != f_pressao_recomendada
        or v_cod_unidade != f_cod_unidade
    then
        update pneu
        set codigo_cliente                     = f_cod_cliente,
            cod_modelo                         = f_cod_modelo,
            cod_dimensao                       = f_cod_dimensao,
            cod_modelo_banda                   = f_cod_modelo_banda,
            dot                                = f_dot,
            valor                              = f_valor,
            vida_total                         = f_vida_total,
            pressao_recomendada                = f_pressao_recomendada,
            cod_colaborador_ultima_atualizacao = f_cod_colaborador_responsavel_edicao
        where codigo = f_cod_original_pneu
          and cod_unidade = f_cod_unidade;
    end if;
end
$$;

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