package br.com.zalf.prolog.webservice.integracao.newrouter;

import br.com.zalf.prolog.webservice.integracao.sistema.SistemaKey;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2021-04-24
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface SistemaIntegrado {
    default boolean matchesKey(@NotNull final SistemaKey sistemaKey) {
        return sistemaKey == getKey();
    }

    @NotNull
    SistemaKey getKey();
}
