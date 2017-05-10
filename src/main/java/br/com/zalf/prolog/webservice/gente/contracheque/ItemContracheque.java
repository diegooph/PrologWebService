package br.com.zalf.prolog.webservice.gente.contracheque;

/**
 * Created by Zalf on 18/11/16.
 */
public class ItemContracheque {

    /**
     * Código pode conter letras, justificando o uso da String
     */
    private String codigo;
    private String descricao;
    private String subDescricao;
    private Double valor;

    public ItemContracheque() {
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getSubDescricao() {
        return subDescricao;
    }

    public void setSubDescricao(String subDescricao) {
        this.subDescricao = subDescricao;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        return " item{" +
                "codigo=" + codigo +
                ", descrição='" + descricao + '\'' +
                ", subDescrição='" + subDescricao + '\'' +
                ", valor=" + valor +
                '}';
    }
}
