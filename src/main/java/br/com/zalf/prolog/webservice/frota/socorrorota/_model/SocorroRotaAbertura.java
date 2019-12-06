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
    private final Long codUnidadeAbertura;
    @NotNull
    private final Long codVeiculoProblema;
    @Nullable
    private final String descricaoProblema;
    @Nullable
    private final String urlFoto1;
    @Nullable
    private final String urlFoto2;
    @Nullable
    private final String urlFoto3;
    @Nullable
    private final String pontoReferencia;



    public SocorroRotaAbertura(@NotNull final Long codUnidadeAbertura,
                               @NotNull final Long codVeiculoProblema,
                               @Nullable final String descricaoProblema,
                               @Nullable final String urlFoto1,
                               @Nullable final String urlFoto2,
                               @Nullable final String urlFoto3,
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
        super(StatusSocorroRota.ABERTO,
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
        this.codUnidadeAbertura = codUnidadeAbertura;
        this.codVeiculoProblema = codVeiculoProblema;
        this.descricaoProblema = descricaoProblema;
        this.urlFoto1 = urlFoto1;
        this.urlFoto2 = urlFoto2;
        this.urlFoto3 = urlFoto3;
        this.pontoReferencia = pontoReferencia;
    }

    @NotNull
    public Long getCodUnidadeAbertura() {
        return codUnidadeAbertura;
    }

    @NotNull
    public Long getCodVeiculoProblema() {
        return codVeiculoProblema;
    }

    @Nullable
    public String getDescricaoProblema() {
        return descricaoProblema;
    }

    @Nullable
    public String getUrlFoto1() {
        return urlFoto1;
    }

    @Nullable
    public String getUrlFoto2() {
        return urlFoto2;
    }

    @Nullable
    public String getUrlFoto3() {
        return urlFoto3;
    }

    @Nullable
    public String getPontoReferencia() {
        return pontoReferencia;
    }
}
