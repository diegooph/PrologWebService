-- Sobre:
--
-- Function para atualizar um tipo de veículo
--
-- Histórico:
-- 2020-03-05-> Atualização de arquivo e documentação (wvinim - PL-2560).
-- 2020-03-05-> Adiciona código auxiliar (wvinim - PL-2560).
CREATE OR REPLACE FUNCTION FUNC_VEICULO_UPDATE_TIPO_VEICULO(F_COD_TIPO_VEICULO BIGINT,
                                                            F_NOME_TIPO_VEICULO TEXT,
                                                            F_COD_DIAGRAMA_TIPO_VEICULO BIGINT,
                                                            F_COD_AUXILIAR TEXT)
    RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_COD_DIAGRAMA_ATUAL_ASSOCIADO BIGINT := (SELECT VT.COD_DIAGRAMA
                                              FROM VEICULO_TIPO VT
                                              WHERE VT.CODIGO = F_COD_TIPO_VEICULO);
    V_QTD_LINHAS_ATUALIZADAS       BIGINT;
BEGIN
    -- Se não tem diagrama associado, podemos setar o recebido no update.
    IF (V_COD_DIAGRAMA_ATUAL_ASSOCIADO IS NULL OR V_COD_DIAGRAMA_ATUAL_ASSOCIADO <= 0)
    THEN
        UPDATE VEICULO_TIPO
        SET NOME         = F_NOME_TIPO_VEICULO,
            COD_DIAGRAMA = F_COD_DIAGRAMA_TIPO_VEICULO,
            COD_AUXILIAR = F_COD_AUXILIAR
        WHERE CODIGO = F_COD_TIPO_VEICULO;
        -- Se tem diagrama associado, e o recebido é o mesmo do já associado, atualizamos o nome e o código auxiliar.
    ELSEIF (V_COD_DIAGRAMA_ATUAL_ASSOCIADO IS NOT NULL
        AND V_COD_DIAGRAMA_ATUAL_ASSOCIADO >= 0
        AND V_COD_DIAGRAMA_ATUAL_ASSOCIADO = F_COD_DIAGRAMA_TIPO_VEICULO)
    THEN
        UPDATE VEICULO_TIPO
        SET NOME         = F_NOME_TIPO_VEICULO,
            COD_AUXILIAR = F_COD_AUXILIAR
        WHERE CODIGO = F_COD_TIPO_VEICULO;
        -- Se tem diagrama associado, e o recebido é DIFERENTE do já associado, lançamos um erro, atualmente não é
        -- possível alterar o diagrama pelo front end pois isso pode impactar os pneus que estão vinculados.
    ELSEIF (V_COD_DIAGRAMA_ATUAL_ASSOCIADO IS NOT NULL
        AND V_COD_DIAGRAMA_ATUAL_ASSOCIADO >= 0
        AND V_COD_DIAGRAMA_ATUAL_ASSOCIADO <> F_COD_DIAGRAMA_TIPO_VEICULO)
    THEN
        PERFORM THROW_GENERIC_ERROR('Não é permitido alterar o diagrama de um tipo de veículo');
    END IF;

    GET DIAGNOSTICS V_QTD_LINHAS_ATUALIZADAS = ROW_COUNT;
    -- Verificamos se o update funcionou.
    IF V_QTD_LINHAS_ATUALIZADAS <= 0
    THEN
        RAISE EXCEPTION 'Erro ao atualizar o tipo de veículo';
    END IF;

    RETURN V_QTD_LINHAS_ATUALIZADAS;
END;
$$;