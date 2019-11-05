package br.com.zalf.prolog.webservice.frota.pneu.nomenclatura._model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 30/08/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class PneuNomenclaturaItemVisualizacao {
    @NotNull
    private final String nomenclatura;
    private final int posicaoProlog;

    public PneuNomenclaturaItemVisualizacao(@NotNull final String nomenclatura,
                                            final int posicaoProlog) {
        this.nomenclatura = nomenclatura;
        this.posicaoProlog = posicaoProlog;
    }

    @NotNull
    public String getNomenclatura() {
        return nomenclatura;
    }

    public int getPosicaoProlog() {
        return posicaoProlog;
    }

    @NotNull
    public static PneuNomenclaturaItemVisualizacao createDummy(int i) {
        return new PneuNomenclaturaItemVisualizacao("POSICAO"+i, i+11);
    }
}
