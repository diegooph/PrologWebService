package br.com.zalf.prolog.webservice.frota.pneu.afericao._model;

import br.com.zalf.prolog.webservice.commons.gson.Exclude;
import br.com.zalf.prolog.webservice.commons.gson.RuntimeTypeAdapterFactory;
import br.com.zalf.prolog.webservice.frota.pneu._model.Restricao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model.FormaColetaDadosAfericaoEnum;
import org.jetbrains.annotations.NotNull;

/**
 * Created by jean on 08/04/16.
 */
public abstract class NovaAfericao {

    @Exclude
    @NotNull
    private final TipoProcessoColetaAfericao tipo;
    private Restricao restricao;
    private double variacaoAceitaSulcoMenorMilimetros;
    private double variacaoAceitaSulcoMaiorMilimetros;
    private boolean bloqueiaValoresMenores;
    private boolean bloqueiaValoresMaiores;
    private FormaColetaDadosAfericaoEnum formaColetaDadosSulco;
    private FormaColetaDadosAfericaoEnum formaColetaDadosPressao;
    private FormaColetaDadosAfericaoEnum formaColetaDadosSulcoPressao;

    public NovaAfericao(@NotNull final TipoProcessoColetaAfericao tipo) {
        this.tipo = tipo;
    }

    @NotNull
    public static RuntimeTypeAdapterFactory<NovaAfericao> provideTypeAdapterFactory() {
        return RuntimeTypeAdapterFactory
                .of(NovaAfericao.class, "tipo")
                .registerSubtype(NovaAfericaoPlaca.class, TipoProcessoColetaAfericao.PLACA.asString())
                .registerSubtype(NovaAfericaoAvulsa.class, TipoProcessoColetaAfericao.PNEU_AVULSO.asString());
    }

    public Restricao getRestricao() {
        return restricao;
    }

    public void setRestricao(final Restricao restricao) {
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

    public void setBloqueiaValoresMenores(final boolean bloqueiaValoresMenores) {
        this.bloqueiaValoresMenores = bloqueiaValoresMenores;
    }

    public boolean isBloqueiaValoresMaiores() {
        return bloqueiaValoresMaiores;
    }

    public void setBloqueiaValoresMaiores(final boolean bloqueiaValoresMaiores) {
        this.bloqueiaValoresMaiores = bloqueiaValoresMaiores;
    }

    public FormaColetaDadosAfericaoEnum getFormaColetaDadosSulco() {
        return formaColetaDadosSulco;
    }

    public void setFormaColetaDadosSulco(final FormaColetaDadosAfericaoEnum formaColetaDadosSulco) {
        this.formaColetaDadosSulco = formaColetaDadosSulco;
    }

    public FormaColetaDadosAfericaoEnum getFormaColetaDadosPressao() {
        return formaColetaDadosPressao;
    }

    public void setFormaColetaDadosPressao(final FormaColetaDadosAfericaoEnum formaColetaDadosPressao) {
        this.formaColetaDadosPressao = formaColetaDadosPressao;
    }

    public FormaColetaDadosAfericaoEnum getFormaColetaDadosSulcoPressao() {
        return formaColetaDadosSulcoPressao;
    }

    public void setFormaColetaDadosSulcoPressao(final FormaColetaDadosAfericaoEnum formaColetaDadosSulcoPressao) {
        this.formaColetaDadosSulcoPressao = formaColetaDadosSulcoPressao;
    }

}