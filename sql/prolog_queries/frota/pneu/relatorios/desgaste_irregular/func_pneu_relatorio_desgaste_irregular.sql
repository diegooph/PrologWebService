create or replace function func_pneu_relatorio_desgaste_irregular(f_cod_unidades bigint[],
                                                                  f_status_pneu pneu_status_type default null)
    returns table
            (
                "UNIDADE ALOCADO"       text,
                "PNEU"                  text,
                "STATUS"                text,
                "VALOR DE AQUISIÇÃO"    text,
                "DATA/HORA CADASTRO"    text,
                "MARCA"                 text,
                "MODELO"                text,
                "BANDA APLICADA"        text,
                "VALOR DA BANDA"        text,
                "MEDIDAS"               text,
                "PLACA"                 text,
                "TIPO"                  text,
                "POSIÇÃO"               text,
                "QUANTIDADE DE SULCOS"  text,
                "SULCO INTERNO"         text,
                "SULCO CENTRAL INTERNO" text,
                "SULCO CENTRAL EXTERNO" text,
                "SULCO EXTERNO"         text,
                "MENOR SULCO"           text,
                "PRESSÃO ATUAL (PSI)"   text,
                "PRESSÃO IDEAL (PSI)"   text,
                "VIDA ATUAL"            text,
                "DOT"                   text,
                "ÚLTIMA AFERIÇÃO"       text,
                "DESCRIÇÃO DESGASTE"    text,
                "NÍVEL DE DESGASTE"     text,
                "APARÊNCIA PNEU"        text,
                "CAUSAS PROVÁVEIS"      text,
                "AÇÃO"                  text,
                "PRECAUÇÃO"             text
            )
    language plpgsql
as
$$
declare
    f_timestamp_format text := 'DD/MM/YYYY HH24:MI';
