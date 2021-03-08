package br.com.zalf.prolog.webservice.integracao.integrador._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public final class UnidadeRestricao {
    @NotNull
    private final Long codUnidade;
    private final int periodoDiasAfericaoSulco;
    private final int periodoDiasAfericaoPressao;
}
