package br.com.zalf.prolog.webservice.commons.util;

import org.jetbrains.annotations.Nullable;

/**
 * Created on 06/02/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class NullIf {
    private NullIf() {
        throw new IllegalStateException(NullIf.class.getSimpleName() + " cannot be instantiated!");
    }

    @Nullable
    public static Integer equal(final int a, final int b) {
        return a == b ? null : a;
    }

    @Nullable
    public static Integer equalOrLess(final int a, final int b) {
        return a <= b ? null : a;
    }

    @Nullable
    public static Double equalOrLess(final double a, final double b) {
        return a <= b ? null : a;
    }

    @Nullable
    public static Double less(final double a, final double b) {
        return a < b ? null : a;
    }

    @Nullable
    public static Long equalOrLess(final long a, final long b) {
        return a <= b ? null : a;
    }
}