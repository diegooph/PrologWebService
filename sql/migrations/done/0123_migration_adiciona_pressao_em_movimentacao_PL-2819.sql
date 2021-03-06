-- PL-2819
-- Adiciona a pressão atual do pneu na tabela de movimentação.
ALTER TABLE MOVIMENTACAO
    ADD COLUMN PRESSAO_ATUAL REAL;

-- Cria function para inserir movimentação
CREATE OR REPLACE FUNCTION FUNC_MOVIMENTACAO_INSERE_MOVIMENTACAO(F_COD_UNIDADE BIGINT,
                                                                 F_COD_MOVIMENTACAO_PROCESSO BIGINT,
                                                                 F_COD_PNEU BIGINT,
                                                                 F_OBSERVACAO TEXT)
    RETURNS BIGINT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_COD_MOVIMENTACAO_REALIZADA BIGINT;
BEGIN
    INSERT INTO MOVIMENTACAO(COD_UNIDADE,
                             COD_MOVIMENTACAO_PROCESSO,
                             COD_PNEU,
                             SULCO_INTERNO,
                             SULCO_CENTRAL_INTERNO,
                             SULCO_CENTRAL_EXTERNO,
                             SULCO_EXTERNO,
                             VIDA,
                             OBSERVACAO,
                             PRESSAO_ATUAL)
    SELECT F_COD_UNIDADE,
           F_COD_MOVIMENTACAO_PROCESSO,
           F_COD_PNEU,
           P.ALTURA_SULCO_INTERNO,
           P.ALTURA_SULCO_CENTRAL_INTERNO,
           P.ALTURA_SULCO_CENTRAL_EXTERNO,
           P.ALTURA_SULCO_EXTERNO,
           P.VIDA_ATUAL,
           F_OBSERVACAO,
           P.PRESSAO_ATUAL
    FROM PNEU P
    WHERE P.COD_UNIDADE = F_COD_UNIDADE
      AND P.CODIGO = F_COD_PNEU RETURNING CODIGO INTO V_COD_MOVIMENTACAO_REALIZADA;
    IF (V_COD_MOVIMENTACAO_REALIZADA <= 0)
    THEN
        PERFORM THROW_GENERIC_ERROR('Erro ao realizar movimentação');
    END IF;

    RETURN V_COD_MOVIMENTACAO_REALIZADA;
END
$$;
