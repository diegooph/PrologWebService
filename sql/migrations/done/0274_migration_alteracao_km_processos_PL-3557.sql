drop function func_veiculo_salva_historico_km_propagacao(f_cod_unidade bigint,
    f_cod_historico_processo_acoplamento bigint,
    f_cod_processo_acoplamento bigint,
    f_cod_veiculo_propagado bigint,
    f_motorizado boolean,
    f_veiculo_fonte_processo boolean,
    f_km_antigo bigint,
    f_km_final bigint,
    f_km_coletado bigint,
    f_tipo_processo types.veiculo_processo_type,
    f_cod_processo bigint,
    f_data_hora timestamp with time zone);
create function func_veiculo_salva_historico_km_propagacao(f_cod_unidade bigint,
                                                           f_cod_historico_processo_acoplamento bigint,
                                                           f_cod_processo_acoplamento bigint,
                                                           f_cod_veiculo_propagado bigint,
                                                           f_motorizado boolean,
                                                           f_veiculo_fonte_processo boolean,
                                                           f_km_antigo bigint,
                                                           f_km_final bigint,
                                                           f_km_coletado bigint,
                                                           f_tipo_processo text,
                                                           f_cod_processo bigint,
                                                           f_data_hora timestamp with time zone)
    returns bigint
    language plpgsql
as
$$
declare
    v_cod_historico_propagacao bigint;
begin
    insert into veiculo_processo_km_historico (cod_unidade,
                                               cod_historico_processo_acoplamento,
                                               cod_processo_acoplamento,
                                               cod_processo_veiculo,
                                               tipo_processo_veiculo,
                                               cod_veiculo,
                                               motorizado,
                                               veiculo_fonte_processo,
                                               km_antigo,
                                               km_final,
                                               km_coletado_processo,
                                               data_hora_processo)
    values (f_cod_unidade,
            f_cod_historico_processo_acoplamento,
            f_cod_processo_acoplamento,
            f_cod_processo,
            f_tipo_processo,
            f_cod_veiculo_propagado,
            f_motorizado,
            f_veiculo_fonte_processo,
            f_km_antigo,
            f_km_final,
            f_km_coletado,
            f_data_hora)
    returning codigo into v_cod_historico_propagacao;
    return v_cod_historico_propagacao;
end;
$$;

drop function func_veiculo_update_km_atual(f_cod_unidade bigint,
    f_cod_veiculo bigint,
    f_km_coletado bigint,
    f_cod_processo bigint,
    f_tipo_processo types.veiculo_processo_type,
    f_deve_propagar_km boolean,
    f_data_hora timestamp with time zone);
create or replace function func_veiculo_update_km_atual(f_cod_unidade bigint,
                                                        f_cod_veiculo bigint,
                                                        f_km_coletado bigint,
                                                        f_cod_processo bigint,
                                                        f_tipo_processo text,
                                                        f_deve_propagar_km boolean,
                                                        f_data_hora timestamp with time zone)
    returns bigint
    language plpgsql
as
$$
declare
    v_km_atual                           bigint;
    v_diferenca_km                       bigint;
    v_km_motorizado                      bigint;
    v_possui_hubodometro                 boolean;
    v_motorizado                         boolean;
    v_cod_processo_acoplamento           bigint;
    v_cod_historico_processo_acoplamento bigint[];
    v_cod_veiculos_acoplados             bigint[];
    v_km_veiculos_acoplados              bigint[];
    v_veiculos_motorizados               boolean[];
    v_cod_empresa                        bigint;
