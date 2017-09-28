package br.com.zalf.prolog.webservice.integracao.avacorpavilan.checklist;

import java.util.List;

public class FarolDia {
    protected String data;
    protected List<VeiculoChecklist> veiculosChecklist;

    public FarolDia() {

    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public List<VeiculoChecklist> getVeiculosChecklist() {
        return veiculosChecklist;
    }

    public void setVeiculosChecklist(List<VeiculoChecklist> veiculosChecklist) {
        this.veiculosChecklist = veiculosChecklist;
    }
}