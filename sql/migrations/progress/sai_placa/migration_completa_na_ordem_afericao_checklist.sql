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

alter table afericao_data
    drop constraint if exists fk_afericao_cod_veiculo_placa;

alter table afericao_data
    add constraint fk_afericao_cod_veiculo foreign key (cod_veiculo)
        references veiculo_data (codigo);

alter table afericao_data
    drop constraint if exists check_estado_placa_km;

alter table afericao_data
    add constraint check_estado_veiculo_km
        check (case tipo_processo_coleta
                   when 'PLACA' then (cod_veiculo is not null) and (km_veiculo is not null)
                   when 'PNEU_AVULSO' then (cod_veiculo is null) and (km_veiculo is null)
                   else null::boolean
            end);

create or replace view afericao as
select ad.codigo,
       ad.data_hora,
       v.placa::varchar(255) as placa_veiculo,
       ad.cod_veiculo,
       ad.cpf_aferidor,
       ad.km_veiculo,
       ad.tempo_realizacao,
       ad.tipo_medicao_coletada,
       ad.cod_unidade,
       ad.tipo_processo_coleta,
       ad.forma_coleta_dados,
       ad.cod_diagrama
from afericao_data ad
         left join veiculo v
                   on ad.cod_veiculo = v.codigo
where ad.deletado = false;

/*
    Chamada de replace da view por algum bug na hora de atualizar os campos de afericao,
    porém não houve nenhuma alteração na view em si
*/
create or replace view view_analise_pneus as
SELECT u.nome                                                                           AS "UNIDADE ALOCADO",
       p.codigo                                                                         AS "COD PNEU",
       p.codigo_cliente                                                                 AS "COD PNEU CLIENTE",
       p.status                                                                         AS "STATUS PNEU",
       p.cod_unidade,
       map.nome                                                                         AS "MARCA",
       mp.nome                                                                          AS "MODELO",
       ((((dp.largura || '/'::text) || dp.altura) || ' R'::text) || dp.aro)             AS "MEDIDAS",
       dados.qt_afericoes                                                               AS "QTD DE AFERIÇÕES",
       to_char((dados.primeira_afericao)::timestamp with time zone, 'DD/MM/YYYY'::text) AS "DTA 1a AFERIÇÃO",
       to_char((dados.ultima_afericao)::timestamp with time zone, 'DD/MM/YYYY'::text)   AS "DTA ÚLTIMA AFERIÇÃO",
       dados.total_dias                                                                 AS "DIAS ATIVO",
       round(
               CASE
                   WHEN (dados.total_dias > 0) THEN (dados.total_km / (dados.total_dias)::numeric)
                   ELSE NULL::numeric
                   END)                                                                 AS "MÉDIA KM POR DIA",
       p.altura_sulco_interno,
       p.altura_sulco_central_interno,
       p.altura_sulco_central_externo,
       p.altura_sulco_externo,
       round((dados.maior_sulco)::numeric, 2)                                           AS "MAIOR MEDIÇÃO VIDA",
       round((dados.menor_sulco)::numeric, 2)                                           AS "MENOR SULCO ATUAL",
       round((dados.sulco_gasto)::numeric, 2)                                           AS "MILIMETROS GASTOS",
       round((dados.km_por_mm)::numeric, 2)                                             AS "KMS POR MILIMETRO",
       round(((dados.km_por_mm * dados.sulco_restante))::numeric)                       AS "KMS A PERCORRER",
       trunc(
               CASE
                   WHEN (((dados.total_km > (0)::numeric) AND (dados.total_dias > 0)) AND
                         ((dados.total_km / (dados.total_dias)::numeric) > (0)::numeric)) THEN (
                           (dados.km_por_mm * dados.sulco_restante) /
                           ((dados.total_km / (dados.total_dias)::numeric))::double precision)
                   ELSE (0)::double precision
                   END)                                                                 AS "DIAS RESTANTES",
       CASE
           WHEN (((dados.total_km > (0)::numeric) AND (dados.total_dias > 0)) AND
                 ((dados.total_km / (dados.total_dias)::numeric) > (0)::numeric)) THEN (
                   (((dados.km_por_mm * dados.sulco_restante) /
                     ((dados.total_km / (dados.total_dias)::numeric))::double precision))::integer +
                   ('NOW'::text)::date)
           ELSE NULL::date
           END                                                                          AS "PREVISÃO DE TROCA"
FROM (((((pneu p
    JOIN (SELECT av.cod_pneu,
                 av.cod_unidade,
                 count(av.altura_sulco_central_interno)                                     AS qt_afericoes,
                 (min(timezone(tz_unidade(a.cod_unidade), a.data_hora)))::date              AS primeira_afericao,
                 (max(timezone(tz_unidade(a.cod_unidade), a.data_hora)))::date              AS ultima_afericao,
                 ((max(timezone(tz_unidade(a.cod_unidade), a.data_hora)))::date -
                  (min(timezone(tz_unidade(a.cod_unidade), a.data_hora)))::date)            AS total_dias,
                 max(total_km.total_km)                                                     AS total_km,
                 max(GREATEST(av.altura_sulco_interno, av.altura_sulco_central_interno, av.altura_sulco_central_externo,
                              av.altura_sulco_externo))                                     AS maior_sulco,
                 min(LEAST(av.altura_sulco_interno, av.altura_sulco_central_interno, av.altura_sulco_central_externo,
                           av.altura_sulco_externo))                                        AS menor_sulco,
                 (max(GREATEST(av.altura_sulco_interno, av.altura_sulco_central_interno,
                               av.altura_sulco_central_externo, av.altura_sulco_externo)) - min(
                          LEAST(av.altura_sulco_interno, av.altura_sulco_central_interno,
                                av.altura_sulco_central_externo, av.altura_sulco_externo))) AS sulco_gasto,
                 CASE
                     WHEN (
                             CASE
                                 WHEN (p_1.vida_atual = p_1.vida_total) THEN (min(LEAST(av.altura_sulco_interno,
                                                                                        av.altura_sulco_central_interno,
                                                                                        av.altura_sulco_central_externo,
                                                                                        av.altura_sulco_externo)) -
                                                                              pru.sulco_minimo_descarte)
                                 WHEN (p_1.vida_atual < p_1.vida_total) THEN (min(LEAST(av.altura_sulco_interno,
                                                                                        av.altura_sulco_central_interno,
                                                                                        av.altura_sulco_central_externo,
                                                                                        av.altura_sulco_externo)) -
                                                                              pru.sulco_minimo_recapagem)
                                 ELSE NULL::real
                                 END < (0)::double precision) THEN (0)::real
                     ELSE
                         CASE
                             WHEN (p_1.vida_atual = p_1.vida_total) THEN (min(LEAST(av.altura_sulco_interno,
                                                                                    av.altura_sulco_central_interno,
                                                                                    av.altura_sulco_central_externo,
                                                                                    av.altura_sulco_externo)) -
                                                                          pru.sulco_minimo_descarte)
                             WHEN (p_1.vida_atual < p_1.vida_total) THEN (min(LEAST(av.altura_sulco_interno,
                                                                                    av.altura_sulco_central_interno,
                                                                                    av.altura_sulco_central_externo,
                                                                                    av.altura_sulco_externo)) -
                                                                          pru.sulco_minimo_recapagem)
                             ELSE NULL::real
                             END
                     END                                                                    AS sulco_restante,
                 CASE
                     WHEN (((max(timezone(tz_unidade(a.cod_unidade), a.data_hora)))::date -
                            (min(timezone(tz_unidade(a.cod_unidade), a.data_hora)))::date) > 0) THEN (
                             ((max(total_km.total_km))::double precision / max(
                                     GREATEST(av.altura_sulco_interno, av.altura_sulco_central_interno,
                                              av.altura_sulco_central_externo, av.altura_sulco_externo))) - min(
                                     LEAST(av.altura_sulco_interno, av.altura_sulco_central_interno,
                                           av.altura_sulco_central_externo, av.altura_sulco_externo)))
                     ELSE (0)::double precision
                     END                                                                    AS km_por_mm
          FROM ((((afericao_valores av
              JOIN afericao a ON ((a.codigo = av.cod_afericao)))
              JOIN pneu p_1 ON ((((p_1.codigo)::text = (av.cod_pneu)::text) AND ((p_1.status)::text = 'EM_USO'::text))))
              JOIN pneu_restricao_unidade pru ON ((pru.cod_unidade = av.cod_unidade)))
                   JOIN (SELECT total_km_rodado.cod_pneu,
                                total_km_rodado.cod_unidade,
                                sum(total_km_rodado.km_rodado) AS total_km
                         FROM (SELECT av_1.cod_pneu,
                                      av_1.cod_unidade,
                                      a_1.placa_veiculo,
                                      (max(a_1.km_veiculo) - min(a_1.km_veiculo)) AS km_rodado
                               FROM (afericao_valores av_1
                                        JOIN afericao a_1 ON ((a_1.codigo = av_1.cod_afericao)))
                               GROUP BY av_1.cod_pneu, av_1.cod_unidade, a_1.placa_veiculo) total_km_rodado
                         GROUP BY total_km_rodado.cod_pneu, total_km_rodado.cod_unidade) total_km
                        ON (((total_km.cod_pneu = av.cod_pneu) AND (total_km.cod_unidade = av.cod_unidade))))
          GROUP BY av.cod_pneu, av.cod_unidade, p_1.vida_atual, p_1.vida_total, pru.sulco_minimo_descarte,
                   pru.sulco_minimo_recapagem) dados ON ((dados.cod_pneu = p.codigo)))
    JOIN dimensao_pneu dp ON ((dp.codigo = p.cod_dimensao)))
    JOIN unidade u ON ((u.codigo = p.cod_unidade)))
    JOIN modelo_pneu mp ON (((mp.codigo = p.cod_modelo) AND (mp.cod_empresa = u.cod_empresa))))
         JOIN marca_pneu map ON ((map.codigo = mp.cod_marca)));

alter table afericao_data
    drop column if exists placa_veiculo;

create or replace function func_afericao_insert_afericao(f_cod_unidade bigint,
                                                         f_data_hora timestamp with time zone,
                                                         f_cpf_aferidor bigint,
                                                         f_tempo_realizacao bigint,
                                                         f_tipo_medicao_coletada varchar(255),
                                                         f_tipo_processo_coleta varchar(255),
                                                         f_forma_coleta_dados text,
                                                         f_cod_veiculo bigint,
                                                         f_km_veiculo bigint)
    returns bigint

    language plpgsql
    security definer
as
$$
declare
    v_cod_tipo_veiculo      bigint := (select v.cod_tipo
                                       from veiculo_data v
                                       where v.codigo = f_cod_veiculo);
    v_cod_diagrama_veiculo  bigint := (select vt.cod_diagrama
                                       from veiculo_tipo vt
                                       where vt.codigo = v_cod_tipo_veiculo);
    v_cod_afericao_inserida bigint;
begin
    insert into afericao_data(data_hora,
                              cpf_aferidor,
                              km_veiculo,
                              tempo_realizacao,
                              tipo_medicao_coletada,
                              cod_unidade,
                              tipo_processo_coleta,
                              deletado,
                              data_hora_deletado,
                              pg_username_delecao,
                              cod_diagrama,
                              forma_coleta_dados,
                              cod_veiculo)
    values (f_data_hora,
            f_cpf_aferidor,
            f_km_veiculo,
            f_tempo_realizacao,
            f_tipo_medicao_coletada,
            f_cod_unidade,
            f_tipo_processo_coleta,
            false,
            null,
            null,
            v_cod_diagrama_veiculo,
            f_forma_coleta_dados,
            f_cod_veiculo)
    returning codigo into v_cod_afericao_inserida;

    return v_cod_afericao_inserida;
end
$$;


CREATE OR REPLACE FUNCTION FUNC_AFERICAO_GET_AFERICOES_PLACAS_PAGINADA(F_COD_UNIDADE BIGINT, F_COD_TIPO_VEICULO BIGINT,
                                                                       F_PLACA_VEICULO TEXT, F_DATA_INICIAL DATE,
                                                                       F_DATA_FINAL DATE, F_LIMIT BIGINT,
                                                                       F_OFFSET BIGINT,
                                                                       F_TZ_UNIDADE TEXT)
    RETURNS TABLE
            (
                KM_VEICULO            BIGINT,
                COD_AFERICAO          BIGINT,
                COD_UNIDADE           BIGINT,
                DATA_HORA             TIMESTAMP WITHOUT TIME ZONE,
                PLACA_VEICULO         TEXT,
                IDENTIFICADOR_FROTA   TEXT,
                TIPO_MEDICAO_COLETADA TEXT,
                TIPO_PROCESSO_COLETA  TEXT,
                FORMA_COLETA_DADOS    TEXT,
                CPF                   TEXT,
                NOME                  TEXT,
                TEMPO_REALIZACAO      BIGINT
            )
    LANGUAGE SQL
AS
$$
SELECT A.KM_VEICULO,
       A.CODIGO                              AS COD_AFERICAO,
       A.COD_UNIDADE                         AS COD_UNIDADE,
       A.DATA_HORA AT TIME ZONE F_TZ_UNIDADE AS DATA_HORA,
       V.PLACA                               AS PLACA_VEICULO,
       V.IDENTIFICADOR_FROTA                 AS IDENTIFICADOR_FROTA,
       A.TIPO_MEDICAO_COLETADA::TEXT         AS TIPO_MEDICAO_COLETADA,
       A.TIPO_PROCESSO_COLETA::TEXT          AS TIPO_PROCESSO_COLETA,
       A.FORMA_COLETA_DADOS::TEXT            AS FORMA_COLETA_DADOS,
       C.CPF::TEXT                           AS CPF,
       C.NOME::TEXT                          AS NOME,
       A.TEMPO_REALIZACAO                    AS TEMPO_REALIZACAO
FROM AFERICAO A
         JOIN VEICULO V ON V.CODIGO = A.COD_VEICULO
         JOIN COLABORADOR C ON C.CPF = A.CPF_AFERIDOR
WHERE A.COD_UNIDADE = F_COD_UNIDADE
  AND CASE
          WHEN F_COD_TIPO_VEICULO IS NOT NULL
              THEN V.COD_TIPO = F_COD_TIPO_VEICULO
          ELSE TRUE END
  AND CASE
          WHEN F_PLACA_VEICULO IS NOT NULL
              THEN V.PLACA = F_PLACA_VEICULO
          ELSE TRUE END
  AND (A.DATA_HORA AT TIME ZONE F_TZ_UNIDADE)::DATE BETWEEN F_DATA_INICIAL AND F_DATA_FINAL
ORDER BY A.DATA_HORA DESC
LIMIT F_LIMIT OFFSET F_OFFSET;
$$;

drop function if exists func_afericao_get_afericoes_avulsas_paginada(f_cod_unidade bigint,
    f_data_inicial date,
    f_data_final date,
    f_limit bigint,
    f_offset bigint,
    f_tz_unidade text);

CREATE OR REPLACE FUNCTION FUNC_AFERICAO_GET_AFERICOES_AVULSAS_PAGINADA(F_COD_UNIDADE BIGINT,
                                                                        F_DATA_INICIAL DATE,
                                                                        F_DATA_FINAL DATE,
                                                                        F_LIMIT BIGINT,
                                                                        F_OFFSET BIGINT,
                                                                        F_TZ_UNIDADE TEXT)
    RETURNS TABLE
            (
                KM_VEICULO            BIGINT,
                COD_AFERICAO          BIGINT,
                COD_UNIDADE           BIGINT,
                DATA_HORA             TIMESTAMP WITHOUT TIME ZONE,
                TIPO_MEDICAO_COLETADA TEXT,
                TIPO_PROCESSO_COLETA  TEXT,
                FORMA_COLETA_DADOS    TEXT,
                CPF                   TEXT,
                NOME                  TEXT,
                TEMPO_REALIZACAO      BIGINT
            )
    LANGUAGE SQL
AS
$$
SELECT A.KM_VEICULO,
       A.CODIGO                              AS COD_AFERICAO,
       A.COD_UNIDADE                         AS COD_UNIDADE,
       A.DATA_HORA AT TIME ZONE F_TZ_UNIDADE AS DATA_HORA,
       A.TIPO_MEDICAO_COLETADA::TEXT         AS TIPO_MEDICAO_COLETADA,
       A.TIPO_PROCESSO_COLETA::TEXT          AS TIPO_PROCESSO_COLETA,
       A.FORMA_COLETA_DADOS::TEXT            AS FORMA_COLETA_DADOS,
       C.CPF::TEXT                           AS CPF,
       C.NOME::TEXT                          AS NOME,
       A.TEMPO_REALIZACAO                    AS TEMPO_REALIZACAO
FROM AFERICAO A
         JOIN COLABORADOR C ON C.CPF = A.CPF_AFERIDOR
WHERE A.COD_UNIDADE = F_COD_UNIDADE
  AND A.TIPO_PROCESSO_COLETA = 'PNEU_AVULSO'
  AND (A.DATA_HORA AT TIME ZONE F_TZ_UNIDADE)::DATE BETWEEN F_DATA_INICIAL AND F_DATA_FINAL
ORDER BY A.DATA_HORA DESC
LIMIT F_LIMIT OFFSET F_OFFSET;
$$;

CREATE OR REPLACE FUNCTION FUNC_AFERICAO_GET_AFERICAO_BY_CODIGO(F_COD_UNIDADE BIGINT,
                                                                F_COD_AFERICAO BIGINT,
                                                                F_TZ_UNIDADE TEXT)
    RETURNS TABLE
            (
                COD_AFERICAO                 BIGINT,
                COD_UNIDADE                  BIGINT,
                DATA_HORA                    TIMESTAMP WITHOUT TIME ZONE,
                PLACA_VEICULO                TEXT,
                IDENTIFICADOR_FROTA          TEXT,
                KM_VEICULO                   BIGINT,
                TEMPO_REALIZACAO             BIGINT,
                TIPO_PROCESSO_COLETA         TEXT,
                TIPO_MEDICAO_COLETADA        TEXT,
                FORMA_COLETA_DADOS           TEXT,
                CPF                          TEXT,
                NOME                         TEXT,
                ALTURA_SULCO_CENTRAL_INTERNO REAL,
                ALTURA_SULCO_CENTRAL_EXTERNO REAL,
                ALTURA_SULCO_EXTERNO         REAL,
                ALTURA_SULCO_INTERNO         REAL,
                PRESSAO_PNEU                 INTEGER,
                POSICAO_PNEU                 INTEGER,
                VIDA_PNEU_MOMENTO_AFERICAO   INTEGER,
                VIDAS_TOTAL_PNEU             INTEGER,
                CODIGO_PNEU                  BIGINT,
                CODIGO_PNEU_CLIENTE          TEXT,
                PRESSAO_RECOMENDADA          REAL
            )
    LANGUAGE SQL
AS
$$
SELECT A.CODIGO                              AS COD_AFERICAO,
       A.COD_UNIDADE                         AS COD_UNIDADE,
       A.DATA_HORA AT TIME ZONE F_TZ_UNIDADE AS DATA_HORA,
       V.PLACA                               AS PLACA_VEICULO,
       V.IDENTIFICADOR_FROTA                 AS IDENTIFICADOR_FROTA,
       A.KM_VEICULO                          AS KM_VEICULO,
       A.TEMPO_REALIZACAO                    AS TEMPO_REALIZACAO,
       A.TIPO_PROCESSO_COLETA::TEXT          AS TIPO_PROCESSO_COLETA,
       A.TIPO_MEDICAO_COLETADA::TEXT         AS TIPO_MEDICAO_COLETADA,
       A.FORMA_COLETA_DADOS::TEXT            AS FORMA_COLETA_DADOS,
       C.CPF::TEXT                           AS CPF,
       C.NOME::TEXT                          AS NOME,
       AV.ALTURA_SULCO_CENTRAL_INTERNO       AS ALTURA_SULCO_CENTRAL_INTERNO,
       AV.ALTURA_SULCO_CENTRAL_EXTERNO       AS ALTURA_SULCO_CENTRAL_EXTERNO,
       AV.ALTURA_SULCO_EXTERNO               AS ALTURA_SULCO_EXTERNO,
       AV.ALTURA_SULCO_INTERNO               AS ALTURA_SULCO_INTERNO,
       AV.PSI::INT                           AS PRESSAO_PNEU,
       AV.POSICAO                            AS POSICAO_PNEU,
       AV.VIDA_MOMENTO_AFERICAO              AS VIDA_PNEU_MOMENTO_AFERICAO,
       P.VIDA_TOTAL                          AS VIDAS_TOTAL_PNEU,
       P.CODIGO                              AS CODIGO_PNEU,
       P.CODIGO_CLIENTE::TEXT                AS CODIGO_PNEU_CLIENTE,
       P.PRESSAO_RECOMENDADA                 AS PRESSAO_RECOMENDADA
FROM AFERICAO A
         JOIN AFERICAO_VALORES AV
              ON A.CODIGO = AV.COD_AFERICAO
         JOIN VEICULO V
              ON V.CODIGO = A.COD_VEICULO
         JOIN PNEU_ORDEM PO
              ON AV.POSICAO = PO.POSICAO_PROLOG
         JOIN PNEU P
              ON P.CODIGO = AV.COD_PNEU
         JOIN MODELO_PNEU MO
              ON MO.CODIGO = P.COD_MODELO
         JOIN MARCA_PNEU MP
              ON MP.CODIGO = MO.COD_MARCA
         JOIN COLABORADOR C
              ON C.CPF = A.CPF_AFERIDOR
WHERE AV.COD_AFERICAO = F_COD_AFERICAO
  AND AV.COD_UNIDADE = F_COD_UNIDADE
ORDER BY PO.ORDEM_EXIBICAO ASC;
$$;

drop function func_afericao_get_cronograma_afericoes_placas(f_cod_unidades bigint[],
    f_data_hora_atual timestamp with time zone);

