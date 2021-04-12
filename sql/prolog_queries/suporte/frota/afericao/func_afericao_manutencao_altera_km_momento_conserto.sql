CREATE OR REPLACE FUNCTION SUPORTE.FUNC_AFERICAO_MANUTENCAO_ALTERA_KM_MOMENTO_CONSERTO(F_COD_UNIDADE BIGINT,
                                                                                       F_COD_PNEU BIGINT,
                                                                                       F_COD_AFERICAO BIGINT,
                                                                                       F_COD_AFERICAO_MANUTENCAO BIGINT,
                                                                                       F_KM_MOMENTO_CONSERTO_ERRADO BIGINT,
                                                                                       F_KM_MOMENTO_CONSERTO_CORRETO BIGINT,
                                                                                       OUT AVISO_KM_AFERICAO_ALTERADO TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    V_COD_EMPRESA BIGINT  := (SELECT U.COD_EMPRESA
                              FROM UNIDADE U
                              WHERE U.CODIGO = F_COD_UNIDADE);
    V_QTD_UPDATES BIGINT;
BEGIN
    PERFORM SUPORTE.FUNC_HISTORICO_SALVA_EXECUCAO();
    -- VERIFICA SE ALGUMA INFORMAÇÃO É NULA
    IF ((F_COD_UNIDADE IS NULL) OR (F_COD_PNEU IS NULL) OR (F_COD_AFERICAO IS NULL) OR
        (F_COD_AFERICAO_MANUTENCAO IS NULL) OR (F_KM_MOMENTO_CONSERTO_ERRADO IS NULL) OR
        (F_KM_MOMENTO_CONSERTO_CORRETO IS NULL))
    THEN
        RAISE EXCEPTION 'Não é permitido valores nulos: Código unidade: % | Código pneu: % | Código aferição: % |'
            'Código aferição manutenção: % | Km errado: % | Km correto: %.', F_COD_UNIDADE, F_COD_PNEU, F_COD_AFERICAO,
            F_COD_AFERICAO_MANUTENCAO, F_KM_MOMENTO_CONSERTO_ERRADO, F_KM_MOMENTO_CONSERTO_CORRETO;
    END IF;

    -- VERIFICA SE KM ERRADO É IGUAL KM CORRETO
    IF (F_KM_MOMENTO_CONSERTO_ERRADO = F_KM_MOMENTO_CONSERTO_CORRETO)
    THEN
        RAISE EXCEPTION 'Não é possível atualizar pois o km errado (antigo) é igual ao km correto (novo). '
            'Km errado: % | Km correto: %.', F_KM_MOMENTO_CONSERTO_ERRADO, F_KM_MOMENTO_CONSERTO_CORRETO;
    END IF;

    -- VERIFICA SE UNIDADE EXISTE
    PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE);

    -- VERIFICA SE PNEU EXISTE
    PERFORM FUNC_GARANTE_PNEU_EXISTE(V_COD_EMPRESA, F_COD_UNIDADE, F_COD_PNEU);

    -- VERIFICA SE A AFERIÇÃO EXISTE NA UNIDADE INFORMADA.
    IF NOT EXISTS(SELECT AM.CODIGO
                  FROM AFERICAO AM
                  WHERE AM.CODIGO = F_COD_AFERICAO
                    AND AM.COD_UNIDADE = F_COD_UNIDADE)
    THEN
        RAISE EXCEPTION 'Não foi possível encontrar a aferição realizada com estes parâmetros: Código aferição: %
            Código da unidade: %', F_COD_AFERICAO, F_COD_UNIDADE;
    END IF;

    -- VERIFICA SE A AFERIÇÃO MANUTENÇÃO EXISTE NA UNIDADE INFORMADA.
    IF NOT EXISTS(SELECT CODIGO
                  FROM AFERICAO_MANUTENCAO AM
                  WHERE AM.CODIGO = F_COD_AFERICAO_MANUTENCAO
                    AND COD_UNIDADE = F_COD_UNIDADE)
    THEN
        RAISE EXCEPTION 'Não foi possível encontrar a aferição realizada com estes parâmetros: Código aferição '
            'manutenção: % Código da unidade: %', F_COD_AFERICAO_MANUTENCAO, F_COD_UNIDADE;
    END IF;

    -- VERIFICA SE A AFERIÇÃO MANUTENÇÃO PERTENCE A AFERIÇÃO INFORMADA.
    IF NOT EXISTS(SELECT AM.CODIGO
                  FROM AFERICAO_MANUTENCAO AM
                  WHERE AM.CODIGO = F_COD_AFERICAO_MANUTENCAO
                    AND AM.COD_AFERICAO = F_COD_AFERICAO)
    THEN
        RAISE EXCEPTION 'Não foi possível encontrar a aferição realizada com estes parâmetros: Código aferição '
            'manutenção: % Código da aferição: %', F_COD_AFERICAO_MANUTENCAO, F_COD_AFERICAO;
    END IF;

    -- VERIFICA SE O SERVIÇO É DA AFERIÇÃO INFORMADA
    IF NOT EXISTS(SELECT AM.CODIGO
                  FROM AFERICAO_MANUTENCAO AM
                  WHERE AM.CODIGO = F_COD_AFERICAO_MANUTENCAO
                    AND AM.COD_AFERICAO = F_COD_AFERICAO)
    THEN
        RAISE EXCEPTION 'Não foi possível encontrar a aferição realizada com estes parâmetros: Código aferição '
            'manutenção: % Código da aferição: %', F_COD_AFERICAO_MANUTENCAO, F_COD_AFERICAO;
    END IF;

    -- VERIFICA SE O PNEU INFORMADO PERTENCE A AFERIÇÃO MANUTENÇÃO INFORMADA.
    IF NOT EXISTS(SELECT AM.CODIGO
                  FROM AFERICAO_MANUTENCAO AM
                  WHERE AM.CODIGO = F_COD_AFERICAO_MANUTENCAO
                    AND AM.COD_PNEU = F_COD_PNEU)
    THEN
        RAISE EXCEPTION 'Não foi possível encontrar a aferição realizada com estes parâmetros: Código aferição '
            'manutenção: % Código do pneu: %', F_COD_AFERICAO_MANUTENCAO, F_COD_PNEU;
    END IF;

    -- VERIFICA SE A KM INFORMADA COMO ERRADA BATE COM A KM DA AFERICAO MANUTENCAO INFORMADA
    IF ((SELECT AM.KM_MOMENTO_CONSERTO
         FROM AFERICAO_MANUTENCAO AM
         WHERE AM.CODIGO = F_COD_AFERICAO_MANUTENCAO) <> F_KM_MOMENTO_CONSERTO_ERRADO)
    THEN
        RAISE EXCEPTION 'Não foi possível encontrar a aferição realizada com estes parâmetros: Código aferição '
            'manutenção: % Km no momento do conserto: %', F_COD_AFERICAO_MANUTENCAO, F_KM_MOMENTO_CONSERTO_ERRADO;
    END IF;

    UPDATE AFERICAO_MANUTENCAO_DATA
    SET KM_MOMENTO_CONSERTO = F_KM_MOMENTO_CONSERTO_CORRETO
    WHERE COD_AFERICAO = F_COD_AFERICAO
      AND COD_UNIDADE = F_COD_UNIDADE
      AND CODIGO = F_COD_AFERICAO_MANUTENCAO
      AND COD_PNEU = F_COD_PNEU
      AND KM_MOMENTO_CONSERTO = F_KM_MOMENTO_CONSERTO_ERRADO;

    GET DIAGNOSTICS V_QTD_UPDATES = ROW_COUNT;

    IF V_QTD_UPDATES <= 0
    THEN
        RAISE EXCEPTION 'Erro ao realizar a modificação de km.';
    ELSE
        SELECT 'O KM DO VEÍCULO NO SERVIÇO DE AFERIÇÃO FOI ALTERADO COM SUCESSO'
                   || ', UNIDADE: ' || F_COD_UNIDADE
                   || ', CODIGO PNEU: ' || F_COD_PNEU
                   || ', CODIGO AFERICAO: ' || F_COD_AFERICAO
                   || ', CODIGO AFERICAO MANUTENCAO: ' || F_COD_AFERICAO_MANUTENCAO
                   || ', KM ERRADO: ' || F_KM_MOMENTO_CONSERTO_ERRADO
                   || ', KM CORRETO: ' || F_KM_MOMENTO_CONSERTO_CORRETO || '.'

        INTO AVISO_KM_AFERICAO_ALTERADO;
    END IF;
END ;
$$;