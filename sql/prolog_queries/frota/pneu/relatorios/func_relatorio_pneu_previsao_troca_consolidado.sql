-- Sobre:
--
-- A function trás a previsão de quantos pneus você deve comprar em qual data, agrupados por marca, modelo e medida.
--
-- Histórico:
-- 2020-12-01 -> Criado arquivo específico. (gustavocnp95 PL-3332)
create function func_relatorio_pneu_previsao_troca_consolidado(f_cod_unidade text[], f_status_pneu text, f_data_inicial date, f_data_final date)
    returns TABLE("UNIDADE" text, data text, marca text, modelo text, medidas text, "QUANTIDADE" bigint)
    language sql
as
$$
SELECT
  VAP."UNIDADE ALOCADO",
  TO_CHAR(VAP."PREVISÃO DE TROCA", 'DD/MM/YYYY') AS DATA,
  VAP."MARCA",
  VAP."MODELO",
  VAP."MEDIDAS",
  COUNT(VAP."MODELO") AS QUANTIDADE
FROM VIEW_ANALISE_PNEUS VAP
WHERE VAP.COD_UNIDADE::TEXT LIKE ANY (f_cod_unidade)
      AND VAP."PREVISÃO DE TROCA" BETWEEN f_data_inicial AND f_data_final
      AND VAP."STATUS PNEU" = f_status_pneu
GROUP BY VAP."UNIDADE ALOCADO", VAP."PREVISÃO DE TROCA", VAP."MARCA",  VAP."MODELO",  VAP."MEDIDAS"
ORDER BY VAP."UNIDADE ALOCADO", VAP."PREVISÃO DE TROCA" ASC, QUANTIDADE DESC;
$$;