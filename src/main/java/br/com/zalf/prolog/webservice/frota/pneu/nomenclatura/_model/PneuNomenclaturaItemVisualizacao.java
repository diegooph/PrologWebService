package br.com.zalf.prolog.webservice.frota.pneu.nomenclatura._model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 30/08/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class PneuNomenclaturaItemVisualizacao {
    @NotNull
    private final String nomenclatura;
    @Nullable
    private final String codAuxiliar;
    private final int posicaoProlog;

    public PneuNomenclaturaItemVisualizacao(@NotNull final String nomenclatura,
                                            @Nullable final String codAuxiliar,
                                            final int posicaoProlog) {
        this.nomenclatura = nomenclatura;
        this.codAuxiliar = codAuxiliar;
        this.posicaoProlog = posicaoProlog;
    }

    @NotNull
    public String getNomenclatura() {
        return nomenclatura;
    }

    @Nullable
    public String getCodAuxiliar() {
        return codAuxiliar;
    }

    public int getPosicaoProlog() {
        return posicaoProlog;
    }

    @NotNull
    public static PneuNomenclaturaItemVisualizacao createDummy(int i) {
        return new PneuNomenclaturaItemVisualizacao("POSICAO" + i, "DE", i + 11);
    }
}
