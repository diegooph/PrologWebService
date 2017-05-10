package br.com.zalf.prolog.webservice.gente.quiz.quiz;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by web on 03/03/17.
 */
public interface QuizDao {

    boolean insert (Quiz quiz) throws SQLException;

    List<Quiz> getRealizadosByColaborador(Long cpf, int limit, int offset) throws SQLException;

    Quiz getByCod(Long codUnidade, Long codQuiz, Long codModeloQuiz) throws SQLException;
}
