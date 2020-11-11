package br.com.zalf.prolog.webservice.frota.veiculo.model.listagem;

import br.com.zalf.prolog.webservice.commons.gson.Exclude;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 2020-11-10
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Data
public final class VeiculoAcopladoListagem {
    @Exclude
    @NotNull
    private final Long codProcessoAcoplamento;
    @NotNull
    private final Long codVeiculo;
    @NotNull
    private final String placaVeiculo;
    @Nullable
    private final String identificadorFrota;
    private final boolean motorizado;
    private final int posicaoAcoplado;
}
