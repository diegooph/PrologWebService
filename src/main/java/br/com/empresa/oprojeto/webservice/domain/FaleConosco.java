package br.com.empresa.oprojeto.webservice.domain;


import java.util.Date;

public class FaleConosco {
	private Long codigo;
	private long cpfColaborador;
	private Date data;
	private String categoria;
	private String descricao;

	public FaleConosco() {
		
	}

	public FaleConosco(Long codigo,long cpfColaborador, Date data, String categoria, String descricao) {
		super();
		this.codigo = codigo;
		this.cpfColaborador = cpfColaborador;
		this.data = data;
		this.categoria = categoria;
		this.descricao = descricao;
	}
	

	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public long getCpfColaborador() {
		return cpfColaborador;
	}

	public void setCpfColaborador(long cpfColaborador) {
		this.cpfColaborador = cpfColaborador;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
}
