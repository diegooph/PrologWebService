package br.com.zalf.prolog.webservice.entrega.produtividade.relatorio;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Created on 23/04/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class ProdutividadeColaboradorDia {

    private LocalDate data;
    private double qtdCaixas;
    private int fator;
    private BigDecimal valor;

    public LocalDate getData() {
        return data;
    }

    public void setData(final LocalDate data) {
        this.data = data;
    }

    public double getQtdCaixas() {
        return qtdCaixas;
    }

    public void setQtdCaixas(final double qtdCaixas) {
        this.qtdCaixas = qtdCaixas;
    }

    public int getFator() {
        return fator;
    }

    public void setFator(final int fator) {
        this.fator = fator;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(final BigDecimal valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        return "ProdutividadeColaboradorDia{" +
                "data=" + data +
                ", qtdCaixas=" + qtdCaixas +
                ", fator=" + fator +
                ", valor=" + valor +
                '}';
    }
}
