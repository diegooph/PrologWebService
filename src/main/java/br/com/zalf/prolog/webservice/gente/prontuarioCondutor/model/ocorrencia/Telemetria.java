package br.com.zalf.prolog.webservice.gente.prontuarioCondutor.model.ocorrencia;

/**
 * Created by Zart on 03/07/2017.
 */
public class Telemetria extends Ocorrencia {

    private int excessoVelocidade1;
    private int excessoVelocidade2;
    private int excessoVelocidade3;
    private int forcaG;
    private int frenagemBrusca;
    private int powerOn;

    public Telemetria() {
    }

    public int getExcessoVelocidade1() {
        return excessoVelocidade1;
    }

    public void setExcessoVelocidade1(int excessoVelocidade1) {
        this.excessoVelocidade1 = excessoVelocidade1;
    }

    public int getExcessoVelocidade2() {
        return excessoVelocidade2;
    }

    public void setExcessoVelocidade2(int excessoVelocidade2) {
        this.excessoVelocidade2 = excessoVelocidade2;
    }

    public int getExcessoVelocidade3() {
        return excessoVelocidade3;
    }

    public void setExcessoVelocidade3(int excessoVelocidade3) {
        this.excessoVelocidade3 = excessoVelocidade3;
    }

    public int getForcaG() {
        return forcaG;
    }

    public void setForcaG(int forcaG) {
        this.forcaG = forcaG;
    }

    public int getFrenagemBrusca() {
        return frenagemBrusca;
    }

    public void setFrenagemBrusca(int frenagemBrusca) {
        this.frenagemBrusca = frenagemBrusca;
    }

    public int getPowerOn() {
        return powerOn;
    }

    public void setPowerOn(int powerOn) {
        this.powerOn = powerOn;
    }
}
