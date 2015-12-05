package br.com.empresa.oprojeto.webservice.domain.treinamento;

import java.util.Date;

public class Treinamento {
	private long codigo;
	private long codUnidade;
	private String titulo;
	private String descricao;
	private String linkArquivo;
	private Date dataLiberacao;

	public Treinamento() {
		
	}

	public Treinamento(int codigo, int codUnidade, String titulo, String descricao, String linkArquivo,
			Date dataLiberacao) {
		super();
		this.codigo = codigo;
		this.codUnidade = codUnidade;
		this.titulo = titulo;
		this.descricao = descricao;
		this.linkArquivo = linkArquivo;
		this.dataLiberacao = dataLiberacao;
	}

	public long getCodigo() {
		return codigo;
	}

	public void setCodigo(long codigo) {
		this.codigo = codigo;
	}

	public long getCodUnidade() {
		return codUnidade;
	}

	public void setCodUnidade(long codUnidade) {
		this.codUnidade = codUnidade;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getLinkArquivo() {
		return linkArquivo;
	}

	public void setLinkArquivo(String linkArquivo) {
		this.linkArquivo = linkArquivo;
	}

	public Date getDataLiberacao() {
		return dataLiberacao;
	}

	public void setDataLiberacao(Date dataLiberacao) {
		this.dataLiberacao = dataLiberacao;
	}
}
