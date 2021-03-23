CREATE OR REPLACE FUNCTION INTERNO.FUNC_CLONA_PNEUS(F_COD_EMPRESA_BASE BIGINT,
                                                    F_COD_UNIDADE_BASE BIGINT,
                                                    F_COD_EMPRESA_USUARIO BIGINT,
                                                    F_COD_UNIDADE_USUARIO BIGINT)
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    -- VERIFICA SE EXISTEM MODELOS DE PNEUS PARA COPIAR.
    IF NOT EXISTS(SELECT MP.CODIGO FROM MODELO_PNEU MP WHERE MP.COD_EMPRESA = F_COD_EMPRESA_BASE)
    THEN
        RAISE EXCEPTION
            'NÃO EXISTEM MODELOS DE PNEUS PARA SEREM COPIADOS DA EMPRESA DE CÓDIGO: %.' , F_COD_EMPRESA_BASE;
    END IF;

    -- VERIFICA SE EXISTEM MARCAS DE BANDA PARA COPIAR.
    IF NOT EXISTS(SELECT MAB.CODIGO FROM MARCA_BANDA MAB WHERE MAB.COD_EMPRESA = F_COD_EMPRESA_BASE)
    THEN
        RAISE EXCEPTION
            'NÃO EXISTEM MARCAS DE BANDAS PARA SEREM COPIADOS DA EMPRESA DE CÓDIGO: %.' , F_COD_EMPRESA_BASE;
    END IF;

    -- VERIFICA SE EXISTEM MODELOS DE BANDA PARA COPIAR.
    IF NOT EXISTS(SELECT MOB.CODIGO FROM MODELO_BANDA MOB WHERE MOB.COD_EMPRESA = F_COD_EMPRESA_BASE)
    THEN
        RAISE EXCEPTION
            'NÃO EXISTEM MODELOS DE BANDA PARA SEREM COPIADOS DA EMPRESA DE CÓDIGO: %.' , F_COD_EMPRESA_BASE;
    END IF;

    -- VERIFICA SE EXISTEM PNEUS PARA COPIAR.
    IF NOT EXISTS(SELECT PD.CODIGO FROM PNEU_DATA PD WHERE PD.COD_UNIDADE = F_COD_UNIDADE_BASE)
    THEN
        RAISE EXCEPTION
            'NÃO EXISTEM PNEUS PARA SEREM COPIADOS DA UNIDADE DE CÓDIGO: %.' , F_COD_UNIDADE_BASE;
    END IF;

    -- COPIA OS MODELOS DE PNEUS.
    INSERT INTO MODELO_PNEU (NOME, COD_MARCA, COD_EMPRESA, QT_SULCOS, ALTURA_SULCOS)
    SELECT MP.NOME,
           MP.COD_MARCA,
           F_COD_EMPRESA_USUARIO,
           MP.QT_SULCOS,
           MP.ALTURA_SULCOS
    FROM MODELO_PNEU MP
    WHERE MP.COD_EMPRESA = F_COD_EMPRESA_BASE;

    -- COPIA AS MARCAS DE BANDAS.
    INSERT INTO MARCA_BANDA(NOME, COD_EMPRESA)
    SELECT MAB.NOME,
           F_COD_EMPRESA_USUARIO
    FROM MARCA_BANDA MAB
    WHERE MAB.COD_EMPRESA = F_COD_EMPRESA_BASE
    ON CONFLICT ON CONSTRAINT UNIQUE_MARCA_BANDA
        DO NOTHING;

    -- REALIZA O DE -> PARA DOS CÓDIGOS DE MARCAS DE BANDA E INSERE OS MODELOS
    WITH DADOS_MARCA_BANDA_DE_PARA AS (
        SELECT MABB.CODIGO       AS COD_MARCA_BANDA_BASE,
               MABB.NOME         AS NOME_MARCA_BANDA_BASE,
               MABN.CODIGO       AS COD_MARCA_BANDA_NOVO,
               MABN.CODIGO       AS NOME_MARCA_BANDA_NOVO,
               MOB.NOME          AS NOME_MODELO_BANDA_BASE,
               MOB.ALTURA_SULCOS AS ALTURA_SULCOS_BANDA_BASE,
               MOB.QT_SULCOS     AS QT_SULCOS_BANDA_BASE
        FROM MARCA_BANDA MABB
                 JOIN MARCA_BANDA MABN ON MABB.NOME = MABN.NOME
                 JOIN MODELO_BANDA MOB ON MABB.CODIGO = MOB.COD_MARCA AND MABB.COD_EMPRESA = MOB.COD_EMPRESA
        WHERE MABB.COD_EMPRESA = F_COD_EMPRESA_BASE
          AND MABN.COD_EMPRESA = F_COD_EMPRESA_USUARIO)

         -- REALIZA A CLONAGEM DE MODELOS DE BANDA COM O CÓDIGO DAS MARCAS DE->PARA.
    INSERT
    INTO MODELO_BANDA(NOME, COD_MARCA, COD_EMPRESA, QT_SULCOS, ALTURA_SULCOS)
    SELECT DMBDP.NOME_MODELO_BANDA_BASE,
           DMBDP.COD_MARCA_BANDA_NOVO,
           F_COD_EMPRESA_USUARIO,
           DMBDP.QT_SULCOS_BANDA_BASE,
           DMBDP.ALTURA_SULCOS_BANDA_BASE
    FROM DADOS_MARCA_BANDA_DE_PARA DMBDP
    ON CONFLICT ON CONSTRAINT UNIQUE_NOME_MODELO_BANDA_POR_MARCA
        DO NOTHING;

    -- DADOS DE PARA
    WITH PNEUS_BASE AS (
        SELECT PD.CODIGO_CLIENTE               AS NUMERO_FOGO_BASE,
               PD.COD_MODELO                   AS COD_MODELO_PNEU_BASE,
               PD.COD_DIMENSAO                 AS COD_DIMENSAO_BASE,
               PD.PRESSAO_RECOMENDADA          AS PRESSAO_RECOMENDADA_BASE,
               PD.PRESSAO_ATUAL                AS PRESSAO_ATUAL_BASE,
               PD.ALTURA_SULCO_INTERNO         AS ALTURA_SULCO_INTERNO_BASE,
               PD.ALTURA_SULCO_CENTRAL_INTERNO AS ALTURA_SULCO_CENTRAL_INTERNO_BASE,
               PD.ALTURA_SULCO_EXTERNO         AS ALTURA_SULCO_EXTERNO_BASE,
               PD.STATUS                       AS STATUS_BASE,
               PD.VIDA_ATUAL                   AS VIDA_ATUAL_BASE,
               PD.VIDA_TOTAL                   AS VIDA_TOTAL_BASE,
               PD.COD_MODELO_BANDA             AS COD_MODELO_BANDA_BASE,
               PD.ALTURA_SULCO_CENTRAL_EXTERNO AS ALTURA_SULCO_CENTRAL_EXTERNO_BASE,
               PD.DOT                          AS DOT_BASE,
               PD.VALOR                        AS VALOR_BASE
        FROM PNEU PD
        WHERE COD_UNIDADE = F_COD_UNIDADE_BASE
    ),
         DADOS_DE_PARA AS (
             SELECT DISTINCT ON (PB.NUMERO_FOGO_BASE) PB.NUMERO_FOGO_BASE,
                                                      PB.COD_MODELO_PNEU_BASE,
                                                      MPB.CODIGO  AS COD_MODELO_PNEU_BASE,
                                                      MPN.CODIGO  AS COD_MODELO_PNEU_NOVO,
                                                      PB.COD_DIMENSAO_BASE,
                                                      PB.PRESSAO_RECOMENDADA_BASE,
                                                      PB.PRESSAO_ATUAL_BASE,
                                                      PB.ALTURA_SULCO_INTERNO_BASE,
                                                      PB.ALTURA_SULCO_CENTRAL_INTERNO_BASE,
                                                      PB.ALTURA_SULCO_EXTERNO_BASE,
                                                      PB.STATUS_BASE,
                                                      PB.VIDA_ATUAL_BASE,
                                                      PB.VIDA_TOTAL_BASE,
                                                      PB.COD_MODELO_BANDA_BASE,
                                                      MABB.NOME   AS NOME_MARCA_BANDA_BASE,
                                                      MABN.NOME   AS NOME_MARCA_BANSA_NOVA,
                                                      MABB.CODIGO AS COD_MARCA_BANDA_BASE,
                                                      MABN.CODIGO AS COD_MARCA_BANDA_NOVA,
                                                      MOBB.CODIGO AS COD_MODELO_BANDA_BASE,
                                                      MOBN.CODIGO AS COD_MODELO_BANDA_NOVO,
                                                      PB.ALTURA_SULCO_CENTRAL_EXTERNO_BASE,
                                                      PB.DOT_BASE,
                                                      PB.VALOR_BASE
             FROM PNEUS_BASE PB
                      JOIN MODELO_PNEU MPB
                           ON MPB.CODIGO = PB.COD_MODELO_PNEU_BASE AND MPB.COD_EMPRESA = F_COD_EMPRESA_BASE
                      JOIN MODELO_PNEU MPN
                           ON MPB.NOME = MPN.NOME AND MPB.COD_MARCA = MPN.COD_MARCA AND
                              MPN.COD_EMPRESA = F_COD_EMPRESA_USUARIO
                      LEFT JOIN MODELO_BANDA MOBB
                                ON PB.COD_MODELO_BANDA_BASE = MOBB.CODIGO AND MOBB.COD_EMPRESA = F_COD_EMPRESA_BASE
                      LEFT JOIN MODELO_BANDA MOBN ON MOBB.NOME = MOBN.NOME AND MOBN.COD_EMPRESA = F_COD_EMPRESA_USUARIO
                      LEFT JOIN MARCA_BANDA MABB ON MABB.CODIGO = MOBB.COD_MARCA
                      LEFT JOIN MARCA_BANDA MABN ON MABN.CODIGO = MOBN.COD_MARCA AND MABB.NOME = MABN.NOME)

         -- REALIZA A CLONAGEM DE PNEUS
    INSERT
    INTO PNEU_DATA (CODIGO_CLIENTE,
                    COD_MODELO,
                    COD_DIMENSAO,
                    PRESSAO_RECOMENDADA,
                    PRESSAO_ATUAL,
                    ALTURA_SULCO_INTERNO,
                    ALTURA_SULCO_CENTRAL_INTERNO,
                    ALTURA_SULCO_EXTERNO,
                    COD_UNIDADE,
                    STATUS,
                    VIDA_ATUAL,
                    VIDA_TOTAL,
                    COD_MODELO_BANDA,
                    ALTURA_SULCO_CENTRAL_EXTERNO,
                    DOT,
                    VALOR,
                    COD_EMPRESA,
                    COD_UNIDADE_CADASTRO)
    SELECT DDP.NUMERO_FOGO_BASE,
           DDP.COD_MODELO_PNEU_NOVO,
           DDP.COD_DIMENSAO_BASE,
           DDP.PRESSAO_RECOMENDADA_BASE,
           DDP.PRESSAO_ATUAL_BASE,
           DDP.ALTURA_SULCO_INTERNO_BASE,
           DDP.ALTURA_SULCO_CENTRAL_INTERNO_BASE,
           DDP.ALTURA_SULCO_EXTERNO_BASE,
           F_COD_UNIDADE_USUARIO,
           DDP.STATUS_BASE,
           DDP.VIDA_ATUAL_BASE,
           DDP.VIDA_TOTAL_BASE,
           DDP.COD_MODELO_BANDA_NOVO,
           DDP.ALTURA_SULCO_CENTRAL_EXTERNO_BASE,
           DDP.DOT_BASE,
           DDP.VALOR_BASE,
           F_COD_EMPRESA_USUARIO,
           F_COD_UNIDADE_USUARIO
    FROM DADOS_DE_PARA DDP;
END;
$$;