BEGIN TRANSACTION;

DROP VIEW VIEW_PRODUTIVIDADE_EXTRATO;
DROP VIEW VIEW_EXTRATO_INDICADORES;
DROP VIEW VIEW_INDICADORES_ACUMULADOS;
DROP FUNCTION FUNC_GET_PRODUTIVIDADE_COLABORADOR(F_MES INTEGER, F_ANO INTEGER, F_CPF BIGINT);
DROP FUNCTION FUNC_GET_PRODUTIVIDADE_CONSOLIDADO_COLABORADORES(F_DATA_INICIAL DATE, F_DATA_FINAL DATE, F_COD_UNIDADE BIGINT, F_EQUIPE TEXT, F_FUNCAO TEXT);
DROP FUNCTION FUNC_RELATORIO_CONSOLIDADO_MAPAS_INDICADORES(F_DATA_INICIAL DATE, F_DATA_FINAL DATE, F_CPF TEXT, F_COD_UNIDADE TEXT, F_COD_EQUIPE TEXT, F_COD_EMPRESA BIGINT, F_COD_REGIONAL TEXT);
DROP FUNCTION FUNC_RELATORIO_EXTRATO_MAPAS_INDICADORES(F_DATA_INICIAL DATE, F_DATA_FINAL DATE, F_CPF TEXT, F_COD_UNIDADE TEXT, F_COD_EQUIPE TEXT, F_COD_EMPRESA BIGINT, F_COD_REGIONAL TEXT);
DROP FUNCTION FUNC_RELATORIO_ACESSOS_PRODUTIVIDADE_ESTRATIFICADO(F_COD_UNIDADE BIGINT, F_DATA_INICIAL DATE, F_DATA_FINAL DATE, F_CPF TEXT);
DROP FUNCTION FUNC_RELATORIO_CONSOLIDADO_PRODUTIVIDADE(F_DT_INICIAL DATE, F_DT_FINAL DATE, F_COD_UNIDADE BIGINT);

-- TABELA MAPA
-- ALTERA COLUNAS TIMESTAMP
ALTER TABLE MAPA ALTER COLUMN HRSAI TYPE TIMESTAMP WITH TIME ZONE USING HRSAI AT TIME ZONE tz_unidade(cod_unidade);
ALTER TABLE MAPA ALTER COLUMN HRENTR TYPE TIMESTAMP WITH TIME ZONE USING HRENTR AT TIME ZONE tz_unidade(cod_unidade);
ALTER TABLE MAPA ALTER COLUMN HRCARREG TYPE TIMESTAMP WITH TIME ZONE USING HRCARREG AT TIME ZONE tz_unidade(cod_unidade);
ALTER TABLE MAPA ALTER COLUMN HRPCFISICA TYPE TIMESTAMP WITH TIME ZONE USING HRPCFISICA AT TIME ZONE tz_unidade(cod_unidade);
ALTER TABLE MAPA ALTER COLUMN HRPCFINANCEIRA TYPE TIMESTAMP WITH TIME ZONE USING HRPCFINANCEIRA AT TIME ZONE tz_unidade(cod_unidade);
ALTER TABLE MAPA ALTER COLUMN DATA_HORA_IMPORT TYPE TIMESTAMP WITH TIME ZONE USING DATA_HORA_IMPORT AT TIME ZONE tz_unidade(cod_unidade);
-- As colunas time não iremos atualizar, continuarão sendo TIME WITHOUT TIME ZONE
-- HRMATINAL
-- HRJORNADALIQ
-- HRMETAJORNADA

-- TABELA TRACKING
-- ALTERA COLUNAS TIMESTAMP
ALTER TABLE TRACKING ALTER COLUMN DATA_HORA_IMPORT TYPE TIMESTAMP WITH TIME ZONE USING DATA_HORA_IMPORT AT TIME ZONE tz_unidade(cod_unidade);
-- As colunas time não iremos atualizar, continuarão sendo TIME WITHOUT TIME ZONE
-- INICIO_ROTA
-- HORÁRIO_MATINAL
-- SAÍDA_CDD
-- CHEGADA_AO_PDV
-- INICIO_ENTREGA
-- FIM_ENTREGA
-- FIM_ROTA
-- ENTRADA_CDD

-- Cria function para converter um time em segundos
CREATE OR REPLACE FUNCTION to_seconds(t time)
  RETURNS BIGINT
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN to_seconds(t::TEXT);
END;
$$;

CREATE OR REPLACE FUNCTION to_text_hhmmss(t interval)
  RETURNS TEXT
LANGUAGE plpgsql
AS $$
DECLARE
    value TEXT;
BEGIN
    SELECT to_char(T, 'HH24:MI:SS'::TEXT) INTO value;
    RETURN value;
END;
$$;

