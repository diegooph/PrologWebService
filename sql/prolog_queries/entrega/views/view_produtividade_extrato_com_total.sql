create view view_produtividade_extrato_com_total
as
select vpe.*,
       round((vpe.valor_rota + vpe.valor_as + vpe.valor_recarga
           + f_if(vpe.diferenca_eld_soma_total, vpe.valor_diferenca_eld, 0::double precision))::numeric,
             2)::double precision as valor
from view_produtividade_extrato vpe;