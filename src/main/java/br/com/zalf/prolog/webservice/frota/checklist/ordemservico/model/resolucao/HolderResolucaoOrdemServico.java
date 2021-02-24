package br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao;

import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.visualizacao.OrdemServicoAbertaVisualizacao;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.visualizacao.OrdemServicoVisualizacao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import org.jetbrains.annotations.NotNull;

/**
 * Classe que contém informações para resolver uma Ordem de Serviço completa.
 * <p>
 * Created on 14/11/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class HolderResolucaoOrdemServico {
    private Long codVeiculo;
    /**
     * Placa do {@link Veiculo} a qual a Ordem de Serviço a ser resolvida pertence.
     */
    private String placaVeiculo;

    /**
     * Quilometragem atual do {@link Veiculo}.
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
        resolucaoOrdemServico.setCodVeiculo(1L);
        resolucaoOrdemServico.setPlacaVeiculo("AAA1234");
        resolucaoOrdemServico.setKmAtualVeiculo(12345);
        resolucaoOrdemServico.setOrdemServico(OrdemServicoAbertaVisualizacao.createDummy());
        return resolucaoOrdemServico;
    }

    public Long getCodVeiculo() {
        return codVeiculo;
    }

    public void setCodVeiculo(final Long codVeiculo) {
        this.codVeiculo = codVeiculo;
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