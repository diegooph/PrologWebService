package br.com.zalf.prolog.webservice.raizen.produtividade.relatorios;

import br.com.zalf.prolog.webservice.commons.report.CsvWriter;
import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

import static br.com.zalf.prolog.webservice.database.DatabaseConnection.closeConnection;
import static br.com.zalf.prolog.webservice.database.DatabaseConnection.getConnection;

/**
 * Created on 31/07/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class RaizenProdutividadeRelatorioServiceDaoImpl implements RaizenProdutividadeRelatorioServiceDao {
    @Override
    public void getDadosGeraisProdutividadeCsv(@NotNull final OutputStream out,
                                               @NotNull final LocalDate dataInicial,
                                               @NotNull final LocalDate dataFinal) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

}
