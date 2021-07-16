package br.com.zalf.prolog.webservice.seguranca.relato;

import br.com.zalf.prolog.webservice.TimeZoneManager;
import br.com.zalf.prolog.webservice.commons.questoes.Alternativa;
import br.com.zalf.prolog.webservice.commons.util.database.SqlType;
import br.com.zalf.prolog.webservice.commons.util.database.StatementUtils;
import br.com.zalf.prolog.webservice.commons.util.datetime.DateUtils;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.seguranca.pdv.Pdv;
import br.com.zalf.prolog.webservice.seguranca.relato.model.Relato;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.time.*;
import java.util.ArrayList;
import java.util.List;

public class RelatoDaoImpl extends DatabaseConnection implements RelatoDao {

    @Override
    public boolean insert(@NotNull final Relato relato,
                          @Nullable final Integer versaoApp) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("INSERT INTO RELATO "
                                                 + "(DATA_HORA_LOCAL,  DATA_HORA_DATABASE, LATITUDE, LONGITUDE, "
                                                 + "URL_FOTO_1, URL_FOTO_2, URL_FOTO_3, COD_COLABORADOR, STATUS, " +
                                                 "COD_UNIDADE, "
                                                 + " COD_SETOR, COD_ALTERNATIVA, RESPOSTA_OUTROS, COD_PDV, VERSAO_APP) "
                                                 + "VALUES (?,?,?,?,?,?,?,?,?,(SELECT C.COD_UNIDADE FROM COLABORADOR " +
                                                 "C WHERE C.CODIGO = ?),"
                                                 + "(SELECT C.COD_SETOR FROM COLABORADOR C WHERE C.CODIGO = ?),?,?,?," +
                                                 "?)");
            final ZoneId unidadeZoneId = TimeZoneManager.getZoneIdForCpf(relato.getColaboradorRelato().getCpf(), conn);
            stmt.setObject(1, relato.getDataLocal().atZone(unidadeZoneId).toOffsetDateTime());
            stmt.setObject(2, OffsetDateTime.now(Clock.systemUTC()));
            stmt.setString(3, relato.getLatitude());
            stmt.setString(4, relato.getLongitude());

