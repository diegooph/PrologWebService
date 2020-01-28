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
    private final String unidade;
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
    @NotNull
    private final StatusSocorroRota statusAtualSocorroRota;

    public SocorroRotaListagem(@NotNull final Long codSocorroRota,
                               @NotNull final String unidade,
                               @NotNull final String placaVeiculo,
                               final boolean isPlacaDeletada,
                               @NotNull final String nomeResponsavelAberturaSocorro,
                               final boolean isColaboradorDeletado,
                               @Nullable final String descricaoFornecidaAberturaSocorro,
                               @NotNull final String descricaoOpcaoProblemaAberturaSocorro,
                               @NotNull final LocalDateTime dataHoraAberturaSocorro,
                               @Nullable final String enderecoAutomaticoAberturaSocorro,
                               @NotNull final StatusSocorroRota statusAtualSocorroRota) {
        this.codSocorroRota = codSocorroRota;
        this.unidade = unidade;
        this.placaVeiculo = placaVeiculo;
        this.isPlacaDeletada = isPlacaDeletada;
        this.nomeResponsavelAberturaSocorro = nomeResponsavelAberturaSocorro;
        this.isColaboradorDeletado = isColaboradorDeletado;
        this.descricaoFornecidaAberturaSocorro = descricaoFornecidaAberturaSocorro;
        this.descricaoOpcaoProblemaAberturaSocorro = descricaoOpcaoProblemaAberturaSocorro;
        this.dataHoraAberturaSocorro = dataHoraAberturaSocorro;
        this.enderecoAutomaticoAberturaSocorro = enderecoAutomaticoAberturaSocorro;
        this.statusAtualSocorroRota = statusAtualSocorroRota;
    }

    @NotNull
    public Long getCodSocorroRota() { return codSocorroRota; }

    @NotNull
    public String getUnidade() { return unidade; }

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

    @NotNull
    public StatusSocorroRota getStatusAtualSocorroRota() {
        return statusAtualSocorroRota;
    }
}
