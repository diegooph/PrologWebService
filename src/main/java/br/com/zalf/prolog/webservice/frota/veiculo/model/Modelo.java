package br.com.zalf.prolog.webservice.frota.veiculo.model;

/**
 * Created by jean on 20/06/16.
 */
public class Modelo {

    private long codigo;
    private String nome;

    public Modelo() {
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
        return "Modelo{" +
                "codigo=" + codigo +
                ", nome='" + nome + '\'' +
                '}';
    }
}
