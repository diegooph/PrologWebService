package br.com.zalf.prolog.webservice.integracao.avacorpavilan.checklist;

import java.util.List;

public class VeiculoFarol {
    protected String placa;
    protected boolean realizouCheckSaida;
    protected boolean realizouCheckRetorno;
    protected List<String> itensCriticosAbertos;

    public VeiculoFarol() {

    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public boolean isRealizouCheckSaida() {
        return realizouCheckSaida;
    }

    public void setRealizouCheckSaida(boolean realizouCheckSaida) {
        this.realizouCheckSaida = realizouCheckSaida;
    }

    public boolean isRealizouCheckRetorno() {
        return realizouCheckRetorno;
    }

    public void setRealizouCheckRetorno(boolean realizouCheckRetorno) {
        this.realizouCheckRetorno = realizouCheckRetorno;
    }

    public List<String> getItensCriticosAbertos() {
        return itensCriticosAbertos;
    }

    public void setItensCriticosAbertos(List<String> itensCriticosAbertos) {
        this.itensCriticosAbertos = itensCriticosAbertos;
    }
}