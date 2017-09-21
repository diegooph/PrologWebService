package br.com.zalf.prolog.webservice.integracao.avacorpavilan.checklist;

import java.util.List;

public class VeiculoFarol {
    protected String placa;
    protected boolean realizouCheckSaida;
    protected String dataHoraCheckSaida;
    protected boolean realizouCheckRetorno;
    protected String dataHoraCheckRetorno;
    protected List<ItemCriticoFarol> itensCriticosAbertos;

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

    public List<ItemCriticoFarol> getItensCriticosAbertos() {
        return itensCriticosAbertos;
    }

    public void setItensCriticosAbertos(List<ItemCriticoFarol> itensCriticosAbertos) {
        this.itensCriticosAbertos = itensCriticosAbertos;
    }

    public String getDataHoraCheckSaida() {
        return dataHoraCheckSaida;
    }

    public void setDataHoraCheckSaida(String dataHoraCheckSaida) {
        this.dataHoraCheckSaida = dataHoraCheckSaida;
    }

    public String getDataHoraCheckRetorno() {
        return dataHoraCheckRetorno;
    }

    public void setDataHoraCheckRetorno(String dataHoraCheckRetorno) {
        this.dataHoraCheckRetorno = dataHoraCheckRetorno;
    }
}