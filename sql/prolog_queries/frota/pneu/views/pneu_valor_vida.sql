-- Sobre:
--
-- View que consolida os valores de cada vida dos pneus no ProLog.
--
-- Histórico:
-- 2019-11-26 -> Alterado JOIN para novo nome de coluna
-- (cod_pneu_servico_realizado -> cod_servico_realizado) (luizfp - PL-2295).
-- 2020-09-20 -> Adiciona coluna de fonte_servico_realizado para usar no relatório km rodado por vida (gustavocnp95 - PL-3156)
create or replace view pneu_valor_vida as
select srr.cod_unidade,
       srr.cod_pneu,
       srrec.cod_modelo_banda,
       srrec.vida_nova_pneu as vida,
       srr.custo            as valor,
       srrec.fonte_servico_realizado
from (pneu_servico_realizado srr
         join pneu_servico_realizado_incrementa_vida srrec
              on ((srr.codigo = srrec.cod_servico_realizado)));

comment on view pneu_valor_vida
    is 'View que contém o valor e a vida associados a um pneu, somente para pneus que já foram recapados.';

