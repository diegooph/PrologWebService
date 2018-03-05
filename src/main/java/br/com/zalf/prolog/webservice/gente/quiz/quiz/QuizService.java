package br.com.zalf.prolog.webservice.gente.quiz.quiz;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.gente.quiz.quiz.model.Quiz;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Zalf on 05/01/17.
 */
public class QuizService {
    private static final String TAG = QuizService.class.getSimpleName();
    private final QuizDao dao = Injection.provideQuizDao();

    public boolean insert(Quiz quiz) {
        try {
            return dao.insert(quiz);
        } catch (SQLException e) {
            Log.e(TAG, "Erro ao inserir um quiz", e);
            return false;
        }
    }

    public List<Quiz> getRealizadosByColaborador(Long cpf, int limit, int offset) {
        try {
            return dao.getRealizadosByColaborador(cpf, limit, offset);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar os quizzes realizados do colaborador. \n" +
                    "cpf: %d \n" +
                    "limit: %d \n" +
                    "offset: %d", cpf, limit, offset), e);
            return null;
        }
    }

    public Quiz getByCod(Long codUnidade, Long codQuiz, Long codModeloQuiz) {
        try {
            return dao.getByCod(codUnidade, codQuiz, codModeloQuiz);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar um quiz. \n" +
                    "codUnidade: %d \n" +
                    "codQuiz: %d \n" +
                    "codModeloQuiz: %d", codUnidade, codQuiz, codModeloQuiz), e);
            return null;
        }
    }
}