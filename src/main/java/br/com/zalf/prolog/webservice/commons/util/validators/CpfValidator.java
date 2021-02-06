package br.com.zalf.prolog.webservice.commons.util.validators;

import br.com.zalf.prolog.webservice.config.BuildConfig;
import org.jetbrains.annotations.NotNull;

public final class CpfValidator {
    private static final int[] PESOS_CPF_DV1 = {10, 9, 8, 7, 6, 5, 4, 3, 2};
    private static final int[] PESOS_CPF_DV2 = {11, 10, 9, 8, 7, 6, 5, 4, 3, 2};

    private CpfValidator() {
        throw new IllegalStateException(CpfValidator.class.getSimpleName() + " cannot be instantiated!");
    }

    public static boolean isCpfValid(@NotNull final String cpf) {
        if (!BuildConfig.DEBUG) {
            if (cpf.length() != 11 || isNumbersEquals(cpf)) {
                return false;
            }

            final int digitoVerificador1 = calcularDigito(cpf.substring(0, 9), PESOS_CPF_DV1);
            final int digitoVerificador2 = calcularDigito(cpf.substring(0, 9) + digitoVerificador1, PESOS_CPF_DV2);

            final String generatedCpf = cpf.substring(0, 9) + digitoVerificador1 + digitoVerificador2;
            return cpf.equals(generatedCpf);
        } else {
            return isCpfCompleto(cpf);
        }
    }

    private static int calcularDigito(@NotNull final String str, final int[] pesosCpf) {
        int digitoVerificador = 0;
        for (int i = 0; i < str.length(); i++) {
            final int posicaoCpf = Integer.parseInt(str.substring(i, i + 1));
            digitoVerificador += posicaoCpf * pesosCpf[i];
        }

        digitoVerificador = 11 - digitoVerificador % 11;
        return digitoVerificador > 9 ? 0 : digitoVerificador;
    }

    private static boolean isNumbersEquals(@NotNull final String cpf) {
        return cpf.equals("11111111111") || cpf.equals("22222222222") || cpf.equals("33333333333")
                || cpf.equals("44444444444") || cpf.equals("55555555555") || cpf.equals("66666666666")
                || cpf.equals("77777777777") || cpf.equals("88888888888") || cpf.equals("99999999999")
                || cpf.equals("00000000000");
    }

    private static boolean isCpfCompleto(@NotNull final String cpf) {
        return cpf.length() == 11;
    }
}