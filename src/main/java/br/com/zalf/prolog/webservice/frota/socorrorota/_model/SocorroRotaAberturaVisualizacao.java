package br.com.zalf.prolog.webservice.frota.socorrorota._model;

import java.time.LocalDateTime;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/**
 * Created on 2020-01-08
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public final class SocorroRotaAberturaVisualizacao {
    @NotNull
    private final String placaVeiculo;
    @NotNull
    private final Long codColaboradorResponsavelAbertura;
    @NotNull
    private final String nomeResponsavelAberturaSocorro;
    @NotNull
    private final String descricaoOpcaoProblemaAberturaSocorro;
    @NotNull
    private final LocalDateTime dataHoraAberturaSocorro;
    @NotNull
    private final LocalizacaoSocorroRota localizacaoAberturaScorro;
    @Nullable
    private final String enderecoAutomaticoAberturaSocorro;
    @Nullable
    private final String marcaAparelhoAberturaSocorro;
    @Nullable
    private final String modeloAparelhoAberturaSocorro;
    @Nullable
    private final String imeiAparelhoAberturaSocorro;

    public SocorroRotaAberturaVisualizacao(@NotNull final String placaVeiculo,
                                           @NotNull final Long codColaboradorResponsavelAbertura,
                                           @NotNull final String nomeResponsavelAberturaSocorro,
                                           @NotNull final String descricaoOpcaoProblemaAberturaSocorro,
                                           @NotNull final LocalDateTime dataHoraAberturaSocorro,
                                           @NotNull final LocalizacaoSocorroRota localizacaoAberturaScorro,
                                           @Nullable final String enderecoAutomaticoAberturaSocorro,
                                           @Nullable final String marcaAparelhoAberturaSocorro,
                                           @Nullable final String modeloAparelhoAberturaSocorro,
                                           @Nullable final String imeiAparelhoAberturaSocorro) {
        this.placaVeiculo = placaVeiculo;
        this.codColaboradorResponsavelAbertura = codColaboradorResponsavelAbertura;
        this.nomeResponsavelAberturaSocorro = nomeResponsavelAberturaSocorro;
        this.descricaoOpcaoProblemaAberturaSocorro = descricaoOpcaoProblemaAberturaSocorro;
        this.dataHoraAberturaSocorro = dataHoraAberturaSocorro;
        this.localizacaoAberturaScorro = localizacaoAberturaScorro;
        this.enderecoAutomaticoAberturaSocorro = enderecoAutomaticoAberturaSocorro;
        this.marcaAparelhoAberturaSocorro = marcaAparelhoAberturaSocorro;
        this.modeloAparelhoAberturaSocorro = modeloAparelhoAberturaSocorro;
        this.imeiAparelhoAberturaSocorro = imeiAparelhoAberturaSocorro;
    }

    @NotNull
    public String getPlacaVeiculo() {
        return placaVeiculo;
    }

    @NotNull
    public Long getCodColaboradorResponsavelAbertura() {
        return codColaboradorResponsavelAbertura;
    }

    @NotNull
    public String getNomeResponsavelAberturaSocorro() {
        return nomeResponsavelAberturaSocorro;
    }

    @NotNull
    public String getDescricaoOpcaoProblemaAberturaSocorro() {
        return descricaoOpcaoProblemaAberturaSocorro;
    }

    @NotNull
    public LocalDateTime getDataHoraAberturaSocorro() {
        return dataHoraAberturaSocorro;
    }

    @NotNull
    public LocalizacaoSocorroRota getLocalizacaoAberturaScorro() {
        return localizacaoAberturaScorro;
    }

    @Nullable
    public String getEnderecoAutomaticoAberturaSocorro() {
        return enderecoAutomaticoAberturaSocorro;
    }

    @NotNull
    public StatusSocorroRota getStatusReferencia() {
        return StatusSocorroRota.ABERTO;
    }
}
