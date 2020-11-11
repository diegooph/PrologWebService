package br.com.zalf.prolog.webservice.frota.veiculo.model.visualizacao;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 2020-11-11
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Data(staticConstructor = "of")
public class VeiculoEstadoAcoplamento {

    @NotNull
    private final Long codigo;
    @NotNull
    private final String placa;
    @NotNull
    private final Long km;
    @Nullable
    private final String identificadorFrota;
    private final boolean motorizado;
    private final boolean possuiHubodometro;
    private final boolean acoplado;
    private final boolean deveColetarKm;

}
