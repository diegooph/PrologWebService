package br.com.zalf.prolog.webservice.integracao.avacorpavilan.checklist;

public class ItemCritico {
    protected String descricao;
    protected String data;

    public ItemCritico(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}