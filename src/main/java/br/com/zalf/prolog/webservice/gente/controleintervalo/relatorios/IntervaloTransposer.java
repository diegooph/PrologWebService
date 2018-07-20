package br.com.zalf.prolog.webservice.gente.controleintervalo.relatorios;

import br.com.zalf.prolog.webservice.commons.report.Transposer;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created on 19/07/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class IntervaloTransposer extends Transposer {

    private static final String ZERO_HORAS = "00:00:00";
    private List<String> header;
    private List<List<String>> table;

    public IntervaloTransposer(@NotNull final ResultSet rSet) {
        super(rSet);
    }

    @Override
    public List<String> getHeader() throws SQLException {
        if (header == null) {
            processResultSet();
        }
        return header;
    }

    @Override
    public Iterable<?> transpose() throws SQLException {
        if (table == null) {
            processResultSet();
        }
        return table;
    }

    private void processResultSet() throws SQLException {
    }
}
