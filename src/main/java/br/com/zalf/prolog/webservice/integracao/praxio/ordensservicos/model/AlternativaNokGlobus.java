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
     * Código único de identificação da alternativa selecionada pelo colaborador.
     */
    @NotNull
    private final Long codAlternativaNok;
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
     * Indica se a alternativa respondida é tipo outros (significa que o usuário descreveu o problema).
     * Caso <code>FALSE</code> indica que a alternativa é pré-definida para a pergunta.
     */
    private final boolean alternativaTipoOutros;
    /**
     * Prioridade de resolução desta alternativa. A prioridade pode ser {@link PrioridadeAlternativaGlobus#BAIXA},
     * {@link PrioridadeAlternativaGlobus#ALTA} ou {@link PrioridadeAlternativaGlobus#CRITICA}.
     */
    @NotNull
    private final PrioridadeAlternativaGlobus prioridadeAlternativaNok;

    public AlternativaNokGlobus(@NotNull final Long codAlternativaNok,
                                @NotNull final Long codContextoAlternativaNok,
                                @NotNull final String descricaoAlternativaNok,
                                final boolean alternativaTipoOutros,
                                @NotNull final PrioridadeAlternativaGlobus prioridadeAlternativaNok) {
        this.codAlternativaNok = codAlternativaNok;
        this.codContextoAlternativaNok = codContextoAlternativaNok;
        this.descricaoAlternativaNok = descricaoAlternativaNok;
        this.alternativaTipoOutros = alternativaTipoOutros;
        this.prioridadeAlternativaNok = prioridadeAlternativaNok;
    }

    @NotNull
    public static AlternativaNokGlobus getDummy() {
        return new AlternativaNokGlobus(
                10L,
                1010L,
                "Farol quebrado",
                false,
                PrioridadeAlternativaGlobus.CRITICA);
    }

    @NotNull
    public Long getCodAlternativaNok() {
        return codAlternativaNok;
    }

    @NotNull
    public Long getCodContextoAlternativaNok() {
        return codContextoAlternativaNok;
    }

    @NotNull
    public String getDescricaoAlternativaNok() {
        return descricaoAlternativaNok;
    }

    public boolean isAlternativaTipoOutros() {
        return alternativaTipoOutros;
    }

    @NotNull
    public PrioridadeAlternativaGlobus getPrioridadeAlternativaNok() {
        return prioridadeAlternativaNok;
    }
}
