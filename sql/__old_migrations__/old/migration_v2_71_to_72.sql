BEGIN TRANSACTION ;
-- ########################################################################################################
-- ########################################################################################################
-- ##############################            PRODUTIVIDADE RAÍZEN            ##############################
-- ########################################################################################################
-- ########################################################################################################

-- ########################################################################################################
-- ########################################    CRIA NOVO SCHEMA    ########################################
-- ########################################################################################################
CREATE SCHEMA RAIZEN;
-- ########################################################################################################
-- ########################################################################################################

-- ########################################################################################################
-- #################### CRIA TABELA PARA IMPLEMENTAR BACK-END DA PRODUTIVIDADE RAÍZEN #####################
-- ########################################################################################################
CREATE TABLE RAIZEN.PRODUTIVIDADE (
  CODIGO BIGSERIAL NOT NULL,
  CPF_MOTORISTA BIGINT NOT NULL,
  PLACA VARCHAR(7) NOT NULL,
  DATA_VIAGEM date NOT NULL,
  VALOR NUMERIC NOT NULL,
  USINA VARCHAR NOT NULL,
  FAZENDA VARCHAR NOT NULL,
  RAIO_KM NUMERIC NOT NULL,
  TONELADAS NUMERIC NOT NULL,
  COD_COLABORADOR_CADASTRO BIGINT NOT NULL,
  COD_COLABORADOR_ALTERACAO BIGINT NOT NULL,
  COD_UNIDADE BIGINT NOT NULL,
  CONSTRAINT PK_PRODUTIVIDADE PRIMARY KEY (CODIGO),
  CONSTRAINT FK_PRODUTIVIDADE_UNIDAE FOREIGN KEY (COD_UNIDADE) REFERENCES UNIDADE(CODIGO),
  CONSTRAINT ENTRADA_NAO_DUPLICADA UNIQUE (CPF_MOTORISTA, PLACA, DATA_VIAGEM, VALOR, USINA, FAZENDA, RAIO_KM, TONELADAS, COD_UNIDADE)
);
COMMENT ON TABLE RAIZEN.PRODUTIVIDADE
IS 'Tabela de produtividade Raízen';
-- ########################################################################################################
-- ########################################################################################################

-- ########################################################################################################
-- ##################### FUNCTION PARA LISTAR A BUSCA POR DATA - PRODUTIVIDADE RAÍZEN #####################
-- ########################################################################################################
CREATE OR REPLACE FUNCTION RAIZEN.func_raizen_produtividade_get_itens_por_data(
  f_cod_unidade bigint, f_data_inicial date, f_data_final date)
  RETURNS TABLE(
    CODIGO BIGINT,
    CPF_MOTORISTA BIGINT,
    NOME_MOTORISTA VARCHAR,
    PLACA VARCHAR,
    PLACA_CADASTRADA BOOLEAN,
    DATA_VIAGEM DATE,
    VALOR NUMERIC,
    USINA VARCHAR,
    FAZENDA VARCHAR,
    RAIO_KM NUMERIC,
    TONELADAS NUMERIC,
    COD_COLABORADOR_CADASTRO BIGINT,
    NOME_COLABORADOR_CADSTRO VARCHAR,
    COD_COLABORADOR_ALTERACAO BIGINT,
    NOME_COLABORADOR_ALTERACAO VARCHAR,
    COD_UNIDADE BIGINT)
LANGUAGE SQL
AS $$
SELECT RP.CODIGO,
  RP.CPF_MOTORISTA,
  CM.NOME AS NOME_MOTORISTA,
  RP.PLACA,
  CASE WHEN V.PLACA IS NOT NULL THEN TRUE ELSE FALSE END AS PLACA_CADASTRADA,
  RP.DATA_VIAGEM,
  RP.VALOR,
  RP.USINA,
  RP.FAZENDA,
  RP.RAIO_KM,
  RP.TONELADAS,
  RP.COD_COLABORADOR_CADASTRO,
  CC.NOME AS NOME_COLABORADOR_CADSTRO,
  RP.COD_COLABORADOR_ALTERACAO,
  CA.NOME AS NOME_COLABORADOR_ALTERACAO,
  RP.COD_UNIDADE
