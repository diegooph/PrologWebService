-- Sobre:
--
-- Esta function retorna a altura do sulco central do pneu baseado em código de unidades e status de pneu.
--
-- Histórico:
-- 2020-12-01 -> Criado arquivo específico. (gustavocnp95 PL-3332)
create function func_relatorio_pneus_by_faixa_sulco(f_cod_unidade text[], f_status_pneu text[])
    returns table(altura_sulco_central real)
    language sql
as
$$
select coalesce(altura_sulco_central_interno, altura_sulco_central_externo, -1) as altura_sulco_central
from pneu
where cod_unidade::text like any (f_cod_unidade)
      and status like any (f_status_pneu)
order by 1 desc;
$$;