package br.com.zalf.prolog.webservice.gente.quiz.quizModelo;

import br.com.zalf.prolog.commons.questoes.Alternativa;
import br.com.zalf.prolog.gente.quiz.AlternativaEscolhaQuiz;
import br.com.zalf.prolog.gente.quiz.AlternativaOrdenamentoQuiz;
import br.com.zalf.prolog.gente.quiz.ModeloQuiz;
import br.com.zalf.prolog.gente.quiz.PerguntaQuiz;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Zalf on 05/01/17.
 */
public class QuizModeloConverter {

    static ModeloQuiz createModeloQuiz(ResultSet rSet) throws SQLException {
        ModeloQuiz modelo = new ModeloQuiz();
        modelo.setCodigo(rSet.getLong("CODIGO"));
        modelo.setDataHoraAbertura(rSet.getTimestamp("DATA_HORA_ABERTURA"));
        modelo.setDataHoraFechamento(rSet.getTimestamp("DATA_HORA_FECHAMENTO"));
        modelo.setDescricao(rSet.getString("DESCRICAO"));
        modelo.setNome(rSet.getString("NOME"));
        modelo.setPorcentagemAprovacao(rSet.getDouble("PORCENTAGEM_APROVACAO"));
        return modelo;
    }

    public static PerguntaQuiz createPerguntaQuiz(ResultSet rSet) throws SQLException{
        PerguntaQuiz pergunta = new PerguntaQuiz();
        pergunta.setCodigo(rSet.getLong("CODIGO"));
        pergunta.setOrdemExibicao(rSet.getInt("ORDEM"));
        pergunta.setUrlImagem(rSet.getString("URL_IMAGEM"));
        pergunta.setPergunta(rSet.getString("PERGUNTA"));
        pergunta.setTipo(rSet.getString("TIPO"));
        return pergunta;
    }

    public static Alternativa createAlternativa(ResultSet rSet, String tipoPergunta) throws SQLException{
        if(tipoPergunta.equals(PerguntaQuiz.TIPO_MULTIPLE_CHOICE) || tipoPergunta.equals(PerguntaQuiz.TIPO_SINGLE_CHOICE)){
            AlternativaEscolhaQuiz alternativa = new AlternativaEscolhaQuiz();
            alternativa.setCodigo(rSet.getLong("CODIGO"));
            alternativa.setAlternativa(rSet.getString("ALTERNATIVA"));
            alternativa.setOrdemExibicao(rSet.getInt("ORDEM"));
            alternativa.setCorreta(rSet.getBoolean("CORRETA"));
            alternativa.setSelecionada(rSet.getBoolean("SELECIONADA"));
            return alternativa;
        }else{
            AlternativaOrdenamentoQuiz alternativa = new AlternativaOrdenamentoQuiz();
            alternativa.setCodigo(rSet.getLong("CODIGO"));
            alternativa.setAlternativa(rSet.getString("ALTERNATIVA"));
            alternativa.setOrdemCorreta(rSet.getInt("ORDEM"));
            alternativa.setOrdemSelecionada(rSet.getInt("ORDEM_SELECIONADA"));
            return alternativa;
        }
    }

}
