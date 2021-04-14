drop view estratificacao_os;
create or replace view estratificacao_os as
select cos.codigo                                                       as cod_os,
       realizador.nome                                                  as nome_realizador_checklist,
       v.codigo                                                         as cod_veiculo,
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

drop function func_checklist_os_relatorio_estratificacao_os(f_cod_unidades bigint[],
    f_placa_veiculo text,
    f_status_os text,
    f_status_item text,
    f_data_inicial_abertura date,
    f_data_final_abertura date,
    f_data_inicial_resolucao date,
    f_data_final_resolucao date);
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_OS_RELATORIO_ESTRATIFICACAO_OS(F_COD_UNIDADES BIGINT[],
                                                                         F_STATUS_OS TEXT,
                                                                         F_STATUS_ITEM TEXT,
                                                                         F_DATA_INICIAL_ABERTURA DATE,
                                                                         F_DATA_FINAL_ABERTURA DATE,
                                                                         F_DATA_INICIAL_RESOLUCAO DATE,
                                                                         F_DATA_FINAL_RESOLUCAO DATE)
    RETURNS TABLE
            (
                UNIDADE                        TEXT,
                "CÓDICO OS"                    BIGINT,
                "CÓDICO ITEM OS"               BIGINT,
                "ABERTURA OS"                  TEXT,
                "DATA LIMITE CONSERTO"         TEXT,
                "STATUS OS"                    TEXT,
                "PLACA"                        TEXT,
                "TIPO DE VEÍCULO"              TEXT,
                "TIPO DO CHECKLIST"            TEXT,
                "MODELO DO CHECKLIST"          TEXT,
                "PERGUNTA"                     TEXT,
                "ALTERNATIVA"                  TEXT,
                "QTD APONTAMENTOS"             INTEGER,
                "PRIORIDADE"                   TEXT,
                "PRAZO EM HORAS"               INTEGER,
                "DESCRIÇÃO"                    TEXT,
                "STATUS ITEM"                  TEXT,
                "DATA INÍCIO RESOLUÇÃO"        TEXT,
                "DATA FIM RESOLUÇÃO"           TEXT,
                "DATA RESOLIVDO PROLOG"        TEXT,
                "MECÂNICO"                     TEXT,
                "DESCRIÇÃO CONSERTO"           TEXT,
                "TEMPO DE CONSERTO EM MINUTOS" BIGINT,
                "KM ABERTURA"                  BIGINT,
                "KM FECHAMENTO"                BIGINT,
                "KM PERCORRIDO"                TEXT,
                "MOTORISTA"                    TEXT
            )
    LANGUAGE SQL
