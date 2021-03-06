package br.com.zalf.prolog.webservice.integracao.newimpl.sistemas;

import br.com.zalf.prolog.webservice.integracao.sistema.SistemaKey;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Created on 2021-04-24
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public final class SistemaFactory {
    @NotNull
    private final Set<SistemaIntegrado> sistemasIntegrados;

    @NotNull
    public SistemaIntegrado createSistema(@NotNull final SistemaKey sistemaKey) {
        return sistemasIntegrados
                .stream()
                .filter(sistemaIntegrado -> sistemaIntegrado.matchesKey(sistemaKey))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Nenhum sistema encontrando com a key: " + sistemaKey));
    }
}
