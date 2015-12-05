package br.com.empresa.oprojeto.webservice.domain;


public class Resposta {
	private long codigo;
	private boolean resposta;
	
	public Resposta(long codigo, boolean resposta) {
		this.codigo = codigo;
		this.resposta = resposta;
	}
	
	public Resposta() {
		
	}

	public long getCodigo() {
		return codigo;
	}

	public void setCodigo(long codigo) {
		this.codigo = codigo;
	}

	public boolean isResposta() {
		return resposta;
	}

	public void setResposta(boolean resposta) {
		this.resposta = resposta;
	}
}
