BEGIN TRANSACTION;

-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Verifica se colaborador está na empresa informada.
--
-- Precondições:
-- 1) Necessário o CPF do colaborador e o código da empresa para a verificar a integração.
--
-- Histórico:
-- 2019-08-20 -> Function criada (thaisksf).
CREATE OR REPLACE FUNCTION FUNC_GARANTE_INTEGRIDADE_EMPRESA_COLABORADOR(
  F_COD_EMPRESA BIGINT,
  F_CPF_COLABORADOR BIGINT)
  RETURNS VOID
LANGUAGE PLPGSQL
AS $$
BEGIN
  --VERIFICA SE COLABORADOR EXISTE
  PERFORM FUNC_GARANTE_COLABORADOR_EXISTE(F_CPF_COLABORADOR);

  --VERIFICA SE EMPRESA EXISTE
  PERFORM FUNC_GARANTE_EMPRESA_EXISTE(F_COD_EMPRESA);

  -- VERIFICA SE O COLABORADOR PERTENCE À EMPRESA.
  IF NOT EXISTS(SELECT C.CPF
                FROM COLABORADOR C
                WHERE C.CPF = F_CPF_COLABORADOR AND C.COD_EMPRESA = F_COD_EMPRESA)
  THEN RAISE EXCEPTION 'O colaborador com CPF: %, nome: %, não pertence a empresa: % - %!',
  F_CPF_COLABORADOR,
  (SELECT C.NOME FROM COLABORADOR C WHERE C.CPF = F_CPF_COLABORADOR),
  F_COD_EMPRESA,
  (SELECT E.NOME FROM EMPRESA E WHERE E.CODIGO = F_COD_EMPRESA);
  END IF;
END;
$$;

-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Verifica se a função - cargo - existe na empresa informada.
--
-- Precondições:
-- 1) Necessário o código da empresa e o código da funcao para a verificação da integridade entre empresa-funcao.
--
-- Histórico:
-- 2019-07-26 -> Function criada (thaisksf).
CREATE OR REPLACE FUNCTION FUNC_GARANTE_CARGO_EXISTE(
  F_COD_EMPRESA BIGINT,
  F_COD_FUNCAO BIGINT)
  RETURNS VOID
LANGUAGE PLPGSQL
AS $$
BEGIN
  -- VERIFICA SE A FUNCAO EXISTE
  IF NOT EXISTS (SELECT F.CODIGO FROM FUNCAO F WHERE F.CODIGO = F_COD_FUNCAO AND F.COD_EMPRESA = F_COD_EMPRESA)
  THEN RAISE EXCEPTION 'A funcao de codigo: % não existe na empresa: %.', F_COD_FUNCAO,
  (SELECT E.NOME FROM EMPRESA E WHERE E.CODIGO = F_COD_EMPRESA);
  END IF;
END;
$$;
--######################################################################################################################
--######################################################################################################################
--############################## CRIA FUNCTION PARA TRANSFERIR COLABORADOR ENTRE EMPRESAS ##############################
--######################################################################################################################
--######################################################################################################################
-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Se o colaborador está inativo, será transferido para a unidade de outra empresa e terá seu status modificado para ativo.
-- Caso contrário, é necessário entrar em contato com a empresa de origem e perguntar se o funcionário n faz mais parte do quadro.
--
-- Precondições:
-- 1) Para a function funcionar é verificado a integridade entre unidade-setor.
-- 2) Verificado se unidades são de empresas distintas
-- 3) Verificado se o colaborador está inativo.
--
-- Histórico:
-- 2019-07-24 -> Function criada (thaisksf - PL-2164).
CREATE OR REPLACE FUNCTION FUNC_COLABORADOR_TRANSFERE_ENTRE_EMPRESAS(
      F_COD_UNIDADE_ORIGEM       BIGINT,
      F_CPF_COLABORADOR          BIGINT,
      F_COD_UNIDADE_DESTINO      INTEGER,
      F_COD_EMPRESA_DESTINO      BIGINT,
      F_COD_SETOR_DESTINO        BIGINT,
      F_COD_EQUIPE_DESTINO       BIGINT,
      F_COD_FUNCAO_DESTINO       INTEGER,
      F_MATRICULA_TRANS          INTEGER DEFAULT NULL,
      F_MATRICULA_AMBEV          INTEGER DEFAULT NULL,
      F_NIVEL_PERMISSAO          INTEGER DEFAULT 0,
  OUT AVISO_COLABORADOR_TRANSFERIDO TEXT)
  RETURNS TEXT
LANGUAGE PLPGSQL
SECURITY DEFINER
AS $$
DECLARE
  EMPRESA_ORIGEM BIGINT := (SELECT U.COD_EMPRESA AS EMPRESA_ORIGEM FROM UNIDADE U WHERE U.CODIGO = F_COD_UNIDADE_ORIGEM);
BEGIN
  -- VERIFICA SE EMPRESA ORIGEM/DESTINO SÃO DISTINTAS
  PERFORM FUNC_GARANTE_EMPRESAS_DISTINTAS(EMPRESA_ORIGEM, F_COD_EMPRESA_DESTINO);

  -- VERIFICA SE COLABORADOR ESTÁ INATIVO
  IF EXISTS (SELECT C.CPF FROM COLABORADOR C WHERE C.CPF = F_CPF_COLABORADOR AND C.STATUS_ATIVO = TRUE)
    THEN
      RAISE EXCEPTION 'O colaborador está ATIVO, portanto não pode ser transferido. Contate o gestor da unidade: %',
      (SELECT U.NOME FROM UNIDADE U WHERE U.CODIGO = F_COD_UNIDADE_ORIGEM);
  END IF;

  -- VERIFICA SE UNIDADE DESTINO EXISTE E SE PERTENCE A EMPRESA.
  PERFORM FUNC_GARANTE_INTEGRIDADE_EMPRESA_UNIDADE(F_COD_EMPRESA_DESTINO, F_COD_UNIDADE_DESTINO);

  --VERIFICA SE O COLABORADOR ESTÁ CADASTRADO E SE PERTENCE A UNIDADE ORIGEM.
  PERFORM FUNC_GARANTE_INTEGRIDADE_UNIDADE_COLABORADOR(F_COD_UNIDADE_ORIGEM, F_CPF_COLABORADOR);

  -- VERIFICA SE O SETOR EXISTE NA UNIDADE DESTINO.
  PERFORM FUNC_GARANTE_SETOR_EXISTE(F_COD_UNIDADE_DESTINO, F_COD_SETOR_DESTINO);

  -- VERIFICA SE A EQUIPE EXISTE NA UNIDADE DESTINO.
  PERFORM FUNC_GARANTE_EQUIPE_EXISTE(F_COD_UNIDADE_DESTINO, F_COD_EQUIPE_DESTINO);

  -- VERIFICA SE A FUNÇÃO EXISTE NA EMPRESA DESTINO.
  PERFORM FUNC_GARANTE_CARGO_EXISTE(F_COD_EMPRESA_DESTINO,F_COD_FUNCAO_DESTINO);

  -- VERIFICA SE PERMISSÃO EXISTE
  IF NOT EXISTS(SELECT P.CODIGO FROM PERMISSAO P WHERE P.CODIGO = F_NIVEL_PERMISSAO)
    THEN RAISE EXCEPTION 'Não existe permissão com o código: %', F_NIVEL_PERMISSAO;
  END IF;

  -- TRANSFERE COLABORADOR
  UPDATE COLABORADOR SET
    COD_UNIDADE     = F_COD_UNIDADE_DESTINO,
    COD_EMPRESA     = F_COD_EMPRESA_DESTINO,
    COD_SETOR       = F_COD_SETOR_DESTINO,
    COD_EQUIPE      = F_COD_EQUIPE_DESTINO,
    COD_FUNCAO      = F_COD_FUNCAO_DESTINO,
    MATRICULA_TRANS = F_MATRICULA_TRANS,
    MATRICULA_AMBEV = F_MATRICULA_AMBEV,
    COD_PERMISSAO   = F_NIVEL_PERMISSAO
  WHERE CPF = F_CPF_COLABORADOR AND COD_UNIDADE = F_COD_UNIDADE_ORIGEM;

  SELECT ('COLABORADOR: '
          || (SELECT C.NOME FROM COLABORADOR C WHERE C.CPF = F_CPF_COLABORADOR)
          || ' , TRANSFERIDO PARA A UNIDADE: '
          || (SELECT U.NOME FROM UNIDADE U WHERE U.CODIGO = F_COD_UNIDADE_DESTINO))
  INTO AVISO_COLABORADOR_TRANSFERIDO;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--############################ ADICIONA NOVOS FILTROS AO RELATÓRIO DE ESTRATIFICAÇÃO DE O.S. ###########################
--######################################################################################################################
--######################################################################################################################
-- PL-2175

-- DROPA A FUNCTION ANTIGA
DROP FUNCTION FUNC_CHECKLIST_OS_RELATORIO_ESTRATIFICACAO_OS(BIGINT[], TEXT, TEXT, TEXT, DATE, DATE);

-- RECRIA A FUNCTION COM OS PARÂMETROS NOVOS
CREATE OR REPLACE FUNCTION FUNC_CHECKLIST_OS_RELATORIO_ESTRATIFICACAO_OS(F_COD_UNIDADES BIGINT[],
                                                                         F_PLACA_VEICULO TEXT,
                                                                         F_STATUS_OS TEXT,
                                                                         F_STATUS_ITEM TEXT,
                                                                         F_DATA_INICIAL_ABERTURA DATE,
                                                                         F_DATA_FINAL_ABERTURA DATE,
                                                                         F_DATA_INICIAL_RESOLUCAO DATE,
                                                                         F_DATA_FINAL_RESOLUCAO DATE)
    RETURNS TABLE
            (
                UNIDADE                        TEXT,
                "CÓDICO OS"                    BIGINT,
                "ABERTURA OS"                  TEXT,
                "DATA LIMITE CONSERTO"         TEXT,
                "STATUS OS"                    TEXT,
                "PLACA"                        TEXT,
                "PERGUNTA"                     TEXT,
                "ALTERNATIVA"                  TEXT,
                "PRIORIDADE"                   TEXT,
                "PRAZO EM HORAS"               INTEGER,
                "DESCRIÇÃO"                    TEXT,
                "STATUS ITEM"                  TEXT,
                "DATA INÍCIO RESOLUÇÃO"        TEXT,
                "DATA FIM RESOLUÇÃO"           TEXT,
                "DATA RESOLIVDO PROLOG"        TEXT,
                "MECÂNICO"                     TEXT,
                "DESCRIÇÃO CONSERTO"           TEXT,
                "TEMPO DE CONSERTO EM MINUTOS" BIGINT,
                "KM ABERTURA"                  BIGINT,
                "KM FECHAMENTO"                BIGINT,
                "KM PERCORRIDO"                TEXT,
                "MOTORISTA"                    TEXT,
                "TIPO DO CHECKLIST"            TEXT
            )
    LANGUAGE SQL
AS
$$
SELECT U.NOME                                                                    AS NOME_UNIDADE,
       EO.COD_OS                                                                 AS CODIGO_OS,
       TO_CHAR(DATA_HORA, 'DD/MM/YYYY HH24:MI')                                  AS ABERTURA_OS,
       TO_CHAR(DATA_HORA + (PRAZO || ' HOUR') :: INTERVAL, 'DD/MM/YYYY HH24:MI') AS DATA_LIMITE_CONSERTO,
       (CASE
            WHEN STATUS_OS = 'A'
                THEN 'ABERTA'
            ELSE 'FECHADA' END)                                                  AS STATUS_OS,
       PLACA_VEICULO                                                             AS PLACA,
       PERGUNTA                                                                  AS PERGUNTA,
       ALTERNATIVA                                                               AS ALTERNATIVA,
       PRIORIDADE                                                                AS PRIORIDADE,
       PRAZO                                                                     AS PRAZO_EM_HORAS,
       RESPOSTA                                                                  AS DESCRICAO,
       CASE
           WHEN STATUS_ITEM = 'P'
               THEN 'PENDENTE'
           ELSE 'RESOLVIDO' END                                                  AS STATUS_ITEM,
       COALESCE(TO_CHAR(
                        DATA_HORA_INICIO_RESOLUCAO_UTC AT TIME ZONE TIME_ZONE_UNIDADE,
                        'DD/MM/YYYY HH24:MI'),
                '-')                                                             AS DATA_INICIO_RESOLUCAO,
       COALESCE(TO_CHAR(
                        DATA_HORA_FIM_RESOLUCAO_UTC AT TIME ZONE TIME_ZONE_UNIDADE,
                        'DD/MM/YYYY HH24:MI'),
                '-')                                                             AS DATA_FIM_RESOLUCAO,
       TO_CHAR(DATA_HORA_CONSERTO, 'DD/MM/YYYY HH24:MI')                         AS DATA_RESOLVIDO_PROLOG,
       NOME_MECANICO                                                             AS MECANICO,
       FEEDBACK_CONSERTO                                                         AS DESCRICAO_CONSERTO,
       TEMPO_REALIZACAO / 1000 / 60                                              AS TEMPO_CONSERTO_MINUTOS,
       KM                                                                        AS KM_ABERTURA,
       KM_FECHAMENTO                                                             AS KM_FECHAMENTO,
       COALESCE((KM_FECHAMENTO - KM) :: TEXT, '-')                               AS KM_PERCORRIDO,
       NOME_REALIZADOR_CHECKLIST                                                 AS MOTORISTA,
       CASE
           WHEN TIPO_CHECKLIST = 'S'
               THEN 'SAÍDA'
           ELSE 'RETORNO' END                                                    AS TIPO_CHECKLIST
