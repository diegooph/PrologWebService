package br.com.zalf.prolog.webservice.gente.controlejornada.OLD;

import br.com.zalf.prolog.webservice.TimeZoneManager;
import br.com.zalf.prolog.webservice.commons.FonteDataHora;
import br.com.zalf.prolog.webservice.commons.util.datetime.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.gente.controlejornada.model.IntervaloMarcacao;
import br.com.zalf.prolog.webservice.gente.controlejornada.model.Localizacao;
import br.com.zalf.prolog.webservice.gente.controlejornada.model.TipoInicioFim;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Created on 08/03/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Deprecated
public final class DeprecatedControleIntervaloDaoImpl_2 extends DatabaseConnection implements DeprecatedControleIntervaloDao_2 {

    public DeprecatedControleIntervaloDaoImpl_2() {

    }

    @Override
    public void insertMarcacaoIntervalo(@NotNull final IntervaloMarcacao intervaloMarcacao) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            // Se a marcação já existir, nós não tentamos inserir novamente e simplemente não fazemos nada. Isso garante
            // um retorno OK para o app e assim a marcação será colocada como sincronizada. É importante tratar esse
            // cenário pois o app pode tentar sincronizar uma marcação, ela ser inserida com sucesso, mas a conexão
            // com o servidor se perder nesse meio tempo, aí o app acha, erroneamente, que a marcação ainda não foi
            // sincronizada.
            if (!marcacaoIntervaloJaExiste(intervaloMarcacao, conn)) {
                internalInsertMarcacaoIntervalo(intervaloMarcacao, conn);
            }
        } finally {
            closeConnection(conn, null, null);
        }
    }

    @Nullable
    @Override
    public IntervaloMarcacao getUltimaMarcacaoInicioNaoFechada(@NotNull final Long codUnidade,
                                                               @NotNull final Long cpf,
                                                               @NotNull final Long codTipoIntervalo)
            throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT " +
                    "  I.CODIGO                          AS CODIGO, " +
                    "  I.CODIGO_MARCACAO_POR_UNIDADE     AS COD_MARCACAO_POR_UNIDADE, " +
                    "  I.COD_UNIDADE                     AS COD_UNIDADE, " +
                    "  I.COD_TIPO_INTERVALO              AS COD_TIPO_INTERVALO, " +
                    "  I.CPF_COLABORADOR                 AS CPF_COLABORADOR, " +
                    "  C.DATA_NASCIMENTO                 AS DATA_NASCIMENTO_COLABORADOR, " +
                    "  I.DATA_HORA AT TIME ZONE ?        AS DATA_HORA, " +
                    "  I.TIPO_MARCACAO                   AS TIPO_MARCACAO, " +
                    "  I.FONTE_DATA_HORA                 AS FONTE_DATA_HORA, " +
                    "  I.JUSTIFICATIVA_TEMPO_RECOMENDADO AS JUSTIFICATIVA_TEMPO_RECOMENDADO, " +
                    "  I.JUSTIFICATIVA_ESTOURO           AS JUSTIFICATIVA_ESTOURO, " +
                    "  I.LATITUDE_MARCACAO               AS LATITUDE_MARCACAO, " +
                    "  I.LONGITUDE_MARCACAO              AS LONGITUDE_MARCACAO " +
                    "FROM VIEW_INTERVALO I " +
                    "JOIN COLABORADOR C ON I.CPF_COLABORADOR = C.CPF " +
                    "WHERE I.COD_UNIDADE = ? " +
                    "      AND I.CPF_COLABORADOR = ? " +
                    "      AND I.COD_TIPO_INTERVALO = ? " +
                    "      AND I.TIPO_MARCACAO = ? " +
                    "      AND DATA_HORA >= (SELECT MAX(DATA_HORA) " +
                    "                        FROM INTERVALO I " +
                    "                        WHERE I.COD_UNIDADE = ? " +
                    "                              AND I.CPF_COLABORADOR = ? " +
                    "                              AND I.COD_TIPO_INTERVALO = ? " +
                    "                              AND I.TIPO_MARCACAO = ?) " +
                    "ORDER BY I.DATA_HORA DESC " +
                    "LIMIT 1;");
            stmt.setString(1, TimeZoneManager.getZoneIdForCodUnidade(codUnidade, conn).getId());
            stmt.setLong(2, codUnidade);
            stmt.setLong(3, cpf);
            stmt.setLong(4, codTipoIntervalo);
            stmt.setString(5, TipoInicioFim.MARCACAO_INICIO.asString());
            stmt.setLong(6, codUnidade);
            stmt.setLong(7, cpf);
            stmt.setLong(8, codTipoIntervalo);
            stmt.setString(9, TipoInicioFim.MARCACAO_FIM.asString());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return createIntervaloMarcacao(rSet);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return null;
    }

    private boolean marcacaoIntervaloJaExiste(@NotNull final IntervaloMarcacao intervaloMarcacao,
                                              @NotNull final Connection conn) throws SQLException {

        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT EXISTS(SELECT I.CODIGO FROM INTERVALO I WHERE " +
                    "I.COD_UNIDADE = ? AND I.CPF_COLABORADOR = ? AND I.DATA_HORA = ? AND I.TIPO_MARCACAO = ?);");
            final ZoneId zoneId = TimeZoneManager.getZoneIdForCodUnidade(intervaloMarcacao.getCodUnidade(), conn);
            stmt.setLong(1, intervaloMarcacao.getCodUnidade());
            stmt.setLong(2, intervaloMarcacao.getCpfColaborador());
            stmt.setObject(3, intervaloMarcacao.getDataHoraMaracao().atZone(zoneId).toOffsetDateTime());
            stmt.setString(4, intervaloMarcacao.getTipoMarcacaoIntervalo().asString());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getBoolean("EXISTS");
            }
        } finally {
            closeConnection(null, stmt, rSet);
        }
        return false;
    }

    private void internalInsertMarcacaoIntervalo(@NotNull final IntervaloMarcacao intervaloMarcacao,
                                                 @NotNull final Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO INTERVALO(COD_UNIDADE, COD_TIPO_INTERVALO, CPF_COLABORADOR, " +
                    "DATA_HORA, TIPO_MARCACAO, FONTE_DATA_HORA, JUSTIFICATIVA_TEMPO_RECOMENDADO, JUSTIFICATIVA_ESTOURO, " +
                    "LATITUDE_MARCACAO, LONGITUDE_MARCACAO, DATA_HORA_SINCRONIZACAO) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            final Long codUnidade = intervaloMarcacao.getCodUnidade();
            final ZoneId zoneId = TimeZoneManager.getZoneIdForCodUnidade(codUnidade, conn);
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, intervaloMarcacao.getCodTipoIntervalo());
            stmt.setLong(3, intervaloMarcacao.getCpfColaborador());
            stmt.setObject(4, intervaloMarcacao.getDataHoraMaracao().atZone(zoneId).toOffsetDateTime());

            stmt.setString(5, intervaloMarcacao.getTipoMarcacaoIntervalo().asString());
            stmt.setString(6, intervaloMarcacao.getFonteDataHora().asString());
            stmt.setString(7, intervaloMarcacao.getJustificativaTempoRecomendado());
            stmt.setString(8, intervaloMarcacao.getJustificativaEstouro());

            final Localizacao localizacao = intervaloMarcacao.getLocalizacaoMarcacao();
            if (localizacao != null) {
                stmt.setString(9, localizacao.getLatitude());
                stmt.setString(10, localizacao.getLongitude());
            } else {
                stmt.setNull(9, Types.VARCHAR);
                stmt.setNull(10, Types.VARCHAR);
            }
            stmt.setTimestamp(11, Now.getTimestampUtc());
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Erro ao inserir marcação de intervalo");
            }
        } finally {
            closeStatement(stmt);
        }
    }

    private IntervaloMarcacao createIntervaloMarcacao(@NotNull final ResultSet rSet) throws SQLException {
        final IntervaloMarcacao intervaloMarcacao = new IntervaloMarcacao();
        intervaloMarcacao.setCodigo(rSet.getLong("CODIGO"));
        intervaloMarcacao.setCodUnidade(rSet.getLong("COD_UNIDADE"));
        intervaloMarcacao.setCpfColaborador(rSet.getLong("CPF_COLABORADOR"));
        intervaloMarcacao.setDataNascimentoColaborador(rSet.getDate("DATA_NASCIMENTO_COLABORADOR"));
        intervaloMarcacao.setCodTipoIntervalo(rSet.getLong("COD_TIPO_INTERVALO"));
        intervaloMarcacao.setDataHoraMaracao(rSet.getObject("DATA_HORA", LocalDateTime.class));
        intervaloMarcacao.setFonteDataHora(FonteDataHora.fromString(rSet.getString("FONTE_DATA_HORA")));
        intervaloMarcacao.setTipoMarcacaoIntervalo(TipoInicioFim.fromString(rSet.getString("TIPO_MARCACAO")));
        intervaloMarcacao.setJustificativaTempoRecomendado(rSet.getString("JUSTIFICATIVA_TEMPO_RECOMENDADO"));
        intervaloMarcacao.setJustificativaEstouro(rSet.getString("JUSTIFICATIVA_ESTOURO"));

        final String latitudeMarcacao = rSet.getString("LATITUDE_MARCACAO");
        if (!rSet.wasNull()) {
            final Localizacao localizacao = new Localizacao();
            localizacao.setLatitude(latitudeMarcacao);
            localizacao.setLongitude(rSet.getString("LONGITUDE_MARCACAO"));
            intervaloMarcacao.setLocalizacaoMarcacao(localizacao);
        }
        return intervaloMarcacao;
    }
}