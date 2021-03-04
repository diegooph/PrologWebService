-- Sobre:
-- Busca um resumo dos checklists que foram realizados e que se enquadram nos parâmetros de filtro utilizados.
--
-- Parâmetros:
-- F_COD_UNIDADES: Um array com os códigos das unidades das quais queremos buscar os checklists realizados.
-- F_PLACA_VEICULO: A placa do veículo do qual queremos buscar os checklists realizados.
--                  Para não filtrar por nenhuma placa específica, envie '%' (sem as aspas simples).
-- F_DATA_INICIAL: Data inicial pela qual se quer filtrar os dados. Apenas checks realizados nessa data ou após serão
--                 buscados.
-- F_DATA_FINAL: Data final pela qual se quer filtrar os dados. Apenas checks realizados nessa data ou antes serão
--               buscados.
--
-- Todos os parâmetros são obrigatórios.
--
-- Histórico:
-- 2019-08-01 -> Adicionado código do checklist ao retorno da function (luizfp).
-- 2019-09-10 -> Adiciona cpf do colaborador no retorno da function (natan_rotta).
-- 2020-03-03 -> Atualização de arquivo e documentação (wvinim - PL-2494).
-- 2020-03-25 -> Adiciona a coluna de tipo de veículo (wvinim - PL-2546).
-- 2020-07-07 -> Adição das informações de totais de mídias coletadas (wvinim - PL-2705).
-- 2020-07-13 -> Atualização de arquivo e documentação (wvinim - PL-2824).
-- 2020-08-27 -> Adiciona equipe e cargo do colaborador (luiz_fp - PL-3106).
-- 2020-09-22 -> Adiciona coluna observação (gustavocnp95 - PL-3165)
create or replace function func_checklist_relatorio_resumo_realizados(f_cod_unidades bigint[],
                                                                      f_placa_veiculo text,
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
       c.placa_veiculo                                        as placa_veiculo,
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
         join veiculo v on v.placa = c.placa_veiculo
         join veiculo_tipo vt on vt.codigo = v.cod_tipo
where c.cod_unidade = any (f_cod_unidades)
  and c.data_hora_realizacao_tz_aplicado :: date >= f_data_inicial
  and c.data_hora_realizacao_tz_aplicado :: date <= f_data_final
  and (f_placa_veiculo = '%' or c.placa_veiculo like f_placa_veiculo)
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
         c.placa_veiculo,
         vt.nome,
         c.km_veiculo,
         c.observacao,
         c.tempo_realizacao,
         c.tipo
order by u.nome,
         c.data_hora_sincronizacao desc;
$$;