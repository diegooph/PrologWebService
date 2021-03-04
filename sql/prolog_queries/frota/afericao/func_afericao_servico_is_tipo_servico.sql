-- Sobre:
--
-- Function utilizada para verificar se um serviço é de um tipo específico. A function retorna 'true' se o serviço
-- analisado é do tipo recebido por parâmetro e 'false' para o caso contrário.
--
-- Histórico:
-- 2019-09-17 -> Function criada (diogenesvanzella - PL-2296).
CREATE OR REPLACE FUNCTION FUNC_AFERICAO_SERVICO_IS_TIPO_SERVICO(F_COD_SERVICO BIGINT, F_TIPO_SERVICO_PNEU TEXT)
  RETURNS BOOLEAN
LANGUAGE PLPGSQL
AS $$
BEGIN
  RETURN
  (SELECT AM.TIPO_SERVICO
   FROM AFERICAO_MANUTENCAO AM
   WHERE AM.CODIGO = F_COD_SERVICO) = F_TIPO_SERVICO_PNEU;
END;
$$;