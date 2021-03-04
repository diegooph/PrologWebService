-- Sobre:
--
-- Essa view realiza um comparativo dos mapas importados (ambev) no sistema com as marcações dos colaboradores que
-- compõem cada mapa (motorista, ajudante 1 e 2).
-- As informações são trazidas apenas para os colaboradores que além de terem mapas vinculados, também estão cadastrados
-- no Prolog.
-- Para cada integrante do mapa são trazidas as seguintes informações:
--  • CPF (ou "-" se não tiver);
--  • Nome (ou "-" se não tiver);
--  • Horas e minutos do início do intervalo (ou "-" se não tiver);
--  • Horas e minutos do fim do intervalo (ou "-" se não tiver);
--  • O tempo decorrido entre a marcação de início e fim (ou "-" se não tiver);
--  • Se cumpriu o tempo mínimo (ou "-" se não tiver);
--  • Booleano indicado se marcações (de início e fim) foram de aparelhos reconhecidos pela empresa (através do IMEI);
--
-- Precondições:
--  • Para trazer alguma informação significativa, é necessário que a unidade tenha importado o arquivo de mapa
--  (tabela MAPA).
--  • Também é preciso que a unidade tenha as parametrizações necessárias definidas na tabela UNIDADE_FUNCAO_PRODUTIVIDADE.
--  • Apenas intervalos que tenham início e fim no mesmo dia do mapa são contabilizadas para a aderência.
--
--
-- Functions de dependência:
--  • f_if
--  • tz_unidade
--  • to_minutes_trunc
--  • tz_date
--  • func_intervalos_agrupados
--
-- Histórico:
-- 2019-08-12 -> View totalmente modificada, corrigido bugs e adicionada ao GitHub (luizfp - PL-2220).
-- 2020-03-25 -> Apenas intervalos que tenham início e fim no mesmo dia do mapa são
--               contabilizadas para a aderência (luizfp - PL-2535)
CREATE OR REPLACE VIEW VIEW_EXTRATO_MAPAS_VERSUS_INTERVALOS AS
SELECT m.data,
       m.mapa,
       m.cod_unidade,
       -- (PL-2220) Antes era utilizado "m.fator + 1", mas isso não funciona porque acaba considerando colaboradores
       -- que não estão cadastrados no Prolog e possuem apenas os mapas importados. Lembre-se que fator é quantidade de
       -- ajudantes que sairam junto do motorista. Podendo ser, atualmente, 1 ou 2.
       (F_IF(mot.cpf is null, 0, 1) + F_IF(aj1.cpf is null, 0, 1) + F_IF(aj2.cpf is null, 0, 1))         AS intervalos_previstos,
       (F_IF(int_mot.data_hora_fim is null or int_mot.data_hora_inicio is null, 0, 1) +
        F_IF(int_aj1.data_hora_fim is null or int_aj1.data_hora_inicio is null, 0, 1) +
        F_IF(int_aj2.data_hora_fim is null or int_aj2.data_hora_inicio is null, 0, 1))                   AS intervalos_realizados,
       mot.cpf                                                                                           AS cpf_motorista,
       mot.nome                                                                                          AS nome_motorista,
       COALESCE(to_char(int_mot.data_hora_inicio at time zone tz_unidade(int_mot.cod_unidade), 'HH24:MI'),
                '-')                                                                                     AS inicio_intervalo_mot,
       COALESCE(to_char(int_mot.data_hora_fim at time zone tz_unidade(int_mot.cod_unidade), 'HH24:MI'),
                '-')                                                                                     AS fim_intervalo_mot,
       F_IF(int_mot.device_imei_inicio_reconhecido AND int_mot.device_imei_fim_reconhecido, TRUE, FALSE) AS marcacoes_reconhecidas_mot,
       coalesce(to_minutes_trunc(int_mot.data_hora_fim - int_mot.data_hora_inicio) :: text,
                '-')                                                                                     AS tempo_decorrido_minutos_mot,
       CASE
           WHEN (int_mot.data_hora_fim IS NULL)
               THEN '-'
           WHEN (tipo_mot.tempo_recomendado_minutos > to_minutes_trunc(int_mot.data_hora_fim - int_mot.data_hora_inicio))
               THEN 'NÃO'
           ELSE 'SIM'
           END                                                                                           AS mot_cumpriu_tempo_minimo,
       aj1.cpf                                                                                           AS cpf_aj1,
       COALESCE(aj1.nome, '-')                                                                           AS nome_aj1,
       COALESCE(to_char(int_aj1.data_hora_inicio at time zone tz_unidade(int_aj1.cod_unidade), 'HH24:MI'),
                '-')                                                                                     AS inicio_intervalo_aj1,
       COALESCE(to_char(int_aj1.data_hora_fim at time zone tz_unidade(int_aj1.cod_unidade), 'HH24:MI'),
                '-')                                                                                     AS fim_intervalo_aj1,
       F_IF(int_aj1.device_imei_inicio_reconhecido AND int_aj1.device_imei_fim_reconhecido, TRUE, FALSE) AS marcacoes_reconhecidas_aj1,
       coalesce(to_minutes_trunc(int_aj1.data_hora_fim - int_aj1.data_hora_inicio) :: text,
                '-')                                                                                     AS tempo_decorrido_minutos_aj1,
       CASE
           WHEN (int_aj1.data_hora_fim IS NULL)
               THEN '-'
           WHEN (tipo_aj1.tempo_recomendado_minutos > to_minutes_trunc(int_aj1.data_hora_fim - int_aj1.data_hora_inicio))
               THEN 'NÃO'
           ELSE 'SIM'
           END                                                                                           AS aj1_cumpriu_tempo_minimo,
       aj2.cpf                                                                                           AS cpf_aj2,
       COALESCE(aj2.nome, '-')                                                                           AS nome_aj2,
       COALESCE(to_char(int_aj2.data_hora_inicio at time zone tz_unidade(int_aj2.cod_unidade), 'HH24:MI'),
                '-')                                                                                     AS inicio_intervalo_aj2,
       COALESCE(to_char(int_aj2.data_hora_fim at time zone tz_unidade(int_aj2.cod_unidade), 'HH24:MI'),
                '-')                                                                                     AS fim_intervalo_aj2,
       F_IF(int_aj2.device_imei_inicio_reconhecido AND int_aj2.device_imei_fim_reconhecido, TRUE, FALSE) AS marcacoes_reconhecidas_aj2,
       coalesce(to_minutes_trunc(int_aj2.data_hora_fim - int_aj2.data_hora_inicio) :: text,
                '-')                                                                                     AS tempo_decorrido_minutos_aj2,
       CASE
           WHEN (int_aj2.data_hora_fim IS NULL)
               THEN '-'
           WHEN (tipo_aj2.tempo_recomendado_minutos > to_minutes_trunc(int_aj2.data_hora_fim - int_aj2.data_hora_inicio))
               THEN 'NÃO'
           ELSE 'SIM'
           END                                                                                           AS aj2_cumpriu_tempo_minimo
