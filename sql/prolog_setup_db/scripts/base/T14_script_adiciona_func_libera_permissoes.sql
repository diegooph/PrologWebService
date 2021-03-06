CREATE OR REPLACE FUNCTION FUNC_LIBERA_TODAS_PERMISSOES(F_CPF BIGINT, OUT F_AVISO_PERMISSOES_INSERIDAS TEXT)
    RETURNS TEXT
    LANGUAGE PLPGSQL
    SECURITY DEFINER
AS
$$
DECLARE
    F_COD_UNIDADE       BIGINT;
    F_COD_FUNCAO        BIGINT;
    F_COD_FUNCAO_PROLOG BIGINT;
BEGIN
    -- VERIFICA SE COLABORADOR EXISTE
    PERFORM FUNC_GARANTE_COLABORADOR_EXISTE(F_CPF);

    SELECT CO.COD_UNIDADE, CO.COD_FUNCAO FROM COLABORADOR CO WHERE CO.CPF = F_CPF INTO F_COD_UNIDADE, F_COD_FUNCAO;

    DELETE FROM CARGO_FUNCAO_PROLOG_V11 WHERE COD_FUNCAO_COLABORADOR = F_COD_FUNCAO AND COD_UNIDADE = F_COD_UNIDADE;

    FOREACH F_COD_FUNCAO_PROLOG IN ARRAY (SELECT ARRAY_AGG(FPV11.CODIGO) FROM FUNCAO_PROLOG_V11 FPV11)
        LOOP
            INSERT INTO CARGO_FUNCAO_PROLOG_V11 (COD_UNIDADE, COD_FUNCAO_COLABORADOR, COD_FUNCAO_PROLOG,
                                                 COD_PILAR_PROLOG)
            SELECT F_COD_UNIDADE,
                   F_COD_FUNCAO,
                   F_COD_FUNCAO_PROLOG,
                   (SELECT FPV11.COD_PILAR FROM FUNCAO_PROLOG_V11 FPV11 WHERE FPV11.CODIGO = F_COD_FUNCAO_PROLOG);
        END LOOP;

    --MENSAGEM DE SUCESSO.
    SELECT 'Todas as permissões do Prolog foram atribuídas ao cargo de código ' || F_COD_FUNCAO ||
           ', unidade: ' || F_COD_UNIDADE || '.'
    INTO F_AVISO_PERMISSOES_INSERIDAS;
END;
$$;
