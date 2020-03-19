package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-03-19
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ProtheusNepomucenoConstants {
    @NotNull
    public static final String DEFAULT_EMPRESA_FILIAL_SEPARERTOR = ":";
    @NotNull
    static final Long DEFAULT_COD_PNEU = 1L;
    @NotNull
    static final Long DEFAULT_COD_MODELO_PNEU = 1L;
    @NotNull
    static final Long DEFAULT_COD_MODELO_BANDA = 1L;
    static final int COD_EMPRESA_INDEX = 0;
    static final int COD_UNIDADE_INDEX = 1;

    private ProtheusNepomucenoConstants() {
        throw new IllegalStateException(ProtheusNepomucenoConstants.class.getSimpleName() + " cannot be instantiated!");
    }
}
