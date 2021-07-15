package br.com.zalf.prolog.webservice.v3.frota.movimentacao._model;

import br.com.zalf.prolog.webservice.v3.frota.kmprocessos._model.VeiculoKmColetado;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 2021-03-30
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Value
public class VeiculoMovimentacao {
    @NotNull
    Long codVeiculo;
    @NotNull
    String placaVeiculo;
    @Nullable
    String identificadorFrota;
    @NotNull
    Long codDiagramaVeiculo;
    long kmColetado;

    @NotNull
    public VeiculoKmColetado toVeiculoKmColetado() {
        return VeiculoKmColetado.of(getCodVeiculo(), getKmColetado());
    }
}
