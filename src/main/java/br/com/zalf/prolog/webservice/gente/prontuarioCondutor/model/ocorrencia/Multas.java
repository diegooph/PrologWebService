package br.com.zalf.prolog.webservice.gente.prontuarioCondutor.model.ocorrencia;

/**
 * Created by Zart on 03/07/2017.
 */
public final class Multas extends Ocorrencia {
    private int leve;
    private int media;
    private int grave;
    private int gravissima;

    public Multas() {
    }

    public int getLeve() {
        return leve;
    }

    public void setLeve(int leve) {
        this.leve = leve;
    }

    public int getMedia() {
        return media;
    }

    public void setMedia(int media) {
        this.media = media;
    }

    public int getGrave() {
        return grave;
    }

    public void setGrave(int grave) {
        this.grave = grave;
    }

    public int getGravissima() {
        return gravissima;
    }

    public void setGravissima(int gravissima) {
        this.gravissima = gravissima;
    }
}
