INSERT INTO public.dashboard_componente (codigo, titulo, subtitulo, descricao, qtd_blocos_horizontais, qtd_blocos_verticais, data_hora_criacao, data_hora_ultima_alteracao, cod_pilar_prolog_componente, cod_tipo_componente, url_endpoint_dados, cor_background_hex, url_icone, label_eixo_x, label_eixo_y, ativo) VALUES (21, 'Quantidade de socorros por status', 'Relação dos últimos 30 dias', 'Mostra a quantidade de socorros por cada status nos últimos 30 dias, considerando a data de abertura do socorro.', 2, 1, '2020-03-31 18:04:12.469993', '2020-03-31 18:04:12.469993', 1, 1, '/dashboards/socorro-rota/quantidade-socorros-por-status', '#E74C3C', null, null, null, true);

INSERT INTO public.dashboard_componente_funcao_prolog (cod_componente, cod_funcao_prolog, cod_pilar_prolog) VALUES (21, 147, 1);

DROP FUNCTION IF EXISTS PUBLIC.FUNC_SOCORRO_ROTA_RELATORIO_SOCORROS_POR_STATUS;

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
          AND CAST(SRA.data_hora_abertura AT TIME ZONE U.timezone AS DATE) BETWEEN (F_DATA_HORA_BUSCA_RELATORIO - INTERVAL '30' day) AND F_DATA_HORA_BUSCA_RELATORIO
        GROUP BY SR.STATUS_ATUAL;
END;
$$;