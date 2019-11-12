package br.com.zalf.prolog.webservice.integracao.praxio;

import br.com.zalf.prolog.webservice.commons.util.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 11/12/19
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class GlobusPiccoloturUtils {

    private GlobusPiccoloturUtils() {
        throw new IllegalStateException(GlobusPiccoloturUtils.class.getSimpleName() + " cannot be instantiated");
    }

    @NotNull
    public static String addHifenPlaca(@NotNull final String placa) {
        return placa.subSequence(0, 3) + "-" + placa.subSequence(3, placa.length());
    }

    @NotNull
    static String formatNumeroFogo(@NotNull final String codigoCliente) {
        return StringUtils.containsLetters(codigoCliente)
                ? codigoCliente
                : String.format("%07d", Integer.parseInt(codigoCliente));
    }
}
