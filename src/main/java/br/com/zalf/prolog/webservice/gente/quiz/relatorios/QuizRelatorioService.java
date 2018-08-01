package br.com.zalf.prolog.webservice.gente.quiz.relatorios;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Date;

/**
 * Created by Zart on 20/03/17.
 */
public class QuizRelatorioService {

    private QuizRelatorioDao dao = Injection.provideQuizRelatorioDao();
    private static final String TAG = QuizRelatorioService.class.getSimpleName();

    public void getEstratificacaoRealizacaoQuizCsv(OutputStream out, String codModeloQuiz, Long codUnidade,
                                                   long dataInicial, long dataFinal) {
        try {
            dao.getEstratificacaoRealizacaoQuizCsv(out, codModeloQuiz, codUnidade, dataInicial, dataFinal);
        } catch (SQLException | IOException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório com a estratificação de realização do quiz (CSV). \n" +
                    "codUnidade: %d \n" +
                    "codModeloQuiz: %s \n" +
                    "dataInicial: %s \n" +
                    "dataFinal: %s", codUnidade, codModeloQuiz, new Date(dataInicial).toString(), new Date(dataFinal).toString()), e);
        }
    }

    public Report getEstratificacaoRealizacaoQuizReport(String codModeloQuiz, Long codUnidade,
                                                        long dataInicial, long dataFinal) {
        try {
            return dao.getEstratificacaoRealizacaoQuizReport(codModeloQuiz, codUnidade, dataInicial, dataFinal);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório com a estratificação de realização do quiz (REPORT). \n" +
                    "codUnidade: %d \n" +
                    "codModeloQuiz: %s \n" +
                    "dataInicial: %s \n" +
                    "dataFinal: %s", codUnidade, codModeloQuiz, new Date(dataInicial).toString(), new Date(dataFinal).toString()), e);
            return null;
        }
    }

    public void getRealizacaoQuizByCargoCsv(OutputStream out, Long codUnidade, String codModeloQuiz) {
        try {
            dao.getRealizacaoQuizByCargoCsv(out, codUnidade, codModeloQuiz);
        } catch (SQLException | IOException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório com a realização de quizzes por cargo (CSV). \n" +
                    "codUnidade: %d \n" +
                    "codModeloQuiz: %s", codUnidade, codModeloQuiz), e);
        }
    }

    public Report getRealizacaoQuizByCargoReport(Long codUnidade, String codModeloQuiz) {
        try {
            return dao.getRealizacaoQuizByCargoReport(codUnidade, codModeloQuiz);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar o relatório com a realização de quizzes por cargo (REPORT). \n" +
                    "codUnidade: %d \n" +
                    "codModeloQuiz: %s", codUnidade, codModeloQuiz), e);
            return null;
        }
    }

    public void getEstratificacaoQuizRespostasCsv(OutputStream out, Long codUnidade, String codModeloQuiz) {
        try {
            dao.getEstratificacaoQuizRespostasCsv(out, codUnidade, codModeloQuiz);
        } catch (SQLException | IOException e) {
            Log.e(TAG, String.format("Erro ao bucar o relatório com a estratificação de respostas (CSV). \n" +
                    "codUnidade: %d \n" +
                    "codModeloQuiz: %s", codUnidade, codModeloQuiz), e);
        }
    }

    public Report getEstratificacaoQuizRespostasReport(Long codUnidade, String codModeloQuiz) {
        try {
            return dao.getEstratificacaoQuizRespostasReport(codUnidade, codModeloQuiz);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao bucar o relatório com a estratificação de respostas (REPORT). \n" +
                    "codUnidade: %d \n" +
                    "codModeloQuiz: %s", codUnidade, codModeloQuiz), e);
            return null;
        }
    }

    public void getExtratoGeralCsv(OutputStream out, Long codUnidade, long dataInicial, long dataFinal) {
        try {
            dao.getExtratoGeralCsv(out, codUnidade, dataInicial, dataFinal);
        } catch (SQLException | IOException e) {
            Log.e(TAG, String.format("Erro a buscar o relatório com o extrato geral de respostas do quiz (CSV). \n" +
                    "codUnidade: %d \n" +
                    "dataInicial: %s \n" +
                    "dataFinal: %s", codUnidade, new Date(dataInicial).toString(), new Date(dataFinal).toString()), e);
        }
    }

    public Report getExtratoGeralReport(Long codUnidade, long dataInicial, long dataFinal) {
        try {
            return dao.getExtratoGeralReport(codUnidade, dataInicial, dataFinal);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro a buscar o relatório com o extrato geral de respostas do quiz (REPORT). \n" +
                    "codUnidade: %d \n" +
                    "dataInicial: %s \n" +
                    "dataFinal: %s", codUnidade, new Date(dataInicial).toString(), new Date(dataFinal).toString()), e);
            return null;
        }
    }
}
