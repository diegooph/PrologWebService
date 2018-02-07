package br.com.zalf.prolog.webservice.gente.quiz.quizModelo;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.colaborador.model.Cargo;
import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.Log;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Zalf on 05/01/17.
 */
public class QuizModeloService {

    private QuizModeloDao dao = Injection.provideQuizModeloDao();
    private static final String TAG = QuizModeloService.class.getSimpleName();

    public List<ModeloQuiz> getModelosQuizDisponiveisByCodUnidadeByCodFuncao(Long codUnidade, Long codFuncaoColaborador) {
        try {
            return dao.getModelosQuizDisponiveis(codUnidade, codFuncaoColaborador);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar os modelos de quiz. \n" +
                    "codUnidade: %d \n" +
                    "codFuncaoColaborador: %d", codUnidade, codFuncaoColaborador), e);
            return null;
        }
    }

    public List<ModeloQuiz> getModelosQuizByCodUnidade(Long codUnidade) {
        try {
            return dao.getModelosQuizByCodUnidade(codUnidade);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar os modelos de quiz da unidade. \n" +
                    "codUnidade: %d", codUnidade), e);
            return null;
        }
    }

    public ModeloQuiz getModeloQuiz(Long codUnidade, Long codModeloQuiz) {
        try {
            return dao.getModeloQuiz(codUnidade, codModeloQuiz);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao buscar o modelo de quiz. \n" +
                    "codUnidade: %d \n" +
                    "codModeloQuiz: %d", codUnidade, codModeloQuiz), e);
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
            Log.e(TAG, String.format("Erro ao inserir o modelo de quiz. \n" +
                    "codUnidade: %d", codUnidade), e);
            return Response.error("Erro ao inserir o modelo de Quiz");
        }
    }

    public boolean updateModeloQuiz(ModeloQuiz modeloQuiz, Long codUnidade) {
        try {
            return dao.updateModeloQuiz(modeloQuiz, codUnidade);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao atualizar o modelo de quiz. \n" +
                    "codUnidade: %d", codUnidade), e);
            return false;
        }
    }

    public boolean updateCargosModeloQuiz(List<Cargo> funcoes, Long codModeloQuiz, Long codUnidade) {
        try {
            return dao.updateCargosModeloQuiz(funcoes, codModeloQuiz, codUnidade);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao atualizar os cargos do modelo de quiz. \n" +
                    "codUnidade: %d \n" +
                    "codModeloQuiz: %d", codUnidade, codModeloQuiz), e);
            return false;
        }
    }

}
