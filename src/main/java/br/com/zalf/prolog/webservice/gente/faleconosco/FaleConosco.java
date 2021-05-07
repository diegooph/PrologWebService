package br.com.zalf.prolog.webservice.gente.faleconosco;


import br.com.zalf.prolog.webservice.gente.colaborador.model.Colaborador;

import java.time.LocalDateTime;

/**
 * Informações do formulário fale conosco.
 *
 *
 * As constantes STATUS existem apenas para questões de filtros na busca, não é preciso ter uma variável status para
 * informar se o fale conosco já foi ou não respondido, basta verificar seo objeto colaboradorFeedback é nulo.
 * Se for, então ainda não foi respondido.
 *
 */
public class FaleConosco {
	public static final String STATUS_PENDENTE = "PENDENTE";
	public static final String STATUS_RESPONDIDO = "RESPONDIDO";
	private Long codigo;
	private Colaborador colaborador;
	private LocalDateTime data;
	private Categoria categoria;
	private String descricao;
    private String status;

	/**
	 * Colaborador responsável por fornecer um feedback
     */
	private Colaborador colaboradorFeedback;
	private LocalDateTime dataFeedback;
	private String feedback;

	public enum Categoria {
		RECLAMACAO("R"),
		SUGESTAO("S");

		private final String s;

		Categoria(final String s) {
			this.s = s;
		}

		public static Categoria fromString(final String text) throws IllegalArgumentException {
			if (text != null) {
				for (final Categoria b : Categoria.values()) {
					if (text.equalsIgnoreCase(b.s)) {
						return b;
					}
				}
			}
			throw new IllegalArgumentException("Nenhum enum com esse valor encontrado");
		}

		public String asString() {
			return s;
		}
	}

	public FaleConosco() {

	}

    @Override
    public String toString() {
        return "FaleConosco{" +
                "codigo=" + codigo +
                ", colaborador=" + colaborador +
                ", data=" + data +
                ", categoria=" + categoria +
                ", descricao='" + descricao + '\'' +
                ", status='" + status + '\'' +
                ", colaboradorFeedback=" + colaboradorFeedback +
                ", dataFeedback=" + dataFeedback +
                ", feedback='" + feedback + '\'' +
                '}';
    }

	public Colaborador getColaborador() {
		return colaborador;
	}

	public void setColaborador(final Colaborador colaborador) {
		this.colaborador = colaborador;
	}

	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(final Long codigo) {
		this.codigo = codigo;
	}

	public LocalDateTime getData() {
		return data;
	}

	public void setData(final LocalDateTime data) {
		this.data = data;
	}

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(final Categoria categoria) {
		this.categoria = categoria;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(final String descricao) {
		this.descricao = descricao;
	}

	public String getFeedback() {
		return feedback;
	}

	public void setFeedback(final String feedback) {
		this.feedback = feedback;
	}

	public Colaborador getColaboradorFeedback() {
		return colaboradorFeedback;
	}

	public void setColaboradorFeedback(final Colaborador colaboradorFeedback) {
		this.colaboradorFeedback = colaboradorFeedback;
	}

	public LocalDateTime getDataFeedback() {
		return dataFeedback;
	}

	public void setDataFeedback(final LocalDateTime dataFeedback) {
		this.dataFeedback = dataFeedback;
	}
}
