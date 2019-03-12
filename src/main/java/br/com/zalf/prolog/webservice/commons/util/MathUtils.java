package br.com.zalf.prolog.webservice.commons.util;

/**
 * Created on 12/03/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class MathUtils {

    private MathUtils() {
        throw new IllegalStateException(MathUtils.class.getSimpleName() + " cannot be instantiated!");
    }

    /**
     * This method is one (1) based. A call like that getNthDigit(123, 2) would return 2 and not 3.
     *
     * @param number a number.
     * @param n the nth digit to recovery.
     * @return a specific digit of a {@code int} value.
     */
    public static int getNthDigit(final int number, final int n) {
        return Character.digit(String.valueOf(number).charAt(n - 1), 10);
    }
}
