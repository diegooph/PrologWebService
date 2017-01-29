package br.com.zalf.prolog.webservice;


import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Classe responsável por escrever informações onde quer que seja requisitado em formato .csv.
 */
public class CsvWriter {

    /**
     * @param resultSet do qual as informações serão extraídas
     * @param outputStream na qual as informações serão escritas
     * @throws IOException se um erro I/O ocorrer
     * @throws SQLException se um erro de acesso ao banco de dados ocorrer
     */
    public void write(@NotNull ResultSet resultSet, @NotNull OutputStream outputStream) throws IOException, SQLException {
        if (resultSet.isClosed())
            throw new IllegalArgumentException("ResultSet can't be closed!!!");

        final Appendable out = new OutputStreamWriter(outputStream);
        final CSVPrinter printer = CSVFormat.DEFAULT.withHeader(resultSet).print(out);
        printer.printRecords(resultSet);
        resultSet.close();
    }
}