            // Garante a ordem das URLs antes de setar, pois no banco a URL 1 tem que ser diferente de nulo.
            relato.corrigeUrls();
            stmt.setString(5, relato.getUrlFoto1());
            stmt.setString(6, relato.getUrlFoto2());
            stmt.setString(7, relato.getUrlFoto3());
            stmt.setLong(8, relato.getColaboradorRelato().getCodigo());
            stmt.setString(9, Relato.PENDENTE_CLASSIFICACAO);
            stmt.setLong(10, relato.getColaboradorRelato().getCodigo());
            stmt.setLong(11, relato.getColaboradorRelato().getCodigo());
            stmt.setLong(12, relato.getAlternativa().codigo);
            stmt.setString(13, relato.getDescricao());
            if (relato.getPdv() != null) {
                stmt.setInt(14, relato.getPdv().getCodigo());
            } else {
                stmt.setNull(14, Types.INTEGER);
            }
            StatementUtils.bindValueOrNull(stmt, 15, versaoApp, SqlType.INTEGER);
            final int count = stmt.executeUpdate();
            if (count == 0) {
                throw new SQLException("Erro ao inserir o relato");
            }
        } finally {
            closeConnection(conn, stmt, null);
        }
        return true;
    }

    @Override
    public boolean delete(final Long codRelato) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("DELETE FROM RELATO WHERE CODIGO = ?");
            stmt.setLong(1, codRelato);
            return (stmt.executeUpdate() > 0);
        } finally {
            closeConnection(conn, stmt, null);
        }
    }

    @Override
    public Relato getByCod(@NotNull final Long codRelato,
                           @NotNull final String userToken) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT " +
                                                 "R.CODIGO AS CODIGO, " +
                                                 "R.DATA_HORA_LOCAL AT TIME ZONE ? AS DATA_HORA_LOCAL, " +
                                                 "R.DATA_HORA_DATABASE AT TIME ZONE ? AS DATA_HORA_DATABASE, " +
                                                 "R.LATITUDE AS LATITUDE, " +
                                                 "R.LONGITUDE AS LONGITUDE, " +
                                                 "R.URL_FOTO_1 AS URL_FOTO_1, " +
                                                 "R.URL_FOTO_2 AS URL_FOTO_2, " +
                                                 "R.URL_FOTO_3 AS URL_FOTO_3, " +
                                                 "C.CPF AS CPF_COLABORADOR, " +
                                                 "R.COD_COLABORADOR AS COD_COLABORADOR, " +
                                                 "C2.CPF AS CPF_CLASSIFICACAO, " +
                                                 "R.COD_COLABORADOR_CLASSIFICACAO AS COD_COLABORADOR_CLASSIFICACAO, " +
                                                 "C3.CPF AS CPF_FECHAMENTO, " +
                                                 "R.COD_COLABORADOR_FECHAMENTO AS COD_COLABORADOR_FECHAMENTO, " +
                                                 "R.DATA_HORA_CLASSIFICACAO AT TIME ZONE ? AS " +
                                                 "DATA_HORA_CLASSIFICACAO, " +
                                                 "R.DATA_HORA_FECHAMENTO AT TIME ZONE ? AS DATA_HORA_FECHAMENTO, " +
                                                 "R.FEEDBACK_FECHAMENTO AS FEEDBACK_FECHAMENTO, " +
                                                 "R.STATUS AS STATUS, " +
                                                 "R.COD_ALTERNATIVA AS COD_ALTERNATIVA, " +
                                                 "RA.ALTERNATIVA AS ALTERNATIVA, " +
                                                 "R.RESPOSTA_OUTROS AS RESPOSTA_OUTROS, " +
                                                 "R.COD_PDV AS COD_PDV, " +
                                                 "C.NOME AS NOME, " +
                                                 "C2.NOME AS NOME_CLASSIFICACAO, " +
                                                 "C3.NOME AS NOME_FECHAMENTO, " +
                                                 "NULL AS DISTANCIA "
                                                 + "FROM RELATO R JOIN "
                                                 + "COLABORADOR C ON R.COD_COLABORADOR = C.CODIGO  LEFT JOIN "
                                                 + "COLABORADOR C2 ON R.COD_COLABORADOR_CLASSIFICACAO = C2.CODIGO " +
                                                 "LEFT JOIN "
                                                 + "COLABORADOR C3 ON R.COD_COLABORADOR_FECHAMENTO = C3.CODIGO JOIN "
                                                 + "RELATO_ALTERNATIVA RA ON RA.CODIGO = R.COD_ALTERNATIVA AND RA" +
                                                 ".COD_UNIDADE = R.COD_UNIDADE "
                                                 + "WHERE R.CODIGO = ?");
            final ZoneId zoneId = TimeZoneManager.getZoneIdForToken(userToken, conn);
            stmt.setString(1, zoneId.getId());
            stmt.setString(2, zoneId.getId());
            stmt.setString(3, zoneId.getId());
            stmt.setString(4, zoneId.getId());
            stmt.setLong(5, codRelato);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return createRelato(rSet);
            }
        } finally {
            close(conn, stmt, rSet);
        }
        return null;
    }

    @Override
    public List<Relato> getAll(@NotNull final Long codUnidade,
                               final int limit,
                               final long offset,
                               final double latitude,
                               final double longitude,
                               final boolean
                                       isOrderByDate,
                               final String status) throws SQLException {
        final List<Relato> relatos = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        String query = "SELECT " +
                "R.CODIGO AS CODIGO, " +
                "R.DATA_HORA_LOCAL AT TIME ZONE ? AS DATA_HORA_LOCAL, " +
                "R.DATA_HORA_DATABASE AT TIME ZONE ? AS DATA_HORA_DATABASE, " +
                "R.LATITUDE AS LATITUDE, " +
                "R.LONGITUDE AS LONGITUDE, " +
                "R.URL_FOTO_1 AS URL_FOTO_1, " +
                "R.URL_FOTO_2 AS URL_FOTO_2, " +
                "R.URL_FOTO_3 AS URL_FOTO_3, " +
                "C.CPF AS CPF_COLABORADOR, " +
                "R.COD_COLABORADOR AS COD_COLABORADOR, " +
                "C2.CPF AS CPF_CLASSIFICACAO, " +
                "R.COD_COLABORADOR_CLASSIFICACAO AS COD_COLABORADOR_CLASSIFICACAO, " +
                "C3.CPF AS CPF_FECHAMENTO, " +
                "R.COD_COLABORADOR_FECHAMENTO AS COD_COLABORADOR_FECHAMENTO, " +
                "R.DATA_HORA_CLASSIFICACAO AT TIME ZONE ? AS DATA_HORA_CLASSIFICACAO, " +
                "R.DATA_HORA_FECHAMENTO AT TIME ZONE ? AS DATA_HORA_FECHAMENTO, " +
                "R.FEEDBACK_FECHAMENTO AS FEEDBACK_FECHAMENTO, " +
                "R.STATUS AS STATUS, " +
                "R.RESPOSTA_OUTROS AS RESPOSTA_OUTROS, " +
                "R.COD_PDV AS COD_PDV, " +
                "R.COD_ALTERNATIVA AS COD_ALTERNATIVA, " +
                "RA.ALTERNATIVA AS ALTERNATIVA, " +
                "C.NOME AS NOME, " +
                "C2.NOME AS NOME_CLASSIFICACAO, " +
                "C3.NOME AS NOME_FECHAMENTO, " +
                "ST_Distance(ST_Point(?, ?)::geography,ST_Point(longitude::real, latitude::real)::geography)/1000 as " +
                "distancia "
                + " FROM RELATO R JOIN "
                + "COLABORADOR C ON R.COD_COLABORADOR = C.CODIGO JOIN "
                + "RELATO_ALTERNATIVA RA ON RA.CODIGO = R.COD_ALTERNATIVA AND RA.COD_UNIDADE = R.COD_UNIDADE LEFT JOIN "
                + "COLABORADOR C2 ON R.COD_COLABORADOR_CLASSIFICACAO = C2.CODIGO LEFT JOIN "
                + "COLABORADOR C3 ON R.COD_COLABORADOR_FECHAMENTO = C3.CODIGO "
                + "WHERE R.COD_UNIDADE = ? AND R.STATUS LIKE ? "
                + "ORDER BY %s "
                + "LIMIT ? OFFSET ? ";
        try {
            conn = getConnection();
            if (isOrderByDate) {
                query = String.format(query, "DATA_HORA_DATABASE DESC");
            } else {
                query = String.format(query, "DISTANCIA ASC");
            }
            stmt = conn.prepareStatement(query);
            final ZoneId zoneId = TimeZoneManager.getZoneIdForCodUnidade(codUnidade, conn);
            stmt.setString(1, zoneId.getId());
            stmt.setString(2, zoneId.getId());
            stmt.setString(3, zoneId.getId());
            stmt.setString(4, zoneId.getId());
            stmt.setDouble(5, longitude);
            stmt.setDouble(6, latitude);
            stmt.setLong(7, codUnidade);
            stmt.setString(8, status);
            stmt.setInt(9, limit);
            stmt.setLong(10, offset);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                relatos.add(createRelato(rSet));
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return relatos;
    }

    @Override
    public List<Relato> getRealizadosByColaborador(@NotNull final Long codColaborador,
                                                   final int limit,
                                                   final long offset,
                                                   final double latitude,
                                                   final double longitude,
                                                   final boolean isOrderByDate,
                                                   @NotNull final String status,
                                                   @NotNull final String campoFiltro) throws SQLException {
        final List<Relato> relatos = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        String query = "SELECT " +
                "R.CODIGO AS CODIGO, " +
                "R.DATA_HORA_LOCAL AT TIME ZONE ? AS DATA_HORA_LOCAL, " +
                "R.DATA_HORA_DATABASE AT TIME ZONE ? AS DATA_HORA_DATABASE, " +
                "R.LATITUDE AS LATITUDE, " +
                "R.LONGITUDE AS LONGITUDE, " +
                "R.URL_FOTO_1 AS URL_FOTO_1, " +
                "R.URL_FOTO_2 AS URL_FOTO_2, " +
                "R.URL_FOTO_3 AS URL_FOTO_3, " +
                "C.CPF AS CPF_COLABORADOR, " +
                "R.COD_COLABORADOR AS COD_COLABORADOR, " +
                "C2.CPF AS CPF_CLASSIFICACAO, " +
                "R.COD_COLABORADOR_CLASSIFICACAO AS COD_COLABORADOR_CLASSIFICACAO, " +
                "C3.CPF AS CPF_FECHAMENTO, " +
                "R.COD_COLABORADOR_FECHAMENTO AS COD_COLABORADOR_FECHAMENTO, " +
                "R.DATA_HORA_CLASSIFICACAO AT TIME ZONE ? AS DATA_HORA_CLASSIFICACAO, " +
                "R.DATA_HORA_FECHAMENTO AT TIME ZONE ? AS DATA_HORA_FECHAMENTO, " +
                "R.FEEDBACK_FECHAMENTO AS FEEDBACK_FECHAMENTO, " +
                "R.STATUS AS STATUS, " +
                "R.RESPOSTA_OUTROS AS RESPOSTA_OUTROS, " +
                "R.COD_ALTERNATIVA AS COD_ALTERNATIVA, " +
                "R.COD_PDV AS COD_PDV, " +
                "RA.ALTERNATIVA AS ALTERNATIVA, " +
                "C.NOME AS NOME, " +
                "C2.NOME AS NOME_CLASSIFICACAO, " +
                "C3.NOME AS NOME_FECHAMENTO, "
                + "ST_Distance(ST_Point(?, ?)::geography,ST_Point(longitude::real, latitude::real)::geography)/1000 " +
                "as distancia "
                + " FROM RELATO R JOIN "
                + "COLABORADOR C ON R.COD_COLABORADOR = C.CODIGO JOIN "
                + "RELATO_ALTERNATIVA RA ON RA.CODIGO = R.COD_ALTERNATIVA AND RA.COD_UNIDADE = R.COD_UNIDADE LEFT JOIN "
                + "COLABORADOR C2 ON R.cod_colaborador_CLASSIFICACAO = C2.CODIGO LEFT JOIN "
                + "COLABORADOR C3 ON R.COD_COLABORADOR_FECHAMENTO = C3.CODIGO "
                + "WHERE %1s = ? AND R.STATUS LIKE ? "
                + "ORDER BY %2s "
                + "LIMIT ? OFFSET ? ";
        try {
            conn = getConnection();
            if (isOrderByDate) {
                query = String.format(query, getCampoFiltro(campoFiltro), "DATA_HORA_DATABASE DESC");
            } else {
                query = String.format(query, getCampoFiltro(campoFiltro), "DISTANCIA ASC");
            }
            stmt = conn.prepareStatement(query);
            final ZoneId zoneId = TimeZoneManager.getZoneIdForCodColaborador(codColaborador, conn);
            stmt.setString(1, zoneId.getId());
            stmt.setString(2, zoneId.getId());
            stmt.setString(3, zoneId.getId());
            stmt.setString(4, zoneId.getId());
            stmt.setDouble(5, longitude);
            stmt.setDouble(6, latitude);
            stmt.setLong(7, codColaborador);
            stmt.setString(8, status);
            stmt.setInt(9, limit);
            stmt.setLong(10, offset);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                relatos.add(createRelato(rSet));
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return relatos;
    }

    @Override
    public List<Relato> getAllExcetoColaborador(@NotNull final Long codColaborador,
                                                final int limit,
                                                final long offset,
                                                final double latitude,
                                                final double longitude,
                                                final boolean isOrderByDate,
                                                @NotNull final String status) throws SQLException {
        final List<Relato> relatos = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        String query = "SELECT " +
                "R.CODIGO AS CODIGO, " +
                "R.DATA_HORA_LOCAL AT TIME ZONE ? AS DATA_HORA_LOCAL, " +
                "R.DATA_HORA_DATABASE AT TIME ZONE ? AS DATA_HORA_DATABASE, " +
                "R.LATITUDE AS LATITUDE, " +
                "R.LONGITUDE AS LONGITUDE, " +
                "R.URL_FOTO_1 AS URL_FOTO_1, " +
                "R.URL_FOTO_2 AS URL_FOTO_2, " +
                "R.URL_FOTO_3 AS URL_FOTO_3, " +
                "C.CPF AS CPF_COLABORADOR, " +
                "R.COD_COLABORADOR AS COD_COLABORADOR, " +
                "C2.CPF AS CPF_CLASSIFICACAO, " +
                "R.COD_COLABORADOR_CLASSIFICACAO AS COD_COLABORADOR_CLASSIFICACAO, " +
                "C3.CPF AS CPF_FECHAMENTO, " +
                "R.COD_COLABORADOR_FECHAMENTO AS COD_COLABORADOR_FECHAMENTO, " +
                "R.DATA_HORA_CLASSIFICACAO AT TIME ZONE ? AS DATA_HORA_CLASSIFICACAO, " +
                "R.DATA_HORA_FECHAMENTO AT TIME ZONE ? AS DATA_HORA_FECHAMENTO, " +
                "R.FEEDBACK_FECHAMENTO AS FEEDBACK_FECHAMENTO, " +
                "R.STATUS AS STATUS, " +
                "R.RESPOSTA_OUTROS AS RESPOSTA_OUTROS, " +
                "R.COD_ALTERNATIVA AS COD_ALTERNATIVA, " +
                "R.COD_PDV AS COD_PDV, " +
                "RA.ALTERNATIVA AS ALTERNATIVA, " +
                "C.NOME AS NOME, " +
                "C2.NOME AS NOME_CLASSIFICACAO, " +
                "C3.NOME AS NOME_FECHAMENTO, " +
                "ST_Distance(ST_Point(?, ?)::geography,ST_Point(longitude::real, latitude::real)::geography)/1000 as " +
                "distancia "
                + " FROM RELATO R JOIN "
                + "COLABORADOR C ON R.COD_COLABORADOR = C.CODIGO JOIN "
                + "RELATO_ALTERNATIVA RA ON RA.CODIGO = R.COD_ALTERNATIVA AND RA.COD_UNIDADE = R.COD_UNIDADE  LEFT " +
                "JOIN "
                + "COLABORADOR C2 ON R.COD_COLABORADOR_CLASSIFICACAO = C2.CODIGO LEFT JOIN "
                + "COLABORADOR C3 ON R.COD_COLABORADOR_FECHAMENTO = C3.CODIGO "
                + "WHERE R.COD_COLABORADOR != ? AND R.STATUS LIKE ? AND R.COD_UNIDADE = (SELECT COD_UNIDADE FROM " +
                " COLABORADOR C WHERE C.CODIGO = ?) "
                + "ORDER BY %s "
                + "LIMIT ? OFFSET ? ";
        try {
            conn = getConnection();
            if (isOrderByDate) {
                query = String.format(query, "DATA_HORA_DATABASE DESC");
            } else {
                query = String.format(query, "DISTANCIA ASC");
            }
            stmt = conn.prepareStatement(query);
            final ZoneId zoneId = TimeZoneManager.getZoneIdForCodColaborador(codColaborador, conn);
            stmt.setString(1, zoneId.getId());
            stmt.setString(2, zoneId.getId());
            stmt.setString(3, zoneId.getId());
            stmt.setString(4, zoneId.getId());
            stmt.setDouble(5, longitude);
            stmt.setDouble(6, latitude);
            stmt.setLong(7, codColaborador);
            stmt.setString(8, status);
            stmt.setLong(9, codColaborador);
            stmt.setInt(10, limit);
            stmt.setLong(11, offset);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                relatos.add(createRelato(rSet));
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return relatos;
    }

    @Override
    public List<Relato> getAllByUnidade(@NotNull final LocalDate dataInicial,
                                        @NotNull final LocalDate dataFinal,
                                        @NotNull final String equipe,
                                        @NotNull final Long codUnidade,
                                        final long limit,
                                        final long offset,
                                        @NotNull final String status)
            throws SQLException {

        final List<Relato> relatos = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(
                    "SELECT " +
                            "  R.CODIGO AS CODIGO, " +
                            "  R.DATA_HORA_LOCAL AT TIME ZONE ? AS DATA_HORA_LOCAL, " +
                            "  R.DATA_HORA_DATABASE AT TIME ZONE ? AS DATA_HORA_DATABASE, " +
                            "  R.LATITUDE AS LATITUDE, " +
                            "  R.LONGITUDE AS LONGITUDE, " +
                            "  R.URL_FOTO_1 AS URL_FOTO_1, " +
                            "  R.URL_FOTO_2 AS URL_FOTO_2, " +
                            "  R.URL_FOTO_3 AS URL_FOTO_3, " +
                            "  C.CPF AS CPF_COLABORADOR, " +
                            "  R.COD_COLABORADOR AS COD_COLABORADOR, " +
                            "  C2.CPF AS CPF_CLASSIFICACAO, " +
                            "  R.COD_COLABORADOR_CLASSIFICACAO AS COD_COLABORADOR_CLASSIFICACAO, " +
                            "  C3.CPF AS CPF_FECHAMENTO, " +
                            "  R.COD_COLABORADOR_FECHAMENTO AS COD_COLABORADOR_FECHAMENTO, " +
                            "  R.DATA_HORA_CLASSIFICACAO AT TIME ZONE ? AS DATA_HORA_CLASSIFICACAO, " +
                            "  R.DATA_HORA_FECHAMENTO AT TIME ZONE ? AS DATA_HORA_FECHAMENTO, " +
                            "  R.FEEDBACK_FECHAMENTO AS FEEDBACK_FECHAMENTO, " +
                            "  R.STATUS AS STATUS, " +
                            "  R.RESPOSTA_OUTROS AS RESPOSTA_OUTROS, " +
                            "  R.COD_ALTERNATIVA AS COD_ALTERNATIVA, " +
                            "  R.COD_PDV AS COD_PDV, " +
                            "  RA.ALTERNATIVA AS ALTERNATIVA, " +
                            "  C.NOME AS NOME, " +
                            "  C2.NOME AS NOME_CLASSIFICACAO, " +
                            "  C3.NOME AS NOME_FECHAMENTO, " +
                            "  NULL AS DISTANCIA " +
                            "FROM RELATO R " +
                            "  JOIN COLABORADOR C ON R.COD_COLABORADOR = C.CODIGO " +
                            "  JOIN EQUIPE E ON E.CODIGO = C.COD_EQUIPE " +
                            "  JOIN RELATO_ALTERNATIVA RA " +
                            "ON RA.CODIGO = R.COD_ALTERNATIVA AND RA.COD_UNIDADE = R.COD_UNIDADE " +
                            "  LEFT JOIN COLABORADOR C2 ON R.COD_COLABORADOR_CLASSIFICACAO = C2.CODIGO " +
                            "  LEFT JOIN COLABORADOR C3 ON R.COD_COLABORADOR_FECHAMENTO = C3.CODIGO " +
                            "WHERE R.COD_UNIDADE = ? " +
                            "      AND R.STATUS LIKE ? " +
                            "      AND E.CODIGO::TEXT LIKE ? " +
                            "      AND R.DATA_HORA_DATABASE::DATE >= (? AT TIME ZONE ?) " +
                            "AND R.DATA_HORA_DATABASE::DATE <= (? AT TIME ZONE ?) " +
                            "ORDER BY DATA_HORA_DATABASE DESC " +
                            "LIMIT ? OFFSET ?");
            final ZoneId zoneId = TimeZoneManager.getZoneIdForCodUnidade(codUnidade, conn);
            stmt.setString(1, zoneId.getId());
            stmt.setString(2, zoneId.getId());
            stmt.setString(3, zoneId.getId());
            stmt.setString(4, zoneId.getId());
            stmt.setLong(5, codUnidade);
            stmt.setString(6, status);
            stmt.setString(7, equipe);
            stmt.setDate(8, DateUtils.toSqlDate(dataInicial));
            stmt.setString(9, zoneId.getId());
            stmt.setDate(10, DateUtils.toSqlDate(dataFinal));
            stmt.setString(11, zoneId.getId());
            stmt.setLong(12, limit);
            stmt.setLong(13, offset);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                relatos.add(createRelato(rSet));
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return relatos;
    }

    @Override
    public boolean classificaRelato(@NotNull final Relato relato) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("UPDATE RELATO SET COD_COLABORADOR_CLASSIFICACAO = ?, "
                                                 + " DATA_HORA_CLASSIFICACAO = ?, STATUS = ?, COD_ALTERNATIVA = ?, " +
                                                 "RESPOSTA_OUTROS = ? "
                                                 + " WHERE CODIGO = ?");

            stmt.setLong(1, relato.getColaboradorClassificacao().getCodigo());
            stmt.setObject(2, OffsetDateTime.now(Clock.systemUTC()));
            stmt.setString(3, relato.getStatus());
            stmt.setLong(4, relato.getAlternativa().codigo);
            stmt.setString(5, relato.getDescricao());
            stmt.setLong(6, relato.getCodigo());
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Erro ao classificar o relato");
            }
        } finally {
            closeConnection(conn, stmt, null);
        }
        return true;
    }

    @Override
    public boolean fechaRelato(@NotNull final Relato relato) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("UPDATE RELATO SET COD_COLABORADOR_FECHAMENTO = ?, "
                                                 + " DATA_HORA_FECHAMENTO = ?, STATUS = ?, FEEDBACK_FECHAMENTO = ?  "
                                                 + " WHERE CODIGO = ?");

            stmt.setLong(1, relato.getColaboradorFechamento().getCodigo());
            stmt.setObject(2, OffsetDateTime.now(Clock.systemUTC()));
            stmt.setString(3, Relato.FECHADO);
            stmt.setString(4, relato.getFeedbackFechamento());
            stmt.setLong(5, relato.getCodigo());
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Erro ao fechar o relato");
            }
        } finally {
            closeConnection(conn, stmt, null);
        }
        return true;
    }

    @NotNull
    @Override
    public List<Alternativa> getAlternativas(@NotNull final Long codUnidade, @NotNull final Long codSetor)
            throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_RELATO_GET_ALTERNATIVAS(?, ?, TRUE)");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, codSetor);
            rSet = stmt.executeQuery();
            final List<Alternativa> alternativas = new ArrayList<>();
            while (rSet.next()) {
                final Alternativa alternativa = new Alternativa();
                alternativa.codigo = rSet.getLong("CODIGO");
                alternativa.alternativa = rSet.getString("ALTERNATIVA");
                if (alternativa.alternativa.equals("Outros")) {
                    alternativa.tipo = Alternativa.TIPO_OUTROS;
                }
                alternativas.add(alternativa);
            }
            return alternativas;
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    private String getCampoFiltro(@NotNull final String campoFiltro) {
        String s = null;
        switch (campoFiltro) {
            case "realizados":
                s = "COD_COLABORADOR";
                break;
            case "classificados":
                s = "COD_COLABORADOR_CLASSIFICACAO";
                break;
            case "fechados":
                s = "COD_COLABORADOR_FECHAMENTO";
                break;
            default:
                break;
        }
        return s;
    }

    private Relato createRelato(@NotNull final ResultSet rSet) throws SQLException {
        final Relato relato = new Relato();
        relato.setCodigo(rSet.getLong("CODIGO"));
        // A hora que será mostrada no android deve ser a Data_Hora_Database
        relato.setDataLocal(rSet.getObject("DATA_HORA_LOCAL", LocalDateTime.class));
        relato.setDataDatabase(rSet.getObject("DATA_HORA_DATABASE", LocalDateTime.class));
        relato.setLatitude(rSet.getString("LATITUDE"));
        relato.setLongitude(rSet.getString("LONGITUDE"));
        relato.setUrlFoto1(rSet.getString("URL_FOTO_1"));
        relato.setUrlFoto2(rSet.getString("URL_FOTO_2"));
        relato.setUrlFoto3(rSet.getString("URL_FOTO_3"));
        relato.setColaboradorRelato(createColaborador(rSet.getString("NOME"),
                                                      rSet.getLong("COD_COLABORADOR"),
                                                      rSet.getLong("CPF_COLABORADOR")));
        relato.setColaboradorClassificacao(createColaborador(rSet.getString("NOME_CLASSIFICACAO"),
                                                             rSet.getLong("COD_COLABORADOR_CLASSIFICACAO"),
                                                             rSet.getLong("CPF_CLASSIFICACAO")));
        relato.setColaboradorFechamento(createColaborador(rSet.getString("NOME_FECHAMENTO"),
                                                          rSet.getLong("COD_COLABORADOR_FECHAMENTO"),
                                                          rSet.getLong("CPF_FECHAMENTO")));
        relato.setDataClassificacao(rSet.getObject("DATA_HORA_CLASSIFICACAO", LocalDateTime.class));
        relato.setDataFechamento(rSet.getObject("DATA_HORA_FECHAMENTO", LocalDateTime.class));
        relato.setFeedbackFechamento(rSet.getString("FEEDBACK_FECHAMENTO"));
        relato.setStatus(rSet.getString("STATUS"));
        relato.setDescricao(rSet.getString("RESPOSTA_OUTROS"));
        final Alternativa alternativa = createAlternativa(rSet);
        relato.setAlternativa(alternativa);
        relato.setDistanciaColaborador(rSet.getDouble("DISTANCIA"));
        final int codPdv = rSet.getInt("COD_PDV");
        if (codPdv > 0) {
            final Pdv pdv = new Pdv();
            pdv.setCodigo(codPdv);
            relato.setPdv(pdv);
        }
        return relato;
    }

    private Colaborador createColaborador(@NotNull final String nome,
                                          @NotNull final Long codigo,
                                          @NotNull final Long cpf) {
        final Colaborador colaborador = new Colaborador();
        colaborador.setNome(nome);
        colaborador.setCodigo((codigo));
        colaborador.setCpf(cpf);
        return colaborador;
    }

    private Alternativa createAlternativa(final ResultSet rSet) throws SQLException {
        final Alternativa alternativa = new Alternativa();
        alternativa.codigo = rSet.getLong("COD_ALTERNATIVA");
        alternativa.alternativa = rSet.getString("ALTERNATIVA");
        if (alternativa.alternativa.equals("Outros")) {
            alternativa.tipo = Alternativa.TIPO_OUTROS;
        }
        return alternativa;
    }
}