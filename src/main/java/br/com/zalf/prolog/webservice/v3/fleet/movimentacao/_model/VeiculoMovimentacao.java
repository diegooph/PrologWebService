package br.com.zalf.prolog.webservice.v3.fleet.movimentacao._model;

import br.com.zalf.prolog.webservice.v3.fleet.processeskm._model.KmCollectedVehicle;
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
    public KmCollectedVehicle toVeiculoKmColetado() {
        return KmCollectedVehicle.of(getCodVeiculo(), getKmColetado());
    }
}