begin
    return query
        select u.nome :: text                                                               as unidade_alocado,
               p.codigo_cliente :: text                                                     as cod_pneu,
               p.status :: text                                                             as status,
               coalesce(trunc(p.valor :: numeric, 2) :: text, '-')                          as valor_aquisicao,
               format_with_tz(p.data_hora_cadastro,
                              tz_unidade(p.cod_unidade_cadastro),
                              f_timestamp_format,
                              '-')                                                          as data_hora_cadastro,
               map.nome :: text                                                             as nome_marca_pneu,
               mp.nome :: text                                                              as nome_modelo_pneu,
               f_if(marb.codigo is null, 'Nunca Recapado', marb.nome || ' - ' || modb.nome) as banda_aplicada,
               coalesce(trunc(pvv.valor :: numeric, 2) :: text, '-')                        as valor_banda,
               func_pneu_format_dimensao(dp.largura, dp.altura, dp.aro)                     as medidas,
               coalesce(v.placa, '-') :: text                                               as placa,
               coalesce(vt.nome, '-') :: text                                               as tipo_veiculo,
               coalesce(ppne.nomenclatura :: text, '-')                                     as posicao_pneu,
               coalesce(modb.qt_sulcos, mp.qt_sulcos) :: text                               as qtd_sulcos,
               func_pneu_format_sulco(p.altura_sulco_interno)                               as sulco_interno,
               func_pneu_format_sulco(p.altura_sulco_central_interno)                       as sulco_central_interno,
               func_pneu_format_sulco(p.altura_sulco_central_externo)                       as sulco_central_externo,
               func_pneu_format_sulco(p.altura_sulco_externo)                               as sulco_externo,
               func_pneu_format_sulco(least(p.altura_sulco_externo, p.altura_sulco_central_externo,
                                            p.altura_sulco_central_interno,
                                            p.altura_sulco_interno))                        as menor_sulco,
               coalesce(trunc(p.pressao_atual) :: text, '-')                                as pressao_atual,
               p.pressao_recomendada :: text                                                as pressao_recomendada,
               pvn.nome :: text                                                             as vida_atual,
               coalesce(p.dot, '-') :: text                                                 as dot,
               -- Usamos um CASE ao invés do coalesce da func FORMAT_WITH_TZ, pois desse modo evitamos o evaluate
               -- dos dois selects internos de consulta na tabela AFERICAO caso o pneu nunca tenha sido aferido.
               case
                   when (select not exists(select av.cod_pneu from afericao_valores av where av.cod_pneu = p.codigo))
                       then 'Nunca Aferido'
                   else
                       to_char((select max(a.data_hora) at time zone tz_unidade(a.cod_unidade) as data_hora
                                from afericao a
                                         join afericao_valores av on a.codigo = av.cod_afericao
                                where av.cod_pneu = p.codigo
                                group by a.codigo, a.cod_unidade, a.data_hora
                                order by a.data_hora desc
                                limit 1), f_timestamp_format)
                   end                                                                      as ultima_afericao,
               ptdi.descricao                                                               as descricao_desgaste,
               -- Por enquanto, deixamos hardcoded os ranges de cada nível de desgaste.
               case
                   when verif_desgaste.nivel_desgaste_irregular = 'BAIXO'
                       then 'BAIXO (0.1 mm até 0.9 mm)'
                   when verif_desgaste.nivel_desgaste_irregular = 'MODERADO'
                       then 'MODERADO (1.0 mm até 2.0 mm)'
                   when verif_desgaste.nivel_desgaste_irregular = 'ACENTUADO'
                       then 'ACENTUADO (2.1 mm e acima)'
                   end                                                                      as nivel_desgaste,
               ptdi.aparencia_pneu                                                          as aparencia_pneu,
               ptdi.causas_provaveis                                                        as causas_provaveis,
               ptdi.acao                                                                    as acao,
               ptdi.precaucao                                                               as precaucao
        from pneu p
                 join dimensao_pneu dp on dp.codigo = p.cod_dimensao
                 join unidade u on u.codigo = p.cod_unidade
                 join empresa e on u.cod_empresa = e.codigo
                 join modelo_pneu mp on mp.codigo = p.cod_modelo and mp.cod_empresa = u.cod_empresa
                 join marca_pneu map on map.codigo = mp.cod_marca
                 join pneu_vida_nomenclatura pvn on pvn.cod_vida = p.vida_atual
                 join func_pneu_verifica_desgaste_irregular(p.codigo,
                                                            p.altura_sulco_externo,
                                                            p.altura_sulco_central_externo,
                                                            p.altura_sulco_central_interno,
                                                            p.altura_sulco_interno) verif_desgaste
                      on verif_desgaste.cod_pneu = p.codigo
                 left join pneu_tipo_desgaste_irregular ptdi
                           on ptdi.tipo_desgaste_irregular = verif_desgaste.tipo_desgaste_irregular
                 left join modelo_banda modb on modb.codigo = p.cod_modelo_banda
                 left join marca_banda marb on marb.codigo = modb.cod_marca
                 left join pneu_valor_vida pvv on p.codigo = pvv.cod_pneu and pvv.vida = p.vida_atual
                 left join veiculo_pneu vp
                           on p.codigo = vp.cod_pneu
                               and p.cod_unidade = vp.cod_unidade
                 left join veiculo v
                           on vp.cod_veiculo = v.codigo
                               and vp.cod_unidade = v.cod_unidade
                 left join veiculo_tipo vt
                           on v.cod_tipo = vt.codigo
                 left join veiculo_diagrama vd on vt.cod_diagrama = vd.codigo
                 left join pneu_posicao_nomenclatura_empresa ppne
                           on ppne.cod_empresa = e.codigo
                               and ppne.cod_diagrama = vd.codigo
                               and ppne.posicao_prolog = vp.posicao
        where p.cod_unidade = any (f_cod_unidades)
          and case
                  when f_status_pneu is null then true
                  else f_status_pneu::text = p.status
            end
          and verif_desgaste.tem_desgaste_irregular
        order by verif_desgaste.nivel_desgaste_irregular desc, u.nome, p.codigo_cliente;
end;
$$;