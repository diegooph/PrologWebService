-- ########################################################################################################
-- ########################################################################################################
-- ######################## FUNCTION PARA ATUALIZAR AS INFORMAÇÕES DE BANDA DO PNEU #######################
-- ########################################################################################################
-- ########################################################################################################
CREATE OR REPLACE FUNCTION func_pneus_update_banda_pneu(
  f_cod_pneu BIGINT, f_cod_modelo_banda BIGINT, f_custo_banda REAL)
  RETURNS BOOLEAN
LANGUAGE plpgsql
AS $$
DECLARE
  cod_servico_realizado BIGINT;
BEGIN
  cod_servico_realizado = (
    SELECT CODIGO
    FROM PNEU_SERVICO_REALIZADO PSR
      JOIN PNEU_SERVICO_REALIZADO_INCREMENTA_VIDA PSRIV
        ON PSR.CODIGO = PSRIV.cod_pneu_servico_realizado
           AND PSR.fonte_servico_realizado = PSRIV.fonte_servico_realizado
    WHERE
      PSR.COD_PNEU = f_cod_pneu
      AND PSR.fonte_servico_realizado = 'FONTE_CADASTRO'
    ORDER BY CODIGO DESC
    LIMIT 1);
  UPDATE PNEU_SERVICO_REALIZADO
  SET CUSTO = f_custo_banda
  WHERE CODIGO = cod_servico_realizado;
  UPDATE PNEU_SERVICO_REALIZADO_INCREMENTA_VIDA
  SET COD_MODELO_BANDA = f_cod_modelo_banda
  WHERE COD_PNEU_SERVICO_REALIZADO = cod_servico_realizado;
  RETURN TRUE;
END;
$$;

-- ########################################################################################################
-- ########################################################################################################