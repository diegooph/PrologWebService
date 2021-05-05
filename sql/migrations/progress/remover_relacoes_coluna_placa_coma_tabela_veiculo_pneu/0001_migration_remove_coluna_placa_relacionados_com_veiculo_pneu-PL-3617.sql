create or replace function func_relatorio_dados_ultima_afericao_pneu(f_cod_unidades text[])
    returns table
            (
                "unidade alocado"               text,
                "pneu"                          text,
                "status atual"                  text,
                "marca pneu"                    text,
                "modelo pneu"                   text,
                "medidas"                       text,
                "placa aplicado"                text,
                "identificador frota"           text,
                "marca veículo"                 text,
                "modelo veículo"                text,
                "tipo veículo"                  text,
                "posição aplicado"              text,
                "sulco interno"                 text,
                "sulco central interno"         text,
                "sulco central externo"         text,
                "sulco externo"                 text,
                "menor sulco"                   text,
                "pressão (psi)"                 text,
                "vida atual"                    text,
                "dot"                           text,
                "última aferição"               text,
                "tipo processo última aferição" text,
                "forma de coleta dos dados"     text
            )
    language plpgsql
as
$$
begin
    -- essa cte busca o código da última aferição de cada pneu.
    -- com o código nós conseguimos buscar depois qualquer outra informação da aferição.
return query
    with cods_afericoes as (
            select av.cod_pneu   as cod_pneu_aferido,
                   max(a.codigo) as cod_afericao
            from afericao a
                     join afericao_valores av
                          on av.cod_afericao = a.codigo
                     join pneu p on p.codigo = av.cod_pneu
            where p.cod_unidade :: text = any (f_cod_unidades)
            group by av.cod_pneu
        ),

             ultimas_afericoes as (
                 select ca.cod_pneu_aferido    as cod_pneu_aferido,
                        a.data_hora            as data_hora_afericao,
                        a.cod_unidade          as cod_unidade_afericao,
                        a.tipo_processo_coleta as tipo_processo_coleta,
                        a.forma_coleta_dados   as forma_coleta_dados
                 from cods_afericoes ca
                          join afericao a on a.codigo = ca.cod_afericao)

select u.nome :: text                                                   as unidade_alocado,
        p.codigo_cliente :: text                                         as cod_pneu,
        p.status :: text                                                 as status_atual,
        map.nome :: text                                                 as nome_marca,
        mp.nome :: text                                                  as nome_modelo,
        ((((dp.largura || '/' :: text) || dp.altura) || ' r' :: text) ||
                dp.aro)                                                         as medidas,
               coalesce(v.placa, '-') :: text                                  as placa,
               coalesce(v.identificador_frota, '-') :: text                     as identificador_frota,
               coalesce(marv.nome, '-') :: text                                 as marca_veiculo,
               coalesce(modv.nome, '-') :: text                                 as modelo_veiculo,
               coalesce(vt.nome, '-') :: text                                   as tipo_veiculo,
               coalesce(ppne.nomenclatura:: text, '-')                          as posicao_pneu,
               func_pneu_format_sulco(p.altura_sulco_interno)                   as sulco_interno,
               func_pneu_format_sulco(p.altura_sulco_central_interno)           as sulco_central_interno,
               func_pneu_format_sulco(p.altura_sulco_central_externo)           as sulco_central_externo,
               func_pneu_format_sulco(p.altura_sulco_externo)                   as sulco_externo,
               func_pneu_format_sulco(least(p.altura_sulco_externo, p.altura_sulco_central_externo,
                                            p.altura_sulco_central_interno,
                                            p.altura_sulco_interno))            as menor_sulco,
               replace(coalesce(trunc(p.pressao_atual) :: text, '-'), '.', ',') as pressao_atual,
               p.vida_atual :: text                                             as vida_atual,
               coalesce(p.dot, '-') :: text                                     as dot,
               coalesce(to_char(ua.data_hora_afericao at time zone
                                tz_unidade(ua.cod_unidade_afericao),
                                'dd/mm/yyyy hh24:mi'),
                        'nunca aferido')                                        as ultima_afericao,
               case
                   when ua.tipo_processo_coleta is null
                       then 'nunca aferido'
                   when ua.tipo_processo_coleta = 'placa'
                       then 'aferido em uma placa'
                   else 'aferido avulso (em estoque)' end                       as tipo_processo_ultima_afericao,
               coalesce(tafcd.status_legivel, '-')::text                        as forma_coleta_dados
        from pneu p
                 join dimensao_pneu dp on dp.codigo = p.cod_dimensao
                 join unidade u on u.codigo = p.cod_unidade
                 join empresa e on u.cod_empresa = e.codigo
                 join modelo_pneu mp on mp.codigo = p.cod_modelo and mp.cod_empresa = u.cod_empresa
                 join marca_pneu map on map.codigo = mp.cod_marca
                 left join veiculo_pneu vp
                           on p.codigo = vp.cod_pneu
                               and p.cod_unidade = vp.cod_unidade
                 left join veiculo v
                           on vp.cod_veiculo = v.codigo
                               and vp.cod_unidade = v.cod_unidade
                 left join veiculo_tipo vt
                           on v.cod_tipo = vt.codigo
                 left join veiculo_diagrama vd on vt.cod_diagrama = vd.codigo
                 left join pneu_posicao_nomenclatura_empresa ppne on ppne.cod_empresa = e.codigo
            and ppne.cod_diagrama = vd.codigo
            and ppne.posicao_prolog = vp.posicao
                 left join modelo_veiculo modv
                           on modv.codigo = v.cod_modelo
                 left join marca_veiculo marv
                           on marv.codigo = modv.cod_marca
                 left join ultimas_afericoes ua
                           on ua.cod_pneu_aferido = p.codigo
                 left join types.afericao_forma_coleta_dados tafcd
                           on tafcd.forma_coleta_dados::text = ua.forma_coleta_dados::text
        where p.cod_unidade :: text = any (f_cod_unidades)
        order by u.nome, p.codigo_cliente;