FROM ESTRATIFICACAO_OS EO
         JOIN UNIDADE U
              ON EO.COD_UNIDADE = U.CODIGO
WHERE EO.COD_UNIDADE = ANY (F_COD_UNIDADES)
  AND EO.PLACA_VEICULO LIKE F_PLACA_VEICULO
  AND EO.STATUS_OS LIKE F_STATUS_OS
  AND EO.STATUS_ITEM LIKE F_STATUS_ITEM
  AND CASE
          -- O usuário pode filtrar tanto por início e fim de abertura ou por início e fim de resolução ou, ainda,
          -- por ambos.
          WHEN (F_DATA_INICIAL_ABERTURA,
                F_DATA_FINAL_ABERTURA,
                F_DATA_INICIAL_RESOLUCAO,
                F_DATA_FINAL_RESOLUCAO) IS NOT NULL
              THEN (
                  EO.DATA_HORA :: DATE BETWEEN F_DATA_INICIAL_ABERTURA AND F_DATA_FINAL_ABERTURA
                  AND
                  EO.DATA_HORA_CONSERTO :: DATE BETWEEN F_DATA_INICIAL_RESOLUCAO AND F_DATA_FINAL_RESOLUCAO)
          WHEN (F_DATA_INICIAL_ABERTURA,
                F_DATA_FINAL_ABERTURA) IS NOT NULL
              THEN
              EO.DATA_HORA :: DATE BETWEEN F_DATA_INICIAL_ABERTURA AND F_DATA_FINAL_ABERTURA
          WHEN (F_DATA_INICIAL_RESOLUCAO,
                F_DATA_FINAL_RESOLUCAO) IS NOT NULL
              THEN
              EO.DATA_HORA_CONSERTO :: DATE BETWEEN F_DATA_INICIAL_RESOLUCAO AND F_DATA_FINAL_RESOLUCAO

          -- Se não entrar em nenhuma condição conhecida, retornamos FALSE para o relatório não retornar dado nenhum.
          ELSE FALSE END
ORDER BY U.NOME, EO.COD_OS, EO.PRAZO;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--############################################ INSERE DIAGRAMA TRICICLO INVERTIDO ######################################
--######################################################################################################################
--######################################################################################################################
--PS-2133
INSERT INTO VEICULO_DIAGRAMA (
  CODIGO,
  NOME,
  URL_IMAGEM)
VALUES (
  13,
  'TRICICLO INVERTIDO',
  'WWW.GOOGLE.COM/TRICICLO-INVERTIDO'
);

INSERT INTO VEICULO_DIAGRAMA_EIXOS (
  COD_DIAGRAMA,
  TIPO_EIXO,
  POSICAO,
  QT_PNEUS,
  EIXO_DIRECIONAL)
VALUES (
  13,
  'D',
  1,
  2,
  FALSE
),
  (
    13,
    'T',
    2,
    1,
    TRUE
  );
--######################################################################################################################
--######################################################################################################################

-- Essa alteração da view já foi rodada, aqui apenas para histórico.
-- --######################################################################################################################
-- --######################################################################################################################
-- --######################################### Correção bug no relatório ##################################################
-- --######################################################################################################################
-- --######################################################################################################################
-- --
-- -- Como essa view abaixo já foi alterada na parte dos dispositivos, essa alteração já está nela. Então precisa ser executada em um BD onde a migration
-- -- dos dispositivos móveis já tenha sido executada.
-- --
-- --PL-2220
-- drop view VIEW_EXTRATO_MAPAS_VERSUS_INTERVALOS cascade;
--
-- -- view_extrato_mapas_versus_intervalos
-- CREATE OR REPLACE VIEW VIEW_EXTRATO_MAPAS_VERSUS_INTERVALOS AS
-- SELECT m.data,
--        m.mapa,
--        m.cod_unidade,
--        (F_IF(mot.cpf is null, 0, 1) + F_IF(aj1.cpf is null, 0, 1) + F_IF(aj2.cpf is null, 0, 1))         AS intervalos_previstos,
--        (F_IF(int_mot.data_hora_fim is null or int_mot.data_hora_inicio is null, 0, 1) +
--         F_IF(int_aj1.data_hora_fim is null or int_aj1.data_hora_inicio is null, 0, 1) +
--         F_IF(int_aj2.data_hora_fim is null or int_aj2.data_hora_inicio is null, 0,
--              1))                                                                                         AS intervalos_realizados,
--        mot.cpf                                                                                           AS cpf_motorista,
--        mot.nome                                                                                          AS nome_motorista,
--        COALESCE(to_char(int_mot.data_hora_inicio at time zone tz_unidade(int_mot.cod_unidade), 'HH24:MI'),
--                 '-')                                                                                     AS inicio_intervalo_mot,
--        COALESCE(to_char(int_mot.data_hora_fim at time zone tz_unidade(int_mot.cod_unidade), 'HH24:MI'),
--                 '-')                                                                                     AS fim_intervalo_mot,
--        F_IF(int_mot.device_imei_inicio_reconhecido AND int_mot.device_imei_fim_reconhecido, TRUE, FALSE) AS marcacoes_reconhecidas_mot,
--        coalesce(to_minutes_trunc(int_mot.data_hora_fim - int_mot.data_hora_inicio) :: text,
--                 '-')                                                                                     AS tempo_decorrido_minutos_mot,
--        CASE
--            WHEN (int_mot.data_hora_fim IS NULL)
--                THEN '-'
--            WHEN (tipo_mot.tempo_recomendado_minutos > to_minutes_trunc(int_mot.data_hora_fim - int_mot.data_hora_inicio))
--                THEN 'NÃO'
--            ELSE 'SIM'
--            END                                                                                           AS mot_cumpriu_tempo_minimo,
--        aj1.cpf                                                                                           AS cpf_aj1,
--        COALESCE(aj1.nome, '-')                                                                           AS nome_aj1,
--        COALESCE(to_char(int_aj1.data_hora_inicio at time zone tz_unidade(int_aj1.cod_unidade), 'HH24:MI'),
--                 '-')                                                                                     AS inicio_intervalo_aj1,
--        COALESCE(to_char(int_aj1.data_hora_fim at time zone tz_unidade(int_aj1.cod_unidade), 'HH24:MI'),
--                 '-')                                                                                     AS fim_intervalo_aj1,
--        F_IF(int_aj1.device_imei_inicio_reconhecido AND int_aj1.device_imei_fim_reconhecido, TRUE, FALSE) AS marcacoes_reconhecidas_aj1,
--        coalesce(to_minutes_trunc(int_aj1.data_hora_fim - int_aj1.data_hora_inicio) :: text,
--                 '-')                                                                                     AS tempo_decorrido_minutos_aj1,
--        CASE
--            WHEN (int_aj1.data_hora_fim IS NULL)
--                THEN '-'
--            WHEN (tipo_aj1.tempo_recomendado_minutos > to_minutes_trunc(int_aj1.data_hora_fim - int_aj1.data_hora_inicio))
--                THEN 'NÃO'
--            ELSE 'SIM'
--            END                                                                                           AS aj1_cumpriu_tempo_minimo,
--        aj2.cpf                                                                                           AS cpf_aj2,
--        COALESCE(aj2.nome, '-')                                                                           AS nome_aj2,
--        COALESCE(to_char(int_aj2.data_hora_inicio at time zone tz_unidade(int_aj2.cod_unidade), 'HH24:MI'),
--                 '-')                                                                                     AS inicio_intervalo_aj2,
--        COALESCE(to_char(int_aj2.data_hora_fim at time zone tz_unidade(int_aj2.cod_unidade), 'HH24:MI'),
--                 '-')                                                                                     AS fim_intervalo_aj2,
--        F_IF(int_aj2.device_imei_inicio_reconhecido AND int_aj2.device_imei_fim_reconhecido, TRUE, FALSE) AS marcacoes_reconhecidas_aj2,
--        coalesce(to_minutes_trunc(int_aj2.data_hora_fim - int_aj2.data_hora_inicio) :: text,
--                 '-')                                                                                     AS tempo_decorrido_minutos_aj2,
--        CASE
--            WHEN (int_aj2.data_hora_fim IS NULL)
--                THEN '-'
--            WHEN (tipo_aj2.tempo_recomendado_minutos > to_minutes_trunc(int_aj2.data_hora_fim - int_aj2.data_hora_inicio))
--                THEN 'NÃO'
--            ELSE 'SIM'
--            END                                                                                           AS aj2_cumpriu_tempo_minimo
-- FROM mapa m
--          JOIN unidade_funcao_produtividade ufp
--               ON ufp.cod_unidade = m.cod_unidade
--          JOIN colaborador mot
--               ON mot.cod_unidade = m.cod_unidade
--                   AND mot.cod_funcao = ufp.cod_funcao_motorista
--                   AND mot.matricula_ambev = m.matricmotorista
--          LEFT JOIN colaborador aj1
--                    ON aj1.cod_unidade = m.cod_unidade
--                        AND aj1.cod_funcao = ufp.cod_funcao_ajudante
--                        AND aj1.matricula_ambev = m.matricajud1
--          LEFT JOIN colaborador aj2
--                    ON aj2.cod_unidade = m.cod_unidade
--                        AND aj2.cod_funcao = ufp.cod_funcao_ajudante
--                        AND aj2.matricula_ambev = m.matricajud2
--          LEFT JOIN func_intervalos_agrupados(NULL, NULL, NULL) int_mot
--                    ON int_mot.cpf_colaborador = mot.cpf
--                        AND tz_date(int_mot.data_hora_inicio, tz_unidade(int_mot.cod_unidade)) = m.data
--          LEFT JOIN intervalo_tipo tipo_mot
--                    ON tipo_mot.codigo = int_mot.cod_tipo_intervalo
--          LEFT JOIN func_intervalos_agrupados(NULL, NULL, NULL) int_aj1
--                    ON int_aj1.cpf_colaborador = aj1.cpf
--                        AND tz_date(int_aj1.data_hora_inicio, tz_unidade(int_aj1.cod_unidade)) = m.data
--          LEFT JOIN intervalo_tipo tipo_aj1
--                    ON tipo_aj1.codigo = int_aj1.cod_tipo_intervalo
--          LEFT JOIN func_intervalos_agrupados(NULL, NULL, NULL) int_aj2
--                    ON int_aj2.cpf_colaborador = aj2.cpf
--                        AND tz_date(int_aj2.data_hora_inicio, tz_unidade(int_aj2.cod_unidade)) = m.data
--          LEFT JOIN intervalo_tipo tipo_aj2
--                    ON tipo_aj2.codigo = int_aj2.cod_tipo_intervalo
-- ORDER BY m.mapa DESC;
--
-- -- Cria function que calcula e retorna o percentual, com tratamento para division by zero.
-- CREATE FUNCTION COALESCE_PERCENTAGE(F_DIVIDENDO FLOAT, F_DIVISOR FLOAT)
--   RETURNS TEXT
-- LANGUAGE PLPGSQL
-- AS $$
-- BEGIN
--   RETURN COALESCE(TRUNC(F_DIVIDENDO/NULLIF(F_DIVISOR, 0)) * 100 || '%', '0%');
-- END;
-- $$;
--
-- -- Dropa a antiga func_relatorio_aderencia_intervalo_dias
-- drop function func_relatorio_aderencia_intervalo_dias(f_cod_unidade bigint, f_data_inicial date, f_data_final date);
--
-- -- Cria a nova func_relatorio_aderencia_intervalo_dias com as correções e melhorias listadas na PL-2220
-- create or replace function func_relatorio_aderencia_intervalo_dias(f_cod_unidade bigint, f_data_inicial date, f_data_final date)
--     returns TABLE
--             (
--                 "DATA"                     CHARACTER VARYING,
--                 "QT MAPAS"                 BIGINT,
--                 "QT MOTORISTAS"            BIGINT,
--                 "QT INTERVALOS MOTORISTAS" BIGINT,
--                 "ADERÊNCIA MOTORISTAS"     TEXT,
--                 "QT AJUDANTES"             BIGINT,
--                 "QT INTERVALOS AJUDANTES"  BIGINT,
--                 "ADERÊNCIA AJUDANTES"      TEXT,
--                 "QT INTERVALOS PREVISTOS"  BIGINT,
--                 "QT INTERVALOS REALIZADOS" BIGINT,
--                 "ADERÊNCIA DIA"            TEXT
--             )
--     language sql
-- as
-- $$
-- SELECT to_char(V.DATA, 'DD/MM/YYYY'),
--        COUNT(V.MAPA)                                                                                    as mapas,
--        SUM(f_if(v.cpf_motorista is not null, 1, 0))                                                     as qt_motoristas,
--        SUM(f_if(v.tempo_decorrido_minutos_mot <> '-', 1, 0))                                            as qt_intervalos_mot,
--        COALESCE_PERCENTAGE(SUM(f_if(v.tempo_decorrido_minutos_mot <> '-', 1, 0)) :: FLOAT,
--                            SUM(f_if(v.cpf_motorista is not null, 1, 0)) :: FLOAT)                       as aderencia_motoristas,
--        SUM(f_if(v.cpf_aj1 is not null, 1, 0)) +
--        SUM(f_if(v.cpf_aj2 is not null, 1, 0))                                                           as numero_ajudantes,
--        SUM(f_if(v.tempo_decorrido_minutos_aj1 <> '-', 1, 0)) +
--        SUM(f_if(v.tempo_decorrido_minutos_aj2 <> '-', 1, 0))                                            as qt_intervalos_aj,
--        COALESCE_PERCENTAGE(
--          SUM(f_if(v.tempo_decorrido_minutos_aj1 <> '-', 1, 0)) +
--          SUM(f_if(v.tempo_decorrido_minutos_aj2 <> '-', 1, 0)) :: FLOAT,
--          SUM(f_if(v.cpf_aj1 is not null, 1, 0)) +
--          SUM(f_if(v.cpf_aj2 is not null, 1, 0)) :: FLOAT)                                               as aderencia_ajudantes,
--        SUM(f_if(v.tempo_decorrido_minutos_aj1 <> '-', 1, 0)) +
--        SUM(V.intervalos_previstos)                                                                      as qt_intervalos_previstos,
--        SUM(V.INTERVALOS_realizados)                                                                     as qt_intervalos_realizados,
--        COALESCE_PERCENTAGE(SUM(V.intervalos_realizados) :: FLOAT,
--                            SUM(V.intervalos_previstos) :: FLOAT)                                        as aderencia_dia
-- FROM view_extrato_mapas_versus_intervalos V
--          JOIN unidade u on u.codigo = v.cod_unidade
--          JOIN empresa e on e.codigo = u.cod_empresa
-- WHERE V.cod_unidade = f_cod_unidade
--   AND V.data BETWEEN f_data_inicial AND f_data_final
-- GROUP BY V.DATA
-- ORDER BY V.DATA
-- $$;
-- --######################################################################################################################
-- --######################################################################################################################
-- --######################################################################################################################
-- --######################################################################################################################
-- --######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--############################################# ATUALIZA STATUS PNEU ###################################################
--######################################################################################################################
--######################################################################################################################
-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Primeiro é feito a validação de todos os dados.
-- Logo, a tabela PNEU_DATA que possui a empresa, unidade informadas por parâmetro e que possui o codigo
-- na tabela VEICULO_PNEU, tem a coluna status atualizada para EM_USO.
--
-- Précondições:
-- Empresa e unidade precisam ter vínculo.
-- F_QUANTIDADE_PNEUS que foram vinculados.
--
-- Histórico:
-- 2019-08-28 -> Function criada (Natan - PL-2219).
--
CREATE OR REPLACE FUNCTION FUNC_PNEU_ALTERA_STATUS_PNEU_VINCULADO(F_COD_EMPRESA BIGINT,
                                                                  F_COD_UNIDADE BIGINT,
                                                                  F_QUANTIDADE_PNEUS BIGINT,
                                                                  OUT AVISO_STATUS_ATUALIZADO TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    F_STATUS_PNEU_VINCULADO TEXT := 'EM_USO';
    F_STATUS_ATUAL          TEXT := 'ESTOQUE';
    QTD_LINHAS_ATUALIZADAS  BIGINT;
BEGIN
    --VERIFICA SE EMPRESA EXISTE.
    PERFORM FUNC_GARANTE_EMPRESA_EXISTE(F_COD_EMPRESA);

    --VERIFICA SE UNIDADE EXISTE.
    PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE);

    --VERIFICA SE EMPRESA POSSUI UNIDADE.
    PERFORM FUNC_GARANTE_EMPRESA_POSSUI_UNIDADE(F_COD_EMPRESA, F_COD_UNIDADE);

    --EXECUTA UPDATE NA TABELA.
    UPDATE PNEU_DATA
    SET STATUS = F_STATUS_PNEU_VINCULADO
    WHERE COD_EMPRESA = F_COD_EMPRESA
      AND COD_UNIDADE = F_COD_UNIDADE
      AND STATUS = F_STATUS_ATUAL
      AND CODIGO IN (SELECT COD_PNEU FROM VEICULO_PNEU);

    GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

    --VERIFICA SE A QUANTIDADE DE LINHAS ATUALIZADAS É IGUAL A DE PNEUS A SEREM ATUALIZADOS.
    IF (QTD_LINHAS_ATUALIZADAS != F_QUANTIDADE_PNEUS)
    THEN
        RAISE EXCEPTION 'ERRO AO ATUALIZAR! A QUANTIDADE DE PNEUS: % NÃO É A MESMA DE LINHAS AFETADAS: % '
            'NA ATUALIZAÇÃO!', F_QUANTIDADE_PNEUS, QTD_LINHAS_ATUALIZADAS;
    ELSE
        --MENSAGEM DE SUCESSO.
        SELECT 'ATUALIZADO COM SUCESSO! NÚMERO TOTAL DE: ' || F_QUANTIDADE_PNEUS || ' PNEUS.'
        INTO AVISO_STATUS_ATUALIZADO;
    END IF;
