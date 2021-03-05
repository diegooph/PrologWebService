CREATE OR REPLACE FUNCTION SUPORTE.TG_FUNC_CHECKLIST_OS_FECHAMENTO_MASSIVO_OS()
    RETURNS TRIGGER
    SECURITY DEFINER
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    TEM_VALOR_NULL                  BOOLEAN                  := (NEW.CPF_MECANICO IS NULL OR
                                                                 NEW.DATA_HORA_FIM_RESOLUCAO IS NULL OR
                                                                 NEW.DATA_HORA_INICIO_RESOLUCAO IS NULL OR
                                                                 NEW.TEMPO_REALIZACAO IS NULL OR
                                                                 NEW.PLACA_VEICULO IS NULL OR
                                                                 NEW.DATA_HORA_CONSERTO IS NULL OR
                                                                 NEW.FEEDBACK_CONSERTO IS NULL OR
                                                                 NEW.COD_UNIDADE IS NULL OR
                                                                 NEW.COD_OS IS NULL OR
                                                                 NEW.COD_PERGUNTA IS NULL OR
                                                                 NEW.COD_ALTERNATIVA IS NULL);
    KM_ATUAL_VEICULO                BIGINT                   := (SELECT V.KM
                                                                 FROM VEICULO V
                                                                 WHERE V.PLACA = NEW.PLACA_VEICULO);
    KM_VEICULO                      BIGINT                   := (CASE
                                                                     WHEN NEW.KM IS NULL THEN KM_ATUAL_VEICULO
                                                                     ELSE NEW.KM END);
    STATUS_RESOLUCAO_COSI_REALIZADO TEXT                     := 'R';
    STATUS_RESOLUCAO_COSI_PENDENTE  TEXT                     := 'P';
    STATUS_COS_FECHADO              TEXT                     := 'F';
    STATUS_COS_ABERTO               TEXT                     := 'A';
    QTD_ERROS                       BIGINT                   := 0;
    SOMA_ERRO                       BIGINT                   := 1;
    MSGS_ERROS                      TEXT;
    CODIGO_EMPRESA                  BIGINT                   := (SELECT U.COD_EMPRESA
                                                                 FROM UNIDADE U
                                                                 WHERE U.CODIGO = NEW.COD_UNIDADE);
    CODIGO_CHECKLIST                BIGINT                   := (SELECT COS.COD_CHECKLIST
                                                                 FROM CHECKLIST_ORDEM_SERVICO COS
                                                                 WHERE COS.CODIGO = NEW.COD_OS
                                                                   AND COS.COD_UNIDADE = NEW.COD_UNIDADE);
    DATA_HORA_CHECKLIST             TIMESTAMP WITH TIME ZONE := (SELECT C.DATA_HORA
                                                                 FROM CHECKLIST C
                                                                 WHERE C.CODIGO = CODIGO_CHECKLIST);
    PLACA_CADASTRADA                BOOLEAN                  := TRUE;
    QUEBRA_LINHA                    TEXT                     := CHR(10);
    VERIFICAR_OS BOOLEAN := FALSE;
