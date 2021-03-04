--Atualiza status de pneus EM_USO que deveriam estar no ESTOQUE

UPDATE PNEU SET STATUS = 'ESTOQUE' WHERE CODIGO IN (SELECT P.codigo FROM PNEU P WHERE P.status = 'EM_USO' AND P.CODIGO NOT IN (SELECT VP.COD_PNEU FROM veiculo_pneu VP));