create or replace function func_pneu_relatorio_pneus_descartados(f_cod_unidades text[],
                                                                 f_data_inicial date,
                                                                 f_data_final date)
    returns table
            (
                "unidade do descarte"               text,
                "responsável pelo descarte"         text,
                "data/hora do descarte"             text,
                "código do pneu"                    text,
                "marca do pneu"                     text,
                "modelo do pneu"                    text,
                "marca da banda"                    text,
                "modelo da banda"                   text,
                "dimensão do pneu"                  text,
                "última pressão"                    text,
                "origem descarte"                   text,
                "placa aplicado momento descarte"   text,
                "posição aplicado momento descarte" text,
                "total de vidas"                    text,
                "altura sulco interno"              text,
                "altura sulco central interno"      text,
                "altura sulco central externo"      text,
                "altura sulco externo"              text,
                "menor sulco"                       text,
                "dot"                               text,
                "motivo do descarte"                text,
                "foto 1"                            text,
                "foto 2"                            text,
                "foto 3"                            text,
                "observação movimentacao"           text,
                "observação geral"                  text
            )
    language sql
as
$$
select u.nome                                                           as unidade_do_descarte,
       c.nome                                                           as responsavel_pelo_descarte,
       to_char(mp.data_hora at time zone tz_unidade(p.cod_unidade),
               'DD/MM/YYYY HH24:MI')                                    as data_hora_descarte,
       p.codigo_cliente                                                 as codigo_pneu,
       map.nome                                                         as marca_pneu,
       mop.nome                                                         as modelo_pneu,
       mab.nome                                                         as marca_banda,
       mob.nome                                                         as modelo_banda,
       'Altura: ' || dp.altura || ' - Largura: ' || dp.largura || ' - Aro: ' || dp.aro
                                                                        as dimensao_pneu,
       replace(coalesce(trunc(p.pressao_atual) :: text, '-'), '.', ',') as ultima_pressao,
       mo.tipo_origem                                                   as origem_descarte,
       coalesce(v.placa :: text, '-')                                   as placa_aplicado_momento_descarte,
       coalesce(ppne.nomenclatura :: text, '-')                         as posicao_aplicado_momento_descarte,
       p.vida_atual :: text                                             as total_vidas,
       func_pneu_format_sulco(p.altura_sulco_interno)                   as sulco_interno,
       func_pneu_format_sulco(p.altura_sulco_central_interno)           as sulco_central_interno,
       func_pneu_format_sulco(p.altura_sulco_central_externo)           as sulco_central_externo,
       func_pneu_format_sulco(p.altura_sulco_externo)                   as sulco_externo,
       func_pneu_format_sulco(least(p.altura_sulco_externo, p.altura_sulco_central_externo,
                                    p.altura_sulco_central_interno,
                                    p.altura_sulco_interno))            as menor_sulco,
       p.dot                                                            as dot,
       mmde.motivo                                                      as motivo_descarte,
       md.url_imagem_descarte_1                                         as foto_1,
       md.url_imagem_descarte_2                                         as foto_2,
       md.url_imagem_descarte_3                                         as foto_3,
       coalesce(m.observacao ::text, '-')                               as observacao_movimentacao,
       coalesce(mp.observacao :: text, '-')                             as observacao_geral
from pneu p
         join modelo_pneu mop on p.cod_modelo = mop.codigo
         join marca_pneu map on mop.cod_marca = map.codigo
         join dimensao_pneu dp on p.cod_dimensao = dp.codigo
         join unidade u on p.cod_unidade = u.codigo
         left join modelo_banda mob on p.cod_modelo_banda = mob.codigo
         left join marca_banda mab on mob.cod_marca = mab.codigo
         left join movimentacao_processo mp on p.cod_unidade = mp.cod_unidade
         left join movimentacao m on mp.codigo = m.cod_movimentacao_processo
         left join movimentacao_origem mo on m.codigo = mo.cod_movimentacao
         left join movimentacao_destino md on m.codigo = md.cod_movimentacao
         left join colaborador c on mp.cpf_responsavel = c.cpf
         left join movimentacao_motivo_descarte_empresa mmde
                   on md.cod_motivo_descarte = mmde.codigo and c.cod_empresa = mmde.cod_empresa
         left join pneu_posicao_nomenclatura_empresa ppne
                   on ppne.cod_diagrama = mo.cod_diagrama
                       and ppne.posicao_prolog = mo.posicao_pneu_origem
                       and ppne.cod_empresa = u.cod_empresa
         left join veiculo v on v.codigo = mo.cod_veiculo
