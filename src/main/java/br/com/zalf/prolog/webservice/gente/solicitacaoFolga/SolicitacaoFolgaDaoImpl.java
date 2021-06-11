package br.com.zalf.prolog.webservice.gente.solicitacaoFolga;

import br.com.zalf.prolog.webservice.TimeZoneManager;
import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.datetime.DateUtils;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Colaborador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class SolicitacaoFolgaDaoImpl extends DatabaseConnection implements SolicitacaoFolgaDao {

    @Override
    public AbstractResponse insert(final SolicitacaoFolga s) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            // Verifica se a folga esta sendo solicitada com 48h de antecedência (2 dias).
            if (ChronoUnit.DAYS.between(LocalDate.now(), DateUtils.toLocalDate(s.getDataFolga())) < 2) {
                return Response.error("Erro ao inserir a solicitação de folga");
            }
            conn = getConnection();
            stmt = conn.prepareStatement("INSERT INTO SOLICITACAO_FOLGA ( "
                    + "COD_COLABORADOR, DATA_SOLICITACAO, DATA_FOLGA, "
                    + "MOTIVO_FOLGA, STATUS, PERIODO) VALUES (?, ?, ?, ?, ?, ?) RETURNING CODIGO");
            final ZoneId zoneId = TimeZoneManager.getZoneIdForCodColaborador(s.getColaborador().getCodigo(), conn);
            stmt.setLong(1, s.getColaborador().getCodigo());
            stmt.setObject(2, LocalDate.now(zoneId));
            stmt.setObject(3, s.getDataFolga().toInstant().atZone(zoneId).toLocalDate());
            stmt.setString(4, s.getMotivoFolga());
            stmt.setString(5, SolicitacaoFolga.STATUS_PENDENTE);
            stmt.setString(6, s.getPeriodo());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return ResponseWithCod.ok("Solicitação inserida com sucesso", rSet.getLong("CODIGO"));
            } else {
                return Response.error("Erro ao inserir a solicitação de folga");
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public boolean update(final SolicitacaoFolga solicitacaoFolga) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("UPDATE SOLICITACAO_FOLGA SET "
                    + " COD_COLABORADOR=? , "
                    + " COD_COLABORADOR_FEEDBACK=? , "
                    + " DATA_SOLICITACAO=? , "
                    + " DATA_FOLGA=? , "
                    + " DATA_FEEDBACK=? , "
                    + " MOTIVO_FOLGA=? , "
                    + " JUSTIFICATIVA_FEEDBACK=? , "
                    + " STATUS=? , "
                    + " PERIODO=? "
                    + "WHERE CODIGO=?");

            stmt.setLong(1, solicitacaoFolga.getColaborador().getCodigo());

            if (solicitacaoFolga.getColaboradorFeedback() != null) {
                stmt.setLong(2, solicitacaoFolga.getColaboradorFeedback().getCodigo());
            } else {
                stmt.setNull(2, java.sql.Types.BIGINT);
            }
            if (solicitacaoFolga.getDataSolicitacao() != null) {
                stmt.setObject(3, DateUtils.toLocalDate(solicitacaoFolga.getDataSolicitacao()));
            } else {
                stmt.setDate(3, null);
            }
            if (solicitacaoFolga.getDataFolga() != null) {
                stmt.setObject(4, DateUtils.toLocalDate(solicitacaoFolga.getDataFolga()));
            } else {
                stmt.setDate(4, null);
            }
            if (solicitacaoFolga.getDataFeedback() != null) {
                stmt.setObject(5, DateUtils.toLocalDate(solicitacaoFolga.getDataFeedback()));
            } else {
                final ZoneId zoneId =
                        TimeZoneManager.getZoneIdForCodColaborador(solicitacaoFolga.getColaborador().getCodigo(), conn);
                stmt.setObject(5, LocalDate.now(zoneId));
            }
            stmt.setString(6, solicitacaoFolga.getMotivoFolga());
            stmt.setString(7, solicitacaoFolga.getJustificativaFeedback());
            stmt.setString(8, solicitacaoFolga.getStatus());
            stmt.setString(9, solicitacaoFolga.getPeriodo());
            stmt.setLong(10, solicitacaoFolga.getCodigo());
            final int count = stmt.executeUpdate();
            return count > 0;
        } finally {
            closeConnection(conn, stmt, null);
        }
    }

    @Override
    public boolean delete(final Long codigo) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("DELETE FROM SOLICITACAO_FOLGA WHERE CODIGO = ? AND STATUS = 'PENDENTE'");
            stmt.setLong(1, codigo);
            final int count = stmt.executeUpdate();
            if (count > 0) {
                return true;
            }
        } finally {
            closeConnection(conn, stmt, null);
        }
        return false;
    }

    @Override
    public List<SolicitacaoFolga> getAll(final LocalDate dataInicial,
                                         final LocalDate dataFinal,
                                         final Long codUnidade,
                                         final String codEquipe,
                                         final String status, final Long codColaborador) throws SQLException {

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final List<SolicitacaoFolga> list = new ArrayList<>();
        try {
            conn = getConnection();
            final String query = "SELECT " +
                    "SF.CODIGO AS CODIGO, " +
                    "C.CPF AS CPF_COLABORADOR, " +
                    "C_FEEDBACK.CPF AS CPF_FEEDBACK, " +
                    "SF.DATA_FEEDBACK AS DATA_FEEDBACK, " +
                    "SF.DATA_FOLGA AS DATA_FOLGA, " +
                    "SF.DATA_SOLICITACAO AS DATA_SOLICITACAO, " +
                    "SF.MOTIVO_FOLGA AS MOTIVO_FOLGA, " +
                    "SF.JUSTIFICATIVA_FEEDBACK AS JUSTIFICATIVA_FEEDBACK, " +
                    "SF.PERIODO AS PERIODO, " +
                    "SF.STATUS AS STATUS, " +
                    "C.NOME AS NOME_SOLICITANTE, " +
                    "C_FEEDBACK.NOME AS NOME_FEEDBACK " +
                    "FROM SOLICITACAO_FOLGA SF "
                    + "JOIN COLABORADOR C ON C.CODIGO = SF.COD_COLABORADOR "
                    + "LEFT JOIN COLABORADOR C_FEEDBACK ON C_FEEDBACK.CODIGO = SF.COD_COLABORADOR_FEEDBACK "
                    + "JOIN EQUIPE E ON E.CODIGO = C.COD_EQUIPE "
                    + "WHERE SF.DATA_FOLGA BETWEEN (? AT TIME ZONE ?) AND (? AT TIME ZONE ?) "
                    + "AND C.COD_UNIDADE = ? "
                    + "AND E.CODIGO::TEXT LIKE ? "
                    + "AND SF.STATUS LIKE ? "
                    + "AND SF.COD_COLABORADOR = ?"
                    + "ORDER BY SF.DATA_SOLICITACAO";
            final String zoneId = TimeZoneManager.getZoneIdForCodUnidade(codUnidade, conn).getId();
            stmt = conn.prepareStatement(query);
            stmt.setObject(1, dataInicial);
            stmt.setString(2, zoneId);
            stmt.setObject(3, dataFinal);
            stmt.setString(4, zoneId);
            stmt.setLong(5, codUnidade);
            stmt.setString(6, codEquipe);
            stmt.setString(7, status);
            stmt.setLong(8, codColaborador);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                list.add(createSolicitacaoFolga(rSet));
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return list;
    }

    @Override
    public List<SolicitacaoFolga> getByColaborador(final Long codColaborador) throws SQLException {
        final List<SolicitacaoFolga> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT " +
                    "SF.CODIGO, " +
                    "SF.CPF_COLABORADOR, " +
                    "SF.CPF_FEEDBACK, " +
                    "SF.DATA_FEEDBACK, " +
                    "SF.DATA_FOLGA, " +
                    "SF.DATA_SOLICITACAO, " +
                    "SF.MOTIVO_FOLGA, " +
                    "SF.JUSTIFICATIVA_FEEDBACK, " +
                    "SF.PERIODO, " +
                    "SF.STATUS, " +
                    "C.NOME AS NOME_SOLICITANTE, " +
                    "C_FEEDBACK.NOME AS NOME_FEEDBACK " +
                    "FROM SOLICITACAO_FOLGA SF JOIN COLABORADOR C ON " +
                    "SF.CPF_COLABORADOR = C.CPF LEFT JOIN COLABORADOR C_FEEDBACK ON " +
                    "SF.CPF_FEEDBACK = C_FEEDBACK.CPF WHERE " +
                    "SF.CPF_COLABORADOR = ?;");
            stmt.setLong(1, codColaborador);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                list.add(createSolicitacaoFolga(rSet));
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return list;
    }

    private SolicitacaoFolga createSolicitacaoFolga(final ResultSet rSet) throws SQLException {
        final SolicitacaoFolga solicitacaoFolga = new SolicitacaoFolga();
        solicitacaoFolga.setCodigo(rSet.getLong("CODIGO"));

        final Colaborador colaborador = new Colaborador();
        colaborador.setCpf(rSet.getLong("CPF_COLABORADOR"));
        colaborador.setNome(rSet.getString("NOME_SOLICITANTE"));
        solicitacaoFolga.setColaborador(colaborador);

        final Colaborador colaboradorFeedback = new Colaborador();
        colaboradorFeedback.setCpf(rSet.getLong("CPF_FEEDBACK"));
        colaboradorFeedback.setNome(rSet.getString("NOME_FEEDBACK"));
        solicitacaoFolga.setColaboradorFeedback(colaboradorFeedback);

        solicitacaoFolga.setDataFeedback(rSet.getDate("DATA_FEEDBACK"));
        solicitacaoFolga.setDataFolga(rSet.getDate("DATA_FOLGA"));
        solicitacaoFolga.setDataSolicitacao(rSet.getDate("DATA_SOLICITACAO"));
        solicitacaoFolga.setMotivoFolga(rSet.getString("MOTIVO_FOLGA"));
        solicitacaoFolga.setJustificativaFeedback(rSet.getString("JUSTIFICATIVA_FEEDBACK"));
        solicitacaoFolga.setPeriodo(rSet.getString("PERIODO"));
        solicitacaoFolga.setStatus(rSet.getString("STATUS"));
        return solicitacaoFolga;
    }
}