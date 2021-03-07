package br.com.zalf.prolog.webservice.integracao.webfinatto.utils;

import org.jetbrains.annotations.NotNull;

public class SistemaWebFinattoConstants {
    @NotNull
    public static final String SEPARADOR_EMPRESA_FILIAL = ":";
    @NotNull
    public static final String SEPARADOR_FILIAIS_REQUEST = ",";
    public static final int COD_EMPRESA_INDEX = 0;
    public static final int COD_FILIAL_INDEX = 1;

    private SistemaWebFinattoConstants() {
        throw new IllegalStateException(SistemaWebFinattoConstants.class.getSimpleName() + " cannot be instantiated!");
    }
}
