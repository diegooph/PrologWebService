package br.com.zalf.prolog.webservice.integracao.avacorpavilan.checklist;

import java.util.List;

public class FarolDia {
    protected String data;
    protected List<VeiculoFarol> veiculosFarol;

    public FarolDia() {

    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public List<VeiculoFarol> getVeiculosFarol() {
        return veiculosFarol;
    }

    public void setVeiculosFarol(List<VeiculoFarol> veiculosFarol) {
        this.veiculosFarol = veiculosFarol;
    }
}