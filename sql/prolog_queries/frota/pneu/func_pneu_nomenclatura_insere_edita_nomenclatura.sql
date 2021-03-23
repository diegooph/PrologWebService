CREATE OR REPLACE FUNCTION
    FUNC_PNEU_NOMENCLATURA_INSERE_EDITA_NOMENCLATURA(F_COD_EMPRESA BIGINT,
                                                     F_COD_DIAGRAMA BIGINT,
                                                     F_POSICAO_PROLOG BIGINT,
                                                     F_NOMENCLATURA TEXT,
                                                     F_COD_AUXILIAR TEXT,
                                                     F_TOKEN_RESPONSAVEL_INSERCAO TEXT,
                                                     F_DATA_HORA_CADASTRO TIMESTAMP WITH TIME ZONE)
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    V_COD_COLABORADOR_INSERCAO BIGINT := (SELECT CODIGO
                                          FROM COLABORADOR
                                          WHERE CPF = (SELECT CPF_COLABORADOR
                                                       FROM TOKEN_AUTENTICACAO
                                                       WHERE TOKEN = F_TOKEN_RESPONSAVEL_INSERCAO));
BEGIN
    INSERT INTO PNEU_POSICAO_NOMENCLATURA_EMPRESA (COD_DIAGRAMA,
                                                   COD_EMPRESA,
                                                   POSICAO_PROLOG,
                                                   NOMENCLATURA,
                                                   COD_AUXILIAR,
                                                   COD_COLABORADOR_CADASTRO,
                                                   DATA_HORA_CADASTRO)
    VALUES (F_COD_DIAGRAMA,
            F_COD_EMPRESA,
            F_POSICAO_PROLOG,
            F_NOMENCLATURA,
            F_COD_AUXILIAR,
            V_COD_COLABORADOR_INSERCAO,
            F_DATA_HORA_CADASTRO)
    ON CONFLICT ON CONSTRAINT UNIQUE_DIAGRAMA_EMPRESA_POSICAO_PROLOG
        DO UPDATE SET NOMENCLATURA             = F_NOMENCLATURA,
                      COD_AUXILIAR             = F_COD_AUXILIAR,
                      COD_COLABORADOR_CADASTRO = V_COD_COLABORADOR_INSERCAO,
                      DATA_HORA_CADASTRO       = F_DATA_HORA_CADASTRO;
END;
$$;