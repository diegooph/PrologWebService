package br.com.zalf.prolog.webservice.frota.pneu.relatorios;

import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.frota.pneu._model.StatusPneu;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.QtdDiasAfericoesVencidas;
import br.com.zalf.prolog.webservice.frota.pneu.relatorios._model.*;
import br.com.zalf.prolog.webservice.frota.pneu.servico._model.TipoServico;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Created by didi on 9/15/16.
 */
public interface RelatorioPneuDao {

    void getFarolAfericaoCsv(@NotNull final OutputStream outputStream,
                             @NotNull final List<Long> codUnidades,
                             @NotNull final LocalDate dataInicial,
                             @NotNull final LocalDate dataFinal) throws Throwable;

    void getPneusComDesgasteIrregularCsv(@NotNull final OutputStream outputStream,
                                         @NotNull final List<Long> codUnidades,
                                         @Nullable final StatusPneu statusPneu) throws Throwable;

    @NotNull
    Report getPneusComDesgasteIrregularReport(@NotNull final List<Long> codUnidades,
                                              @Nullable final StatusPneu statusPneu) throws Throwable;

    void getStatusAtualPneusCsv(@NotNull final OutputStream outputStream,
                                @NotNull final List<Long> codUnidades) throws Throwable;

    @NotNull
    Report getStatusAtualPneusReport(@NotNull final List<Long> codUnidades) throws Throwable;

    void getKmRodadoPorPneuPorVidaEmLinhasCsv(@NotNull final OutputStream outputStream,
                                              @NotNull final List<Long> codUnidades) throws Throwable;

    @NotNull
    Report getKmRodadoPorPneuPorVidaEmLinhasReport(@NotNull final List<Long> codUnidades) throws Throwable;

    void getKmRodadoPorPneuPorVidaEmColunasCsv(@NotNull final OutputStream outputStream,
                                               @NotNull final List<Long> codUnidades) throws Throwable;

    void getAfericoesAvulsasCsv(@NotNull final OutputStream outputStream,
                                @NotNull final List<Long> codUnidades,
                                @NotNull final LocalDate dataInicial,
                                @NotNull final LocalDate dataFinal) throws Throwable;

    @NotNull
    Report getAfericoesAvulsasReport(@NotNull final List<Long> codUnidades,
                                     @NotNull final LocalDate dataInicial,
                                     @NotNull final LocalDate dataFinal) throws Throwable;

    List<Faixa> getQtdPneusByFaixaSulco(@NotNull final List<Long> codUnidades,
                                        @NotNull final List<String> status) throws SQLException;

    void getPrevisaoTrocaEstratificadoCsv(@NotNull final OutputStream outputStream,
                                          @NotNull final List<Long> codUnidades,
                                          @NotNull final LocalDate dataInicial,
                                          @NotNull final LocalDate dataFinal) throws SQLException, IOException;

    Report getPrevisaoTrocaEstratificadoReport(@NotNull final List<Long> codUnidades,
                                               @NotNull final LocalDate dataInicial,
                                               @NotNull final LocalDate dataFinal) throws SQLException;

    void getPrevisaoTrocaConsolidadoCsv(@NotNull final OutputStream outputStream,
                                        @NotNull final List<Long> codUnidades,
                                        @NotNull final LocalDate dataInicial,
                                        @NotNull final LocalDate dataFinal) throws IOException, SQLException;

    Report getPrevisaoTrocaConsolidadoReport(@NotNull final List<Long> codUnidades,
                                             @NotNull final LocalDate dataInicial,
                                             @NotNull final LocalDate dataFinal) throws SQLException;

    void getAderenciaPlacasCsv(@NotNull final OutputStream outputStream,
                               @NotNull final List<Long> codUnidades,
                               @NotNull final LocalDate dataInicial,
                               @NotNull final LocalDate dataFinal) throws IOException, SQLException;

