package br.com.zalf.prolog.webservice.gente.quiz.quiz.model;

import br.com.zalf.prolog.webservice.colaborador.Colaborador;

import java.util.Date;
import java.util.List;

public class Quiz {
	private Long codigo;
	private Long codModeloQuiz;
	private String nome;
	private Colaborador colaborador;
	private Date dataHoraRealizacao;
	private List<PerguntaQuiz> perguntas;
	private int qtdRespostasCorretas;
	private int qtdRespostasErradas;
	private long tempoRealizacaoInMillis;

	/**
	 * Informa se o usuário foi aprovado no quiz
     */
	private boolean aprovado;

	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public Long getCodModeloQuiz() {
		return codModeloQuiz;
	}

	public void setCodModeloQuiz(Long codModeloQuiz) {
		this.codModeloQuiz = codModeloQuiz;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Colaborador getColaborador() {
		return colaborador;
	}

	public void setColaborador(Colaborador colaborador) {
		this.colaborador = colaborador;
	}

	public Date getDataHoraRealizacao() {
		return dataHoraRealizacao;
	}

	public void setDataHoraRealizacao(Date dataHoraRealizacao) {
		this.dataHoraRealizacao = dataHoraRealizacao;
	}

	public List<PerguntaQuiz> getPerguntas() {
		return perguntas;
	}

	public void setPerguntas(List<PerguntaQuiz> perguntas) {
		this.perguntas = perguntas;
	}

	public int getQtdRespostasCorretas() {
		return qtdRespostasCorretas;
	}

	public void setQtdRespostasCorretas(int qtdRespostasCorretas) {
		this.qtdRespostasCorretas = qtdRespostasCorretas;
	}

	public int getQtdRespostasErradas() {
		return qtdRespostasErradas;
	}

	public void setQtdRespostasErradas(int qtdRespostasErradas) {
		this.qtdRespostasErradas = qtdRespostasErradas;
	}

	public long getTempoRealizacaoInMillis() {
		return tempoRealizacaoInMillis;
	}

	public void setTempoRealizacaoInMillis(long tempoRealizacaoInMillis) {
		this.tempoRealizacaoInMillis = tempoRealizacaoInMillis;
	}

	public boolean getAprovado() {		return aprovado;
	}

	public void setAprovado(boolean aprovado) {
		this.aprovado = aprovado;
	}
}