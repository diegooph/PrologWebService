package br.com.zalf.prolog.webservice.gente.faleConosco;

import br.com.zalf.prolog.webservice.TimeZoneManager;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Colaborador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class FaleConoscoDaoImpl extends DatabaseConnection implements FaleConoscoDao {

    public FaleConoscoDaoImpl() {

    }

    @Override
    public Long insert(final FaleConosco faleConosco, final Long codUnidade) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("insert into fale_conosco("
                                                 + "data_hora, "
                                                 + "descricao,"
                                                 + "categoria, "
                                                 + "cod_colaborador,"
                                                 + "cod_unidade, "
                                                 + "status) "
                                                 + "values (?,?,?,?,?,?) returning codigo");
            stmt.setObject(1, OffsetDateTime.now(Clock.systemUTC()));
            stmt.setString(2, faleConosco.getDescricao());
            stmt.setString(3, faleConosco.getCategoria().asString());
            stmt.setLong(4, faleConosco.getColaborador().getCodigo());
            stmt.setLong(5, codUnidade);
            stmt.setString(6, FaleConosco.STATUS_PENDENTE);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getLong("CODIGO");
            } else {
                throw new SQLException("Erro ao inserir o fale conosco");
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public FaleConosco getByCod(final Long codigo, final Long codUnidade) throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT " +
                                                 "F.STATUS AS STATUS, " +
                                                 "F.CODIGO AS CODIGO, " +
                                                 "F.DATA_HORA AT TIME ZONE ? AS DATA_HORA, " +
                                                 "F.DESCRICAO AS DESCRICAO, " +
                                                 "F.CATEGORIA AS CATEGORIA, " +
                                                 "F.FEEDBACK AS FEEDBACK, " +
                                                 "F.DATA_HORA_FEEDBACK AT TIME ZONE ? AS DATA_HORA_FEEDBACK, " +
                                                 "C.CPF AS CPF_COLABORADOR, " +
                                                 "C.NOME AS NOME_COLABORADOR, " +
                                                 "C2.CPF AS CPF_FEEDBACK, " +
                                                 "C2.NOME AS NOME_FEEDBACK " +
                                                 "FROM FALE_CONOSCO F JOIN COLABORADOR C ON C.CPF = F.CPF_COLABORADOR" +
                                                 " " +
                                                 "LEFT JOIN COLABORADOR C2 ON C2.CPF = F.CPF_FEEDBACK " +
                                                 "WHERE F.CODIGO = ? AND F.COD_UNIDADE = ?");
            final ZoneId zoneId = TimeZoneManager.getZoneIdForCodUnidade(codUnidade, conn);
            stmt.setString(1, zoneId.getId());
            stmt.setString(2, zoneId.getId());
            stmt.setLong(3, codigo);
            stmt.setLong(4, codUnidade);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return createFaleConosco(rSet);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return null;
    }

    @Override
    public List<FaleConosco> getAll(final long dataInicial,
                                    final long dataFinal,
                                    final int limit,
                                    final int offset,
                                    final String cpf,
                                    final String equipe,
                                    final Long codUnidade,
                                    final String status,
                                    final String categoria) throws Exception {
        final List<FaleConosco> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT " +
                                                 "  F.STATUS AS STATUS, " +
                                                 "  F.CODIGO AS CODIGO, " +
                                                 "  F.DATA_HORA AT TIME ZONE ? AS DATA_HORA, " +
                                                 "  F.DESCRICAO AS DESCRICAO, " +
                                                 "  F.CATEGORIA AS CATEGORIA, " +
                                                 "  F.FEEDBACK AS FEEDBACK, " +
                                                 "  F.DATA_HORA_FEEDBACK AT TIME ZONE ? AS DATA_HORA_FEEDBACK, " +
                                                 "  C.CPF AS CPF_COLABORADOR, " +
                                                 "  C.NOME AS NOME_COLABORADOR, " +
                                                 "  C2.CPF AS CPF_FEEDBACK, " +
                                                 "  C2.NOME AS NOME_FEEDBACK " +
                                                 "FROM FALE_CONOSCO F JOIN colaborador C ON C.cpf = F.CPF_COLABORADOR" +
                                                 " " +
                                                 "  JOIN EQUIPE E ON E.codigo = C.cod_equipe " +
                                                 "  LEFT JOIN COLABORADOR C2 ON C2.CPF = F.CPF_FEEDBACK " +
                                                 "WHERE E.nome LIKE ? " +
                                                 "      AND F.cod_unidade = ? " +
                                                 "      AND F.status LIKE ? " +
                                                 "      AND F.categoria LIKE ? " +
                                                 "      AND C.CPF::TEXT LIKE ? " +
                                                 "      AND F.DATA_HORA::date >= (? AT TIME ZONE ?)::date " +
                                                 "      AND F.DATA_HORA::date <= (? AT TIME ZONE ?)::date " +
                                                 "ORDER BY F.DATA_HORA " +
                                                 "LIMIT ? OFFSET ?");
            final ZoneId zoneId = TimeZoneManager.getZoneIdForCodUnidade(codUnidade, conn);
            stmt.setString(1, zoneId.getId());
            stmt.setString(2, zoneId.getId());
            stmt.setString(3, equipe);
            stmt.setLong(4, codUnidade);
            stmt.setString(5, status);
            stmt.setString(6, categoria);
            stmt.setString(7, cpf);
            stmt.setDate(8, new java.sql.Date(dataInicial));
            stmt.setString(9, zoneId.getId());
            stmt.setDate(10, new java.sql.Date(dataFinal));
            stmt.setString(11, zoneId.getId());
            stmt.setInt(12, limit);
            stmt.setInt(13, offset);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                final FaleConosco faleConosco = createFaleConosco(rSet);
                list.add(faleConosco);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return list;
    }

    @Override
    public List<FaleConosco> getByColaborador(final Long cpf, final String status) throws Exception {
        final List<FaleConosco> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT " +
                                                 "F.STATUS AS STATUS, " +
                                                 "F.CODIGO AS CODIGO, " +
                                                 "F.DATA_HORA AT TIME ZONE ? AS DATA_HORA, " +
                                                 "F.DESCRICAO AS DESCRICAO, " +
                                                 "F.CATEGORIA AS CATEGORIA, " +
                                                 "F.FEEDBACK AS FEEDBACK, " +
                                                 "F.DATA_HORA_FEEDBACK AT TIME ZONE ? AS DATA_HORA_FEEDBACK, " +
                                                 "C.CPF AS CPF_COLABORADOR, " +
                                                 "C.NOME AS NOME_COLABORADOR, " +
                                                 "C2.CPF AS CPF_FEEDBACK, " +
                                                 "C2.NOME AS NOME_FEEDBACK " +
                                                 "FROM FALE_CONOSCO F JOIN colaborador C ON F.cpf_colaborador = C.cpf" +
                                                 " " +
                                                 "LEFT JOIN colaborador C2 ON C2.cpf = F.CPF_FEEDBACK WHERE " +
                                                 "CPF_COLABORADOR = ? and f.status like ? ORDER BY F.data_hora");
            final ZoneId zoneId = TimeZoneManager.getZoneIdForCpf(cpf, conn);
            stmt.setString(1, zoneId.getId());
            stmt.setString(2, zoneId.getId());
            stmt.setLong(3, cpf);
            stmt.setString(4, status);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                final FaleConosco faleConosco = createFaleConosco(rSet);
                list.add(faleConosco);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return list;
    }

    @Override
    public boolean insertFeedback(final FaleConosco faleConosco, final Long codUnidade) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(" UPDATE FALE_CONOSCO SET "
                                                 + "DATA_HORA_FEEDBACK = ?, CPF_FEEDBACK = ?," +
                                                 "FEEDBACK = ?, STATUS = ? "
                                                 + "WHERE CODIGO = ? AND COD_UNIDADE = ? ");

            stmt.setObject(1, OffsetDateTime.now(Clock.systemUTC()));
            stmt.setLong(2, faleConosco.getColaboradorFeedback().getCpf());
            stmt.setString(3, faleConosco.getFeedback());
            stmt.setString(4, FaleConosco.STATUS_RESPONDIDO);
            stmt.setLong(5, faleConosco.getCodigo());
            stmt.setLong(6, codUnidade);

            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Erro ao inserir feedback no fale conosco");
            }
        } finally {
            closeConnection(conn, stmt, null);
        }
        return true;
    }

    private FaleConosco createFaleConosco(final ResultSet rSet) throws Exception {
        final FaleConosco faleConosco = new FaleConosco();
        faleConosco.setStatus(rSet.getString("STATUS"));
        faleConosco.setCodigo(rSet.getLong("CODIGO"));
        faleConosco.setData(rSet.getObject("DATA_HORA", LocalDateTime.class));
        faleConosco.setDescricao(rSet.getString("DESCRICAO"));
        final Colaborador realizador = new Colaborador();
        realizador.setCpf(rSet.getLong("CPF_COLABORADOR"));
        faleConosco.setColaborador(realizador);
        faleConosco.setCategoria(FaleConosco.Categoria.fromString(rSet.getString("CATEGORIA")));
        final String feedback = rSet.getString("FEEDBACK");
        if (feedback != null) {
            faleConosco.setFeedback(feedback);
            final Colaborador colaboradorFeedback = new Colaborador();
            colaboradorFeedback.setCpf(rSet.getLong("CPF_FEEDBACK"));
            colaboradorFeedback.setNome(rSet.getString("NOME_FEEDBACK"));
            faleConosco.setColaboradorFeedback(colaboradorFeedback);
            faleConosco.setDataFeedback(rSet.getObject("DATA_HORA_FEEDBACK", LocalDateTime.class));
        }
        return faleConosco;
    }
}