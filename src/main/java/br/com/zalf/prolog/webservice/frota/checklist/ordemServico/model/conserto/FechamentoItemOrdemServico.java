package br.com.zalf.prolog.webservice.frota.checklist.ordemServico.model.conserto;

import br.com.zalf.prolog.webservice.frota.checklist.ordemServico.model.visualizacao.item.ItemOrdemServicoVisualizacao;

import java.util.List;

/**
 * Created on 14/11/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class FechamentoItemOrdemServico {
    private String placaVeiculo;
    private long kmAtualVeiculo;
    private List<ItemOrdemServicoVisualizacao> itens;

    public FechamentoItemOrdemServico() {

    }

    public String getPlacaVeiculo() {
        return placaVeiculo;
    }

    public void setPlacaVeiculo(final String placaVeiculo) {
        this.placaVeiculo = placaVeiculo;
    }

    public long getKmAtualVeiculo() {
        return kmAtualVeiculo;
    }

    public void setKmAtualVeiculo(final long kmAtualVeiculo) {
        this.kmAtualVeiculo = kmAtualVeiculo;
    }

    public List<ItemOrdemServicoVisualizacao> getItens() {
        return itens;
    }

    public void setItens(final List<ItemOrdemServicoVisualizacao> itens) {
        this.itens = itens;
    }
}