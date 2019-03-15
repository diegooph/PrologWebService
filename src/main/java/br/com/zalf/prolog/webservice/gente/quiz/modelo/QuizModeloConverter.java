package br.com.zalf.prolog.webservice.gente.quiz.modelo;

import br.com.zalf.prolog.webservice.commons.questoes.Alternativa;
import br.com.zalf.prolog.webservice.gente.quiz.quiz.model.AlternativaEscolhaQuiz;
import br.com.zalf.prolog.webservice.gente.quiz.quiz.model.AlternativaOrdenamentoQuiz;
import br.com.zalf.prolog.webservice.gente.quiz.quiz.model.PerguntaQuiz;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Created by Zalf on 05/01/17.
 */
public final class QuizModeloConverter {

    private QuizModeloConverter() {
        throw new IllegalStateException(QuizModeloConverter.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    static ModeloQuizListagem createModeloQuizListagem(@NotNull final ResultSet rSet,
                                                       @NotNull final Set<String> cargosLiberados) throws Throwable {
        return new ModeloQuizListagem(
                rSet.getLong("COD_MODELO_QUIZ"),
                rSet.getString("NOME_MODELO_QUIZ"),
                rSet.getLong("COD_UNIDADE_MODELO_QUIZ"),
                cargosLiberados,
                rSet.getDouble("PORCENTAGEM_APROVACAO"),
                rSet.getInt("QTD_PERGUNTAS"),
                rSet.getBoolean("TEM_MATERIAL_APOIO"),
                rSet.getBoolean("ESTA_ABERTO_PARA_REALIZACAO"));
    }

    @NotNull
    static ModeloQuiz createModeloQuiz(@NotNull final ResultSet rSet) throws Throwable {
        final ModeloQuiz modelo = new ModeloQuiz();
        modelo.setCodigo(rSet.getLong("CODIGO"));
        modelo.setDataHoraAbertura(rSet.getObject("DATA_HORA_ABERTURA", LocalDateTime.class));
        modelo.setDataHoraFechamento(rSet.getObject("DATA_HORA_FECHAMENTO", LocalDateTime.class));
        modelo.setDescricao(rSet.getString("DESCRICAO"));
        modelo.setNome(rSet.getString("NOME"));
        modelo.setPorcentagemAprovacao(rSet.getDouble("PORCENTAGEM_APROVACAO"));
        return modelo;
    }

    @NotNull
    public static PerguntaQuiz createPerguntaQuiz(@NotNull final ResultSet rSet) throws Throwable {
        final PerguntaQuiz pergunta = new PerguntaQuiz();
        pergunta.setCodigo(rSet.getLong("CODIGO"));
        pergunta.setOrdemExibicao(rSet.getInt("ORDEM"));
        pergunta.setUrlImagem(rSet.getString("URL_IMAGEM"));
        pergunta.setPergunta(rSet.getString("PERGUNTA"));
        pergunta.setTipo(rSet.getString("TIPO"));
        return pergunta;
    }

    @NotNull
    public static Alternativa createAlternativa(@NotNull final ResultSet rSet,
                                                @NotNull final String tipoPergunta) throws Throwable {
        if (tipoPergunta.equals(PerguntaQuiz.TIPO_MULTIPLE_CHOICE)
                || tipoPergunta.equals(PerguntaQuiz.TIPO_SINGLE_CHOICE)) {
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