-- RECRIA AS VIEWS.
-- Essa view já foi alterada no que se refere a timezone
CREATE VIEW view_produtividade_extrato AS
  SELECT vmc.cod_unidade,
    c.matricula_ambev,
    m.data,
    vmc.cpf,
    c.nome AS nome_colaborador,
    c.data_nascimento,
    f.nome AS funcao,
    f.codigo AS cod_funcao,
    e.nome AS nome_equipe,
    m.fator,
    m.cargaatual,
    m.entrega,
    m.mapa,
    m.placa,
    m.cxcarreg,
    m.cxentreg,
    m.qthlcarregados,
    m.qthlentregues,
    m.qtnfcarregadas,
    m.qtnfentregues,
    m.entregascompletas,
    m.entregasnaorealizadas,
    m.entregasparciais,
    m.kmprevistoroad,
    m.kmsai,
    m.kmentr,
    to_seconds(m.tempoprevistoroad::text) AS tempoprevistoroad,
    m.hrsai,
    m.hrentr,
    to_seconds(m.hrentr - m.hrsai) AS tempo_rota,
    to_seconds(m.tempointerno) AS tempointerno,
    m.hrmatinal,
    tracking.apontamentos_ok,
    tracking.total_apontamentos AS total_tracking,
    to_seconds(
        CASE
            WHEN (((m.hrsai at time zone tz_unidade(m.cod_unidade))::TIME < m.hrmatinal) OR (m.hrmatinal = '00:00:00'::TIME)) THEN um.meta_tempo_largada_horas::text
            ELSE (m.hrsai - m.hrmatinal::INTERVAL)
        END) AS tempo_largada,
    um.meta_tracking,
    um.meta_tempo_rota_mapas,
    um.meta_caixa_viagem,
    um.meta_dev_hl,
    um.meta_dev_nf,
    um.meta_dev_pdv,
    um.meta_dispersao_km,
    um.meta_dispersao_tempo,
    um.meta_jornada_liquida_mapas,
    um.meta_raio_tracking,
    um.meta_tempo_interno_mapas,
    um.meta_tempo_largada_mapas,
    to_seconds((um.meta_tempo_rota_horas)::text) AS meta_tempo_rota_segundos,
    to_seconds((um.meta_tempo_interno_horas)::text) AS meta_tempo_interno_segundos,
    to_seconds((um.meta_tempo_largada_horas)::text) AS meta_tempo_largada_segundos,
    to_seconds((um.meta_jornada_liquida_horas)::text) AS meta_jornada_liquida_segundos,
        CASE
            WHEN (((c.matricula_ambev = m.matricmotorista) AND (c.cod_funcao = ufp.cod_funcao_motorista)) AND (((m.entrega)::text <> 'AS'::text) AND ((m.cargaatual)::text <> 'Recarga'::text))) THEN (m.vlbateujornmot + m.vlnaobateujornmot)
            WHEN (((c.matricula_ambev = m.matricajud1) AND (c.cod_funcao = ufp.cod_funcao_ajudante)) AND (((m.entrega)::text <> 'AS'::text) AND ((m.cargaatual)::text <> 'Recarga'::text))) THEN ((m.vlbateujornaju + m.vlnaobateujornaju) / m.fator)
            WHEN (((c.matricula_ambev = m.matricajud2) AND (c.cod_funcao = ufp.cod_funcao_ajudante)) AND (((m.entrega)::text <> 'AS'::text) AND ((m.cargaatual)::text <> 'Recarga'::text))) THEN ((m.vlbateujornaju + m.vlnaobateujornaju) / m.fator)
            ELSE (0)::real
        END AS valor_rota,
    (
        CASE
            WHEN (((m.entrega)::text <> 'AS'::text) AND ((m.cargaatual)::text = 'Recarga'::text)) THEN
            CASE
                WHEN ((c.matricula_ambev = m.matricmotorista) AND (c.cod_funcao = ufp.cod_funcao_motorista)) THEN m.vlrecargamot
                WHEN ((c.matricula_ambev = m.matricajud1) AND (c.cod_funcao = ufp.cod_funcao_ajudante)) THEN (m.vlrecargaaju / m.fator)
                WHEN ((c.matricula_ambev = m.matricajud2) AND (c.cod_funcao = ufp.cod_funcao_ajudante)) THEN (m.vlrecargaaju / m.fator)
                ELSE (0)::real
            END
            ELSE (0)::real
        END +
        CASE
            WHEN (((m.entrega)::text = 'AS'::text) AND ((m.cargaatual)::text = 'Recarga'::text)) THEN
            CASE
                WHEN ((c.matricula_ambev = m.matricmotorista) AND (c.cod_funcao = ufp.cod_funcao_motorista)) THEN uv.rm_motorista_valor_as_recarga
                WHEN ((c.matricula_ambev = m.matricajud1) AND (c.cod_funcao = ufp.cod_funcao_ajudante)) THEN uv.rm_ajudante_valor_as_recarga
                WHEN ((c.matricula_ambev = m.matricajud2) AND (c.cod_funcao = ufp.cod_funcao_ajudante)) THEN uv.rm_ajudante_valor_as_recarga
                ELSE (0)::real
            END
            ELSE (0)::real
        END) AS valor_recarga,
        CASE
            WHEN (((((c.matricula_ambev = m.matricmotorista) AND (c.cod_funcao = ufp.cod_funcao_motorista)) AND ((m.entrega)::text <> 'AS'::text)) AND (m.tempoprevistoroad > um.meta_tempo_rota_horas)) AND ((m.cargaatual)::text <> 'Recarga'::text)) THEN (((m.cxentreg * (view_valor_cx_unidade.valor_cx_motorista_rota)::double precision) / (m.fator)::double precision) - ((m.vlbateujornmot + m.vlnaobateujornmot) + m.vlrecargamot))
            WHEN (((((c.matricula_ambev = m.matricajud1) AND (c.cod_funcao = ufp.cod_funcao_ajudante)) AND ((m.entrega)::text <> 'AS'::text)) AND (m.tempoprevistoroad > um.meta_tempo_rota_horas)) AND ((m.cargaatual)::text <> 'Recarga'::text)) THEN (((m.cxentreg * (view_valor_cx_unidade.valor_cx_ajudante_rota)::double precision) / (m.fator)::double precision) - (((m.vlbateujornaju + m.vlnaobateujornaju) + m.vlrecargaaju) / m.fator))
            WHEN (((((c.matricula_ambev = m.matricajud2) AND (c.cod_funcao = ufp.cod_funcao_ajudante)) AND ((m.entrega)::text <> 'AS'::text)) AND (m.tempoprevistoroad > um.meta_tempo_rota_horas)) AND ((m.cargaatual)::text <> 'Recarga'::text)) THEN (((m.cxentreg * (view_valor_cx_unidade.valor_cx_ajudante_rota)::double precision) / (m.fator)::double precision) - (((m.vlbateujornaju + m.vlnaobateujornaju) + m.vlrecargaaju) / m.fator))
            ELSE (0)::double precision
        END AS valor_diferenca_eld,
        CASE
            WHEN ((((c.matricula_ambev = m.matricmotorista) AND (c.cod_funcao = ufp.cod_funcao_motorista)) AND ((m.entrega)::text = 'AS'::text)) AND ((m.cargaatual)::text <> 'Recarga'::text)) THEN
            CASE
                WHEN (m.entregas = 1) THEN uv.rm_motorista_valor_as_1_entrega
                WHEN (m.entregas = 2) THEN uv.rm_motorista_valor_as_2_entregas
                WHEN (m.entregas = 3) THEN uv.rm_motorista_valor_as_3_entregas
                WHEN (m.entregas > 3) THEN uv.rm_motorista_valor_as_maior_3_entregas
                ELSE (0)::real
            END
            WHEN ((((c.matricula_ambev = m.matricajud1) AND (c.cod_funcao = ufp.cod_funcao_ajudante)) AND ((m.entrega)::text = 'AS'::text)) AND ((m.cargaatual)::text <> 'Recarga'::text)) THEN
            CASE
                WHEN (m.entregas = 1) THEN uv.rm_ajudante_valor_as_1_entrega
                WHEN (m.entregas = 2) THEN uv.rm_ajudante_valor_as_2_entregas
                WHEN (m.entregas = 3) THEN uv.rm_ajudante_valor_as_3_entregas
                WHEN (m.entregas > 3) THEN uv.rm_ajudante_valor_as_maior_3_entregas
                ELSE (0)::real
            END
            WHEN ((((c.matricula_ambev = m.matricajud2) AND (c.cod_funcao = ufp.cod_funcao_ajudante)) AND ((m.entrega)::text = 'AS'::text)) AND ((m.cargaatual)::text <> 'Recarga'::text)) THEN
            CASE
                WHEN (m.entregas = 1) THEN uv.rm_ajudante_valor_as_1_entrega
                WHEN (m.entregas = 2) THEN uv.rm_ajudante_valor_as_2_entregas
                WHEN (m.entregas = 3) THEN uv.rm_ajudante_valor_as_3_entregas
                WHEN (m.entregas > 2) THEN uv.rm_ajudante_valor_as_maior_3_entregas
                ELSE (0)::real
            END
            ELSE (0)::real
        END AS valor_as,
    ((
        CASE
            WHEN ((((c.matricula_ambev = m.matricmotorista) AND (c.cod_funcao = ufp.cod_funcao_motorista)) AND ((m.entrega)::text <> 'AS'::text)) AND ((m.tempoprevistoroad <= um.meta_tempo_rota_horas) OR ((m.cargaatual)::text = 'Recarga'::text))) THEN ((m.vlbateujornmot + m.vlnaobateujornmot) + m.vlrecargamot)
            WHEN ((((c.matricula_ambev = m.matricajud1) AND (c.cod_funcao = ufp.cod_funcao_ajudante)) AND ((m.entrega)::text <> 'AS'::text)) AND ((m.tempoprevistoroad <= um.meta_tempo_rota_horas) OR ((m.cargaatual)::text = 'Recarga'::text))) THEN (((m.vlbateujornaju + m.vlnaobateujornaju) + m.vlrecargaaju) / m.fator)
            WHEN ((((c.matricula_ambev = m.matricajud2) AND (c.cod_funcao = ufp.cod_funcao_ajudante)) AND ((m.entrega)::text <> 'AS'::text)) AND ((m.tempoprevistoroad <= um.meta_tempo_rota_horas) OR ((m.cargaatual)::text = 'Recarga'::text))) THEN (((m.vlbateujornaju + m.vlnaobateujornaju) + m.vlrecargaaju) / m.fator)
            ELSE (0)::real
        END +
        CASE
            WHEN (((((c.matricula_ambev = m.matricmotorista) AND (c.cod_funcao = ufp.cod_funcao_motorista)) AND ((m.entrega)::text <> 'AS'::text)) AND (m.tempoprevistoroad > um.meta_tempo_rota_horas)) AND ((m.cargaatual)::text <> 'Recarga'::text)) THEN ((m.cxentreg * (view_valor_cx_unidade.valor_cx_motorista_rota)::double precision) / (m.fator)::double precision)
            WHEN (((((c.matricula_ambev = m.matricajud1) AND (c.cod_funcao = ufp.cod_funcao_ajudante)) AND ((m.entrega)::text <> 'AS'::text)) AND (m.tempoprevistoroad > um.meta_tempo_rota_horas)) AND ((m.cargaatual)::text <> 'Recarga'::text)) THEN ((m.cxentreg * (view_valor_cx_unidade.valor_cx_ajudante_rota)::double precision) / (m.fator)::double precision)
            WHEN (((((c.matricula_ambev = m.matricajud2) AND (c.cod_funcao = ufp.cod_funcao_ajudante)) AND ((m.entrega)::text <> 'AS'::text)) AND (m.tempoprevistoroad > um.meta_tempo_rota_horas)) AND ((m.cargaatual)::text <> 'Recarga'::text)) THEN ((m.cxentreg * (view_valor_cx_unidade.valor_cx_ajudante_rota)::double precision) / (m.fator)::double precision)
            ELSE (0)::double precision
        END) +
        CASE
            WHEN (((c.matricula_ambev = m.matricmotorista) AND (c.cod_funcao = ufp.cod_funcao_motorista)) AND ((m.entrega)::text = 'AS'::text)) THEN
            CASE
                WHEN ((m.entregas = 1) AND ((m.cargaatual)::text <> 'Recarga'::text)) THEN uv.rm_motorista_valor_as_1_entrega
                WHEN ((m.entregas = 2) AND ((m.cargaatual)::text <> 'Recarga'::text)) THEN uv.rm_motorista_valor_as_2_entregas
                WHEN ((m.entregas = 3) AND ((m.cargaatual)::text <> 'Recarga'::text)) THEN uv.rm_motorista_valor_as_3_entregas
                WHEN ((m.entregas > 3) AND ((m.cargaatual)::text <> 'Recarga'::text)) THEN uv.rm_motorista_valor_as_maior_3_entregas
                WHEN ((m.cargaatual)::text = 'Recarga'::text) THEN uv.rm_motorista_valor_as_recarga
                ELSE (0)::real
            END
            WHEN (((c.matricula_ambev = m.matricajud1) AND (c.cod_funcao = ufp.cod_funcao_ajudante)) AND ((m.entrega)::text = 'AS'::text)) THEN
            CASE
                WHEN ((m.entregas = 1) AND ((m.cargaatual)::text <> 'Recarga'::text)) THEN uv.rm_ajudante_valor_as_1_entrega
                WHEN ((m.entregas = 2) AND ((m.cargaatual)::text <> 'Recarga'::text)) THEN uv.rm_ajudante_valor_as_2_entregas
                WHEN ((m.entregas = 3) AND ((m.cargaatual)::text <> 'Recarga'::text)) THEN uv.rm_ajudante_valor_as_3_entregas
                WHEN ((m.entregas > 3) AND ((m.cargaatual)::text <> 'Recarga'::text)) THEN uv.rm_ajudante_valor_as_maior_3_entregas
                WHEN ((m.cargaatual)::text = 'Recarga'::text) THEN uv.rm_ajudante_valor_as_recarga
                ELSE (0)::real
            END
            WHEN ((((c.matricula_ambev = m.matricajud2) AND (c.cod_funcao = ufp.cod_funcao_ajudante)) AND ((m.entrega)::text = 'AS'::text)) AND ((m.cargaatual)::text <> 'Recarga'::text)) THEN
            CASE
                WHEN (m.entregas = 1) THEN uv.rm_ajudante_valor_as_1_entrega
                WHEN (m.entregas = 2) THEN uv.rm_ajudante_valor_as_2_entregas
                WHEN (m.entregas = 3) THEN uv.rm_ajudante_valor_as_3_entregas
                WHEN (m.entregas > 2) THEN uv.rm_ajudante_valor_as_maior_3_entregas
                WHEN ((m.cargaatual)::text = 'Recarga'::text) THEN uv.rm_ajudante_valor_as_recarga
                ELSE (0)::real
            END
            ELSE (0)::real
        END) AS valor
   FROM (((((((((view_mapa_colaborador vmc
     JOIN colaborador c ON ((vmc.cpf = c.cpf)))
     JOIN funcao f ON (((f.codigo = c.cod_funcao) AND (f.cod_empresa = c.cod_empresa))))
     JOIN mapa m ON (((m.mapa = vmc.mapa) AND (m.cod_unidade = vmc.cod_unidade))))
     JOIN unidade_metas um ON ((um.cod_unidade = m.cod_unidade)))
     JOIN view_valor_cx_unidade ON ((view_valor_cx_unidade.cod_unidade = m.cod_unidade)))
     JOIN equipe e ON (((e.cod_unidade = c.cod_unidade) AND (c.cod_equipe = e.codigo))))
     JOIN unidade_funcao_produtividade ufp ON (((ufp.cod_unidade = c.cod_unidade) AND (ufp.cod_unidade = m.cod_unidade))))
     LEFT JOIN unidade_valores_rm uv ON ((uv.cod_unidade = m.cod_unidade)))
     LEFT JOIN ( SELECT t.mapa AS tracking_mapa,
            t."código_transportadora" AS cod_transportadora,
            sum(
                CASE
                    WHEN (t.disp_apont_cadastrado <= um_1.meta_raio_tracking) THEN 1
                    ELSE 0
                END) AS apontamentos_ok,
            count(t.disp_apont_cadastrado) AS total_apontamentos
           FROM (tracking t
             JOIN unidade_metas um_1 ON ((um_1.cod_unidade = t."código_transportadora")))
          GROUP BY t.mapa, t."código_transportadora") tracking ON (((tracking.tracking_mapa = m.mapa) AND (tracking.cod_transportadora = m.cod_unidade))));

