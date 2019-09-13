package br.com.zalf.prolog.webservice.integracao.api.checklist;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 07/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ApiAlternativaModeloChecklist {
    @NotNull
    private final Long codUnidade;
    @NotNull
    private final String nomeUnidade;
    @NotNull
    private final Long codModeloChecklist;
    @NotNull
    private final String nomeModeloChecklist;
    private final boolean statusModeloChecklist;
    @NotNull
    private final Long codPergunta;
    @NotNull
    private final String descricaoPergunta;
    private final boolean singleChoice;
    private final boolean statusPergunta;
    @NotNull
    private final Long codAlternativa;
    @NotNull
    private final String descricaoAlternativa;
    private final boolean tipoOutros;
    @NotNull
    private final ApiPrioridadeAlternativa prioridadeAlternativa;
    private final boolean deveAbrirOrdemServico;
    private final boolean statusAlternativa;

    public ApiAlternativaModeloChecklist(@NotNull final Long codUnidade,
                                         @NotNull final String nomeUnidade,
                                         @NotNull final Long codModeloChecklist,
                                         @NotNull final String nomeModeloChecklist,
                                         final boolean statusModeloChecklist,
                                         @NotNull final Long codPergunta,
                                         @NotNull final String descricaoPergunta,
                                         final boolean singleChoice,
                                         final boolean statusPergunta,
                                         @NotNull final Long codAlternativa,
                                         @NotNull final String descricaoAlternativa,
                                         final boolean tipoOutros,
                                         @NotNull final ApiPrioridadeAlternativa prioridadeAlternativa,
                                         final boolean deveAbrirOrdemServico,
                                         final boolean statusAlternativa) {
        this.codUnidade = codUnidade;
        this.nomeUnidade = nomeUnidade;
        this.codModeloChecklist = codModeloChecklist;
        this.nomeModeloChecklist = nomeModeloChecklist;
        this.statusModeloChecklist = statusModeloChecklist;
        this.codPergunta = codPergunta;
        this.descricaoPergunta = descricaoPergunta;
        this.singleChoice = singleChoice;
        this.statusPergunta = statusPergunta;
        this.codAlternativa = codAlternativa;
        this.descricaoAlternativa = descricaoAlternativa;
        this.tipoOutros = tipoOutros;
        this.prioridadeAlternativa = prioridadeAlternativa;
        this.deveAbrirOrdemServico = deveAbrirOrdemServico;
        this.statusAlternativa = statusAlternativa;
    }

    @NotNull
    private static ApiAlternativaModeloChecklist getAlternativaModeloChecklistApiDummy() {
        return new ApiAlternativaModeloChecklist(
                5L,
                "Zalf Sistemas",
                424L,
                "Checklist de Distribuição",
                true,
                5196L,
                "Setas dianteiras",
                false,
                true,
                13709L,
                "Lâmpada não funciona",
                false,
                ApiPrioridadeAlternativa.CRITICA,
                true,
                false);
    }

    @NotNull
    public Long getCodUnidade() {
        return codUnidade;
    }

    @NotNull
    public String getNomeUnidade() {
        return nomeUnidade;
    }

    @NotNull
    public Long getCodModeloChecklist() {
        return codModeloChecklist;
    }

    @NotNull
    public String getNomeModeloChecklist() {
        return nomeModeloChecklist;
    }

    public boolean isStatusModeloChecklist() {
        return statusModeloChecklist;
    }

    @NotNull
    public Long getCodPergunta() {
        return codPergunta;
    }

    @NotNull
    public String getDescricaoPergunta() {
        return descricaoPergunta;
    }

    public boolean isSingleChoice() {
        return singleChoice;
    }

    public boolean isStatusPergunta() {
        return statusPergunta;
    }

    @NotNull
    public Long getCodAlternativa() {
        return codAlternativa;
    }

    @NotNull
    public String getDescricaoAlternativa() {
        return descricaoAlternativa;
    }

    public boolean isTipoOutros() {
        return tipoOutros;
    }

    @NotNull
    public ApiPrioridadeAlternativa getPrioridadeAlternativa() {
        return prioridadeAlternativa;
    }

    public boolean isDeveAbrirOrdemServico() {
        return deveAbrirOrdemServico;
    }

    public boolean isStatusAlternativa() {
        return statusAlternativa;
    }
}
