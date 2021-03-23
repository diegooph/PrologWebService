package br.com.zalf.prolog.webservice.integracao.webfinatto._model.movimentacao;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public class PneuMovimentavaoWebFinatto {
    @NotNull
    private final String codEmpresaPneu;
    @NotNull
    private final String codFilialPneu;
    @NotNull
    private final String codPneu;
    @NotNull
    private final String codigoCliente;
    @NotNull
    private final Integer vidaAtualPneu;
    @NotNull
    private final Double pressaoAtualPneuEmPsi;
    @NotNull
    private final Double sulcoInternoPneuEmMilimetros;
    @NotNull
    private final Double sulcoCentralInternoPneuEmMilimetros;
    @NotNull
    private final Double sulcoCentralExternoPneuEmMilimetros;
    @NotNull
    private final Double sulcoExternoPneuEmMilimetros;
}
