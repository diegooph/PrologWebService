-- Sobre:
--
-- Esta function retorna um objeto de tipo de marcação
--
-- Precondições:
-- 1) Criada a view VIEW_INTERVALO_TIPO
--
-- Histórico:
-- 2019-08-29 -> Adicionada coluna de código auxiliar (wvinim - PL-2223).
CREATE OR REPLACE FUNCTION FUNC_MARCACAO_GET_TIPO_MARCACAO(F_COD_TIPO_MARCACAO BIGINT)
  RETURNS TABLE(
    CODIGO_TIPO_INTERVALO             BIGINT,
    CODIGO_TIPO_INTERVALO_POR_UNIDADE BIGINT,
    NOME_TIPO_INTERVALO               CHARACTER VARYING,
    COD_UNIDADE                       BIGINT,
    ATIVO                             BOOLEAN,
    HORARIO_SUGERIDO                  TIME WITHOUT TIME ZONE,
    ICONE                             CHARACTER VARYING,
    TEMPO_ESTOURO_MINUTOS             BIGINT,
    TEMPO_RECOMENDADO_MINUTOS         BIGINT,
    TIPO_JORNADA                      BOOLEAN,
    COD_AUXILIAR                      TEXT)
LANGUAGE SQL
AS $$
SELECT
  IT.CODIGO                            AS CODIGO_TIPO_INTERVALO,
  IT.CODIGO_TIPO_INTERVALO_POR_UNIDADE AS CODIGO_TIPO_INTERVALO_POR_UNIDADE,
  IT.NOME                              AS NOME_TIPO_INTERVALO,
  IT.COD_UNIDADE                       AS COD_UNIDADE,
  IT.ATIVO                             AS ATIVO,
  IT.HORARIO_SUGERIDO                  AS HORARIO_SUGERIDO,
  IT.ICONE                             AS ICONE,
  IT.TEMPO_ESTOURO_MINUTOS             AS TEMPO_ESTOURO_MINUTOS,
  IT.TEMPO_RECOMENDADO_MINUTOS         AS TEMPO_RECOMENDADO_MINUTOS,
  IT.TIPO_JORNADA                      AS TIPO_JORNADA,
  IT.COD_AUXILIAR                      AS COD_AUXILIAR
FROM VIEW_INTERVALO_TIPO IT
WHERE IT.CODIGO = F_COD_TIPO_MARCACAO;
$$;