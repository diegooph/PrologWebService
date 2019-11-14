package br.com.zalf.prolog.webservice.gente.prontuarioCondutor.model.ocorrencia;

/**
 * Created by Zart on 03/07/2017.
 */
public final class Sav extends Ocorrencia {
    private int impericia;
    private int imprudencia;

    public Sav() {
    }

    public int getImpericia() {
        return impericia;
    }

    public void setImpericia(int impericia) {
        this.impericia = impericia;
    }

    public int getImprudencia() {
        return imprudencia;
    }

    public void setImprudencia(int imprudencia) {
        this.imprudencia = imprudencia;
    }
}