AS
$$
SELECT U.NOME                                                                                   AS NOME_UNIDADE,
       EO.COD_OS                                                                                AS CODIGO_OS,
       EO.CODIGO                                                                                AS COD_ITEM_OS,
       FORMAT_TIMESTAMP(EO.DATA_HORA, 'DD/MM/YYYY HH24:MI')                                     AS ABERTURA_OS,
       FORMAT_TIMESTAMP(EO.DATA_HORA + (EO.PRAZO || ' HOUR') :: INTERVAL, 'DD/MM/YYYY HH24:MI') AS DATA_LIMITE_CONSERTO,
       (CASE
            WHEN STATUS_OS = 'A'
                THEN 'ABERTA'
            ELSE 'FECHADA' END)                                                                 AS STATUS_OS,
       EO.PLACA_VEICULO                                                                         AS PLACA,
       VT.NOME                                                                                  AS TIPO_VEICULO,
       CASE
           WHEN TIPO_CHECKLIST = 'S'
               THEN 'SAÍDA'
           ELSE 'RETORNO' END                                                                   AS TIPO_CHECKLIST,
       CM.NOME                                                                                  AS CHECKLIST_MODELO,
       PERGUNTA                                                                                 AS PERGUNTA,
       ALTERNATIVA                                                                              AS ALTERNATIVA,
       QT_APONTAMENTOS                                                                          AS QTD_APONTAMENTOS,
       PRIORIDADE                                                                               AS PRIORIDADE,
       PRAZO                                                                                    AS PRAZO_EM_HORAS,
       RESPOSTA_OUTROS                                                                          AS DESCRICAO,
       CASE
           WHEN STATUS_ITEM = 'P'
               THEN 'PENDENTE'
           ELSE 'RESOLVIDO' END                                                                 AS STATUS_ITEM,
       FORMAT_TIMESTAMP(
               DATA_HORA_INICIO_RESOLUCAO_UTC AT TIME ZONE TIME_ZONE_UNIDADE,
               'DD/MM/YYYY HH24:MI',
               '-')                                                                             AS DATA_INICIO_RESOLUCAO,
       FORMAT_TIMESTAMP(
               DATA_HORA_FIM_RESOLUCAO_UTC AT TIME ZONE TIME_ZONE_UNIDADE,
               'DD/MM/YYYY HH24:MI', '-')                                                       AS DATA_FIM_RESOLUCAO,
       FORMAT_TIMESTAMP(DATA_HORA_CONSERTO, 'DD/MM/YYYY HH24:MI')                               AS DATA_RESOLVIDO_PROLOG,
       NOME_MECANICO                                                                            AS MECANICO,
       FEEDBACK_CONSERTO                                                                        AS DESCRICAO_CONSERTO,
       EO.TEMPO_REALIZACAO / 1000 / 60                                                          AS TEMPO_CONSERTO_MINUTOS,
       KM                                                                                       AS KM_ABERTURA,
       KM_FECHAMENTO                                                                            AS KM_FECHAMENTO,
       COALESCE((KM_FECHAMENTO - KM) :: TEXT, '-')                                              AS KM_PERCORRIDO,
       NOME_REALIZADOR_CHECKLIST                                                                AS MOTORISTA
FROM ESTRATIFICACAO_OS EO
         JOIN UNIDADE U
              ON EO.COD_UNIDADE = U.CODIGO
         JOIN VEICULO_TIPO VT
              ON VT.CODIGO = EO.COD_TIPO
         JOIN CHECKLIST C
              ON C.CODIGO = EO.COD_CHECKLIST
         JOIN CHECKLIST_MODELO CM
              ON CM.CODIGO = C.COD_CHECKLIST_MODELO
WHERE EO.COD_UNIDADE = ANY (F_COD_UNIDADES)
  AND EO.STATUS_OS LIKE F_STATUS_OS
  AND EO.STATUS_ITEM LIKE F_STATUS_ITEM
  AND CASE
    -- O usuário pode filtrar tanto por início e fim de abertura ou por início e fim de resolução ou, ainda,
    -- por ambos.
          WHEN (F_DATA_INICIAL_ABERTURA,
                F_DATA_FINAL_ABERTURA,
                F_DATA_INICIAL_RESOLUCAO,
                F_DATA_FINAL_RESOLUCAO) IS NOT NULL
              THEN (
                  EO.DATA_HORA :: DATE BETWEEN F_DATA_INICIAL_ABERTURA AND F_DATA_FINAL_ABERTURA
                  AND
                  EO.DATA_HORA_CONSERTO :: DATE BETWEEN F_DATA_INICIAL_RESOLUCAO AND F_DATA_FINAL_RESOLUCAO)
          WHEN (F_DATA_INICIAL_ABERTURA,
                F_DATA_FINAL_ABERTURA) IS NOT NULL
              THEN
              EO.DATA_HORA :: DATE BETWEEN F_DATA_INICIAL_ABERTURA AND F_DATA_FINAL_ABERTURA
          WHEN (F_DATA_INICIAL_RESOLUCAO,
                F_DATA_FINAL_RESOLUCAO) IS NOT NULL
              THEN
              EO.DATA_HORA_CONSERTO :: DATE BETWEEN F_DATA_INICIAL_RESOLUCAO AND F_DATA_FINAL_RESOLUCAO

    -- Se não entrar em nenhuma condição conhecida, retornamos FALSE para o relatório não retornar dado nenhum.
          ELSE FALSE END
