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
    @NotNull
    private final Boolean statusModeloChecklist;
    @NotNull
    private final Long codPergunta;
    @NotNull
    private final String descricaoPergunta;
    @NotNull
    private final Boolean singleChoice;
    @NotNull
    private final Boolean statusPergunta;
    @NotNull
    private final Long codAlternativa;
    @NotNull
    private final String descricaoAlternativa;
    @NotNull
    private final Boolean tipoOutros;
    @NotNull
    private final ApiPrioridadeAlternativa prioridadeAlternativa;
    @NotNull
    private final Boolean deveAbrirOrdemServico;
    @NotNull
    private final Boolean statusAlternativa;

    public ApiAlternativaModeloChecklist(@NotNull final Long codUnidade,
                                         @NotNull final String nomeUnidade,
                                         @NotNull final Long codModeloChecklist,
                                         @NotNull final String nomeModeloChecklist,
                                         @NotNull final Boolean statusModeloChecklist,
                                         @NotNull final Long codPergunta,
                                         @NotNull final String descricaoPergunta,
                                         @NotNull final Boolean singleChoice,
                                         @NotNull final Boolean statusPergunta,
                                         @NotNull final Long codAlternativa,
                                         @NotNull final String descricaoAlternativa,
                                         @NotNull final Boolean tipoOutros,
                                         @NotNull final ApiPrioridadeAlternativa prioridadeAlternativa,
                                         @NotNull final Boolean deveAbrirOrdemServico,
                                         @NotNull final Boolean statusAlternativa) {
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

    @NotNull
    public Boolean getStatusModeloChecklist() {
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

    @NotNull
    public Boolean getSingleChoice() {
        return singleChoice;
    }

    @NotNull
    public Boolean getStatusPergunta() {
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

    @NotNull
    public Boolean getTipoOutros() {
        return tipoOutros;
    }

    @NotNull
    public ApiPrioridadeAlternativa getPrioridadeAlternativa() {
        return prioridadeAlternativa;
    }

    @NotNull
    public Boolean getDeveAbrirOrdemServico() {
        return deveAbrirOrdemServico;
    }

    @NotNull
    public Boolean getStatusAlternativa() {
        return statusAlternativa;
    }
}
