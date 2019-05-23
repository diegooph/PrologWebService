package br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 02/05/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class AlternativaNok {
    private Long codAlternativaNok;
    private String descricaoAlternativaNok;
    private String prioridadeAlternativaNok;

    public AlternativaNok() {
    }

    @NotNull
    public static AlternativaNok getDummy() {
        final AlternativaNok alternativa = new AlternativaNok();
        alternativa.setCodAlternativaNok(1010L);
        alternativa.setDescricaoAlternativaNok("Farol quebrado");
        alternativa.setPrioridadeAlternativaNok("CRITICA");
        return alternativa;
    }

    public Long getCodAlternativaNok() {
        return codAlternativaNok;
    }

    public void setCodAlternativaNok(final Long codAlternativaNok) {
        this.codAlternativaNok = codAlternativaNok;
    }

    public String getDescricaoAlternativaNok() {
        return descricaoAlternativaNok;
    }

    public void setDescricaoAlternativaNok(final String descricaoAlternativaNok) {
        this.descricaoAlternativaNok = descricaoAlternativaNok;
    }

    public String getPrioridadeAlternativaNok() {
        return prioridadeAlternativaNok;
    }

    public void setPrioridadeAlternativaNok(final String prioridadeAlternativaNok) {
        this.prioridadeAlternativaNok = prioridadeAlternativaNok;
    }
}
