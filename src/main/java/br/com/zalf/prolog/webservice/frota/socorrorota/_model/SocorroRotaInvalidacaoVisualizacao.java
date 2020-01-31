package br.com.zalf.prolog.webservice.frota.socorrorota._model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.time.LocalDateTime;

/**
 * Created on 2020-01-08
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public final class SocorroRotaInvalidacaoVisualizacao {
    @NotNull
    private final Long codColaboradorResponsavelInvalidacao;
    @NotNull
    private final String nomeResponsavelInvalidacaoSocorro;
    @NotNull
    private final LocalDateTime dataHoraInvalidacaoSocorro;
    @NotNull
    private final LocalizacaoSocorroRota localizacaoInvalidacaoScorro;
    @Nullable
    private final String enderecoAutomaticoInvalidacaoSocorro;
    @Nullable
    private final String marcaAparelhoInvalidacaoSocorro;
    @Nullable
    private final String modeloAparelhoInvalidacaoSocorro;
    @Nullable
    private final String imeiAparelhoInvalidacaoSocorro;
    @Nullable
    private final String urlFoto1Invalidacao;
    @Nullable
    private final String urlFoto2Invalidacao;
    @Nullable
    private final String urlFoto3Invalidacao;


    public SocorroRotaInvalidacaoVisualizacao(@NotNull final Long codColaboradorResponsavelInvalidacao,
                                              @NotNull final String nomeResponsavelInvalidacaoSocorro,
                                              @NotNull final LocalDateTime dataHoraInvalidacaoSocorro,
                                              @NotNull final LocalizacaoSocorroRota localizacaoInvalidacaoScorro,
                                              @Nullable final String enderecoAutomaticoInvalidacaoSocorro,
                                              @Nullable final String marcaAparelhoInvalidacaoSocorro,
                                              @Nullable final String modeloAparelhoInvalidacaoSocorro,
                                              @Nullable final String imeiAparelhoInvalidacaoSocorro,
                                              @Nullable final String urlFoto1Invalidacao,
                                              @Nullable final String urlFoto2Invalidacao,
                                              @Nullable final String urlFoto3Invalidacao) {
        this.codColaboradorResponsavelInvalidacao = codColaboradorResponsavelInvalidacao;
        this.nomeResponsavelInvalidacaoSocorro = nomeResponsavelInvalidacaoSocorro;
        this.dataHoraInvalidacaoSocorro = dataHoraInvalidacaoSocorro;
        this.localizacaoInvalidacaoScorro = localizacaoInvalidacaoScorro;
        this.enderecoAutomaticoInvalidacaoSocorro = enderecoAutomaticoInvalidacaoSocorro;
        this.marcaAparelhoInvalidacaoSocorro = marcaAparelhoInvalidacaoSocorro;
        this.modeloAparelhoInvalidacaoSocorro = modeloAparelhoInvalidacaoSocorro;
        this.imeiAparelhoInvalidacaoSocorro = imeiAparelhoInvalidacaoSocorro;
        this.urlFoto1Invalidacao = urlFoto1Invalidacao;
        this.urlFoto2Invalidacao = urlFoto2Invalidacao;
        this.urlFoto3Invalidacao = urlFoto3Invalidacao;
    }

    @NotNull
    public Long getCodColaboradorResponsavelInvalidacao() { return codColaboradorResponsavelInvalidacao; }

    @NotNull
    public String getNomeResponsavelInvalidacaoSocorro() {
        return nomeResponsavelInvalidacaoSocorro;
    }

    @NotNull
    public LocalDateTime getDataHoraInvalidacaoSocorro() {
        return dataHoraInvalidacaoSocorro;
    }

    @NotNull
    public LocalizacaoSocorroRota getLocalizacaoInvalidacaoScorro() {
        return localizacaoInvalidacaoScorro;
    }

    @Nullable
    public String getEnderecoAutomaticoInvalidacaoSocorro() {
        return enderecoAutomaticoInvalidacaoSocorro;
    }

    @NotNull
    public StatusSocorroRota getStatusReferencia() {
        return StatusSocorroRota.INVALIDO;
    }

    @Nullable
    public String getUrlFoto1Invalidacao() { return urlFoto1Invalidacao; }

    @Nullable
    public String getUrlFoto2Invalidacao() { return urlFoto2Invalidacao; }

    @Nullable
    public String getUrlFoto3Invalidacao() { return urlFoto3Invalidacao; }
}
