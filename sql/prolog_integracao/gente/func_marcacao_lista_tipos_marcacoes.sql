-- Sobre:
--
-- Function disponível na API do ProLog para listar os tipos de marcações de uma empresa.
--
-- A function lista todas as informações dos tipos de marcações de todas as unidades. Utiliza uma flag como parâmetro
-- para distinguir se deve retornar apenas tipos de marcações ativos ou também os inativos.
--
-- Precondições:
-- Para listar os tipos de marcações é necessário que o token repassado para function exista.
--
-- Histórico:
-- 2019-08-30 -> Function criada (diogenesvanzella - PL-2271).
CREATE OR REPLACE FUNCTION INTEGRACAO.FUNC_MARCACAO_LISTA_TIPOS_MARCACOES(
  F_TOKEN_INTEGRACAO TEXT,
  F_APENAS_TIPO_MARCACOES_ATIVAS BOOLEAN)
  RETURNS TABLE (
    COD_EMPRESA                  BIGINT,
    COD_UNIDADE                  BIGINT,
    CODIGO                       BIGINT,
    NOME                         TEXT,
    ICONE                        TEXT,
    TEMPO_RECOMENDADO_EM_MINUTOS BIGINT,
    TEMPO_ESTOURO_EM_MINUTOS     BIGINT,
    HORARIO_SUGERIDO_MARCAR      TIME,
    IS_TIPO_JORNADA              BOOLEAN,
    DESCONTA_JORNADA_BRUTA       BOOLEAN,
    DESCONTA_JORNADA_LIQUIDA     BOOLEAN,
    STATUS_ATIVO                 BOOLEAN)
LANGUAGE PLPGSQL
AS $$
DECLARE
  COD_EMPRESA_TOKEN BIGINT := (SELECT TI.COD_EMPRESA
                               FROM INTEGRACAO.TOKEN_INTEGRACAO TI
                               WHERE TI.TOKEN_INTEGRACAO = F_TOKEN_INTEGRACAO);
BEGIN
  -- Validamos se a Empresa é válida.
  PERFORM FUNC_GARANTE_EMPRESA_EXISTE(COD_EMPRESA_TOKEN,
                                      FORMAT('O token %s não é de uma Empresa válida', F_TOKEN_INTEGRACAO));

  RETURN QUERY
  SELECT
    U.COD_EMPRESA                                       AS COD_EMPRESA,
    U.CODIGO                                            AS COD_UNIDADE,
    IT.CODIGO                                           AS CODIGO,
    IT.NOME::TEXT                                       AS NOME,
    IT.ICONE::TEXT                                      AS ICONE,
    IT.TEMPO_RECOMENDADO_MINUTOS                        AS TEMPO_RECOMENDADO_EM_MINNUTOS,
    IT.TEMPO_ESTOURO_MINUTOS                            AS TEMPO_ESTOURO_EM_MINUTOS,
    IT.HORARIO_SUGERIDO                                 AS HORARIO_SUGERIDO_MARCAR,
    F_IF(IT.CODIGO = MTJ.COD_TIPO_JORNADA, TRUE, FALSE) AS IS_TIPO_JORNADA,
    F_IF(MTDCJBL.DESCONTA_JORNADA_BRUTA IS NULL,
         FALSE,
         MTDCJBL.DESCONTA_JORNADA_BRUTA)                AS DESCONTA_JORNADA_BRUTA,
    F_IF(MTDCJBL.DESCONTA_JORNADA_LIQUIDA IS NULL,
         FALSE,
         MTDCJBL.DESCONTA_JORNADA_LIQUIDA)              AS DESCONTA_JORNADA_LIQUIDA,
    IT.ATIVO                                            AS STATUS_ATIVO
  FROM INTERVALO_TIPO IT
    JOIN UNIDADE U
      ON IT.COD_UNIDADE = U.CODIGO
    LEFT JOIN MARCACAO_TIPO_JORNADA MTJ
      ON IT.COD_UNIDADE = MTJ.COD_UNIDADE AND IT.CODIGO = MTJ.COD_TIPO_JORNADA
    LEFT JOIN MARCACAO_TIPOS_DESCONTADOS_CALCULO_JORNADA_BRUTA_LIQUIDA MTDCJBL
      ON IT.COD_UNIDADE = MTDCJBL.COD_UNIDADE AND IT.CODIGO = MTDCJBL.COD_TIPO_DESCONTADO
  WHERE IT.COD_UNIDADE IN (SELECT U.CODIGO
                           FROM UNIDADE U
                           WHERE U.COD_EMPRESA = COD_EMPRESA_TOKEN)
        AND F_IF(F_APENAS_TIPO_MARCACOES_ATIVAS, IT.ATIVO = TRUE, TRUE);
END;
$$;