package br.com.zalf.prolog.webservice.gente.quiz.relatorios;

import br.com.zalf.prolog.webservice.commons.report.Report;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.OutputStream;
import java.time.LocalDate;

/**
 * Created on 17/01/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface QuizRelatorioDao {

    void getEstratificacaoRealizacaoQuizCsv(@NotNull final OutputStream out,
                                            @NotNull final Long codUnidade,
                                            @Nullable final Long codModeloQuiz,
                                            @NotNull final LocalDate dataInicial,
                                            @NotNull final LocalDate dataFinal) throws Throwable;

    @NotNull
    Report getEstratificacaoRealizacaoQuizReport(@NotNull final Long codUnidade,
                                                 @Nullable final Long codModeloQuiz,
                                                 @NotNull final LocalDate dataInicial,
                                                 @NotNull final LocalDate dataFinal) throws Throwable;

    void getRealizacaoQuizByCargoCsv(@NotNull final OutputStream out,
                                     @NotNull final Long codUnidade,
                                     @Nullable final Long codModeloQuiz) throws Throwable;

    @NotNull
    Report getRealizacaoQuizByCargoReport(@NotNull final Long codUnidade,
                                          @Nullable final Long codModeloQuiz) throws Throwable;

    void getEstratificacaoQuizRespostasCsv(@NotNull final OutputStream out,
                                           @NotNull final Long codUnidade,
                                           @Nullable final Long codModeloQuiz) throws Throwable;

    @NotNull
    Report getEstratificacaoQuizRespostasReport(@NotNull final Long codUnidade,
                                                @Nullable final Long codModeloQuiz) throws Throwable;

    void getExtratoGeralCsv(@NotNull final OutputStream out,
                            @NotNull final Long codUnidade,
                            @NotNull final LocalDate dataInicial,
                            @NotNull final LocalDate dataFinal) throws Throwable;

    @NotNull
    Report getExtratoGeralReport(@NotNull final Long codUnidade,
                                 @NotNull final LocalDate dataInicial,
                                 @NotNull final LocalDate dataFinal) throws Throwable;

    void getRespostasRealizadosCsv(@NotNull final OutputStream out,
                                   @NotNull final Long codUnidade,
                                   @Nullable final Long codModeloQuiz,
                                   @Nullable final Long cpfColaborador,
                                   @NotNull final LocalDate dataInicial,
                                   @NotNull final LocalDate dataFinal,
                                   final boolean apenasSelecionadas) throws Throwable;

    @NotNull
    Report getRespostasRealizadosReport(@NotNull final Long codUnidade,
                                        @Nullable final Long codModeloQuiz,
                                        @Nullable final Long cpfColaborador,
                                        @NotNull final LocalDate dataInicial,
                                        @NotNull final LocalDate dataFinal,
                                        final boolean apenasSelecionadas) throws Throwable;
}