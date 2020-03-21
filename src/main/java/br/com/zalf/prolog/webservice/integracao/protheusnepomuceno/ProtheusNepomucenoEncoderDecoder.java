package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno;

import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;

/**
 * Created on 2020-03-21
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
final class ProtheusNepomucenoEncoderDecoder {
    private ProtheusNepomucenoEncoderDecoder() {
        throw new IllegalStateException(
                ProtheusNepomucenoEncoderDecoder.class.getSimpleName() + "cannot be instantiated!");
    }

    @NotNull
    static Long encode(@NotNull final String codigo) {
        if (codigo.length() > 8) {
            return new BigInteger(codigo.getBytes()).longValueExact();
        } else {
            throw new GenericException("O código do pneus excede a quantidade de 8 caracteres:" +
                    "Código: " + codigo + "\n" +
                    "Total de caracteres: " + codigo.length());
        }
    }

    @NotNull
    static String decode(@NotNull final Long codigo) {
        return new String(BigInteger.valueOf(codigo).toByteArray());
    }
}