where p.cod_unidade :: text like any (f_cod_unidades)
  and p.status = 'DESCARTE'
  and m.cod_pneu = p.codigo
  and md.tipo_destino = 'DESCARTE'
  and (mp.data_hora at time zone tz_unidade(mp.cod_unidade)) :: date >= f_data_inicial
  and (mp.data_hora at time zone tz_unidade(mp.cod_unidade)) :: date <= f_data_final
order by u.nome;
$$;



CREATE OR REPLACE FUNCTION FUNC_MOVIMENTACAO_RELATORIO_DADOS_GERAIS(F_COD_UNIDADES BIGINT[],
                                                                    F_DATA_INICIAL DATE,
                                                                    F_DATA_FINAL DATE)
    RETURNS TABLE
            (
                "CÓDIGO PROCESSO MOVIMENTAÇÃO" TEXT,
                "CÓDIGO MOVIMENTAÇÃO"          TEXT,
                "UNIDADE"                      TEXT,
                "DATA E HORA"                  TEXT,
                "CPF DO RESPONSÁVEL"           TEXT,
                "NOME"                         TEXT,
                "PNEU"                         TEXT,
                "MARCA"                        TEXT,
                "MODELO"                       TEXT,
                "BANDA APLICADA"               TEXT,
                "MEDIDAS"                      TEXT,
                "SULCO INTERNO"                TEXT,
                "SULCO CENTRAL INTERNO"        TEXT,
                "SULCO CENTRAL EXTERNO"        TEXT,
                "SULCO EXTERNO"                TEXT,
                "MENOR SULCO"                  TEXT,
                "VIDA PNEU"                    TEXT,
                "ORIGEM"                       TEXT,
                "PLACA DE ORIGEM"              TEXT,
                "IDENTIFICADOR FROTA ORIGEM"   TEXT,
                "POSIÇÃO DE ORIGEM"            TEXT,
                "DESTINO"                      TEXT,
                "PLACA DE DESTINO"             TEXT,
                "IDENTIFICADOR FROTA DESTINO"  TEXT,
                "POSIÇÃO DE DESTINO"           TEXT,
                "MOTIVO DA MOVIMENTAÇÃO"       TEXT,
                "KM MOVIMENTAÇÃO"              TEXT,
                "RECAPADORA DESTINO"           TEXT,
                "CÓDIGO COLETA"                TEXT,
                "SERVIÇOS APLICADOS"           TEXT,
                "CUSTO DOS SERVIÇOS"           TEXT,
                "OBS. MOVIMENTAÇÃO"            TEXT,
                "OBS. GERAL"                   TEXT
            )
    LANGUAGE SQL
