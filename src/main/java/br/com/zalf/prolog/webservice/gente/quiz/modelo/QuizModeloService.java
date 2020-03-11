package br.com.zalf.prolog.webservice.gente.quiz.modelo;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Cargo;
import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by Zalf on 05/01/17.
 */
public final class QuizModeloService {
    private static final String TAG = QuizModeloService.class.getSimpleName();
    @NotNull
    private final QuizModeloDao dao = Injection.provideQuizModeloDao();

    @NotNull
    AbstractResponse insertModeloQuiz(final Long codUnidade,
                                      final ModeloQuiz modeloQuiz) throws ProLogException {
        try {
            QuizModeloValidator.validaQuizModelo(codUnidade, modeloQuiz);
            return ResponseWithCod.ok(
                    "Quiz criado com sucesso",
                    dao.insertModeloQuiz(codUnidade, modeloQuiz));
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao inserir o modelo de quiz", e);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(e, "Erro ao inserir o modelo de quiz, tente novamente");
        }
    }

    @NotNull
    Response updateModeloQuiz(final Long codUnidade, final ModeloQuiz modeloQuiz) throws ProLogException {
        try {
            dao.updateModeloQuiz(codUnidade, modeloQuiz);
            return Response.ok("Modelo de quiz atualizado com sucesso");
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao atualizar o modelo de quiz. \n" +
                    "codUnidade: %d", codUnidade), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao atualizar o modelo de quiz, tente novamente");
        }
    }

    @NotNull
    Response updateCargosModeloQuiz(final Long codUnidade,
                                    final Long codModeloQuiz,
                                    final List<Cargo> funcoes) throws ProLogException {
        try {
            dao.updateCargosModeloQuiz(codUnidade, codModeloQuiz, funcoes);
            return Response.ok("Funções alteradas com sucesso");
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao atualizar os cargos do modelo de quiz. \n" +
                    "codUnidade: %d \n" +
                    "codModeloQuiz: %d", codUnidade, codModeloQuiz), t);
            final String fallbackMessage = "Erro ao alterar as funções vinculadas ao modelo de quiz, tente novamente";
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, fallbackMessage);
        }
    }

    @NotNull
    List<ModeloQuiz> getModelosQuizDisponiveisByCodUnidadeByCodFuncao(
            final Long codUnidade,
            final Long codFuncaoColaborador) throws ProLogException {
        try {
            return dao.getModelosQuizDisponiveis(codUnidade, codFuncaoColaborador);
        } catch (final Throwable throwable) {
            Log.e(TAG, String.format("Erro ao buscar os modelos de quiz. \n" +
                    "codUnidade: %d \n" +
                    "codFuncaoColaborador: %d", codUnidade, codFuncaoColaborador), throwable);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(throwable, "Erro ao buscar os modelos de quizzes, tente novamente");
        }
    }

    @NotNull
    List<ModeloQuizListagem> getModelosQuizzesByCodUnidade(final Long codUnidade) throws ProLogException {
        try {
            return dao.getModelosQuizzesByCodUnidade(codUnidade);
        } catch (final Throwable throwable) {
            Log.e(TAG, String.format("Erro ao buscar os modelos de quizzes da unidade. \n" +
                    "codUnidade: %d", codUnidade), throwable);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(throwable, "Erro ao buscar os modelos de quizzes, tente novamente");
        }
    }

    @NotNull
    ModeloQuiz getModeloQuiz(final Long codUnidade, final Long codModeloQuiz) throws ProLogException {
        try {
            return dao.getModeloQuiz(codUnidade, codModeloQuiz);
        } catch (final Throwable throwable) {
            Log.e(TAG, String.format("Erro ao buscar o modelo de quiz. \n" +
                    "codUnidade: %d \n" +
                    "codModeloQuiz: %d", codUnidade, codModeloQuiz), throwable);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(throwable, "Erro ao buscar o modelo de quiz, tente novamente");
        }
    }
}