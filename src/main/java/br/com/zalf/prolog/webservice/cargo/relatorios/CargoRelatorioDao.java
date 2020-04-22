package br.com.zalf.prolog.webservice.cargo.relatorios;

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

    /**
     * Método para gerar um relatório contendo as permissões detalhadas em arquivo CSV.
     *
     * @param out         Streaming onde os dados serão escritos.
     * @param codUnidades Códigos das unidades pela quais as informações serão filtradas.
     * @throws Throwable Se algum erro ocorrer.
     */
    void getPermissoesDetalhadasCsv(@NotNull final OutputStream out, @NotNull final List<Long> codUnidades)
            throws Throwable;

    /**
     * Método para gerar um relatório contendo todos as permissões detalhadas em formato {@link Report report}.
     *
     * @param codUnidades Códigos das unidades pela quais as informações serão filtradas.
     * @throws Throwable Se algum erro ocorrer.
     */
    Report getPermissoesDetalhadasReport(@NotNull final List<Long> codUnidades) throws Throwable;
}
