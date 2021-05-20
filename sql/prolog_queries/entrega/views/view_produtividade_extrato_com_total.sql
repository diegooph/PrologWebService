create view view_produtividade_extrato_com_total
as
select vpe.*,
       (vpe.valor_rota + vpe.valor_as + vpe.valor_recarga + vpe.valor_diferenca_eld)::double precision as valor
from view_produtividade_extrato vpe;