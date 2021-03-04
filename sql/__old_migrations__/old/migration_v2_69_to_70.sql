BEGIN TRANSACTION ;
-- ########################################################################################################
-- ###################################### CRIA CHECK PARA STATUS VÁLIDOS DOS PNEUS ########################
-- ########################################################################################################
alter table pneu add constraint check_status_validos
check (status in ('EM_USO', 'ESTOQUE', 'ANALISE', 'DESCARTE'));
-- ########################################################################################################
-- ########################################################################################################

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

  -- FOUND será true se alguma linha foi modificada pela query executada
  IF FOUND THEN
    RETURN TRUE;
  ELSE
    RETURN FALSE;
  END IF;
END;
$$;
-- ########################################################################################################
-- ########################################################################################################

-- ########################################################################################################
-- ########################################################################################################
-- ############### FUNCTION PARA LISTAR A SOMA DE TODOS OS INTERVALOS MARCADOS POR TIPO ###################
-- ########################################################################################################
-- ########################################################################################################
-- Cria function para converter um interval em segundos
CREATE OR REPLACE FUNCTION to_seconds(t interval)
  RETURNS BIGINT
LANGUAGE plpgsql
AS $$
DECLARE
    seconds INTEGER;
BEGIN
    SELECT (EXTRACT (EPOCH from t))::BIGINT INTO seconds;
    RETURN seconds;
END;
$$;

CREATE OR REPLACE FUNCTION FUNC_INTERVALOS_GET_TOTAL_TEMPO_POR_TIPO_INTERVALO(
  F_COD_UNIDADE BIGINT,
  F_COD_TIPO_INTERVALO BIGINT,
  F_DATA_INICIAL TIMESTAMP WITHOUT TIME ZONE,
  F_DATA_FINAL TIMESTAMP WITHOUT TIME ZONE,
  F_CURRENT_TIMESTAMP_UTC TIMESTAMP WITHOUT TIME ZONE)
  RETURNS table(
    CPF_COLABORADOR TEXT,
    NOME TEXT,
    CARGO TEXT,
    COD_TIPO_INTERVALO TEXT,
    NOME_TIPO_INTERVALO TEXT,
    TEMPO_TOTAL_MILLIS TEXT)
LANGUAGE plpgsql
AS $$
DECLARE
  FILTRO_INICIO TIMESTAMP WITHOUT TIME ZONE := F_DATA_INICIAL;
  -- Sem o filtro de fim for maior que o horário atual do sistema, precisamos garantir que os cálculos utilizem o
  -- horário atual e não o filtro de fim.
  FILTRO_FIM TIMESTAMP WITHOUT TIME ZONE := CASE
                                           WHEN F_DATA_FINAL < F_CURRENT_TIMESTAMP_UTC
                                             THEN F_DATA_INICIAL
                                           ELSE F_CURRENT_TIMESTAMP_UTC
                                           END;
  TZ_UNIDADE TEXT := TZ_UNIDADE(F_COD_UNIDADE);
