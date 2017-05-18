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
        PreparedStatement stmt = conn.prepareStatement("SELECT\n" +
                "  nome_colaborador,\n" +
                "  funcao,\n" +
                "  count(mapa)                     AS mapas,\n" +
                "  trunc(sum(cxentreg))            AS cxs_entregues,\n" +
                "  trunc(sum(valor) :: NUMERIC, 2) AS valor_total\n" +
                "FROM view_produtividade_extrato\n" +
                "WHERE cod_unidade = ? AND data BETWEEN ? AND ? \n" +
                "GROUP BY nome_colaborador, funcao\n" +
                "ORDER BY funcao, nome_colaborador");
        stmt.setLong(1, codUnidade);
        stmt.setDate(2, dataInicial);
        stmt.setDate(3, dataFinal);
        return stmt;
    }


    @Override
    public void getExtratoIndividualProdutividadeCsv(@NotNull OutputStream outputStream, @NotNull Long cpf, @NotNull Date dataInicial, @NotNull Date dataFinal) throws SQLException, IOException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getExtratoIndividualProdutividade(conn, cpf, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, outputStream);
        }finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public Report getExtratoIndividualProdutividadeReport(@NotNull Long cpf, @NotNull Date dataInicial, @NotNull Date dataFinal) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getExtratoIndividualProdutividade(conn, cpf, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        }finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    private PreparedStatement getExtratoIndividualProdutividade(Connection conn, Long cpf, Date dataInicial, Date dataFinal) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT\n" +
                "  data,\n" +
                "  placa,\n" +
                "  mapa,\n" +
                "  cargaatual,\n" +
                "  entrega,\n" +
                "  fator,\n" +
                "  cxentreg,\n" +
                "  entregascompletas + view_produtividade_extrato.entregasnaorealizadas + view_produtividade_extrato.entregasparciais AS entregas,\n" +
                "  round((valor / cxentreg) :: NUMERIC, 2) AS valor_cx,\n" +
                "  round(valor :: NUMERIC, 2) AS valor_total\n" +
                "FROM view_produtividade_extrato \n" +
                "WHERE cpf = ? AND data BETWEEN ? AND ? \n" +
                "ORDER BY data ASC;");
        stmt.setLong(1, cpf);
        stmt.setDate(2, dataInicial);
        stmt.setDate(3, dataFinal);
        return stmt;
    }


}
