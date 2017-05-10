package br.com.zalf.prolog.webservice.commons.questoes;

/**
 * Resposta das perguntas do GSD e do checklist.
 */
public class Resposta {
	private Long codigo;
	private String resposta;
	
	public Resposta(Long codigo, String resposta) {
		this.codigo = codigo;
		this.resposta = resposta;
	}
	
	public Resposta() {
		
	}

	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public String getResposta() {
		return resposta;
	}

	public void setResposta(String resposta) {
		this.resposta = resposta;
	}
}
