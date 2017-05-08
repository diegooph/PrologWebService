package br.com.zalf.prolog.gente.fale_conosco;


import br.com.zalf.prolog.commons.colaborador.Colaborador;

import java.util.Date;

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
	private Date data;
	private Categoria categoria;
	private String descricao;
    private String status;

	/**
	 * Colaborador responsável por fornecer um feedback
     */
	private Colaborador colaboradorFeedback;
	private Date dataFeedback;
	private String feedback;

	public enum Categoria {
		RECLAMACAO("R"),
		SUGESTAO("S");

		private final String s;

		Categoria(String s) {
			this.s = s;
		}

		public String asString() {
			return s;
		}

		public static Categoria fromString(String text) throws IllegalArgumentException{
			if (text != null) {
				for (Categoria b : Categoria.values()) {
					if (text.equalsIgnoreCase(b.s)) {
						return b;
					}
				}
			}
			throw new IllegalArgumentException("Nenhum enum com esse valor encontrado");
		}
	}

	public FaleConosco() {

	}

	public FaleConosco(Long codigo, Colaborador colaborador, Date data, Categoria categoria, String descricao, Colaborador colaboradorFeedback, Date dataFeedback, String feedback) {
		this.codigo = codigo;
		this.colaborador = colaborador;
		this.data = data;
		this.categoria = categoria;
		this.descricao = descricao;
		this.colaboradorFeedback = colaboradorFeedback;
		this.dataFeedback = dataFeedback;
		this.feedback = feedback;
	}

	public Colaborador getColaborador() {
		return colaborador;
	}

	public void setColaborador(Colaborador colaborador) {
		this.colaborador = colaborador;
	}

	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getFeedback() {
		return feedback;
	}

	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}

	public Colaborador getColaboradorFeedback() {
		return colaboradorFeedback;
	}

	public void setColaboradorFeedback(Colaborador colaboradorFeedback) {
		this.colaboradorFeedback = colaboradorFeedback;
	}

	public Date getDataFeedback() {
		return dataFeedback;
	}

	public void setDataFeedback(Date dataFeedback) {
		this.dataFeedback = dataFeedback;
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
}