-- View já alterada para considerar tz
CREATE VIEW view_extrato_indicadores AS
  SELECT dados.cod_empresa,
    dados.cod_regional,
    dados.cod_unidade,
    dados.cod_equipe,
    dados.cpf,
    dados.nome,
    dados.equipe,
    dados.funcao,
    dados.data,
    dados.mapa,
    dados.placa,
    dados.cxcarreg,
    dados.qthlcarregados,
    dados.qthlentregues,
    dados.qthldevolvidos,
    dados.resultado_devolucao_hectolitro,
    dados.qtnfcarregadas,
    dados.qtnfentregues,
    dados.qtnfdevolvidas,
    dados.resultado_devolucao_nf,
    dados.entregascompletas,
    dados.entregasnaorealizadas,
    dados.entregasparciais,
    dados.entregas_carregadas,
    dados.resultado_devolucao_pdv,
    dados.kmprevistoroad,
    dados.kmsai,
    dados.kmentr,
    dados.km_percorrido,
    dados.resultado_dispersao_km,
    dados.hrsai,
    dados.hrentr,
    dados.tempo_rota,
    dados.tempoprevistoroad,
    dados.resultado_tempo_rota_segundos,
    dados.resultado_dispersao_tempo,
    dados.resultado_tempo_interno_segundos,
    dados.tempo_interno,
    dados.hrmatinal,
    dados.resultado_tempo_largada_segundos,
    dados.tempo_largada,
    dados.total_tracking,
    dados.apontamentos_ok,
    dados.apontamentos_nok,
    dados.resultado_tracking,
    dados.meta_tracking,
    dados.meta_tempo_rota_mapas,
    dados.meta_caixa_viagem,
    dados.meta_dev_hl,
    dados.meta_dev_pdv,
    dados.meta_dev_nf,
    dados.meta_dispersao_km,
    dados.meta_dispersao_tempo,
    dados.meta_jornada_liquida_mapas,
    dados.meta_raio_tracking,
    dados.meta_tempo_interno_mapas,
    dados.meta_tempo_largada_mapas,
    dados.meta_tempo_rota_segundos,
    dados.meta_tempo_interno_segundos,
    dados.meta_tempo_largada_segundos,
    dados.meta_jornada_liquida_segundos,
        CASE
            WHEN ((dados.resultado_devolucao_pdv)::double precision <= dados.meta_dev_pdv) THEN 'SIM'::text
            ELSE 'NÃO'::text
        END AS bateu_dev_pdv,
        CASE
            WHEN ((dados.resultado_devolucao_hectolitro)::double precision <= dados.meta_dev_hl) THEN 'SIM'::text
            ELSE 'NÃO'::text
        END AS bateu_dev_hl,
        CASE
            WHEN ((dados.resultado_devolucao_nf)::double precision <= dados.meta_dev_nf) THEN 'SIM'::text
            ELSE 'NÃO'::text
        END AS bateu_dev_nf,
        CASE
            WHEN (dados.resultado_dispersao_tempo <= dados.meta_dispersao_tempo) THEN 'SIM'::text
            ELSE 'NÃO'::text
        END AS bateu_dispersao_tempo,
        CASE
            WHEN ((dados.resultado_dispersao_km)::double precision <= dados.meta_dispersao_km) THEN 'SIM'::text
            ELSE 'NÃO'::text
        END AS bateu_dispersao_km,
        CASE
            WHEN (dados.resultado_tempo_interno_segundos <= (dados.meta_tempo_interno_segundos)::double precision) THEN 'SIM'::text
            ELSE 'NÃO'::text
        END AS bateu_tempo_interno,
        CASE
            WHEN (dados.resultado_tempo_rota_segundos <= (dados.meta_tempo_rota_segundos)::double precision) THEN 'SIM'::text
            ELSE 'NÃO'::text
        END AS bateu_tempo_rota,
        CASE
            WHEN (dados.resultado_tempo_largada_segundos <= (dados.meta_tempo_largada_segundos)::double precision) THEN 'SIM'::text
            ELSE 'NÃO'::text
        END AS bateu_tempo_largada,
        CASE
            WHEN (((dados.resultado_tempo_largada_segundos + dados.resultado_tempo_rota_segundos) + dados.resultado_tempo_interno_segundos) <= (dados.meta_jornada_liquida_segundos)::double precision) THEN 'SIM'::text
            ELSE 'NÃO'::text
        END AS bateu_jornada,
        CASE
            WHEN ((dados.resultado_tracking)::double precision >= dados.meta_tracking) THEN 'SIM'::text
            ELSE 'NÃO'::text
        END AS bateu_tracking,
        CASE
            WHEN ((dados.resultado_devolucao_pdv)::double precision <= dados.meta_dev_pdv) THEN 1
            ELSE 0
        END AS gol_dev_pdv,
        CASE
            WHEN ((dados.resultado_devolucao_hectolitro)::double precision <= dados.meta_dev_hl) THEN 1
            ELSE 0
        END AS gol_dev_hl,
        CASE
            WHEN ((dados.resultado_devolucao_nf)::double precision <= dados.meta_dev_nf) THEN 1
            ELSE 0
        END AS gol_dev_nf,
        CASE
            WHEN (dados.resultado_dispersao_tempo <= dados.meta_dispersao_tempo) THEN 1
            ELSE 0
        END AS gol_dispersao_tempo,
        CASE
            WHEN ((dados.resultado_dispersao_km)::double precision <= dados.meta_dispersao_km) THEN 1
            ELSE 0
        END AS gol_dispersao_km,
        CASE
            WHEN (dados.resultado_tempo_interno_segundos <= (dados.meta_tempo_interno_segundos)::double precision) THEN 1
            ELSE 0
        END AS gol_tempo_interno,
        CASE
            WHEN (dados.resultado_tempo_rota_segundos <= (dados.meta_tempo_rota_segundos)::double precision) THEN 1
            ELSE 0
        END AS gol_tempo_rota,
        CASE
            WHEN (dados.resultado_tempo_largada_segundos <= (dados.meta_tempo_largada_segundos)::double precision) THEN 1
            ELSE 0
        END AS gol_tempo_largada,
        CASE
            WHEN (((dados.resultado_tempo_largada_segundos + dados.resultado_tempo_rota_segundos) + dados.resultado_tempo_interno_segundos) <= (dados.meta_jornada_liquida_segundos)::double precision) THEN 1
            ELSE 0
        END AS gol_jornada,
        CASE
            WHEN ((dados.resultado_tracking)::double precision >= dados.meta_tracking) THEN 1
            ELSE 0
        END AS gol_tracking
   FROM ( SELECT u.cod_empresa,
            u.cod_regional,
            u.codigo AS cod_unidade,
            e.codigo AS cod_equipe,
            c.cpf,
            c.nome,
            e.nome AS equipe,
            f.nome AS funcao,
            m.data,
            m.mapa,
            m.placa,
            m.cxcarreg,
            m.qthlcarregados,
            m.qthlentregues,
            trunc(((m.qthlcarregados - m.qthlentregues))::numeric, 2) AS qthldevolvidos,
            trunc((
                CASE
                    WHEN (m.qthlcarregados > (0)::double precision) THEN ((m.qthlcarregados - m.qthlentregues) / m.qthlcarregados)
                    ELSE (0)::real
                END)::numeric, 4) AS resultado_devolucao_hectolitro,
            m.qtnfcarregadas,
            m.qtnfentregues,
            (m.qtnfcarregadas - m.qtnfentregues) AS qtnfdevolvidas,
            trunc((
                CASE
                    WHEN (m.qtnfcarregadas > 0) THEN (((m.qtnfcarregadas - m.qtnfentregues))::double precision / (m.qtnfcarregadas)::real)
                    ELSE (0)::double precision
                END)::numeric, 4) AS resultado_devolucao_nf,
            m.entregascompletas,
            m.entregasnaorealizadas,
            m.entregasparciais,
            (m.entregascompletas + m.entregasnaorealizadas) AS entregas_carregadas,
            trunc((
                CASE
                    WHEN (((m.entregascompletas + m.entregasnaorealizadas) + m.entregasparciais) > 0) THEN (((m.entregasnaorealizadas)::real + (m.entregasparciais)::double precision) / (((m.entregascompletas + m.entregasnaorealizadas) + m.entregasparciais))::double precision)
                    ELSE (0)::double precision
                END)::numeric, 4) AS resultado_devolucao_pdv,
            m.kmprevistoroad,
            m.kmsai,
            m.kmentr,
            (m.kmentr - m.kmsai) AS km_percorrido,
                CASE
                    WHEN (m.kmprevistoroad > (0)::double precision) THEN trunc((((((m.kmentr - m.kmsai))::double precision - m.kmprevistoroad) / m.kmprevistoroad))::numeric, 4)
                    ELSE NULL::numeric
                END AS resultado_dispersao_km,
            m.hrsai AS hrsai,
            m.hrentr AS hrentr,
            to_text_hhmmss(m.hrentr - m.hrsai) as tempo_rota,
            to_seconds(m.tempoprevistoroad) AS tempoprevistoroad,
            to_seconds(m.hrentr - m.hrsai) AS resultado_tempo_rota_segundos,
                CASE
                    WHEN (to_seconds(m.tempoprevistoroad) > (0)::double precision) THEN ((to_seconds(m.hrentr - m.hrsai) - to_seconds(m.tempoprevistoroad)) / to_seconds(m.tempoprevistoroad))
                    ELSE (0)::double precision
                END AS resultado_dispersao_tempo,
            to_seconds(m.tempointerno) AS resultado_tempo_interno_segundos,
            m.tempointerno AS tempo_interno,
            m.hrmatinal,
            to_seconds(
                CASE
                    WHEN ((m.hrsai at time zone tz_unidade(m.cod_unidade))::TIME < m.hrmatinal) THEN um.meta_tempo_largada_horas
                    ELSE (m.hrsai - m.hrmatinal::INTERVAL)::TIME WITHOUT TIME ZONE
                END) AS resultado_tempo_largada_segundos,
                CASE
                    WHEN ((m.hrsai at time zone tz_unidade(m.cod_unidade))::TIME < m.hrmatinal) THEN um.meta_tempo_largada_horas
                    ELSE (m.hrsai - m.hrmatinal::INTERVAL)::TIME WITHOUT TIME ZONE
                END AS tempo_largada,
            COALESCE(tracking.total_apontamentos, (0)::bigint) AS total_tracking,
            COALESCE(tracking.apontamentos_ok, (0)::bigint) AS apontamentos_ok,
            COALESCE((tracking.total_apontamentos - tracking.apontamentos_ok), (0)::bigint) AS apontamentos_nok,
                CASE
                    WHEN (tracking.total_apontamentos > 0) THEN (tracking.apontamentos_ok / tracking.total_apontamentos)
                    ELSE (0)::bigint
                END AS resultado_tracking,
            um.meta_tracking,
            um.meta_tempo_rota_mapas,
            um.meta_caixa_viagem,
            um.meta_dev_hl,
            um.meta_dev_pdv,
            um.meta_dev_nf,
            um.meta_dispersao_km,
            um.meta_dispersao_tempo,
            um.meta_jornada_liquida_mapas,
            um.meta_raio_tracking,
            um.meta_tempo_interno_mapas,
            um.meta_tempo_largada_mapas,
            to_seconds((um.meta_tempo_rota_horas)::text) AS meta_tempo_rota_segundos,
            to_seconds((um.meta_tempo_interno_horas)::text) AS meta_tempo_interno_segundos,
            to_seconds((um.meta_tempo_largada_horas)::text) AS meta_tempo_largada_segundos,
            to_seconds((um.meta_jornada_liquida_horas)::text) AS meta_jornada_liquida_segundos
           FROM (((((((((view_mapa_colaborador vmc
             JOIN colaborador c ON (((c.cpf = vmc.cpf) AND (c.cod_unidade = vmc.cod_unidade))))
             JOIN mapa m ON (((m.mapa = vmc.mapa) AND (m.cod_unidade = vmc.cod_unidade))))
             JOIN unidade u ON ((u.codigo = m.cod_unidade)))
             JOIN empresa em ON ((em.codigo = u.cod_empresa)))
             JOIN regional r ON ((r.codigo = u.cod_regional)))
             JOIN unidade_metas um ON ((um.cod_unidade = u.codigo)))
             JOIN equipe e ON (((e.cod_unidade = c.cod_unidade) AND (c.cod_equipe = e.codigo))))
             JOIN funcao f ON (((f.codigo = c.cod_funcao) AND (f.cod_empresa = em.codigo))))
             LEFT JOIN ( SELECT t.mapa AS tracking_mapa,
                    t."código_transportadora" AS tracking_unidade,
                    count(t.disp_apont_cadastrado) AS total_apontamentos,
                    sum(
                        CASE
                            WHEN (t.disp_apont_cadastrado <= um_1.meta_raio_tracking) THEN 1
                            ELSE 0
                        END) AS apontamentos_ok
                   FROM (tracking t
                     JOIN unidade_metas um_1 ON ((um_1.cod_unidade = t."código_transportadora")))
                  GROUP BY t.mapa, t."código_transportadora") tracking ON (((tracking.tracking_mapa = m.mapa) AND (tracking.tracking_unidade = m.cod_unidade))))
          ORDER BY m.data) dados;

