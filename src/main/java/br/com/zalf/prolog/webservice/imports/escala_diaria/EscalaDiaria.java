package br.com.zalf.prolog.webservice.imports.escala_diaria;

import java.util.List;

/**
 * Created on 10/04/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class EscalaDiaria {

    private List<EscalaDiariaItem> ItensEscalaDiaria;
    private int qtdColaboradorErrado;
    private int qtdColaboradorPlacaErrada;
    private int qtdColaboradorMapaErrado;

    public EscalaDiaria() {
    }

    public List<EscalaDiariaItem> getItensEscalaDiaria() {
        return ItensEscalaDiaria;
    }

    public void setItensEscalaDiaria(final List<EscalaDiariaItem> itensEscalaDiaria) {
        this.ItensEscalaDiaria = itensEscalaDiaria;
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
        return "EscalaDiaria{" +
                "ItensEscalaDiaria=" + ItensEscalaDiaria +
                ", qtdColaboradorErrado=" + qtdColaboradorErrado +
                ", qtdColaboradorPlacaErrada=" + qtdColaboradorPlacaErrada +
                ", qtdColaboradorMapaErrado=" + qtdColaboradorMapaErrado +
                '}';
    }
}
