package br.com.zalf.prolog.webservice.entrega.metas;

import br.com.zalf.prolog.webservice.commons.util.datetime.TimeUtils;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public final class MetasDaoImpl extends DatabaseConnection implements MetasDao {

    public MetasDaoImpl() {

    }

    @NotNull
    @Override
    public Optional<Metas> getByCodUnidade(@NotNull final Long codUnidade) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM UNIDADE_METAS WHERE COD_UNIDADE = ?");
            stmt.setLong(1, codUnidade);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return MetasConverter.createMetas(rSet);
            } else {
                return Optional.empty();
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public void update(@NotNull final Metas metas, @NotNull final Long codUnidade) throws Throwable {
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
            stmt.setDouble(1, metas.getMetaDevHl());
            stmt.setDouble(2, metas.getMetaDevPdv());
            stmt.setDouble(3, metas.getMetaDevNf());
            stmt.setDouble(4, metas.getMetaTracking());
            stmt.setDouble(5, metas.getMetaRaioTracking() / 1000.0);
            stmt.setDouble(6, metas.getMetaTempoLargadaMapas());
            stmt.setDouble(7, metas.getMetaTempoRotaMapas());
            stmt.setDouble(8, metas.getMetaTempoInternoMapas());
            stmt.setDouble(9, metas.getMetaJornadaLiquidaMapas());
            stmt.setTime(10, TimeUtils.timeFromDuration(metas.getMetaTempoLargadaHoras()));
            stmt.setTime(11, TimeUtils.timeFromDuration(metas.getMetaTempoRotaHoras()));
            stmt.setTime(12, TimeUtils.timeFromDuration(metas.getMetaTempoInternoHoras()));
            stmt.setTime(13, TimeUtils.timeFromDuration(metas.getMetaJornadaLiquidaHoras()));
            stmt.setInt(14, metas.getMetaCaixaViagem());
            stmt.setDouble(15, metas.getMetaDispersaoKm());
            stmt.setDouble(16, metas.getMetaDispersaoTempo());
            stmt.setLong(17, codUnidade);
            final int count = stmt.executeUpdate();
            if (count == 0) {
                throw new SQLException("Erro ao atualizar as metas");
            }
        } finally {
            close(conn, stmt);
        }
    }
}