END
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--########################### CRIA TABELA EMPRESA_BLOQUEADA_FECHAMENTO_OS_TRANSFERENCIA ################################
--######################################################################################################################
--######################################################################################################################
-- PL-2267
CREATE OR REPLACE FUNCTION FUNC_COLABORADOR_VERIFICA_PERMISSOES(F_PERMISSOES_COLABORADOR INTEGER[],
                                                                F_PERMISSSOES_NECESSARIAS INTEGER[],
                                                                F_PRECISA_TER_TODAS_AS_PERMISSOES BOOLEAN)
    RETURNS TABLE
            (
                TOKEN_VALIDO      BOOLEAN,
                POSSUI_PERMISSSAO BOOLEAN
            )
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    -- Se permissões colaborador for null, então o token não existe.
    IF F_PERMISSOES_COLABORADOR IS NULL
    THEN
        RETURN QUERY
            SELECT FALSE AS TOKEN_VALIDO,
                   FALSE AS POSSUI_PERMISSAO;
        -- Sem esse RETURN para barrar a execução a query pode acabar retornando duas linhas.
        RETURN;
    END IF;

    -- PERMISSOES_COLABORADOR contains F_PERMISSSOES_NECESSARIAS
    IF (F_PRECISA_TER_TODAS_AS_PERMISSOES AND F_PERMISSOES_COLABORADOR @> F_PERMISSSOES_NECESSARIAS)
        OR
        -- PERMISSOES_COLABORADOR overlap (have elements in common) F_PERMISSSOES_NECESSARIAS
       (NOT F_PRECISA_TER_TODAS_AS_PERMISSOES AND F_PERMISSOES_COLABORADOR && F_PERMISSSOES_NECESSARIAS)
    THEN
        RETURN QUERY
            SELECT TRUE AS TOKEN_VALIDO,
                   TRUE AS POSSUI_PERMISSAO;
    ELSE
        RETURN QUERY
            SELECT TRUE  AS TOKEN_VALIDO,
                   FALSE AS POSSUI_PERMISSAO;
    END IF;
END;
$$;


CREATE OR REPLACE FUNCTION FUNC_COLABORADOR_VERIFICA_PERMISSOES_TOKEN(F_TOKEN TEXT,
                                                                      F_PERMISSSOES_NECESSARIAS INTEGER[],
                                                                      F_PRECISA_TER_TODAS_AS_PERMISSOES BOOLEAN,
                                                                      F_APENAS_USUARIOS_ATIVOS BOOLEAN)
    RETURNS TABLE
            (
                TOKEN_VALIDO      BOOLEAN,
                POSSUI_PERMISSSAO BOOLEAN
            )
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    RETURN QUERY
        SELECT F.TOKEN_VALIDO      AS TOKEN_VALIDO,
               F.POSSUI_PERMISSSAO AS POSSUI_PERMISSAO
        FROM FUNC_COLABORADOR_VERIFICA_PERMISSOES(
                     (SELECT ARRAY_AGG(CFP.COD_FUNCAO_PROLOG)
                      FROM TOKEN_AUTENTICACAO TA
                               JOIN COLABORADOR C ON C.CPF = TA.CPF_COLABORADOR
                               -- Usando um LEFT JOIN aqui, caso o token não exista nada será retornado, porém, se o
                               -- token existir mas o usuário não tiver nenhuma permissão, será retornando um array
                               -- contendo null.
                               LEFT JOIN CARGO_FUNCAO_PROLOG_V11 CFP
                                         ON CFP.COD_UNIDADE = C.COD_UNIDADE
                                             AND CFP.COD_FUNCAO_COLABORADOR = C.COD_FUNCAO
                      WHERE TA.TOKEN = F_TOKEN
                        AND F_IF(F_APENAS_USUARIOS_ATIVOS, C.STATUS_ATIVO = TRUE, TRUE)) :: INTEGER[],
                     F_PERMISSSOES_NECESSARIAS,
                     F_PRECISA_TER_TODAS_AS_PERMISSOES) F;
END;
$$;


CREATE OR REPLACE FUNCTION FUNC_COLABORADOR_VERIFICA_PERMISSOES_CPF_DATA_NASCIMENTO(F_CPF BIGINT,
                                                                                    F_DATA_NASCIMENTO DATE,
                                                                                    F_PERMISSSOES_NECESSARIAS INTEGER[],
                                                                                    F_PRECISA_TER_TODAS_AS_PERMISSOES BOOLEAN,
                                                                                    F_APENAS_USUARIOS_ATIVOS BOOLEAN)
    RETURNS TABLE
            (
                TOKEN_VALIDO      BOOLEAN,
                POSSUI_PERMISSSAO BOOLEAN
            )
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    RETURN QUERY
        SELECT F.TOKEN_VALIDO      AS TOKEN_VALIDO,
               F.POSSUI_PERMISSSAO AS POSSUI_PERMISSAO
        FROM FUNC_COLABORADOR_VERIFICA_PERMISSOES(
                     (SELECT ARRAY_AGG(CFP.COD_FUNCAO_PROLOG) AS COD_PERMISSAO
                      FROM COLABORADOR C
                               -- Usando um LEFT JOIN aqui, caso o token não exista nada será retornado, porém, se o
                               -- token existir mas o usuário não tiver nenhuma permissão, será retornando um array
                               -- contendo null.
                               LEFT JOIN CARGO_FUNCAO_PROLOG_V11 CFP
                                         ON CFP.COD_UNIDADE = C.COD_UNIDADE
                                             AND CFP.COD_FUNCAO_COLABORADOR = C.COD_FUNCAO
                      WHERE C.CPF = F_CPF
                        AND C.DATA_NASCIMENTO = F_DATA_NASCIMENTO
                        AND F_IF(F_APENAS_USUARIOS_ATIVOS, C.STATUS_ATIVO = TRUE, TRUE)) :: INTEGER[],
                     F_PERMISSSOES_NECESSARIAS,
                     F_PRECISA_TER_TODAS_AS_PERMISSOES) F;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--###########################    ADICIONA HISTÓRICO DE CRIAÇÃO DE MARCAÇÃO DE INÍCIO    ################################
--######################################################################################################################
--######################################################################################################################
-- PL-2280
WITH HISTORICO AS (
    SELECT
      MVIF.COD_MARCACAO_INICIO,
      MH.COD_AJUSTE,
      (SELECT I.DATA_HORA FROM INTERVALO I WHERE I.CODIGO = MVIF.COD_MARCACAO_INICIO) AS DATA
    FROM MARCACAO_HISTORICO MH
      JOIN MARCACAO_AJUSTE MA ON MH.COD_AJUSTE = MA.CODIGO
      JOIN MARCACAO_VINCULO_INICIO_FIM MVIF ON MVIF.COD_MARCACAO_FIM = MH.COD_MARCACAO
    WHERE MA.ACAO_AJUSTE = 'ADICAO_INICIO_FIM'
)

INSERT INTO MARCACAO_HISTORICO (COD_MARCACAO, COD_AJUSTE, DATA_HORA_ANTIGA)
  SELECT
    H.COD_MARCACAO_INICIO,
    H.COD_AJUSTE,
    H.DATA
  FROM HISTORICO H;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--###########################    ADICIONA CAMPO DE MENOR SULCO NOS RELATÓRIOS DE PNEUS    ##############################
--######################################################################################################################
--######################################################################################################################
-- PL-2169
-- Dropa a FUNC_RELATORIO_DADOS_ULTIMA_AFERICAO_PNEU
DROP FUNCTION FUNC_RELATORIO_DADOS_ULTIMA_AFERICAO_PNEU(TEXT[]);

-- Recria a FUNC_RELATORIO_DADOS_ULTIMA_AFERICAO_PNEU
CREATE OR REPLACE FUNCTION FUNC_RELATORIO_DADOS_ULTIMA_AFERICAO_PNEU(F_COD_UNIDADES TEXT [])
  RETURNS TABLE(
    "UNIDADE ALOCADO"               TEXT,
    "PNEU"                          TEXT,
    "STATUS ATUAL"                  TEXT,
    "MARCA PNEU"                    TEXT,
    "MODELO PNEU"                   TEXT,
    "MEDIDAS"                       TEXT,
    "PLACA APLICADO"                TEXT,
    "MARCA VEÍCULO"                 TEXT,
    "MODELO VEÍCULO"                TEXT,
    "TIPO VEÍCULO"                  TEXT,
    "POSIÇÃO APLICADO"              TEXT,
    "SULCO INTERNO"                 TEXT,
    "SULCO CENTRAL INTERNO"         TEXT,
    "SULCO CENTRAL EXTERNO"         TEXT,
    "SULCO EXTERNO"                 TEXT,
    "MENOR SULCO"                   TEXT,
    "PRESSÃO (PSI)"                 TEXT,
    "VIDA ATUAL"                    TEXT,
    "DOT"                           TEXT,
    "ÚLTIMA AFERIÇÃO"               TEXT,
    "TIPO PROCESSO ÚLTIMA AFERIÇÃO" TEXT)