AS
$$
SELECT MOVP.CODIGO :: TEXT                                                                               AS COD_PROCESSO_MOVIMENTACAO,
       M.CODIGO :: TEXT                                                                                  AS COD_MOVIMENTACAO,
       U.NOME                                                                                            AS NOME_UNIDADE,
       TO_CHAR((MOVP.DATA_HORA AT TIME ZONE TZ_UNIDADE(MOVP.COD_UNIDADE)), 'DD/MM/YYYY HH24:MI') :: TEXT AS DATA_HORA,
       LPAD(MOVP.CPF_RESPONSAVEL :: TEXT, 11, '0')                                                       AS CPF_COLABORADOR,
       C.NOME                                                                                            AS NOME_COLABORADOR,
       P.CODIGO_CLIENTE                                                                                  AS PNEU,
       MAP.NOME                                                                                          AS NOME_MARCA_PNEU,
       MP.NOME                                                                                           AS NOME_MODELO_PNEU,
       F_IF(MARB.CODIGO IS NULL, 'Nunca Recapado',
            MARB.NOME || ' - ' || MODB.NOME)                                                             AS BANDA_APLICADA,
       ((((DP.LARGURA || '/' :: TEXT) || DP.ALTURA) || ' R' :: TEXT) || DP.ARO)                          AS MEDIDAS,
       FUNC_PNEU_FORMAT_SULCO(M.SULCO_INTERNO)                                                           AS SULCO_INTERNO,
       FUNC_PNEU_FORMAT_SULCO(M.SULCO_CENTRAL_INTERNO)                                                   AS SULCO_CENTRAL_INTERNO,
       FUNC_PNEU_FORMAT_SULCO(M.SULCO_CENTRAL_EXTERNO)                                                   AS SULCO_CENTRAL_EXTERNO,
       FUNC_PNEU_FORMAT_SULCO(M.SULCO_EXTERNO)                                                           AS SULCO_EXTERNO,
       FUNC_PNEU_FORMAT_SULCO(LEAST(M.SULCO_EXTERNO,
                                    M.SULCO_CENTRAL_EXTERNO,
                                    M.SULCO_CENTRAL_INTERNO,
                                    M.SULCO_INTERNO))                                                    AS MENOR_SULCO,
       PVN.NOME :: TEXT                                                                                  AS VIDA_PNEU,
       O.TIPO_ORIGEM                                                                                     AS ORIGEM,
       COALESCE(V_ORIGEM.PLACA, '-')                                                                     AS PLACA_ORIGEM,
       COALESCE(VORIGEM.IDENTIFICADOR_FROTA, '-')                                                        AS IDENTIFICADOR_FROTA_ORIGEM,
       COALESCE(NOMENCLATURA_ORIGEM.NOMENCLATURA, '-')                                                   AS POSICAO_ORIGEM,
       D.TIPO_DESTINO                                                                                    AS DESTINO,
       COALESCE(V_DESTINO.PLACA, '-')                                                                    AS PLACA_DESTINO,
       COALESCE(VDESTINO.IDENTIFICADOR_FROTA, '-')                                                       AS IDENTIFICADOR_PLACA_DESTINO,
       COALESCE(NOMENCLATURA_DESTINO.NOMENCLATURA, '-')                                                  AS POSICAO_DESTINO,
       COALESCE(MMM.MOTIVO, '-')                                                                         AS MOTIVO_DA_MOVIMENTACAO,
       COALESCE(O.KM_VEICULO, D.KM_VEICULO) :: TEXT                                                      AS KM_COLETADO_MOVIMENTACAO,
       COALESCE(R.NOME, '-')                                                                             AS RECAPADORA_DESTINO,
       COALESCE(NULLIF(TRIM(D.COD_COLETA), ''), '-')                                                     AS COD_COLETA_RECAPADORA,
       CASE
           WHEN O.TIPO_ORIGEM = 'ANALISE' AND D.TIPO_DESTINO <> 'DESCARTE'
               THEN
               (SELECT COALESCE(STRING_AGG(TRIM(PTS.NOME), ', ')::TEXT, '-')
                FROM MOVIMENTACAO_PNEU_SERVICO_REALIZADO MPSR
                         JOIN PNEU_SERVICO_REALIZADO PSR ON MPSR.COD_SERVICO_REALIZADO = PSR.CODIGO
                         JOIN PNEU_TIPO_SERVICO PTS ON PSR.COD_TIPO_SERVICO = PTS.CODIGO
                WHERE MPSR.COD_MOVIMENTACAO = M.CODIGO
                ORDER BY M.CODIGO)
           ELSE
               '-' :: TEXT
           END                                                                                           AS SERVICOS_APLICADOS,
       CASE
           WHEN O.TIPO_ORIGEM = 'ANALISE' AND D.TIPO_DESTINO <> 'DESCARTE'
               THEN
               (SELECT COALESCE(
                               STRING_AGG(
                                       CONCAT('R$', CAST(PSR.CUSTO AS TEXT)
                                           ),
                                       ', ')::TEXT,
                               '-')
                FROM MOVIMENTACAO_PNEU_SERVICO_REALIZADO MPSR
                         JOIN PNEU_SERVICO_REALIZADO PSR ON MPSR.COD_SERVICO_REALIZADO = PSR.CODIGO
                WHERE MPSR.COD_MOVIMENTACAO = M.CODIGO
                ORDER BY M.CODIGO)
           ELSE
               '-' :: TEXT
           END                                                                                           AS CUSTO_DO_SERVICO,
       COALESCE(NULLIF(TRIM(M.OBSERVACAO), ''), '-')                                                     AS OBSERVACAO_MOVIMENTACAO,
       COALESCE(NULLIF(TRIM(MOVP.OBSERVACAO), ''), '-')                                                  AS OBSERVACAO_GERAL
