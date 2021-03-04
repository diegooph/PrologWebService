-- Sobre:
-- A lógica aplicada nessa function é a seguinte:
-- Caso a dimensão que desejamos inserir exista no banco, ela não será cadastrada.
-- Caso a dimensão seja realemtne nova, ela será adicionada na base de dados.
--
-- Précondições:
-- Dimensão não deve existir no banco.
-- Valores maiores que 0.
--
-- Histórico:
-- 2019-10-15 -> Function criada (natanrotta - PL-2312).
-- 2020-08-14 -> Adiciona chamada para logar execução da function (gustavocnp95 - PL-3066).
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