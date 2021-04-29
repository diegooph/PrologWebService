drop view afericao_manutencao;
create or replace view afericao_manutencao
            (cod_afericao, cod_pneu, cod_unidade, tipo_servico, data_hora_resolucao, cpf_mecanico, qt_apontamentos,
             psi_apos_conserto, km_momento_conserto, cod_alternativa, cod_pneu_inserido, codigo,
             cod_processo_movimentacao, tempo_realizacao_millis, fechado_automaticamente_movimentacao,
             fechado_automaticamente_integracao, fechado_automaticamente_afericao, cod_afericao_fechamento_automatico,
             forma_coleta_dados_fechamento)
as
select am.cod_afericao,
       am.cod_pneu,
       am.cod_unidade,
       am.tipo_servico,
       am.data_hora_resolucao,
       am.cpf_mecanico,
       am.qt_apontamentos,
       am.psi_apos_conserto,
       am.km_momento_conserto,
       am.cod_alternativa,
       am.cod_pneu_inserido,
       am.codigo,
       am.cod_processo_movimentacao,
       am.tempo_realizacao_millis,
       am.fechado_automaticamente_movimentacao,
       am.fechado_automaticamente_integracao,
       am.fechado_automaticamente_afericao,
       am.cod_afericao_fechamento_automatico,
       am.forma_coleta_dados_fechamento
from afericao_manutencao_data am
where am.deletado = false;

drop function func_pneu_relatorio_extrato_servicos_fechados(bigint[], date, date);
create function func_pneu_relatorio_extrato_servicos_fechados(f_cod_unidades bigint[], f_data_inicial date,
f_data_final date)
    returns table("UNIDADE DO SERVIÇO" text, "DATA AFERIÇÃO" text, "DATA RESOLUÇÃO" text, "HORAS PARA RESOLVER"
        double precision, "MINUTOS PARA RESOLVER" double precision, "PLACA" text, "IDENTIFICADOR FROTA" text,
        "KM AFERIÇÃO" bigint, "KM CONSERTO" bigint, "KM PERCORRIDO" bigint, "COD PNEU" character varying,
        "PRESSÃO RECOMENDADA" real, "PRESSÃO AFERIÇÃO" text, "DISPERSÃO RECOMENDADA X AFERIÇÃO" text, "PRESSÃO INSERIDA"
            text, "DISPERSÃO RECOMENDADA X INSERIDA" text, "POSIÇÃO PNEU ABERTURA SERVIÇO" text, "SERVIÇO" text,
        "MECÂNICO" text, "PROBLEMA APONTADO (INSPEÇÃO)" text, "FECHADO AUTOMATICAMENTE" text,
        "FORMA DE COLETA DOS DADOS" text)
    language sql
as
$$
select u.nome                                                                       as unidade_servico,
       to_char((a.data_hora at time zone tz_unidade(am.cod_unidade)),
               'DD/MM/YYYY HH24:MI:SS')                                             as data_hora_afericao,
       to_char((am.data_hora_resolucao at time zone tz_unidade(am.cod_unidade)),
               'DD/MM/YYYY HH24:MI:SS')                                             as data_hora_resolucao,
       trunc(extract(epoch from ((am.data_hora_resolucao) - (a.data_hora))) / 3600) as horas_resolucao,
       trunc(extract(epoch from ((am.data_hora_resolucao) - (a.data_hora))) / 60)   as minutos_resolucao,
       a.placa_veiculo                                                              as placa_veiculo,
       coalesce(v.identificador_frota, '-')                                         as identificador_frota,
       a.km_veiculo                                                                 as km_afericao,
       am.km_momento_conserto                                                       as km_momento_conserto,
       am.km_momento_conserto - a.km_veiculo                                        as km_percorrido,
       p.codigo_cliente                                                             as codigo_cliente_pneu,
       p.pressao_recomendada                                                        as pressao_recomendada_pneu,
       coalesce(replace(round(av.psi :: numeric, 2) :: text, '.', ','), '-')        as psi_afericao,
       coalesce(replace(round((((av.psi / p.pressao_recomendada) - 1) * 100) :: numeric, 2) || '%', '.', ','),
                '-')                                                                as dispersao_pressao_antes,
       coalesce(replace(round(am.psi_apos_conserto :: numeric, 2) :: text, '.', ','),
                '-')                                                                as psi_pos_conserto,
       coalesce(replace(round((((am.psi_apos_conserto / p.pressao_recomendada) - 1) * 100) :: numeric, 2) || '%', '.',
                        ','), '-')                                                  as dispersao_pressao_depois,
       coalesce(ppne.nomenclatura, '-')                                             as posicao,
       am.tipo_servico                                                              as tipo_servico,
       coalesce(initcap(c.nome), '-')                                               as nome_mecanico,
       coalesce(aa.alternativa, '-')                                                as problema_apontado,
       f_if(am.fechado_automaticamente_movimentacao or am.fechado_automaticamente_integracao or
            am.fechado_automaticamente_afericao, 'Sim' :: text, 'Não')              as tipo_fechamento,
       coalesce(afcd.status_legivel, '-')                                           as forma_coleta_dados_fechamento
from afericao_manutencao am
         join unidade u
              on am.cod_unidade = u.codigo
         join afericao_valores av
              on am.cod_unidade = av.cod_unidade
                  and am.cod_afericao = av.cod_afericao
                  and am.cod_pneu = av.cod_pneu
         join afericao a
              on a.codigo = av.cod_afericao
         left join colaborador c
                   on am.cpf_mecanico = c.cpf
         join pneu p
              on p.codigo = av.cod_pneu
         left join veiculo_pneu vp
                   on vp.cod_pneu = p.codigo
                       and vp.cod_unidade = p.cod_unidade
         left join afericao_alternativa_manutencao_inspecao aa
                   on aa.codigo = am.cod_alternativa
         left join veiculo v
                   on v.placa = vp.placa
         left join empresa e
                   on u.cod_empresa = e.codigo
         left join veiculo_tipo vt
                   on e.codigo = vt.cod_empresa
                       and vt.codigo = v.cod_tipo
         left join pneu_posicao_nomenclatura_empresa ppne
                   on ppne.cod_empresa = p.cod_empresa
                       and ppne.cod_diagrama = vt.cod_diagrama
                       and ppne.posicao_prolog = av.posicao
         left join types.afericao_forma_coleta_dados afcd
                   on afcd.forma_coleta_dados = am.forma_coleta_dados_fechamento
where av.cod_unidade = any (f_cod_unidades)
  and am.data_hora_resolucao is not null
  and (am.data_hora_resolucao at time zone tz_unidade(am.cod_unidade)) :: date >= f_data_inicial
  and (am.data_hora_resolucao at time zone tz_unidade(am.cod_unidade)) :: date <= f_data_final
order by u.nome, a.data_hora desc
$$;