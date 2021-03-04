-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Clona veículos de uma unidade para outra.
--
-- Histórico:
-- 2020-04-06 -> Function criada (thaisksf - PL-2034).
CREATE OR REPLACE FUNCTION INTERNO.FUNC_CLONA_VEICULOS(F_COD_EMPRESA_BASE BIGINT,
                                                       F_COD_UNIDADE_BASE BIGINT,
                                                       F_COD_EMPRESA_USUARIO BIGINT,
                                                       F_COD_UNIDADE_USUARIO BIGINT)
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_PLACA_PREFIXO_PADRAO          TEXT   := 'ZXY';
    V_PLACA_SUFIXO_PADRAO           BIGINT := 0;
    V_PLACA_VERIFICACAO             TEXT;
    V_PLACAS_VALIDAS_CADASTRO       TEXT[];
    V_TENTATIVA_BUSCAR_PLACA_VALIDA BIGINT := 0;

BEGIN
    -- VERIFICA SE EXISTEM MODELOS DE VEÍCULOS PARA COPIAR.
    IF NOT EXISTS(SELECT MV.CODIGO FROM MODELO_VEICULO MV WHERE MV.COD_EMPRESA = F_COD_EMPRESA_BASE)
    THEN
        RAISE EXCEPTION
            'Não existem modelos de veículos para serem copiados da empresa de código: %.' , F_COD_EMPRESA_BASE;
    END IF;

    -- VERIFICA SE EXISTEM TIPOS DE VEÍCULOS PARA COPIAR.
    IF NOT EXISTS(SELECT VT.CODIGO FROM VEICULO_TIPO VT WHERE VT.COD_EMPRESA = F_COD_EMPRESA_BASE)
    THEN
        RAISE EXCEPTION
            'Não existem tipos de veículos para serem copiados da empresa de código: %.' , F_COD_EMPRESA_BASE;
    END IF;

    -- VERIFICA SE EXISTEM VEÍCULOS PARA COPIAR.
    IF NOT EXISTS(SELECT VD.CODIGO FROM VEICULO_DATA VD WHERE VD.COD_UNIDADE = F_COD_UNIDADE_BASE)
    THEN
        RAISE EXCEPTION
            'Não existem veículos para serem copiados da unidade de código: %.' , F_COD_UNIDADE_BASE;
    END IF;

    -- COPIA OS MODELOS DE VEÍCULOS.
    INSERT INTO MODELO_VEICULO (NOME,
                                COD_MARCA,
                                COD_EMPRESA)
    SELECT MV.NOME,
           MV.COD_MARCA,
           F_COD_EMPRESA_USUARIO
    FROM MODELO_VEICULO MV
    WHERE MV.COD_EMPRESA = F_COD_EMPRESA_BASE
    ON CONFLICT ON CONSTRAINT NOMES_UNICOS_POR_EMPRESA_E_MARCA DO NOTHING;

    -- COPIA OS TIPOS DE VEÍCULOS.
    INSERT INTO VEICULO_TIPO(NOME,
                             STATUS_ATIVO,
                             COD_DIAGRAMA,
                             COD_EMPRESA)
    SELECT VT.NOME,
           VT.STATUS_ATIVO,
           VT.COD_DIAGRAMA,
           F_COD_EMPRESA_USUARIO
    FROM VEICULO_TIPO VT
    WHERE VT.COD_EMPRESA = F_COD_EMPRESA_BASE;

    --SELECIONA PLACAS VÁLIDAS PARA CADASTRO.
    WHILE
    ((ARRAY_LENGTH(V_PLACAS_VALIDAS_CADASTRO, 1) < (SELECT COUNT(VD.PLACA)
                                                    FROM VEICULO_DATA VD
                                                    WHERE VD.COD_UNIDADE = F_COD_UNIDADE_BASE)) OR
     (ARRAY_LENGTH(V_PLACAS_VALIDAS_CADASTRO, 1) IS NULL))
        LOOP
            --EXISTEM 10000 PLACAS DISPONÍVEIS PARA CADASTRO (DE ZXY0000 ATÉ ZXY9999),
            --CASO EXCEDA O NÚMERO DE TENTATIVAS - UM ERRO É MOSTRADO.
            IF (V_TENTATIVA_BUSCAR_PLACA_VALIDA = 10000)
            THEN
                RAISE EXCEPTION
                    'Não existem placas válidas para serem cadastradas';
            END IF;
            V_PLACA_VERIFICACAO := CONCAT(V_PLACA_PREFIXO_PADRAO, LPAD(V_PLACA_SUFIXO_PADRAO::TEXT, 4, '0'));
            IF NOT EXISTS(SELECT VD.PLACA FROM VEICULO_DATA VD WHERE VD.PLACA ILIKE V_PLACA_VERIFICACAO)
            THEN
                -- PLACAS VÁLIDAS PARA CADASTRO.
                V_PLACAS_VALIDAS_CADASTRO := ARRAY_APPEND(V_PLACAS_VALIDAS_CADASTRO, V_PLACA_VERIFICACAO);
            END IF;
            V_PLACA_SUFIXO_PADRAO := V_PLACA_SUFIXO_PADRAO + 1;
            V_TENTATIVA_BUSCAR_PLACA_VALIDA := V_TENTATIVA_BUSCAR_PLACA_VALIDA + 1;
        END LOOP;

    WITH PLACAS_VALIDAS_CADASTRO AS (
        SELECT ROW_NUMBER() OVER () AS CODIGO,
               VDN                  AS PLACA_CADASTRO
        FROM UNNEST(V_PLACAS_VALIDAS_CADASTRO) VDN),
         VEICULOS_BASE AS (
             SELECT ROW_NUMBER() OVER () AS CODIGO,
                    VD.PLACA             AS PLACA_BASE,
                    VD.KM                AS KM_BASE,
                    VD.COD_MODELO        AS MODELO_BASE,
                    VD.COD_TIPO          AS TIPO_BASE,
                    VD.COD_DIAGRAMA      AS COD_DIAGRAMA_BASE
             FROM VEICULO_DATA VD
             WHERE COD_UNIDADE = F_COD_UNIDADE_BASE
         ),
         DADOS_DE_PARA AS (
             SELECT DISTINCT ON (PVC.PLACA_CADASTRO, VB.PLACA_BASE) PVC.PLACA_CADASTRO   AS PLACA_CADASTRO,
                                                                    VB.PLACA_BASE        AS PLACA_BASE,
                                                                    VB.KM_BASE           AS KM_BASE,
                                                                    MVA.CODIGO           AS MODELO_BASE,
                                                                    MVN.CODIGO           AS MODELO_NOVO,
                                                                    VTA.CODIGO           AS TIPO_BASE,
                                                                    VTN.CODIGO           AS TIPO_NOVO,
                                                                    VB.COD_DIAGRAMA_BASE AS COD_DIAGRAMA_BASE
             FROM VEICULOS_BASE VB
                      JOIN MODELO_VEICULO MVA ON MVA.CODIGO = VB.MODELO_BASE
                      JOIN MODELO_VEICULO MVN ON MVA.NOME = MVN.NOME AND MVA.COD_MARCA = MVN.COD_MARCA
                      JOIN VEICULO_TIPO VTA ON VB.TIPO_BASE = VTA.CODIGO
                      JOIN VEICULO_TIPO VTN ON VTA.NOME = VTN.NOME AND VTA.COD_DIAGRAMA = VTN.COD_DIAGRAMA
                      JOIN PLACAS_VALIDAS_CADASTRO PVC ON PVC.CODIGO = VB.CODIGO
             WHERE MVA.COD_EMPRESA = F_COD_EMPRESA_BASE
               AND MVN.COD_EMPRESA = F_COD_EMPRESA_USUARIO
               AND VTA.COD_EMPRESA = F_COD_EMPRESA_BASE
               AND VTN.COD_EMPRESA = F_COD_EMPRESA_USUARIO)

         -- INSERE AS PLACAS DE->PARA.
    INSERT
    INTO VEICULO_DATA(PLACA,
                      COD_UNIDADE,
                      KM,
                      STATUS_ATIVO,
                      COD_TIPO,
                      COD_MODELO,
                      COD_EIXOS,
                      COD_UNIDADE_CADASTRO,
                      DELETADO,
                      COD_EMPRESA,
                      COD_DIAGRAMA)
    SELECT DDP.PLACA_CADASTRO,
           F_COD_UNIDADE_USUARIO,
           DDP.KM_BASE,
           TRUE,
           DDP.TIPO_NOVO,
           DDP.MODELO_NOVO,
           1,
           F_COD_UNIDADE_USUARIO,
           FALSE,
           F_COD_EMPRESA_USUARIO,
           DDP.COD_DIAGRAMA_BASE
    FROM DADOS_DE_PARA DDP;
END ;
$$;
