create table if not exists suporte.historico_uso_function
(
    codigo               bigserial                not null,
    function_query       text                     not null,
    data_hora_execucao   timestamp with time zone not null,
    pg_username_execucao text                     not null,
    constraint pk_historico_usos_functions primary key (codigo)
);

create or replace function suporte.func_historico_salva_execucao()
    returns void
    security definer
    language sql
as
$$



insert into suporte.historico_uso_function (function_query,
                                            data_hora_execucao,
                                            pg_username_execucao)
values (current_query(),
        now(),
        session_user)
$$;


-------------------------------------------FUNCTIONS-----------------------------------------
CREATE OR REPLACE FUNCTION SUPORTE.FUNC_AFERICAO_ALTERA_KM_COLETADO_AFERICAO(F_COD_UNIDADE BIGINT,
                                                                             F_PLACA TEXT,
                                                                             F_COD_AFERICAO BIGINT,
                                                                             F_NOVO_KM BIGINT,
                                                                             OUT AVISO_KM_AFERICAO_ALTERADO TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    QTD_LINHAS_ATUALIZADAS BIGINT;
BEGIN
    PERFORM SUPORTE.FUNC_HISTORICO_SALVA_EXECUCAO();
    PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE);
    PERFORM FUNC_GARANTE_VEICULO_EXISTE(F_COD_UNIDADE, F_PLACA);
    PERFORM FUNC_GARANTE_NOVO_KM_MENOR_QUE_ATUAL_VEICULO(F_COD_UNIDADE, F_PLACA, F_NOVO_KM);

    -- Verifica se aferição existe.
    IF NOT EXISTS(SELECT AF.CODIGO
                  FROM AFERICAO AF
                  WHERE AF.PLACA_VEICULO IS NOT NULL
                    AND AF.PLACA_VEICULO = F_PLACA
                    AND AF.COD_UNIDADE = F_COD_UNIDADE
                    AND AF.CODIGO = F_COD_AFERICAO)
    THEN
        RAISE EXCEPTION 'Não foi possível encontrar a aferição realizada com estes parâmetros: Unidade %, Placa %,
                     Código da aferição %', F_COD_UNIDADE, F_PLACA, F_COD_AFERICAO;
    END IF;

    UPDATE AFERICAO
    SET KM_VEICULO = F_NOVO_KM
    WHERE CODIGO = F_COD_AFERICAO
      AND COD_UNIDADE = F_COD_UNIDADE
      AND PLACA_VEICULO = F_PLACA;

    GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

    IF (QTD_LINHAS_ATUALIZADAS <= 0)
    THEN
        RAISE EXCEPTION 'Erro ao atualizar o km da aferição com estes parâemtros: Unidade %, Placa %, Código
            da aferição %', F_COD_UNIDADE, F_PLACA, F_COD_AFERICAO;
    END IF;

    SELECT 'O KM DO VEÍCULO NA AFERIÇÃO FOI ALTERADO COM SUCESSO, UNIDADE: '
               || F_COD_UNIDADE
               || ', PLACA: '
               || F_PLACA
               || ', CÓDIGO DA AFERIÇÃO: '
               || F_COD_AFERICAO
    INTO AVISO_KM_AFERICAO_ALTERADO;
END;
$$;


CREATE OR REPLACE FUNCTION SUPORTE.FUNC_AFERICAO_DELETA_AFERICAO(F_COD_UNIDADE BIGINT,
                                                                 F_PLACA TEXT,
                                                                 F_CODIGO_AFERICAO BIGINT,
                                                                 F_MOTIVO_DELECAO TEXT,
                                                                 OUT AVISO_AFERICAO_DELETADA TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    QTD_LINHAS_ATUALIZADAS BIGINT;
BEGIN
    PERFORM SUPORTE.FUNC_HISTORICO_SALVA_EXECUCAO();
    IF ((SELECT COUNT(codigo)
         FROM AFERICAO_DATA
         WHERE CODIGO = F_CODIGO_AFERICAO
           AND COD_UNIDADE = F_COD_UNIDADE
           AND PLACA_VEICULO = F_PLACA) <= 0)
    THEN
        RAISE EXCEPTION 'Nenhuma aferição encontrada com estes parâmetros: Unidade %, Placa % e Código %',
            F_COD_UNIDADE, F_PLACA, F_CODIGO_AFERICAO;
    END IF;

    -- DELETA AFERIÇÃO.
    UPDATE AFERICAO_DATA
    SET DELETADO            = TRUE,
        DATA_HORA_DELETADO  = NOW(),
        PG_USERNAME_DELECAO = SESSION_USER,
        MOTIVO_DELECAO = F_MOTIVO_DELECAO
    WHERE COD_UNIDADE = F_COD_UNIDADE
      AND PLACA_VEICULO = F_PLACA
      AND CODIGO = F_CODIGO_AFERICAO
      AND DELETADO = FALSE;

    GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

    IF (QTD_LINHAS_ATUALIZADAS <= 0)
    THEN
        RAISE EXCEPTION 'Erro ao deletar o aferição de unidade: %, placa: % e código: %',
            F_COD_UNIDADE, F_PLACA, F_CODIGO_AFERICAO;
    END IF;

    -- DELETA AFERIÇÃO VALORES.
    UPDATE AFERICAO_VALORES_DATA
    SET DELETADO            = TRUE,
        DATA_HORA_DELETADO  = NOW(),
        PG_USERNAME_DELECAO = SESSION_USER,
        MOTIVO_DELECAO = F_MOTIVO_DELECAO
    WHERE COD_UNIDADE = F_COD_UNIDADE
      AND COD_AFERICAO = F_CODIGO_AFERICAO
      AND DELETADO = FALSE;

    GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

    -- SE TEM AFERIÇÃO, TAMBÉM DEVERÁ CONTER VALORES DE AFERIÇÃO, ENTÃO DEVE-SE VERIFICAR.
    IF ((QTD_LINHAS_ATUALIZADAS <= 0) AND ((SELECT COUNT(*)
                                            FROM AFERICAO_VALORES_DATA AVD
                                            WHERE AVD.COD_UNIDADE = F_COD_UNIDADE
                                              AND AVD.COD_AFERICAO = F_CODIGO_AFERICAO) > 0))
    THEN
        RAISE EXCEPTION 'Erro ao deletar os valores de  aferição de unidade: %, placa: % e código: %',
            F_COD_UNIDADE, F_PLACA, F_CODIGO_AFERICAO;
    END IF;

    -- DELETA AFERIÇÃO MANUTENÇÃO.
    -- Não verificamos quantas linhas atualizadas pois aferição pode não ter manutenções.
    UPDATE AFERICAO_MANUTENCAO_DATA
    SET DELETADO            = TRUE,
        DATA_HORA_DELETADO  = NOW(),
        PG_USERNAME_DELECAO = SESSION_USER,
        MOTIVO_DELECAO = F_MOTIVO_DELECAO
    WHERE COD_UNIDADE = F_COD_UNIDADE
      AND COD_AFERICAO = F_CODIGO_AFERICAO;

    SELECT 'AFERIÇÃO DELETADA: '
               || F_CODIGO_AFERICAO
               || ', PLACA: '
               || F_PLACA
               || ', CÓDIGO DA UNIDADE: '
               || F_COD_UNIDADE
    INTO AVISO_AFERICAO_DELETADA;
END;
$$;


CREATE OR REPLACE FUNCTION SUPORTE.FUNC_AFERICAO_DELETA_AFERICAO_VALORES(F_COD_UNIDADE BIGINT,
                                                                         F_CODIGO_PNEU BIGINT,
                                                                         F_CODIGO_AFERICAO BIGINT,
                                                                         F_MOTIVO_DELECAO TEXT,
                                                                         OUT AVISO_AFERICAO_VALOR_DELETADA TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    V_QTD_LINHAS_ATUALIZADAS   BIGINT;
    -- Busca a quantidade de valores aferidos estão ativos nesta aferição
    V_QTD_VALORES_AFERICAO     BIGINT := (SELECT COUNT(*)
                                          FROM AFERICAO_VALORES
                                          WHERE COD_AFERICAO = F_CODIGO_AFERICAO
                                            AND COD_UNIDADE = F_COD_UNIDADE);

    -- Variável utilizada para melhorar o feedback da function de acordo com o fluxo
    V_PREFIXO_MENSAGEM_RETORNO TEXT;
    V_PLACA                    TEXT   := (SELECT A.PLACA_VEICULO
                                          FROM AFERICAO A
                                          WHERE A.CODIGO = F_CODIGO_AFERICAO);
BEGIN
    PERFORM SUPORTE.FUNC_HISTORICO_SALVA_EXECUCAO();
    PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE);

    IF NOT EXISTS(SELECT *
                  FROM AFERICAO_VALORES
                  WHERE COD_AFERICAO = F_CODIGO_AFERICAO
                    AND COD_UNIDADE = F_COD_UNIDADE
                    AND COD_PNEU = F_CODIGO_PNEU)
    THEN
        RAISE EXCEPTION 'Nenhum valor de aferição encontrado com estes parâmetros: Unidade %, Pneu %
            e Código %', F_COD_UNIDADE, F_CODIGO_PNEU, F_CODIGO_AFERICAO;
    END IF;

    -- Define qual fluxo executar de acordo com a quantidade de valores de aferição encontrados
    CASE V_QTD_VALORES_AFERICAO
        WHEN 1
            THEN
                -- Somente um valor de aferição foi encontrado, deletar toda a aferição, manutenção e valores
                PERFORM SUPORTE.FUNC_AFERICAO_DELETA_AFERICAO(F_COD_UNIDADE, V_PLACA, F_CODIGO_AFERICAO,
                                                              F_MOTIVO_DELECAO);
                V_PREFIXO_MENSAGEM_RETORNO := 'AFERIÇÃO, MANUTENÇÃO E VALOR DE AFERIÇÃO DELETADO ';
        ELSE
            -- Existe mais de um valor de aferição, deletar exclusivamente por COD_PNEU
            -- DELETA AFERIÇÃO.
            UPDATE AFERICAO_VALORES_DATA
            SET DELETADO            = TRUE,
                DATA_HORA_DELETADO  = NOW(),
                PG_USERNAME_DELECAO = SESSION_USER,
                MOTIVO_DELECAO      = F_MOTIVO_DELECAO
            WHERE COD_UNIDADE = F_COD_UNIDADE
              AND COD_PNEU = F_CODIGO_PNEU
              AND COD_AFERICAO = F_CODIGO_AFERICAO;

            GET DIAGNOSTICS V_QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

            IF (V_QTD_LINHAS_ATUALIZADAS <= 0)
            THEN
                RAISE EXCEPTION 'Erro ao deletar os valores de aferição com estes parâmetros Unidade %,
                    Pneu % e Código %', F_COD_UNIDADE, F_CODIGO_PNEU, F_CODIGO_AFERICAO;
            END IF;

            -- DELETA AFERIÇÃO MANUTENÇÃO.
            -- Não verificamos quantas linhas atualizadas pois aferição pode não ter manutenções.
            UPDATE AFERICAO_MANUTENCAO_DATA
            SET DELETADO            = TRUE,
                DATA_HORA_DELETADO  = NOW(),
                PG_USERNAME_DELECAO = SESSION_USER,
                MOTIVO_DELECAO      = F_MOTIVO_DELECAO
            WHERE COD_UNIDADE = F_COD_UNIDADE
              AND COD_PNEU = F_CODIGO_PNEU
              AND COD_AFERICAO = F_CODIGO_AFERICAO;

            V_PREFIXO_MENSAGEM_RETORNO := 'VALOR DE AFERIÇÃO DELETADO ';
        END CASE;

    SELECT V_PREFIXO_MENSAGEM_RETORNO
               || ', CÓDIGO DA UNIDADE: '
               || F_COD_UNIDADE
               || ', CÓDIGO DO PNEU: '
               || F_CODIGO_PNEU
               || ', CÓDIGO DA AFERIÇÃO: '
               || F_CODIGO_AFERICAO
    INTO AVISO_AFERICAO_VALOR_DELETADA;
END
$$;


CREATE OR REPLACE FUNCTION SUPORTE.FUNC_AFERICAO_DELETA_SERVICO_AFERICAO(F_COD_EMPRESA BIGINT,
                                                                         F_COD_UNIDADE BIGINT,
                                                                         F_COD_PNEU BIGINT,
                                                                         F_NUMERO_FOGO TEXT,
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
    PERFORM FUNC_GARANTE_PNEU_EXISTE(F_COD_EMPRESA, F_COD_UNIDADE, F_COD_PNEU, F_NUMERO_FOGO);
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
    V_NUMERO_FOGO VARCHAR := (SELECT P.CODIGO_CLIENTE
                              FROM PNEU P
                              WHERE P.CODIGO = F_COD_PNEU);
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
    PERFORM FUNC_GARANTE_PNEU_EXISTE(V_COD_EMPRESA, F_COD_UNIDADE, F_COD_PNEU, V_NUMERO_FOGO);

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



CREATE OR REPLACE FUNCTION SUPORTE.FUNC_CHECKLIST_ALTERA_KM_COLETADO_CHECKLIST_REALIZADO(F_COD_UNIDADE BIGINT,
                                                                                         F_PLACA TEXT,
                                                                                         F_COD_CHECKLIST_REALIZADO BIGINT,
                                                                                         F_NOVO_KM BIGINT,
                                                                                         OUT AVISO_KM_CHECKLIST_ALTERADO TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    QTD_LINHAS_ATUALIZADAS BIGINT;
BEGIN
    PERFORM SUPORTE.FUNC_HISTORICO_SALVA_EXECUCAO();
    PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE);
    PERFORM FUNC_GARANTE_VEICULO_EXISTE(F_COD_UNIDADE, F_PLACA);
    PERFORM FUNC_GARANTE_NOVO_KM_MENOR_QUE_ATUAL_VEICULO(F_COD_UNIDADE, F_PLACA, F_NOVO_KM);

    -- Verifica se o checklist existe.
    IF NOT EXISTS(SELECT CD.CODIGO
                  FROM CHECKLIST CD
                  WHERE CD.CODIGO = F_COD_CHECKLIST_REALIZADO
                    AND CD.COD_UNIDADE = F_COD_UNIDADE
                    AND CD.PLACA_VEICULO = F_PLACA)
    THEN
        RAISE EXCEPTION 'Não foi possível encontrar o checklist realizado com estes parâmetros: Unidade %, Placa %,
                     Código da realização do checklist %', F_COD_UNIDADE, F_PLACA, F_COD_CHECKLIST_REALIZADO;
    END IF;

    UPDATE CHECKLIST_DATA
    SET KM_VEICULO = F_NOVO_KM
    WHERE CODIGO = F_COD_CHECKLIST_REALIZADO
      AND COD_UNIDADE = F_COD_UNIDADE
      AND PLACA_VEICULO = F_PLACA;

    GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

    IF (QTD_LINHAS_ATUALIZADAS <= 0)
    THEN
        RAISE EXCEPTION 'Erro ao atualizar o km do checklist realizado com estes parâemtros: Unidade %, Placa %,
                     Código da realização do checklist %', F_COD_UNIDADE, F_PLACA, F_COD_CHECKLIST_REALIZADO;
    END IF;

    SELECT 'O KM DO VEÍCULO NO CHECKLIST REALIZADO FOI ALTERADO COM SUCESSO, UNIDADE: '
               || F_COD_UNIDADE
               || ', PLACA: '
               || F_PLACA
               || ', CÓDIGO DO CHECKLIST REALIZADO: '
               || F_COD_CHECKLIST_REALIZADO
    INTO AVISO_KM_CHECKLIST_ALTERADO;
END;
$$;


CREATE OR REPLACE FUNCTION SUPORTE.FUNC_CHECKLIST_COPIA_MODELO_CHECKLIST(F_COD_MODELO_CHECKLIST_COPIADO BIGINT,
                                                                         F_COD_UNIDADE_DESTINO_MODELO_CHECKLIST BIGINT,
                                                                         F_COD_COLABORADOR_SOLICITANTE_COPIA BIGINT,
                                                                         F_COPIAR_CARGOS_LIBERADOS BOOLEAN DEFAULT TRUE,
                                                                         F_COPIAR_TIPOS_VEICULOS_LIBERADOS BOOLEAN DEFAULT TRUE,
                                                                         OUT COD_MODELO_CHECKLIST_INSERIDO BIGINT,
                                                                         OUT AVISO_MODELO_INSERIDO TEXT)
    RETURNS RECORD
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    COD_UNIDADE_MODELO_CHECKLIST_COPIADO  BIGINT;
    COD_PERGUNTA_CRIADO                   BIGINT;
    F_COD_EMPRESA                         BIGINT := (SELECT COD_EMPRESA
                                                     FROM UNIDADE
                                                     WHERE CODIGO = F_COD_UNIDADE_DESTINO_MODELO_CHECKLIST);
    PERGUNTA_MODELO_CHECKLIST_COPIADO     CHECKLIST_PERGUNTAS_DATA%ROWTYPE;
    MODELO_VEICULO_TIPO_CHECKLIST_COPIADO CHECKLIST_MODELO_VEICULO_TIPO%ROWTYPE;
    NOME_MODELO_CHECKLIST_COPIADO         TEXT;
    COD_VERSAO_MODELO_CHECKLIST_COPIADO   BIGINT := (SELECT COD_VERSAO_ATUAL
                                                     FROM CHECKLIST_MODELO
                                                     WHERE CODIGO = F_COD_MODELO_CHECKLIST_COPIADO);
    STATUS_MODELO_CHECKLIST_COPIADO       BOOLEAN;
    NOVO_COD_VERSAO_MODELO                BIGINT;
BEGIN
    PERFORM SUPORTE.FUNC_HISTORICO_SALVA_EXECUCAO();
    -- VERIFICA SE COLABORADOR PERTENCE À EMPRESA.
    PERFORM FUNC_GARANTE_INTEGRIDADE_EMPRESA_COD_COLABORADOR(F_COD_EMPRESA, F_COD_COLABORADOR_SOLICITANTE_COPIA);

    -- VERIFICA SE O MODELO DE CHECKLIST EXISTE.
    IF NOT EXISTS(SELECT CODIGO
                  FROM CHECKLIST_MODELO
                  WHERE CODIGO = F_COD_MODELO_CHECKLIST_COPIADO)
    THEN
        RAISE EXCEPTION 'Modelo de checklist de código % não existe!', F_COD_MODELO_CHECKLIST_COPIADO;
    END IF;

    -- VERIFICA SE A UNIDADE DE CÓDIGO INFORMADO EXISTE.
    PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE_DESTINO_MODELO_CHECKLIST);

    -- VERIFICA SE ESTAMOS COPIANDO O MODELO DE CHECKLIST ENTRE UNIDADES DA MESMA EMPRESA.
    SELECT COD_UNIDADE
    FROM CHECKLIST_MODELO CM
    WHERE CM.CODIGO = F_COD_MODELO_CHECKLIST_COPIADO
    INTO COD_UNIDADE_MODELO_CHECKLIST_COPIADO;
    IF (F_COD_EMPRESA !=
        (SELECT U.COD_EMPRESA
         FROM UNIDADE U
         WHERE U.CODIGO = COD_UNIDADE_MODELO_CHECKLIST_COPIADO))
    THEN
        RAISE EXCEPTION 'Só é possível copiar modelos de checklists entre unidades da mesma empresa para garantirmos
            o vínculo correto de imagens da galeria.';
    END IF;

    -- Busca o nome e status do modelo copiado.
    SELECT CONCAT(CC.NOME, ' (cópia)'), CC.STATUS_ATIVO
    FROM CHECKLIST_MODELO CC
    WHERE CC.CODIGO = F_COD_MODELO_CHECKLIST_COPIADO
    INTO NOME_MODELO_CHECKLIST_COPIADO, STATUS_MODELO_CHECKLIST_COPIADO;

    -- Busca o novo código de versão do modelo de checklist
    NOVO_COD_VERSAO_MODELO := NEXTVAL(
            PG_GET_SERIAL_SEQUENCE('checklist_modelo_versao', 'cod_versao_checklist_modelo'));

    -- Assim conseguimos inserir mantendo a referência circular entre modelo e versão.
    SET CONSTRAINTS ALL DEFERRED;

    -- INSERE O MODELO DE CHECKLIST.
    INSERT INTO CHECKLIST_MODELO (COD_UNIDADE, COD_VERSAO_ATUAL, NOME, STATUS_ATIVO)
    VALUES (F_COD_UNIDADE_DESTINO_MODELO_CHECKLIST,
            NOVO_COD_VERSAO_MODELO,
            NOME_MODELO_CHECKLIST_COPIADO,
            STATUS_MODELO_CHECKLIST_COPIADO)
    RETURNING CODIGO INTO COD_MODELO_CHECKLIST_INSERIDO;

    -- VERIFICAMOS SE O INSERT FUNCIONOU.
    IF COD_MODELO_CHECKLIST_INSERIDO IS NULL OR COD_MODELO_CHECKLIST_INSERIDO <= 0
    THEN
        RAISE EXCEPTION 'Não foi possível copiar o modelo de checklist';
    END IF;

    -- INSERE A VERSÃO
    INSERT INTO CHECKLIST_MODELO_VERSAO(COD_VERSAO_CHECKLIST_MODELO,
                                        COD_VERSAO_USER_FRIENDLY,
                                        COD_CHECKLIST_MODELO,
                                        DATA_HORA_CRIACAO_VERSAO,
                                        COD_COLABORADOR_CRIACAO_VERSAO)
    VALUES (NOVO_COD_VERSAO_MODELO,
            1,
            COD_MODELO_CHECKLIST_INSERIDO,
            NOW(),
            F_COD_COLABORADOR_SOLICITANTE_COPIA);

    SELECT CONCAT('Modelo inserido com sucesso, código: ', COD_MODELO_CHECKLIST_INSERIDO)
    INTO AVISO_MODELO_INSERIDO;

    IF F_COPIAR_CARGOS_LIBERADOS
    THEN
        -- INSERE OS CARGOS LIBERADOS.
        INSERT INTO CHECKLIST_MODELO_FUNCAO (COD_CHECKLIST_MODELO, COD_UNIDADE, COD_FUNCAO)
            (SELECT COD_MODELO_CHECKLIST_INSERIDO,
                    F_COD_UNIDADE_DESTINO_MODELO_CHECKLIST,
                    CMF.COD_FUNCAO
             FROM CHECKLIST_MODELO_FUNCAO CMF
             WHERE CMF.COD_CHECKLIST_MODELO = F_COD_MODELO_CHECKLIST_COPIADO);
    END IF;

    IF F_COPIAR_TIPOS_VEICULOS_LIBERADOS
    THEN
        -- COPIA OS TIPOS DE VEÍCULO VINCULADOS.
        FOR MODELO_VEICULO_TIPO_CHECKLIST_COPIADO IN
            SELECT CMVT.COD_UNIDADE,
                   CMVT.COD_MODELO,
                   CMVT.COD_TIPO_VEICULO
            FROM CHECKLIST_MODELO_VEICULO_TIPO CMVT
            WHERE CMVT.COD_MODELO = F_COD_MODELO_CHECKLIST_COPIADO
            LOOP
                -- INSERE OS TIPOS DE VEÍCULOS VINCULADOS.
                INSERT INTO CHECKLIST_MODELO_VEICULO_TIPO (cod_unidade, cod_modelo, cod_tipo_veiculo)
                VALUES (F_COD_UNIDADE_DESTINO_MODELO_CHECKLIST,
                        COD_MODELO_CHECKLIST_INSERIDO,
                        MODELO_VEICULO_TIPO_CHECKLIST_COPIADO.COD_TIPO_VEICULO);
            END LOOP;
    END IF;

    -- INSERE AS PERGUNTAS E ALTERNATIVAS.
    FOR PERGUNTA_MODELO_CHECKLIST_COPIADO IN
        -- Usamos vários NULLs pois o rowtype se baseia na ordem de criação das coluna na tabela, não na view.
        -- E antes da coluna de mídia, existem várias outras.
        SELECT CP.COD_CHECKLIST_MODELO,
               CP.COD_UNIDADE,
               CP.ORDEM,
               CP.PERGUNTA,
               CP.SINGLE_CHOICE,
               CP.COD_IMAGEM,
               CP.CODIGO,
               NULL,
               NULL,
               NULL,
               NULL,
               NULL,
               CP.ANEXO_MIDIA_RESPOSTA_OK
        FROM CHECKLIST_PERGUNTAS CP
        WHERE CP.COD_VERSAO_CHECKLIST_MODELO = COD_VERSAO_MODELO_CHECKLIST_COPIADO
        LOOP
            -- PERGUNTA.
            INSERT INTO CHECKLIST_PERGUNTAS (COD_CHECKLIST_MODELO,
                                             COD_UNIDADE,
                                             ORDEM,
                                             PERGUNTA,
                                             SINGLE_CHOICE,
                                             COD_IMAGEM,
                                             ANEXO_MIDIA_RESPOSTA_OK,
                                             COD_VERSAO_CHECKLIST_MODELO)
            VALUES (COD_MODELO_CHECKLIST_INSERIDO,
                    F_COD_UNIDADE_DESTINO_MODELO_CHECKLIST,
                    PERGUNTA_MODELO_CHECKLIST_COPIADO.ORDEM,
                    PERGUNTA_MODELO_CHECKLIST_COPIADO.PERGUNTA,
                    PERGUNTA_MODELO_CHECKLIST_COPIADO.SINGLE_CHOICE,
                    PERGUNTA_MODELO_CHECKLIST_COPIADO.COD_IMAGEM,
                    PERGUNTA_MODELO_CHECKLIST_COPIADO.ANEXO_MIDIA_RESPOSTA_OK,
                    NOVO_COD_VERSAO_MODELO)
            RETURNING CODIGO INTO COD_PERGUNTA_CRIADO;
            -- ALTERNATIVA.
            INSERT INTO CHECKLIST_ALTERNATIVA_PERGUNTA (COD_CHECKLIST_MODELO,
                                                        COD_UNIDADE,
                                                        ALTERNATIVA,
                                                        ORDEM,
                                                        COD_PERGUNTA,
                                                        ALTERNATIVA_TIPO_OUTROS,
                                                        PRIORIDADE,
                                                        DEVE_ABRIR_ORDEM_SERVICO,
                                                        ANEXO_MIDIA,
                                                        COD_VERSAO_CHECKLIST_MODELO)
                (SELECT COD_MODELO_CHECKLIST_INSERIDO,
                        F_COD_UNIDADE_DESTINO_MODELO_CHECKLIST,
                        CAP.ALTERNATIVA,
                        CAP.ORDEM,
                        COD_PERGUNTA_CRIADO,
                        CAP.ALTERNATIVA_TIPO_OUTROS,
                        CAP.PRIORIDADE,
                        CAP.DEVE_ABRIR_ORDEM_SERVICO,
                        CAP.ANEXO_MIDIA,
                        NOVO_COD_VERSAO_MODELO
                 FROM CHECKLIST_ALTERNATIVA_PERGUNTA CAP
                 WHERE CAP.COD_VERSAO_CHECKLIST_MODELO = COD_VERSAO_MODELO_CHECKLIST_COPIADO
                   AND CAP.COD_PERGUNTA = PERGUNTA_MODELO_CHECKLIST_COPIADO.CODIGO);
        END LOOP;
END;
$$;


CREATE OR REPLACE FUNCTION SUPORTE.FUNC_CHECKLIST_COPIA_MODELO_CHECKLIST_ENTRE_EMPRESAS(F_COD_MODELO_CHECKLIST_COPIADO BIGINT,
                                                                                        F_COD_UNIDADE_DESTINO_MODELO_CHECKLIST BIGINT,
                                                                                        F_COD_COLABORADOR_SOLICITANTE_COPIA BIGINT,
                                                                                        F_COD_CARGOS_CHECKLIST BIGINT[] DEFAULT NULL,
                                                                                        F_COD_TIPOS_VEICULOS_CHECKLIST BIGINT[] DEFAULT NULL,
                                                                                        OUT COD_MODELO_CHECKLIST_INSERIDO BIGINT,
                                                                                        OUT AVISO_MODELO_INSERIDO TEXT)
    RETURNS RECORD
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    F_COD_EMPRESA_DESTINO_MODELO_CHECKLIST BIGINT := (SELECT U.COD_EMPRESA
                                                      FROM UNIDADE U
                                                      WHERE U.CODIGO = F_COD_UNIDADE_DESTINO_MODELO_CHECKLIST);
    COD_UNIDADE_MODELO_CHECKLIST_COPIADO   BIGINT;
    COD_PERGUNTA_CRIADO                    BIGINT;
    NOVO_COD_VERSAO_MODELO                 BIGINT;
    COD_VERSAO_MODELO_CHECKLIST_COPIADO    BIGINT := (SELECT COD_VERSAO_ATUAL
                                                      FROM CHECKLIST_MODELO
                                                      WHERE CODIGO = F_COD_MODELO_CHECKLIST_COPIADO);
    PERGUNTA_MODELO_CHECKLIST_COPIADO      CHECKLIST_PERGUNTAS_DATA%ROWTYPE;
BEGIN
    PERFORM SUPORTE.FUNC_HISTORICO_SALVA_EXECUCAO();
    -- VERIFICA SE COLABORADOR PERTENCE À EMPRESA DESTINO.
    PERFORM FUNC_GARANTE_INTEGRIDADE_EMPRESA_COD_COLABORADOR(F_COD_EMPRESA_DESTINO_MODELO_CHECKLIST,
                                                             F_COD_COLABORADOR_SOLICITANTE_COPIA);

    -- VERIFICA SE O MODELO DE CHECKLIST EXISTE.
    IF NOT EXISTS(SELECT CM.CODIGO
                  FROM CHECKLIST_MODELO CM
                  WHERE CM.CODIGO = F_COD_MODELO_CHECKLIST_COPIADO)
    THEN
        RAISE EXCEPTION 'Modelo de checklist de código % não existe!', F_COD_MODELO_CHECKLIST_COPIADO;
    END IF;

    -- VERIFICA SE A UNIDADE DE CÓDIGO INFORMADO EXISTE.
    PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE_DESTINO_MODELO_CHECKLIST);

    -- VERIFICA SE ESTAMOS COPIANDO O MODELO DE CHECKLIST ENTRE UNIDADES DE EMPRESAS DIFERENTES.
    SELECT CM.COD_UNIDADE
    FROM CHECKLIST_MODELO CM
    WHERE CM.CODIGO = F_COD_MODELO_CHECKLIST_COPIADO
    INTO COD_UNIDADE_MODELO_CHECKLIST_COPIADO;
    IF (F_COD_EMPRESA_DESTINO_MODELO_CHECKLIST =
        (SELECT U.COD_EMPRESA
         FROM UNIDADE U
         WHERE U.CODIGO = COD_UNIDADE_MODELO_CHECKLIST_COPIADO))
    THEN
        RAISE EXCEPTION 'Essa function deve ser utilizada para copiar modelos de checklists entre empresas diferentes.
                        Utilize a function: FUNC_CHECKLIST_COPIA_MODELO_CHECKLIST, para copiar checklists entre unidades
             da mesma empresa.';
    END IF;

    IF F_COD_CARGOS_CHECKLIST IS NOT NULL
    THEN
        -- VERIFICA SE TODOS OS CARGOS EXISTEM.
        IF (SELECT EXISTS(SELECT COD_CARGO
                          FROM UNNEST(F_COD_CARGOS_CHECKLIST) AS COD_CARGO
                                   LEFT JOIN FUNCAO F ON F.CODIGO = COD_CARGO
                          WHERE F.CODIGO IS NULL))
        THEN
            RAISE EXCEPTION 'O(s) cargo(s) % não existe(m) no ProLog', (SELECT ARRAY_AGG(COD_CARGO)
                                                                        FROM UNNEST(F_COD_CARGOS_CHECKLIST)
                                                                                 AS COD_CARGO
                                                                                 LEFT JOIN FUNCAO F
                                                                                           ON F.CODIGO = COD_CARGO
                                                                        WHERE F.CODIGO IS NULL);
        END IF;

        -- VERIFICA SE TODOS OS CARGOS PERTENCEM A EMPRESA DE DESTINO.
        IF (SELECT EXISTS(SELECT COD_CARGO
                          FROM UNNEST(F_COD_CARGOS_CHECKLIST) AS COD_CARGO
                                   LEFT JOIN FUNCAO F ON F.CODIGO = COD_CARGO
                          WHERE F.COD_EMPRESA <> F_COD_EMPRESA_DESTINO_MODELO_CHECKLIST))
        THEN
            RAISE EXCEPTION 'O(s) cargo(s) % não pertence(m) a empresa para a qual você está tentando copiar o
                modelo checklit, empresa: %',
                (SELECT ARRAY_AGG(COD_CARGO)
                 FROM UNNEST(F_COD_CARGOS_CHECKLIST) AS COD_CARGO
                          LEFT JOIN FUNCAO F ON F.CODIGO = COD_CARGO
                 WHERE F.COD_EMPRESA <> F_COD_EMPRESA_DESTINO_MODELO_CHECKLIST),
                (SELECT E.NOME
                 FROM EMPRESA E
                 WHERE E.CODIGO = F_COD_EMPRESA_DESTINO_MODELO_CHECKLIST);
        END IF;
    END IF;

    IF F_COD_TIPOS_VEICULOS_CHECKLIST IS NOT NULL
    THEN
        -- VERIFICA SE TODOS OS TIPOS DE VEÍCULO EXISTEM.
        IF (SELECT EXISTS(SELECT COD_TIPO_VEICULO
                          FROM UNNEST(F_COD_TIPOS_VEICULOS_CHECKLIST) AS COD_TIPO_VEICULO
                                   LEFT JOIN VEICULO_TIPO VT
                                             ON VT.CODIGO = COD_TIPO_VEICULO
                          WHERE VT.CODIGO IS NULL))
        THEN
            RAISE EXCEPTION 'O(s) tipo(s) de veículo % não existe(m) no ProLog', (SELECT ARRAY_AGG(COD_TIPO_VEICULO)
                                                                                  FROM UNNEST(F_COD_TIPOS_VEICULOS_CHECKLIST)
                                                                                           AS COD_TIPO_VEICULO
                                                                                           LEFT JOIN VEICULO_TIPO VT
                                                                                                     ON VT.CODIGO = COD_TIPO_VEICULO
                                                                                  WHERE VT.CODIGO IS NULL);
        END IF;

        -- VERIFICA SE TODOS OS TIPOS DE VEÍCULO PERTENCEM A EMPRESA DE DESTINO.
        IF (SELECT EXISTS(SELECT COD_TIPO_VEICULO
                          FROM UNNEST(F_COD_TIPOS_VEICULOS_CHECKLIST) AS COD_TIPO_VEICULO
                                   LEFT JOIN VEICULO_TIPO VT
                                             ON VT.CODIGO = COD_TIPO_VEICULO
                          WHERE VT.COD_EMPRESA <> F_COD_EMPRESA_DESTINO_MODELO_CHECKLIST))
        THEN
            RAISE EXCEPTION 'O(s) tipo(s) de veículo % não pertence(m) a empresa para a qual você está tentando
                copiar o modelo checklit, empresa: %',
                (SELECT ARRAY_AGG(COD_TIPO_VEICULO)
                 FROM UNNEST(F_COD_TIPOS_VEICULOS_CHECKLIST) AS COD_TIPO_VEICULO
                          LEFT JOIN VEICULO_TIPO VT
                                    ON VT.CODIGO = COD_TIPO_VEICULO
                 WHERE VT.COD_EMPRESA <> F_COD_EMPRESA_DESTINO_MODELO_CHECKLIST),
                (SELECT E.NOME
                 FROM EMPRESA E
                 WHERE E.CODIGO = F_COD_EMPRESA_DESTINO_MODELO_CHECKLIST);
        END IF;
    END IF;

    -- Busca o novo código de versão do modelo de checklist.
    NOVO_COD_VERSAO_MODELO := NEXTVAL(
            PG_GET_SERIAL_SEQUENCE('checklist_modelo_versao', 'cod_versao_checklist_modelo'));

    -- Assim conseguimos inserir mantendo a referência circular entre modelo e versão.
    SET CONSTRAINTS ALL DEFERRED;

    -- INSERE O MODELO DE CHECKLIST.
    INSERT INTO CHECKLIST_MODELO (COD_UNIDADE, COD_VERSAO_ATUAL, NOME, STATUS_ATIVO)
    SELECT F_COD_UNIDADE_DESTINO_MODELO_CHECKLIST,
           NOVO_COD_VERSAO_MODELO,
           CONCAT(CC.NOME, ' (cópia)'),
           CC.STATUS_ATIVO
    FROM CHECKLIST_MODELO CC
    WHERE CC.CODIGO = F_COD_MODELO_CHECKLIST_COPIADO
    RETURNING CODIGO INTO COD_MODELO_CHECKLIST_INSERIDO;

    -- VERIFICAMOS SE O INSERT FUNCIONOU.
    IF COD_MODELO_CHECKLIST_INSERIDO IS NULL OR COD_MODELO_CHECKLIST_INSERIDO <= 0
    THEN
        RAISE EXCEPTION 'Não foi possível copiar o modelo de checklist';
    END IF;

    -- INSERE A VERSÃO.
    INSERT INTO CHECKLIST_MODELO_VERSAO(COD_VERSAO_CHECKLIST_MODELO,
                                        COD_VERSAO_USER_FRIENDLY,
                                        COD_CHECKLIST_MODELO,
                                        DATA_HORA_CRIACAO_VERSAO,
                                        COD_COLABORADOR_CRIACAO_VERSAO)
    VALUES (NOVO_COD_VERSAO_MODELO,
            1,
            COD_MODELO_CHECKLIST_INSERIDO,
            NOW(),
            F_COD_COLABORADOR_SOLICITANTE_COPIA);

    SELECT CONCAT('Modelo inserido com sucesso, código: ', COD_MODELO_CHECKLIST_INSERIDO)
    INTO AVISO_MODELO_INSERIDO;

    IF F_COD_CARGOS_CHECKLIST IS NOT NULL
    THEN
        -- INSERE CARGOS QUE PODEM REALIZAR O MODELO DE CHECKLIST
        INSERT INTO CHECKLIST_MODELO_FUNCAO (COD_CHECKLIST_MODELO, COD_UNIDADE, COD_FUNCAO)
        SELECT COD_MODELO_CHECKLIST_INSERIDO          COD_CHECKLIST_MODELO,
               F_COD_UNIDADE_DESTINO_MODELO_CHECKLIST COD_UNIDADE,
               CODIGO_FUNCAO
        FROM UNNEST(F_COD_CARGOS_CHECKLIST) CODIGO_FUNCAO;
    END IF;

    IF F_COD_TIPOS_VEICULOS_CHECKLIST IS NOT NULL
    THEN
        -- INSERE TIPOS DE VEÍCULOS LIBERADOS PARA O MODELO DE CHECKLIST
        INSERT INTO CHECKLIST_MODELO_VEICULO_TIPO (COD_MODELO, COD_UNIDADE, COD_TIPO_VEICULO)
        SELECT COD_MODELO_CHECKLIST_INSERIDO          COD_CHECKLIST_MODELO,
               F_COD_UNIDADE_DESTINO_MODELO_CHECKLIST COD_UNIDADE,
               CODIGO_TIPO_VEICULO
        FROM UNNEST(F_COD_TIPOS_VEICULOS_CHECKLIST) CODIGO_TIPO_VEICULO;
    END IF;

    -- INSERE AS PERGUNTAS E ALTERNATIVAS.
    FOR PERGUNTA_MODELO_CHECKLIST_COPIADO IN
        -- Usamos vários NULLs pois o rowtype se baseia na ordem de criação das coluna na tabela, não na view.
        -- E antes da coluna de mídia, existem várias outras.
        SELECT CP.COD_CHECKLIST_MODELO,
               CP.COD_UNIDADE,
               CP.ORDEM,
               CP.PERGUNTA,
               CP.SINGLE_CHOICE,
               CP.COD_IMAGEM,
               CP.CODIGO,
               NULL,
               NULL,
               NULL,
               NULL,
               NULL,
               CP.ANEXO_MIDIA_RESPOSTA_OK
        FROM CHECKLIST_PERGUNTAS CP
        WHERE CP.COD_VERSAO_CHECKLIST_MODELO = COD_VERSAO_MODELO_CHECKLIST_COPIADO
        LOOP
            -- PERGUNTA.
            INSERT INTO CHECKLIST_PERGUNTAS (COD_CHECKLIST_MODELO,
                                             COD_UNIDADE,
                                             ORDEM,
                                             PERGUNTA,
                                             SINGLE_CHOICE,
                                             COD_IMAGEM,
                                             ANEXO_MIDIA_RESPOSTA_OK,
                                             COD_VERSAO_CHECKLIST_MODELO)
            VALUES (COD_MODELO_CHECKLIST_INSERIDO,
                    F_COD_UNIDADE_DESTINO_MODELO_CHECKLIST,
                    PERGUNTA_MODELO_CHECKLIST_COPIADO.ORDEM,
                    PERGUNTA_MODELO_CHECKLIST_COPIADO.PERGUNTA,
                    PERGUNTA_MODELO_CHECKLIST_COPIADO.SINGLE_CHOICE,
                       -- Só copiamos o código da imagem se a imagem vinculada for da galeria pública do Prolog.
                    F_IF((SELECT EXISTS(SELECT CGI.COD_IMAGEM
                                        FROM CHECKLIST_GALERIA_IMAGENS CGI
                                        WHERE CGI.COD_IMAGEM = PERGUNTA_MODELO_CHECKLIST_COPIADO.COD_IMAGEM
                                          AND CGI.COD_EMPRESA IS NULL)),
                         PERGUNTA_MODELO_CHECKLIST_COPIADO.COD_IMAGEM,
                         NULL),
                    PERGUNTA_MODELO_CHECKLIST_COPIADO.ANEXO_MIDIA_RESPOSTA_OK,
                    NOVO_COD_VERSAO_MODELO)
            RETURNING CODIGO INTO COD_PERGUNTA_CRIADO;
            -- ALTERNATIVA.
            INSERT INTO CHECKLIST_ALTERNATIVA_PERGUNTA (COD_CHECKLIST_MODELO,
                                                        COD_UNIDADE,
                                                        ALTERNATIVA,
                                                        ORDEM,
                                                        COD_PERGUNTA,
                                                        ALTERNATIVA_TIPO_OUTROS,
                                                        PRIORIDADE,
                                                        DEVE_ABRIR_ORDEM_SERVICO,
                                                        ANEXO_MIDIA,
                                                        COD_VERSAO_CHECKLIST_MODELO)
                (SELECT COD_MODELO_CHECKLIST_INSERIDO,
                        F_COD_UNIDADE_DESTINO_MODELO_CHECKLIST,
                        CAP.ALTERNATIVA,
                        CAP.ORDEM,
                        COD_PERGUNTA_CRIADO,
                        CAP.ALTERNATIVA_TIPO_OUTROS,
                        CAP.PRIORIDADE,
                        CAP.DEVE_ABRIR_ORDEM_SERVICO,
                        CAP.ANEXO_MIDIA,
                        NOVO_COD_VERSAO_MODELO
                 FROM CHECKLIST_ALTERNATIVA_PERGUNTA CAP
                 WHERE CAP.COD_VERSAO_CHECKLIST_MODELO = COD_VERSAO_MODELO_CHECKLIST_COPIADO
                   AND CAP.COD_PERGUNTA = PERGUNTA_MODELO_CHECKLIST_COPIADO.CODIGO);
        END LOOP;
