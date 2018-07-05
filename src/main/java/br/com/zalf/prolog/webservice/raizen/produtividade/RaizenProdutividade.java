package br.com.zalf.prolog.webservice.raizen.produtividade;

import java.util.List;

/**
 * Created on 04/07/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class RaizenProdutividade {

    private List<RaizenProdutividadeItem> raizenItens;

    public RaizenProdutividade() {
    }

    public List<RaizenProdutividadeItem> getItensRaizen() {
        return raizenItens;
    }

    public void setRaizenItens(final List<RaizenProdutividadeItem> raizenItens) {
        this.raizenItens = raizenItens;
    }

    @Override
    public String toString() {
        return "RaizenProdutividade{" +
                "raizenItens=" + raizenItens +
                "}";
    }
}