package br.com.zalf.prolog.webservice.frota.checklist.ordemServico.model.conserto;

import br.com.zalf.prolog.webservice.frota.checklist.ordemServico.model.visualizacao.OrdemServicoAbertaVisualizacao;
import br.com.zalf.prolog.webservice.frota.checklist.ordemServico.model.visualizacao.OrdemServicoVisualizacao;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 14/11/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class FechamentoOrdemServico {
    private String placaVeiculo;
    private long kmAtualVeiculo;
    private OrdemServicoVisualizacao ordemServico;

    public FechamentoOrdemServico() {

    }

    @NotNull
    public static FechamentoOrdemServico createDummy() {
        final FechamentoOrdemServico fechamento = new FechamentoOrdemServico();
        fechamento.setPlacaVeiculo("AAA1234");
        fechamento.setKmAtualVeiculo(12345);
        fechamento.setOrdemServico(OrdemServicoAbertaVisualizacao.createDummy());
        return fechamento;
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