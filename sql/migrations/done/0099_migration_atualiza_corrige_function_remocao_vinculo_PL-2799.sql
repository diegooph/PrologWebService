-- 2020-06-03 -> Substitui código do cliente por cod_pneu e corrige a function. (thaisksf - PL-2799).
DROP FUNCTION SUPORTE.FUNC_PNEU_REMOVE_VINCULO_PNEU(F_CPF_SOLICITANTE BIGINT,
                                                    F_COD_UNIDADE BIGINT,
                                                    F_PLACA_VEICULO TEXT,
                                                    F_LISTA_PNEUS CHARACTER VARYING[],
                                                    OUT AVISO_PNEUS_DESVINCULADOS TEXT);

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
    -- VERIFICA SE COLABORADOR POSSUI INTEGRIDADE COM UNIDADE;
    PERFORM FUNC_GARANTE_INTEGRIDADE_UNIDADE_COLABORADOR(F_COD_UNIDADE, F_CPF_SOLICITANTE);

    -- VERIFICA SE UNIDADE EXISTE;
    PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE);

    -- VERIFICA SE VEÍCULO EXISTE;
    PERFORM FUNC_GARANTE_VEICULO_EXISTE(F_COD_UNIDADE, F_PLACA_VEICULO);

    -- VERIFICA QUANTIADE DE PNEUS RECEBIDA;
    IF (ARRAY_LENGTH(F_LISTA_COD_PNEUS, 1) > 0)
    THEN
        -- CRIA PROCESSO PARA MOVIMENTAÇÃO
        INSERT INTO MOVIMENTACAO_PROCESSO(COD_UNIDADE, DATA_HORA, CPF_RESPONSAVEL, OBSERVACAO)
        VALUES (F_COD_UNIDADE,
                DATA_HORA_ATUAL,
                F_CPF_SOLICITANTE,
                'Processo para desvincular o pneu de uma placa') RETURNING CODIGO INTO COD_PROCESSO_MOVIMENTACAO_CRIADO;

        FOREACH COD_PNEU_DA_VEZ IN ARRAY F_LISTA_COD_PNEUS
            LOOP
                -- VERIFICA SE PNEU NÃO ESTÁ VINCULADO A PLACA INFORMADA;
                IF NOT EXISTS(SELECT VP.PLACA
                              FROM VEICULO_PNEU VP
                              WHERE VP.PLACA = F_PLACA_VEICULO
                                AND VP.COD_PNEU = COD_PNEU_DA_VEZ)
                THEN
                    RAISE EXCEPTION 'Erro! O pneu com código: % não está vinculado ao veículo %',
                        COD_PNEU_DA_VEZ, F_PLACA_VEICULO;
                END IF;

                -- BUSCA VIDA ATUAL E POSICAO DO PNEU;
                SELECT P.VIDA_ATUAL, VP.POSICAO
                FROM PNEU P
                         JOIN VEICULO_PNEU VP ON P.CODIGO = VP.COD_PNEU
                WHERE P.CODIGO = COD_PNEU_DA_VEZ
                INTO VIDA_ATUAL_PNEU, POSICAO_PNEU;

                IF (COD_PROCESSO_MOVIMENTACAO_CRIADO > 0)
                THEN
                    -- INSERE MOVIMENTAÇÃO RETORNANDO O CÓDIGO DA MESMA;
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
                    WHERE P.CODIGO = COD_PNEU_DA_VEZ RETURNING CODIGO INTO COD_MOVIMENTACAO_CRIADA;

                    -- INSERE DESTINO DA MOVIMENTAÇÃO;
                    INSERT INTO MOVIMENTACAO_DESTINO(COD_MOVIMENTACAO, TIPO_DESTINO)
                    VALUES (COD_MOVIMENTACAO_CRIADA, STATUS_PNEU_ESTOQUE);

                    -- INSERE ORIGEM DA MOVIMENTAÇÃO;
                    PERFORM FUNC_MOVIMENTACAO_INSERT_MOVIMENTACAO_VEICULO_ORIGEM(COD_PNEU_DA_VEZ,
                                                                                 F_COD_UNIDADE,
                                                                                 STATUS_PNEU_EM_USO,
                                                                                 COD_MOVIMENTACAO_CRIADA,
                                                                                 F_PLACA_VEICULO,
                                                                                 KM_ATUAL_VEICULO,
                                                                                 POSICAO_PNEU);

                    -- REMOVE PNEU DO VINCULO;
                    DELETE FROM VEICULO_PNEU WHERE COD_PNEU = COD_PNEU_DA_VEZ AND PLACA = F_PLACA_VEICULO;

                    -- ATUALIZA STATUS DO PNEU
                    UPDATE PNEU
                    SET STATUS = STATUS_PNEU_ESTOQUE
                    WHERE CODIGO = COD_PNEU_DA_VEZ
                      AND COD_UNIDADE = F_COD_UNIDADE;

                    -- VERIFICA SE O PNEU POSSUI SERVIÇOS EM ABERTO;
                    IF EXISTS(SELECT AM.COD_PNEU
                              FROM AFERICAO_MANUTENCAO AM
                              WHERE AM.COD_UNIDADE = F_COD_UNIDADE
                                AND AM.COD_PNEU = COD_PNEU_DA_VEZ
                                AND AM.DATA_HORA_RESOLUCAO IS NULL
                                AND AM.CPF_MECANICO IS NULL
                                AND AM.FECHADO_AUTOMATICAMENTE_MOVIMENTACAO IS FALSE
                                AND AM.FECHADO_AUTOMATICAMENTE_INTEGRACAO IS FALSE)
                    THEN
                        -- REMOVE SERVIÇOS EM ABERTO;
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

    -- MENSAGEM DE SUCESSO;
    SELECT 'Movimentação realizada com sucesso!! Autorizada por ' || NOME_COLABORADOR ||
           ' com CPF: ' || F_CPF_SOLICITANTE || '. Os pneus que estavam na placa ' || F_PLACA_VEICULO ||
           ' foram movidos para estoque.'
    INTO AVISO_PNEUS_DESVINCULADOS;
END
$$;