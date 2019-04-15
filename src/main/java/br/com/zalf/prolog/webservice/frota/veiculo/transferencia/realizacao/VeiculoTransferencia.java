package br.com.zalf.prolog.webservice.frota.veiculo.transferencia.realizacao;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 12/04/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class VeiculoTransferencia {
    /**
     * Código único de identificação do veículo no banco de dados.
     */
    @NotNull
    private final Long codVeiculo;
    /**
     * Lista contendo os códigos dos pneus que estão aplicados na placa no momento da transferência.
     */
    @NotNull
    private final List<Long> codPneusAplicadosVeiculo;

    public VeiculoTransferencia(@NotNull final Long codVeiculo,
                                @NotNull final List<Long> codPneusAplicadosVeiculo) {
        this.codVeiculo = codVeiculo;
        this.codPneusAplicadosVeiculo = codPneusAplicadosVeiculo;
    }

    @NotNull
    public static VeiculoTransferencia createDummy() {
        final List<Long> codPneusAplicadosVeiculo = new ArrayList<>();
        codPneusAplicadosVeiculo.add(1223L);
        codPneusAplicadosVeiculo.add(9288L);
        codPneusAplicadosVeiculo.add(1586L);
        return new VeiculoTransferencia(223L, codPneusAplicadosVeiculo);
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