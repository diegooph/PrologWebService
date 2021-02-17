package br.com.zalf.prolog.webservice.integracao.webfinatto._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public class MedicaoAfericaoWebFinatto {
    @NotNull
    private final String codPneu;
    @NotNull
    private final String codigoCliente;
    @NotNull
    private final Integer vidaAtual;
    @NotNull
    private final Double pressaoAtualEmPsi;
    @NotNull
    private final Double sulcoExternoPneuEmMilimetros;
    @NotNull
    private final Double sulcoCentralExternoPneuEmMilimetros;
    @NotNull
    private final Double sulcoCentralInternoPneuEmMilimetros;
    @NotNull
    private final Double sulcoInternoPneuEmMilimetros;
}
