package br.com.zalf.prolog.webservice.integracao.webfinatto.utils;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2021-03-01
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class SistemaWebFinattoEncoderDecoder {
    private static final int IDENTIFICADOR_UNIDADE = 1;
    private static final int NUMERO_CARACTERES_UNIDADE = 5;
    private static final int NUMERO_CARACTERES_VEICULO = 13;

    private SistemaWebFinattoEncoderDecoder() {
        throw new IllegalStateException(
                SistemaWebFinattoEncoderDecoder.class.getSimpleName() + "cannot be instantiated!");
    }

    @NotNull
    public static Long generateCodVeiculo(@NotNull final Long codUnidade, @NotNull final Long codVeiculo) {
        final String codigoVeiculoGerado = IDENTIFICADOR_UNIDADE
                + StringUtils.leftPad(String.valueOf(codUnidade), NUMERO_CARACTERES_UNIDADE, "0")
                + StringUtils.leftPad(String.valueOf(codVeiculo), NUMERO_CARACTERES_VEICULO, "0");
        return Long.valueOf(codigoVeiculoGerado);
    }

    @NotNull
    public static Long extraiCodUnidade(@NotNull final Long codVeiculo) {
        final String string = String.valueOf(codVeiculo);
        return Long.valueOf(string.substring(1, NUMERO_CARACTERES_UNIDADE + 1));
    }

    @NotNull
    public static Long extraiCodVeiculo(@NotNull final Long codVeiculo) {
        final String string = String.valueOf(codVeiculo);
        return Long.valueOf(string.substring(NUMERO_CARACTERES_UNIDADE + 1));
    }
}