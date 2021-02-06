package br.com.zalf.prolog.webservice.gente.colaborador.relatorios;

import br.com.zalf.prolog.webservice.commons.report.Report;
import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;
import java.util.List;

/**
 * Created on 05/04/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public interface ColaboradorRelatorioDao {

    void getListagemColaboradoresByUnidadeCsv(@NotNull final OutputStream out,
                                              @NotNull final List<Long> codUnidades) throws Throwable;

    @NotNull
    Report getListagemColaboradoresByUnidadeReport(@NotNull final List<Long> codUnidades) throws Throwable;
}