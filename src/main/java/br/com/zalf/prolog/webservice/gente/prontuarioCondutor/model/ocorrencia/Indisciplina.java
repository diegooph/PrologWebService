package br.com.zalf.prolog.webservice.gente.prontuarioCondutor.model.ocorrencia;

/**
 * Created by Zart on 03/07/2017.
 */
public final class Indisciplina extends Ocorrencia {
    private int advertencias;
    private int suspensoes;

    public Indisciplina() {
    }

    public int getAdvertencias() {
        return advertencias;
    }

    public void setAdvertencias(int advertencias) {
        this.advertencias = advertencias;
    }

    public int getSuspensoes() {
        return suspensoes;
    }

    public void setSuspensoes(int suspensoes) {
        this.suspensoes = suspensoes;
    }
}
