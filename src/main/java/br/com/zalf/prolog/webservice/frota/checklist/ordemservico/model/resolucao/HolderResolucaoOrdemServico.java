package br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao;

import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.visualizacao.OrdemServicoAbertaVisualizacao;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.visualizacao.OrdemServicoVisualizacao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import org.jetbrains.annotations.NotNull;

/**
 * Classe que contém informações para resolver uma Ordem de Serviço completa.
 *
 * Created on 14/11/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class HolderResolucaoOrdemServico {
    /**
     * Placa do {@link Veiculo} a qual a Ordem de Serviço a ser resolvida pertence.
     */
    private String placaVeiculo;

    /**
     * Quilometragem do {@link Veiculo} no momento de resolução da Ordem de Serviço.
     */
    private long kmAtualVeiculo;

    /**
     * Objeto {@link OrdemServicoVisualizacao} cotendo as
     * informações da Ordem de Serviço que será resolvida.
     */
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