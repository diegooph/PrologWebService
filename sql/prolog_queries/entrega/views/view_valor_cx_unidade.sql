create view view_valor_cx_unidade(cod_unidade, valor_cx_motorista_rota, valor_cx_ajudante_rota) as
SELECT DISTINCT m.cod_unidade,
               max(round(((m.vlbateujornmot / NULLIF(m.cxentreg, (0)::double precision)))::numeric,
                         2)) AS valor_cx_motorista_rota,
               max(round(((m.vlbateujornaju / NULLIF(m.cxentreg, (0)::double precision)))::numeric,
                         2)) AS valor_cx_ajudante_rota
FROM mapa m
WHERE ((m.vltotalmapa > (0)::double precision) AND (m.vlbateujornmot > (0)::double precision))
GROUP BY m.cod_unidade;