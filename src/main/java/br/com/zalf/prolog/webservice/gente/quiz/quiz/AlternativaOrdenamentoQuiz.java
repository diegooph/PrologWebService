package br.com.zalf.prolog.webservice.gente.quiz.quiz;

import br.com.zalf.prolog.webservice.commons.questoes.Alternativa;

/**
 * Essa classe será usada para perguntas do tipo {@link PerguntaQuiz#TIPO_ORDERING}, que são perguntas nas quais o
 * usuário deve realizar o ordenamento correto das alternativas.
 */
public class AlternativaOrdenamentoQuiz extends Alternativa {
	/**
	 * Indica a ordem correta dessa alternativa.
	 */
	private int ordemCorreta;

	/**
	 * Indica a ordem selecionada pelo usuário para essa alternativa.
	 */
	private int ordemSelecionada;

	public int getOrdemCorreta() {
		return ordemCorreta;
	}

	public void setOrdemCorreta(int ordemCorreta) {
		this.ordemCorreta = ordemCorreta;
	}

	public int getOrdemSelecionada() {
		return ordemSelecionada;
	}

	public void setOrdemSelecionada(int ordemSelecionada) {
		this.ordemSelecionada = ordemSelecionada;
	}
}