package br.com.zalf.prolog.webservice.frota.veiculo.transferencia.realizacao;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 12/04/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class VeiculoTransferencia {
    @NotNull
    private final Long codVeiculo;
    @NotNull
    private final List<Long> codPneusAplicadosVeiculo;

    public VeiculoTransferencia(@NotNull final Long codVeiculo,
                                @NotNull final List<Long> codPneusAplicadosVeiculo) {
        this.codVeiculo = codVeiculo;
        this.codPneusAplicadosVeiculo = codPneusAplicadosVeiculo;
    }

    @NotNull
    public Long getCodVeiculo() {
        return codVeiculo;
    }

    @NotNull
    public List<Long> getCodPneusAplicadosVeiculo() {
        return codPneusAplicadosVeiculo;
    }
}