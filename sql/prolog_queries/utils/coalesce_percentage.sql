-- Sobre:
--
-- Essa function retorna o percentual com base no dividendo e divisor.
-- Ela realiza um tratamento para que não retorne erro caso o divisor seja 0.
--
-- Histórico:
-- 2019-08-15 -> Function criada e adicionada ao GitHub. (wvinim - PL-2220).
-- 2019-08-21 -> Alteração da function para corrigir o trunc. (wvinim).
CREATE OR REPLACE FUNCTION COALESCE_PERCENTAGE(F_DIVIDENDO FLOAT, F_DIVISOR FLOAT)
  RETURNS TEXT
LANGUAGE PLPGSQL
AS $$
BEGIN
  RETURN COALESCE(TRUNC(F_DIVIDENDO/NULLIF(F_DIVISOR, 0) * 100) || '%', '0%');
END;
$$;