FROM mapa m
         JOIN unidade_funcao_produtividade ufp
              ON ufp.cod_unidade = m.cod_unidade
         JOIN colaborador mot
              ON mot.cod_unidade = m.cod_unidade
                  AND mot.cod_funcao = ufp.cod_funcao_motorista
                  AND mot.matricula_ambev = m.matricmotorista
         LEFT JOIN colaborador aj1
                   ON aj1.cod_unidade = m.cod_unidade
                       AND aj1.cod_funcao = ufp.cod_funcao_ajudante
                       AND aj1.matricula_ambev = m.matricajud1
         LEFT JOIN colaborador aj2
                   ON aj2.cod_unidade = m.cod_unidade
                       AND aj2.cod_funcao = ufp.cod_funcao_ajudante
                       AND aj2.matricula_ambev = m.matricajud2
         LEFT JOIN func_intervalos_agrupados(NULL, NULL, NULL) int_mot
                   ON int_mot.cpf_colaborador = mot.cpf
                       AND tz_date(int_mot.data_hora_inicio, tz_unidade(int_mot.cod_unidade)) = m.data
                       AND tz_date(int_mot.data_hora_fim, tz_unidade(int_mot.cod_unidade)) = m.data
         LEFT JOIN intervalo_tipo tipo_mot
                   ON tipo_mot.codigo = int_mot.cod_tipo_intervalo
         LEFT JOIN func_intervalos_agrupados(NULL, NULL, NULL) int_aj1
                   ON int_aj1.cpf_colaborador = aj1.cpf
                       AND tz_date(int_aj1.data_hora_inicio, tz_unidade(int_aj1.cod_unidade)) = m.data
                       AND tz_date(int_aj1.data_hora_fim, tz_unidade(int_aj1.cod_unidade)) = m.data
         LEFT JOIN intervalo_tipo tipo_aj1
                   ON tipo_aj1.codigo = int_aj1.cod_tipo_intervalo
         LEFT JOIN func_intervalos_agrupados(NULL, NULL, NULL) int_aj2
                   ON int_aj2.cpf_colaborador = aj2.cpf
                       AND tz_date(int_aj2.data_hora_inicio, tz_unidade(int_aj2.cod_unidade)) = m.data
                       AND tz_date(int_aj2.data_hora_fim, tz_unidade(int_aj2.cod_unidade)) = m.data
         LEFT JOIN intervalo_tipo tipo_aj2
                   ON tipo_aj2.codigo = int_aj2.cod_tipo_intervalo
ORDER BY m.mapa DESC;