LANGUAGE PLPGSQL
AS $$
BEGIN
  -- Essa CTE busca o código da última aferição de cada pneu.
  -- Com o código nós conseguimos buscar depois qualquer outra informação da aferição.
  RETURN QUERY
  WITH CODS_AFERICOES AS (
      SELECT
        AV.COD_PNEU   AS COD_PNEU_AFERIDO,
        MAX(A.CODIGO) AS COD_AFERICAO
      FROM AFERICAO A
        JOIN AFERICAO_VALORES AV
          ON AV.COD_AFERICAO = A.CODIGO
        JOIN PNEU P ON P.CODIGO = AV.COD_PNEU
      WHERE P.COD_UNIDADE :: TEXT = ANY (F_COD_UNIDADES)
      GROUP BY AV.COD_PNEU
  ),

      ULTIMAS_AFERICOES AS (
        SELECT
          CA.COD_PNEU_AFERIDO    AS COD_PNEU_AFERIDO,
          A.DATA_HORA            AS DATA_HORA_AFERICAO,
          A.COD_UNIDADE          AS COD_UNIDADE_AFERICAO,
          A.TIPO_PROCESSO_COLETA AS TIPO_PROCESSO_COLETA
        FROM CODS_AFERICOES CA
          JOIN AFERICAO A ON A.CODIGO = CA.COD_AFERICAO)

  SELECT U.NOME :: TEXT                                                                               AS UNIDADE_ALOCADO,
         P.CODIGO_CLIENTE :: TEXT                                                                     AS COD_PNEU,
         P.STATUS :: TEXT                                                                             AS STATUS_ATUAL,
         MAP.NOME :: TEXT                                                                             AS NOME_MARCA,
         MP.NOME :: TEXT                                                                              AS NOME_MODELO,
         ((((DP.LARGURA || '/' :: TEXT) || DP.ALTURA) || ' R' :: TEXT) ||
          DP.ARO)                                                                                     AS MEDIDAS,
         COALESCE(VP.PLACA, '-') :: TEXT                                                              AS PLACA,
         COALESCE(MARV.NOME, '-') :: TEXT                                                             AS MARCA_VEICULO,
         COALESCE(MODV.NOME, '-') :: TEXT                                                             AS MODELO_VEICULO,
         COALESCE(VT.NOME, '-') :: TEXT                                                               AS TIPO_VEICULO,
         COALESCE(PONU.NOMENCLATURA, '-') :: TEXT                                                     AS POSICAO_PNEU,
         FUNC_PNEU_FORMAT_SULCO(P.ALTURA_SULCO_INTERNO)                                               AS SULCO_INTERNO,
         FUNC_PNEU_FORMAT_SULCO(P.ALTURA_SULCO_CENTRAL_INTERNO)                                       AS SULCO_CENTRAL_INTERNO,
         FUNC_PNEU_FORMAT_SULCO(P.ALTURA_SULCO_CENTRAL_EXTERNO)                                       AS SULCO_CENTRAL_EXTERNO,
         FUNC_PNEU_FORMAT_SULCO(P.ALTURA_SULCO_EXTERNO)                                               AS SULCO_EXTERNO,
         FUNC_PNEU_FORMAT_SULCO(LEAST(P.ALTURA_SULCO_EXTERNO, P.ALTURA_SULCO_CENTRAL_EXTERNO,
                                      P.ALTURA_SULCO_CENTRAL_INTERNO,
                                      P.ALTURA_SULCO_INTERNO))                                        AS MENOR_SULCO,
         REPLACE(COALESCE(TRUNC(P.PRESSAO_ATUAL) :: TEXT, '-'), '.', ',')                             AS PRESSAO_ATUAL,
         P.VIDA_ATUAL :: TEXT                                                                         AS VIDA_ATUAL,
         COALESCE(P.DOT, '-') :: TEXT                                                                 AS DOT,
         COALESCE(TO_CHAR(UA.DATA_HORA_AFERICAO AT TIME ZONE
                          tz_unidade(UA.COD_UNIDADE_AFERICAO),
                          'DD/MM/YYYY HH24:MI'),
                  'Nunca Aferido')                                                                    AS ULTIMA_AFERICAO,
         CASE
             WHEN UA.TIPO_PROCESSO_COLETA IS NULL
                 THEN 'Nunca Aferido'
             WHEN UA.TIPO_PROCESSO_COLETA = 'PLACA'
                 THEN 'Aferido em uma placa'
             ELSE 'Aferido Avulso (em estoque)' END                                                   AS TIPO_PROCESSO_ULTIMA_AFERICAO
  FROM PNEU P
    JOIN DIMENSAO_PNEU DP ON DP.CODIGO = P.COD_DIMENSAO
    JOIN UNIDADE U ON U.CODIGO = P.COD_UNIDADE
    JOIN MODELO_PNEU MP ON MP.CODIGO = P.COD_MODELO AND MP.COD_EMPRESA = U.COD_EMPRESA
    JOIN MARCA_PNEU MAP ON MAP.CODIGO = MP.COD_MARCA
    LEFT JOIN VEICULO_PNEU VP
      ON P.CODIGO = VP.COD_PNEU
         AND P.COD_UNIDADE = VP.COD_UNIDADE
    LEFT JOIN VEICULO V
      ON VP.PLACA = V.PLACA
         AND VP.COD_UNIDADE = V.COD_UNIDADE
    LEFT JOIN PNEU_ORDEM_NOMENCLATURA_UNIDADE PONU
      ON PONU.COD_UNIDADE = V.COD_UNIDADE
         AND PONU.COD_TIPO_VEICULO = V.COD_TIPO
         AND PONU.POSICAO_PROLOG = VP.POSICAO
    LEFT JOIN VEICULO_TIPO VT
      ON VT.CODIGO = V.COD_TIPO
    LEFT JOIN MODELO_VEICULO MODV
      ON MODV.CODIGO = V.COD_MODELO
    LEFT JOIN MARCA_VEICULO MARV
      ON MARV.CODIGO = MODV.COD_MARCA
    LEFT JOIN ULTIMAS_AFERICOES UA
      ON UA.COD_PNEU_AFERIDO = P.CODIGO
  WHERE P.COD_UNIDADE :: TEXT = ANY (F_COD_UNIDADES)
  ORDER BY U.NOME, P.CODIGO_CLIENTE;
END;
$$;

-- Dropa a FUNC_RELATORIO_PNEUS_DESCARTADOS
DROP FUNCTION FUNC_RELATORIO_PNEUS_DESCARTADOS(TEXT[],DATE,DATE);

-- Recria a FUNC_RELATORIO_PNEUS_DESCARTADOS
CREATE OR REPLACE FUNCTION FUNC_RELATORIO_PNEUS_DESCARTADOS(F_COD_UNIDADE TEXT[], F_DATA_INICIAL DATE, F_DATA_FINAL DATE)
    RETURNS TABLE
            (
                "UNIDADE DO DESCARTE"          TEXT,
                "RESPONSÁVEL PELO DESCARTE"    TEXT,
                "DATA/HORA DO DESCARTE"        TEXT,
                "CÓDIGO DO PNEU"               TEXT,
                "MARCA DO PNEU"                TEXT,
                "MODELO DO PNEU"               TEXT,
                "MARCA DA BANDA"               TEXT,
                "MODELO DA BANDA"              TEXT,
                "DIMENSÃO DO PNEU"             TEXT,
                "ÚLTIMA PRESSÃO"               TEXT,
                "TOTAL DE VIDAS"               TEXT,
                "ALTURA SULCO INTERNO"         TEXT,
                "ALTURA SULCO CENTRAL INTERNO" TEXT,
                "ALTURA SULCO CENTRAL EXTERNO" TEXT,
                "ALTURA SULCO EXTERNO"         TEXT,
                "MENOR SULCO"                  TEXT,
                "DOT"                          TEXT,
                "MOTIVO DO DESCARTE"           TEXT,
                "FOTO 1"                       TEXT,
                "FOTO 2"                       TEXT,
                "FOTO 3"                       TEXT
            )
    LANGUAGE SQL
AS
$$
SELECT U.NOME                                                                                       AS UNIDADE_DO_DESCARTE,
       C.NOME                                                                                       AS RESPONSAVEL_PELO_DESCARTE,
       TO_CHAR(MP.DATA_HORA AT TIME ZONE tz_unidade(P.COD_UNIDADE),
               'DD/MM/YYYY HH24:MI')                                                                AS DATA_HORA_DESCARTE,
       P.CODIGO_CLIENTE                                                                             AS CODIGO_PNEU,
       MAP.NOME                                                                                     AS MARCA_PNEU,
       MOP.NOME                                                                                     AS MODELO_PNEU,
       MAB.NOME                                                                                     AS MARCA_BANDA,
       MOB.NOME                                                                                     AS MODELO_BANDA,
       'Altura: ' || DP.ALTURA || ' - Largura: ' || DP.LARGURA || ' - Aro: ' || DP.ARO              AS DIMENSAO_PNEU,
       REPLACE(COALESCE(TRUNC(P.PRESSAO_ATUAL) :: TEXT, '-'), '.', ',')                             AS ULTIMA_PRESSAO,
       P.VIDA_ATUAL :: TEXT                                                                         AS TOTAL_VIDAS,
       FUNC_PNEU_FORMAT_SULCO(P.ALTURA_SULCO_INTERNO)                                               AS SULCO_INTERNO,
       FUNC_PNEU_FORMAT_SULCO(P.ALTURA_SULCO_CENTRAL_INTERNO)                                       AS SULCO_CENTRAL_INTERNO,
       FUNC_PNEU_FORMAT_SULCO(P.ALTURA_SULCO_CENTRAL_EXTERNO)                                       AS SULCO_CENTRAL_EXTERNO,
       FUNC_PNEU_FORMAT_SULCO(P.ALTURA_SULCO_EXTERNO)                                               AS SULCO_EXTERNO,
       FUNC_PNEU_FORMAT_SULCO(LEAST(P.ALTURA_SULCO_EXTERNO, P.ALTURA_SULCO_CENTRAL_EXTERNO,
                                      P.ALTURA_SULCO_CENTRAL_INTERNO,
                                      P.ALTURA_SULCO_INTERNO))                                      AS MENOR_SULCO,
       P.DOT                                                                                        AS DOT,
       MMDE.MOTIVO                                                                                  AS MOTIVO_DESCARTE,
       MD.URL_IMAGEM_DESCARTE_1                                                                     AS FOTO_1,
       MD.URL_IMAGEM_DESCARTE_2                                                                     AS FOTO_2,
       MD.URL_IMAGEM_DESCARTE_3                                                                     AS FOTO_3
FROM PNEU P
         JOIN MODELO_PNEU MOP ON P.COD_MODELO = MOP.CODIGO
         JOIN MARCA_PNEU MAP ON MOP.COD_MARCA = MAP.CODIGO
         JOIN DIMENSAO_PNEU DP ON P.COD_DIMENSAO = DP.CODIGO
         JOIN UNIDADE U ON P.COD_UNIDADE = U.CODIGO
         LEFT JOIN MODELO_BANDA MOB ON P.COD_MODELO_BANDA = MOB.CODIGO
         LEFT JOIN MARCA_BANDA MAB ON MOB.COD_MARCA = MAB.CODIGO
         LEFT JOIN MOVIMENTACAO_PROCESSO MP ON P.COD_UNIDADE = MP.COD_UNIDADE
         LEFT JOIN MOVIMENTACAO M ON MP.CODIGO = M.COD_MOVIMENTACAO_PROCESSO
         LEFT JOIN MOVIMENTACAO_DESTINO MD ON M.CODIGO = MD.COD_MOVIMENTACAO
         LEFT JOIN COLABORADOR C ON MP.CPF_RESPONSAVEL = C.CPF
         LEFT JOIN MOVIMENTACAO_MOTIVO_DESCARTE_EMPRESA MMDE
                   ON MD.COD_MOTIVO_DESCARTE = MMDE.CODIGO AND C.COD_EMPRESA = MMDE.COD_EMPRESA
WHERE P.COD_UNIDADE :: TEXT LIKE ANY (F_COD_UNIDADE)
  AND P.STATUS = 'DESCARTE'
  AND M.COD_PNEU = P.CODIGO
  AND MD.TIPO_DESTINO = 'DESCARTE'
  AND (MP.DATA_HORA AT TIME ZONE tz_unidade(MP.COD_UNIDADE)) :: DATE >= F_DATA_INICIAL
  AND (MP.DATA_HORA AT TIME ZONE tz_unidade(MP.COD_UNIDADE)) :: DATE <= F_DATA_FINAL
ORDER BY U.NOME;
$$;

-- Dropa a FUNC_RELATORIO_PNEU_EXTRATO_SERVICOS_ABERTOS
DROP FUNCTION FUNC_RELATORIO_PNEU_EXTRATO_SERVICOS_ABERTOS(TEXT[],DATE,DATE,DATE);

-- Recria a FUNC_RELATORIO_PNEU_EXTRATO_SERVICOS_ABERTOS
CREATE OR REPLACE FUNCTION FUNC_RELATORIO_PNEU_EXTRATO_SERVICOS_ABERTOS(F_COD_UNIDADE TEXT[],
                                                                        F_DATA_INICIAL DATE,
                                                                        F_DATA_FINAL DATE,
                                                                        F_DATA_ATUAL DATE)
    RETURNS TABLE
            (
                "UNIDADE DO SERVIÇO"            TEXT,
                "CÓDIGO DO SERVIÇO"             TEXT,
                "TIPO DO SERVIÇO"               TEXT,
                "QTD APONTAMENTOS"              TEXT,
                "DATA HORA ABERTURA"            TEXT,
                "QTD DIAS EM ABERTO"            TEXT,
                "NOME DO COLABORADOR"           TEXT,
                "PLACA"                         TEXT,
                "PNEU"                          TEXT,
                "POSIÇÃO PNEU ABERTURA SERVIÇO" TEXT,
                "MEDIDAS"                       TEXT,
                "COD AFERIÇÃO"                  TEXT,
                "SULCO INTERNO"                 TEXT,
                "SULCO CENTRAL INTERNO"         TEXT,
                "SULCO CENTRAL EXTERNO"         TEXT,
                "SULCO EXTERNO"                 TEXT,
                "MENOR SULCO"                   TEXT,
                "PRESSÃO (PSI)"                 TEXT,
                "PRESSÃO RECOMENDADA (PSI)"     TEXT,
                "ESTADO ATUAL"                  TEXT,
                "MÁXIMO DE RECAPAGENS"          TEXT
            )
    LANGUAGE SQL