ORDER BY U.NOME, EO.COD_OS, EO.PRAZO;
$$;

drop function func_checklist_os_relatorio_placas_maior_qtd_itens_abertos(bigint[], integer);
create or replace function
    func_checklist_os_relatorio_placas_maior_qtd_itens_abertos(f_cod_unidades bigint[],
                                                               f_total_placas_para_buscar integer)
    returns table
            (
                nome_unidade                      text,
                placa                             text,
                quantidade_itens_abertos          bigint,
                quantidade_itens_criticos_abertos bigint
            )
    language plpgsql
as
$$
declare
    status_itens_abertos char    := 'P';
    prioridade_critica   varchar := 'CRITICA';
begin
    return query
        with placas as (
            select v.placa                   as placa_veiculo,
                   count(cosi.codigo)        as quantidade_itens_abertos,
                   count(case
                             when cap.prioridade = prioridade_critica
                                 then 1 end) as quantidade_itens_criticos_abertos
            from checklist_ordem_servico_itens cosi
                     join checklist_ordem_servico cos
                          on cosi.cod_os = cos.codigo
                              and cosi.cod_unidade = cos.cod_unidade
                              -- Este filtro é importante! Ele nos previne de selecionar muitas OSs, filtrando aqui com
                              -- o index que existe na COS. Vindo menos linhas aqui, menos linhas também são
                              -- trazidas no join com a CAP abaixo. Assim, preveninos de usar disco.
                              and cos.cod_unidade = any (f_cod_unidades)
                     join checklist_alternativa_pergunta cap
                          on cosi.cod_alternativa_primeiro_apontamento = cap.codigo
                     join checklist c
                          on c.codigo = cos.cod_checklist
                     join veiculo v on c.cod_veiculo = v.codigo
            where c.cod_unidade = any (f_cod_unidades)
              and cosi.status_resolucao = status_itens_abertos
            group by v.placa
            order by quantidade_itens_abertos desc,
                     v.placa
            limit f_total_placas_para_buscar
        )

        select u.nome::text as nome_unidade,
               p.placa_veiculo::text,
               p.quantidade_itens_abertos,
               p.quantidade_itens_criticos_abertos
        from placas p
                 join veiculo v on v.placa = p.placa_veiculo
                 join unidade u on v.cod_unidade = u.codigo;
end;
$$;

-- #####################################################################################################################
-- #####################################################################################################################
-- #####################################################################################################################
-- #####################################################################################################################
drop function func_checklist_relatorio_resumo_realizados(f_cod_unidades bigint[],
    f_placa_veiculo text,
    f_data_inicial date,
    f_data_final date);
create or replace function func_checklist_relatorio_resumo_realizados(f_cod_unidades bigint[],
                                                                      f_data_inicial date,
                                                                      f_data_final date)
    returns table
            (
                "UNIDADE"                     text,
                "MODELO CHECKLIST"            text,
                "CÓDIGO CHECKLIST"            bigint,
                "DATA REALIZAÇÃO"             text,
                "DATA IMPORTADO"              text,
                "COLABORADOR"                 text,
                "CPF"                         text,
                "EQUIPE"                      text,
                "CARGO"                       text,
                "PLACA"                       text,
                "TIPO DE VEÍCULO"             text,
                "KM"                          bigint,
                "TEMPO REALIZAÇÃO (SEGUNDOS)" bigint,
                "TIPO"                        text,
                "TOTAL DE PERGUNTAS"          smallint,
                "TOTAL NOK"                   bigint,
                "TOTAL IMAGENS PERGUNTAS"     smallint,
                "TOTAL IMAGENS ALTERNATIVAS"  smallint,
                "PRIORIDADE BAIXA"            bigint,
                "PRIORIDADE ALTA"             bigint,
                "PRIORIDADE CRÍTICA"          bigint,
                "OBSERVAÇÃO"                  text
            )
    language sql
