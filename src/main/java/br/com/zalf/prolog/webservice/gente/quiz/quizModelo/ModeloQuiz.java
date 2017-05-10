package br.com.zalf.prolog.webservice.gente.quiz.quizModelo;

import br.com.zalf.prolog.webservice.commons.colaborador.Funcao;
import br.com.zalf.prolog.webservice.gente.quiz.quiz.PerguntaQuiz;
import br.com.zalf.prolog.webservice.gente.treinamento.Treinamento;
import com.sun.istack.internal.Nullable;

import java.util.Date;
import java.util.List;


public class ModeloQuiz {
	private Long codigo;
	private String nome;
	@Nullable
	private String descricao;
	private Date dataHoraAbertura;
	private Date dataHoraFechamento;
	/**
	 * Fun��es (cargos) que t�m acesso a este modelo de quiz.
	 */
	private List<Funcao> funcoesLiberadas;

	private List<PerguntaQuiz> perguntas;

	/**
	 * Um material de apoio para estudo pr�vio.
	 * Pode ser null pois a unidade pode ou n�o disponibilizar esse material.
	 */
	@Nullable
	private Treinamento materialApoio;

	private double porcentagemAprovacao;

	public double getPorcentagemAprovacao() {
		return porcentagemAprovacao;
	}

	public void setPorcentagemAprovacao(double porcentagemAprovacao) {
		this.porcentagemAprovacao = porcentagemAprovacao;
	}

	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Date getDataHoraAbertura() {
		return dataHoraAbertura;
	}

	public void setDataHoraAbertura(Date dataHoraAbertura) {
		this.dataHoraAbertura = dataHoraAbertura;
	}

	public Date getDataHoraFechamento() {
		return dataHoraFechamento;
	}

	public void setDataHoraFechamento(Date dataHoraFechamento) {
		this.dataHoraFechamento = dataHoraFechamento;
	}

	public List<Funcao> getFuncoesLiberadas() {
		return funcoesLiberadas;
	}

	public void setFuncoesLiberadas(List<Funcao> funcoesLiberadas) {
		this.funcoesLiberadas = funcoesLiberadas;
	}

	public List<PerguntaQuiz> getPerguntas() {
		return perguntas;
	}

	public void setPerguntas(List<PerguntaQuiz> perguntas) {
		this.perguntas = perguntas;
	}

	public Treinamento getMaterialApoio() {
		return materialApoio;
	}

	public void setMaterialApoio(Treinamento materialApoio) {
		this.materialApoio = materialApoio;
	}
}