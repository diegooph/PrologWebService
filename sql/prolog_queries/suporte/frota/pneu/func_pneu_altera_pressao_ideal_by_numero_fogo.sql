CREATE OR REPLACE FUNCTION SUPORTE.FUNC_PNEU_ALTERA_PRESSAO_IDEAL_BY_NUMERO_FOGO(F_COD_EMPRESA BIGINT,
                                                                                 F_COD_UNIDADE BIGINT,
                                                                                 F_NUMERO_FOGO TEXT,
                                                                                 F_NOVA_PRESSAO_RECOMENDADA BIGINT,
                                                                                 OUT AVISO_PRESSAO_ALTERADA TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    QTD_LINHAS_ATUALIZADAS     BIGINT;
    PRESSAO_MINIMA_RECOMENDADA BIGINT := 25;
    PRESSAO_MAXIMA_RECOMENDADA BIGINT := 150;
BEGIN
    PERFORM SUPORTE.FUNC_HISTORICO_SALVA_EXECUCAO();
    PERFORM FUNC_GARANTE_INTEGRIDADE_EMPRESA_UNIDADE(F_COD_EMPRESA, F_COD_UNIDADE);

    --Verifica se a pressao informada está dentro das recomendadas.
    IF (F_NOVA_PRESSAO_RECOMENDADA NOT BETWEEN PRESSAO_MINIMA_RECOMENDADA AND PRESSAO_MAXIMA_RECOMENDADA)
    THEN
        RAISE EXCEPTION 'Pressão recomendada não está dentro dos valores pré-estabelecidos.
                        Mínima Recomendada: % ---- Máxima Recomendada: %', PRESSAO_MINIMA_RECOMENDADA,
            PRESSAO_MAXIMA_RECOMENDADA;
    END IF;

    -- Verifica se existe o número de fogo informado.
    IF NOT EXISTS(SELECT PD.CODIGO
                  FROM PNEU PD
                  WHERE PD.CODIGO_CLIENTE = F_NUMERO_FOGO
                    AND PD.COD_EMPRESA = F_COD_EMPRESA)
    THEN
        RAISE EXCEPTION 'Número de fogo % não está cadastrado na empresa %!', F_NUMERO_FOGO, F_COD_EMPRESA;
    END IF;

    UPDATE PNEU
    SET PRESSAO_RECOMENDADA = F_NOVA_PRESSAO_RECOMENDADA
    WHERE CODIGO_CLIENTE = F_NUMERO_FOGO
      AND COD_UNIDADE = F_COD_UNIDADE
      AND COD_EMPRESA = F_COD_EMPRESA;

    GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

    IF (QTD_LINHAS_ATUALIZADAS <= 0)
    THEN
        RAISE EXCEPTION 'Erro ao atualizar a pressão recomendada com estes parâemtros:
                     Empresa %, Unidade %, Número de fogo %, Nova pressão %',
            F_COD_EMPRESA,
            F_COD_UNIDADE,
            F_NUMERO_FOGO,
            F_NOVA_PRESSAO_RECOMENDADA;
    END IF;

    SELECT CONCAT('Pressão recomendada do pneu com número de fogo ',
                  F_NUMERO_FOGO,
                  ' da empresa ',
                  F_COD_EMPRESA,
                  ' da unidade ',
                  F_COD_UNIDADE,
                  ' alterada para ',
                  F_NOVA_PRESSAO_RECOMENDADA,
                  ' psi')
    INTO AVISO_PRESSAO_ALTERADA;
END;
$$;