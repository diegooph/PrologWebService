package br.com.zalf.prolog.webservice.frota.veiculo.model;

/**
 * @deprecated at 22/05/2017.
 */
@Deprecated
public class Eixos {
    public int dianteiro;
    public int traseiro;
    public long codigo;
    public String nome;

    public Eixos() {
    }

    @Override
    public String toString() {
        return "Eixos{" +
                "dianteiro=" + dianteiro +
                ", traseiro=" + traseiro +
                ", codigo=" + codigo +
                ", nome='" + nome + '\'' +
                '}';
    }
}