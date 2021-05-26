create or replace function func_pneu_relatorio_previsao_troca(f_data_final date,
                                                              f_cod_unidades bigint[],
                                                              f_status_pneu text)
    returns table
            (
                "UNIDADE ALOCADO"         text,
                "COD PNEU"                text,
                "STATUS"                  text,
                "VIDA ATUAL"              integer,
                "MARCA"                   text,
                "MODELO"                  text,
                "MEDIDAS"                 text,
                "PLACA APLICADO"          text,
                "POSIÇÃO APLICADO"        text,
                "QTD DE AFERIÇÕES"        bigint,
                "DATA 1ª AFERIÇÃO"        text,
                "DATA ÚLTIMA AFERIÇÃO"    text,
                "DIAS ATIVO"              integer,
                "MÉDIA KM POR DIA"        numeric,
                "MAIOR MEDIÇÃO VIDA"      numeric,
                "MENOR SULCO ATUAL"       numeric,
                "MILÍMETROS GASTOS"       numeric,
                "KMS POR MILÍMETRO"       numeric,
                "VALOR VIDA"              real,
                "VALOR ACUMULADO"         real,
                "VALOR POR KM VIDA ATUAL" numeric,
                "VALOR POR KM ACUMULADO"  numeric,
                "KMS A PERCORRER"         numeric,
                "DIAS RESTANTES"          double precision,
                "PREVISÃO DE TROCA"       text,
                "DESTINO"                 text
            )
    language sql
as
$$
select vap.nome_unidade_alocado                               as nome_unidade_alocado,
       vap.cod_cliente_pneu                                   as cod_cliente_pneu,
       vap.status_pneu                                        as status_pneu,
       vap.vida_atual                                         as vida_atual,
       vap.nome_marca                                         as nome_marca,
       vap.nome_modelo                                        as nome_modelo,
       vap.medidas                                            as medidas,
       v.placa                                                as placa_aplicado,
       coalesce(ppne.nomenclatura, '' - '') :: text           as posicao_aplicado,
       vap.qtd_afericoes                                      as qtd_afericoes,
       vap.data_primeira_afericao                             as data_primeira_afericao,
       vap.data_ultima_afericao                               as data_ultima_afericao,
       vap.dias_ativo                                         as dias_ativo,
       vap.media_km_por_dia                                   as media_km_por_dia,
       vap.maior_sulco_vida                                   as maior_sulco_vida,
       vap.menor_sulco_vida                                   as menor_sulco_vida,
       vap.milimetros_gastos                                  as milimetros_gastos,
       vap.kms_por_milimetro                                  as kms_por_milimetro,
       vap.valor_vida_atual                                   as valor_vida_atual,
       vap.valor_acumulado                                    as valor_acumulado,
       vap.valor_por_km                                       as valor_por_km,
       vap.valor_por_km_acumulado                             as valor_por_km_acumulado,
       vap.kms_a_percorrer                                    as kms_a_percorrer,
       vap.dias_restantes_pneu                                as dias_restantes_pneu,
       to_char(vap.data_prevista_troca, '' dd / mm / yyyy '') as data_prevista_troca,
       vap.destino_pneu                                       as destino_pneu
from view_pneu_analise_vida_atual as vap
         join veiculo_pneu vp
              on vap.cod_pneu = vp.cod_pneu
         join veiculo v
              on vp.cod_veiculo = v.codigo
         left join veiculo_tipo vt
                   on v.cod_tipo = vt.codigo
         join empresa e on vt.cod_empresa = e.codigo
         left join veiculo_diagrama vd on vt.cod_diagrama = vd.codigo
         left join pneu_posicao_nomenclatura_empresa ppne on ppne.cod_empresa = e.codigo
    and ppne.cod_diagrama = vd.codigo
    and vp.posicao = ppne.posicao_prolog
where vap.cod_unidade = any (f_cod_unidades)
  and vap.data_prevista_troca <= f_data_final
  and vap.status_pneu like f_status_pneu
order by vap.nome_unidade_alocado;
$$;