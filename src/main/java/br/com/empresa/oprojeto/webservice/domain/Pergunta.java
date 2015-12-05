package br.com.empresa.oprojeto.webservice.domain;


public class Pergunta {
	private long codigo;
	private String pergunta;
	private boolean ativo;

	public Pergunta(long codigo, String pergunta, boolean ativo) {
		this.codigo = codigo;
		this.pergunta = pergunta;
		this.ativo = ativo;
	}

	public Pergunta() {
		
	}

	public long getCodigo() {
		return codigo;
	}

	public void setCodigo(long codigo) {
		this.codigo = codigo;
	}

	public String getPergunta() {
		return pergunta;
	}

	public void setPergunta(String pergunta) {
		this.pergunta = pergunta;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}
}
