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
BEGIN
    PERFORM SUPORTE.FUNC_HISTORICO_SALVA_EXECUCAO();
    RAISE EXCEPTION 'A deleção de checklists deve ser realizada utilizando o Site - Prolog App';
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
BEGIN
    PERFORM SUPORTE.FUNC_HISTORICO_SALVA_EXECUCAO();
    RAISE EXCEPTION 'A deleção de checklists deve ser realizada utilizando o Site - Prolog App';
END;
$$;