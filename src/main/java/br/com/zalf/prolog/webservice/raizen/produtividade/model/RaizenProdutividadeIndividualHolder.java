package br.com.zalf.prolog.webservice.raizen.produtividade.model;

import br.com.zalf.prolog.webservice.raizen.produtividade.model.itens.RaizenprodutividadeItemIndividual;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created on 19/07/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class RaizenProdutividadeIndividualHolder {
    @NotNull
    private final List<RaizenprodutividadeItemIndividual> produtividadeItens;
    @NotNull
    private final BigDecimal valorTotal;

    public RaizenProdutividadeIndividualHolder(
            @NotNull final List<RaizenprodutividadeItemIndividual> produtividadeItens) {
        this.produtividadeItens = produtividadeItens;
        this.valorTotal = calculaValorTotal();
    }

    @NotNull
    public List<RaizenprodutividadeItemIndividual> getProdutividadeItens() {
        return produtividadeItens;
    }

    @NotNull
    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    @NotNull
    private BigDecimal calculaValorTotal() {
        //noinspection ConstantConditions
        Preconditions.checkState(produtividadeItens != null, "Para calcular o valor total você " +
                "precisa setar os itens antes");
        return produtividadeItens
                .stream()
                .map(RaizenprodutividadeItemIndividual::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}