-- Recria as functions
-- Function já alterada para considerar tz
CREATE OR REPLACE FUNCTION func_get_produtividade_colaborador(f_mes integer, f_ano integer, f_cpf bigint)
  RETURNS TABLE(
    cod_unidade integer,
    matricula_ambev integer,
    data date,
    cpf bigint,
    nome_colaborador character varying,
    data_nascimento date,
    funcao character varying,
    cod_funcao bigint,
    nome_equipe character varying,
    fator real,
    cargaatual character varying,
    entrega character varying,
    mapa integer,
    placa character varying,
    cxcarreg real,
    cxentreg real,
    qthlcarregados real,
    qthlentregues real,
    qtnfcarregadas integer,
    qtnfentregues integer,
    entregascompletas integer,
    entregasnaorealizadas integer,
    entregasparciais integer,
    kmprevistoroad real,
    kmsai integer,
    kmentr integer,
    tempoprevistoroad integer,
    hrsai timestamp with time zone,
    hrentr timestamp with time zone,
    tempo_rota integer,
    tempointerno integer,
    hrmatinal time without time zone,
    apontamentos_ok bigint,
    total_tracking bigint,
    tempo_largada integer,
    meta_tracking real,
    meta_tempo_rota_mapas real,
    meta_caixa_viagem real,
    meta_dev_hl real,
    meta_dev_nf real,
    meta_dev_pdv real,
    meta_dispersao_km real,
    meta_dispersao_tempo real,
    meta_jornada_liquida_mapas real,
    meta_raio_tracking real,
    meta_tempo_interno_mapas real,
    meta_tempo_largada_mapas real,
    meta_tempo_rota_horas integer,
    meta_tempo_interno_horas integer,
    meta_tempo_largada_horas integer,
    meta_jornada_liquida_horas integer,
    valor_rota real,
    valor_recarga real,
    valor_diferenca_eld double precision,
    valor_as real,
    valor double precision)
LANGUAGE SQL
AS $$
SELECT * FROM VIEW_PRODUTIVIDADE_EXTRATO
  WHERE DATA BETWEEN func_get_data_inicio_produtividade(f_ano, f_mes, f_cpf, null) AND
        func_get_data_fim_produtividade(f_ano, f_mes, f_cpf, null) AND cpf = f_cpf ORDER BY DATA ASC
$$;

-- Func já alterada para lidar com tz
CREATE FUNCTION func_get_produtividade_consolidado_colaboradores(f_data_inicial date, f_data_final date, f_cod_unidade bigint, f_equipe text, f_funcao text)
  RETURNS TABLE(cpf bigint, matricula_ambev integer, nome text, data_nascimento date, funcao text, mapas bigint, caixas real, valor double precision)
LANGUAGE SQL
AS $$
SELECT cpf,
  matricula_ambev,
  nome_colaborador AS nome,
  data_nascimento,
  funcao,
  count(mapa) as mapas,
  sum(cxentreg) as caixas,
  sum(valor) as valor
FROM VIEW_PRODUTIVIDADE_EXTRATO
WHERE data between f_data_inicial and f_data_final
      and cod_unidade = f_cod_unidade
      and nome_equipe like f_equipe
      and cod_funcao::text like f_funcao
GROUP BY 1,2,3,4,5
order by funcao, valor desc, nome
$$;


-- Func já alterada para lidar com tz
CREATE FUNCTION func_relatorio_consolidado_mapas_indicadores(
  f_data_inicial date,
  f_data_final date,
  f_cpf text,
  f_cod_unidade text,
  f_cod_equipe text,
  f_cod_empresa bigint,
  f_cod_regional text)
  RETURNS TABLE(
    "NOME" text,
    "EQUIPE" text,
    "FUNÇÃO" text,
    "TOTAL MAPAS REALIZADOS" bigint,
    "HL CARREGADOS" numeric,
    "HL ENTREGUES" numeric,
    "HL DEVOLVIDOS" numeric,
    "RESULTADO DEV HL" text,
    "META DEV HL" text,
    "BATEU DEV HL" text,
    "GOLS DEV HL" bigint,
    "NF CARREGADAS" bigint,
    "NF ENTREGUES" bigint,
    "NF DEVOLVIDAS" bigint,
    "RESULTADO DEV NF" text,
    "META DEV NF" text,
    "BATEU DEV NF" text,
    "GOLS DEV NF" bigint,
    "PDVS CARREGADOS" bigint,
    "PDVS ENTREGUES" bigint,
    "RESULTADO DEV PDV" text,
    "META DEV PDV" text,
    "BATEU DEV PDV" text,
    "GOLS DEV PDV" bigint,
    "KM PLANEJADO" numeric,
    "KM PERCORRIDO" bigint,
    "KM DISPERSO" numeric,
    "RESULTADO DISPERSAO KM" text,
    "META DISPERSAO KM" text,
    "BATEU DISPERSAO KM" text,
    "GOLS DISPERSAO KM" bigint,
    "MEDIA TEMPO LARGADA" text,
    "MAPAS VÁLIDOS TEMPO LARGADA" bigint,
    "MAPAS BATERAM TEMPO LARGADA" bigint,
    "RESULTADO TEMPO LARGADA" text,
    "META TEMPO LARGADA" text,
    "BATEU TEMPO LARGADA" text,
    "GOLS TEMPO LARGADA" bigint,
    "MEDIA TEMPO EM ROTA" text,
    "MAPAS VÁLIDOS TEMPO EM ROTA" bigint,
    "MAPAS BATERAM TEMPO EM ROTA" bigint,
    "RESULTADO TEMPO EM ROTA" text,
    "META TEMPO EM ROTA" text,
    "BATEU TEMPO EM ROTA" text,
    "GOLS TEMPO EM ROTA" bigint,
    "MEDIA TEMPO INTERNO" text,
    "MAPAS VÁLIDOS TEMPO INTERNO" bigint,
    "MAPAS BATERAM TEMPO INTERNO" bigint,
    "RESULTADO TEMPO INTERNO" text,
    "META TEMPO INTERNO" text,
    "BATEU TEMPO INTERNO" text,
    "GOLS TEMPO INTERNO" bigint,
    "MEDIA JORNADA" text,
    "MAPAS VÁLIDOS JORNADA" bigint,
    "MAPAS BATERAM JORNADA" bigint,
    "RESULTADO JORNADA" text,
    "META JORNADA" text,
    "BATEU JORNADA" text,
    "GOLS JORNADA" bigint,
    "MEDIA TEMPO PLANEJADO" text,
    "MAPAS VÁLIDOS DISPERSAO TEMPO" bigint,
    "MAPAS BATERAM DISP TEMPO" bigint,
    "RESULTADO DISP TEMPO" text,
    "META DISP TEMPO" text,
    "BATEU DISP TEMPO" text,
    "GOLS DISP TEMPO" bigint,
    "TOTAL TRACKING" numeric,
    "TOTAL OK" numeric,
    "TOTAL NOK" numeric,
    "RESULTADO TRACKING" text,
    "META TRACKING" text,
    "BATEU TRACKING" text,
    "GOLS TRACKING" bigint,
    "TOTAL DE GOLS GERAL" bigint)