CREATE OR REPLACE FUNCTION FUNC_AFERICAO_GET_CRONOGRAMA_AFERICOES_PLACAS(F_COD_UNIDADES BIGINT[],
                                                                         F_DATA_HORA_ATUAL TIMESTAMP WITH TIME ZONE)
    RETURNS TABLE
            (
                PLACA                            TEXT,
                COD_VEICULO                      BIGINT,
                IDENTIFICADOR_FROTA              TEXT,
                COD_UNIDADE_PLACA                BIGINT,
                NOME_MODELO                      TEXT,
                INTERVALO_PRESSAO                INTEGER,
                INTERVALO_SULCO                  INTEGER,
                PERIODO_AFERICAO_SULCO           INTEGER,
                PERIODO_AFERICAO_PRESSAO         INTEGER,
                PNEUS_APLICADOS                  INTEGER,
                STATUS_ATIVO_TIPO_VEICULO        BOOLEAN,
                FORMA_COLETA_DADOS_SULCO         TEXT,
                FORMA_COLETA_DADOS_PRESSAO       TEXT,
                FORMA_COLETA_DADOS_SULCO_PRESSAO TEXT,
                PODE_AFERIR_ESTEPE               BOOLEAN
            )
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    RETURN QUERY
        SELECT V.PLACA :: TEXT                                              AS PLACA,
               COALESCE(V.CODIGO, -1)::BIGINT                               AS COD_VEICULO,
               V.IDENTIFICADOR_FROTA ::TEXT                                 AS IDENTIFICADOR_FROTA,
               V.COD_UNIDADE :: BIGINT                                      AS COD_UNIDADE_PLACA,
               MV.NOME :: TEXT                                              AS NOME_MODELO,
               COALESCE(INTERVALO_PRESSAO.INTERVALO, -1) :: INTEGER         AS INTERVALO_PRESSAO,
               COALESCE(INTERVALO_SULCO.INTERVALO, -1) :: INTEGER           AS INTERVALO_SULCO,
               PRU.PERIODO_AFERICAO_SULCO                                   AS PERIODO_AFERICAO_SULCO,
               PRU.PERIODO_AFERICAO_PRESSAO                                 AS PERIODO_AFERICAO_PRESSAO,
               COALESCE(NUMERO_PNEUS.TOTAL, 0) :: INTEGER                   AS PNEUS_APLICADOS,
               VT.STATUS_ATIVO                                              AS STATUS_ATIVO_TIPO_VEICULO,
               F_IF(CONFIG.CODIGO IS NULL, 'EQUIPAMENTO',
                    CONFIG.FORMA_COLETA_DADOS_SULCO)                        AS FORMA_COLETA_DADOS_SULCO,
               F_IF(CONFIG.CODIGO IS NULL, 'EQUIPAMENTO',
                    CONFIG.FORMA_COLETA_DADOS_PRESSAO)                      AS FORMA_COLETA_DADOS_PRESSAO,
               F_IF(CONFIG.CODIGO IS NULL, 'EQUIPAMENTO',
                    CONFIG.FORMA_COLETA_DADOS_SULCO_PRESSAO)                AS FORMA_COLETA_DADOS_SULCO_PRESSAO,
               F_IF(CONFIG.CODIGO IS NULL, TRUE, CONFIG.PODE_AFERIR_ESTEPE) AS PODE_AFERIR_ESTEPE
        FROM VEICULO V
                 JOIN PNEU_RESTRICAO_UNIDADE PRU ON PRU.
                                                        COD_UNIDADE = V.COD_UNIDADE
                 JOIN VEICULO_TIPO VT ON VT.
                                             CODIGO = V.COD_TIPO
                 JOIN MODELO_VEICULO MV ON MV.
                                               CODIGO = V.COD_MODELO
                 LEFT JOIN AFERICAO_CONFIGURACAO_TIPO_AFERICAO_VEICULO CONFIG
                           ON CONFIG.
                                  COD_TIPO_VEICULO = VT.CODIGO
                               AND CONFIG.COD_UNIDADE = V.COD_UNIDADE
                 LEFT JOIN (SELECT A.COD_VEICULO                                                              AS COD_VEICULO_INTERVALO,
                                   EXTRACT(DAYS FROM (F_DATA_HORA_ATUAL) -
                                                     MAX(A.DATA_HORA AT TIME ZONE TZ_UNIDADE(A.COD_UNIDADE))) AS INTERVALO
                            FROM AFERICAO A
                            WHERE A.TIPO_MEDICAO_COLETADA = 'PRESSAO'
                               OR A.TIPO_MEDICAO_COLETADA = 'SULCO_PRESSAO'
                            GROUP BY A.COD_VEICULO) AS INTERVALO_PRESSAO
                           ON INTERVALO_PRESSAO.COD_VEICULO_INTERVALO = V.CODIGO
                 LEFT JOIN (SELECT A.COD_VEICULO                                                              AS COD_VEICULO_INTERVALO,
                                   EXTRACT(DAYS FROM (F_DATA_HORA_ATUAL) -
                                                     MAX(A.DATA_HORA AT TIME ZONE TZ_UNIDADE(A.COD_UNIDADE))) AS INTERVALO
                            FROM AFERICAO A
                            WHERE A.TIPO_MEDICAO_COLETADA = 'SULCO'
                               OR A.TIPO_MEDICAO_COLETADA = 'SULCO_PRESSAO'
                            GROUP BY A.COD_VEICULO) AS INTERVALO_SULCO
                           ON INTERVALO_SULCO.COD_VEICULO_INTERVALO = V.CODIGO
                 LEFT JOIN (SELECT VP.COD_VEICULO     AS COD_VEICULOS,
                                   COUNT(VP.COD_PNEU) AS TOTAL
                            FROM VEICULO_PNEU VP
                            WHERE VP.COD_UNIDADE = ANY (F_COD_UNIDADES)
                            GROUP BY VP.COD_VEICULO) AS NUMERO_PNEUS ON
            COD_VEICULOS = V.CODIGO
        WHERE V.STATUS_ATIVO = TRUE
          AND V.COD_UNIDADE = ANY (F_COD_UNIDADES)
        ORDER BY MV.NOME, INTERVALO_PRESSAO DESC, INTERVALO_SULCO DESC, PNEUS_APLICADOS DESC;
END;
$$;

--------------------------------------------------------- INTEGRAÇÃO ---------------------------------------------------

create or replace function
    integracao.func_integracao_busca_afericoes_empresa(f_token_integracao text,
                                                       f_cod_ultima_afericao_sincronizada bigint)
    returns table
            (
                cod_afericao                       bigint,
                cod_unidade_afericao               bigint,
                cpf_colaborador                    text,
                placa_veiculo_aferido              varchar(255),
                cod_pneu_aferido                   bigint,
                numero_fogo                        varchar(255),
                altura_sulco_interno               numeric,
                altura_sulco_central_interno       numeric,
                altura_sulco_central_externo       numeric,
                altura_sulco_externo               numeric,
                pressao                            numeric,
                km_veiculo_momento_afericao        bigint,
                tempo_realizacao_afericao_em_milis bigint,
                vida_momento_afericao              integer,
                posicao_pneu_momento_afericao      integer,
                data_hora_afericao                 timestamp without time zone,
                tipo_medicao_coletada              varchar(13),
                tipo_processo_coleta               varchar(11)
            )
    language sql
as
$$
select a.codigo                                           as cod_afericao,
       a.cod_unidade                                      as cod_unidade_afericao,
       lpad(a.cpf_aferidor :: text, 11, '0')              as cpf_colaborador,
       v.placa                                            as placa_veiculo_aferido,
       av.cod_pneu                                        as cod_pneu_aferido,
       p.codigo_cliente                                   as numero_fogo,
       trunc(av.altura_sulco_interno::numeric, 1)         as altura_sulco_interno,
       trunc(av.altura_sulco_central_interno::numeric, 1) as altura_sulco_central_interno,
       trunc(av.altura_sulco_central_externo::numeric, 1) as altura_sulco_central_externo,
       trunc(av.altura_sulco_externo::numeric, 1)         as altura_sulco_externo,
       trunc(av.psi::numeric, 1)                          as pressao,
       a.km_veiculo                                       as km_veiculo_momento_afericao,
       a.tempo_realizacao                                 as tempo_realizacao_afericao_em_milis,
       av.vida_momento_afericao                           as vida_momento_afericao,
       av.posicao                                         as posicao_pneu_momento_afericao,
       a.data_hora at time zone tz_unidade(a.cod_unidade) as data_hora_afericao,
       a.tipo_medicao_coletada                            as tipo_medicao_coletada,
       a.tipo_processo_coleta                             as tipo_processo_coleta
from afericao a
         left join veiculo v on v.codigo = a.cod_veiculo
         join afericao_valores av on a.codigo = av.cod_afericao
         join pneu p on av.cod_pneu = p.codigo
where a.cod_unidade in (select codigo
                        from unidade
                        where cod_empresa in (select ti.cod_empresa
                                              from integracao.token_integracao ti
                                              where ti.token_integracao = f_token_integracao))
  and a.codigo > f_cod_ultima_afericao_sincronizada
order by a.codigo;
$$;

create or replace function
    integracao.func_pneu_afericao_busca_afericoes_realizadas_by_codigo(f_token_integracao text,
                                                                       f_cod_ultima_afericao_sincronizada bigint)
    returns table
            (
                cod_afericao                          bigint,
                cod_unidade_afericao                  bigint,
                cpf_colaborador                       text,
                identificador_frota                   text,
                placa_veiculo_aferido                 text,
                cod_pneu_aferido                      bigint,
                numero_fogo                           text,
                altura_sulco_interno                  numeric,
                altura_sulco_central_interno          numeric,
                altura_sulco_central_externo          numeric,
                altura_sulco_externo                  numeric,
                pressao                               numeric,
                km_veiculo_momento_afericao           bigint,
                tempo_realizacao_afericao_em_segundos bigint,
                vida_momento_afericao                 integer,
                posicao_pneu_momento_afericao         integer,
                data_hora_afericao                    timestamp without time zone,
                tipo_medicao_coletada                 text,
                tipo_processo_coleta                  text
            )
    language sql
as
$$
select a.codigo                                           as cod_afericao,
       a.cod_unidade                                      as cod_unidade_afericao,
       lpad(a.cpf_aferidor::text, 11, '0')                as cpf_colaborador,
       v.identificador_frota                              as identificador_frota,
       v.placa                                            as placa_veiculo_aferido,
       av.cod_pneu                                        as cod_pneu_aferido,
       p.codigo_cliente::text                             as numero_fogo,
       trunc(av.altura_sulco_interno::numeric, 2)         as altura_sulco_interno,
       trunc(av.altura_sulco_central_interno::numeric, 2) as altura_sulco_central_interno,
       trunc(av.altura_sulco_central_externo::numeric, 2) as altura_sulco_central_externo,
       trunc(av.altura_sulco_externo::numeric, 2)         as altura_sulco_externo,
       trunc(av.psi::numeric, 2)                          as pressao,
       a.km_veiculo                                       as km_veiculo_momento_afericao,
       f_millis_to_seconds(a.tempo_realizacao)            as tempo_realizacao_afericao_em_segundos,
       av.vida_momento_afericao                           as vida_momento_afericao,
       av.posicao                                         as posicao_pneu_momento_afericao,
       a.data_hora at time zone tz_unidade(a.cod_unidade) as data_hora_afericao,
       a.tipo_medicao_coletada::text                      as tipo_medicao_coletada,
       a.tipo_processo_coleta::text                       as tipo_processo_coleta
from afericao a
         join afericao_valores av on a.codigo = av.cod_afericao
         join pneu p on av.cod_pneu = p.codigo
         join veiculo v on v.codigo = a.cod_veiculo
where a.cod_unidade in (select codigo
                        from unidade
                        where cod_empresa in (select ti.cod_empresa
                                              from integracao.token_integracao ti
                                              where ti.token_integracao = f_token_integracao))
  and a.codigo > f_cod_ultima_afericao_sincronizada
order by a.codigo;
$$;

create or replace function
    integracao.func_pneu_afericao_busca_afericoes_realizadas_by_data_hora(f_token_integracao text,
                                                                          f_data_hora timestamp without time zone)
    returns table
            (
                cod_afericao                          bigint,
                cod_unidade_afericao                  bigint,
                cpf_colaborador                       text,
                identificador_frota                   text,
                placa_veiculo_aferido                 text,
                cod_pneu_aferido                      bigint,
                numero_fogo                           text,
                altura_sulco_interno                  numeric,
                altura_sulco_central_interno          numeric,
                altura_sulco_central_externo          numeric,
                altura_sulco_externo                  numeric,
                pressao                               numeric,
                km_veiculo_momento_afericao           bigint,
                tempo_realizacao_afericao_em_segundos bigint,
                vida_momento_afericao                 integer,
                posicao_pneu_momento_afericao         integer,
                data_hora_afericao                    timestamp without time zone,
                tipo_medicao_coletada                 text,
                tipo_processo_coleta                  text
            )
    language sql
as
$$
select a.codigo                                           as cod_afericao,
       a.cod_unidade                                      as cod_unidade_afericao,
       lpad(a.cpf_aferidor::text, 11, '0')                as cpf_colaborador,
       v.placa                                            as placa_veiculo_aferido,
       v.identificador_frota                              as identificador_frota,
       av.cod_pneu                                        as cod_pneu_aferido,
       p.codigo_cliente::text                             as numero_fogo,
       trunc(av.altura_sulco_interno::numeric, 2)         as altura_sulco_interno,
       trunc(av.altura_sulco_central_interno::numeric, 2) as altura_sulco_central_interno,
       trunc(av.altura_sulco_central_externo::numeric, 2) as altura_sulco_central_externo,
       trunc(av.altura_sulco_externo::numeric, 2)         as altura_sulco_externo,
       trunc(av.psi::numeric, 2)                          as pressao,
       a.km_veiculo                                       as km_veiculo_momento_afericao,
       f_millis_to_seconds(a.tempo_realizacao)            as tempo_realizacao_afericao_em_segundos,
       av.vida_momento_afericao                           as vida_momento_afericao,
       av.posicao                                         as posicao_pneu_momento_afericao,
       a.data_hora at time zone tz_unidade(a.cod_unidade) as data_hora_afericao,
       a.tipo_medicao_coletada::text                      as tipo_medicao_coletada,
       a.tipo_processo_coleta::text                       as tipo_processo_coleta
from afericao a
         join afericao_valores av on a.codigo = av.cod_afericao
         join pneu p on av.cod_pneu = p.codigo
         join veiculo v on a.cod_veiculo = v.codigo
where a.cod_unidade in (select codigo
                        from unidade
                        where cod_empresa in (select ti.cod_empresa
                                              from integracao.token_integracao ti
                                              where ti.token_integracao = f_token_integracao))
  and a.data_hora > f_data_hora
order by a.data_hora;
$$;

create or replace function func_afericao_relatorio_exportacao_protheus(f_cod_unidades bigint[],
                                                                       f_cod_veiculos bigint[],
                                                                       f_data_inicial date,
                                                                       f_data_final date)
    returns table
            (
                codigo_afericao      bigint,
                cabecalho_placa      text,
                placa                varchar(7),
                data                 text,
                hora                 text,
                cabecalho_pneu       text,
                codigo_cliente_pneu  text,
                nomenclatura_posicao text,
                calibragem_aferida   real,
                calibragem_realizada real,
                sulco_interno        real,
                sulco_central        real,
                sulco_externo        real
            )
    language plpgsql
as
$$
begin
    return query
        select a.codigo                                                                        as codigo_afericao,
               'TTO'                                                                           as cabecalho_placa,
               v.placa                                                                         as placa,
               to_char(a.data_hora at time zone tz_unidade(a.cod_unidade), 'DD/MM/YYYY')::text as data,
               to_char(a.data_hora at time zone tz_unidade(a.cod_unidade), 'HH24:MI')::text    as hora,
               'TTP'                                                                           as cabecalho_pneu,
               lpad(p.codigo_cliente::text, 7, '0')                                            as codigo_fogo_pneu,
               remove_extra_spaces(coalesce(ppne.nomenclatura::text, ''), true)
                                                                                               as nomenclatura_posicao,
               coalesce(round(cast(av.psi as numeric), 2), -1)::real                           as calibragem_aferida,
               coalesce(round(cast(av.psi as numeric), 2), -1)::real                           as calibragem_realizada,
               coalesce(round(cast(av.altura_sulco_interno as numeric), 2), -1)::real          as altura_sulco_interno,
               coalesce(round(cast(av.altura_sulco_central_interno as numeric), 2), -1)::real  as sulco_central,
               coalesce(round(cast(av.altura_sulco_externo as numeric), 2), -1)::real          as altura_sulco_externo
        from afericao a
                 inner join afericao_valores av on av.cod_afericao = a.codigo
                 inner join pneu p on p.codigo = av.cod_pneu
                 inner join veiculo v on v.codigo = a.cod_veiculo
                 left join pneu_posicao_nomenclatura_empresa ppne
                           on ppne.posicao_prolog = av.posicao and ppne.cod_diagrama = a.cod_diagrama and
                              ppne.cod_empresa = v.cod_empresa
        where a.tipo_processo_coleta = 'PLACA'
          and a.cod_veiculo = any (f_cod_veiculos)
          and a.cod_unidade = any (f_cod_unidades)
          and (a.data_hora at time zone tz_unidade(a.cod_unidade))::date
            between f_data_inicial and f_data_final
        order by a.codigo;
end
$$;

CREATE OR REPLACE FUNCTION
    FUNC_AFERICAO_RELATORIO_CRONOGRAMA_AFERICOES_PLACAS(F_COD_UNIDADES BIGINT[],
                                                        F_DATA_HORA_ATUAL_UTC TIMESTAMP WITH TIME ZONE,
                                                        F_DATA_HORA_GERACAO_RELATORIO TIMESTAMP WITH TIME ZONE)
    RETURNS TABLE
            (
                UNIDADE                              TEXT,
                PLACA                                TEXT,
                "IDENTIFICADOR FROTA"                TEXT,
                "QTD PNEUS APLICADOS"                TEXT,
                "MODELO VEÍCULO"                     TEXT,
                "TIPO VEÍCULO"                       TEXT,
                "STATUS SULCO"                       TEXT,
                "STATUS PRESSÃO"                     TEXT,
                "DATA VENCIMENTO SULCO"              TEXT,
                "DATA VENCIMENTO PRESSÃO"            TEXT,
                "DIAS VENCIMENTO SULCO"              TEXT,
                "DIAS VENCIMENTO PRESSÃO"            TEXT,
                "DIAS DESDE ÚLTIMA AFERIÇÃO SULCO"   TEXT,
                "DATA/HORA ÚLTIMA AFERIÇÃO SULCO"    TEXT,
                "DIAS DESDE ÚLTIMA AFERIÇÃO PRESSÃO" TEXT,
                "DATA/HORA ÚLTIMA AFERIÇÃO PRESSÃO"  TEXT,
                "DATA/HORA GERAÇÃO RELATÓRIO"        TEXT
            )
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    RETURN QUERY
        WITH DADOS AS (
            SELECT U.NOME::TEXT                                                         AS NOME_UNIDADE,
                   V.PLACA::TEXT                                                        AS PLACA_VEICULO,
                   COALESCE(V.IDENTIFICADOR_FROTA::TEXT, '-')                           AS IDENTIFICADOR_FROTA,
                   (SELECT COUNT(VP.COD_PNEU)
                    FROM VEICULO_PNEU VP
                    WHERE VP.COD_VEICULO = V.CODIGO
                    GROUP BY VP.COD_VEICULO)::TEXT                                      AS QTD_PNEUS_APLICADOS,
                   MV.NOME::TEXT                                                        AS NOME_MODELO_VEICULO,
                   VT.NOME::TEXT                                                        AS NOME_TIPO_VEICULO,
                   TO_CHAR(SULCO.DATA_HORA_ULTIMA_AFERICAO_SULCO, 'DD/MM/YYYY HH24:MI') AS DATA_HORA_ULTIMA_AFERICAO_SULCO,
                   TO_CHAR(PRESSAO.DATA_HORA_ULTIMA_AFERICAO_PRESSAO,
                           'DD/MM/YYYY HH24:MI')                                        AS DATA_HORA_ULTIMA_AFERICAO_PRESSAO,
                   TO_CHAR(SULCO.DATA_ULTIMA_AFERICAO_SULCO
                               + (PRU.PERIODO_AFERICAO_SULCO || ' DAYS')::INTERVAL,
                           'DD/MM/YYYY')                                                AS DATA_VENCIMENTO_SULCO,
                   TO_CHAR(PRESSAO.DATA_ULTIMA_AFERICAO_PRESSAO
                               + (PRU.PERIODO_AFERICAO_PRESSAO || ' DAYS')::INTERVAL,
                           'DD/MM/YYYY')                                                AS DATA_VENCIMENTO_PRESSAO,
                   (PRU.PERIODO_AFERICAO_SULCO - SULCO.DIAS)::TEXT                      AS DIAS_VENCIMENTO_SULCO,
                   (PRU.PERIODO_AFERICAO_PRESSAO - PRESSAO.DIAS)::TEXT                  AS DIAS_VENCIMENTO_PRESSAO,
                   SULCO.DIAS::TEXT                                                     AS DIAS_DESDE_ULTIMA_AFERICAO_SULCO,
                   PRESSAO.DIAS::TEXT                                                   AS DIAS_DESDE_ULTIMA_AFERICAO_PRESSAO,
                   F_IF(CONFIG.FORMA_COLETA_DADOS_SULCO <> 'BLOQUEADO'
                            OR CONFIG.FORMA_COLETA_DADOS_SULCO_PRESSAO <> 'BLOQUEADO',
                        TRUE,
                        FALSE)                                                          AS PODE_AFERIR_SULCO,
                   F_IF(CONFIG.FORMA_COLETA_DADOS_PRESSAO <> 'BLOQUEADO'
                            OR CONFIG.FORMA_COLETA_DADOS_SULCO_PRESSAO <> 'BLOQUEADO',
                        TRUE,
                        FALSE)                                                          AS PODE_AFERIR_PRESSAO,
                   F_IF(SULCO.DIAS IS NULL, TRUE,
                        FALSE)                                                          AS SULCO_NUNCA_AFERIDO,
                   F_IF(PRESSAO.DIAS IS NULL, TRUE,
                        FALSE)                                                          AS PRESSAO_NUNCA_AFERIDA,
                   F_IF(SULCO.DIAS > PRU.PERIODO_AFERICAO_SULCO, TRUE,
                        FALSE)                                                          AS AFERICAO_SULCO_VENCIDA,
                   F_IF(PRESSAO.DIAS > PRU.PERIODO_AFERICAO_PRESSAO, TRUE,
                        FALSE)                                                          AS AFERICAO_PRESSAO_VENCIDA
            FROM VEICULO V
                     JOIN MODELO_VEICULO MV
                          ON MV.CODIGO = V.COD_MODELO
                     JOIN VEICULO_TIPO VT
                          ON VT.CODIGO = V.COD_TIPO
                     JOIN FUNC_AFERICAO_GET_CONFIG_TIPO_AFERICAO_VEICULO(V.COD_UNIDADE) CONFIG
                          ON CONFIG.COD_TIPO_VEICULO = V.COD_TIPO
                     LEFT JOIN
                 (SELECT A.COD_VEICULO                                                 AS COD_VEICULO_INTERVALO,
                         MAX(A.DATA_HORA AT TIME ZONE
                             TZ_UNIDADE(A.COD_UNIDADE))::DATE                          AS DATA_ULTIMA_AFERICAO_PRESSAO,
                         MAX(A.DATA_HORA AT TIME ZONE
                             TZ_UNIDADE(A.COD_UNIDADE))                                AS DATA_HORA_ULTIMA_AFERICAO_PRESSAO,
                         EXTRACT(DAYS FROM (F_DATA_HORA_ATUAL_UTC) - MAX(A.DATA_HORA)) AS DIAS
                  FROM AFERICAO A
                  WHERE A.TIPO_MEDICAO_COLETADA = 'PRESSAO'
                     OR A.TIPO_MEDICAO_COLETADA = 'SULCO_PRESSAO'
                  GROUP BY A.COD_VEICULO) AS PRESSAO ON PRESSAO.COD_VEICULO_INTERVALO = V.CODIGO
                     LEFT JOIN
                 (SELECT A.COD_VEICULO                                               AS COD_VEICULO_INTERVALO,
                         MAX(A.DATA_HORA AT TIME ZONE
                             TZ_UNIDADE(A.COD_UNIDADE)) :: DATE                      AS DATA_ULTIMA_AFERICAO_SULCO,
                         MAX(A.DATA_HORA AT TIME ZONE
                             TZ_UNIDADE(A.COD_UNIDADE))                              AS DATA_HORA_ULTIMA_AFERICAO_SULCO,
                         EXTRACT(DAYS FROM F_DATA_HORA_ATUAL_UTC - MAX(A.DATA_HORA)) AS DIAS
                  FROM AFERICAO A
                  WHERE A.TIPO_MEDICAO_COLETADA = 'SULCO'
                     OR A.TIPO_MEDICAO_COLETADA = 'SULCO_PRESSAO'
                  GROUP BY A.COD_VEICULO) AS SULCO ON SULCO.COD_VEICULO_INTERVALO = V.CODIGO
                     JOIN PNEU_RESTRICAO_UNIDADE PRU
                          ON PRU.COD_UNIDADE = V.COD_UNIDADE
                     JOIN UNIDADE U
                          ON U.CODIGO = V.COD_UNIDADE
            WHERE V.STATUS_ATIVO = TRUE
              AND V.COD_UNIDADE = ANY (F_COD_UNIDADES)
            ORDER BY U.CODIGO, V.PLACA
        )
             -- Todos os coalesce ficam aqui.
        SELECT D.NOME_UNIDADE                                               AS NOME_UNIDADE,
               D.PLACA_VEICULO                                              AS PLACA_VEICULO,
               D.IDENTIFICADOR_FROTA                                        AS IDENTIFICADOR_FROTA,
               COALESCE(D.QTD_PNEUS_APLICADOS, '-')                         AS QTD_PNEUS_APLICADOS,
               D.NOME_MODELO_VEICULO                                        AS NOME_MODELO_VEICULO,
               D.NOME_TIPO_VEICULO                                          AS NOME_TIPO_VEICULO,
               CASE
                   WHEN NOT D.PODE_AFERIR_SULCO
                       THEN 'BLOQUEADO AFERIÇÃO'
                   WHEN D.SULCO_NUNCA_AFERIDO
                       THEN 'SULCO NUNCA AFERIDO'
                   WHEN D.AFERICAO_SULCO_VENCIDA
                       THEN 'VENCIDO'
                   ELSE 'NO PRAZO'
                   END                                                      AS STATUS_SULCO,
               CASE
                   WHEN NOT D.PODE_AFERIR_PRESSAO
                       THEN 'BLOQUEADO AFERIÇÃO'
                   WHEN D.PRESSAO_NUNCA_AFERIDA
                       THEN 'PRESSÃO NUNCA AFERIDA'
                   WHEN D.AFERICAO_PRESSAO_VENCIDA
                       THEN 'VENCIDO'
                   ELSE 'NO PRAZO'
                   END                                                      AS STATUS_PRESSAO,
               F_IF(D.SULCO_NUNCA_AFERIDO, '-',
                    D.DATA_VENCIMENTO_SULCO)                                AS DATA_VENCIMENTO_SULCO,
               F_IF(D.PRESSAO_NUNCA_AFERIDA, '-',
                    D.DATA_VENCIMENTO_PRESSAO)                              AS DATA_VENCIMENTO_PRESSAO,
               F_IF(D.SULCO_NUNCA_AFERIDO, '-',
                    D.DIAS_VENCIMENTO_SULCO)                                AS DIAS_VENCIMENTO_SULCO,
               F_IF(D.PRESSAO_NUNCA_AFERIDA, '-',
                    D.DIAS_VENCIMENTO_PRESSAO)                              AS DIAS_VENCIMENTO_PRESSAO,
               F_IF(D.SULCO_NUNCA_AFERIDO, '-',
                    D.DIAS_DESDE_ULTIMA_AFERICAO_SULCO)                     AS DIAS_DESDE_ULTIMA_AFERICAO_SULCO,
               COALESCE(D.DATA_HORA_ULTIMA_AFERICAO_SULCO, '-')             AS DATA_HORA_ULTIMA_AFERICAO_SULCO,
               F_IF(D.PRESSAO_NUNCA_AFERIDA, '-',
                    D.DIAS_DESDE_ULTIMA_AFERICAO_PRESSAO)                   AS DIAS_DESDE_ULTIMA_AFERICAO_PRESSAO,
               COALESCE(D.DATA_HORA_ULTIMA_AFERICAO_PRESSAO, '-')           AS DATA_HORA_ULTIMA_AFERICAO_PRESSAO,
               TO_CHAR(F_DATA_HORA_GERACAO_RELATORIO, 'DD/MM/YYYY HH24:MI') AS DATA_HORA_GERACAO_RELATORIO
        FROM DADOS D;
