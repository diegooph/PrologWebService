package br.com.zalf.prolog.webservice.gente.quiz.modelo;

import br.com.zalf.prolog.webservice.gente.colaborador.model.Cargo;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by Zalf on 05/01/17.
 */
public interface QuizModeloDao {

    /**
     * Método utilizado para inserir um {@link ModeloQuiz modelo de quiz}. Este modelo é criado pelo usuário com as
     * informações específicas.
     *
     * @param codUnidade Código da unidade onde este quiz estará disponível.
     * @param modeloQuiz Objeto contendo as informações do quiz.
     * @return Código do modelo de quiz inserido no bando de dados.
     * @throws Throwable Caso algum erro ocorra ao salvar o modelo de quiz no banco de dados.
     */
    @NotNull
    Long insertModeloQuiz(@NotNull final Long codUnidade, @NotNull final ModeloQuiz modeloQuiz) throws Throwable;

    /**
     * Método utilizado para atualizar as informações de um {@link ModeloQuiz modelo de quiz} específico.
     * As informações presentes no objeto serão inseridas no banco de dados utilizando o
     * {@link ModeloQuiz#codigo código do modelo}.
     *
     * @param codUnidade Código da unidade onde o quiz está vinculado.
     * @param modeloQuiz Objeto contendo as informações para serem atualizadas.
     * @throws Throwable Caso algum erro ocorra ao atualizar o modelo de quiz no banco de dados.
     */
    void updateModeloQuiz(@NotNull final Long codUnidade, @NotNull final ModeloQuiz modeloQuiz) throws Throwable;

    /**
     * Método utilizado para atualizar os {@link Cargo cargos} que podem realizar o quiz represetado pelo
     * {@link ModeloQuiz#codigo código do modelo}.
     * É importante ressaltar que os cargos antigos vinculados ao modelo serão deletados, passando a estar vinculados
     * apenos os cargos presentes no atributo {@code funcoes}.
     *
     * @param codUnidade Código da unidade onde o quiz está vinculado.
     * @param codModeloQuiz Código do modelo do quiz que terá os cargos atualizados.
     * @param funcoes Lista de {@link Cargo cargos} que serão vinculados ao modelo de quiz.
     * @throws Throwable Se ocorrer algum erro ao vincular os cargos novos.
     */
    void updateCargosModeloQuiz(@NotNull final Long codUnidade,
                                @NotNull final Long codModeloQuiz,
                                @NotNull final List<Cargo> funcoes) throws Throwable;

    /**
     * Busca os modelos de Quiz disponiveis para o codUnidade e codFuncaoColaborador especificados, também
     * verifica se a data de liberação/fechamento bate com a data atual
     *
     * @param codUnidade           codUnidade
     * @param codFuncaoColaborador codigo do cargo do colaborador ou % para todos
     * @return lista de ModeloQuiz completos, com perguntas e alternativas
     * @throws Throwable caso não seja possível realizar as buscas
     */
    @NotNull
    List<ModeloQuiz> getModelosQuizDisponiveis(@NotNull final Long codUnidade,
                                               @NotNull final Long codFuncaoColaborador) throws Throwable;

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
     * Busca um único modelo de quiz
     *
     * @param codUnidade    codUnidade
     * @param codModeloQuiz codModeloQuiz
     * @return um modelo de quiz completo
     * @throws Throwable caso não seja possível realizar a busca
     */
    @NotNull
    ModeloQuiz getModeloQuiz(@NotNull final Long codUnidade, @NotNull final Long codModeloQuiz) throws Throwable;
}