LANGUAGE SQL
AS $$
select
  dados.nome,
  dados.equipe,
  dados.funcao,
  dados.total_mapas,
  trunc(dados.hl_carregados_total::numeric, 2) as hl_carregados_total,
  trunc((dados.hl_carregados_total - dados.hl_devolvidos_total)::numeric, 2) as hl_entregues_total,
  trunc(dados.hl_devolvidos_total::numeric, 2) as hl_devolvidos_total,
  case when dados.hl_carregados_total > 0 then
    trunc(((dados.hl_devolvidos_total / dados.hl_carregados_total) * 100)::numeric, 2) else 0 end || '%' as resultado_dev_hl,
  trunc((dados.meta_dev_hl * 100)::numeric, 2) || '%' as meta_dev_hl,
  case when dados.hl_carregados_total > 0 and ((dados.hl_devolvidos_total / dados.hl_carregados_total) <= dados.meta_dev_hl) then 'SIM' else 'NÃO' end as bateu_dev_hl,
  dados.gols_dev_hl,
  dados.nf_carregadas_total,
  dados.nf_carregadas_total - dados.nf_devolvidas_total as nf_entregues_total,
  dados.nf_devolvidas_total,
  case when dados.nf_carregadas_total > 0 then
    trunc(((dados.nf_devolvidas_total::float / dados.nf_carregadas_total)*100)::numeric, 2) else 0 end || '%' as resultado_dev_nf,
  trunc((dados.meta_dev_nf * 100)::numeric, 2) || '%' as meta_dev_nf,
  case when dados.nf_carregadas_total > 0 and  ((dados.nf_devolvidas_total / dados.nf_carregadas_total) <= dados.meta_dev_nf) then 'SIM' else 'NÃO' end,
  dados.gols_dev_nf,
  dados.pdv_carregados_total,
  dados.pdv_carregados_total - dados.pdv_devolvidos_total as pdv_entregues,
  case when dados.pdv_carregados_total > 0 then
    trunc(((dados.pdv_devolvidos_total / dados.pdv_carregados_total::float)*100)::numeric, 2) else 0 end || '%' as resultado_dev_pdv,
  trunc((dados.meta_dev_pdv * 100)::numeric, 2) || '%' as meta_dev_pdv,
  case when dados.pdv_carregados_total > 0  and ((dados.pdv_devolvidos_total / dados.pdv_carregados_total) <= dados.meta_dev_pdv) then 'SIM' else 'NÃO' end as bateu_dev_pdv,
  dados.gols_dev_pdv,
  trunc(dados.km_planejado_total::numeric, 2),
  dados.km_percorrido_total,
  trunc((dados.km_percorrido_total - dados.km_planejado_total)::numeric, 2) as km_disperso,
  trunc(((CASE WHEN dados.km_planejado_total > 0
    THEN (dados.km_percorrido_total - dados.km_planejado_total) / dados.km_planejado_total
          ELSE 0 END) * 100)::numeric, 2) || '% ' AS resultado_disp_km,
  trunc((dados.meta_dispersao_km * 100)::numeric, 2) || '%' as meta_disp_km,
  case when (CASE WHEN dados.km_planejado_total > 0
    THEN (dados.km_percorrido_total - dados.km_planejado_total) / dados.km_planejado_total
             ELSE 0 END) <= dados.meta_dev_pdv then 'SIM' else 'NÃO' end as bateu_disp_km,
  dados.gols_dispersao_km,
  dados.media_tempo_largada,
  dados.total_mapas_validos_tempo_largada,
  dados.total_mapas_bateu_tempo_largada,
  case when dados.total_mapas_validos_tempo_largada > 0 then
    trunc(((dados.total_mapas_bateu_tempo_largada / dados.total_mapas_validos_tempo_largada::float) *100)::numeric, 2) else 0 end || '%' as resultado_tempo_largada,
  trunc((dados.meta_tempo_largada_mapas * 100)::numeric, 2) || '%' as meta_tempo_largada,
  case when dados.total_mapas_validos_tempo_largada > 0 and ((dados.total_mapas_bateu_tempo_largada / dados.total_mapas_validos_tempo_largada::float) >= dados.meta_tempo_largada_mapas)
    then 'SIM' else 'NÃO' end as bateu_tempo_largada,
  dados.gols_tempo_largada,
  dados.media_tempo_rota,
  dados.total_mapas,
  dados.total_mapas_bateu_tempo_rota,
  case when dados.total_mapas > 0 then
    trunc(((dados.total_mapas_bateu_tempo_rota / dados.total_mapas::float)*100)::numeric, 2) else 0 end || '%' as resultado_tempo_rota,
  trunc((dados.meta_tempo_rota_mapas * 100)::numeric, 2) || '%' as meta_tempo_rota,
  case when dados.total_mapas > 0 and  ((dados.total_mapas_bateu_tempo_rota / dados.total_mapas::float) >= dados.meta_tempo_rota_mapas) then 'SIM' else 'NÃO' end as bateu_tempo_rota,
  dados.gols_tempo_rota,
  dados.media_tempo_interno,
  dados.total_mapas_validos_tempo_interno,
  dados.total_mapas_bateu_tempo_interno,
  case when dados.total_mapas_validos_tempo_interno > 0 then
    trunc(((dados.total_mapas_bateu_tempo_interno / dados.total_mapas_validos_tempo_interno::float)*100)::numeric, 2) else 0 end || '%' as resultado_tempo_interno,
  trunc((dados.meta_tempo_interno_mapas * 100)::numeric, 2) || '%' as meta_tempo_interno_mapas,
  case when dados.total_mapas_validos_tempo_interno > 0 and ((dados.total_mapas_bateu_tempo_interno / dados.total_mapas_validos_tempo_interno::float) >= dados.meta_tempo_interno_mapas) then 'SIM' else 'NÃO' end as bateu_tempo_interno,
  dados.gols_tempo_interno,
  dados.media_jornada,
  dados.total_mapas,
  dados.total_mapas_bateu_jornada,
  case when dados.total_mapas > 0 then
    trunc(((dados.total_mapas_bateu_jornada / dados.total_mapas::float)*100)::numeric, 2) else 0 end || '%' as resultado_jornada,
  trunc((dados.meta_jornada_liquida_mapas * 100)::numeric, 2) || '%' as meta_jornada_liquida_mapas,
  case when dados.total_mapas > 0 and ((dados.total_mapas_bateu_jornada / dados.total_mapas::float) >= dados.meta_jornada_liquida_mapas) then 'SIM' else 'NÃO' end as bateu_jornada,
  dados.gols_jornada,
  dados.media_tempo_planejado,
  dados.total_mapas,
  dados.total_mapas_bateram_dispersao_tempo,
  case when dados.total_mapas > 0 then
    trunc(((dados.total_mapas_bateram_dispersao_tempo / dados.total_mapas::float)*100)::numeric, 2) else 0 end || '%' as resultado_dispersao_tempo,
  trunc((dados.meta_dispersao_tempo * 100)::numeric, 2) || '%' as meta_dispersao_tempo,
  case when dados.total_mapas > 0 and ((dados.total_mapas_bateram_dispersao_tempo / dados.total_mapas::float) >= dados.meta_dispersao_tempo) then 'SIM' ELSE 'NÃO' END as bateu_dispersao_tempo,
  dados.gols_dispersao_tempo,
  dados.total_apontamentos,
  dados.total_apontamentos_ok,
  dados.total_apontamentos - dados.total_apontamentos_ok as total_apontamentos_nok,
  case when dados.total_apontamentos > 0 then
    trunc(((dados.total_apontamentos_ok / dados.total_apontamentos::float) * 100)::numeric, 2) else 0 end || '%' as resultado_tracking,
  trunc(trunc((dados.meta_tracking)::numeric, 3) * 100, 2) || '%' as meta_tracking,
  case when dados.total_apontamentos > 0 and ((dados.total_apontamentos_ok / dados.total_apontamentos::float) >= dados.meta_tracking) then 'SIM' ELSE 'NÃO' END as bateu_tracking,
  dados.gols_tracking,
  (dados.gols_dev_nf +
   dados.gols_dev_pdv +
   dados.gols_dev_hl +
   dados.gols_tracking +
   dados.gols_dispersao_tempo +
   dados.gols_jornada +
   dados.gols_tempo_interno +
   dados.gols_tempo_largada +
   dados.gols_tempo_rota +
   dados.gols_dispersao_km) as total_gols
