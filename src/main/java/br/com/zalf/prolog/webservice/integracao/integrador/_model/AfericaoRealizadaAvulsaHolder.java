package br.com.zalf.prolog.webservice.integracao.integrador._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Data
public class AfericaoRealizadaAvulsaHolder {
    @NotNull
    private final List<AfericaoRealizadaAvulsa> afericoesRealizadasAvulsas;
}
