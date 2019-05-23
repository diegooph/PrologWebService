package br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Essa classe encapsula os atributos de uma Pergunta NOK marcada pelo colaborador na realização de um
 * {@link ChecklistItensNokGlobus checklist}.
 *
 * Created on 02/05/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class PerguntaNokGlobus {
    /**
     * Código único de identificação da pergunta marcada como NOK.
     */
    @NotNull
    private final Long codPerguntaNok;
    /**
     * Texto que descreve a pergunta.
     */
    @NotNull
    private final String descricaoPerguntaNok;
    /**
     * Lista de {@link AlternativaNokGlobus alternativas} marcadas como NOK pelo colaborador que realizou o checklist.
     */
    @NotNull
    private final List<AlternativaNokGlobus> alternativasNok;

    public PerguntaNokGlobus(@NotNull final Long codPerguntaNok,
                             @NotNull final String descricaoPerguntaNok,
                             @NotNull final List<AlternativaNokGlobus> alternativasNok) {
        this.codPerguntaNok = codPerguntaNok;
        this.descricaoPerguntaNok = descricaoPerguntaNok;
        this.alternativasNok = alternativasNok;
    }

    @NotNull
    public static PerguntaNokGlobus getDummy() {
        final List<AlternativaNokGlobus> alternativas = new ArrayList<>();
        alternativas.add(AlternativaNokGlobus.getDummy());
        return new PerguntaNokGlobus(501L, "Farol", alternativas);
    }

    @NotNull
    public Long getCodPerguntaNok() {
        return codPerguntaNok;
    }

    @NotNull
    public String getDescricaoPerguntaNok() {
        return descricaoPerguntaNok;
    }

    @NotNull
    public List<AlternativaNokGlobus> getAlternativasNok() {
        return alternativasNok;
    }
}
