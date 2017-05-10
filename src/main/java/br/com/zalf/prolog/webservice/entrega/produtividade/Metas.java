package br.com.zalf.prolog.webservice.entrega.produtividade;

/**
 * Created by jean on 22/02/16.
 * Objeto utilizado na parte web para edição de metas existentes no BD
 */
@Deprecated
public class Metas<T> {

    private int codigo;
    private String nome;
    /**
     * <T> pode ser Time ou double, dependendo da meta a ser editada.
     */
    private T valor;

    public Metas() {
    }

    public Metas(int codigo, String nome, T valor) {
        this.codigo = codigo;
        this.nome = nome;
        this.valor = valor;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public T getValor() {
        return valor;
    }

    public void setValor(T valor) {
        this.valor = valor;
    }

}