END
$$;


CREATE OR REPLACE FUNCTION SUPORTE.FUNC_CHECKLIST_DELETA_CHECKLIST_E_OS(F_COD_UNIDADE BIGINT,
                                                                        F_COD_CHECKLIST BIGINT,
                                                                        F_PLACA TEXT,
                                                                        F_CPF_COLABORADOR BIGINT,
                                                                        F_MOTIVO_DELECAO TEXT,
                                                                        OUT AVISO_CHECKLIST_DELETADO TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    V_COD_OS_DELETADA BIGINT;
BEGIN
    PERFORM SUPORTE.FUNC_HISTORICO_SALVA_EXECUCAO();
    IF (SELECT NOT EXISTS(SELECT CODIGO
                          FROM CHECKLIST
                          WHERE CODIGO = F_COD_CHECKLIST
                            AND COD_UNIDADE = F_COD_UNIDADE
                            AND PLACA_VEICULO = F_PLACA
                            AND CPF_COLABORADOR = F_CPF_COLABORADOR))
    THEN
        RAISE EXCEPTION 'Nenhum checklist encontrado com as informações fornecidas, verifique!';
    END IF;

    -- Deleta checklist de forma lógica.
    UPDATE CHECKLIST_DATA
    SET DELETADO            = TRUE,
        DATA_HORA_DELETADO  = NOW(),
        PG_USERNAME_DELECAO = SESSION_USER,
        MOTIVO_DELECAO      = F_MOTIVO_DELECAO
    WHERE CODIGO = F_COD_CHECKLIST
      AND COD_UNIDADE = F_COD_UNIDADE
      AND PLACA_VEICULO = F_PLACA
      AND CPF_COLABORADOR = F_CPF_COLABORADOR
      AND DELETADO = FALSE;

    -- Validamos se o checklist realmente foi deletado lógicamente.
    IF (NOT FOUND)
    THEN
        RAISE EXCEPTION 'Erro ao deletar o checklist de código: % da Unidade: %', F_COD_CHECKLIST, F_COD_UNIDADE;
    END IF;

    -- Deleta lógicamente a Ordem de Serviço e os itens vinculada ao checklist, se existir.
    IF (SELECT EXISTS(SELECT CODIGO
                      FROM CHECKLIST_ORDEM_SERVICO
                      WHERE COD_CHECKLIST = F_COD_CHECKLIST
                        AND COD_UNIDADE = F_COD_UNIDADE))
    THEN
        -- Deleta lógicamente a O.S.
        UPDATE CHECKLIST_ORDEM_SERVICO_DATA
        SET DELETADO            = TRUE,
            DATA_HORA_DELETADO  = NOW(),
            PG_USERNAME_DELECAO = SESSION_USER,
            MOTIVO_DELECAO      = F_MOTIVO_DELECAO
        WHERE COD_CHECKLIST = F_COD_CHECKLIST
          AND COD_UNIDADE = F_COD_UNIDADE
          AND DELETADO = FALSE RETURNING CODIGO INTO V_COD_OS_DELETADA;

        IF (NOT FOUND)
        THEN
            RAISE EXCEPTION 'Erro ao deletar O.S. do checklist: % da Unidade: %', F_COD_CHECKLIST, F_COD_UNIDADE;
        END IF;

        -- Deleta lógicamente os Itens da O.S.
        UPDATE CHECKLIST_ORDEM_SERVICO_ITENS_DATA
        SET DELETADO            = TRUE,
            DATA_HORA_DELETADO  = NOW(),
            PG_USERNAME_DELECAO = SESSION_USER,
            MOTIVO_DELECAO      = F_MOTIVO_DELECAO
        WHERE COD_UNIDADE = F_COD_UNIDADE
          AND COD_OS = V_COD_OS_DELETADA
          AND DELETADO = FALSE;

        IF (NOT FOUND)
        THEN
            RAISE EXCEPTION 'Erro ao deletar Itens da O.S. do checklist: % da Unidade: %',
                F_COD_CHECKLIST, F_COD_UNIDADE;
        END IF;
    END IF;

    -- Deleta checklist da integração da Piccolotur
    IF (SELECT EXISTS(SELECT COD_CHECKLIST_PARA_SINCRONIZAR
                      FROM PICCOLOTUR.CHECKLIST_PENDENTE_PARA_SINCRONIZAR
                      WHERE COD_CHECKLIST_PARA_SINCRONIZAR = F_COD_CHECKLIST))
    THEN
        -- Deletamos apenas da tabela de pendente para evitar o envio dos checks que ainda não foram sincronizados e
        -- estão sendo deletados.
        DELETE
        FROM PICCOLOTUR.CHECKLIST_PENDENTE_PARA_SINCRONIZAR
        WHERE COD_CHECKLIST_PARA_SINCRONIZAR = F_COD_CHECKLIST;
    END IF;

    SELECT 'CHECKLIST DELETADO: '
               || F_COD_CHECKLIST
               || ', CÓDIGO DA UNIDADE: '
               || F_COD_UNIDADE
    INTO AVISO_CHECKLIST_DELETADO;
END;
$$;




CREATE OR REPLACE FUNCTION SUPORTE.FUNC_CHECKLIST_DELETA_CHECKLIST_E_OS_BY_UNIDADE_MODELO(F_COD_UNIDADE BIGINT,
                                                                                          F_COD_CHECKLIST_MODELO BIGINT,
                                                                                          F_MOTIVO_DELECAO TEXT,
                                                                                          OUT AVISO_CHECKLIST_DELETADO TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_COD_CHECKLISTS   BIGINT[];
    V_COD_OS_DELETADAS BIGINT[];
BEGIN
    PERFORM SUPORTE.FUNC_HISTORICO_SALVA_EXECUCAO();
    IF (SELECT NOT EXISTS(SELECT CODIGO
                          FROM CHECKLIST
                          WHERE COD_UNIDADE = F_COD_UNIDADE
                            AND COD_CHECKLIST_MODELO = F_COD_CHECKLIST_MODELO))
    THEN
        RAISE EXCEPTION 'Nenhum checklist encontrado com as informações fornecidas, verifique.';
    END IF;

    SELECT ARRAY_AGG(CODIGO)
    FROM CHECKLIST
    WHERE COD_UNIDADE = F_COD_UNIDADE
      AND COD_CHECKLIST_MODELO = F_COD_CHECKLIST_MODELO
    INTO V_COD_CHECKLISTS;


    -- Deleta checklist de forma lógica.
    UPDATE
        CHECKLIST_DATA
    SET DELETADO            = TRUE,
        DATA_HORA_DELETADO  = NOW(),
        PG_USERNAME_DELECAO = SESSION_USER,
        MOTIVO_DELECAO      = F_MOTIVO_DELECAO
    WHERE COD_UNIDADE = F_COD_UNIDADE
      AND COD_CHECKLIST_MODELO = F_COD_CHECKLIST_MODELO
      AND DELETADO = FALSE;
    -- Validamos se o checklist realmente foi deletado lógicamente.
    IF (NOT FOUND)
    THEN
        RAISE EXCEPTION 'Erro ao deletar o checklist da Unidade: %, Modelo: %.', F_COD_UNIDADE, F_COD_CHECKLIST_MODELO;
    END IF;

    -- Deleta lógicamente a Ordem de Serviço e os itens vinculada ao checklist, se existir.
    IF (SELECT EXISTS(SELECT CODIGO
                      FROM CHECKLIST_ORDEM_SERVICO
                      WHERE COD_UNIDADE = F_COD_UNIDADE
                        AND COD_CHECKLIST = ANY (V_COD_CHECKLISTS)))
    THEN

        WITH OS_DELETADA(COD_OS) AS (
            UPDATE CHECKLIST_ORDEM_SERVICO_DATA
                SET DELETADO = TRUE,
                    DATA_HORA_DELETADO = NOW(),
                    PG_USERNAME_DELECAO = SESSION_USER,
                    MOTIVO_DELECAO = F_MOTIVO_DELECAO
                WHERE COD_UNIDADE = F_COD_UNIDADE
                    AND COD_CHECKLIST = ANY (V_COD_CHECKLISTS)
                    AND DELETADO = FALSE RETURNING CODIGO
        )
        SELECT ARRAY_AGG(COD_OS)
        FROM OS_DELETADA
        INTO V_COD_OS_DELETADAS;

        -- Deleta lógicamente a O.S.
        IF (NOT FOUND)
        THEN
            RAISE EXCEPTION 'Erro ao deletar O.S. da Unidade: %, Modelo: %.', F_COD_UNIDADE, F_COD_CHECKLIST_MODELO;
        END IF;

        -- Deleta lógicamente os Itens da O.S.
        UPDATE CHECKLIST_ORDEM_SERVICO_ITENS_DATA
        SET DELETADO            = TRUE,
            DATA_HORA_DELETADO  = NOW(),
            PG_USERNAME_DELECAO = SESSION_USER,
            MOTIVO_DELECAO      = F_MOTIVO_DELECAO
        WHERE COD_UNIDADE = F_COD_UNIDADE
          AND COD_OS = ANY (V_COD_OS_DELETADAS)
          AND DELETADO = FALSE;

        IF (NOT FOUND)
        THEN
            RAISE EXCEPTION 'Erro ao deletar Itens da O.S. da Unidade: %, Modelo: %.',
                F_COD_UNIDADE, F_COD_CHECKLIST_MODELO;
        END IF;
    END IF;

    -- Deleta checklist da integração da Piccolotur
    IF (SELECT EXISTS(SELECT COD_CHECKLIST_PARA_SINCRONIZAR
                      FROM PICCOLOTUR.CHECKLIST_PENDENTE_PARA_SINCRONIZAR
                      WHERE COD_CHECKLIST_PARA_SINCRONIZAR = ANY (V_COD_CHECKLISTS)))
    THEN
        -- Deletamos apenas da tabela de pendente para evitar o envio dos checks que ainda não foram sincronizados e
        -- estão sendo deletados.
        DELETE
        FROM PICCOLOTUR.CHECKLIST_PENDENTE_PARA_SINCRONIZAR
        WHERE COD_CHECKLIST_PARA_SINCRONIZAR = ANY (V_COD_CHECKLISTS);
    END IF;

    SELECT 'MODELO DE CHECKLIST DELETADO: '
               || F_COD_CHECKLIST_MODELO
               || ', CÓDIGO DA UNIDADE: '
               || F_COD_UNIDADE
    INTO AVISO_CHECKLIST_DELETADO;
END;
$$;


CREATE OR REPLACE FUNCTION SUPORTE.FUNC_CHECKLIST_DELETA_MODELO_CHECKLIST(F_COD_UNIDADE BIGINT,
                                                                          F_COD_MODELO_CHECKLIST BIGINT,
                                                                          F_MOTIVO_DELECAO TEXT,
                                                                          F_DELETAR_CHECKLISTS_REALIZADOS_DESSE_MODELO
                                                                              BOOLEAN DEFAULT FALSE,
                                                                          OUT AVISO_MODELO_CHECKLIST_DELETADO TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    QTD_LINHAS_ATUALIZADAS BIGINT;
BEGIN
    PERFORM SUPORTE.FUNC_HISTORICO_SALVA_EXECUCAO();
    IF F_DELETAR_CHECKLISTS_REALIZADOS_DESSE_MODELO
    THEN
        PERFORM SUPORTE.FUNC_CHECKLIST_DELETA_CHECKLIST_E_OS_BY_UNIDADE_MODELO(
                F_COD_UNIDADE := F_COD_UNIDADE,
                F_COD_CHECKLIST_MODELO := F_COD_MODELO_CHECKLIST,
                F_MOTIVO_DELECAO := F_MOTIVO_DELECAO);
    END IF;

    -- Deleta modelo de checklist.
    UPDATE CHECKLIST_MODELO_DATA
    SET DELETADO            = TRUE,
        DATA_HORA_DELETADO  = NOW(),
        PG_USERNAME_DELECAO = SESSION_USER,
        MOTIVO_DELECAO      = F_MOTIVO_DELECAO
    WHERE CODIGO = F_COD_MODELO_CHECKLIST
      AND COD_UNIDADE = F_COD_UNIDADE;

    GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

    IF (QTD_LINHAS_ATUALIZADAS <= 0)
    THEN
        RAISE EXCEPTION 'Erro ao deletar o modelo de checklist de código: % e Unidade: %',
            F_COD_MODELO_CHECKLIST, F_COD_UNIDADE;
    END IF;

    -- Deleta pergundas do modelo de checklist.
    UPDATE CHECKLIST_PERGUNTAS_DATA
    SET DELETADO            = TRUE,
        DATA_HORA_DELETADO  = NOW(),
        PG_USERNAME_DELECAO = SESSION_USER,
        MOTIVO_DELECAO      = F_MOTIVO_DELECAO
    WHERE COD_CHECKLIST_MODELO = F_COD_MODELO_CHECKLIST
      AND COD_UNIDADE = F_COD_UNIDADE;

    GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

    IF (QTD_LINHAS_ATUALIZADAS <= 0)
    THEN
        RAISE EXCEPTION 'Erro ao deletar as perguntas do modelo de checklist de código: % e Unidade: %',
            F_COD_MODELO_CHECKLIST, F_COD_UNIDADE;
    END IF;

    -- Deleta as alternativas das pergundas do modelo de checklist.
    UPDATE CHECKLIST_ALTERNATIVA_PERGUNTA_DATA
    SET DELETADO            = TRUE,
        DATA_HORA_DELETADO  = NOW(),
        PG_USERNAME_DELECAO = SESSION_USER,
        MOTIVO_DELECAO      = F_MOTIVO_DELECAO
    WHERE COD_CHECKLIST_MODELO = F_COD_MODELO_CHECKLIST
      AND COD_UNIDADE = F_COD_UNIDADE;

    GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

    IF (QTD_LINHAS_ATUALIZADAS <= 0)
    THEN
        RAISE EXCEPTION 'Erro ao deletar as alternativas do modelo de checklist de código: % e Unidade: %',
            F_COD_MODELO_CHECKLIST, F_COD_UNIDADE;
    END IF;

    -- As únicas coisas que deletamos de fato são os vínculos de cargos e tipos de veículos, assim um modelo marcado
    -- como "deletado" não fica com vínculos que podem bloquear outras operações do BD.
    DELETE
    FROM CHECKLIST_MODELO_FUNCAO
    WHERE COD_UNIDADE = F_COD_UNIDADE
      AND COD_CHECKLIST_MODELO = F_COD_MODELO_CHECKLIST;
    DELETE
    FROM CHECKLIST_MODELO_VEICULO_TIPO
    WHERE COD_UNIDADE = F_COD_UNIDADE
      AND COD_MODELO = F_COD_MODELO_CHECKLIST;

    SELECT 'MODELO DE CHECKLIST DELETADO: '
               || F_COD_MODELO_CHECKLIST
               || ', CÓDIGO DA UNIDADE: '
               || F_COD_UNIDADE
               || F_IF(F_DELETAR_CHECKLISTS_REALIZADOS_DESSE_MODELO,
                       '. OS CHECKLISTS REALIZADOS DESSE MODELO TAMBÉM FORAM DELETADOS.' :: TEXT,
                       '. OS CHECKLISTS REALIZADOS DESSE MODELO NÃO FORAM DELETADOS.' :: TEXT)
    INTO AVISO_MODELO_CHECKLIST_DELETADO;
END;
$$;


CREATE OR REPLACE FUNCTION SUPORTE.FUNC_CHECKLIST_DESBLOQUEIA_CHECKLIST_OFFLINE(F_COD_EMPRESA BIGINT,
                                                                                F_NOME_EMPRESA TEXT,
                                                                                OUT AVISO_CHECKLIST_OFFLINE_LIBERADO TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    QTD_LINHAS_ATUALIZADAS BIGINT;
BEGIN
    PERFORM SUPORTE.FUNC_HISTORICO_SALVA_EXECUCAO();
    -- Verifica se empresa existe
    PERFORM FUNC_GARANTE_EMPRESA_EXISTE(F_COD_EMPRESA);

    -- Garante integridade da empresa
    PERFORM FUNC_GARANTE_INTEGRIDADE_EMPRESA(F_COD_EMPRESA, F_NOME_EMPRESA);

    -- Verifica se empresa possui bloqueio checklist
    IF NOT EXISTS(SELECT COD_EMPRESA FROM CHECKLIST_OFFLINE_EMPRESA_BLOQUEADA WHERE COD_EMPRESA = F_COD_EMPRESA)
    THEN
        RAISE EXCEPTION 'O checklist offline não está bloqueado para empresa % - %.', F_COD_EMPRESA, F_NOME_EMPRESA;
    END IF;

    -- Desbloqueia checklist.
    DELETE
    FROM CHECKLIST_OFFLINE_EMPRESA_BLOQUEADA
    WHERE COD_EMPRESA = F_COD_EMPRESA;

    GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

    IF (QTD_LINHAS_ATUALIZADAS <= 0)
    THEN
        RAISE EXCEPTION 'Erro ao liberar o checklist offline para a empresa: % - %.',
            F_COD_EMPRESA, F_NOME_EMPRESA;
    END IF;

    SELECT 'CHECKLIST OFFLINE LIBERADO PARA A EMPRESA: '
               || F_NOME_EMPRESA
               || ', CÓDIGO: '
               || F_COD_EMPRESA
               || '.'
    INTO AVISO_CHECKLIST_OFFLINE_LIBERADO;
END
$$;



CREATE OR REPLACE FUNCTION SUPORTE.FUNC_CHECKLIST_OS_ALTERA_KM_FECHAMENTO_OS(F_COD_UNIDADE BIGINT,
                                                                             F_COD_OS BIGINT,
                                                                             F_KM_ATUAL BIGINT,
                                                                             F_KM_CORRETO BIGINT,
                                                                             OUT F_AVISO_KM_ALTERADO TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    V_QTD_UPDATES  BIGINT;
    V_COD_ITENS_OS BIGINT[];
BEGIN
    PERFORM SUPORTE.FUNC_HISTORICO_SALVA_EXECUCAO();
    -- GARANTE QUE UNIDADE EXISTE.
    PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE);

    -- VERIFICA SE EXISTE ITENS NA ORDEM DE SERVIÇO.
    IF NOT EXISTS(SELECT COSI.COD_OS
                  FROM CHECKLIST_ORDEM_SERVICO_ITENS COSI
                  WHERE COSI.COD_UNIDADE = F_COD_UNIDADE
                    AND COSI.COD_OS = F_COD_OS
                    AND COSI.KM = F_KM_ATUAL)
    THEN
        RAISE EXCEPTION
            'Não existem itens de ordem de serviço com esses dados:
            código da unidade = % | código da ordem de serviço = % | km = % .', F_COD_UNIDADE, F_COD_OS, F_KM_ATUAL;
    ELSE
        V_COD_ITENS_OS := (SELECT array_agg(COSI.CODIGO)
                           FROM CHECKLIST_ORDEM_SERVICO_ITENS COSI
                           WHERE COSI.COD_UNIDADE = F_COD_UNIDADE
                             AND COSI.COD_OS = F_COD_OS
                             AND COSI.KM = F_KM_ATUAL);
    END IF;

    -- REALIZA A ALTERAÇÃO DO KM.
    UPDATE CHECKLIST_ORDEM_SERVICO_ITENS_DATA
    SET KM = F_KM_CORRETO
    WHERE COD_UNIDADE = F_COD_UNIDADE
      AND COD_OS = F_COD_OS
      AND KM = F_KM_ATUAL;

    GET DIAGNOSTICS V_QTD_UPDATES = ROW_COUNT;
    IF (V_QTD_UPDATES IS NULL OR V_QTD_UPDATES <= 0)
    THEN
        RAISE EXCEPTION
            'Não foi possível modificar o km da OS % na tabela checklist_ordem_servico_itens.', F_COD_OS;
    ELSE
        SELECT 'O km da OS foi modificado de ' || F_KM_ATUAL ||
               ' para ' || F_KM_CORRETO || ' nos tens:' || V_COD_ITENS_OS::TEXT
        INTO F_AVISO_KM_ALTERADO;
    END IF;
END;
$$;


CREATE OR REPLACE FUNCTION SUPORTE.FUNC_MOVIMENTACAO_ALTERA_KM_MOVIMENTACAO(F_COD_MOVIMENTACAO BIGINT,
                                                                            F_PLACA VARCHAR(7),
                                                                            F_KM_ATUALIZADO INTEGER,
                                                                            OUT AVISO_KM_ATUALIZADO TEXT) RETURNS TEXT
    SECURITY DEFINER
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    QTD_LINHAS_ATUALIZADAS  BIGINT;
    TIPO                    TEXT    := 'EM_USO';
    MOVIMENTACAO_ATUALIZADA TEXT;
    EXISTE_DESTINO          BOOLEAN := TRUE;
    EXISTE_ORIGEM           BOOLEAN := TRUE;
BEGIN
    PERFORM SUPORTE.FUNC_HISTORICO_SALVA_EXECUCAO();
    PERFORM FUNC_GARANTE_PLACA_CADASTRADA(F_PLACA);
    PERFORM FUNC_GARANTE_NOVO_KM_MENOR_QUE_ATUAL_VEICULO(
                        (SELECT V.COD_UNIDADE FROM VEICULO V WHERE V.PLACA = F_PLACA),
                        F_PLACA,
                        F_KM_ATUALIZADO);

    --VERIFICA SE A PLACA AINDA ESTÁ NA EMPRESA QUE FOI REALIZADA A MOVIMENTACAO.
    IF ((SELECT U.COD_EMPRESA
         FROM UNIDADE U
         WHERE U.CODIGO = (SELECT V.COD_UNIDADE FROM VEICULO V WHERE V.PLACA = F_PLACA)) !=
        (SELECT U.COD_EMPRESA
         FROM UNIDADE U
         WHERE U.CODIGO = (SELECT M.COD_UNIDADE FROM MOVIMENTACAO M WHERE M.CODIGO = F_COD_MOVIMENTACAO)))
    THEN
        RAISE EXCEPTION 'A PLACA % NÃO ESTÁ MAIS NA EMPRESA %.',
            F_PLACA,
            (SELECT E.NOME
             FROM EMPRESA E
             WHERE E.CODIGO = (SELECT U.COD_EMPRESA
                               FROM UNIDADE U
                               WHERE U.CODIGO =
                                     (SELECT M.COD_UNIDADE FROM MOVIMENTACAO M WHERE M.CODIGO = F_COD_MOVIMENTACAO)));
    END IF;

    --VERIFICA SE KM É NULL OU IGUAL A 0
    IF (F_KM_ATUALIZADO IS NULL OR F_KM_ATUALIZADO <= 0)
    THEN
        RAISE EXCEPTION 'O VALOR ATRIBUÍDO PARA QUILOMETRAGEM NÃO PODE SER VAZIO OU MENOR OU IGUAL A ZERO(0).';
    END IF;

    --VERIFICA SE COD_MOVIMENTACAO É NULL
    IF (F_COD_MOVIMENTACAO IS NULL)
    THEN
        RAISE EXCEPTION 'O VALOR ATRIBUÍDO PARA CÓDIGO MOVIMENTAÇÃO NÃO PODE SER VAZIO.';
    END IF;

    --VERIFICA SE OS DADOS EXISTEM NA TABELA MOVIMENTACAO_DESTINO E, CASO NÃO ENCONTRE, SETA A FLAG.
    IF NOT EXISTS(
            SELECT MD.COD_MOVIMENTACAO
            FROM MOVIMENTACAO_DESTINO MD
            WHERE MD.COD_MOVIMENTACAO = F_COD_MOVIMENTACAO
              AND MD.PLACA = F_PLACA
              AND MD.TIPO_DESTINO = TIPO
        )
    THEN
        EXISTE_DESTINO = FALSE;
    END IF;

    --VERIFICA SE OS DADOS EXISTEM NA TABELA MOVIMENTACAO_ORIGEM E, CASO NÃO ENCONTRE, SETA A FLAG.
    IF NOT EXISTS(
            SELECT MO.COD_MOVIMENTACAO
            FROM MOVIMENTACAO_ORIGEM MO
            WHERE MO.COD_MOVIMENTACAO = F_COD_MOVIMENTACAO
              AND MO.PLACA = F_PLACA
              AND MO.TIPO_ORIGEM = TIPO
        )
    THEN
        EXISTE_ORIGEM = FALSE;
    END IF;

    --VERIFICA SE HÁ INCONSISTÊNCIA NO BANCO.
    IF (EXISTE_DESTINO AND EXISTE_ORIGEM)
    THEN
        RAISE EXCEPTION 'HÁ UMA INCONSISTÊNCIA NO BANCO, A MOVIMENTACAO COM CÓDIGO % TEM O TIPO "EM USO" TANTO NA
             MOVIMENTAÇÃO ORIGEM QUANTO NA MOVIMENTAÇÃO DESTINO.', F_COD_MOVIMENTACAO;
    END IF;

    --VERIFICA SE A MOVIMENTAÇÃO EXISTE NAS TABELAS MOVIMENTACAO_DESTINO E MOVIMENTACAO_ORIGEM.
    IF (EXISTE_DESTINO IS FALSE AND EXISTE_ORIGEM IS FALSE)
    THEN
        RAISE EXCEPTION 'NÃO FOI POSSÍVEL ATUALIZAR A QUILOMETRAGEM! FAVOR VERIFICAR OS SEGUINTES DADOS:
                          CODIGO MOVIMENTAÇÃO: %, PLACA: %',
            F_COD_MOVIMENTACAO, F_PLACA;
    END IF;

    --VERIFICA SE A MOVIMENTAÇÃO EXISTE NAS TABELAS MOVIMENTACAO_DESTINO E MOVIMENTACAO_ORIGEM.
    IF (EXISTE_DESTINO IS FALSE AND EXISTE_ORIGEM IS FALSE)
    THEN
        RAISE EXCEPTION 'NÃO FOI POSSÍVEL ATUALIZAR A QUILOMETRAGEM! FAVOR VERIFICAR OS SEGUINTES DADOS:
                          CODIGO MOVIMENTAÇÃO: %, PLACA: %',
            F_COD_MOVIMENTACAO, F_PLACA;
    END IF;

    -- REALIZA UPDATE NA TABELA CORRESPONDENTE
    IF (EXISTE_DESTINO AND EXISTE_ORIGEM IS FALSE)
    THEN
        UPDATE MOVIMENTACAO_DESTINO
        SET KM_VEICULO = F_KM_ATUALIZADO
        WHERE COD_MOVIMENTACAO = F_COD_MOVIMENTACAO
          AND PLACA = F_PLACA
          AND TIPO_DESTINO = TIPO;
        MOVIMENTACAO_ATUALIZADA = 'DESTINO';
        GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;
    ELSE
        IF (EXISTE_DESTINO IS FALSE AND EXISTE_ORIGEM)
        THEN
            UPDATE MOVIMENTACAO_ORIGEM
            SET KM_VEICULO = F_KM_ATUALIZADO
            WHERE COD_MOVIMENTACAO = F_COD_MOVIMENTACAO
              AND PLACA = F_PLACA
              AND TIPO_ORIGEM = TIPO;
            MOVIMENTACAO_ATUALIZADA = 'ORIGEM';
            GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;
        END IF;
    END IF;

    IF (QTD_LINHAS_ATUALIZADAS > 0)
    THEN
        SELECT 'ATUALIZAÇÃO REALIZADA COM SUCESSO EM MOVIMENTAÇÃO '
                   || MOVIMENTACAO_ATUALIZADA
                   || '! CÓDIGO MOVIMENTAÇÃO: '
                   || F_COD_MOVIMENTACAO
                   || ', PLACA: '
                   || F_PLACA
                   || ', KM_VEICULO: '
                   || F_KM_ATUALIZADO
        INTO AVISO_KM_ATUALIZADO;
    ELSE
        RAISE EXCEPTION 'NÃO FOI POSSÍVEL ATUALIZAR A QUILOMETRAGEM! FAVOR, VERIFICAR OS SEGUINTES DADOS:
                          CODIGO MOVIMENTAÇÃO: %, PLACA: % ' ,
            F_COD_MOVIMENTACAO, F_PLACA;

    END IF;
END
$$;


CREATE OR REPLACE FUNCTION SUPORTE.FUNC_PNEU_ALTERA_PRESSAO_IDEAL_BY_DIMENSAO(F_COD_EMPRESA BIGINT,
                                                                              F_COD_UNIDADE BIGINT,
                                                                              F_COD_DIMENSAO BIGINT,
                                                                              F_NOVA_PRESSAO_RECOMENDADA BIGINT,
                                                                              F_QTD_PNEUS_IMPACTADOS BIGINT,
                                                                              OUT AVISO_PRESSAO_ALTERADA TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    QTD_REAL_PNEUS_IMPACTADOS  BIGINT;
    PRESSAO_MINIMA_RECOMENDADA BIGINT := 25;
    PRESSAO_MAXIMA_RECOMENDADA BIGINT := 150;
BEGIN
    PERFORM SUPORTE.FUNC_HISTORICO_SALVA_EXECUCAO();
    --Verifica se a pressao informada está dentro das recomendadas.
    IF (F_NOVA_PRESSAO_RECOMENDADA NOT BETWEEN PRESSAO_MINIMA_RECOMENDADA AND PRESSAO_MAXIMA_RECOMENDADA)
    THEN
        RAISE EXCEPTION 'Pressão recomendada não está dentro dos valores pré-estabelecidos.
                        Mínima Recomendada: % ---- Máxima Recomendada: %', PRESSAO_MINIMA_RECOMENDADA,
            PRESSAO_MAXIMA_RECOMENDADA;
    END IF;

    -- Verifica se a empresa existe.
    IF NOT EXISTS(SELECT E.CODIGO
                  FROM EMPRESA E
                  WHERE E.CODIGO = F_COD_EMPRESA)
    THEN
        RAISE EXCEPTION 'Empresa de código % não existe!', F_COD_EMPRESA;
    END IF;

    -- Verifica se a unidade existe.
    IF NOT EXISTS(SELECT U.CODIGO
                  FROM UNIDADE U
                  WHERE U.CODIGO = F_COD_UNIDADE)
    THEN
        RAISE EXCEPTION 'Unidade de código % não existe!', F_COD_UNIDADE;
    END IF;

    -- Verifica se existe a dimensão informada.
    IF NOT EXISTS(SELECT DM.CODIGO
                  FROM DIMENSAO_PNEU DM
                  WHERE DM.CODIGO = F_COD_DIMENSAO)
    THEN
        RAISE EXCEPTION 'Dimensao de código % não existe!', F_COD_DIMENSAO;
    END IF;

    -- Verifica se a unidade é da empresa informada.
    IF NOT EXISTS(SELECT U.CODIGO
                  FROM UNIDADE U
                  WHERE U.CODIGO = F_COD_UNIDADE
                    AND U.COD_EMPRESA = F_COD_EMPRESA)
    THEN
        RAISE EXCEPTION 'A unidade % não pertence a empresa %!', F_COD_UNIDADE, F_COD_EMPRESA;
    END IF;

    -- Verifica se algum pneu possui dimensão informada.
    IF NOT EXISTS(SELECT P.COD_DIMENSAO
                  FROM PNEU P
                  WHERE P.COD_DIMENSAO = F_COD_DIMENSAO
                    AND P.COD_UNIDADE = F_COD_UNIDADE
                    AND P.COD_EMPRESA = F_COD_EMPRESA)
    THEN
        RAISE EXCEPTION 'Não existem pneus com a dimensão % na unidade %', F_COD_DIMENSAO, F_COD_UNIDADE;
    END IF;

    -- Verifica quantidade de pneus impactados.
    SELECT COUNT(P.CODIGO)
    FROM PNEU P
    WHERE P.COD_DIMENSAO = F_COD_DIMENSAO
      AND P.COD_UNIDADE = F_COD_UNIDADE
      AND P.COD_EMPRESA = F_COD_EMPRESA
    INTO QTD_REAL_PNEUS_IMPACTADOS;
    IF (QTD_REAL_PNEUS_IMPACTADOS <> F_QTD_PNEUS_IMPACTADOS)
    THEN
        RAISE EXCEPTION 'A quantidade de pneus informados como impactados pela mudança de pressão (%) não condiz com a
                       quantidade real de pneus que serão afetados!', F_QTD_PNEUS_IMPACTADOS;
    END IF;

    UPDATE PNEU
    SET PRESSAO_RECOMENDADA = F_NOVA_PRESSAO_RECOMENDADA
    WHERE COD_DIMENSAO = F_COD_DIMENSAO
      AND COD_UNIDADE = F_COD_UNIDADE
      AND COD_EMPRESA = F_COD_EMPRESA;

    SELECT CONCAT('Pressão recomendada dos pneus com dimensão ',
                  F_COD_DIMENSAO,
                  ' da unidade ',
                  F_COD_UNIDADE,
                  ' alterada para ',
                  F_NOVA_PRESSAO_RECOMENDADA,
                  ' psi')
    INTO AVISO_PRESSAO_ALTERADA;
END;
$$;


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



CREATE OR REPLACE FUNCTION SUPORTE.FUNC_PNEU_ALTERA_RESTRICAO_PNEU(F_COD_EMPRESA BIGINT,
                                                                   F_COD_UNIDADE BIGINT,
                                                                   F_TOLERANCIA_CALIBRAGEM REAL,
                                                                   F_TOLERANCIA_INSPECAO REAL,
                                                                   F_SULCO_MINIMO_RECAPAGEM REAL,
                                                                   F_SULCO_MINIMO_DESCARTE REAL,
                                                                   F_PERIODO_AFERICAO_PRESSAO BIGINT,
                                                                   F_PERIODO_AFERICAO_SULCO BIGINT,
                                                                   OUT PARAMETRIZACAO_ATUALIZADA TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    COD_PARAMETRIZACAO_EXISTENTE   BIGINT := (SELECT COD_EMPRESA
                                              FROM PNEU_RESTRICAO_UNIDADE
                                              WHERE COD_EMPRESA = F_COD_EMPRESA
                                                AND COD_UNIDADE = F_COD_UNIDADE);
    TOLERANCIA_CALIBRAGEM_ATUAL    REAL;
    SULCO_MINIMO_RECAPAGEM_ATUAL   REAL;
    SULCO_MINIMO_DESCARTE_ATUAL    REAL;
    TOLERANCIA_INSPECAO_ATUAL      REAL;
    PERIODO_AFERICAO_PRESSAO_ATUAL BIGINT;
    PERIODO_AFERICAO_SULCO_ATUAL   BIGINT;
BEGIN
    PERFORM SUPORTE.FUNC_HISTORICO_SALVA_EXECUCAO();
    --SETAR VARIÁVEIS
    SELECT TOLERANCIA_CALIBRAGEM,
           SULCO_MINIMO_RECAPAGEM,
           SULCO_MINIMO_DESCARTE,
           TOLERANCIA_INSPECAO,
           PERIODO_AFERICAO_PRESSAO,
           PERIODO_AFERICAO_SULCO
    INTO
        TOLERANCIA_CALIBRAGEM_ATUAL,
        SULCO_MINIMO_RECAPAGEM_ATUAL,
        SULCO_MINIMO_DESCARTE_ATUAL,
        TOLERANCIA_INSPECAO_ATUAL,
        PERIODO_AFERICAO_PRESSAO_ATUAL,
        PERIODO_AFERICAO_SULCO_ATUAL
    FROM PNEU_RESTRICAO_UNIDADE
    WHERE COD_EMPRESA = F_COD_EMPRESA
      AND COD_UNIDADE = F_COD_UNIDADE;

    --GARANTE QUE EMPRESA POSSUI UNIDADE.
    PERFORM FUNC_GARANTE_EMPRESA_POSSUI_UNIDADE(F_COD_EMPRESA, F_COD_UNIDADE);

    --VERIFICA SE A EMPRESA POSSUI PARAMETRIZACÃO NESSA UNIDADE.
    IF (COD_PARAMETRIZACAO_EXISTENTE IS NULL)
    THEN
        RAISE EXCEPTION 'ERRO! A EMPRESA: % NÃO POSSUI PARAMETRIZAÇÃO NA UNIDADE: %', F_COD_EMPRESA, F_COD_UNIDADE;
    END IF;

    --VERIFICA SE ALGUM DADO É MENOR QUE ZERO.
    IF (F_TOLERANCIA_CALIBRAGEM < 0)
    THEN
        RAISE EXCEPTION 'ERRO! O VALOR ATRIBUÍDO PARA TOLERÂNCIA DE CALIBRAGEM DEVE SER MAIOR QUE 0(ZERO)';
    END IF;

    IF (F_SULCO_MINIMO_RECAPAGEM < 0)
    THEN
        RAISE EXCEPTION 'ERRO! O VALOR ATRIBUÍDO PARA SULCO MÍNIMO RECAPAGEM DEVE SER MAIOR QUE 0(ZERO)';
    END IF;

    IF (F_SULCO_MINIMO_DESCARTE < 0)
    THEN
        RAISE EXCEPTION 'ERRO! O VALOR ATRIBUÍDO PARA SULCO MÍNIMO DESCARTE DEVE SER MAIOR QUE 0(ZERO)';
    END IF;

    IF (F_TOLERANCIA_INSPECAO < 0)
    THEN
        RAISE EXCEPTION 'ERRO! O VALOR ATRIBUÍDO PARA TOLERÂNCIA INSPEÇÃO DEVE SER MAIOR QUE 0(ZERO)';
    END IF;

    IF (F_PERIODO_AFERICAO_PRESSAO < 0)
    THEN
        RAISE EXCEPTION 'ERRO! O VALOR ATRIBUÍDO PARA PERÍODO AFERIÇÃO PRESSÃO DEVE SER MAIOR QUE 0(ZERO)';
    END IF;

    IF (F_PERIODO_AFERICAO_SULCO < 0)
    THEN
        RAISE EXCEPTION 'ERRO! O VALOR ATRIBUÍDO PARA PERÍODO AFERIÇÃO SULCO DEVE SER MAIOR QUE 0(ZERO)';
    END IF;

    --ATUALIZA DADOS DA PARAMETRIZAÇÃO.
    UPDATE PNEU_RESTRICAO_UNIDADE
    SET TOLERANCIA_CALIBRAGEM    = F_IF(F_TOLERANCIA_CALIBRAGEM IS NULL, TOLERANCIA_CALIBRAGEM_ATUAL,
                                        F_TOLERANCIA_CALIBRAGEM),
        SULCO_MINIMO_RECAPAGEM   = F_IF(F_SULCO_MINIMO_RECAPAGEM IS NULL, SULCO_MINIMO_RECAPAGEM_ATUAL,
                                        F_SULCO_MINIMO_RECAPAGEM),
        SULCO_MINIMO_DESCARTE    = F_IF(F_SULCO_MINIMO_DESCARTE IS NULL, SULCO_MINIMO_DESCARTE_ATUAL,
                                        F_SULCO_MINIMO_DESCARTE),
        TOLERANCIA_INSPECAO      = F_IF(F_TOLERANCIA_INSPECAO IS NULL, TOLERANCIA_INSPECAO_ATUAL,
                                        F_TOLERANCIA_INSPECAO),
        PERIODO_AFERICAO_PRESSAO = F_IF(F_PERIODO_AFERICAO_PRESSAO IS NULL, PERIODO_AFERICAO_PRESSAO_ATUAL,
                                        F_PERIODO_AFERICAO_PRESSAO),
        PERIODO_AFERICAO_SULCO   = F_IF(F_PERIODO_AFERICAO_SULCO IS NULL, PERIODO_AFERICAO_SULCO_ATUAL,
                                        F_PERIODO_AFERICAO_SULCO)
    WHERE COD_EMPRESA = F_COD_EMPRESA
      AND COD_UNIDADE = F_COD_UNIDADE;

    --MENSAGEM DE SUCESSO.
    SELECT 'DADOS ATUALIZADOS COM SUCESSO!!'
               || ' EMPRESA: '
               || COD_EMPRESA
               || ', UNIDADE: '
               || COD_UNIDADE
               || ', TOLERANCIA CALIBRAGEM: '
               || TOLERANCIA_CALIBRAGEM
               || ', SULCO MÍNIMO RECAPAGEM: '
               || SULCO_MINIMO_RECAPAGEM
               || ', SULCO MÍNIMO DESCARTE: '
               || SULCO_MINIMO_DESCARTE
               || ' TOLERANCIA INSPEÇÃO: '
               || TOLERANCIA_INSPECAO
               || ',PERÍODO AFERIÇÃO PRESSÃO: '
               || PERIODO_AFERICAO_PRESSAO
               || ', PERÍODO AFERIÇÃO SULCO: '
               || PERIODO_AFERICAO_SULCO
    FROM PNEU_RESTRICAO_UNIDADE
    WHERE COD_EMPRESA = F_COD_EMPRESA
      AND COD_UNIDADE = F_COD_UNIDADE
    INTO PARAMETRIZACAO_ATUALIZADA;
END
$$;



CREATE OR REPLACE FUNCTION SUPORTE.FUNC_PNEU_CADASTRA_DIMENSAO_PNEU(F_ALTURA BIGINT,
                                                                    F_LARGURA BIGINT,
                                                                    F_ARO REAL,
                                                                    OUT AVISO_DIMENSAO_CRIADA TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    COD_DIMENSAO_EXISTENTE BIGINT := (SELECT CODIGO
                                      FROM DIMENSAO_PNEU
                                      WHERE LARGURA = F_LARGURA
                                        AND ALTURA = F_ALTURA
                                        AND ARO = F_ARO);
    COD_DIMENSAO_CRIADA    BIGINT;
BEGIN
    PERFORM SUPORTE.FUNC_HISTORICO_SALVA_EXECUCAO();
    --VERIFICA SE OS DADOS INFORMADOS SÃO MAIORES QUE 0.
    IF(F_ALTURA < 0)
    THEN
        RAISE EXCEPTION 'O VALOR ATRIBUÍDO PARA ALTURA DEVE SER MAIOR QUE 0(ZERO). VALOR INFORMADO: %', F_ALTURA;
    END IF;

    IF(F_LARGURA < 0)
    THEN
        RAISE EXCEPTION 'O VALOR ATRIBUÍDO PARA LARGURA DEVE SER MAIOR QUE 0(ZERO). VALOR INFORMADO: %', F_LARGURA;
    END IF;

    IF(F_ARO < 0)
    THEN
        RAISE EXCEPTION 'O VALOR ATRIBUÍDO PARA ARO DEVE SER MAIOR QUE 0(ZERO). VALOR INFORMADO: %', F_ARO;
    END IF;

    --VERIFICA SE ESSA DIMENSÃO EXISTE NA BASE DE DADOS.
    IF (COD_DIMENSAO_EXISTENTE IS NOT NULL)
    THEN
        RAISE EXCEPTION 'ERRO! ESSA DIMENSÃO JÁ ESTÁ CADASTRADA, POSSUI O CÓDIGO = %.', COD_DIMENSAO_EXISTENTE;
    END IF;

    --ADICIONA NOVA DIMENSÃO E RETORNA SEU ID.
    INSERT INTO DIMENSAO_PNEU(ALTURA, LARGURA, ARO)
    VALUES (F_ALTURA, F_LARGURA, F_ARO) RETURNING CODIGO INTO COD_DIMENSAO_CRIADA;

    --MENSAGEM DE SUCESSO.
    SELECT 'DIMENSÃO CADASTRADA COM SUCESSO! DIMENSÃO: ' || F_LARGURA || '/' || F_ALTURA || 'R' || F_ARO ||
           ' COM CÓDIGO: '
               || COD_DIMENSAO_CRIADA || '.'
    INTO AVISO_DIMENSAO_CRIADA;
END
$$;



CREATE OR REPLACE FUNCTION SUPORTE.FUNC_PNEU_DELETA_PNEU(F_COD_UNIDADE BIGINT,
                                                         F_CODIGO_PNEU BIGINT,
                                                         F_CODIGO_CLIENTE TEXT,
                                                         F_MOTIVO_DELECAO TEXT,
                                                         OUT AVISO_PNEU_DELETADO TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    V_STATUS_PNEU_ANALISE CONSTANT   TEXT := 'ANALISE';
    V_QTD_LINHAS_ATUALIZADAS         BIGINT;
    V_COD_AFERICAO                   BIGINT[];
    V_COD_AFERICAO_FOREACH           BIGINT;
    V_QTD_AFERICAO_VALORES           BIGINT;
    V_QTD_AFERICAO_VALORES_DELETADOS BIGINT;
BEGIN
    PERFORM SUPORTE.FUNC_HISTORICO_SALVA_EXECUCAO();
    -- Verifica se o pneu existe.
    IF ((SELECT COUNT(P.CODIGO)
         FROM PNEU_DATA P
         WHERE P.CODIGO = F_CODIGO_PNEU
           AND P.COD_UNIDADE = F_COD_UNIDADE
           AND P.CODIGO_CLIENTE = F_CODIGO_CLIENTE) <= 0)
    THEN
        RAISE EXCEPTION 'Nenhum pneu encontrado com estes parâmetros: Código %, Código cliente % e Unidade %',
            F_CODIGO_PNEU, F_CODIGO_CLIENTE, F_COD_UNIDADE;
    END IF;

    -- Verifica se o pneu está aplicado.
    IF ((SELECT COUNT(VP.PLACA)
         FROM VEICULO_PNEU VP
         WHERE VP.COD_PNEU = F_CODIGO_PNEU
           AND VP.COD_UNIDADE = F_COD_UNIDADE) > 0)
    THEN
        RAISE EXCEPTION 'O pneu não pode ser deletado pois está aplicado! Parâmetros: Código %, Código cliente % e
            Unidade %', F_CODIGO_PNEU, F_CODIGO_CLIENTE, F_COD_UNIDADE;
    END IF;

    -- Verifica se o pneu está em análise.
    IF ((SELECT COUNT(P.CODIGO)
         FROM PNEU_DATA P
         WHERE P.CODIGO = F_CODIGO_PNEU
           AND P.COD_UNIDADE = F_COD_UNIDADE
           AND P.CODIGO_CLIENTE = F_CODIGO_CLIENTE
           AND P.STATUS = V_STATUS_PNEU_ANALISE) > 0)
    THEN
        RAISE EXCEPTION 'O pneu não pode ser deletado pois está em análise! Parâmetros: Código %, Código cliente % e
            Unidade %', F_CODIGO_PNEU, F_CODIGO_CLIENTE, F_COD_UNIDADE;
    END IF;

    -- Verifica se pneu é integrado
    IF EXISTS(SELECT IPC.COD_PNEU_CADASTRO_PROLOG
              FROM INTEGRACAO.PNEU_CADASTRADO IPC
              WHERE IPC.COD_PNEU_CADASTRO_PROLOG = F_CODIGO_PNEU
                AND IPC.COD_UNIDADE_CADASTRO = F_COD_UNIDADE)
    THEN
        -- Deleta Pneu (Não temos deleção lógica)
        DELETE
        FROM INTEGRACAO.PNEU_CADASTRADO
        WHERE COD_PNEU_CADASTRO_PROLOG = F_CODIGO_PNEU
          AND COD_UNIDADE_CADASTRO = F_COD_UNIDADE;
    END IF;

    -- Deleta pneu Prolog.
    UPDATE PNEU_DATA
    SET DELETADO            = TRUE,
        DATA_HORA_DELETADO  = NOW(),
        PG_USERNAME_DELECAO = SESSION_USER,
        MOTIVO_DELECAO      = F_MOTIVO_DELECAO
    WHERE CODIGO = F_CODIGO_PNEU
      AND COD_UNIDADE = F_COD_UNIDADE
      AND CODIGO_CLIENTE = F_CODIGO_CLIENTE;

    GET DIAGNOSTICS V_QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

    IF (V_QTD_LINHAS_ATUALIZADAS IS NULL OR V_QTD_LINHAS_ATUALIZADAS <= 0)
    THEN
        RAISE EXCEPTION 'Erro ao deletar o pneu de Código %, Código cliente % e Unidade %',
            F_CODIGO_PNEU, F_CODIGO_CLIENTE, F_COD_UNIDADE;
    END IF;

    -- Verifica se o pneu está em afericao_manutencao_data.
    IF (SELECT EXISTS(SELECT AM.COD_AFERICAO
                      FROM AFERICAO_MANUTENCAO_DATA AM
                      WHERE AM.COD_PNEU = F_CODIGO_PNEU
                        AND AM.COD_UNIDADE = F_COD_UNIDADE
                        AND AM.DELETADO = FALSE))
    THEN
        UPDATE AFERICAO_MANUTENCAO_DATA
        SET DELETADO            = TRUE,
            DATA_HORA_DELETADO  = NOW(),
            PG_USERNAME_DELECAO = SESSION_USER,
            MOTIVO_DELECAO      = F_MOTIVO_DELECAO
        WHERE COD_PNEU = F_CODIGO_PNEU
          AND COD_UNIDADE = F_COD_UNIDADE
          AND DELETADO = FALSE;

        GET DIAGNOSTICS V_QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

        -- Garante que a deleção foi realizada.
        IF (V_QTD_LINHAS_ATUALIZADAS IS NULL OR V_QTD_LINHAS_ATUALIZADAS <= 0)
        THEN
            RAISE EXCEPTION 'Erro ao deletar o pneu de Código %, Código Cliente % e Unidade % '
                'em afericao_manutencao_data', F_CODIGO_PNEU, F_CODIGO_CLIENTE, F_COD_UNIDADE;
        END IF;
    END IF;

    -- Verifica se o pneu está em afericao_valores_data.
    IF (SELECT EXISTS(SELECT AV.COD_AFERICAO
                      FROM AFERICAO_VALORES_DATA AV
                      WHERE AV.COD_PNEU = F_CODIGO_PNEU
                        AND AV.COD_UNIDADE = F_COD_UNIDADE
                        AND AV.DELETADO = FALSE))
    THEN
        UPDATE AFERICAO_VALORES_DATA
        SET DELETADO            = TRUE,
            DATA_HORA_DELETADO  = NOW(),
            PG_USERNAME_DELECAO = SESSION_USER,
            MOTIVO_DELECAO      = F_MOTIVO_DELECAO
        WHERE COD_PNEU = F_CODIGO_PNEU
          AND COD_UNIDADE = F_COD_UNIDADE
          AND DELETADO = FALSE;

        GET DIAGNOSTICS V_QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

        -- Garante que a deleção foi realizada.
        IF (V_QTD_LINHAS_ATUALIZADAS IS NULL OR V_QTD_LINHAS_ATUALIZADAS <= 0)
        THEN
            RAISE EXCEPTION 'Erro ao deletar o pneu de Código %, Código cliente % e Unidade % em afericao_valores_data',
                F_CODIGO_PNEU, F_CODIGO_CLIENTE, F_COD_UNIDADE;
        END IF;
    END IF;

    --Busca todos os cod_afericao deletados a partir do pneu.
    SELECT ARRAY_AGG(AV.COD_AFERICAO)
    FROM AFERICAO_VALORES_DATA AV
    WHERE AV.COD_PNEU = F_CODIGO_PNEU
      AND AV.COD_UNIDADE = F_COD_UNIDADE
      AND AV.DELETADO IS TRUE
    INTO V_COD_AFERICAO;

    -- Verifica se algum valor foi deletado em afericao_valores_data.
    IF (V_COD_AFERICAO IS NOT NULL AND ARRAY_LENGTH(V_COD_AFERICAO, 1) > 0)
    THEN
        -- Iteração com cada cod_afericao deletado em afericao_valores_data.
        FOREACH V_COD_AFERICAO_FOREACH IN ARRAY V_COD_AFERICAO
            LOOP
                -- Coleta a quantidade de aferições em afericao_valores_data.
                V_QTD_AFERICAO_VALORES = (SELECT COUNT(AVD.COD_AFERICAO)
                                          FROM AFERICAO_VALORES_DATA AVD
                                          WHERE AVD.COD_AFERICAO = V_COD_AFERICAO_FOREACH);

                -- Coleta a quantidade de aferições deletadas em afericao_valores_data.
                V_QTD_AFERICAO_VALORES_DELETADOS = (SELECT COUNT(AVD.COD_AFERICAO)
                                                    FROM AFERICAO_VALORES_DATA AVD
                                                    WHERE AVD.COD_AFERICAO = V_COD_AFERICAO_FOREACH
                                                      AND AVD.DELETADO IS TRUE);

                -- Verifica se todos os valores da aferição foram deletados, para que assim seja deletada a aferição também.
                IF (V_QTD_AFERICAO_VALORES = V_QTD_AFERICAO_VALORES_DELETADOS)
                THEN
                    UPDATE AFERICAO_DATA
                    SET DELETADO            = TRUE,
                        DATA_HORA_DELETADO  = NOW(),
                        PG_USERNAME_DELECAO = SESSION_USER,
                        MOTIVO_DELECAO      = F_MOTIVO_DELECAO
                    WHERE CODIGO = V_COD_AFERICAO_FOREACH;

                    GET DIAGNOSTICS V_QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

                    -- Garante que a deleção foi realizada.
                    IF (V_QTD_LINHAS_ATUALIZADAS IS NULL OR V_QTD_LINHAS_ATUALIZADAS <= 0)
                    THEN
                        RAISE EXCEPTION 'Erro ao deletar aferição com Código: %, Unidade: %',
                            V_COD_AFERICAO_FOREACH, F_COD_UNIDADE;
                    END IF;
                END IF;
            END LOOP;
    END IF;

    SELECT 'PNEU DELETADO: '
               || F_CODIGO_PNEU
               || ', CÓDIGO DO CLIENTE: '
               || F_CODIGO_CLIENTE
               || ', CÓDIGO DA UNIDADE: '
               || F_COD_UNIDADE
    INTO AVISO_PNEU_DELETADO;
END
$$;



CREATE OR REPLACE FUNCTION SUPORTE.FUNC_PNEU_REMOVE_VINCULO_PNEU(F_CPF_SOLICITANTE BIGINT,
                                                                 F_COD_UNIDADE BIGINT,
                                                                 F_PLACA_VEICULO TEXT,
                                                                 F_LISTA_COD_PNEUS BIGINT[],
                                                                 OUT AVISO_PNEUS_DESVINCULADOS TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    STATUS_PNEU_ESTOQUE              TEXT                     := 'ESTOQUE';
    STATUS_PNEU_EM_USO               TEXT                     := 'EM_USO';
    DATA_HORA_ATUAL                  TIMESTAMP WITH TIME ZONE := NOW();
    COD_PNEU_DA_VEZ                  BIGINT;
    COD_MOVIMENTACAO_CRIADA          BIGINT;
    COD_PROCESSO_MOVIMENTACAO_CRIADO BIGINT;
    VIDA_ATUAL_PNEU                  BIGINT;
    POSICAO_PNEU                     INTEGER;
    KM_ATUAL_VEICULO                 BIGINT                   := (SELECT V.KM
                                                                  FROM VEICULO V
                                                                  WHERE V.COD_UNIDADE = F_COD_UNIDADE
                                                                    AND V.PLACA = F_PLACA_VEICULO);
    NOME_COLABORADOR                 TEXT                     := (SELECT C.NOME
                                                                  FROM COLABORADOR C
                                                                  WHERE C.CPF = F_CPF_SOLICITANTE);
BEGIN
    PERFORM SUPORTE.FUNC_HISTORICO_SALVA_EXECUCAO();
    -- Verifica se colaborador possui integridade com unidade;
    PERFORM FUNC_GARANTE_INTEGRIDADE_UNIDADE_COLABORADOR(F_COD_UNIDADE, F_CPF_SOLICITANTE);

    -- Verifica se unidade existe;
    PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE);

    -- Verifica se veículo existe;
    PERFORM FUNC_GARANTE_VEICULO_EXISTE(F_COD_UNIDADE, F_PLACA_VEICULO);

    -- Verifica quantiade de pneus recebida;
    IF (ARRAY_LENGTH(F_LISTA_COD_PNEUS, 1) > 0)
    THEN
        -- Cria processo para movimentação
        INSERT INTO MOVIMENTACAO_PROCESSO(COD_UNIDADE, DATA_HORA, CPF_RESPONSAVEL, OBSERVACAO)
        VALUES (F_COD_UNIDADE,
                DATA_HORA_ATUAL,
                F_CPF_SOLICITANTE,
                'Processo para desvincular o pneu de uma placa')
        RETURNING CODIGO INTO COD_PROCESSO_MOVIMENTACAO_CRIADO;

        FOREACH COD_PNEU_DA_VEZ IN ARRAY F_LISTA_COD_PNEUS
            LOOP
                -- Verifica se pneu não está vinculado a placa informada;
                IF NOT EXISTS(SELECT VP.PLACA
                              FROM VEICULO_PNEU VP
                              WHERE VP.PLACA = F_PLACA_VEICULO
                                AND VP.COD_PNEU = COD_PNEU_DA_VEZ)
                THEN
                    RAISE EXCEPTION 'Erro! O pneu com código: % não está vinculado ao veículo %',
                        COD_PNEU_DA_VEZ, F_PLACA_VEICULO;
                END IF;

                -- Busca vida atual e posicao do pneu;
                SELECT P.VIDA_ATUAL, VP.POSICAO
                FROM PNEU P
                         JOIN VEICULO_PNEU VP ON P.CODIGO = VP.COD_PNEU
                WHERE P.CODIGO = COD_PNEU_DA_VEZ
                INTO VIDA_ATUAL_PNEU, POSICAO_PNEU;

                IF (COD_PROCESSO_MOVIMENTACAO_CRIADO > 0)
                THEN
                    -- Insere movimentação retornando o código da mesma;
                    INSERT INTO MOVIMENTACAO(COD_MOVIMENTACAO_PROCESSO,
                                             COD_UNIDADE,
                                             COD_PNEU,
                                             SULCO_INTERNO,
                                             SULCO_CENTRAL_INTERNO,
                                             SULCO_EXTERNO,
                                             VIDA,
                                             OBSERVACAO,
                                             SULCO_CENTRAL_EXTERNO)
                    SELECT COD_PROCESSO_MOVIMENTACAO_CRIADO,
                           F_COD_UNIDADE,
                           COD_PNEU_DA_VEZ,
                           P.ALTURA_SULCO_INTERNO,
                           P.ALTURA_SULCO_CENTRAL_INTERNO,
                           P.ALTURA_SULCO_EXTERNO,
                           VIDA_ATUAL_PNEU,
                           NULL,
                           P.ALTURA_SULCO_CENTRAL_EXTERNO
                    FROM PNEU P
                    WHERE P.CODIGO = COD_PNEU_DA_VEZ
                    RETURNING CODIGO INTO COD_MOVIMENTACAO_CRIADA;

                    -- Insere destino da movimentação;
                    INSERT INTO MOVIMENTACAO_DESTINO(COD_MOVIMENTACAO, TIPO_DESTINO)
                    VALUES (COD_MOVIMENTACAO_CRIADA, STATUS_PNEU_ESTOQUE);

                    -- Insere origem da movimentação;
                    PERFORM FUNC_MOVIMENTACAO_INSERT_MOVIMENTACAO_VEICULO_ORIGEM(COD_PNEU_DA_VEZ,
                                                                                 F_COD_UNIDADE,
                                                                                 STATUS_PNEU_EM_USO,
                                                                                 COD_MOVIMENTACAO_CRIADA,
                                                                                 F_PLACA_VEICULO,
                                                                                 KM_ATUAL_VEICULO,
                                                                                 POSICAO_PNEU);

                    -- Remove pneu do vinculo;
                    DELETE FROM VEICULO_PNEU WHERE COD_PNEU = COD_PNEU_DA_VEZ AND PLACA = F_PLACA_VEICULO;

                    -- Atualiza status do pneu
                    UPDATE PNEU
                    SET STATUS = STATUS_PNEU_ESTOQUE
                    WHERE CODIGO = COD_PNEU_DA_VEZ
                      AND COD_UNIDADE = F_COD_UNIDADE;

                    -- Verifica se o pneu possui serviços em aberto;
                    IF EXISTS(SELECT AM.COD_PNEU
                              FROM AFERICAO_MANUTENCAO AM
                              WHERE AM.COD_UNIDADE = F_COD_UNIDADE
                                AND AM.COD_PNEU = COD_PNEU_DA_VEZ
                                AND AM.DATA_HORA_RESOLUCAO IS NULL
                                AND AM.CPF_MECANICO IS NULL
                                AND AM.FECHADO_AUTOMATICAMENTE_MOVIMENTACAO IS FALSE
                                AND AM.FECHADO_AUTOMATICAMENTE_INTEGRACAO IS FALSE)
                    THEN
                        -- REmove serviços em aberto;
                        UPDATE AFERICAO_MANUTENCAO
                        SET FECHADO_AUTOMATICAMENTE_MOVIMENTACAO = TRUE,
                            COD_PROCESSO_MOVIMENTACAO            = COD_PROCESSO_MOVIMENTACAO_CRIADO,
                            DATA_HORA_RESOLUCAO                  = DATA_HORA_ATUAL
                        WHERE COD_UNIDADE = F_COD_UNIDADE
                          AND COD_PNEU = COD_PNEU_DA_VEZ
                          AND DATA_HORA_RESOLUCAO IS NULL
                          AND CPF_MECANICO IS NULL
                          AND FECHADO_AUTOMATICAMENTE_MOVIMENTACAO IS FALSE
                          AND FECHADO_AUTOMATICAMENTE_INTEGRACAO IS FALSE;
                    END IF;
                ELSE
                    RAISE EXCEPTION 'Erro! Não foi possível realizar o processo de movimentação para o pneu código: %',
                        COD_PNEU_DA_VEZ;
                END IF;
            END LOOP;
    ELSE
        RAISE EXCEPTION 'Erro! Precisa-se de pelo menos um (1) pneu para realizar a operação!';
    END IF;

    -- Mensagem de sucesso;
    SELECT 'Movimentação realizada com sucesso!! Autorizada por ' || NOME_COLABORADOR ||
           ' com CPF: ' || F_CPF_SOLICITANTE || '. Os pneus que estavam na placa ' || F_PLACA_VEICULO ||
           ' foram movidos para estoque.'
    INTO AVISO_PNEUS_DESVINCULADOS;
END
$$;



CREATE OR REPLACE FUNCTION SUPORTE.FUNC_PNEU_RETORNA_PNEU_DO_DESCARTE(F_COD_EMPRESA BIGINT,
                                                                      F_COD_UNIDADE BIGINT,
                                                                      F_COD_PNEU BIGINT,
                                                                      F_NUMERO_FOGO_PNEU VARCHAR,
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
    PERFORM FUNC_GARANTE_PNEU_EXISTE(F_COD_EMPRESA, F_COD_UNIDADE, F_COD_PNEU, F_NUMERO_FOGO_PNEU);

    -- VERIFICA SE O STATUS DO PNEU ESTÁ COMO 'DESCARTE'.
    IF (SELECT P.STATUS
        FROM PNEU P
        WHERE P.CODIGO_CLIENTE = F_NUMERO_FOGO_PNEU
          AND P.CODIGO = F_COD_PNEU
          AND P.COD_UNIDADE = F_COD_UNIDADE) != F_STATUS_PNEU_DESCARTE
    THEN
        RAISE EXCEPTION 'Pneu de número de fogo %, com código % da unidade %, não está com status = %!',
            F_NUMERO_FOGO_PNEU, F_COD_PNEU, F_COD_UNIDADE, F_STATUS_PNEU_DESCARTE;
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
        RAISE EXCEPTION '[INCONSISTÊNCIA] A ultima movimentação do pneu de número de fogo %, com código % da unidade %,
      não foi para %!',
            F_NUMERO_FOGO_PNEU, F_COD_PNEU, F_COD_UNIDADE, F_STATUS_PNEU_DESCARTE;
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
          AND CODIGO_CLIENTE = F_NUMERO_FOGO_PNEU
          AND COD_UNIDADE = F_COD_UNIDADE
          AND COD_EMPRESA = F_COD_EMPRESA;
    ELSE
        UPDATE PNEU
        SET STATUS = F_STATUS_PNEU_ESTOQUE
        WHERE CODIGO = F_COD_PNEU
          AND CODIGO_CLIENTE = F_NUMERO_FOGO_PNEU
          AND COD_UNIDADE = F_COD_UNIDADE
          AND COD_EMPRESA = F_COD_EMPRESA;
    END IF;

    SELECT CONCAT('Pneu retornado para ', (SELECT P.STATUS FROM PNEU P WHERE P.CODIGO = F_COD_PNEU),
                  ', Código: ', F_COD_PNEU,
                  ', Número de fogo: ', F_NUMERO_FOGO_PNEU,
                  ', Unidade: ', F_COD_UNIDADE, ' - ', (SELECT U.NOME FROM UNIDADE U WHERE U.CODIGO = F_COD_UNIDADE),
                  ', Empresa: ', F_COD_EMPRESA, ' - ', (SELECT E.NOME FROM EMPRESA E WHERE E.CODIGO = F_COD_EMPRESA))
    INTO AVISO_PNEU_RETORNADO;
END;
$$;



CREATE OR REPLACE FUNCTION SUPORTE.FUNC_PNEU_RETROCEDER_VIDA_PNEU(F_COD_UNIDADE_PNEU BIGINT,
                                                                  F_COD_CLIENTE_PNEU TEXT,
                                                                  F_VIDA_NOVA_PNEU INTEGER,
                                                                  F_MOTIVO_RETROCESSO TEXT,
                                                                  OUT AVISO_PNEU_VIDA_RETROCEDIDA TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    V_COD_PNEU      CONSTANT      BIGINT := (SELECT P.CODIGO
                                             FROM PNEU P
                                             WHERE P.COD_UNIDADE = F_COD_UNIDADE_PNEU
                                               AND P.CODIGO_CLIENTE = F_COD_CLIENTE_PNEU);
    V_VIDA_CADASTRO CONSTANT      BIGINT := COALESCE((SELECT PSR.VIDA
                                                      FROM PUBLIC.PNEU_SERVICO_REALIZADO PSR
                                                      WHERE PSR.COD_PNEU = V_COD_PNEU
                                                        AND PSR.FONTE_SERVICO_REALIZADO = 'FONTE_CADASTRO') + 1,
                                                     1);
    V_VIDA_ATUAL_PNEU             INTEGER;
    V_COD_MODELO_BANDA_ATUAL_PNEU BIGINT;
    V_VALOR_BANDA_ATUAL_PNEU      REAL;
    V_COD_SERVICOS_DELETADOS      BIGINT[];
BEGIN
    PERFORM SUPORTE.FUNC_HISTORICO_SALVA_EXECUCAO();
    -- Validações.
    PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE_PNEU);

    IF (SELECT NOT EXISTS(SELECT P.CODIGO
                          FROM PNEU P
                          WHERE P.COD_UNIDADE = F_COD_UNIDADE_PNEU
                            AND P.CODIGO_CLIENTE = F_COD_CLIENTE_PNEU))
    THEN
        RAISE EXCEPTION 'Pneu da unidade % e código % não encontrado!', F_COD_UNIDADE_PNEU, F_COD_CLIENTE_PNEU;
    END IF;

    -- Busca valores.
    -- Feito após a primeira validação pois caso o pneu com os parâmetros informados não exista, queremos
    -- retornar um erro formatado e não que o STRICT estoure.
    SELECT P.VIDA_ATUAL, PVV.COD_MODELO_BANDA, PVV.VALOR
    INTO STRICT V_VIDA_ATUAL_PNEU, V_COD_MODELO_BANDA_ATUAL_PNEU, V_VALOR_BANDA_ATUAL_PNEU
    FROM PNEU P
             LEFT JOIN PNEU_VALOR_VIDA PVV ON P.CODIGO = PVV.COD_PNEU AND P.VIDA_ATUAL = PVV.VIDA
    WHERE P.CODIGO = V_COD_PNEU;

    IF (SELECT P.STATUS
        FROM PNEU P
        WHERE P.CODIGO = V_COD_PNEU) = 'DESCARTE'
    THEN
        RAISE EXCEPTION 'O pneu % está descartado e não pode ter sua vida retrocedida!', F_COD_CLIENTE_PNEU;
    END IF;

    -- Esse cenário já é coberto pelo conjunto das duas próximas validações, mas mantemos ele para retornar uma mensagem
    -- de erro melhor.
    IF V_VIDA_ATUAL_PNEU = 1
    THEN
        RAISE EXCEPTION 'Só é possível retroceder a vida de pneus acima da vida 1! Vida atual: %', V_VIDA_ATUAL_PNEU;
    END IF;

    IF F_VIDA_NOVA_PNEU <= 0
    THEN
        RAISE EXCEPTION 'A nova vida do pneu precisa ser um valor maior do que 0! Nova: %', F_VIDA_NOVA_PNEU;
    END IF;

    IF F_VIDA_NOVA_PNEU >= V_VIDA_ATUAL_PNEU
    THEN
        RAISE EXCEPTION 'A nova vida do pneu deve ser menor do que a vida atual! Nova: % | Atual: %',
            F_VIDA_NOVA_PNEU,
            V_VIDA_ATUAL_PNEU;
    END IF;

    -- Lógica de retrocesso.
    IF (SELECT EXISTS(SELECT PVV.COD_PNEU
                      FROM PNEU_VALOR_VIDA PVV
                      WHERE PVV.COD_PNEU = V_COD_PNEU))
    THEN
        WITH SERVICOS_DELETADOS AS (
            UPDATE PUBLIC.PNEU_SERVICO_REALIZADO_DATA
                SET DELETADO = TRUE,
                    DATA_HORA_DELETADO = NOW(),
                    PG_USERNAME_DELECAO = SESSION_USER,
                    MOTIVO_DELECAO = F_MOTIVO_RETROCESSO
                WHERE DELETADO = FALSE
                    AND COD_PNEU = V_COD_PNEU
                    AND (VIDA > F_VIDA_NOVA_PNEU
                        OR VIDA = F_VIDA_NOVA_PNEU AND EXISTS(SELECT PSRIVD.COD_SERVICO_REALIZADO
                                                              FROM PNEU_SERVICO_REALIZADO_INCREMENTA_VIDA_DATA PSRIVD
                                                              WHERE PSRIVD.COD_SERVICO_REALIZADO = CODIGO))
                RETURNING CODIGO
        )
        SELECT ARRAY_AGG(SD.CODIGO)
        INTO STRICT V_COD_SERVICOS_DELETADOS
        FROM SERVICOS_DELETADOS SD;

        PERFORM FUNC_GARANTE_UPDATE_OK(FOUND, FORMAT('Erro ao deletar serviços do pneu %s!', F_COD_CLIENTE_PNEU));

        UPDATE PUBLIC.PNEU_SERVICO_REALIZADO_INCREMENTA_VIDA_DATA
        SET DELETADO            = TRUE,
            DATA_HORA_DELETADO  = NOW(),
            PG_USERNAME_DELECAO = SESSION_USER,
            MOTIVO_DELECAO      = F_MOTIVO_RETROCESSO
        WHERE DELETADO = FALSE
          AND COD_SERVICO_REALIZADO = ANY (V_COD_SERVICOS_DELETADOS)
          AND VIDA_NOVA_PNEU > F_VIDA_NOVA_PNEU;

        PERFORM FUNC_GARANTE_UPDATE_OK(FOUND,
                                       FORMAT('Erro ao deletar serviços de incremento de vida do pneu %s!',
                                              F_COD_CLIENTE_PNEU));

        -- Estes três updates nas tabelas de serviço de cadastro/movimentação propositalmente não possuem validação de
        -- sucesso pois o pneu pode não ter entradas nestas tabelas.
        UPDATE PUBLIC.PNEU_SERVICO_CADASTRO_DATA
        SET DELETADO            = TRUE,
            DATA_HORA_DELETADO  = NOW(),
            PG_USERNAME_DELECAO = SESSION_USER,
            MOTIVO_DELECAO      = F_MOTIVO_RETROCESSO
        WHERE DELETADO = FALSE
          AND COD_SERVICO_REALIZADO = ANY (V_COD_SERVICOS_DELETADOS);

        UPDATE PUBLIC.MOVIMENTACAO_PNEU_SERVICO_REALIZADO_DATA
        SET DELETADO            = TRUE,
            DATA_HORA_DELETADO  = NOW(),
            PG_USERNAME_DELECAO = SESSION_USER,
            MOTIVO_DELECAO      = F_MOTIVO_RETROCESSO
        WHERE DELETADO = FALSE
          AND COD_SERVICO_REALIZADO = ANY (V_COD_SERVICOS_DELETADOS);

        UPDATE PUBLIC.MOVIMENTACAO_PNEU_SERVICO_REALIZADO_RECAPADORA_DATA
        SET DELETADO            = TRUE,
            DATA_HORA_DELETADO  = NOW(),
            PG_USERNAME_DELECAO = SESSION_USER,
            MOTIVO_DELECAO      = F_MOTIVO_RETROCESSO
        WHERE DELETADO = FALSE
          AND COD_SERVICO_REALIZADO_MOVIMENTACAO = ANY (V_COD_SERVICOS_DELETADOS);

        -- Se estamos regredindo a vida para uma anterior à vida de cadastro e diferente da primeira vida, precisamos
        -- cadastrar um novo serviço de FONTE_CADASTRO de incremento de vida.
        IF F_VIDA_NOVA_PNEU < V_VIDA_CADASTRO AND F_VIDA_NOVA_PNEU <> 1
        THEN
            PERFORM FUNC_PNEU_REALIZA_INCREMENTO_VIDA_CADASTRO(F_COD_UNIDADE_PNEU,
                                                               V_COD_PNEU,
                                                               V_COD_MODELO_BANDA_ATUAL_PNEU,
                                                               V_VALOR_BANDA_ATUAL_PNEU,
                                                               F_VIDA_NOVA_PNEU);
        END IF;

        -- Por fim, atualizamos o pneu.
        UPDATE PNEU
        SET VIDA_ATUAL       = F_VIDA_NOVA_PNEU,
            COD_MODELO_BANDA = F_IF(F_VIDA_NOVA_PNEU = 1, NULL, (SELECT PVV.COD_MODELO_BANDA
                                                                 FROM PNEU_VALOR_VIDA PVV
                                                                 WHERE PVV.COD_PNEU = V_COD_PNEU
                                                                   AND PVV.VIDA = F_VIDA_NOVA_PNEU))
        WHERE CODIGO = V_COD_PNEU;

        PERFORM FUNC_GARANTE_UPDATE_OK(FOUND, FORMAT('Erro ao atualizar a vida do pneu %s!', F_COD_CLIENTE_PNEU));

        SELECT FORMAT('Pneu %s teve sua vida regredida de %s para %s com sucesso!',
                      F_COD_CLIENTE_PNEU,
                      V_VIDA_ATUAL_PNEU,
                      F_VIDA_NOVA_PNEU)
        INTO AVISO_PNEU_VIDA_RETROCEDIDA;
        RETURN;
    END IF;

    RAISE EXCEPTION 'Não é possível regredir a vida do pneu %. Ele encontra-se em um estado não mapeado!',
        F_COD_CLIENTE_PNEU;
END ;
$$;



CREATE OR REPLACE FUNCTION SUPORTE.FUNC_VEICULO_ALTERA_PLACA(F_COD_UNIDADE BIGINT,
                                                             F_COD_VEICULO_ANTIGO BIGINT,
                                                             F_PLACA_ANTIGA TEXT,
                                                             F_PLACA_NOVA TEXT,
                                                             F_FORCAR_ATUALIZACAO_PLACA_INTEGRACAO BOOLEAN DEFAULT FALSE,
                                                             OUT F_AVISO_PLACA_ALTERADA TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    V_KM                 BIGINT;
    V_STATUS_ATIVO       BOOLEAN;
    V_COD_TIPO           BIGINT;
    V_COD_MODELO         BIGINT;
    V_COD_EIXOS          BIGINT;
    V_DATA_HORA_CADASTRO TIMESTAMP WITH TIME ZONE;
    V_COD_EMPRESA        BIGINT;
    V_COD_DIAGRAMA       BIGINT;
    V_COD_VEICULO_NOVO   BIGINT;
    V_QTD_INSERTS        BIGINT;
    V_QTD_UPDATES        BIGINT;
    V_QTD_DELETES        BIGINT;
BEGIN
    PERFORM SUPORTE.FUNC_HISTORICO_SALVA_EXECUCAO();
    -- GARANTE QUE UNIDADE EXISTE.
    PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE);

    -- VERIFICA SE VEÍCULO ANTIGO EXISTE.
    PERFORM FUNC_GARANTE_VEICULO_EXISTE(F_COD_UNIDADE, F_PLACA_ANTIGA);

    -- VERIFICA SE PLACA NOVA ESTÁ DISPONÍVEL.
    IF EXISTS(SELECT VD.PLACA FROM VEICULO_DATA VD WHERE VD.PLACA = F_PLACA_NOVA)
    THEN
        RAISE EXCEPTION
            'A placa % já existe no banco.', F_PLACA_NOVA;
    END IF;

    -- VERIFICA SE A PLACA É DE INTEGRAÇÃO.
    IF EXISTS(SELECT VC.PLACA_VEICULO_CADASTRO
              FROM INTEGRACAO.VEICULO_CADASTRADO VC
              WHERE VC.PLACA_VEICULO_CADASTRO = F_PLACA_ANTIGA)
    THEN
        -- VERIFICA SE DEVE ALTERAR PLACA EM INTEGRAÇÃO.
        IF (F_FORCAR_ATUALIZACAO_PLACA_INTEGRACAO IS FALSE)
        THEN
            RAISE EXCEPTION
                'A placa % pertence à integração. Para atualizar a mesma, deve-se passar TRUE como parâmetro',
                F_PLACA_ANTIGA;
        END IF;
    END IF;

    -- BUSCA INFORMAÇÕES DO VEÍCULO ANTIGO.
    SELECT V.KM,
           V.STATUS_ATIVO,
           V.COD_TIPO,
           V.COD_MODELO,
           V.COD_EIXOS,
           V.DATA_HORA_CADASTRO,
           V.COD_EMPRESA,
           V.COD_DIAGRAMA
    FROM VEICULO V
    WHERE V.PLACA = F_PLACA_ANTIGA
      AND V.CODIGO = F_COD_VEICULO_ANTIGO
      AND V.COD_UNIDADE = F_COD_UNIDADE
    INTO V_KM,
        V_STATUS_ATIVO,
        V_COD_TIPO,
        V_COD_MODELO,
        V_COD_EIXOS,
        V_DATA_HORA_CADASTRO,
        V_COD_EMPRESA,
        V_COD_DIAGRAMA;

    IF ((V_KM IS NULL) OR (V_STATUS_ATIVO IS NULL) OR (V_COD_TIPO IS NULL) OR (V_COD_MODELO IS NULL)
        OR (V_COD_EIXOS IS NULL) OR (V_COD_EMPRESA IS NULL))
    THEN
        RAISE EXCEPTION
            'Não foi possível buscar informações do veículo com placa %, código %', F_PLACA_ANTIGA, F_COD_VEICULO_ANTIGO;
    END IF;

    -- DUPLICA INFORMAÇÕES DO VEÍCULO ANTIGO PARA A PLACA NOVA.
    INSERT INTO VEICULO_DATA (PLACA,
                              COD_UNIDADE,
                              KM,
                              STATUS_ATIVO,
                              COD_TIPO,
                              COD_MODELO,
                              COD_EIXOS,
                              DATA_HORA_CADASTRO,
                              COD_UNIDADE_CADASTRO,
                              COD_EMPRESA,
                              COD_DIAGRAMA)
    VALUES (F_PLACA_NOVA,
            F_COD_UNIDADE,
            V_KM,
            V_STATUS_ATIVO,
            V_COD_TIPO,
            V_COD_MODELO,
            V_COD_EIXOS,
            V_DATA_HORA_CADASTRO,
            F_COD_UNIDADE,
            V_COD_EMPRESA,
            V_COD_DIAGRAMA) RETURNING CODIGO INTO V_COD_VEICULO_NOVO;
    GET DIAGNOSTICS V_QTD_INSERTS = ROW_COUNT;
    IF (V_QTD_INSERTS IS NULL OR V_QTD_INSERTS <= 0)
    THEN
        RAISE EXCEPTION
            'Não foi possível modificar a placa para %', F_PLACA_NOVA;
    END IF;

    -- MODIFICA PLACA EM INTEGRAÇÃO VEICULO_CADASTRADO
    IF EXISTS(SELECT VC.PLACA_VEICULO_CADASTRO
              FROM INTEGRACAO.VEICULO_CADASTRADO VC
              WHERE VC.PLACA_VEICULO_CADASTRO = F_PLACA_ANTIGA)
    THEN
        UPDATE INTEGRACAO.VEICULO_CADASTRADO
        SET PLACA_VEICULO_CADASTRO = F_PLACA_NOVA
        WHERE PLACA_VEICULO_CADASTRO = F_PLACA_ANTIGA;
        GET DIAGNOSTICS V_QTD_UPDATES = ROW_COUNT;
        IF (V_QTD_UPDATES IS NULL OR V_QTD_UPDATES <= 0)
        THEN
            RAISE EXCEPTION
                'Não foi possível modificar a placa para % na tabela de integração veiculo_cadastrado', F_PLACA_NOVA;
        END IF;
    END IF;

    -- MODIFICA PLACA EM VEICULO_PNEU.
    IF EXISTS(SELECT VP.PLACA FROM VEICULO_PNEU VP WHERE VP.PLACA = F_PLACA_ANTIGA)
    THEN
        UPDATE VEICULO_PNEU SET PLACA = F_PLACA_NOVA WHERE PLACA = F_PLACA_ANTIGA;
        GET DIAGNOSTICS V_QTD_UPDATES = ROW_COUNT;
        IF (V_QTD_UPDATES IS NULL OR V_QTD_UPDATES <= 0)
        THEN
            RAISE EXCEPTION
                'Não foi possível modificar a placa para % no vínculo de veículo pneu', F_PLACA_NOVA;
        END IF;
    END IF;

    -- MODIFICA PLACA NA AFERIÇÃO.
    IF EXISTS(SELECT AD.PLACA_VEICULO FROM AFERICAO_DATA AD WHERE AD.PLACA_VEICULO = F_PLACA_ANTIGA)
    THEN
        UPDATE AFERICAO_DATA SET PLACA_VEICULO = F_PLACA_NOVA WHERE PLACA_VEICULO = F_PLACA_ANTIGA;
        GET DIAGNOSTICS V_QTD_UPDATES = ROW_COUNT;
        IF (V_QTD_UPDATES IS NULL OR V_QTD_UPDATES <= 0)
        THEN
            RAISE EXCEPTION
                'Não foi possível modificar a placa para % nas aferições', F_PLACA_NOVA;
        END IF;
    END IF;

    -- MODIFICA PLACA NO CHECKLIST.
    IF EXISTS(SELECT CD.PLACA_VEICULO FROM CHECKLIST_DATA CD WHERE CD.PLACA_VEICULO = F_PLACA_ANTIGA)
    THEN
        UPDATE CHECKLIST_DATA SET PLACA_VEICULO = F_PLACA_NOVA WHERE PLACA_VEICULO = F_PLACA_ANTIGA;
        GET DIAGNOSTICS V_QTD_UPDATES = ROW_COUNT;
        IF (V_QTD_UPDATES IS NULL OR V_QTD_UPDATES <= 0)
        THEN
            RAISE EXCEPTION
                'Não foi possível modificar a placa para % nos checklists', F_PLACA_NOVA;
        END IF;
    END IF;

    -- MODIFICA PLACA NAS MOVIMENTAÇÕES.
    IF EXISTS(SELECT MO.PLACA FROM MOVIMENTACAO_ORIGEM MO WHERE MO.PLACA = F_PLACA_ANTIGA)
    THEN
        UPDATE MOVIMENTACAO_ORIGEM SET PLACA = F_PLACA_NOVA WHERE PLACA = F_PLACA_ANTIGA;
        GET DIAGNOSTICS V_QTD_UPDATES = ROW_COUNT;
        IF (V_QTD_UPDATES IS NULL OR V_QTD_UPDATES <= 0)
        THEN
            RAISE EXCEPTION
                'Não foi possível modificar a placa para % em movimentacao origem', F_PLACA_NOVA;
        END IF;
    END IF;

    IF EXISTS(SELECT MD.PLACA FROM MOVIMENTACAO_DESTINO MD WHERE MD.PLACA = F_PLACA_ANTIGA)
    THEN
        UPDATE MOVIMENTACAO_DESTINO SET PLACA = F_PLACA_NOVA WHERE PLACA = F_PLACA_ANTIGA;
        GET DIAGNOSTICS V_QTD_UPDATES = ROW_COUNT;
        IF (V_QTD_UPDATES IS NULL OR V_QTD_UPDATES <= 0)
        THEN
            RAISE EXCEPTION
                'Não foi possível modificar a placa para % em movimentacao destino', F_PLACA_NOVA;
        END IF;
    END IF;

    -- DELETA PLACA ANTIGA.
    DELETE
    FROM VEICULO_DATA VD
    WHERE VD.COD_UNIDADE = F_COD_UNIDADE
      AND VD.PLACA = F_PLACA_ANTIGA;
    GET DIAGNOSTICS V_QTD_DELETES = ROW_COUNT;
    IF (V_QTD_DELETES IS NULL OR V_QTD_DELETES <= 0)
    THEN
        RAISE EXCEPTION
            'Não foi possivel modificar a placa % para %.', F_PLACA_ANTIGA, F_PLACA_NOVA;
    END IF;

    -- ALTERA CÓDIGO DO VEÍCULO NOVO PARA CÓDIGO ANTIGO
    UPDATE VEICULO SET CODIGO = F_COD_VEICULO_ANTIGO WHERE CODIGO = V_COD_VEICULO_NOVO;
    GET DIAGNOSTICS V_QTD_UPDATES = ROW_COUNT;
    IF (V_QTD_UPDATES IS NULL OR V_QTD_UPDATES <= 0)
    THEN
        RAISE EXCEPTION
            'Não foi possivel o código da placa % para %.', F_PLACA_NOVA, F_COD_VEICULO_ANTIGO;
    ELSE
        SELECT 'A placa foi alterada de ' || F_PLACA_ANTIGA ||
               ' para ' || F_PLACA_NOVA || '.'
        INTO F_AVISO_PLACA_ALTERADA;
    END IF;
END;
$$;



CREATE OR REPLACE FUNCTION SUPORTE.FUNC_VEICULO_ALTERA_TIPO_VEICULO(F_PLACA_VEICULO TEXT,
                                                                    F_COD_VEICULO_TIPO_NOVO BIGINT,
                                                                    F_COD_UNIDADE BIGINT,
                                                                    OUT AVISO_TIPO_VEICULO_ALTERADO TEXT)
    RETURNS TEXT
    SECURITY DEFINER
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    F_COD_DIAGRAMA_NOVO BIGINT := (SELECT VT.COD_DIAGRAMA
                                   FROM VEICULO_TIPO VT
                                   WHERE VT.CODIGO = F_COD_VEICULO_TIPO_NOVO);
    F_COD_EMPRESA       BIGINT := (SELECT U.COD_EMPRESA
                                   FROM UNIDADE U
                                   WHERE U.CODIGO = F_COD_UNIDADE);

BEGIN
    PERFORM SUPORTE.FUNC_HISTORICO_SALVA_EXECUCAO();
    -- Garante que unidade/empresa existem
    PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE);

    -- Garante que veiculo existe e pertence a unidade
    PERFORM FUNC_GARANTE_VEICULO_EXISTE(F_COD_UNIDADE, F_PLACA_VEICULO);

    -- Verifica se veículo não está deletado (a function FUNC_GARANTE_VEICULO_EXISTE, utiliza a veiculo_data, portanto
    -- também trás veículos deletados em sua consulta)
    IF EXISTS (SELECT VD.PLACA FROM VEICULO_DATA VD WHERE VD.PLACA = F_PLACA_VEICULO AND VD.DELETADO = TRUE)
    THEN
        RAISE EXCEPTION
            'O veículo consta como deletado, placa: %, código da unidade: %.' , F_PLACA_VEICULO,
            (SELECT VD.COD_UNIDADE FROM VEICULO_DATA VD WHERE VD.PLACA = F_PLACA_VEICULO);
    END IF;

    -- Garante que tipo_veiculo_novo pertence a empresa
    IF NOT EXISTS(SELECT VT.CODIGO
                  FROM VEICULO_TIPO VT
                  WHERE VT.CODIGO = F_COD_VEICULO_TIPO_NOVO
                    AND VT.COD_EMPRESA = F_COD_EMPRESA)
    THEN
        RAISE EXCEPTION
            'O tipo de veículo de código: % Não pertence à empresa: %',
            F_COD_VEICULO_TIPO_NOVO,
            F_COD_EMPRESA;
    END IF;

    -- Verifica se o tipo_veiculo_novo é o atual
    IF EXISTS(SELECT V.CODIGO FROM VEICULO V WHERE V.PLACA = F_PLACA_VEICULO AND V.COD_TIPO = F_COD_VEICULO_TIPO_NOVO)
    THEN
        RAISE EXCEPTION
            'O tipo de veículo atual da placa % é igual ao informado. Código tipo de veículo: %',
            F_PLACA_VEICULO,
            F_COD_VEICULO_TIPO_NOVO;
    END IF;

    -- Verifica se placa tem pneus aplicados
    IF EXISTS(SELECT VP.PLACA FROM VEICULO_PNEU VP WHERE VP.PLACA = F_PLACA_VEICULO)
    THEN
        -- Se existirem pneus, verifica se os pneus que estão aplicados possuem as mesmas posições do novo tipo
        IF ((SELECT ARRAY_AGG(VP.POSICAO)
             FROM VEICULO_PNEU VP
             WHERE VP.PLACA = F_PLACA_VEICULO)<@
            (SELECT ARRAY_AGG(VDPP.POSICAO_PROLOG :: INTEGER)
             FROM VEICULO_DIAGRAMA_POSICAO_PROLOG VDPP
             WHERE COD_DIAGRAMA = F_COD_DIAGRAMA_NOVO) = FALSE)
        THEN
            RAISE EXCEPTION
                'Existem pneus aplicados em posições que não fazem parte do tipo de veículo de código: %',
                F_COD_VEICULO_TIPO_NOVO;
        END IF;
    END IF;

    -- Realiza a mudança de tipo
    UPDATE VEICULO_DATA
    SET COD_TIPO = F_COD_VEICULO_TIPO_NOVO
    WHERE PLACA = F_PLACA_VEICULO
      AND COD_UNIDADE = F_COD_UNIDADE;

    --MENSAGEM DE SUCESSO.
    SELECT 'Tipo do veículo alterado! Placa: ' || F_PLACA_VEICULO ||
           ', Código da unidade: ' || F_COD_UNIDADE ||
           ', Tipo: ' || (SELECT VT.NOME FROM VEICULO_TIPO VT WHERE VT.CODIGO = F_COD_VEICULO_TIPO_NOVO) ||
           ', Código do tipo: ' || F_COD_VEICULO_TIPO_NOVO || '.'
    INTO AVISO_TIPO_VEICULO_ALTERADO;
END;

$$;



CREATE OR REPLACE FUNCTION SUPORTE.FUNC_VEICULO_DELETA_VEICULO(F_COD_UNIDADE BIGINT,
                                                               F_PLACA VARCHAR(255),
                                                               F_MOTIVO_DELECAO TEXT,
                                                               OUT DEPENDENCIAS_DELETADAS TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    V_CODIGO_LOOP                   BIGINT;
    V_LISTA_COD_AFERICAO_PLACA      BIGINT[];
    V_LISTA_COD_CHECK_PLACA         BIGINT[];
    V_LISTA_COD_PROLOG_DELETADO_COS BIGINT[];
    V_NOME_EMPRESA                  VARCHAR(255) := (SELECT E.NOME
                                                     FROM EMPRESA E
                                                     WHERE E.CODIGO =
                                                           (SELECT U.COD_EMPRESA
                                                            FROM UNIDADE U
                                                            WHERE U.CODIGO = F_COD_UNIDADE));
    V_NOME_UNIDADE                  VARCHAR(255) := (SELECT U.NOME
                                                     FROM UNIDADE U
                                                     WHERE U.CODIGO = F_COD_UNIDADE);
BEGIN
    PERFORM SUPORTE.FUNC_HISTORICO_SALVA_EXECUCAO();
    -- VERIFICA SE UNIDADE EXISTE;
    PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE);

    -- VERIFICA SE VEÍCULO EXISTE.
    PERFORM FUNC_GARANTE_VEICULO_EXISTE(F_COD_UNIDADE, F_PLACA);

    -- VERIFICA SE VEÍCULO POSSUI PNEU APLICADOS.
    IF EXISTS(SELECT VP.COD_PNEU FROM VEICULO_PNEU VP WHERE VP.PLACA = F_PLACA AND VP.COD_UNIDADE = F_COD_UNIDADE)
    THEN
        RAISE EXCEPTION 'Erro! A Placa: % possui pneus aplicados. Favor removê-los', F_PLACA;
    END IF;

    -- VERIFICA SE PLACA POSSUI AFERIÇÃO.
    IF EXISTS(SELECT A.CODIGO FROM AFERICAO_DATA A WHERE A.PLACA_VEICULO = F_PLACA)
    THEN
        -- COLETAMOS TODOS OS COD_AFERICAO QUE A PLACA POSSUI.
        SELECT ARRAY_AGG(A.CODIGO)
        FROM AFERICAO_DATA A
        WHERE A.PLACA_VEICULO = F_PLACA
        INTO V_LISTA_COD_AFERICAO_PLACA;

        -- DELETAMOS AFERIÇÃO EM AFERICAO_MANUTENCAO_DATA.
        UPDATE AFERICAO_MANUTENCAO_DATA
        SET DELETADO            = TRUE,
            DATA_HORA_DELETADO  = NOW(),
            PG_USERNAME_DELECAO = SESSION_USER,
            MOTIVO_DELECAO      = F_MOTIVO_DELECAO
        WHERE DELETADO = FALSE
          AND COD_AFERICAO = ANY (V_LISTA_COD_AFERICAO_PLACA);

        -- DELETAMOS AFERIÇÃO EM AFERICAO_VALORES_DATA.
        UPDATE AFERICAO_VALORES_DATA
        SET DELETADO            = TRUE,
            DATA_HORA_DELETADO  = NOW(),
            PG_USERNAME_DELECAO = SESSION_USER,
            MOTIVO_DELECAO      = F_MOTIVO_DELECAO
        WHERE DELETADO = FALSE
          AND COD_AFERICAO = ANY (V_LISTA_COD_AFERICAO_PLACA);

        -- DELETAMOS AFERIÇÃO.
        UPDATE AFERICAO_DATA
        SET DELETADO            = TRUE,
            DATA_HORA_DELETADO  = NOW(),
            PG_USERNAME_DELECAO = SESSION_USER,
            MOTIVO_DELECAO      = F_MOTIVO_DELECAO
        WHERE DELETADO = FALSE
          AND CODIGO = ANY (V_LISTA_COD_AFERICAO_PLACA);
    END IF;

    -- VERIFICA SE PLACA POSSUI CHECKLIST.
    IF EXISTS(SELECT C.PLACA_VEICULO FROM CHECKLIST_DATA C WHERE C.PLACA_VEICULO = F_PLACA)
    THEN
        -- BUSCA TODOS OS CÓDIGO DO CHECKLIST DA PLACA.
        SELECT ARRAY_AGG(C.CODIGO)
        FROM CHECKLIST_DATA C
        WHERE C.PLACA_VEICULO = F_PLACA
        INTO V_LISTA_COD_CHECK_PLACA;

        -- DELETA COD_CHECK EM COS.
        UPDATE CHECKLIST_ORDEM_SERVICO_DATA
        SET DELETADO            = TRUE,
            DATA_HORA_DELETADO  = NOW(),
            PG_USERNAME_DELECAO = SESSION_USER,
            MOTIVO_DELECAO      = F_MOTIVO_DELECAO
        WHERE DELETADO = FALSE
          AND COD_CHECKLIST = ANY (V_LISTA_COD_CHECK_PLACA);

        -- BUSCO OS CODIGO PROLOG DELETADOS EM COS.
        SELECT ARRAY_AGG(CODIGO_PROLOG)
        FROM CHECKLIST_ORDEM_SERVICO_DATA
        WHERE COD_CHECKLIST = ANY (V_LISTA_COD_CHECK_PLACA)
          AND COD_UNIDADE = F_COD_UNIDADE
          AND DELETADO IS TRUE
        INTO V_LISTA_COD_PROLOG_DELETADO_COS;

        -- PARA CADA CÓDIGO PROLOG DELETADO EM COS, DELETAMOS O REFERENTE NA COSI.
        FOREACH V_CODIGO_LOOP IN ARRAY V_LISTA_COD_PROLOG_DELETADO_COS
            LOOP
                -- DELETA EM COSI AQUELES QUE FORAM DELETADOS NA COS.
                UPDATE CHECKLIST_ORDEM_SERVICO_ITENS_DATA
                SET DELETADO            = TRUE,
                    DATA_HORA_DELETADO  = NOW(),
                    PG_USERNAME_DELECAO = SESSION_USER,
                    MOTIVO_DELECAO      = F_MOTIVO_DELECAO
                WHERE DELETADO = FALSE
                  AND (COD_OS, COD_UNIDADE) = (SELECT COS.CODIGO, COS.COD_UNIDADE
                                               FROM CHECKLIST_ORDEM_SERVICO_DATA COS
                                               WHERE COS.CODIGO_PROLOG = V_CODIGO_LOOP);
            END LOOP;

        -- DELETA TODOS CHECKLIST DA PLACA.
        UPDATE CHECKLIST_DATA
        SET DELETADO            = TRUE,
            DATA_HORA_DELETADO  = NOW(),
            PG_USERNAME_DELECAO = SESSION_USER,
            MOTIVO_DELECAO      = F_MOTIVO_DELECAO
        WHERE PLACA_VEICULO = F_PLACA
          AND DELETADO = FALSE
          AND CODIGO = ANY (V_LISTA_COD_CHECK_PLACA);
    END IF;

    -- Verifica se a placa é integrada.
    IF EXISTS(SELECT IVC.PLACA_VEICULO_CADASTRO
              FROM INTEGRACAO.VEICULO_CADASTRADO IVC
              WHERE IVC.COD_UNIDADE_CADASTRO = F_COD_UNIDADE
                AND IVC.PLACA_VEICULO_CADASTRO = F_PLACA)
    THEN
        -- Realiza a deleção da placa. (Não possuímos deleção lógica)
        DELETE
        FROM INTEGRACAO.VEICULO_CADASTRADO
        WHERE COD_UNIDADE_CADASTRO = F_COD_UNIDADE
          AND PLACA_VEICULO_CADASTRO = F_PLACA;
    END IF;

    -- REALIZA DELEÇÃO DA PLACA.
    UPDATE VEICULO_DATA
    SET DELETADO            = TRUE,
        DATA_HORA_DELETADO  = NOW(),
        PG_USERNAME_DELECAO = SESSION_USER,
        MOTIVO_DELECAO      = F_MOTIVO_DELECAO
    WHERE COD_UNIDADE = F_COD_UNIDADE
      AND PLACA = F_PLACA
      AND DELETADO = FALSE;

    -- MENSAGEM DE SUCESSO.
    SELECT 'Veículo deletado junto com suas dependências. Veículo: '
               || F_PLACA
               || ', Empresa: '
               || V_NOME_EMPRESA
               || ', Unidade: '
               || V_NOME_UNIDADE
    INTO DEPENDENCIAS_DELETADAS;
END;
$$;



CREATE OR REPLACE FUNCTION SUPORTE.FUNC_VEICULO_TRANSFERE_VEICULO_ENTRE_EMPRESAS(F_PLACA_VEICULO VARCHAR(7),
                                                                                 F_COD_EMPRESA_ORIGEM BIGINT,
                                                                                 F_COD_UNIDADE_ORIGEM BIGINT,
                                                                                 F_COD_EMPRESA_DESTINO BIGINT,
                                                                                 F_COD_UNIDADE_DESTINO BIGINT,
                                                                                 F_COD_MODELO_VEICULO_DESTINO BIGINT,
                                                                                 F_COD_TIPO_VEICULO_DESTINO BIGINT,
                                                                                 OUT VEICULO_TRANSFERIDO TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    V_NOME_EMPRESA_DESTINO                           VARCHAR(255) := (SELECT E.NOME
                                                                      FROM EMPRESA E
                                                                      WHERE E.CODIGO = F_COD_EMPRESA_DESTINO);
    V_NOME_UNIDADE_DESTINO                           VARCHAR(255) := (SELECT U.NOME
                                                                      FROM UNIDADE U
                                                                      WHERE U.CODIGO = F_COD_UNIDADE_DESTINO);
    V_LISTA_COD_AFERICAO_PLACA                       BIGINT[];
    V_COD_AFERICAO_FOREACH                           BIGINT;
    V_LISTA_COD_PNEU_EM_AFERICAO_MANUTENCAO          BIGINT[];
    V_QTD_COD_AFERICAO_EM_AFERICAO_VALORES           BIGINT;
    V_QTD_COD_AFERICAO_DELETADOS_EM_AFERICAO_VALORES BIGINT;


BEGIN
    PERFORM SUPORTE.FUNC_HISTORICO_SALVA_EXECUCAO();
    --VERIFICA SE EMPRESA ORIGEM POSSUI UNIDADE ORIGEM.
    PERFORM FUNC_GARANTE_INTEGRIDADE_EMPRESA_UNIDADE(F_COD_EMPRESA_ORIGEM, F_COD_UNIDADE_ORIGEM);

    --VERIFICA SE EMPRESA DESTINO POSSUI UNIDADE DESTINO.
    PERFORM FUNC_GARANTE_INTEGRIDADE_EMPRESA_UNIDADE(F_COD_EMPRESA_DESTINO, F_COD_UNIDADE_DESTINO);

    --VERIFICA SE EMPRESA ORIGEM/DESTINO SÃO DISTINTAS.
    PERFORM FUNC_GARANTE_EMPRESAS_DISTINTAS(F_COD_EMPRESA_ORIGEM, F_COD_EMPRESA_DESTINO);

    --VERIFICA SE VEICULO EXISTE
    PERFORM FUNC_GARANTE_VEICULO_EXISTE(F_COD_UNIDADE_ORIGEM, F_PLACA_VEICULO);

    --VERIFICA SE A PLACA POSSUI PNEUS.
    IF EXISTS(SELECT VP.COD_PNEU
              FROM VEICULO_PNEU VP
              WHERE VP.PLACA = F_PLACA_VEICULO
                AND VP.COD_UNIDADE = F_COD_UNIDADE_ORIGEM)
    THEN
        RAISE EXCEPTION 'ERRO! A PLACA: % POSSUI PNEUS VINCULADOS, FAVOR REMOVER OS PNEUS DO MESMO', F_PLACA_VEICULO;
    END IF;

    --VERIFICA SE EMPRESA DESTINO POSSUI TIPO DO VEÍCULO INFORMADO.
    IF NOT EXISTS(
            SELECT VT.CODIGO
            FROM VEICULO_TIPO VT
            WHERE VT.COD_EMPRESA = F_COD_EMPRESA_DESTINO
              AND VT.CODIGO = F_COD_TIPO_VEICULO_DESTINO)
    THEN
        RAISE EXCEPTION 'ERRO! O CÓDIGO TIPO: % NÃO EXISTE NA EMPRESA DESTINO: %', F_COD_TIPO_VEICULO_DESTINO,
            V_NOME_EMPRESA_DESTINO;
    END IF;

    --VERIFICA SE O TIPO DE VEÍCULO INFORMADO TEM O MESMO DIAGRAMA DO VEÍCULO
    IF NOT EXISTS(
            SELECT VD.CODIGO
            FROM VEICULO_DATA VD
                     JOIN VEICULO_TIPO VT ON VD.COD_DIAGRAMA = VT.COD_DIAGRAMA
            WHERE VD.PLACA = F_PLACA_VEICULO
              AND VT.CODIGO = F_COD_TIPO_VEICULO_DESTINO)
    THEN
        RAISE EXCEPTION 'ERRO! O DIAGRAMA DO TIPO: % É DIFERENTE DO VEÍCULO: %', F_COD_TIPO_VEICULO_DESTINO,
            F_PLACA_VEICULO;
    END IF;

    --VERIFICA SE EMPRESA DESTINO POSSUI MODELO DO VEÍCULO INFORMADO.
    IF NOT EXISTS(SELECT MV.CODIGO
                  FROM MODELO_VEICULO MV
                  WHERE MV.COD_EMPRESA = F_COD_EMPRESA_DESTINO
                    AND MV.CODIGO = F_COD_MODELO_VEICULO_DESTINO)
    THEN
        RAISE EXCEPTION 'ERRO! O CÓDIGO MODELO: % NÃO EXISTE NA EMPRESA DESTINO: %', F_COD_MODELO_VEICULO_DESTINO,
            V_NOME_EMPRESA_DESTINO;
    END IF;

    --VERIFICA SE PLACA POSSUI AFERIÇÃO.
    IF EXISTS(SELECT A.CODIGO
              FROM AFERICAO A
              WHERE A.PLACA_VEICULO = F_PLACA_VEICULO)
    THEN
        --ENTÃO COLETAMOS TODOS OS CÓDIGOS DAS AFERIÇÕES QUE A PLACA POSSUI E ADICIONAMOS NO ARRAY.
        SELECT DISTINCT ARRAY_AGG(A.CODIGO)
        FROM AFERICAO A
        WHERE A.PLACA_VEICULO = F_PLACA_VEICULO
        INTO V_LISTA_COD_AFERICAO_PLACA;

        --LAÇO FOR PARA PERCORRER TODOS OS VALORES EM F_LISTA_COD_AFERICAO_PLACA.
        FOREACH V_COD_AFERICAO_FOREACH IN ARRAY V_LISTA_COD_AFERICAO_PLACA
            LOOP
                --PARA CADA VALOR EM: F_LISTA_COD_AFERICAO_PLACA
                IF EXISTS(SELECT AM.COD_AFERICAO
                          FROM AFERICAO_MANUTENCAO AM
                          WHERE AM.COD_AFERICAO = V_COD_AFERICAO_FOREACH
                            AND AM.DATA_HORA_RESOLUCAO IS NULL
                            AND AM.FECHADO_AUTOMATICAMENTE_INTEGRACAO IS FALSE
                            AND AM.FECHADO_AUTOMATICAMENTE_MOVIMENTACAO IS FALSE)
                THEN
                    --COLETA O(S) COD_PNEU CORRESPONDENTES AO COD_AFERICAO.
                    SELECT ARRAY_AGG(AM.COD_PNEU)
                    FROM AFERICAO_MANUTENCAO AM
                    WHERE AM.COD_AFERICAO = V_COD_AFERICAO_FOREACH
                      AND AM.DATA_HORA_RESOLUCAO IS NULL
                      AND AM.FECHADO_AUTOMATICAMENTE_INTEGRACAO IS FALSE
                      AND AM.FECHADO_AUTOMATICAMENTE_MOVIMENTACAO IS FALSE
                    INTO V_LISTA_COD_PNEU_EM_AFERICAO_MANUTENCAO;

                    --DELETA AFERIÇÃO EM AFERICAO_MANUTENCAO_DATA ATRAVÉS DO COD_AFERICAO E COD_PNEU.
                    UPDATE AFERICAO_MANUTENCAO_DATA
                    SET DELETADO            = TRUE,
                        DATA_HORA_DELETADO  = NOW(),
                        PG_USERNAME_DELECAO = SESSION_USER
                    WHERE COD_UNIDADE = F_COD_UNIDADE_ORIGEM
                      AND COD_AFERICAO = V_COD_AFERICAO_FOREACH
                      AND COD_PNEU = ANY (V_LISTA_COD_PNEU_EM_AFERICAO_MANUTENCAO);

                    --DELETA AFERICAO EM AFERICAO_VALORES_DATA ATRAVÉS DO COD_AFERICAO E COD_PNEU.
                    UPDATE AFERICAO_VALORES_DATA
                    SET DELETADO            = TRUE,
                        DATA_HORA_DELETADO  = NOW(),
                        PG_USERNAME_DELECAO = SESSION_USER
                    WHERE COD_UNIDADE = F_COD_UNIDADE_ORIGEM
                      AND COD_AFERICAO = V_COD_AFERICAO_FOREACH
                      AND COD_PNEU = ANY (V_LISTA_COD_PNEU_EM_AFERICAO_MANUTENCAO);
                END IF;
            END LOOP;

        --SE, E SOMENTE SE, A AFERIÇÃO POSSUIR TODOS OS VALORES EXCLUÍDOS, DEVE-SE EXCLUIR TODA A AFERIÇÃO.
        --SENÃO, A AFERIÇÃO CONTINUA EXISTINDO.
        FOREACH V_COD_AFERICAO_FOREACH IN ARRAY V_LISTA_COD_AFERICAO_PLACA
            LOOP
                V_QTD_COD_AFERICAO_EM_AFERICAO_VALORES = (SELECT COUNT(AVD.COD_AFERICAO)
                                                          FROM AFERICAO_VALORES_DATA AVD
                                                          WHERE AVD.COD_AFERICAO = V_COD_AFERICAO_FOREACH);

                V_QTD_COD_AFERICAO_DELETADOS_EM_AFERICAO_VALORES = (SELECT COUNT(AVD.COD_AFERICAO)
                                                                    FROM AFERICAO_VALORES_DATA AVD
                                                                    WHERE AVD.COD_AFERICAO = V_COD_AFERICAO_FOREACH
                                                                      AND AVD.DELETADO IS TRUE);

                --SE A QUANTIDADE DE UM COD_AFERICAO EM AFERICAO_VALORES_DATA FOR IGUAL A QUANTIDADE DE UM COD_AFERICAO
                --DELETADO EM AFERICAO_VALORES_DATA, DEVEMOS EXCLUIR A AFERIÇÃO, POIS, TODOS SEUS VALORES FORAM
                --DELETADOS.
                IF (V_QTD_COD_AFERICAO_EM_AFERICAO_VALORES =
                    V_QTD_COD_AFERICAO_DELETADOS_EM_AFERICAO_VALORES)
                THEN
                    UPDATE AFERICAO_DATA
                    SET DELETADO            = TRUE,
                        DATA_HORA_DELETADO  = NOW(),
                        PG_USERNAME_DELECAO = SESSION_USER
                    WHERE COD_UNIDADE = F_COD_UNIDADE_ORIGEM
                      AND CODIGO = V_COD_AFERICAO_FOREACH;
                END IF;
            END LOOP;
    END IF;

    -- SE O VEÍCULO FOR INTEGRADO, ATUALIZA OS DADOS DE EMPRESA E UNIDADE NA TABELA DE INTEGRAÇÃO.
    IF EXISTS(SELECT IVC.PLACA_VEICULO_CADASTRO
              FROM INTEGRACAO.VEICULO_CADASTRADO IVC
              WHERE IVC.COD_EMPRESA_CADASTRO = F_COD_EMPRESA_ORIGEM
                AND IVC.COD_UNIDADE_CADASTRO = F_COD_UNIDADE_ORIGEM
                AND IVC.PLACA_VEICULO_CADASTRO = F_PLACA_VEICULO)
    THEN
        UPDATE INTEGRACAO.VEICULO_CADASTRADO
        SET COD_UNIDADE_CADASTRO = F_COD_UNIDADE_DESTINO,
            COD_EMPRESA_CADASTRO = F_COD_EMPRESA_DESTINO
        WHERE COD_EMPRESA_CADASTRO = F_COD_EMPRESA_ORIGEM
          AND COD_UNIDADE_CADASTRO = F_COD_UNIDADE_ORIGEM
          AND PLACA_VEICULO_CADASTRO = F_PLACA_VEICULO;
    END IF;

    --REALIZA TRANSFERÊNCIA.
    UPDATE VEICULO_DATA
    SET COD_EMPRESA = F_COD_EMPRESA_DESTINO,
        COD_UNIDADE = F_COD_UNIDADE_DESTINO,
        COD_TIPO    = F_COD_TIPO_VEICULO_DESTINO,
        COD_MODELO  = F_COD_MODELO_VEICULO_DESTINO
    WHERE COD_EMPRESA = F_COD_EMPRESA_ORIGEM
      AND COD_UNIDADE = F_COD_UNIDADE_ORIGEM
      AND PLACA = F_PLACA_VEICULO;

    --MENSAGEM DE SUCESSO.
    SELECT 'VEÍCULO TRANSFERIDO COM SUCESSO! O VEÍCULO COM PLACA: ' || F_PLACA_VEICULO ||
           ' FOI TRANSFERIDO PARA A EMPRESA ' || V_NOME_EMPRESA_DESTINO || ' JUNTO A UNIDADE ' ||
           V_NOME_UNIDADE_DESTINO || '.'
    INTO VEICULO_TRANSFERIDO;
END
$$;



CREATE OR REPLACE FUNCTION
    SUPORTE.FUNC_CARGOS_LIBERA_BLOQUEIA_PERMISSOES_BY_AGRUPAMENTO(F_LIBERAR_BLOQUEAR TEXT,
                                                                  F_COD_AGRUPAMENTOS BIGINT[],
                                                                  F_COD_EMPRESAS BIGINT[],
                                                                  F_COD_MOTIVO_BLOQUEIO BIGINT,
                                                                  F_OBSERVACAO_BLOQUEIO TEXT DEFAULT NULL,
                                                                  F_COD_UNIDADES BIGINT[] DEFAULT NULL)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    V_COD_AGRUPAMENTOS_NAO_MAPEADOS BIGINT[] := (SELECT ARRAY_AGG(AGRUPAMENTO.COD_AGRUPAMENTO)
                                                 FROM (SELECT UNNEST(F_COD_AGRUPAMENTOS) AS COD_AGRUPAMENTO) AGRUPAMENTO
                                                 WHERE AGRUPAMENTO.COD_AGRUPAMENTO NOT IN
                                                       (SELECT FPA.CODIGO FROM FUNCAO_PROLOG_AGRUPAMENTO FPA));
    V_COD_PERMISSOES                BIGINT[] := (SELECT ARRAY_AGG(FP.CODIGO)
                                                 FROM FUNCAO_PROLOG_V11 FP
                                                 WHERE FP.COD_AGRUPAMENTO = ANY (F_COD_AGRUPAMENTOS));
BEGIN
    PERFORM SUPORTE.FUNC_HISTORICO_SALVA_EXECUCAO();
    -- Validamos apenas os agrupamentos não mapeados, demais validações são feitas pela function interna.
    IF (F_SIZE_ARRAY(V_COD_AGRUPAMENTOS_NAO_MAPEADOS) > 0)
    THEN
        PERFORM THROW_GENERIC_ERROR(
                FORMAT('Códigos de agrupamentos inválidos (%s). ' ||
                       'Verifique os códigos na tabela funcao_prolog_agrupamento.',
                       V_COD_AGRUPAMENTOS_NAO_MAPEADOS));
    END IF;

    PERFORM SUPORTE.FUNC_CARGOS_LIBERA_BLOQUEIA_PERMISSOES_BY_CODIGO(F_LIBERAR_BLOQUEAR,
                                                                     V_COD_PERMISSOES,
                                                                     F_COD_EMPRESAS,
                                                                     F_COD_MOTIVO_BLOQUEIO,
                                                                     F_OBSERVACAO_BLOQUEIO,
                                                                     F_COD_UNIDADES);

    RETURN (SELECT FORMAT('A operação de %s foi realizada com sucesso para as permissões dos agrupamentos (%s)',
                          F_LIBERAR_BLOQUEAR, F_COD_AGRUPAMENTOS));
END;
$$;



create or replace function
    suporte.func_cargos_libera_bloqueia_permissoes_by_codigo(f_liberar_bloquear text,
                                                             f_cod_permissoes bigint[],
                                                             f_cod_empresas bigint[],
                                                             f_cod_motivo_bloqueio bigint,
                                                             f_observacao_bloqueio text default null,
                                                             f_cod_unidades bigint[] default null)
    returns text
    language plpgsql
    security definer
as
$$
declare
    v_cod_permissoes_nao_mapeados bigint[] := (select array_agg(permissoes.cod_permissao)
                                               from (select unnest(f_cod_permissoes) as cod_permissao) permissoes
                                               where permissoes.cod_permissao not in
                                                     (select fp.codigo from funcao_prolog_v11 fp));
    v_cod_empresas_nao_mapeadas   bigint[] := (select array_agg(empresas.cod_empresa)
                                               from (select unnest(f_cod_empresas) as cod_empresa) empresas
                                               where empresas.cod_empresa not in (select e.codigo from empresa e));
    v_cod_unidades_nao_mapeadas   bigint[] := (select array_agg(unidades.cod_unidade)
                                               from (select unnest(f_cod_unidades) as cod_unidade) unidades
                                               where unidades.cod_unidade not in
                                                     (select u.codigo
                                                      from unidade u
                                                      where u.cod_empresa = any (f_cod_empresas)));
    -- Caso o usuário informar as unidades, utilizaremos elas para liberar ou bloquear as permissões. Caso o usuário
    -- forneça somente a empresa, utilizamos todas as unidades de empresa para liberar ou bloquear as permissões.
    v_cod_unidades_mapeadas       bigint[] := (f_if(f_cod_unidades is not null,
                                                    f_cod_unidades,
                                                    (select array_agg(u.codigo)
                                                     from unidade u
                                                     where u.cod_empresa = any (f_cod_empresas))));
begin
    perform suporte.func_historico_salva_execucao();
    if (upper(f_liberar_bloquear) != 'LIBERAR' and upper(f_liberar_bloquear) != 'BLOQUEAR')
    then
        perform throw_generic_error('Deve-se informar o tipo correto da operação: LIBERAR ou BLOQUEAR.');
    end if;

    if (f_size_array(v_cod_permissoes_nao_mapeados) > 0)
    then
        perform throw_generic_error(
                format('Códigos de permissões inválidos (%s). ' ||
                       'Verifique os códigos na tabela funcao_prolog_v11.',
                       v_cod_permissoes_nao_mapeados));
    end if;

    if (f_size_array(v_cod_empresas_nao_mapeadas) > 0)
    then
        perform throw_generic_error(
                format('Nenhuma empresa encontrada para os códigos (%s)', v_cod_empresas_nao_mapeadas));
    end if;

    if (f_size_array(v_cod_unidades_nao_mapeadas) > 0)
    then
        perform throw_generic_error(
                format('As unidades (%s) não pertencem as empresas (%s)',
                       v_cod_unidades_nao_mapeadas,
                       f_cod_empresas));
    end if;

    -- Depois de validar os atributos necessários, fazemos o bloqueio ou liberação.
    if (upper(f_liberar_bloquear) = 'LIBERAR')
    then
        -- Devemos deletar da tabela de bloqueio as permissões com códigos e unidades mapeadas.
        delete
        from funcao_prolog_bloqueada
        where cod_funcao_prolog = any (f_cod_permissoes)
          and cod_unidade = any (v_cod_unidades_mapeadas);
    else
        if (select not exists(select codigo from funcao_prolog_motivo_bloqueio where codigo = f_cod_motivo_bloqueio))
        then
            perform throw_generic_error(
                    format('O motivo do bloqueio informado (%s) não é válido', f_cod_motivo_bloqueio));
        end if;

        -- Devemos inserir na tabela de bloqueio as permissões com códigos e unidades mapeadas.
        insert into funcao_prolog_bloqueada (cod_unidade,
                                             cod_pilar_funcao,
                                             cod_funcao_prolog,
                                             cod_motivo_bloqueio,
                                             observacao_bloqueio)
        select unnest(v_cod_unidades_mapeadas) as cod_unidade,
               fp.cod_pilar,
               fp.codigo,
               f_cod_motivo_bloqueio,
               f_observacao_bloqueio
        from funcao_prolog_v11 as fp
        where fp.codigo = any (f_cod_permissoes)
        on conflict
            on constraint pk_funcao_prolog_bloqueada
            do update
            set cod_motivo_bloqueio = f_cod_motivo_bloqueio,
                observacao_bloqueio = f_observacao_bloqueio;

        -- Devemos também remover essas permissões dos cargos associados nas unidades.
        delete
        from cargo_funcao_prolog_v11
        where cod_unidade = any (v_cod_unidades_mapeadas)
          and cod_funcao_prolog = any (f_cod_permissoes);
    end if;

    return (select format('A operação de %s foi realizada com sucesso para as permissões (%s)',
                          f_liberar_bloquear, f_cod_permissoes));
end;
$$;



create or replace function
    suporte.func_cargos_libera_bloqueia_permissoes_by_pilar(f_liberar_bloquear text,
                                                            f_cod_pilares bigint[],
                                                            f_cod_empresas bigint[],
                                                            f_cod_motivo_bloqueio bigint,
                                                            f_observacao_bloqueio text default null,
                                                            f_cod_unidades bigint[] default null)
    returns text
    language plpgsql
    security definer
as
$$
declare
    v_cod_permissoes bigint[] := (select array_agg(fp.codigo)
                                  from funcao_prolog_v11 fp
                                  where fp.cod_pilar = any (f_cod_pilares));
begin
    perform suporte.func_historico_salva_execucao();
    -- Validamos apenas os pilares não mapeados, demais validações são feitas pela function interna.
    perform func_garante_pilares_validos(f_cod_pilares::integer[]);

    perform suporte.func_cargos_libera_bloqueia_permissoes_by_codigo(f_liberar_bloquear,
                                                                     v_cod_permissoes,
                                                                     f_cod_empresas,
                                                                     f_cod_motivo_bloqueio,
                                                                     f_observacao_bloqueio,
                                                                     f_cod_unidades);

    return (select format('A operação de %s foi realizada com sucesso para as permissões dos pilares (%s)',
                          f_liberar_bloquear, f_cod_pilares));
end;
$$;



create or replace function suporte.func_colaborador_busca_por_permissao_empresa(f_cod_empresa bigint,
                                                                                f_cod_permissao bigint)
    returns table
            (
                funcionalidade  text,
                permissao       text,
                cod_empresa     bigint,
                empresa         text,
                cod_unidade     bigint,
                unidade         text,
                cod_colaborador bigint,
                colaborador     text,
                cpf             bigint,
                data_nascimento date,
                cargo           text
            )
    language plpgsql
as
$$
begin
    perform suporte.func_historico_salva_execucao();
    return query
        select fpa.nome::text    as funcionalidade,
               fp.funcao::text   as permissao,
               e.codigo          as cod_empresa,
               e.nome::text      as empresa,
               u.codigo          as cod_unidade,
               u.nome::text      as unidade,
               c.codigo          as cod_colaborador,
               c.nome::text      as colaborador,
               c.cpf             as cpf,
               c.data_nascimento as data_nascimento,
               f.nome::text      as cargo
        from colaborador c
                 left join cargo_funcao_prolog_v11 cfp
                           on cfp.cod_funcao_colaborador = c.cod_funcao and cfp.cod_unidade = c.cod_unidade
                 left join unidade u on u.codigo = c.cod_unidade
                 left join empresa e on e.codigo = c.cod_empresa
                 left join funcao f on f.codigo = c.cod_funcao
                 left join funcao_prolog_v11 fp on fp.codigo = cfp.cod_funcao_prolog
                 left join funcao_prolog_agrupamento fpa on fpa.codigo = fp.cod_agrupamento
        where c.cod_empresa = f_cod_empresa
          and c.status_ativo = true
          and cfp.cod_funcao_prolog = f_cod_permissao
        order by unidade, colaborador;
end;
$$;



CREATE OR REPLACE FUNCTION SUPORTE.FUNC_COLABORADOR_BUSCA_VINCULAR_PERMISSAO(F_COD_EMPRESA BIGINT)
    RETURNS TABLE
            (
                FUNCIONALIDADE  TEXT,
                PERMISSAO       TEXT,
                COD_EMPRESA     BIGINT,
                EMPRESA         TEXT,
                COD_UNIDADE     BIGINT,
                UNIDADE         TEXT,
                COD_COLABORADOR BIGINT,
                COLABORADOR     TEXT,
                CPF             BIGINT,
                DATA_NASCIMENTO DATE,
                CARGO           TEXT
            )
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    F_COD_FUNCAO BIGINT := 329;
BEGIN
    PERFORM SUPORTE.FUNC_HISTORICO_SALVA_EXECUCAO();
    RETURN QUERY
        SELECT *
        FROM SUPORTE.FUNC_COLABORADOR_BUSCA_POR_PERMISSAO_EMPRESA(F_COD_EMPRESA, F_COD_FUNCAO);
END;
$$;



CREATE OR REPLACE FUNCTION SUPORTE.FUNC_COLABORADOR_DELETA_COLABORADOR(F_CPF_COLABORADOR BIGINT,
                                                                       F_COD_COLABORADOR BIGINT,
                                                                       F_COD_UNIDADE BIGINT,
                                                                       F_MOTIVO_DELECAO TEXT,
                                                                       OUT AVISO_COLABORADOR_DELETADO TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    QTD_LINHAS_ATUALIZADAS BIGINT;
    F_COD_EMPRESA          BIGINT := (SELECT U.COD_EMPRESA
                                      FROM UNIDADE U
                                      WHERE U.CODIGO = F_COD_UNIDADE);
BEGIN
    PERFORM SUPORTE.FUNC_HISTORICO_SALVA_EXECUCAO();
    -- Verifica integridade de colaborador com unidade.
    PERFORM FUNC_GARANTE_INTEGRIDADE_EMPRESA_COLABORADOR(F_COD_EMPRESA, F_CPF_COLABORADOR);

    -- Deleta colaborador.
    UPDATE COLABORADOR_DATA
    SET DELETADO            = TRUE,
        DATA_HORA_DELETADO  = NOW(),
        PG_USERNAME_DELECAO = SESSION_USER,
        MOTIVO_DELECAO      = F_MOTIVO_DELECAO
    WHERE CODIGO = F_COD_COLABORADOR
      AND COD_UNIDADE = F_COD_UNIDADE
      AND CPF = F_CPF_COLABORADOR;

    GET DIAGNOSTICS QTD_LINHAS_ATUALIZADAS = ROW_COUNT;

    IF (QTD_LINHAS_ATUALIZADAS <= 0)
    THEN
        RAISE EXCEPTION 'Erro ao deletar o colaborador de Código: %, CPF: % e Unidade: %',
            F_COD_COLABORADOR, F_CPF_COLABORADOR, F_COD_UNIDADE;
    END IF;

    -- Desloga colaborador.
    DELETE FROM TOKEN_AUTENTICACAO WHERE COD_COLABORADOR = F_COD_COLABORADOR AND CPF_COLABORADOR = F_CPF_COLABORADOR;

    SELECT 'COLABORADOR COM CÓDIGO: '
               || F_COD_COLABORADOR
               || ', CPF: '
               || F_CPF_COLABORADOR
               || ', CÓDIGO DA UNIDADE: '
               || F_COD_UNIDADE
               || ' DELETADO COM SUCESSO!'
    INTO AVISO_COLABORADOR_DELETADO;
END
$$;



CREATE OR REPLACE FUNCTION SUPORTE.FUNC_COLABORADOR_TRANSFERE_ENTRE_EMPRESAS(F_COD_UNIDADE_ORIGEM BIGINT,
                                                                             F_CPF_COLABORADOR BIGINT,
                                                                             F_COD_UNIDADE_DESTINO INTEGER,
                                                                             F_COD_EMPRESA_DESTINO BIGINT,
                                                                             F_COD_SETOR_DESTINO BIGINT,
                                                                             F_COD_EQUIPE_DESTINO BIGINT,
                                                                             F_COD_FUNCAO_DESTINO INTEGER,
                                                                             F_MATRICULA_TRANS INTEGER DEFAULT NULL,
                                                                             F_MATRICULA_AMBEV INTEGER DEFAULT NULL,
                                                                             F_NIVEL_PERMISSAO INTEGER DEFAULT 0,
                                                                             OUT AVISO_COLABORADOR_TRANSFERIDO TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    EMPRESA_ORIGEM BIGINT := (SELECT U.COD_EMPRESA AS EMPRESA_ORIGEM
                              FROM UNIDADE U
                              WHERE U.CODIGO = F_COD_UNIDADE_ORIGEM);
BEGIN
    PERFORM SUPORTE.FUNC_HISTORICO_SALVA_EXECUCAO();
    -- VERIFICA SE EMPRESA ORIGEM/DESTINO SÃO DISTINTAS
    PERFORM FUNC_GARANTE_EMPRESAS_DISTINTAS(EMPRESA_ORIGEM, F_COD_EMPRESA_DESTINO);

    -- VERIFICA SE UNIDADE DESTINO EXISTE E SE PERTENCE A EMPRESA.
    PERFORM FUNC_GARANTE_INTEGRIDADE_EMPRESA_UNIDADE(F_COD_EMPRESA_DESTINO, F_COD_UNIDADE_DESTINO);

    --VERIFICA SE O COLABORADOR ESTÁ CADASTRADO E SE PERTENCE A UNIDADE ORIGEM.
    PERFORM FUNC_GARANTE_INTEGRIDADE_UNIDADE_COLABORADOR(F_COD_UNIDADE_ORIGEM, F_CPF_COLABORADOR);

    -- VERIFICA SE O SETOR EXISTE NA UNIDADE DESTINO.
    PERFORM FUNC_GARANTE_SETOR_EXISTE(F_COD_UNIDADE_DESTINO, F_COD_SETOR_DESTINO);

    -- VERIFICA SE A EQUIPE EXISTE NA UNIDADE DESTINO.
    PERFORM FUNC_GARANTE_EQUIPE_EXISTE(F_COD_UNIDADE_DESTINO, F_COD_EQUIPE_DESTINO);

    -- VERIFICA SE A FUNÇÃO EXISTE NA EMPRESA DESTINO.
    PERFORM FUNC_GARANTE_CARGO_EXISTE(F_COD_EMPRESA_DESTINO, F_COD_FUNCAO_DESTINO);

    -- VERIFICA SE PERMISSÃO EXISTE
    IF NOT EXISTS(SELECT P.CODIGO FROM PERMISSAO P WHERE P.CODIGO = F_NIVEL_PERMISSAO)
    THEN
        RAISE EXCEPTION 'Não existe permissão com o código: %', F_NIVEL_PERMISSAO;
    END IF;

    -- TRANSFERE COLABORADOR
    UPDATE COLABORADOR
    SET COD_UNIDADE     = F_COD_UNIDADE_DESTINO,
        COD_EMPRESA     = F_COD_EMPRESA_DESTINO,
        COD_SETOR       = F_COD_SETOR_DESTINO,
        COD_EQUIPE      = F_COD_EQUIPE_DESTINO,
        COD_FUNCAO      = F_COD_FUNCAO_DESTINO,
        MATRICULA_TRANS = F_MATRICULA_TRANS,
        MATRICULA_AMBEV = F_MATRICULA_AMBEV,
        COD_PERMISSAO   = F_NIVEL_PERMISSAO
    WHERE CPF = F_CPF_COLABORADOR
      AND COD_UNIDADE = F_COD_UNIDADE_ORIGEM;

    SELECT ('COLABORADOR: '
                || (SELECT C.NOME FROM COLABORADOR C WHERE C.CPF = F_CPF_COLABORADOR)
                || ' , TRANSFERIDO PARA A UNIDADE: '
        || (SELECT U.NOME FROM UNIDADE U WHERE U.CODIGO = F_COD_UNIDADE_DESTINO))
    INTO AVISO_COLABORADOR_TRANSFERIDO;
END;
$$;



CREATE OR REPLACE FUNCTION SUPORTE.FUNC_COLABORADOR_TRANSFERE_ENTRE_UNIDADES(F_COD_EMPRESA_ORIGEM BIGINT,
                                                                             F_COD_UNIDADE_ORIGEM BIGINT,
                                                                             F_COD_UNIDADE_DESTINO BIGINT,
                                                                             F_CPF_COLABORADOR BIGINT,
                                                                             F_COD_SETOR_DESTINO BIGINT,
                                                                             F_COD_EQUIPE_DESTINO BIGINT,
                                                                             F_COD_FUNCAO_DESTINO INTEGER,
                                                                             OUT COLABORADOR_TRANSFERIDO TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
BEGIN
    PERFORM SUPORTE.FUNC_HISTORICO_SALVA_EXECUCAO();
    -- VERIFICA SE EMPRESA ORIGEM POSSUI UNIDADE ORIGEM.
    PERFORM FUNC_GARANTE_EMPRESA_POSSUI_UNIDADE(F_COD_EMPRESA_ORIGEM, F_COD_UNIDADE_ORIGEM);

    -- VERIFICA SE EMPRESA POSSUI UNIDADE DESTINO.
    PERFORM FUNC_GARANTE_EMPRESA_POSSUI_UNIDADE(F_COD_EMPRESA_ORIGEM, F_COD_UNIDADE_DESTINO);

    -- VERIFICA SE COLABORADOR EXISTE NA UNIDADE ORIGEM.
    PERFORM FUNC_GARANTE_INTEGRIDADE_UNIDADE_COLABORADOR(F_COD_UNIDADE_ORIGEM, F_CPF_COLABORADOR);

    -- VERIFICA SE O SETOR EXISTE NA UNIDADE DESTINO.
    PERFORM FUNC_GARANTE_SETOR_EXISTE(F_COD_UNIDADE_DESTINO, F_COD_SETOR_DESTINO);

    -- VERIFICA SE A EQUIPE EXISTE NA UNIDADE DESTINO.
    PERFORM FUNC_GARANTE_EQUIPE_EXISTE(F_COD_UNIDADE_DESTINO, F_COD_EQUIPE_DESTINO);

    -- VERIFICA SE A FUNÇÃO EXISTE NA EMPRESA DESTINO.
    PERFORM FUNC_GARANTE_CARGO_EXISTE(F_COD_EMPRESA_ORIGEM, F_COD_FUNCAO_DESTINO);

    -- TRANSFERE COLABORADOR.
    UPDATE COLABORADOR_DATA
    SET COD_UNIDADE  = F_COD_UNIDADE_DESTINO,
        COD_SETOR    = F_COD_SETOR_DESTINO,
        COD_EQUIPE   = F_COD_EQUIPE_DESTINO,
        COD_FUNCAO   = F_COD_FUNCAO_DESTINO,
        -- Também ativa o colaborador ao transferir.
        STATUS_ATIVO = TRUE
    WHERE CPF = F_CPF_COLABORADOR
      AND COD_UNIDADE = F_COD_UNIDADE_ORIGEM;

    -- MENSAGEM DE SUCESSO.
    SELECT ('COLABORADOR COM CPF: '
                || (SELECT C.CPF
                    FROM COLABORADOR C
                    WHERE C.CPF = F_CPF_COLABORADOR
                      AND COD_UNIDADE = F_COD_UNIDADE_DESTINO)
                || ', FOI TRANSFERIDO PARA A UNIDADE: '
        || (SELECT U.NOME FROM UNIDADE U WHERE U.CODIGO = F_COD_UNIDADE_DESTINO))
    INTO COLABORADOR_TRANSFERIDO;
END
$$;



CREATE OR REPLACE FUNCTION SUPORTE.FUNC_INTERVALO_DELETA_INTERVALO_BY_CODIGO(F_COD_UNIDADE_INTERVALO BIGINT,
                                                                             F_CPF BIGINT,
                                                                             F_COD_INTERVALO BIGINT,
                                                                             OUT MENSAGEM_SUCESSO TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    V_COD_COLABORADOR BIGINT   := (SELECT C.CODIGO
                                   FROM COLABORADOR C
                                            JOIN INTERVALO I ON I.CODIGO = F_COD_INTERVALO AND I.CPF_COLABORADOR = C.CPF
                                       AND C.CPF = F_CPF
                                       AND I.COD_UNIDADE = F_COD_UNIDADE_INTERVALO);
    V_CODS_AJUSTE     BIGINT[] := (SELECT ARRAY_AGG(MH.COD_AJUSTE)
                                   FROM MARCACAO_HISTORICO MH
                                   WHERE MH.COD_MARCACAO = F_COD_INTERVALO);
BEGIN
    PERFORM SUPORTE.FUNC_HISTORICO_SALVA_EXECUCAO();
    --Verifica se o colaborador pertence a essa unidade.
    PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE_INTERVALO);

    -- Existe a possibilidade do colaborador ter registrado um intervalo em uma unidade e posteriormente ter sido
    -- transferido para outra, por este motivo utilizamos o código da unidade do intervalo para aumentar a segurança.

    -- Verifica se o intervalo a ser deletado foi registrado pelo colaborador informado e na unidade informada.
    IF V_COD_COLABORADOR IS NULL
    THEN
        PERFORM THROW_GENERIC_ERROR('Não foi possível encontrar este intervalo para este colaborador nesta unidade.');
    END IF;

    DELETE
    FROM MARCACAO_INCONSISTENCIA MI
    WHERE MI.COD_MARCACAO_INCONSISTENTE = F_COD_INTERVALO;

    DELETE
    FROM MARCACAO_VINCULO_INICIO_FIM MVIF
    WHERE MVIF.COD_MARCACAO_INICIO = F_COD_INTERVALO
       OR MVIF.COD_MARCACAO_FIM = F_COD_INTERVALO;

    DELETE
    FROM MARCACAO_INICIO MI
    WHERE MI.COD_MARCACAO_INICIO = F_COD_INTERVALO;

    DELETE
    FROM MARCACAO_FIM MF
    WHERE MF.COD_MARCACAO_FIM = F_COD_INTERVALO;

    DELETE
    FROM MARCACAO_HISTORICO MH
    WHERE MH.COD_MARCACAO = F_COD_INTERVALO;

    DELETE
    FROM INTERVALO I
    WHERE I.CODIGO = F_COD_INTERVALO;

    DELETE
    FROM MARCACAO_AJUSTE MJ
    WHERE MJ.COD_COLABORADOR_AJUSTE = V_COD_COLABORADOR
      AND MJ.CODIGO = ANY(V_CODS_AJUSTE);

    SELECT 'O intervalo foi apagado com sucesso.' INTO MENSAGEM_SUCESSO;
END;
$$;



CREATE OR REPLACE FUNCTION SUPORTE.FUNC_UNIDADE_ALTERA_LATITUDE_LONGITUDE(F_COD_UNIDADE BIGINT,
                                                                          F_LATITUDE_UNIDADE TEXT,
                                                                          F_LONGITUDE_UNIDADE TEXT,
                                                                          OUT AVISO TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
BEGIN
    PERFORM SUPORTE.FUNC_HISTORICO_SALVA_EXECUCAO();
    PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE);

    UPDATE UNIDADE
    SET LATITUDE_UNIDADE  = F_LATITUDE_UNIDADE,
        LONGITUDE_UNIDADE = F_LONGITUDE_UNIDADE
    WHERE CODIGO = F_COD_UNIDADE;

    SELECT 'LATITUDE E LONGITUDE DA UNIDADE '
               || (SELECT U.NOME FROM UNIDADE U WHERE U.CODIGO = F_COD_UNIDADE)
               || ', CÓDIGO UNIDADE: '
               || F_COD_UNIDADE
               || ' ALTERADAS.'
    INTO AVISO;
END ;
$$;



CREATE OR REPLACE FUNCTION SUPORTE.FUNC_UNIDADE_CADASTRA_UNIDADE(F_COD_EMPRESA BIGINT,
                                                                 F_COD_REGIONAL BIGINT,
                                                                 F_NOME_UNIDADE TEXT,
                                                                 F_TIMEZONE TEXT,
                                                                 F_PILARES_LIBERADOS INTEGER[],
                                                                 OUT AVISO_UNIDADE_CADASTRADA TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    QTD_LINHAS_CADASTRADAS BIGINT;
    COD_UNIDADE_CADASTRADA BIGINT;
    MAX_LENGTH_COLUMN      INTEGER := 40;
    COD_PILAR_GENTE        INTEGER := 3;
BEGIN
    PERFORM SUPORTE.FUNC_HISTORICO_SALVA_EXECUCAO();
    PERFORM FUNC_GARANTE_EMPRESA_EXISTE(F_COD_EMPRESA);
    PERFORM FUNC_GARANTE_REGIONAL_EXISTE(F_COD_REGIONAL);
    PERFORM FUNC_GARANTE_NOT_NULL(F_NOME_UNIDADE, 'Nome Unidade');
    PERFORM FUNC_GARANTE_NOT_NULL(F_TIMEZONE, 'Timezone');
    PERFORM FUNC_GARANTE_NOT_NULL(F_PILARES_LIBERADOS, 'Pilares Liberados');
    PERFORM FUNC_GARANTE_PILARES_VALIDOS(F_PILARES_LIBERADOS);

    -- Todas as unidades devem ter o pilar GENTE.
    SELECT ARRAY_APPEND(F_PILARES_LIBERADOS, COD_PILAR_GENTE) INTO F_PILARES_LIBERADOS;
    -- Após adicionar o pilar GENTE, removemos do array qualquer valor duplicado.
    SELECT ARRAY_DISTINCT(F_PILARES_LIBERADOS) INTO F_PILARES_LIBERADOS;

    -- Garante que nome unidade não tenha mais do que 40 caracteres.
    IF (LENGTH(TRIM_AND_REMOVE_EXTRA_SPACES(F_NOME_UNIDADE)) > MAX_LENGTH_COLUMN)
    THEN
        RAISE EXCEPTION 'O nome da unidade não pode ter mais do que % caracteres', MAX_LENGTH_COLUMN;
    END IF;

    -- Garante que unidade com mesmo nome não exista para a mesma empresa.
    IF (LOWER(UNACCENT(TRIM_AND_REMOVE_EXTRA_SPACES(F_NOME_UNIDADE))) IN
        (SELECT LOWER(UNACCENT(TRIM_AND_REMOVE_EXTRA_SPACES(U.NOME)))
         FROM UNIDADE U
         WHERE U.COD_EMPRESA = F_COD_EMPRESA))
    THEN
        RAISE EXCEPTION 'Já existe uma unidade com nome % cadastrada para a empresa %',
            TRIM_AND_REMOVE_EXTRA_SPACES(F_NOME_UNIDADE),
            F_COD_EMPRESA;
    END IF;

    -- Garante que o timezone informado exista.
    IF (NOT IS_TIMEZONE(F_TIMEZONE))
    THEN
        RAISE EXCEPTION '% não é um timezone válido', F_TIMEZONE;
    END IF;

    -- Insere a unidade.
    INSERT INTO UNIDADE (NOME, TIMEZONE, COD_REGIONAL, COD_EMPRESA)
    VALUES (F_NOME_UNIDADE, F_TIMEZONE, F_COD_REGIONAL, F_COD_EMPRESA) RETURNING CODIGO INTO COD_UNIDADE_CADASTRADA;

    -- Verifica se insert de unidade funcionou.
    IF (COD_UNIDADE_CADASTRADA <= 0)
    THEN
        RAISE EXCEPTION 'Erro ao cadastrar unidade de nome: %', F_NOME_UNIDADE;
    END IF;

    -- Insere os pilares.
    INSERT INTO UNIDADE_PILAR_PROLOG (COD_UNIDADE, COD_PILAR)
    SELECT COD_UNIDADE_CADASTRADA,
           UNNEST(F_PILARES_LIBERADOS);

    GET DIAGNOSTICS QTD_LINHAS_CADASTRADAS = ROW_COUNT;

    -- Verifica se insert de pilares funcionou.
    IF (QTD_LINHAS_CADASTRADAS <= 0)
    THEN
        RAISE EXCEPTION 'Erro ao liberar pilares para a unidade de nome: %', F_NOME_UNIDADE;
    END IF;

    SELECT 'UNIDADE CADASTRADA: '
               || F_NOME_UNIDADE
               || ', CÓDIGO UNIDADE: '
               || COD_UNIDADE_CADASTRADA
               || ', E PILARES: '
               || ARRAY_TO_STRING(F_PILARES_LIBERADOS, ', ')
    INTO AVISO_UNIDADE_CADASTRADA;
END ;
$$;