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
    @Nullable
    private final String observacaoFinalizacao;
    @Nullable
    private final String urlFoto1;
    @Nullable
    private final String urlFoto2;
    @Nullable
    private final String urlFoto3;

    public SocorroRotaFinalizacao(@NotNull final Long codSocorroRota,
                                  @Nullable final String observacaoFinalizacao,
                                  @Nullable final String urlFoto1,
                                  @Nullable final String urlFoto2,
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
        this.urlFoto1 = urlFoto1;
        this.urlFoto2 = urlFoto2;
        this.urlFoto3 = urlFoto3;
    }

    @NotNull
    public Long getCodSocorroRota() {
        return codSocorroRota;
    }

    @Nullable
    public String getObservacaoFinalizacao() {
        return observacaoFinalizacao;
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

}
