package br.com.zalf.prolog.webservice.gente.controleintervalo.model;

/**
 * Created on 09/03/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class IntervaloAgrupadoAjuste {

    private IntervaloAjuste intervaloIncial;
    private IntervaloAjuste intervaloFinal;

    public IntervaloAgrupadoAjuste() {
    }

    public IntervaloAjuste getIntervaloIncial() {
        return intervaloIncial;
    }

    public void setIntervaloIncial(final IntervaloAjuste intervaloIncial) {
        this.intervaloIncial = intervaloIncial;
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
                "intervaloIncial=" + intervaloIncial +
                ", intervaloFinal=" + intervaloFinal +
                '}';
    }
}
