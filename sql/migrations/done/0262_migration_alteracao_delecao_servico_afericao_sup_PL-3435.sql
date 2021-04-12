drop function if exists func_garante_pneu_existe(f_cod_empresa bigint,
                                                 f_cod_unidade bigint,
                                                 f_cod_pneu bigint,
                                                 f_numero_fogo text);


CREATE OR REPLACE FUNCTION FUNC_GARANTE_PNEU_EXISTE(
  F_COD_EMPRESA BIGINT,
  F_COD_UNIDADE BIGINT,
  F_COD_PNEU    BIGINT)
  RETURNS VOID
LANGUAGE PLPGSQL
AS $$
BEGIN
  IF NOT EXISTS(SELECT P.CODIGO
                FROM PNEU P
                WHERE P.CODIGO = F_COD_PNEU AND P.COD_UNIDADE = F_COD_UNIDADE AND P.COD_EMPRESA = F_COD_EMPRESA)
  THEN RAISE EXCEPTION 'Pneu com código %, não existe na unidade % - %', F_COD_PNEU,
  F_COD_UNIDADE, (SELECT U.NOME
                  FROM UNIDADE U
                  WHERE U.CODIGO = F_COD_UNIDADE);
  END IF;
END;
$$;

drop function if exists SUPORTE.func_afericao_deleta_servico_afericao(f_cod_empresa bigint,
                                                                      f_cod_unidade bigint,
                                                                      f_cod_pneu bigint,
                                                                      f_numero_fogo text,
                                                                      f_codigo_afericao bigint,
                                                                      f_cod_servico_afericao bigint,
                                                                      f_tipo_servico_afericao text,
                                                                      f_motivo_delecao text,
                                                                      aviso_servico_afericao_deletado text);

