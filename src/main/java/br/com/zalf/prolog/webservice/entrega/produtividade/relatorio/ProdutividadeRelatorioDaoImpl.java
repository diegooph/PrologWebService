package br.com.zalf.prolog.webservice.entrega.produtividade.relatorio;

import br.com.zalf.prolog.webservice.Filtros;
import br.com.zalf.prolog.webservice.TimeZoneManager;
import br.com.zalf.prolog.webservice.commons.report.CsvWriter;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.report.ReportTransformer;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.entrega.produtividade.relatorio._model.ProdutividadeColaboradorDia;
import br.com.zalf.prolog.webservice.entrega.produtividade.relatorio._model.ProdutividadeColaboradorRelatorio;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Colaborador;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static br.com.zalf.prolog.webservice.entrega.produtividade.relatorio._model.ProdutividadeRelatorioConverter.createProdutividadeColaboradorDia;
import static br.com.zalf.prolog.webservice.entrega.produtividade.relatorio._model.ProdutividadeRelatorioConverter.createProdutividadeColaboradorRelatorio;

/**
 * Created by Zart on 18/05/2017.
 */
public class ProdutividadeRelatorioDaoImpl extends DatabaseConnection implements ProdutividadeRelatorioDao {

    public ProdutividadeRelatorioDaoImpl() {

    }