BEGIN
  RETURN QUERY SELECT
    LPAD(DURACAO_INTERVALOS.CPF_COLABORADOR::TEXT, 11, '0') AS CPF_COLABORADOR,
    DURACAO_INTERVALOS.NOME_COLABORADOR::TEXT AS NOME,
    DURACAO_INTERVALOS.NOME_FUNCAO::TEXT AS CARGO,
    DURACAO_INTERVALOS.COD_TIPO_INTERVALO::TEXT AS COD_TIPO_INTERVALO,
    DURACAO_INTERVALOS.NOME_TIPO_INTERVALO::TEXT AS NOME_TIPO_INTERVALO,
    (SUM(DURACAO_INTERVALOS.SOMA_DURACAO_SEGUNDOS) * 1000)::TEXT AS TEMPO_TOTAL_MILLIS
  FROM (SELECT
          C.CPF AS CPF_COLABORADOR,
          C.NOME AS NOME_COLABORADOR,
          F.NOME AS NOME_FUNCAO,
          IT.CODIGO AS COD_TIPO_INTERVALO,
          IT.NOME AS NOME_TIPO_INTERVALO,
          to_seconds(
              CASE
              -- 1) Para o caso em que o início do intervalo está fora do período filtrado mas a finalização dentro do filtro
              WHEN (I.DATA_HORA_INICIO < FILTRO_INICIO
                    AND FILTRO_INICIO < I.DATA_HORA_FIM
                    AND I.DATA_HORA_FIM < FILTRO_FIM
                    AND IT.CODIGO = I.COD_TIPO_INTERVALO)
                THEN I.DATA_HORA_FIM - FILTRO_INICIO
              -- 2) Para o caso em que o início do intervalo está dentro do período filtrado mas a finalização fora do filtro
              WHEN (I.DATA_HORA_INICIO > FILTRO_INICIO
                    AND FILTRO_FIM > I.DATA_HORA_INICIO
                    AND I.DATA_HORA_FIM > FILTRO_FIM
                    AND IT.CODIGO = I.COD_TIPO_INTERVALO)
                THEN FILTRO_FIM - I.DATA_HORA_INICIO
              -- 3) Para o caso em que o início e o fim estão dentro do intervalo do filtro
              WHEN (FILTRO_INICIO < I.DATA_HORA_INICIO
                    AND FILTRO_FIM > I.DATA_HORA_FIM
                    AND IT.CODIGO = I.COD_TIPO_INTERVALO)
                THEN I.DATA_HORA_FIM - I.DATA_HORA_INICIO
              -- 4) Para o caso em que o início e o fim estão fora do intervalo do filtro
              WHEN (I.DATA_HORA_INICIO < FILTRO_INICIO
                    AND I.DATA_HORA_FIM > FILTRO_FIM
                    AND IT.CODIGO = I.COD_TIPO_INTERVALO)
                THEN FILTRO_FIM - FILTRO_INICIO
              END) AS SOMA_DURACAO_SEGUNDOS
        FROM func_intervalos_agrupados(f_cod_unidade, NULL, f_cod_tipo_intervalo) AS I
          JOIN COLABORADOR AS C
            ON I.CPF_COLABORADOR = C.CPF
          JOIN FUNCAO AS F
            ON C.COD_FUNCAO = F.CODIGO
          LEFT JOIN INTERVALO_TIPO AS IT
            ON I.COD_UNIDADE = IT.COD_UNIDADE
               AND IT.ATIVO = TRUE
        WHERE (((I.DATA_HORA_INICIO AT TIME ZONE TZ_UNIDADE >= f_data_inicial)
                AND ((I.DATA_HORA_INICIO AT TIME ZONE TZ_UNIDADE <= f_data_final)))
               OR
               ((I.DATA_HORA_FIM AT TIME ZONE TZ_UNIDADE >= f_data_inicial)
                AND (I.DATA_HORA_FIM AT TIME ZONE TZ_UNIDADE <= f_data_final))
                 -- Expurga marcações que não tem início
               AND I.DATA_HORA_INICIO IS NOT NULL)
              OR
            -- Filtra por marcações que tiveram seu início antes do filtro e fim após o filtro.
              (I.DATA_HORA_INICIO AT TIME ZONE TZ_UNIDADE  < f_data_inicial
              AND I.DATA_HORA_FIM AT TIME ZONE TZ_UNIDADE  > f_data_final)
        GROUP BY C.CPF, C.NOME, F.CODIGO, IT.CODIGO, I.COD_TIPO_INTERVALO,
          IT.NOME, I.DATA_HORA_INICIO, I.DATA_HORA_FIM) AS DURACAO_INTERVALOS
  GROUP BY DURACAO_INTERVALOS.CPF_COLABORADOR, DURACAO_INTERVALOS.NOME_COLABORADOR,
    DURACAO_INTERVALOS.NOME_FUNCAO, DURACAO_INTERVALOS.COD_TIPO_INTERVALO, DURACAO_INTERVALOS.NOME_TIPO_INTERVALO
  ORDER BY CPF_COLABORADOR, COD_TIPO_INTERVALO DESC;
