package br.com.zalf.prolog.webservice.raizen.produtividade.relatorios;

import br.com.zalf.prolog.webservice.commons.report.Report;
import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;
import java.time.LocalDate;

/**
 * Created on 31/07/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public interface RaizenProdutividadeRelatorioDao {

    /**
     * Método para gerar um relatório contendo todos os dados da produtividade em arquivo CSV.
     *
     * @param out         - Streaming onde os dados serão escritos.
     * @param codEmpresa  - Código da empresa pela qual as informações serão filtradas.
     * @param dataInicial - Data inicial do período de filtro.
     * @param dataFinal   - Data final do período de filtro.
     * @throws Throwable - Se algum erro ocorrer.
     */
    void getDadosGeraisProdutividadeCsv(@NotNull final OutputStream out,
                                        @NotNull final Long codEmpresa,
                                        @NotNull final LocalDate dataInicial,
                                        @NotNull final LocalDate dataFinal) throws Throwable;

    /**
     * Método para gerar um relatório contendo todos os dados da produtividade em arquivo CSV.
     *
     * @param codEmpresa  - Código da empresa pela qual as informações serão filtradas.
     * @param dataInicial - Data inicial do período de filtro.
     * @param dataFinal   - Data final do período de filtro.
     * @throws Throwable - Se algum erro ocorrer.
     */
    @NotNull
    Report getDadosGeraisProdutividadeReport(@NotNull final Long codEmpresa,
                                             @NotNull final LocalDate dataInicial,
                                             @NotNull final LocalDate dataFinal) throws Throwable;

}