begin
    select v.km, v.possui_hubodometro, v.motorizado, vaa.cod_processo, v.cod_empresa
    from veiculo v
             left join veiculo_acoplamento_atual vaa on v.codigo = vaa.cod_veiculo
    where v.codigo = f_cod_veiculo
    into strict v_km_atual, v_possui_hubodometro, v_motorizado, v_cod_processo_acoplamento, v_cod_empresa;

    case when exists(select vael.cod_empresa
                     from veiculo_acoplamento_empresa_liberada vael
                     where vael.cod_empresa = v_cod_empresa)
        then
            f_deve_propagar_km = true;
        else
            f_deve_propagar_km = false;
        end case;

    if not f_deve_propagar_km
    then
        if v_km_atual < f_km_coletado
        then
            update veiculo set km = f_km_coletado where codigo = f_cod_veiculo;
            return f_km_coletado;
        else
            return f_km_coletado;
        end if;
    end if;

    case when (f_km_coletado is not null) then
        case when ((v_motorizado is true or v_possui_hubodometro is true) and v_km_atual > f_km_coletado)
            then
                return f_km_coletado;
            else
                if (v_cod_processo_acoplamento is not null)
                then
                    select array_agg(vaa.cod_veiculo), array_agg(v.motorizado), array_agg(v.km), array_agg(vah.codigo)
                    from veiculo_acoplamento_atual vaa
                             join veiculo v
                                  on vaa.cod_unidade = v.cod_unidade
                                      and vaa.cod_veiculo = v.codigo
                             inner join veiculo_acoplamento_historico vah on vaa.cod_processo = vah.cod_processo
                        and vaa.cod_veiculo = vah.cod_veiculo
                    where vaa.cod_unidade = f_cod_unidade
                      and vaa.cod_processo = v_cod_processo_acoplamento
                      and v.possui_hubodometro is false
                    into v_cod_veiculos_acoplados,
                        v_veiculos_motorizados,
                        v_km_veiculos_acoplados,
                        v_cod_historico_processo_acoplamento;
                end if;
                case when (v_possui_hubodometro is false and v_motorizado is false and
                           v_cod_processo_acoplamento is null)
                    then
                        perform func_veiculo_salva_historico_km_propagacao(
                                f_cod_unidade,
                                null,
                                v_cod_processo_acoplamento,
                                f_cod_veiculo,
                                v_motorizado,
                                true,
                                v_km_atual,
                                v_km_atual,
                                f_km_coletado,
                                f_tipo_processo,
                                f_cod_processo,
                                f_data_hora);
                        return v_km_atual;
                    else
                        case when (v_possui_hubodometro is true or
                                   (v_motorizado is true and v_cod_processo_acoplamento is null))
                            then
                                update veiculo set km = f_km_coletado where codigo = f_cod_veiculo;
                                return f_km_coletado;
                            else
                                case when (v_possui_hubodometro is false and v_cod_processo_acoplamento is not null)
                                    then
                                        case when (v_motorizado is true)
                                            then
                                                v_diferenca_km = f_km_coletado - v_km_atual;
                                            else
                                                v_km_motorizado = (select v.km
                                                                   from veiculo v
                                                                   where v.cod_unidade = f_cod_unidade
                                                                     and v.codigo = any (v_cod_veiculos_acoplados)
                                                                     and v.motorizado is true);
                                                case when (v_km_motorizado > f_km_coletado)
                                                    then
                                                        perform func_veiculo_salva_historico_km_propagacao(
                                                                f_cod_unidade,
                                                                unnest(v_cod_historico_processo_acoplamento),
                                                                v_cod_processo_acoplamento,
                                                                unnest(v_cod_veiculos_acoplados),
                                                                unnest(v_veiculos_motorizados),
                                                                (unnest(v_cod_veiculos_acoplados) = f_cod_veiculo),
                                                                unnest(v_km_veiculos_acoplados),
                                                                unnest(v_km_veiculos_acoplados),
                                                                f_km_coletado,
                                                                f_tipo_processo,
                                                                f_cod_processo,
                                                                f_data_hora);
                                                        return v_km_atual;
                                                    else
                                                        v_diferenca_km = f_km_coletado - v_km_motorizado;
                                                    end case;
                                            end case;
                                        case when (v_diferenca_km is not null)
                                            then
                                                update veiculo v
                                                set km = km + v_diferenca_km
                                                where v.codigo = any (v_cod_veiculos_acoplados);
                                                perform func_veiculo_salva_historico_km_propagacao(
                                                        f_cod_unidade,
                                                        unnest(v_cod_historico_processo_acoplamento),
                                                        v_cod_processo_acoplamento,
                                                        unnest(v_cod_veiculos_acoplados),
                                                        unnest(v_veiculos_motorizados),
                                                        (unnest(v_cod_veiculos_acoplados) = f_cod_veiculo),
                                                        unnest(v_km_veiculos_acoplados),
                                                        unnest(v_km_veiculos_acoplados) + v_diferenca_km,
                                                        f_km_coletado,
                                                        f_tipo_processo,
                                                        f_cod_processo,
                                                        f_data_hora);
                                                return v_km_atual + v_diferenca_km;
                                            else
                                                return v_km_atual;
                                            end case;
                                    end case;
                            end case;
                    end case;
            end case;
        else
            return v_km_atual;
        end case;
