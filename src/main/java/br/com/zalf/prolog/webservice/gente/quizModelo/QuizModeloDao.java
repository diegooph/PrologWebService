package br.com.zalf.prolog.webservice.gente.quizModelo;

import br.com.zalf.prolog.gente.quiz.ModeloQuiz;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Zalf on 05/01/17.
 */
public interface QuizModeloDao {

    /**
     * Busca os modelos de Quiz disponiveis para o codUnidade e codFuncaoColaborador especificados, também
     * verifica se a data de liberação/fechamento bate com a data atual
     * @param codUnidade codUnidade
     * @param codFuncaoColaborador codigo do cargo do colaborador ou % para todos
     * @return lista de ModeloQuiz completos, com perguntas e alternativas
     * @throws SQLException caso não seja possível realizar as buscas
     */
    public List<ModeloQuiz> getModelosQuizDisponiveisByCodUnidadeByCodFuncao(Long codUnidade, Long codFuncaoColaborador) throws SQLException;

}
