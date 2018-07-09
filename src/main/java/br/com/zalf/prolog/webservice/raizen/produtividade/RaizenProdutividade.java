package br.com.zalf.prolog.webservice.raizen.produtividade;

import java.util.List;

/**
 * Created on 04/07/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class RaizenProdutividade {

    private int totalCPFNOK;
    private int totalPlacaNOK;
    private RaizenProdutividadeAgrupamento tipoAgrupamento;
    private List<RaizenProdutividadeItem> raizenItens;

    public RaizenProdutividade() {
    }

    public int getTotalCPFNOK() {
        return totalCPFNOK;
    }

    public void setTotalCPFNOK(int totalCPFNOK) {
        this.totalCPFNOK = totalCPFNOK;
    }

    public int getTotalPlacaNOK() {
        return totalPlacaNOK;
    }

    public void setTotalPlacaNOK(int totalPlacaNOK) {
        this.totalPlacaNOK = totalPlacaNOK;
    }

    public RaizenProdutividadeAgrupamento getTipoAgrupamento() {
        return tipoAgrupamento;
    }

    public void setTipoAgrupamento(RaizenProdutividadeAgrupamento tipoAgrupamento) {
        this.tipoAgrupamento = tipoAgrupamento;
    }

    public List<RaizenProdutividadeItem> getRaizenItens() {
        return raizenItens;
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