END;
$$;

create or replace function func_afericao_relatorio_dados_gerais(f_cod_unidades bigint[],
                                                                f_data_inicial date,
                                                                f_data_final date)
    returns table
            (
                "CÓDIGO AFERIÇÃO"           text,
                "UNIDADE"                   text,
                "DATA E HORA"               text,
                "CPF DO RESPONSÁVEL"        text,
                "NOME COLABORADOR"          text,
                "PNEU"                      text,
                "STATUS ATUAL"              text,
                "VALOR COMPRA"              text,
                "MARCA DO PNEU"             text,
                "MODELO DO PNEU"            text,
                "QTD SULCOS MODELO"         text,
                "VIDA ATUAL"                text,
                "VALOR VIDA ATUAL"          text,
                "BANDA APLICADA"            text,
                "QTD SULCOS BANDA"          text,
                "DIMENSÃO"                  text,
                "DOT"                       text,
                "DATA E HORA CADASTRO"      text,
                "POSIÇÃO PNEU"              text,
                "PLACA"                     text,
                "IDENTIFICADOR FROTA"       text,
                "VIDA MOMENTO AFERIÇÃO"     text,
                "KM NO MOMENTO DA AFERIÇÃO" text,
                "KM ATUAL"                  text,
                "MARCA DO VEÍCULO"          text,
                "MODELO DO VEÍCULO"         text,
                "TIPO DE MEDIÇÃO COLETADA"  text,
                "TIPO DA AFERIÇÃO"          text,
                "TEMPO REALIZAÇÃO (MM:SS)"  text,
                "SULCO INTERNO"             text,
                "SULCO CENTRAL INTERNO"     text,
                "SULCO CENTRAL EXTERNO"     text,
                "SULCO EXTERNO"             text,
                "MENOR SULCO"               text,
                "PRESSÃO"                   text,
                "FORMA DE COLETA DOS DADOS" text
            )
    language sql
as
$$
select a.codigo :: text                                                                 as cod_afericao,
       u.nome                                                                           as unidade,
       to_char((a.data_hora at time zone tz_unidade(a.cod_unidade)),
               'DD/MM/YYYY HH24:MI')                                                    as data_hora_afericao,
       lpad(c.cpf :: text, 11, '0')                                                     as cpf_colaborador,
       c.nome                                                                           as nome_colaborador,
       p.codigo_cliente                                                                 as codigo_cliente_pneu,
       p.status                                                                         as status_atual_pneu,
       round(p.valor :: numeric, 2) :: text                                             as valor_compra,
       map.nome                                                                         as marca_pneu,
       mp.nome                                                                          as modelo_pneu,
       mp.qt_sulcos :: text                                                             as qtd_sulcos_modelo,
       (select pvn.nome
        from pneu_vida_nomenclatura pvn
        where pvn.cod_vida = p.vida_atual)                                              as vida_atual,
       coalesce(round(pvv.valor :: numeric, 2) :: text, '-')                            as valor_vida_atual,
       f_if(marb.codigo is not null, marb.nome || ' - ' || modb.nome, 'Nunca Recapado') as banda_aplicada,
       coalesce(modb.qt_sulcos :: text, '-')                                            as qtd_sulcos_banda,
       dp.largura || '-' || dp.altura || '/' || dp.aro                                  as dimensao,
       p.dot                                                                            as dot,
       coalesce(to_char(p.data_hora_cadastro at time zone tz_unidade(p.cod_unidade_cadastro),
                        'DD/MM/YYYY HH24:MI'),
                '-')                                                                    as data_hora_cadastro,
       coalesce(ppne.nomenclatura, '-')                                                 as posicao,
       coalesce(v.placa, '-')                                                           as placa,
       coalesce(v.identificador_frota, '-')                                             as identificador_frota,
       (select pvn.nome
        from pneu_vida_nomenclatura pvn
        where pvn.cod_vida = av.vida_momento_afericao)                                  as vida_momento_afericao,
       coalesce(a.km_veiculo :: text, '-')                                              as km_momento_afericao,
       coalesce(v.km :: text, '-')                                                      as km_atual,
       coalesce(m2.nome, '-')                                                           as marca_veiculo,
       coalesce(mv.nome, '-')                                                           as modelo_veiculo,
       a.tipo_medicao_coletada                                                          as tipo_medicao_coletada,
       a.tipo_processo_coleta                                                           as tipo_processo_coleta,
       to_char((a.tempo_realizacao || ' milliseconds') :: interval, 'MI:SS')            as tempo_realizacao_minutos,
       func_pneu_format_sulco(av.altura_sulco_interno)                                  as sulco_interno,
       func_pneu_format_sulco(av.altura_sulco_central_interno)                          as sulco_central_interno,
       func_pneu_format_sulco(av.altura_sulco_central_externo)                          as sulco_central_externo,
       func_pneu_format_sulco(av.altura_sulco_externo)                                  as sulco_externo,
       func_pneu_format_sulco(least(av.altura_sulco_externo,
                                    av.altura_sulco_central_externo,
                                    av.altura_sulco_central_interno,
                                    av.altura_sulco_interno))                           as menor_sulco,
       replace(coalesce(trunc(av.psi :: numeric, 1) :: text, '-'), '.', ',')            as pressao,
       coalesce(tafcd.status_legivel::text, '-'::text)                                  as status_legivel
from afericao a
         join afericao_valores av on a.codigo = av.cod_afericao
         join unidade u on u.codigo = a.cod_unidade
         join colaborador c on c.cpf = a.cpf_aferidor
         join pneu p on p.codigo = av.cod_pneu
         join modelo_pneu mp on p.cod_modelo = mp.codigo
         join marca_pneu map on map.codigo = mp.cod_marca
         join dimensao_pneu dp on p.cod_dimensao = dp.codigo
         left join pneu_valor_vida pvv on p.codigo = pvv.cod_pneu and p.vida_atual = pvv.vida
         left join types.afericao_forma_coleta_dados tafcd on tafcd.forma_coleta_dados = a.forma_coleta_dados::text

    -- Pode não possuir banda.
         left join modelo_banda modb on modb.codigo = p.cod_modelo_banda
         left join marca_banda marb on marb.codigo = modb.cod_marca

    -- Se foi aferição de pneu avulso, pode não possuir codigo do veiculo.
         left join veiculo v on v.codigo = a.cod_veiculo

         left join pneu_posicao_nomenclatura_empresa ppne
                   on ppne.cod_empresa = p.cod_empresa
                       and ppne.cod_diagrama = a.cod_diagrama
                       and ppne.posicao_prolog = av.posicao
         left join modelo_veiculo mv
                   on mv.codigo = v.cod_modelo
         left join marca_veiculo m2
                   on mv.cod_marca = m2.codigo
where a.cod_unidade = any (f_cod_unidades)
  and a.data_hora >= (f_data_inicial::date - interval '1 day')
  and a.data_hora <= (f_data_final::date + interval '1 day')
  and (a.data_hora at time zone tz_unidade(a.cod_unidade)) :: date between f_data_inicial and f_data_final
order by u.codigo, a.data_hora desc;
$$;