FROM MOVIMENTACAO_PROCESSO MOVP
         JOIN MOVIMENTACAO M ON MOVP.CODIGO = M.COD_MOVIMENTACAO_PROCESSO AND MOVP.COD_UNIDADE = M.COD_UNIDADE
         JOIN MOVIMENTACAO_DESTINO D ON M.CODIGO = D.COD_MOVIMENTACAO
         JOIN PNEU P ON P.CODIGO = M.COD_PNEU
         JOIN MOVIMENTACAO_ORIGEM O ON M.CODIGO = O.COD_MOVIMENTACAO
         JOIN UNIDADE U ON U.CODIGO = MOVP.COD_UNIDADE
         JOIN EMPRESA E ON U.COD_EMPRESA = E.CODIGO
         JOIN COLABORADOR C ON MOVP.CPF_RESPONSAVEL = C.CPF
         JOIN DIMENSAO_PNEU DP ON DP.CODIGO = P.COD_DIMENSAO
         JOIN MODELO_PNEU MP ON MP.CODIGO = P.COD_MODELO AND MP.COD_EMPRESA = U.COD_EMPRESA
         JOIN MARCA_PNEU MAP ON MAP.CODIGO = MP.COD_MARCA
         JOIN PNEU_VIDA_NOMENCLATURA PVN ON PVN.COD_VIDA = M.VIDA
         JOIN VEICULO V_ORIGEM ON V_ORIGEM.CODIGO = O.COD_VEICULO
         JOIN VEICULO V_DESTINO ON V_DESTINO.CODIGO = D.COD_VEICULO
    -- Terá recapadora apenas se foi movido para análise.
         LEFT JOIN RECAPADORA R ON R.CODIGO = D.COD_RECAPADORA_DESTINO

    -- Pode não possuir banda.
         LEFT JOIN MODELO_BANDA MODB ON MODB.CODIGO = P.COD_MODELO_BANDA
         LEFT JOIN MARCA_BANDA MARB ON MARB.CODIGO = MODB.COD_MARCA

    -- Joins para buscar a nomenclatura da posição do pneu na placa de ORIGEM, que a unidade pode não possuir.
         LEFT JOIN VEICULO VORIGEM
                   ON O.COD_VEICULO = VORIGEM.CODIGO
         LEFT JOIN VEICULO_TIPO VTORIGEM ON E.CODIGO = VTORIGEM.COD_EMPRESA AND VTORIGEM.CODIGO = VORIGEM.COD_TIPO
         LEFT JOIN VEICULO_DIAGRAMA VDORIGEM ON VTORIGEM.COD_DIAGRAMA = VDORIGEM.CODIGO
         LEFT JOIN PNEU_POSICAO_NOMENCLATURA_EMPRESA NOMENCLATURA_ORIGEM
                   ON NOMENCLATURA_ORIGEM.COD_EMPRESA = P.COD_EMPRESA
                       AND NOMENCLATURA_ORIGEM.COD_DIAGRAMA = VDORIGEM.CODIGO
                       AND NOMENCLATURA_ORIGEM.POSICAO_PROLOG = O.POSICAO_PNEU_ORIGEM

    -- Joins para buscar a nomenclatura da posição do pneu na placa de DESTINO, que a unidade pode não possuir.
         LEFT JOIN VEICULO VDESTINO
                   ON D.COD_VEICULO = VDESTINO.CODIGO
         LEFT JOIN VEICULO_TIPO VTDESTINO ON E.CODIGO = VTDESTINO.COD_EMPRESA AND VTDESTINO.CODIGO = VDESTINO.COD_TIPO
         LEFT JOIN VEICULO_DIAGRAMA VDDESTINO ON VTDESTINO.COD_DIAGRAMA = VDDESTINO.CODIGO
         LEFT JOIN PNEU_POSICAO_NOMENCLATURA_EMPRESA NOMENCLATURA_DESTINO
                   ON NOMENCLATURA_DESTINO.COD_EMPRESA = P.COD_EMPRESA
                       AND NOMENCLATURA_DESTINO.COD_DIAGRAMA = VDDESTINO.CODIGO
                       AND NOMENCLATURA_DESTINO.POSICAO_PROLOG = D.POSICAO_PNEU_DESTINO

    -- Joins para buscar o motivo da movimentação.
         LEFT JOIN MOVIMENTACAO_MOTIVO_MOVIMENTO_RESPOSTA MMMR ON MMMR.COD_MOVIMENTACAO = M.CODIGO
         LEFT JOIN MOVIMENTACAO_MOTIVO_MOVIMENTO MMM ON MMM.CODIGO = MMMR.COD_MOTIVO_MOVIMENTO
WHERE MOVP.COD_UNIDADE = ANY (F_COD_UNIDADES)
  AND (MOVP.DATA_HORA AT TIME ZONE TZ_UNIDADE(MOVP.COD_UNIDADE)) :: DATE BETWEEN F_DATA_INICIAL AND F_DATA_FINAL
ORDER BY U.CODIGO, MOVP.DATA_HORA DESC
$$;



