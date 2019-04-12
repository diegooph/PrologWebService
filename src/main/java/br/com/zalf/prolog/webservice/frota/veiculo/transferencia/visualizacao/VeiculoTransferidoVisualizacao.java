package br.com.zalf.prolog.webservice.frota.veiculo.transferencia.visualizacao;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 12/04/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class VeiculoTransferidoVisualizacao {
    /**
     * Atributo que identifica a placa do veículo.
     */
    @NotNull
    private final String placaVeiculo;
    /**
     * Uma Lista contendo a identificação de cada pneu, normalmente, número de fogo do pneu.
     */
    @NotNull
    private final List<String> codPneusCliente;

    public VeiculoTransferidoVisualizacao(@NotNull final String placaVeiculo,
                                          @NotNull final List<String> codPneusCliente) {
        this.placaVeiculo = placaVeiculo;
        this.codPneusCliente = codPneusCliente;
    }

    @NotNull
    public String getPlacaVeiculo() {
        return placaVeiculo;
    }

    @NotNull
    public List<String> getCodPneusCliente() {
        return codPneusCliente;
    }
}