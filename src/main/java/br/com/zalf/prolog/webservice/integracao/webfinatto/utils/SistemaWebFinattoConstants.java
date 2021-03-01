package br.com.zalf.prolog.webservice.integracao.webfinatto.utils;

import org.jetbrains.annotations.NotNull;

public class SistemaWebFinattoConstants {
    @NotNull
    public static final String SEPARADOR_EMPRESA_FILIAL = ":";
    @NotNull
    public static final String SEPARADOR_FILIAIS_REQUEST = ",";

    private SistemaWebFinattoConstants() {
        throw new IllegalStateException(SistemaWebFinattoConstants.class.getSimpleName() + " cannot be instantiated!");
    }
}
