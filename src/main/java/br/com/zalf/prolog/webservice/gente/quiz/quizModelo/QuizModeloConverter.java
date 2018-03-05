package br.com.zalf.prolog.webservice.gente.quiz.quizModelo;

import br.com.zalf.prolog.webservice.commons.questoes.Alternativa;
import br.com.zalf.prolog.webservice.gente.quiz.quiz.model.AlternativaEscolhaQuiz;
import br.com.zalf.prolog.webservice.gente.quiz.quiz.model.AlternativaOrdenamentoQuiz;
import br.com.zalf.prolog.webservice.gente.quiz.quiz.model.PerguntaQuiz;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * Created by Zalf on 05/01/17.
 */
public class QuizModeloConverter {

    private QuizModeloConverter() {
        throw new IllegalStateException(QuizModeloConverter.class.getSimpleName() + " cannot be instantiated!");
    }

    static ModeloQuiz createModeloQuiz(ResultSet rSet) throws SQLException {
        final ModeloQuiz modelo = new ModeloQuiz();
        modelo.setCodigo(rSet.getLong("CODIGO"));
        modelo.setDataHoraAbertura(rSet.getObject("DATA_HORA_ABERTURA", LocalDateTime.class));
        modelo.setDataHoraFechamento(rSet.getObject("DATA_HORA_FECHAMENTO", LocalDateTime.class));
        modelo.setDescricao(rSet.getString("DESCRICAO"));
        modelo.setNome(rSet.getString("NOME"));
        modelo.setPorcentagemAprovacao(rSet.getDouble("PORCENTAGEM_APROVACAO"));
        return modelo;
    }

    public static PerguntaQuiz createPerguntaQuiz(ResultSet rSet) throws SQLException{
        final PerguntaQuiz pergunta = new PerguntaQuiz();
        pergunta.setCodigo(rSet.getLong("CODIGO"));
        pergunta.setOrdemExibicao(rSet.getInt("ORDEM"));
        pergunta.setUrlImagem(rSet.getString("URL_IMAGEM"));
        pergunta.setPergunta(rSet.getString("PERGUNTA"));
        pergunta.setTipo(rSet.getString("TIPO"));
        return pergunta;
    }

    public static Alternativa createAlternativa(ResultSet rSet, String tipoPergunta) throws SQLException{
        if (tipoPergunta.equals(PerguntaQuiz.TIPO_MULTIPLE_CHOICE) || tipoPergunta.equals(PerguntaQuiz.TIPO_SINGLE_CHOICE)) {
            final AlternativaEscolhaQuiz alternativa = new AlternativaEscolhaQuiz();
            alternativa.setCodigo(rSet.getLong("CODIGO"));
            alternativa.setAlternativa(rSet.getString("ALTERNATIVA"));
            alternativa.setOrdemExibicao(rSet.getInt("ORDEM"));
            alternativa.setCorreta(rSet.getBoolean("CORRETA"));
            alternativa.setSelecionada(rSet.getBoolean("SELECIONADA"));
            return alternativa;
        } else {
            final AlternativaOrdenamentoQuiz alternativa = new AlternativaOrdenamentoQuiz();
            alternativa.setCodigo(rSet.getLong("CODIGO"));
            alternativa.setAlternativa(rSet.getString("ALTERNATIVA"));
            alternativa.setOrdemCorreta(rSet.getInt("ORDEM"));
            alternativa.setOrdemSelecionada(rSet.getInt("ORDEM_SELECIONADA"));
            return alternativa;
        }
    }
}