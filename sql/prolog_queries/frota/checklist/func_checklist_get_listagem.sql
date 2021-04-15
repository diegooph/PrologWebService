create function func_checklist_get_listagem(f_cod_unidades bigint[],
                                            f_cod_colaborador bigint,
                                            f_cod_tipo_veiculo bigint,
                                            f_cod_veiculo bigint,
                                            f_incluir_respostas boolean,
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
    select c.*
    from checklist c
    where c.cod_unidade = any (f_cod_unidades)
      and c.data_hora_realizacao_tz_aplicado :: date >= f_data_inicial
      and c.data_hora_realizacao_tz_aplicado :: date <= f_data_final
    order by c.data_hora_sincronizacao desc
    limit f_limit offset f_offset
),
     checks_resto as --!ver outro nome
         (select c.*,
                 co.codigo                     as cod_colaborador,
                 co.cod_equipe                 as cod_equipe_colaborador,
                 co.nome :: text               as nome_colaborador,
                 v.placa :: text               as placa,
                 v.identificador_frota :: text as identificador_frota
          from checks c
                   left join checklist_modelo cm on f_incluir_respostas
              and cm.cod_unidade = any (f_cod_unidades)
              and c.cod_checklist_modelo = cm.codigo
                   left join checklist_perguntas cp on f_incluir_respostas
              and cp.cod_versao_checklist_modelo = c.cod_versao_checklist_modelo
                   left join checklist_alternativa_pergunta cap on f_incluir_respostas
              and cap.cod_pergunta = cp.codigo
                   left join checklist_respostas_nok crn on f_incluir_respostas
              and c.codigo = crn.cod_checklist
              and crn.cod_alternativa = cap.codigo
                   join colaborador co
                        on co.cpf = c.cpf_colaborador
                   join veiculo v
                        on v.codigo = c.cod_veiculo
          where case when f_cod_colaborador is null then true else co.codigo = f_cod_colaborador end
            and case when f_cod_tipo_veiculo is null then true else v.cod_tipo = f_cod_tipo_veiculo end
            and case when f_cod_veiculo is null then true else c.cod_veiculo = f_cod_veiculo end)
select cr.codigo                                             as cod_checklist,
       cr.cod_checklist_modelo                               as cod_checklist_modelo,
       cr.cod_versao_checklist_modelo                        as cod_versao_checklist_modelo,
       cr.data_hora_realizacao_tz_aplicado                   as data_hora_realizacao,
       cr.data_hora_importado_prolog at time zone f_timezone as data_hora_importado_prolog,
       cr.km_veiculo                                         as km_veiculo_momento_realizacao,
       cr.tempo_realizacao                                   as duracao_realizacao_millis,
       cr.cod_colaborador                                    as cod_colaborador,
       cr.cpf_colaborador                                    as cpf_colaborador,
       cr.nome_colaborador                                   as nome_colaborador,
       cr.cod_veiculo                                        as cod_veiculo,
       cr.placa                                              as placa_veiculo,
       cr.identificador_frota                                as identificador_frota,
       cr.tipo                                               as tipo_checklist,
       cr.total_perguntas_ok                                 as total_perguntas_ok,
       cr.total_perguntas_nok                                as total_perguntas_nok,
       cr.total_alternativas_ok                              as total_alternativas_ok,
       cr.total_alternativas_nok                             as total_alternativas_nok,
       cr.total_midias_perguntas_ok                          as total_midias_perguntas_ok,
       cr.total_midias_alternativas_nok                      as total_midias_alternativas_nok,
       (select count(*)
        from checklist_respostas_nok crn
                 join checklist_alternativa_pergunta cap
                      on crn.cod_alternativa = cap.codigo
        where crn.cod_checklist = cr.codigo
          and cap.prioridade = 'BAIXA') :: smallint          as total_baixa,
       (select count(*)
        from checklist_respostas_nok crn
                 join checklist_alternativa_pergunta cap
                      on crn.cod_alternativa = cap.codigo
        where crn.cod_checklist = cr.codigo
          and cap.prioridade = 'ALTA') :: smallint           as total_alta,
       (select count(*)
        from checklist_respostas_nok crn
                 join checklist_alternativa_pergunta cap
                      on crn.cod_alternativa = cap.codigo
        where crn.cod_checklist = cr.codigo
          and cap.prioridade = 'CRITICA') :: smallint        as total_critica
from checks_resto cr
         join equipe e
              on e.codigo = cr.cod_equipe_colaborador
order by cr.data_hora_sincronizacao desc
$$;