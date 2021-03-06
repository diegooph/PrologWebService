CREATE OR REPLACE FUNCTION FUNC_PNEU_ALTERA_DOT_PNEU(F_COD_EMPRESA BIGINT,
                                                     F_COD_UNIDADE BIGINT,
                                                     F_QUANTIDADE_DOT BIGINT,
                                                     OUT AVISO_DOT_ATUALIZADO TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    F_QUANTIDADE_DOT_ATUALIZADOS BIGINT;
    F_DOT_ATUAL                  VARCHAR(20) := '1199';
    F_VALIDA_CONDICOES           BOOLEAN     := FALSE;
BEGIN

    --EXECUTA VALIDAÇÕES.
    IF (F_VALIDA_CONDICOES IS FALSE)
    THEN
        --VERIFICA SE EMPRESA EXISTE.
        PERFORM FUNC_GARANTE_EMPRESA_EXISTE(F_COD_EMPRESA);

        --VERIFICA SE UNIDADE EXISTE.
        PERFORM FUNC_GARANTE_UNIDADE_EXISTE(F_COD_UNIDADE);

        --VERIFICA SE EMPRESA E UNIDADE POSSUEM VÍNCULO.
        PERFORM FUNC_GARANTE_EMPRESA_POSSUI_UNIDADE(F_COD_EMPRESA, F_COD_UNIDADE);

        --VERIFICA SE A QUANTIDADE DE DOT NÃO É NULL.
        IF (F_QUANTIDADE_DOT <= 0)
        THEN
            RAISE EXCEPTION 'ERRO! A QUANTIDADE DE DOT A SER ATUALIZADO DEVE SER MAIOR/DIFERENTE DE 0(ZERO).';
        END IF;

        --TUDO CORRETO
        F_VALIDA_CONDICOES = TRUE;
    END IF;

    --SE TUDO ESTÁ CORRETO, REALIZA O UPDATE.
    IF (F_VALIDA_CONDICOES)
    THEN
        UPDATE PNEU_DATA
        SET DOT = NULL
        WHERE COD_EMPRESA = F_COD_EMPRESA
          AND COD_UNIDADE = F_COD_UNIDADE
          AND DOT = F_DOT_ATUAL;

        GET DIAGNOSTICS F_QUANTIDADE_DOT_ATUALIZADOS = ROW_COUNT;

        --VERIFICA SE A QUANTIDADE INFORMADA FOR A MESMA DE LINHAS AFETADAS.
        IF (F_QUANTIDADE_DOT_ATUALIZADOS != F_QUANTIDADE_DOT)
        THEN
            RAISE EXCEPTION 'ERRO AO ATUALIZAR! A QUANTIDADE DE DOT: % NÃO É A MESMA DE LINHAS AFETADAS: %
                NA ATUALIZAÇÃO!', F_QUANTIDADE_DOT, F_QUANTIDADE_DOT_ATUALIZADOS;
        END IF;
    END IF;

    --MENSAGEM DE SUCESSO.
    SELECT 'ATUALIZADO COM SUCESSO! NÚMERO TOTAL DE: ' || F_QUANTIDADE_DOT || ' DOT ATUALIZADOS.'
    INTO AVISO_DOT_ATUALIZADO;

END
$$;