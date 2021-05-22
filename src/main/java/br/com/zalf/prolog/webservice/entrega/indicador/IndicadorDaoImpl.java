package br.com.zalf.prolog.webservice.entrega.indicador;

import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.datetime.DateUtils;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.entrega.indicador.acumulado.*;
import br.com.zalf.prolog.webservice.entrega.indicador.item.*;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class IndicadorDaoImpl extends DatabaseConnection implements IndicadorDao {

    public static final String COLUNAS_ACUMULADOS = "-- CaixaViagem\n" +
            "sum(m.cxcarreg) as carregadas_total, count(m.mapa) as viagens_total,\n" +
            "-- Dev Hl\n" +
            "sum(m.qthlcarregados) hl_carregados_total, sum(qthlcarregados - qthlentregues) as hl_devolvidos_total,\n" +
            "-- Dev Nf\n" +
            "sum(m.qtnfcarregadas) nf_carregadas_total, sum(qtnfcarregadas - qtnfentregues) as nf_devolvidas_total,\n" +
            "-- Dev Pdv\n" +
            "sum(m.entregascompletas + m.entregasnaorealizadas + m.entregasparciais) as pdv_carregados_total, sum(m" +
            ".entregasnaorealizadas + m.entregasparciais) as pdv_devolvidos_total,\n" +
            "-- Dispersão Km\n" +
            "sum(case when (kmentr - m.kmsai) > 0 and (kmentr - m.kmsai) < 2000 then m.kmprevistoroad\n" +
            "else 0 end) as km_planejado_total,\n" +
            "sum(case when (kmentr - m.kmsai) > 0 and (kmentr - m.kmsai) < 2000 then (m.kmentr - m.kmsai)\n" +
            "else 0 end) as km_percorrido_total,\n" +
            "-- Dispersão de tempo\n" +
            "sum(case when (m.hrentr - m.hrsai) <= m.tempoprevistoroad and (m.hrentr - m.hrsai) > '00:00' and m" +
            ".tempoprevistoroad > '00:00' then 1\n" +
            "else 0\n" +
            "end) as total_mapas_bateram_dispersao_tempo,\n" +
            "extract(epoch from avg(case WHEN (m.hrentr - m.hrsai) > '00:00'  and m.tempoprevistoroad > '00:00' then " +
            "(m.hrentr - m.hrsai)\n" +
            "end)) as media_dispersao_tempo_realizado,\n" +
            "extract(epoch from avg(case WHEN (m.hrentr - m.hrsai) > '00:00'  and m.tempoprevistoroad > '00:00' then " +
            "m.tempoprevistoroad\n" +
            "end)) as media_dispersao_tempo_planejado,\n" +
            "-- Jornada --  primeiro verifica se é >00:00, depois verifica se é menor do que a meta\n" +
            "sum(case when\n" +
            "(case when m.hrsai::time < m.hrmatinal then (um.meta_tempo_largada_horas::timetz + (m.hrentr - m.hrsai) " +
            "+ m.tempointerno)\n" +
            "when m.hrsai::time >= m.hrmatinal then ((m.hrsai::time - m.hrmatinal) + (m.hrentr - m.hrsai) + m" +
            ".tempointerno)\n" +
            "end) > '00:00'\n" +
            "and\n" +
            "(case when m.hrsai::time < m.hrmatinal then (um.meta_tempo_largada_horas::timetz + (m.hrentr - m.hrsai) " +
            "+ m.tempointerno)\n" +
            "when m.hrsai::time >= m.hrmatinal then ((m.hrsai::time - m.hrmatinal) + (m.hrentr - m.hrsai) + m" +
            ".tempointerno)\n" +
            "end) <= um.meta_jornada_liquida_horas then 1 else 0 end) as total_mapas_bateram_jornada,\n" +
            "extract(epoch from avg(case when m.hrsai::time < m.hrmatinal then (um.meta_tempo_largada_horas::interval" +
            " + (m.hrentr - m.hrsai) + m.tempointerno)\n" +
            "when m.hrsai::time >= m.hrmatinal then ((m.hrsai::time - m.hrmatinal) + (m.hrentr - m.hrsai) + m" +
            ".tempointerno)\n" +
            "else null\n" +
            "end)) as media_jornada,\n" +
            "--Tempo Interno\n" +
            "sum(case when m.tempointerno <= um.meta_tempo_interno_horas and m.tempointerno > '00:00' then 1\n" +
            "else 0\n" +
            "end) as total_mapas_bateram_tempo_interno,\n" +
            "sum(case when m.tempointerno <= '05:00' and m.tempointerno > '00:00' then 1\n" +
            "else 0\n" +
            "end) as total_mapas_validos_tempo_interno,\n" +
            "extract(epoch from avg(case when m.tempointerno > '00:00' and m.tempointerno <= '05:00' then m" +
            ".tempointerno\n" +
            "else null\n" +
            "end)) as media_tempo_interno,\n" +
            "-- Tempo largada\n" +
            "sum(case when\n" +
            "(case when m.hrsai::time < m.hrmatinal then um.meta_tempo_largada_horas\n" +
            "else (m.hrsai - m.hrmatinal)::time\n" +
            "end) <= um.meta_tempo_largada_horas then 1\n" +
            "else 0 end) as total_mapas_bateram_tempo_largada,\n" +
            "sum(case when\n" +
            "(case when m.hrsai::time < m.hrmatinal then um.meta_tempo_largada_horas\n" +
            "else (m.hrsai - m.hrmatinal)::time\n" +
            "end) <= '05:00' then 1\n" +
            "else 0 end) as total_mapas_validos_tempo_largada,\n" +
            "extract(epoch from avg(case when m.hrsai::time < m.hrmatinal then um.meta_tempo_largada_horas\n" +
            "when (m.hrsai - m.hrmatinal)::time > '05:00' then '00:30'\n" +
            "else (m.hrsai - m.hrmatinal)::time\n" +
            "end)) media_tempo_largada,\n" +
            "-- Tempo Rota\n" +
            "sum(case when (m.hrentr - m.hrsai) > '00:00' and (m.hrentr - m.hrsai) <= meta_tempo_rota_horas then 1\n" +
            "else 0 end) as total_mapas_bateram_tempo_rota,\n" +
            "-- to_seconds(avg(case when (m.hrentr - m.hrsai) > '00:00' then (m.hrentr - m.hrsai)\n" +
            "-- end)::text) as media_tempo_rota,\n" +
            "\n" +
            "      extract(epoch from avg(CASE WHEN (m.hrentr - m.hrsai) > '00:00'\n" +
            "    THEN (m.hrentr - m.hrsai)\n" +
            "                 END)) :: text\n" +
            "    AS media_tempo_rota,\n" +
            "\n" +
            "-- Tracking\n" +
            "sum(tracking.apontamentos_ok) as total_apontamentos_ok,\n" +
            "sum(tracking.total_apontamentos) as total_apontamentos,\n" +
            "um.meta_tracking,to_seconds(um.meta_tempo_rota_horas::text) as meta_tempo_rota_horas,um" +
            ".meta_tempo_rota_mapas,um.meta_caixa_viagem,\n" +
            "um.meta_dev_hl,um.meta_dev_pdv,um.meta_dispersao_km,um.meta_dispersao_tempo,to_seconds(um" +
            ".meta_jornada_liquida_horas::text) as meta_jornada_liquida_horas,\n" +
            "um.meta_jornada_liquida_mapas,um.meta_raio_tracking,to_seconds(um.meta_tempo_interno_horas::text) as " +
            "meta_tempo_interno_horas,um.meta_tempo_interno_mapas,to_seconds(um.meta_tempo_largada_horas::text) as " +
            "meta_tempo_largada_horas,\n" +
            "um.meta_tempo_largada_mapas, um.meta_dev_nf\n ";
    public static final String COLUNAS_EXTRATO =
            " M.DATA,  M.mapa, M.PLACA, E.nome as equipe, c1.nome as motorista,c2.nome as aj1," +
                    "c3.nome as aj2,M.cxcarreg, M.QTHLCARREGADOS,  M.QTHLENTREGUES, M.QTNFCARREGADAS, M" +
                    ".QTNFENTREGUES,  M.entregascompletas,  M.entregasnaorealizadas,  m.entregasparciais, " +
                    "M.kmprevistoroad, M.kmsai, M.kmentr, to_seconds(M.tempoprevistoroad::text) as tempoprevistoroad," +
                    "\n" +
                    "M.HRSAI,  M.HRENTR, to_seconds((M.hrentr - M.hrsai)::text) AS TEMPO_ROTA,  to_seconds(M" +
                    ".TEMPOINTERNO::text) as tempointerno,  M.HRMATINAL,  TRACKING.TOTAL_APONTAMENTOS AS " +
                    "TOTAL_TRACKING,  TRACKING.APONTAMENTOS_OK, " +
                    "to_seconds((case when m.hrsai::time < m.hrmatinal then um.meta_tempo_largada_horas " +
                    "else (m.hrsai - m.hrmatinal)::time\n" +
                    "end)::text) as tempo_largada,\n" +
                    "um.meta_tracking," +
                    "um.meta_tempo_rota_mapas, " +
                    "um.meta_caixa_viagem,\n" +
                    "um.meta_dev_hl, " +
                    "um.meta_dev_pdv, " +
                    "um.meta_dev_nf, " +
                    "um.meta_dispersao_km, " +
                    "um.meta_dispersao_tempo, " +
                    "um.meta_jornada_liquida_mapas, " +
                    "um.meta_raio_tracking, " +
                    "um.meta_tempo_interno_mapas, " +
                    "um.meta_tempo_largada_mapas," +
                    "to_seconds(um.meta_tempo_rota_horas::text) as meta_tempo_rota_horas, " +
                    "to_seconds(um.meta_tempo_interno_horas::text) as meta_tempo_interno_horas, " +
                    "to_seconds(um.meta_tempo_largada_horas::text) as meta_tempo_largada_horas,\n" +
                    "to_seconds(um.meta_jornada_liquida_horas::text) as meta_jornada_liquida_horas \n";
    private static final String TAG = IndicadorDaoImpl.class.getSimpleName();
    private static final String BUSCA_EXTRATO_INDICADORES = "SELECT DISTINCT\n" +
            " M.DATA,  M.mapa, M.PLACA, E.nome as equipe,\n" +
            " M.cxcarreg, M.QTHLCARREGADOS,  M.QTHLENTREGUES, M.QTNFCARREGADAS, M.QTNFENTREGUES,  M" +
            ".entregascompletas,  M.entregasnaorealizadas, m.entregasparciais, M.kmprevistoroad, M.kmsai, M.kmentr, " +
            "extract(epoch from (M.tempoprevistoroad)) as tempoprevistoroad,\n" +
            "M.HRSAI,  M.HRENTR, extract(epoch from ((M.hrentr - M.hrsai))) AS TEMPO_ROTA,  extract(epoch from (M" +
            ".TEMPOINTERNO)) as tempointerno,  M.HRMATINAL,  TRACKING.TOTAL_APONTAMENTOS AS TOTAL_TRACKING,  TRACKING" +
            ".APONTAMENTOS_OK, extract(epoch from (case when m.hrsai::time < m.hrmatinal then um" +
            ".meta_tempo_largada_horas else (m.hrsai - m.hrmatinal)::time end)) as tempo_largada,\n" +
            "um.meta_tracking,um.meta_tempo_rota_mapas, um.meta_caixa_viagem,\n" +
            "um.meta_dev_hl, um.meta_dev_pdv, um.meta_dev_nf, um.meta_dispersao_km, um.meta_dispersao_tempo, um" +
            ".meta_jornada_liquida_mapas, um.meta_raio_tracking, um.meta_tempo_interno_mapas, um" +
            ".meta_tempo_largada_mapas,to_seconds(um.meta_tempo_rota_horas::text) as meta_tempo_rota_horas, " +
            "to_seconds" +
            "(um.meta_tempo_interno_horas::text) as meta_tempo_interno_horas, to_seconds(um" +
            ".meta_tempo_largada_horas::text) as meta_tempo_largada_horas,\n" +
            "to_seconds(um.meta_jornada_liquida_horas::text) as meta_jornada_liquida_horas\n" +
            "FROM\n" +
            "VIEW_MAPA_COLABORADOR VMC\n" +
            "JOIN COLABORADOR C ON C.CPF = VMC.cpf AND C.COD_UNIDADE = VMC.cod_unidade\n" +
            "JOIN MAPA M ON M.MAPA = VMC.MAPA AND M.COD_UNIDADE = VMC.cod_unidade\n" +
            "JOIN UNIDADE U ON U.CODIGO = M.cod_unidade\n" +
            "JOIN EMPRESA EM ON EM.codigo = U.cod_empresa\n" +
            "JOIN regional R ON R.codigo = U.cod_regional\n" +
            "JOIN unidade_metas um on um.cod_unidade = u.codigo\n" +
            "JOIN equipe E ON E.cod_unidade = c.cod_unidade AND C.cod_equipe = E.codigo\n" +
            "LEFT JOIN (SELECT T.MAPA AS TRACKING_MAPA, T.cod_unidade TRACKING_UNIDADE, COUNT(T" +
            ".disp_apont_cadastrado) AS TOTAL_APONTAMENTOS,\n" +
            "  SUM(CASE WHEN T.disp_apont_cadastrado <= UM.meta_raio_tracking THEN 1\n" +
            "      ELSE 0 END) AS APONTAMENTOS_OK\n" +
            "FROM TRACKING T JOIN UNIDADE_METAS UM ON UM.COD_UNIDADE = T.cod_unidade\n" +
            "GROUP BY 1,2) AS TRACKING ON TRACKING_MAPA = M.MAPA AND TRACKING_UNIDADE = M.cod_unidade\n" +
            "  where\n" +
            "VMC.CPF::TEXT LIKE ? and\n" +
            "DATA BETWEEN ? AND ? AND\n" +
            "R.codigo::TEXT LIKE ? AND\n" +
            "U.codigo::TEXT LIKE ? AND\n" +
            "E.nome::TEXT LIKE ? AND\n" +
            "EM.codigo::TEXT LIKE ?\n" +
            "ORDER BY M.DATA;";

    public IndicadorDaoImpl() {

    }

    @Override
    public List<IndicadorAcumulado> getAcumuladoIndicadoresIndividual(@NotNull final Long cpf,
                                                                      @NotNull final LocalDate dataInicial,
                                                                      @NotNull final LocalDate dataFinal)
            throws SQLException {
        Connection conn = null;
        ResultSet rSet = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("select * from func_indicador_get_acumulado_individual(" +
                                                 "f_cpf => ?," +
                                                 "f_data_inicial => ?," +
                                                 "f_data_final => ?)");
            stmt.setLong(1, cpf);
            stmt.setObject(2, dataInicial);
            stmt.setObject(3, dataFinal);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return createAcumulados(rSet);
            } else {
                return Collections.emptyList();
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public List<Indicador> getExtratoIndicador(final Long dataInicial,
                                               final Long dataFinal,
                                               final String codRegional,
                                               final String codEmpresa,
                                               final String codUnidade,
                                               final String equipe,
                                               final String cpf,
                                               final String indicador) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        List<Indicador> itens;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(BUSCA_EXTRATO_INDICADORES);
            stmt.setString(1, cpf);
            stmt.setDate(2, DateUtils.toSqlDate(new Date(dataInicial)));
            stmt.setDate(3, DateUtils.toSqlDate(new Date(dataFinal)));
            stmt.setString(4, codRegional);
            stmt.setString(5, codUnidade);
            stmt.setString(6, equipe);
            stmt.setString(7, codEmpresa);
            Log.d(TAG, stmt.toString());
            rSet = stmt.executeQuery();
            itens = createExtratoIndicador(rSet, indicador);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return itens;
    }

    @Override
    public List<IndicadorItem> createExtratoDia(final ResultSet rSet) throws SQLException {
        final List<IndicadorItem> itens = new ArrayList<>();
        itens.add(IndicadorConverter.createDevHl(rSet));
        itens.add(IndicadorConverter.createDevPdv(rSet));
        itens.add(IndicadorConverter.createDevNf(rSet));
        itens.add(IndicadorConverter.createTracking(rSet));
        itens.add(IndicadorConverter.createTempoLargada(rSet));
        itens.add(IndicadorConverter.createTempoRota(rSet));
        itens.add(IndicadorConverter.createTempoInterno(rSet));
        itens.add(IndicadorConverter.createJornada(rSet));
        itens.add(IndicadorConverter.createCaixaViagem(rSet));
        itens.add(IndicadorConverter.createDispersaoKm(rSet));
        itens.add(IndicadorConverter.createDispersaoTempo(rSet));
        return itens;
    }

    @Override
    public List<IndicadorAcumulado> createAcumulados(final ResultSet rSet) throws SQLException {
        final List<IndicadorAcumulado> acumulados = new ArrayList<>();
        acumulados.add(IndicadorConverter.createAcumuladoDevHl(rSet));
        acumulados.add(IndicadorConverter.createAcumuladoDevPdv(rSet));
        acumulados.add(IndicadorConverter.createAcumuladoDevNf(rSet));
        acumulados.add(IndicadorConverter.createAcumuladoTracking(rSet));
        acumulados.add(IndicadorConverter.createAcumuladoTempoLargadaMapas(rSet));
        acumulados.add(IndicadorConverter.createAcumuladoTempoRotaMapas(rSet));
        acumulados.add(IndicadorConverter.createAcumuladoTempoInternoMapas(rSet));
        acumulados.add(IndicadorConverter.createAcumuladoJornadaMapas(rSet));
        acumulados.add(IndicadorConverter.createAcumuladoDispersaoTempoMapas(rSet));
        acumulados.add(IndicadorConverter.createAcumuladoDispersaoKm(rSet));
        acumulados.add(IndicadorConverter.createAcumuladoCaixaViagem(rSet));
        acumulados.add(IndicadorConverter.createAcumuladoTempoLargadaMedia(rSet));
        acumulados.add(IndicadorConverter.createAcumuladoTempoRotaMedia(rSet));
        acumulados.add(IndicadorConverter.createAcumuladoTempoInternoMedia(rSet));
        acumulados.add(IndicadorConverter.createAcumuladoJornadaMedia(rSet));
        acumulados.add(IndicadorConverter.createAcumuladoDispersaoTempoMedia(rSet));
        return acumulados;
    }

    @Override
    public IndicadorAcumulado createAcumuladoIndicador(final ResultSet rSet, final String indicador)
            throws SQLException {
        switch (indicador) {
            case CaixaViagemAcumulado.CAIXA_VIAGEM_ACUMULADO:
                return IndicadorConverter.createAcumuladoCaixaViagem(rSet);
            case DevHlAcumulado.DEV_HL_ACUMULADO:
                return IndicadorConverter.createAcumuladoDevHl(rSet);
            case DevNfAcumulado.DEV_NF_ACUMULADO:
                return IndicadorConverter.createAcumuladoDevNf(rSet);
            case DevPdvAcumulado.DEV_PDV_ACUMULADO:
                return IndicadorConverter.createAcumuladoDevPdv(rSet);
            case DispersaoKmAcumulado.DISPERSAO_KM_ACUMULADO:
                return IndicadorConverter.createAcumuladoDispersaoKm(rSet);
            case TrackingAcumulado.TRACKING_ACUMULADO:
                return IndicadorConverter.createAcumuladoTracking(rSet);
            case DispersaoTempoAcumuladoMapas.DISPERSAO_TEMPO_ACUMULADO_MAPAS:
                return IndicadorConverter.createAcumuladoDispersaoTempoMapas(rSet);
            case DispersaoTempoAcumuladoMedia.DISPERSAO_TEMPO_ACUMULADO_MEDIA:
                return IndicadorConverter.createAcumuladoDispersaoTempoMedia(rSet);
            case JornadaAcumuladoMapas.JORNADA_ACUMULADO_MAPAS:
                return IndicadorConverter.createAcumuladoJornadaMapas(rSet);
            case JornadaAcumuladoMedia.JORNADA_ACUMULADO_MEDIA:
                return IndicadorConverter.createAcumuladoJornadaMedia(rSet);
            case TempoInternoAcumuladoMapas.TEMPO_INTERNO_ACUMULADO_MAPAS:
                return IndicadorConverter.createAcumuladoTempoInternoMapas(rSet);
            case TempoInternoAcumuladoMedia.TEMPO_INTERNO_ACUMULADO_MEDIA:
                return IndicadorConverter.createAcumuladoTempoInternoMedia(rSet);
            case TempoLargadaAcumuladoMapas.TEMPO_LARGADA_ACUMULADO_MAPAS:
                return IndicadorConverter.createAcumuladoTempoLargadaMapas(rSet);
            case TempoLargadaAcumuladoMedia.TEMPO_LARGADA_ACUMULADO_MEDIA:
                return IndicadorConverter.createAcumuladoTempoLargadaMedia(rSet);
            case TempoRotaAcumuladoMapas.TEMPO_ROTA_ACUMULADO_MAPAS:
                return IndicadorConverter.createAcumuladoTempoRotaMapas(rSet);
            case TempoRotaAcumuladoMedia.TEMPO_ROTA_ACUMULADO_MEDIA:
                return IndicadorConverter.createAcumuladoTempoRotaMedia(rSet);
        }
        return null;
    }

    /**
     * Cria os itens para compor o extrato
     *
     * @param rSet      um ResultSet contendo o resultado da busca
     * @param indicador String para indicar qual o indicador que deve ser montado o extrato
     * @return uma lista de Indicador {@link Indicador}
     *
     * @throws SQLException caso não seja possível recuparar alguma coluna do ResultSet
     */
    private List<Indicador> createExtratoIndicador(final ResultSet rSet, final String indicador) throws SQLException {
        switch (indicador) {
            case CaixaViagem.CAIXA_VIAGEM:
                return IndicadorConverter.createExtratoCaixaViagem(rSet);
            case DevHl.DEVOLUCAO_HL:
                return IndicadorConverter.createExtratoDevHl(rSet);
            case DevPdv.DEVOLUCAO_PDV:
                return IndicadorConverter.createExtratoDevPdv(rSet);
            case DispersaoKm.DISPERSAO_KM:
                return IndicadorConverter.createExtratoDispersaoKm(rSet);
            case Tracking.TRACKING:
                return IndicadorConverter.createExtratoTracking(rSet);
            case DispersaoTempo.DISPERSAO_TEMPO:
                return IndicadorConverter.createExtratoDispersaoTempo(rSet);
            case Jornada.JORNADA:
                return IndicadorConverter.createExtratoJornada(rSet);
            case TempoInterno.TEMPO_INTERNO:
                return IndicadorConverter.createExtratoTempoInterno(rSet);
            case TempoLargada.TEMPO_LARGADA:
                return IndicadorConverter.createExtratoTempoLargada(rSet);
            case TempoRota.TEMPO_ROTA:
                return IndicadorConverter.createExtratoTempoRota(rSet);
            case DevNf.DEVOLUCAO_NF:
                return IndicadorConverter.createExtratoDevNf(rSet);
        }
        return Collections.emptyList();
    }
}