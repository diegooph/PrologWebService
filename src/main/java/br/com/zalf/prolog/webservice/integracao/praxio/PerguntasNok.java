package br.com.zalf.prolog.webservice.integracao.praxio;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 02/05/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class PerguntasNok {
    private Long codPerguntaNok;
    private String descricaoPerguntaNok;
    private List<AlternativaNok> alternativasNok;

    public PerguntasNok() {
    }

    @NotNull
    public static PerguntasNok getDummy() {
        final PerguntasNok resposta = new PerguntasNok();
        resposta.setCodPerguntaNok(501L);
        resposta.setDescricaoPerguntaNok("Farol");
        final List<AlternativaNok> alternativas = new ArrayList<>();
        alternativas.add(AlternativaNok.getDummy());
        resposta.setAlternativasNok(alternativas);
        return resposta;
    }

    public Long getCodPerguntaNok() {
        return codPerguntaNok;
    }

    public void setCodPerguntaNok(final Long codPerguntaNok) {
        this.codPerguntaNok = codPerguntaNok;
    }

    public String getDescricaoPerguntaNok() {
        return descricaoPerguntaNok;
    }

    public void setDescricaoPerguntaNok(final String descricaoPerguntaNok) {
        this.descricaoPerguntaNok = descricaoPerguntaNok;
    }

    public List<AlternativaNok> getAlternativasNok() {
        return alternativasNok;
    }

    public void setAlternativasNok(final List<AlternativaNok> alternativasNok) {
        this.alternativasNok = alternativasNok;
    }
}
