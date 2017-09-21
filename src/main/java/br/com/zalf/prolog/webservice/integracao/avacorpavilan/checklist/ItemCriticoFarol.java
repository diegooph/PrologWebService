package br.com.zalf.prolog.webservice.integracao.avacorpavilan.checklist;

public class ItemCriticoFarol {
    protected String nomeItem;
    protected String dataHoraApontamento;

    public ItemCriticoFarol(String nomeItem) {
        this.nomeItem = nomeItem;
    }

    public String getNomeItem() {
        return nomeItem;
    }

    public void setNomeItem(String nomeItem) {
        this.nomeItem = nomeItem;
    }

    public String getDataHoraApontamento() {
        return dataHoraApontamento;
    }

    public void setDataHoraApontamento(String dataHoraApontamento) {
        this.dataHoraApontamento = dataHoraApontamento;
    }
}