package br.com.zalf.prolog.webservice.integracao.integrador._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Data
public final class UnidadeDePara {
    @NotNull
    private final Long codUnidadeProlog;
    @NotNull
    private final String nomeUnidadeProlog;
    @NotNull
    private final Long codRegionalProlog;
    @NotNull
    private final String nomeRegionalProlog;
    @Nullable
    private final String codAuxiliarUnidade;
}
