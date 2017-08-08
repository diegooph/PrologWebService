package br.com.zalf.prolog.webservice.gente.prontuarioCondutor.model.ocorrencia;

import br.com.zalf.prolog.webservice.commons.util.DateUtils;
import com.google.common.base.Preconditions;

import java.time.LocalDate;
import java.util.Date;

/**
 * Created by Zart on 03/07/2017.
 */
public class Cnh {

    private int pontuacao;
    private Date vencimento;

    /**
     * {@code true} se a CNH já estiver vencida.
     */
    private boolean cnhVencida;

    public Cnh() {

    }

    public Cnh(int pontuacao, Date vencimento) {
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

    public Date getVencimento() {
        return vencimento;
    }

    public void setVencimento(Date vencimento) {
        Preconditions.checkNotNull(vencimento);
        this.vencimento = vencimento;
        calculaVencimento();
    }

    private void calculaVencimento() {
        cnhVencida = LocalDate.now().isAfter(DateUtils.toLocalDate(vencimento));
    }
}