WITH PLACA AS (
    SELECT ARRAY ['QHW1797', 'FGK4051'] AS PLACA
),

     DADOS AS (
         (select 'MOVIMENTAÇÃO'                                       as processo,
                 mp.data_hora at time zone tz_unidade(mp.cod_unidade) as data_hora,
                 coalesce(v_origem.placa, v_destino.placa)            as placa,
                 coalesce(o.km_veiculo, d.km_veiculo)                 as km_coletado
          from movimentacao_processo mp
                   join movimentacao m on mp.codigo = m.cod_movimentacao_processo and mp.cod_unidade = m.cod_unidade
                   join movimentacao_destino d on m.codigo = d.cod_movimentacao
                   join veiculo v_destino on v_destino.codigo = d.cod_veiculo
                   join movimentacao_origem o on m.codigo = o.cod_movimentacao
                   join veiculo v_origem on v_origem.codigo = o.cod_veiculo
          where (select coalesce(v_origem.placa::text, v_destino.placa::text) = any (p.placa) from placa p)
          group by mp.cod_unidade, mp.codigo, v_origem.placa, v_destino.placa, o.km_veiculo, d.km_veiculo, mp.data_hora
          order by mp.data_hora asc)
         UNION ALL
         (select 'CHECKLIST'                                        as processo,
                 c.data_hora at time zone tz_unidade(c.cod_unidade) as data_hora,
                 c.placa_veiculo                                    as placa,
                 c.km_veiculo                                       as km_coletado
          from checklist c
          where (select c.placa_veiculo::text = any (p.placa) from placa p)
          order by c.data_hora asc)
         UNION ALL
         (select 'AFERIÇÃO'                                         as processo,
                 a.data_hora at time zone tz_unidade(a.cod_unidade) as data_hora,
                 a.placa_veiculo                                    as placa,
                 a.km_veiculo                                       as km_coletado
          from afericao a
          where (select a.placa_veiculo::text = any (p.placa) from placa p)
          order by a.data_hora asc)
         UNION ALL
         (select 'FECHAMENTO SERVIÇO PNEU'                          as processo,
                 a.data_hora at time zone tz_unidade(a.cod_unidade) as data_hora,
                 a.placa_veiculo                                    as placa,
                 am.km_momento_conserto                             as km_coletado
          from afericao a
                   join afericao_manutencao am on am.cod_afericao = a.codigo
          where (select a.placa_veiculo::text = any (p.placa) from placa p)
            and data_hora_resolucao is not null
          order by a.data_hora asc)
         UNION ALL
         (select 'FECHAMENTO ITEM CHECKLIST'                        as processo,
                 c.data_hora at time zone tz_unidade(c.cod_unidade) as data_hora,
                 c.placa_veiculo                                    as placa,
                 cosi.km                                            as km_coletado
          from checklist c
                   join checklist_ordem_servico cos on cos.cod_checklist = c.codigo
                   join checklist_ordem_servico_itens cosi
                        on cosi.cod_os = cos.codigo and cosi.cod_unidade = cos.cod_unidade
          where (select c.placa_veiculo::text = any (p.placa) from placa p)
            and cosi.status_resolucao = 'R'
          order by c.data_hora asc)
         UNION ALL
         (select 'TRANSFERÊNCIA DE VEÍCULOS'                                                               as processo,
                 vtp.data_hora_transferencia_processo at time zone tz_unidade(vtp.cod_unidade_colaborador) as data_hora,
                 v.placa                                                                                   as placa,
                 vti.km_veiculo_momento_transferencia                                                      as km_coletado
          from veiculo_transferencia_processo vtp
                   join veiculo_transferencia_informacoes vti on vtp.codigo = vti.cod_processo_transferencia
                   join veiculo v on vti.cod_veiculo = v.codigo
          where (select v.placa::text = any (p.placa) from placa p))
     )

select d.*,
       v.km                 as km_atual,
       v.km - d.km_coletado as diferenca_atual_coletado
from dados d
         join veiculo v on v.placa = d.placa
order by d.placa asc, d.data_hora asc;



create or replace function func_pneu_calcula_km_aplicacao_remocao_pneu(f_cod_pneu bigint,
                                                                       f_vida_pneu integer)
    returns numeric
    language sql
