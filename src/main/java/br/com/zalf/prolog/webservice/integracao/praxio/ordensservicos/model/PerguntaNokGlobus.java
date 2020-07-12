package br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.model;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Essa classe encapsula os atributos de uma Pergunta NOK marcada pelo colaborador na realização de um
 * {@link ChecklistItensNokGlobus checklist}.
 * <p>
 * Created on 02/05/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class PerguntaNokGlobus {
    /**
     * Código único de identificação da pergunta selecionada pelo colaborador.
     */
    @NotNull
    private final Long codPerguntaNok;
    /**
     * Código de contexto da pergunta. Este código é utilizado na integração como código único de indentificação da
     * pergunta marcada como NOK.
     */
    @NotNull
    private final Long codContextoPerguntaNok;
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
                             @NotNull final Long codContextoPerguntaNok,
                             @NotNull final String descricaoPerguntaNok,
                             @NotNull final List<AlternativaNokGlobus> alternativasNok) {
        this.codPerguntaNok = codPerguntaNok;
        this.codContextoPerguntaNok = codContextoPerguntaNok;
        this.descricaoPerguntaNok = descricaoPerguntaNok;
        this.alternativasNok = alternativasNok;
    }

    @NotNull
    public static PerguntaNokGlobus getDummy() {
        final List<AlternativaNokGlobus> alternativas = new ArrayList<>();
        alternativas.add(AlternativaNokGlobus.getDummy());
        return new PerguntaNokGlobus(
                10L,
                101L,
                "Farol",
                alternativas);
    }

    @NotNull
    public Long getCodPerguntaNok() {
        return codPerguntaNok;
    }

    @NotNull
    public Long getCodContextoPerguntaNok() {
        return codContextoPerguntaNok;
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