CREATE OR REPLACE FUNCTION SUPORTE.FUNC_AFERICAO_DELETA_SERVICO_AFERICAO(F_COD_EMPRESA BIGINT,
                                                                         F_COD_UNIDADE BIGINT,
                                                                         F_COD_PNEU BIGINT,
                                                                         F_CODIGO_AFERICAO BIGINT,
                                                                         F_COD_SERVICO_AFERICAO BIGINT,
                                                                         F_TIPO_SERVICO_AFERICAO TEXT,
                                                                         F_MOTIVO_DELECAO TEXT,
                                                                         OUT AVISO_SERVICO_AFERICAO_DELETADO TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    F_TIPO_SERVICO_AFERICAO TEXT := LOWER(F_TIPO_SERVICO_AFERICAO);
BEGIN
    PERFORM SUPORTE.FUNC_HISTORICO_SALVA_EXECUCAO();
    --Garante integridade entre unidade e empresa
    PERFORM FUNC_GARANTE_INTEGRIDADE_EMPRESA_UNIDADE(F_COD_EMPRESA, F_COD_UNIDADE);
    --Verifica se o pneu existe
    PERFORM FUNC_GARANTE_PNEU_EXISTE(F_COD_EMPRESA, F_COD_UNIDADE, F_COD_PNEU);
    --Verifica se existe afericao
    IF NOT EXISTS(SELECT A.CODIGO
                  FROM AFERICAO A
                  WHERE A.CODIGO = F_CODIGO_AFERICAO
                    AND A.COD_UNIDADE = F_COD_UNIDADE)
    THEN
        RAISE EXCEPTION 'Aferição de código % não existe para a unidade % - %', F_CODIGO_AFERICAO, F_COD_UNIDADE,
            (SELECT NOME
             FROM UNIDADE
             WHERE CODIGO = F_COD_UNIDADE);
    END IF;
    --Verifica se existe serviço de afericao
    IF NOT EXISTS(SELECT AM.CODIGO
                  FROM AFERICAO_MANUTENCAO AM
                  WHERE AM.CODIGO = F_COD_SERVICO_AFERICAO
                    AND AM.COD_AFERICAO = F_CODIGO_AFERICAO
                    AND AM.COD_PNEU = F_COD_PNEU
                    AND AM.COD_UNIDADE = F_COD_UNIDADE
                    AND AM.TIPO_SERVICO = F_TIPO_SERVICO_AFERICAO)
    THEN
        RAISE EXCEPTION 'Não existe serviço de aferição com código: %, do tipo: "%", código de aferição: %,
     e codigo de pneus: %
                      para a unidade % - %', F_COD_SERVICO_AFERICAO, F_TIPO_SERVICO_AFERICAO,
            F_CODIGO_AFERICAO, F_COD_PNEU, F_COD_UNIDADE, (SELECT NOME
                                                           FROM UNIDADE
                                                           WHERE CODIGO = F_COD_UNIDADE);
    END IF;
    -- Deleta aferição manutenção.
    UPDATE AFERICAO_MANUTENCAO_DATA
    SET DELETADO            = TRUE,
        DATA_HORA_DELETADO  = NOW(),
        PG_USERNAME_DELECAO = SESSION_USER,
        MOTIVO_DELECAO      = F_MOTIVO_DELECAO
    WHERE CODIGO = F_COD_SERVICO_AFERICAO
      AND COD_AFERICAO = F_CODIGO_AFERICAO
      AND COD_UNIDADE = F_COD_UNIDADE
      AND COD_PNEU = F_COD_PNEU;
    SELECT 'SERVIÇO DE AFERIÇÃO DELETADO: '
               || F_COD_SERVICO_AFERICAO
               || ', DO TIPO: '
               || F_TIPO_SERVICO_AFERICAO
               || ', CODIGO DE AFERIÇÃO: '
               || F_CODIGO_AFERICAO
               || ', CÓDIGO PNEU: '
               || F_COD_PNEU
               || ', UNIDADE: '
               || F_COD_UNIDADE
               || ' - '
               || (SELECT U.NOME
                   FROM UNIDADE U
                   WHERE U.CODIGO = F_COD_UNIDADE)
    INTO AVISO_SERVICO_AFERICAO_DELETADO;
END
$$;

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

CREATE OR REPLACE FUNCTION SUPORTE.FUNC_PNEU_RETORNA_PNEU_DO_DESCARTE(F_COD_EMPRESA BIGINT,
                                                                      F_COD_UNIDADE BIGINT,
                                                                      F_COD_PNEU BIGINT,
                                                                      OUT AVISO_PNEU_RETORNADO TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    F_STATUS_PNEU_ESTOQUE       VARCHAR := 'ESTOQUE';
    F_STATUS_PNEU_DESCARTE      VARCHAR := 'DESCARTE';
    F_STATUS_PNEU_EM_USO        VARCHAR := 'EM_USO';
    F_STATUS_ORIGEM_PNEU        VARCHAR;
    F_STATUS_DESTINO_PNEU       VARCHAR;
    F_COD_MOVIMENTACAO_PROCESSO BIGINT;
    F_COD_MOVIMENTACAO          BIGINT;
BEGIN
    PERFORM SUPORTE.FUNC_HISTORICO_SALVA_EXECUCAO();
    -- VERIFICA SE UNIDADE EXISTE, SE EMPRESA EXISTE E SE UNIDADE PERTENCE A EMPRESA.
    PERFORM FUNC_GARANTE_INTEGRIDADE_EMPRESA_UNIDADE(F_COD_EMPRESA, F_COD_UNIDADE);

    -- VERIFICA SE PNEU EXISTE NA UNIDADE.
    PERFORM FUNC_GARANTE_PNEU_EXISTE(F_COD_EMPRESA, F_COD_UNIDADE, F_COD_PNEU);

    -- VERIFICA SE O STATUS DO PNEU ESTÁ COMO 'DESCARTE'.
    IF (SELECT P.STATUS
        FROM PNEU P WHERE P.CODIGO = F_COD_PNEU
          AND P.COD_UNIDADE = F_COD_UNIDADE) != F_STATUS_PNEU_DESCARTE
    THEN
        RAISE EXCEPTION 'Pneu com código % da unidade %, não está com status = %!',
             F_COD_PNEU, F_COD_UNIDADE, F_STATUS_PNEU_DESCARTE;
    END IF;

    -- VAI PEGAR AS INFORMAÇÕES DA ÚLTIMA MOVIMENTAÇÃO DO PNEU ASSUMINDO QUE FOI PARA DESCARTE.
    -- NA SEQUÊNCIA ISSO SERÁ VALIDADO.
    SELECT M.CODIGO                    AS COD_MOVIMENTACAO,
           M.COD_MOVIMENTACAO_PROCESSO AS COD_MOVIMENTACAO_PROCESSO,
           MO.TIPO_ORIGEM              AS TIPO_ORIGEM,
           MD.TIPO_DESTINO             AS TIPO_DESTINO
    FROM MOVIMENTACAO M
             JOIN MOVIMENTACAO_DESTINO MD ON M.CODIGO = MD.COD_MOVIMENTACAO
             JOIN MOVIMENTACAO_ORIGEM MO ON M.CODIGO = MO.COD_MOVIMENTACAO
    WHERE M.COD_PNEU = F_COD_PNEU
      AND M.COD_UNIDADE = F_COD_UNIDADE
    ORDER BY M.CODIGO DESC
    LIMIT 1
    INTO F_COD_MOVIMENTACAO, F_COD_MOVIMENTACAO_PROCESSO, F_STATUS_ORIGEM_PNEU, F_STATUS_DESTINO_PNEU;

    -- GARANTE QUE A ÚLTIMA MOVIMENTAÇÃO DO PNEU TENHA SIDO PARA DESCARTE.
    IF F_STATUS_DESTINO_PNEU != F_STATUS_PNEU_DESCARTE
    THEN
        RAISE EXCEPTION '[INCONSISTÊNCIA] A ultima movimentação do pneu com código % da unidade %,
      não foi para %!',
           F_COD_PNEU, F_COD_UNIDADE, F_STATUS_PNEU_DESCARTE;
    END IF;

    -- DELETA A MOVIMENTAÇÃO QUE MOVEU O PNEU PARA O DESCARTE SE A ORIGEM NÃO FOR 'EM_USO', CASO SEJA, MODIFICA
    -- DESTINO PARA 'ESTOQUE'.
    -- ISSO É FEITO PORQUE UM PNEU QUE ESTAVA APLICADO, NÃO PODE VOLTAR AO VEÍCULO, JÁ QUE PODERÍAMOS ESBARRAR NO CASO
    -- ONDE JÁ EXISTE OUTRO PNEU NA POSIÇÃO QUE ELE ESTAVA ANTES.
    IF F_STATUS_ORIGEM_PNEU != F_STATUS_PNEU_EM_USO
    THEN
        DELETE
        FROM MOVIMENTACAO M
        WHERE M.CODIGO = F_COD_MOVIMENTACAO
          AND M.COD_MOVIMENTACAO_PROCESSO = F_COD_MOVIMENTACAO_PROCESSO
          AND M.COD_PNEU = F_COD_PNEU
          AND M.COD_UNIDADE = F_COD_UNIDADE;
        -- VERIFICA SE MOVIMENTACAO ERA A UNICA EXISTENTE NO PROCESSO.
        IF NOT EXISTS(
                SELECT M.CODIGO FROM MOVIMENTACAO M WHERE M.COD_MOVIMENTACAO_PROCESSO = F_COD_MOVIMENTACAO_PROCESSO)
        THEN
            -- DELETA PROCESSO DE MOVIMENTACAO.
            DELETE FROM MOVIMENTACAO_PROCESSO WHERE CODIGO = F_COD_MOVIMENTACAO_PROCESSO;
        END IF;
    ELSE
        UPDATE MOVIMENTACAO_DESTINO
        SET TIPO_DESTINO          = F_STATUS_PNEU_ESTOQUE,
            COD_MOTIVO_DESCARTE   = NULL,
            URL_IMAGEM_DESCARTE_1 = NULL,
            URL_IMAGEM_DESCARTE_2 = NULL,
            URL_IMAGEM_DESCARTE_3 = NULL
        WHERE COD_MOVIMENTACAO = F_COD_MOVIMENTACAO;
    END IF;

    -- ALTERA STATUS DO PNEU.
    IF F_STATUS_ORIGEM_PNEU != F_STATUS_PNEU_EM_USO
    THEN
        UPDATE PNEU
        SET STATUS = F_STATUS_ORIGEM_PNEU
        WHERE CODIGO = F_COD_PNEU
          AND COD_UNIDADE = F_COD_UNIDADE
          AND COD_EMPRESA = F_COD_EMPRESA;
    ELSE
        UPDATE PNEU
        SET STATUS = F_STATUS_PNEU_ESTOQUE
        WHERE CODIGO = F_COD_PNEU
          AND COD_UNIDADE = F_COD_UNIDADE
          AND COD_EMPRESA = F_COD_EMPRESA;
    END IF;

    SELECT CONCAT('Pneu retornado para ', (SELECT P.STATUS FROM PNEU P WHERE P.CODIGO = F_COD_PNEU),
                  ', Código: ', F_COD_PNEU,
                  ', Unidade: ', F_COD_UNIDADE, ' - ', (SELECT U.NOME FROM UNIDADE U WHERE U.CODIGO = F_COD_UNIDADE),
                  ', Empresa: ', F_COD_EMPRESA, ' - ', (SELECT E.NOME FROM EMPRESA E WHERE E.CODIGO = F_COD_EMPRESA))
    INTO AVISO_PNEU_RETORNADO;
END;
$$;