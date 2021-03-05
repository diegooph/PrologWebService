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

