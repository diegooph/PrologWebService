BEGIN TRANSACTION ;

--######################################################################################################################
--######################################################################################################################
--##################################### FUNC UTILITÁRIA PARA FAZER UM IF TERNÁRIO ######################################
--######################################################################################################################
--######################################################################################################################
CREATE OR REPLACE FUNCTION F_IF(BOOLEAN, ANYELEMENT, ANYELEMENT)
  RETURNS ANYELEMENT AS $$
BEGIN
  CASE WHEN ($1)
    THEN
      RETURN ($2);
  ELSE
    RETURN ($3);
  END CASE;
END;
$$
LANGUAGE PLPGSQL;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--##################################### FUNC PARA BUSCAR AS ALTERNATIVAS DO RELATO #####################################
--######################################################################################################################
--######################################################################################################################
CREATE OR REPLACE FUNCTION FUNC_RELATO_GET_ALTERNATIVAS(
  F_COD_UNIDADE  BIGINT,
  F_COD_SETOR    BIGINT,
  F_STATUS_ATIVO BOOLEAN)
  RETURNS TABLE(
    CODIGO       BIGINT,
    ALTERNATIVA  TEXT,
    STATUS_ATIVO BOOLEAN)
LANGUAGE SQL
AS $$
SELECT
  RA.CODIGO,
  RA.ALTERNATIVA,
  RA.STATUS_ATIVO
FROM RELATO_ALTERNATIVA RA
WHERE
  COD_UNIDADE = F_COD_UNIDADE
  AND F_IF(F_COD_SETOR IS NOT NULL, F_COD_SETOR = COD_SETOR, TRUE)
  AND F_IF(F_STATUS_ATIVO IS NOT NULL, F_STATUS_ATIVO = STATUS_ATIVO, TRUE)
ORDER BY
  ALTERNATIVA ASC
$$;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--##################################### FUNC DE CÓPIA DE NOMENCLATURA DE PNEUS #########################################
--######################################################################################################################
--######################################################################################################################
CREATE OR REPLACE FUNCTION FUNC_PNEUS_COPIA_NOMENCLATURAS_ENTRE_UNIDADES(
      F_COD_UNIDADE_ORIGEM_COPIA_NOMENCLATURAS BIGINT,
      F_COD_UNIDADES_DESTINO_NOMENCLATURAS     BIGINT [],
  OUT AVISO_NOMENCLATURAS_COPIADAS             TEXT)
  RETURNS TEXT
LANGUAGE PLPGSQL
AS $$
BEGIN
  -- REMOVE UNIDADES DUPLICADAS DO ARRAY DE DESTINO.
  F_COD_UNIDADES_DESTINO_NOMENCLATURAS := ARRAY_DISTINCT(F_COD_UNIDADES_DESTINO_NOMENCLATURAS);

  -- VERIFICA SE A UNIDADE DE ORIGEM NÃO ESTÁ ENTRE AS DE DESTINO.
  IF F_COD_UNIDADE_ORIGEM_COPIA_NOMENCLATURAS = ANY (F_COD_UNIDADES_DESTINO_NOMENCLATURAS)
  THEN RAISE EXCEPTION 'O código da unidade de origem não pode constar nas unidades de destino!';
  END IF;

  -- VERIFICA SE TODAS AS UNIDADES DE DESTINO PERTENCEM A MESMA EMPRESA.
  IF (SELECT COUNT(DISTINCT U.COD_EMPRESA)
      FROM UNIDADE U
      WHERE U.CODIGO = ANY (F_COD_UNIDADES_DESTINO_NOMENCLATURAS)) > 1
  THEN RAISE EXCEPTION 'Só é possível copiar as nomenclaturas para unidades da mesma empresa!';
  END IF;

  -- VERIFICA SE A EMPRESA DA UNIDADE DE ORIGEM É A MESMA DAS UNIDADES DE DESTINO.
  IF (SELECT DISTINCT U.COD_EMPRESA
      FROM UNIDADE U
      WHERE U.CODIGO = ANY (F_COD_UNIDADES_DESTINO_NOMENCLATURAS)) != (SELECT U.COD_EMPRESA
                                                                       FROM UNIDADE U
                                                                       WHERE U.CODIGO =
                                                                             F_COD_UNIDADE_ORIGEM_COPIA_NOMENCLATURAS)
  THEN RAISE EXCEPTION 'A empresa da unidade de origem precisa ser a mesma das unidades de destino!';
  END IF;

  -- DELETA TODAS AS NOMENCLATURAS QUE EXISTAM NA UNIADDE DE DESTINO.
  DELETE FROM PNEU_ORDEM_NOMENCLATURA_UNIDADE PONU
  WHERE PONU.COD_UNIDADE = ANY (F_COD_UNIDADES_DESTINO_NOMENCLATURAS);

  -- COPIA AS NOMECLATURAS DA UNIDADE DE ORIGEM PARA TODAS AS DE DESTINO.
  WITH DIAGRAMAS AS (SELECT
                       DISTINCT ON (VT.COD_DIAGRAMA)
                       VT.CODIGO       AS COD_TIPO_VEICULO,
                       VT.COD_UNIDADE  AS COD_UNIDADE,
                       VT.COD_DIAGRAMA AS COD_DIAGRAMA
                     FROM VEICULO_TIPO VT
                     WHERE VT.COD_UNIDADE = F_COD_UNIDADE_ORIGEM_COPIA_NOMENCLATURAS
                     ORDER BY COD_DIAGRAMA, CODIGO
  ),
      NOMENCLATURAS AS (SELECT
                          D.COD_DIAGRAMA      AS COD_DIAGRAMA,
                          PONU.POSICAO_PROLOG AS POSICAO_PROLOG,
                          PONU.NOMENCLATURA   AS NOMENCLATURA
                        FROM DIAGRAMAS D
                          JOIN PNEU_ORDEM_NOMENCLATURA_UNIDADE PONU
                            ON PONU.COD_TIPO_VEICULO = D.COD_TIPO_VEICULO
                               AND PONU.COD_UNIDADE = D.COD_UNIDADE
    )

  INSERT INTO PNEU_ORDEM_NOMENCLATURA_UNIDADE (COD_TIPO_VEICULO, COD_UNIDADE, POSICAO_PROLOG, NOMENCLATURA)
    SELECT
      VT.CODIGO        AS COD_TIPO_VEICULO,
      VT.COD_UNIDADE   AS COD_UNIDADE,
      N.POSICAO_PROLOG AS POSICAO_PROLOG,
      N.NOMENCLATURA   AS NOMENCLATURA
    FROM NOMENCLATURAS N
      CROSS JOIN UNNEST(F_COD_UNIDADES_DESTINO_NOMENCLATURAS) T(COD_UNIDADE)
      JOIN VEICULO_TIPO VT
        ON VT.COD_UNIDADE = T.COD_UNIDADE
           AND N.COD_DIAGRAMA = VT.COD_DIAGRAMA
    ORDER BY
      T.COD_UNIDADE ASC,
      VT.CODIGO ASC;

  SELECT 'NOMENCLATURAS COPIADAS COM SUCESSO DA UNIDADE '
         || F_COD_UNIDADE_ORIGEM_COPIA_NOMENCLATURAS
         || ' PARA A(S) UNIDADE(S) '
         || ARRAY_TO_STRING(F_COD_UNIDADES_DESTINO_NOMENCLATURAS, ', ')
  INTO AVISO_NOMENCLATURAS_COPIADAS;
