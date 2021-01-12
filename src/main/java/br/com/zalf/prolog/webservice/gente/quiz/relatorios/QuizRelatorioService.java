package br.com.zalf.prolog.webservice.gente.quiz.relatorios;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.PrologDateParser;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;

/**
 * Created by Zart on 20/03/17.
 */
class QuizRelatorioService {
    private static final String TAG = QuizRelatorioService.class.getSimpleName();
    @NotNull
    private final QuizRelatorioDao dao = Injection.provideQuizRelatorioDao();

    void getEstratificacaoRealizacaoQuizCsv(final OutputStream out,
                                            final Long codUnidade,
                                            final Long codModeloQuiz,
                                            final String dataInicial,
                                            final String dataFinal) {
        try {
            dao.getEstratificacaoRealizacaoQuizCsv(
                    out,
                    codUnidade,
                    codModeloQuiz,
                    PrologDateParser.toLocalDate(dataInicial),
                    PrologDateParser.toLocalDate(dataFinal));
        } catch (final Throwable throwable) {
            Log.e(TAG, String.format("Erro ao buscar o relatório com a estratificação de realização do quiz (CSV).\n" +
                            "codUnidade: %d\n" +
                            "codModeloQuiz: %d\n" +
                            "dataInicial: %s\n" +
                            "dataFinal: %s",
                    codUnidade,
                    codModeloQuiz,
                    dataInicial,
                    dataInicial),
                    throwable);
            throw new RuntimeException(throwable);
        }
    }

    @NotNull
    Report getEstratificacaoRealizacaoQuizReport(final Long codUnidade,
                                                 final Long codModeloQuiz,
                                                 final String dataInicial,
                                                 final String dataFinal) throws ProLogException {
        try {
            return dao.getEstratificacaoRealizacaoQuizReport(
                    codUnidade,
                    codModeloQuiz,
                    PrologDateParser.toLocalDate(dataInicial),
                    PrologDateParser.toLocalDate(dataFinal));
        } catch (final Throwable throwable) {
            Log.e(TAG, String.format("Erro ao buscar o relatório com a estratificação de realização do quiz (REPORT).\n" +
                            "codUnidade: %d\n" +
                            "codModeloQuiz: %d\n" +
                            "dataInicial: %s\n" +
                            "dataFinal: %s",
                    codUnidade,
                    codModeloQuiz,
                    dataInicial,
                    dataFinal), throwable);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(throwable, "Erro ao gerar relatório, tente novamente");
        }
    }

    void getRealizacaoQuizByCargoCsv(final OutputStream out,
                                     final Long codUnidade,
                                     final Long codModeloQuiz) {
        try {
            dao.getRealizacaoQuizByCargoCsv(
                    out,
                    codUnidade,
                    codModeloQuiz);
        } catch (final Throwable throwable) {
            Log.e(TAG, String.format("Erro ao buscar o relatório com a realização de quizzes por cargo (CSV).\n" +
                    "codUnidade: %d\n" +
                    "codModeloQuiz: %d",
                    codUnidade,
                    codModeloQuiz),
                    throwable);
            throw new RuntimeException(throwable);
        }
    }

    @NotNull
    Report getRealizacaoQuizByCargoReport(final Long codUnidade,
                                          final Long codModeloQuiz) throws ProLogException {
        try {
            return dao.getRealizacaoQuizByCargoReport(
                    codUnidade,
                    codModeloQuiz);
        } catch (final Throwable throwable) {
            Log.e(TAG, String.format("Erro ao buscar o relatório com a realização de quizzes por cargo (REPORT).\n" +
                    "codUnidade: %d\n" +
                    "codModeloQuiz: %d",
                    codUnidade,
                    codModeloQuiz),
                    throwable);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(throwable, "Erro ao gerar relatório, tente novamente");
        }
    }

    void getEstratificacaoQuizRespostasCsv(final OutputStream out,
                                           final Long codUnidade,
                                           final Long codModeloQuiz) {
        try {
            dao.getEstratificacaoQuizRespostasCsv(
                    out,
                    codUnidade,
                    codModeloQuiz);
        } catch (final Throwable throwable) {
            Log.e(TAG, String.format("Erro ao bucar o relatório com a estratificação de respostas (CSV).\n" +
                    "codUnidade: %d\n" +
                    "codModeloQuiz: %d",
                    codUnidade,
                    codModeloQuiz),
                    throwable);
            throw new RuntimeException(throwable);
        }
    }

