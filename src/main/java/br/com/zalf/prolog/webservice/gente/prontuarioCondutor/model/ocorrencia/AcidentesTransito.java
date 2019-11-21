package br.com.zalf.prolog.webservice.gente.prontuarioCondutor.model.ocorrencia;

/**
 * Created by Zart on 03/07/2017.
 */
public final class AcidentesTransito extends Ocorrencia {

    private int capotamentos;
    private int colisoes;
    private int tombamentos;

    public AcidentesTransito() {
    }

    public int getCapotamentos() {
        return capotamentos;
    }

    public void setCapotamentos(int capotamentos) {
        this.capotamentos = capotamentos;
    }

    public int getColisoes() {
        return colisoes;
    }

    public void setColisoes(int colisoes) {
        this.colisoes = colisoes;
    }

    public int getTombamentos() {
        return tombamentos;
    }

    public void setTombamentos(int tombamentos) {
        this.tombamentos = tombamentos;
    }
}
