package br.com.zalf.prolog.webservice.v3.frota.kmprocessos.visitor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2021-03-25
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Getter
@AllArgsConstructor
public abstract class AlteracaoKmProcesso {
    @NotNull
    private final Long codEmpresa;
    @NotNull
    private final Long codProcesso;
    private final long novoKm;

    protected abstract void accept(@NotNull final AlteracaoKmProcessoVisitor visitor);
}