    @Override
    public void getConsolidadoProdutividadeCsv(@NotNull final OutputStream outputStream,
                                               @NotNull final Long codUnidade,
                                               @NotNull final Date dataInicial,
                                               @NotNull final Date dataFinal) throws SQLException, IOException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getConsolidadoProdutividade(conn, codUnidade, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, outputStream);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public Report getConsolidadoProdutividadeReport(@NotNull final Long codUnidade,
                                                    @NotNull final Date dataInicial,
                                                    @NotNull final Date dataFinal) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getConsolidadoProdutividade(conn, codUnidade, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public void getExtratoIndividualProdutividadeCsv(@NotNull final OutputStream outputStream,
                                                     @NotNull final String cpf,
                                                     @NotNull final Long codUnidade,
                                                     @NotNull final Date dataInicial,
                                                     @NotNull final Date dataFinal) throws SQLException, IOException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getExtratoIndividualProdutividade(conn, cpf, codUnidade, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, outputStream);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public Report getExtratoIndividualProdutividadeReport(@NotNull final String cpf,
                                                          @NotNull final Long codUnidade,
                                                          @NotNull final Date dataInicial,
                                                          @NotNull final Date dataFinal) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getExtratoIndividualProdutividade(conn, cpf, codUnidade, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public void getAcessosProdutividadeCsv(@NotNull final OutputStream outputStream,
                                           @NotNull final String cpf,
                                           @NotNull final Long codUnidade,
                                           @NotNull final Date dataInicial,
                                           @NotNull final Date dataFinal) throws SQLException, IOException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getAcessosProdutividadeStatement(conn, cpf, codUnidade, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, outputStream);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public Report getAcessosProdutividadeReport(@NotNull final String cpf,
                                                @NotNull final Long codUnidade,
                                                @NotNull final Date dataInicial,
                                                @NotNull final Date dataFinal) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getAcessosProdutividadeStatement(conn, cpf, codUnidade, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public List<ProdutividadeColaboradorRelatorio> getRelatorioProdutividadeColaborador(
            @NotNull final Long codUnidade,
            @NotNull final String cpfColaborador,
            @NotNull final LocalDate dataInicial,
            @NotNull final LocalDate dataFinal) throws SQLException {
        final List<ProdutividadeColaboradorRelatorio> relatorioColaboradores = new ArrayList<>();
        List<ProdutividadeColaboradorDia> relatorioDias = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(
                    "select * from func_relatorio_produtividade_remuneracao_acumulada_colaborador(" +
                            "f_cod_unidade => ?, " +
                            "f_cpf_colaborador => ?, " +
                            "f_data_inicial => ?, " +
                            "f_data_final => ?);");
            stmt.setLong(1, codUnidade);
            if (Filtros.isFiltroTodos(cpfColaborador)) {
                stmt.setNull(2, Types.BIGINT);
            } else {
                stmt.setLong(2, Long.parseLong(cpfColaborador));
            }
            stmt.setObject(3, dataInicial);
            stmt.setObject(4, dataFinal);
            rSet = stmt.executeQuery();
            String ultimoNomeColaborador = null, nomeColaboradorAtual;
            Long ultimoCpf = null, cpfAtual;
            while (rSet.next()) {
                cpfAtual = rSet.getLong("CPF_COLABORADOR");
                nomeColaboradorAtual = rSet.getString("NOME_COLABORADOR");
                if (ultimoCpf == null) {
                    ultimoCpf = cpfAtual;
                    ultimoNomeColaborador = nomeColaboradorAtual;
                } else if (!cpfAtual.equals(ultimoCpf)) {
                    // trocou de usuário
                    relatorioColaboradores.add(
                            createProdutividadeColaboradorRelatorio(ultimoCpf, ultimoNomeColaborador, relatorioDias));
                    relatorioDias = new ArrayList<>();
                    ultimoCpf = cpfAtual;
                    ultimoNomeColaborador = nomeColaboradorAtual;
                }
                relatorioDias.add(createProdutividadeColaboradorDia(rSet));
            }
            if (ultimoCpf != null) {
                relatorioColaboradores.add(
                        createProdutividadeColaboradorRelatorio(ultimoCpf, ultimoNomeColaborador, relatorioDias));
            }
        } finally {
            close(conn, stmt, rSet);
        }
        return relatorioColaboradores.stream()
                .sorted((produtividade1, produtividade2) -> {
                    final Colaborador colaborador1 = produtividade1.getColaborador();
                    final Colaborador colaborador2 = produtividade2.getColaborador();
                    final int nomeComparison = colaborador1.getNome().compareTo(colaborador2.getNome());
                    final int cpfComparison = colaborador1.getCpf().compareTo(colaborador2.getCpf());
                    return Integer.compare(nomeComparison, cpfComparison);
                })
                .collect(Collectors.toList());
    }

    @NotNull
    private PreparedStatement getConsolidadoProdutividade(@NotNull final Connection conn,
                                                          @NotNull final Long codUnidade,
                                                          @NotNull final Date dataInicial,
                                                          @NotNull final Date dataFinal) throws SQLException {
        final PreparedStatement stmt = conn.prepareStatement(
                "select * from func_relatorio_consolidado_produtividade(?, ?, ?)");
        stmt.setDate(1, dataInicial);
        stmt.setDate(2, dataFinal);
        stmt.setLong(3, codUnidade);
        return stmt;
    }

    private PreparedStatement getExtratoIndividualProdutividade(@NotNull final Connection conn,
                                                                @NotNull final String cpf,
                                                                @NotNull final Long codUnidade,
                                                                @NotNull final Date dataInicial,
                                                                @NotNull final Date dataFinal) throws SQLException {
        final PreparedStatement stmt = conn.prepareStatement("SELECT " +
                "   to_char(data, 'DD/MM/YYYY') AS \"DATA\", " +
                "   nome_colaborador AS \"COLABORADOR\", " +
                "   placa AS \"PLACA\", " +
                "   mapa AS \"MAPA\", " +
                "   cargaatual AS \"CARGA\", " +
                "   entrega AS \"ENTREGA\", " +
                "   fator AS \"FATOR\", " +
                "   trunc(cxentreg) AS \"CXS ENTREGUES\", " +
                "   entregascompletas + view_produtividade_extrato.entregasnaorealizadas + view_produtividade_extrato.entregasparciais AS \"ENTREGAS\", " +
                "   CASE WHEN cxentreg > 0 THEN round((valor / cxentreg) :: NUMERIC, 2) " +
                "     else 0 end AS \"VALOR/CX\", " +
                "   round(valor_rota :: NUMERIC, 2) AS \"VALOR ROTA\", " +
                "   round(valor_diferenca_eld :: NUMERIC, 2) AS \"DIFERENÇA ELD\", " +
                "   round(valor_as :: NUMERIC, 2) AS \"VALOR AS\", " +
                "   round(valor :: NUMERIC, 2) AS \"PRODUTIVIDADE TOTAL\" " +
                "   FROM view_produtividade_extrato " +
                "   WHERE cpf::TEXT LIKE ? AND data BETWEEN (? AT TIME ZONE ?) AND (? AT TIME ZONE ?) AND cod_unidade = ? " +
                "   ORDER BY data ASC;");
        final String zoneId = TimeZoneManager.getZoneIdForCodUnidade(codUnidade, conn).getId();
        stmt.setString(1, cpf);
        stmt.setDate(2, dataInicial);
        stmt.setString(3, zoneId);
        stmt.setDate(4, dataFinal);
        stmt.setString(5, zoneId);
        stmt.setLong(6, codUnidade);
        return stmt;
    }

    private PreparedStatement getAcessosProdutividadeStatement(Connection conn, String cpf, Long codUnidade,
                                                               Date dataInicial, Date dataFinal) throws SQLException {
        final PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM func_relatorio_acessos_produtividade_estratificado(?, ?, ?, ?);");
        stmt.setLong(1, codUnidade);
        stmt.setDate(2, dataInicial);
        stmt.setDate(3, dataFinal);
        stmt.setString(4, cpf);
        return stmt;
    }
}
