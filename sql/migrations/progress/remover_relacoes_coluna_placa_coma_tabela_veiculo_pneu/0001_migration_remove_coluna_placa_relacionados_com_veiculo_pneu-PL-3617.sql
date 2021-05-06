create or replace function func_relatorio_dados_ultima_afericao_pneu(f_cod_unidades text[])
    returns table
            (
                "UNIDADE ALOCADO"               text,
                "PNEU"                          text,
                "STATUS ATUAL"                  text,
                "MARCA PNEU"                    text,
                "MODELO PNEU"                   text,
                "MEDIDAS"                       text,
                "PLACA APLICADO"                text,
                "IDENTIFICADOR FROTA"           text,
                "MARCA VEÍCULO"                 text,
                "MODELO VEÍCULO"                text,
                "TIPO VEÍCULO"                  text,
                "POSIÇÃO APLICADO"              text,
                "SULCO INTERNO"                 text,
                "SULCO CENTRAL INTERNO"         text,
                "SULCO CENTRAL EXTERNO"         text,
                "SULCO EXTERNO"                 text,
                "MENOR SULCO"                   text,
                "PRESSÃO (PSI)"                 text,
                "VIDA ATUAL"                    text,
                "DOT"                           text,
                "ÚLTIMA AFERIÇÃO"               text,
                "TIPO PROCESSO ÚLTIMA AFERIÇÃO" text,
                "FORMA DE COLETA DOS DADOS"     text
            )
    language plpgsql
as
$$
begin
    -- Essa CTE busca o código da última aferição de cada pneu.
    -- Com o código nós conseguimos buscar depois qualquer outra informação da aferição.
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
        ((((dp.largura || '/' :: text) || dp.altura) || ' R' :: text) ||
                dp.aro)                                                         as medidas,
               coalesce(v.placa, '-') :: text                                   as placa,
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
                                'DD/MM/YYYY HH24:MI'),
                                'Nunca Aferido')                                as ultima_afericao,
               case
                   when ua.tipo_processo_coleta is null
                       then 'Nunca Aferido'
                   when ua.tipo_processo_coleta = 'PLACA'
                       then 'Aferido em uma placa'
                   else 'Aferido Avulso (em estoque)' end                       as tipo_processo_ultima_afericao,
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

create or replace function func_pneu_relatorio_status_atual_pneus(
    f_cod_unidades bigint[])
    returns table
            (
                "unidade alocado"    text,
                "pneu"               text,
                "status atual"       text,
                "placa aplicado"     text,
                "posição aplicado"   text,
                "recapadora alocado" text
            )
    language plpgsql
as
$$
declare
f_status_analise text := 'ANALISE';
begin
return query
select u.nome :: text                           as unidade_alocado,
        p.codigo_cliente :: text                as cod_pneu,
        p.status :: text                        as status_atual,
        coalesce(v.placa :: text, '-')          as placa_aplicado,
       coalesce(ppne.nomenclatura :: text, '-') as posicao_aplicado,
       coalesce(
               case
                   when p.status = f_status_analise
                       then (select r.nome as nome_recapadora
                             from movimentacao m
                                      join movimentacao_destino md
                                           on m.codigo = md.cod_movimentacao
                                      join recapadora r on md.cod_recapadora_destino = r.codigo
                             where m.cod_pneu = p.codigo
                             order by m.codigo desc
                       limit 1)
                           end,
                       '-')                             as recapadora_alocado
from pneu p
         join unidade u
              on p.cod_unidade = u.codigo
         join empresa e on u.cod_empresa = e.codigo
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
where p.cod_unidade = any (f_cod_unidades)
order by u.codigo asc, p.codigo_cliente asc;
end;
$$;

create or replace function func_pneu_relatorio_validade_dot(f_cod_unidades bigint[],
                                                            f_data_atual timestamp without time zone)
    returns table
            (
                "UNIDADE"         text,
                "COD PNEU"        text,
                "PLACA"           text,
                "POSIÇÃO"         text,
                "DOT CADASTRADO"  text,
                "DOT VÁLIDO"      text,
                "TEMPO DE USO"    text,
                "TEMPO RESTANTE"  text,
                "DATA VENCIMENTO" text,
                "VENCIDO"         text,
                "DATA GERAÇÃO"    text
            )
    language plpgsql
as
$$
declare
    date_format        text := 'YY "ano(s)" MM "mes(es)" DD "dia(s)"';
    dia_mes_ano_format text := 'DD/MM/YYYY';
    data_hora_format   text := 'DD/MM/YYYY HH24:MI';
    date_converter     text := 'YYYYWW';
    prefixo_ano        text := substring(f_data_atual::text, 1, 2);
