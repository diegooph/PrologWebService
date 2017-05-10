package br.com.zalf.prolog.webservice.report;

import br.com.zalf.prolog.webservice.commons.Report;
import br.com.zalf.prolog.webservice.util.L;
import com.sun.istack.internal.NotNull;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe responsável por converter um {@link java.sql.ResultSet} em um {@link Report}.
 */
public class ReportTransformer {
    private static final String TAG = ReportTransformer.class.getSimpleName();

    private ReportTransformer() {
        // you shall not pass
    }

    public static @NotNull Report createReport(@NotNull final ResultSet resultSet) throws SQLException {
        if (resultSet.isClosed()) {
            throw new IllegalArgumentException("ResultSet can't be closed!!!");
        }

        final Report report = new Report();
        final List<List<String>> data = new ArrayList<>();

        final int columns = resultSet.getMetaData().getColumnCount();
        while (resultSet.next()) {
            final List<String> row = new ArrayList<>();
            for (int i = 1; i <= columns; i++) {
                row.add(resultSet.getString(i));
            }
            data.add(row);
        }

        report.setHeader(getHeader(resultSet.getMetaData()));
        report.setData(data);
        return report;
    }

    private static @NotNull List<String> getHeader(@NotNull final ResultSetMetaData metaData) throws SQLException {
        final List<String> header = new ArrayList<>();
        L.d(TAG, String.valueOf(metaData.getColumnCount()));
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            header.add(metaData.getColumnLabel(i));
        }
        return header;
    }
}