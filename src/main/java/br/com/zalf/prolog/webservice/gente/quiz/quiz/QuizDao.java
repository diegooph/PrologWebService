package br.com.zalf.prolog.webservice.gente.quiz.quiz;

import br.com.zalf.prolog.webservice.gente.quiz.quiz.model.Quiz;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by web on 03/03/17.
 */
public interface QuizDao {

    boolean insert(@NotNull final Quiz quiz) throws Throwable;

    @NotNull
    List<Quiz> getRealizadosByColaborador(@NotNull final Long cpf,
                                          final int limit,
                                          final int offset) throws Throwable;

    @NotNull
    Quiz getByCod(@NotNull final Long codQuiz) throws Throwable;
}