begin
return query
    with informacoes_pneu as (
            select p.codigo_cliente                               as cod_pneu,
                   p.dot                                          as dot_cadastrado,
                   -- Remove letras, characteres especiais e espaços do dot.
                   -- A flag 'g' indica que serão removidas todas as aparições do padrão específicado não somente o primeiro caso.
                   trim(regexp_replace(p.dot, '[^0-9]', '', 'g')) as dot_limpo,
                   p.cod_unidade                                  as cod_unidade,
                   u.nome                                         as unidade,
                   v.placa                                        as placa_aplicado,
                   ppne.nomenclatura                              as posicao_pneu
            from pneu p
                     join unidade u on p.cod_unidade = u.codigo
                     join empresa e on e.codigo = u.cod_empresa
                     left join veiculo_pneu vp on vp.cod_pneu = p.codigo
                     left join veiculo v on vp.cod_veiculo = v.codigo and vp.cod_unidade = v.cod_unidade
                     left join veiculo_tipo vt on v.cod_tipo = vt.codigo
                     left join veiculo_diagrama vd on vt.cod_diagrama = vd.codigo
                     left join pneu_posicao_nomenclatura_empresa ppne on ppne.cod_empresa = e.codigo
                and ppne.cod_diagrama = vd.codigo
                and ppne.posicao_prolog = vp.posicao
            where p.cod_unidade = any (f_cod_unidades)
        ),

             data_dot as (
                 select ip.cod_pneu,
                        -- Transforma o DOT_FORMATADO em data
                        case
                            when (char_length(ip.dot_limpo) = 4)
                                then
                                to_date(concat(prefixo_ano, (substring(ip.dot_limpo, 3, 4)),
                                               (substring(ip.dot_limpo, 1, 2))),
                                        date_converter)
                            else null end as dot_em_data
                 from informacoes_pneu ip
             ),

             vencimento_dot as (
                 select dd.cod_pneu,
                        -- Verifica se a data do DOT que foi transformado é menor ou igual a data atual. Se for maior está errado,
                        -- então retornará NULL, senão somará 5 dias e 5 anos à data do dot para gerar a data de vencimento.
                        -- O vencimento de um pneu é de 5 anos, como o DOT é fornecido em "SEMANA DO ANO/ANO", para que o vencimento
                        -- tenha seu prazo máximo (1 dia antes da próxima semana) serão adicionados + 5 dias ao cálculo.
                        case
                            when dd.dot_em_data <= (f_data_atual::date)
                                then dd.dot_em_data + interval '5 days 5 years'
                            else null end as data_vencimento
                 from data_dot dd
             ),

             calculos as (
                 select dd.cod_pneu,
                        -- Verifica se o dot é válido
                        -- Apenas os DOTs que, após formatados, possuiam tamanho = 4 tiveram data de vencimento gerada, portanto
                        -- podemos considerar inválidos os que possuem vencimento = null.
                        case when vd.data_vencimento is null then 'INVÁLIDO' else 'VÁLIDO' end        as dot_valido,
                        -- Cálculo tempo de uso
                        case
                            when vd.data_vencimento is null
                                then null
                            else
                                to_char(age((f_data_atual :: date), dd.dot_em_data), date_format) end as tempo_de_uso,
                        -- Cálculo dias restantes
                        to_char(age(vd.data_vencimento, f_data_atual), date_format)                   as tempo_restante,
                        -- Boolean vencimento (Se o inteiro for negativo, então o dot está vencido, senão não está vencido.
                        f_if(((vd.data_vencimento::date) - (f_data_atual::date)) < 0, true, false)    as vencido
                 from data_dot dd
                          join vencimento_dot vd on dd.cod_pneu = vd.cod_pneu
             )
select ip.unidade::text,
       ip.cod_pneu::text,
       coalesce(ip.placa_aplicado::text, '-'),
       coalesce(ip.posicao_pneu::text, '-'),
       coalesce(ip.dot_cadastrado::text, '-'),
       ca.dot_valido,
       coalesce(ca.tempo_de_uso, '-'),
       coalesce(ca.tempo_restante, '-'),
       coalesce(to_char(vd.data_vencimento, dia_mes_ano_format)::text, '-'),
       f_if(ca.vencido, 'SIM' :: text, 'NÃO' :: text),
       to_char(f_data_atual, data_hora_format)::text
from informacoes_pneu ip
         join vencimento_dot vd on ip.cod_pneu = vd.cod_pneu
         join calculos ca on ca.cod_pneu = vd.cod_pneu and ca.cod_pneu = ip.cod_pneu
order by vd.data_vencimento asc, ip.placa_aplicado;
end;
$$;

create or replace function func_relatorio_pneu_resumo_geral_pneus(f_cod_unidade text[],
                                                                  f_status_pneu text)
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
                "IDENTIFICADOR FROTA"   text,
                "TIPO"                  text,
                "POSIÇÃO"               text,
                "QUANTIDADE DE SULCOS"  text,
                "ALTURA DO SULCO NOVO"  text,
                "SULCO INTERNO"         text,
                "SULCO CENTRAL INTERNO" text,
                "SULCO CENTRAL EXTERNO" text,
                "SULCO EXTERNO"         text,
                "MENOR SULCO"           text,
                "PRESSÃO ATUAL (PSI)"   text,
                "PRESSÃO IDEAL (PSI)"   text,
                "VIDA ATUAL"            text,
                "DOT"                   text,
                "ÚLTIMA AFERIÇÃO"       text
            )
    language sql
