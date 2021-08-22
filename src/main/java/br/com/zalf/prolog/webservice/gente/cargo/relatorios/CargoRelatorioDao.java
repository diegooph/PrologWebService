package br.com.zalf.prolog.webservice.gente.cargo.relatorios;

import br.com.zalf.prolog.webservice.commons.report.Report;
import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;
import java.util.List;

/**
 * Created on 25/03/2020.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public interface CargoRelatorioDao {
    void getPermissoesDetalhadasCsv(@NotNull final OutputStream out, @NotNull final List<Long> codUnidades)
            throws Throwable;
    Report getPermissoesDetalhadasReport(@NotNull final List<Long> codUnidades) throws Throwable;
}