from
(select
m.nome,
m.equipe,
m.funcao,
count(m.MAPA) as total_mapas,
   -- CaixaViagem
   sum(m.cxcarreg)                                                                        AS carregadas_total,
   -- Dev Hl
   sum(m.qthlcarregados)                                                                  AS hl_carregados_total,
   sum(qthlcarregados - qthlentregues)                                                    AS hl_devolvidos_total,
   sum(CASE WHEN m.bateu_dev_hl = 'SIM'
     THEN 1
       ELSE 0 END)                                                                        AS total_mapas_bateu_dev_hl,
   -- Dev Nf
   sum(m.qtnfcarregadas)                                                                     nf_carregadas_total,
   sum(qtnfcarregadas - qtnfentregues)                                                    AS nf_devolvidas_total,
   sum(CASE WHEN m.bateu_dev_nf = 'SIM'
     THEN 1
       ELSE 0 END)                                                                        AS total_mapas_bateu_dev_nf,
   -- Dev Pdv
   sum(m.entregascompletas + m.entregasnaorealizadas + m.entregasparciais)                AS pdv_carregados_total,
   sum(m.entregasnaorealizadas + m.entregasparciais)                                      AS pdv_devolvidos_total,
   sum(CASE WHEN m.bateu_dev_pdv = 'SIM'
     THEN 1
       ELSE 0 END)                                                                        AS total_mapas_bateu_dev_pdv,
   -- Dispersão Km
   sum(CASE WHEN (kmentr - m.kmsai) > 0 AND (kmentr - m.kmsai) < 2000
     THEN m.kmprevistoroad
       ELSE 0 END)                                                                        AS km_planejado_total,
   sum(CASE WHEN (kmentr - m.kmsai) > 0 AND (kmentr - m.kmsai) < 2000
     THEN (m.kmentr - m.kmsai)
       ELSE 0 END)                                                                        AS km_percorrido_total,
   sum(CASE WHEN m.bateu_dispersao_km = 'SIM'
     THEN 1
       ELSE 0 END)                                                                        AS total_mapas_bateu_disp_km,
   -- Dispersão de tempo
   sum(CASE WHEN m.bateu_dispersao_tempo = 'SIM'
     THEN 1
       ELSE 0 END)                                                                        AS total_mapas_bateram_dispersao_tempo,
   to_text_hhmmss((avg(m.resultado_tempo_rota_segundos) || ' second') :: INTERVAL) AS media_tempo_realizado,
   to_text_hhmmss((avg(m.tempoprevistoroad) || ' second') :: INTERVAL)             AS media_tempo_planejado,
   -- Jornada --  primeiro verifica se é >00:00, depois verifica se é menor do que a meta
   to_text_hhmmss(
       (AVG(m.RESULTADO_tempo_largada_SEGUNDOS + m.RESULTADO_TEMPO_ROTA_SEGUNDOS + m.RESULTADO_TEMPO_INTERNO_SEGUNDOS)
        || ' second') :: INTERVAL)                                          AS media_jornada,
   sum(CASE WHEN m.bateu_jornada = 'SIM'
     THEN 1
       ELSE 0 END)                                                                        AS total_mapas_bateu_jornada,
   --Tempo Interno
   sum(CASE WHEN m.bateu_tempo_interno = 'SIM'
     THEN 1
       ELSE 0 END)                                                                        AS total_mapas_bateu_tempo_interno,
   sum(CASE WHEN to_text_hhmmss((m.tempo_interno || ' second') :: INTERVAL) :: TIME <= '05:00' AND
                 m.resultado_tempo_interno_segundos > 0
     THEN 1
       ELSE 0
       END)                                                                               AS total_mapas_validos_tempo_interno,
   to_text_hhmmss((avg(m.resultado_tempo_interno_segundos) || ' second')::interval) as media_tempo_interno,
   -- Tempo largada
   sum(case when m.bateu_tempo_largada = 'SIM' then 1 else 0 end) as total_mapas_bateu_tempo_largada,
   -- Total de mapas com tempo de largada válido
   sum(CASE WHEN
     (CASE WHEN (m.hrsai at time zone tz_unidade(m.cod_unidade))::TIME < m.hrmatinal
       THEN to_text_hhmmss((M.meta_tempo_largada_horas || ' second') :: INTERVAL) :: TIME
      ELSE (m.hrsai - m.hrmatinal::INTERVAL) :: TIME
       -- Expurgo de mapas com problema
      END) <= '05:00:00'
     THEN 1
       ELSE 0 END) AS total_mapas_validos_tempo_largada,
   to_text_hhmmss((AVG(resultado_tempo_largada_segundos) || ' second') :: INTERVAL) media_tempo_largada,
   -- Tempo Rota
   SUM(CASE WHEN M.bateu_tempo_rota = 'SIM'
     THEN 1
       ELSE 0 END)              AS                                                        total_mapas_bateu_tempo_rota,
   to_text_hhmmss((AVG(M.resultado_tempo_rota_segundos) || ' second') :: INTERVAL) media_tempo_rota,
   -- Tracking
   sum(M.apontamentos_ok)       AS                                                        total_apontamentos_ok,
   sum(M.total_tracking)        AS                                                        total_apontamentos,
   m.meta_tracking,
   m.meta_tempo_rota_horas      AS                                                        meta_tempo_rota_horas,
   m.meta_tempo_rota_mapas,
   m.meta_caixa_viagem,
   m.meta_dev_hl,
   m.meta_dev_pdv,
   m.meta_dispersao_km,
   m.meta_dispersao_tempo,
   m.meta_jornada_liquida_horas AS                                                        meta_jornada_liquida_horas,
   m.meta_jornada_liquida_mapas,
   m.meta_raio_tracking,
   m.meta_tempo_interno_horas   AS                                                        meta_tempo_interno_horas,
   m.meta_tempo_interno_mapas,
   m.meta_tempo_largada_horas   AS                                                        meta_tempo_largada_horas,
   m.meta_tempo_largada_mapas,
   m.meta_dev_nf,
   sum(m.gol_dev_pdv)           AS                                                        gols_dev_pdv,
   sum(m.gol_dev_nf)            AS                                                        gols_dev_nf,
   sum(m.gol_dev_hl)            AS                                                        gols_dev_hl,
   sum(m.gol_jornada)           AS                                                        gols_jornada,
   sum(m.gol_tempo_interno)     AS                                                        gols_tempo_interno,
   sum(m.gol_tempo_rota)        AS                                                        gols_tempo_rota,
   sum(m.gol_dispersao_tempo)   AS                                                        gols_dispersao_tempo,
   sum(m.gol_dispersao_km)      AS                                                        gols_dispersao_km,
   sum(m.gol_tracking)          AS                                                        gols_tracking,
   sum(m.gol_tempo_largada)     AS                                                        gols_tempo_largada
 FROM view_extrato_indicadores m
 WHERE
   m.cod_empresa = f_cod_empresa
   AND m.cod_regional :: TEXT LIKE f_cod_regional
   AND m.cod_unidade :: TEXT LIKE f_cod_unidade
   AND m.cod_equipe :: TEXT LIKE f_cod_equipe
   AND m.cpf :: TEXT LIKE f_cpf
   AND M.DATA BETWEEN f_data_inicial AND f_data_final
 GROUP BY m.equipe, m.cpf, m.nome, m.cod_unidade, m.meta_tracking,
   m.meta_tempo_rota_horas, m.meta_tempo_rota_mapas, m.meta_caixa_viagem, m.meta_dev_hl, m.meta_dev_pdv,
   m.meta_dispersao_km, m.meta_dispersao_tempo, m.meta_jornada_liquida_horas, m.meta_jornada_liquida_mapas,
   m.meta_raio_tracking, m.meta_tempo_interno_horas, m.meta_tempo_interno_mapas, m.meta_tempo_largada_horas,
   m.meta_tempo_largada_mapas, m.meta_dev_nf, m.funcao) AS dados
ORDER BY total_gols DESC
$$;

-- Func já alterada para lidar com tz
CREATE FUNCTION func_relatorio_extrato_mapas_indicadores(f_data_inicial date, f_data_final date, f_cpf text, f_cod_unidade text, f_cod_equipe text, f_cod_empresa bigint, f_cod_regional text)
  RETURNS TABLE(
    "DATA" text,
    "EQUIPE" text,
    "NOME" text,
    "FUNÇÃO" text,
    "MAPA" integer,
    "PLACA" text,
    "CAIXAS CARREGADAS" real,
    "HL CARREGADOS" real,
    "HL ENTREGUES" real,
    "HL DEVOLVIDOS" numeric,
    "RESULTADO DEV HL" text,
    "META DEV HL" text,
    "BATEU DEV HL" text,
    "NF CARREGADAS" integer,
    "NF ENTREGUES" integer,
    "NF DEVOLVIDAS" integer,
    "RESULTADO DEV NF" text,
    "META DEV NF" text,
    "BATEU DEV NF" text,
    "ENTREGAS CARREGADAS" integer,
    "ENTREGAS REALIZADAS" integer,
    "ENTREGAS DEVOLVIDAS" integer,
    "RESULTADO DEV PDV" text,
    "META DEV PDV" text,
    "BATEU DEV PDV" text,
    "KM PREVISTO" real,
    "KM PERCORRIDO" integer,
    "KM DISPERSO" numeric,
    "RESULTADO DISP KM" text,
    "META DISP KM" text,
    "BATEU DISP KM" text,
    "HORÁRIO MATINAL" time without time zone,
    "HORÁRIO SAÍDA" text,
    "TEMPO DE LARGADA" time without time zone,
    "META TEMPO DE LARGADA" text,
    "BATEU TML" text,
    "HR SAÍDA" text,
    "HR ENTR" text,
    "TEMPO EM ROTA" text,
    "META TEMPO EM ROTA" text,
    "BATEU TEMPO EM ROTA" text,
    "TEMPO INTERNO" text,
    "META TEMPO INTERNO" text,
    "BATEU TEMPO INTERNO" text,
    "JORNADA" text,
    "META JORNADA" text,
    "BATEU JORNADA" text,
    "TEMPO PREVISTO" text,
    "TEMPO REALIZADO" text,
    "DISPERSÃO" text,
    "META DISPERSÃO DE TEMPO" text,
    "BATEU DISPERSÃO DE TEMPO" text,
    "TOTAL ENTREGAS" bigint,
    "APONTAMENTOS OK" bigint,
    "APONTAMENTOS NOK" bigint,
    "ADERENCIA TRACKING" text,
    "META TRACKING" text,
    "BATEU TRACKING" text,
    "TOTAL DE GOLS" integer)
LANGUAGE SQL
AS $$
SELECT to_char(v.data, 'DD/MM/YYYY'),
    v.equipe,
    v.nome,
    v.funcao,
    v.mapa,
    v.placa,
    v.cxcarreg,
    --     dev HL
    v.qthlcarregados,
    v.qthlentregues,
    v.qthldevolvidos,
    trunc((v.resultado_devolucao_hectolitro * 100)::numeric, 2) || '%',
    trunc((v.meta_dev_hl * 100)::numeric, 2) || '%',
    v.BATEU_DEV_HL,
--     dev NF
    v.qtnfcarregadas,
    v.qtnfentregues,
    v.qtnfdevolvidas,
    trunc((v.resultado_devolucao_nf * 100)::numeric, 2) || '%',
    trunc((v.meta_dev_nf * 100)::numeric, 2) || '%',
    v.BATEU_DEV_NF,
--   dev PDV
    v.entregas_carregadas,
    v.entregascompletas,
    v.entregasparciais + v.entregasnaorealizadas,
    trunc((v.resultado_devolucao_pdv * 100)::numeric, 2) || '%',
    trunc((v.meta_dev_pdv * 100)::numeric, 2) || '%',
    v.BATEU_DEV_PDV,
