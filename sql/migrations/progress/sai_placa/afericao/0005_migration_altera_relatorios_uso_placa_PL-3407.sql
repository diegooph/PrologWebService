create or replace function func_afericao_relatorio_exportacao_protheus(f_cod_unidades bigint[],
                                                                       f_cod_veiculos bigint[],
                                                                       f_data_inicial date,
                                                                       f_data_final date)
    returns table
            (
                codigo_afericao      bigint,
                cabecalho_placa      text,
                placa                varchar(7),
                data                 text,
                hora                 text,
                cabecalho_pneu       text,
                codigo_cliente_pneu  text,
                nomenclatura_posicao text,
                calibragem_aferida   real,
                calibragem_realizada real,
                sulco_interno        real,
                sulco_central        real,
                sulco_externo        real
            )
    language plpgsql
as
$$
begin
    return query
        select a.codigo                                                                        as codigo_afericao,
               'TTO'                                                                           as cabecalho_placa,
               v.placa                                                                         as placa,
               to_char(a.data_hora at time zone tz_unidade(a.cod_unidade), 'DD/MM/YYYY')::text as data,
               to_char(a.data_hora at time zone tz_unidade(a.cod_unidade), 'HH24:MI')::text    as hora,
               'TTP'                                                                           as cabecalho_pneu,
               lpad(p.codigo_cliente::text, 7, '0')                                            as codigo_fogo_pneu,
               remove_extra_spaces(coalesce(ppne.nomenclatura::text, ''), true)
                                                                                               as nomenclatura_posicao,
               coalesce(round(cast(av.psi as numeric), 2), -1)::real                           as calibragem_aferida,
               coalesce(round(cast(av.psi as numeric), 2), -1)::real                           as calibragem_realizada,
               coalesce(round(cast(av.altura_sulco_interno as numeric), 2), -1)::real          as altura_sulco_interno,
               coalesce(round(cast(av.altura_sulco_central_interno as numeric), 2), -1)::real  as sulco_central,
               coalesce(round(cast(av.altura_sulco_externo as numeric), 2), -1)::real          as altura_sulco_externo
        from afericao a
                 inner join afericao_valores av on av.cod_afericao = a.codigo
                 inner join pneu p on p.codigo = av.cod_pneu
                 inner join veiculo v on v.codigo = a.cod_veiculo
                 left join pneu_posicao_nomenclatura_empresa ppne
                           on ppne.posicao_prolog = av.posicao and ppne.cod_diagrama = a.cod_diagrama and
                              ppne.cod_empresa = v.cod_empresa
        where a.tipo_processo_coleta = 'PLACA'
          and a.cod_veiculo = any (f_cod_veiculos)
          and a.cod_unidade = any (f_cod_unidades)
          and (a.data_hora at time zone tz_unidade(a.cod_unidade))::date
            between f_data_inicial and f_data_final
        order by a.codigo;
end
$$;

