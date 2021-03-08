-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Se o colaborador está inativo, será transferido para a unidade de outra empresa e terá seu status modificado para ativo.
-- Caso contrário, é necessário entrar em contato com a empresa de origem e perguntar se o funcionário n faz mais parte do quadro.
--
-- Precondições:
-- 1) Para a function funcionar é verificado a integridade entre unidade-setor.
-- 2) Verificado se unidades são de empresas distintas
-- 3) Verificado se o colaborador está inativo.
--
-- Histórico:
-- 2019-07-24 -> Function criada (thaisksf - PL-2164).
-- 2019-09-04 -> Corrige update (luizfp).
-- 2019-09-18 -> Adiciona no schema suporte (natanrotta - PL-2242).
-- 2019-12-04 -> Remove tratamento de colaborador inativo (natanrotta - PL-2407).
-- 2020-08-14 -> Adiciona chamada para logar execução da function (gustavocnp95 - PL-3066).
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