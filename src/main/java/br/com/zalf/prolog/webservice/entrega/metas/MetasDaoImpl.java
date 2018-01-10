package br.com.zalf.prolog.webservice.entrega.metas;

import br.com.zalf.prolog.webservice.commons.util.TimeUtils;
import br.com.zalf.prolog.webservice.DatabaseConnection;

import java.sql.*;
import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class MetasDaoImpl extends DatabaseConnection implements MetasDao {

    public MetasDaoImpl() {

    }

    @Override
    public Metas getByCodUnidade(Long codUnidade) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM UNIDADE_METAS WHERE COD_UNIDADE = ?");
            stmt.setLong(1, codUnidade);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return createMeta(rSet);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return null;
    }

    @Override
    public boolean update(Metas metas, Long codUnidade) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("UPDATE UNIDADE_METAS SET " +
                    "  META_DEV_HL = ?," +
                    "  META_DEV_PDV = ?," +
                    "  META_DEV_NF = ?," +
                    "  META_TRACKING = ?," +
                    "  META_RAIO_TRACKING = ?," +
                    "  META_TEMPO_LARGADA_MAPAS = ?," +
                    "  META_TEMPO_ROTA_MAPAS = ?," +
                    "  META_TEMPO_INTERNO_MAPAS = ?," +
                    "  META_JORNADA_LIQUIDA_MAPAS = ?," +
                    "  META_TEMPO_LARGADA_HORAS = ?," +
                    "  META_TEMPO_ROTA_HORAS = ?," +
                    "  META_TEMPO_INTERNO_HORAS = ?," +
                    "  META_JORNADA_LIQUIDA_HORAS = ?," +
                    "  META_CAIXA_VIAGEM = ?," +
                    "  META_DISPERSAO_KM = ?," +
                    "  META_DISPERSAO_TEMPO = ? WHERE COD_UNIDADE = ?");
            stmt.setDouble(1, metas.metaDevHl);
            stmt.setDouble(2, metas.metaDevPdv);
            stmt.setDouble(3, metas.metaDevNf);
            stmt.setDouble(4, metas.metaTracking);
            stmt.setDouble(5, metas.metaRaioTracking/1000.0);
            stmt.setDouble(6, metas.metaTempoLargadaMapas);
            stmt.setDouble(7, metas.metaTempoRotaMapas);
            stmt.setDouble(8, metas.metaTempoInternoMapas);
            stmt.setDouble(9, metas.metaJornadaLiquidaMapas);
            stmt.setTime(10, ofDuration(metas.metaTempoLargadaHoras));
            stmt.setTime(11, ofDuration(metas.metaTempoRotaHoras));
            stmt.setTime(12, ofDuration(metas.metaTempoInternoHoras));
            stmt.setTime(13, ofDuration(metas.metaJornadaLiquidaHoras));
            stmt.setInt(14, metas.metaCaixaViagem);
            stmt.setDouble(15, metas.metaDispersaoKm);
            stmt.setDouble(16, metas.metaDispersaoTempo);
            stmt.setLong(17, codUnidade);
            int count = stmt.executeUpdate();
            if (count == 0) {
                throw new SQLException("Erro ao atualizar as metas");
            }
        } finally {
            closeConnection(conn, stmt, null);
        }
        return true;
    }

    private Metas createMeta(ResultSet rSet) throws SQLException {
        Metas meta = new Metas();
        meta.metaDevHl = rSet.getDouble("META_DEV_HL");
        meta.metaDevPdv = rSet.getDouble("META_DEV_PDV");
        meta.metaTracking = rSet.getDouble("META_TRACKING");
        meta.metaRaioTracking = (int) rSet.getDouble("META_RAIO_TRACKING")*1000;
        meta.metaTempoLargadaMapas = rSet.getDouble("META_TEMPO_LARGADA_MAPAS");
        meta.metaTempoRotaMapas = rSet.getDouble("META_TEMPO_ROTA_MAPAS");
        meta.metaTempoInternoMapas = rSet.getDouble("META_TEMPO_INTERNO_MAPAS");
        meta.metaJornadaLiquidaMapas = rSet.getDouble("META_JORNADA_LIQUIDA_MAPAS");
        meta.metaTempoLargadaHoras = ofTime(rSet.getTime("META_TEMPO_LARGADA_HORAS"));
        meta.metaTempoRotaHoras = ofTime(rSet.getTime("META_TEMPO_ROTA_HORAS"));
        meta.metaTempoInternoHoras = ofTime(rSet.getTime("META_TEMPO_INTERNO_HORAS"));
        meta.metaJornadaLiquidaHoras = ofTime(rSet.getTime("META_JORNADA_LIQUIDA_HORAS"));
        meta.metaCaixaViagem = rSet.getInt("META_CAIXA_VIAGEM");
        meta.metaDispersaoKm = rSet.getDouble("META_DISPERSAO_KM");
        meta.metaDispersaoTempo = rSet.getDouble("META_DISPERSAO_TEMPO");
        meta.metaDevNf = rSet.getDouble("META_DEV_NF");
        return meta;
    }

    private Time ofDuration(Duration duration) {
        long segundos = Math.abs(duration.getSeconds());
        return TimeUtils.toSqlTime(LocalTime.ofSecondOfDay(segundos));
    }


    private Duration ofTime(Time time) {
        LocalTime localTime = TimeUtils.toLocalTime(time);
        long segundos = localTime.getSecond() + (localTime.getMinute() * 60) + (localTime.getHour() * 60 * 60);
        return Duration.of(segundos, ChronoUnit.SECONDS);
    }
}