drop function func_relatorio_previsao_troca(f_data_inicial date,
    f_data_final date,
    f_cod_unidade text[],
    f_status_pneu character varying);

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
select vap."UNIDADE ALOCADO",
       vap."COD PNEU CLIENTE",
       vap."STATUS PNEU",
       vap."VIDA ATUAL",
       vap."MARCA",
       vap."MODELO",
       vap."MEDIDAS",
       v.placa                                  as placa_aplicado,
       coalesce(ppne.nomenclatura, '-') :: text as posicao_aplicado,
       vap."QTD DE AFERIÇÕES",
       vap."DTA 1A AFERIÇÃO",
       vap."DTA ÚLTIMA AFERIÇÃO",
       vap."DIAS ATIVO",
       vap."MÉDIA KM POR DIA",
       vap."MAIOR MEDIÇÃO VIDA",
       vap."MENOR SULCO ATUAL",
       vap."MILIMETROS GASTOS",
       vap."KMS POR MILIMETRO",
       vap.valor_vida_atual,
       vap.valor_acumulado,
       vap."VALOR POR KM",
       vap."VALOR POR KM ACUMULADO",
       vap."KMS A PERCORRER",
       vap."DIAS RESTANTES",
       to_char(vap."PREVISÃO DE TROCA", 'DD/MM/YYYY'),
       vap."DESTINO"
from view_pneu_analise_vida_atual as vap
         join veiculo_pneu vp
              on vap."COD PNEU" = vp.cod_pneu
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
  and vap."PREVISÃO DE TROCA" <= f_data_final
  and vap."STATUS PNEU" like f_status_pneu
order by vap."UNIDADE ALOCADO";
$$;