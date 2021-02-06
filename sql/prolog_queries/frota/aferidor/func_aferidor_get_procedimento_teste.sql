-- Sobre:
-- Busca o procedimento de testes do equipamento de aferição.
--
-- Histórico:
-- 2019-10-08 -> Function criada (luizfp - PL-2343).
-- 2019-10-10 -> Melhora desempenho utilizando SQL (diogenesvanzella - PL-2343).
CREATE OR REPLACE FUNCTION AFERIDOR.FUNC_AFERIDOR_GET_PROCEDIMENTO_TESTE()
  RETURNS TEXT[]
LANGUAGE SQL
AS
$$
SELECT CT.COMANDOS FROM AFERIDOR.COMANDOS_TESTE CT;
$$;