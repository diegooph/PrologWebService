package br.com.zalf.prolog.webservice.commons.report;

import java.sql.SQLException;
import java.util.List;

/**
 * Created on 19/07/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface Transposable {
    List<String> getHeader() throws SQLException;
    Iterable<?> transpose() throws SQLException;
}
