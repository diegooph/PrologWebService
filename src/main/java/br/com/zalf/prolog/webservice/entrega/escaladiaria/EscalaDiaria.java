package br.com.zalf.prolog.webservice.entrega.escaladiaria;

import com.google.common.base.Preconditions;

import java.util.List;

/**
 * Created on 10/04/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class EscalaDiaria {

    private List<EscalaDiariaItem> itensEscalaDiaria;
    private int qtdColaboradoresErrados;
    private int qtdPlacasErradas;
    private int qtdMapasErrados;

    public EscalaDiaria() {
    }

    public List<EscalaDiariaItem> getItensEscalaDiaria() {
        return itensEscalaDiaria;
    }

    public void setItensEscalaDiaria(final List<EscalaDiariaItem> itensEscalaDiaria) {
        this.itensEscalaDiaria = itensEscalaDiaria;
        calculaItensErrados();
    }

    public int getQtdColaboradoresErrados() {
        return qtdColaboradoresErrados;
    }

    public void setQtdColaboradoresErrados(final int qtdColaboradoresErrados) {
        this.qtdColaboradoresErrados = qtdColaboradoresErrados;
    }

    public int getQtdPlacasErradas() {
        return qtdPlacasErradas;
    }

    public void setQtdPlacasErradas(final int qtdPlacasErradas) {
        this.qtdPlacasErradas = qtdPlacasErradas;
    }

    public int getQtdMapasErrados() {
        return qtdMapasErrados;
    }

    public void setQtdMapasErrados(final int qtdMapasErrados) {
        this.qtdMapasErrados = qtdMapasErrados;
    }

    private void calculaItensErrados() {
        Preconditions.checkNotNull(itensEscalaDiaria, "itensEscalaDiaria não pode ser null!");
        int qtdColaboradoresErrados = 0;
        int qtdPlacasErradas = 0;
        int qtdMapasErrados = 0;
        for (final EscalaDiariaItem item : itensEscalaDiaria) {
            if (!item.isCpfMotoristaOk() || !item.isCpfAjudante1Ok() || !item.isCpfAjudante2Ok()) {
                qtdColaboradoresErrados++;
            }
            if (!item.isPlacaOk()) {
                qtdPlacasErradas++;
            }
            if (!item.isMapaOk()) {
                qtdMapasErrados++;
            }
        }
        this.qtdColaboradoresErrados = qtdColaboradoresErrados;
        this.qtdPlacasErradas = qtdPlacasErradas;
        this.qtdMapasErrados = qtdMapasErrados;
    }

    @Override
    public String toString() {
        return "EscalaDiaria{" +
                "itensEscalaDiaria=" + itensEscalaDiaria +
                ", qtdColaboradoresErrados=" + qtdColaboradoresErrados +
                ", qtdPlacasErradas=" + qtdPlacasErradas +
                ", qtdMapasErrados=" + qtdMapasErrados +
                '}';
    }
}
