package br.com.zalf.prolog.webservice.integracao.api.controlejornada._model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 02/09/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ApiCoordenadasMarcacao {
    @NotNull
    private final String latitudeMarcacao;
    @NotNull
    private final String longitudeMarcacao;

    public ApiCoordenadasMarcacao(@NotNull final String latitudeMarcacao, @NotNull final String longitudeMarcacao) {
        this.latitudeMarcacao = latitudeMarcacao;
        this.longitudeMarcacao = longitudeMarcacao;
    }

    @NotNull
    public String getLatitudeMarcacao() {
        return latitudeMarcacao;
    }

    @NotNull
    public String getLongitudeMarcacao() {
        return longitudeMarcacao;
    }
}
