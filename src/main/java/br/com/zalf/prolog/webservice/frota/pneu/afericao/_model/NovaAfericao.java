package br.com.zalf.prolog.webservice.frota.pneu.afericao._model;

import br.com.zalf.prolog.webservice.commons.gson.Exclude;
import br.com.zalf.prolog.webservice.commons.gson.RuntimeTypeAdapterFactory;
import br.com.zalf.prolog.webservice.frota.pneu._model.Restricao;
import org.jetbrains.annotations.NotNull;

/**
 * Created by jean on 08/04/16.
 */
public abstract class NovaAfericao {
    private Restricao restricao;
    private double variacaoAceitaSulcoMenorMilimetros;
    private double variacaoAceitaSulcoMaiorMilimetros;
    private boolean bloqueiaValoresMenores;
    private boolean bloqueiaValoresMaiores;

    @Exclude
    @NotNull
    private final TipoProcessoColetaAfericao tipo;

    public NovaAfericao(@NotNull final TipoProcessoColetaAfericao tipo) {
        this.tipo = tipo;
    }

    public Restricao getRestricao() {
        return restricao;
    }

    public void setRestricao(Restricao restricao) {
        this.restricao = restricao;
    }

    public double getVariacaoAceitaSulcoMenorMilimetros() {
        return variacaoAceitaSulcoMenorMilimetros;
    }

    public void setVariacaoAceitaSulcoMenorMilimetros(final double variacaoAceitaSulcoMenorMilimetros) {
        this.variacaoAceitaSulcoMenorMilimetros = variacaoAceitaSulcoMenorMilimetros;
    }

    public double getVariacaoAceitaSulcoMaiorMilimetros() {
        return variacaoAceitaSulcoMaiorMilimetros;
    }

    public void setVariacaoAceitaSulcoMaiorMilimetros(final double variacaoAceitaSulcoMaiorMilimetros) {
        this.variacaoAceitaSulcoMaiorMilimetros = variacaoAceitaSulcoMaiorMilimetros;
    }

    public boolean isBloqueiaValoresMenores() {
        return bloqueiaValoresMenores;
    }

    public void setBloqueiaValoresMenores(boolean bloqueiaValoresMenores) {
        this.bloqueiaValoresMenores = bloqueiaValoresMenores;
    }

    public boolean isBloqueiaValoresMaiores() {
        return bloqueiaValoresMaiores;
    }

    public void setBloqueiaValoresMaiores(boolean bloqueiaValoresMaiores) {
        this.bloqueiaValoresMaiores = bloqueiaValoresMaiores;
    }

    @NotNull
    public static RuntimeTypeAdapterFactory<NovaAfericao> provideTypeAdapterFactory() {
        return RuntimeTypeAdapterFactory
                .of(NovaAfericao.class, "tipo")
                .registerSubtype(NovaAfericaoPlaca.class, TipoProcessoColetaAfericao.PLACA.asString())
                .registerSubtype(NovaAfericaoAvulsa.class, TipoProcessoColetaAfericao.PNEU_AVULSO.asString());
    }
}