as
$$
select u.nome                                                 as nome_unidade,
       cm.nome                                                as nome_modelo,
       c.codigo                                               as cod_checklist,
       format_timestamp(
               c.data_hora_realizacao_tz_aplicado,
               'DD/MM/YYYY HH24:MI')                          as data_hora_realizacao,
       format_with_tz(
               c.data_hora_importado_prolog,
               tz_unidade(c.cod_unidade),
               'DD/MM/YYYY HH24:MI',
               '-')                                           as data_hora_importado,
       co.nome                                                as nome_colaborador,
       lpad(co.cpf :: text, 11, '0')                          as cpf_colaborador,
       e.nome                                                 as equipe_colaborador,
       f.nome                                                 as cargo_colaborador,
       v.placa::text                                          as placa_veiculo,
       vt.nome                                                as tipo_veiculo,
       c.km_veiculo                                           as km_veiculo,
       c.tempo_realizacao / 1000                              as tempo_realizacao_segundos,
       f_if(c.tipo = 'S', 'Saída' :: text, 'Retorno' :: text) as tipo_checklist,
       c.total_perguntas_ok + c.total_perguntas_nok           as total_perguntas,
       (select count(*)
        from checklist_respostas_nok crn
        where crn.cod_checklist = c.codigo)                   as total_nok,
       coalesce(c.total_midias_perguntas_ok, 0)::smallint     as total_midias_perguntas,
       coalesce(c.total_midias_alternativas_nok, 0)::smallint as total_midias_alternativas,
       (select count(*)
        from checklist_respostas_nok crn
                 join checklist_alternativa_pergunta cap
                      on crn.cod_alternativa = cap.codigo
        where crn.cod_checklist = c.codigo
          and cap.prioridade = 'BAIXA')                       as total_baixa,
       (select count(*)
        from checklist_respostas_nok crn
                 join checklist_alternativa_pergunta cap
                      on crn.cod_alternativa = cap.codigo
        where crn.cod_checklist = c.codigo
          and cap.prioridade = 'ALTA')                        as total_alta,
       (select count(*)
        from checklist_respostas_nok crn
                 join checklist_alternativa_pergunta cap
                      on crn.cod_alternativa = cap.codigo
        where crn.cod_checklist = c.codigo
          and cap.prioridade = 'CRITICA')                     as total_critica,
       c.observacao                                           as observacao
from checklist c
         join checklist_perguntas cp
              on cp.cod_versao_checklist_modelo = c.cod_versao_checklist_modelo
         join colaborador co
              on c.cpf_colaborador = co.cpf
         join equipe e
              on co.cod_equipe = e.codigo
         join funcao f
              on co.cod_funcao = f.codigo
         join unidade u
              on c.cod_unidade = u.codigo
         join checklist_modelo cm on cm.codigo = c.cod_checklist_modelo
         join veiculo v on v.codigo = c.cod_veiculo
         join veiculo_tipo vt on vt.codigo = v.cod_tipo
where c.cod_unidade = any (f_cod_unidades)
  and c.data_hora_realizacao_tz_aplicado :: date >= f_data_inicial
  and c.data_hora_realizacao_tz_aplicado :: date <= f_data_final
group by c.codigo,
         cm.nome,
         c.total_perguntas_ok,
         c.total_midias_perguntas_ok,
         c.total_midias_alternativas_nok,
         c.total_perguntas_nok,
         u.codigo,
         u.nome,
         co.nome,
         co.cpf,
         e.nome,
         f.nome,
         c.data_hora,
         c.data_hora_realizacao_tz_aplicado,
         c.data_hora_importado_prolog,
         c.data_hora_sincronizacao,
         c.cod_unidade,
         v.placa,
         vt.nome,
         c.km_veiculo,
         c.observacao,
         c.tempo_realizacao,
         c.tipo
order by u.nome,
         c.data_hora_sincronizacao desc;
$$;