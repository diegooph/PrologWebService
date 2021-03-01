package br.com.zalf.prolog.webservice.integracao.webfinatto._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Data
public class PneuWebFinatto {
    @NotNull
    private final String codPneu;
    @NotNull
    private final String codigoCliente;
    @NotNull
    private final String codEmpresaPneu;
    @NotNull
    private final String codFilialPneu;
    @NotNull
    private final Integer vidaAtualPneu;
    @NotNull
    private final Integer vidaTotalPneu;
    @Nullable
    private final String posicaoAplicado;
    @NotNull
    private final Double pressaoRecomendadaPneuEmPsi;
    @NotNull
    private final Double pressaoAtualPneuEmPsi;
    @NotNull
    private final Double sulcoExternoPneuEmMilimetros;
    @NotNull
    private final Double sulcoCentralExternoPneuEmMilimetros;
    @NotNull
    private final Double sulcoCentralInternoPneuEmMilimetros;
    @NotNull
    private final Double sulcoInternoPneuEmMilimetros;
    @Nullable
    private final String dotPneu;
    @NotNull
    private final Long codEstruturaPneu;
    @NotNull
    private final Double alturaEstruturaPneu;
    @NotNull
    private final Double larguraEstruturaPneu;
    @NotNull
    private final Double aroEstruturaPneu;
    @NotNull
    private final String nomeMarcaPneu;
    @NotNull
    private final String codMarcaPneu;
    @NotNull
    private final String nomeModeloPneu;
    @NotNull
    private final String codModeloPneu;
    @NotNull
    private final Long alturaSulcosModeloPneuEmMilimetros;
    @NotNull
    private final Integer qtdSulcosModeloPneu;
    @Nullable
    private final String nomeMarcaBanda;
    @Nullable
    private final String codMarcaBanda;
    @Nullable
    private final String nomeModeloBanda;
    @Nullable
    private final String codModeloBanda;
    @Nullable
    private final Long alturaSulcosModeloBandaEmMilimetros;
    @Nullable
    private final Integer qtdSulcosModeloBanda;
    private final boolean isPneuEstepe;

    public boolean isRecapado() {
        return vidaAtualPneu > 1;
    }

    @NotNull
    public String getPosicaoAplicado() {
        if (posicaoAplicado == null) {
            throw new IllegalStateException("A posição do pneu não pode ser nula para esse cenário.");
        }
        return posicaoAplicado;
    }
}
