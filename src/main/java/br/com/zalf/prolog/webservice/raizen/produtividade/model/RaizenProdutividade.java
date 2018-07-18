package br.com.zalf.prolog.webservice.raizen.produtividade.model;

import br.com.zalf.prolog.webservice.commons.gson.RuntimeTypeAdapterFactory;

/**
 * Created on 04/07/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class RaizenProdutividade {

    private int totalCPFNOK;
    private int totalPlacaNOK;
    private RaizenProdutividadeAgrupamento tipoAgrupamento;

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

    public static RuntimeTypeAdapterFactory<RaizenProdutividade> provideTypeAdapterFactory() {
        return RuntimeTypeAdapterFactory
                .of(RaizenProdutividade.class, "agrupamento")
                .registerSubtype(RaizenProdutividadeColaborador.class, RaizenProdutividadeAgrupamento.POR_COLABORADOR.asString())
                .registerSubtype(RaizenProdutividadeData.class, RaizenProdutividadeAgrupamento.POR_DATA.asString());
    }

    public RaizenProdutividadeAgrupamento getTipoAgrupamento() {
        return tipoAgrupamento;
    }

    public void setTipoAgrupamento(RaizenProdutividadeAgrupamento tipoAgrupamento) {
        this.tipoAgrupamento = tipoAgrupamento;
    }
}