as
$$
select u.nome                                                                   as unidade_alocado,
       p.codigo_cliente                                                         as cod_pneu,
       p.status                                                                 as status,
       coalesce(trunc(p.valor :: numeric, 2) :: text, '-')                      as valor_aquisicao,
       coalesce(to_char(p.data_hora_cadastro at time zone tz_unidade(p.cod_unidade_cadastro), 'DD/MM/YYYY HH24:MI'),
           '-')                                                                 as data_hora_cadastro,
       map.nome                                                                 as nome_marca_pneu,
       mp.nome                                                                  as nome_modelo_pneu,
       case
           when marb.codigo is null
               then 'NUNCA RECAPADO'
           else marb.nome || ' - ' || modb.nome
           end                                                                  as banda_aplicada,
       coalesce(trunc(pvv.valor :: numeric, 2) :: text, '-')                    as valor_banda,
       ((((dp.largura || '/' :: text) || dp.altura) || ' R' :: text) || dp.aro) as medidas,
       coalesce(posicao_pneu_veiculo.placa_veiculo_pneu, '-')                   as placa,
       coalesce(posicao_pneu_veiculo.identificador_frota, '-')                  as identificador_frota,
       coalesce(posicao_pneu_veiculo.veiculo_tipo, '-')                         as tipo_veiculo,
       coalesce(posicao_pneu_veiculo.posicao_pneu, '-') :: text                 as posicao_pneu,
       coalesce(modb.qt_sulcos, mp.qt_sulcos) :: text                           as qtd_sulcos,
       case p.vida_atual
       when 1 then
           func_pneu_format_sulco(mp.altura_sulcos)
       else
           func_pneu_format_sulco(modb.altura_sulcos) end                       as altura_sulco_novo,
       func_pneu_format_sulco(p.altura_sulco_interno)                           as sulco_interno,
       func_pneu_format_sulco(p.altura_sulco_central_interno)                   as sulco_central_interno,
       func_pneu_format_sulco(p.altura_sulco_central_externo)                   as sulco_central_externo,
       func_pneu_format_sulco(p.altura_sulco_externo)                           as sulco_externo,
       func_pneu_format_sulco(least(p.altura_sulco_externo, p.altura_sulco_central_externo,
                                    p.altura_sulco_central_interno,
                                    p.altura_sulco_interno))                    as menor_sulco,
       coalesce(trunc(p.pressao_atual) :: text, '-')                            as pressao_atual,
       p.pressao_recomendada :: text                                            as pressao_recomendada,
       pvn.nome :: text                                                         as vida_atual,
       coalesce(p.dot, '-')                                                     as dot,
       coalesce(
               to_char(f.data_hora_ultima_afericao at time zone tz_unidade(f.cod_unidade_ultima_afericao),
                       'DD/MM/YYYY HH24:MI'), 'NUNCA AFERIDO')                  as ultima_afericao
