package test.br.com.zalf.prolog.webservice.routines;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Created on 28/02/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class TokenIntegracaoGenerator {

    public static void main(String[] args) {
        final String tokenSizeValidator = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        final String tokenMarcacao = new TokenGenerator().nextSessionId();
        if (tokenSizeValidator.length() == tokenMarcacao.length()) {
            System.out.println(tokenMarcacao);
        }
    }

    private static final class TokenGenerator {
        private SecureRandom random = new SecureRandom();

        String nextSessionId() {
            return new BigInteger(255, random).toString(32);
        }
    }
}