CREATE OR REPLACE FUNCTION FUNC_AFERICAO_RELATORIO_QTD_DIAS_PLACAS_VENCIDAS(F_COD_UNIDADES BIGINT[],
                                                                            F_DATA_HOJE_UTC TIMESTAMP WITH TIME ZONE)
    RETURNS TABLE
            (
                UNIDADE                           TEXT,
                PLACA                             TEXT,
                IDENTIFICADOR_FROTA               TEXT,
                PODE_AFERIR_SULCO                 BOOLEAN,
                PODE_AFERIR_PRESSAO               BOOLEAN,
                QTD_DIAS_AFERICAO_SULCO_VENCIDA   INTEGER,
                QTD_DIAS_AFERICAO_PRESSAO_VENCIDA INTEGER
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    AFERICAO_SULCO         VARCHAR := 'SULCO';
    AFERICAO_PRESSAO       VARCHAR := 'PRESSAO';
    AFERICAO_SULCO_PRESSAO VARCHAR := 'SULCO_PRESSAO';
BEGIN
    RETURN QUERY
        WITH VEICULOS_ATIVOS_UNIDADES AS (
            SELECT V.CODIGO
            FROM VEICULO V
            WHERE V.COD_UNIDADE = ANY (F_COD_UNIDADES)
              AND V.STATUS_ATIVO
        ),
             -- As CTEs ULTIMA_AFERICAO_SULCO e ULTIMA_AFERICAO_PRESSAO retornam o codigo de cada veículo e a quantidade de dias
             -- que a aferição de sulco e pressão, respectivamente, estão vencidas. Um número negativo será retornado caso ainda
             -- esteja com a aferição no prazo e ele indicará quantos dias faltam para vencer. Um -20, por exemplo, significa
             -- que a aferição vai vencer em 20 dias.
             ULTIMA_AFERICAO_SULCO AS (
                 SELECT DISTINCT ON (A.COD_VEICULO) A.COD_UNIDADE,
                                                    A.COD_VEICULO                AS COD_VEICULO,
                                                    DATE_PART('DAY', F_DATA_HOJE_UTC - MAX(DATA_HORA))
                                                        -
                                                    (PRU.PERIODO_AFERICAO_SULCO) AS QTD_DIAS_ENTRE_ULTIMA_AFERICAO_SULCO_E_HOJE
                 FROM AFERICAO A
                          JOIN PNEU_RESTRICAO_UNIDADE PRU
                               ON (SELECT V.COD_UNIDADE
                                   FROM VEICULO V
                                   WHERE V.CODIGO = A.COD_VEICULO) = PRU.COD_UNIDADE
                 WHERE A.TIPO_MEDICAO_COLETADA IN (AFERICAO_SULCO, AFERICAO_SULCO_PRESSAO)
                   -- Desse modo nós buscamos a última aferição de cada placa que está ativa nas unidades filtradas, independente
                   -- de onde foram foram aferidas.
                   AND COD_VEICULO = ANY (SELECT VAU.CODIGO
                                          FROM VEICULOS_ATIVOS_UNIDADES VAU)
                 GROUP BY A.DATA_HORA,
                          A.COD_UNIDADE,
                          A.COD_VEICULO,
                          PRU.PERIODO_AFERICAO_SULCO
                 ORDER BY A.COD_VEICULO, A.DATA_HORA DESC
             ),
             ULTIMA_AFERICAO_PRESSAO AS (
                 SELECT DISTINCT ON (A.COD_VEICULO) A.COD_UNIDADE,
                                                    A.COD_VEICULO                  AS COD_VEICULO,
                                                    DATE_PART('DAY', F_DATA_HOJE_UTC - MAX(DATA_HORA))
                                                        -
                                                    (PRU.PERIODO_AFERICAO_PRESSAO) AS QTD_DIAS_ENTRE_ULTIMA_AFERICAO_PRESSAO_E_HOJE
                 FROM AFERICAO A
                          JOIN PNEU_RESTRICAO_UNIDADE PRU
                               ON (SELECT V.COD_UNIDADE
                                   FROM VEICULO V
                                   WHERE V.CODIGO = A.COD_VEICULO) = PRU.COD_UNIDADE
                 WHERE A.COD_UNIDADE = ANY (F_COD_UNIDADES)
                   AND A.TIPO_MEDICAO_COLETADA IN (AFERICAO_PRESSAO, AFERICAO_SULCO_PRESSAO)
                   AND COD_VEICULO = ANY (SELECT VAU.CODIGO
                                          FROM VEICULOS_ATIVOS_UNIDADES VAU)
                 GROUP BY A.DATA_HORA,
                          A.COD_UNIDADE,
                          A.COD_VEICULO,
                          PRU.PERIODO_AFERICAO_PRESSAO
                 ORDER BY A.COD_VEICULO, A.DATA_HORA DESC
             ),

             PRE_SELECT AS (
                 SELECT U.NOME                                            AS NOME_UNIDADE,
                        V.PLACA                                           AS PLACA_VEICULO,
                        COALESCE(V.IDENTIFICADOR_FROTA, '-')              AS IDENTIFICADOR_FROTA,
                        COALESCE((
                                     SELECT (FA.FORMA_COLETA_DADOS_SULCO IN ('EQUIPAMENTO', 'MANUAL') OR
                                             FA.FORMA_COLETA_DADOS_SULCO_PRESSAO IN ('EQUIPAMENTO', 'MANUAL'))
                                     FROM FUNC_AFERICAO_GET_CONFIG_TIPO_AFERICAO_VEICULO(V.COD_UNIDADE) FA
                                     WHERE FA.COD_TIPO_VEICULO = V.COD_TIPO), FALSE)
                                                                          AS PODE_AFERIR_SULCO,
                        COALESCE((
                                     SELECT (FA.FORMA_COLETA_DADOS_PRESSAO IN ('EQUIPAMENTO', 'MANUAL') OR
                                             FA.FORMA_COLETA_DADOS_SULCO_PRESSAO IN ('EQUIPAMENTO', 'MANUAL'))
                                     FROM FUNC_AFERICAO_GET_CONFIG_TIPO_AFERICAO_VEICULO(V.COD_UNIDADE) FA
                                     WHERE FA.COD_TIPO_VEICULO = V.COD_TIPO), FALSE)
                                                                          AS PODE_AFERIR_PRESSAO,
                        -- Por conta do filtro no where, agora não é mais a diferença de dias e sim somente as vencidas (ou ainda
                        -- nunca aferidas).
                        UAS.QTD_DIAS_ENTRE_ULTIMA_AFERICAO_SULCO_E_HOJE   AS QTD_DIAS_AFERICAO_SULCO_VENCIDA,
                        UAP.QTD_DIAS_ENTRE_ULTIMA_AFERICAO_PRESSAO_E_HOJE AS QTD_DIAS_AFERICAO_PRESSAO_VENCIDA
                 FROM UNIDADE U
                          JOIN VEICULO V
                               ON V.COD_UNIDADE = U.CODIGO
                          LEFT JOIN ULTIMA_AFERICAO_SULCO UAS
                                    ON UAS.COD_VEICULO = V.CODIGO
                          LEFT JOIN ULTIMA_AFERICAO_PRESSAO UAP
                                    ON UAP.COD_VEICULO = V.CODIGO
                 WHERE
                     -- Se algum dos dois tipos de aferição estiver vencido, retornamos a linha.
                     (UAS.QTD_DIAS_ENTRE_ULTIMA_AFERICAO_SULCO_E_HOJE > 0 OR
                      UAP.QTD_DIAS_ENTRE_ULTIMA_AFERICAO_PRESSAO_E_HOJE > 0)
                 GROUP BY U.NOME,
                          V.PLACA,
                          V.IDENTIFICADOR_FROTA,
                          V.COD_TIPO,
                          V.COD_UNIDADE,
                          UAS.QTD_DIAS_ENTRE_ULTIMA_AFERICAO_SULCO_E_HOJE,
                          UAP.QTD_DIAS_ENTRE_ULTIMA_AFERICAO_PRESSAO_E_HOJE
             )
        SELECT PS.NOME_UNIDADE::TEXT                         AS NOME_UNIDADE,
               PS.PLACA_VEICULO::TEXT                        AS PLACA_VEICULO,
               PS.IDENTIFICADOR_FROTA::TEXT                  AS IDENTIFICADOR_FROTA,
               PS.PODE_AFERIR_SULCO                          AS PODE_AFERIR_SULCO,
               PS.PODE_AFERIR_PRESSAO                        AS PODE_AFERIR_PRESSAO,
               PS.QTD_DIAS_AFERICAO_SULCO_VENCIDA::INTEGER   AS QTD_DIAS_AFERICAO_SULCO_VENCIDA,
               PS.QTD_DIAS_AFERICAO_PRESSAO_VENCIDA::INTEGER AS QTD_DIAS_AFERICAO_PRESSAO_VENCIDA
        FROM PRE_SELECT PS
             -- Para a placa ser exibida, ao menos um dos tipos de aferições, de sulco ou pressão, devem estar habilitadas.
        WHERE PS.PODE_AFERIR_SULCO <> FALSE
           OR PS.PODE_AFERIR_PRESSAO <> FALSE
        ORDER BY PS.QTD_DIAS_AFERICAO_SULCO_VENCIDA DESC,
                 PS.QTD_DIAS_AFERICAO_PRESSAO_VENCIDA DESC;
END;
$$;

CREATE OR REPLACE FUNCTION FUNC_PNEU_RELATORIO_QUANTIDADE_KMS_RODADOS_COM_SERVICOS_ABERTOS(F_COD_UNIDADES BIGINT[])
    RETURNS TABLE
            (
                PLACA_VEICULO TEXT,
                TOTAL_KM      BIGINT
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    TIPO_SERVICO_CALIBRAGEM TEXT := 'calibragem';
    TIPO_SERVICO_INSPECAO   TEXT := 'inspecao';
BEGIN
    RETURN QUERY
        WITH DADOS AS (SELECT DISTINCT ON (V.PLACA) V.PLACA                               AS PLACA_VEICULO,
                                                    AM.KM_MOMENTO_CONSERTO - A.KM_VEICULO AS TOTAL_KM
                       FROM AFERICAO_MANUTENCAO AM
                                JOIN AFERICAO A ON A.CODIGO = AM.COD_AFERICAO
                                JOIN VEICULO V ON V.CODIGO = A.COD_VEICULO
                                JOIN VEICULO_PNEU VP ON VP.COD_VEICULO = A.COD_VEICULO
                           AND AM.COD_PNEU = VP.COD_PNEU
                           AND AM.COD_UNIDADE = VP.COD_UNIDADE
                       WHERE AM.COD_UNIDADE = ANY (F_COD_UNIDADES)
                         AND AM.DATA_HORA_RESOLUCAO IS NOT NULL
                         AND (AM.TIPO_SERVICO IN (TIPO_SERVICO_CALIBRAGEM, TIPO_SERVICO_INSPECAO))
        )

        SELECT D.PLACA_VEICULO :: TEXT AS PLACA_VEICULO,
               D.TOTAL_KM              AS TOTAL_KM
        FROM DADOS D
        WHERE D.TOTAL_KM > 0
        ORDER BY TOTAL_KM DESC;
END;
$$;

CREATE OR REPLACE FUNCTION FUNC_RELATORIO_PNEU_ADERENCIA_AFERICAO(F_COD_UNIDADE TEXT[], F_DATA_INICIAL DATE, F_DATA_FINAL DATE)
    RETURNS TABLE
            (
                "UNIDADE ALOCADA"                          TEXT,
                "PLACA"                                    CHARACTER VARYING,
                "IDENTIFICADOR FROTA"                      TEXT,
                "QT AFERIÇÕES DE PRESSÃO"                  BIGINT,
                "MAX DIAS ENTRE AFERIÇÕES DE PRESSÃO"      TEXT,
                "MIN DIAS ENTRE AFERIÇÕES DE PRESSÃO"      TEXT,
                "MÉDIA DE DIAS ENTRE AFERIÇÕES DE PRESSÃO" TEXT,
                "QTD AFERIÇÕES DE PRESSÃO DENTRO DA META"  BIGINT,
                "ADERÊNCIA AFERIÇÕES DE PRESSÃO"           TEXT,
                "QT AFERIÇÕES DE SULCO"                    BIGINT,
                "MAX DIAS ENTRE AFERIÇÕES DE SULCO"        TEXT,
                "MIN DIAS ENTRE AFERIÇÕES DE SULCO"        TEXT,
                "MÉDIA DE DIAS ENTRE AFERIÇÕES DE SULCO"   TEXT,
                "QTD AFERIÇÕES DE SULCO DENTRO DA META"    BIGINT,
                "ADERÊNCIA AFERIÇÕES DE SULCO"             TEXT
            )
    LANGUAGE SQL
AS
$$
SELECT U.NOME                               AS "UNIDADE ALOCADA",
       V.PLACA                              AS PLACA,
       COALESCE(V.IDENTIFICADOR_FROTA, '-') AS IDENTIFICADOR_FROTA,
       COALESCE(CALCULO_PRESSAO.QTD_AFERICOES, 0),
       COALESCE(CALCULO_PRESSAO.MAX_DIAS_ENTRE_AFERICOES, '0'),
       COALESCE(CALCULO_PRESSAO.MIN_DIAS_ENTRE_AFERICOES, '0'),
       COALESCE(CALCULO_PRESSAO.MD_DIAS_ENTRE_AFERICOES, '0'),
       COALESCE(CALCULO_PRESSAO.QTD_AFERICOES_DENTRO_META, 0),
       COALESCE(CALCULO_PRESSAO.ADERENCIA, '0%'),
       COALESCE(CALCULO_SULCO.QTD_AFERICOES, 0),
       COALESCE(CALCULO_SULCO.MAX_DIAS_ENTRE_AFERICOES, '0'),
       COALESCE(CALCULO_SULCO.MIN_DIAS_ENTRE_AFERICOES, '0'),
       COALESCE(CALCULO_SULCO.MD_DIAS_ENTRE_AFERICOES, '0'),
       COALESCE(CALCULO_SULCO.QTD_AFERICOES_DENTRO_META, 0),
       COALESCE(CALCULO_SULCO.ADERENCIA, '0%')
FROM VEICULO V
         JOIN UNIDADE U ON V.COD_UNIDADE = U.CODIGO
         LEFT JOIN (SELECT CALCULO_AFERICAO_PRESSAO.PLACA,
                           COUNT(CALCULO_AFERICAO_PRESSAO.PLACA) AS QTD_AFERICOES,
                           CASE
                               WHEN
                                   MAX(CALCULO_AFERICAO_PRESSAO.DIAS_ENTRE_AFERICOES) IS NOT NULL
                                   THEN MAX(CALCULO_AFERICAO_PRESSAO.DIAS_ENTRE_AFERICOES)::TEXT
                               ELSE '-' END                      AS MAX_DIAS_ENTRE_AFERICOES,
                           CASE
                               WHEN
                                   MIN(CALCULO_AFERICAO_PRESSAO.DIAS_ENTRE_AFERICOES) IS NOT NULL
                                   THEN MIN(CALCULO_AFERICAO_PRESSAO.DIAS_ENTRE_AFERICOES)::TEXT
                               ELSE '-' END                      AS MIN_DIAS_ENTRE_AFERICOES,
                           CASE
                               WHEN
                                   MAX(CALCULO_AFERICAO_PRESSAO.DIAS_ENTRE_AFERICOES) IS NOT NULL
                                   THEN TRUNC(
                                       CASE
                                           WHEN SUM(CALCULO_AFERICAO_PRESSAO.DIAS_ENTRE_AFERICOES) IS NOT NULL
                                               THEN
                                                   SUM(CALCULO_AFERICAO_PRESSAO.DIAS_ENTRE_AFERICOES) /
                                                   SUM(CASE
                                                           WHEN CALCULO_AFERICAO_PRESSAO.DIAS_ENTRE_AFERICOES IS NOT NULL
                                                               THEN 1
                                                           ELSE 0 END)
                                           END)::TEXT
                               ELSE '-' END                      AS MD_DIAS_ENTRE_AFERICOES,
                           SUM(CASE
                                   WHEN CALCULO_AFERICAO_PRESSAO.DIAS_ENTRE_AFERICOES <=
                                        CALCULO_AFERICAO_PRESSAO.PERIODO_AFERICAO
                                       THEN 1
                                   ELSE 0 END)                   AS QTD_AFERICOES_DENTRO_META,
                           TRUNC(SUM(CASE
                                         WHEN CALCULO_AFERICAO_PRESSAO.DIAS_ENTRE_AFERICOES <=
                                              CALCULO_AFERICAO_PRESSAO.PERIODO_AFERICAO
                                             THEN 1
                                         ELSE 0 END) / COUNT(CALCULO_AFERICAO_PRESSAO.PLACA)::NUMERIC * 100) ||
                           '%'                                   AS ADERENCIA
                    FROM (SELECT V.PLACA                    AS PLACA,
                                 A.DATA_HORA                AS DATA_HORA_AFERICAO,
                                 A.TIPO_MEDICAO_COLETADA,
                                 R.PERIODO_AFERICAO_PRESSAO AS PERIODO_AFERICAO,
                                 CASE
                                     WHEN V.PLACA = LAG(V.PLACA) OVER (ORDER BY V.PLACA, DATA_HORA)
                                         THEN EXTRACT(DAYS FROM A.DATA_HORA -
                                                                LAG(A.DATA_HORA) OVER (ORDER BY V.PLACA, DATA_HORA))
                                     END                    AS DIAS_ENTRE_AFERICOES,
                                 A.COD_UNIDADE
                          FROM AFERICAO A
                                   JOIN VEICULO V ON V.CODIGO = A.COD_VEICULO
                                   JOIN PNEU_RESTRICAO_UNIDADE R ON R.COD_UNIDADE = V.COD_UNIDADE
                          WHERE V.COD_UNIDADE::TEXT LIKE ANY (F_COD_UNIDADE)
                            -- (PL-1900) Passamos a realizar uma subtração da data inicial pelo periodo em questão
                            -- para poder buscar a aferição anterior a primeira aferição filtrada para fazer o calculo
                            -- de se ela foi realizada dentro da meta.
                            AND (A.DATA_HORA AT TIME ZONE tz_unidade(A.COD_UNIDADE))::DATE >=
                                (F_DATA_INICIAL - R.PERIODO_AFERICAO_PRESSAO)::DATE
                            AND (A.DATA_HORA AT TIME ZONE tz_unidade(A.COD_UNIDADE))::DATE <= (F_DATA_FINAL)
                            AND (A.TIPO_MEDICAO_COLETADA = 'PRESSAO' OR A.TIPO_MEDICAO_COLETADA = 'SULCO_PRESSAO')
                            AND A.TIPO_PROCESSO_COLETA = 'PLACA'
                          ORDER BY 1, 2) AS CALCULO_AFERICAO_PRESSAO
                         -- (PL-1900) Aqui retiramos as aferições trazidas que eram de antes da data filtrada, pois eram
                         -- apenas para o calculo da meta da primeira aferição da faixa do filtro.
                    WHERE (CALCULO_AFERICAO_PRESSAO.DATA_HORA_AFERICAO AT TIME ZONE
                           tz_unidade(CALCULO_AFERICAO_PRESSAO.COD_UNIDADE))::DATE >= F_DATA_INICIAL::DATE
                      AND (CALCULO_AFERICAO_PRESSAO.DATA_HORA_AFERICAO AT TIME ZONE
                           tz_unidade(CALCULO_AFERICAO_PRESSAO.COD_UNIDADE))::DATE <= F_DATA_FINAL::DATE
                    GROUP BY CALCULO_AFERICAO_PRESSAO.PLACA) AS CALCULO_PRESSAO
                   ON CALCULO_PRESSAO.PLACA = V.PLACA
         LEFT JOIN (SELECT CALCULO_AFERICAO_SULCO.PLACA,
                           COUNT(CALCULO_AFERICAO_SULCO.PLACA) AS QTD_AFERICOES,
                           CASE
                               WHEN
                                   MAX(CALCULO_AFERICAO_SULCO.DIAS_ENTRE_AFERICOES) IS NOT NULL
                                   THEN MAX(CALCULO_AFERICAO_SULCO.DIAS_ENTRE_AFERICOES)::TEXT
                               ELSE '-' END                    AS MAX_DIAS_ENTRE_AFERICOES,
                           CASE
                               WHEN
                                   MIN(CALCULO_AFERICAO_SULCO.DIAS_ENTRE_AFERICOES) IS NOT NULL
                                   THEN MIN(CALCULO_AFERICAO_SULCO.DIAS_ENTRE_AFERICOES)::TEXT
                               ELSE '-' END                    AS MIN_DIAS_ENTRE_AFERICOES,
                           CASE
                               WHEN
                                   MAX(CALCULO_AFERICAO_SULCO.DIAS_ENTRE_AFERICOES) IS NOT NULL
                                   THEN TRUNC(
                                       CASE
                                           WHEN SUM(CALCULO_AFERICAO_SULCO.DIAS_ENTRE_AFERICOES) IS NOT NULL
                                               THEN
                                                   SUM(CALCULO_AFERICAO_SULCO.DIAS_ENTRE_AFERICOES) /
                                                   SUM(CASE
                                                           WHEN CALCULO_AFERICAO_SULCO.DIAS_ENTRE_AFERICOES IS NOT NULL
                                                               THEN 1
                                                           ELSE 0 END)
                                           END) :: TEXT
                               ELSE '-' END                    AS MD_DIAS_ENTRE_AFERICOES,
                           SUM(CASE
                                   WHEN CALCULO_AFERICAO_SULCO.DIAS_ENTRE_AFERICOES <=
                                        CALCULO_AFERICAO_SULCO.PERIODO_AFERICAO
                                       THEN 1
                                   ELSE 0 END)                 AS QTD_AFERICOES_DENTRO_META,
                           TRUNC(SUM(CASE
                                         WHEN CALCULO_AFERICAO_SULCO.DIAS_ENTRE_AFERICOES <=
                                              CALCULO_AFERICAO_SULCO.PERIODO_AFERICAO
                                             THEN 1
                                         ELSE 0 END) / COUNT(CALCULO_AFERICAO_SULCO.PLACA)::NUMERIC * 100) ||
                           '%'                                 AS ADERENCIA
                    FROM (SELECT V.PLACA                  AS PLACA,
                                 A.DATA_HORA              AS DATA_HORA_AFERICAO,
                                 A.TIPO_MEDICAO_COLETADA,
                                 R.PERIODO_AFERICAO_SULCO AS PERIODO_AFERICAO,
                                 CASE
                                     WHEN V.PLACA = LAG(V.PLACA) OVER (ORDER BY V.PLACA, DATA_HORA)
                                         THEN EXTRACT(DAYS FROM A.DATA_HORA -
                                                                LAG(A.DATA_HORA) OVER (ORDER BY V.PLACA, DATA_HORA))
                                     ELSE 0
                                     END                  AS DIAS_ENTRE_AFERICOES,
                                 A.COD_UNIDADE
                          FROM AFERICAO A
                                   JOIN VEICULO V ON V.CODIGO = A.COD_VEICULO
                                   JOIN PNEU_RESTRICAO_UNIDADE R ON R.COD_UNIDADE = V.COD_UNIDADE
                          WHERE V.COD_UNIDADE::TEXT LIKE ANY (F_COD_UNIDADE)
                            -- (PL-1900) Passamos a realizar uma subtração da data inicial pelo periodo em questão
                            -- para poder buscar a aferição anterior a primeira aferição filtrada para fazer o calculo
                            -- de se ela foi realizada dentro da meta.
                            AND (A.DATA_HORA AT TIME ZONE tz_unidade(A.COD_UNIDADE))::DATE >=
                                (F_DATA_INICIAL - R.PERIODO_AFERICAO_SULCO)::DATE
                            AND (A.DATA_HORA AT TIME ZONE tz_unidade(A.COD_UNIDADE))::DATE <= (F_DATA_FINAL)
                            AND (A.TIPO_MEDICAO_COLETADA = 'SULCO' OR A.TIPO_MEDICAO_COLETADA = 'SULCO_PRESSAO')
                            AND A.TIPO_PROCESSO_COLETA = 'PLACA'
                          ORDER BY 1, 2) AS CALCULO_AFERICAO_SULCO
                         -- (PL-1900) Aqui retiramos as aferições trazidas que eram de antes da data filtrada, pois eram
                         -- apenas para o calculo da meta da primeira aferição da faixa do filtro.
                    WHERE CAST(CALCULO_AFERICAO_SULCO.DATA_HORA_AFERICAO AT TIME ZONE
                               tz_unidade(CALCULO_AFERICAO_SULCO.COD_UNIDADE) AS DATE) >= F_DATA_INICIAL::DATE
                      AND CAST(CALCULO_AFERICAO_SULCO.DATA_HORA_AFERICAO AT TIME ZONE
                               tz_unidade(CALCULO_AFERICAO_SULCO.COD_UNIDADE) AS DATE) <= F_DATA_FINAL::DATE
                    GROUP BY CALCULO_AFERICAO_SULCO.PLACA) AS CALCULO_SULCO
                   ON CALCULO_SULCO.PLACA = V.PLACA
WHERE V.COD_UNIDADE::TEXT LIKE ANY (F_COD_UNIDADE)
  AND V.STATUS_ATIVO IS TRUE
ORDER BY U.NOME, V.PLACA;
$$;

CREATE OR REPLACE FUNCTION FUNC_PNEU_RELATORIO_FAROL_AFERICAO(F_COD_UNIDADES BIGINT[], F_DATA_INICIAL DATE, F_DATA_FINAL DATE)
    RETURNS TABLE
            (
                "UNIDADE"                             TEXT,
                "QTD DE FROTAS"                       TEXT,
                "QTD DE PNEUS"                        TEXT,
                "QTD DE PNEUS AFERIDOS - PRESSÃO"     TEXT,
                "PERCENTUAL PNEUS AFERIDOS - PRESSÃO" TEXT,
                "QTD DE PNEUS AFERIDOS - SULCO"       TEXT,
                "PERCENTUAL PNEUS AFERIDOS - SULCO"   TEXT
            )
    LANGUAGE SQL
AS
$$
WITH FAROL_AFERICAO AS (
    SELECT U.NOME                                                AS NOME_UNIDADE,
           COUNT(DISTINCT V.PLACA)                               AS QTD_VEICULOS,
           COUNT(DISTINCT VP.*)                                  AS QTD_PNEUS,
           COUNT(DISTINCT VP.COD_PNEU) FILTER (
               WHERE A.TIPO_MEDICAO_COLETADA = 'PRESSAO'
                   OR A.TIPO_MEDICAO_COLETADA = 'SULCO_PRESSAO') AS TOTAL_PRESSAO,
           COUNT(DISTINCT VP.COD_PNEU) FILTER (
               WHERE A.TIPO_MEDICAO_COLETADA = 'SULCO'
                   OR A.TIPO_MEDICAO_COLETADA = 'SULCO_PRESSAO') AS TOTAL_SULCO
    FROM VEICULO_DATA V
             JOIN UNIDADE U ON V.COD_UNIDADE = U.CODIGO
             JOIN VEICULO_PNEU VP ON V.CODIGO = VP.COD_VEICULO AND V.COD_UNIDADE = VP.COD_UNIDADE
             LEFT JOIN AFERICAO_VALORES AV ON AV.COD_PNEU = VP.COD_PNEU
             LEFT JOIN AFERICAO A ON V.CODIGO = A.COD_VEICULO AND A.CODIGO = AV.COD_AFERICAO
    WHERE V.COD_UNIDADE = ANY (F_COD_UNIDADES)
      AND (A.DATA_HORA AT TIME ZONE TZ_UNIDADE(A.COD_UNIDADE))::DATE >= (F_DATA_INICIAL)
      AND (A.DATA_HORA AT TIME ZONE TZ_UNIDADE(A.COD_UNIDADE))::DATE <= (F_DATA_FINAL)
    GROUP BY V.COD_UNIDADE, U.NOME
)
SELECT NOME_UNIDADE :: TEXT,
       QTD_VEICULOS :: TEXT,
       QTD_PNEUS :: TEXT,
       TOTAL_PRESSAO :: TEXT,
       COALESCE_PERCENTAGE(TOTAL_PRESSAO, QTD_PNEUS) :: TEXT AS PERCENTUAL_PRESSAO,
       TOTAL_SULCO :: TEXT,
       COALESCE_PERCENTAGE(TOTAL_SULCO, QTD_PNEUS) :: TEXT   AS PERCENTUAL_SULCO
FROM FAROL_AFERICAO
ORDER BY (TOTAL_PRESSAO :: REAL / NULLIF(QTD_PNEUS, 0) :: REAL) ASC NULLS LAST;
$$;

CREATE OR REPLACE FUNCTION FUNC_PNEU_RELATORIO_EXTRATO_SERVICOS_FECHADOS(F_COD_UNIDADES BIGINT[],
                                                                         F_DATA_INICIAL DATE,
                                                                         F_DATA_FINAL DATE)
    RETURNS TABLE
            (
                "UNIDADE DO SERVIÇO"               TEXT,
                "DATA AFERIÇÃO"                    TEXT,
                "DATA RESOLUÇÃO"                   TEXT,
                "HORAS PARA RESOLVER"              DOUBLE PRECISION,
                "MINUTOS PARA RESOLVER"            DOUBLE PRECISION,
                "PLACA"                            TEXT,
                "IDENTIFICADOR FROTA"              TEXT,
                "KM AFERIÇÃO"                      BIGINT,
                "KM CONSERTO"                      BIGINT,
                "KM PERCORRIDO"                    BIGINT,
                "COD PNEU"                         CHARACTER VARYING,
                "PRESSÃO RECOMENDADA"              REAL,
                "PRESSÃO AFERIÇÃO"                 TEXT,
                "DISPERSÃO RECOMENDADA X AFERIÇÃO" TEXT,
                "PRESSÃO INSERIDA"                 TEXT,
                "DISPERSÃO RECOMENDADA X INSERIDA" TEXT,
                "POSIÇÃO PNEU ABERTURA SERVIÇO"    TEXT,
                "SERVIÇO"                          TEXT,
                "MECÂNICO"                         TEXT,
                "PROBLEMA APONTADO (INSPEÇÃO)"     TEXT,
                "FECHADO AUTOMATICAMENTE"          TEXT,
                "FORMA DE COLETA DOS DADOS"        TEXT
            )
    LANGUAGE SQL
AS
$$
SELECT U.NOME                                                                       AS UNIDADE_SERVICO,
       TO_CHAR((A.DATA_HORA AT TIME ZONE TZ_UNIDADE(AM.COD_UNIDADE)),
               'DD/MM/YYYY HH24:MI:SS')                                             AS DATA_HORA_AFERICAO,
       TO_CHAR((AM.DATA_HORA_RESOLUCAO AT TIME ZONE TZ_UNIDADE(AM.COD_UNIDADE)),
               'DD/MM/YYYY HH24:MI:SS')                                             AS DATA_HORA_RESOLUCAO,
       TRUNC(EXTRACT(EPOCH FROM ((AM.DATA_HORA_RESOLUCAO) - (A.DATA_HORA))) /
             3600)                                                                  AS HORAS_RESOLUCAO,
       TRUNC(
               EXTRACT(EPOCH FROM ((AM.DATA_HORA_RESOLUCAO) - (A.DATA_HORA))) / 60) AS MINUTOS_RESOLUCAO,
       V.PLACA                                                                      AS PLACA_VEICULO,
       COALESCE(V.IDENTIFICADOR_FROTA, '-')                                         AS IDENTIFICADOR_FROTA,
       A.KM_VEICULO                                                                 AS KM_AFERICAO,
       AM.KM_MOMENTO_CONSERTO                                                       AS KM_MOMENTO_CONSERTO,
       AM.KM_MOMENTO_CONSERTO - A.KM_VEICULO                                        AS KM_PERCORRIDO,
       P.CODIGO_CLIENTE                                                             AS CODIGO_CLIENTE_PNEU,
       P.PRESSAO_RECOMENDADA                                                        AS PRESSAO_RECOMENDADA_PNEU,
       COALESCE(REPLACE(ROUND(AV.PSI :: NUMERIC, 2) :: TEXT, '.', ','),
                '-')                                                                AS PSI_AFERICAO,
       COALESCE(REPLACE(ROUND((((AV.PSI / P.PRESSAO_RECOMENDADA) - 1) * 100) :: NUMERIC, 2) || '%', '.', ','),
                '-')                                                                AS DISPERSAO_PRESSAO_ANTES,
       COALESCE(REPLACE(ROUND(AM.PSI_APOS_CONSERTO :: NUMERIC, 2) :: TEXT, '.', ','),
                '-')                                                                AS PSI_POS_CONSERTO,
       COALESCE(REPLACE(ROUND((((AM.PSI_APOS_CONSERTO / P.PRESSAO_RECOMENDADA) - 1) * 100) :: NUMERIC, 2) || '%', '.',
                        ','),
                '-')                                                                AS DISPERSAO_PRESSAO_DEPOIS,
       COALESCE(PPNE.NOMENCLATURA, '-')                                             AS POSICAO,
       AM.TIPO_SERVICO                                                              AS TIPO_SERVICO,
       COALESCE(INITCAP(C.NOME), '-')                                               AS NOME_MECANICO,
       COALESCE(AA.ALTERNATIVA, '-')                                                AS PROBLEMA_APONTADO,
       F_IF(AM.FECHADO_AUTOMATICAMENTE_MOVIMENTACAO OR AM.FECHADO_AUTOMATICAMENTE_INTEGRACAO, 'Sim' :: TEXT,
            'Não')                                                                  AS TIPO_FECHAMENTO,
       COALESCE(AFCD.STATUS_LEGIVEL, '-')                                           AS FORMA_COLETA_DADOS_FECHAMENTO
FROM AFERICAO_MANUTENCAO AM
         JOIN UNIDADE U
              ON AM.COD_UNIDADE = U.CODIGO
         JOIN AFERICAO_VALORES AV
              ON AM.COD_UNIDADE = AV.COD_UNIDADE
                  AND AM.COD_AFERICAO = AV.COD_AFERICAO
                  AND AM.COD_PNEU = AV.COD_PNEU
         JOIN AFERICAO A
              ON A.CODIGO = AV.COD_AFERICAO
         LEFT JOIN COLABORADOR C
                   ON AM.CPF_MECANICO = C.CPF
         JOIN PNEU P
              ON P.CODIGO = AV.COD_PNEU
         LEFT JOIN VEICULO_PNEU VP
                   ON VP.COD_PNEU = P.CODIGO
                       AND VP.COD_UNIDADE = P.COD_UNIDADE
         LEFT JOIN AFERICAO_ALTERNATIVA_MANUTENCAO_INSPECAO AA
                   ON AA.CODIGO = AM.COD_ALTERNATIVA
         LEFT JOIN VEICULO V
                   ON V.CODIGO = VP.COD_VEICULO
         LEFT JOIN EMPRESA E
                   ON U.COD_EMPRESA = E.CODIGO
         LEFT JOIN VEICULO_TIPO VT
                   ON E.CODIGO = VT.COD_EMPRESA
                       AND VT.CODIGO = V.COD_TIPO
         LEFT JOIN PNEU_POSICAO_NOMENCLATURA_EMPRESA PPNE
                   ON PPNE.COD_EMPRESA = P.COD_EMPRESA
                       AND PPNE.COD_DIAGRAMA = VT.COD_DIAGRAMA
                       AND PPNE.POSICAO_PROLOG = AV.POSICAO
         LEFT JOIN TYPES.AFERICAO_FORMA_COLETA_DADOS AFCD
                   ON AFCD.FORMA_COLETA_DADOS = AM.FORMA_COLETA_DADOS_FECHAMENTO
WHERE AV.COD_UNIDADE = ANY (F_COD_UNIDADES)
  AND AM.DATA_HORA_RESOLUCAO IS NOT NULL
  AND (AM.DATA_HORA_RESOLUCAO AT TIME ZONE TZ_UNIDADE(AM.COD_UNIDADE)) :: DATE >= F_DATA_INICIAL
  AND (AM.DATA_HORA_RESOLUCAO AT TIME ZONE TZ_UNIDADE(AM.COD_UNIDADE)) :: DATE <= F_DATA_FINAL
ORDER BY U.NOME, A.DATA_HORA DESC
$$;

CREATE OR REPLACE FUNCTION FUNC_PNEU_RELATORIO_EXTRATO_SERVICOS_ABERTOS(F_COD_UNIDADES BIGINT[],
                                                                        F_DATA_INICIAL DATE,
                                                                        F_DATA_FINAL DATE,
                                                                        F_DATA_ATUAL DATE)
    RETURNS TABLE
            (
                "UNIDADE DO SERVIÇO"            TEXT,
                "CÓDIGO DO SERVIÇO"             TEXT,
                "TIPO DO SERVIÇO"               TEXT,
                "QTD APONTAMENTOS"              TEXT,
                "DATA HORA ABERTURA"            TEXT,
                "QTD DIAS EM ABERTO"            TEXT,
                "NOME DO COLABORADOR"           TEXT,
                "PLACA"                         TEXT,
                "IDENTIFICADOR FROTA"           TEXT,
                "PNEU"                          TEXT,
                "POSIÇÃO PNEU ABERTURA SERVIÇO" TEXT,
                "MEDIDAS"                       TEXT,
                "COD AFERIÇÃO"                  TEXT,
                "SULCO INTERNO"                 TEXT,
                "SULCO CENTRAL INTERNO"         TEXT,
                "SULCO CENTRAL EXTERNO"         TEXT,
                "SULCO EXTERNO"                 TEXT,
                "MENOR SULCO"                   TEXT,
                "PRESSÃO (PSI)"                 TEXT,
                "PRESSÃO RECOMENDADA (PSI)"     TEXT,
                "ESTADO ATUAL"                  TEXT,
                "MÁXIMO DE RECAPAGENS"          TEXT
            )
    LANGUAGE SQL
AS
$$
SELECT U.NOME                                                                                   AS UNIDADE_SERVICO,
       AM.CODIGO :: TEXT                                                                        AS CODIGO_SERVICO,
       AM.TIPO_SERVICO                                                                          AS TIPO_SERVICO,
       AM.QT_APONTAMENTOS :: TEXT                                                               AS QT_APONTAMENTOS,
       TO_CHAR((A.DATA_HORA AT TIME ZONE TZ_UNIDADE(AM.COD_UNIDADE)),
               'DD/MM/YYYY HH24:MI') :: TEXT                                                    AS DATA_HORA_ABERTURA,
       (F_DATA_ATUAL - ((A.DATA_HORA AT TIME ZONE TZ_UNIDADE(AM.COD_UNIDADE)) :: DATE)) :: TEXT AS DIAS_EM_ABERTO,
       C.NOME                                                                                   AS NOME_COLABORADOR,
       V.PLACA                                                                                  AS PLACA_VEICULO,
       COALESCE(V.IDENTIFICADOR_FROTA, '-')                                                     AS IDENTIFICADOR_FROTA,
       P.CODIGO_CLIENTE                                                                         AS COD_PNEU_PROBLEMA,
       COALESCE(PPNE.NOMENCLATURA :: TEXT, '-')                                                 AS POSICAO_PNEU_PROBLEMA,
       DP.LARGURA || '/' :: TEXT || DP.ALTURA || ' R' :: TEXT || DP.ARO                         AS MEDIDAS,
       A.CODIGO :: TEXT                                                                         AS COD_AFERICAO,
       FUNC_PNEU_FORMAT_SULCO(AV.ALTURA_SULCO_INTERNO)                                          AS SULCO_INTERNO,
       FUNC_PNEU_FORMAT_SULCO(AV.ALTURA_SULCO_CENTRAL_INTERNO)                                  AS SULCO_CENTRAL_INTERNO,
       FUNC_PNEU_FORMAT_SULCO(AV.ALTURA_SULCO_CENTRAL_EXTERNO)                                  AS SULCO_CENTRAL_EXTERNO,
       FUNC_PNEU_FORMAT_SULCO(AV.ALTURA_SULCO_EXTERNO)                                          AS SULCO_EXTERNO,
       FUNC_PNEU_FORMAT_SULCO(LEAST(AV.ALTURA_SULCO_EXTERNO, AV.ALTURA_SULCO_CENTRAL_EXTERNO,
                                    AV.ALTURA_SULCO_CENTRAL_INTERNO,
                                    AV.ALTURA_SULCO_INTERNO))                                   AS MENOR_SULCO,
       REPLACE(COALESCE(TRUNC(AV.PSI) :: TEXT, '-'), '.', ',')                                  AS PRESSAO_PNEU_PROBLEMA,
       REPLACE(COALESCE(TRUNC(P.PRESSAO_RECOMENDADA) :: TEXT, '-'), '.',
               ',')                                                                             AS PRESSAO_RECOMENDADA,
       PVN.NOME                                                                                 AS VIDA_PNEU_PROBLEMA,
       PRN.NOME                                                                                 AS TOTAL_RECAPAGENS
FROM AFERICAO_MANUTENCAO AM
         JOIN PNEU P
              ON AM.COD_PNEU = P.CODIGO
         JOIN DIMENSAO_PNEU DP
              ON DP.CODIGO = P.COD_DIMENSAO
         JOIN AFERICAO A
              ON A.CODIGO = AM.COD_AFERICAO
         JOIN COLABORADOR C
              ON A.CPF_AFERIDOR = C.CPF
         JOIN AFERICAO_VALORES AV
              ON AV.COD_AFERICAO = AM.COD_AFERICAO
                  AND AV.COD_PNEU = AM.COD_PNEU
         JOIN UNIDADE U
              ON U.CODIGO = AM.COD_UNIDADE
         JOIN EMPRESA E
              ON U.COD_EMPRESA = E.CODIGO
         JOIN PNEU_VIDA_NOMENCLATURA PVN
              ON PVN.COD_VIDA = AV.VIDA_MOMENTO_AFERICAO
         JOIN PNEU_RECAPAGEM_NOMENCLATURA PRN
              ON PRN.COD_TOTAL_VIDA = P.VIDA_TOTAL
         JOIN VEICULO V
              ON A.COD_VEICULO = V.CODIGO
                  AND V.COD_UNIDADE = A.COD_UNIDADE
         LEFT JOIN VEICULO_TIPO VT
                   ON V.COD_TIPO = VT.CODIGO
         LEFT JOIN VEICULO_DIAGRAMA VD ON VT.COD_DIAGRAMA = VD.CODIGO
         LEFT JOIN PNEU_POSICAO_NOMENCLATURA_EMPRESA PPNE ON PPNE.COD_EMPRESA = E.CODIGO
    AND PPNE.COD_DIAGRAMA = VD.CODIGO
    AND AV.POSICAO = PPNE.POSICAO_PROLOG
WHERE AM.COD_UNIDADE = ANY (F_COD_UNIDADES)
  AND (A.DATA_HORA AT TIME ZONE TZ_UNIDADE(AM.COD_UNIDADE)) :: DATE >= F_DATA_INICIAL
  AND (A.DATA_HORA AT TIME ZONE TZ_UNIDADE(AM.COD_UNIDADE)) :: DATE <= F_DATA_FINAL
  AND AM.DATA_HORA_RESOLUCAO IS NULL
  AND (AM.FECHADO_AUTOMATICAMENTE_MOVIMENTACAO = FALSE OR AM.FECHADO_AUTOMATICAMENTE_MOVIMENTACAO IS NULL)
  AND (AM.FECHADO_AUTOMATICAMENTE_INTEGRACAO = FALSE OR AM.FECHADO_AUTOMATICAMENTE_INTEGRACAO IS NULL)
ORDER BY U.NOME, A.DATA_HORA;
$$;

--Cria function com outros parametros
create or replace function func_garante_novo_km_menor_que_atual_veiculo(f_cod_veiculo bigint,
                                                                        f_novo_km bigint)
    returns void
    language plpgsql
as
$$
declare
    f_km_atual_veiculo constant bigint := (select v.km
                                           from veiculo v
                                           where v.codigo = f_cod_veiculo);
begin
    if (f_km_atual_veiculo is not null and f_novo_km > f_km_atual_veiculo)
    then
        raise exception 'O Km enviado não pode ser maior que o Km atual do veículo : Km enviado %, Km atual %',
            f_novo_km,
            f_km_atual_veiculo;
    end if;
end;
$$;

-- remove placa da func
drop function if exists suporte.func_afericao_altera_km_coletado_afericao(f_placa text,
    f_cod_afericao bigint,
    f_novo_km bigint);
create or replace function suporte.func_afericao_altera_km_coletado_afericao(f_placa text,
                                                                             f_cod_afericao bigint,
                                                                             f_novo_km bigint,
                                                                             out aviso_km_afericao_alterado text)
    returns text
    language plpgsql
    security definer
as
$$
declare
    v_qtd_linhas_atualizadas bigint;
    -- Não usa NOT NULL para não quebrar aqui com um erro não significativo para quem usar a function.
    v_cod_veiculo            bigint;
    v_km_atual               bigint;
begin
    select a.cod_veiculo, vd.km
    from afericao a
             join veiculo_data vd
                  on vd.placa = f_placa and a.cod_veiculo = vd.codigo
    where a.codigo = f_cod_afericao
    into v_cod_veiculo, v_km_atual;

    perform suporte.func_historico_salva_execucao();

    if (v_cod_veiculo is null)
    then
        raise exception 'Não foi possível encontrar a aferição realizada com estes parâmetros: Placa %,
                     Código da aferição %', f_placa, f_cod_afericao;
    end if;

    perform func_garante_novo_km_menor_que_atual_veiculo(v_cod_veiculo, f_novo_km);

    update afericao
    set km_veiculo = f_novo_km
    where codigo = f_cod_afericao
      and cod_veiculo = v_cod_veiculo;

    get diagnostics v_qtd_linhas_atualizadas = row_count;

    if (v_qtd_linhas_atualizadas <= 0)
    then
        raise exception 'Erro ao atualizar o km da aferição com estes parâemtros: Placa %, Código
            da aferição %', f_placa, f_cod_afericao;
    end if;

    select 'O KM DO VEÍCULO NA AFERIÇÃO FOI ALTERADO COM SUCESSO '
               || ', PLACA: '
               || f_placa
               || ', CÓDIGO DA AFERIÇÃO: '
               || f_cod_afericao
    into aviso_km_afericao_alterado;
end;
$$;

alter table integracao.afericao_integrada
    add column if not exists cod_veiculo bigint;

drop function integracao.func_pneu_afericao_insert_afericao_integrada(f_cod_unidade_prolog bigint,
    f_cod_auxiliar_unidade text,
    f_cpf_aferidor text,
    f_placa_veiculo text,
    f_cod_auxiliar_tipo_veiculo_prolog text,
    f_km_veiculo text,
    f_tempo_realizacao bigint,
    f_data_hora timestamp with time zone,
    f_tipo_medicao_coletada text,
    f_tipo_processo_coleta text);

create or replace function
    integracao.func_pneu_afericao_insert_afericao_integrada(f_cod_unidade_prolog bigint,
                                                            f_cod_auxiliar_unidade text,
                                                            f_cpf_aferidor text,
                                                            f_cod_veiculo bigint,
                                                            f_placa_veiculo text,
                                                            f_cod_auxiliar_tipo_veiculo_prolog text,
                                                            f_km_veiculo text,
                                                            f_tempo_realizacao bigint,
                                                            f_data_hora timestamp with time zone,
                                                            f_tipo_medicao_coletada text,
                                                            f_tipo_processo_coleta text)
    returns bigint
    language plpgsql
as
$$
declare
    v_cod_empresa_prolog              bigint;
    v_cod_empresa_cliente             text;
    v_cod_tipo_veiculo_prolog         bigint;
    v_cod_diagrama_veiculo_prolog     bigint;
    v_cod_afericao_integrada_inserida bigint;
begin
    -- Busca os dados de empresa e unidade para integração
    select e.codigo,
           e.cod_auxiliar
    from unidade u
             join empresa e on u.cod_empresa = e.codigo
    where u.codigo = f_cod_unidade_prolog
    into v_cod_empresa_prolog, v_cod_empresa_cliente;

    -- Busca os dados de tipo de veículo e diagrama para enriquecer o registro de aferição integrada.
    select vt.cod_tipo_veiculo,
           vt.cod_diagrama
    from (select vt.codigo                                   as cod_tipo_veiculo,
                 vt.cod_diagrama                             as cod_diagrama,
                 regexp_split_to_table(vt.cod_auxiliar, ',') as cod_auxiliar
          from veiculo_tipo vt
          where vt.cod_empresa = v_cod_empresa_prolog) as vt
    where vt.cod_auxiliar = f_cod_auxiliar_tipo_veiculo_prolog
    into v_cod_tipo_veiculo_prolog, v_cod_diagrama_veiculo_prolog;

    -- Realiza a inserção do registro de aferição integrada.
    insert into integracao.afericao_integrada(cod_empresa_prolog,
                                              cod_empresa_cliente,
                                              cod_unidade_prolog,
                                              cod_unidade_cliente,
                                              cpf_aferidor,
                                              cod_veiculo,
                                              placa_veiculo,
                                              cod_tipo_veiculo_prolog,
                                              cod_tipo_veiculo_cliente,
                                              cod_diagrama_prolog,
                                              km_veiculo,
                                              tempo_realizacao,
                                              data_hora,
                                              tipo_medicao_coletada,
                                              tipo_processo_coleta)
    values (v_cod_empresa_prolog,
            v_cod_empresa_cliente,
            f_cod_unidade_prolog,
            f_cod_auxiliar_unidade,
            f_cpf_aferidor,
            f_cod_veiculo,
            f_placa_veiculo,
            v_cod_tipo_veiculo_prolog,
            f_cod_auxiliar_tipo_veiculo_prolog,
            v_cod_diagrama_veiculo_prolog,
            f_km_veiculo,
            f_tempo_realizacao,
            f_data_hora,
            f_tipo_medicao_coletada,
            f_tipo_processo_coleta)
    returning codigo into v_cod_afericao_integrada_inserida;

    if (v_cod_afericao_integrada_inserida is null or v_cod_afericao_integrada_inserida <= 0)
    then
        raise exception 'Não foi possível inserir a aferição nas tabelas de integração, tente novamente';
    end if;

    return v_cod_afericao_integrada_inserida;
end
$$;


drop view if exists view_analise_pneus;
drop view if exists view_pneu_analise_vida_atual;
drop view if exists view_pneu_analise_vidas;
drop view if exists view_pneu_km_rodado_total;
drop view if exists view_pneu_km_rodado_vida;
drop view if exists afericao;

create or replace view afericao as
select ad.codigo,
       ad.data_hora,
       ad.cod_veiculo,
       ad.cpf_aferidor,
       ad.km_veiculo,
       ad.tempo_realizacao,
       ad.tipo_medicao_coletada,
       ad.cod_unidade,
       ad.tipo_processo_coleta,
       ad.forma_coleta_dados,
       ad.cod_diagrama
from afericao_data ad
where ad.deletado = false;

-- Corrige Inconsistência: Veículos deletados com aferições não deletadas.
-- Deleta serviço de aferição
update afericao_manutencao_data
set deletado            = true,
    data_hora_deletado  = now(),
    pg_username_delecao = session_user,
    motivo_delecao      = 'Inconsistência - PL-3438'
where deletado = false
  and cod_afericao in (select a.codigo
                       from afericao a
                       where a.cod_veiculo not in (select v.codigo from veiculo v));

-- Deleta valores de aferição.
update afericao_valores_data
set deletado            = true,
    data_hora_deletado  = now(),
    pg_username_delecao = session_user,
    motivo_delecao      = 'Inconsistência - PL-3438'
where deletado = false
  and cod_afericao in (select a.codigo
                       from afericao a
                       where a.cod_veiculo not in (select v.codigo from veiculo v));

-- Deleta aferição
update afericao_data
set deletado            = true,
    data_hora_deletado  = now(),
    pg_username_delecao = session_user,
    motivo_delecao      = 'Inconsistência - PL-3438'
where deletado = false
  and codigo in (select a.codigo
                 from afericao a
                 where a.cod_veiculo not in (select v.codigo from veiculo v));

drop function func_afericao_get_pneus_disponiveis_afericao_avulsa(f_cod_unidade bigint);
create or replace function func_afericao_get_pneus_disponiveis_afericao_avulsa(f_cod_unidade bigint)
    returns table
            (
                codigo                                bigint,
                codigo_cliente                        text,
                dot                                   text,
                valor                                 real,
                cod_unidade_alocado                   bigint,
                nome_unidade_alocado                  text,
                cod_regional_alocado                  bigint,
                nome_regional_alocado                 text,
                pneu_novo_nunca_rodado                boolean,
                cod_marca_pneu                        bigint,
                nome_marca_pneu                       text,
                cod_modelo_pneu                       bigint,
                nome_modelo_pneu                      text,
                qt_sulcos_modelo_pneu                 smallint,
                cod_marca_banda                       bigint,
                nome_marca_banda                      text,
                altura_sulcos_modelo_pneu             real,
                cod_modelo_banda                      bigint,
                nome_modelo_banda                     text,
                qt_sulcos_modelo_banda                smallint,
                altura_sulcos_modelo_banda            real,
                valor_banda                           real,
                altura                                integer,
                largura                               integer,
                aro                                   real,
                cod_dimensao                          bigint,
                altura_sulco_central_interno          real,
                altura_sulco_central_externo          real,
                altura_sulco_interno                  real,
                altura_sulco_externo                  real,
                pressao_recomendada                   real,
                pressao_atual                         real,
                status                                text,
                vida_atual                            integer,
                vida_total                            integer,
                posicao_pneu                          integer,
                posicao_aplicado_cliente              text,
                cod_veiculo_aplicado                  bigint,
                placa_aplicado                        text,
                identificador_frota                   text,
                ja_foi_aferido                        boolean,
                cod_ultima_afericao                   bigint,
                data_hora_ultima_afericao             timestamp without time zone,
                placa_veiculo_ultima_afericao         text,
                identificador_frota_ultima_afericao   text,
                tipo_medicao_coletada_ultima_afericao text,
                tipo_processo_coleta_ultima_afericao  text,
                nome_colaborador_ultima_afericao      text
            )
    language sql
as
$$
with afericoes as (
    select inner_table.codigo           as cod_afericao,
           inner_table.cod_pneu         as cod_pneu,
           inner_table.data_hora,
           inner_table.cod_veiculo,
           inner_table.tipo_medicao_coletada,
           inner_table.tipo_processo_coleta,
           inner_table.nome_colaborador as nome_colaborador,
           case
               when inner_table.nome_colaborador is not null
                   then true
               else false end           as ja_foi_aferido
    from (select a.codigo,
                 av.cod_pneu,
                 a.data_hora,
                 a.cod_veiculo,
                 a.tipo_medicao_coletada,
                 a.tipo_processo_coleta,
                 c.nome                      as nome_colaborador,
                 MAX(a.codigo)
                 over (
                     partition by cod_pneu ) as max_cod_afericao
          from pneu p
                   left join afericao_valores av on p.codigo = av.cod_pneu
                   left join afericao a on av.cod_afericao = a.codigo
                   left join colaborador c on a.cpf_aferidor = c.cpf
          where p.cod_unidade = f_cod_unidade
            and p.status = 'ESTOQUE') as inner_table
    where codigo = inner_table.max_cod_afericao
)
select f.*,
       a.ja_foi_aferido                as ja_foi_aferido,
       a.cod_afericao                  as cod_ultima_afericao,
       a.data_hora at time zone tz_unidade(f_cod_unidade)
                                       as data_hora_ultima_afericao,
       v.placa :: text                 as placa_veiculo_ultima_afericao,
       v.identificador_frota :: text   as identificador_frota_ultima_afericao,
       a.tipo_medicao_coletada :: text as tipo_medicao_coletada_ultima_afericao,
       a.tipo_processo_coleta :: text  as tipo_processo_coleta_ultima_afericao,
       a.nome_colaborador :: text      as nome_colaborador_ultima_afericao
from func_pneu_get_listagem_pneus_by_status(array [f_cod_unidade], 'ESTOQUE') as f
         left join afericoes a on f.codigo = a.cod_pneu
         left join veiculo v on a.cod_veiculo = v.codigo;
$$;

drop function func_afericao_get_pneu_para_afericao_avulsa(f_cod_pneu bigint, f_tz_unidade text);
create or replace function func_afericao_get_pneu_para_afericao_avulsa(f_cod_pneu bigint, f_tz_unidade text)
    returns table
            (
                codigo                                bigint,
                codigo_cliente                        text,
                dot                                   text,
                valor                                 real,
                cod_unidade_alocado                   bigint,
                cod_regional_alocado                  bigint,
                pneu_novo_nunca_rodado                boolean,
                cod_marca_pneu                        bigint,
                nome_marca_pneu                       text,
                cod_modelo_pneu                       bigint,
                nome_modelo_pneu                      text,
                qt_sulcos_modelo_pneu                 smallint,
                cod_marca_banda                       bigint,
                nome_marca_banda                      text,
                altura_sulcos_modelo_pneu             real,
                cod_modelo_banda                      bigint,
                nome_modelo_banda                     text,
                qt_sulcos_modelo_banda                smallint,
                altura_sulcos_modelo_banda            real,
                valor_banda                           real,
                altura                                integer,
                largura                               integer,
                aro                                   real,
                cod_dimensao                          bigint,
                altura_sulco_central_interno          real,
                altura_sulco_central_externo          real,
                altura_sulco_interno                  real,
                altura_sulco_externo                  real,
                pressao_recomendada                   real,
                pressao_atual                         real,
                status                                text,
                vida_atual                            integer,
                vida_total                            integer,
                posicao_pneu                          integer,
                posicao_aplicado_cliente              text,
                cod_veiculo_aplicado                  bigint,
                placa_aplicado                        text,
                identificador_frota                   text,
                ja_foi_aferido                        boolean,
                cod_ultima_afericao                   bigint,
                data_hora_ultima_afericao             timestamp without time zone,
                placa_veiculo_ultima_afericao         text,
                identificador_frota_ultima_afericao   text,
                tipo_medicao_coletada_ultima_afericao text,
                tipo_processo_coleta_ultima_afericao  text,
                nome_colaborador_ultima_afericao      text
            )
    language sql
as
$$
with afericoes as (
    select inner_table.codigo           as cod_afericao,
           inner_table.cod_pneu         as cod_pneu,
           inner_table.data_hora,
           inner_table.cod_veiculo,
           inner_table.tipo_medicao_coletada,
           inner_table.tipo_processo_coleta,
           inner_table.nome_colaborador as nome_colaborador,
           case
               when inner_table.nome_colaborador is not null
                   then true
               else false end           as ja_foi_aferido
    from (select a.codigo,
                 av.cod_pneu,
                 a.data_hora,
                 a.cod_veiculo,
                 a.tipo_medicao_coletada,
                 a.tipo_processo_coleta,
                 c.nome                      as nome_colaborador,
                 MAX(a.codigo)
                 over (
                     partition by cod_pneu ) as max_cod_afericao
          from pneu p
                   left join afericao_valores av on p.codigo = av.cod_pneu
                   left join afericao a on av.cod_afericao = a.codigo
                   left join colaborador c on a.cpf_aferidor = c.cpf
          where p.status = 'ESTOQUE'
            and p.codigo = f_cod_pneu) as inner_table
    where codigo = inner_table.max_cod_afericao
)

select func.*,
       a.ja_foi_aferido                      as ja_foi_aferido,
       a.cod_afericao                        as cod_ultima_afericao,
       a.data_hora at time zone f_tz_unidade as data_hora_ultima_afericao,
       v.placa :: text                       as placa_veiculo_ultima_afericao,
       v.identificador_frota                 as identificador_frota_ultima_afericao,
       a.tipo_medicao_coletada :: text       as tipo_medicao_coletada_ultima_afericao,
       a.tipo_processo_coleta :: text        as tipo_processo_coleta_ultima_afericao,
       a.nome_colaborador :: text            as nome_colaborador_ultima_afericao
from func_pneu_get_pneu_by_codigo(f_cod_pneu) as func
         left join afericoes a on func.codigo = a.cod_pneu
         left join veiculo v on v.codigo = a.cod_veiculo
where func.codigo = f_cod_pneu;
$$;

drop function func_pneu_calcula_km_aplicacao_remocao_pneu(f_cod_pneu bigint, f_vida_pneu integer);
create or replace function func_pneu_calcula_km_aplicacao_remocao_pneu(f_cod_pneu bigint,
                                                                       f_vida_pneu integer)
    returns numeric
    language sql
as
$$
with movimentacoes_vida_pneu as (
    select mp.data_hora    as data_hora_movimentacao,
           mo.tipo_origem  as tipo_origem,
           md.tipo_destino as tipo_destino,
           v_destino.placa as placa_destino,
           md.km_veiculo   as km_veiculo_destino,
           v_origem.placa  as placa_origem,
           mo.km_veiculo   as km_veiculo_origem
    from movimentacao_processo mp
             join movimentacao m on mp.codigo = m.cod_movimentacao_processo
             join movimentacao_origem mo on m.codigo = mo.cod_movimentacao
             left join veiculo v_origem on v_origem.codigo = mo.cod_veiculo
             join movimentacao_destino md on m.codigo = md.cod_movimentacao
             left join veiculo v_destino on v_destino.codigo = md.cod_veiculo
    where (mo.tipo_origem = 'EM_USO' or md.tipo_destino = 'EM_USO')
      and m.cod_pneu = f_cod_pneu
      and m.vida = f_vida_pneu
),

     afericoes_vida_pneu as (
         select a.data_hora  as data_hora_afericao,
                v.placa      as placa_afericao,
                a.km_veiculo as km_veiculo_afericao
         from afericao a
                  join afericao_valores av on av.cod_afericao = a.codigo
                  join veiculo v on a.cod_veiculo = v.codigo
         where a.tipo_processo_coleta = 'PLACA'
           and av.cod_pneu = f_cod_pneu
           and av.vida_momento_afericao = f_vida_pneu
     ),

     kms_primeira_aplicacao_ate_primeira_afericao as (
         select sum((select avp.km_veiculo_afericao
                     from afericoes_vida_pneu avp
                     where avp.placa_afericao = pvp.placa_destino
                       and avp.data_hora_afericao > pvp.data_hora_movimentacao
                     order by avp.data_hora_afericao
                     limit 1) - pvp.km_veiculo_destino) as km_percorrido
         from movimentacoes_vida_pneu pvp
              -- Saiu de qualquer origem e foi aplicado no veículo.
         where pvp.tipo_origem <> 'EM_USO'
           and pvp.tipo_destino = 'EM_USO'
     ),

     kms_ultima_afericao_ate_remocao as (
         select sum(pvp.km_veiculo_origem - (select avp.km_veiculo_afericao
                                             from afericoes_vida_pneu avp
                                             where avp.placa_afericao = pvp.placa_origem
                                               and avp.data_hora_afericao < pvp.data_hora_movimentacao
                                             order by avp.data_hora_afericao desc
                                             limit 1)) as km_percorrido
         from movimentacoes_vida_pneu pvp
              -- Saiu do veículo e foi movido para qualquer outro destino que não veículo.
         where pvp.tipo_origem = 'EM_USO'
           and pvp.tipo_destino <> 'EM_USO'
     )

select coalesce((select aplicacao.km_percorrido
                 from kms_primeira_aplicacao_ate_primeira_afericao aplicacao), 0)
           +
       coalesce((select remocao.km_percorrido
                 from kms_ultima_afericao_ate_remocao remocao), 0) as km_total_aplicacao_remocao;
$$;

drop function func_veiculo_transferencia_deleta_servicos_pneu(f_cod_veiculo bigint,
    f_cod_pneu bigint,
    f_cod_transferencia_veiculo_informacoes bigint,
    f_data_hora_realizacao_transferencia timestamp with time zone);
create or replace function func_veiculo_transferencia_deleta_servicos_pneu(f_cod_veiculo bigint,
                                                                           f_cod_pneu bigint,
                                                                           f_cod_transferencia_veiculo_informacoes bigint,
                                                                           f_data_hora_realizacao_transferencia timestamp with time zone)
    returns void
    language plpgsql
as
$$
declare
    v_qtd_inserts bigint;
    v_qtd_updates bigint;
begin
    insert into afericao_manutencao_servico_deletado_transferencia (cod_servico,
                                                                    cod_veiculo_transferencia_informacoes)
    select am.codigo,
           f_cod_transferencia_veiculo_informacoes
           -- Utilizamos propositalmente a view e não a tabela _DATA, para não copiar serviços deletados e não fechados.
    from afericao_manutencao am
             join afericao a on a.codigo = am.cod_afericao
    where a.cod_veiculo = f_cod_veiculo
      and am.cod_pneu = f_cod_pneu
      and am.data_hora_resolucao is null
      and (am.fechado_automaticamente_movimentacao = false or am.fechado_automaticamente_movimentacao is null);

    get diagnostics v_qtd_inserts = row_count;

    update afericao_manutencao_data
    set deletado            = true,
        pg_username_delecao = SESSION_USER,
        data_hora_deletado  = f_data_hora_realizacao_transferencia
    where cod_pneu = f_cod_pneu
      and deletado = false
      and data_hora_resolucao is null
      and (fechado_automaticamente_movimentacao = false or fechado_automaticamente_movimentacao is null);

    get diagnostics v_qtd_updates = row_count;

    -- O SELECT do INSERT e o UPDATE são propositalmente diferentes nas condições do WHERE. No INSERT fazemos o JOIN
    -- com AFERICAO para buscar apenas os serviços em aberto do pneu no veículo em que ele está sendo transferido.
    -- Isso é importante, pois como fazemos o vínculo com a transferência do veículo, não podemos vincular que o veículo
    -- fechou serviços em aberto do veículo B. Ainda que seja o mesmo pneu em jogo.
    -- Em teoria, não deveriam existir serviços em aberto em outra placa que não a atual em que o pneu está aplicado.
    -- Porém, podemos ter uma inconsistência no BD.
    -- Utilizando essas condições diferentes no WHERE do INSERT e UPDATE, nós garantimos que o ROW_COUNT será diferente
    -- em ambos e vamos lançar uma exception, mapeando esse problema para termos visibilidade.
    if v_qtd_inserts <> v_qtd_updates
    then
        raise exception 'Erro ao deletar os serviços de pneus na transferência de veículos. Rollback necessário!';
    end if;
end;
$$;

drop function suporte.func_veiculo_deleta_veiculo(f_cod_unidade bigint,
    f_placa varchar,
    f_motivo_delecao text);
create or replace function suporte.func_veiculo_deleta_veiculo(f_cod_unidade bigint,
                                                               f_placa varchar(255),
                                                               f_motivo_delecao text,
                                                               out dependencias_deletadas text)
    returns text
    language plpgsql
    security definer
as
$$
declare
    v_codigo_loop                   bigint;
    v_lista_cod_afericao_placa      bigint[];
    v_lista_cod_check_placa         bigint[];
    v_lista_cod_prolog_deletado_cos bigint[];
    v_nome_empresa                  varchar(255) := (select e.nome
                                                     from empresa e
                                                     where e.codigo =
                                                           (select u.cod_empresa
                                                            from unidade u
                                                            where u.codigo = f_cod_unidade));
    v_nome_unidade                  varchar(255) := (select u.nome
                                                     from unidade u
                                                     where u.codigo = f_cod_unidade);
    v_cod_veiculo                   bigint       := (select codigo
                                                     from veiculo v
                                                     where v.placa = f_placa
                                                       and v.cod_unidade = f_cod_unidade);
begin
    perform suporte.func_historico_salva_execucao();

    perform func_garante_unidade_existe(f_cod_unidade);

    perform func_garante_veiculo_existe(f_cod_unidade, f_placa);

    -- Verifica se veículo possui pneus aplicados.
    if exists(select vp.cod_pneu from veiculo_pneu vp where vp.placa = f_placa and vp.cod_unidade = f_cod_unidade)
    then
        raise exception 'Erro! A Placa: % possui pneus aplicados. Favor removê-los', f_placa;
    end if;

    -- Verifica se placa possui aferição. Optamos por usar _DATA para garantir que tudo será deletado.
    if exists(select a.codigo from afericao_data a where a.cod_veiculo = v_cod_veiculo)
    then
        -- Coletamos todos os cod_afericao que a placa possui.
        select array_agg(a.codigo)
        from afericao_data a
        where a.cod_veiculo = v_cod_veiculo
        into v_lista_cod_afericao_placa;

        -- Deletamos aferição em afericao_manutencao_data, caso não esteja deletada.
        update afericao_manutencao_data
        set deletado            = true,
            data_hora_deletado  = now(),
            pg_username_delecao = session_user,
            motivo_delecao      = f_motivo_delecao
        where deletado = false
          and cod_afericao = any (v_lista_cod_afericao_placa);

        -- Deletamos aferição em afericao_valores_data, caso não esteja deletada.
        update afericao_valores_data
        set deletado            = true,
            data_hora_deletado  = now(),
            pg_username_delecao = session_user,
            motivo_delecao      = f_motivo_delecao
        where deletado = false
          and cod_afericao = any (v_lista_cod_afericao_placa);

        -- Deletamos aferição, caso não esteja deletada.
        update afericao_data
        set deletado            = true,
            data_hora_deletado  = now(),
            pg_username_delecao = session_user,
            motivo_delecao      = f_motivo_delecao
        where deletado = false
          and codigo = any (v_lista_cod_afericao_placa);
    end if;

    -- Verifica se placa possui checklist. Optamos por usar _DATA para garantir que tudo será deletado.
    if exists(select c.placa_veiculo from checklist_data c where c.deletado = false and c.placa_veiculo = f_placa)
    then
        -- Busca todos os códigos de checklists da placa.
        select array_agg(c.codigo)
        from checklist_data c
        where c.deletado = false
          and c.placa_veiculo = f_placa
        into v_lista_cod_check_placa;

        -- Deleta todos os checklists da placa. Usamos deleção lógica em conjunto com uma tabela de deleção específica.
        insert into checklist_delecao (cod_checklist,
                                       cod_colaborador,
                                       data_hora,
                                       acao_executada,
                                       origem_delecao,
                                       observacao,
                                       pg_username_delecao)
        select unnest(v_lista_cod_check_placa),
               null,
               now(),
               'DELETADO',
               'SUPORTE',
               f_motivo_delecao,
               session_user;

        update checklist_data set deletado = true where codigo = any (v_lista_cod_check_placa);

        -- Usamos, obrigatoriamente, a view checklist_ordem_servico para
        -- evitar de tentar deletar OSs que estão deletadas.
        if exists(select cos.codigo
                  from checklist_ordem_servico cos
                  where cos.cod_checklist = any (v_lista_cod_check_placa))
        then
            -- Deleta ordens de serviços dos checklists.
            update checklist_ordem_servico_data
            set deletado            = true,
                data_hora_deletado  = now(),
                pg_username_delecao = session_user,
                motivo_delecao      = f_motivo_delecao
            where deletado = false
              and cod_checklist = any (v_lista_cod_check_placa);

            -- Busca os codigo Prolog deletados nas Ordens de Serviços.
            select array_agg(codigo_prolog)
            from checklist_ordem_servico_data
            where cod_checklist = any (v_lista_cod_check_placa)
              and deletado is true
            into v_lista_cod_prolog_deletado_cos;

            -- Para cada código prolog deletado em cos, deletamos o referente na cosi.
            foreach v_codigo_loop in array v_lista_cod_prolog_deletado_cos
                loop
                    -- Deleta em cosi aqueles que foram deletados na cos.
                    update checklist_ordem_servico_itens_data
                    set deletado            = true,
                        data_hora_deletado  = now(),
                        pg_username_delecao = session_user,
                        motivo_delecao      = f_motivo_delecao
                    where deletado = false
                      and (cod_os, cod_unidade) = (select cos.codigo, cos.cod_unidade
                                                   from checklist_ordem_servico_data cos
                                                   where cos.codigo_prolog = v_codigo_loop);
                end loop;
        end if;
    end if;

    -- Verifica se a placa é integrada.
    if exists(select ivc.placa_veiculo_cadastro
              from integracao.veiculo_cadastrado ivc
              where ivc.cod_unidade_cadastro = f_cod_unidade
                and ivc.placa_veiculo_cadastro = f_placa)
    then
        -- Realiza a deleção da placa (não possuímos deleção lógica).
        delete
        from integracao.veiculo_cadastrado
        where cod_unidade_cadastro = f_cod_unidade
          and placa_veiculo_cadastro = f_placa;
    end if;

    -- Realiza deleção da placa.
    update veiculo_data
    set deletado            = true,
        data_hora_deletado  = now(),
        pg_username_delecao = session_user,
        motivo_delecao      = f_motivo_delecao
    where cod_unidade = f_cod_unidade
      and placa = f_placa
      and deletado = false;

    -- Mensagem de sucesso.
    select 'Veículo deletado junto com suas dependências. Veículo: '
               || f_placa
               || ', Empresa: '
               || v_nome_empresa
               || ', Unidade: '
               || v_nome_unidade
    into dependencias_deletadas;
end;
$$;

drop function suporte.func_veiculo_transfere_veiculo_entre_empresas(f_placa_veiculo varchar,
    f_cod_empresa_origem bigint,
    f_cod_unidade_origem bigint,
    f_cod_empresa_destino bigint,
    f_cod_unidade_destino bigint,
    f_cod_modelo_veiculo_destino bigint,
    f_cod_tipo_veiculo_destino bigint);
create or replace function suporte.func_veiculo_transfere_veiculo_entre_empresas(f_placa_veiculo varchar(7),
                                                                                 f_cod_empresa_origem bigint,
                                                                                 f_cod_unidade_origem bigint,
                                                                                 f_cod_empresa_destino bigint,
                                                                                 f_cod_unidade_destino bigint,
                                                                                 f_cod_modelo_veiculo_destino bigint,
                                                                                 f_cod_tipo_veiculo_destino bigint,
                                                                                 out veiculo_transferido text)
    returns text
    language plpgsql
    security definer
as
$$
declare
    v_nome_empresa_destino                           varchar(255) := (select e.nome
                                                                      from empresa e
                                                                      where e.codigo = f_cod_empresa_destino);
    v_nome_unidade_destino                           varchar(255) := (select u.nome
                                                                      from unidade u
                                                                      where u.codigo = f_cod_unidade_destino);
    v_lista_cod_oss_check                            bigint[];
    v_lista_cod_afericao_placa                       bigint[];
    v_cod_afericao_foreach                           bigint;
    v_lista_cod_pneu_em_afericao_manutencao          bigint[];
    v_qtd_cod_afericao_em_afericao_valores           bigint;
    v_qtd_cod_afericao_deletados_em_afericao_valores bigint;
    v_cod_veiculo                                    bigint       := (select v.codigo
                                                                      from veiculo v
                                                                      where v.placa = f_placa_veiculo
                                                                        and v.cod_unidade = f_cod_unidade_origem);
begin
    perform suporte.func_historico_salva_execucao();

    -- Verifica se empresa origem possui unidade origem.
    perform func_garante_integridade_empresa_unidade(f_cod_empresa_origem, f_cod_unidade_origem);

    -- Verifica se empresa destino possui unidade destino.
    perform func_garante_integridade_empresa_unidade(f_cod_empresa_destino, f_cod_unidade_destino);

    perform func_garante_empresas_distintas(f_cod_empresa_origem, f_cod_empresa_destino);
    perform func_garante_veiculo_existe(f_cod_unidade_origem, f_placa_veiculo);

    -- Verifica se a placa possui pneus.
    if exists(select vp.cod_pneu
              from veiculo_pneu vp
              where vp.placa = f_placa_veiculo
                and vp.cod_unidade = f_cod_unidade_origem)
    then
        raise exception 'Erro! A placa: % possui pneus vinculados, favor remover os pneus do mesmo', f_placa_veiculo;
    end if;

    -- Verifica se empresa destino possui tipo do veículo informado.
    if not exists(
            select vt.codigo
            from veiculo_tipo vt
            where vt.cod_empresa = f_cod_empresa_destino
              and vt.codigo = f_cod_tipo_veiculo_destino)
    then
        raise exception 'Erro! O código tipo: % não existe na empresa destino: %', f_cod_tipo_veiculo_destino,
            v_nome_empresa_destino;
    end if;

    -- Verifica se o tipo de veículo informado tem o mesmo diagrama do veículo.
    if not exists(
            select v.codigo
            from veiculo v
                     join veiculo_tipo vt on v.cod_diagrama = vt.cod_diagrama
            where v.placa = f_placa_veiculo
              and vt.codigo = f_cod_tipo_veiculo_destino)
    then
        raise exception 'Erro! O diagrama do tipo: % é diferente do veículo: %', f_cod_tipo_veiculo_destino,
            f_placa_veiculo;
    end if;

    -- Verifica se empresa destino possui modelo do veículo informado.
    if not exists(select mv.codigo
                  from modelo_veiculo mv
                  where mv.cod_empresa = f_cod_empresa_destino
                    and mv.codigo = f_cod_modelo_veiculo_destino)
    then
        raise exception 'Erro! O código modelo: % não existe na empresa destino: %', f_cod_modelo_veiculo_destino,
            v_nome_empresa_destino;
    end if;

    -- Verifica se placa possui aferição.
    if exists(select a.codigo
              from afericao a
              where a.cod_veiculo = v_cod_veiculo)
    then
        -- Então coletamos todos os códigos das aferições que a placa possui e adicionamos no array.
        select distinct array_agg(a.codigo)
        from afericao a
        where a.cod_veiculo = v_cod_veiculo
        into v_lista_cod_afericao_placa;

        -- Laço for para percorrer todos os valores em f_lista_cod_afericao_placa.
        foreach v_cod_afericao_foreach in array v_lista_cod_afericao_placa
            loop
                -- Para cada valor em: f_lista_cod_afericao_placa.
                if exists(select am.cod_afericao
                          from afericao_manutencao am
                          where am.cod_afericao = v_cod_afericao_foreach
                            and am.data_hora_resolucao is null
                            and am.fechado_automaticamente_integracao is false
                            and am.fechado_automaticamente_movimentacao is false)
                then
                    -- Coleta o(s) cod_pneu correspondentes ao cod_afericao.
                    select array_agg(am.cod_pneu)
                    from afericao_manutencao am
                    where am.cod_afericao = v_cod_afericao_foreach
                      and am.data_hora_resolucao is null
                      and am.fechado_automaticamente_integracao is false
                      and am.fechado_automaticamente_movimentacao is false
                    into v_lista_cod_pneu_em_afericao_manutencao;

                    -- Deleta aferição em afericao_manutencao_data através do cod_afericao e cod_pneu.
                    update afericao_manutencao_data
                    set deletado            = true,
                        data_hora_deletado  = now(),
                        pg_username_delecao = session_user
                    where cod_unidade = f_cod_unidade_origem
                      and cod_afericao = v_cod_afericao_foreach
                      and cod_pneu = any (v_lista_cod_pneu_em_afericao_manutencao);

                    -- Deleta afericao em afericao_valores_data através do cod_afericao e cod_pneu.
                    update afericao_valores_data
                    set deletado            = true,
                        data_hora_deletado  = now(),
                        pg_username_delecao = session_user
                    where cod_unidade = f_cod_unidade_origem
                      and cod_afericao = v_cod_afericao_foreach
                      and cod_pneu = any (v_lista_cod_pneu_em_afericao_manutencao);
                end if;
            end loop;

        -- Se, e somente se, a aferição possuir todos os valores excluídos, deve-se excluir toda a aferição.
        -- Senão, a aferição continua existindo.
        foreach v_cod_afericao_foreach in array v_lista_cod_afericao_placa
            loop
                v_qtd_cod_afericao_em_afericao_valores = (select count(avd.cod_afericao)
                                                          from afericao_valores_data avd
                                                          where avd.cod_afericao = v_cod_afericao_foreach);

                v_qtd_cod_afericao_deletados_em_afericao_valores = (select count(avd.cod_afericao)
                                                                    from afericao_valores_data avd
                                                                    where avd.cod_afericao = v_cod_afericao_foreach
                                                                      and avd.deletado is true);

                -- Se a quantidade de um cod_afericao em afericao_valores_data for igual a quantidade de um cod_afericao
                -- deletado em afericao_valores_data, devemos excluir a aferição, pois, todos seus valores foram
                -- deletados.
                if (v_qtd_cod_afericao_em_afericao_valores =
                    v_qtd_cod_afericao_deletados_em_afericao_valores)
                then
                    update afericao_data
                    set deletado            = true,
                        data_hora_deletado  = now(),
                        pg_username_delecao = session_user
                    where cod_unidade = f_cod_unidade_origem
                      and codigo = v_cod_afericao_foreach;
                end if;
            end loop;
    end if;

    -- Se possuir itens de OS aberto, deletamos esses itens.
    select array_agg(cos.codigo_prolog)
    from checklist c
             join checklist_ordem_servico cos
                  on c.codigo = cos.cod_checklist
    where c.placa_veiculo = f_placa_veiculo
      and cos.status = 'A'
    into v_lista_cod_oss_check;

    if (f_size_array(v_lista_cod_oss_check) > 0)
    then
        -- Deletamos primeiro as OSs.
        update checklist_ordem_servico_data
        set deletado            = true,
            data_hora_deletado  = now(),
            pg_username_delecao = session_user,
            motivo_delecao      = format(
                    'Deletado por conta de uma transferência de veículo entre empresas (%s -> %s) em: %s.',
                    f_cod_empresa_origem,
                    f_cod_empresa_destino,
                    now())
        where codigo_prolog = any (v_lista_cod_oss_check);

        -- Agora deletamos os itens.
        update checklist_ordem_servico_itens_data
        set deletado            = true,
            data_hora_deletado  = now(),
            pg_username_delecao = session_user,
            motivo_delecao      = format(
                    'Deletado por conta de uma transferência de veículo entre empresas (%s -> %s) em: %s.',
                    f_cod_empresa_origem,
                    f_cod_empresa_destino,
                    now())
            -- Precisamos usar a _DATA nesse where pois já deletamos as OSs.
        where (cod_os, cod_unidade) in (select cosd.codigo, cosd.cod_unidade
                                        from checklist_ordem_servico_data cosd
                                        where cosd.codigo_prolog = any (v_lista_cod_oss_check));
    end if;

    -- Se o veículo for integrado, atualiza os dados de empresa e unidade na tabela de integração.
    if exists(select ivc.placa_veiculo_cadastro
              from integracao.veiculo_cadastrado ivc
              where ivc.cod_empresa_cadastro = f_cod_empresa_origem
                and ivc.cod_unidade_cadastro = f_cod_unidade_origem
                and ivc.placa_veiculo_cadastro = f_placa_veiculo)
    then
        update integracao.veiculo_cadastrado
        set cod_unidade_cadastro = f_cod_unidade_destino,
            cod_empresa_cadastro = f_cod_empresa_destino
        where cod_empresa_cadastro = f_cod_empresa_origem
          and cod_unidade_cadastro = f_cod_unidade_origem
          and placa_veiculo_cadastro = f_placa_veiculo;
    end if;

    -- Realiza transferência.
    update veiculo
    set cod_empresa = f_cod_empresa_destino,
        cod_unidade = f_cod_unidade_destino,
        cod_tipo    = f_cod_tipo_veiculo_destino,
        cod_modelo  = f_cod_modelo_veiculo_destino
    where cod_empresa = f_cod_empresa_origem
      and cod_unidade = f_cod_unidade_origem
      and placa = f_placa_veiculo;

    -- Mensagem de sucesso.
    select 'Veículo transferido com sucesso! O veículo com placa: ' || f_placa_veiculo ||
           ' foi transferido para a empresa ' || v_nome_empresa_destino || ' junto a unidade ' ||
           v_nome_unidade_destino || '.'
    into veiculo_transferido;
end
$$;

drop function func_veiculo_busca_evolucao_km_consolidado(f_cod_empresa bigint,
    f_cod_veiculo bigint,
    f_data_inicial date,
    f_data_final date);
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
                                            m.codigo                                    as codigo,
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

-- Recria views

create or replace view view_analise_pneus as
select u.nome                                                                         as "UNIDADE ALOCADO",
       pd.codigo                                                                      as "COD PNEU",
       pd.codigo_cliente                                                              as "COD PNEU CLIENTE",
       pd.status                                                                      as "STATUS PNEU",
       pd.cod_unidade,
       map.nome                                                                       as "MARCA",
       mp.nome                                                                        as "MODELO",
       (((dp.largura || '/'::text) || dp.altura) || ' R'::text) || dp.aro             as "MEDIDAS",
       dados.qt_afericoes                                                             as "QTD DE AFERIÇÕES",
       to_char(dados.primeira_afericao::timestamp with time zone, 'DD/MM/YYYY'::text) as "DTA 1a AFERIÇÃO",
       to_char(dados.ultima_afericao::timestamp with time zone, 'DD/MM/YYYY'::text)   as "DTA ÚLTIMA AFERIÇÃO",
       dados.total_dias                                                               as "DIAS ATIVO",
       round(
               case
                   when dados.total_dias > 0 then dados.total_km / dados.total_dias::numeric
                   else null::numeric
                   end)                                                               as "MÉDIA KM POR DIA",
       pd.altura_sulco_interno,
       pd.altura_sulco_central_interno,
       pd.altura_sulco_central_externo,
       pd.altura_sulco_externo,
       round(dados.maior_sulco::numeric, 2)                                           as "MAIOR MEDIÇÃO VIDA",
       round(dados.menor_sulco::numeric, 2)                                           as "MENOR SULCO ATUAL",
       round(dados.sulco_gasto::numeric, 2)                                           as "MILIMETROS GASTOS",
       round(dados.km_por_mm::numeric, 2)                                             as "KMS POR MILIMETRO",
       round((dados.km_por_mm * dados.sulco_restante)::numeric)                       as "KMS A PERCORRER",
       trunc(
               case
                   when dados.total_km > 0::numeric and dados.total_dias > 0 and
                        (dados.total_km / dados.total_dias::numeric) > 0::numeric then
                           dados.km_por_mm * dados.sulco_restante /
                           (dados.total_km / dados.total_dias::numeric)::double precision
                   else 0::double precision
                   end)                                                               as "DIAS RESTANTES",
       case
           when dados.total_km > 0::numeric and dados.total_dias > 0 and
                (dados.total_km / dados.total_dias::numeric) > 0::numeric then (dados.km_por_mm * dados.sulco_restante /
                                                                                (dados.total_km / dados.total_dias::numeric)::double precision)::integer +
                                                                               'NOW'::text::date
           else null::date
           end                                                                        as "PREVISÃO DE TROCA"
from pneu_data pd
         join (select av.cod_pneu,
                      av.cod_unidade,
                      count(av.altura_sulco_central_interno)                                   as qt_afericoes,
                      min(timezone(tz_unidade(a.cod_unidade), a.data_hora))::date              as primeira_afericao,
                      max(timezone(tz_unidade(a.cod_unidade), a.data_hora))::date              as ultima_afericao,
                      max(timezone(tz_unidade(a.cod_unidade), a.data_hora))::date -
                      min(timezone(tz_unidade(a.cod_unidade), a.data_hora))::date              as total_dias,
                      max(total_km.total_km)                                                   as total_km,
                      max(GREATEST(av.altura_sulco_interno, av.altura_sulco_central_interno,
                                   av.altura_sulco_central_externo, av.altura_sulco_externo))  as maior_sulco,
                      min(LEAST(av.altura_sulco_interno, av.altura_sulco_central_interno,
                                av.altura_sulco_central_externo, av.altura_sulco_externo))     as menor_sulco,
                      max(GREATEST(av.altura_sulco_interno, av.altura_sulco_central_interno,
                                   av.altura_sulco_central_externo, av.altura_sulco_externo)) - min(
                              LEAST(av.altura_sulco_interno, av.altura_sulco_central_interno,
                                    av.altura_sulco_central_externo, av.altura_sulco_externo)) as sulco_gasto,
                      case
                          when
                                  case
                                      when p_1.vida_atual = p_1.vida_total then min(LEAST(av.altura_sulco_interno,
                                                                                          av.altura_sulco_central_interno,
                                                                                          av.altura_sulco_central_externo,
                                                                                          av.altura_sulco_externo)) -
                                                                                pru.sulco_minimo_descarte
                                      when p_1.vida_atual < p_1.vida_total then min(LEAST(av.altura_sulco_interno,
                                                                                          av.altura_sulco_central_interno,
                                                                                          av.altura_sulco_central_externo,
                                                                                          av.altura_sulco_externo)) -
                                                                                pru.sulco_minimo_recapagem
                                      else null::real
                                      end < 0::double precision then 0::real
                          else
                              case
                                  when p_1.vida_atual = p_1.vida_total then min(LEAST(av.altura_sulco_interno,
                                                                                      av.altura_sulco_central_interno,
                                                                                      av.altura_sulco_central_externo,
                                                                                      av.altura_sulco_externo)) -
                                                                            pru.sulco_minimo_descarte
                                  when p_1.vida_atual < p_1.vida_total then min(LEAST(av.altura_sulco_interno,
                                                                                      av.altura_sulco_central_interno,
                                                                                      av.altura_sulco_central_externo,
                                                                                      av.altura_sulco_externo)) -
                                                                            pru.sulco_minimo_recapagem
                                  else null::real
                                  end
                          end                                                                  as sulco_restante,
                      case
                          when (max(timezone(tz_unidade(a.cod_unidade), a.data_hora))::date -
                                min(timezone(tz_unidade(a.cod_unidade), a.data_hora))::date) > 0 then
                                      max(total_km.total_km)::double precision / max(
                                          GREATEST(av.altura_sulco_interno, av.altura_sulco_central_interno,
                                                   av.altura_sulco_central_externo, av.altura_sulco_externo)) - min(
                                              LEAST(av.altura_sulco_interno, av.altura_sulco_central_interno,
                                                    av.altura_sulco_central_externo, av.altura_sulco_externo))
                          else 0::double precision
                          end                                                                  as km_por_mm
               from afericao_valores_data av
                        join afericao_data a on a.codigo = av.cod_afericao
                        join pneu_data p_1 on p_1.codigo::text = av.cod_pneu::text and p_1.status::text = 'EM_USO'::text
                        join pneu_restricao_unidade pru on pru.cod_unidade = av.cod_unidade
                        join (select total_km_rodado.cod_pneu,
                                     total_km_rodado.cod_unidade,
                                     sum(total_km_rodado.km_rodado) as total_km
                              from (select av_1.cod_pneu,
                                           av_1.cod_unidade,
                                           max(a_1.km_veiculo) - min(a_1.km_veiculo) as km_rodado
                                    from afericao_valores_data av_1
                                             join afericao_data a_1 on a_1.codigo = av_1.cod_afericao
                                    group by av_1.cod_pneu, av_1.cod_unidade, a_1.cod_veiculo) total_km_rodado
                              group by total_km_rodado.cod_pneu, total_km_rodado.cod_unidade) total_km
                             on total_km.cod_pneu = av.cod_pneu and total_km.cod_unidade = av.cod_unidade
               group by av.cod_pneu, av.cod_unidade, p_1.vida_atual, p_1.vida_total, pru.sulco_minimo_descarte,
                        pru.sulco_minimo_recapagem) dados on dados.cod_pneu = pd.codigo
         join dimensao_pneu dp on dp.codigo = pd.cod_dimensao
         join unidade u on u.codigo = pd.cod_unidade
         join modelo_pneu mp on mp.codigo = pd.cod_modelo and mp.cod_empresa = u.cod_empresa
         join marca_pneu map on map.codigo = mp.cod_marca;

create or replace view view_pneu_km_rodado_vida as
select p.codigo                                                                      as cod_pneu,
       coalesce(q.vida_pneu, p.vida_atual)                                           as vida_pneu,
       (coalesce(sum(q.km_rodado), 0)
           +
        (select func_pneu_calcula_km_aplicacao_remocao_pneu(p.codigo, q.vida_pneu))) as km_rodado_vida
from (select av.cod_pneu,
             av.vida_momento_afericao                as vida_pneu,
             (max(a.km_veiculo) - min(a.km_veiculo)) as km_rodado
      from (afericao_valores av
               join afericao a on a.codigo = av.cod_afericao
          )
      where ((a.tipo_processo_coleta)::text = 'PLACA'::text)
      group by av.cod_pneu, a.cod_veiculo, av.vida_momento_afericao
      order by av.cod_pneu) q
         right join pneu_data p on p.codigo = q.cod_pneu
group by p.codigo, q.vida_pneu
order by p.codigo, q.vida_pneu;

create view view_pneu_analise_vidas as
with dados_afericao as (
    select a.codigo                                                                            as cod_afericao,
           a.cod_unidade                                                                       as cod_unidade_afericao,
           a.data_hora                                                                         as data_hora_afericao,
           a.tipo_processo_coleta                                                              as tipo_processo_coleta_afericao,
           av.cod_pneu,
           av.vida_momento_afericao,
           av.altura_sulco_central_interno,
           av.altura_sulco_central_externo,
           av.altura_sulco_externo,
           av.altura_sulco_interno,
           row_number()
           over (partition by av.cod_pneu, av.vida_momento_afericao order by a.data_hora)      as row_number_asc,
           row_number()
           over (partition by av.cod_pneu, av.vida_momento_afericao order by a.data_hora desc) as row_number_desc
    from (afericao a
             join afericao_valores av on ((a.codigo = av.cod_afericao)))
),
     primeira_afericao as (
         select da.cod_pneu,
                da.vida_momento_afericao,
                da.cod_afericao,
                da.cod_unidade_afericao,
                da.data_hora_afericao
         from dados_afericao da
         where (da.row_number_asc = 1)
     ),
     ultima_afericao as (
         select da.cod_pneu,
                da.vida_momento_afericao,
                da.cod_afericao,
                da.cod_unidade_afericao,
                da.data_hora_afericao
         from dados_afericao da
         where (da.row_number_desc = 1)
     ),
     analises_afericoes as (
         select da.cod_pneu,
                da.vida_momento_afericao               as vida_analisada_pneu,
                count(da.cod_pneu)                     as quantidade_afericoes_pneu_vida,
                max(GREATEST(da.altura_sulco_externo, da.altura_sulco_central_externo, da.altura_sulco_central_interno,
                             da.altura_sulco_interno)) as maior_sulco_aferido_vida,
                min(LEAST(da.altura_sulco_externo, da.altura_sulco_central_externo, da.altura_sulco_central_interno,
                          da.altura_sulco_interno))    as menor_sulco_aferido_vida
         from dados_afericao da
         group by da.cod_pneu, da.vida_momento_afericao
     )
select p.codigo                                                                            as cod_pneu,
       p.status,
       p.valor                                                                             as valor_pneu,
       COALESCE(pvv.valor, (0)::real)                                                      as valor_banda,
       pa.data_hora_afericao                                                               as data_hora_primeira_afericao,
       pa.cod_afericao                                                                     as cod_primeira_afericao,
       pa.cod_unidade_afericao                                                             as cod_unidade_primeira_afericao,
       ua.data_hora_afericao                                                               as data_hora_ultima_afericao,
       ua.cod_afericao                                                                     as cod_ultima_afericao,
       ua.cod_unidade_afericao                                                             as cod_unidade_ultima_afericao,
       aa.vida_analisada_pneu,
       aa.quantidade_afericoes_pneu_vida,
       aa.maior_sulco_aferido_vida,
       aa.menor_sulco_aferido_vida,
       (aa.maior_sulco_aferido_vida - aa.menor_sulco_aferido_vida)                         as sulco_gasto,
       (date_part('days'::text, (ua.data_hora_afericao - pa.data_hora_afericao)))::integer as total_dias_ativo,
       km_rodado_pneu.km_rodado_vida                                                       as total_km_rodado_vida,
       func_pneu_calcula_sulco_restante(p.vida_atual, p.vida_total, p.altura_sulco_externo,
                                        p.altura_sulco_central_externo, p.altura_sulco_central_interno,
                                        p.altura_sulco_interno, pru.sulco_minimo_recapagem,
                                        pru.sulco_minimo_descarte)                         as sulco_restante,
       case
           when ((date_part('days'::text, (ua.data_hora_afericao - pa.data_hora_afericao)) > (0)::double precision) and
                 ((aa.maior_sulco_aferido_vida - aa.menor_sulco_aferido_vida) > (0)::double precision)) then (
                   (km_rodado_pneu.km_rodado_vida)::double precision /
                   (aa.maior_sulco_aferido_vida - aa.menor_sulco_aferido_vida))
           else (0)::double precision
           end                                                                             as km_por_mm_vida,
       case
           when (km_rodado_pneu.km_rodado_vida = (0)::numeric) then (0)::double precision
           else
               case
                   when (km_rodado_pneu.vida_pneu = 1)
                       then (p.valor / (km_rodado_pneu.km_rodado_vida)::double precision)
                   else (COALESCE(pvv.valor, (0)::real) / (km_rodado_pneu.km_rodado_vida)::double precision)
                   end
           end                                                                             as valor_por_km_vida
from ((((((analises_afericoes aa
    join primeira_afericao pa on (((pa.cod_pneu = aa.cod_pneu) and (pa.vida_momento_afericao = aa.vida_analisada_pneu))))
    join ultima_afericao ua on (((ua.cod_pneu = aa.cod_pneu) and (ua.vida_momento_afericao = aa.vida_analisada_pneu))))
    join pneu p on ((aa.cod_pneu = p.codigo)))
    join pneu_restricao_unidade pru on ((p.cod_unidade = pru.cod_unidade)))
    left join pneu_valor_vida pvv on ((p.codigo = pvv.cod_pneu)))
         join view_pneu_km_rodado_vida km_rodado_pneu
              on (((km_rodado_pneu.cod_pneu = aa.cod_pneu) and (km_rodado_pneu.vida_pneu = aa.vida_analisada_pneu))))
order by aa.cod_pneu, aa.vida_analisada_pneu;

create view view_pneu_km_rodado_total as
with km_rodado_total as (
    select view_pneu_km_rodado_vida.cod_pneu,
           sum(view_pneu_km_rodado_vida.km_rodado_vida) as total_km_rodado_todas_vidas
    from view_pneu_km_rodado_vida
    group by view_pneu_km_rodado_vida.cod_pneu
    order by view_pneu_km_rodado_vida.cod_pneu
)
select km_vida.cod_pneu,
       km_vida.vida_pneu,
       km_vida.km_rodado_vida,
       km_total.total_km_rodado_todas_vidas
from (view_pneu_km_rodado_vida km_vida
         join km_rodado_total km_total on ((km_vida.cod_pneu = km_total.cod_pneu)))
order by km_vida.cod_pneu, km_vida.vida_pneu;

create view view_pneu_analise_vida_atual as
select u.nome                                                               as "UNIDADE ALOCADO",
       p.codigo                                                             as "COD PNEU",
       p.codigo_cliente                                                     as "COD PNEU CLIENTE",
       (p.valor + sum(pvv.valor))                                           as valor_acumulado,
       sum(v.total_km_rodado_todas_vidas)                                   as km_acumulado,
       p.vida_atual                                                         as "VIDA ATUAL",
       p.status                                                             as "STATUS PNEU",
       p.cod_unidade,
       p.valor                                                              as valor_pneu,
       case
           when (dados.vida_analisada_pneu = 1) then dados.valor_pneu
           else dados.valor_banda
           end                                                              as valor_vida_atual,
       map.nome                                                             as "MARCA",
       mp.nome                                                              as "MODELO",
       ((((dp.largura || '/'::text) || dp.altura) || ' R'::text) || dp.aro) as "MEDIDAS",
       dados.quantidade_afericoes_pneu_vida                                 as "QTD DE AFERIÇÕES",
       to_char(dados.data_hora_primeira_afericao, 'DD/MM/YYYY'::text)       as "DTA 1a AFERIÇÃO",
       to_char(dados.data_hora_ultima_afericao, 'DD/MM/YYYY'::text)         as "DTA ÚLTIMA AFERIÇÃO",
       dados.total_dias_ativo                                               as "DIAS ATIVO",
       round(
               case
                   when (dados.total_dias_ativo > 0)
                       then (dados.total_km_rodado_vida / (dados.total_dias_ativo)::numeric)
                   else null::numeric
                   end)                                                     as "MÉDIA KM POR DIA",
       round((dados.maior_sulco_aferido_vida)::numeric, 2)                  as "MAIOR MEDIÇÃO VIDA",
       round((dados.menor_sulco_aferido_vida)::numeric, 2)                  as "MENOR SULCO ATUAL",
       round((dados.sulco_gasto)::numeric, 2)                               as "MILIMETROS GASTOS",
       round((dados.km_por_mm_vida)::numeric, 2)                            as "KMS POR MILIMETRO",
       round((dados.valor_por_km_vida)::numeric, 2)                         as "VALOR POR KM",
       round((
                 case
                     when (sum(v.total_km_rodado_todas_vidas) > (0)::numeric) then ((p.valor + sum(pvv.valor)) /
                                                                                    (sum(v.total_km_rodado_todas_vidas))::double precision)
                     else (0)::double precision
                     end)::numeric, 2)                                      as "VALOR POR KM ACUMULADO",
       round(((dados.km_por_mm_vida * dados.sulco_restante))::numeric)      as "KMS A PERCORRER",
       trunc(
               case
                   when (((dados.total_km_rodado_vida > (0)::numeric) and (dados.total_dias_ativo > 0)) and
                         ((dados.total_km_rodado_vida / (dados.total_dias_ativo)::numeric) > (0)::numeric)) then (
                           (dados.km_por_mm_vida * dados.sulco_restante) /
                           ((dados.total_km_rodado_vida / (dados.total_dias_ativo)::numeric))::double precision)
                   else (0)::double precision
                   end)                                                     as "DIAS RESTANTES",
       case
           when (((dados.total_km_rodado_vida > (0)::numeric) and (dados.total_dias_ativo > 0)) and
                 ((dados.total_km_rodado_vida / (dados.total_dias_ativo)::numeric) > (0)::numeric)) then (
                   (((dados.km_por_mm_vida * dados.sulco_restante) /
                     ((dados.total_km_rodado_vida / (dados.total_dias_ativo)::numeric))::double precision))::integer +
                   ('NOW'::text)::date)
           else null::date
           end                                                              as "PREVISÃO DE TROCA",
       case
           when (p.vida_atual = p.vida_total) then 'DESCARTE'::text
           else 'ANÁLISE'::text
           end                                                              as "DESTINO"
from (((((((pneu p
    join (select view_pneu_analise_vidas.cod_pneu,
                 view_pneu_analise_vidas.vida_analisada_pneu,
                 view_pneu_analise_vidas.status,
                 view_pneu_analise_vidas.valor_pneu,
                 view_pneu_analise_vidas.valor_banda,
                 view_pneu_analise_vidas.quantidade_afericoes_pneu_vida,
                 view_pneu_analise_vidas.data_hora_primeira_afericao,
                 view_pneu_analise_vidas.data_hora_ultima_afericao,
                 view_pneu_analise_vidas.total_dias_ativo,
                 view_pneu_analise_vidas.total_km_rodado_vida,
                 view_pneu_analise_vidas.maior_sulco_aferido_vida,
                 view_pneu_analise_vidas.menor_sulco_aferido_vida,
                 view_pneu_analise_vidas.sulco_gasto,
                 view_pneu_analise_vidas.sulco_restante,
                 view_pneu_analise_vidas.km_por_mm_vida,
                 view_pneu_analise_vidas.valor_por_km_vida
          from view_pneu_analise_vidas) dados on (((dados.cod_pneu = p.codigo) and
                                                   (dados.vida_analisada_pneu = p.vida_atual))))
    join dimensao_pneu dp on ((dp.codigo = p.cod_dimensao)))
    join unidade u on ((u.codigo = p.cod_unidade)))
    join modelo_pneu mp on (((mp.codigo = p.cod_modelo) and (mp.cod_empresa = u.cod_empresa))))
    join marca_pneu map on ((map.codigo = mp.cod_marca)))
    join view_pneu_km_rodado_total v on (((p.codigo = v.cod_pneu) and (p.vida_atual = v.vida_pneu))))
         left join pneu_valor_vida pvv on ((pvv.cod_pneu = p.codigo)))
group by u.nome, p.codigo, p.valor, p.vida_atual, p.status, p.vida_total, p.codigo_cliente, p.cod_unidade,
         dados.valor_banda, dados.valor_pneu, map.nome,
         mp.nome, dp.largura, dp.altura, dp.aro, dados.quantidade_afericoes_pneu_vida,
         dados.data_hora_primeira_afericao, dados.data_hora_ultima_afericao, dados.total_dias_ativo,
         dados.total_km_rodado_vida, dados.maior_sulco_aferido_vida, dados.menor_sulco_aferido_vida, dados.sulco_gasto,
         dados.km_por_mm_vida, dados.valor_por_km_vida, dados.sulco_restante, dados.vida_analisada_pneu
order by case
             when (((dados.total_km_rodado_vida > (0)::numeric) and (dados.total_dias_ativo > 0)) and
                   ((dados.total_km_rodado_vida / (dados.total_dias_ativo)::numeric) > (0)::numeric)) then (
                     (((dados.km_por_mm_vida * dados.sulco_restante) /
                       ((dados.total_km_rodado_vida / (dados.total_dias_ativo)::numeric))::double precision))::integer +
                     ('NOW'::text)::date)
             else null::date
             end;

create or replace function func_afericao_insert_afericao(f_cod_unidade bigint,
                                                         f_data_hora timestamp with time zone,
                                                         f_cpf_aferidor bigint,
                                                         f_tempo_realizacao bigint,
                                                         f_tipo_medicao_coletada varchar(255),
                                                         f_tipo_processo_coleta varchar(255),
                                                         f_forma_coleta_dados text,
                                                         f_cod_veiculo bigint,
                                                         f_km_veiculo bigint)
    returns bigint

    language plpgsql
    security definer
as
$$
declare
    v_cod_tipo_veiculo      bigint := (select v.cod_tipo
                                       from veiculo_data v
                                       where v.codigo = f_cod_veiculo);
    v_cod_diagrama_veiculo  bigint := (select vt.cod_diagrama
                                       from veiculo_tipo vt
                                       where vt.codigo = v_cod_tipo_veiculo);
    v_cod_afericao          bigint;
    v_cod_afericao_inserida bigint;
    v_km_final              bigint;


begin
    v_cod_afericao := (select nextval(pg_get_serial_sequence('afericao_data', 'codigo')));

    if f_cod_veiculo is not null
    then
        v_km_final := (select *
                       from func_veiculo_update_km_atual(f_cod_unidade,
                                                         f_cod_veiculo,
                                                         f_km_veiculo,
                                                         v_cod_afericao,
                                                         'AFERICAO',
                                                         true,
                                                         f_data_hora));
    end if;

    -- realiza inserção da aferição.
    insert into afericao_data(codigo,
                              data_hora,
                              cpf_aferidor,
                              km_veiculo,
                              tempo_realizacao,
                              tipo_medicao_coletada,
                              cod_unidade,
                              tipo_processo_coleta,
                              deletado,
                              data_hora_deletado,
                              pg_username_delecao,
                              cod_diagrama,
                              forma_coleta_dados,
                              cod_veiculo)
    values (v_cod_afericao,
            f_data_hora,
            f_cpf_aferidor,
            v_km_final,
            f_tempo_realizacao,
            f_tipo_medicao_coletada,
            f_cod_unidade,
            f_tipo_processo_coleta,
            false,
            null,
            null,
            v_cod_diagrama_veiculo,
            f_forma_coleta_dados,
            f_cod_veiculo)
    returning codigo into v_cod_afericao_inserida;

    if (v_cod_afericao_inserida <= 0)
    then
        perform throw_generic_error('Erro ao inserir aferição');
    end if;

    return v_cod_afericao_inserida;
end
$$;

create or replace function suporte.func_veiculo_deleta_veiculo(f_cod_unidade bigint,
                                                               f_placa varchar(255),
                                                               f_motivo_delecao text,
                                                               out dependencias_deletadas text)
    returns text
    language plpgsql
    security definer
as
$$
declare
    v_codigo_loop                   bigint;
    v_lista_cod_afericao_placa      bigint[];
    v_lista_cod_check_placa         bigint[];
    v_lista_cod_prolog_deletado_cos bigint[];
    v_nome_empresa                  varchar(255) := (select e.nome
                                                     from empresa e
                                                     where e.codigo =
                                                           (select u.cod_empresa
                                                            from unidade u
                                                            where u.codigo = f_cod_unidade));
    v_nome_unidade                  varchar(255) := (select u.nome
                                                     from unidade u
                                                     where u.codigo = f_cod_unidade);
    v_cod_veiculo                   bigint       := (select codigo
                                                     from veiculo v
                                                     where v.placa = f_placa
                                                       and v.cod_unidade = f_cod_unidade);
begin
    perform suporte.func_historico_salva_execucao();

    perform func_garante_unidade_existe(f_cod_unidade);

    perform func_garante_veiculo_existe(f_cod_unidade, f_placa);

    -- Verifica se veiculo possui pneus aplicados.
    if exists(select vp.cod_pneu from veiculo_pneu vp where vp.placa = f_placa and vp.cod_unidade = f_cod_unidade)
    then
        raise exception 'Erro! A Placa: % possui pneus aplicados. Favor removê-los', f_placa;
    end if;

    -- Verifica se possui acoplamento.
    if EXISTS(select vd.codigo
              from veiculo_data vd
              where vd.placa = f_placa
                and vd.cod_unidade = f_cod_unidade
                and vd.acoplado is true)
    then
        raise exception 'Erro! A Placa: % possui acoplamentos. Favor removê-los', f_placa;
    end if;

    -- Verifica se placa possui aferição. Optamos por usar _DATA para garantir que tudo será deletado.
    if exists(select a.codigo from afericao_data a where a.cod_veiculo = v_cod_veiculo)
    then
        -- Coletamos todos os cod_afericao que a placa possui.
        select array_agg(a.codigo)
        from afericao_data a
        where a.cod_veiculo = v_cod_veiculo
        into v_lista_cod_afericao_placa;

        -- Deletamos aferição em afericao_manutencao_data, caso não esteja deletada.
        update afericao_manutencao_data
        set deletado            = true,
            data_hora_deletado  = now(),
            pg_username_delecao = session_user,
            motivo_delecao      = f_motivo_delecao
        where deletado = false
          and cod_afericao = any (v_lista_cod_afericao_placa);

        -- Deletamos aferição em afericao_valores_data, caso não esteja deletada.
        update afericao_valores_data
        set deletado            = true,
            data_hora_deletado  = now(),
            pg_username_delecao = session_user,
            motivo_delecao      = f_motivo_delecao
        where deletado = false
          and cod_afericao = any (v_lista_cod_afericao_placa);

        -- Deletamos aferição, caso não esteja deletada.
        update afericao_data
        set deletado            = true,
            data_hora_deletado  = now(),
            pg_username_delecao = session_user,
            motivo_delecao      = f_motivo_delecao
        where deletado = false
          and codigo = any (v_lista_cod_afericao_placa);
    end if;

    -- Verifica se placa possui checklist. Optamos por usar _DATA para garantir que tudo será deletado.
    if exists(select c.placa_veiculo from checklist_data c where c.deletado = false and c.placa_veiculo = f_placa)
    then
        -- Busca todos os códigos de checklists da placa.
        select array_agg(c.codigo)
        from checklist_data c
        where c.deletado = false
          and c.placa_veiculo = f_placa
        into v_lista_cod_check_placa;

        -- Deleta todos os checklists da placa. Usamos deleção lógica em conjunto com uma tabela de deleção específica.
        insert into checklist_delecao (cod_checklist,
                                       cod_colaborador,
                                       data_hora,
                                       acao_executada,
                                       origem_delecao,
                                       observacao,
                                       pg_username_delecao)
        select unnest(v_lista_cod_check_placa),
               null,
               now(),
               'DELETADO',
               'SUPORTE',
               f_motivo_delecao,
               session_user;

        update checklist_data set deletado = true where codigo = any (v_lista_cod_check_placa);

        -- Usamos, obrigatoriamente, a view checklist_ordem_servico para
        -- evitar de tentar deletar OSs que estão deletadas.
        if exists(select cos.codigo
                  from checklist_ordem_servico cos
                  where cos.cod_checklist = any (v_lista_cod_check_placa))
        then
            -- Deleta ordens de serviços dos checklists.
            update checklist_ordem_servico_data
            set deletado            = true,
                data_hora_deletado  = now(),
                pg_username_delecao = session_user,
                motivo_delecao      = f_motivo_delecao
            where deletado = false
              and cod_checklist = any (v_lista_cod_check_placa);

            -- Busca os codigo Prolog deletados nas Ordens de Serviços.
            select array_agg(codigo_prolog)
            from checklist_ordem_servico_data
            where cod_checklist = any (v_lista_cod_check_placa)
              and deletado is true
            into v_lista_cod_prolog_deletado_cos;

            -- Para cada código prolog deletado em cos, deletamos o referente na cosi.
            foreach v_codigo_loop in array v_lista_cod_prolog_deletado_cos
                loop
                    -- Deleta em cosi aqueles que foram deletados na cos.
                    update checklist_ordem_servico_itens_data
                    set deletado            = true,
                        data_hora_deletado  = now(),
                        pg_username_delecao = session_user,
                        motivo_delecao      = f_motivo_delecao
                    where deletado = false
                      and (cod_os, cod_unidade) = (select cos.codigo, cos.cod_unidade
                                                   from checklist_ordem_servico_data cos
                                                   where cos.codigo_prolog = v_codigo_loop);
                end loop;
        end if;
    end if;

    -- Verifica se a placa é integrada.
    if exists(select ivc.placa_veiculo_cadastro
              from integracao.veiculo_cadastrado ivc
              where ivc.cod_unidade_cadastro = f_cod_unidade
                and ivc.placa_veiculo_cadastro = f_placa)
    then
        -- Realiza a deleção da placa (não possuímos deleção lógica).
        delete
        from integracao.veiculo_cadastrado
        where cod_unidade_cadastro = f_cod_unidade
          and placa_veiculo_cadastro = f_placa;
    end if;

    -- Realiza deleção da placa.
    update veiculo_data
    set deletado            = true,
        data_hora_deletado  = now(),
        pg_username_delecao = session_user,
        motivo_delecao      = f_motivo_delecao
    where cod_unidade = f_cod_unidade
      and placa = f_placa
      and deletado = false;

    -- Mensagem de sucesso.
    select 'Veículo deletado junto com suas dependências. Veículo: '
               || f_placa
               || ', Empresa: '
               || v_nome_empresa
               || ', Unidade: '
               || v_nome_unidade
    into dependencias_deletadas;
end;
$$;

create or replace view estratificacao_os as
select cos.codigo                                                       as cod_os,
       realizador.nome                                                  as nome_realizador_checklist,
       v.placa                                                          as placa_veiculo,
       c.km_veiculo                                                     as km,
       c.data_hora_realizacao_tz_aplicado                               as data_hora,
       c.tipo                                                           as tipo_checklist,
       cp.codigo                                                        as cod_pergunta,
       cp.codigo_contexto                                               as cod_contexto_pergunta,
       cp.ordem                                                         as ordem_pergunta,
       cp.pergunta,
       cp.single_choice,
       null :: unknown                                                  as url_imagem,
       cap.prioridade,
       case cap.prioridade
           when 'CRITICA' :: text
               then 1
           when 'ALTA' :: text
               then 2
           when 'BAIXA' :: text
               then 3
           else null :: integer
           end                                                          as prioridade_ordem,
       cap.codigo                                                       as cod_alternativa,
       cap.codigo_contexto                                              as cod_contexto_alternativa,
       cap.alternativa,
       prio.prazo,
       crn.resposta_outros,
       v.cod_tipo,
       cos.cod_unidade,
       cos.status                                                       as status_os,
       cos.cod_checklist,
       tz_unidade(cos.cod_unidade)                                      as time_zone_unidade,
       cosi.status_resolucao                                            as status_item,
       mecanico.nome                                                    as nome_mecanico,
       cosi.cpf_mecanico,
       cosi.tempo_realizacao,
       cosi.data_hora_conserto at time zone tz_unidade(cos.cod_unidade) as data_hora_conserto,
       cosi.data_hora_inicio_resolucao                                  as data_hora_inicio_resolucao_utc,
       cosi.data_hora_fim_resolucao                                     as data_hora_fim_resolucao_utc,
       cosi.km                                                          as km_fechamento,
       cosi.qt_apontamentos,
       cosi.feedback_conserto,
       cosi.codigo
from checklist_data c
         join colaborador realizador
              on realizador.cpf = c.cpf_colaborador
         join veiculo v
              on v.codigo = c.cod_veiculo
         join checklist_ordem_servico cos
              on c.codigo = cos.cod_checklist
         join checklist_ordem_servico_itens cosi
              on cos.codigo = cosi.cod_os
                  and cos.cod_unidade = cosi.cod_unidade
         join checklist_perguntas cp
              on cp.cod_versao_checklist_modelo = c.cod_versao_checklist_modelo
                  and cosi.cod_contexto_pergunta = cp.codigo_contexto
         join checklist_alternativa_pergunta cap
              on cap.cod_pergunta = cp.codigo
                  and cosi.cod_contexto_alternativa = cap.codigo_contexto
         join checklist_alternativa_prioridade prio
              on prio.prioridade :: text = cap.prioridade :: text
         join checklist_respostas_nok crn
              on crn.cod_checklist = c.codigo
                  and crn.cod_alternativa = cap.codigo
         left join colaborador mecanico on mecanico.cpf = cosi.cpf_mecanico;

drop view checklist;
create or replace view checklist as
select c.cod_unidade                        as cod_unidade,
       c.cod_checklist_modelo               as cod_checklist_modelo,
       c.cod_versao_checklist_modelo        as cod_versao_checklist_modelo,
       c.codigo                             as codigo,
       c.data_hora                          as data_hora,
       c.data_hora_realizacao_tz_aplicado   as data_hora_realizacao_tz_aplicado,
       c.data_hora_importado_prolog         as data_hora_importado_prolog,
       c.cpf_colaborador                    as cpf_colaborador,
       c.cod_veiculo                        as cod_veiculo,
       c.tipo                               as tipo,
       c.tempo_realizacao                   as tempo_realizacao,
       c.km_veiculo                         as km_veiculo,
       c.observacao                         as observacao,
       c.data_hora_sincronizacao            as data_hora_sincronizacao,
       c.fonte_data_hora_realizacao         as fonte_data_hora_realizacao,
       c.versao_app_momento_realizacao      as versao_app_momento_realizacao,
       c.versao_app_momento_sincronizacao   as versao_app_momento_sincronizacao,
       c.device_id                          as device_id,
       c.device_imei                        as device_imei,
       c.device_uptime_realizacao_millis    as device_uptime_realizacao_millis,
       c.device_uptime_sincronizacao_millis as device_uptime_sincronizacao_millis,
       c.foi_offline                        as foi_offline,
       c.total_perguntas_ok                 as total_perguntas_ok,
       c.total_perguntas_nok                as total_perguntas_nok,
       c.total_alternativas_ok              as total_alternativas_ok,
       c.total_alternativas_nok             as total_alternativas_nok,
       c.total_midias_perguntas_ok          as total_midias_perguntas_ok,
       c.total_midias_alternativas_nok      as total_midias_alternativas_nok
from checklist_data c
where c.deletado = false;

alter table checklist_data
    drop column placa_veiculo;

alter table checklist_data
    add constraint fk_checklist_cod_veiculo
        foreign key (cod_veiculo) references veiculo_data (codigo);

alter table checklist_data
    add constraint unique_checklist unique
        (cod_unidade, cod_checklist_modelo, data_hora, cpf_colaborador, cod_veiculo, tipo,
         tempo_realizacao, km_veiculo, fonte_data_hora_realizacao, versao_app_momento_realizacao,
         device_id, device_imei, device_uptime_realizacao_millis);


-- Esta function não é mais necessária e o arquivo específico será deletado.
drop function func_checklist_get_cod_checklist_duplicado(f_cod_unidade_checklist bigint,
    f_cod_modelo_checklist bigint,
    f_data_hora_realizacao timestamp with time zone,
    f_cod_colaborador bigint,
    f_placa_veiculo text,
    f_tipo_checklist char,
    f_km_coletado bigint,
    f_tempo_realizacao bigint,
    f_fonte_data_hora_realizacao text,
    f_versao_app_momento_realizacao integer,
    f_device_id text,
    f_device_imei text,
    f_device_uptime_realizacao_millis bigint);

-- Corrige function de busca de checklists por colaborador.
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_GET_REALIZADOS_BY_COLABORADOR(F_COD_COLABORADOR BIGINT,
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
                COD_COLABORADOR               BIGINT,
                CPF_COLABORADOR               BIGINT,
                NOME_COLABORADOR              TEXT,
                COD_VEICULO                   BIGINT,
                PLACA_VEICULO                 TEXT,
                IDENTIFICADOR_FROTA           TEXT,
                TIPO_CHECKLIST                CHARACTER,
                TOTAL_PERGUNTAS_OK            SMALLINT,
                TOTAL_PERGUNTAS_NOK           SMALLINT,
                TOTAL_ALTERNATIVAS_OK         SMALLINT,
                TOTAL_ALTERNATIVAS_NOK        SMALLINT,
                TOTAL_MIDIAS_PERGUNTAS_OK     SMALLINT,
                TOTAL_MIDIAS_ALTERNATIVAS_NOK SMALLINT,
                TOTAL_NOK_BAIXA               SMALLINT,
                TOTAL_NOK_ALTA                SMALLINT,
                TOTAL_NOK_CRITICA             SMALLINT
            )
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    RETURN QUERY
        SELECT C.CODIGO                                             AS COD_CHECKLIST,
               C.COD_CHECKLIST_MODELO                               AS COD_CHECKLIST_MODELO,
               C.COD_VERSAO_CHECKLIST_MODELO                        AS COD_VERSAO_CHECKLIST_MODELO,
               C.DATA_HORA_REALIZACAO_TZ_APLICADO                   AS DATA_HORA_REALIZACAO,
               C.DATA_HORA_IMPORTADO_PROLOG AT TIME ZONE F_TIMEZONE AS DATA_HORA_IMPORTADO_PROLOG,
               C.KM_VEICULO                                         AS KM_VEICULO_MOMENTO_REALIZACAO,
               C.TEMPO_REALIZACAO                                   AS DURACAO_REALIZACAO_MILLIS,
               CO.CODIGO                                            AS COD_COLABORADOR,
               C.CPF_COLABORADOR                                    AS CPF_COLABORADOR,
               CO.NOME :: TEXT                                      AS NOME_COLABORADOR,
               V.CODIGO                                             AS COD_VEICULO,
               V.PLACA :: TEXT                                      AS PLACA_VEICULO,
               V.IDENTIFICADOR_FROTA :: TEXT                        AS IDENTIFICADOR_FROTA,
               C.TIPO                                               AS TIPO_CHECKLIST,
               C.TOTAL_PERGUNTAS_OK                                 AS TOTAL_PERGUNTAS_OK,
               C.TOTAL_PERGUNTAS_NOK                                AS TOTAL_PERGUNTAS_NOK,
               C.TOTAL_ALTERNATIVAS_OK                              AS TOTAL_ALTERNATIVAS_OK,
               C.TOTAL_ALTERNATIVAS_NOK                             AS TOTAL_ALTERNATIVAS_NOK,
               C.TOTAL_MIDIAS_PERGUNTAS_OK                          AS TOTAL_MIDIAS_PERGUNTAS_OK,
               C.TOTAL_MIDIAS_ALTERNATIVAS_NOK                      AS TOTAL_MIDIAS_ALTERNATIVAS_NOK,
               (SELECT COUNT(*)
                FROM CHECKLIST_RESPOSTAS_NOK CRN
                         JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
                              ON CRN.COD_ALTERNATIVA = CAP.CODIGO
                WHERE CRN.COD_CHECKLIST = C.CODIGO
                  AND CAP.PRIORIDADE = 'BAIXA') :: SMALLINT         AS TOTAL_BAIXA,
               (SELECT COUNT(*)
                FROM CHECKLIST_RESPOSTAS_NOK CRN
                         JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
                              ON CRN.COD_ALTERNATIVA = CAP.CODIGO
                WHERE CRN.COD_CHECKLIST = C.CODIGO
                  AND CAP.PRIORIDADE = 'ALTA') :: SMALLINT          AS TOTAL_ALTA,
               (SELECT COUNT(*)
                FROM CHECKLIST_RESPOSTAS_NOK CRN
                         JOIN CHECKLIST_ALTERNATIVA_PERGUNTA CAP
                              ON CRN.COD_ALTERNATIVA = CAP.CODIGO
                WHERE CRN.COD_CHECKLIST = C.CODIGO
                  AND CAP.PRIORIDADE = 'CRITICA') :: SMALLINT       AS TOTAL_CRITICA
        FROM CHECKLIST C
                 JOIN COLABORADOR CO
                      ON CO.CPF = C.CPF_COLABORADOR
                 JOIN VEICULO V
                      ON V.CODIGO = C.COD_VEICULO
        WHERE CO.CODIGO = F_COD_COLABORADOR
          AND C.DATA_HORA_REALIZACAO_TZ_APLICADO :: DATE >= F_DATA_INICIAL
          AND C.DATA_HORA_REALIZACAO_TZ_APLICADO :: DATE <= F_DATA_FINAL
        ORDER BY C.DATA_HORA_SINCRONIZACAO DESC
        LIMIT F_LIMIT OFFSET F_OFFSET;
END;
$$;

CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_GET_REALIZADOS_BY_COLABORADOR_DEPRECATED(F_CPF_COLABORADOR BIGINT,
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
                TIPO_CHECKLIST                CHARACTER,
                NOME_COLABORADOR              TEXT,
                TOTAL_ITENS_OK                SMALLINT,
                TOTAL_ITENS_NOK               SMALLINT,
                OBSERVACAO                    TEXT
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    F_HAS_DATA_INICIAL INTEGER := CASE WHEN F_DATA_INICIAL IS NULL THEN 1 ELSE 0 END;
    F_HAS_DATA_FINAL   INTEGER := CASE WHEN F_DATA_FINAL IS NULL THEN 1 ELSE 0 END;
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
                 JOIN VEICULO V
                      ON V.CODIGO = C.COD_VEICULO
        WHERE C.CPF_COLABORADOR = F_CPF_COLABORADOR
          AND (F_HAS_DATA_INICIAL = 1 OR (C.DATA_HORA AT TIME ZONE F_TIMEZONE) :: DATE >= F_DATA_INICIAL)
          AND (F_HAS_DATA_FINAL = 1 OR (C.DATA_HORA AT TIME ZONE F_TIMEZONE) :: DATE <= F_DATA_FINAL)
        ORDER BY C.DATA_HORA DESC
        LIMIT F_LIMIT OFFSET F_OFFSET;
END;
$$;


-- Corrige problema na formatação da data e na ordem dos parâmetros da mensagem de erro.
-- Isso resolve a PL-3583.
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
    v_error_message                              text            := E'Erro! A data de resolução "%s" não pode ser anterior a data de abertura "%s" do item "%s".';
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
                perform throw_generic_error(format(
                        v_error_message,
                        format_with_tz(f_data_hora_inicio_resolucao, tz_unidade(f_cod_unidade), 'DD/MM/YYYY HH24:MI'),
                        format_with_tz(v_data_realizacao_checklist, tz_unidade(f_cod_unidade), 'DD/MM/YYYY HH24:MI'),
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

-- Remove usos de placa da tabela de checklist.
create or replace function func_checklist_relatorio_ultimo_checklist_realizado_placa(f_cod_unidades bigint[],
                                                                                     f_cod_tipos_veiculos bigint[])
    returns table
            (
                "UNIDADE DA PLACA"            TEXT,
                "PLACA"                       TEXT,
                "TIPO VEÍCULO"                TEXT,
                "KM ATUAL"                    TEXT,
                "KM COLETADO"                 TEXT,
                "MODELO ÚLTIMO CHECKLIST"     TEXT,
                "TIPO CHECKLIST"              TEXT,
                "CPF COLABORADOR"             TEXT,
                "COLABORADOR REALIZAÇÃO"      TEXT,
                "DATA/HORA ÚLTIMO CHECKLIST"  TEXT,
                "QTD DIAS SEM CHECKLIST"      TEXT,
                "TEMPO REALIZAÇÃO(SEGUNDOS)"  TEXT,
                "TOTAL PERGUNTAS"             TEXT,
                "TOTAL NOK"                   TEXT,
                "OBSERVAÇÃO ÚLTIMO CHECKLIST" TEXT
            )
    language sql
as
$$
with geracao_dados as (select distinct on (
    c.cod_veiculo) u.nome                                         as nome_unidade,
                   v.placa                                        as placa,
                   vt.nome                                        as nome_tipo_veiculo,
                   v.km                                           as km_atual,
                   c.km_veiculo                                   as km_coletado,
                   cm.nome                                        as nome_modelo,
                   case
                       when (c.tipo = 'S')
                           then 'SAÍDA'
                       else
                           case
                               when (c.tipo = 'R')
                                   then 'RETORNO'
                               end
                       end                                        as tipo_checklist,
                   lpad(c.cpf_colaborador::text, 11, '0')         as cpf_colaborador,
                   co.nome                                        as nome_colaborador,
                   format_timestamp((max(c.data_hora)::timestamp),
                                    'DD/MM/YYYY HH24:MI')         as data_hora_checklist,
                   c.observacao                                   as observacao,
                   extract(day from (now() - c.data_hora))        as qtd_dias_sem_checklist,
                   c.tempo_realizacao                             as tempo_realizacao,
                   (c.total_perguntas_ok + c.total_perguntas_nok) as total_perguntas,
                   c.total_perguntas_nok                          as total_perguntas_nok
                       from checklist c
                                join checklist_modelo cm on c.cod_checklist_modelo = cm.codigo
                                join colaborador co on c.cpf_colaborador = co.cpf
                                join veiculo v on c.cod_veiculo = v.codigo
                                join veiculo_tipo vt on v.cod_empresa = vt.cod_empresa
                           and v.cod_tipo = vt.codigo
                                join unidade u on c.cod_unidade = u.codigo
                       where v.cod_unidade = any (f_cod_unidades)
                         and v.cod_tipo = any (f_cod_tipos_veiculos)
                       group by c.data_hora,
                                u.nome,
                                c.cod_veiculo,
                                v.placa,
                                vt.nome,
                                v.km,
                                c.km_veiculo,
                                cm.nome,
                                c.tipo,
                                c.cpf_colaborador,
                                co.nome,
                                c.tempo_realizacao,
                                (c.total_perguntas_ok + c.total_perguntas_nok),
                                c.total_perguntas_nok,
                                c.observacao
                       order by c.cod_veiculo, c.data_hora desc)

select gd.nome_unidade::text,
       gd.placa::text,
       gd.nome_tipo_veiculo::text,
       gd.km_atual::text,
       gd.km_coletado::text,
       gd.nome_modelo::text,
       gd.tipo_checklist::text,
       gd.cpf_colaborador::text,
       gd.nome_colaborador::text,
       gd.data_hora_checklist::text,
       gd.qtd_dias_sem_checklist::text,
       gd.tempo_realizacao::text,
       gd.total_perguntas::text,
       gd.total_perguntas_nok::text,
       gd.observacao::text
from geracao_dados gd
order by gd.qtd_dias_sem_checklist desc, gd.nome_unidade, gd.placa;
$$;

-- Podemos dropar esta function pois uma nova versão sem receber o TZ foi feita para o v3 com Spring.
DROP FUNCTION FUNC_AFERICAO_GET_AFERICOES_PLACAS_PAGINADA(F_COD_UNIDADE BIGINT, F_COD_TIPO_VEICULO BIGINT,
    F_PLACA_VEICULO TEXT, F_DATA_INICIAL DATE,
    F_DATA_FINAL DATE, F_LIMIT BIGINT,
    F_OFFSET BIGINT,
    F_TZ_UNIDADE TEXT);