from pneu p
         join dimensao_pneu dp on dp.codigo = p.cod_dimensao
         join unidade u on u.codigo = p.cod_unidade
         join modelo_pneu mp on mp.codigo = p.cod_modelo and mp.cod_empresa = u.cod_empresa
         join marca_pneu map on map.codigo = mp.cod_marca
         join pneu_vida_nomenclatura pvn on pvn.cod_vida = p.vida_atual
         left join modelo_banda modb on modb.codigo = p.cod_modelo_banda
         left join marca_banda marb on marb.codigo = modb.cod_marca
         left join pneu_valor_vida pvv on p.codigo = pvv.cod_pneu and pvv.vida = p.vida_atual
         left join (select ppne.nomenclatura     as posicao_pneu,
                           vp.cod_pneu           as codigo_pneu,
                           v.placa               as placa_veiculo_pneu,
                           vp.cod_unidade        as cod_unidade_pneu,
                           vt.nome               as veiculo_tipo,
                           v.identificador_frota as identificador_frota
                    from veiculo v
                             join veiculo_pneu vp
                                  on vp.cod_veiculo = v.codigo and vp.cod_unidade = v.cod_unidade
                             join veiculo_tipo vt
                                  on v.cod_tipo = vt.codigo
                             join empresa e on vt.cod_empresa = e.codigo
                        -- left join porque unidade pode não ter nomenclatura.
                             left join veiculo_diagrama vd on vt.cod_diagrama = vd.codigo
                             left join pneu_posicao_nomenclatura_empresa ppne
                                       on ppne.cod_empresa = e.codigo
                                           and ppne.cod_diagrama = vd.codigo
                                           and vp.posicao = ppne.posicao_prolog
                    where v.cod_unidade :: text like any (f_cod_unidade)
                    order by vp.cod_pneu) as posicao_pneu_veiculo
                   on p.codigo = posicao_pneu_veiculo.codigo_pneu
         left join func_pneu_get_primeira_ultima_afericao(p.codigo) f
                   on f.cod_pneu = p.codigo
where p.cod_unidade :: text like any (f_cod_unidade)
  and case
          when f_status_pneu is null
              then true
          else p.status = f_status_pneu
end
order by u.nome, p.codigo_cliente;
$$;

create or replace function func_relatorio_previsao_troca(f_data_inicial date,
                                                         f_data_final date,
                                                         f_cod_unidade text[],
                                                         f_status_pneu character varying)
    returns table
            (
                "unidade alocado"         text,
                "cod pneu"                text,
                "status"                  text,
                "vida atual"              integer,
                "marca"                   text,
                "modelo"                  text,
                "medidas"                 text,
                "placa aplicado"          text,
                "posição aplicado"        text,
                "qtd de aferições"        bigint,
                "data 1ª aferição"        text,
                "data última aferição"    text,
                "dias ativo"              integer,
                "média km por dia"        numeric,
                "maior medição vida"      numeric,
                "menor sulco atual"       numeric,
                "milímetros gastos"       numeric,
                "kms por milímetro"       numeric,
                "valor vida"              real,
                "valor acumulado"         real,
                "valor por km vida atual" numeric,
                "valor por km acumulado"  numeric,
                "kms a percorrer"         numeric,
                "dias restantes"          double precision,
                "previsão de troca"       text,
                "destino"                 text
            )
    language sql
as
$$
select vap."unidade alocado",
       vap."cod pneu cliente",
       vap."status pneu",
       vap."vida atual",
       vap."marca",
       vap."modelo",
       vap."medidas",
       v.placa                                  as placa_aplicado,
       coalesce(ppne.nomenclatura, '-') :: text as posicao_aplicado,
       vap."qtd de aferições",
       vap."dta 1a aferição",
       vap."dta última aferição",
       vap."dias ativo",
       vap."média km por dia",
       vap."maior medição vida",
       vap."menor sulco atual",
       vap."milimetros gastos",
       vap."kms por milimetro",
       vap.valor_vida_atual,
       vap.valor_acumulado,
       vap."valor por km",
       vap."valor por km acumulado",
       vap."kms a percorrer",
       vap."dias restantes",
       to_char(vap."previsão de troca", 'DD/MM/YYYY'),
       vap."destino"
from view_pneu_analise_vida_atual as vap
         join veiculo_pneu vp
              on vap."cod pneu" = vp.cod_pneu
         join veiculo v
              on vp.cod_veiculo = v.codigo
         left join veiculo_tipo vt
                   on v.cod_tipo = vt.codigo
         join empresa e on vt.cod_empresa = e.codigo
         left join veiculo_diagrama vd on vt.cod_diagrama = vd.codigo
         left join pneu_posicao_nomenclatura_empresa ppne on ppne.cod_empresa = e.codigo
    and ppne.cod_diagrama = vd.codigo
    and vp.posicao = ppne.posicao_prolog
