package br.com.zalf.prolog.webservice.interceptors.auth.authenticator;

import br.com.zalf.prolog.webservice.interceptors.auth.ColaboradorAutenticado;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public abstract class PrologAuthenticator {

    @NotNull
    public abstract Optional<ColaboradorAutenticado> validate(@NotNull final String token,
                                                              @Nullable final Secured secured);
}