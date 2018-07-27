package br.com.zalf.prolog.webservice.frota.pneu.afericao.model;

import br.com.zalf.prolog.webservice.commons.gson.Exclude;
import br.com.zalf.prolog.webservice.commons.gson.RuntimeTypeAdapterFactory;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Restricao;
import org.jetbrains.annotations.NotNull;

/**
 * Created by jean on 08/04/16.
 */
public abstract class NovaAfericao {
    private Restricao restricao;

    @Exclude
    @NotNull
    private final TipoNovaAfericao tipo;

    public NovaAfericao(@NotNull final TipoNovaAfericao tipo) {
        this.tipo = tipo;
    }

    @NotNull
    public static RuntimeTypeAdapterFactory<NovaAfericao> provideTypeAdapterFactory() {
        return RuntimeTypeAdapterFactory
                .of(NovaAfericao.class, "tipo")
                .registerSubtype(NovaAfericaoPlaca.class, TipoNovaAfericao.AFERICAO_PLACA.asString())
                .registerSubtype(NovaAfericaoAvulsa.class, TipoNovaAfericao.AFERICAO_AVULSA.asString());
    }

    public Restricao getRestricao() {
        return restricao;
    }

    public void setRestricao(Restricao restricao) {
        this.restricao = restricao;
    }
}