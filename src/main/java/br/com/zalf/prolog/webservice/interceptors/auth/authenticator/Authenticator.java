package br.com.zalf.prolog.webservice.interceptors.auth.authenticator;

import br.com.zalf.prolog.webservice.interceptors.auth.ColaboradorAutenticado;
import org.jetbrains.annotations.NotNull;

public interface Authenticator {
    @NotNull
    ColaboradorAutenticado validate(@NotNull final String value,
                                    @NotNull final int[] permissions,
                                    final boolean needsToHaveAllPermissions,
                                    final boolean considerOnlyActiveUsers);
}