--   dispersão KM
    v.kmprevistoroad,
    v.km_percorrido,
    trunc((v.km_percorrido - v.kmprevistoroad)::numeric, 2),
    trunc((v.resultado_dispersao_km * 100)::numeric, 2) || '%',
    trunc((v.meta_dispersao_km * 100)::numeric, 2) || '%',
    v.BATEU_DISPERSAO_KM,
--     tempo de largada
    v.hrmatinal,
    v.hrsai,
    v.TEMPO_LARGADA,
    to_text_hhmmss((v.meta_tempo_largada_horas || ' second')::interval),
    v.BATEU_TEMPO_LARGADA,
--     tempo em rota
    v.hrsai,
    v.hrentr,
    v.tempo_rota,
    to_text_hhmmss((v.meta_tempo_rota_horas || ' second')::interval),
    v.BATEU_TEMPO_ROTA,
--   tempo interno
    case when v.RESULTADO_TEMPO_INTERNO_SEGUNDOS > 0 then
    to_text_hhmmss((v.RESULTADO_TEMPO_INTERNO_SEGUNDOS || ' second')::interval) else 0::text end,
    case when v.meta_tempo_interno_horas > 0 then
    to_text_hhmmss((v.meta_tempo_interno_horas || ' second')::interval) else 0::text end,
    v.BATEU_TEMPO_INTERNO,
--     jornada
    case when (v.RESULTADO_TEMPO_INTERNO_SEGUNDOS + v.RESULTADO_tempo_largada_SEGUNDOS + v.RESULTADO_TEMPO_ROTA_SEGUNDOS) > 0 then
    to_text_hhmmss(((v.RESULTADO_TEMPO_INTERNO_SEGUNDOS + v.RESULTADO_tempo_largada_SEGUNDOS + v.RESULTADO_TEMPO_ROTA_SEGUNDOS) || ' second')::interval) else 0::text end,
    case when v.meta_jornada_liquida_horas > 0 then
    to_text_hhmmss((v.meta_jornada_liquida_horas || ' second')::interval) else 0::text end,
    v.BATEU_JORNADA,
--     dispersao de tempo
    case when v.tempoprevistoroad > 0 then
    to_text_hhmmss((v.tempoprevistoroad || ' second')::interval) else 0::text end,
    v.tempo_rota,
    trunc((v.RESULTADO_DISPERSAO_TEMPO * 100)::numeric, 2) || '%',
    trunc(trunc((v.meta_dispersao_tempo)::numeric, 3) * 100, 2) || '%',
    v.BATEU_DISPERSAO_TEMPO,
--     tracking
    v.TOTAL_TRACKING,
    v.APONTAMENTOS_OK,
    v.APONTAMENTOS_NOK,
    trunc((v.RESULTADO_TRACKING * 100)::numeric, 2) || '%',
    trunc(trunc((v.meta_tracking)::numeric, 3) * 100, 2) || '%',
    v.BATEU_TRACKING,
    (v.gol_dev_nf +
   v.gol_dev_pdv +
   v.gol_dev_hl +
   v.gol_tracking +
   v.gol_dispersao_tempo +
    v.gol_jornada +
   v.gol_tempo_interno +
  v.gol_tempo_largada +
   v.gol_tempo_rota +
   v.gol_dispersao_km) as total_gols
  FROM VIEW_EXTRATO_INDICADORES V
  WHERE
    case when f_cod_empresa is null then true else (v.cod_empresa = f_cod_empresa) end AND
    V.cod_regional::text like f_cod_regional AND
    v.cod_unidade::text like f_cod_unidade AND
    v.cod_equipe::text like f_cod_equipe AND
    v.cpf::text LIKE f_cpf AND
    v.data between f_data_inicial and f_data_final
$$;

-- Func já alterada para lidar com tz
CREATE FUNCTION func_relatorio_acessos_produtividade_estratificado(f_cod_unidade bigint, f_data_inicial date, f_data_final date, f_cpf text)
  RETURNS TABLE("NOME" text, "CARGO" text, "EQUIPE" text, "DATA DO ACESSO" text, "PERÍODO CONSULTADO" text)
LANGUAGE SQL
AS $$
SELECT C.NOME, F.NOME, E.nome, TO_CHAR(AP.data_hora_consulta at time zone tz_unidade(f_cod_unidade), 'DD/MM/YYYY HH24:MI'), AP.mes_ano_consultado
FROM acessos_produtividade AP JOIN COLABORADOR C ON C.CPF = AP.cpf_colaborador
  JOIN EQUIPE E ON E.CODIGO = C.cod_equipe AND E.cod_unidade = C.COD_UNIDADE
  JOIN FUNCAO F ON F.CODIGO = C.COD_FUNCAO AND F.COD_EMPRESA = C.cod_empresa
WHERE AP.cpf_colaborador :: TEXT LIKE f_cpf
      AND (AP.data_hora_consulta at time zone tz_unidade(f_cod_unidade))::DATE BETWEEN f_data_inicial AND f_data_final
      AND AP.cod_unidade = f_cod_unidade
ORDER BY AP.data_hora_consulta
$$;

-- Func já alterada para lidar com tz
CREATE FUNCTION func_relatorio_consolidado_produtividade(f_dt_inicial date, f_dt_final date, f_cod_unidade bigint)
  RETURNS TABLE(
    "MATRICULA AMBEV" integer,
    "COLABORADOR" text,
    "FUNÇÃO" text,
    "CXS ENTREGUES" integer,
    "JORNADAS BATIDAS" bigint,
    "RESULTADO JORNADA" text,
    "DEV PDV" text,
    "META DEV PDV" text,
    "RECEBE BÔNUS" text,
    "VALOR BÔNUS" text,
    "Nº FATOR 1" bigint,
    "Nº FATOR 2" bigint,
    "Nº ROTAS" bigint,
    "VALOR ROTA" text,
    "Nº RECARGAS" bigint,
    "VALOR RECARGA" text,
    "Nº ELD" bigint,
    "DIFERENÇA ELD" text,
    "Nº AS" bigint,
    "VALOR AS" text,
    "Nº MAPAS TOTAL" bigint,
    "VALOR TOTAL" text)
LANGUAGE SQL
AS $$
SELECT
  matricula_ambev,
  initcap(nome_colaborador) AS "COLABORADOR",
  funcao AS "FUNÇÃO",
  trunc(sum(cxentreg))::INT        AS "CXS ENTREGUES",
  sum( case when (tempo_largada + tempo_rota + tempointerno) <= meta_jornada_liquida_segundos
    then 1 else 0 end ) as qtde_jornada_batida,
  trunc((sum( case when (tempo_largada + tempo_rota + tempointerno) <= meta_jornada_liquida_segundos
    then 1 else 0 end )::float / count(meta_jornada_liquida_segundos))*100) || '%' as porcentagem_jornada,
  REPLACE(round( ((sum(entregasnaorealizadas + entregasparciais))::numeric / sum(entregascompletas+entregasparciais+entregasnaorealizadas)::numeric)*100, 2)::TEXT, '.', ',') || '%' as "DEV PDV",
  REPLACE(round((meta_dev_pdv * 100)::numeric, 2)::TEXT, '.', ',') || '%' AS "META DEV PDV",
  CASE WHEN round(1 - sum(entregascompletas)/sum(entregascompletas + entregasparciais + entregasnaorealizadas)::numeric, 4) <= meta_dev_pdv THEN
    'SIM' ELSE 'NÃO' END as "RECEBE BÔNUS",
  REPLACE((CASE WHEN round(1 - sum(entregascompletas)/sum(entregascompletas + entregasparciais + entregasnaorealizadas)::numeric, 4) <= meta_dev_pdv AND VPE.cod_funcao = PCI.cod_cargo_motorista THEN
    PCI.bonus_motorista
             WHEN round(1 - sum(entregascompletas)/sum(entregascompletas + entregasparciais + entregasnaorealizadas)::numeric, 4) <= meta_dev_pdv AND VPE.cod_funcao = PCI.cod_cargo_ajudante THEN
               PCI.bonus_ajudante
             ELSE 0 END)::TEXT, '.', ',') as "VALOR BÔNUS",
  sum(CASE WHEN fator = 1 then 1 else 0 end) as "Nº FATOR 1",
  sum(CASE WHEN fator = 2 then 1 else 0 end) as "Nº FATOR 2",
  sum(CASE WHEN valor_rota > 0 THEN 1 else 0 END) as "Nº ROTAS",
  REPLACE('R$ ' || trunc(sum(valor_rota)::NUMERIC, 2),'.', ',') AS "VALOR ROTA",
  sum(CASE WHEN valor_recarga > 0 THEN 1 else 0 END) as "Nº RECARGAS",
  REPLACE('R$ ' || trunc(sum(valor_recarga) :: NUMERIC, 2),'.', ',') AS "VALOR RECARGA",
  sum(CASE WHEN valor_diferenca_eld > 0 THEN 1 else 0 END) as "Nº ELD",
  REPLACE('R$ ' || trunc(sum(valor_DIFERENCA_ELD) :: NUMERIC, 2), '.', ',') AS "DIFERENÇA ELD" ,
  sum(CASE WHEN valor_as > 0 THEN 1 else 0 END) as "Nº AS",
  REPLACE('R$ ' || trunc(sum(valor_AS) :: NUMERIC, 2), '.', ',') AS "VALOR AS",
  sum(CASE WHEN valor > 0 THEN 1 else 0 END) as "Nº MAPAS TOTAL",
  REPLACE('R$ ' ||trunc(((CASE WHEN round(1 - sum(entregascompletas)/sum(entregascompletas + entregasparciais + entregasnaorealizadas)::numeric, 4) <= meta_dev_pdv AND VPE.cod_funcao = PCI.cod_cargo_motorista THEN
    PCI.bonus_motorista
                          WHEN round(1 - sum(entregascompletas)/sum(entregascompletas + entregasparciais + entregasnaorealizadas)::numeric, 4) <= meta_dev_pdv AND VPE.cod_funcao = PCI.cod_cargo_ajudante THEN
                            PCI.bonus_ajudante
                          ELSE 0 END) +
                         sum(valor)) :: NUMERIC, 2), '.', ',') AS "VALOR TOTAL"
FROM view_produtividade_extrato vpe
  LEFT JOIN pre_contracheque_informacoes pci on pci.cod_unidade = vpe.cod_unidade
