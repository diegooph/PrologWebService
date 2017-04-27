package br.com.zalf.prolog.gente.quiz;

import br.com.zalf.prolog.commons.questoes.Alternativa;

/**
 * Essa classe será usada para perguntas dos tipos {@link PerguntaQuiz#TIPO_SINGLE_CHOICE} e
 * {@link PerguntaQuiz#TIPO_MULTIPLE_CHOICE}, que são perguntas nas quais o usuário deve selecionar, respectivamente,
 * uma ou mais respostas como correta.
 */
public class AlternativaEscolhaQuiz extends Alternativa {
	/**
	 * Indica se essa alternativas é correta.
	 */
	private boolean correta;

	/**
	 * Indica se essa alternativa foi selecionada pelo usuário.
	 */
	private boolean selecionada;

	public boolean isCorreta() {
		return correta;
	}

	public void setCorreta(boolean correta) {
		this.correta = correta;
	}

	public boolean isSelecionada() {
		return selecionada;
	}

	public void setSelecionada(boolean selecionada) {
		this.selecionada = selecionada;
	}
}