package br.com.zalf.prolog.webservice.frota.socorrorota._model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.time.LocalDateTime;

/**
 * Created on 2020-01-08
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public final class SocorroRotaFinalizacaoVisualizacao {
    @NotNull
    private final Long codColaboradorResponsavelFinalizacao;
    @NotNull
    private final String nomeResponsavelFinalizacaoSocorro;
    @NotNull
    private final String observacaoFinalizacao;
    @NotNull
    private final LocalDateTime dataHoraFinalizacaoSocorro;
    @NotNull
    private final LocalizacaoSocorroRota localizacaoFinalizacaoScorro;
    @Nullable
    private final String enderecoAutomaticoFinalizacaoSocorro;
    @Nullable
    private final String marcaAparelhoFinalizacaoSocorro;
    @Nullable
    private final String modeloAparelhoFinalizacaoSocorro;
    @Nullable
    private final String imeiAparelhoFinalizacaoSocorro;
    @Nullable
    private final String urlFoto1Finalizacao;
    @Nullable
    private final String urlFoto2Finalizacao;
    @Nullable
    private final String urlFoto3Finalizacao;
    @Nullable
    private final String tempoAtendimentoFinalizacao;


    public SocorroRotaFinalizacaoVisualizacao(@NotNull final Long codColaboradorResponsavelFinalizacao,
                                              @NotNull final String nomeResponsavelFinalizacaoSocorro,
                                              @NotNull final String observacaoFinalizacao,
                                              @NotNull final LocalDateTime dataHoraFinalizacaoSocorro,
                                              @NotNull final LocalizacaoSocorroRota localizacaoFinalizacaoScorro,
                                              @Nullable final String enderecoAutomaticoFinalizacaoSocorro,
                                              @Nullable final String marcaAparelhoFinalizacaoSocorro,
                                              @Nullable final String modeloAparelhoFinalizacaoSocorro,
                                              @Nullable final String imeiAparelhoFinalizacaoSocorro,
                                              @Nullable final String urlFoto1Finalizacao,
                                              @Nullable final String urlFoto2Finalizacao,
                                              @Nullable final String urlFoto3Finalizacao,
                                              @Nullable final String tempoAtendimentoFinalizacao) {
        this.codColaboradorResponsavelFinalizacao = codColaboradorResponsavelFinalizacao;
        this.nomeResponsavelFinalizacaoSocorro = nomeResponsavelFinalizacaoSocorro;
        this.observacaoFinalizacao = observacaoFinalizacao;
        this.dataHoraFinalizacaoSocorro = dataHoraFinalizacaoSocorro;
        this.localizacaoFinalizacaoScorro = localizacaoFinalizacaoScorro;
        this.enderecoAutomaticoFinalizacaoSocorro = enderecoAutomaticoFinalizacaoSocorro;
        this.marcaAparelhoFinalizacaoSocorro = marcaAparelhoFinalizacaoSocorro;
        this.modeloAparelhoFinalizacaoSocorro = modeloAparelhoFinalizacaoSocorro;
        this.imeiAparelhoFinalizacaoSocorro = imeiAparelhoFinalizacaoSocorro;
        this.urlFoto1Finalizacao = urlFoto1Finalizacao;
        this.urlFoto2Finalizacao = urlFoto2Finalizacao;
        this.urlFoto3Finalizacao = urlFoto3Finalizacao;
        this.tempoAtendimentoFinalizacao = tempoAtendimentoFinalizacao;
    }

    @NotNull
    public Long getCodColaboradorResponsavelFinalizacao() { return codColaboradorResponsavelFinalizacao; }

    @NotNull
    public String getNomeResponsavelFinalizacaoSocorro() {
        return nomeResponsavelFinalizacaoSocorro;
    }

    @NotNull
    public String getObservacaoFinalizacao() { return observacaoFinalizacao; }

    @NotNull
    public LocalDateTime getDataHoraFinalizacaoSocorro() {
        return dataHoraFinalizacaoSocorro;
    }

    @NotNull
    public LocalizacaoSocorroRota getLocalizacaoFinalizacaoScorro() {
        return localizacaoFinalizacaoScorro;
    }

    @Nullable
    public String getEnderecoAutomaticoFinalizacaoSocorro() {
        return enderecoAutomaticoFinalizacaoSocorro;
    }

    @NotNull
    public StatusSocorroRota getStatusReferencia() {
        return StatusSocorroRota.FINALIZADO;
    }

    @Nullable
    public String getUrlFoto1Finalizacao() { return urlFoto1Finalizacao; }

    @Nullable
    public String getUrlFoto2Finalizacao() { return urlFoto2Finalizacao; }

    @Nullable
    public String getUrlFoto3Finalizacao() { return urlFoto3Finalizacao; }

    @Nullable
    public String getTempoAtendimentoFinalizacao() {
        return tempoAtendimentoFinalizacao;
    }
}
