drop function if exists func_checklist_relatorio_ultimo_checklist_realizado_placa(f_cod_unidades bigint[],
    f_cod_tipos_veiculos bigint[]);

create or replace function func_checklist_relatorio_ultimo_checklist_realizado_placa(f_cod_unidades bigint[],
                                                                                     f_cod_tipos_veiculos bigint[])
    returns table
            (
                "UNIDADE DA PLACA"            TEXT,
                "PLACA"                       TEXT,
                "TIPO VEÍCULO"                TEXT,
                "STATUS"                      TEXT,
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
                   case
                       when (v.status_ativo)
                           then 'ATIVO'
                       else
                           'INATIVO'
                       end                                        as status,
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
                                join checklist_modelo cm
                                     on c.cod_checklist_modelo = cm.codigo
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
                                c.observacao,
                                v.status_ativo
                       order by c.cod_veiculo, c.data_hora desc)

select gd.nome_unidade::text,
       gd.placa::text,
       gd.nome_tipo_veiculo::text,
       gd.status::text,
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