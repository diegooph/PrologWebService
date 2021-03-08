create or replace function func_afericao_relatorio_dados_gerais(f_cod_unidades bigint[],
                                                                f_data_inicial date,
                                                                f_data_final date)
    returns table
            (
                "CÓDIGO AFERIÇÃO"           text,
                "UNIDADE"                   text,
                "DATA E HORA"               text,
                "CPF DO RESPONSÁVEL"        text,
                "NOME COLABORADOR"          text,
                "PNEU"                      text,
                "STATUS ATUAL"              text,
                "VALOR COMPRA"              text,
                "MARCA DO PNEU"             text,
                "MODELO DO PNEU"            text,
                "QTD SULCOS MODELO"         text,
                "VIDA ATUAL"                text,
                "VALOR VIDA ATUAL"          text,
                "BANDA APLICADA"            text,
                "QTD SULCOS BANDA"          text,
                "DIMENSÃO"                  text,
                "DOT"                       text,
                "DATA E HORA CADASTRO"      text,
                "POSIÇÃO PNEU"              text,
                "PLACA"                     text,
                "IDENTIFICADOR FROTA"       text,
                "VIDA MOMENTO AFERIÇÃO"     text,
                "KM NO MOMENTO DA AFERIÇÃO" text,
                "KM ATUAL"                  text,
                "MARCA DO VEÍCULO"          text,
                "MODELO DO VEÍCULO"         text,
                "TIPO DE MEDIÇÃO COLETADA"  text,
                "TIPO DA AFERIÇÃO"          text,
                "TEMPO REALIZAÇÃO (MM:SS)"  text,
                "SULCO INTERNO"             text,
                "SULCO CENTRAL INTERNO"     text,
                "SULCO CENTRAL EXTERNO"     text,
                "SULCO EXTERNO"             text,
                "MENOR SULCO"               text,
                "PRESSÃO"                   text,
                "FORMA DE COLETA DOS DADOS" text
            )
    language sql
as
$$
select a.codigo :: text                                                                 as cod_afericao,
       u.nome                                                                           as unidade,
       to_char((a.data_hora at time zone tz_unidade(a.cod_unidade)),
               'DD/MM/YYYY HH24:MI')                                                    as data_hora_afericao,
       lpad(c.cpf :: text, 11, '0')                                                     as cpf_colaborador,
       c.nome                                                                           as nome_colaborador,
       p.codigo_cliente                                                                 as codigo_cliente_pneu,
       p.status                                                                         as status_atual_pneu,
       round(p.valor :: numeric, 2) :: text                                             as valor_compra,
       map.nome                                                                         as marca_pneu,
       mp.nome                                                                          as modelo_pneu,
       mp.qt_sulcos :: text                                                             as qtd_sulcos_modelo,
       (select pvn.nome
        from pneu_vida_nomenclatura pvn
        where pvn.cod_vida = p.vida_atual)                                              as vida_atual,
       coalesce(round(pvv.valor :: numeric, 2) :: text, '-')                            as valor_vida_atual,
       f_if(marb.codigo is not null, marb.nome || ' - ' || modb.nome, 'Nunca Recapado') as banda_aplicada,
       coalesce(modb.qt_sulcos :: text, '-')                                            as qtd_sulcos_banda,
       dp.largura || '-' || dp.altura || '/' || dp.aro                                  as dimensao,
       p.dot                                                                            as dot,
       coalesce(to_char(p.data_hora_cadastro at time zone tz_unidade(p.cod_unidade_cadastro),
                        'DD/MM/YYYY HH24:MI'),
                '-')                                                                    as data_hora_cadastro,
       coalesce(ppne.nomenclatura, '-')                                                 as posicao,
       coalesce(a.placa_veiculo :: text, '-')                                           as placa,
       coalesce(v.identificador_frota, '-')                                             as identificador_frota,
       (select pvn.nome
        from pneu_vida_nomenclatura pvn
        where pvn.cod_vida = av.vida_momento_afericao)                                  as vida_momento_afericao,
       coalesce(a.km_veiculo :: text, '-')                                              as km_momento_afericao,
       coalesce(v.km :: text, '-')                                                      as km_atual,
       coalesce(m2.nome, '-')                                                           as marca_veiculo,
       coalesce(mv.nome, '-')                                                           as modelo_veiculo,
       a.tipo_medicao_coletada                                                          as tipo_medicao_coletada,
       a.tipo_processo_coleta                                                           as tipo_processo_coleta,
       to_char((a.tempo_realizacao || ' milliseconds') :: interval, 'MI:SS')            as tempo_realizacao_minutos,
       func_pneu_format_sulco(av.altura_sulco_interno)                                  as sulco_interno,
       func_pneu_format_sulco(av.altura_sulco_central_interno)                          as sulco_central_interno,
       func_pneu_format_sulco(av.altura_sulco_central_externo)                          as sulco_central_externo,
       func_pneu_format_sulco(av.altura_sulco_externo)                                  as sulco_externo,
       func_pneu_format_sulco(least(av.altura_sulco_externo,
                                    av.altura_sulco_central_externo,
                                    av.altura_sulco_central_interno,
                                    av.altura_sulco_interno))                           as menor_sulco,
       replace(coalesce(trunc(av.psi :: numeric, 1) :: text, '-'), '.', ',')            as pressao,
       coalesce(tafcd.status_legivel::text, '-'::text)                                  as status_legivel
from afericao a
         join afericao_valores av on a.codigo = av.cod_afericao
         join unidade u on u.codigo = a.cod_unidade
         join colaborador c on c.cpf = a.cpf_aferidor
         join pneu p on p.codigo = av.cod_pneu
         join modelo_pneu mp on p.cod_modelo = mp.codigo
         join marca_pneu map on map.codigo = mp.cod_marca
         join dimensao_pneu dp on p.cod_dimensao = dp.codigo
         left join pneu_valor_vida pvv on p.codigo = pvv.cod_pneu and p.vida_atual = pvv.vida
         left join types.afericao_forma_coleta_dados tafcd on tafcd.forma_coleta_dados = a.forma_coleta_dados::text

    -- Pode não possuir banda.
         left join modelo_banda modb on modb.codigo = p.cod_modelo_banda
         left join marca_banda marb on marb.codigo = modb.cod_marca

    -- Se foi aferição de pneu avulso, pode não possuir placa.
         left join veiculo v on v.placa = a.placa_veiculo

         left join pneu_posicao_nomenclatura_empresa ppne
                   on ppne.cod_empresa = p.cod_empresa
                       and ppne.cod_diagrama = a.cod_diagrama
                       and ppne.posicao_prolog = av.posicao
         left join modelo_veiculo mv
                   on mv.codigo = v.cod_modelo
         left join marca_veiculo m2
                   on mv.cod_marca = m2.codigo
where a.cod_unidade = any (f_cod_unidades)
  and a.data_hora >= (f_data_inicial::date - interval '1 day')
  and a.data_hora <= (f_data_final::date + interval '1 day')
  and (a.data_hora at time zone tz_unidade(a.cod_unidade)) :: date between f_data_inicial and f_data_final
order by u.codigo, a.data_hora desc;
$$;