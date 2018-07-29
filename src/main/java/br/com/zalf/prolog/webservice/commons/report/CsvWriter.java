package br.com.zalf.prolog.webservice.commons.report;


import com.google.common.base.Preconditions;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Classe responsável por escrever informações onde quer que seja requisitado em formato .csv.
 */
public class CsvWriter {
    private static final char DEFAULT_DELIMITER = ';';
    private ResultSet rSet;
    private OutputStream outputStream;
    private char delimiter;
    private CsvReport csvReport;

    public CsvWriter() {

    }

    private CsvWriter(@NotNull final Builder builder) {
        this.rSet = builder.rSet;
        this.outputStream = builder.outputStream;
        this.delimiter = builder.delimiter;
        this.csvReport = builder.csvReport;
    }

    /**
     * Método para escrita de informações em um arquivo de extensõa '.csv'.
     * Deve ser invocado após o {@link Builder}.
     *
     * @throws IOException  - se um erro I/O ocorrer.
     * @throws SQLException - se um erro de acesso ao banco de dados ocorrer.
     */
    public void write() throws SQLException, IOException {
        Preconditions.checkNotNull(outputStream, "outputStream não pode ser null");

        final Appendable out = new OutputStreamWriter(outputStream);

        final CSVPrinter printer;
        if (rSet != null) {
            if (rSet.isClosed()) {
                throw new IllegalArgumentException("ResultSet can't be closed!!!");
            }
            printer = CSVFormat.DEFAULT
                    .withDelimiter(delimiter)
                    .withHeader(rSet)
                    .print(out);
            printer.printRecords(rSet);
        } else {
            printer = CSVFormat.DEFAULT
                    .withDelimiter(delimiter)
                    .withHeader(csvReport.getHeader().toArray(new String[0]))
                    .print(out);
            printer.printRecords(csvReport.getData());
        }
        printer.flush();
    }

    /**
     * Método para escrita de informações em um arquivo de extensõa '.csv'.
     *
     * @param resultSet    - {@link ResultSet} do qual as informações serão extraídas.
     * @param outputStream - {@link OutputStream} na qual as informações serão escritas.
     * @throws IOException  - se um erro I/O ocorrer.
     * @throws SQLException - se um erro de acesso ao banco de dados ocorrer.
     */
    public void write(@NotNull final ResultSet resultSet,
                      @NotNull final OutputStream outputStream) throws IOException, SQLException {
        if (resultSet.isClosed()) {
            throw new IllegalArgumentException("ResultSet can't be closed!!!");
        }

        this.rSet = resultSet;
        this.outputStream = outputStream;
        this.delimiter = DEFAULT_DELIMITER;
        write();
    }

    public static class Builder {
        private ResultSet rSet;
        private OutputStream outputStream;
        private char delimiter;
        private CsvReport csvReport;

        public Builder(@NotNull final OutputStream outputStream) {
            this.outputStream = outputStream;
            this.delimiter = DEFAULT_DELIMITER;
        }

        @NotNull
        public Builder withDelimiter(final char delimiter) {
            this.delimiter = delimiter;
            return this;
        }

        @NotNull
        public Builder withCsvReport(@NotNull final CsvReport csvReport) {
            this.csvReport = csvReport;
            return this;
        }

        @NotNull
        public Builder withResultSet(@NotNull final ResultSet resultSet) {
            rSet = resultSet;
            return this;
        }

        @NotNull
        public CsvWriter build() {
            return new CsvWriter(this);
        }
    }
}