end;
$$;

-- Será recriada no fim da migration pois mais coisas mudaram.
drop function func_checklist_os_resolver_itens(f_cod_unidade bigint,
    f_cod_veiculo bigint,
    f_cod_itens bigint[],
    f_cpf bigint,
    f_tempo_realizacao bigint,
    f_km bigint,
    f_status_resolucao text,
    f_data_hora_conserto timestamp with time zone,
    f_data_hora_inicio_resolucao timestamp with time zone,
    f_data_hora_fim_resolucao timestamp with time zone,
    f_feedback_conserto text);

drop view veiculo_km_propagacao;

alter table if exists veiculo_processo_km_historico
    alter column tipo_processo_veiculo set data type text
        using tipo_processo_veiculo::text;

alter table if exists types.veiculo_processo
    alter column processo type text
        using processo::text;

alter table veiculo_processo_km_historico
    add constraint fk_tipo_processo_alterado
        foreign key (tipo_processo_veiculo) references types.veiculo_processo (processo);

create or replace view veiculo_km_propagacao as
select codigo,
       cod_veiculo,
       cod_processo_acoplamento,
       cod_historico_processo_acoplamento,
       cod_processo_veiculo,
       tipo_processo_veiculo,
       cod_unidade,
       veiculo_fonte_processo,
       motorizado,
       km_antigo,
       km_final,
       km_coletado_processo,
       data_hora_processo
from veiculo_processo_km_historico
where km_antigo <> km_final
  and veiculo_fonte_processo = false;

drop type types.veiculo_processo_type;

-- Altera para retornar o código do processo de movimentação nas evoluções de KM referentes a movimentação.
-- Antes retornava o código da tabela 'movimentacao'.
create or replace function func_veiculo_busca_evolucao_km_consolidado(f_cod_empresa bigint,
                                                                      f_cod_veiculo bigint,
                                                                      f_data_inicial date,
                                                                      f_data_final date)
    returns table
            (
                processo                       text,
                cod_processo                   bigint,
                data_hora                      timestamp without time zone,
                placa                          varchar(7),
                km_coletado                    bigint,
                variacao_km_entre_coletas      bigint,
                km_atual                       bigint,
                diferenca_km_atual_km_coletado bigint
            )
    language plpgsql
as
$$
declare
    v_cod_unidades constant bigint[] not null := (select array_agg(u.codigo)
                                                  from unidade u
                                                  where u.cod_empresa = f_cod_empresa);
    v_check_data            boolean not null  := f_if(f_data_inicial is null or f_data_final is null,
                                                      false,
                                                      true);
