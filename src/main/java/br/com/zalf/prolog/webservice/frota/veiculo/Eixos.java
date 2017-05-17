package br.com.zalf.prolog.webservice.frota.veiculo;

/**
 * Created by jean on 19/06/16.
 */

public class Eixos{
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