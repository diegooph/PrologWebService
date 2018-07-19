package br.com.zalf.prolog.webservice.raizen.produtividade.model;

import br.com.zalf.prolog.webservice.raizen.produtividade.model.itens.RaizenprodutividadeItemIndividual;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created on 19/07/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class RaizenProdutividadeIndividualHolder {
    private List<RaizenprodutividadeItemIndividual> produtividadeItens;
    private BigDecimal valorTotal;

    public RaizenProdutividadeIndividualHolder() {

    }

    public List<RaizenprodutividadeItemIndividual> getProdutividadeItens() {
        return produtividadeItens;
    }

    public void setProdutividadeItens(final List<RaizenprodutividadeItemIndividual> produtividadeItens) {
        this.produtividadeItens = produtividadeItens;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(final BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }
}