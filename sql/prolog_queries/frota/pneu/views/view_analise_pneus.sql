create view view_analise_pneus as
  SELECT u.nome AS "UNIDADE ALOCADO",
    p.codigo AS "COD PNEU",
    p.codigo_cliente AS "COD PNEU CLIENTE",
    p.status AS "STATUS PNEU",
    p.cod_unidade,
    map.nome AS "MARCA",
    mp.nome AS "MODELO",
    ((((dp.largura || '/'::text) || dp.altura) || ' R'::text) || dp.aro) AS "MEDIDAS",
    dados.qt_afericoes AS "QTD DE AFERIÇÕES",
    to_char((dados.primeira_afericao)::timestamp with time zone, 'DD/MM/YYYY'::text) AS "DTA 1a AFERIÇÃO",
    to_char((dados.ultima_afericao)::timestamp with time zone, 'DD/MM/YYYY'::text) AS "DTA ÚLTIMA AFERIÇÃO",
    dados.total_dias AS "DIAS ATIVO",
    round(
        CASE
            WHEN (dados.total_dias > 0) THEN (dados.total_km / (dados.total_dias)::numeric)
            ELSE NULL::numeric
        END) AS "MÉDIA KM POR DIA",
    p.altura_sulco_interno,
    p.altura_sulco_central_interno,
    p.altura_sulco_central_externo,
    p.altura_sulco_externo,
    round((dados.maior_sulco)::numeric, 2) AS "MAIOR MEDIÇÃO VIDA",
    round((dados.menor_sulco)::numeric, 2) AS "MENOR SULCO ATUAL",
    round((dados.sulco_gasto)::numeric, 2) AS "MILIMETROS GASTOS",
    round((dados.km_por_mm)::numeric, 2) AS "KMS POR MILIMETRO",
    round(((dados.km_por_mm * dados.sulco_restante))::numeric) AS "KMS A PERCORRER",
    trunc(
        CASE
            WHEN (((dados.total_km > (0)::numeric) AND (dados.total_dias > 0)) AND ((dados.total_km / (dados.total_dias)::numeric) > (0)::numeric)) THEN ((dados.km_por_mm * dados.sulco_restante) / ((dados.total_km / (dados.total_dias)::numeric))::double precision)
            ELSE (0)::double precision
        END) AS "DIAS RESTANTES",
        CASE
            WHEN (((dados.total_km > (0)::numeric) AND (dados.total_dias > 0)) AND ((dados.total_km / (dados.total_dias)::numeric) > (0)::numeric)) THEN ((((dados.km_por_mm * dados.sulco_restante) / ((dados.total_km / (dados.total_dias)::numeric))::double precision))::integer + ('NOW'::text)::date)
            ELSE NULL::date
        END AS "PREVISÃO DE TROCA"
   FROM (((((pneu p
     JOIN ( SELECT av.cod_pneu,
            av.cod_unidade,
            count(av.altura_sulco_central_interno) AS qt_afericoes,
            (min(timezone(tz_unidade(a.cod_unidade), a.data_hora)))::date AS primeira_afericao,
            (max(timezone(tz_unidade(a.cod_unidade), a.data_hora)))::date AS ultima_afericao,
            ((max(timezone(tz_unidade(a.cod_unidade), a.data_hora)))::date - (min(timezone(tz_unidade(a.cod_unidade), a.data_hora)))::date) AS total_dias,
            max(total_km.total_km) AS total_km,
            max(GREATEST(av.altura_sulco_interno, av.altura_sulco_central_interno, av.altura_sulco_central_externo, av.altura_sulco_externo)) AS maior_sulco,
            min(LEAST(av.altura_sulco_interno, av.altura_sulco_central_interno, av.altura_sulco_central_externo, av.altura_sulco_externo)) AS menor_sulco,
            (max(GREATEST(av.altura_sulco_interno, av.altura_sulco_central_interno, av.altura_sulco_central_externo, av.altura_sulco_externo)) - min(LEAST(av.altura_sulco_interno, av.altura_sulco_central_interno, av.altura_sulco_central_externo, av.altura_sulco_externo))) AS sulco_gasto,
                CASE
                    WHEN (
                    CASE
                        WHEN (p_1.vida_atual = p_1.vida_total) THEN (min(LEAST(av.altura_sulco_interno, av.altura_sulco_central_interno, av.altura_sulco_central_externo, av.altura_sulco_externo)) - pru.sulco_minimo_descarte)
                        WHEN (p_1.vida_atual < p_1.vida_total) THEN (min(LEAST(av.altura_sulco_interno, av.altura_sulco_central_interno, av.altura_sulco_central_externo, av.altura_sulco_externo)) - pru.sulco_minimo_recapagem)
                        ELSE NULL::real
                    END < (0)::double precision) THEN (0)::real
                    ELSE
                    CASE
                        WHEN (p_1.vida_atual = p_1.vida_total) THEN (min(LEAST(av.altura_sulco_interno, av.altura_sulco_central_interno, av.altura_sulco_central_externo, av.altura_sulco_externo)) - pru.sulco_minimo_descarte)
                        WHEN (p_1.vida_atual < p_1.vida_total) THEN (min(LEAST(av.altura_sulco_interno, av.altura_sulco_central_interno, av.altura_sulco_central_externo, av.altura_sulco_externo)) - pru.sulco_minimo_recapagem)
                        ELSE NULL::real
                    END
                END AS sulco_restante,
                CASE
                    WHEN (((max(timezone(tz_unidade(a.cod_unidade), a.data_hora)))::date - (min(timezone(tz_unidade(a.cod_unidade), a.data_hora)))::date) > 0) THEN (((max(total_km.total_km))::double precision / max(GREATEST(av.altura_sulco_interno, av.altura_sulco_central_interno, av.altura_sulco_central_externo, av.altura_sulco_externo))) - min(LEAST(av.altura_sulco_interno, av.altura_sulco_central_interno, av.altura_sulco_central_externo, av.altura_sulco_externo)))
                    ELSE (0)::double precision
                END AS km_por_mm
           FROM ((((afericao_valores av
             JOIN afericao a ON ((a.codigo = av.cod_afericao)))
             JOIN pneu p_1 ON ((((p_1.codigo)::text = (av.cod_pneu)::text) AND ((p_1.status)::text = 'EM_USO'::text))))
             JOIN pneu_restricao_unidade pru ON ((pru.cod_unidade = av.cod_unidade)))
             JOIN ( SELECT total_km_rodado.cod_pneu,
                    total_km_rodado.cod_unidade,
                    sum(total_km_rodado.km_rodado) AS total_km
                   FROM ( SELECT av_1.cod_pneu,
                            av_1.cod_unidade,
                            a_1.placa_veiculo,
                            (max(a_1.km_veiculo) - min(a_1.km_veiculo)) AS km_rodado
                           FROM (afericao_valores av_1
                             JOIN afericao a_1 ON ((a_1.codigo = av_1.cod_afericao)))
                          GROUP BY av_1.cod_pneu, av_1.cod_unidade, a_1.placa_veiculo) total_km_rodado
                  GROUP BY total_km_rodado.cod_pneu, total_km_rodado.cod_unidade) total_km ON (((total_km.cod_pneu = av.cod_pneu) AND (total_km.cod_unidade = av.cod_unidade))))
          GROUP BY av.cod_pneu, av.cod_unidade, p_1.vida_atual, p_1.vida_total, pru.sulco_minimo_descarte, pru.sulco_minimo_recapagem) dados ON ((dados.cod_pneu = p.codigo)))
     JOIN dimensao_pneu dp ON ((dp.codigo = p.cod_dimensao)))
     JOIN unidade u ON ((u.codigo = p.cod_unidade)))
     JOIN modelo_pneu mp ON (((mp.codigo = p.cod_modelo) AND (mp.cod_empresa = u.cod_empresa))))
     JOIN marca_pneu map ON ((map.codigo = mp.cod_marca)));

comment on view view_analise_pneus
is 'View utilizada para gerar dados de uso sobre os pneus, esses dados são usados para gerar relatórios';

