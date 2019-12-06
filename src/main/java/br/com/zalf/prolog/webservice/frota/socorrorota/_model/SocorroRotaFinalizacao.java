package br.com.zalf.prolog.webservice.frota.socorrorota._model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

/**
 * Created on 2019-12-06
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class SocorroRotaFinalizacao extends SocorroRotaAcao {
    @NotNull
    private final Long codSocorroRota;
    @NotNull
    private final String observacaoFinalizacao;
    @Nullable
    private final String urlFoto1Finalizacao;
    @Nullable
    private final String urlFoto2Finalizacao;
    @Nullable
    private final String urlFoto3;

    public SocorroRotaFinalizacao(@NotNull final Long codSocorroRota,
                                  @NotNull final String observacaoFinalizacao,
                                  @Nullable final String urlFoto1Finalizacao,
                                  @Nullable final String urlFoto2Finalizacao,
                                  @Nullable final String urlFoto3,
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
        super(StatusSocorroRota.FINALIZADO,
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
        this.codSocorroRota = codSocorroRota;
        this.observacaoFinalizacao = observacaoFinalizacao;
        this.urlFoto1Finalizacao = urlFoto1Finalizacao;
        this.urlFoto2Finalizacao = urlFoto2Finalizacao;
        this.urlFoto3 = urlFoto3;
    }

    @NotNull
    public Long getCodSocorroRota() {
        return codSocorroRota;
    }

    @NotNull
    public String getObservacaoFinalizacao() {
        return observacaoFinalizacao;
    }

    @Nullable
    public String getUrlFoto1Finalizacao() {
        return urlFoto1Finalizacao;
    }

    @Nullable
    public String getUrlFoto2Finalizacao() {
        return urlFoto2Finalizacao;
    }

    @Nullable
    public String getUrlFoto3() {
        return urlFoto3;
    }

}
