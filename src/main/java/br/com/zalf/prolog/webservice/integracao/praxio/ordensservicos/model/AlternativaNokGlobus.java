package br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.model;

import org.jetbrains.annotations.NotNull;

/**
 * Classe responsável por encapsular as informações das alternativas marcadas como NOK de uma
 * {@link PerguntaNokGlobus pergunta}.
 * <p>
 * Created on 02/05/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class AlternativaNokGlobus {
    /**
     * Código de contexto da alternativa. Este código é utilizado na integração como código único de identificação da
     * alternativa selecionada pelo colaborador.
     */
    @NotNull
    private final Long codContextoAlternativaNok;
    /**
     * Texto que descreve a alternativa.
     */
    @NotNull
    private final String descricaoAlternativaNok;
    /**
     * Prioridade de resolução desta alternativa. A prioridade pode ser {@link PrioridadeAlternativaGlobus#BAIXA},
     * {@link PrioridadeAlternativaGlobus#ALTA} ou {@link PrioridadeAlternativaGlobus#CRITICA}.
     */
    @NotNull
    private final PrioridadeAlternativaGlobus prioridadeAlternativaNok;

    public AlternativaNokGlobus(@NotNull final Long codContextoAlternativaNok,
                                @NotNull final String descricaoAlternativaNok,
                                @NotNull final PrioridadeAlternativaGlobus prioridadeAlternativaNok) {
        this.codContextoAlternativaNok = codContextoAlternativaNok;
        this.descricaoAlternativaNok = descricaoAlternativaNok;
        this.prioridadeAlternativaNok = prioridadeAlternativaNok;
    }

    @NotNull
    public static AlternativaNokGlobus getDummy() {
        return new AlternativaNokGlobus(
                1010L,
                "Farol quebrado",
                PrioridadeAlternativaGlobus.CRITICA);
    }

    @NotNull
    public Long getCodContextoAlternativaNok() {
        return codContextoAlternativaNok;
    }

    @NotNull
    public String getDescricaoAlternativaNok() {
        return descricaoAlternativaNok;
    }

    @NotNull
    public PrioridadeAlternativaGlobus getPrioridadeAlternativaNok() {
        return prioridadeAlternativaNok;
    }
}
