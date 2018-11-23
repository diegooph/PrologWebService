package br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao;

import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.visualizacao.item.ItemOrdemServicoPendente;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.visualizacao.item.ItemOrdemServicoVisualizacao;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 14/11/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class HolderResolucaoItensOrdemServico {
    private String placaVeiculo;
    private long kmAtualVeiculo;
    private List<ItemOrdemServicoVisualizacao> itens;

    public HolderResolucaoItensOrdemServico() {

    }

    @NotNull
    public static HolderResolucaoItensOrdemServico createDummy() {
        final HolderResolucaoItensOrdemServico resolucaoItensOrdemServico = new HolderResolucaoItensOrdemServico();
        resolucaoItensOrdemServico.setPlacaVeiculo("AAA1234");
        resolucaoItensOrdemServico.setKmAtualVeiculo(12345);
        final List<ItemOrdemServicoVisualizacao> itens = new ArrayList<>();
        itens.add(ItemOrdemServicoPendente.createDummy());
        itens.add(ItemOrdemServicoPendente.createDummy());
        itens.add(ItemOrdemServicoPendente.createDummy());
        resolucaoItensOrdemServico.setItens(itens);
        return resolucaoItensOrdemServico;
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