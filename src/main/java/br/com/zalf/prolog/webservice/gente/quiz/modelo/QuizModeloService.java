package br.com.zalf.prolog.webservice.gente.quiz.modelo;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.colaborador.model.Cargo;
import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogExceptionHandler;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Zalf on 05/01/17.
 */
public class QuizModeloService {
    private static final String TAG = QuizModeloService.class.getSimpleName();
    @NotNull
    private final QuizModeloDao dao = Injection.provideQuizModeloDao();
    @NotNull
    private final ProLogExceptionHandler exceptionHandler = Injection.provideProLogExceptionHandler();

    @NotNull
    public AbstractResponse insertModeloQuiz(ModeloQuiz modeloQuiz, Long codUnidade) throws ProLogException {
        try {
            QuizModeloValidator.validaQuizModelo(modeloQuiz, codUnidade);
            return ResponseWithCod.ok(
                    "Modelo de Quiz inserido com sucesso",
                    dao.insertModeloQuiz(modeloQuiz, codUnidade));
        } catch (final Throwable e) {
            final String errorMessage = "Erro ao inserir o modelo de quiz";
            Log.e(TAG, errorMessage, e);
            throw exceptionHandler.map(e, errorMessage);
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

    public List<ModeloQuiz> getModelosQuizDisponiveisByCodUnidadeByCodFuncao(Long codUnidade, Long
            codFuncaoColaborador) {
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
