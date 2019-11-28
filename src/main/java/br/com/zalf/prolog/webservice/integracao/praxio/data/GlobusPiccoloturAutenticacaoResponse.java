package br.com.zalf.prolog.webservice.integracao.praxio.data;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 11/26/19
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class GlobusPiccoloturAutenticacaoResponse {
    @NotNull
    private static final String BEARER = "Bearer ";

    private final boolean sucesso;
    @NotNull
    private final String data;

    public GlobusPiccoloturAutenticacaoResponse(final boolean sucesso, @NotNull final String data) {
        this.sucesso = sucesso;
        this.data = data;
    }

    @NotNull
    public String getFormattedBearerToken() {
        return BEARER.concat(this.data);
    }

    public boolean isSucesso() {
        return sucesso;
    }

    @NotNull
    public String getData() {
        return data;
    }
}
