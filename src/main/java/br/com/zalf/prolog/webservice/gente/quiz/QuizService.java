package br.com.zalf.prolog.webservice.gente.quiz;

import br.com.zalf.prolog.gente.quiz.Quiz;

import java.sql.SQLException;

/**
 * Created by Zalf on 05/01/17.
 */
public class QuizService {

    private QuizDaoImpl dao = new QuizDaoImpl();

    public boolean insert (Quiz quiz){
        try{
            return dao.insert(quiz);
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

}
