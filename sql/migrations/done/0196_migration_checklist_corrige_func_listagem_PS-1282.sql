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
    select c.*,
           co.codigo                     as cod_colaborador,
           co.cod_equipe                 as cod_equipe_colaborador,
           co.nome :: text               as nome_colaborador,
           v.placa :: text               as placa,
           v.identificador_frota :: text as identificador_frota
    from checklist c
             join colaborador co
                  on co.cpf = c.cpf_colaborador
             join veiculo v
                  on v.codigo = c.cod_veiculo
    where c.cod_unidade = f_cod_unidade
      and c.data_hora_realizacao_tz_aplicado :: date >= f_data_inicial
      and c.data_hora_realizacao_tz_aplicado :: date <= f_data_final
      and case when f_cod_equipe is null then true else co.cod_equipe = f_cod_equipe end
      and case when f_cod_tipo_veiculo is null then true else v.cod_tipo = f_cod_tipo_veiculo end
      and case when f_cod_veiculo is null then true else c.cod_veiculo = f_cod_veiculo end
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
       c.cod_colaborador                                    as cod_colaborador,
       c.cpf_colaborador                                    as cpf_colaborador,
       c.nome_colaborador                                   as nome_colaborador,
       c.cod_veiculo                                        as cod_veiculo,
       c.placa                                              as placa_veiculo,
       c.identificador_frota                                as identificador_frota,
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
         join equipe e
              on e.codigo = c.cod_equipe_colaborador
order by c.data_hora_sincronizacao desc
$$;