FROM RAIZEN.PRODUTIVIDADE RP
  LEFT JOIN COLABORADOR AS CM ON CM.CPF = RP.CPF_MOTORISTA
  LEFT JOIN COLABORADOR AS CC ON CC.CODIGO = RP.COD_COLABORADOR_CADASTRO
  LEFT JOIN COLABORADOR AS CA ON CA.CODIGO = RP.COD_COLABORADOR_ALTERACAO
  LEFT JOIN VEICULO AS V ON V.PLACA = RP.PLACA
WHERE RP.COD_UNIDADE = f_cod_unidade AND (RP.DATA_VIAGEM >= f_data_inicial AND RP.DATA_VIAGEM <= f_data_final)
ORDER BY RP.DATA_VIAGEM;
$$;
-- ########################################################################################################
-- ########################################################################################################

-- ########################################################################################################
-- ############### FUNCTION PARA LISTAR A BUSCA POR COLABORADOR- PRODUTIVIDADE RAÍZEN #####################
-- ########################################################################################################
CREATE OR REPLACE FUNCTION RAIZEN.func_raizen_produtividade_get_itens_por_colaborador(
  f_cod_unidade bigint, f_data_inicial date, f_data_final date)
  RETURNS TABLE(
    CODIGO BIGINT,
    CPF_MOTORISTA BIGINT,
    NOME_MOTORISTA VARCHAR,
    PLACA VARCHAR,
    PLACA_CADASTRADA boolean,
    DATA_VIAGEM DATE,
    VALOR NUMERIC,
    USINA VARCHAR,
    FAZENDA VARCHAR,
    RAIO_KM NUMERIC,
    TONELADAS NUMERIC,
    COD_COLABORADOR_CADASTRO BIGINT,
    NOME_CADASTRO VARCHAR,
    COD_COLABORADOR_ALTERACAO BIGINT,
    NOME_ALTERACAO VARCHAR,
    COD_UNIDADE BIGINT)
LANGUAGE SQL
AS $$
SELECT RP.CODIGO,
  RP.CPF_MOTORISTA,
  CM.NOME AS NOME_MOTORISTA,
  RP.PLACA,
  CASE WHEN V.PLACA IS NOT NULL THEN TRUE ELSE FALSE END AS PLACA_CADASTRADA,
  RP.DATA_VIAGEM,
  RP.VALOR,
  RP.USINA,
  RP.FAZENDA,
  RP.RAIO_KM,
  RP.TONELADAS,
  RP.COD_COLABORADOR_CADASTRO,
  CC.NOME AS NOME_COLABORADOR_CADSTRO,
  RP.COD_COLABORADOR_ALTERACAO,
  CA.NOME AS NOME_COLABORADOR_ALTERACAO,
  RP.COD_UNIDADE
FROM RAIZEN.PRODUTIVIDADE RP
  LEFT JOIN COLABORADOR AS CM ON CM.CPF = RP.CPF_MOTORISTA
  LEFT JOIN COLABORADOR AS CC ON CC.CODIGO = RP.COD_COLABORADOR_CADASTRO
  LEFT JOIN COLABORADOR AS CA ON CA.CODIGO = RP.COD_COLABORADOR_ALTERACAO
  LEFT JOIN VEICULO AS V ON V.PLACA = RP.PLACA
WHERE RP.COD_UNIDADE = f_cod_unidade AND (RP.DATA_VIAGEM >= f_data_inicial AND RP.DATA_VIAGEM <= f_data_final)
ORDER BY NOME_MOTORISTA, DATA_VIAGEM;
$$;
-- ########################################################################################################
-- ########################################################################################################

-- ########################################################################################################
-- ######### FUNCTION PARA LISTAR A BUSCA POR COLABORADOR INDIVIDUALMENTE - PRODUTIVIDADE RAÍZEN ##########
-- ########################################################################################################
CREATE OR REPLACE FUNCTION RAIZEN.func_raizen_produtividade_get_itens_individual (
  f_cod_unidade bigint, f_cod_colaborador bigint, f_mes int, f_ano int)
  RETURNS TABLE(
    CODIGO BIGINT,
    CPF_MOTORISTA BIGINT,
    NOME_MOTORISTA VARCHAR,
    PLACA VARCHAR,
    PLACA_CADASTRADA BOOLEAN,
    DATA_VIAGEM DATE,
    VALOR NUMERIC,
    USINA VARCHAR,
    FAZENDA VARCHAR,
    RAIO_KM NUMERIC,
    TONELADAS NUMERIC,
    COD_COLABORADOR_CADASTRO BIGINT,
    NOME_COLABORADOR_CADASTRO VARCHAR,
    COD_COLABORADOR_ALTERACAO BIGINT,
    NOME_COLABORADOR_ALTERACAO VARCHAR,
    COD_UNIDADE BIGINT)
