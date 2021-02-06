package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.relatorios;

import br.com.zalf.prolog.webservice.commons.report.Report;
import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;
import java.time.LocalDate;
import java.util.List;

/**
 * Created on 04/09/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public interface MovimentacaoRelatorioDao {

    void getDadosGeraisMovimentacoesCsv(@NotNull final OutputStream out,
                                        @NotNull final List<Long> codUnidades,
                                        @NotNull final LocalDate dataInicial,
                                        @NotNull final LocalDate dataFinal) throws Throwable;

    @NotNull
    Report getDadosGeraisMovimentacoesReport(@NotNull final List<Long> codUnidades,
                                             @NotNull final LocalDate dataInicial,
                                             @NotNull final LocalDate dataFinal) throws Throwable;
}