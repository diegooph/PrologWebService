package br.com.zalf.prolog.webservice.commons.colaborador;

/**
 * Created by jean on 23/02/16.
 *
 */
public class Equipe {
    private long codigo;
    private String nome;

    public Equipe() {
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

    @Override
    public String toString() {
        return "Equipe{" +
                "codigo=" + codigo +
                ", nome='" + nome + '\'' +
                '}';
    }
}
