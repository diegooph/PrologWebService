package br.com.zalf.prolog.webservice.commons.util.validators;

import org.jetbrains.annotations.Nullable;

/**
 * Created on 2021-01-12
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public final class PisPasepValidator {
    public PisPasepValidator() {
        throw new UnsupportedOperationException("An util class cannot be instantiated!");
    }

    public static boolean isPisPasepValid(@Nullable final String pisOrPasep) {
        if (pisOrPasep == null) {
            return false;
        }
        final String n = pisOrPasep.replaceAll("[^0-9]*", "");
        if (n.length() != 11) {
            return false;
        }
        int digit;
        int foundDv;
        int sum = 0;
        int coeficient = 2;
        final int dv = Integer.parseInt(String.valueOf(n.charAt(n.length() - 1)));
        for (int i = n.length() - 2; i >= 0; i--) {
            digit = Integer.parseInt(String.valueOf(n.charAt(i)));
            sum += digit * coeficient;
            coeficient++;
            if (coeficient > 9) {
                coeficient = 2;
            }
        }
        foundDv = 11 - sum % 11;
        if (foundDv >= 10) {
            foundDv = 0;
        }
        return dv == foundDv;
    }
}