END;
$$;
-- ########################################################################################################
-- ########################################################################################################

-- ########################################################################################################
-- ########################################################################################################
-- ############### ALTERA FUNCTION DO RELATÓRIO DE FOLHA DE PONTO #########################################
-- ########################################################################################################
-- ########################################################################################################
drop function func_relatorio_intervalo_folha_de_ponto(f_cod_unidade bigint, f_cod_tipo_intervalo bigint, f_cpf_colaborador bigint, f_data_inicial date, f_data_final date, f_time_zone_unidade text);
create function func_relatorio_intervalo_folha_ponto(f_cod_unidade bigint, f_cod_tipo_intervalo bigint, f_cpf_colaborador bigint, f_data_inicial date, f_data_final date, f_time_zone_unidade text)
  returns TABLE(cpf_colaborador bigint, nome_colaborador text, cod_tipo_intervalo bigint, cod_tipo_intervalo_por_unidade bigint, data_hora_inicio timestamp without time zone, data_hora_fim timestamp without time zone, data_hora_inicio_utc timestamp without time zone, data_hora_fim_utc timestamp without time zone, diferenca_marcacoes_segundos double precision, trocou_dia boolean)
language sql
as $$
SELECT
  CPF_COLABORADOR,
  C.NOME AS NOME_COLABORADOR,
  COD_TIPO_INTERVALO AS COD_TIPO_INTERVALO,
  COD_TIPO_INTERVALO_POR_UNIDADE AS COD_TIPO_INTERVALO_POR_UNIDADE,
  DATA_HORA_INICIO AT TIME ZONE F_TIME_ZONE_UNIDADE,
  DATA_HORA_FIM AT TIME ZONE F_TIME_ZONE_UNIDADE,
  DATA_HORA_INICIO AT TIME ZONE 'UTC' AS DATA_HORA_INICIO_UTC,
  DATA_HORA_FIM AT TIME ZONE 'UTC' AS DATA_HORA_FIM_UTC,
  EXTRACT(EPOCH FROM (DATA_HORA_FIM - DATA_HORA_INICIO)) AS DIFERENCA_MARCACOES_SEGUNDOS,
  DATA_HORA_INICIO IS NOT NULL AND DATA_HORA_FIM IS NOT NULL AND (DATA_HORA_INICIO AT TIME ZONE F_TIME_ZONE_UNIDADE)::DATE != (DATA_HORA_FIM AT TIME ZONE F_TIME_ZONE_UNIDADE)::DATE AS TROCOU_DIA
FROM FUNC_INTERVALOS_AGRUPADOS(F_COD_UNIDADE, F_CPF_COLABORADOR, F_COD_TIPO_INTERVALO) F
  JOIN COLABORADOR C
    ON F.CPF_COLABORADOR = C.CPF
WHERE
  -- Filtra por marcações que tenham seu INÍCIO dentro do período filtrado, não importando se tenham FIM.
  ((F.DATA_HORA_INICIO AT TIME ZONE F_TIME_ZONE_UNIDADE) :: DATE >= F_DATA_INICIAL
   AND (F.DATA_HORA_INICIO AT TIME ZONE F_TIME_ZONE_UNIDADE) :: DATE <= F_DATA_FINAL)
  OR
  -- Filtra por marcações que tenham seu FIM dentro do período filtrado, não importando se tenham INÍCIO.
  ((F.DATA_HORA_FIM AT TIME ZONE F_TIME_ZONE_UNIDADE) :: DATE >= F_DATA_INICIAL
   AND (F.DATA_HORA_FIM AT TIME ZONE F_TIME_ZONE_UNIDADE) :: DATE <= F_DATA_FINAL)
  OR
  -- Filtra por marcações que tiveram seu início antes do filtro e fim após o filtro.
  ((F.DATA_HORA_INICIO AT TIME ZONE F_TIME_ZONE_UNIDADE) :: DATE < F_DATA_INICIAL
   AND (F.DATA_HORA_FIM AT TIME ZONE F_TIME_ZONE_UNIDADE) :: DATE > F_DATA_FINAL)
