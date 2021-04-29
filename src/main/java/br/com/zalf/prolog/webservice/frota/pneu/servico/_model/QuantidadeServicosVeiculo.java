package br.com.zalf.prolog.webservice.frota.pneu.servico._model;

/**
 * Created on 12/4/17
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class QuantidadeServicosVeiculo extends QuantidadeServicos {
    private Long codVeiculo;
    private String placaVeiculo;
    private String identificadorFrota;

    public QuantidadeServicosVeiculo() {
        setAgrupamento(AgrupamentoQuantidadeServicos.POR_VEICULO);
    }

    public Long getCodVeiculo() {
        return codVeiculo;
    }

    public void setCodVeiculo(Long codVeiculo) {
        this.codVeiculo = codVeiculo;
    }

    public String getPlacaVeiculo() {
        return placaVeiculo;
    }

    public void setPlacaVeiculo(String placaVeiculo) {
        this.placaVeiculo = placaVeiculo;
    }

    public String getIdentificadorFrota() {
        return identificadorFrota;
    }

    public void setIdentificadorFrota(String identificadorFrota) {
        this.identificadorFrota = identificadorFrota;
    }
}