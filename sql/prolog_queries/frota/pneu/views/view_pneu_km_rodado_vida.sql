-- Sobre:
--
-- Esta view calcula o km total percorrido por um pneu em cada vida. O que compõem este cálculo é o seguinte:
-- 1 - Km percorrido desde a aplicação de um pneu em uma placa até a primeira aferição nessa mesma placa.
-- 2 - Km das aferições realizadas na placa em que o pneu está aplicado.
--     * Aqui temos duas observações importantes:
--       a) Apenas aferições de placa, não avulsas, são consideradas.
--       b) É preciso que exista mais de uma aferição na mesma placa, pois o cálculo é feito pegando a diferença de km
--          entre a última e a primeira aferição da placa.
--       c) Se não houver nenhuma aferição em uma placa para o cálculo, iremos apresentar o pneu, com a vida atual e km
--          zerado.
-- 3 - Km percorrido desde a última aferição do pneu em uma placa até a remoção do pneu dessa mesma placa.
--
-- Histórico:
-- 2019-02-19 -> Arquivo da view criado (thaisksf).
-- 2020-06-05 -> Alterado cálculo de km para contabilizar kms entre movimentações e aferições (luiz_fp - PL-2803).
-- 2020-11-12 -> Adiciona na view pneus sem aferição (gustavocnp95 - PL-3180).
create or replace view view_pneu_km_rodado_vida as
select p.codigo                                                                      as cod_pneu,
       coalesce(q.vida_pneu, p.vida_atual)                                           as vida_pneu,
       (coalesce(sum(q.km_rodado), 0)
           +
        (select func_pneu_calcula_km_aplicacao_remocao_pneu(p.codigo, q.vida_pneu))) as km_rodado_vida
from (select av.cod_pneu,
             av.vida_momento_afericao                as vida_pneu,
             (max(a.km_veiculo) - min(a.km_veiculo)) as km_rodado
      from (afericao_valores av
               join afericao a on ((a.codigo = av.cod_afericao)))
      where ((a.tipo_processo_coleta)::text = 'PLACA'::text)
      group by av.cod_pneu, a.placa_veiculo, av.vida_momento_afericao
      order by av.cod_pneu) q
         right join pneu_data p on p.codigo = q.cod_pneu
group by p.codigo, q.vida_pneu
order by p.codigo, q.vida_pneu;

