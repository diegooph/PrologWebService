package br.com.zalf.prolog.webservice.util;

import java.math.BigInteger;
import java.security.SecureRandom;
/**
 * Gera o token
 */
public final class SessionIdentifierGenerator {
    private SecureRandom random = new SecureRandom();

    public String nextSessionId() {
        return new BigInteger(130, random).toString(32);
    }
}