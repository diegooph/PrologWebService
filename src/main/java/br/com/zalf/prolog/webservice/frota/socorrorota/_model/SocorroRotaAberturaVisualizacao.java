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
    private final long kmVeiculoColetadoAbertura;
    @NotNull
    private final String descricaoOpcaoProblemaAberturaSocorro;
    @Nullable
    private final String descricaoFornecidaAberturaSocorro;
    @Nullable
    private final String pontoReferenciaFornecidoAbertura;
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
    @Nullable
    private final String urlFoto1Abertura;
    @Nullable
    private final String urlFoto2Abertura;
    @Nullable
    private final String urlFoto3Abertura;

    public SocorroRotaAberturaVisualizacao(@NotNull final String placaVeiculo,
                                           @NotNull final Long codColaboradorResponsavelAbertura,
                                           @NotNull final String nomeResponsavelAberturaSocorro,
                                           final long kmVeiculoColetadoAbertura,
                                           @NotNull final String descricaoOpcaoProblemaAberturaSocorro,
                                           @Nullable final String descricaoFornecidaAberturaSocorro,
                                           @Nullable final String pontoReferenciaFornecidoAbertura,
                                           @NotNull final LocalDateTime dataHoraAberturaSocorro,
                                           @NotNull final LocalizacaoSocorroRota localizacaoAberturaScorro,
                                           @Nullable final String enderecoAutomaticoAberturaSocorro,
                                           @Nullable final String marcaAparelhoAberturaSocorro,
                                           @Nullable final String modeloAparelhoAberturaSocorro,
                                           @Nullable final String imeiAparelhoAberturaSocorro,
                                           @Nullable final String urlFoto1Abertura,
                                           @Nullable final String urlFoto2Abertura,
                                           @Nullable final String urlFoto3Abertura) {
        this.placaVeiculo = placaVeiculo;
        this.codColaboradorResponsavelAbertura = codColaboradorResponsavelAbertura;
        this.nomeResponsavelAberturaSocorro = nomeResponsavelAberturaSocorro;
        this.kmVeiculoColetadoAbertura = kmVeiculoColetadoAbertura;
        this.descricaoOpcaoProblemaAberturaSocorro = descricaoOpcaoProblemaAberturaSocorro;
        this.descricaoFornecidaAberturaSocorro = descricaoFornecidaAberturaSocorro;
        this.pontoReferenciaFornecidoAbertura = pontoReferenciaFornecidoAbertura;
        this.dataHoraAberturaSocorro = dataHoraAberturaSocorro;
        this.localizacaoAberturaScorro = localizacaoAberturaScorro;
        this.enderecoAutomaticoAberturaSocorro = enderecoAutomaticoAberturaSocorro;
        this.marcaAparelhoAberturaSocorro = marcaAparelhoAberturaSocorro;
        this.modeloAparelhoAberturaSocorro = modeloAparelhoAberturaSocorro;
        this.imeiAparelhoAberturaSocorro = imeiAparelhoAberturaSocorro;
        this.urlFoto1Abertura = urlFoto1Abertura;
        this.urlFoto2Abertura = urlFoto2Abertura;
        this.urlFoto3Abertura = urlFoto3Abertura;
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

    public long getKmVeiculoColetadoAbertura() { return kmVeiculoColetadoAbertura; }

    @NotNull
    public String getDescricaoOpcaoProblemaAberturaSocorro() {
        return descricaoOpcaoProblemaAberturaSocorro;
    }

    @Nullable
    public String getDescricaoFornecidaAberturaSocorro() { return descricaoFornecidaAberturaSocorro; }

    @Nullable
    public String getPontoReferenciaFornecidoAbertura() { return pontoReferenciaFornecidoAbertura; }

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

    @Nullable
    public String getUrlFoto1Abertura() { return urlFoto1Abertura; }

    @Nullable
    public String getUrlFoto2Abertura() { return urlFoto2Abertura; }

    @Nullable
    public String getUrlFoto3Abertura() { return urlFoto3Abertura; }
}
