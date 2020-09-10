package br.com.zalf.prolog.webservice.frota.veiculo.model.edicao;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 2020-09-09
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Data
public final class VeiculoAntesEdicao {
    @NotNull
    private final String placaAntiga;
    @Nullable
    private final String identificadorFrotaAntigo;
    @NotNull
    private final Long codModeloAntigo;
    @NotNull
    private final Long codTipoAntigo;
    private final long kmAntigo;
    private final boolean statusAtivoAntigo;
}
