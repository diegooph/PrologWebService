package br.com.zalf.prolog.webservice.integracao.praxio.utils;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-07-21
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class UnidadePraxioValidator {

    private UnidadePraxioValidator() {
        throw new IllegalStateException(UnidadePraxioValidator.class.getSimpleName() + " cannot be instantiated!");
    }

    public static boolean isUnidadeBloqueada(@NotNull final Long codUnidade) {
        return UnidadesBloqueadasIntegracaoPneusPraxioLoader.getUnidadesBloqueadas().contains(codUnidade);
    }
}