BEGIN
    IF (TG_OP = 'UPDATE' AND OLD.STATUS_ITEM_FECHADO IS TRUE)
    THEN
        NEW.MENSAGEM_STATUS_ITEM = CONCAT(OLD.MENSAGEM_STATUS_ITEM, QUEBRA_LINHA, 'O ITEM JÁ ESTAVA FECHADO');
        NEW.MENSAGEM_STATUS_OS = OLD.MENSAGEM_STATUS_OS;
        NEW.CPF_MECANICO = OLD.CPF_MECANICO;
        NEW.DATA_HORA_FIM_RESOLUCAO = OLD.DATA_HORA_FIM_RESOLUCAO;
        NEW.DATA_HORA_INICIO_RESOLUCAO = OLD.DATA_HORA_INICIO_RESOLUCAO;
        NEW.TEMPO_REALIZACAO = OLD.TEMPO_REALIZACAO;
        NEW.PLACA_VEICULO = OLD.PLACA_VEICULO;
        NEW.KM = OLD.KM;
        NEW.DATA_HORA_CONSERTO = OLD.DATA_HORA_CONSERTO;
        NEW.FEEDBACK_CONSERTO = OLD.FEEDBACK_CONSERTO;
        NEW.COD_UNIDADE = OLD.COD_UNIDADE;
        NEW.COD_OS = OLD.COD_OS;
        NEW.COD_PERGUNTA = OLD.COD_PERGUNTA;
        NEW.COD_ALTERNATIVA = OLD.COD_ALTERNATIVA;
        NEW.STATUS_ITEM_FECHADO = OLD.STATUS_ITEM_FECHADO;
        NEW.STATUS_OS_FECHADA = OLD.STATUS_OS_FECHADA;
        NEW.USUARIO := CONCAT(OLD.USUARIO, QUEBRA_LINHA, NEW.USUARIO);
        NEW.DATA_SOLICITACAO := OLD.DATA_SOLICITACAO;
        NEW.HORA_SOLICITACAO := OLD.HORA_SOLICITACAO;
    ELSE
        NEW.USUARIO := SESSION_USER;
        NEW.DATA_SOLICITACAO := CURRENT_DATE;
        NEW.HORA_SOLICITACAO := CURRENT_TIME;

        --VERIFICA INFORMAÇÕES
        IF (TEM_VALOR_NULL)
        THEN
            QTD_ERROS = QTD_ERROS + SOMA_ERRO;
            MSGS_ERROS = CONCAT(MSGS_ERROS, QUEBRA_LINHA, QTD_ERROS, ' - EXISTEM CAMPOS SEM PREENCHIMENTO');
        END IF;

        --VERIFICA SE UNIDADE ESTÁ CADASTRADA
        IF ((NEW.COD_UNIDADE IS NOT NULL) AND
            NOT EXISTS(SELECT U.CODIGO FROM UNIDADE U WHERE U.CODIGO = NEW.COD_UNIDADE))
        THEN
            QTD_ERROS = QTD_ERROS + SOMA_ERRO;
            MSGS_ERROS = CONCAT(MSGS_ERROS, QUEBRA_LINHA, QTD_ERROS, ' - UNIDADE NÃO CADASTRADA');
        END IF;

        --VERIFICA SE O COLABORADOR (MECÂNICO) EXISTE
        IF ((NEW.CPF_MECANICO IS NOT NULL) AND
            NOT EXISTS(SELECT C.CPF FROM COLABORADOR C WHERE C.CPF = NEW.CPF_MECANICO))
        THEN
            QTD_ERROS = QTD_ERROS + SOMA_ERRO;
            MSGS_ERROS = CONCAT(MSGS_ERROS, QUEBRA_LINHA, QTD_ERROS, ' - MECÂNICO NÃO CADASTRADO');
            --VERIFICA SE O COLABORADOR (MECÂNICO) É DA EMPRESA
        ELSEIF ((NEW.CPF_MECANICO IS NOT NULL) AND
                NOT EXISTS(
                        SELECT C.CPF
                        FROM COLABORADOR C
                        WHERE C.CPF = NEW.CPF_MECANICO
                          AND C.COD_EMPRESA = CODIGO_EMPRESA))
        THEN
            QTD_ERROS = QTD_ERROS + SOMA_ERRO;
            MSGS_ERROS =
                    CONCAT(MSGS_ERROS, QUEBRA_LINHA, QTD_ERROS,
                           ' - MECÂNICO NÃO PERTENCE À EMPRESA DA ORDEM DE SERVIÇO');
        END IF;

        --VERIFICA SE PLACA ESTÁ CADASTRADA
        IF ((NEW.PLACA_VEICULO IS NOT NULL) AND
            NOT EXISTS(SELECT V.PLACA FROM VEICULO V WHERE V.PLACA = NEW.PLACA_VEICULO))
        THEN
            QTD_ERROS = QTD_ERROS + SOMA_ERRO;
            MSGS_ERROS = CONCAT(MSGS_ERROS, QUEBRA_LINHA, QTD_ERROS, ' - PLACA NÃO ESTÁ CADASTRADA');
            PLACA_CADASTRADA = FALSE;
        END IF;

        --VERIFICAÇÕES REFERENTE ÀS DATAS E HORAS
        ---DATA/H INICIO DA RESOLUÇÃO:
        ----Verifica e adiciona msg de erro se a data/h atual for menor que a data/h do início da resolução
        IF ((NEW.DATA_HORA_INICIO_RESOLUCAO IS NOT NULL) AND ((NEW.DATA_SOLICITACAO + NEW.HORA_SOLICITACAO) < NEW.DATA_HORA_INICIO_RESOLUCAO))
        THEN
            QTD_ERROS = QTD_ERROS + SOMA_ERRO;
            MSGS_ERROS = CONCAT(MSGS_ERROS, QUEBRA_LINHA, QTD_ERROS,
                                ' - A DATA/HORA DE INICIO DA RESOLUÇÃO NÃO PODE SER MAIOR QUE A ATUAL ');
            ----Verifica e adiciona msg de erro se a  data do inicio da resolução for menor que a data/h do checklist realizado
        ELSEIF ((NEW.DATA_HORA_INICIO_RESOLUCAO IS NOT NULL)
            AND (NEW.DATA_HORA_INICIO_RESOLUCAO < DATA_HORA_CHECKLIST))
        THEN
            QTD_ERROS = QTD_ERROS + SOMA_ERRO;
            MSGS_ERROS = CONCAT(MSGS_ERROS, QUEBRA_LINHA, QTD_ERROS,
                                ' - A DATA/HORA DE INICIO DA RESOLUÇÃO NÃO PODE SER MENOR QUE A DATA/HORA DA REALIZAÇÃO DO CHECKLIST');
        END IF;

        ---DATA/HR FIM DA RESOLUÇÃO
        ----Verifica e adiciona msg de erro se a data/h atual for menor que a data/h do fim da resolução
        IF ((NEW.DATA_HORA_FIM_RESOLUCAO IS NOT NULL) AND ((NEW.DATA_SOLICITACAO + NEW.HORA_SOLICITACAO) < NEW.DATA_HORA_FIM_RESOLUCAO))
        THEN
            QTD_ERROS = QTD_ERROS + SOMA_ERRO;
            MSGS_ERROS = CONCAT(MSGS_ERROS, QUEBRA_LINHA, QTD_ERROS,
                                ' - A DATA/HORA DE FIM DA RESOLUÇÃO NÃO PODE SER MAIOR QUE A ATUAL');
            ----Verifica e adiciona msg de erro se a data/h do fim da resolução for menor que a data/h do inicio da resolucao
        ELSEIF ((NEW.DATA_HORA_FIM_RESOLUCAO IS NOT NULL)
            AND (NEW.DATA_HORA_FIM_RESOLUCAO < NEW.DATA_HORA_INICIO_RESOLUCAO))
        THEN
            QTD_ERROS = QTD_ERROS + SOMA_ERRO;
            MSGS_ERROS = CONCAT(MSGS_ERROS, QUEBRA_LINHA, QTD_ERROS,
                                ' - A DATA/HORA DE FIM DA RESOLUÇÃO NÃO PODE SER MENOR QUE A DATA/HORA DE INICIO DA RESOLUCAO');
            ----Verifica e adiciona msg de erro se a data/h do fim da resolução for menor que a data/h do checklist realizado
        ELSEIF ((NEW.DATA_HORA_FIM_RESOLUCAO IS NOT NULL)
            AND (NEW.DATA_HORA_FIM_RESOLUCAO < DATA_HORA_CHECKLIST))
        THEN
            QTD_ERROS = QTD_ERROS + SOMA_ERRO;
            MSGS_ERROS = CONCAT(MSGS_ERROS, QUEBRA_LINHA, QTD_ERROS,
                                ' - A DATA/HORA DE FIM DA RESOLUÇÃO NÃO PODE SER MENOR QUE A DATA/HORA DA REALIZAÇÃO DO CHECKLIST');
        END IF;

        ---DATA/H CONSERTO
        ----Verifica e adiciona msg de erro se a data/h atual for menor que a data/h do conserto
        IF ((NEW.DATA_HORA_CONSERTO IS NOT NULL) AND ((NEW.DATA_SOLICITACAO + NEW.HORA_SOLICITACAO) < NEW.DATA_HORA_CONSERTO))
        THEN
            QTD_ERROS = QTD_ERROS + SOMA_ERRO;
            MSGS_ERROS = CONCAT(MSGS_ERROS, QUEBRA_LINHA, QTD_ERROS,
                                ' - A DATA/HORA DO CONSERTO NÃO PODE SER MAIOR QUE A ATUAL');
            ----Verifica e adiciona msg de erro se a data/h do conserto for menor que a data/h da realização do checklist
        ELSEIF ((NEW.DATA_HORA_CONSERTO IS NOT NULL)
            AND (NEW.DATA_HORA_CONSERTO < DATA_HORA_CHECKLIST))
        THEN
            QTD_ERROS = QTD_ERROS + SOMA_ERRO;
            MSGS_ERROS = CONCAT(MSGS_ERROS, QUEBRA_LINHA, QTD_ERROS,
                                ' - A DATA/HORA DO CONSERTO NÃO PODE SER MENOR QUE A DATA/HORA DA REALIZAÇÃO DO CHECKLIST');
            ----Verifica e adiciona msg de erro se a data/h do conserto for menor que a data/h do fim da resolucao
        ELSEIF ((NEW.DATA_HORA_CONSERTO IS NOT NULL) AND (NEW.DATA_HORA_CONSERTO < NEW.DATA_HORA_FIM_RESOLUCAO))
        THEN
            QTD_ERROS = QTD_ERROS + SOMA_ERRO;
            MSGS_ERROS =
                    CONCAT(MSGS_ERROS, QUEBRA_LINHA, QTD_ERROS,
                           ' - A DATA/HORA DE CONSERTO NÃO PODE SER MENOR QUE A DATA/HORA DO FIM DA RESOLUCAO');
        END IF;

        ---VERIFICA SE COD_OS EXISTE
        IF ((NEW.COD_OS IS NOT NULL AND NEW.COD_UNIDADE IS NOT NULL) AND
            NOT EXISTS(SELECT COS.CODIGO FROM CHECKLIST_ORDEM_SERVICO COS WHERE COS.CODIGO = NEW.COD_OS))
        THEN
            QTD_ERROS = QTD_ERROS + SOMA_ERRO;
            MSGS_ERROS = CONCAT(MSGS_ERROS, QUEBRA_LINHA, QTD_ERROS, ' - CODIGO DA ORDEM DE SERVIÇO NÃO EXISTE');

            --VERIFICAÇÕES SE A OS EXISTIR
        ELSEIF ((NEW.COD_OS IS NOT NULL) AND
                EXISTS(SELECT COSI.COD_OS
                       FROM CHECKLIST_ORDEM_SERVICO_ITENS COSI
                       WHERE COSI.COD_OS = NEW.COD_OS))
        THEN
            ---VERIFICA SE A PLACA É DO CHECKLIST QUE GEROU A OS
            IF ((NEW.PLACA_VEICULO IS NOT NULL) AND (PLACA_CADASTRADA) AND NOT EXISTS(SELECT C.PLACA_VEICULO
                                                                                      FROM CHECKLIST C
                                                                                      WHERE C.CODIGO = CODIGO_CHECKLIST
                                                                                        AND C.PLACA_VEICULO = NEW.PLACA_VEICULO))
            THEN
                QTD_ERROS = QTD_ERROS + SOMA_ERRO;
                MSGS_ERROS =
                        CONCAT(MSGS_ERROS, QUEBRA_LINHA, QTD_ERROS,
                               ' - A PLACA NÃO PERTENCE AO CHECKLIST DA ORDEM DE SERVIÇO');
                ---VERIFICA SE OS PERTENCE À UNIDADE
            END IF;
            IF ((NEW.COD_UNIDADE IS NOT NULL) AND
                NOT EXISTS(SELECT COS.CODIGO
                           FROM CHECKLIST_ORDEM_SERVICO COS
                           WHERE COS.CODIGO = NEW.COD_OS
                             AND COS.COD_UNIDADE = NEW.COD_UNIDADE))
            THEN
                QTD_ERROS = QTD_ERROS + SOMA_ERRO;
                MSGS_ERROS =
                        CONCAT(MSGS_ERROS, QUEBRA_LINHA, QTD_ERROS,
                               ' - A ORDEM DE SERVIÇO ABERTA NÃO É DA UNIDADE INFORMADA');
            END IF;

            ---VERIFICA SE A PERGUNTA ESTÁ CONTIDA NA ORDEM DE SERVIÇO ITENS
            IF ((NEW.COD_PERGUNTA IS NOT NULL) AND
                NOT EXISTS(SELECT COSI.COD_PERGUNTA_PRIMEIRO_APONTAMENTO
                           FROM CHECKLIST_ORDEM_SERVICO_ITENS COSI
                           WHERE COSI.COD_OS = NEW.COD_OS
                             AND COSI.COD_PERGUNTA_PRIMEIRO_APONTAMENTO = NEW.COD_PERGUNTA))
            THEN
                QTD_ERROS = QTD_ERROS + SOMA_ERRO;
                MSGS_ERROS = CONCAT(MSGS_ERROS, QUEBRA_LINHA, QTD_ERROS,
                                    ' - A PERGUNTA NÃO EXISTE PARA A ORDEM DE SERVIÇO INFORMADA');
                ---VERIFICA SE A ALTERNATIVA ESTÁ CONTIDA NA PERGUNTA
            ELSEIF ((NEW.COD_PERGUNTA IS NOT NULL AND NEW.COD_ALTERNATIVA IS NOT NULL) AND
                    NOT EXISTS(SELECT COSI.COD_ALTERNATIVA_PRIMEIRO_APONTAMENTO
                               FROM CHECKLIST_ORDEM_SERVICO_ITENS COSI
                               WHERE COSI.COD_PERGUNTA_PRIMEIRO_APONTAMENTO = NEW.COD_PERGUNTA
                                 AND COSI.COD_ALTERNATIVA_PRIMEIRO_APONTAMENTO = NEW.COD_ALTERNATIVA))
            THEN
                QTD_ERROS = QTD_ERROS + SOMA_ERRO;
                MSGS_ERROS = CONCAT(MSGS_ERROS, QUEBRA_LINHA, QTD_ERROS,
                                    ' - A ALTERNATIVA NÃO EXISTE PARA PERGUNTA INFORMADA');
            END IF;
        END IF;

        --REALIZA O FECHAMENTO DOS ITENS SE NÃO HOUVER ERROS
        IF (QTD_ERROS > 0)
        THEN
            NEW.STATUS_ITEM_FECHADO := FALSE;
            NEW.MENSAGEM_STATUS_ITEM :=
                    CONCAT('ITEM NÃO FECHADO', QUEBRA_LINHA, 'QUANTIDADE DE ERROS: ', QTD_ERROS, QUEBRA_LINHA,
                           MSGS_ERROS);
            NEW.STATUS_OS_FECHADA := FALSE;
            NEW.MENSAGEM_STATUS_OS := 'FECHAMENTO NÃO REALIZADO';
        ELSEIF (NOT TEM_VALOR_NULL OR QTD_ERROS = 0)
        THEN
            ---VERIFICA SE O ITEM JÁ ESTAVA FECHADO (STATUS REALIZADO)
            IF ((NEW.COD_OS IS NOT NULL AND NEW.COD_PERGUNTA IS NOT NULL AND NEW.COD_ALTERNATIVA IS NOT NULL) AND
                EXISTS(SELECT COSI.CODIGO
                       FROM CHECKLIST_ORDEM_SERVICO_ITENS COSI
                       WHERE COSI.COD_OS = NEW.COD_OS
                         AND COSI.COD_PERGUNTA_PRIMEIRO_APONTAMENTO = NEW.COD_PERGUNTA
                         AND COSI.COD_ALTERNATIVA_PRIMEIRO_APONTAMENTO = NEW.COD_ALTERNATIVA
                         AND COSI.STATUS_RESOLUCAO = STATUS_RESOLUCAO_COSI_REALIZADO))
            THEN
                -- O ITEM JÁ ESTAVA FECHADO
                NEW.STATUS_ITEM_FECHADO = TRUE;
                NEW.MENSAGEM_STATUS_ITEM = 'O ITEM JÁ ESTAVA FECHADO';
                VERIFICAR_OS := TRUE;
            ELSE
                -- REALIZADO O FECHAMENTO DO ITEM
                UPDATE CHECKLIST_ORDEM_SERVICO_ITENS_DATA
                SET CPF_MECANICO               = NEW.CPF_MECANICO,
                    DATA_HORA_FIM_RESOLUCAO    = NEW.DATA_HORA_FIM_RESOLUCAO,
                    DATA_HORA_INICIO_RESOLUCAO = NEW.DATA_HORA_INICIO_RESOLUCAO,
                    TEMPO_REALIZACAO           = NEW.TEMPO_REALIZACAO,
                    KM                         = KM_VEICULO,
                    DATA_HORA_CONSERTO         = NEW.DATA_HORA_CONSERTO,
                    FEEDBACK_CONSERTO          = NEW.FEEDBACK_CONSERTO,
                    STATUS_RESOLUCAO           = STATUS_RESOLUCAO_COSI_REALIZADO
                WHERE COD_UNIDADE = NEW.COD_UNIDADE
                  AND COD_OS = NEW.COD_OS
                  AND COD_PERGUNTA_PRIMEIRO_APONTAMENTO = NEW.COD_PERGUNTA
                  AND COD_ALTERNATIVA_PRIMEIRO_APONTAMENTO = NEW.COD_ALTERNATIVA;
                NEW.STATUS_ITEM_FECHADO = TRUE;
                NEW.MENSAGEM_STATUS_ITEM := CONCAT('ITEM FECHADO ATRAVÉS DO SUPORTE', QUEBRA_LINHA, 'KM DO VEÍCULO NA HORA DO FECHAMENTO: ', KM_ATUAL_VEICULO);
                VERIFICAR_OS := TRUE;
            END IF;
        ELSE
            RAISE EXCEPTION 'ERRO AO FECHAR O ITEM DA ORDEM DE SERVIÇO';
        END IF;

        IF (VERIFICAR_OS)
        THEN
            -- VERIFICA SE EXISTEM ITENS PENDENTES NA OS
            IF EXISTS(SELECT COSI.CODIGO
                      FROM CHECKLIST_ORDEM_SERVICO_ITENS COSI
                      WHERE COSI.COD_OS = NEW.COD_OS
                        AND COSI.COD_UNIDADE = NEW.COD_UNIDADE
                        AND COSI.STATUS_RESOLUCAO = STATUS_RESOLUCAO_COSI_PENDENTE)
            THEN
                NEW.STATUS_OS_FECHADA := FALSE;
                NEW.MENSAGEM_STATUS_OS := 'OS ABERTA';
                -- VERIFICA SE A OS ESTÁ FECHADA MESMO COM ITENS PENDENTES
                IF EXISTS(SELECT COS.STATUS
                    FROM CHECKLIST_ORDEM_SERVICO COS
                    WHERE COS.STATUS = STATUS_COS_FECHADO
                      AND COS.CODIGO = NEW.COD_OS
                      AND COS.COD_UNIDADE = NEW.COD_UNIDADE)
                THEN
                    -- SE ESTIVER, É UM ERRO. ENTÃO REABRE A OS E EXIBE MSG ESPECÍFICA.
                    UPDATE CHECKLIST_ORDEM_SERVICO
                    SET STATUS = STATUS_COS_ABERTO
                    WHERE CODIGO = NEW.COD_OS
                      AND COD_UNIDADE = NEW.COD_UNIDADE;
                    NEW.STATUS_OS_FECHADA := FALSE;
                    NEW.MENSAGEM_STATUS_OS :=
                            'ORDEM DE SERVIÇO ESTAVA FECHADA MAS POSSUÍA ITENS PENDENTES - OS FOI REABERTA';
                END IF;
            ELSE
                -- SE NÃO EXISTIR ITENS PENDENTES, A OS PODE SER FECHADA.
                UPDATE CHECKLIST_ORDEM_SERVICO_DATA
                SET STATUS               = STATUS_COS_FECHADO,
                    DATA_HORA_FECHAMENTO = (SELECT COSI.DATA_HORA_FIM_RESOLUCAO
                                            FROM CHECKLIST_ORDEM_SERVICO_ITENS COSI
                                            WHERE COSI.COD_UNIDADE = NEW.COD_UNIDADE
                                              AND COSI.COD_OS = NEW.COD_OS
                                              AND COSI.COD_PERGUNTA_PRIMEIRO_APONTAMENTO = NEW.COD_PERGUNTA
                                              AND COSI.COD_ALTERNATIVA_PRIMEIRO_APONTAMENTO = NEW.COD_ALTERNATIVA)
                WHERE COD_UNIDADE = NEW.COD_UNIDADE
                  AND CODIGO NOT IN
                      (SELECT COD_OS
                       FROM CHECKLIST_ORDEM_SERVICO_ITENS
                       WHERE COD_UNIDADE = NEW.COD_UNIDADE
                         AND CPF_MECANICO IS NULL);
                NEW.STATUS_OS_FECHADA := TRUE;
                NEW.MENSAGEM_STATUS_OS := 'OS FECHADA';
            END IF;
        END IF;
    END IF;
    RETURN NEW;
END
$$;