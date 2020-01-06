package br.com.zalf.prolog.webservice.gente.prontuarioCondutor.model;

import br.com.zalf.prolog.webservice.commons.util.date.Now;
import com.google.common.base.Preconditions;

import java.time.LocalDate;

/**
 * Created by Zart on 03/07/2017.
 */
public final class Cnh {
    private int pontuacao;
    private LocalDate vencimento;

    /**
     * {@code true} se a CNH já estiver vencida.
     */
    private boolean cnhVencida;

    public Cnh() {

    }

    public Cnh(int pontuacao, LocalDate vencimento) {
        Preconditions.checkNotNull(vencimento);

        this.pontuacao = pontuacao;
        this.vencimento = vencimento;

        calculaVencimento();
    }

    public int getPontuacao() {
        return pontuacao;
    }

    public void setPontuacao(int pontuacao) {
        this.pontuacao = pontuacao;
    }

    public LocalDate getVencimento() {
        return vencimento;
    }

    public void setVencimento(LocalDate vencimento) {
        Preconditions.checkNotNull(vencimento);
        this.vencimento = vencimento;
        calculaVencimento();
    }

    private void calculaVencimento() {
        cnhVencida = Now.localDateUtc().isAfter(vencimento);
    }
}
