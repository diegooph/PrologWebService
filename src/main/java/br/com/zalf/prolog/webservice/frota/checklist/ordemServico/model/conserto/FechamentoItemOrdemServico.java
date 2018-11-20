package br.com.zalf.prolog.webservice.frota.checklist.ordemServico.model.conserto;

import br.com.zalf.prolog.webservice.frota.checklist.ordemServico.model.visualizacao.item.ItemOrdemServicoAberto;
import br.com.zalf.prolog.webservice.frota.checklist.ordemServico.model.visualizacao.item.ItemOrdemServicoVisualizacao;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
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

    @NotNull
    public static FechamentoItemOrdemServico createDummy() {
        final FechamentoItemOrdemServico fechamento = new FechamentoItemOrdemServico();
        fechamento.setPlacaVeiculo("AAA1234");
        fechamento.setKmAtualVeiculo(12345);
        final List<ItemOrdemServicoVisualizacao> itens = new ArrayList<>();
        itens.add(ItemOrdemServicoAberto.createDummy());
        itens.add(ItemOrdemServicoAberto.createDummy());
        itens.add(ItemOrdemServicoAberto.createDummy());
        fechamento.setItens(itens);
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

    public List<ItemOrdemServicoVisualizacao> getItens() {
        return itens;
    }

    public void setItens(final List<ItemOrdemServicoVisualizacao> itens) {
        this.itens = itens;
    }
}