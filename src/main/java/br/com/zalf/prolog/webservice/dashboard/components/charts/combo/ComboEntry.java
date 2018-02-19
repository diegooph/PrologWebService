package br.com.zalf.prolog.webservice.dashboard.components.charts.combo;

import br.com.zalf.prolog.webservice.dashboard.base.Entry;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 10/01/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class ComboEntry extends Entry {
    private double valor;
    @NotNull
    private String representacaoValor;
    private int index;

    @NotNull
    public static ComboEntry create(final double valor,
                                    @NotNull final String representacaoValor,
                                    final int index) {
        return new ComboEntry(valor, representacaoValor, index);
    }

    private ComboEntry(final double valor,
                       @NotNull final String representacaoValor,
                       final int index) {
        this.valor = valor;
        this.representacaoValor = representacaoValor;
        this.index = index;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    @NotNull
    public String getRepresentacaoValor() {
        return representacaoValor;
    }

    public void setRepresentacaoValor(@NotNull String representacaoValor) {
        this.representacaoValor = representacaoValor;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return "ComboEntry{" +
                ", valor=" + valor +
                ", representacaoValor='" + representacaoValor + '\'' +
                ", index=" + index +
                '}';
    }
}