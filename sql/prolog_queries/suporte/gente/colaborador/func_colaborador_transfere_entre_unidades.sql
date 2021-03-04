-- Sobre:
-- Lógica aplicada:
-- Consiste basicamente em transferir um colaborador entre unidades, desde que as unidades pertençam a mesma empresa.
--
-- Précondições:
-- Empresa possuir unidade origem.
-- Empresa possuir unidade destino.
-- Colaborador existir na unidade origem.
-- Setor existir na unidade destino.
-- Equipe existir na unidade destino.
--
-- Histórico:
-- 2019-11-07 -> function criada (natanrotta - PL-2377).
-- 2020-08-14 -> Adiciona chamada para logar execução da function (gustavocnp95 - PL-3066).
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