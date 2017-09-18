package br.com.zalf.prolog.webservice.interceptors.auth.authenticator;

import com.sun.istack.internal.NotNull;

import javax.ws.rs.NotAuthorizedException;

public interface Authenticator {
    void validate(@NotNull final String value,
                  @NotNull final int[] permissions,
                  final boolean needsToHaveAllPermissions,
                  final boolean considerOnlyActiveUsers) throws NotAuthorizedException;
}