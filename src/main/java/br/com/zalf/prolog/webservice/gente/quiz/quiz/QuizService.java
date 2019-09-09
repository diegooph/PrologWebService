package br.com.zalf.prolog.webservice.gente.quiz.quiz;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.gente.quiz.quiz.model.Quiz;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by Zalf on 05/01/17.
 */
public class QuizService {
    private static final String TAG = QuizService.class.getSimpleName();
    private final QuizDao dao = Injection.provideQuizDao();

    public boolean insert(@NotNull final Quiz quiz) {
        try {
            return dao.insert(quiz);
        } catch (Throwable t) {
            Log.e(TAG, "Erro ao inserir um quiz", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao inserir o quiz, tente novamente");
        }
    }

    @NotNull
    public List<Quiz> getRealizadosByColaborador(@NotNull final Long cpf,
                                                 final int limit,
                                                 final int offset) {
        try {
            return dao.getRealizadosByColaborador(cpf, limit, offset);
        } catch (Throwable t) {
            Log.e(TAG, String.format("Erro ao buscar os quizzes realizados do colaborador.\n" +
                    "cpf: %d\n" +
                    "limit: %d\n" +
                    "offset: %d", cpf, limit, offset), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar os quizzes, tente novamente");
        }
    }

    @NotNull
    public Quiz getByCod(@NotNull final Long codQuiz) {
        try {
            return dao.getByCod(codQuiz);
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao buscar um quiz.\n" +
                    "codQuiz: %d", codQuiz), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar o quiz, tente novamente");
        }
    }
}