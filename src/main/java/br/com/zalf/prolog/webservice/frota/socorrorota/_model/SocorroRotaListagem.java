package br.com.zalf.prolog.webservice.frota.socorrorota._model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

/**
 * Created on 2019-12-06
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class SocorroRotaListagem {
    @NotNull
    private final Long codSocorroRota;
    @NotNull
    private final String nomeUnidade;
    @NotNull
    private final String placaVeiculo;
    private final boolean isPlacaDeletada;
    @NotNull
    private final String nomeResponsavelAberturaSocorro;
    private final boolean isColaboradorDeletado;
    @Nullable
    private final String descricaoFornecidaAberturaSocorro;
    @NotNull
    private final String descricaoOpcaoProblemaAberturaSocorro;
    @NotNull
    private final LocalDateTime dataHoraAberturaSocorro;
    @Nullable
    private final String enderecoAutomaticoAberturaSocorro;
    @Nullable
    private final String urlFoto1Abertura;
    @Nullable
    private final String urlFoto2Abertura;
    @Nullable
    private final String urlFoto3Abertura;
    @NotNull
    private final StatusSocorroRota statusAtualSocorroRota;

    public SocorroRotaListagem(@NotNull final Long codSocorroRota,
                               @NotNull final String nomeUnidade,
                               @NotNull final String placaVeiculo,
                               final boolean isPlacaDeletada,
                               @NotNull final String nomeResponsavelAberturaSocorro,
                               final boolean isColaboradorDeletado,
                               @Nullable final String descricaoFornecidaAberturaSocorro,
                               @NotNull final String descricaoOpcaoProblemaAberturaSocorro,
                               @NotNull final LocalDateTime dataHoraAberturaSocorro,
                               @Nullable final String enderecoAutomaticoAberturaSocorro,
                               @Nullable final String urlFoto1Abertura,
                               @Nullable final String urlFoto2Abertura,
                               @Nullable final String urlFoto3Abertura,
                               @NotNull final StatusSocorroRota statusAtualSocorroRota) {
        this.codSocorroRota = codSocorroRota;
        this.nomeUnidade = nomeUnidade;
        this.placaVeiculo = placaVeiculo;
        this.isPlacaDeletada = isPlacaDeletada;
        this.nomeResponsavelAberturaSocorro = nomeResponsavelAberturaSocorro;
        this.isColaboradorDeletado = isColaboradorDeletado;
        this.descricaoFornecidaAberturaSocorro = descricaoFornecidaAberturaSocorro;
        this.descricaoOpcaoProblemaAberturaSocorro = descricaoOpcaoProblemaAberturaSocorro;
        this.dataHoraAberturaSocorro = dataHoraAberturaSocorro;
        this.enderecoAutomaticoAberturaSocorro = enderecoAutomaticoAberturaSocorro;
        this.urlFoto1Abertura = urlFoto1Abertura;
        this.urlFoto2Abertura = urlFoto2Abertura;
        this.urlFoto3Abertura = urlFoto3Abertura;
        this.statusAtualSocorroRota = statusAtualSocorroRota;
    }

    @NotNull
    public Long getCodSocorroRota() { return codSocorroRota; }

    @NotNull
    public String getNomeUnidade() { return nomeUnidade; }

    @NotNull
    public String getPlacaVeiculo() {
        return placaVeiculo;
    }

    public boolean isPlacaDeletada() { return isPlacaDeletada; }

    @NotNull
    public String getNomeResponsavelAberturaSocorro() {
        return nomeResponsavelAberturaSocorro;
    }

    public boolean isColaboradorDeletado() { return isColaboradorDeletado; }

    @Nullable
    public String getDescricaoFornecidaAberturaSocorro() { return descricaoFornecidaAberturaSocorro; }

    @NotNull
    public String getDescricaoOpcaoProblemaAberturaSocorro() {
        return descricaoOpcaoProblemaAberturaSocorro;
    }

    @NotNull
    public LocalDateTime getDataHoraAberturaSocorro() {
        return dataHoraAberturaSocorro;
    }

    @Nullable
    public String getEnderecoAutomaticoAberturaSocorro() {
        return enderecoAutomaticoAberturaSocorro;
    }

    @Nullable
    public String getUrlFoto1Abertura() {
        return urlFoto1Abertura;
    }

    @Nullable
    public String getUrlFoto2Abertura() {
        return urlFoto2Abertura;
    }

    @Nullable
    public String getUrlFoto3Abertura() {
        return urlFoto3Abertura;
    }

    @NotNull
    public StatusSocorroRota getStatusAtualSocorroRota() {
        return statusAtualSocorroRota;
    }
}
