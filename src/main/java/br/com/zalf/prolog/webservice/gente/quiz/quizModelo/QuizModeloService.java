package br.com.zalf.prolog.webservice.gente.quiz.quizModelo;

import br.com.zalf.prolog.webservice.colaborador.Funcao;
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

    public AbstractResponse insertModeloQuiz(ModeloQuiz modeloQuiz, Long codUnidade) {
        try {
            Long codigo = dao.insertModeloQuiz(modeloQuiz, codUnidade);
            if (codigo != null) {
                return ResponseWithCod.Ok("Modelo de Quiz inserido com sucesso", codigo);
            } else {
                return Response.Error("Erro ao inserir o modelo de Quiz");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.Error("Erro ao inserir o modelo de Quiz");
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

    public boolean updateCargosModeloQuiz(List<Funcao> funcoes, Long codModeloQuiz, Long codUnidade) {
        try {
            return dao.updateCargosModeloQuiz(funcoes, codModeloQuiz, codUnidade);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
