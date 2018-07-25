package br.com.zalf.prolog.webservice.commons.report;

import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;

/**
 * Created on 19/07/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface CsvReport {
    @NotNull
    List<String> getHeader() throws SQLException;
    @NotNull
    Iterable<?> getData() throws SQLException;
}