LANGUAGE SQL
AS $$
SELECT RP.CODIGO,
  RP.CPF_MOTORISTA,
  CM.NOME AS NOME_MOTORISTA,
  RP.PLACA,
  CASE WHEN V.PLACA IS NOT NULL THEN TRUE ELSE FALSE END AS PLACA_CADASTRADA,
  RP.DATA_VIAGEM,
  RP.VALOR,
  RP.USINA,
  RP.FAZENDA,
  RP.RAIO_KM,
  RP.TONELADAS,
  RP.COD_COLABORADOR_CADASTRO,
  CC.NOME AS NOME_COLABORADOR_CADASTRO,
  RP.COD_COLABORADOR_ALTERACAO,
  CA.NOME AS NOME_COLABORADOR_ALTERACAO,
  RP.COD_UNIDADE
FROM RAIZEN.PRODUTIVIDADE RP
  LEFT JOIN COLABORADOR AS CM ON CM.CPF = RP.CPF_MOTORISTA
  LEFT JOIN COLABORADOR AS CC ON CC.CODIGO = RP.COD_COLABORADOR_CADASTRO
  LEFT JOIN COLABORADOR AS CA ON CA.CODIGO = RP.COD_COLABORADOR_ALTERACAO
  LEFT JOIN VEICULO AS V ON V.PLACA = RP.PLACA
WHERE RP.COD_UNIDADE = f_cod_unidade
      AND CM.CODIGO = f_cod_colaborador
      AND extract(MONTH FROM RP.DATA_VIAGEM) = f_mes
      AND extract(YEAR FROM RP.DATA_VIAGEM) = f_ano
ORDER BY RP.DATA_VIAGEM ASC, RP.PLACA ASC;
$$;
-- ########################################################################################################
-- ########################################################################################################

-- ########################################################################################################
-- ######### FUNCTION PARA BUSCAR UM ITEM ESPECÍFICO DA PRODUTIVIDADE POR CÓDIGO ##########################
-- ########################################################################################################
CREATE OR REPLACE FUNCTION RAIZEN.func_raizen_produtividade_get_item_por_codigo(f_codigo bigint)
  RETURNS TABLE(
    CODIGO BIGINT,
    CPF_MOTORISTA BIGINT,
    MOTORISTA_CADASTRADO BOOLEAN,
    PLACA VARCHAR,
    PLACA_CADASTRADA BOOLEAN,
    DATA_VIAGEM DATE,
    VALOR NUMERIC,
    USINA VARCHAR,
    FAZENDA VARCHAR,
    RAIO_KM NUMERIC,
    TONELADAS NUMERIC,
    COD_COLABORADOR_CADASTRO BIGINT,
    COD_COLABORADOR_ALTERACAO BIGINT,
    COD_UNIDADE BIGINT)
LANGUAGE SQL
AS $$
SELECT
  RP.CODIGO,
  RP.CPF_MOTORISTA,
  CASE WHEN CM.NOME IS NOT NULL THEN TRUE ELSE FALSE END AS MOTORISTA_CADASTRADO,
  RP.PLACA,
  CASE WHEN V.PLACA IS NOT NULL THEN TRUE ELSE FALSE END AS PLACA_CADASTRADA,
  RP.DATA_VIAGEM,
  RP.VALOR,
  RP.USINA,
  RP.FAZENDA,
  RP.RAIO_KM,
  RP.TONELADAS,
  RP.COD_COLABORADOR_CADASTRO,
  RP.COD_COLABORADOR_ALTERACAO,
  RP.COD_UNIDADE
FROM RAIZEN.PRODUTIVIDADE RP
  LEFT JOIN COLABORADOR AS CM ON CM.CPF = RP.CPF_MOTORISTA
  LEFT JOIN VEICULO AS V ON V.PLACA = RP.PLACA
