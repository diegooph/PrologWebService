-- Remove vínculo de pneus que estão deletados.
delete
from veiculo_pneu vp
where vp.cod_pneu in
      (select pd.codigo from pneu_data pd where pd.deletado = true and pd.status = 'EM_USO');

-- Coloca pneus que estão deletados para estoque.
update pneu_data pd
set status = 'ESTOQUE'
where pd.deletado = true
  and pd.status = 'EM_USO';