package br.com.zalf.prolog.webservice.gente.quizModelo;

import br.com.zalf.prolog.gente.quiz.ModeloQuiz;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Zalf on 05/01/17.
 */
public class QuizModeloService {

    private QuizModeloDao dao = new QuizModeloDaoImpl();

    public List<ModeloQuiz> getModelosQuizDisponiveisByCodUnidadeByCodFuncao(Long codUnidade, Long codFuncaoColaborador){
        try{
            return dao.getModelosQuizDisponiveisByCodUnidadeByCodFuncao(codUnidade, codFuncaoColaborador);
        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }

}
