package br.com.zalf.prolog.webservice.autenticacao._model.token;

import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.security.SecureRandom;

public final class TokenGenerator {
    @NotNull
    private final SecureRandom random = new SecureRandom();

    @NotNull
    public String getNextToken() {
        return new BigInteger(130, random).toString(32);
    }
}