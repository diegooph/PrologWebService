package br.com.zalf.prolog.webservice.frota.pneu.afericao.relatorios;

import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.relatorios._model.AfericaoExportacaoProtheus;
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

    void getCronogramaAfericoesPlacasCsv(@NotNull final OutputStream out,
                                         @NotNull final List<Long> codUnidades,
                                         @NotNull final String userToken) throws Throwable;

    @NotNull
    Report getCronogramaAfericoesPlacasReport(@NotNull final List<Long> codUnidades,
                                              @NotNull final String userToken) throws Throwable;

    void getDadosGeraisAfericoesCsv(@NotNull final OutputStream out,
                                    @NotNull final List<Long> codUnidades,
                                    @NotNull final LocalDate dataInicial,
                                    @NotNull final LocalDate dataFinal) throws Throwable;

    @NotNull
    Report getDadosGeraisAfericoesReport(@NotNull final List<Long> codUnidades,
                                         @NotNull final LocalDate dataInicial,
                                         @NotNull final LocalDate dataFinal) throws Throwable;

    @NotNull
    List<AfericaoExportacaoProtheus> getExportacaoAfericoesProtheus(@NotNull final List<Long> codUnidades,
                                                                    @NotNull final List<Long> codVeiculos,
                                                                    @NotNull final LocalDate dataInicial,
                                                                    @NotNull final LocalDate dataFinal) throws Throwable;
}