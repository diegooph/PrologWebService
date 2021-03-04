-- Sobre:
--
-- Function para buscar as informações de um checklist realizado.
--
-- Histórico:
-- 2020-03-03 -> Atualização de arquivo e documentação (wvinim - PL-2494).
-- 2020-07-07 -> Adição das informações de mídias coletadas (wvinim - PL-2705).
-- 2020-07-13 -> Atualização de arquivo e documentação (wvinim - PL-2824).
-- 2020-09-22 -> Insere nova coluna observação no retorno (gustavocnp95 - PL-3164).
create or replace function func_checklist_get_by_codigo(f_cod_checklist bigint)
    returns table
            (
                cod_checklist                 bigint,
                cod_checklist_modelo          bigint,
                cod_versao_checklist_modelo   bigint,
                data_hora_realizacao          timestamp without time zone,
                data_hora_importado_prolog    timestamp without time zone,
                km_veiculo_momento_realizacao bigint,
                observacao                    text,
                duracao_realizacao_millis     bigint,
                cpf_colaborador               bigint,
                placa_veiculo                 text,
                tipo_checklist                char,
                nome_colaborador              text,
                cod_pergunta                  bigint,
                ordem_pergunta                integer,
                descricao_pergunta            text,
                pergunta_single_choice        boolean,
                cod_alternativa               bigint,
                prioridade_alternativa        text,
                ordem_alternativa             integer,
                descricao_alternativa         text,
                alternativa_tipo_outros       boolean,
                cod_imagem                    bigint,
                url_imagem                    text,
                alternativa_selecionada       boolean,
                resposta_outros               text,
                tem_midia_pergunta_ok         boolean,
                uuid_midia_pergunta_ok        uuid,
                url_midia_pergunta_ok         text,
                tipo_midia_pergunta_ok        text,
                tem_midia_alternativa         boolean,
                uuid_midia_alternativa        uuid,
                url_midia_alternativa         text,
                tipo_midia_alternativa        text
            )
    language plpgsql
as
$$
begin
    return query
        select c.codigo                                                            as cod_checklist,
               c.cod_checklist_modelo                                              as cod_checklist_modelo,
               c.cod_versao_checklist_modelo                                       as cod_versao_checklist_modelo,
               c.data_hora_realizacao_tz_aplicado                                  as data_hora_realizacao,
               c.data_hora_importado_prolog at time zone tz_unidade(c.cod_unidade) as data_hora_importado_prolog,
               c.km_veiculo                                                        as km_veiculo_momento_realizacao,
               c.observacao                                                        as observacao,
               c.tempo_realizacao                                                  as duracao_realizacao_millis,
               c.cpf_colaborador                                                   as cpf_colaborador,
               c.placa_veiculo :: text                                             as placa_veiculo,
               c.tipo                                                              as tipo_checklist,
               co.nome :: text                                                     as nome_colaborador,
               cp.codigo                                                           as cod_pergunta,
               cp.ordem                                                            as ordem_pergunta,
               cp.pergunta                                                         as descricao_pergunta,
               cp.single_choice                                                    as pergunta_single_choice,
               cap.codigo                                                          as cod_alternativa,
               cap.prioridade :: text                                              as prioridade_alternativa,
               cap.ordem                                                           as ordem_alternativa,
               cap.alternativa                                                     as descricao_alternativa,
               cap.alternativa_tipo_outros                                         as alternativa_tipo_outros,
               cgi.cod_imagem                                                      as cod_imagem,
               cgi.url_imagem                                                      as url_imagem,
               crn.codigo is not null                                              as alternativa_selecionada,
               crn.resposta_outros                                                 as resposta_outros,
               crmpo.uuid is not null                                              as tem_midia_pergunta_ok,
               crmpo.uuid                                                          as uuid_midia_pergunta_ok,
               crmpo.url_midia                                                     as url_midia_pergunta_ok,
               crmpo.tipo_midia                                                    as tipo_midia_pergunta_ok,
               crman.uuid is not null                                              as tem_midia_alternativa,
               crman.uuid                                                          as uuid_midia_alternativa,
               crman.url_midia                                                     as url_midia_alternativa,
               crman.tipo_midia                                                    as tipo_midia_alternativa
        from checklist c
                 join colaborador co
                      on co.cpf = c.cpf_colaborador
                 join checklist_perguntas cp
                      on cp.cod_versao_checklist_modelo = c.cod_versao_checklist_modelo
                 join checklist_alternativa_pergunta cap
                      on cap.cod_pergunta = cp.codigo
                 left join checklist_respostas_nok crn
                           on c.codigo = crn.cod_checklist
                               and cap.codigo = crn.cod_alternativa
                 left join checklist_galeria_imagens cgi
                           on cp.cod_imagem = cgi.cod_imagem
                 left join checklist_respostas_midias_perguntas_ok crmpo
                           on crmpo.cod_checklist = c.codigo and crmpo.cod_pergunta = cp.codigo and crn.codigo is null
                 left join checklist_respostas_midias_alternativas_nok crman
                           on crman.cod_checklist = c.codigo and crman.cod_alternativa = cap.codigo and
                              crn.codigo is not null
        where c.codigo = f_cod_checklist
        order by cp.codigo, cap.codigo;
end;
$$;