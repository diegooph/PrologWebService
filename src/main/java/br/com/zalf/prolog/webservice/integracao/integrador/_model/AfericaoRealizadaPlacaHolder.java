package br.com.zalf.prolog.webservice.integracao.integrador._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@Data
public final class AfericaoRealizadaPlacaHolder {
    @NotNull
    final Map<String, AfericaoRealizadaPlaca> afericaoRealizadaPlaca;
}
