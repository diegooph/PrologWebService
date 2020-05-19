package br.com.zalf.prolog.webservice.frota.pneu.nomenclatura._model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 2019-09-12
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class PneuNomenclaturaItemCadastro {
    @NotNull
    private final String nomenclatura;
    @Nullable
    private final String codAuxiliar;
    private final int posicaoProLog;

    public PneuNomenclaturaItemCadastro(@NotNull final String nomenclatura,
                                        @Nullable final String codAuxiliar,
                                        final int posicaoProLog) {
        this.nomenclatura = nomenclatura;
        this.codAuxiliar = codAuxiliar;
        this.posicaoProLog = posicaoProLog;
    }

    @NotNull
    public String getNomenclatura() {
        return nomenclatura;
    }

    @Nullable
    public String getCodAuxiliar() {
        return codAuxiliar;
    }

    public int getPosicaoProLog() {
        return posicaoProLog;
    }

    @NotNull
    public static PneuNomenclaturaItemCadastro createDummy(int i) {
        return new PneuNomenclaturaItemCadastro("POSICAO" + i, "DE", i + 11);
    }
}