package br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao;

import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.visualizacao.OrdemServicoAbertaVisualizacao;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.visualizacao.OrdemServicoVisualizacao;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 14/11/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class HolderResolucaoOrdemServico {
    private String placaVeiculo;
    private long kmAtualVeiculo;
    private OrdemServicoVisualizacao ordemServico;

    public HolderResolucaoOrdemServico() {

    }

    @NotNull
    public static HolderResolucaoOrdemServico createDummy() {
        final HolderResolucaoOrdemServico resolucaoOrdemServico = new HolderResolucaoOrdemServico();
        resolucaoOrdemServico.setPlacaVeiculo("AAA1234");
        resolucaoOrdemServico.setKmAtualVeiculo(12345);
        resolucaoOrdemServico.setOrdemServico(OrdemServicoAbertaVisualizacao.createDummy());
        return resolucaoOrdemServico;
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

    public OrdemServicoVisualizacao getOrdemServico() {
        return ordemServico;
    }

    public void setOrdemServico(final OrdemServicoVisualizacao ordemServico) {
        this.ordemServico = ordemServico;
    }
}