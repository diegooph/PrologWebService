CREATE OR REPLACE FUNCTION FUNC_PNEU_ATUALIZA(F_COD_CLIENTE TEXT,
                                              F_COD_MODELO BIGINT,
                                              F_COD_DIMENSAO BIGINT,
                                              F_COD_MODELO_BANDA BIGINT,
                                              F_DOT TEXT,
                                              F_VALOR NUMERIC,
                                              F_VIDA_TOTAL INT,
                                              F_PRESSAO_RECOMENDADA DOUBLE PRECISION,
                                              F_COD_ORIGINAL_PNEU BIGINT,
                                              F_COD_UNIDADE BIGINT)
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_COD_CLIENTE         TEXT;
    V_COD_MODELO          BIGINT;
    V_COD_DIMENSAO        BIGINT;
    V_COD_MODELO_BANDA    BIGINT;
    V_DOT                 TEXT;
    V_VALOR               NUMERIC;
    V_VIDA_TOTAL          INT;
    V_PRESSAO_RECOMENDADA DOUBLE PRECISION;
    V_COD_UNIDADE         BIGINT;
BEGIN
    SELECT CODIGO_CLIENTE,
           COD_MODELO,
           COD_DIMENSAO,
           COD_MODELO_BANDA,
           DOT,
           VALOR,
           VIDA_TOTAL,
           PRESSAO_RECOMENDADA,
           COD_UNIDADE
    INTO STRICT V_COD_CLIENTE,
        V_COD_MODELO,
        V_COD_DIMENSAO,
        V_COD_MODELO_BANDA,
        V_DOT,
        V_VALOR,
        V_VIDA_TOTAL,
        V_PRESSAO_RECOMENDADA,
        V_COD_UNIDADE
    FROM PNEU
    WHERE CODIGO = F_COD_ORIGINAL_PNEU;

    IF V_COD_CLIENTE != F_COD_CLIENTE
        OR V_COD_MODELO != F_COD_MODELO
        OR V_COD_DIMENSAO != F_COD_DIMENSAO
        OR V_COD_MODELO_BANDA != F_COD_MODELO_BANDA
        OR V_DOT != F_DOT
        OR V_VALOR != F_VALOR
        OR V_VIDA_TOTAL != F_VIDA_TOTAL
        OR V_PRESSAO_RECOMENDADA != F_PRESSAO_RECOMENDADA
        OR V_COD_UNIDADE != F_COD_UNIDADE
    THEN
        UPDATE PNEU
        SET CODIGO_CLIENTE      = F_COD_CLIENTE,
            COD_MODELO          = F_COD_MODELO,
            COD_DIMENSAO        = F_COD_DIMENSAO,
            COD_MODELO_BANDA    = F_COD_MODELO_BANDA,
            DOT                 = F_DOT,
            VALOR               = F_VALOR,
            VIDA_TOTAL          = F_VIDA_TOTAL,
            PRESSAO_RECOMENDADA = F_PRESSAO_RECOMENDADA
        WHERE CODIGO = F_COD_ORIGINAL_PNEU
          AND COD_UNIDADE = F_COD_UNIDADE;
    END IF;
END
$$;