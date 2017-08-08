package br.com.zalf.prolog.webservice.entrega.produtividade.relatorio;

import br.com.zalf.prolog.webservice.DatabaseConnection;
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
        PreparedStatement stmt = conn.prepareStatement("select * from func_relatorio_consolidado_produtividade(?, ?, ?)");
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
        PreparedStatement stmt = conn.prepareStatement("SELECT\n" +
                "   to_char(data, 'DD/MM/YYYY') AS \"DATA\",\n" +
                "  nome_colaborador AS \"COLABORADOR\",\n" +
                "   placa AS \"PLACA\",\n" +
                "   mapa AS \"MAPA\",\n" +
                "   cargaatual AS \"CARGA\",\n" +
                "   entrega AS \"ENTREGA\",\n" +
                "   fator AS \"FATOR\",\n" +
                "   trunc(cxentreg) AS \"CXS ENTREGUES\",\n" +
                "   entregascompletas + view_produtividade_extrato.entregasnaorealizadas + view_produtividade_extrato.entregasparciais AS \"ENTREGAS\",\n" +
                "   CASE WHEN cxentreg > 0 THEN round((valor / cxentreg) :: NUMERIC, 2)\n" +
                "     else 0 end AS \"VALOR/CX\",\n" +
                "   round(valor_rota :: NUMERIC, 2) AS \"VALOR ROTA\",\n" +
                "   round(valor_diferenca_eld :: NUMERIC, 2) AS \"DIFERENÇA ELD\",\n" +
                "   round(valor_as :: NUMERIC, 2) AS \"VALOR AS\",\n" +
                "   round(valor :: NUMERIC, 2) AS \"PRODUTIVIDADE TOTAL\"\n" +
                "   FROM view_produtividade_extrato\n" +
                "   WHERE cpf::TEXT LIKE ? AND data BETWEEN ? AND ? AND cod_unidade = ?\n" +
                "   ORDER BY data ASC;");
        stmt.setString(1, cpf);
        stmt.setDate(2, dataInicial);
        stmt.setDate(3, dataFinal);
        stmt.setLong(4, codUnidade);
        return stmt;
    }


}
