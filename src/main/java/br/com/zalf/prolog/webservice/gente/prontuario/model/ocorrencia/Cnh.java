package br.com.zalf.prolog.webservice.gente.prontuario.model.ocorrencia;

import java.util.Date;

/**
 * Created by Zart on 03/07/2017.
 */
public class Cnh {

    private int pontuacao;
    private Date vencimento;

    public Cnh() {
    }

    public int getPontuacao() {
        return pontuacao;
    }

    public void setPontuacao(int pontuacao) {
        this.pontuacao = pontuacao;
    }

    public Date getVencimento() {
        return vencimento;
    }

    public void setVencimento(Date vencimento) {
        this.vencimento = vencimento;
    }
}
