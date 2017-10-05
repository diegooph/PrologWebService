package br.com.zalf.prolog.webservice.gente.quiz.quiz.model;

import br.com.zalf.prolog.webservice.commons.questoes.Alternativa;
import br.com.zalf.prolog.webservice.commons.questoes.Pergunta;

import java.util.List;

public class PerguntaQuiz extends Pergunta {
	public static final String TIPO_SINGLE_CHOICE = "SINGLE_CHOICE";
	public static final String TIPO_MULTIPLE_CHOICE = "MULTIPLE_CHOICE";
	public static final String TIPO_ORDERING = "ORDERING";
	private List<Alternativa> alternativas;
	private int ordemExibicao;
	private String urlImagem;

	public List<Alternativa> getAlternativas() {
		return alternativas;
	}

	public void setAlternativas(List<Alternativa> alternativas) {
		this.alternativas = alternativas;
	}

	public int getOrdemExibicao() {
		return ordemExibicao;
	}

	public void setOrdemExibicao(int ordemExibicao) {
		this.ordemExibicao = ordemExibicao;
	}

	public String getUrlImagem() {
		return urlImagem;
	}

	public void setUrlImagem(String urlImagem) {
		this.urlImagem = urlImagem;
	}

	public boolean acertouResposta() {
		if (getTipo().equals(PerguntaQuiz.TIPO_SINGLE_CHOICE)) {
			for (Alternativa alternativa : alternativas) {
				final AlternativaEscolhaQuiz alternativaEscolha = (AlternativaEscolhaQuiz) alternativa;
				if (alternativaEscolha.isCorreta() && alternativaEscolha.isSelecionada()) {
					return true;
				}
			}
		} else if (getTipo().equals(PerguntaQuiz.TIPO_MULTIPLE_CHOICE)) {
			boolean acertou = true;
			for (Alternativa alternativa : alternativas) {
				final AlternativaEscolhaQuiz alternativaEscolha = (AlternativaEscolhaQuiz) alternativa;
				if (alternativaEscolha.isCorreta() && !alternativaEscolha.isSelecionada()
						|| !alternativaEscolha.isCorreta() && alternativaEscolha.isSelecionada()) {
					acertou = false;
				}
			}
			return acertou;
		} else if (getTipo().equals(PerguntaQuiz.TIPO_ORDERING)) {
			throw new IllegalStateException(PerguntaQuiz.TIPO_ORDERING + " não pode ser executado!");
		}
		return false;
	}
}