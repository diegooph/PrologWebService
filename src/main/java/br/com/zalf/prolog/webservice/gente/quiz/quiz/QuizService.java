package br.com.zalf.prolog.webservice.gente.quiz.quiz;

import br.com.zalf.prolog.webservice.gente.quiz.quiz.model.Quiz;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Zalf on 05/01/17.
 */
public class QuizService {

    private QuizDao dao = new QuizDaoImpl();

    public boolean insert (Quiz quiz){
        try{
            return dao.insert(quiz);
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public List<Quiz> getRealizadosByColaborador(Long cpf, int limit, int offset){
        try{
            return dao.getRealizadosByColaborador(cpf, limit, offset);
        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    public Quiz getByCod(Long codUnidade, Long codQuiz, Long codModeloQuiz){
        try{
            return dao.getByCod(codUnidade, codQuiz, codModeloQuiz);
        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }

}