AS
$$
SELECT U.NOME                                                                                        AS UNIDADE_SERVICO,
       AM.CODIGO :: TEXT                                                                             AS CODIGO_SERVICO,
       AM.TIPO_SERVICO                                                                               AS TIPO_SERVICO,
       AM.QT_APONTAMENTOS :: TEXT                                                                    AS QT_APONTAMENTOS,
       TO_CHAR((A.DATA_HORA AT TIME ZONE tz_unidade(AM.COD_UNIDADE)),
               'DD/MM/YYYY HH24:MI') :: TEXT                                                         AS DATA_HORA_ABERTURA,
       (F_DATA_ATUAL - ((A.DATA_HORA AT TIME ZONE TZ_UNIDADE(AM.COD_UNIDADE)) :: DATE)) :: TEXT      AS DIAS_EM_ABERTO,
       C.NOME                                                                                        AS NOME_COLABORADOR,
       A.PLACA_VEICULO                                                                               AS PLACA_VEICULO,
       P.CODIGO_CLIENTE                                                                              AS COD_PNEU_PROBLEMA,
       COALESCE(PONU.NOMENCLATURA, '-')                                                              AS POSICAO_PNEU_PROBLEMA,
       DP.LARGURA || '/' :: TEXT || DP.ALTURA || ' R' :: TEXT || DP.ARO                              AS MEDIDAS,
       A.CODIGO :: TEXT                                                                              AS COD_AFERICAO,
       FUNC_PNEU_FORMAT_SULCO(AV.ALTURA_SULCO_INTERNO)                                               AS SULCO_INTERNO,
       FUNC_PNEU_FORMAT_SULCO(AV.ALTURA_SULCO_CENTRAL_INTERNO)                                       AS SULCO_CENTRAL_INTERNO,
       FUNC_PNEU_FORMAT_SULCO(AV.ALTURA_SULCO_CENTRAL_EXTERNO)                                       AS SULCO_CENTRAL_EXTERNO,
       FUNC_PNEU_FORMAT_SULCO(AV.ALTURA_SULCO_EXTERNO)                                               AS SULCO_EXTERNO,
       FUNC_PNEU_FORMAT_SULCO(LEAST(AV.ALTURA_SULCO_EXTERNO, AV.ALTURA_SULCO_CENTRAL_EXTERNO,
                                      AV.ALTURA_SULCO_CENTRAL_INTERNO,
                                      AV.ALTURA_SULCO_INTERNO))                                      AS MENOR_SULCO,
       REPLACE(COALESCE(TRUNC(AV.PSI) :: TEXT, '-'), '.', ',')                                       AS PRESSAO_PNEU_PROBLEMA,
       REPLACE(COALESCE(TRUNC(P.PRESSAO_RECOMENDADA) :: TEXT, '-'), '.',
               ',')                                                                                  AS PRESSAO_RECOMENDADA,
       PVN.NOME                                                                                      AS VIDA_PNEU_PROBLEMA,
       PRN.NOME                                                                                      AS TOTAL_RECAPAGENS
FROM AFERICAO_MANUTENCAO AM
         JOIN PNEU P
              ON AM.COD_PNEU = P.CODIGO
         JOIN DIMENSAO_PNEU DP
              ON DP.CODIGO = P.COD_DIMENSAO
         JOIN AFERICAO A
              ON A.CODIGO = AM.COD_AFERICAO
         JOIN COLABORADOR C
              ON A.CPF_AFERIDOR = C.CPF
         JOIN AFERICAO_VALORES AV
              ON AV.COD_AFERICAO = AM.COD_AFERICAO
                  AND AV.COD_PNEU = AM.COD_PNEU
         JOIN UNIDADE U
              ON U.CODIGO = AM.COD_UNIDADE
         JOIN PNEU_VIDA_NOMENCLATURA PVN
              ON PVN.COD_VIDA = AV.VIDA_MOMENTO_AFERICAO
         JOIN PNEU_RECAPAGEM_NOMENCLATURA PRN
              ON PRN.COD_TOTAL_VIDA = P.VIDA_TOTAL
         JOIN VEICULO V
              ON A.PLACA_VEICULO = V.PLACA
                  AND V.COD_UNIDADE = A.COD_UNIDADE
         LEFT JOIN PNEU_ORDEM_NOMENCLATURA_UNIDADE PONU
                   ON AM.COD_UNIDADE = PONU.COD_UNIDADE
                       AND AV.POSICAO = PONU.POSICAO_PROLOG
                       AND V.COD_TIPO = PONU.COD_TIPO_VEICULO
WHERE AM.COD_UNIDADE :: TEXT LIKE ANY (F_COD_UNIDADE)
  AND (A.DATA_HORA AT TIME ZONE tz_unidade(AM.COD_UNIDADE)) :: DATE >= F_DATA_INICIAL
  AND (A.DATA_HORA AT TIME ZONE tz_unidade(AM.COD_UNIDADE)) :: DATE <= F_DATA_FINAL
  AND AM.DATA_HORA_RESOLUCAO IS NULL
  AND (AM.FECHADO_AUTOMATICAMENTE_MOVIMENTACAO = FALSE OR AM.FECHADO_AUTOMATICAMENTE_MOVIMENTACAO IS NULL)
ORDER BY U.NOME, A.DATA_HORA;
$$;

-- Dropa a FUNC_RELATORIO_PNEU_RESUMO_GERAL_PNEUS
DROP FUNCTION FUNC_RELATORIO_PNEU_RESUMO_GERAL_PNEUS(TEXT[],TEXT);

-- Recria a FUNC_RELATORIO_PNEU_RESUMO_GERAL_PNEUS
CREATE OR REPLACE FUNCTION FUNC_RELATORIO_PNEU_RESUMO_GERAL_PNEUS(
  F_COD_UNIDADE TEXT [],
  F_STATUS_PNEU TEXT)
  RETURNS TABLE(
    "UNIDADE ALOCADO"       TEXT,
    "PNEU"                  TEXT,
    "STATUS"                TEXT,
    "VALOR DE AQUISIÇÃO"    TEXT,
    "DATA/HORA CADASTRO"    TEXT,
    "MARCA"                 TEXT,
    "MODELO"                TEXT,
    "BANDA APLICADA"        TEXT,
    "VALOR DA BANDA"        TEXT,
    "MEDIDAS"               TEXT,
    "PLACA"                 TEXT,
    "TIPO"                  TEXT,
    "POSIÇÃO"               TEXT,
    "QUANTIDADE DE SULCOS"  TEXT,
    "SULCO INTERNO"         TEXT,
    "SULCO CENTRAL INTERNO" TEXT,
    "SULCO CENTRAL EXTERNO" TEXT,
    "SULCO EXTERNO"         TEXT,
    "MENOR SULCO"           TEXT,
    "PRESSÃO ATUAL (PSI)"   TEXT,
    "PRESSÃO IDEAL (PSI)"   TEXT,
    "VIDA ATUAL"            TEXT,
    "DOT"                   TEXT,
    "ÚLTIMA AFERIÇÃO"       TEXT)
LANGUAGE SQL
AS $$
SELECT U.NOME                                                                                        AS UNIDADE_ALOCADO,
       P.CODIGO_CLIENTE                                                                              AS COD_PNEU,
       P.STATUS                                                                                      AS STATUS,
       COALESCE(TRUNC(P.VALOR :: NUMERIC, 2) :: TEXT, '-')                                           AS VALOR_AQUISICAO,
       COALESCE(TO_CHAR(P.DATA_HORA_CADASTRO AT TIME ZONE TZ_UNIDADE(P.COD_UNIDADE_CADASTRO),
                        'DD/MM/YYYY HH24:MI'),
                '-')                                                                                 AS DATA_HORA_CADASTRO,
       MAP.NOME                                                                                      AS NOME_MARCA_PNEU,
       MP.NOME                                                                                       AS NOME_MODELO_PNEU,
       CASE
           WHEN MARB.CODIGO IS NULL
               THEN 'Nunca Recapado'
           ELSE MARB.NOME || ' - ' || MODB.NOME
           END                                                                                       AS BANDA_APLICADA,
       COALESCE(TRUNC(PVV.VALOR :: NUMERIC, 2) :: TEXT, '-')                                         AS VALOR_BANDA,
       ((((DP.LARGURA || '/' :: TEXT) || DP.ALTURA) || ' R' :: TEXT) || DP.ARO)                      AS MEDIDAS,
       COALESCE(POSICAO_PNEU_VEICULO.PLACA_VEICULO_PNEU, '-')                                        AS PLACA,
       COALESCE(POSICAO_PNEU_VEICULO.VEICULO_TIPO, '-')                                              AS TIPO_VEICULO,
       COALESCE(POSICAO_PNEU_VEICULO.POSICAO_PNEU, '-')                                              AS POSICAO_PNEU,
       COALESCE(MODB.QT_SULCOS, MP.QT_SULCOS) :: TEXT                                                AS QTD_SULCOS,
       FUNC_PNEU_FORMAT_SULCO(P.ALTURA_SULCO_INTERNO)                                                AS SULCO_INTERNO,
       FUNC_PNEU_FORMAT_SULCO(P.ALTURA_SULCO_CENTRAL_INTERNO)                                        AS SULCO_CENTRAL_INTERNO,
       FUNC_PNEU_FORMAT_SULCO(P.ALTURA_SULCO_CENTRAL_EXTERNO)                                        AS SULCO_CENTRAL_EXTERNO,
       FUNC_PNEU_FORMAT_SULCO(P.ALTURA_SULCO_EXTERNO)                                                AS SULCO_EXTERNO,
       FUNC_PNEU_FORMAT_SULCO(LEAST(P.ALTURA_SULCO_EXTERNO, P.ALTURA_SULCO_CENTRAL_EXTERNO,
                                      P.ALTURA_SULCO_CENTRAL_INTERNO,
                                      P.ALTURA_SULCO_INTERNO))                                       AS MENOR_SULCO,
       COALESCE(TRUNC(P.PRESSAO_ATUAL) :: TEXT, '-')                                                 AS PRESSAO_ATUAL,
       P.PRESSAO_RECOMENDADA :: TEXT                                                                 AS PRESSAO_RECOMENDADA,
       PVN.NOME :: TEXT                                                                              AS VIDA_ATUAL,
       COALESCE(P.DOT, '-')                                                                          AS DOT,
       COALESCE(
               TO_CHAR(F.DATA_HORA_ULTIMA_AFERICAO AT TIME ZONE TZ_UNIDADE(F.COD_UNIDADE_ULTIMA_AFERICAO),
                       'DD/MM/YYYY HH24:MI'), 'Nunca Aferido')                                       AS ULTIMA_AFERICAO
FROM PNEU P
  JOIN DIMENSAO_PNEU DP ON DP.CODIGO = P.COD_DIMENSAO
  JOIN UNIDADE U ON U.CODIGO = P.COD_UNIDADE
  JOIN MODELO_PNEU MP ON MP.CODIGO = P.COD_MODELO AND MP.COD_EMPRESA = U.COD_EMPRESA
  JOIN MARCA_PNEU MAP ON MAP.CODIGO = MP.COD_MARCA
  JOIN PNEU_VIDA_NOMENCLATURA PVN ON PVN.COD_VIDA = P.VIDA_ATUAL
  LEFT JOIN MODELO_BANDA MODB ON MODB.CODIGO = P.COD_MODELO_BANDA
  LEFT JOIN MARCA_BANDA MARB ON MARB.CODIGO = MODB.COD_MARCA
  LEFT JOIN PNEU_VALOR_VIDA PVV ON P.CODIGO = PVV.COD_PNEU AND PVV.VIDA = P.VIDA_ATUAL
  LEFT JOIN (SELECT
               PON.NOMENCLATURA AS POSICAO_PNEU,
               VP.COD_PNEU      AS CODIGO_PNEU,
               VP.PLACA         AS PLACA_VEICULO_PNEU,
               VP.COD_UNIDADE   AS COD_UNIDADE_PNEU,
               VT.NOME          AS VEICULO_TIPO
             FROM VEICULO V
               JOIN VEICULO_PNEU VP
                 ON VP.PLACA = V.PLACA AND VP.COD_UNIDADE = V.COD_UNIDADE
               JOIN VEICULO_TIPO VT
                 ON V.COD_TIPO = VT.CODIGO
               -- LEFT JOIN porque unidade pode não ter nomenclatura.
               LEFT JOIN PNEU_ORDEM_NOMENCLATURA_UNIDADE PON
                 ON PON.COD_UNIDADE = V.COD_UNIDADE AND PON.COD_TIPO_VEICULO = V.COD_TIPO
                    AND VP.POSICAO = PON.POSICAO_PROLOG
             WHERE V.COD_UNIDADE :: TEXT LIKE ANY (f_cod_unidade)
             ORDER BY VP.COD_PNEU) AS POSICAO_PNEU_VEICULO
    ON P.CODIGO = POSICAO_PNEU_VEICULO.CODIGO_PNEU
  LEFT JOIN FUNC_PNEU_GET_PRIMEIRA_ULTIMA_AFERICAO(P.CODIGO) F
    ON F.COD_PNEU = P.CODIGO