WHERE vpe.cod_unidade = f_cod_unidade AND vpe.data BETWEEN f_dt_inicial AND f_dt_final
GROUP BY matricula_ambev, nome_colaborador, vpe.cod_funcao,funcao, meta_dev_pdv, PCI.cod_cargo_ajudante, PCI.cod_cargo_motorista, PCI.bonus_ajudante, PCI.bonus_motorista
ORDER BY nome_colaborador;
$$;

-- Nova view
CREATE VIEW view_indicadores_acumulados AS
  SELECT
    -- CaixaViagem
    sum(m.cxcarreg) as carregadas_total,
    count(m.mapa) as viagens_total,


    -- Dev Hl
    sum(m.qthlcarregados) hl_carregados_total,
    sum(qthlcarregados - qthlentregues) as hl_devolvidos_total,


    -- Dev Nf
    sum(m.qtnfcarregadas) nf_carregadas_total,
    sum(qtnfcarregadas - qtnfentregues) as nf_devolvidas_total,


    -- Dev Pdv
    sum(m.entregascompletas + m.entregasnaorealizadas + m.entregasparciais) as pdv_carregados_total,
    sum(m.entregasnaorealizadas + m.entregasparciais) as pdv_devolvidos_total,


    -- Dispersão Km
    sum(
        case when
          -- A comparação de < 2000 serve para expurgar valores de mapas com problema.
          (kmentr - m.kmsai) > 0 and (kmentr - m.kmsai) < 2000
          then m.kmprevistoroad
        else 0
        end
    ) as km_planejado_total,
    sum(
        case when
          -- A comparação de < 2000 serve para expurgar valores de mapas com problema.
          (kmentr - m.kmsai) > 0 and (kmentr - m.kmsai) < 2000
          then (m.kmentr - m.kmsai)
        else 0
        end
    ) as km_percorrido_total,


    -- Dispersão de tempo
    sum(
        case when
          (m.hrentr - m.hrsai)::TIME <= m.tempoprevistoroad
          and (m.hrentr - m.hrsai)::TIME > '00:00:00'::TIME
          and m.tempoprevistoroad > '00:00:00'::TIME
          then 1
        else 0
        end
    ) as total_mapas_bateram_dispersao_tempo,
    extract(epoch from avg(
        case WHEN
          (m.hrentr - m.hrsai)::TIME > '00:00:00'::TIME
          and m.tempoprevistoroad > '00:00:00'::TIME
          then (m.hrentr - m.hrsai)::TIME
        end)
    ) as media_dispersao_tempo_realizado,
    extract(epoch from avg(
        case WHEN
          (m.hrentr - m.hrsai)::TIME > '00:00:00'::TIME
          and m.tempoprevistoroad > '00:00:00'::TIME
          then m.tempoprevistoroad
        end)
    ) as media_dispersao_tempo_planejado,


    -- Jornada --  primeiro verifica se é > 00:00, depois verifica se é menor do que a meta.
    sum(
        case when
          (case
           when (m.hrsai at time zone tz_unidade(m.cod_unidade)) :: time < m.hrmatinal
             then (um.meta_tempo_largada_horas :: INTERVAL + (m.hrentr - m.hrsai)::INTERVAL + m.tempointerno :: INTERVAL)
           when (m.hrsai at time zone tz_unidade(m.cod_unidade)) :: time >= m.hrmatinal
             then (((m.hrsai at time zone tz_unidade(m.cod_unidade))::TIME::INTERVAL - m.hrmatinal::INTERVAL) + (m.hrentr - m.hrsai) + m.tempointerno::INTERVAL)
           end)::TIME > '00:00:00' :: TIME
          and
          (case
           when (m.hrsai at time zone tz_unidade(m.cod_unidade)) :: time < m.hrmatinal
             then (um.meta_tempo_largada_horas :: INTERVAL + (m.hrentr - m.hrsai) :: INTERVAL + m.tempointerno :: INTERVAL)
           when (m.hrsai at time zone tz_unidade(m.cod_unidade)) :: time >= m.hrmatinal
             -- Aqui temos um caso diferente, onde precisamos subtrair um TIME de um TIMESTAMPTZ porém precisamos que o
             -- retorno de tudo seja um INTERVAL, ou TIME
             then (((m.hrsai at time zone tz_unidade(m.cod_unidade))::TIME::INTERVAL - m.hrmatinal::INTERVAL) + (m.hrentr - m.hrsai) + m.tempointerno::INTERVAL)
           end) :: TIME <= um.meta_jornada_liquida_horas
          then 1
        else 0 end
    ) as total_mapas_bateram_jornada,
    extract(epoch from avg(
        case
        when (m.hrsai at time zone tz_unidade(m.cod_unidade))::time < m.hrmatinal
          then (um.meta_tempo_largada_horas::interval + (m.hrentr - m.hrsai) + m.tempointerno)
        when (m.hrsai at time zone tz_unidade(m.cod_unidade))::time >= m.hrmatinal
          then (((m.hrsai at time zone tz_unidade(m.cod_unidade))::TIME::INTERVAL - m.hrmatinal::INTERVAL) + (m.hrentr - m.hrsai) + m.tempointerno)
        else null
        end)
    ) as media_jornada,


    --Tempo Interno
    sum(
        case
        when m.tempointerno <= um.meta_tempo_interno_horas and m.tempointerno > '00:00:00'::TIME
          then 1
        else 0
        end
    ) as total_mapas_bateram_tempo_interno,
    -- A comparação de <= 05:00:00 serve para expurgar valores de mapas com problema.
    sum(
        case when m.tempointerno <= '05:00:00'::TIME and m.tempointerno > '00:00:00'::TIME
          then 1
        else 0
        end
    ) as total_mapas_validos_tempo_interno,
    -- A comparação de <= 05:00:00 serve para expurgar valores de mapas com problema.
    extract(epoch from avg(
        case when m.tempointerno > '00:00:00'::TIME and m.tempointerno <= '05:00:00'::TIME
          then m.tempointerno
        else null
        end)
    ) as media_tempo_interno,


    -- Tempo largada
    sum(
        case when
          (case when (m.hrsai at time zone tz_unidade(m.cod_unidade)) :: time < m.hrmatinal
            then um.meta_tempo_largada_horas
           else ((m.hrsai - m.hrmatinal::INTERVAL) at time zone tz_unidade(m.cod_unidade)):: time
           end) :: TIME <= um.meta_tempo_largada_horas
          then 1
        else 0 end
    ) as total_mapas_bateram_tempo_largada,
    sum(
        case when
          (case when (m.hrsai at time zone tz_unidade(m.cod_unidade)) :: time < m.hrmatinal
            then um.meta_tempo_largada_horas
           else ((m.hrsai - m.hrmatinal::INTERVAL) at time zone tz_unidade(m.cod_unidade)):: time
           -- A comparação de <= 05:00:00 serve para expurgar valores de mapas com problema.
           end) <= '05:00:00'::TIME
          then 1
        else 0 end
    ) as total_mapas_validos_tempo_largada,
    extract(epoch from avg(
        case when (m.hrsai at time zone tz_unidade(m.cod_unidade)) :: time < m.hrmatinal
          then um.meta_tempo_largada_horas
        -- A comparação de > 05:00:00 serve para expurgar valores de mapas com problema.
        when ((m.hrsai - m.hrmatinal::INTERVAL) at time zone tz_unidade(m.cod_unidade)):: time > '05:00:00'::TIME
          then '00:30:00'::TIME
        else ((m.hrsai - m.hrmatinal::INTERVAL) at time zone tz_unidade(m.cod_unidade)):: time
        end)
    ) media_tempo_largada,


    -- Tempo Rota
    sum(
        case when (m.hrentr - m.hrsai)::TIME > '00:00:00' and (m.hrentr - m.hrsai)::TIME <= meta_tempo_rota_horas
          then 1
        else 0
        end
    ) as total_mapas_bateram_tempo_rota,
    extract(epoch from avg(
        CASE WHEN (m.hrentr - m.hrsai)::TIME > '00:00:00'
          THEN (m.hrentr - m.hrsai)::TIME
        END)
    )::text AS media_tempo_rota,

    -- Tracking
    sum(tracking.apontamentos_ok) as total_apontamentos_ok,
    sum(tracking.total_apontamentos) as total_apontamentos,

    um.meta_tracking,
    to_seconds(um.meta_tempo_rota_horas) as meta_tempo_rota_horas,
    um.meta_tempo_rota_mapas,
    um.meta_caixa_viagem,
    um.meta_dev_hl,
    um.meta_dev_pdv,
    um.meta_dispersao_km,
    um.meta_dispersao_tempo,
    to_seconds(um.meta_jornada_liquida_horas) as meta_jornada_liquida_horas,
    um.meta_jornada_liquida_mapas,
    um.meta_raio_tracking,
    to_seconds(um.meta_tempo_interno_horas) as meta_tempo_interno_horas,
    um.meta_tempo_interno_mapas,
    to_seconds(um.meta_tempo_largada_horas) as meta_tempo_largada_horas,
    um.meta_tempo_largada_mapas,
    um.meta_dev_nf
    FROM MAPA M
      join unidade_metas um on um.cod_unidade = m.cod_unidade
      LEFT JOIN (SELECT
                   t.mapa                         as tracking_mapa,
                   sum(case when t.disp_apont_cadastrado <= um.meta_raio_tracking
                     then 1
                       else 0 end)                as apontamentos_ok,
                   count(t.disp_apont_cadastrado) as total_apontamentos
                 from tracking t
                   join unidade_metas um on um.cod_unidade = t.código_transportadora
                 group by 1) as tracking on tracking_mapa = m.mapa
      JOIN UNIDADE U ON U.codigo = M.cod_unidade
    group by
      um.cod_unidade,
      um.meta_tracking,
      um.meta_tempo_rota_horas,
      um.meta_tempo_rota_mapas,
      um.meta_caixa_viagem,
      um.meta_dev_hl,
      um.meta_dev_pdv,
      um.meta_dispersao_km,
      um.meta_dispersao_tempo,
      um.meta_jornada_liquida_horas,
      um.meta_jornada_liquida_mapas,
      um.meta_raio_tracking,
      um.meta_tempo_interno_horas,
      um.meta_tempo_interno_mapas,
      um.meta_tempo_largada_horas,
      um.meta_tempo_largada_mapas,
      um.meta_dev_nf;

END TRANSACTION;