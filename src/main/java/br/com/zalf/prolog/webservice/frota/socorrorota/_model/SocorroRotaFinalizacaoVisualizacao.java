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
    private final String nomeResponsavelFinalizacaoSocorro;
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


    public SocorroRotaFinalizacaoVisualizacao(@NotNull final String nomeResponsavelFinalizacaoSocorro,
                                              @NotNull final LocalDateTime dataHoraFinalizacaoSocorro,
                                              @NotNull final LocalizacaoSocorroRota localizacaoFinalizacaoScorro,
                                              @Nullable final String enderecoAutomaticoFinalizacaoSocorro,
                                              @Nullable final String marcaAparelhoFinalizacaoSocorro,
                                              @Nullable final String modeloAparelhoFinalizacaoSocorro,
                                              @Nullable final String imeiAparelhoFinalizacaoSocorro) {
        this.nomeResponsavelFinalizacaoSocorro = nomeResponsavelFinalizacaoSocorro;
        this.dataHoraFinalizacaoSocorro = dataHoraFinalizacaoSocorro;
        this.localizacaoFinalizacaoScorro = localizacaoFinalizacaoScorro;
        this.enderecoAutomaticoFinalizacaoSocorro = enderecoAutomaticoFinalizacaoSocorro;
        this.marcaAparelhoFinalizacaoSocorro = marcaAparelhoFinalizacaoSocorro;
        this.modeloAparelhoFinalizacaoSocorro = modeloAparelhoFinalizacaoSocorro;
        this.imeiAparelhoFinalizacaoSocorro = imeiAparelhoFinalizacaoSocorro;
    }

    @NotNull
    public String getNomeResponsavelFinalizacaoSocorro() {
        return nomeResponsavelFinalizacaoSocorro;
    }

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
}
