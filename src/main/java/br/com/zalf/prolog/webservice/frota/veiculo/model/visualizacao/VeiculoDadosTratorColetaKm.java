package br.com.zalf.prolog.webservice.frota.veiculo.model.visualizacao;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 2021-05-31
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Data(staticConstructor = "of")
public final class VeiculoDadosTratorColetaKm {
    @NotNull
    private final Long codVeiculoTrator;
    @NotNull
    private final String placaTrator;
    @Nullable
    private final String identificadorFrotaTrator;
    @NotNull
    private final Long kmAtualTrator;
}