WHERE P.COD_UNIDADE :: TEXT LIKE ANY (f_cod_unidade)
      AND CASE
          WHEN F_STATUS_PNEU IS NULL
            THEN TRUE
          ELSE P.STATUS = F_STATUS_PNEU
          END
ORDER BY U.NOME, P.CODIGO_CLIENTE;
$$;

-- Dropa a FUNC_RELATORIO_PNEU_AFERICOES_AVULSAS
DROP FUNCTION FUNC_RELATORIO_PNEU_AFERICOES_AVULSAS(BIGINT[],DATE,DATE);

-- Recria a FUNC_RELATORIO_PNEU_AFERICOES_AVULSAS
CREATE OR REPLACE FUNCTION FUNC_RELATORIO_PNEU_AFERICOES_AVULSAS(F_COD_UNIDADES BIGINT[], F_DATA_INICIAL DATE, F_DATA_FINAL DATE)
    RETURNS TABLE
            (
                "DATA/HORA AFERIÇÃO"    TEXT,
                "QUEM AFERIU?"          CHARACTER VARYING,
                "UNIDADE ALOCADO"       CHARACTER VARYING,
                "PNEU"                  CHARACTER VARYING,
                "MARCA"                 CHARACTER VARYING,
                "MODELO"                CHARACTER VARYING,
                "MEDIDAS"               TEXT,
                "SULCO INTERNO"         TEXT,
                "SULCO CENTRAL INTERNO" TEXT,
                "SULCO CENTRAL EXTERNO" TEXT,
                "SULCO EXTERNO"         TEXT,
                "MENOR SULCO"           TEXT,
                "VIDA"                  TEXT,
                "DOT"                   CHARACTER VARYING
            )
    LANGUAGE plpgsql
AS
$$
DECLARE
    DATE_FORMAT                   TEXT := 'DD/MM/YYYY HH24:MI';
    PNEU_NUNCA_AFERIDO            TEXT := 'Nunca Aferido';
    PROCESSO_AFERICAO_PNEU_AVULSO TEXT := 'PNEU_AVULSO';
BEGIN
    RETURN QUERY
        SELECT COALESCE(TO_CHAR(A.DATA_HORA AT TIME ZONE TZ_UNIDADE(A.COD_UNIDADE), DATE_FORMAT),
                        PNEU_NUNCA_AFERIDO)                                                               AS ULTIMA_AFERICAO,
               C.NOME,
               U.NOME                                                                                     AS UNIDADE_ALOCADO,
               P.CODIGO_CLIENTE                                                                           AS COD_PNEU,
               MAP.NOME                                                                                   AS NOME_MARCA,
               MP.NOME                                                                                    AS NOME_MODELO,
               ((((DP.LARGURA || '/'::TEXT) || DP.ALTURA) || ' R'::TEXT) || DP.ARO)                       AS MEDIDAS,
               FUNC_PNEU_FORMAT_SULCO(AV.ALTURA_SULCO_INTERNO)                                            AS SULCO_INTERNO,
               FUNC_PNEU_FORMAT_SULCO(AV.ALTURA_SULCO_CENTRAL_INTERNO)                                    AS SULCO_CENTRAL_INTERNO,
               FUNC_PNEU_FORMAT_SULCO(AV.ALTURA_SULCO_CENTRAL_EXTERNO)                                    AS SULCO_CENTRAL_EXTERNO,
               FUNC_PNEU_FORMAT_SULCO(AV.ALTURA_SULCO_EXTERNO)                                            AS SULCO_EXTERNO,
               FUNC_PNEU_FORMAT_SULCO(LEAST(AV.ALTURA_SULCO_EXTERNO, AV.ALTURA_SULCO_CENTRAL_EXTERNO,
                                      AV.ALTURA_SULCO_CENTRAL_INTERNO,
                                      AV.ALTURA_SULCO_INTERNO))                                           AS MENOR_SULCO,
               P.VIDA_ATUAL::TEXT                                                                         AS VIDA_ATUAL,
               COALESCE(P.DOT, '-')                                                                       AS DOT
        FROM PNEU P
                 JOIN DIMENSAO_PNEU DP ON DP.CODIGO = P.COD_DIMENSAO
                 JOIN UNIDADE U ON U.CODIGO = P.COD_UNIDADE
                 JOIN MODELO_PNEU MP ON MP.CODIGO = P.COD_MODELO AND MP.COD_EMPRESA = U.COD_EMPRESA
                 JOIN MARCA_PNEU MAP ON MAP.CODIGO = MP.COD_MARCA
                 JOIN AFERICAO_VALORES AV ON AV.COD_PNEU = P.CODIGO
                 JOIN AFERICAO A ON A.CODIGO = AV.COD_AFERICAO
                 JOIN COLABORADOR C ON A.CPF_AFERIDOR = C.CPF
        WHERE P.COD_UNIDADE = ANY (F_COD_UNIDADES)
          AND A.TIPO_PROCESSO_COLETA = PROCESSO_AFERICAO_PNEU_AVULSO
          AND (A.DATA_HORA AT TIME ZONE TZ_UNIDADE(A.COD_UNIDADE))::DATE BETWEEN F_DATA_INICIAL AND F_DATA_FINAL
        ORDER BY U.NOME ASC, ULTIMA_AFERICAO DESC NULLS LAST;
END;
$$;

-- Dropa a FUNC_MOVIMENTACAO_RELATORIO_DADOS_GERAIS
DROP FUNCTION FUNC_MOVIMENTACAO_RELATORIO_DADOS_GERAIS(BIGINT[],DATE,DATE);

-- Recria a FUNC_MOVIMENTACAO_RELATORIO_DADOS_GERAIS
CREATE OR REPLACE FUNCTION FUNC_MOVIMENTACAO_RELATORIO_DADOS_GERAIS(
  F_COD_UNIDADES BIGINT [],
  F_DATA_INICIAL DATE,
  F_DATA_FINAL   DATE)
  RETURNS TABLE(
    "UNIDADE"               TEXT,
    "DATA E HORA"           TEXT,
    "CPF DO RESPONSÁVEL"    TEXT,
    "NOME"                  TEXT,
    "PNEU"                  TEXT,
    "MARCA"                 TEXT,
    "MODELO"                TEXT,
    "BANDA APLICADA"        TEXT,
    "MEDIDAS"               TEXT,
    "SULCO INTERNO"         TEXT,
    "SULCO CENTRAL INTERNO" TEXT,
    "SULCO CENTRAL EXTERNO" TEXT,
    "SULCO EXTERNO"         TEXT,
    "MENOR SULCO"           TEXT,
    "PRESSÃO ATUAL (PSI)"   TEXT,
    "VIDA ATUAL"            TEXT,
    "ORIGEM"                TEXT,
    "PLACA DE ORIGEM"       TEXT,
    "POSIÇÃO DE ORIGEM"     TEXT,
    "DESTINO"               TEXT,
    "PLACA DE DESTINO"      TEXT,
    "POSIÇÃO DE DESTINO"    TEXT,
    "RECAPADORA DESTINO"    TEXT,
    "CÓDIGO COLETA"         TEXT,
    "OBS. MOVIMENTAÇÃO"     TEXT,
    "OBS. GERAL"            TEXT)
LANGUAGE SQL
AS $$
SELECT
  U.NOME,
  TO_CHAR((MOVP.DATA_HORA AT TIME ZONE TZ_UNIDADE(MOVP.COD_UNIDADE)), 'DD/MM/YYYY HH24:MI') :: TEXT AS DATA_HORA,
  LPAD(MOVP.CPF_RESPONSAVEL :: TEXT, 11, '0'),
  C.NOME,
  P.CODIGO_CLIENTE                                                                                  AS PNEU,
  MAP.NOME                                                                                          AS NOME_MARCA_PNEU,
  MP.NOME                                                                                           AS NOME_MODELO_PNEU,
  F_IF(MARB.CODIGO IS NULL, 'Nunca Recapado', MARB.NOME || ' - ' || MODB.NOME)                      AS BANDA_APLICADA,
  ((((DP.LARGURA || '/' :: TEXT) || DP.ALTURA) || ' R' :: TEXT) || DP.ARO)                          AS MEDIDAS,
  FUNC_PNEU_FORMAT_SULCO(P.ALTURA_SULCO_INTERNO)                                                    AS SULCO_INTERNO,
  FUNC_PNEU_FORMAT_SULCO(P.ALTURA_SULCO_CENTRAL_INTERNO)                                            AS SULCO_CENTRAL_INTERNO,
  FUNC_PNEU_FORMAT_SULCO(P.ALTURA_SULCO_CENTRAL_EXTERNO)                                            AS SULCO_CENTRAL_EXTERNO,
  FUNC_PNEU_FORMAT_SULCO(P.ALTURA_SULCO_EXTERNO)                                                    AS SULCO_EXTERNO,
  FUNC_PNEU_FORMAT_SULCO(LEAST(P.ALTURA_SULCO_EXTERNO, P.ALTURA_SULCO_CENTRAL_EXTERNO,
                                      P.ALTURA_SULCO_CENTRAL_INTERNO,
                                      P.ALTURA_SULCO_INTERNO))                                      AS MENOR_SULCO,
  COALESCE(TRUNC(P.PRESSAO_ATUAL) :: TEXT, '-')                                                     AS PRESSAO_ATUAL,
  PVN.NOME :: TEXT                                                                                  AS VIDA_ATUAL,
  O.TIPO_ORIGEM                                                                                     AS ORIGEM,
  COALESCE(O.PLACA, '-')                                                                            AS PLACA_ORIGEM,
  COALESCE(NOMENCLATURA_ORIGEM.NOMENCLATURA, '-')                                                   AS POSICAO_ORIGEM,
  D.TIPO_DESTINO                                                                                    AS DESTINO,
  COALESCE(D.PLACA, '-')                                                                            AS PLACA_DESTINO,
  COALESCE(NOMENCLATURA_DESTINO.NOMENCLATURA, '-')                                                  AS POSICAO_DESTINO,
  COALESCE(R.NOME, '-')                                                                             AS RECAPADORA_DESTINO,
  COALESCE(NULLIF(TRIM(D.COD_COLETA), ''), '-')                                                     AS COD_COLETA_RECAPADORA,
  COALESCE(NULLIF(TRIM(M.OBSERVACAO), ''), '-')                                                     AS OBSERVACAO_MOVIMENTACAO,
  COALESCE(NULLIF(TRIM(MOVP.OBSERVACAO), ''), '-')                                                  AS OBSERVACAO_GERAL
FROM
  MOVIMENTACAO_PROCESSO MOVP
  JOIN MOVIMENTACAO M ON MOVP.CODIGO = M.COD_MOVIMENTACAO_PROCESSO AND MOVP.COD_UNIDADE = M.COD_UNIDADE
  JOIN MOVIMENTACAO_DESTINO D ON M.CODIGO = D.COD_MOVIMENTACAO
  JOIN PNEU P ON P.CODIGO = M.COD_PNEU
  JOIN MOVIMENTACAO_ORIGEM O ON M.CODIGO = O.COD_MOVIMENTACAO
  JOIN UNIDADE U ON U.CODIGO = MOVP.COD_UNIDADE
  JOIN COLABORADOR C ON MOVP.CPF_RESPONSAVEL = C.CPF
  JOIN DIMENSAO_PNEU DP ON DP.CODIGO = P.COD_DIMENSAO
  JOIN MODELO_PNEU MP ON MP.CODIGO = P.COD_MODELO AND MP.COD_EMPRESA = U.COD_EMPRESA
  JOIN MARCA_PNEU MAP ON MAP.CODIGO = MP.COD_MARCA
  JOIN PNEU_VIDA_NOMENCLATURA PVN ON PVN.COD_VIDA = P.VIDA_ATUAL

  -- Terá recapadora apenas se foi movido para análise.
  LEFT JOIN RECAPADORA R ON R.CODIGO = D.COD_RECAPADORA_DESTINO

  -- Pode não possuir banda.
  LEFT JOIN MODELO_BANDA MODB ON MODB.CODIGO = P.COD_MODELO_BANDA
  LEFT JOIN MARCA_BANDA MARB ON MARB.CODIGO = MODB.COD_MARCA

  -- Joins para buscar a nomenclatura da posição do pneu na placa de ORIGEM, que a unidade pode não possuir.
  LEFT JOIN VEICULO VORIGEM
    ON O.PLACA = VORIGEM.PLACA
  LEFT JOIN PNEU_ORDEM_NOMENCLATURA_UNIDADE NOMENCLATURA_ORIGEM
    ON NOMENCLATURA_ORIGEM.COD_UNIDADE = P.COD_UNIDADE
       AND NOMENCLATURA_ORIGEM.COD_TIPO_VEICULO = VORIGEM.COD_TIPO
       AND NOMENCLATURA_ORIGEM.POSICAO_PROLOG = O.POSICAO_PNEU_ORIGEM

  -- Joins para buscar a nomenclatura da posição do pneu na placa de DESTINO, que a unidade pode não possuir.
  LEFT JOIN VEICULO VDESTINO
    ON D.PLACA = VDESTINO.PLACA
  LEFT JOIN PNEU_ORDEM_NOMENCLATURA_UNIDADE NOMENCLATURA_DESTINO
    ON NOMENCLATURA_DESTINO.COD_UNIDADE = P.COD_UNIDADE
       AND NOMENCLATURA_DESTINO.COD_TIPO_VEICULO = VDESTINO.COD_TIPO
       AND NOMENCLATURA_DESTINO.POSICAO_PROLOG = D.POSICAO_PNEU_DESTINO