as
$$
with movimentacoes_vida_pneu as (
    select mp.data_hora    as data_hora_movimentacao,
           mo.tipo_origem  as tipo_origem,
           md.tipo_destino as tipo_destino,
           v_destino.placa as placa_destino,
           md.km_veiculo   as km_veiculo_destino,
           v_origem.placa  as placa_origem,
           mo.km_veiculo   as km_veiculo_origem
    from movimentacao_processo mp
             join movimentacao m on mp.codigo = m.cod_movimentacao_processo
             join movimentacao_origem mo on m.codigo = mo.cod_movimentacao
             join veiculo v_origem on v_origem.codigo = mo.cod_veiculo
             join movimentacao_destino md on m.codigo = md.cod_movimentacao
             join veiculo v_destino on v_destino.codigo = md.cod_veiculo
    where (mo.tipo_origem = 'EM_USO' or md.tipo_destino = 'EM_USO')
      and m.cod_pneu = f_cod_pneu
      and m.vida = f_vida_pneu
),

     afericoes_vida_pneu as (
         select a.data_hora     as data_hora_afericao,
                a.placa_veiculo as placa_afericao,
                a.km_veiculo    as km_veiculo_afericao
         from afericao a
                  join afericao_valores av on av.cod_afericao = a.codigo
         where a.tipo_processo_coleta = 'PLACA'
           and av.cod_pneu = f_cod_pneu
           and av.vida_momento_afericao = f_vida_pneu
     ),

     kms_primeira_aplicacao_ate_primeira_afericao as (
         select sum((select avp.km_veiculo_afericao
                     from afericoes_vida_pneu avp
                     where avp.placa_afericao = pvp.placa_destino
                       and avp.data_hora_afericao > pvp.data_hora_movimentacao
                     order by avp.data_hora_afericao
                     limit 1) - pvp.km_veiculo_destino) as km_percorrido
         from movimentacoes_vida_pneu pvp
              -- Saiu de qualquer origem e foi aplicado no veículo.
         where pvp.tipo_origem <> 'EM_USO'
           and pvp.tipo_destino = 'EM_USO'
     ),

     kms_ultima_afericao_ate_remocao as (
         select sum(pvp.km_veiculo_origem - (select avp.km_veiculo_afericao
                                             from afericoes_vida_pneu avp
                                             where avp.placa_afericao = pvp.placa_origem
                                               and avp.data_hora_afericao < pvp.data_hora_movimentacao
                                             order by avp.data_hora_afericao desc
                                             limit 1)) as km_percorrido
         from movimentacoes_vida_pneu pvp
              -- Saiu do veículo e foi movido para qualquer outro destino que não veículo.
         where pvp.tipo_origem = 'EM_USO'
           and pvp.tipo_destino <> 'EM_USO'
     )

select coalesce((select aplicacao.km_percorrido
                 from kms_primeira_aplicacao_ate_primeira_afericao aplicacao), 0)
           +
       coalesce((select remocao.km_percorrido
                 from kms_ultima_afericao_ate_remocao remocao), 0) as km_total_aplicacao_remocao;
$$;



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
            (select distinct on (mp.codigo) 'MOVIMENTACAO'                            as processo,
                                            m.codigo                                  as codigo,
                                            mp.data_hora at time zone tz_unidade(mp.cod_unidade)
                                                                                      as data_hora,
                                            coalesce(v_origem.placa, v_destino.placa) as placa,
                                            coalesce(mo.km_veiculo, md.km_veiculo)    as km_coletado
             from movimentacao_processo mp
                      join movimentacao m on mp.codigo = m.cod_movimentacao_processo
                 and mp.cod_unidade = m.cod_unidade
                      join movimentacao_destino md on m.codigo = md.cod_movimentacao
                      join veiculo v_destino on v_destino.codigo = md.cod_veiculo
                      join movimentacao_origem mo on m.codigo = mo.cod_movimentacao
                      join veiculo v_origem on v_origem.codigo = mo.cod_veiculo
             where coalesce(mo.cod_veiculo, md.cod_veiculo) = f_cod_veiculo
             group by m.codigo, mp.cod_unidade, mp.codigo, v_origem.placa, v_destino.placa, mo.km_veiculo,
                      md.km_veiculo)
            union
            (select 'CHECKLIST'                                        as processo,
                    c.codigo                                           as codigo,
                    c.data_hora at time zone tz_unidade(c.cod_unidade) as data_hora,
                    c.placa_veiculo                                    as placa,
                    c.km_veiculo                                       as km_coletado
             from checklist c
             where c.cod_veiculo = f_cod_veiculo
               and c.cod_unidade = any (v_cod_unidades)
             union
             (select 'AFERICAO'                                         as processo,
                     a.codigo                                           as codigo,
                     a.data_hora at time zone tz_unidade(a.cod_unidade) as data_hora,
                     a.placa_veiculo                                    as placa,
                     a.km_veiculo                                       as km_coletado
              from afericao a
              where a.cod_veiculo = f_cod_veiculo
                and a.cod_unidade = any (v_cod_unidades)
             )
             union
             (select 'FECHAMENTO_SERVICO_PNEU'                                     as processo,
                     am.codigo                                                     as codigo,
                     am.data_hora_resolucao at time zone tz_unidade(a.cod_unidade) as data_hora,
                     a.placa_veiculo                                               as placa,
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
                     c.placa_veiculo                                                as placa,
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
                     v.placa                                 as placa,
                     vti.km_veiculo_momento_transferencia    as km_coletado
              from veiculo_transferencia_processo vtp
                       join veiculo_transferencia_informacoes vti on vtp.codigo = vti.cod_processo_transferencia
                       join veiculo v on vti.cod_veiculo = v.codigo
              where v.codigo = f_cod_veiculo
                and vtp.cod_unidade_destino = any (v_cod_unidades)
                and vtp.cod_unidade_origem = any (v_cod_unidades)
             )
             union
             (select 'SOCORRO_EM_ROTA'                                              as processo,
                     sra.cod_socorro_rota                                           as codigo,
                     sra.data_hora_abertura at time zone tz_unidade(sr.cod_unidade) as data_hora,
                     v.placa                                                        as placa,
                     sra.km_veiculo_abertura                                        as km_coletado
              from socorro_rota_abertura sra
                       join veiculo v on v.codigo = sra.cod_veiculo_problema
                       join socorro_rota sr on sra.cod_socorro_rota = sr.codigo
              where v.codigo = f_cod_veiculo
                and sr.cod_unidade = any (v_cod_unidades)
             )
             union
             (select distinct on (func.km_veiculo) 'EDICAO_DE_VEICULOS'  as processo,
                                                   func.codigo_historico as codigo,
                                                   func.data_hora_edicao as data_hora,
                                                   func.placa            as placa,
                                                   func.km_veiculo       as km_coletado
              from func_veiculo_listagem_historico_edicoes(f_cod_empresa, f_cod_veiculo) as func
              where func.codigo_historico is not null
             )
            )
        )
        select d.processo,
               d.codigo,
               d.data_hora,
               d.placa,
               d.km_coletado,
               d.km_coletado - lag(d.km_coletado) over (order by d.data_hora) as variacao_entre_coletas,
               v.km                                                           as km_atual,
               (v.km - d.km_coletado)                                         as diferenca_atual_coletado
        from dados d
                 join veiculo v on v.placa = d.placa
        where f_if(v_check_data, d.data_hora :: date between f_data_inicial and f_data_final, true)
        order by row_number() over () desc;
