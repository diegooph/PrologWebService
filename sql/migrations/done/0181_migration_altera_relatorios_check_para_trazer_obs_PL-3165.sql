drop function if exists func_checklist_relatorio_resumo_realizados(f_cod_unidades bigint[],
    f_placa_veiculo text,
    f_data_inicial date,
    f_data_final date);
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



drop function if exists func_checklist_relatorio_ultimo_checklist_realizado_placa(f_cod_unidades bigint[],
    f_cod_tipos_veiculos bigint[]);
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
    c.placa_veiculo) u.nome                                         as nome_unidade,
                     c.placa_veiculo                                as placa,
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
                                join veiculo v on c.placa_veiculo = v.placa
                                join veiculo_tipo vt on v.cod_empresa = vt.cod_empresa
                           and v.cod_tipo = vt.codigo
                                join unidade u on c.cod_unidade = u.codigo
                       where v.cod_unidade = any (f_cod_unidades)
                         and v.cod_tipo = any (f_cod_tipos_veiculos)
                       group by c.data_hora,
                                u.nome,
                                c.placa_veiculo,
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
                       order by c.placa_veiculo, c.data_hora desc)

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