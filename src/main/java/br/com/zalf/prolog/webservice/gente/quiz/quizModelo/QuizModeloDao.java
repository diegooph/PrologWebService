package br.com.zalf.prolog.webservice.gente.quiz.quizModelo;

import br.com.zalf.prolog.webservice.colaborador.Cargo;

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
     * @throws SQLException caso não seja possível realizar as buscas
     */
    public List<ModeloQuiz> getModelosQuizDisponiveis(Long codUnidade, Long codFuncaoColaborador) throws SQLException;

    /**
     * Busca apenas o nome dos modelos de quiz cadastrados para determinada unidade
     *
     * @param codUnidade código da unidade
     * @return lista de strings
     * @throws SQLException caso não seja possível
     */
    public List<ModeloQuiz> getModelosQuizByCodUnidade(Long codUnidade) throws SQLException;

    /**
     * Insere um modelo de Quiz
     *
     * @param modeloQuiz
     * @param codUnidade
     * @return
     * @throws SQLException
     */
    public Long insertModeloQuiz(ModeloQuiz modeloQuiz, Long codUnidade) throws SQLException;

    /**
     * Atualiza dados gerais de um modelo de Quiz
     *
     * @param modeloQuiz
     * @param codUnidade
     * @return
     * @throws SQLException
     */
    public boolean updateModeloQuiz(ModeloQuiz modeloQuiz, Long codUnidade) throws SQLException;

    /**
     * Atualiza os cargos associados a um modelo de quiz
     *
     * @param funcoes
     * @param codModeloQuiz
     * @param codUnidade
     * @return
     * @throws SQLException
     */
    public boolean updateCargosModeloQuiz(List<Cargo> funcoes, Long codModeloQuiz, Long codUnidade) throws SQLException;
}
