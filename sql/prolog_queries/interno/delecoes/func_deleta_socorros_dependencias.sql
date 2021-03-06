CREATE OR REPLACE FUNCTION INTERNO.FUNC_DELETA_SOCORROS_DEPENDENCIAS(F_COD_EMPRESA BIGINT, F_COD_SOCORROS BIGINT[])
    RETURNS VOID
    LANGUAGE PLPGSQL
AS
$$
DECLARE
    COD_PROBLEMAS_SOCORROS_ROTAS BIGINT[] := (SELECT array_agg(SROP.CODIGO)
                                              FROM SOCORRO_ROTA_OPCAO_PROBLEMA SROP
                                              WHERE SROP.COD_EMPRESA = (F_COD_EMPRESA));
BEGIN
    -- Dropa constraints que garantem a relação entre status e registros de ações de socorro.
    ALTER TABLE SOCORRO_ROTA DROP CONSTRAINT CHECK_SOCORRO_ROTA_STATUS_ABERTURA;
    ALTER TABLE SOCORRO_ROTA DROP CONSTRAINT CHECK_SOCORRO_ROTA_STATUS_ATENDIMENTO;
    ALTER TABLE SOCORRO_ROTA DROP CONSTRAINT CHECK_SOCORRO_ROTA_STATUS_INVALIDACAO;
    ALTER TABLE SOCORRO_ROTA DROP CONSTRAINT CHECK_SOCORRO_ROTA_STATUS_FINALIZACAO;

    -- Tornando as constraints de FK deferíveis.
    SET CONSTRAINTS SOCORRO_ROTA_SOCORRO_ROTA_ABERTURA_FK DEFERRED;
    SET CONSTRAINTS SOCORRO_ROTA_SOCORRO_ROTA_ATENDIMENTO_FK DEFERRED;
    SET CONSTRAINTS SOCORRO_ROTA_SOCORRO_ROTA_INVALIDACAO_FK DEFERRED;
    SET CONSTRAINTS SOCORRO_ROTA_SOCORRO_ROTA_FINALIZACAO_FK DEFERRED;

    -- Deleta os registros.
    DELETE
    FROM SOCORRO_ROTA_FINALIZACAO SRF
    WHERE SRF.COD_SOCORRO_ROTA = ANY (F_COD_SOCORROS);

    DELETE
    FROM SOCORRO_ROTA_INVALIDACAO SRI
    WHERE SRI.COD_SOCORRO_ROTA = ANY (F_COD_SOCORROS);

    DELETE
    FROM SOCORRO_ROTA_ATENDIMENTO SRAT
    WHERE SRAT.COD_SOCORRO_ROTA = ANY (F_COD_SOCORROS);

    DELETE
    FROM SOCORRO_ROTA_ABERTURA SRA
    WHERE SRA.COD_SOCORRO_ROTA = ANY (F_COD_SOCORROS);

    DELETE
    FROM SOCORRO_ROTA SR
    WHERE SR.CODIGO = ANY (F_COD_SOCORROS);

    DELETE
    FROM SOCORRO_ROTA_OPCAO_PROBLEMA_HISTORICO SROPH
    WHERE SROPH.COD_PROBLEMA_SOCORRO_ROTA = ANY (COD_PROBLEMAS_SOCORROS_ROTAS);

    DELETE
    FROM SOCORRO_ROTA_OPCAO_PROBLEMA SROP
    WHERE SROP.CODIGO = ANY (COD_PROBLEMAS_SOCORROS_ROTAS);

    -- Recria constraints de status e registros.

    -- Garante que no status aberto só existam dados nas filhas para este status
    ALTER TABLE SOCORRO_ROTA
        ADD CONSTRAINT CHECK_SOCORRO_ROTA_STATUS_ABERTURA
            CHECK (STATUS_ATUAL <> 'ABERTO' OR
                   (STATUS_ATUAL = 'ABERTO' AND COD_ABERTURA IS NOT NULL AND COD_ATENDIMENTO IS NULL AND
                    COD_INVALIDACAO IS NULL AND COD_FINALIZACAO IS NULL));

    -- Garante que no status em atendimento só existam dados nas filhas para este status
    ALTER TABLE SOCORRO_ROTA
        ADD CONSTRAINT CHECK_SOCORRO_ROTA_STATUS_ATENDIMENTO
            CHECK (STATUS_ATUAL <> 'EM_ATENDIMENTO' OR
                   (STATUS_ATUAL = 'EM_ATENDIMENTO' AND COD_ABERTURA IS NOT NULL AND COD_ATENDIMENTO IS NOT NULL AND
                    COD_INVALIDACAO IS NULL AND COD_FINALIZACAO IS NULL));

    -- Garante que no status de invalidação só existam dados nas filhas para este status
    ALTER TABLE SOCORRO_ROTA
        ADD CONSTRAINT CHECK_SOCORRO_ROTA_STATUS_INVALIDACAO
            CHECK (STATUS_ATUAL <> 'INVALIDO' OR
                   (STATUS_ATUAL = 'INVALIDO' AND COD_ABERTURA IS NOT NULL AND COD_INVALIDACAO IS NOT NULL AND
                    COD_FINALIZACAO IS NULL));

    -- Garante que no status finalizado só existam dados nas filhas para este status
    ALTER TABLE SOCORRO_ROTA
        ADD CONSTRAINT CHECK_SOCORRO_ROTA_STATUS_FINALIZACAO
            CHECK (STATUS_ATUAL <> 'FINALIZADO' OR
                   (STATUS_ATUAL = 'FINALIZADO' AND COD_ABERTURA IS NOT NULL AND COD_ATENDIMENTO IS NOT NULL AND
                    COD_INVALIDACAO IS NULL AND COD_FINALIZACAO IS NOT NULL));
END;
$$;