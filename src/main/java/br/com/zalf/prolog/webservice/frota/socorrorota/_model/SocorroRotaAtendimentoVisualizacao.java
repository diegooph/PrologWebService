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
    private final String nomeResponsavelAtendimentoSocorro;
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

    public SocorroRotaAtendimentoVisualizacao(@NotNull final String nomeResponsavelAtendimentoSocorro,
                                              @NotNull final LocalDateTime dataHoraAtendimentoSocorro,
                                              @NotNull final LocalizacaoSocorroRota localizacaoAtendimentoScorro,
                                              @Nullable final String enderecoAutomaticoAtendimentoSocorro,
                                              @Nullable final String marcaAparelhoAtendimentoSocorro,
                                              @Nullable final String modeloAparelhoAtendimentoSocorro,
                                              @Nullable final String imeiAparelhoAtendimentoSocorro) {
        this.nomeResponsavelAtendimentoSocorro = nomeResponsavelAtendimentoSocorro;
        this.dataHoraAtendimentoSocorro = dataHoraAtendimentoSocorro;
        this.localizacaoAtendimentoScorro = localizacaoAtendimentoScorro;
        this.enderecoAutomaticoAtendimentoSocorro = enderecoAutomaticoAtendimentoSocorro;
        this.marcaAparelhoAtendimentoSocorro = marcaAparelhoAtendimentoSocorro;
        this.modeloAparelhoAtendimentoSocorro = modeloAparelhoAtendimentoSocorro;
        this.imeiAparelhoAtendimentoSocorro = imeiAparelhoAtendimentoSocorro;
    }

    @NotNull
    public String getNomeResponsavelAtendimentoSocorro() {
        return nomeResponsavelAtendimentoSocorro;
    }

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
}
