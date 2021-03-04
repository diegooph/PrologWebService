create view view_intervalo as
  SELECT row_number() OVER (PARTITION BY i.cod_unidade ORDER BY i.codigo) AS codigo_marcacao_por_unidade,
    i.codigo,
    i.cod_unidade,
    i.cod_tipo_intervalo,
    i.cpf_colaborador,
    i.data_hora,
    i.tipo_marcacao,
    i.fonte_data_hora,
    i.justificativa_tempo_recomendado,
    i.justificativa_estouro,
    i.latitude_marcacao,
    i.longitude_marcacao,
    i.valido,
    i.foi_ajustado,
    i.cod_colaborador_insercao,
    i.status_ativo,
    i.data_hora_sincronizacao
   FROM intervalo i;

