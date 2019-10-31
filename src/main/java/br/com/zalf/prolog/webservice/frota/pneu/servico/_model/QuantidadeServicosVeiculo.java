package br.com.zalf.prolog.webservice.frota.pneu.servico._model;

/**
 * Created on 12/4/17
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class QuantidadeServicosVeiculo extends QuantidadeServicos {
    private String placaVeiculo;

    public QuantidadeServicosVeiculo() {
        setAgrupamento(AgrupamentoQuantidadeServicos.POR_VEICULO);
    }

    public String getPlacaVeiculo() {
        return placaVeiculo;
    }

    public void setPlacaVeiculo(String placaVeiculo) {
        this.placaVeiculo = placaVeiculo;
    }
}