package br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 09/03/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class IntervaloAgrupadoAjuste {
    private IntervaloAjuste intervaloInicial;
    private IntervaloAjuste intervaloFinal;

    public IntervaloAgrupadoAjuste() {
    }

    @NotNull
    public static IntervaloAgrupadoAjuste createDummy() {
        final IntervaloAgrupadoAjuste intervaloAjuste = new IntervaloAgrupadoAjuste();
        intervaloAjuste.setIntervaloInicial(IntervaloAjuste.createDummyInicio());
        intervaloAjuste.setIntervaloFinal(IntervaloAjuste.createDummyFim());
        return intervaloAjuste;
    }

    public IntervaloAjuste getIntervaloInicial() {
        return intervaloInicial;
    }

    public void setIntervaloInicial(final IntervaloAjuste intervaloInicial) {
        this.intervaloInicial = intervaloInicial;
    }

    public IntervaloAjuste getIntervaloFinal() {
        return intervaloFinal;
    }

    public void setIntervaloFinal(final IntervaloAjuste intervaloFinal) {
        this.intervaloFinal = intervaloFinal;
    }

    @Override
    public String toString() {
        return "IntervaloAgrupadoAjuste{" +
                "intervaloInicial=" + intervaloInicial +
                ", intervaloFinal=" + intervaloFinal +
                '}';
    }
}