begin
    return query
        with dados as (
            (select distinct on (mp.codigo) 'MOVIMENTACAO'                              as processo,
                                            mp.codigo                                   as codigo,
                                            mp.data_hora at time zone tz_unidade(mp.cod_unidade)
                                                                                        as data_hora,
                                            coalesce(v_origem.codigo, v_destino.codigo) as cod_veiculo,
                                            coalesce(mo.km_veiculo, md.km_veiculo)      as km_coletado
             from movimentacao_processo mp
                      join movimentacao m on mp.codigo = m.cod_movimentacao_processo
                 and mp.cod_unidade = m.cod_unidade
                      join movimentacao_destino md on m.codigo = md.cod_movimentacao
                      join veiculo v_destino on v_destino.codigo = md.cod_veiculo
                      join movimentacao_origem mo on m.codigo = mo.cod_movimentacao
                      join veiculo v_origem on v_origem.codigo = mo.cod_veiculo
             where coalesce(mo.cod_veiculo, md.cod_veiculo) = f_cod_veiculo
             group by m.codigo, mp.cod_unidade, mp.codigo, v_origem.codigo, v_destino.codigo, mo.km_veiculo,
                      md.km_veiculo)
            union
            (select 'CHECKLIST'                                        as processo,
                    c.codigo                                           as codigo,
                    c.data_hora at time zone tz_unidade(c.cod_unidade) as data_hora,
                    c.cod_veiculo                                      as cod_veiculo,
                    c.km_veiculo                                       as km_coletado
             from checklist c
             where c.cod_veiculo = f_cod_veiculo
               and c.cod_unidade = any (v_cod_unidades)
             union
             (select 'AFERICAO'                                         as processo,
                     a.codigo                                           as codigo,
                     a.data_hora at time zone tz_unidade(a.cod_unidade) as data_hora,
                     a.cod_veiculo                                      as cod_veiculo,
                     a.km_veiculo                                       as km_coletado
              from afericao a
              where a.cod_veiculo = f_cod_veiculo
                and a.cod_unidade = any (v_cod_unidades)
             )
             union
             (select 'FECHAMENTO_SERVICO_PNEU'                                     as processo,
                     am.codigo                                                     as codigo,
                     am.data_hora_resolucao at time zone tz_unidade(a.cod_unidade) as data_hora,
                     a.cod_veiculo                                                 as cod_veiculo,
                     am.km_momento_conserto                                        as km_coletado
              from afericao a
                       join afericao_manutencao am on a.codigo = am.cod_afericao
              where a.cod_veiculo = f_cod_veiculo
                and am.cod_unidade = any (v_cod_unidades)
                and am.data_hora_resolucao is not null
             )
             union
             (select 'FECHAMENTO_ITEM_CHECKLIST'                                    as processo,
                     cosi.codigo                                                    as codigo,
                     cosi.data_hora_conserto at time zone tz_unidade(c.cod_unidade) as data_hora,
                     c.cod_veiculo                                                  as cod_veiculo,
                     cosi.km                                                        as km_coletado
              from checklist c
                       join checklist_ordem_servico cos on cos.cod_checklist = c.codigo
                       join checklist_ordem_servico_itens cosi
                            on cosi.cod_os = cos.codigo and cosi.cod_unidade = cos.cod_unidade
              where c.cod_veiculo = f_cod_veiculo
                and cosi.status_resolucao = 'R'
                and cosi.cod_unidade = any (v_cod_unidades)
              order by cosi.data_hora_fim_resolucao)
             union
             (select 'TRANSFERENCIA_DE_VEICULOS'             as processo,
                     vtp.codigo                              as codigo,
                     vtp.data_hora_transferencia_processo at time zone
                     tz_unidade(vtp.cod_unidade_colaborador) as data_hora,
                     vti.cod_veiculo                         as cod_veiculo,
                     vti.km_veiculo_momento_transferencia    as km_coletado
              from veiculo_transferencia_processo vtp
                       join veiculo_transferencia_informacoes vti on vtp.codigo = vti.cod_processo_transferencia
              where vti.cod_veiculo = f_cod_veiculo
                and vtp.cod_unidade_destino = any (v_cod_unidades)
                and vtp.cod_unidade_origem = any (v_cod_unidades)
             )
             union
             (select 'SOCORRO_EM_ROTA'                                              as processo,
                     sra.cod_socorro_rota                                           as codigo,
                     sra.data_hora_abertura at time zone tz_unidade(sr.cod_unidade) as data_hora,
                     sra.cod_veiculo_problema                                       as cod_veiculo,
                     sra.km_veiculo_abertura                                        as km_coletado
              from socorro_rota_abertura sra
                       join socorro_rota sr on sra.cod_socorro_rota = sr.codigo
              where sra.cod_veiculo_problema = f_cod_veiculo
                and sr.cod_unidade = any (v_cod_unidades)
             )
             union
             (select distinct on (func.km_veiculo) 'EDICAO_DE_VEICULOS'       as processo,
                                                   func.codigo_historico      as codigo,
                                                   func.data_hora_edicao      as data_hora,
                                                   func.codigo_veiculo_edicao as cod_veiculo,
                                                   func.km_veiculo            as km_coletado
              from func_veiculo_listagem_historico_edicoes(f_cod_empresa, f_cod_veiculo) as func
              where func.codigo_historico is not null
             )
            )
        )
        select d.processo,
               d.codigo,
               d.data_hora,
               v.placa,
               d.km_coletado,
               d.km_coletado - lag(d.km_coletado) over (order by d.data_hora) as variacao_entre_coletas,
               v.km                                                           as km_atual,
               (v.km - d.km_coletado)                                         as diferenca_atual_coletado
        from dados d
                 join veiculo v on v.codigo = d.cod_veiculo
        where f_if(v_check_data, d.data_hora :: date between f_data_inicial and f_data_final, true)
        order by row_number() over () desc;
