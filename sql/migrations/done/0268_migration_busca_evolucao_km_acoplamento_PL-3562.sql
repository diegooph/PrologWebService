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
                                            mp.codigo                                   as codigo,
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
             union
             (
                 select 'ACOPLAMENTO'                              as processo,
                        vah.cod_processo                           as codigo,
                        vap.data_hora::timestamp without time zone as data_hora,
                        vah.cod_veiculo                            as cod_veiculo,
                        vah.km_veiculo                             as km_coletado
                 from veiculo_acoplamento_historico vah
                          inner join veiculo_acoplamento_processo vap on vah.cod_processo = vap.codigo
                 where cod_veiculo = f_cod_veiculo)
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

insert into types.processo_evolucao_km_type (processo,
                                             processo_legivel_pt_br,
                                             processo_legivel_es)
values ('ACOPLAMENTO', 'ACOPLAMENTO', 'ACOPLAMIENTO');