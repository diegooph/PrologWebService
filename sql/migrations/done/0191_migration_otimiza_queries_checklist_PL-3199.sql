-- Esta migração otimiza as seguintes queries:
-- func_checklist_get_all_checklists_realizados.
-- func_checklist_get_modelos_selecao_realizacao.

-- Otimiza query — criado CTE.
-- Antes:
-- Planning Time: 1.793 ms
-- Execution Time: 63.924 ms

-- Depois:
-- Planning Time: 0.039 ms
-- Execution Time: 8.027 ms
create or replace function func_checklist_get_all_checklists_realizados(f_cod_unidade bigint,
                                                                        f_cod_equipe bigint,
                                                                        f_cod_tipo_veiculo bigint,
                                                                        f_cod_veiculo bigint,
                                                                        f_data_inicial date,
                                                                        f_data_final date,
                                                                        f_timezone text,
                                                                        f_limit integer,
                                                                        f_offset bigint)
    returns table
            (
                cod_checklist                 bigint,
                cod_checklist_modelo          bigint,
                cod_versao_checklist_modelo   bigint,
                data_hora_realizacao          timestamp without time zone,
                data_hora_importado_prolog    timestamp without time zone,
                km_veiculo_momento_realizacao bigint,
                duracao_realizacao_millis     bigint,
                cod_colaborador               bigint,
                cpf_colaborador               bigint,
                nome_colaborador              text,
                cod_veiculo                   bigint,
                placa_veiculo                 text,
                identificador_frota           text,
                tipo_checklist                character,
                total_perguntas_ok            smallint,
                total_perguntas_nok           smallint,
                total_alternativas_ok         smallint,
                total_alternativas_nok        smallint,
                total_midias_perguntas_ok     smallint,
                total_midias_alternativas_nok smallint,
                total_nok_baixa               smallint,
                total_nok_alta                smallint,
                total_nok_critica             smallint
            )
    language sql
as
$$
with checks as (
    select *
    from checklist c
    where c.cod_unidade = f_cod_unidade
      and c.data_hora_realizacao_tz_aplicado :: date >= f_data_inicial
      and c.data_hora_realizacao_tz_aplicado :: date <= f_data_final
    order by c.data_hora_sincronizacao desc
    limit f_limit offset f_offset
)
select c.codigo                                             as cod_checklist,
       c.cod_checklist_modelo                               as cod_checklist_modelo,
       c.cod_versao_checklist_modelo                        as cod_versao_checklist_modelo,
       c.data_hora_realizacao_tz_aplicado                   as data_hora_realizacao,
       c.data_hora_importado_prolog at time zone f_timezone as data_hora_importado_prolog,
       c.km_veiculo                                         as km_veiculo_momento_realizacao,
       c.tempo_realizacao                                   as duracao_realizacao_millis,
       co.codigo                                            as cod_colaborador,
       c.cpf_colaborador                                    as cpf_colaborador,
       co.nome :: text                                      as nome_colaborador,
       v.codigo                                             as cod_veiculo,
       v.placa :: text                                      as placa_veiculo,
       v.identificador_frota :: text                        as identificador_frota,
       c.tipo                                               as tipo_checklist,
       c.total_perguntas_ok                                 as total_perguntas_ok,
       c.total_perguntas_nok                                as total_perguntas_nok,
       c.total_alternativas_ok                              as total_alternativas_ok,
       c.total_alternativas_nok                             as total_alternativas_nok,
       c.total_midias_perguntas_ok                          as total_midias_perguntas_ok,
       c.total_midias_alternativas_nok                      as total_midias_alternativas_nok,
       (select count(*)
        from checklist_respostas_nok crn
                 join checklist_alternativa_pergunta cap
                      on crn.cod_alternativa = cap.codigo
        where crn.cod_checklist = c.codigo
          and cap.prioridade = 'BAIXA') :: smallint         as total_baixa,
       (select count(*)
        from checklist_respostas_nok crn
                 join checklist_alternativa_pergunta cap
                      on crn.cod_alternativa = cap.codigo
        where crn.cod_checklist = c.codigo
          and cap.prioridade = 'ALTA') :: smallint          as total_alta,
       (select count(*)
        from checklist_respostas_nok crn
                 join checklist_alternativa_pergunta cap
                      on crn.cod_alternativa = cap.codigo
        where crn.cod_checklist = c.codigo
          and cap.prioridade = 'CRITICA') :: smallint       as total_critica
from checks c
         join colaborador co
              on co.cpf = c.cpf_colaborador
         join equipe e
              on e.codigo = co.cod_equipe
         join veiculo v
              on v.placa = c.placa_veiculo
where case when f_cod_equipe is null then true else e.codigo = f_cod_equipe end
  and case when f_cod_tipo_veiculo is null then true else v.cod_tipo = f_cod_tipo_veiculo end
  and case when f_cod_veiculo is null then true else v.codigo = f_cod_veiculo end
order by c.data_hora_sincronizacao desc
$$;

-- Otimza query — remove join com 'veiculo_tipo' e altera filtragens.
-- Antes:
-- Planning Time: 1.691 ms
-- Execution Time: 14.251 ms

-- Depois:
-- Planning Time: 1.098 ms
-- Execution Time: 4.667 ms
create or replace function func_checklist_get_modelos_selecao_realizacao(f_cod_unidade bigint,
                                                                         f_cod_cargo bigint)
    returns table
            (
                cod_modelo              bigint,
                cod_versao_atual_modelo bigint,
                cod_unidade_modelo      bigint,
                nome_modelo             text,
                cod_veiculo             bigint,
                placa_veiculo           text,
                km_atual_veiculo        bigint
            )
    language sql
as
$$
select cm.codigo           as cod_modelo,
       cm.cod_versao_atual as cod_versao_atual_modelo,
       cm.cod_unidade      as cod_unidade_modelo,
       cm.nome :: text     as nome_modelo,
       v.codigo            as cod_veiculo,
       v.placa :: text     as placa_veiculo,
       v.km                as km_atual_veiculo
from checklist_modelo cm
         join checklist_modelo_funcao cmf
              on cmf.cod_checklist_modelo = cm.codigo
         join checklist_modelo_veiculo_tipo cmvt
              on cmvt.cod_modelo = cm.codigo
         join veiculo v
              on v.cod_tipo = cmvt.cod_tipo_veiculo
-- Os parâmetros de filtro estão nessa ordem pois assim geramos o melhor desempenho. Inclusive o filtro
-- 'cmf.cod_unidade = f_cod_unidade' que pode não parecer útil acaba influenciando o query planner a usar a PK da
-- 'checklist_modelo_funcao', portanto, só altere se tiver certeza que não prejudicará desempenho.
where v.cod_unidade = f_cod_unidade
  and v.status_ativo = true
  and cmf.cod_funcao = f_cod_cargo
  and cmf.cod_unidade = f_cod_unidade
  and cm.status_ativo = true
order by cm.codigo, v.placa;
$$;