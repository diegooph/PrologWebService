-- Essa migração deve ser executada quando o WS versão 49 for publicado.
BEGIN TRANSACTION;

CREATE FUNCTION func_relatorio_intervalo_portaria_1510_tipo_3(f_data_inicial date, f_data_final date, f_time_zone_datas text, f_cod_unidade bigint, f_cpf_colaborador bigint)
  RETURNS TABLE("NSR" text, "TIPO DO REGISTRO" text, "DATA DA MARCAÇÃO" text, "HORA DA MARCAÇÃO" text, "PIS COLABORADOR" text)
LANGUAGE SQL
AS $$
SELECT
  LPAD(vi.codigo_marcacao_por_unidade :: TEXT, 9, '0')             AS NSR,
  '3' :: TEXT                                                      AS TIPO_REGISTRO,
  TO_CHAR(VI.data_hora AT TIME ZONE F_TIME_ZONE_DATAS, 'DDMMYYYY') AS DATA_MARCACAO,
  TO_CHAR(VI.data_hora AT TIME ZONE F_TIME_ZONE_DATAS, 'HH24MI')   AS HORARIO_MARCACAO,
  C.PIS                                                            AS PIS
FROM VIEW_INTERVALO VI
  JOIN COLABORADOR C ON VI.cpf_colaborador = C.cpf
WHERE
  (VI.data_hora AT TIME ZONE F_TIME_ZONE_DATAS) :: DATE >= F_DATA_INICIAL
  AND
  (VI.data_hora AT TIME ZONE F_TIME_ZONE_DATAS) :: DATE <= F_DATA_FINAL
  AND
  VI.COD_UNIDADE = f_cod_unidade
  AND
  CASE WHEN F_CPF_COLABORADOR IS NULL
    THEN TRUE
  ELSE VI.cpf_colaborador = F_CPF_COLABORADOR END;
$$;

END TRANSACTION;