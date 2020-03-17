package br.com.zalf.prolog.webservice.entrega.produtividade.relatorio;

import br.com.zalf.prolog.webservice.gente.colaborador.model.Colaborador;
import com.google.common.base.Preconditions;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created on 23/04/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class ProdutividadeColaboradorRelatorio {

    private Colaborador colaborador;
    private BigDecimal valorTotal;
    private List<ProdutividadeColaboradorDia> produtividadeDias;

    public Colaborador getColaborador() {
        return colaborador;
    }

    public void setColaborador(final Colaborador colaborador) {
        this.colaborador = colaborador;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(final BigDecimal valorTotal) {
        this.valorTotal = valorTotal.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public List<ProdutividadeColaboradorDia> getProdutividadeDias() {
        return produtividadeDias;
    }

    public void setProdutividadeDias(final List<ProdutividadeColaboradorDia> produtividadeDias) {
        this.produtividadeDias = produtividadeDias;
    }

    public void calculaValorTotal() {
        Preconditions.checkNotNull(produtividadeDias, "produtividadeDias não pode ser null");

        BigDecimal valorTotal = new BigDecimal(0);
        for (final ProdutividadeColaboradorDia dia : produtividadeDias) {
            valorTotal = valorTotal.add(dia.getValor());
        }
        setValorTotal(valorTotal);
    }

    @Override
    public String toString() {
        return "ProdutividadeColaboradorRelatorio{" +
                "colaborador=" + colaborador +
                ", valorTotal=" + valorTotal +
                ", produtividadeDias=" + produtividadeDias +
                '}';
    }
}