WHERE RP.CODIGO = f_codigo
$$;
-- ########################################################################################################
-- ########################################################################################################

-- ########################################################################################################
-- ######### FUNCTION QUE GERA RELATÓRIO DE DADOS GERAIS DA PRODUTIVIDADE RAÍZEN ##########################
-- ########################################################################################################
CREATE OR REPLACE FUNCTION RAIZEN.func_raizen_produtividade_relatorio_dados_gerais_produtividade (
  f_cod_unidade bigint,
  f_data_inicial DATE,
  f_data_final DATE)
  RETURNS TABLE (
  "NOME MOTORISTA" TEXT,
  "CPF MOTORISTA" TEXT,
  "PLACA" TEXT,
  "DATA DA VIAGEM" TEXT,
  "VALOR" TEXT,
  "USINA" TEXT,
  "FAZENDA" TEXT,
  "RAIO KM" TEXT,
  "TONELADAS" TEXT
  ) AS
$func$
SELECT
  COALESCE(CM.NOME,'-')::TEXT,
  LPAD(RP.CPF_MOTORISTA::TEXT, 11, '0'),
  RP.PLACA::TEXT,
  TO_CHAR(RP.DATA_VIAGEM, 'DD/MM/YYYY'),
  RP.VALOR::TEXT,
  RP.USINA::TEXT,
  RP.FAZENDA::TEXT,
  RP.RAIO_KM::TEXT,
  RP.TONELADAS::TEXT
FROM RAIZEN.PRODUTIVIDADE RP
  LEFT JOIN COLABORADOR AS CM ON CM.CPF = RP.CPF_MOTORISTA
WHERE RP.DATA_VIAGEM >= f_data_inicial AND RP.DATA_VIAGEM <= f_data_final AND RP.COD_UNIDADE = f_cod_unidade
ORDER BY DATA_VIAGEM;
$func$ LANGUAGE SQL;
-- ########################################################################################################
-- ########################################################################################################

-- ########################################################################################################
-- ################ ADICIONA FUNÇÕES PROLOG PARA A PRODUTIVIDADE RAIZEN - FUNCAO_PROLOG_V11 ###############
-- ########################################################################################################
INSERT INTO FUNCAO_PROLOG_V11 (CODIGO, FUNCAO, COD_PILAR)
VALUES (414, 'Raizen Produtividade - Visualizar todos', 4),
  (415, 'Raizen Produtividade - Visualizar próprios', 4),
  (416, 'Raizen Produtividade - Editar', 4),
  (417, 'Raizen Produtividade - Upload', 4),
  (418, 'Raizen Produtividade - Deletar', 4),
  (419, 'Raizen Produtividade - Visualizar Relatórios', 4);
-- ########################################################################################################
-- ########################################################################################################

-- ########################################################################################################
-- ################ CORRIGE CÁLCULO DO TOTAL DE HORAS #####################################################
-- ########################################################################################################
drop function func_intervalos_get_total_tempo_por_tipo_intervalo(f_cod_unidade bigint, f_cod_tipo_intervalo bigint, f_data_inicial timestamp without time zone, f_data_final timestamp without time zone, f_current_timestamp_utc timestamp without time zone);
create or replace function func_intervalos_get_total_tempo_por_tipo_intervalo(f_cod_unidade bigint, f_cod_tipo_intervalo bigint, f_data_inicial timestamp without time zone, f_data_final timestamp without time zone, f_current_timestamp_utc timestamp without time zone)
  returns TABLE(cpf_colaborador text, nome text, cargo text, cod_tipo_intervalo text, nome_tipo_intervalo text, tempo_total_millis text)
language plpgsql
as $$
DECLARE
  FILTRO_INICIO TIMESTAMP WITHOUT TIME ZONE := F_DATA_INICIAL;
  -- Sem o filtro de fim for maior que o horário atual do sistema, precisamos garantir que os cálculos utilizem o
  -- horário atual e não o filtro de fim.
  FILTRO_FIM TIMESTAMP WITHOUT TIME ZONE := CASE
                                           WHEN F_DATA_FINAL < F_CURRENT_TIMESTAMP_UTC
                                             THEN F_DATA_FINAL
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
END TRANSACTION ;