package br.com.zalf.prolog.webservice.entrega.produtividade.relatorio;

import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.TimeZoneManager;
import br.com.zalf.prolog.webservice.commons.CsvWriter;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.report.ReportTransformer;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.*;

/**
 * Created by Zart on 18/05/2017.
 */
public class ProdutividadeRelatorioDaoImpl extends DatabaseConnection implements ProdutividadeRelatorioDao {

    public ProdutividadeRelatorioDaoImpl() {

    }

    @Override
    public void getConsolidadoProdutividadeCsv(@NotNull OutputStream outputStream,
                                               @NotNull Long codUnidade,
                                               @NotNull Date dataInicial,
                                               @NotNull Date dataFinal) throws SQLException, IOException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getConsolidadoProdutividade(conn, codUnidade, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, outputStream);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public Report getConsolidadoProdutividadeReport(@NotNull Long codUnidade,
                                                    @NotNull Date dataInicial,
                                                    @NotNull Date dataFinal) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getConsolidadoProdutividade(conn, codUnidade, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @NotNull
    private PreparedStatement getConsolidadoProdutividade(Connection conn, Long codUnidade, Date dataInicial, Date dataFinal)
            throws SQLException {
        final PreparedStatement stmt = conn.prepareStatement("select * from func_relatorio_consolidado_produtividade(?, ?, ?)");
        stmt.setDate(1, dataInicial);
        stmt.setDate(2, dataFinal);
        stmt.setLong(3, codUnidade);
        return stmt;
    }


    @Override
    public void getExtratoIndividualProdutividadeCsv(@NotNull OutputStream outputStream, @NotNull String cpf,
                                                     @NotNull Long codUnidade, @NotNull Date dataInicial,
                                                     @NotNull Date dataFinal) throws SQLException, IOException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getExtratoIndividualProdutividade(conn, cpf, codUnidade, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, outputStream);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public Report getExtratoIndividualProdutividadeReport(@NotNull String cpf, @NotNull Long codUnidade,
                                                          @NotNull Date dataInicial, @NotNull Date dataFinal) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getExtratoIndividualProdutividade(conn, cpf, codUnidade, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    private PreparedStatement getExtratoIndividualProdutividade(Connection conn, String cpf, Long codUnidade,
                                                                Date dataInicial, Date dataFinal) throws SQLException {
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

    @Override
    public void getAcessosProdutividadeCsv(OutputStream outputStream, String cpf, Long codUnidade, Date dataInicial, Date dataFinal)
            throws SQLException, IOException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getAcessosProdutividadeStatement(conn, cpf, codUnidade, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, outputStream);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public Report getAcessosProdutividadeReport(String cpf, Long codUnidade, Date dataInicial, Date dataFinal) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getAcessosProdutividadeStatement(conn, cpf, codUnidade, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    private PreparedStatement getAcessosProdutividadeStatement(Connection conn, String cpf, Long codUnidade,
                                                               Date dataInicial, Date dataFinal) throws SQLException {
        final PreparedStatement stmt = conn.prepareStatement("SELECT * FROM func_relatorio_acessos_produtividade_estratificado" +
                "(?, ?, ?, ?);");
        stmt.setLong(1, codUnidade);
        stmt.setDate(2, dataInicial);
        stmt.setDate(3, dataFinal);
        stmt.setString(4, cpf);
        return stmt;
    }
}
