package br.com.zalf.prolog.webservice.frota.socorrorota._model;

import br.com.zalf.prolog.webservice.commons.util.PrologPlatform;
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
    private final String placaVeiculoProblema;
    @NotNull
    private final String nomeColaboradorAbertura;
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
                               @NotNull final String placaVeiculoProblema,
                               @NotNull final String nomeColaboradorAbertura,
                               @NotNull final Long codProblemaSocorroRota,
                               @Nullable final String descricaoProblema,
                               @Nullable final String urlFoto1Abertura,
                               @Nullable final String urlFoto2Abertura,
                               @Nullable final String urlFoto3Abertura,
                               @Nullable final String pontoReferencia,
                               @NotNull final Long codColaborador,
                               @NotNull final LocalDateTime dataHora,
                               @Nullable final String enderecoAutomatico,
                               @NotNull final LocalizacaoSocorroRota localizacao,
                               @Nullable final String deviceId,
                               @Nullable final String deviceImei,
                               final int androidApiVersion,
                               final long deviceUptimeMillis,
                               @Nullable final String marcaDevice,
                               @Nullable final String modeloDevice,
                               @NotNull final PrologPlatform plataformaOrigem,
                               @NotNull final String versaoPlataformaOrigem) {
        super(  codUnidadeAbertura,
                StatusSocorroRota.ABERTO,
                codColaborador,
                dataHora,
                localizacao,
                enderecoAutomatico,
                deviceId,
                deviceImei,
                androidApiVersion,
                deviceUptimeMillis,
                marcaDevice,
                modeloDevice,
                plataformaOrigem,
                versaoPlataformaOrigem);
        this.codVeiculoProblema = codVeiculoProblema;
        this.kmVeiculoAbertura = kmVeiculoAbertura;
        this.placaVeiculoProblema = placaVeiculoProblema;
        this.nomeColaboradorAbertura = nomeColaboradorAbertura;
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
    public String getPlacaVeiculoProblema() {
        return placaVeiculoProblema;
    }

    @NotNull
    public String getNomeColaboradorAbertura() {
        return nomeColaboradorAbertura;
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
