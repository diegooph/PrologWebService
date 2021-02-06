package br.com.zalf.prolog.webservice.gente.quiz.modelo;

import br.com.zalf.prolog.webservice.gente.colaborador.model.Cargo;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by Zalf on 05/01/17.
 */
public interface QuizModeloDao {

    @NotNull
    Long insertModeloQuiz(@NotNull final Long codUnidade, @NotNull final ModeloQuiz modeloQuiz) throws Throwable;

    void updateModeloQuiz(@NotNull final Long codUnidade, @NotNull final ModeloQuiz modeloQuiz) throws Throwable;

    void updateCargosModeloQuiz(@NotNull final Long codUnidade,
                                @NotNull final Long codModeloQuiz,
                                @NotNull final List<Cargo> funcoes) throws Throwable;

    @NotNull
    List<ModeloQuiz> getModelosQuizDisponiveis(@NotNull final Long codUnidade,
                                               @NotNull final Long codFuncaoColaborador) throws Throwable;

    @NotNull
    List<ModeloQuizListagem> getModelosQuizzesByCodUnidade(@NotNull final Long codUnidade) throws Throwable;

    @NotNull
    ModeloQuiz getModeloQuiz(@NotNull final Long codUnidade, @NotNull final Long codModeloQuiz) throws Throwable;
}
