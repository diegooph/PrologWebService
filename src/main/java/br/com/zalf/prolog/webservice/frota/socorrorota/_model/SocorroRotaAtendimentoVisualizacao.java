package br.com.zalf.prolog.webservice.frota.socorrorota._model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.time.LocalDateTime;

/**
 * Created on 2020-01-08
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public final class SocorroRotaAtendimentoVisualizacao {
    @NotNull
    private final Long codColaboradorResponsavelAtendimento;
    @NotNull
    private final String nomeResponsavelAtendimentoSocorro;
    @Nullable
    private final String observacaoAtendimento;
    @NotNull
    private final LocalDateTime dataHoraAtendimentoSocorro;
    @NotNull
    private final LocalizacaoSocorroRota localizacaoAtendimentoScorro;
    @Nullable
    private final String enderecoAutomaticoAtendimentoSocorro;
    @Nullable
    private final String marcaAparelhoAtendimentoSocorro;
    @Nullable
    private final String modeloAparelhoAtendimentoSocorro;
    @Nullable
    private final String imeiAparelhoAtendimentoSocorro;
    @Nullable
    private final LocalDateTime dataHoraDeslocamentoInicio;
    @Nullable
    private final LocalizacaoSocorroRota localizacaoDescolamentoInicio;
    @Nullable
    private final LocalDateTime dataHoraDeslocamentoFim;
    @Nullable
    private final LocalizacaoSocorroRota localizacaoDescolamentoFim;

    public SocorroRotaAtendimentoVisualizacao(@NotNull final Long codColaboradorResponsavelAtendimento,
                                              @NotNull final String nomeResponsavelAtendimentoSocorro,
                                              @Nullable final String observacaoAtendimento,
                                              @NotNull final LocalDateTime dataHoraAtendimentoSocorro,
                                              @NotNull final LocalizacaoSocorroRota localizacaoAtendimentoScorro,
                                              @Nullable final String enderecoAutomaticoAtendimentoSocorro,
                                              @Nullable final String marcaAparelhoAtendimentoSocorro,
                                              @Nullable final String modeloAparelhoAtendimentoSocorro,
                                              @Nullable final String imeiAparelhoAtendimentoSocorro,
                                              @Nullable final LocalDateTime dataHoraDeslocamentoInicio,
                                              @Nullable final LocalizacaoSocorroRota localizacaoDescolamentoInicio,
                                              @Nullable final LocalDateTime dataHoraDeslocamentoFim,
                                              @Nullable final LocalizacaoSocorroRota localizacaoDescolamentoFim) {
        this.codColaboradorResponsavelAtendimento = codColaboradorResponsavelAtendimento;
        this.nomeResponsavelAtendimentoSocorro = nomeResponsavelAtendimentoSocorro;
        this.observacaoAtendimento = observacaoAtendimento;
        this.dataHoraAtendimentoSocorro = dataHoraAtendimentoSocorro;
        this.localizacaoAtendimentoScorro = localizacaoAtendimentoScorro;
        this.enderecoAutomaticoAtendimentoSocorro = enderecoAutomaticoAtendimentoSocorro;
        this.marcaAparelhoAtendimentoSocorro = marcaAparelhoAtendimentoSocorro;
        this.modeloAparelhoAtendimentoSocorro = modeloAparelhoAtendimentoSocorro;
        this.imeiAparelhoAtendimentoSocorro = imeiAparelhoAtendimentoSocorro;
        this.dataHoraDeslocamentoInicio = dataHoraDeslocamentoInicio;
        this.localizacaoDescolamentoInicio = localizacaoDescolamentoInicio;
        this.dataHoraDeslocamentoFim = dataHoraDeslocamentoFim;
        this.localizacaoDescolamentoFim = localizacaoDescolamentoFim;
    }

    @NotNull
    public Long getCodColaboradorResponsavelAtendimento() { return codColaboradorResponsavelAtendimento; }

    @NotNull
    public String getNomeResponsavelAtendimentoSocorro() {
        return nomeResponsavelAtendimentoSocorro;
    }

    @Nullable
    public String getObservacaoAtendimento() { return observacaoAtendimento; }

    @NotNull
    public LocalDateTime getDataHoraAtendimentoSocorro() {
        return dataHoraAtendimentoSocorro;
    }

    @NotNull
    public LocalizacaoSocorroRota getLocalizacaoAtendimentoScorro() {
        return localizacaoAtendimentoScorro;
    }

    @Nullable
    public String getEnderecoAutomaticoAtendimentoSocorro() {
        return enderecoAutomaticoAtendimentoSocorro;
    }

    @NotNull
    public StatusSocorroRota getStatusReferencia() {
        return StatusSocorroRota.EM_ATENDIMENTO;
    }

    @Nullable
    public LocalDateTime getDataHoraDeslocamentoInicio() { return dataHoraDeslocamentoInicio; }

    @Nullable
    public LocalizacaoSocorroRota getLocalizacaoDescolamentoInicio() { return localizacaoDescolamentoInicio; }

    @Nullable
    public LocalDateTime getDataHoraDeslocamentoFim() { return dataHoraDeslocamentoFim; }

    @Nullable
    public LocalizacaoSocorroRota getLocalizacaoDescolamentoFim() { return localizacaoDescolamentoFim; }
}
