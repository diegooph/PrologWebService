package br.com.zalf.prolog.webservice.gente.quizRelatorios;

import br.com.zalf.prolog.commons.Report;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;

/**
 * Created by Zart on 20/03/17.
 */
public class QuizRelatorioService {

    private QuizRelatorioDaoImpl dao = new QuizRelatorioDaoImpl();

    public void getEstratificacaoRealizacaoQuizCsv(OutputStream out, String cpf, String codModeloQuiz, Long codUnidade,
                                                   long dataInicial, long dataFinal) {
        try {
            dao.getEstratificacaoRealizacaoQuizCsv(out, cpf, codModeloQuiz, codUnidade, dataInicial, dataFinal);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public Report getEstratificacaoRealizacaoQuizReport(String cpf, String codModeloQuiz, Long codUnidade,
                                                        long dataInicial, long dataFinal) {
        try {
            return dao.getEstratificacaoRealizacaoQuizReport(cpf, codModeloQuiz, codUnidade, dataInicial, dataFinal);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void getRealizacaoQuizByCargoCsv(OutputStream out, Long codUnidade, String codCargo, String codModeloQuiz){
        try {
            dao.getRealizacaoQuizByCargoCsv(out, codUnidade, codCargo, codModeloQuiz);
        }catch (SQLException | IOException e){
            e.printStackTrace();
        }
    }

    public Report getRealizacaoQuizByCargoReport(Long codUnidade, String codCargo, String codModeloQuiz){
        try{
            return dao.getRealizacaoQuizByCargoReport(codUnidade, codCargo, codModeloQuiz);
        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }


}
