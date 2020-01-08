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


    public SocorroRotaInvalidacaoVisualizacao(@NotNull final String nomeResponsavelInvalidacaoSocorro,
                                              @NotNull final LocalDateTime dataHoraInvalidacaoSocorro,
                                              @NotNull final LocalizacaoSocorroRota localizacaoInvalidacaoScorro,
                                              @Nullable final String enderecoAutomaticoInvalidacaoSocorro,
                                              @Nullable final String marcaAparelhoInvalidacaoSocorro,
                                              @Nullable final String modeloAparelhoInvalidacaoSocorro,
                                              @Nullable final String imeiAparelhoInvalidacaoSocorro) {
        this.nomeResponsavelInvalidacaoSocorro = nomeResponsavelInvalidacaoSocorro;
        this.dataHoraInvalidacaoSocorro = dataHoraInvalidacaoSocorro;
        this.localizacaoInvalidacaoScorro = localizacaoInvalidacaoScorro;
        this.enderecoAutomaticoInvalidacaoSocorro = enderecoAutomaticoInvalidacaoSocorro;
        this.marcaAparelhoInvalidacaoSocorro = marcaAparelhoInvalidacaoSocorro;
        this.modeloAparelhoInvalidacaoSocorro = modeloAparelhoInvalidacaoSocorro;
        this.imeiAparelhoInvalidacaoSocorro = imeiAparelhoInvalidacaoSocorro;
    }

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
}