WHERE MOVP.COD_UNIDADE = ANY (F_COD_UNIDADES)
      AND (MOVP.DATA_HORA AT TIME ZONE TZ_UNIDADE(MOVP.COD_UNIDADE)) :: DATE BETWEEN F_DATA_INICIAL AND F_DATA_FINAL
ORDER BY U.CODIGO, MOVP.DATA_HORA DESC;
$$;

-- Dropa a FUNC_AFERICAO_RELATORIO_DADOS_GERAIS
DROP FUNCTION FUNC_AFERICAO_RELATORIO_DADOS_GERAIS(BIGINT[],DATE,DATE);

-- Recria a FUNC_AFERICAO_RELATORIO_DADOS_GERAIS
CREATE OR REPLACE FUNCTION FUNC_AFERICAO_RELATORIO_DADOS_GERAIS(
  F_COD_UNIDADES BIGINT [],
  F_DATA_INICIAL DATE,
  F_DATA_FINAL   DATE)
  RETURNS TABLE(
    "CÓDIGO AFERIÇÃO"           TEXT,
    "UNIDADE"                   TEXT,
    "DATA E HORA"               TEXT,
    "CPF DO RESPONSÁVEL"        TEXT,
    "NOME COLABORADOR"          TEXT,
    "PNEU"                      TEXT,
    "STATUS ATUAL"              TEXT,
    "VALOR COMPRA"              TEXT,
    "MARCA DO PNEU"             TEXT,
    "MODELO DO PNEU"            TEXT,
    "QTD SULCOS MODELO"         TEXT,
    "VIDA ATUAL"                TEXT,
    "VALOR VIDA ATUAL"          TEXT,
    "BANDA APLICADA"            TEXT,
    "QTD SULCOS BANDA"          TEXT,
    "DIMENSÃO"                  TEXT,
    "DOT"                       TEXT,
    "DATA E HORA CADASTRO"      TEXT,
    "POSIÇÃO PNEU"              TEXT,
    "PLACA"                     TEXT,
    "VIDA MOMENTO AFERIÇÃO"     TEXT,
    "KM NO MOMENTO DA AFERIÇÃO" TEXT,
    "KM ATUAL"                  TEXT,
    "MARCA DO VEÍCULO"          TEXT,
    "MODELO DO VEÍCULO"         TEXT,
    "TIPO DE MEDIÇÃO COLETADA"  TEXT,
    "TIPO DA AFERIÇÃO"          TEXT,
    "TEMPO REALIZAÇÃO (mm:ss)"  TEXT,
    "SULCO INTERNO"             TEXT,
    "SULCO CENTRAL INTERNO"     TEXT,
    "SULCO CENTRAL EXTERNO"     TEXT,
    "SULCO EXTERNO"             TEXT,
    "MENOR SULCO"               TEXT,
    "PRESSÃO"                   TEXT)
LANGUAGE SQL
AS $$
SELECT A.CODIGO :: TEXT                                                                              AS COD_AFERICAO,
       U.NOME                                                                                        AS UNIDADE,
       TO_CHAR((A.DATA_HORA AT TIME ZONE TZ_UNIDADE(A.COD_UNIDADE)),
               'DD/MM/YYYY HH24:MI')                                                                 AS DATA_HORA_AFERICAO,
       LPAD(C.CPF :: TEXT, 11, '0')                                                                  AS CPF_COLABORADOR,
       C.NOME                                                                                        AS NOME_COLABORADOR,
       P.CODIGO_CLIENTE                                                                              AS CODIGO_CLIENTE_PNEU,
       P.STATUS                                                                                      AS STATUS_ATUAL_PNEU,
       ROUND(P.VALOR :: NUMERIC, 2) :: TEXT                                                          AS VALOR_COMPRA,
       MAP.NOME                                                                                      AS MARCA_PNEU,
       MP.NOME                                                                                       AS MODELO_PNEU,
       MP.QT_SULCOS :: TEXT                                                                          AS QTD_SULCOS_MODELO,
       (SELECT PVN.NOME
        FROM PNEU_VIDA_NOMENCLATURA PVN
        WHERE PVN.COD_VIDA = P.VIDA_ATUAL)                                                           AS VIDA_ATUAL,
       COALESCE(ROUND(PVV.VALOR :: NUMERIC, 2) :: TEXT, '-')                                         AS VALOR_VIDA_ATUAL,
       F_IF(MARB.CODIGO IS NOT NULL, MARB.NOME || ' - ' || MODB.NOME, 'Nunca Recapado')              AS BANDA_APLICADA,
       COALESCE(MODB.QT_SULCOS :: TEXT, '-')                                                         AS QTD_SULCOS_BANDA,
       DP.LARGURA || '-' || DP.ALTURA || '/' || DP.ARO                                               AS DIMENSAO,
       P.DOT                                                                                         AS DOT,
       COALESCE(TO_CHAR(P.DATA_HORA_CADASTRO AT TIME ZONE TZ_UNIDADE(P.COD_UNIDADE_CADASTRO),
                        'DD/MM/YYYY HH24:MI'),
                '-')                                                                                 AS DATA_HORA_CADASTRO,
       COALESCE(TRIM(REGEXP_REPLACE(NOMENCLATURA, '[\u0080-\u00ff]|(\s+)', ' ', 'g')), '-')          AS POSICAO,
       COALESCE(A.PLACA_VEICULO, '-')                                                                AS PLACA,
       (SELECT PVN.NOME
        FROM PNEU_VIDA_NOMENCLATURA PVN
        WHERE PVN.COD_VIDA = AV.VIDA_MOMENTO_AFERICAO)                                               AS VIDA_MOMENTO_AFERICAO,
       COALESCE(A.KM_VEICULO :: TEXT, '-')                                                           AS KM_MOMENTO_AFERICAO,
       COALESCE(V.KM :: TEXT, '-')                                                                   AS KM_ATUAL,
       COALESCE(M2.NOME, '-')                                                                        AS MARCA_VEICULO,
       COALESCE(MV.NOME, '-')                                                                        AS MODELO_VEICULO,
       A.TIPO_MEDICAO_COLETADA,
       A.TIPO_PROCESSO_COLETA,
       TO_CHAR((A.TEMPO_REALIZACAO || ' milliseconds') :: INTERVAL, 'MI:SS')                         AS TEMPO_REALIZACAO_MINUTOS,
       FUNC_PNEU_FORMAT_SULCO(AV.ALTURA_SULCO_INTERNO)                                               AS SULCO_INTERNO,
       FUNC_PNEU_FORMAT_SULCO(AV.ALTURA_SULCO_CENTRAL_INTERNO)                                       AS SULCO_CENTRAL_INTERNO,
       FUNC_PNEU_FORMAT_SULCO(AV.ALTURA_SULCO_CENTRAL_EXTERNO)                                       AS SULCO_CENTRAL_EXTERNO,
       FUNC_PNEU_FORMAT_SULCO(AV.ALTURA_SULCO_EXTERNO)                                               AS SULCO_EXTERNO,
       FUNC_PNEU_FORMAT_SULCO(LEAST(AV.ALTURA_SULCO_EXTERNO, AV.ALTURA_SULCO_CENTRAL_EXTERNO,
                                      AV.ALTURA_SULCO_CENTRAL_INTERNO,
                                      AV.ALTURA_SULCO_INTERNO))                                      AS MENOR_SULCO,
       REPLACE(COALESCE(TRUNC(AV.PSI :: NUMERIC, 1) :: TEXT, '-'), '.', ',')                         AS PRESSAO
FROM AFERICAO A
  JOIN AFERICAO_VALORES AV ON A.CODIGO = AV.COD_AFERICAO AND A.COD_UNIDADE = AV.COD_UNIDADE
  JOIN UNIDADE U ON U.CODIGO = A.COD_UNIDADE
  JOIN COLABORADOR C ON C.CPF = A.CPF_AFERIDOR
  JOIN PNEU P ON P.CODIGO = AV.COD_PNEU AND P.COD_UNIDADE = AV.COD_UNIDADE
  JOIN MODELO_PNEU MP ON P.COD_MODELO = MP.CODIGO AND MP.COD_EMPRESA = P.COD_EMPRESA
  JOIN MARCA_PNEU MAP ON MAP.CODIGO = MP.COD_MARCA
  JOIN DIMENSAO_PNEU DP ON P.COD_DIMENSAO = DP.CODIGO
  LEFT JOIN PNEU_VALOR_VIDA PVV ON P.CODIGO = PVV.COD_PNEU AND P.VIDA_ATUAL = PVV.VIDA

  -- Pode não possuir banda.
  LEFT JOIN MODELO_BANDA MODB ON MODB.CODIGO = P.COD_MODELO_BANDA
  LEFT JOIN MARCA_BANDA MARB ON MARB.CODIGO = MODB.COD_MARCA

  -- Se foi aferição de pneu avulso, pode não possuir placa.
  LEFT JOIN VEICULO V ON V.PLACA = A.PLACA_VEICULO
  LEFT JOIN PNEU_ORDEM_NOMENCLATURA_UNIDADE PONU
    ON U.CODIGO = PONU.COD_UNIDADE AND PONU.COD_TIPO_VEICULO = V.COD_TIPO AND AV.POSICAO = PONU.POSICAO_PROLOG
  LEFT JOIN MODELO_VEICULO MV
    ON MV.CODIGO = V.COD_MODELO
  LEFT JOIN MARCA_VEICULO M2
    ON MV.COD_MARCA = M2.CODIGO
WHERE A.COD_UNIDADE = ANY (F_COD_UNIDADES)
      AND (A.DATA_HORA AT TIME ZONE TZ_UNIDADE(A.COD_UNIDADE)) :: DATE BETWEEN F_DATA_INICIAL AND F_DATA_FINAL
ORDER BY U.CODIGO, A.DATA_HORA DESC;
$$;

-- Dropa a FUNC_PNEU_RELATORIO_DESGASTE_IRREGULAR
DROP FUNCTION FUNC_PNEU_RELATORIO_DESGASTE_IRREGULAR(BIGINT[],PNEU_STATUS_TYPE);

-- Recria a FUNC_PNEU_RELATORIO_DESGASTE_IRREGULAR
CREATE OR REPLACE FUNCTION FUNC_PNEU_RELATORIO_DESGASTE_IRREGULAR(
  F_COD_UNIDADES BIGINT [],
  F_STATUS_PNEU  PNEU_STATUS_TYPE DEFAULT NULL)
  RETURNS TABLE(
    "UNIDADE ALOCADO"       TEXT,
    "PNEU"                  TEXT,
    "STATUS"                TEXT,
    "VALOR DE AQUISIÇÃO"    TEXT,
    "DATA/HORA CADASTRO"    TEXT,
    "MARCA"                 TEXT,
    "MODELO"                TEXT,
    "BANDA APLICADA"        TEXT,
    "VALOR DA BANDA"        TEXT,
    "MEDIDAS"               TEXT,
    "PLACA"                 TEXT,
    "TIPO"                  TEXT,
    "POSIÇÃO"               TEXT,
    "QUANTIDADE DE SULCOS"  TEXT,
    "SULCO INTERNO"         TEXT,
    "SULCO CENTRAL INTERNO" TEXT,
    "SULCO CENTRAL EXTERNO" TEXT,
    "SULCO EXTERNO"         TEXT,
    "MENOR SULCO"           TEXT,
    "PRESSÃO ATUAL (PSI)"   TEXT,
    "PRESSÃO IDEAL (PSI)"   TEXT,
    "VIDA ATUAL"            TEXT,
    "DOT"                   TEXT,
    "ÚLTIMA AFERIÇÃO"       TEXT,
    "DESCRIÇÃO DESGASTE"    TEXT,
    "NÍVEL DE DESGASTE"     TEXT,
    "APARÊNCIA PNEU"        TEXT,
    "CAUSAS PROVÁVEIS"      TEXT,
    "AÇÃO"                  TEXT,
    "PRECAUÇÃO"             TEXT)
LANGUAGE PLPGSQL
AS $$
DECLARE
  F_TIMESTAMP_FORMAT TEXT := 'DD/MM/YYYY HH24:MI';
