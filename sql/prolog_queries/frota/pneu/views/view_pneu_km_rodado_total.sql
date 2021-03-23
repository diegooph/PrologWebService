create view view_pneu_km_rodado_total as
  WITH km_rodado_total AS (
         SELECT view_pneu_km_rodado_vida.cod_pneu,
            sum(view_pneu_km_rodado_vida.km_rodado_vida) AS total_km_rodado_todas_vidas
           FROM view_pneu_km_rodado_vida
          GROUP BY view_pneu_km_rodado_vida.cod_pneu
          ORDER BY view_pneu_km_rodado_vida.cod_pneu
        )
 SELECT km_vida.cod_pneu,
    km_vida.vida_pneu,
    km_vida.km_rodado_vida,
    km_total.total_km_rodado_todas_vidas
   FROM (view_pneu_km_rodado_vida km_vida
     JOIN km_rodado_total km_total ON ((km_vida.cod_pneu = km_total.cod_pneu)))
  ORDER BY km_vida.cod_pneu, km_vida.vida_pneu;