end;
$$;

create table public.veiculo_processo_alteracao_km
(
    codigo                    bigserial                not null,
    cod_colaborador_alteracao bigint,
    data_hora_alteracao       timestamp with time zone not null,
    origem_alteracao          text                     not null,
    cod_processo_alterado     bigint                   not null,
    tipo_processo_alterado    text                     not null,
    km_antigo                 bigint                   not null,
    km_novo                   bigint                   not null,
    constraint pk_veiculo_processo_alteracao_km primary key (codigo),
    constraint fk_colaborador foreign key (cod_colaborador_alteracao) references colaborador_data (codigo),
    constraint fk_origem_alteracao foreign key (origem_alteracao) references types.origem_acao_type (origem_acao),
    constraint fk_tipo_processo_alterado foreign key (tipo_processo_alterado) references types.veiculo_processo (processo),
    constraint check_km_antigo_diferente_novo check ( km_antigo <> km_novo ),
    constraint check_apenas_processos_alteracao_km_permitida check
        ( tipo_processo_alterado not in ('ACOPLAMENTO', 'EDICAO_DE_VEICULOS') ),
    constraint check_colaborador_alteracao_preenchido check (
            (origem_alteracao = 'PROLOG_WEB'
                and cod_colaborador_alteracao is not null) or
            (origem_alteracao <> 'PROLOG_WEB'
                and cod_colaborador_alteracao is null))
);

-- Fora do escopo dessa tarefa, mas o nome da coluna 'cod_processo' na tabela 'checklist_ordem_servico_itens'
-- será melhorado para 'cod_agrupamento_fechamento_em_lote'.
drop view estratificacao_os;
drop view checklist_ordem_servico_itens;

alter table checklist_ordem_servico_itens_data
    rename column cod_processo to cod_agrupamento_resolucao_em_lote;
comment on column checklist_ordem_servico_itens_data.cod_agrupamento_resolucao_em_lote is
    'Como os itens de ordem de serviço podem ser resolvidos em lote e como um único lote pode conter itens de diferentes
    OSs, precisamos de um jeito de identificar quais itens foram resolvidos juntos. Essa é a utilidade desta coluna.
    Ela não possui FK com tabela nenhuma. É apenas um código único gerado através da sequence: codigo_resolucao_item_os';

create or replace view checklist_ordem_servico_itens as
select cosi.cod_unidade,
       cosi.codigo,
       cosi.cod_os,
       cosi.cpf_mecanico,
       cosi.cod_pergunta_primeiro_apontamento,
       cosi.cod_contexto_pergunta,
       cosi.cod_contexto_alternativa,
       cosi.cod_alternativa_primeiro_apontamento,
       cosi.status_resolucao,
       cosi.qt_apontamentos,
       cosi.km,
       cosi.data_hora_conserto,
       cosi.data_hora_inicio_resolucao,
       cosi.data_hora_fim_resolucao,
       cosi.tempo_realizacao,
       cosi.feedback_conserto,
       cosi.cod_agrupamento_resolucao_em_lote
from checklist_ordem_servico_itens_data cosi
where cosi.deletado = false;

