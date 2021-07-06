package br.com.zalf.prolog.webservice.gente.solicitacaoFolga;

import br.com.zalf.prolog.webservice.TimeZoneManager;
import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.database.SqlType;
import br.com.zalf.prolog.webservice.commons.util.datetime.DateUtils;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Colaborador;

import javax.validation.constraints.NotNull;
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
    public AbstractResponse insert(@NotNull final SolicitacaoFolga s) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            // Verifica se a folga esta sendo solicitada com 48h de antecedência (2 dias).
            if (ChronoUnit.DAYS.between(LocalDate.now(), DateUtils.toLocalDate(s.getDataFolga())) < 2) {
                return Response.error("Erro ao inserir a solicitação de folga");
            }
            conn = getConnection();
            stmt = conn.prepareStatement("insert into solicitacao_folga (cod_colaborador, " +
                                                 "data_solicitacao, " +
                                                 "data_folga, " +
                                                 "motivo_folga, " +
                                                 "status, " +
                                                 "periodo) " +
                                                 "values (?, ?, ?, ?, ?, ?) " +
                                                 "returning codigo;");
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
    public boolean update(@NotNull final SolicitacaoFolga solicitacaoFolga) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("update solicitacao_folga " +
                                                 "set cod_colaborador = ?, " +
                                                 "    cod_colaborador_feedback = ?, " +
                                                 "    data_solicitacao = ?, " +
                                                 "    data_folga = ?, " +
                                                 "    data_feedback = ?, " +
                                                 "    motivo_folga = ?, " +
                                                 "    justificativa_feedback = ?, " +
                                                 "    status = ?, " +
                                                 "    periodo = ? " +
                                                 "where codigo = ?;");
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
    public boolean delete(@NotNull final Long codigo) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("delete from solicitacao_folga where codigo = ? and status = 'PENDENTE'");
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
            final String query = "select sf.codigo                 as codigo, " +
                    "c.cpf                     as cpf_colaborador, " +
                    "cf.cpf                    as cpf_feedback, " +
                    "sf.data_feedback          as data_feedback, " +
                    "sf.data_folga             as data_folga, " +
                    "sf.data_solicitacao       as data_solicitacao, " +
                    "sf.motivo_folga           as motivo_folga, " +
                    "sf.justificativa_feedback as justificativa_feedback, " +
                    "sf.periodo                as periodo, " +
                    "sf.status                 as status, " +
                    "c.nome                    as nome_solicitante, " +
                    "cf.nome                   as nome_feedback " +
                    "from solicitacao_folga sf " +
                    "join colaborador c on c.codigo = sf.cod_colaborador " +
                    "left join colaborador cf on cf.codigo = sf.cod_colaborador_feedback " +
                    "join equipe e on e.codigo = c.cod_equipe " +
                    "where sf.data_folga between (? at time zone ?) and (? at time zone ?) " +
                    "  and c.cod_unidade = ? " +
                    "  and e.codigo::text like ? " +
                    "  and sf.status like ? " +
                    "  and (? is true or sf.cod_colaborador = ?) " +
                    "order by sf.data_solicitacao;";
            final String zoneId = TimeZoneManager.getZoneIdForCodUnidade(codUnidade, conn).getId();
            stmt = conn.prepareStatement(query);
            stmt.setObject(1, dataInicial);
            stmt.setString(2, zoneId);
            stmt.setObject(3, dataFinal);
            stmt.setString(4, zoneId);
            stmt.setLong(5, codUnidade);
            stmt.setString(6, codEquipe);
            stmt.setString(7, status);
            if (codColaborador == null) {
                stmt.setBoolean(8, true);
                stmt.setNull(9, SqlType.BIGINT.asIntTypeJava());
            } else {
                stmt.setBoolean(8, false);
                stmt.setLong(9, codColaborador);
            }
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
            stmt = conn.prepareStatement(
                    "select sf.codigo                 as codigo, " +
                            "c.cpf                     as cpf_colaborador, " +
                            "cf.cpf                    as cpf_feedback, " +
                            "sf.data_feedback          as data_feedback, " +
                            "sf.data_folga             as data_folga, " +
                            "sf.data_solicitacao       as data_solicitacao, " +
                            "sf.motivo_folga           as motivo_folga, " +
                            "sf.justificativa_feedback as justificativa_feedback, " +
                            "sf.periodo                as periodo, " +
                            "sf.status                 as status, " +
                            "c.nome                    as nome_solicitante, " +
                            "cf.nome                   as nome_feedback " +
                            "from solicitacao_folga sf " +
                            "join colaborador c on sf.cod_colaborador = c.codigo " +
                            "left join colaborador cf on sf.cod_colaborador_feedback = cf.codigo " +
                            "where sf.cod_colaborador = ?;");
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