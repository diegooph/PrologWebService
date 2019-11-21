package br.com.zalf.prolog.webservice.integracao.praxio;

import br.com.zalf.prolog.webservice.commons.util.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 11/12/19
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class GlobusPiccoloturUtils {
    private static final int PLACA_PADRAO_ANTIGO_LENGTH = 7;
    private GlobusPiccoloturUtils() {
        throw new IllegalStateException(GlobusPiccoloturUtils.class.getSimpleName() + " cannot be instantiated");
    }

    @NotNull
    public static String addHifenPlacaSePadraoAntigo(@NotNull final String placa) {
        if (placa.length() == PLACA_PADRAO_ANTIGO_LENGTH) {
            final String letras = (String) placa.subSequence(0, 3);
            final String numeros = (String) placa.subSequence(3, placa.length());
            if (StringUtils.isAlpha(letras) && StringUtils.isIntegerValuePositive(numeros)) {
                return letras + "-" + numeros;
            }
        }

        // Placa no novo padrão, não adicionamos o hífen.
        return placa;
    }
    @NotNull
    static String formatNumeroFogo(@NotNull final String codigoCliente) {
        return StringUtils.containsLetters(codigoCliente)
                ? codigoCliente
                : String.format("%07d", Integer.parseInt(codigoCliente));
    }
}