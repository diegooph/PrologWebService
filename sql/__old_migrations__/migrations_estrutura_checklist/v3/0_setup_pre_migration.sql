--######################################################################################################################
--######################################################################################################################
-- Rodar antes de tudo em prod.
BEGIN TRANSACTION ;

CREATE SCHEMA MIGRATION_CHECKLIST;

CREATE TABLE MIGRATION_CHECKLIST.BASE_CHECKLIST_ALTERNATIVAS_AGG (
    COD_CHECKLIST BIGINT PRIMARY KEY,
    COD_CHECKLIST_MODELO BIGINT,
    ALTERNATIVAS TEXT
);

END TRANSACTION ;
--######################################################################################################################
--######################################################################################################################

--######################################################################################################################
--######################################################################################################################
-- Após criar a tabela acima, rodar o insert abaixo de 100k em 100k para os checks já existentes, rodando até
-- o BATCH_END_EXCLUSIVE chegar em 1.600.000. Assim, os demais ~75k de checklists existentes até hoje (2019-01-19)
-- serão processados no migration final (1_setup_pre_migration.sql).
BEGIN TRANSACTION ;
DO
$body$
    DECLARE
        BATCH_INIT_INCLUSIVE BIGINT := 0;
        BATCH_END_EXCLUSIVE BIGINT := 100000;
    BEGIN

        WITH CHECKLISTS AS (
            SELECT DISTINCT(CR.COD_CHECKLIST)                                        AS COD_CHECKLIST,
                           CR.COD_CHECKLIST_MODELO                                   AS COD_CHECKLIST_MODELO,
                           ARRAY_AGG(CR.COD_ALTERNATIVA ORDER BY CR.COD_ALTERNATIVA) AS ALTERNATIVAS
            FROM CHECKLIST_RESPOSTAS CR
            WHERE CR.COD_CHECKLIST >= BATCH_INIT_INCLUSIVE
              AND CR.COD_CHECKLIST < BATCH_END_EXCLUSIVE
            GROUP BY CR.COD_CHECKLIST, COD_CHECKLIST_MODELO
            ORDER BY CR.COD_CHECKLIST)
        INSERT
        INTO MIGRATION_CHECKLIST.BASE_CHECKLIST_ALTERNATIVAS_AGG (COD_CHECKLIST, ALTERNATIVAS, COD_CHECKLIST_MODELO)
        SELECT C.COD_CHECKLIST, C.ALTERNATIVAS, C.COD_CHECKLIST_MODELO
        FROM CHECKLISTS C
        ORDER BY COD_CHECKLIST_MODELO;
    END;
$body$
LANGUAGE 'plpgsql';
END TRANSACTION ;
--######################################################################################################################
--######################################################################################################################