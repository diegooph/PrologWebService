package br.com.zalf.prolog.webservice.imports.escala_diaria;

import java.util.List;

/**
 * Created on 10/04/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class EscalaHolder {

    private List<EscalaDiaria> escalasDiarias;
    private int qtdColaboradorErrado;
    private int qtdColaboradorPlacaErrada;
    private int qtdColaboradorMapaErrado;

    public EscalaHolder() {
    }

    public List<EscalaDiaria> getEscalasDiarias() {
        return escalasDiarias;
    }

    public void setEscalasDiarias(final List<EscalaDiaria> escalasDiarias) {
        this.escalasDiarias = escalasDiarias;
    }

    public int getQtdColaboradorErrado() {
        return qtdColaboradorErrado;
    }

    public void setQtdColaboradorErrado(final int qtdColaboradorErrado) {
        this.qtdColaboradorErrado = qtdColaboradorErrado;
    }

    public int getQtdColaboradorPlacaErrada() {
        return qtdColaboradorPlacaErrada;
    }

    public void setQtdColaboradorPlacaErrada(final int qtdColaboradorPlacaErrada) {
        this.qtdColaboradorPlacaErrada = qtdColaboradorPlacaErrada;
    }

    public int getQtdColaboradorMapaErrado() {
        return qtdColaboradorMapaErrado;
    }

    public void setQtdColaboradorMapaErrado(final int qtdColaboradorMapaErrado) {
        this.qtdColaboradorMapaErrado = qtdColaboradorMapaErrado;
    }

    @Override
    public String toString() {
        return "EscalaHolder{" +
                "escalasDiarias=" + escalasDiarias +
                ", qtdColaboradorErrado=" + qtdColaboradorErrado +
                ", qtdColaboradorPlacaErrada=" + qtdColaboradorPlacaErrada +
                ", qtdColaboradorMapaErrado=" + qtdColaboradorMapaErrado +
                '}';
    }
}
