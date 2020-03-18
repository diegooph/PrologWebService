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

    /**
     * Método para buscar o relatório de listagem de colaboradores em CSV.
     *
     * @param out         Streaming onde os dados serão escritos.
     * @param codUnidades Códigos das unidades para as quais as informações serão filtradas.
     * @throws Throwable Se algum erro ocorrer.
     */
    void getListagemColaboradoresByUnidadeCsv(@NotNull final OutputStream out,
                                              @NotNull final List<Long> codUnidades) throws Throwable;

    /**
     * Método para buscar o relatório de listagem de colaboradores em formato {@link Report report}.
     *
     * @param codUnidades Códigos das unidades para as quais as informações serão filtradas.
     * @throws Throwable Se algum erro ocorrer.
     */
    @NotNull
    Report getListagemColaboradoresByUnidadeReport(@NotNull final List<Long> codUnidades) throws Throwable;
}