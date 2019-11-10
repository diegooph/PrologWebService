package br.com.zalf.prolog.webservice.frota.pneu.nomenclatura._model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 2019-09-12
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class PneuNomenclaturaItemCadastro {
    @NotNull
    private final String nomenclatura;
    private final int posicaoProLog;

    public PneuNomenclaturaItemCadastro(@NotNull final String nomenclatura,
                                            final int posicaoProLog) {
        this.nomenclatura = nomenclatura;
        this.posicaoProLog = posicaoProLog;
    }

    @NotNull
    public String getNomenclatura() {
        return nomenclatura;
    }

    public int getPosicaoProLog() {
        return posicaoProLog;
    }

    @NotNull
    public static PneuNomenclaturaItemCadastro createDummy(int i) {
        return new PneuNomenclaturaItemCadastro("POSICAO"+i, i+11);
    }
}