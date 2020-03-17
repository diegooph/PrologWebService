package br.com.zalf.prolog.webservice.gente.controlejornada.OLD;

import br.com.zalf.prolog.webservice.TimeZoneManager;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.commons.FonteDataHora;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.gente.controlejornada.ControleJornadaDaoImpl;
import br.com.zalf.prolog.webservice.gente.controlejornada.model.DadosMarcacaoUnidade;
import br.com.zalf.prolog.webservice.gente.controlejornada.model.Intervalo;
import br.com.zalf.prolog.webservice.gente.controlejornada.model.Localizacao;
import br.com.zalf.prolog.webservice.gente.controlejornada.tipomarcacao.TipoMarcacao;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Zart on 18/08/2017.
 */
@Deprecated
public class DeprecatedControleIntervaloDaoImpl_1 extends DatabaseConnection implements DeprecatedControleIntervaloDao_1 {

    private static final String TAG = DeprecatedControleIntervaloDaoImpl_1.class.getSimpleName();

    @Override
    public Intervalo getIntervaloAberto(Long cpf, TipoMarcacao tipoInvervalo) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
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
                return createIntervaloAberto(rSet, conn);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return null;
    }

    @Override
    public void insertOrUpdateIntervalo(Intervalo intervalo) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            if (intervalo.getDataHoraInicio() != null && intervalo.getFonteDataHoraFim() != null) {
                // Intervalo completo, apenas inserir.
                insertIntervalo(intervalo);
            } else if (intervalo.getDataHoraInicio() == null && intervalo.getFonteDataHoraFim() != null) {
                // Intervalo veio apenas com data de finalização, verificar se existe um em aberto para fazer o update,
                // caso não tenha, inserir a finalização avulsa.
                final Intervalo intervaloEmAberto = getIntervaloAberto(
                        intervalo.getColaborador().getCpf(),
                        intervalo.getTipo());
                if (intervaloEmAberto != null) {
                    intervaloEmAberto.setDataHoraFim(intervalo.getDataHoraFim());
                    intervaloEmAberto.setFonteDataHoraFim(intervalo.getFonteDataHoraFim());
                    intervaloEmAberto.setJustificativaEstouro(intervalo.getJustificativaEstouro());
                    intervaloEmAberto.setJustificativaTempoRecomendado(intervalo.getJustificativaTempoRecomendado());
                    intervaloEmAberto.setLocalizacaoFim(intervalo.getLocalizacaoFim());
                    updateIntervalo(intervaloEmAberto);
                } else {
                    insertIntervalo(intervalo);
                }
            } else {
                // Intervalo veio apenas com data_hora de início, inserir na tabela.
                insertIntervalo(intervalo);
            }
        } finally {
            closeConnection(conn, null, null);
        }
    }

    @Override
    public void insertIntervalo(Intervalo intervalo) throws SQLException {
        PreparedStatement stmt = null;
        Connection conn = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("INSERT INTO INTERVALO(COD_UNIDADE, COD_TIPO_INTERVALO, CPF_COLABORADOR," +
                    "FONTE_DATA_HORA_INICIO, DATA_HORA_INICIO, FONTE_DATA_HORA_FIM, DATA_HORA_FIM, JUSTIFICATIVA_ESTOURO, " +
                    " JUSTIFICATIVA_TEMPO_RECOMENDADO, LATITUDE_INICIO, LATITUDE_FIM, LONGITUDE_INICIO, LONGITUDE_FIM) \n" +
                    "    VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)");
            final Long codUnidade = intervalo.getColaborador().getUnidade().getCodigo();
            final ZoneId zoneId = TimeZoneManager.getZoneIdForCodUnidade(codUnidade, conn);
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, intervalo.getTipo().getCodigo());
            stmt.setLong(3, intervalo.getColaborador().getCpf());
            if (intervalo.getDataHoraInicio() != null) {
                stmt.setString(4, intervalo.getFonteDataHoraInicio().asString());
                stmt.setObject(5, intervalo.getDataHoraInicio().atZone(zoneId).toOffsetDateTime());
            } else {
                stmt.setNull(4, Types.VARCHAR);
                stmt.setNull(5, Types.TIMESTAMP_WITH_TIMEZONE);
            }
            if (intervalo.getDataHoraFim() != null) {
                stmt.setString(6, intervalo.getFonteDataHoraFim().asString());
                stmt.setObject(7, intervalo.getDataHoraFim().atZone(zoneId).toOffsetDateTime());
            } else {
                stmt.setNull(6, Types.VARCHAR);
                stmt.setNull(7, Types.TIMESTAMP_WITH_TIMEZONE);
            }
            stmt.setString(8, intervalo.getJustificativaEstouro());
            stmt.setString(9, intervalo.getJustificativaTempoRecomendado());

            final Localizacao localizacaoInicio = intervalo.getLocalizacaoInicio();
            if (localizacaoInicio != null) {
                stmt.setString(10, localizacaoInicio.getLatitude());
                stmt.setString(12, localizacaoInicio.getLongitude());
            } else {
                stmt.setNull(10, Types.VARCHAR);
                stmt.setNull(12, Types.VARCHAR);
            }
            final Localizacao localizacaoFim = intervalo.getLocalizacaoFim();
            if (localizacaoFim != null) {
                stmt.setString(11, localizacaoFim.getLatitude());
                stmt.setString(13, localizacaoFim.getLongitude());
            } else {
                stmt.setNull(11, Types.VARCHAR);
                stmt.setNull(13, Types.VARCHAR);
            }
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Erro ao inserir o intervalo");
            }
        } finally {
            closeConnection(conn, stmt, null);
        }
    }

    @Override
    public void updateIntervalo(Intervalo intervalo) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("UPDATE INTERVALO SET FONTE_DATA_HORA_INICIO = ?, DATA_HORA_INICIO = ?, " +
                    " FONTE_DATA_HORA_FIM = ?, DATA_HORA_FIM = ?, JUSTIFICATIVA_ESTOURO = ?, JUSTIFICATIVA_TEMPO_RECOMENDADO = ?, " +
                    "LATITUDE_INICIO = ?, LONGITUDE_INICIO = ?, LATITUDE_FIM = ?, LONGITUDE_FIM = ? " +
                    "WHERE CPF_COLABORADOR = ? AND CODIGO = ?;");
            final Long cpf = intervalo.getColaborador().getCpf();
            final ZoneId zoneId = TimeZoneManager.getZoneIdForCpf(cpf, conn);
            stmt.setString(1, intervalo.getFonteDataHoraInicio().asString());
            stmt.setObject(2, intervalo.getDataHoraInicio().atZone(zoneId).toOffsetDateTime());
            stmt.setString(3, intervalo.getFonteDataHoraFim().asString());
            stmt.setObject(4, intervalo.getDataHoraFim().atZone(zoneId).toOffsetDateTime());
            stmt.setString(5, intervalo.getJustificativaEstouro());
            stmt.setString(6, intervalo.getJustificativaTempoRecomendado());

            final Localizacao localizacaoInicio = intervalo.getLocalizacaoInicio();
            if (localizacaoInicio != null) {
                stmt.setString(7, localizacaoInicio.getLatitude());
                stmt.setString(8, localizacaoInicio.getLongitude());
            } else {
                stmt.setNull(7, Types.VARCHAR);
                stmt.setNull(8, Types.VARCHAR);
            }
            final Localizacao localizacaoFim = intervalo.getLocalizacaoFim();
            if (localizacaoFim != null) {
                stmt.setString(9, localizacaoFim.getLatitude());
                stmt.setString(10, localizacaoFim.getLongitude());
            } else {
                stmt.setNull(9, Types.VARCHAR);
                stmt.setNull(10, Types.VARCHAR);
            }

            stmt.setLong(11, cpf);
            stmt.setLong(12, intervalo.getCodigo());
            int count = stmt.executeUpdate();
            if (count == 0) {
                throw new SQLException("Erro ao finalizar o intervalo");
            }
        } finally {
            closeConnection(conn, stmt, null);
        }
    }

    @Override
    public List<Intervalo> getIntervalosColaborador(Long cpf, String codTipo, long limit, long offset) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        List<Intervalo> intervalos = new ArrayList<>();
        try {
//            conn = getConnection();
//            stmt = conn.prepareStatement("SELECT i.codigo as cod_intervalo, it.CODIGO as codigo_tipo_intervalo, i.DATA_HORA_INICIO, i.DATA_HORA_FIM,\n" +
//                    "i.JUSTIFICATIVA_ESTOURO, i.VALIDO, it.nome as nome_tipo_intervalo, it.ICONE, it.TEMPO_RECOMENDADO_MINUTOS, it.TEMPO_ESTOURO_MINUTOS,\n" +
//                    "it.HORARIO_SUGERIDO,i.cod_unidade, it.ativo, ULTIMA_ABERTURA.*,\n" +
//                    "  CASE WHEN I.DATA_HORA_FIM IS NULL AND I.DATA_HORA_INICIO = ULTIMA_ABERTURA.ULTIMO_INICIO THEN\n" +
//                    "  EXTRACT(EPOCH FROM now() - i.DATA_HORA_INICIO)\n" +
//                    "  WHEN I.DATA_HORA_INICIO IS NULL THEN NULL\n" +
//                    "  WHEN I.DATA_HORA_FIM IS NULL AND I.DATA_HORA_INICIO <> ULTIMA_ABERTURA.ULTIMO_INICIO THEN NULL\n" +
//                    "    ELSE EXTRACT(EPOCH FROM I.DATA_HORA_FIM - I.DATA_HORA_INICIO) END  AS TEMPO_DECORRIDO\n" +
//                    "FROM\n" +
//                    "  INTERVALO I JOIN INTERVALO_TIPO IT ON IT.COD_UNIDADE = I.COD_UNIDADE AND IT.CODIGO = I.COD_TIPO_INTERVALO\n" +
//                    "  JOIN (SELECT COD_UNIDADE, COD_TIPO_INTERVALO AS COD_TIPO_ULTIMO_INICIO, MAX(DATA_HORA_INICIO) AS ULTIMO_INICIO FROM INTERVALO WHERE CPF_COLABORADOR = ? \n" +
//                    "GROUP BY 1,2) AS ULTIMA_ABERTURA ON ULTIMA_ABERTURA.COD_UNIDADE = I.COD_UNIDADE AND ULTIMA_ABERTURA.COD_TIPO_ULTIMO_INICIO = I.COD_TIPO_INTERVALO\n" +
//                    "WHERE I.CPF_COLABORADOR = ? and i.cod_tipo_intervalo::text like ?\n" +
//                    "ORDER BY cod_intervalo DESC " +
//                    "LIMIT ? OFFSET ?;");
//            stmt.setLong(1, cpf);
//            stmt.setLong(2, cpf);
//            stmt.setString(3, codTipo);
//            stmt.setLong(4, limit);
//            stmt.setLong(5, offset);
//            rSet = stmt.executeQuery();
//            while (rSet.next()){
//                intervalos.add(createIntervalo(rSet, conn));
//            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return intervalos;
    }

    @Override
    @NotNull
    public Optional<Long> getVersaoDadosIntervaloByUnidade(@NotNull final Long codUnidade) throws SQLException {
        final Optional<DadosMarcacaoUnidade> versaoDados =
                new ControleJornadaDaoImpl().getDadosMarcacaoUnidade(codUnidade);
        return versaoDados.map(DadosMarcacaoUnidade::getVersaoDadosBanco);
    }

    @Deprecated
    @Override
    public Long iniciaIntervalo(Long codUnidade, Long cpf, Long codTipo) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            final Intervalo intervalo = new Intervalo();
            final TipoMarcacao tipoIntervalo = new TipoMarcacao();
            tipoIntervalo.setCodigo(codTipo);
            final Colaborador colaborador = new Colaborador();
            colaborador.setCpf(cpf);
            intervalo.setTipo(tipoIntervalo);
            intervalo.setColaborador(colaborador);
            intervalo.setDataHoraInicio(LocalDateTime.now(TimeZoneManager.getZoneIdForCodUnidade(codUnidade, conn)));
            intervalo.setFonteDataHoraInicio(FonteDataHora.SERVIDOR);
            return insertIntervalo(intervalo, codUnidade, conn);
        } finally {
            closeConnection(conn, null, null);
        }
    }

    @Deprecated
    @Override
    public boolean insereFinalizacaoIntervalo (Intervalo intervalo, Long codUnidade) throws SQLException{
        Connection conn = null;
        try {
            conn = getConnection();
            // Seta fontes por questão de compatibilidade.
            intervalo.setFonteDataHoraInicio(FonteDataHora.SERVIDOR);
            intervalo.setFonteDataHoraFim(FonteDataHora.SERVIDOR);
            final Intervalo intervaloEmAberto = getIntervaloAberto(intervalo.getColaborador().getCpf(), intervalo.getTipo());
            if (intervaloEmAberto != null) {
                if(intervalo.getCodigo().equals(intervaloEmAberto.getCodigo())){
                    return finalizaIntervaloEmAberto(intervalo);
                }
            } else {
                intervalo.setDataHoraInicio(null);
                intervalo.setDataHoraFim(LocalDateTime.now(TimeZoneManager.getZoneIdForCodUnidade(codUnidade, conn)));
                Long codigo = insertIntervalo(intervalo, codUnidade, conn);
                if (codigo != null) {
                    return true;
                }
            }
        } finally {
            closeConnection(conn, null, null);
        }
        return false;
    }

    @Deprecated
    @Override
    public boolean finalizaIntervaloEmAberto(Intervalo intervalo) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
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
            closeConnection(conn, stmt, null);
        }
        return true;
    }

    @Deprecated
    private Long insertIntervalo(Intervalo intervalo, Long codUnidade, Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
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
            closeConnection(conn, stmt, rSet);
        }
        return null;
    }

    private Intervalo createIntervaloAberto(ResultSet rSet, Connection conn) throws SQLException {
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