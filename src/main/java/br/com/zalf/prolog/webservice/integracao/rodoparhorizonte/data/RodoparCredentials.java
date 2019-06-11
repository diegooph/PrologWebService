package br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.data;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 11/06/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class RodoparCredentials {
    @NotNull
    private final String username;
    @NotNull
    private final String password;
    @NotNull
    private final String grantType;

    public RodoparCredentials(@NotNull final String username,
                              @NotNull final String password,
                              @NotNull final String grantType) {
        this.username = username;
        this.password = password;
        this.grantType = grantType;
    }

    @NotNull
    public String getUsername() {
        return username;
    }

    @NotNull
    public String getPassword() {
        return password;
    }

    @NotNull
    public String getGrantType() {
        return grantType;
    }
}
