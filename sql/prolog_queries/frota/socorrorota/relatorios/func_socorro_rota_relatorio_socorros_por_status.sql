-- Sobre:
-- Esta function retorna uma lista dos socorros em rota por status, usando como base de filtro os códigos de unidade
-- passados por parâmetro e a data atual, também passado por parâmetro pelo webservice.
--
-- Histórico:
-- 2020-04-03 -> Function criada (gustavocnp95 - PL-2618).
CREATE OR REPLACE FUNCTION PUBLIC.FUNC_SOCORRO_ROTA_RELATORIO_SOCORROS_POR_STATUS(F_COD_UNIDADES BIGINT[],
                                                                                  F_DATA_HORA_BUSCA_RELATORIO DATE)
    RETURNS TABLE
            (
                QUANTIDADE_SOCORROS BIGINT,
                STATUS              socorro_rota_status_type
            )
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    RETURN QUERY
        SELECT COUNT(*), SR.status_atual
        FROM socorro_rota SR
                 INNER JOIN SOCORRO_ROTA_ABERTURA SRA ON SR.codigo = SRA.cod_socorro_rota
                 INNER JOIN UNIDADE U ON U.codigo = SR.cod_unidade
        WHERE SR.cod_unidade = ANY (F_COD_UNIDADES)
          -- Após aplicar o timezone da unidade do socorro aberto, casteamos para date, sem time, para
          -- buscar os registros entre a data atual, que é fornecida pelo webservice e 30 dias atrás.
          AND CAST(SRA.data_hora_abertura AT TIME ZONE U.timezone AS DATE) BETWEEN (F_DATA_HORA_BUSCA_RELATORIO - INTERVAL '30' day) AND F_DATA_HORA_BUSCA_RELATORIO
        GROUP BY SR.STATUS_ATUAL;
END;
$$;