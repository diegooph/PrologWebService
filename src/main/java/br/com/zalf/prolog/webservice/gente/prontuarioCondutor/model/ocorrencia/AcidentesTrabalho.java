package br.com.zalf.prolog.webservice.gente.prontuarioCondutor.model.ocorrencia;

/**
 * Created by Zart on 03/07/2017.
 */
public final class AcidentesTrabalho extends Ocorrencia {

    private int fai;
    private int lti;
    private int mdi;
    private int mti;

    public AcidentesTrabalho() {
    }

    public int getFai() {
        return fai;
    }

    public void setFai(int fai) {
        this.fai = fai;
    }

    public int getLti() {
        return lti;
    }

    public void setLti(int lti) {
        this.lti = lti;
    }

    public int getMdi() {
        return mdi;
    }

    public void setMdi(int mdi) {
        this.mdi = mdi;
    }

    public int getMti() {
        return mti;
    }

    public void setMti(int mti) {
        this.mti = mti;
    }
}