ORDER BY F.CPF_COLABORADOR, COALESCE(F.DATA_HORA_INICIO, F.DATA_HORA_FIM) ASC;
$$;
-- ########################################################################################################
-- ########################################################################################################


-- ########################################################################################################
-- ########################################################################################################
-- ############### FUNCTION PARA BUSCAR OS COLABORADORES DE UMA UNIDADE ###################################
-- ########################################################################################################
-- ########################################################################################################
CREATE OR REPLACE FUNCTION FUNC_COLABORADORES_GET_ALL_BY_UNIDADE(F_COD_UNIDADE BIGINT, F_STATUS_ATIVOS BOOLEAN)
  RETURNS TABLE(
    CODIGO             BIGINT,
    CPF                BIGINT,
    PIS                VARCHAR(11),
    MATRICULA_AMBEV    INTEGER,
    MATRICULA_TRANS    INTEGER,
    DATA_NASCIMENTO    DATE,
    DATA_ADMISSAO      DATE,
    DATA_DEMISSAO      DATE,
    STATUS_ATIVO       BOOLEAN,
    NOME_COLABORADOR   TEXT,
    NOME_EMPRESA       TEXT,
    COD_EMPRESA        BIGINT,
    LOGO_THUMBNAIL_URL TEXT,
    NOME_REGIONAL      TEXT,
    COD_REGIONAL       BIGINT,
    NOME_UNIDADE       TEXT,
    COD_UNIDADE        BIGINT,
    NOME_EQUIPE        TEXT,
    COD_EQUIPE         BIGINT,
    NOME_SETOR         TEXT,
    COD_SETOR          BIGINT,
    COD_FUNCAO         BIGINT,
    NOME_FUNCAO        TEXT,
    PERMISSAO          BIGINT)
LANGUAGE SQL
AS $$

SELECT
  C.CODIGO,
  C.CPF,
  C.PIS,
  C.MATRICULA_AMBEV,
  C.MATRICULA_TRANS,
  C.DATA_NASCIMENTO,
  C.DATA_ADMISSAO,
  C.DATA_DEMISSAO,
  C.STATUS_ATIVO,
  initcap(C.NOME) AS NOME_COLABORADOR,
  EM.NOME         AS NOME_EMPRESA,
  EM.CODIGO       AS COD_EMPRESA,
  EM.LOGO_THUMBNAIL_URL,
  R.REGIAO        AS NOME_REGIONAL,
  R.CODIGO        AS COD_REGIONAL,
  U.NOME          AS NOME_UNIDADE,
  U.CODIGO        AS COD_UNIDADE,
  EQ.NOME         AS NOME_EQUIPE,
  EQ.CODIGO       AS COD_EQUIPE,
  S.NOME          AS NOME_SETOR,
  S.CODIGO        AS COD_SETOR,
  F.CODIGO        AS COD_FUNCAO,
  F.NOME          AS NOME_FUNCAO,
  C.COD_PERMISSAO AS PERMISSAO
FROM COLABORADOR C
  JOIN FUNCAO F ON C.COD_FUNCAO = F.CODIGO
  JOIN EQUIPE EQ ON EQ.CODIGO = C.COD_EQUIPE
  JOIN UNIDADE U ON U.CODIGO = C.COD_UNIDADE
  JOIN EMPRESA EM ON EM.CODIGO = C.COD_EMPRESA AND EM.CODIGO = U.COD_EMPRESA
  JOIN REGIONAL R ON R.CODIGO = U.COD_REGIONAL
  JOIN SETOR S ON S.CODIGO = C.COD_SETOR AND C.COD_UNIDADE = S.COD_UNIDADE
WHERE
  C.COD_UNIDADE = F_COD_UNIDADE
  AND
  CASE
  WHEN F_STATUS_ATIVOS IS NULL
    THEN 1 = 1
  ELSE C.status_ativo = F_STATUS_ATIVOS
  END
