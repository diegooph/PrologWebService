package br.com.zalf.prolog.webservice.raizen.produtividade.model;

import br.com.zalf.prolog.webservice.commons.gson.Exclude;
import br.com.zalf.prolog.webservice.commons.gson.RuntimeTypeAdapterFactory;
import br.com.zalf.prolog.webservice.raizen.produtividade.model.itens.RaizenProdutividadeItem;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 04/07/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public abstract class RaizenProdutividade {
    private int totalPlacaNaoCadastradas;
    protected List<? extends RaizenProdutividadeItem> itensRaizen;
    @Exclude
    private final RaizenProdutividadeAgrupamento tipoAgrupamento;

    public RaizenProdutividade(@NotNull final RaizenProdutividadeAgrupamento tipoAgrupamento) {
        this.tipoAgrupamento = tipoAgrupamento;
    }

    public int getTotalPlacaNaoCadastradas() {
        return totalPlacaNaoCadastradas;
    }

    protected void setTotalPlacaNaoCadastradas(final int totalPlacaNaoCadastradas) {
        this.totalPlacaNaoCadastradas = totalPlacaNaoCadastradas;
    }

    public void setItensRaizen(List<? extends RaizenProdutividadeItem> itensRaizen) {
        this.itensRaizen = itensRaizen;
    }

    public abstract void calculaItensNaoCadastrados();

    @NotNull
    public static RuntimeTypeAdapterFactory<RaizenProdutividade> provideTypeAdapterFactory() {
        return RuntimeTypeAdapterFactory
                .of(RaizenProdutividade.class, "tipoAgrupamento")
                .registerSubtype(RaizenProdutividadeColaborador.class, RaizenProdutividadeAgrupamento.POR_COLABORADOR.asString())
                .registerSubtype(RaizenProdutividadeData.class, RaizenProdutividadeAgrupamento.POR_DATA.asString());
    }
}