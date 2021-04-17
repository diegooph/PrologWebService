CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_GET_ALL_CHECKLISTS_REALIZADOS_DEPRECATED(F_COD_UNIDADE BIGINT,
                                                                                   F_COD_EQUIPE BIGINT,
                                                                                   F_COD_TIPO_VEICULO BIGINT,
                                                                                   F_PLACA_VEICULO CHARACTER VARYING,
                                                                                   F_DATA_INICIAL DATE,
                                                                                   F_DATA_FINAL DATE,
                                                                                   F_TIMEZONE TEXT,
                                                                                   F_LIMIT INTEGER,
                                                                                   F_OFFSET BIGINT)
    RETURNS TABLE
            (
                COD_CHECKLIST                 BIGINT,
                COD_CHECKLIST_MODELO          BIGINT,
                COD_VERSAO_CHECKLIST_MODELO   BIGINT,
                DATA_HORA_REALIZACAO          TIMESTAMP WITHOUT TIME ZONE,
                DATA_HORA_IMPORTADO_PROLOG    TIMESTAMP WITHOUT TIME ZONE,
                KM_VEICULO_MOMENTO_REALIZACAO BIGINT,
                DURACAO_REALIZACAO_MILLIS     BIGINT,
                CPF_COLABORADOR               BIGINT,
                PLACA_VEICULO                 TEXT,
                TIPO_CHECKLIST                CHAR,
                NOME_COLABORADOR              TEXT,
                TOTAL_ITENS_OK                SMALLINT,
                TOTAL_ITENS_NOK               SMALLINT,
                OBSERVACAO                    TEXT
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    F_HAS_EQUIPE           INTEGER := CASE WHEN F_COD_EQUIPE IS NULL THEN 0 ELSE 1 END;
    F_HAS_COD_TIPO_VEICULO INTEGER := CASE WHEN F_COD_TIPO_VEICULO IS NULL THEN 0 ELSE 1 END;
    F_HAS_PLACA_VEICULO    INTEGER := CASE WHEN F_PLACA_VEICULO IS NULL THEN 0 ELSE 1 END;
BEGIN
    RETURN QUERY
        SELECT C.CODIGO                                             AS COD_CHECKLIST,
               C.COD_CHECKLIST_MODELO                               AS COD_CHECKLIST_MODELO,
               C.COD_VERSAO_CHECKLIST_MODELO                        AS COD_VERSAO_CHECKLIST_MODELO,
               C.DATA_HORA AT TIME ZONE F_TIMEZONE                  AS DATA_HORA_REALIZACAO,
               C.DATA_HORA_IMPORTADO_PROLOG AT TIME ZONE F_TIMEZONE AS DATA_HORA_IMPORTADO_PROLOG,
               C.KM_VEICULO                                         AS KM_VEICULO_MOMENTO_REALIZACAO,
               C.TEMPO_REALIZACAO                                   AS DURACAO_REALIZACAO_MILLIS,
               C.CPF_COLABORADOR                                    AS CPF_COLABORADOR,
               V.PLACA :: TEXT                                      AS PLACA_VEICULO,
               C.TIPO                                               AS TIPO_CHECKLIST,
               CO.NOME :: TEXT                                      AS NOME_COLABORADOR,
               C.TOTAL_PERGUNTAS_OK                                 AS TOTAL_ITENS_OK,
               C.TOTAL_PERGUNTAS_NOK                                AS TOTAL_ITENS_NOK,
               C.OBSERVACAO                                         AS OBSERVACAO
        FROM CHECKLIST C
                 JOIN COLABORADOR CO
                      ON CO.CPF = C.CPF_COLABORADOR
                 JOIN EQUIPE E
                      ON E.CODIGO = CO.COD_EQUIPE
                 JOIN VEICULO V
                      ON V.PLACA = C.PLACA_VEICULO
        WHERE C.DATA_HORA_REALIZACAO_TZ_APLICADO :: DATE >= F_DATA_INICIAL
          AND C.DATA_HORA_REALIZACAO_TZ_APLICADO :: DATE <= F_DATA_FINAL
          AND C.COD_UNIDADE = F_COD_UNIDADE
          AND (F_HAS_EQUIPE = 0 OR E.CODIGO = F_COD_EQUIPE)
          AND (F_HAS_COD_TIPO_VEICULO = 0 OR V.COD_TIPO = F_COD_TIPO_VEICULO)
          AND (F_HAS_PLACA_VEICULO = 0 OR V.PLACA = F_PLACA_VEICULO)
        ORDER BY DATA_HORA_SINCRONIZACAO DESC
        LIMIT F_LIMIT OFFSET F_OFFSET;
END;
$$;

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
               v.placa :: text                                                     as placa_veiculo,
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
                 join veiculo v on v.codigo = c.cod_veiculo
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

create or replace function func_checklist_get_farol_checklist(f_cod_unidade bigint,
                                                              f_data_inicial date,
                                                              f_data_final date,
                                                              f_itens_criticos_retroativos boolean)
    returns table
            (
                data                               date,
                placa                              text,
                cod_checklist_saida                bigint,
                data_hora_ultimo_checklist_saida   timestamp without time zone,
                cod_checklist_modelo_saida         bigint,
                nome_colaborador_checklist_saida   text,
                cod_checklist_retorno              bigint,
                data_hora_ultimo_checklist_retorno timestamp without time zone,
                cod_checklist_modelo_retorno       bigint,
                nome_colaborador_checklist_retorno text,
                codigo_pergunta                    bigint,
                descricao_pergunta                 text,
                descricao_alternativa              text,
                alternativa_tipo_outros            boolean,
                descricao_alternativa_tipo_outros  text,
                codigo_item_critico                bigint,
                data_hora_apontamento_item_critico timestamp without time zone
            )
    language plpgsql
as
$$
declare
    -- Algumas empresas não fecham OS, o que acarreta em um grande número de itens críticos para cada placa. A função
    -- do Farol é alertar sobre a existência de pendências, não listar um a um, assim limitamos a busca a apenas cinco
    -- itens críticos por placa e garantimos o desempenho da query.
    v_qtd_maxima_itens_criticos constant bigint not null := 5;
begin
    -- Aumenta memória disponível da function para evitar trabalho em disco.
    set local work_mem = '8MB';

    return query
        -- A utilização de uma CTE para filtrar os checklists parece ser desnecessária, porém mostrou uma melhora
        -- significativa no desempenho da query no geral.
        with checks_filtrados as (
            select c.codigo                           as cod_checklist,
                   c.cod_checklist_modelo             as cod_checklist_modelo,
                   c.cod_veiculo                      as cod_veiculo,
                   c.cpf_colaborador                  as cpf_colaborador,
                   c.data_hora_realizacao_tz_aplicado as data_hora_realizacao_tz_aplicado,
                   c.tipo                             as tipo_checklist,
                   co.nome                            as nome_colaborador
            from checklist c
                     join colaborador co on co.cpf = c.cpf_colaborador
            where c.cod_unidade = f_cod_unidade
              and c.data_hora_realizacao_tz_aplicado::date between f_data_inicial and f_data_final
        ),
             ultimos_checklists_veiculos as (
                 select checks_placas_dias.data                  as data,
                        checks_placas_dias.cod_veiculo           as cod_veiculo,
                        checks_placas_dias.placa                 as placa,
                        checks_placas_dias.cod_checklist_saida   as cod_checklist_saida,
                        cfs.data_hora_realizacao_tz_aplicado     as data_hora_ultimo_checklist_saida,
                        cfs.cod_checklist_modelo                 as cod_checklist_modelo_saida,
                        cfs.nome_colaborador                     as nome_colaborador_checklist_saida,
                        checks_placas_dias.cod_checklist_retorno as cod_checklist_retorno,
                        cfr.data_hora_realizacao_tz_aplicado     as data_hora_ultimo_checklist_retorno,
                        cfr.cod_checklist_modelo                 as cod_checklist_modelo_retorno,
                        cfr.nome_colaborador                     as nome_colaborador_checklist_retorno
                 from (select g.day::date                                                      as data,
                              v.codigo                                                         as cod_veiculo,
                              v.placa                                                          as placa,
                              max(case when cf.tipo_checklist = 'S' then cf.cod_checklist end) as cod_checklist_saida,
                              max(case when cf.tipo_checklist = 'R' then cf.cod_checklist end) as cod_checklist_retorno
                       from veiculo v
                                cross join generate_series(f_data_inicial, f_data_final, '1 DAY') g(day)
                                left join checks_filtrados cf
                                          on cf.cod_veiculo = v.codigo and
                                             g.day::date = (cf.data_hora_realizacao_tz_aplicado)::date
                       where v.cod_unidade = f_cod_unidade
                         and v.status_ativo = true
                       group by data, v.placa) as checks_placas_dias
                          left join checks_filtrados cfs on cfs.cod_checklist = checks_placas_dias.cod_checklist_saida
                          left join checks_filtrados cfr on cfr.cod_checklist = checks_placas_dias.cod_checklist_retorno
             ),
             itens_criticos_filtrados as (
                 select row_number() over (partition by c.cod_veiculo) as row_id,
                        c.codigo                                       as cod_checklist_item,
                        c.cod_veiculo                                  as cod_veiculo_checklist_item,
                        cap.cod_pergunta                               as cod_pergunta,
                        cap.codigo                                     as cod_alternativa,
                        cap.alternativa                                as descricao_alternativa,
                        cap.alternativa_tipo_outros                    as alternativa_tipo_outros,
                        cosi.codigo                                    as cod_item_os,
                        c.data_hora_realizacao_tz_aplicado             as data_hora_abertura_os
                 from checklist_ordem_servico_itens cosi
                          join checklist_ordem_servico cos
                               on cos.codigo = cosi.cod_os and cos.cod_unidade = cosi.cod_unidade
                          join checklist c on cos.cod_checklist = c.codigo
                          join checklist_alternativa_pergunta cap
                               on cap.codigo = cosi.cod_alternativa_primeiro_apontamento
                 where cosi.cod_unidade = f_cod_unidade
                   and cosi.status_resolucao = 'P'
                   and cap.prioridade = 'CRITICA'
                   -- Buscar itens retroativos é uma decisão de, respeitar ou não a data de apontamento do item.
                   -- Fazemos isso utilizando a data de realização do checklist.
                   and case
                           when f_itens_criticos_retroativos then true
                           else (c.data_hora_realizacao_tz_aplicado::date between f_data_inicial and f_data_final) end
                   -- Ordenamento para garantirmos a exibição dos itens mais recentes.
                 order by cosi.codigo desc
             ),
             itens_criticos_placa as (
                 select icf.cod_checklist_item         as cod_checklist_item,
                        icf.cod_veiculo_checklist_item as cod_veiculo_checklist_item,
                        cp.codigo                      as cod_pergunta,
                        cp.pergunta                    as descricao_pergunta,
                        icf.cod_alternativa            as cod_alternativa,
                        icf.descricao_alternativa      as descricao_alternativa,
                        icf.alternativa_tipo_outros    as alternativa_tipo_outros,
                        crn.resposta_outros            as descricao_alternativa_tipo_outros,
                        icf.cod_item_os                as cod_item_os,
                        icf.data_hora_abertura_os      as data_hora_abertura_os
                 from itens_criticos_filtrados icf
                          join checklist_perguntas cp
                               on cp.codigo = icf.cod_pergunta
                          join checklist_respostas_nok crn
                               on crn.cod_checklist = icf.cod_checklist_item
                                   and crn.cod_alternativa = icf.cod_alternativa
                 where icf.row_id <= v_qtd_maxima_itens_criticos
             )

        select ucv.data,
               ucv.placa::text,
               ucv.cod_checklist_saida,
               ucv.data_hora_ultimo_checklist_saida,
               ucv.cod_checklist_modelo_saida,
               ucv.nome_colaborador_checklist_saida::text,
               ucv.cod_checklist_retorno,
               ucv.data_hora_ultimo_checklist_retorno,
               ucv.cod_checklist_modelo_retorno,
               ucv.nome_colaborador_checklist_retorno::text,
               -- informações dos itens críticos, não necessáriamente relacionados ao checklist
               icp.cod_pergunta,
               icp.descricao_pergunta::text,
               icp.descricao_alternativa::text,
               icp.alternativa_tipo_outros,
               icp.descricao_alternativa_tipo_outros::text,
               icp.cod_item_os,
               icp.data_hora_abertura_os
        from ultimos_checklists_veiculos ucv
                 -- O join deve ocorrer pelo código do checklist ou pelo código do veículo, mas sem duplicar.
                 left join itens_criticos_placa icp
                           on case
                                  when icp.cod_checklist_item in (ucv.cod_checklist_saida, ucv.cod_checklist_retorno)
                                      then icp.cod_checklist_item in
                                           (ucv.cod_checklist_saida, ucv.cod_checklist_retorno)
                                  else icp.cod_veiculo_checklist_item = ucv.cod_veiculo end
        order by ucv.data, ucv.cod_veiculo, icp.cod_item_os;
end;
$$;