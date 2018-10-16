package br.com.zalf.prolog.webservice.gente.controleintervalo;

import br.com.zalf.prolog.webservice.TimeZoneManager;
import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.FonteDataHora;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.Intervalo;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.Localizacao;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.TipoMarcacao;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.time.*;
import java.time.temporal.ChronoUnit;

/**
 * Created by Zart on 18/08/2017.
 */
@Deprecated
public class DeprecatedControleIntervaloDaoImpl extends DatabaseConnection implements DeprecatedControleIntervaloDao {

    @Deprecated
    @Override
    public Long iniciaIntervalo(@NotNull final Long codUnidade,
                                @NotNull final Long cpf,
                                @NotNull final Long codTipoIntervalo) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            final Intervalo intervalo = new Intervalo();
            final TipoMarcacao tipoIntervalo = new TipoMarcacao();
            tipoIntervalo.setCodigo(codTipoIntervalo);
            final Colaborador colaborador = new Colaborador();
            colaborador.setCpf(cpf);
            intervalo.setTipo(tipoIntervalo);
            intervalo.setColaborador(colaborador);
            intervalo.setDataHoraInicio(LocalDateTime.now(TimeZoneManager.getZoneIdForCodUnidade(codUnidade, conn)));
            intervalo.setFonteDataHoraInicio(FonteDataHora.SERVIDOR);
            return insertIntervalo(conn, codUnidade, intervalo);
        } finally {
            close(conn);
        }
    }

    @Deprecated
    @Override
    public boolean insereFinalizacaoIntervalo (@NotNull final Long codUnidade,
                                               @NotNull final Intervalo intervalo) throws SQLException{
        Connection conn = null;
        try {
            conn = getConnection();
            // Seta fontes por questão de compatibilidade.
            intervalo.setFonteDataHoraInicio(FonteDataHora.SERVIDOR);
            intervalo.setFonteDataHoraFim(FonteDataHora.SERVIDOR);
            final Intervalo intervaloEmAberto =
                    getIntervaloAberto(conn, intervalo.getColaborador().getCpf(), intervalo.getTipo());
            if (intervaloEmAberto != null) {
                if(intervalo.getCodigo().equals(intervaloEmAberto.getCodigo())){
                    return finalizaIntervaloEmAberto(conn, intervalo);
                }
            } else {
                intervalo.setDataHoraInicio(null);
                intervalo.setDataHoraFim(LocalDateTime.now(TimeZoneManager.getZoneIdForCodUnidade(codUnidade, conn)));
                final Long codigo = insertIntervalo(conn, codUnidade, intervalo);
                if (codigo != null) {
                    return true;
                }
            }
        } finally {
            close(conn);
        }
        return false;
    }

    @Nullable
    private Long insertIntervalo(@NotNull final Connection conn,
                                 @NotNull final Long codUnidade,
                                 @NotNull final Intervalo intervalo) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO INTERVALO(COD_UNIDADE, COD_TIPO_INTERVALO, CPF_COLABORADOR, " +
                    "DATA_HORA_INICIO, FONTE_DATA_HORA_INICIO, DATA_HORA_FIM, FONTE_DATA_HORA_FIM, " +
                    "LATITUDE_INICIO, LATITUDE_FIM, LONGITUDE_INICIO, LONGITUDE_FIM) VALUES (?,?,?,?,?,?,?,?,?,?,?) " +
                    "RETURNING CODIGO;");
            final ZoneId zoneId = TimeZoneManager.getZoneIdForCodUnidade(codUnidade, conn);
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, intervalo.getTipo().getCodigo());
            stmt.setLong(3, intervalo.getColaborador().getCpf());
            if (intervalo.getDataHoraInicio() != null) {
                stmt.setObject(4, intervalo.getDataHoraInicio().atZone(zoneId).toOffsetDateTime());
                stmt.setString(5, intervalo.getFonteDataHoraInicio().asString());
            } else {
                stmt.setNull(4, Types.TIMESTAMP_WITH_TIMEZONE);
                stmt.setNull(5, Types.VARCHAR);
            }
            if (intervalo.getDataHoraFim() != null) {
                stmt.setObject(6, intervalo.getDataHoraFim().atZone(zoneId).toOffsetDateTime());
                stmt.setString(7, intervalo.getFonteDataHoraInicio().asString());
            } else {
                stmt.setNull(6, Types.TIMESTAMP_WITH_TIMEZONE);
                stmt.setNull(7, Types.VARCHAR);
            }
            final Localizacao localizacaoInicio = intervalo.getLocalizacaoInicio();
            final Localizacao localizacaoFim = intervalo.getLocalizacaoFim();
            stmt.setString(8, localizacaoInicio.getLatitude());
            stmt.setString(9, localizacaoFim.getLatitude());
            stmt.setString(10, localizacaoInicio.getLongitude());
            stmt.setString(11, localizacaoFim.getLongitude());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getLong("CODIGO");
            }
        } finally {
            close(stmt, rSet);
        }
        return null;
    }

    @Nullable
    private Intervalo getIntervaloAberto(@NotNull final Connection conn,
                                         @NotNull final Long cpf,
                                         @NotNull final TipoMarcacao tipoInvervalo) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT " +
                    "I.CPF_COLABORADOR AS CPF_COLABORADOR, " +
                    "I.CODIGO AS CODIGO, " +
                    "I.DATA_HORA_INICIO AT TIME ZONE ? AS DATA_HORA_INICIO, " +
                    "I.VALIDO AS VALIDO, " +
                    "I.COD_TIPO_INTERVALO AS COD_TIPO_INTERVALO, " +
                    "I.LATITUDE_INICIO AS LATITUDE_INICIO, " +
                    "I.LONGITUDE_INICIO AS LONGITUDE_INICIO, " +
                    "I.FONTE_DATA_HORA_INICIO AS FONTE_DATA_HORA_INICIO " +
                    "FROM INTERVALO I " +
                    "WHERE I.CPF_COLABORADOR = ? AND I.COD_TIPO_INTERVALO = ? AND I.COD_UNIDADE = (SELECT COD_UNIDADE " +
                    "                                                                                     FROM COLABORADOR " +
                    "                                                                                     WHERE CPF = ?) AND " +
                    "      DATA_HORA_FIM IS NULL " +
                    "      AND DATA_HORA_INICIO >= (SELECT MAX(DATA_HORA_INICIO) " +
                    "                               FROM INTERVALO I " +
                    "                               WHERE I.CPF_COLABORADOR = ? " +
                    "                                     AND I.COD_TIPO_INTERVALO = ? AND I.COD_UNIDADE = (SELECT COD_UNIDADE " +
                    "                                                                                       FROM COLABORADOR " +
                    "                                                                                       WHERE CPF = ?));");
            stmt.setString(1, TimeZoneManager.getZoneIdForCpf(cpf, conn).getId());
            stmt.setLong(2, cpf);
            stmt.setLong(3, tipoInvervalo.getCodigo());
            stmt.setLong(4, cpf);
            stmt.setLong(5, cpf);
            stmt.setLong(6, tipoInvervalo.getCodigo());
            stmt.setLong(7, cpf);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return createIntervaloAberto(conn, rSet);
            }
        } finally {
            close(stmt, rSet);
        }
        return null;
    }

    private boolean finalizaIntervaloEmAberto(@NotNull final Connection conn,
                                              @NotNull final Intervalo intervalo) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("UPDATE INTERVALO SET DATA_HORA_FIM = ?, FONTE_DATA_HORA_FIM = ?, " +
                    "JUSTIFICATIVA_ESTOURO = ?, LATITUDE_FIM = ?, LONGITUDE_FIM = ? WHERE CPF_COLABORADOR = ? AND CODIGO = ?;");
            stmt.setObject(1, OffsetDateTime.now(Clock.systemUTC()));
            stmt.setString(2, intervalo.getFonteDataHoraFim().asString());
            stmt.setString(3, intervalo.getJustificativaEstouro());
            final Localizacao localizacaoFim = intervalo.getLocalizacaoFim();
            stmt.setString(4, localizacaoFim.getLatitude());
            stmt.setString(5, localizacaoFim.getLongitude());
            stmt.setLong(6, intervalo.getColaborador().getCpf());
            stmt.setLong(7, intervalo.getCodigo());
            int count = stmt.executeUpdate();
            if (count == 0) {
                throw new SQLException("Erro ao finalizar o intervalo");
            }
        } finally {
            close(stmt);
        }
        return true;
    }

    @NotNull
    private Intervalo createIntervaloAberto(@NotNull final Connection conn,
                                            @NotNull final ResultSet rSet) throws SQLException {
        final Colaborador colaborador = new Colaborador();
        final Long cpf = rSet.getLong("CPF_COLABORADOR");
        colaborador.setCpf(cpf);
        final Intervalo intervalo = new Intervalo();
        intervalo.setCodigo(rSet.getLong("CODIGO"));
        intervalo.setDataHoraInicio(rSet.getObject("DATA_HORA_INICIO", LocalDateTime.class));
        intervalo.setValido(rSet.getBoolean("VALIDO"));
        final LocalDateTime dataAtual = LocalDateTime.now(TimeZoneManager.getZoneIdForCpf(cpf, conn));
        intervalo.setTempoDecorrido(Duration.ofMillis(ChronoUnit.MILLIS.between(dataAtual, intervalo.getDataHoraInicio())));
        final TipoMarcacao tipoIntervalo = new TipoMarcacao();
        tipoIntervalo.setCodigo(rSet.getLong("COD_TIPO_INTERVALO"));
        intervalo.setTipo(tipoIntervalo);
        intervalo.setColaborador(colaborador);
        final String latitudeInicio = rSet.getString("LATITUDE_INICIO");
        if (!rSet.wasNull()) {
            final Localizacao localizacaoInicio = new Localizacao();
            localizacaoInicio.setLatitude(latitudeInicio);
            localizacaoInicio.setLongitude(rSet.getString("LONGITUDE_INICIO"));
            intervalo.setLocalizacaoInicio(localizacaoInicio);
        }
        String fonteDataHoraInicio = rSet.getString("FONTE_DATA_HORA_INICIO");
        // Setar apenas a fonte do inicio, sendo que não tem como um intervalo em aberto vir com fonte de término
        if (fonteDataHoraInicio != null) {
            intervalo.setFonteDataHoraInicio(FonteDataHora.fromString(fonteDataHoraInicio));
        }
        return intervalo;
    }
}