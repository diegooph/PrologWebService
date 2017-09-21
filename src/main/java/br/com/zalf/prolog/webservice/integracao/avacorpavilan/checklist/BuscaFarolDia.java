package br.com.zalf.prolog.webservice.integracao.avacorpavilan.checklist;

import java.util.List;

public class BuscaFarolDia {
    protected boolean sucesso;
    protected String mensagem;
    protected List<FarolDia> farol;

    public BuscaFarolDia() {

    }

    public boolean isSucesso() {
        return sucesso;
    }

    public void setSucesso(boolean sucesso) {
        this.sucesso = sucesso;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public List<FarolDia> getFarol() {
        return farol;
    }

    public void setFarol(List<FarolDia> farol) {
        this.farol = farol;
    }
}