ORDER BY C.NOME ASC
$$;
-- ########################################################################################################
-- ########################################################################################################


-- ########################################################################################################
-- ########################################################################################################
-- ############### FUNCTION PARA BUSCAR OS COLABORADORES DE UMA EMPRESA ###################################
-- ########################################################################################################
-- ########################################################################################################
CREATE OR REPLACE FUNCTION FUNC_COLABORADORES_GET_ALL_BY_EMPRESA(F_COD_EMPRESA BIGINT, F_STATUS_ATIVOS BOOLEAN)
  RETURNS TABLE(
    CODIGO             BIGINT,
    CPF                BIGINT,
    PIS                VARCHAR(11),
    MATRICULA_AMBEV    INTEGER,
    MATRICULA_TRANS    INTEGER,
    DATA_NASCIMENTO    DATE,
    DATA_ADMISSAO      DATE,
    DATA_DEMISSAO      DATE,
    STATUS_ATIVO       BOOLEAN,
    NOME_COLABORADOR   TEXT,
    NOME_EMPRESA       TEXT,
    COD_EMPRESA        BIGINT,
    LOGO_THUMBNAIL_URL TEXT,
    NOME_REGIONAL      TEXT,
    COD_REGIONAL       BIGINT,
    NOME_UNIDADE       TEXT,
    COD_UNIDADE        BIGINT,
    NOME_EQUIPE        TEXT,
    COD_EQUIPE         BIGINT,
    NOME_SETOR         TEXT,
    COD_SETOR          BIGINT,
    COD_FUNCAO         BIGINT,
    NOME_FUNCAO        TEXT,
    PERMISSAO          BIGINT)
LANGUAGE SQL
AS $$

SELECT
  C.CODIGO,
  C.CPF,
  C.PIS,
  C.MATRICULA_AMBEV,
  C.MATRICULA_TRANS,
  C.DATA_NASCIMENTO,
  C.DATA_ADMISSAO,
  C.DATA_DEMISSAO,
  C.STATUS_ATIVO,
  initcap(C.NOME) AS NOME_COLABORADOR,
  EM.NOME         AS NOME_EMPRESA,
  EM.CODIGO       AS COD_EMPRESA,
  EM.LOGO_THUMBNAIL_URL,
  R.REGIAO        AS NOME_REGIONAL,
  R.CODIGO        AS COD_REGIONAL,
  U.NOME          AS NOME_UNIDADE,
  U.CODIGO        AS COD_UNIDADE,
  EQ.NOME         AS NOME_EQUIPE,
  EQ.CODIGO       AS COD_EQUIPE,
  S.NOME          AS NOME_SETOR,
  S.CODIGO        AS COD_SETOR,
  F.CODIGO        AS COD_FUNCAO,
  F.NOME          AS NOME_FUNCAO,
  C.COD_PERMISSAO AS PERMISSAO
FROM COLABORADOR C
  JOIN FUNCAO F ON C.COD_FUNCAO = F.CODIGO
  JOIN EQUIPE EQ ON EQ.CODIGO = C.COD_EQUIPE
  JOIN UNIDADE U ON U.CODIGO = C.COD_UNIDADE
  JOIN EMPRESA EM ON EM.CODIGO = C.COD_EMPRESA AND EM.CODIGO = U.COD_EMPRESA
  JOIN REGIONAL R ON R.CODIGO = U.COD_REGIONAL
  JOIN SETOR S ON S.CODIGO = C.COD_SETOR AND C.COD_UNIDADE = S.COD_UNIDADE
WHERE
  C.COD_EMPRESA = F_COD_EMPRESA
  AND
  CASE
  WHEN F_STATUS_ATIVOS IS NULL
    THEN 1 = 1
  ELSE C.status_ativo = F_STATUS_ATIVOS
  END
ORDER BY C.NOME ASC
$$;
-- ########################################################################################################
-- ########################################################################################################
END TRANSACTION ;