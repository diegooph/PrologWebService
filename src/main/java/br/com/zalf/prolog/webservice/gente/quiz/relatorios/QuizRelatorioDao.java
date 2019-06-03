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

    /**
     * Método que busca gera um arquivo CSV contendo informações de realização de
     * quizzes, estratificados por modelo e unidade.
     *
     * @param out           Local onde o csv será escrito
     * @param codUnidade    Código da unidade a qual será filtrado os dados
     * @param codModeloQuiz Código do modelo de quiz
     * @param dataInicial   Data incial do período de busca
     * @param dataFinal     Data final do período de busca
     * @throws Throwable Caso algum erro ocorra
     */
    void getEstratificacaoRealizacaoQuizCsv(@NotNull final OutputStream out,
                                            @NotNull final Long codUnidade,
                                            @Nullable final Long codModeloQuiz,
                                            @NotNull final LocalDate dataInicial,
                                            @NotNull final LocalDate dataFinal) throws Throwable;

    /**
     * Método que cria um {@link Report} contendo informações de realização de
     * quizzes, estratificados por modelo e unidade.
     *
     * @param codUnidade    Código da unidade a qual será filtrado os dados
     * @param codModeloQuiz Código do modelo de quiz
     * @param dataInicial   Data incial do período de busca
     * @param dataFinal     Data final do período de busca
     * @return Um objeto {@link Report} contendo as informações filtradas
     * @throws Throwable Caso algum erro ocorra
     */
    @NotNull
    Report getEstratificacaoRealizacaoQuizReport(@NotNull final Long codUnidade,
                                                 @Nullable final Long codModeloQuiz,
                                                 @NotNull final LocalDate dataInicial,
                                                 @NotNull final LocalDate dataFinal) throws Throwable;

    /**
     * Método que cria um arquivo CSV contendo informações de realizações de quiz estratificados
     * por cargos da unidade.
     *
     * @param out           Local onde o csv será escrito
     * @param codUnidade    Código da unidade a qual será filtrado os dados
     * @param codModeloQuiz Código do modelo de quiz
     * @throws Throwable Caso algum erro ocorra
     */
    void getRealizacaoQuizByCargoCsv(@NotNull final OutputStream out,
                                     @NotNull final Long codUnidade,
                                     @Nullable final Long codModeloQuiz) throws Throwable;

    /**
     * Método que cria um {@link Report} contendo informações de realizações de quiz estratificados
     * por cargos da unidade.
     *
     * @param codUnidade    Código da unidade a qual será filtrado os dados
     * @param codModeloQuiz Código do modelo de quiz
     * @return Um objeto {@link Report} contendo as informações filtradas
     * @throws Throwable Caso algum erro ocorra
     */
    @NotNull
    Report getRealizacaoQuizByCargoReport(@NotNull final Long codUnidade,
                                          @Nullable final Long codModeloQuiz) throws Throwable;

    /**
     * Método que cria um arquivo CSV contendo informações de respostas certas de quizzes de um certo
     * modelo, filtrados por unidade.
     *
     * @param out           Local onde o csv será escrito
     * @param codUnidade    Código da unidade a qual será filtrado os dados
     * @param codModeloQuiz Código do modelo de quiz
     * @throws Throwable Caso algum erro ocorra
     */
    void getEstratificacaoQuizRespostasCsv(@NotNull final OutputStream out,
                                           @NotNull final Long codUnidade,
                                           @Nullable final Long codModeloQuiz) throws Throwable;

    /**
     * Método que cria um {@link Report} contendo informações de respostas certas de quizzes de um certo
     * modelo, filtrados por unidade.
     *
     * @param codUnidade    Código da unidade a qual será filtrado os dados
     * @param codModeloQuiz Código do modelo de quiz
     * @return Um objeto {@link Report} contendo as informações filtradas
     * @throws Throwable Caso algum erro ocorra
     */
    @NotNull
    Report getEstratificacaoQuizRespostasReport(@NotNull final Long codUnidade,
                                                @Nullable final Long codModeloQuiz) throws Throwable;

    /**
     * Método que cria um arquivo CSV contendo informações gerais dos quizzes, filtrados por unidade de acordo
     * com um período aplicado.
     *
     * @param out         Local onde o csv será escrito
     * @param codUnidade  Código da unidade a qual será filtrado os dados
     * @param dataInicial Data incial do período de busca
     * @param dataFinal   Data final do período de busca
     * @throws Throwable Caso algum erro ocorra
     */
    void getExtratoGeralCsv(@NotNull final OutputStream out,
                            @NotNull final Long codUnidade,
                            @NotNull final LocalDate dataInicial,
                            @NotNull final LocalDate dataFinal) throws Throwable;

    /**
     * Método que cria um {@link Report} contendo informações gerais dos quizzes, filtrados por unidade de acordo
     * com um período aplicado.
     *
     * @param codUnidade  Código da unidade a qual será filtrado os dados
     * @param dataInicial Data incial do período de busca
     * @param dataFinal   Data final do período de busca
     * @return Um objeto {@link Report} contendo as informações filtradas
     * @throws Throwable Caso algum erro ocorra
     */
    @NotNull
    Report getExtratoGeralReport(@NotNull final Long codUnidade,
                                 @NotNull final LocalDate dataInicial,
                                 @NotNull final LocalDate dataFinal) throws Throwable;

    /**
     * Método que cria um arquivo CSV contendo todas as respostas dos quizzes realizados.
     *
     * @param out                Local onde o csv será escrito
     * @param codUnidade         Código da unidade a qual será filtrado os dados
     * @param codModeloQuiz      Código do modelo do quiz que será filtrado os dados
     * @param cpfColaborador     CPF do colaborador a qual será filtrado os dados
     * @param dataInicial        Data inicial do período de busca
     * @param dataFinal          Data final do período de busca
     * @param apenasSelecionadas Define se a consulta retornará apenas respostas selecionadas
     * @throws Throwable Caso algum erro ocorra
     */
    void getRespostasRealizadosCsv(@NotNull final OutputStream out,
                                   @NotNull final Long codUnidade,
                                   @Nullable final Long codModeloQuiz,
                                   @Nullable final Long cpfColaborador,
                                   @NotNull final LocalDate dataInicial,
                                   @NotNull final LocalDate dataFinal,
                                   final boolean apenasSelecionadas) throws Throwable;

    /**
     * Método que cria um {@link Report report} contendo todas as respostas dos quizzes realizados.
     *
     * @param codUnidade         Código da unidade a qual será filtrado os dados
     * @param codModeloQuiz      Código do modelo do quiz que será filtrado os dados
     * @param cpfColaborador     CPF do colaborador a qual será filtrado os dados
     * @param dataInicial        Data inicial do período de busca
     * @param dataFinal          Data final do período de busca
     * @param apenasSelecionadas Define se a consulta retornará apenas respostas selecionadas
     * @return Um objeto {@link Report report} contendo as informações filtradas
     * @throws Throwable Caso algum erro ocorra
     */
    @NotNull
    Report getRespostasRealizadosReport(@NotNull final Long codUnidade,
                                        @Nullable final Long codModeloQuiz,
                                        @Nullable final Long cpfColaborador,
                                        @NotNull final LocalDate dataInicial,
                                        @NotNull final LocalDate dataFinal,
                                        final boolean apenasSelecionadas) throws Throwable;
}