end;
$$;

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
    -- Essa CTE busca o código da última aferição de cada pneu.
    -- Com o código nós conseguimos buscar depois a data/hora da aferição e o código da unidade em que ocorreu,
    -- para aplicar o TZ correto.
    with ultimas_afericoes as (
            select av.cod_pneu   as cod_pneu_aferido,
                   max(a.codigo) as cod_afericao
            from afericao a
                     join afericao_valores av
                          on av.cod_afericao = a.codigo
                     join pneu p on p.codigo = av.cod_pneu
            where p.cod_unidade = any (f_cod_unidades)
            group by av.cod_pneu
        )

select u.nome :: text                                                                as unidade_alocado,
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
                                    p.altura_sulco_interno))                         as menor_sulco,
        coalesce(trunc(p.pressao_atual) :: text, '-')                                as pressao_atual,
        p.pressao_recomendada :: text                                                as pressao_recomendada,
        pvn.nome :: text                                                             as vida_atual,
        coalesce(p.dot, '-') :: text                                                 as dot,
               -- Usamos um CASE ao invés do coalesce da func FORMAT_WITH_TZ, pois desse modo evitamos o evaluate
               -- dos dois selects internos de consulta na tabela AFERICAO caso o pneu nunca tenha sido aferido.
        case
            when ua.cod_afericao is null
                then 'nunca aferido'
            else
                format_with_tz((select a.data_hora
                                from afericao a
                                where a.codigo = ua.cod_afericao),
                               tz_unidade((select a.cod_unidade
                                           from afericao a
                                           where a.codigo = ua.cod_afericao)),
                               f_timestamp_format)
            end                                                                      as ultima_afericao,
       ptdi.descricao                                                                as descricao_desgaste,
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
         left join pneu_posicao_nomenclatura_empresa ppne on ppne.cod_empresa = e.codigo
    and ppne.cod_diagrama = vd.codigo
    and ppne.posicao_prolog = vp.posicao
         left join ultimas_afericoes ua
                   on ua.cod_pneu_aferido = p.codigo
where p.cod_unidade = any (f_cod_unidades)
  and f_if(f_status_pneu is null, true, f_status_pneu = p.status :: pneu_status_type)
  and verif_desgaste.tem_desgaste_irregular
order by verif_desgaste.nivel_desgaste_irregular desc, u.nome, p.codigo_cliente;
end;
$$;