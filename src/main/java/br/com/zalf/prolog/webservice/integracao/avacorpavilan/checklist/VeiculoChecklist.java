package br.com.zalf.prolog.webservice.integracao.avacorpavilan.checklist;

import java.util.List;

public class VeiculoChecklist {
    protected String placa;
    protected List<ItemCritico> itensCriticos;
    protected List<Avaliacao> avaliacoes;

    public VeiculoChecklist() {

    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public List<ItemCritico> getItensCriticos() {
        return itensCriticos;
    }

    public void setItensCriticos(List<ItemCritico> itensCriticos) {
        this.itensCriticos = itensCriticos;
    }

    public List<Avaliacao> getAvaliacoes() {
        return avaliacoes;
    }

    public void setAvaliacoes(List<Avaliacao> avaliacoes) {
        this.avaliacoes = avaliacoes;
    }
}