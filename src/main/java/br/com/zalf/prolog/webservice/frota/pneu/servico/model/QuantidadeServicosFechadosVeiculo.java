package br.com.zalf.prolog.webservice.frota.pneu.servico.model;

/**
 * Created on 12/4/17
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class QuantidadeServicosFechadosVeiculo extends QuantidadeServicosFechados {
    private String placaVeiculo;

    public QuantidadeServicosFechadosVeiculo() {
        setAgrupamento(AgrupamentoServicosFechados.POR_VEICULO);
    }

    public String getPlacaVeiculo() {
        return placaVeiculo;
    }

    public void setPlacaVeiculo(String placaVeiculo) {
        this.placaVeiculo = placaVeiculo;
    }
}