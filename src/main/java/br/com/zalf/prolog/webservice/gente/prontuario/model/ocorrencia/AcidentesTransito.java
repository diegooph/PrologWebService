package br.com.zalf.prolog.webservice.gente.prontuario.model.ocorrencia;

/**
 * Created by Zart on 03/07/2017.
 */
public class AcidentesTransito {

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