create or replace function func_checklist_os_resolver_itens(f_cod_unidade bigint,
                                                            f_cod_veiculo bigint,
                                                            f_cod_itens bigint[],
                                                            f_cpf bigint,
                                                            f_tempo_realizacao bigint,
                                                            f_km bigint,
                                                            f_status_resolucao text,
                                                            f_data_hora_conserto timestamp with time zone,
                                                            f_data_hora_inicio_resolucao timestamp with time zone,
                                                            f_data_hora_fim_resolucao timestamp with time zone,
                                                            f_feedback_conserto text) returns bigint
    language plpgsql
as
$$
declare
    v_cod_item                                   bigint;
    v_data_realizacao_checklist                  timestamp with time zone;
    v_alternativa_item                           text;
    v_error_message                              text            := E'Erro! A data de resolução %s não pode ser anterior a data de abertura %s do item "%s".';
    v_qtd_linhas_atualizadas                     bigint;
    v_total_linhas_atualizadas                   bigint          := 0;
    v_cod_agrupamento_resolucao_em_lote constant bigint not null := (select nextval('CODIGO_RESOLUCAO_ITEM_OS'));
    v_tipo_processo                     constant text not null   := 'FECHAMENTO_ITEM_CHECKLIST';
    v_km_real                                    bigint;
begin
    v_km_real := (select *
                  from func_veiculo_update_km_atual(f_cod_unidade,
                                                    f_cod_veiculo,
                                                    f_km,
                                                    v_cod_agrupamento_resolucao_em_lote,
                                                    v_tipo_processo,
                                                    true,
                                                    CURRENT_TIMESTAMP));

    foreach v_cod_item in array f_cod_itens
        loop
            -- Busca a data de realização do check e a pergunta que originou o item de O.S.
            select c.data_hora, capd.alternativa
            from checklist_ordem_servico_itens cosi
                     join checklist_ordem_servico cos
                          on cosi.cod_os = cos.codigo and cosi.cod_unidade = cos.cod_unidade
                     join checklist c on cos.cod_checklist = c.codigo
                     join checklist_alternativa_pergunta_data capd
                          on capd.codigo = cosi.cod_alternativa_primeiro_apontamento
            where cosi.codigo = v_cod_item
            into v_data_realizacao_checklist, v_alternativa_item;

            -- Bloqueia caso a data de resolução seja menor ou igual que a data de realização do checklist
            if v_data_realizacao_checklist is not null and v_data_realizacao_checklist >= f_data_hora_inicio_resolucao
            then
                perform throw_generic_error(
                        FORMAT(v_error_message, v_data_realizacao_checklist, f_data_hora_inicio_resolucao,
                               v_alternativa_item));
            end if;

            -- Atualiza os itens
            update checklist_ordem_servico_itens
            set cpf_mecanico                      = f_cpf,
                tempo_realizacao                  = f_tempo_realizacao,
                km                                = v_km_real,
                status_resolucao                  = f_status_resolucao,
                data_hora_conserto                = f_data_hora_conserto,
                data_hora_inicio_resolucao        = f_data_hora_inicio_resolucao,
                data_hora_fim_resolucao           = f_data_hora_fim_resolucao,
                feedback_conserto                 = f_feedback_conserto,
                cod_agrupamento_resolucao_em_lote = v_cod_agrupamento_resolucao_em_lote
            where cod_unidade = f_cod_unidade
              and codigo = v_cod_item
              and data_hora_conserto is null;

            get diagnostics v_qtd_linhas_atualizadas = row_count;

            -- Verificamos se o update funcionou.
            if v_qtd_linhas_atualizadas is null or v_qtd_linhas_atualizadas <= 0
            then
                perform throw_generic_error('Erro ao marcar os itens como resolvidos.');
            end if;
            v_total_linhas_atualizadas := v_total_linhas_atualizadas + v_qtd_linhas_atualizadas;
        end loop;
    return v_total_linhas_atualizadas;
end;
$$;

