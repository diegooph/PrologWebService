-- 2020-07-06 -> Adiciona informações dos colaboradores cadastrados ao retorno de sucesso. (thaisksf - PL-2802).
CREATE OR REPLACE FUNCTION INTERNO.FUNC_RESETA_EMPRESA_APRESENTACAO(F_COD_EMPRESA_BASE BIGINT,
                                                                    F_COD_EMPRESA_USUARIO BIGINT,
                                                                    OUT MENSAGEM_SUCESSO TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_COD_UNIDADES_BASE                     BIGINT[] := (SELECT ARRAY_AGG(U.CODIGO)
                                                         FROM UNIDADE U
                                                         WHERE U.COD_EMPRESA = F_COD_EMPRESA_BASE);
    V_COD_UNIDADE_BASE                      BIGINT;
    V_COD_UNIDADES_USUARIO                  BIGINT[] := (SELECT ARRAY_AGG(U.CODIGO)
                                                         FROM UNIDADE U
                                                         WHERE U.COD_EMPRESA = F_COD_EMPRESA_USUARIO);
    V_COD_UNIDADE_USUARIO_NOVA              BIGINT;
    V_COD_COLABORADORES_USUARIO             BIGINT[] := (SELECT ARRAY_AGG(CD.CODIGO)
                                                         FROM COLABORADOR_DATA CD
                                                         WHERE CD.COD_UNIDADE = ANY (V_COD_UNIDADES_USUARIO));
    V_COD_AFERICOES                         BIGINT[] := (SELECT ARRAY_AGG(AD.CODIGO)
                                                         FROM AFERICAO_DATA AD
                                                         WHERE AD.COD_UNIDADE = ANY (V_COD_UNIDADES_USUARIO));
    V_COD_CHECKLISTS                        BIGINT[] := (SELECT ARRAY_AGG(CD.CODIGO)
                                                         FROM CHECKLIST_DATA CD
                                                         WHERE CD.COD_UNIDADE = ANY (V_COD_UNIDADES_USUARIO));
    V_COD_CHECKLISTS_MODELO                 BIGINT[] := (SELECT DISTINCT ARRAY_AGG(CMD.CODIGO)
                                                         FROM CHECKLIST_MODELO_DATA CMD
                                                         WHERE CMD.COD_UNIDADE = ANY (V_COD_UNIDADES_USUARIO));
    V_TOKENS_CHECKLISTS_OFF                 TEXT     := (SELECT ARRAY_AGG(CODU.TOKEN_SINCRONIZACAO_CHECKLIST)
                                                         FROM CHECKLIST_OFFLINE_DADOS_UNIDADE CODU
                                                         WHERE CODU.COD_UNIDADE = ANY (V_COD_UNIDADES_USUARIO));
    V_COD_MOVIMENTACOES                     BIGINT[] := (SELECT ARRAY_AGG(MO.CODIGO)
                                                         FROM MOVIMENTACAO MO
                                                         WHERE MO.COD_UNIDADE = ANY (V_COD_UNIDADES_USUARIO));
    V_COD_SOCORROS                          BIGINT[] := (SELECT ARRAY_AGG(SR.CODIGO)
                                                         FROM SOCORRO_ROTA SR
                                                         WHERE SR.COD_UNIDADE = ANY (V_COD_UNIDADES_USUARIO));
    V_COD_VEICULOS_TRANSFERENCIAS_PROCESSOS BIGINT[] := (SELECT ARRAY_AGG(VTP.CODIGO)
                                                         FROM VEICULO_TRANSFERENCIA_PROCESSO VTP
                                                         WHERE (VTP.COD_UNIDADE_DESTINO = ANY (V_COD_UNIDADES_USUARIO))
                                                            OR (VTP.COD_UNIDADE_ORIGEM = ANY (V_COD_UNIDADES_USUARIO)));
    V_COD_PNEU_TRANSFERENCIAS_PROCESSOS     BIGINT[] := (SELECT ARRAY_AGG(PTP.CODIGO)
                                                         FROM PNEU_TRANSFERENCIA_PROCESSO PTP
                                                         WHERE (PTP.COD_UNIDADE_ORIGEM = ANY (V_COD_UNIDADES_USUARIO))
                                                            OR (PTP.COD_UNIDADE_DESTINO = ANY (V_COD_UNIDADES_USUARIO)));
    V_COD_COLABORADORES_NPS                 BIGINT[] := (SELECT ARRAY_AGG(COLABORADORES.COD_COLABORADOR_NPS)
                                                         FROM (SELECT NBPC.COD_COLABORADOR_BLOQUEIO AS COD_COLABORADOR_NPS
                                                               FROM CS.NPS_BLOQUEIO_PESQUISA_COLABORADOR NBPC
                                                               WHERE NBPC.COD_COLABORADOR_BLOQUEIO = ANY (V_COD_COLABORADORES_USUARIO)
                                                               UNION
                                                               SELECT NR.COD_COLABORADOR_RESPOSTAS AS COD_COLABORADOR_NPS
                                                               FROM CS.NPS_RESPOSTAS NR
                                                               WHERE NR.COD_COLABORADOR_RESPOSTAS = ANY (V_COD_COLABORADORES_USUARIO)) COLABORADORES);
    V_COD_TREINAMENTOS                      BIGINT[] := (SELECT ARRAY_AGG(T.CODIGO)
                                                         FROM TREINAMENTO T
                                                         WHERE T.COD_UNIDADE = ANY (V_COD_UNIDADES_USUARIO));
    V_COD_SERVICOS_REALIZADOS               BIGINT[] := (SELECT ARRAY_AGG(PSR.CODIGO)
                                                         FROM PNEU_SERVICO_REALIZADO_DATA PSR
                                                         WHERE PSR.COD_UNIDADE = ANY (V_COD_UNIDADES_USUARIO));
    V_COD_INTERVALO                         BIGINT[] := (SELECT ARRAY_AGG(IU.COD_UNIDADE)
                                                         FROM INTERVALO_UNIDADE IU
                                                         WHERE IU.COD_UNIDADE = ANY (V_COD_UNIDADES_USUARIO));
    V_COD_MARCACOES                         BIGINT[] := (SELECT ARRAY_AGG(I.CODIGO)
                                                         FROM INTERVALO I
                                                         WHERE I.COD_UNIDADE = ANY (V_COD_UNIDADES_USUARIO));
    V_COD_RELATOS                           BIGINT[] := (SELECT ARRAY_AGG(R.CODIGO)
                                                         FROM RELATO R
                                                         WHERE R.COD_UNIDADE = ANY (V_COD_UNIDADES_USUARIO));
    V_COD_QUIZ                              BIGINT[] := (SELECT ARRAY_AGG(Q.CODIGO)
                                                         FROM QUIZ Q
                                                         WHERE Q.COD_UNIDADE = ANY (V_COD_UNIDADES_USUARIO));
    V_COD_FALE_CONOSCO                      BIGINT[] := (SELECT ARRAY_AGG(FC.CODIGO)
                                                         FROM FALE_CONOSCO FC
                                                         WHERE FC.COD_UNIDADE = ANY (V_COD_UNIDADES_USUARIO));
    V_COD_TESTES_AFERIDOR                   BIGINT[] := (SELECT ARRAY_AGG(PT.CODIGO)
                                                         FROM AFERIDOR.PROCEDIMENTO_TESTE PT
                                                         WHERE PT.COD_COLABORADOR_EXECUCAO = ANY (V_COD_COLABORADORES_USUARIO));
    V_COLABORADORES_CADASTRADOS             TEXT[] ;
BEGIN
    -- VERIFICA SE EMPRESAS EXISTEM.
    PERFORM FUNC_GARANTE_EMPRESA_EXISTE(F_COD_EMPRESA_BASE);
    PERFORM FUNC_GARANTE_EMPRESA_EXISTE(F_COD_EMPRESA_USUARIO);

    -- BUSCA E DELETA VÍNCULOS QUE POSSAM EXISTIR DE COLABORADOR, VEÍCULOS E PNEUS.
    --- AFERIÇAO.
    IF (V_COD_AFERICOES IS NOT NULL)
    THEN
        PERFORM INTERNO.FUNC_DELETA_AFERICOES_DEPENDENCIAS(V_COD_UNIDADES_USUARIO, V_COD_AFERICOES);
    END IF;

    --- CHECKLIST.
    IF ((V_COD_CHECKLISTS IS NOT NULL) OR (V_COD_CHECKLISTS_MODELO IS NOT NULL))
    THEN
        PERFORM INTERNO.FUNC_DELETA_CHECKLISTS_DEPENDENCIAS(V_COD_UNIDADES_USUARIO,
                                                            V_COD_CHECKLISTS,
                                                            V_COD_CHECKLISTS_MODELO);
    END IF;

    --- DELETA TOKEN CKECKLIST OFFLINE
    -- (MESMO SEM TER CHECKLIST - PODE HAVER O TOKEN - POIS ELE É CRIADO ASSIM QUE UMA UNIDADE É CADASTRADA)
    IF (V_TOKENS_CHECKLISTS_OFF IS NOT NULL)
    THEN
        PERFORM INTERNO.FUNC_DELETA_TOKENS_CHECKLISTS_OFFLINES(V_COD_UNIDADES_USUARIO);
    END IF;

    -- MOVIMENTAÇÃO.
    IF (V_COD_MOVIMENTACOES IS NOT NULL)
    THEN
        PERFORM INTERNO.FUNC_DELETA_MOVIMENTACOES_DEPENDENCIAS(V_COD_UNIDADES_USUARIO, V_COD_MOVIMENTACOES);
    END IF;

    --- SOCORRO EM ROTA.
    IF (V_COD_SOCORROS IS NOT NULL)
    THEN
        PERFORM INTERNO.FUNC_DELETA_SOCORROS_DEPENDENCIAS(F_COD_EMPRESA_USUARIO, V_COD_SOCORROS);
    END IF;

    --- TRANSFERENCIA DE VEÍCULOS.
    IF (V_COD_VEICULOS_TRANSFERENCIAS_PROCESSOS IS NOT NULL)
    THEN
        PERFORM INTERNO.FUNC_DELETA_TRANSFERENCIAS_VEICULOS_DEPENDENCIAS(V_COD_VEICULOS_TRANSFERENCIAS_PROCESSOS);
    END IF;

    -- TRANSFERENCIA DE PNEU
    IF (V_COD_PNEU_TRANSFERENCIAS_PROCESSOS IS NOT NULL)
    THEN
        PERFORM INTERNO.FUNC_DELETA_TRANSFERENCIAS_PNEUS_DEPENDENCIAS(V_COD_PNEU_TRANSFERENCIAS_PROCESSOS,
                                                                      V_COD_UNIDADES_USUARIO);
    END IF;

    -- INTERVALO
    IF (V_COD_INTERVALO IS NOT NULL)
    THEN
        PERFORM INTERNO.FUNC_DELETA_INTERVALO_DEPENDENCIAS(V_COD_UNIDADES_USUARIO, V_COD_MARCACOES);
    END IF;

    -- NPS
    IF (V_COD_COLABORADORES_NPS IS NOT NULL)
    THEN
        PERFORM INTERNO.FUNC_DELETA_NPS(V_COD_COLABORADORES_NPS);
    END IF;

    -- PRODUTIVIDADE
    IF EXISTS(SELECT AP.COD_UNIDADE FROM ACESSOS_PRODUTIVIDADE AP WHERE AP.COD_UNIDADE = ANY (V_COD_UNIDADES_USUARIO))
    THEN
        PERFORM INTERNO.FUNC_DELETA_PRODUTIVIDADES_DEPENDENCIAS(V_COD_UNIDADES_USUARIO);
    END IF;

    -- RELATO
    IF (V_COD_RELATOS IS NOT NULL)
    THEN
        PERFORM INTERNO.FUNC_DELETA_RELATOS_DEPENDENCIAS(V_COD_UNIDADES_USUARIO);
    END IF;

    -- QUIZ
    IF (V_COD_QUIZ IS NOT NULL)
    THEN
        PERFORM INTERNO.FUNC_DELETA_QUIZ_DEPENDENCIAS(V_COD_UNIDADES_USUARIO, V_COD_QUIZ);
    END IF;

    -- TREINAMENTO
    IF (V_COD_TREINAMENTOS IS NOT NULL)
    THEN
        PERFORM INTERNO.FUNC_DELETA_TREINAMENTOS_DEPENDENCIAS(V_COD_TREINAMENTOS);
    END IF;

    -- SERVICO PNEU
    IF (V_COD_SERVICOS_REALIZADOS IS NOT NULL)
    THEN
        PERFORM INTERNO.FUNC_DELETA_SERVICOS_PNEU_DEPENDENCIAS(F_COD_EMPRESA_USUARIO, V_COD_SERVICOS_REALIZADOS);
    END IF;

    -- FALE CONOSCO
    IF (V_COD_FALE_CONOSCO IS NOT NULL)
    THEN
        PERFORM INTERNO.FUNC_DELETA_FALE_CONOSCO(V_COD_FALE_CONOSCO);
    END IF;

    -- TESTES AFERIDOR
    IF (V_COD_TESTES_AFERIDOR IS NOT NULL)
    THEN
        PERFORM INTERNO.FUNC_DELETA_TESTES_AFERIDOR(V_COD_TESTES_AFERIDOR);
    END IF;

    -- DELETA VEÍCULOS
    PERFORM INTERNO.FUNC_DELETA_VEICULOS(F_COD_EMPRESA_USUARIO, V_COD_UNIDADES_USUARIO);

    -- DELETA PNEUS
    PERFORM INTERNO.FUNC_DELETA_PNEUS(F_COD_EMPRESA_USUARIO, V_COD_UNIDADES_USUARIO);

    -- DELETA COLABORADORES
    PERFORM INTERNO.FUNC_DELETA_COLABORADORES(F_COD_EMPRESA_USUARIO, V_COD_UNIDADES_USUARIO);

    -- DELETA UNIDADES
    PERFORM INTERNO.FUNC_DELETA_UNIDADES(F_COD_EMPRESA_USUARIO, V_COD_UNIDADES_USUARIO);

    -- CLONAGENS
    --- CLONA UNIDADES
    PERFORM INTERNO.FUNC_CLONA_UNIDADES(F_COD_EMPRESA_BASE, F_COD_EMPRESA_USUARIO);

    --- CLONA NOMENCLATURAS
    PERFORM INTERNO.FUNC_CLONA_NOMENCLATURAS(F_COD_EMPRESA_BASE, F_COD_EMPRESA_USUARIO);

    FOREACH V_COD_UNIDADE_BASE IN ARRAY V_COD_UNIDADES_BASE
        LOOP
            V_COD_UNIDADE_USUARIO_NOVA := (SELECT UNOVA.CODIGO
                                           FROM UNIDADE UBASE
                                                    JOIN UNIDADE UNOVA ON UBASE.NOME = UNOVA.NOME
                                           WHERE UBASE.CODIGO = V_COD_UNIDADE_BASE
                                             AND UNOVA.COD_EMPRESA = F_COD_EMPRESA_USUARIO);

            --- CLONA VEÍCULOS
            IF EXISTS(SELECT VD.CODIGO FROM VEICULO_DATA VD WHERE VD.COD_UNIDADE = V_COD_UNIDADE_BASE)
            THEN
                PERFORM INTERNO.FUNC_CLONA_VEICULOS(F_COD_EMPRESA_BASE, V_COD_UNIDADE_BASE, F_COD_EMPRESA_USUARIO,
                                                    V_COD_UNIDADE_USUARIO_NOVA);

            END IF;

            --- CLONA PNEUS
            IF EXISTS(SELECT PD.CODIGO FROM PNEU_DATA PD WHERE PD.COD_UNIDADE = V_COD_UNIDADE_BASE)
            THEN
                PERFORM INTERNO.FUNC_CLONA_PNEUS(F_COD_EMPRESA_BASE, V_COD_UNIDADE_BASE, F_COD_EMPRESA_USUARIO,
                                                 V_COD_UNIDADE_USUARIO_NOVA);
            END IF;

            --- CLONA VINCULOS
            IF EXISTS(SELECT VP.PLACA FROM VEICULO_PNEU VP WHERE VP.COD_UNIDADE = V_COD_UNIDADE_BASE)
            THEN
                PERFORM INTERNO.FUNC_CLONA_VINCULO_VEICULOS_PNEUS(V_COD_UNIDADE_BASE, V_COD_UNIDADE_USUARIO_NOVA);
            END IF;

            --- CLONA COLABORADORES
            IF EXISTS(SELECT CD.CODIGO FROM COLABORADOR_DATA CD WHERE CD.COD_UNIDADE = V_COD_UNIDADE_BASE)
            THEN
                PERFORM INTERNO.FUNC_CLONA_COLABORADORES(F_COD_EMPRESA_BASE, V_COD_UNIDADE_BASE,
                                                         F_COD_EMPRESA_USUARIO,
                                                         V_COD_UNIDADE_USUARIO_NOVA);
            END IF;
        END LOOP;

    V_COLABORADORES_CADASTRADOS = (SELECT ARRAY_AGG(CONCAT('CPF: ', C.CPF,
                                                           ' | DATA NASCIMENTO: ', C.DATA_NASCIMENTO,
                                                           ' | NÍVEL DE PERMISSAO: ', C.COD_PERMISSAO,
                                                           ' | CARGO: ', F.NOME))
                                   FROM COLABORADOR C
                                            JOIN FUNCAO F ON F.COD_EMPRESA = C.COD_EMPRESA AND F.CODIGO = C.COD_FUNCAO
                                   WHERE C.COD_EMPRESA = F_COD_EMPRESA_USUARIO);

    SELECT 'A EMPRESA FOI RESETADA E OS DADOS FORAM CLONADOS COM SUCESSO. OS COLABORADORES CADASTRADOS SÃO: ' ||
           CONCAT(V_COLABORADORES_CADASTRADOS)
    INTO MENSAGEM_SUCESSO;
END ;
$$;