BEGIN
  RETURN QUERY
  -- Essa CTE busca o código da última aferição de cada pneu.
  -- Com o código nós conseguimos buscar depois a data/hora da aferição e o código da unidade em que ocorreu,
  -- para aplicar o TZ correto.
  WITH ULTIMAS_AFERICOES AS (
      SELECT
        AV.COD_PNEU   AS COD_PNEU_AFERIDO,
        MAX(A.CODIGO) AS COD_AFERICAO
      FROM AFERICAO A
        JOIN AFERICAO_VALORES AV
          ON AV.COD_AFERICAO = A.CODIGO
        JOIN PNEU P ON P.CODIGO = AV.COD_PNEU
      WHERE P.COD_UNIDADE = ANY (F_COD_UNIDADES)
      GROUP BY AV.COD_PNEU
  )

  SELECT U.NOME :: TEXT                                                               AS UNIDADE_ALOCADO,
         P.CODIGO_CLIENTE :: TEXT                                                     AS COD_PNEU,
         P.STATUS :: TEXT                                                             AS STATUS,
         COALESCE(TRUNC(P.VALOR :: NUMERIC, 2) :: TEXT, '-')                          AS VALOR_AQUISICAO,
         FORMAT_WITH_TZ(P.DATA_HORA_CADASTRO,
                        TZ_UNIDADE(P.COD_UNIDADE_CADASTRO),
                        F_TIMESTAMP_FORMAT,
                        '-')                                                          AS DATA_HORA_CADASTRO,
         MAP.NOME :: TEXT                                                             AS NOME_MARCA_PNEU,
         MP.NOME :: TEXT                                                              AS NOME_MODELO_PNEU,
         F_IF(MARB.CODIGO IS NULL, 'Nunca Recapado', MARB.NOME || ' - ' || MODB.NOME) AS BANDA_APLICADA,
         COALESCE(TRUNC(PVV.VALOR :: NUMERIC, 2) :: TEXT, '-')                        AS VALOR_BANDA,
         FUNC_PNEU_FORMAT_DIMENSAO(DP.LARGURA, DP.ALTURA, DP.ARO)                     AS MEDIDAS,
         COALESCE(VP.PLACA, '-') :: TEXT                                              AS PLACA,
         COALESCE(VT.NOME, '-') :: TEXT                                               AS TIPO_VEICULO,
         COALESCE(PONU.NOMENCLATURA, '-') :: TEXT                                     AS POSICAO_PNEU,
         COALESCE(MODB.QT_SULCOS, MP.QT_SULCOS) :: TEXT                               AS QTD_SULCOS,
         FUNC_PNEU_FORMAT_SULCO(P.ALTURA_SULCO_INTERNO)                               AS SULCO_INTERNO,
         FUNC_PNEU_FORMAT_SULCO(P.ALTURA_SULCO_CENTRAL_INTERNO)                       AS SULCO_CENTRAL_INTERNO,
         FUNC_PNEU_FORMAT_SULCO(P.ALTURA_SULCO_CENTRAL_EXTERNO)                       AS SULCO_CENTRAL_EXTERNO,
         FUNC_PNEU_FORMAT_SULCO(P.ALTURA_SULCO_EXTERNO)                               AS SULCO_EXTERNO,
         FUNC_PNEU_FORMAT_SULCO(LEAST(P.ALTURA_SULCO_EXTERNO, P.ALTURA_SULCO_CENTRAL_EXTERNO,
                                      P.ALTURA_SULCO_CENTRAL_INTERNO,
                                      P.ALTURA_SULCO_INTERNO))                        AS MENOR_SULCO,
         COALESCE(TRUNC(P.PRESSAO_ATUAL) :: TEXT, '-')                                AS PRESSAO_ATUAL,
         P.PRESSAO_RECOMENDADA :: TEXT                                                AS PRESSAO_RECOMENDADA,
         PVN.NOME :: TEXT                                                             AS VIDA_ATUAL,
         COALESCE(P.DOT, '-') :: TEXT                                                 AS DOT,
         -- Usamos um CASE ao invés do coalesce da func FORMAT_WITH_TZ, pois desse modo evitamos o evaluate
         -- dos dois selects internos de consulta na tabela AFERICAO caso o pneu nunca tenha sido aferido.
         CASE
             WHEN UA.COD_AFERICAO IS NULL
                 THEN 'Nunca Aferido'
             ELSE
                 FORMAT_WITH_TZ((SELECT A.DATA_HORA
                                 FROM AFERICAO A
                                 WHERE A.CODIGO = UA.COD_AFERICAO),
                                TZ_UNIDADE((SELECT A.COD_UNIDADE
                                            FROM AFERICAO A
                                            WHERE A.CODIGO = UA.COD_AFERICAO)),
                                F_TIMESTAMP_FORMAT)
             END                                                                      AS ULTIMA_AFERICAO,
         PTDI.DESCRICAO                                                               AS DESCRICAO_DESGASTE,
         -- Por enquanto, deixamos hardcoded os ranges de cada nível de desgaste.
         CASE
             WHEN VERIF_DESGASTE.NIVEL_DESGASTE_IRREGULAR = 'BAIXO'
                 THEN 'BAIXO (0.1 mm até 0.9 mm)'
             WHEN VERIF_DESGASTE.NIVEL_DESGASTE_IRREGULAR = 'MODERADO'
                 THEN 'MODERADO (1.0 mm até 2.0 mm)'
             WHEN VERIF_DESGASTE.NIVEL_DESGASTE_IRREGULAR = 'ACENTUADO'
                 THEN 'ACENTUADO (2.1 mm e acima)'
             END                                                                      AS NIVEL_DESGASTE,
         PTDI.APARENCIA_PNEU                                                          AS APARENCIA_PNEU,
         PTDI.CAUSAS_PROVAVEIS                                                        AS CAUSAS_PROVAVEIS,
         PTDI.ACAO                                                                    AS ACAO,
         PTDI.PRECAUCAO                                                               AS PRECAUCAO
  FROM PNEU P
    JOIN DIMENSAO_PNEU DP ON DP.CODIGO = P.COD_DIMENSAO
    JOIN UNIDADE U ON U.CODIGO = P.COD_UNIDADE
    JOIN MODELO_PNEU MP ON MP.CODIGO = P.COD_MODELO AND MP.COD_EMPRESA = U.COD_EMPRESA
    JOIN MARCA_PNEU MAP ON MAP.CODIGO = MP.COD_MARCA
    JOIN PNEU_VIDA_NOMENCLATURA PVN ON PVN.COD_VIDA = P.VIDA_ATUAL
    JOIN FUNC_PNEU_VERIFICA_DESGASTE_IRREGULAR(P.CODIGO,
                                               P.ALTURA_SULCO_EXTERNO,
                                               P.ALTURA_SULCO_CENTRAL_EXTERNO,
                                               P.ALTURA_SULCO_CENTRAL_INTERNO,
                                               P.ALTURA_SULCO_INTERNO) VERIF_DESGASTE
      ON VERIF_DESGASTE.COD_PNEU = P.CODIGO
    LEFT JOIN PNEU_TIPO_DESGASTE_IRREGULAR PTDI
      ON PTDI.TIPO_DESGASTE_IRREGULAR = VERIF_DESGASTE.TIPO_DESGASTE_IRREGULAR
    LEFT JOIN MODELO_BANDA MODB ON MODB.CODIGO = P.COD_MODELO_BANDA
    LEFT JOIN MARCA_BANDA MARB ON MARB.CODIGO = MODB.COD_MARCA
    LEFT JOIN PNEU_VALOR_VIDA PVV ON P.CODIGO = PVV.COD_PNEU AND PVV.VIDA = P.VIDA_ATUAL
    LEFT JOIN VEICULO_PNEU VP
      ON P.CODIGO = VP.COD_PNEU
         AND P.COD_UNIDADE = VP.COD_UNIDADE
    LEFT JOIN VEICULO V
      ON VP.PLACA = V.PLACA
         AND VP.COD_UNIDADE = V.COD_UNIDADE
    LEFT JOIN VEICULO_TIPO VT
      ON V.cod_tipo = VT.codigo
    LEFT JOIN PNEU_ORDEM_NOMENCLATURA_UNIDADE PONU
      ON PONU.COD_UNIDADE = V.COD_UNIDADE
         AND PONU.COD_TIPO_VEICULO = V.COD_TIPO
         AND PONU.POSICAO_PROLOG = VP.POSICAO
    LEFT JOIN ULTIMAS_AFERICOES UA
      ON UA.COD_PNEU_AFERIDO = P.CODIGO
  WHERE P.COD_UNIDADE = ANY (F_COD_UNIDADES)
        AND F_IF(F_STATUS_PNEU IS NULL, TRUE, F_STATUS_PNEU = P.STATUS :: PNEU_STATUS_TYPE)
        AND VERIF_DESGASTE.TEM_DESGASTE_IRREGULAR
  ORDER BY VERIF_DESGASTE.NIVEL_DESGASTE_IRREGULAR DESC, U.NOME, P.CODIGO_CLIENTE;
END;
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--############################# ADICIONA CÓDIGO AUXILIAR NO CRUD DE TIPOS DE MARCAÇÕES #################################
--######################################################################################################################
--######################################################################################################################
-- PL-2223

-- Adiciona a coluna de código auxiliar na tabela UNIDADE.
ALTER TABLE UNIDADE
	ADD COD_AUXILIAR TEXT DEFAULT NULL;

-- Adiciona a coluna de código auxiliar na tabela INTERVALO_TIPO.
ALTER TABLE INTERVALO_TIPO
	ADD COD_AUXILIAR TEXT DEFAULT NULL;

-- Dropa as dependências
DROP VIEW VIEW_INTERVALO_TIPO;
DROP FUNCTION FUNC_MARCACAO_GET_TIPO_MARCACAO(BIGINT);
DROP FUNCTION FUNC_MARCACAO_GET_TIPOS_MARCACOES(BIGINT,BOOLEAN);

-- Recria as dependências
CREATE OR REPLACE VIEW VIEW_INTERVALO_TIPO AS
  SELECT
    ROW_NUMBER()
    OVER (
      PARTITION BY IT.COD_UNIDADE
      ORDER BY IT.CODIGO )           AS CODIGO_TIPO_INTERVALO_POR_UNIDADE,
    IT.CODIGO                        AS CODIGO,
    IT.COD_UNIDADE                   AS COD_UNIDADE,
    IT.NOME                          AS NOME,
    IT.ICONE                         AS ICONE,
    IT.TEMPO_RECOMENDADO_MINUTOS     AS TEMPO_RECOMENDADO_MINUTOS,
    IT.TEMPO_ESTOURO_MINUTOS         AS TEMPO_ESTOURO_MINUTOS,
    IT.HORARIO_SUGERIDO              AS HORARIO_SUGERIDO,
    IT.ATIVO                         AS ATIVO,
    MTJ.COD_TIPO_JORNADA IS NOT NULL AS TIPO_JORNADA,
    IT.COD_AUXILIAR                  AS COD_AUXILIAR
  FROM INTERVALO_TIPO IT
    LEFT JOIN MARCACAO_TIPO_JORNADA MTJ
      ON IT.COD_UNIDADE = MTJ.COD_UNIDADE
         AND IT.CODIGO = MTJ.COD_TIPO_JORNADA;

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

CREATE OR REPLACE FUNCTION FUNC_MARCACAO_GET_TIPOS_MARCACOES(F_COD_UNIDADE BIGINT, F_APENAS_ATIVOS BOOLEAN)
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
SELECT DISTINCT
  IT.CODIGO                            AS CODIGO_TIPO_INTERVALO,
  IT.CODIGO_TIPO_INTERVALO_POR_UNIDADE AS CODIGO_TIPO_INTERVALO_POR_UNIDADE,
  IT.NOME                              AS NOME_TIPO_INTERVALO,
  IT.COD_UNIDADE,
  IT.ATIVO,
  IT.HORARIO_SUGERIDO,
  IT.ICONE,
  IT.TEMPO_ESTOURO_MINUTOS,
  IT.TEMPO_RECOMENDADO_MINUTOS,
  IT.TIPO_JORNADA,
  IT.COD_AUXILIAR
FROM VIEW_INTERVALO_TIPO IT
WHERE IT.COD_UNIDADE = F_COD_UNIDADE
      AND CASE WHEN F_APENAS_ATIVOS IS TRUE
  THEN IT.ATIVO = TRUE
          ELSE TRUE END
ORDER BY IT.ATIVO DESC, IT.NOME ASC;
$$;

-- Cria a function que retorna os dados para exportação genérica de marcações
CREATE OR REPLACE FUNCTION FUNC_MARCACAO_RELATORIO_EXPORTACAO_GENERICA(F_COD_UNIDADE             BIGINT,
                                                                       F_COD_TIPO_INTERVALO      BIGINT,
                                                                       F_COD_COLABORADOR         BIGINT,
                                                                       F_APENAS_MARCACOES_ATIVAS BOOLEAN,
                                                                       F_DATA_INICIAL            DATE,
                                                                       F_DATA_FINAL              DATE)
    RETURNS TABLE
            (
                PIS           TEXT,
                EVENTO        TEXT,
                DATA          TEXT,
                HORA          TEXT,
                NUMERORELOGIO TEXT
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    TZ_UNIDADE TEXT := TZ_UNIDADE(F_COD_UNIDADE);
BEGIN
    RETURN QUERY
        SELECT LPAD(C.PIS :: TEXT, 11, '0')                             AS PIS,
               COALESCE(IT.COD_AUXILIAR, '00')                          AS EVENTO,
               TO_CHAR(I.DATA_HORA AT TIME ZONE TZ_UNIDADE, 'DDMMYYYY') AS DATA,
               TO_CHAR(I.DATA_HORA AT TIME ZONE TZ_UNIDADE, 'HH24mi')   AS HORA,
               COALESCE(U.COD_AUXILIAR, '00')                           AS NUMERORELOGIO
        FROM INTERVALO I
                 JOIN COLABORADOR C ON I.CPF_COLABORADOR = C.CPF
                 JOIN INTERVALO_TIPO IT ON I.COD_UNIDADE = IT.COD_UNIDADE AND I.COD_TIPO_INTERVALO = IT.CODIGO
                 JOIN UNIDADE U ON I.COD_UNIDADE = U.CODIGO
        WHERE I.COD_UNIDADE = F_COD_UNIDADE
          AND (I.DATA_HORA AT TIME ZONE TZ_UNIDADE) :: DATE >= F_DATA_INICIAL
          AND (I.DATA_HORA AT TIME ZONE TZ_UNIDADE) :: DATE <= F_DATA_FINAL
          AND C.PIS IS NOT NULL
          AND C.PIS <> ''
          AND F_IF(F_COD_COLABORADOR IS NULL, TRUE, C.CODIGO = F_COD_COLABORADOR)
          AND F_IF(F_COD_TIPO_INTERVALO IS NULL, TRUE, IT.CODIGO = F_COD_TIPO_INTERVALO)
          AND F_IF(F_APENAS_MARCACOES_ATIVAS IS NULL, TRUE, I.STATUS_ATIVO);
END;
$$;
--######################################################################################################################
--######################################################################################################################
END TRANSACTION;