    @NotNull
    Report getEstratificacaoQuizRespostasReport(final Long codUnidade,
                                                final Long codModeloQuiz) throws ProLogException {
        try {
            return dao.getEstratificacaoQuizRespostasReport(
                    codUnidade,
                    codModeloQuiz);
        } catch (final Throwable throwable) {
            Log.e(TAG, String.format("Erro ao bucar o relatório com a estratificação de respostas (REPORT).\n" +
                    "codUnidade: %d\n" +
                    "codModeloQuiz: %d",
                    codUnidade,
                    codModeloQuiz),
                    throwable);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(throwable, "Erro ao gerar relatório, tente novamente");
        }
    }

    void getExtratoGeralCsv(final OutputStream out,
                            final Long codUnidade,
                            final String dataInicial,
                            final String dataFinal) {
        try {
            dao.getExtratoGeralCsv(
                    out,
                    codUnidade,
                    PrologDateParser.toLocalDate(dataInicial),
                    PrologDateParser.toLocalDate(dataFinal));
        } catch (final Throwable throwable) {
            Log.e(TAG, String.format("Erro a buscar o relatório com o extrato geral de respostas do quiz (CSV).\n" +
                            "codUnidade: %d\n" +
                            "dataInicial: %s\n" +
                            "dataFinal: %s",
                    codUnidade,
                    dataInicial,
                    dataFinal),
                    throwable);
            throw new RuntimeException(throwable);
        }
    }

    @NotNull
    Report getExtratoGeralReport(final Long codUnidade,
                                 final String dataInicial,
                                 final String dataFinal) throws ProLogException {
        try {
            return dao.getExtratoGeralReport(
                    codUnidade,
                    PrologDateParser.toLocalDate(dataInicial),
                    PrologDateParser.toLocalDate(dataFinal));
        } catch (final Throwable throwable) {
            Log.e(TAG, String.format("Erro a buscar o relatório com o extrato geral de respostas do quiz (REPORT).\n" +
                            "codUnidade: %d\n" +
                            "dataInicial: %s\n" +
                            "dataFinal: %s",
                    codUnidade,
                    dataInicial,
                    dataFinal),
                    throwable);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(throwable, "Erro ao gerar relatório, tente novamente");
        }
    }

    void getRespostasRealizadosCsv(final OutputStream out,
                                   final Long codUnidade,
                                   final Long codModeloQuiz,
                                   final Long cpfColaborador,
                                   final String dataInicial,
                                   final String dataFinal,
                                   final boolean apenasSelecionadas) {
        try {
            dao.getRespostasRealizadosCsv(out,
                                          codUnidade,
                                          codModeloQuiz,
                                          cpfColaborador,
                                          PrologDateParser.toLocalDate(dataInicial),
                                          PrologDateParser.toLocalDate(dataFinal),
                                          apenasSelecionadas);
        } catch (final Throwable throwable) {
            Log.e(TAG, String.format("Erro a buscar o relatório de respostas de quizzes realizados (CSV).\n" +
                            "codUnidade: %d\n" +
                            "codModeloQuiz: %d\n" +
                            "cpfColaborador: %d\n" +
                            "dataInicial: %s\n" +
                            "dataFinal: %s\n" +
                            "apenasSelecionadas: %b",
                    codUnidade,
                    codModeloQuiz,
                    cpfColaborador,
                    dataInicial,
                    dataFinal,
                    apenasSelecionadas),
                    throwable);
            throw new RuntimeException(throwable);
        }
    }

    @NotNull
    Report getRespostasRealizadosReport(final Long codUnidade,
                                        final Long codModeloQuiz,
                                        final Long cpfColaborador,
                                        final String dataInicial,
                                        final String dataFinal,
                                        final boolean apenasSelecionadas) throws ProLogException {
        try {
            return dao.getRespostasRealizadosReport(
                    codUnidade,
                    codModeloQuiz,
                    cpfColaborador,
                    PrologDateParser.toLocalDate(dataInicial),
                    PrologDateParser.toLocalDate(dataFinal),
                    apenasSelecionadas);
        } catch (final Throwable throwable) {
            Log.e(TAG, String.format("Erro a buscar o relatório de respostas de quizzes realizados (REPORT).\n" +
                            "codUnidade: %d\n" +
                            "codModeloQuiz: %d\n" +
                            "cpfColaborador: %d\n" +
                            "dataInicial: %s\n" +
                            "dataFinal: %s\n" +
                            "apenasSelecionadas: %b",
                    codUnidade,
                    codModeloQuiz,
                    cpfColaborador,
                    dataInicial,
                    dataFinal,
                    apenasSelecionadas),
                    throwable);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(throwable, "Erro ao gerar relatório, tente novamente");
        }
    }
}
