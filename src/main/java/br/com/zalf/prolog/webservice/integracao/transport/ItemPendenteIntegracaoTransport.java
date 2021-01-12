package br.com.zalf.prolog.webservice.integracao.transport;

import br.com.zalf.prolog.webservice.commons.util.datetime.Now;
import br.com.zalf.prolog.webservice.frota.checklist.model.PrioridadeAlternativa;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.StatusItemOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.StatusOrdemServico;
import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

/**
 * Created on 03/01/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ItemPendenteIntegracaoTransport {
    @NotNull
    private final String placaVeiculo;
    @NotNull
    private final Long kmAberturaServico;
    @NotNull
    private final Long codOrdemServico;
    @NotNull
    private final Long codUnidadeOrdemServico;
    @NotNull
    private final StatusOrdemServico statusOrdemServico;
    @NotNull
    private final LocalDateTime dataHoraAberturaServico;
    @NotNull
    private final Long codItemOrdemServico;
    @NotNull
    private final Long codUnidadeItemOrdemServico;
    @NotNull
    private final LocalDateTime dataHoraPrimeiroApontamento;
    @NotNull
    private final StatusItemOrdemServico statusItemOrdemServico;
    @NotNull
    private final Integer prazoResolucaoItemHoras;
    @NotNull
    private final Integer qtdApontamentos;
    @NotNull
    private final Long codChecklistPrimeiroApontamento;
    @NotNull
    @SerializedName("codPergunta")
    private final Long codContextoPergunta;
    @NotNull
    private final String descricaoPergunta;
    @NotNull
    @SerializedName("codAlternativaPergunta")
    private final Long codContextoAlternativa;
    @NotNull
    private final String descricaoAlternativa;
    private final boolean isTipoOutros;
    @Nullable
    private final String descricaoTipoOutros;
    @NotNull
    private final PrioridadeAlternativa prioridadeAlternativa;

    ItemPendenteIntegracaoTransport(@NotNull final String placaVeiculo,
                                    @NotNull final Long kmAberturaServico,
                                    @NotNull final Long codOrdemServico,
                                    @NotNull final Long codUnidadeOrdemServico,
                                    @NotNull final LocalDateTime dataHoraAberturaServico,
                                    @NotNull final Long codItemOrdemServico,
                                    @NotNull final Long codUnidadeItemOrdemServico,
                                    @NotNull final LocalDateTime dataHoraPrimeiroApontamento,
                                    @NotNull final Integer prazoResolucaoItemHoras,
                                    @NotNull final Integer qtdApontamentos,
                                    @NotNull final Long codChecklistPrimeiroApontamento,
                                    @NotNull final Long codContextoPergunta,
                                    @NotNull final String descricaoPergunta,
                                    @NotNull final Long codContextoAlternativa,
                                    @NotNull final String descricaoAlternativa,
                                    @NotNull final Boolean isTipoOutros,
                                    @Nullable final String descricaoTipoOutros,
                                    @NotNull final PrioridadeAlternativa prioridadeAlternativa) {
        this.placaVeiculo = placaVeiculo;
        this.kmAberturaServico = kmAberturaServico;
        this.codOrdemServico = codOrdemServico;
        this.codUnidadeOrdemServico = codUnidadeOrdemServico;
        this.dataHoraAberturaServico = dataHoraAberturaServico;
        this.codItemOrdemServico = codItemOrdemServico;
        this.codUnidadeItemOrdemServico = codUnidadeItemOrdemServico;
        this.dataHoraPrimeiroApontamento = dataHoraPrimeiroApontamento;
        this.prazoResolucaoItemHoras = prazoResolucaoItemHoras;
        this.qtdApontamentos = qtdApontamentos;
        this.codChecklistPrimeiroApontamento = codChecklistPrimeiroApontamento;
        this.codContextoPergunta = codContextoPergunta;
        this.descricaoPergunta = descricaoPergunta;
        this.codContextoAlternativa = codContextoAlternativa;
        this.descricaoAlternativa = descricaoAlternativa;
        this.isTipoOutros = isTipoOutros;
        this.descricaoTipoOutros = descricaoTipoOutros;
        this.prioridadeAlternativa = prioridadeAlternativa;
        statusOrdemServico = StatusOrdemServico.ABERTA;
        statusItemOrdemServico = StatusItemOrdemServico.PENDENTE;
    }

    @NotNull
    static ItemPendenteIntegracaoTransport getDummy() {
        return new ItemPendenteIntegracaoTransport(
                "PRO0001",
                90051L,
                94L,
                5L,
                Now.getLocalDateTimeUtc(),
                106851L,
                5L,
                Now.getLocalDateTimeUtc(),
                1,
                1,
                80931L,
                1130L,
                "Cintos de segurança e sensor",
                294L,
                "Sensor com problema",
                false,
                null,
                PrioridadeAlternativa.CRITICA);
    }

    @NotNull
    static ItemPendenteIntegracaoTransport getDummyTipoOutros() {
        return new ItemPendenteIntegracaoTransport(
                "PRO0001",
                854966L,
                65L,
                5L,
                Now.getLocalDateTimeUtc(),
                26304L,
                5L,
                Now.getLocalDateTimeUtc(),
                720,
                1,
                80931L,
                1163L,
                "Tampa da buzina, painel, porta luvas",
                381L,
                "Outros",
                true,
                "Luz do painel estragada",
                PrioridadeAlternativa.BAIXA);
    }

    @NotNull
    public String getPlacaVeiculo() {
        return placaVeiculo;
    }

    @NotNull
    public Long getKmAberturaServico() {
        return kmAberturaServico;
    }

    @NotNull
    public Long getCodOrdemServico() {
        return codOrdemServico;
    }

    @NotNull
    public Long getCodUnidadeOrdemServico() {
        return codUnidadeOrdemServico;
    }

    @NotNull
    public StatusOrdemServico getStatusOrdemServico() {
        return statusOrdemServico;
    }

    @NotNull
    public LocalDateTime getDataHoraAberturaServico() {
        return dataHoraAberturaServico;
    }

    @NotNull
    public Long getCodItemOrdemServico() {
        return codItemOrdemServico;
    }

    @NotNull
    public Long getCodUnidadeItemOrdemServico() {
        return codUnidadeItemOrdemServico;
    }

    @NotNull
    public LocalDateTime getDataHoraPrimeiroApontamento() {
        return dataHoraPrimeiroApontamento;
    }

    @NotNull
    public StatusItemOrdemServico getStatusItemOrdemServico() {
        return statusItemOrdemServico;
    }

    @NotNull
    public Integer getPrazoResolucaoItemHoras() {
        return prazoResolucaoItemHoras;
    }

    @NotNull
    public Integer getQtdApontamentos() {
        return qtdApontamentos;
    }

    @NotNull
    public Long getCodChecklistPrimeiroApontamento() {
        return codChecklistPrimeiroApontamento;
    }

    @NotNull
    public Long getCodContextoPergunta() {
        return codContextoPergunta;
    }

    @NotNull
    public String getDescricaoPergunta() {
        return descricaoPergunta;
    }

    @NotNull
    public Long getCodContextoAlternativa() {
        return codContextoAlternativa;
    }

    @NotNull
    public String getDescricaoAlternativa() {
        return descricaoAlternativa;
    }

    public boolean getTipoOutros() {
        return isTipoOutros;
    }

    @Nullable
    public String getDescricaoTipoOutros() {
        return descricaoTipoOutros;
    }

    @NotNull
    public PrioridadeAlternativa getPrioridadeAlternativa() {
        return prioridadeAlternativa;
    }
}
