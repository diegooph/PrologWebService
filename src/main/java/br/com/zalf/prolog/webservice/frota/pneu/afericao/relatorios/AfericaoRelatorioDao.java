package br.com.zalf.prolog.webservice.frota.pneu.afericao.relatorios;

import br.com.zalf.prolog.webservice.commons.report.Report;
import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;
import java.time.LocalDate;
import java.util.List;

/**
 * Created on 30/08/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public interface AfericaoRelatorioDao {

    /**
     * Método para buscar o relatório de cronograma das aferições de placas em CSV.
     *
     * @param out         Streaming onde os dados serão escritos.
     * @param codUnidades Códigos das unidades pela quais as informações serão filtradas.
     * @param userToken   Código token do usuário que requisitou o relatório.
     * @throws Throwable Se algum erro ocorrer.
     */
    void getCronogramaAfericoesPlacasCsv(@NotNull final OutputStream out,
                                         @NotNull final List<Long> codUnidades,
                                         @NotNull final String userToken) throws Throwable;

    /**
     * Método para buscar o relatório de cronograma das aferições de placas em formato {@link Report report}.
     *
     * @param codUnidades Códigos das unidades pela quais as informações serão filtradas.
     * @param userToken   Código token do usuário que requisitou o relatório.
     * @throws Throwable Se algum erro ocorrer.
     */
    @NotNull
    Report getCronogramaAfericoesPlacasReport(@NotNull final List<Long> codUnidades,
                                              @NotNull final String userToken) throws Throwable;

    /**
     * Método para gerar um relatório contendo todos os dados de aferições realizadas em arquivo CSV.
     *
     * @param out         Streaming onde os dados serão escritos.
     * @param codUnidades Códigos das unidades pela quais as informações serão filtradas.
     * @param dataInicial Data inicial do período de filtro.
     * @param dataFinal   Data final do período de filtro.
     * @throws Throwable Se algum erro ocorrer.
     */
    void getDadosGeraisAfericoesCsv(@NotNull final OutputStream out,
                                    @NotNull final List<Long> codUnidades,
                                    @NotNull final LocalDate dataInicial,
                                    @NotNull final LocalDate dataFinal) throws Throwable;

    /**
     * Método para gerar um relatório contendo todos os dados de aferições realizadas em formato {@link Report report}.
     *
     * @param codUnidades Códigos das unidades pela quais as informações serão filtradas.
     * @param dataInicial Data inicial do período de filtro.
     * @param dataFinal   Data final do período de filtro.
     * @throws Throwable Se algum erro ocorrer.
     */
    @NotNull
    Report getDadosGeraisAfericoesReport(@NotNull final List<Long> codUnidades,
                                         @NotNull final LocalDate dataInicial,
                                         @NotNull final LocalDate dataFinal) throws Throwable;
}