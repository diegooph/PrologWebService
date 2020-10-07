package br.com.zalf.prolog.webservice.frota.veiculo.evolucaoKm._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.List;
/**
 * Created on 05/10/2020.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Data
public final class VeiculoEvolucaoKmResponse {
    @NotNull
    private final Long kmAtual;
    @NotNull
    private final List<VeiculoEvolucaoKm> veiculoEvolucaoKms;
}