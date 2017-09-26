package br.com.zalf.prolog.webservice.gente.quiz.quizModelo;

import br.com.zalf.prolog.webservice.colaborador.model.Cargo;
import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Zalf on 05/01/17.
 */
public class QuizModeloService {

    private QuizModeloDao dao = new QuizModeloDaoImpl();

    public List<ModeloQuiz> getModelosQuizDisponiveisByCodUnidadeByCodFuncao(Long codUnidade, Long codFuncaoColaborador) {
        try {
            return dao.getModelosQuizDisponiveis(codUnidade, codFuncaoColaborador);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<ModeloQuiz> getModelosQuizByCodUnidade(Long codUnidade) {
        try {
            return dao.getModelosQuizByCodUnidade(codUnidade);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ModeloQuiz getModeloQuiz(Long codUnidade, Long codModeloQuiz) {
        try {
            return dao.getModeloQuiz(codUnidade, codModeloQuiz);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public AbstractResponse insertModeloQuiz(ModeloQuiz modeloQuiz, Long codUnidade) {
        try {
            Long codigo = dao.insertModeloQuiz(modeloQuiz, codUnidade);
            if (codigo != null) {
                return ResponseWithCod.ok("Modelo de Quiz inserido com sucesso", codigo);
            } else {
                return Response.error("Erro ao inserir o modelo de Quiz");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.error("Erro ao inserir o modelo de Quiz");
        }
    }

    public boolean updateModeloQuiz(ModeloQuiz modeloQuiz, Long codUnidade) {
        try {
            return dao.updateModeloQuiz(modeloQuiz, codUnidade);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateCargosModeloQuiz(List<Cargo> funcoes, Long codModeloQuiz, Long codUnidade) {
        try {
            return dao.updateCargosModeloQuiz(funcoes, codModeloQuiz, codUnidade);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
