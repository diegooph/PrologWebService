package br.com.zalf.prolog.webservice.frota.socorrorota._model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

/**
 * Created on 2019-12-06
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class SocorroRotaAbertura extends SocorroRotaAcao {
    @NotNull
    private final Long codVeiculoProblema;
    private final long kmVeiculoAbertura;
    @NotNull
    private final Long codProblemaSocorroRota;
    @Nullable
    private final String descricaoProblema;
    @Nullable
    private final String urlFoto1Abertura;
    @Nullable
    private final String urlFoto2Abertura;
    @Nullable
    private final String urlFoto3Abertura;
    @Nullable
    private final String pontoReferencia;



    public SocorroRotaAbertura(@NotNull final Long codUnidadeAbertura,
                               @NotNull final Long codVeiculoProblema,
                               final long kmVeiculoAbertura,
                               @NotNull final Long codProblemaSocorroRota,
                               @Nullable final String descricaoProblema,
                               @Nullable final String urlFoto1Abertura,
                               @Nullable final String urlFoto2Abertura,
                               @Nullable final String urlFoto3Abertura,
                               @Nullable final String pontoReferencia,
                               @NotNull final Long codColaborador,
                               @NotNull final LocalDateTime dataHora,
                               @NotNull final LocalizacaoSocorroRota localizacao,
                               final int versaoAppAtual,
                               @Nullable final String deviceId,
                               @Nullable final String deviceImei,
                               final int androidApiVersion,
                               final long deviceUptimeMillis,
                               @Nullable final String marcaDevice,
                               @Nullable final String modeloDevice) {
        super(  codUnidadeAbertura,
                StatusSocorroRota.ABERTO,
                codColaborador,
                dataHora,
                localizacao,
                versaoAppAtual,
                deviceId,
                deviceImei,
                androidApiVersion,
                deviceUptimeMillis,
                marcaDevice,
                modeloDevice);
        this.codVeiculoProblema = codVeiculoProblema;
        this.kmVeiculoAbertura = kmVeiculoAbertura;
        this.codProblemaSocorroRota = codProblemaSocorroRota;
        this.descricaoProblema = descricaoProblema;
        this.urlFoto1Abertura = urlFoto1Abertura;
        this.urlFoto2Abertura = urlFoto2Abertura;
        this.urlFoto3Abertura = urlFoto3Abertura;
        this.pontoReferencia = pontoReferencia;
    }

    @NotNull
    public Long getCodVeiculoProblema() {
        return codVeiculoProblema;
    }

    public long getKmVeiculoAbertura() {
        return kmVeiculoAbertura;
    }

    @NotNull
    public Long getCodProblemaSocorroRota() {
        return codProblemaSocorroRota;
    }

    @Nullable
    public String getDescricaoProblema() {
        return descricaoProblema;
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

    @Nullable
    public String getPontoReferencia() {
        return pontoReferencia;
    }
}