CREATE OR REPLACE FUNCTION
    FUNC_AFERICAO_RELATORIO_CRONOGRAMA_AFERICOES_PLACAS(F_COD_UNIDADES BIGINT[],
                                                        F_DATA_HORA_ATUAL_UTC TIMESTAMP WITH TIME ZONE,
                                                        F_DATA_HORA_GERACAO_RELATORIO TIMESTAMP WITH TIME ZONE)
    RETURNS TABLE
            (
                UNIDADE                              TEXT,
                PLACA                                TEXT,
                "IDENTIFICADOR FROTA"                TEXT,
                "QTD PNEUS APLICADOS"                TEXT,
                "MODELO VEÍCULO"                     TEXT,
                "TIPO VEÍCULO"                       TEXT,
                "STATUS SULCO"                       TEXT,
                "STATUS PRESSÃO"                     TEXT,
                "DATA VENCIMENTO SULCO"              TEXT,
                "DATA VENCIMENTO PRESSÃO"            TEXT,
                "DIAS VENCIMENTO SULCO"              TEXT,
                "DIAS VENCIMENTO PRESSÃO"            TEXT,
                "DIAS DESDE ÚLTIMA AFERIÇÃO SULCO"   TEXT,
                "DATA/HORA ÚLTIMA AFERIÇÃO SULCO"    TEXT,
                "DIAS DESDE ÚLTIMA AFERIÇÃO PRESSÃO" TEXT,
                "DATA/HORA ÚLTIMA AFERIÇÃO PRESSÃO"  TEXT,
                "DATA/HORA GERAÇÃO RELATÓRIO"        TEXT
            )
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    RETURN QUERY
        WITH DADOS AS (
            SELECT U.NOME::TEXT                                                         AS NOME_UNIDADE,
                   V.PLACA::TEXT                                                        AS PLACA_VEICULO,
                   COALESCE(V.IDENTIFICADOR_FROTA::TEXT, '-')                           AS IDENTIFICADOR_FROTA,
                   (SELECT COUNT(VP.COD_PNEU)
                    FROM VEICULO_PNEU VP
                    WHERE VP.COD_VEICULO = V.CODIGO
                    GROUP BY VP.COD_VEICULO)::TEXT                                      AS QTD_PNEUS_APLICADOS,
                   MV.NOME::TEXT                                                        AS NOME_MODELO_VEICULO,
                   VT.NOME::TEXT                                                        AS NOME_TIPO_VEICULO,
                   TO_CHAR(SULCO.DATA_HORA_ULTIMA_AFERICAO_SULCO, 'DD/MM/YYYY HH24:MI') AS DATA_HORA_ULTIMA_AFERICAO_SULCO,
                   TO_CHAR(PRESSAO.DATA_HORA_ULTIMA_AFERICAO_PRESSAO,
                           'DD/MM/YYYY HH24:MI')                                        AS DATA_HORA_ULTIMA_AFERICAO_PRESSAO,
                   TO_CHAR(SULCO.DATA_ULTIMA_AFERICAO_SULCO
                               + (PRU.PERIODO_AFERICAO_SULCO || ' DAYS')::INTERVAL,
                           'DD/MM/YYYY')                                                AS DATA_VENCIMENTO_SULCO,
                   TO_CHAR(PRESSAO.DATA_ULTIMA_AFERICAO_PRESSAO
                               + (PRU.PERIODO_AFERICAO_PRESSAO || ' DAYS')::INTERVAL,
                           'DD/MM/YYYY')                                                AS DATA_VENCIMENTO_PRESSAO,
                   (PRU.PERIODO_AFERICAO_SULCO - SULCO.DIAS)::TEXT                      AS DIAS_VENCIMENTO_SULCO,
                   (PRU.PERIODO_AFERICAO_PRESSAO - PRESSAO.DIAS)::TEXT                  AS DIAS_VENCIMENTO_PRESSAO,
                   SULCO.DIAS::TEXT                                                     AS DIAS_DESDE_ULTIMA_AFERICAO_SULCO,
                   PRESSAO.DIAS::TEXT                                                   AS DIAS_DESDE_ULTIMA_AFERICAO_PRESSAO,
                   F_IF(CONFIG.FORMA_COLETA_DADOS_SULCO <> 'BLOQUEADO'
                            OR CONFIG.FORMA_COLETA_DADOS_SULCO_PRESSAO <> 'BLOQUEADO',
                        TRUE,
                        FALSE)                                                          AS PODE_AFERIR_SULCO,
                   F_IF(CONFIG.FORMA_COLETA_DADOS_PRESSAO <> 'BLOQUEADO'
                            OR CONFIG.FORMA_COLETA_DADOS_SULCO_PRESSAO <> 'BLOQUEADO',
                        TRUE,
                        FALSE)                                                          AS PODE_AFERIR_PRESSAO,
                   F_IF(SULCO.DIAS IS NULL, TRUE,
                        FALSE)                                                          AS SULCO_NUNCA_AFERIDO,
                   F_IF(PRESSAO.DIAS IS NULL, TRUE,
                        FALSE)                                                          AS PRESSAO_NUNCA_AFERIDA,
                   F_IF(SULCO.DIAS > PRU.PERIODO_AFERICAO_SULCO, TRUE,
                        FALSE)                                                          AS AFERICAO_SULCO_VENCIDA,
                   F_IF(PRESSAO.DIAS > PRU.PERIODO_AFERICAO_PRESSAO, TRUE,
                        FALSE)                                                          AS AFERICAO_PRESSAO_VENCIDA
            FROM VEICULO V
                     JOIN MODELO_VEICULO MV
                          ON MV.CODIGO = V.COD_MODELO
                     JOIN VEICULO_TIPO VT
                          ON VT.CODIGO = V.COD_TIPO
                     JOIN FUNC_AFERICAO_GET_CONFIG_TIPO_AFERICAO_VEICULO(V.COD_UNIDADE) CONFIG
                          ON CONFIG.COD_TIPO_VEICULO = V.COD_TIPO
                     LEFT JOIN
                 (SELECT A.COD_VEICULO                                               AS COD_VEICULO_INTERVALO,
                         MAX(A.DATA_HORA AT TIME ZONE
                             TZ_UNIDADE(A.COD_UNIDADE))::DATE                          AS DATA_ULTIMA_AFERICAO_PRESSAO,
                         MAX(A.DATA_HORA AT TIME ZONE
                             TZ_UNIDADE(A.COD_UNIDADE))                                AS DATA_HORA_ULTIMA_AFERICAO_PRESSAO,
                         EXTRACT(DAYS FROM (F_DATA_HORA_ATUAL_UTC) - MAX(A.DATA_HORA)) AS DIAS
                  FROM AFERICAO A
                  WHERE A.TIPO_MEDICAO_COLETADA = 'PRESSAO'
                     OR A.TIPO_MEDICAO_COLETADA = 'SULCO_PRESSAO'
                  GROUP BY A.COD_VEICULO) AS PRESSAO ON PRESSAO.COD_VEICULO_INTERVALO = V.CODIGO
                     LEFT JOIN
                 (SELECT A.COD_VEICULO                                             AS COD_VEICULO_INTERVALO,
                         MAX(A.DATA_HORA AT TIME ZONE
                             TZ_UNIDADE(A.COD_UNIDADE)) :: DATE                      AS DATA_ULTIMA_AFERICAO_SULCO,
                         MAX(A.DATA_HORA AT TIME ZONE
                             TZ_UNIDADE(A.COD_UNIDADE))                              AS DATA_HORA_ULTIMA_AFERICAO_SULCO,
                         EXTRACT(DAYS FROM F_DATA_HORA_ATUAL_UTC - MAX(A.DATA_HORA)) AS DIAS
                  FROM AFERICAO A
                  WHERE A.TIPO_MEDICAO_COLETADA = 'SULCO'
                     OR A.TIPO_MEDICAO_COLETADA = 'SULCO_PRESSAO'
                  GROUP BY A.COD_VEICULO) AS SULCO ON SULCO.COD_VEICULO_INTERVALO = V.CODIGO
                     JOIN PNEU_RESTRICAO_UNIDADE PRU
                          ON PRU.COD_UNIDADE = V.COD_UNIDADE
                     JOIN UNIDADE U
                          ON U.CODIGO = V.COD_UNIDADE
            WHERE V.STATUS_ATIVO = TRUE
              AND V.COD_UNIDADE = ANY (F_COD_UNIDADES)
            ORDER BY U.CODIGO, V.PLACA
        )
             -- Todos os coalesce ficam aqui.
        SELECT D.NOME_UNIDADE                                               AS NOME_UNIDADE,
               D.PLACA_VEICULO                                              AS PLACA_VEICULO,
               D.IDENTIFICADOR_FROTA                                        AS IDENTIFICADOR_FROTA,
               COALESCE(D.QTD_PNEUS_APLICADOS, '-')                         AS QTD_PNEUS_APLICADOS,
               D.NOME_MODELO_VEICULO                                        AS NOME_MODELO_VEICULO,
               D.NOME_TIPO_VEICULO                                          AS NOME_TIPO_VEICULO,
               CASE
                   WHEN NOT D.PODE_AFERIR_SULCO
                       THEN 'BLOQUEADO AFERIÇÃO'
                   WHEN D.SULCO_NUNCA_AFERIDO
                       THEN 'SULCO NUNCA AFERIDO'
                   WHEN D.AFERICAO_SULCO_VENCIDA
                       THEN 'VENCIDO'
                   ELSE 'NO PRAZO'
                   END                                                      AS STATUS_SULCO,
               CASE
                   WHEN NOT D.PODE_AFERIR_PRESSAO
                       THEN 'BLOQUEADO AFERIÇÃO'
                   WHEN D.PRESSAO_NUNCA_AFERIDA
                       THEN 'PRESSÃO NUNCA AFERIDA'
                   WHEN D.AFERICAO_PRESSAO_VENCIDA
                       THEN 'VENCIDO'
                   ELSE 'NO PRAZO'
                   END                                                      AS STATUS_PRESSAO,
               F_IF(D.SULCO_NUNCA_AFERIDO, '-',
                    D.DATA_VENCIMENTO_SULCO)                                AS DATA_VENCIMENTO_SULCO,
               F_IF(D.PRESSAO_NUNCA_AFERIDA, '-',
                    D.DATA_VENCIMENTO_PRESSAO)                              AS DATA_VENCIMENTO_PRESSAO,
               F_IF(D.SULCO_NUNCA_AFERIDO, '-',
                    D.DIAS_VENCIMENTO_SULCO)                                AS DIAS_VENCIMENTO_SULCO,
               F_IF(D.PRESSAO_NUNCA_AFERIDA, '-',
                    D.DIAS_VENCIMENTO_PRESSAO)                              AS DIAS_VENCIMENTO_PRESSAO,
               F_IF(D.SULCO_NUNCA_AFERIDO, '-',
                    D.DIAS_DESDE_ULTIMA_AFERICAO_SULCO)                     AS DIAS_DESDE_ULTIMA_AFERICAO_SULCO,
               COALESCE(D.DATA_HORA_ULTIMA_AFERICAO_SULCO, '-')             AS DATA_HORA_ULTIMA_AFERICAO_SULCO,
               F_IF(D.PRESSAO_NUNCA_AFERIDA, '-',
                    D.DIAS_DESDE_ULTIMA_AFERICAO_PRESSAO)                   AS DIAS_DESDE_ULTIMA_AFERICAO_PRESSAO,
               COALESCE(D.DATA_HORA_ULTIMA_AFERICAO_PRESSAO, '-')           AS DATA_HORA_ULTIMA_AFERICAO_PRESSAO,
               TO_CHAR(F_DATA_HORA_GERACAO_RELATORIO, 'DD/MM/YYYY HH24:MI') AS DATA_HORA_GERACAO_RELATORIO
        FROM DADOS D;
END;
$$;

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
       coalesce(v.placa, '-')                                                           as placa,
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

    -- Se foi aferição de pneu avulso, pode não possuir codigo do veiculo.
         left join veiculo v on v.codigo = a.cod_veiculo

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

