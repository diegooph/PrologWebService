package br.com.zalf.prolog.webservice.commons.colaborador;

import br.com.zalf.prolog.webservice.permissao.pilares.Pilar;

import java.util.List;

/**
 * Função do colaborador
 */
public class Funcao {
	private Long codigo;
	private String nome;
	private List<Pilar> permissoes;
	
	public Funcao() {
		
	}
	
	public Funcao(Long codigo, String nome) {
		this.codigo = codigo;
		this.nome = nome;
	}

	public List<Pilar> getPermissoes() {
		return permissoes;
	}

	public void setPermissoes(List<Pilar> permissoes) {
		this.permissoes = permissoes;
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

	@Override
	public String toString() {
		return "Funcao{" +
				"codigo=" + codigo +
				", nome='" + nome + '\'' +
				", permissões=" + permissoes +
				'}';
	}
}