END;
$$;
--######################################################################################################################
--######################################################################################################################


--######################################################################################################################
--######################################################################################################################
--##################################### INSERE NA TABELA RELATO A VERSÃO DO APP ########################################
--######################################################################################################################
--######################################################################################################################
-- COM A VERSÃO DO APP NA TABELA RELATO IREMOS SABER COM QUAL VERSÃO O APP ESTAVA AO SINCRONIZAR O RELATO.
ALTER TABLE APP_VERSION ALTER COLUMN VERSION_CODE TYPE INTEGER;
ALTER TABLE RELATO ADD COLUMN VERSAO_APP INTEGER;
COMMENT ON COLUMN RELATO.VERSAO_APP IS
  'Optamos por não colocar FK para a tabela APP_VERSION para não correr o risco de algum relato não sincronizar por conta disso';
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
--##################################### FUNC DE RELATÓRIO DE KM RODADO POR PNEU E VIDA #################################
--######################################################################################################################
--######################################################################################################################
CREATE OR REPLACE FUNCTION FUNC_PNEU_RELATORIO_KM_RODADO_POR_VIDA(F_COD_UNIDADES BIGINT[])
  RETURNS TABLE(
    "UNIDADE ALOCADO"          TEXT,
    "PNEU"                     TEXT,
    "VIDA"                     INTEGER,
    "KM RODADO VIDA"           NUMERIC,
    "KM RODADO TODAS AS VIDAS" NUMERIC)
LANGUAGE SQL
AS $$
SELECT
  U.NOME                         AS UNIDADE_ALOCADO,
  P.CODIGO_CLIENTE               AS COD_PNEU,
  VP.VIDA_PNEU                   AS VIDA,
  VP.KM_RODADO_VIDA              AS KM_RODADO_VIDA,
  VP.TOTAL_KM_RODADO_TODAS_VIDAS AS KM_RODADO_TODAS_VIDAS
FROM VIEW_PNEU_KM_RODADO_TOTAL VP
  JOIN PNEU P
    ON P.CODIGO = VP.COD_PNEU
  JOIN UNIDADE U
    ON U.CODIGO = P.COD_UNIDADE
WHERE P.COD_UNIDADE = ANY (F_COD_UNIDADES)
ORDER BY U.CODIGO ASC, P.CODIGO_CLIENTE ASC, VP.VIDA_PNEU ASC;
$$;
--######################################################################################################################
--######################################################################################################################

END TRANSACTION ;