    Report getAderenciaPlacasReport(@NotNull final List<Long> codUnidades,
                                    @NotNull final LocalDate dataInicial,
                                    @NotNull final LocalDate dataFinal) throws SQLException;

    Report getPneusDescartadosReport(@NotNull final List<Long> codUnidades,
                                     @NotNull final LocalDate dataInicial,
                                     @NotNull final LocalDate dataFinal) throws SQLException;

    void getPneusDescartadosCsv(@NotNull final OutputStream outputStream,
                                @NotNull final List<Long> codUnidades,
                                @NotNull final LocalDate dataInicial,
                                @NotNull final LocalDate dataFinal) throws IOException, SQLException;

    void getDadosUltimaAfericaoCsv(@NotNull final OutputStream outputStream,
                                   @NotNull final List<Long> codUnidades) throws SQLException, IOException;

    Report getDadosUltimaAfericaoReport(@NotNull final List<Long> codUnidades) throws SQLException;

    void getResumoGeralPneusCsv(@NotNull final OutputStream outputStream,
                                @NotNull final List<Long> codUnidades,
                                @Nullable final String status) throws SQLException, IOException;

    Report getResumoGeralPneusReport(@NotNull final List<Long> codUnidades,
                                     @Nullable final String status) throws SQLException;

    @Deprecated
    List<Aderencia> getAderenciaByUnidade(int ano, int mes, Long codUnidade) throws Throwable;

    @Deprecated
    List<Faixa> getQtPneusByFaixaPressao(List<String> codUnidades, List<String> status) throws Throwable;

    Map<StatusPneu, Integer> getQtdPneusByStatus(@NotNull final List<Long> codUnidades) throws SQLException;

    List<QuantidadeAfericao> getQtdAfericoesByTipoByData(@NotNull final List<Long> codUnidades,
                                                         @NotNull final Date dataInicial,
                                                         @NotNull final Date dataFinal) throws Throwable;

    Map<TipoServico, Integer> getServicosEmAbertoByTipo(@NotNull final List<Long> codUnidades) throws SQLException;

    StatusPlacasAfericao getStatusPlacasAfericao(@NotNull final List<Long> codUnidades) throws SQLException;

    Map<TipoServico, Integer> getMediaTempoConsertoServicoPorTipo(@NotNull final List<Long> codUnidades) throws
            SQLException;

    Map<String, Integer> getQtdKmRodadoComServicoEmAberto(@NotNull final List<Long> codUnidades) throws SQLException;

    Map<String, Integer> getPlacasComPneuAbaixoLimiteMilimetragem(@NotNull final List<Long> codUnidades) throws
            SQLException;

    int getQtdPneusPressaoIncorreta(@NotNull final List<Long> codUnidades) throws SQLException;

    @NotNull
    List<SulcoPressao> getMenorSulcoEPressaoPneus(@NotNull final List<Long> codUnidades) throws Throwable;

    Map<String, Integer> getQtdPneusDescartadosPorMotivo(@NotNull final List<Long> codUnidades) throws SQLException;

    @NotNull
    List<QtdDiasAfericoesVencidas> getQtdDiasAfericoesVencidas(@NotNull final List<Long> codUnidades) throws Throwable;

    @NotNull
    List<QuantidadeAfericao> getQtdAfericoesRealizadasPorDiaByTipo(
            @NotNull final List<Long> codUnidades,
            final int diasRetroativosParaBuscar) throws Throwable;

    @NotNull
    Report getVencimentoDotReport(@NotNull final List<Long> codUnidades,
                                  @NotNull final String userToken) throws Throwable;

    void getVencimentoDotCsv(@NotNull final OutputStream out,
                             @NotNull final List<Long> codUnidades,
                             @NotNull final String userToken) throws Throwable;

    void getCpkPorMarcaModeloDimensaomCsv(@NotNull final OutputStream outputStream,
                                          @NotNull final List<Long> codUnidades) throws Throwable;
}