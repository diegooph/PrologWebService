create view view_pneu_analise_vidas as
  WITH dados_afericao AS (
         SELECT a.codigo AS cod_afericao,
            a.cod_unidade AS cod_unidade_afericao,
            a.data_hora AS data_hora_afericao,
            a.tipo_processo_coleta AS tipo_processo_coleta_afericao,
            av.cod_pneu,
            av.vida_momento_afericao,
            av.altura_sulco_central_interno,
            av.altura_sulco_central_externo,
            av.altura_sulco_externo,
            av.altura_sulco_interno,
            row_number() OVER (PARTITION BY av.cod_pneu, av.vida_momento_afericao ORDER BY a.data_hora) AS row_number_asc,
            row_number() OVER (PARTITION BY av.cod_pneu, av.vida_momento_afericao ORDER BY a.data_hora DESC) AS row_number_desc
           FROM (afericao a
             JOIN afericao_valores av ON ((a.codigo = av.cod_afericao)))
        ), primeira_afericao AS (
         SELECT da.cod_pneu,
            da.vida_momento_afericao,
            da.cod_afericao,
            da.cod_unidade_afericao,
            da.data_hora_afericao
           FROM dados_afericao da
          WHERE (da.row_number_asc = 1)
        ), ultima_afericao AS (
         SELECT da.cod_pneu,
            da.vida_momento_afericao,
            da.cod_afericao,
            da.cod_unidade_afericao,
            da.data_hora_afericao
           FROM dados_afericao da
          WHERE (da.row_number_desc = 1)
        ), analises_afericoes AS (
         SELECT da.cod_pneu,
            da.vida_momento_afericao AS vida_analisada_pneu,
            count(da.cod_pneu) AS quantidade_afericoes_pneu_vida,
            max(GREATEST(da.altura_sulco_externo, da.altura_sulco_central_externo, da.altura_sulco_central_interno, da.altura_sulco_interno)) AS maior_sulco_aferido_vida,
            min(LEAST(da.altura_sulco_externo, da.altura_sulco_central_externo, da.altura_sulco_central_interno, da.altura_sulco_interno)) AS menor_sulco_aferido_vida
           FROM dados_afericao da
          GROUP BY da.cod_pneu, da.vida_momento_afericao
        )
 SELECT p.codigo AS cod_pneu,
    p.status,
    p.valor AS valor_pneu,
    COALESCE(pvv.valor, (0)::real) AS valor_banda,
    pa.data_hora_afericao AS data_hora_primeira_afericao,
    pa.cod_afericao AS cod_primeira_afericao,
    pa.cod_unidade_afericao AS cod_unidade_primeira_afericao,
    ua.data_hora_afericao AS data_hora_ultima_afericao,
    ua.cod_afericao AS cod_ultima_afericao,
    ua.cod_unidade_afericao AS cod_unidade_ultima_afericao,
    aa.vida_analisada_pneu,
    aa.quantidade_afericoes_pneu_vida,
    aa.maior_sulco_aferido_vida,
    aa.menor_sulco_aferido_vida,
    (aa.maior_sulco_aferido_vida - aa.menor_sulco_aferido_vida) AS sulco_gasto,
    (date_part('days'::text, (ua.data_hora_afericao - pa.data_hora_afericao)))::integer AS total_dias_ativo,
    km_rodado_pneu.km_rodado_vida AS total_km_rodado_vida,
    func_pneu_calcula_sulco_restante(p.vida_atual, p.vida_total, p.altura_sulco_externo, p.altura_sulco_central_externo, p.altura_sulco_central_interno, p.altura_sulco_interno, pru.sulco_minimo_recapagem, pru.sulco_minimo_descarte) AS sulco_restante,
        CASE
            WHEN ((date_part('days'::text, (ua.data_hora_afericao - pa.data_hora_afericao)) > (0)::double precision) AND ((aa.maior_sulco_aferido_vida - aa.menor_sulco_aferido_vida) > (0)::double precision)) THEN ((km_rodado_pneu.km_rodado_vida)::double precision / (aa.maior_sulco_aferido_vida - aa.menor_sulco_aferido_vida))
            ELSE (0)::double precision
        END AS km_por_mm_vida,
        CASE
            WHEN (km_rodado_pneu.km_rodado_vida = (0)::numeric) THEN (0)::double precision
            ELSE
            CASE
                WHEN (km_rodado_pneu.vida_pneu = 1) THEN (p.valor / (km_rodado_pneu.km_rodado_vida)::double precision)
                ELSE (COALESCE(pvv.valor, (0)::real) / (km_rodado_pneu.km_rodado_vida)::double precision)
            END
        END AS valor_por_km_vida
   FROM ((((((analises_afericoes aa
     JOIN primeira_afericao pa ON (((pa.cod_pneu = aa.cod_pneu) AND (pa.vida_momento_afericao = aa.vida_analisada_pneu))))
     JOIN ultima_afericao ua ON (((ua.cod_pneu = aa.cod_pneu) AND (ua.vida_momento_afericao = aa.vida_analisada_pneu))))
     JOIN pneu p ON ((aa.cod_pneu = p.codigo)))
     JOIN pneu_restricao_unidade pru ON ((p.cod_unidade = pru.cod_unidade)))
     LEFT JOIN pneu_valor_vida pvv ON ((p.codigo = pvv.cod_pneu)))
     JOIN view_pneu_km_rodado_vida km_rodado_pneu ON (((km_rodado_pneu.cod_pneu = aa.cod_pneu) AND (km_rodado_pneu.vida_pneu = aa.vida_analisada_pneu))))
  ORDER BY aa.cod_pneu, aa.vida_analisada_pneu;

