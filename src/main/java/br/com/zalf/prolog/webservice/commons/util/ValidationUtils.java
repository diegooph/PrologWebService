package br.com.zalf.prolog.webservice.commons.util;

import br.com.zalf.prolog.webservice.config.BuildConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ValidationUtils {

    /**
     * Array de valores inteiros decrescentes começando do 11 até 2. Estes valores são multiplicados
     * aos valores do cpf gerando os Dígitos Verificadores (DV)
     */
    private static final int[] PESOS_CPF_DV1 = {10, 9, 8, 7, 6, 5, 4, 3, 2};
    private static final int[] PESOS_CPF_DV2 = {11, 10, 9, 8, 7, 6, 5, 4, 3, 2};

    private ValidationUtils() {
    }

    /**
     * Método de validação de CPF em runtime. Esta técnica é utilizada pela Receita Federal e consiste
     * em multiplicar os 9 primeiros dígitos por pesos decrescentes ({@link #PESOS_CPF_DV1} e
     * {@link #PESOS_CPF_DV2}), cujo cálculo dos valores resultará nos dígitos verificadores
     * (últimos dois dígitos do cpf).
     *
     * @param cpf - String contendo cpf a ser verificado.
     * @return - Verdadeiro caso for um cpf válido, caso contrário, falso.
     */
    public static boolean isValidCpf(@NotNull final String cpf) {
        if (!BuildConfig.DEBUG) {
            if (cpf.length() != 11 || isNumbersEquals(cpf))
                return false;

            final Integer digitoVerificador1 = calcularDigito(cpf.substring(0, 9), PESOS_CPF_DV1);
            final Integer digitoVerificador2 = calcularDigito(cpf.substring(0, 9) + digitoVerificador1, PESOS_CPF_DV2);

            final String generatedCpf = cpf.substring(0, 9) + digitoVerificador1.toString() + digitoVerificador2.toString();
            return cpf.equals(generatedCpf);
        } else {
            // Mesmo sendo versão DEBUG, ainda precisamos verificar se possui 11 numeros
            // essa comparação retornará true se cpf conter 11 números.
            return cpf.length() == 11;
        }
    }

    /**
     * Método para verificar Pis
     */
    public static boolean validaPIS(@Nullable final String pisOrPasep) {
        if (pisOrPasep == null) return false;
        final String n = pisOrPasep.replaceAll("[^0-9]*", "");
        //boolean isPis = n.length() == PIS_DIGITS;
        //boolean isPasep = n.length() == PASEP_DIGITS;
        if (n.length() != 11) return false;
        int digit;      // A number digit
        int coeficient; // A coeficient
        int sum;        // The sum of (Digit * Coeficient)
        int foundDv;    // The found Dv (Chek Digit)
        int dv = Integer.parseInt(String.valueOf(n.charAt(n.length() - 1)));
        sum = 0;
        coeficient = 2;
        for (int i = n.length() - 2; i >= 0; i--) {
            digit = Integer.parseInt(String.valueOf(n.charAt(i)));
            sum += digit * coeficient;
            coeficient++;
            if (coeficient > 9) coeficient = 2;
        }
        foundDv = 11 - sum % 11;
        if (foundDv >= 10) foundDv = 0;
        return dv == foundDv;
    }

    /**
     * Método que calcula os Dígitos Verificadores. Seguindo padrão da Receita Federal.
     *
     * @param str      - String que consiste nos 9 primeiros dígitos do cpf.
     * @param pesosCpf - Pesos usados para calcular o Dígito Verificador.
     * @return - Valor Inteiro referente ao Dígito Verificador.
     */
    private static int calcularDigito(@NotNull final String str, final int[] pesosCpf) {
        //valorDV é o Valor do Digito Verificador
        int valorDV = 0;
        for (int i = 0; i < str.length(); i++) {
            int posicaoCpf = Integer.parseInt(str.substring(i, i + 1));
            valorDV += posicaoCpf * pesosCpf[i];
        }

        valorDV = 11 - valorDV % 11;
        return valorDV > 9 ? 0 : valorDV;
    }

    /**
     * Método que verifica se o cpf é composto por 11 números iguais.
     *
     * @param cpf - String referente ao cpf a ser analisado.
     * @return - Verdadeiro caso possuir todos os números iguais, caso contrário, falso.
     */
    private static boolean isNumbersEquals(String cpf) {
        return cpf.equals("11111111111") || cpf.equals("22222222222") || cpf.equals("33333333333")
                || cpf.equals("44444444444") || cpf.equals("55555555555") || cpf.equals("66666666666")
                || cpf.equals("77777777777") || cpf.equals("88888888888") || cpf.equals("99999999999")
                || cpf.equals("00000000000");
    }
}