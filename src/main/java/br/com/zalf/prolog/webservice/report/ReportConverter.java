package br.com.zalf.prolog.webservice.report;

import br.com.zalf.prolog.commons.Report;
import com.sun.istack.internal.NotNull;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe responsável por converter um {@link java.sql.ResultSet} em um {@link Report}.
 */
public class ReportConverter {

    private ReportConverter() {
        // you shall not pass
    }

    public static @NotNull Report createReport(@NotNull ResultSet resultSet) throws SQLException {
        if (resultSet.isClosed())
            throw new IllegalArgumentException("ResultSet can't be null!!!");

        Report report = new Report();
        List<List<String>> data = new ArrayList<>();

        int columns = resultSet.getMetaData().getColumnCount();
        while (resultSet.next()) {
            List<String> row = new ArrayList<>();
            for (int i = 1; i < columns; i++) {
                row.add(resultSet.getString(i));
            }
            data.add(row);
        }

        report.setHeader(getHeader(resultSet.getMetaData()));
        report.setData(data);
        return report;
    }

    private static @NotNull List<String> getHeader(@NotNull ResultSetMetaData metaData) throws SQLException {
        List<String> header = new ArrayList<>();
        for (int i = 1; i < metaData.getColumnCount(); i++) {
            header.add(metaData.getColumnLabel(i));
        }
        return header;
    }
}