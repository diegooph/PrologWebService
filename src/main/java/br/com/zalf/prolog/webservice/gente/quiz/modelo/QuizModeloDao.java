package br.com.zalf.prolog.webservice.gente.quiz.modelo;

import br.com.zalf.prolog.webservice.colaborador.model.Cargo;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Zalf on 05/01/17.
 */
public interface QuizModeloDao {

    /**
     * Busca os modelos de Quiz disponiveis para o codUnidade e codFuncaoColaborador especificados, também
     * verifica se a data de liberação/fechamento bate com a data atual
     *
     * @param codUnidade           codUnidade
     * @param codFuncaoColaborador codigo do cargo do colaborador ou % para todos
     * @return lista de ModeloQuiz completos, com perguntas e alternativas
     * @throws Throwable caso não seja possível realizar as buscas
     */
    List<ModeloQuiz> getModelosQuizDisponiveis(Long codUnidade, Long codFuncaoColaborador) throws Throwable;

    /**
     * Busca um único modelo de quiz
     * @param codUnidade codUnidade
     * @param codModeloQuiz codModeloQuiz
     * @return um modelo de quiz completo
     * @throws Throwable caso não seja possível realizar a busca
     */
    ModeloQuiz getModeloQuiz(Long codUnidade, Long codModeloQuiz) throws Throwable;

    /**
     * Busca os modelos de quizzes existentes para a unidade buscada.
     *
     * @param codUnidade código da unidade.
     * @return uma lista contendo todos os modelos de quizzes que a unidade possui.
     * @throws Throwable caso qualquer erro aconteça.
     */
    @NotNull
    List<ModeloQuizListagem> getModelosQuizzesByCodUnidade(@NotNull final Long codUnidade) throws Throwable;

    /**
     * Insere um modelo de Quiz
     */
    @NotNull
    Long insertModeloQuiz(@NotNull final ModeloQuiz modeloQuiz, @NotNull final Long codUnidade) throws Throwable;

    /**
     * Atualiza dados gerais de um modelo de Quiz
     *
     * @param modeloQuiz
     * @param codUnidade
     * @return
     * @throws SQLException
     */
    boolean updateModeloQuiz(ModeloQuiz modeloQuiz, Long codUnidade) throws SQLException;

    /**
     * Atualiza os cargos associados a um modelo de quiz
     *
     * @param funcoes
     * @param codModeloQuiz
     * @param codUnidade
     * @return
     * @throws SQLException
     */
    boolean updateCargosModeloQuiz(List<Cargo> funcoes, Long codModeloQuiz, Long codUnidade) throws SQLException;
}
