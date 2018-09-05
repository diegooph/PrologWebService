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

    /**
     * Método para gerar um relatório contendo todos os dados de aferições realizadas em arquivo CSV.
     *
     * @param out         - Streaming onde os dados serão escritos.
     * @param codUnidades - Código da unidade pela qual as informações serão filtradas.
     * @param dataInicial - Data inicial do período de filtro.
     * @param dataFinal   - Data final do período de filtro.
     * @throws Throwable - Se algum erro ocorrer.
     */

    void getDadosGeraisMovimentacaoCsv(@NotNull final OutputStream out,
                                       @NotNull final List<Long> codUnidades,
                                       @NotNull final LocalDate dataInicial,
                                       @NotNull final LocalDate dataFinal) throws Throwable;

    /**
     * Método para gerar um relatório contendo todos os dados de aferições realizadas em arquivo CSV.
     *
     * @param codUnidades - Código da unidade pela qual as informações serão filtradas.
     * @param dataInicial - Data inicial do período de filtro.
     * @param dataFinal   - Data final do período de filtro.
     * @throws Throwable - Se algum erro ocorrer.
     */
    @NotNull
    Report getDadosGeraisMovimentacaoReport(@NotNull final List<Long> codUnidades,
                                            @NotNull final LocalDate dataInicial,
                                            @NotNull final LocalDate dataFinal) throws Throwable;
}