end;
$$;



CREATE OR REPLACE FUNCTION SUPORTE.FUNC_MOVIMENTACAO_ALTERA_KM_MOVIMENTACAO(F_COD_MOVIMENTACAO BIGINT,
                                                                            F_PLACA VARCHAR(7),
                                                                            F_KM_ATUALIZADO INTEGER,
                                                                            OUT AVISO_KM_ATUALIZADO TEXT) RETURNS TEXT
    SECURITY DEFINER
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    QTD_LINHAS_ATUALIZADAS  BIGINT;
    TIPO                    TEXT    := 'EM_USO';
    MOVIMENTACAO_ATUALIZADA TEXT;
    EXISTE_DESTINO          BOOLEAN := TRUE;
    EXISTE_ORIGEM           BOOLEAN := TRUE;
BEGIN
    PERFORM SUPORTE.FUNC_HISTORICO_SALVA_EXECUCAO();
    PERFORM FUNC_GARANTE_PLACA_CADASTRADA(F_PLACA);
    PERFORM FUNC_GARANTE_NOVO_KM_MENOR_QUE_ATUAL_VEICULO(
                (SELECT V.COD_UNIDADE FROM VEICULO V WHERE V.PLACA = F_PLACA),
                F_PLACA,
                F_KM_ATUALIZADO);

    -- VERIFICA SE KM É NULL OU IGUAL A 0.
    IF (F_KM_ATUALIZADO IS NULL OR F_KM_ATUALIZADO <= 0)
    THEN
        RAISE EXCEPTION 'O VALOR ATRIBUÍDO PARA QUILOMETRAGEM NÃO PODE SER VAZIO OU MENOR OU IGUAL A ZERO(0).';
    END IF;

    -- VERIFICA SE COD_MOVIMENTACAO É NULL.
    IF (F_COD_MOVIMENTACAO IS NULL)
    THEN
        RAISE EXCEPTION 'O VALOR ATRIBUÍDO PARA CÓDIGO MOVIMENTAÇÃO NÃO PODE SER VAZIO.';
    END IF;

    -- VERIFICA SE OS DADOS EXISTEM NA TABELA MOVIMENTACAO_DESTINO E, CASO NÃO ENCONTRE, SETA A FLAG.
    IF NOT EXISTS(
            SELECT MD.COD_MOVIMENTACAO
            FROM MOVIMENTACAO_DESTINO MD
                     INNER JOIN VEICULO V_DESTINO ON V_DESTINO.CODIGO = MD.COD_VEICULO
            WHERE MD.COD_MOVIMENTACAO = F_COD_MOVIMENTACAO
              AND V_DESTINO.PLACA = F_PLACA
              AND MD.TIPO_DESTINO = TIPO
        )
    THEN
        EXISTE_DESTINO = FALSE;
    END IF;

    -- VERIFICA SE OS DADOS EXISTEM NA TABELA MOVIMENTACAO_ORIGEM E, CASO NÃO ENCONTRE, SETA A FLAG.
    IF NOT EXISTS(
            SELECT MO.COD_MOVIMENTACAO
            FROM MOVIMENTACAO_ORIGEM MO
                     INNER JOIN VEICULO V_ORIGEM ON V_ORIGEM.CODIGO = MO.COD_VEICULO
            WHERE MO.COD_MOVIMENTACAO = F_COD_MOVIMENTACAO
              AND V_ORIGEM.PLACA = F_PLACA
              AND MO.TIPO_ORIGEM = TIPO
        )
    THEN
        EXISTE_ORIGEM = FALSE;
    END IF;

    -- VERIFICA SE A MOVIMENTAÇÃO EXISTE NAS TABELAS MOVIMENTACAO_DESTINO E MOVIMENTACAO_ORIGEM.
    IF (EXISTE_DESTINO IS FALSE AND EXISTE_ORIGEM IS FALSE)
    THEN
        RAISE EXCEPTION 'NÃO FOI POSSÍVEL ATUALIZAR A QUILOMETRAGEM! FAVOR VERIFICAR OS SEGUINTES DADOS:
                          CODIGO MOVIMENTAÇÃO: %, PLACA: %',
            F_COD_MOVIMENTACAO, F_PLACA;
    END IF;

    --VERIFICA SE A MOVIMENTAÇÃO EXISTE NAS TABELAS MOVIMENTACAO_DESTINO E MOVIMENTACAO_ORIGEM.
    IF (EXISTE_DESTINO IS FALSE AND EXISTE_ORIGEM IS FALSE)
    THEN
        RAISE EXCEPTION 'NÃO FOI POSSÍVEL ATUALIZAR A QUILOMETRAGEM! FAVOR VERIFICAR OS SEGUINTES DADOS:
                          CODIGO MOVIMENTAÇÃO: %, PLACA: %',
            F_COD_MOVIMENTACAO, F_PLACA;
    END IF;

    -- REALIZA UPDATE NA TABELA CORRESPONDENTE.
    IF (EXISTE_DESTINO AND EXISTE_ORIGEM IS FALSE)
    THEN
        UPDATE MOVIMENTACAO_DESTINO
        SET KM_VEICULO = F_KM_ATUALIZADO
        WHERE COD_MOVIMENTACAO = F_COD_MOVIMENTACAO
          AND TIPO_DESTINO = TIPO;
        MOVIMENTACAO_ATUALIZADA = 'DESTINO';
        GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;
    ELSEIF (EXISTE_DESTINO IS FALSE AND EXISTE_ORIGEM)
    THEN
        UPDATE MOVIMENTACAO_ORIGEM
        SET KM_VEICULO = F_KM_ATUALIZADO
        WHERE COD_MOVIMENTACAO = F_COD_MOVIMENTACAO
          AND TIPO_ORIGEM = TIPO;
        MOVIMENTACAO_ATUALIZADA = 'ORIGEM';
        GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;
    ELSE
        UPDATE MOVIMENTACAO_DESTINO
        SET KM_VEICULO = F_KM_ATUALIZADO
        WHERE COD_MOVIMENTACAO = F_COD_MOVIMENTACAO
          AND TIPO_DESTINO = TIPO;
        MOVIMENTACAO_ATUALIZADA = 'DESTINO';
        GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

        UPDATE MOVIMENTACAO_ORIGEM
        SET KM_VEICULO = F_KM_ATUALIZADO
        WHERE COD_MOVIMENTACAO = F_COD_MOVIMENTACAO
          AND TIPO_ORIGEM = TIPO;
        MOVIMENTACAO_ATUALIZADA = 'ORIGEM';
        GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;
    END IF;

    IF (QTD_LINHAS_ATUALIZADAS > 0)
    THEN
        SELECT 'ATUALIZAÇÃO REALIZADA COM SUCESSO EM MOVIMENTAÇÃO '
                   || MOVIMENTACAO_ATUALIZADA
                   || '! CÓDIGO MOVIMENTAÇÃO: '
                   || F_COD_MOVIMENTACAO
                   || ', PLACA: '
                   || F_PLACA
                   || ', KM_VEICULO: '
                   || F_KM_ATUALIZADO
        INTO AVISO_KM_ATUALIZADO;
    ELSE
        RAISE EXCEPTION 'NÃO FOI POSSÍVEL ATUALIZAR A QUILOMETRAGEM! FAVOR, VERIFICAR OS SEGUINTES DADOS:
                          CODIGO MOVIMENTAÇÃO: %, PLACA: % ' ,
            F_COD_MOVIMENTACAO, F_PLACA;

    END IF;
END
$$;