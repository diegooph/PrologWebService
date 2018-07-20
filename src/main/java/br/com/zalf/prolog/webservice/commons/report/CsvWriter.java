package br.com.zalf.prolog.webservice.commons.report;


import com.google.common.base.Preconditions;
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
    private static final char DEFAULT_DELIMITER = ';';
    private ResultSet rSet;
    private OutputStream outputStream;
    private char delimiter;
    private Transposable transposer;

    public CsvWriter() {
    }

    private CsvWriter(@NotNull final Builder builder) {
        this.rSet = builder.rSet;
        this.outputStream = builder.outputStream;
        this.delimiter = builder.delimiter;
        this.transposer = builder.transposer;
    }

    /**
     * Método para escrita de informações em um arquivo de extensõa '.csv'.
     * Deve ser invocado após o {@link Builder}.
     *
     * @throws IOException  - se um erro I/O ocorrer.
     * @throws SQLException - se um erro de acesso ao banco de dados ocorrer.
     */
    public void write() throws SQLException, IOException {
        Preconditions.checkNotNull(rSet, "rSet não pode ser null");
        Preconditions.checkNotNull(outputStream, "outputStream não pode ser null");

        if (rSet.isClosed()) {
            throw new IllegalArgumentException("ResultSet can't be closed!!!");
        }

        if (transposer == null) {
            write(rSet, outputStream, delimiter);
        } else {
            write(outputStream, transposer, delimiter);
        }
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

        write(resultSet, outputStream, DEFAULT_DELIMITER);
    }

    private void write(@NotNull final ResultSet resultSet,
                       @NotNull final OutputStream outputStream,
                       final char delimiter) throws IOException, SQLException {
        final Appendable out = new OutputStreamWriter(outputStream);
        final CSVPrinter printer = CSVFormat.DEFAULT
                .withDelimiter(delimiter)
                .withHeader(resultSet)
                .print(out);
        printer.printRecords(resultSet);
        printer.printRecord();
        printer.flush();
    }

    private void write(@NotNull final OutputStream outputStream,
                       @NotNull final Transposable transposer,
                       final char delimiter) throws IOException, SQLException {
        final Appendable out = new OutputStreamWriter(outputStream);
        final CSVPrinter printer = CSVFormat.DEFAULT
                .withDelimiter(delimiter)
                .withHeader(transposer.getHeader().toString())
                .print(out);
        printer.printRecords(transposer.transpose());
        printer.printRecord();
        printer.flush();
    }

    public static class Builder {
        private ResultSet rSet;
        private OutputStream outputStream;
        private char delimiter;
        private Transposable transposer;

        public Builder(@NotNull final ResultSet rSet, @NotNull final OutputStream outputStream) {
            this.rSet = rSet;
            this.outputStream = outputStream;
            this.delimiter = DEFAULT_DELIMITER;
        }

        @NotNull
        public Builder withDelimiter(final char delimiter) {
            this.delimiter = delimiter;
            return this;
        }

        @NotNull
        public Builder withTransposer(@NotNull final Transposable transposer) {
            this.transposer = transposer;
            return this;
        }

        @NotNull
        public CsvWriter build() {
            return new CsvWriter(this);
        }
    }
}