where vap.cod_unidade :: text like any (f_cod_unidade)
  and vap."previsão de troca" <= f_data_final
  and vap."status pneu" like f_status_pneu
order by vap."unidade alocado";
$$;

create or replace function func_veiculo_relatorio_listagem_veiculos_by_unidade(f_cod_unidades bigint[])
    returns table
            (
                UNIDADE                  text,
                PLACA                    text,
                "IDENTIFICADOR FROTA"    text,
                MARCA                    text,
                MODELO                   text,
                TIPO                     text,
                "DIAGRAMA VINCULADO?"    text,
                "KM ATUAL"               text,
                STATUS                   text,
                "DATA/HORA CADASTRO"     text,
                "VEÍCULO COMPLETO"       text,
                "QTD PNEUS VINCULADOS"   text,
                "QTD POSIÇÕES DIAGRAMA"  text,
                "QTD POSIÇÕES SEM PNEUS" text,
                "QTD ESTEPES"            text
            )
    language plpgsql
as
$$
declare
estepes            integer := 900;
    posicoes_sem_pneus integer = 0;
    sim                text    := 'SIM';
    nao                text    := 'NÃO';
begin
return query
    -- Calcula a quantidade de pneus e estepes que estão vinculados na placa.
    with qtd_pneus_vinculados_placa as (
            select v.placa,
                   count(v.placa)
                   filter (where vp.posicao < estepes)  as qtd_pneus_vinculados,
                   count(vp.cod_veiculo)
                   filter (where vp.posicao >= estepes) as qtd_estepes_vinculados,
                   vt.cod_diagrama
            from veiculo v
                     join veiculo_tipo vt on v.cod_tipo = vt.codigo
                     left join veiculo_pneu vp on v.codigo = vp.cod_veiculo and v.cod_unidade = vp.cod_unidade
            where v.cod_unidade = any (f_cod_unidades)
            group by v.placa,
                     vt.cod_diagrama
        ),

             -- Calcula a quantidade de posições nos diagramas que existem no prolog.
             qtd_posicoes_diagrama as (
                 select vde.cod_diagrama,
                        sum(vde.qt_pneus) as qtd_posicoes_diagrama
                 from veiculo_diagrama_eixos vde
                 group by cod_diagrama
             )

select u.nome :: text                                                     as unidade,
        v.placa :: text                                                   as placa,
        v.identificador_frota :: text                                     as identificador_frota,
        ma.nome :: text                                                   as marca,
        mo.nome :: text                                                   as modelo,
        vt.nome :: text                                                   as tipo,
        case
            when qpvp.cod_diagrama is null
                then 'NÃO'
            else 'SIM' end                                                as possui_diagrama,
       v.km :: text                                                       as km_atual,
       f_if(v.status_ativo, 'ATIVO' :: text, 'INATIVO' :: text)           as status,
       coalesce(to_char(v.data_hora_cadastro, 'DD/MM/YYYY HH24:MI'), '-') as data_hora_cadastro,
       -- Caso a quantidade de posições sem pneus seja 0 é porque o veículo está com todos os pneus - veículo completo.
       case
           when (qsd.qtd_posicoes_diagrama - qpvp.qtd_pneus_vinculados) = posicoes_sem_pneus
               then sim
           else nao end                                                   as veiculo_completo,
       qpvp.qtd_pneus_vinculados :: text                                  as qtd_pneus_vinculados,
        qsd.qtd_posicoes_diagrama :: text                                 as qtd_posicoes_diagrama,
               -- Calcula a quantidade de posições sem pneus.
        (qsd.qtd_posicoes_diagrama - qpvp.qtd_pneus_vinculados) :: text   as qtd_posicoes_sem_pneus,
        qpvp.qtd_estepes_vinculados :: text                               as qtd_estepes_vinculados
from veiculo v
         join unidade u on v.cod_unidade = u.codigo
         join modelo_veiculo mo on v.cod_modelo = mo.codigo
         join marca_veiculo ma on mo.cod_marca = ma.codigo
         join veiculo_tipo vt on v.cod_tipo = vt.codigo
         right join qtd_pneus_vinculados_placa qpvp on qpvp.placa = v.placa
         left join qtd_posicoes_diagrama qsd on qsd.cod_diagrama = qpvp.cod_diagrama
order by u.nome asc,
         status asc,
         v.placa asc,
         ma.nome asc,
         mo.nome asc,
         vt.nome asc,
         qtd_posicoes_sem_pneus desc;
end;
$$;