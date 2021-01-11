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

    void getDadosGeraisProdutividadeCsv(@NotNull final OutputStream out,
                                        @NotNull final Long codUnidade,
                                        @NotNull final LocalDate dataInicial,
                                        @NotNull final LocalDate dataFinal) throws Throwable;

    @NotNull
    Report getDadosGeraisProdutividadeReport(@NotNull final Long codUnidade,
                                             @NotNull final LocalDate dataInicial,
                                             @NotNull final LocalDate dataFinal) throws Throwable;

}