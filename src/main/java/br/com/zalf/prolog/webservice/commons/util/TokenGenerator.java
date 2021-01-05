package br.com.zalf.prolog.webservice.commons.util;

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