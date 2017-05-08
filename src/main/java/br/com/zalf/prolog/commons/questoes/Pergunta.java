package br.com.zalf.prolog.commons.questoes;

/**
 * Utilizado para criar as perguntas do GSD e do checklist
 */
public class Pergunta {
	private Long codigo;
	private String pergunta;
	private String tipo;

	public Pergunta(Long codigo, String pergunta, String tipo) {
		this.codigo = codigo;
		this.pergunta = pergunta;
		this.tipo = tipo;
	}

	public Pergunta() {

	}

	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public String getPergunta() {
		return pergunta;
	}

	public void setPergunta(String pergunta) {
		this.pergunta = pergunta;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	@Override
	public String toString() {
		return "Pergunta{" +
				"codigo=" + codigo +
				", pergunta='" + pergunta + '\'' +
				", tipo='" + tipo + '\'' +
				'}';
	}
}