CREATE OR REPLACE FUNCTION FUNC_AFERICAO_RELATORIO_QTD_DIAS_PLACAS_VENCIDAS(F_COD_UNIDADES BIGINT[],
                                                                            F_DATA_HOJE_UTC TIMESTAMP WITH TIME ZONE)
    RETURNS TABLE
            (
                UNIDADE                           TEXT,
                PLACA                             TEXT,
                IDENTIFICADOR_FROTA               TEXT,
                PODE_AFERIR_SULCO                 BOOLEAN,
                PODE_AFERIR_PRESSAO               BOOLEAN,
                QTD_DIAS_AFERICAO_SULCO_VENCIDA   INTEGER,
                QTD_DIAS_AFERICAO_PRESSAO_VENCIDA INTEGER
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    AFERICAO_SULCO         VARCHAR := 'SULCO';
    AFERICAO_PRESSAO       VARCHAR := 'PRESSAO';
    AFERICAO_SULCO_PRESSAO VARCHAR := 'SULCO_PRESSAO';
BEGIN
    RETURN QUERY
        WITH VEICULOS_ATIVOS_UNIDADES AS (
            SELECT V.CODIGO
            FROM VEICULO V
            WHERE V.COD_UNIDADE = ANY (F_COD_UNIDADES)
              AND V.STATUS_ATIVO
        ),
             -- As CTEs ULTIMA_AFERICAO_SULCO e ULTIMA_AFERICAO_PRESSAO retornam o codigo de cada veículo e a quantidade de dias
             -- que a aferição de sulco e pressão, respectivamente, estão vencidas. Um número negativo será retornado caso ainda
             -- esteja com a aferição no prazo e ele indicará quantos dias faltam para vencer. Um -20, por exemplo, significa
             -- que a aferição vai vencer em 20 dias.
             ULTIMA_AFERICAO_SULCO AS (
                 SELECT DISTINCT ON (A.COD_VEICULO) A.COD_UNIDADE,
                                                      A.COD_VEICULO              AS COD_VEICULO,
                                                      DATE_PART('DAY', F_DATA_HOJE_UTC - MAX(DATA_HORA))
                                                          -
                                                      (PRU.PERIODO_AFERICAO_SULCO) AS QTD_DIAS_ENTRE_ULTIMA_AFERICAO_SULCO_E_HOJE
                 FROM AFERICAO A
                          JOIN PNEU_RESTRICAO_UNIDADE PRU
                               ON (SELECT V.COD_UNIDADE
                                   FROM VEICULO V
                                   WHERE V.CODIGO = A.COD_VEICULO) = PRU.COD_UNIDADE
                 WHERE A.TIPO_MEDICAO_COLETADA IN (AFERICAO_SULCO, AFERICAO_SULCO_PRESSAO)
                   -- Desse modo nós buscamos a última aferição de cada placa que está ativa nas unidades filtradas, independente
                   -- de onde foram foram aferidas.
                   AND COD_VEICULO = ANY (SELECT VAU.CODIGO
                                            FROM VEICULOS_ATIVOS_UNIDADES VAU)
                 GROUP BY A.DATA_HORA,
                          A.COD_UNIDADE,
                          A.COD_VEICULO,
                          PRU.PERIODO_AFERICAO_SULCO
                 ORDER BY A.COD_VEICULO, A.DATA_HORA DESC
             ),
             ULTIMA_AFERICAO_PRESSAO AS (
                 SELECT DISTINCT ON (A.COD_VEICULO) A.COD_UNIDADE,
                                                      A.COD_VEICULO                AS COD_VEICULO,
                                                      DATE_PART('DAY', F_DATA_HOJE_UTC - MAX(DATA_HORA))
                                                          -
                                                      (PRU.PERIODO_AFERICAO_PRESSAO) AS QTD_DIAS_ENTRE_ULTIMA_AFERICAO_PRESSAO_E_HOJE
                 FROM AFERICAO A
                          JOIN PNEU_RESTRICAO_UNIDADE PRU
                               ON (SELECT V.COD_UNIDADE
                                   FROM VEICULO V
                                   WHERE V.CODIGO = A.COD_VEICULO) = PRU.COD_UNIDADE
                 WHERE A.COD_UNIDADE = ANY (F_COD_UNIDADES)
                   AND A.TIPO_MEDICAO_COLETADA IN (AFERICAO_PRESSAO, AFERICAO_SULCO_PRESSAO)
                   AND COD_VEICULO = ANY (SELECT VAU.CODIGO
                                            FROM VEICULOS_ATIVOS_UNIDADES VAU)
                 GROUP BY A.DATA_HORA,
                          A.COD_UNIDADE,
                          A.COD_VEICULO,
                          PRU.PERIODO_AFERICAO_PRESSAO
                 ORDER BY A.COD_VEICULO, A.DATA_HORA DESC
             ),

             PRE_SELECT AS (
                 SELECT U.NOME                                            AS NOME_UNIDADE,
                        V.PLACA                                           AS PLACA_VEICULO,
                        COALESCE(V.IDENTIFICADOR_FROTA, '-')              AS IDENTIFICADOR_FROTA,
                        COALESCE((
                                     SELECT (FA.FORMA_COLETA_DADOS_SULCO IN ('EQUIPAMENTO', 'MANUAL') OR
                                             FA.FORMA_COLETA_DADOS_SULCO_PRESSAO IN ('EQUIPAMENTO', 'MANUAL'))
                                     FROM FUNC_AFERICAO_GET_CONFIG_TIPO_AFERICAO_VEICULO(V.COD_UNIDADE) FA
                                     WHERE FA.COD_TIPO_VEICULO = V.COD_TIPO), FALSE)
                                                                          AS PODE_AFERIR_SULCO,
                        COALESCE((
                                     SELECT (FA.FORMA_COLETA_DADOS_PRESSAO IN ('EQUIPAMENTO', 'MANUAL') OR
                                             FA.FORMA_COLETA_DADOS_SULCO_PRESSAO IN ('EQUIPAMENTO', 'MANUAL'))
                                     FROM FUNC_AFERICAO_GET_CONFIG_TIPO_AFERICAO_VEICULO(V.COD_UNIDADE) FA
                                     WHERE FA.COD_TIPO_VEICULO = V.COD_TIPO), FALSE)
                                                                          AS PODE_AFERIR_PRESSAO,
                        -- Por conta do filtro no where, agora não é mais a diferença de dias e sim somente as vencidas (ou ainda
                        -- nunca aferidas).
                        UAS.QTD_DIAS_ENTRE_ULTIMA_AFERICAO_SULCO_E_HOJE   AS QTD_DIAS_AFERICAO_SULCO_VENCIDA,
                        UAP.QTD_DIAS_ENTRE_ULTIMA_AFERICAO_PRESSAO_E_HOJE AS QTD_DIAS_AFERICAO_PRESSAO_VENCIDA
                 FROM UNIDADE U
                          JOIN VEICULO V
                               ON V.COD_UNIDADE = U.CODIGO
                          LEFT JOIN ULTIMA_AFERICAO_SULCO UAS
                                    ON UAS.COD_VEICULO = V.CODIGO
                          LEFT JOIN ULTIMA_AFERICAO_PRESSAO UAP
                                    ON UAP.COD_VEICULO = V.CODIGO
                 WHERE
                     -- Se algum dos dois tipos de aferição estiver vencido, retornamos a linha.
                     (UAS.QTD_DIAS_ENTRE_ULTIMA_AFERICAO_SULCO_E_HOJE > 0 OR
                      UAP.QTD_DIAS_ENTRE_ULTIMA_AFERICAO_PRESSAO_E_HOJE > 0)
                 GROUP BY U.NOME,
                          V.PLACA,
                          V.IDENTIFICADOR_FROTA,
                          V.COD_TIPO,
                          V.COD_UNIDADE,
                          UAS.QTD_DIAS_ENTRE_ULTIMA_AFERICAO_SULCO_E_HOJE,
                          UAP.QTD_DIAS_ENTRE_ULTIMA_AFERICAO_PRESSAO_E_HOJE
             )
        SELECT PS.NOME_UNIDADE::TEXT                         AS NOME_UNIDADE,
               PS.PLACA_VEICULO::TEXT                        AS PLACA_VEICULO,
               PS.IDENTIFICADOR_FROTA::TEXT                  AS IDENTIFICADOR_FROTA,
               PS.PODE_AFERIR_SULCO                          AS PODE_AFERIR_SULCO,
               PS.PODE_AFERIR_PRESSAO                        AS PODE_AFERIR_PRESSAO,
               PS.QTD_DIAS_AFERICAO_SULCO_VENCIDA::INTEGER   AS QTD_DIAS_AFERICAO_SULCO_VENCIDA,
               PS.QTD_DIAS_AFERICAO_PRESSAO_VENCIDA::INTEGER AS QTD_DIAS_AFERICAO_PRESSAO_VENCIDA
        FROM PRE_SELECT PS
             -- Para a placa ser exibida, ao menos um dos tipos de aferições, de sulco ou pressão, devem estar habilitadas.
        WHERE PS.PODE_AFERIR_SULCO <> FALSE
           OR PS.PODE_AFERIR_PRESSAO <> FALSE
        ORDER BY PS.QTD_DIAS_AFERICAO_SULCO_VENCIDA DESC,
                 PS.QTD_DIAS_AFERICAO_PRESSAO_VENCIDA DESC;
END;
$$;

CREATE OR REPLACE FUNCTION FUNC_PNEU_RELATORIO_QUANTIDADE_KMS_RODADOS_COM_SERVICOS_ABERTOS(F_COD_UNIDADES BIGINT [])
  RETURNS TABLE(
    PLACA_VEICULO TEXT,
    TOTAL_KM      BIGINT)
LANGUAGE PLPGSQL
AS $$
DECLARE
  TIPO_SERVICO_CALIBRAGEM TEXT := 'calibragem';
  TIPO_SERVICO_INSPECAO   TEXT := 'inspecao';
BEGIN
  RETURN QUERY
  WITH DADOS AS (SELECT
                   DISTINCT ON (V.PLACA)
                   V.PLACA AS PLACA_VEICULO,
                   AM.KM_MOMENTO_CONSERTO - A.KM_VEICULO AS TOTAL_KM
                 FROM AFERICAO_MANUTENCAO AM
                   JOIN AFERICAO A ON A.CODIGO = AM.COD_AFERICAO
                   JOIN VEICULO V ON V.CODIGO = A.COD_VEICULO
                   JOIN VEICULO_PNEU VP ON VP.COD_VEICULO = A.COD_VEICULO
                                           AND AM.COD_PNEU = VP.COD_PNEU
                                           AND AM.COD_UNIDADE = VP.COD_UNIDADE
                 WHERE AM.COD_UNIDADE = ANY (F_COD_UNIDADES)
                       AND AM.DATA_HORA_RESOLUCAO IS NOT NULL
                       AND (AM.TIPO_SERVICO IN (TIPO_SERVICO_CALIBRAGEM, TIPO_SERVICO_INSPECAO))
  )

  SELECT
    D.PLACA_VEICULO :: TEXT AS PLACA_VEICULO,
    D.TOTAL_KM              AS TOTAL_KM
  FROM DADOS D
  WHERE D.TOTAL_KM > 0
  ORDER BY TOTAL_KM DESC;
END;
$$;

CREATE OR REPLACE FUNCTION FUNC_RELATORIO_PNEU_ADERENCIA_AFERICAO(F_COD_UNIDADE TEXT[], F_DATA_INICIAL DATE, F_DATA_FINAL DATE)
    RETURNS TABLE
            (
                "UNIDADE ALOCADA"                          TEXT,
                "PLACA"                                    CHARACTER VARYING,
                "IDENTIFICADOR FROTA"                      TEXT,
                "QT AFERIÇÕES DE PRESSÃO"                  BIGINT,
                "MAX DIAS ENTRE AFERIÇÕES DE PRESSÃO"      TEXT,
                "MIN DIAS ENTRE AFERIÇÕES DE PRESSÃO"      TEXT,
                "MÉDIA DE DIAS ENTRE AFERIÇÕES DE PRESSÃO" TEXT,
                "QTD AFERIÇÕES DE PRESSÃO DENTRO DA META"  BIGINT,
                "ADERÊNCIA AFERIÇÕES DE PRESSÃO"           TEXT,
                "QT AFERIÇÕES DE SULCO"                    BIGINT,
                "MAX DIAS ENTRE AFERIÇÕES DE SULCO"        TEXT,
                "MIN DIAS ENTRE AFERIÇÕES DE SULCO"        TEXT,
                "MÉDIA DE DIAS ENTRE AFERIÇÕES DE SULCO"   TEXT,
                "QTD AFERIÇÕES DE SULCO DENTRO DA META"    BIGINT,
                "ADERÊNCIA AFERIÇÕES DE SULCO"             TEXT
            )
    LANGUAGE SQL
AS
$$
SELECT U.NOME                               AS "UNIDADE ALOCADA",
       V.PLACA                              AS PLACA,
       COALESCE(V.IDENTIFICADOR_FROTA, '-') AS IDENTIFICADOR_FROTA,
       COALESCE(CALCULO_PRESSAO.QTD_AFERICOES, 0),
       COALESCE(CALCULO_PRESSAO.MAX_DIAS_ENTRE_AFERICOES, '0'),
       COALESCE(CALCULO_PRESSAO.MIN_DIAS_ENTRE_AFERICOES, '0'),
       COALESCE(CALCULO_PRESSAO.MD_DIAS_ENTRE_AFERICOES, '0'),
       COALESCE(CALCULO_PRESSAO.QTD_AFERICOES_DENTRO_META, 0),
       COALESCE(CALCULO_PRESSAO.ADERENCIA, '0%'),
       COALESCE(CALCULO_SULCO.QTD_AFERICOES, 0),
       COALESCE(CALCULO_SULCO.MAX_DIAS_ENTRE_AFERICOES, '0'),
       COALESCE(CALCULO_SULCO.MIN_DIAS_ENTRE_AFERICOES, '0'),
       COALESCE(CALCULO_SULCO.MD_DIAS_ENTRE_AFERICOES, '0'),
       COALESCE(CALCULO_SULCO.QTD_AFERICOES_DENTRO_META, 0),
       COALESCE(CALCULO_SULCO.ADERENCIA, '0%')
FROM VEICULO V
         JOIN UNIDADE U ON V.COD_UNIDADE = U.CODIGO
         LEFT JOIN (SELECT CALCULO_AFERICAO_PRESSAO.PLACA,
                           COUNT(CALCULO_AFERICAO_PRESSAO.PLACA) AS QTD_AFERICOES,
                           CASE
                               WHEN
                                   MAX(CALCULO_AFERICAO_PRESSAO.DIAS_ENTRE_AFERICOES) IS NOT NULL
                                   THEN MAX(CALCULO_AFERICAO_PRESSAO.DIAS_ENTRE_AFERICOES)::TEXT
                               ELSE '-' END                      AS MAX_DIAS_ENTRE_AFERICOES,
                           CASE
                               WHEN
                                   MIN(CALCULO_AFERICAO_PRESSAO.DIAS_ENTRE_AFERICOES) IS NOT NULL
                                   THEN MIN(CALCULO_AFERICAO_PRESSAO.DIAS_ENTRE_AFERICOES)::TEXT
                               ELSE '-' END                      AS MIN_DIAS_ENTRE_AFERICOES,
                           CASE
                               WHEN
                                   MAX(CALCULO_AFERICAO_PRESSAO.DIAS_ENTRE_AFERICOES) IS NOT NULL
                                   THEN TRUNC(
                                       CASE
                                           WHEN SUM(CALCULO_AFERICAO_PRESSAO.DIAS_ENTRE_AFERICOES) IS NOT NULL
                                               THEN
                                                   SUM(CALCULO_AFERICAO_PRESSAO.DIAS_ENTRE_AFERICOES) /
                                                   SUM(CASE
                                                           WHEN CALCULO_AFERICAO_PRESSAO.DIAS_ENTRE_AFERICOES IS NOT NULL
                                                               THEN 1
                                                           ELSE 0 END)
                                           END)::TEXT
                               ELSE '-' END                      AS MD_DIAS_ENTRE_AFERICOES,
                           SUM(CASE
                                   WHEN CALCULO_AFERICAO_PRESSAO.DIAS_ENTRE_AFERICOES <=
                                        CALCULO_AFERICAO_PRESSAO.PERIODO_AFERICAO
                                       THEN 1
                                   ELSE 0 END)                   AS QTD_AFERICOES_DENTRO_META,
                           TRUNC(SUM(CASE
                                         WHEN CALCULO_AFERICAO_PRESSAO.DIAS_ENTRE_AFERICOES <=
                                              CALCULO_AFERICAO_PRESSAO.PERIODO_AFERICAO
                                             THEN 1
                                         ELSE 0 END) / COUNT(CALCULO_AFERICAO_PRESSAO.PLACA)::NUMERIC * 100) ||
                           '%'                                   AS ADERENCIA
                    FROM (SELECT V.PLACA                AS PLACA,
                                 A.DATA_HORA            AS DATA_HORA_AFERICAO,
                                 A.TIPO_MEDICAO_COLETADA,
                                 R.PERIODO_AFERICAO_PRESSAO AS PERIODO_AFERICAO,
                                 CASE
                                     WHEN V.PLACA = LAG(V.PLACA) OVER (ORDER BY V.PLACA, DATA_HORA)
                                         THEN EXTRACT(DAYS FROM A.DATA_HORA -
                                                                LAG(A.DATA_HORA) OVER (ORDER BY V.PLACA, DATA_HORA))
                                     END                    AS DIAS_ENTRE_AFERICOES,
                                 A.COD_UNIDADE
                          FROM AFERICAO A
                                   JOIN VEICULO V ON V.CODIGO = A.COD_VEICULO
                                   JOIN PNEU_RESTRICAO_UNIDADE R ON R.COD_UNIDADE = V.COD_UNIDADE
                          WHERE V.COD_UNIDADE::TEXT LIKE ANY (F_COD_UNIDADE)
                            -- (PL-1900) Passamos a realizar uma subtração da data inicial pelo periodo em questão
                            -- para poder buscar a aferição anterior a primeira aferição filtrada para fazer o calculo
                            -- de se ela foi realizada dentro da meta.
                            AND (A.DATA_HORA AT TIME ZONE tz_unidade(A.COD_UNIDADE))::DATE >=
                                (F_DATA_INICIAL - R.PERIODO_AFERICAO_PRESSAO)::DATE
                            AND (A.DATA_HORA AT TIME ZONE tz_unidade(A.COD_UNIDADE))::DATE <= (F_DATA_FINAL)
                            AND (A.TIPO_MEDICAO_COLETADA = 'PRESSAO' OR A.TIPO_MEDICAO_COLETADA = 'SULCO_PRESSAO')
                            AND A.TIPO_PROCESSO_COLETA = 'PLACA'
                          ORDER BY 1, 2) AS CALCULO_AFERICAO_PRESSAO
                         -- (PL-1900) Aqui retiramos as aferições trazidas que eram de antes da data filtrada, pois eram
                         -- apenas para o calculo da meta da primeira aferição da faixa do filtro.
                    WHERE (CALCULO_AFERICAO_PRESSAO.DATA_HORA_AFERICAO AT TIME ZONE
                           tz_unidade(CALCULO_AFERICAO_PRESSAO.COD_UNIDADE))::DATE >= F_DATA_INICIAL::DATE
                      AND (CALCULO_AFERICAO_PRESSAO.DATA_HORA_AFERICAO AT TIME ZONE
                           tz_unidade(CALCULO_AFERICAO_PRESSAO.COD_UNIDADE))::DATE <= F_DATA_FINAL::DATE
                    GROUP BY CALCULO_AFERICAO_PRESSAO.PLACA) AS CALCULO_PRESSAO
                   ON CALCULO_PRESSAO.PLACA = V.PLACA
         LEFT JOIN (SELECT CALCULO_AFERICAO_SULCO.PLACA,
                           COUNT(CALCULO_AFERICAO_SULCO.PLACA) AS QTD_AFERICOES,
                           CASE
                               WHEN
                                   MAX(CALCULO_AFERICAO_SULCO.DIAS_ENTRE_AFERICOES) IS NOT NULL
                                   THEN MAX(CALCULO_AFERICAO_SULCO.DIAS_ENTRE_AFERICOES)::TEXT
                               ELSE '-' END                    AS MAX_DIAS_ENTRE_AFERICOES,
                           CASE
                               WHEN
                                   MIN(CALCULO_AFERICAO_SULCO.DIAS_ENTRE_AFERICOES) IS NOT NULL
                                   THEN MIN(CALCULO_AFERICAO_SULCO.DIAS_ENTRE_AFERICOES)::TEXT
                               ELSE '-' END                    AS MIN_DIAS_ENTRE_AFERICOES,
                           CASE
                               WHEN
                                   MAX(CALCULO_AFERICAO_SULCO.DIAS_ENTRE_AFERICOES) IS NOT NULL
                                   THEN TRUNC(
                                       CASE
                                           WHEN SUM(CALCULO_AFERICAO_SULCO.DIAS_ENTRE_AFERICOES) IS NOT NULL
                                               THEN
                                                   SUM(CALCULO_AFERICAO_SULCO.DIAS_ENTRE_AFERICOES) /
                                                   SUM(CASE
                                                           WHEN CALCULO_AFERICAO_SULCO.DIAS_ENTRE_AFERICOES IS NOT NULL
                                                               THEN 1
                                                           ELSE 0 END)
                                           END) :: TEXT
                               ELSE '-' END                    AS MD_DIAS_ENTRE_AFERICOES,
                           SUM(CASE
                                   WHEN CALCULO_AFERICAO_SULCO.DIAS_ENTRE_AFERICOES <=
                                        CALCULO_AFERICAO_SULCO.PERIODO_AFERICAO
                                       THEN 1
                                   ELSE 0 END)                 AS QTD_AFERICOES_DENTRO_META,
                           TRUNC(SUM(CASE
                                         WHEN CALCULO_AFERICAO_SULCO.DIAS_ENTRE_AFERICOES <=
                                              CALCULO_AFERICAO_SULCO.PERIODO_AFERICAO
                                             THEN 1
                                         ELSE 0 END) / COUNT(CALCULO_AFERICAO_SULCO.PLACA)::NUMERIC * 100) ||
                           '%'                                 AS ADERENCIA
                    FROM (SELECT V.PLACA          AS PLACA,
                                 A.DATA_HORA      AS DATA_HORA_AFERICAO,
                                 A.TIPO_MEDICAO_COLETADA,
                                 R.PERIODO_AFERICAO_SULCO AS PERIODO_AFERICAO,
                                 CASE
                                     WHEN V.PLACA = LAG(V.PLACA) OVER (ORDER BY V.PLACA, DATA_HORA)
                                         THEN EXTRACT(DAYS FROM A.DATA_HORA -
                                                                LAG(A.DATA_HORA) OVER (ORDER BY V.PLACA, DATA_HORA))
                                     ELSE 0
                                     END                  AS DIAS_ENTRE_AFERICOES,
                                 A.COD_UNIDADE
                          FROM AFERICAO A
                                   JOIN VEICULO V ON V.CODIGO = A.COD_VEICULO
                                   JOIN PNEU_RESTRICAO_UNIDADE R ON R.COD_UNIDADE = V.COD_UNIDADE
                          WHERE V.COD_UNIDADE::TEXT LIKE ANY (F_COD_UNIDADE)
                            -- (PL-1900) Passamos a realizar uma subtração da data inicial pelo periodo em questão
                            -- para poder buscar a aferição anterior a primeira aferição filtrada para fazer o calculo
                            -- de se ela foi realizada dentro da meta.
                            AND (A.DATA_HORA AT TIME ZONE tz_unidade(A.COD_UNIDADE))::DATE >=
                                (F_DATA_INICIAL - R.PERIODO_AFERICAO_SULCO)::DATE
                            AND (A.DATA_HORA AT TIME ZONE tz_unidade(A.COD_UNIDADE))::DATE <= (F_DATA_FINAL)
                            AND (A.TIPO_MEDICAO_COLETADA = 'SULCO' OR A.TIPO_MEDICAO_COLETADA = 'SULCO_PRESSAO')
                            AND A.TIPO_PROCESSO_COLETA = 'PLACA'
                          ORDER BY 1, 2) AS CALCULO_AFERICAO_SULCO
                         -- (PL-1900) Aqui retiramos as aferições trazidas que eram de antes da data filtrada, pois eram
                         -- apenas para o calculo da meta da primeira aferição da faixa do filtro.
                    WHERE CAST(CALCULO_AFERICAO_SULCO.DATA_HORA_AFERICAO AT TIME ZONE
                               tz_unidade(CALCULO_AFERICAO_SULCO.COD_UNIDADE) AS DATE) >= F_DATA_INICIAL::DATE
                      AND CAST(CALCULO_AFERICAO_SULCO.DATA_HORA_AFERICAO AT TIME ZONE
                               tz_unidade(CALCULO_AFERICAO_SULCO.COD_UNIDADE) AS DATE) <= F_DATA_FINAL::DATE
                    GROUP BY CALCULO_AFERICAO_SULCO.PLACA) AS CALCULO_SULCO
                   ON CALCULO_SULCO.PLACA = V.PLACA
WHERE V.COD_UNIDADE::TEXT LIKE ANY (F_COD_UNIDADE)
  AND V.STATUS_ATIVO IS TRUE
ORDER BY U.NOME, V.PLACA;
$$;

CREATE OR REPLACE FUNCTION FUNC_PNEU_RELATORIO_FAROL_AFERICAO(F_COD_UNIDADES BIGINT[], F_DATA_INICIAL DATE, F_DATA_FINAL DATE)
    RETURNS TABLE
            (
                "UNIDADE"                             TEXT,
                "QTD DE FROTAS"                       TEXT,
                "QTD DE PNEUS"                        TEXT,
                "QTD DE PNEUS AFERIDOS - PRESSÃO"     TEXT,
                "PERCENTUAL PNEUS AFERIDOS - PRESSÃO" TEXT,
                "QTD DE PNEUS AFERIDOS - SULCO"       TEXT,
                "PERCENTUAL PNEUS AFERIDOS - SULCO"   TEXT
            )
    LANGUAGE SQL
AS
$$
WITH FAROL_AFERICAO AS (
    SELECT U.NOME                                                AS NOME_UNIDADE,
           COUNT(DISTINCT V.PLACA)                               AS QTD_VEICULOS,
           COUNT(DISTINCT VP.*)                                  AS QTD_PNEUS,
           COUNT(DISTINCT VP.COD_PNEU) FILTER (
               WHERE A.TIPO_MEDICAO_COLETADA = 'PRESSAO'
                   OR A.TIPO_MEDICAO_COLETADA = 'SULCO_PRESSAO') AS TOTAL_PRESSAO,
           COUNT(DISTINCT VP.COD_PNEU) FILTER (
               WHERE A.TIPO_MEDICAO_COLETADA = 'SULCO'
                   OR A.TIPO_MEDICAO_COLETADA = 'SULCO_PRESSAO') AS TOTAL_SULCO
    FROM VEICULO_DATA V
             JOIN UNIDADE U ON V.COD_UNIDADE = U.CODIGO
             JOIN VEICULO_PNEU VP ON V.CODIGO = VP.COD_VEICULO AND V.COD_UNIDADE = VP.COD_UNIDADE
             LEFT JOIN AFERICAO_VALORES AV ON AV.COD_PNEU = VP.COD_PNEU
             LEFT JOIN AFERICAO A ON V.CODIGO = A.COD_VEICULO AND A.CODIGO = AV.COD_AFERICAO
    WHERE V.COD_UNIDADE = ANY (F_COD_UNIDADES)
      AND (A.DATA_HORA AT TIME ZONE TZ_UNIDADE(A.COD_UNIDADE))::DATE >= (F_DATA_INICIAL)
      AND (A.DATA_HORA AT TIME ZONE TZ_UNIDADE(A.COD_UNIDADE))::DATE <= (F_DATA_FINAL)
    GROUP BY V.COD_UNIDADE, U.NOME
)
SELECT NOME_UNIDADE :: TEXT,
       QTD_VEICULOS :: TEXT,
       QTD_PNEUS :: TEXT,
       TOTAL_PRESSAO :: TEXT,
       COALESCE_PERCENTAGE(TOTAL_PRESSAO, QTD_PNEUS) :: TEXT AS PERCENTUAL_PRESSAO,
       TOTAL_SULCO :: TEXT,
       COALESCE_PERCENTAGE(TOTAL_SULCO, QTD_PNEUS) :: TEXT   AS PERCENTUAL_SULCO
FROM FAROL_AFERICAO
ORDER BY (TOTAL_PRESSAO :: REAL / NULLIF(QTD_PNEUS, 0) :: REAL) ASC NULLS LAST;
$$;

CREATE OR REPLACE FUNCTION FUNC_PNEU_RELATORIO_EXTRATO_SERVICOS_FECHADOS(F_COD_UNIDADES BIGINT[],
                                                                         F_DATA_INICIAL DATE,
                                                                         F_DATA_FINAL DATE)
    RETURNS TABLE
            (
                "UNIDADE DO SERVIÇO"               TEXT,
                "DATA AFERIÇÃO"                    TEXT,
                "DATA RESOLUÇÃO"                   TEXT,
                "HORAS PARA RESOLVER"              DOUBLE PRECISION,
                "MINUTOS PARA RESOLVER"            DOUBLE PRECISION,
                "PLACA"                            TEXT,
                "IDENTIFICADOR FROTA"              TEXT,
                "KM AFERIÇÃO"                      BIGINT,
                "KM CONSERTO"                      BIGINT,
                "KM PERCORRIDO"                    BIGINT,
                "COD PNEU"                         CHARACTER VARYING,
                "PRESSÃO RECOMENDADA"              REAL,
                "PRESSÃO AFERIÇÃO"                 TEXT,
                "DISPERSÃO RECOMENDADA X AFERIÇÃO" TEXT,
                "PRESSÃO INSERIDA"                 TEXT,
                "DISPERSÃO RECOMENDADA X INSERIDA" TEXT,
                "POSIÇÃO PNEU ABERTURA SERVIÇO"    TEXT,
                "SERVIÇO"                          TEXT,
                "MECÂNICO"                         TEXT,
                "PROBLEMA APONTADO (INSPEÇÃO)"     TEXT,
                "FECHADO AUTOMATICAMENTE"          TEXT,
                "FORMA DE COLETA DOS DADOS"        TEXT
            )
    LANGUAGE SQL
AS
$$
SELECT U.NOME                                                                       AS UNIDADE_SERVICO,
       TO_CHAR((A.DATA_HORA AT TIME ZONE TZ_UNIDADE(AM.COD_UNIDADE)),
               'DD/MM/YYYY HH24:MI:SS')                                             AS DATA_HORA_AFERICAO,
       TO_CHAR((AM.DATA_HORA_RESOLUCAO AT TIME ZONE TZ_UNIDADE(AM.COD_UNIDADE)),
               'DD/MM/YYYY HH24:MI:SS')                                             AS DATA_HORA_RESOLUCAO,
       TRUNC(EXTRACT(EPOCH FROM ((AM.DATA_HORA_RESOLUCAO) - (A.DATA_HORA))) /
             3600)                                                                  AS HORAS_RESOLUCAO,
       TRUNC(
               EXTRACT(EPOCH FROM ((AM.DATA_HORA_RESOLUCAO) - (A.DATA_HORA))) / 60) AS MINUTOS_RESOLUCAO,
       V.PLACA                                                                      AS PLACA_VEICULO,
       COALESCE(V.IDENTIFICADOR_FROTA, '-')                                         AS IDENTIFICADOR_FROTA,
       A.KM_VEICULO                                                                 AS KM_AFERICAO,
       AM.KM_MOMENTO_CONSERTO                                                       AS KM_MOMENTO_CONSERTO,
       AM.KM_MOMENTO_CONSERTO - A.KM_VEICULO                                        AS KM_PERCORRIDO,
       P.CODIGO_CLIENTE                                                             AS CODIGO_CLIENTE_PNEU,
       P.PRESSAO_RECOMENDADA                                                        AS PRESSAO_RECOMENDADA_PNEU,
       COALESCE(REPLACE(ROUND(AV.PSI :: NUMERIC, 2) :: TEXT, '.', ','),
                '-')                                                                AS PSI_AFERICAO,
       COALESCE(REPLACE(ROUND((((AV.PSI / P.PRESSAO_RECOMENDADA) - 1) * 100) :: NUMERIC, 2) || '%', '.', ','),
                '-')                                                                AS DISPERSAO_PRESSAO_ANTES,
       COALESCE(REPLACE(ROUND(AM.PSI_APOS_CONSERTO :: NUMERIC, 2) :: TEXT, '.', ','),
                '-')                                                                AS PSI_POS_CONSERTO,
       COALESCE(REPLACE(ROUND((((AM.PSI_APOS_CONSERTO / P.PRESSAO_RECOMENDADA) - 1) * 100) :: NUMERIC, 2) || '%', '.',
                        ','),
                '-')                                                                AS DISPERSAO_PRESSAO_DEPOIS,
       COALESCE(PPNE.NOMENCLATURA, '-')                                             AS POSICAO,
       AM.TIPO_SERVICO                                                              AS TIPO_SERVICO,
       COALESCE(INITCAP(C.NOME), '-')                                               AS NOME_MECANICO,
       COALESCE(AA.ALTERNATIVA, '-')                                                AS PROBLEMA_APONTADO,
       F_IF(AM.FECHADO_AUTOMATICAMENTE_MOVIMENTACAO OR AM.FECHADO_AUTOMATICAMENTE_INTEGRACAO, 'Sim' :: TEXT,
            'Não')                                                                  AS TIPO_FECHAMENTO,
       COALESCE(AFCD.STATUS_LEGIVEL, '-')                                           AS FORMA_COLETA_DADOS_FECHAMENTO
FROM AFERICAO_MANUTENCAO AM
         JOIN UNIDADE U
              ON AM.COD_UNIDADE = U.CODIGO
         JOIN AFERICAO_VALORES AV
              ON AM.COD_UNIDADE = AV.COD_UNIDADE
                  AND AM.COD_AFERICAO = AV.COD_AFERICAO
                  AND AM.COD_PNEU = AV.COD_PNEU
         JOIN AFERICAO A
              ON A.CODIGO = AV.COD_AFERICAO
         LEFT JOIN COLABORADOR C
                   ON AM.CPF_MECANICO = C.CPF
         JOIN PNEU P
              ON P.CODIGO = AV.COD_PNEU
         LEFT JOIN VEICULO_PNEU VP
                   ON VP.COD_PNEU = P.CODIGO
                       AND VP.COD_UNIDADE = P.COD_UNIDADE
         LEFT JOIN AFERICAO_ALTERNATIVA_MANUTENCAO_INSPECAO AA
                   ON AA.CODIGO = AM.COD_ALTERNATIVA
         LEFT JOIN VEICULO V
                   ON V.CODIGO = VP.COD_VEICULO
         LEFT JOIN EMPRESA E
                   ON U.COD_EMPRESA = E.CODIGO
         LEFT JOIN VEICULO_TIPO VT
                   ON E.CODIGO = VT.COD_EMPRESA
                       AND VT.CODIGO = V.COD_TIPO
         LEFT JOIN PNEU_POSICAO_NOMENCLATURA_EMPRESA PPNE
                   ON PPNE.COD_EMPRESA = P.COD_EMPRESA
                       AND PPNE.COD_DIAGRAMA = VT.COD_DIAGRAMA
                       AND PPNE.POSICAO_PROLOG = AV.POSICAO
         LEFT JOIN TYPES.AFERICAO_FORMA_COLETA_DADOS AFCD
                   ON AFCD.FORMA_COLETA_DADOS = AM.FORMA_COLETA_DADOS_FECHAMENTO
WHERE AV.COD_UNIDADE = ANY (F_COD_UNIDADES)
  AND AM.DATA_HORA_RESOLUCAO IS NOT NULL
  AND (AM.DATA_HORA_RESOLUCAO AT TIME ZONE TZ_UNIDADE(AM.COD_UNIDADE)) :: DATE >= F_DATA_INICIAL
  AND (AM.DATA_HORA_RESOLUCAO AT TIME ZONE TZ_UNIDADE(AM.COD_UNIDADE)) :: DATE <= F_DATA_FINAL
ORDER BY U.NOME, A.DATA_HORA DESC
$$;

CREATE OR REPLACE FUNCTION FUNC_PNEU_RELATORIO_EXTRATO_SERVICOS_ABERTOS(F_COD_UNIDADES BIGINT[],
                                                                        F_DATA_INICIAL DATE,
                                                                        F_DATA_FINAL DATE,
                                                                        F_DATA_ATUAL DATE)
    RETURNS TABLE
            (
                "UNIDADE DO SERVIÇO"            TEXT,
                "CÓDIGO DO SERVIÇO"             TEXT,
                "TIPO DO SERVIÇO"               TEXT,
                "QTD APONTAMENTOS"              TEXT,
                "DATA HORA ABERTURA"            TEXT,
                "QTD DIAS EM ABERTO"            TEXT,
                "NOME DO COLABORADOR"           TEXT,
                "PLACA"                         TEXT,
                "IDENTIFICADOR FROTA"           TEXT,
                "PNEU"                          TEXT,
                "POSIÇÃO PNEU ABERTURA SERVIÇO" TEXT,
                "MEDIDAS"                       TEXT,
                "COD AFERIÇÃO"                  TEXT,
                "SULCO INTERNO"                 TEXT,
                "SULCO CENTRAL INTERNO"         TEXT,
                "SULCO CENTRAL EXTERNO"         TEXT,
                "SULCO EXTERNO"                 TEXT,
                "MENOR SULCO"                   TEXT,
                "PRESSÃO (PSI)"                 TEXT,
                "PRESSÃO RECOMENDADA (PSI)"     TEXT,
                "ESTADO ATUAL"                  TEXT,
                "MÁXIMO DE RECAPAGENS"          TEXT
            )
    LANGUAGE SQL
AS
$$
SELECT U.NOME                                                                                   AS UNIDADE_SERVICO,
       AM.CODIGO :: TEXT                                                                        AS CODIGO_SERVICO,
       AM.TIPO_SERVICO                                                                          AS TIPO_SERVICO,
       AM.QT_APONTAMENTOS :: TEXT                                                               AS QT_APONTAMENTOS,
       TO_CHAR((A.DATA_HORA AT TIME ZONE TZ_UNIDADE(AM.COD_UNIDADE)),
               'DD/MM/YYYY HH24:MI') :: TEXT                                                    AS DATA_HORA_ABERTURA,
       (F_DATA_ATUAL - ((A.DATA_HORA AT TIME ZONE TZ_UNIDADE(AM.COD_UNIDADE)) :: DATE)) :: TEXT AS DIAS_EM_ABERTO,
       C.NOME                                                                                   AS NOME_COLABORADOR,
       V.PLACA                                                                                  AS PLACA_VEICULO,
       COALESCE(V.IDENTIFICADOR_FROTA, '-')                                                     AS IDENTIFICADOR_FROTA,
       P.CODIGO_CLIENTE                                                                         AS COD_PNEU_PROBLEMA,
       COALESCE(PPNE.NOMENCLATURA :: TEXT, '-')                                                 AS POSICAO_PNEU_PROBLEMA,
       DP.LARGURA || '/' :: TEXT || DP.ALTURA || ' R' :: TEXT || DP.ARO                         AS MEDIDAS,
       A.CODIGO :: TEXT                                                                         AS COD_AFERICAO,
       FUNC_PNEU_FORMAT_SULCO(AV.ALTURA_SULCO_INTERNO)                                          AS SULCO_INTERNO,
       FUNC_PNEU_FORMAT_SULCO(AV.ALTURA_SULCO_CENTRAL_INTERNO)                                  AS SULCO_CENTRAL_INTERNO,
       FUNC_PNEU_FORMAT_SULCO(AV.ALTURA_SULCO_CENTRAL_EXTERNO)                                  AS SULCO_CENTRAL_EXTERNO,
       FUNC_PNEU_FORMAT_SULCO(AV.ALTURA_SULCO_EXTERNO)                                          AS SULCO_EXTERNO,
       FUNC_PNEU_FORMAT_SULCO(LEAST(AV.ALTURA_SULCO_EXTERNO, AV.ALTURA_SULCO_CENTRAL_EXTERNO,
                                    AV.ALTURA_SULCO_CENTRAL_INTERNO,
                                    AV.ALTURA_SULCO_INTERNO))                                   AS MENOR_SULCO,
       REPLACE(COALESCE(TRUNC(AV.PSI) :: TEXT, '-'), '.', ',')                                  AS PRESSAO_PNEU_PROBLEMA,
       REPLACE(COALESCE(TRUNC(P.PRESSAO_RECOMENDADA) :: TEXT, '-'), '.',
               ',')                                                                             AS PRESSAO_RECOMENDADA,
       PVN.NOME                                                                                 AS VIDA_PNEU_PROBLEMA,
       PRN.NOME                                                                                 AS TOTAL_RECAPAGENS
FROM AFERICAO_MANUTENCAO AM
         JOIN PNEU P
              ON AM.COD_PNEU = P.CODIGO
         JOIN DIMENSAO_PNEU DP
              ON DP.CODIGO = P.COD_DIMENSAO
         JOIN AFERICAO A
              ON A.CODIGO = AM.COD_AFERICAO
         JOIN COLABORADOR C
              ON A.CPF_AFERIDOR = C.CPF
         JOIN AFERICAO_VALORES AV
              ON AV.COD_AFERICAO = AM.COD_AFERICAO
                  AND AV.COD_PNEU = AM.COD_PNEU
         JOIN UNIDADE U
              ON U.CODIGO = AM.COD_UNIDADE
         JOIN EMPRESA E
              ON U.COD_EMPRESA = E.CODIGO
         JOIN PNEU_VIDA_NOMENCLATURA PVN
              ON PVN.COD_VIDA = AV.VIDA_MOMENTO_AFERICAO
         JOIN PNEU_RECAPAGEM_NOMENCLATURA PRN
              ON PRN.COD_TOTAL_VIDA = P.VIDA_TOTAL
         JOIN VEICULO V
              ON A.COD_VEICULO = V.CODIGO
                  AND V.COD_UNIDADE = A.COD_UNIDADE
         LEFT JOIN VEICULO_TIPO VT
                   ON V.COD_TIPO = VT.CODIGO
         LEFT JOIN VEICULO_DIAGRAMA VD ON VT.COD_DIAGRAMA = VD.CODIGO
         LEFT JOIN PNEU_POSICAO_NOMENCLATURA_EMPRESA PPNE ON PPNE.COD_EMPRESA = E.CODIGO
    AND PPNE.COD_DIAGRAMA = VD.CODIGO
    AND AV.POSICAO = PPNE.POSICAO_PROLOG
WHERE AM.COD_UNIDADE = ANY (F_COD_UNIDADES)
  AND (A.DATA_HORA AT TIME ZONE TZ_UNIDADE(AM.COD_UNIDADE)) :: DATE >= F_DATA_INICIAL
  AND (A.DATA_HORA AT TIME ZONE TZ_UNIDADE(AM.COD_UNIDADE)) :: DATE <= F_DATA_FINAL
  AND AM.DATA_HORA_RESOLUCAO IS NULL
  AND (AM.FECHADO_AUTOMATICAMENTE_MOVIMENTACAO = FALSE OR AM.FECHADO_AUTOMATICAMENTE_MOVIMENTACAO IS NULL)
  AND (AM.FECHADO_AUTOMATICAMENTE_INTEGRACAO = FALSE OR AM.FECHADO_AUTOMATICAMENTE_INTEGRACAO IS NULL)
ORDER BY U.NOME, A.DATA_HORA;
$$;