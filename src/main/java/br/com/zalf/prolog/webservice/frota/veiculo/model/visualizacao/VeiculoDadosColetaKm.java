package br.com.zalf.prolog.webservice.frota.veiculo.model.visualizacao;

import lombok.Builder;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 2020-11-11
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Data(staticConstructor = "of")
public class VeiculoDadosColetaKm {

    @NotNull
    private final Long codigo;
    @NotNull
    private final String placa;
    private final long kmAtual;
    @Nullable
    private final String identificadorFrota;
    private final boolean motorizado;
    private final boolean possuiHubodometro;
    private final boolean acoplado;
    private final boolean deveColetarKm;
    @NotNull
    private final VeiculoDadosTratorColetaKm dadosTrator;

    @Builder(setterPrefix = "with", builderClassName = "Builder")
    public static class VeiculoDadosTratorColetaKm {
        @Nullable
        private final Long codVeiculoTrator;
        @Nullable
        private final String placaTrator;
        @Nullable
        private final Long kmAtualTrator;
        @Nullable
        private final String identificadorFrotaTrator;

    }

}
