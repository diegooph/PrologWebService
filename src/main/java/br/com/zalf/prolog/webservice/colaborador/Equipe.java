package br.com.zalf.prolog.webservice.colaborador;

/**
 * Created by jean on 23/02/16.
 *
 */
public class Equipe {
    private long codigo;
    private String nome;

    public Equipe() {

    }

    public Equipe(Long codigo, String nome) {
        this.codigo = codigo;
        this.nome = nome;
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
