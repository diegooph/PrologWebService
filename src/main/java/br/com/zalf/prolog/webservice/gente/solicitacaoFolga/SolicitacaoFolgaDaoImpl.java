package br.com.zalf.prolog.webservice.gente.solicitacaoFolga;

import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.TimeZoneManager;
import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.DateUtils;

import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class SolicitacaoFolgaDaoImpl extends DatabaseConnection implements SolicitacaoFolgaDao {

    @Override
    public AbstractResponse insert(SolicitacaoFolga s) throws SQLException {
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
                    + "CPF_COLABORADOR, DATA_SOLICITACAO, DATA_FOLGA, "
                    + "MOTIVO_FOLGA, STATUS, PERIODO) VALUES (?, ?, ?, ?, ?, ?) RETURNING CODIGO");
            final ZoneId zoneId = TimeZoneManager.getZoneIdForCpf(s.getColaborador().getCpf(), conn);
            stmt.setLong(1, s.getColaborador().getCpf());
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
    public boolean update(SolicitacaoFolga solicitacaoFolga) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("UPDATE SOLICITACAO_FOLGA SET "
                    + " CPF_COLABORADOR=? , "
                    + " CPF_FEEDBACK=? , "
                    + " DATA_SOLICITACAO=? , "
                    + " DATA_FOLGA=? , "
                    + " DATA_FEEDBACK=? , "
                    + " MOTIVO_FOLGA=? , "
                    + " JUSTIFICATIVA_FEEDBACK=? , "
                    + " STATUS=? , "
                    + " PERIODO=? "
                    + "WHERE CODIGO=?");

            stmt.setLong(1, solicitacaoFolga.getColaborador().getCpf());

            if (solicitacaoFolga.getColaboradorFeedback() != null) {
                stmt.setLong(2, solicitacaoFolga.getColaboradorFeedback().getCpf());
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
                final ZoneId zoneId = TimeZoneManager.getZoneIdForCpf(solicitacaoFolga.getColaborador().getCpf(), conn);
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
    public boolean delete(Long codigo) throws SQLException {
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
    public List<SolicitacaoFolga> getAll(LocalDate dataInicial, LocalDate dataFinal,
                                         Long codUnidade, String codEquipe, String status, String cpfColaborador) throws SQLException {

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final List<SolicitacaoFolga> list = new ArrayList<>();
        try {
            conn = getConnection();
            final String query = "SELECT " +
                    "SF.CODIGO AS CODIGO, " +
                    "SF.CPF_COLABORADOR AS CPF_COLABORADOR, " +
                    "SF.CPF_FEEDBACK AS CPF_FEEDBACK, " +
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
                    + "JOIN COLABORADOR C ON C.CPF = SF.CPF_COLABORADOR "
                    + "LEFT JOIN COLABORADOR C_FEEDBACK ON C_FEEDBACK.CPF = SF.CPF_FEEDBACK "
                    + "JOIN EQUIPE E ON E.CODIGO = C.COD_EQUIPE "
                    + "WHERE SF.DATA_FOLGA BETWEEN ? AND ? "
                    + "AND C.COD_UNIDADE = ? "
                    + "AND E.CODIGO::TEXT LIKE ? "
                    + "AND SF.STATUS LIKE ? "
                    + "AND SF.CPF_COLABORADOR::TEXT LIKE ?"
                    + "ORDER BY SF.DATA_SOLICITACAO";

            stmt = conn.prepareStatement(query);
            stmt.setObject(1, dataInicial);
            stmt.setObject(2, dataFinal);
            stmt.setLong(3, codUnidade);
            stmt.setString(4, codEquipe);
            stmt.setString(5, status);
            stmt.setString(6, cpfColaborador);
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
    public List<SolicitacaoFolga> getByColaborador(Long cpf) throws SQLException {
        List<SolicitacaoFolga> list = new ArrayList<>();
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
            stmt.setLong(1, cpf);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                list.add(createSolicitacaoFolga(rSet));
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return list;
    }

    private SolicitacaoFolga createSolicitacaoFolga(ResultSet rSet) throws SQLException {
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