package br.com.zalf.prolog.webservice.gente.faleconosco;

import br.com.zalf.prolog.webservice.TimeZoneManager;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Colaborador;
import org.jetbrains.annotations.NotNull;

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
            stmt = conn.prepareStatement("select "
                                                 + "f.status as status, "
                                                 + "f.codigo as codigo, "
                                                 + "f.data_hora at time zone ? as data_hora, "
                                                 + "f.descricao as descricao, "
                                                 + "f.categoria as categoria, "
                                                 + "f.feedback as feedback, "
                                                 + "f.data_hora_feedback at time zone ? as data_hora_feedback, "
                                                 + "c.cpf as cpf_colaborador, "
                                                 + "c.nome as nome_colaborador, "
                                                 + "c2.cpf as cpf_feedback, "
                                                 + "c2.nome as nome_feedback "
                                                 + "from fale_conosco f "
                                                 + "join colaborador c on c.codigo = f.cod_colaborador "
                                                 + "left join colaborador c2 "
                                                 + "on c2.codigo = f.cod_colaborador_feedback "
                                                 + "where f.codigo = ? and f.cod_unidade = ?");
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
                                    final Long codColaborador,
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
            stmt = conn.prepareStatement("select "
                                                 + "f.status as status, "
                                                 + "f.codigo as codigo, "
                                                 + "f.data_hora at time zone ? as data_hora, "
                                                 + "f.descricao as descricao, "
                                                 + "f.categoria as categoria, "
                                                 + "f.feedback as feedback, "
                                                 + "f.data_hora_feedback at time zone ? as data_hora_feedback, "
                                                 + "c.cpf as cpf_colaborador, "
                                                 + "c.nome as nome_colaborador, "
                                                 + "c2.cpf as cpf_feedback, "
                                                 + "c2.nome as nome_feedback "
                                                 + "from fale_conosco f "
                                                 + "join colaborador c on c.codigo = f.cod_colaborador "
                                                 + "join equipe e on e.codigo = c.cod_equipe "
                                                 + "left join colaborador c2 on c2.codigo = f.cod_colaborador_feedback "
                                                 + "where e.nome like ? "
                                                 + "and f.cod_unidade = ? "
                                                 + "and f.status like ? "
                                                 + "and f.categoria like ? "
                                                 + "and f.cod_colaborador = ? "
                                                 + "and f.data_hora::date >= (? at time zone ?)::date "
                                                 + "and f.data_hora::date <= (? at time zone ?)::date "
                                                 + "order by f.data_hora "
                                                 + "limit ? offset ?");
            final ZoneId zoneId = TimeZoneManager.getZoneIdForCodUnidade(codUnidade, conn);
            stmt.setString(1, zoneId.getId());
            stmt.setString(2, zoneId.getId());
            stmt.setString(3, equipe);
            stmt.setLong(4, codUnidade);
            stmt.setString(5, status);
            stmt.setString(6, categoria);
            stmt.setLong(7, codColaborador);
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
    @NotNull
    public List<FaleConosco> getByColaborador(@NotNull final Long codColaborador,
                                              @NotNull final String status) throws Exception {
        final List<FaleConosco> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("select "
                                                 + "f.status as status, "
                                                 + "f.codigo as codigo, "
                                                 + "f.data_hora at time zone ? as data_hora, "
                                                 + "f.descricao as descricao, "
                                                 + "f.categoria as categoria, "
                                                 + "f.feedback as feedback, "
                                                 + "f.data_hora_feedback at time zone ? as data_hora_feedback, "
                                                 + "c.cpf as cpf_colaborador, "
                                                 + "c.nome as nome_colaborador, "
                                                 + "c2.cpf as cpf_feedback, "
                                                 + "c2.nome as nome_feedback "
                                                 + "from fale_conosco f "
                                                 + "join colaborador c on f.cod_colaborador = c.codigo "
                                                 + "left join colaborador c2 on c2.codigo = f.cod_colaborador_feedback "
                                                 + "where f.cod_colaborador = ? "
                                                 + "and f.status like ? "
                                                 + "order by f.data_hora");
            final ZoneId zoneId = TimeZoneManager.getZoneIdForCodColaborador(codColaborador, conn);
            stmt.setString(1, zoneId.getId());
            stmt.setString(2, zoneId.getId());
            stmt.setLong(3, codColaborador);
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
            stmt = conn.prepareStatement(" update fale_conosco set "
                                                 + "data_hora_feedback = ?, "
                                                 + "cod_colaborador_feedback = ?, "
                                                 + "feedback = ?, "
                                                 + " status = ? "
                                                 + "where codigo = ? "
                                                 + "and cod_unidade = ? ");

            stmt.setObject(1, OffsetDateTime.now(Clock.systemUTC()));
            stmt.setLong(2, faleConosco.getColaboradorFeedback().getCodigo());
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