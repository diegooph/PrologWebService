package br.com.empresa.oprojeto.webservice.domain;


public class Funcao {
	private long codigo;
	private String nome;
	
	public Funcao() {
		
	}
	
	public Funcao(long codigo, String nome) {
		this.codigo = codigo;
		this.nome = nome;
	}
	
	public long getCodigo() {
		return codigo;
	}

	public void setCodigo(long codigo) {
		this.codigo = codigo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
}
