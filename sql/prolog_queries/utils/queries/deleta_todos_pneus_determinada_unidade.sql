-- Deleta todos os pneus de uma determinada unidade

BEGIN  TRANSACTION ;

DELETE FROM afericao_valores WHERE cod_unidade = -1 /*CÓD_UNIDADE_AQUI*/;
DELETE FROM afericao_manutencao WHERE cod_unidade = -1 /*CÓD_UNIDADE_AQUI*/;
DELETE FROM afericao WHERE cod_unidade = -1 /*CÓD_UNIDADE_AQUI*/;
DELETE FROM movimentacao_origem WHERE cod_movimentacao IN (SELECT CODIGO FROM movimentacao WHERE cod_unidade = -1 /*CÓD_UNIDADE_AQUI*/);
DELETE FROM movimentacao_destino WHERE cod_movimentacao IN (SELECT CODIGO FROM movimentacao WHERE cod_unidade = -1 /*CÓD_UNIDADE_AQUI*/);
DELETE FROM movimentacao WHERE cod_unidade = -1 /*CÓD_UNIDADE_AQUI*/;
DELETE FROM movimentacao_processo WHERE cod_unidade = -1 /*CÓD_UNIDADE_AQUI*/;
DELETE FROM veiculo_pneu WHERE cod_unidade = -1 /*CÓD_UNIDADE_AQUI*/;
DELETE FROM pneu_valor_vida WHERE cod_unidade = -1 /*CÓD_UNIDADE_AQUI*/;
DELETE FROM pneu_foto_cadastro where cod_unidade_pneu = -1 /*CÓD_UNIDADE_AQUI*/;
DELETE FROM pneu WHERE cod_unidade = -1 /*CÓD_UNIDADE_AQUI*/;

END TRANSACTION ;