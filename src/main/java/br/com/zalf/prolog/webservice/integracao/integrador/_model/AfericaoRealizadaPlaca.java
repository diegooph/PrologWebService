package br.com.zalf.prolog.webservice.integracao.integrador._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public final class AfericaoRealizadaPlaca {
    @NotNull
    private final String placaVeiculo;
    private final int diasUltimaAfericaoSulco;
    private final int diasUltimaAfericaoPressao;
}