CREATE OR REPLACE VIEW ESTRATIFICACAO_OS AS
SELECT COS.CODIGO                                                       AS COD_OS,
       REALIZADOR.NOME                                                  AS NOME_REALIZADOR_CHECKLIST,
       V.PLACA                                                          AS PLACA_VEICULO,
       C.KM_VEICULO                                                     AS KM,
       C.DATA_HORA_REALIZACAO_TZ_APLICADO                               AS DATA_HORA,
       C.TIPO                                                           AS TIPO_CHECKLIST,
       CP.CODIGO                                                        AS COD_PERGUNTA,
       CP.CODIGO_CONTEXTO                                               AS COD_CONTEXTO_PERGUNTA,
       CP.ORDEM                                                         AS ORDEM_PERGUNTA,
       CP.PERGUNTA,
       CP.SINGLE_CHOICE,
       NULL :: UNKNOWN                                                  AS URL_IMAGEM,
       CAP.PRIORIDADE,
       CASE CAP.PRIORIDADE
           WHEN 'CRITICA' :: TEXT
               THEN 1
           WHEN 'ALTA' :: TEXT
               THEN 2
           WHEN 'BAIXA' :: TEXT
               THEN 3
           ELSE NULL :: INTEGER
           END                                                          AS PRIORIDADE_ORDEM,
       CAP.CODIGO                                                       AS COD_ALTERNATIVA,
       CAP.CODIGO_CONTEXTO                                              AS COD_CONTEXTO_ALTERNATIVA,
       CAP.ALTERNATIVA,
       PRIO.PRAZO,
       CRN.RESPOSTA_OUTROS,
       V.COD_TIPO,
       COS.COD_UNIDADE,
       COS.STATUS                                                       AS STATUS_OS,
       COS.COD_CHECKLIST,
       TZ_UNIDADE(COS.COD_UNIDADE)                                      AS TIME_ZONE_UNIDADE,
       COSI.STATUS_RESOLUCAO                                            AS STATUS_ITEM,
       MECANICO.NOME                                                    AS NOME_MECANICO,
       COSI.CPF_MECANICO,
       COSI.TEMPO_REALIZACAO,
       COSI.DATA_HORA_CONSERTO AT TIME ZONE TZ_UNIDADE(COS.COD_UNIDADE) AS DATA_HORA_CONSERTO,
       COSI.DATA_HORA_INICIO_RESOLUCAO                                  AS DATA_HORA_INICIO_RESOLUCAO_UTC,
       COSI.DATA_HORA_FIM_RESOLUCAO                                     AS DATA_HORA_FIM_RESOLUCAO_UTC,
       COSI.KM                                                          AS KM_FECHAMENTO,
       COSI.QT_APONTAMENTOS,
       COSI.FEEDBACK_CONSERTO,
       COSI.CODIGO
FROM CHECKLIST_DATA C
         JOIN COLABORADOR REALIZADOR
              ON REALIZADOR.CPF = C.CPF_COLABORADOR
         JOIN VEICULO V
              ON V.CODIGO = C.COD_VEICULO
         JOIN CHECKLIST_ORDEM_SERVICO COS
              ON C.CODIGO = COS.COD_CHECKLIST
         JOIN CHECKLIST_ORDEM_SERVICO_ITENS COSI
              ON COS.CODIGO = COSI.COD_OS
                  AND COS.COD_UNIDADE = COSI.COD_UNIDADE
         JOIN CHECKLIST_PERGUNTAS CP
              ON CP.COD_VERSAO_CHECKLIST_MODELO = C.COD_VERSAO_CHECKLIST_MODELO
                  AND COSI.COD_CONTEXTO_PERGUNTA = CP.CODIGO_CONTEXTO
         JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
              ON CAP.COD_PERGUNTA = CP.CODIGO
                  AND COSI.COD_CONTEXTO_ALTERNATIVA = CAP.CODIGO_CONTEXTO
         JOIN CHECKLIST_ALTERNATIVA_PRIORIDADE PRIO
              ON PRIO.PRIORIDADE :: TEXT = CAP.PRIORIDADE :: TEXT
         JOIN CHECKLIST_RESPOSTAS_NOK CRN
              ON CRN.COD_CHECKLIST = C.CODIGO
                  AND CRN.COD_ALTERNATIVA = CAP.CODIGO
         LEFT JOIN COLABORADOR MECANICO ON MECANICO.CPF = COSI.CPF_MECANICO;