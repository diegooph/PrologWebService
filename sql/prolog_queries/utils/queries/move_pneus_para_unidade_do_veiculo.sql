--Move pneus que estão associados a algum veículo, e que estão em unidades diferentes desses veículos, para a unidade correta


SELECT P.codigo, P.cod_unidade FROM PNEU P JOIN veiculo_pneu VP ON P.codigo = VP.cod_pneu JOIN VEICULO V ON VP.placa = V.placa
WHERE P.cod_unidade != VP.cod_unidade;

UPDATE VEICULO_PNEU SET COD_UNIDADE = -1 /*COD_UNIDADE_AQUI*/ WHERE cod_pneu IN (SELECT P.codigo FROM PNEU P JOIN veiculo_pneu VP ON P.codigo = VP.cod_pneu JOIN VEICULO V ON VP.placa = V.placa
WHERE P.cod_unidade != VP.cod_unidade);