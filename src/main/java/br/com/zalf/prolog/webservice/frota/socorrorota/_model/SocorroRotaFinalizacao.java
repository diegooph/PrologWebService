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
    private final String urlFoto3Finalizacao;

    public SocorroRotaFinalizacao(@NotNull final Long codUnidadeAbertura,
                                  @NotNull final Long codSocorroRota,
                                  @NotNull final String observacaoFinalizacao,
                                  @Nullable final String urlFoto1Finalizacao,
                                  @Nullable final String urlFoto2Finalizacao,
                                  @Nullable final String urlFoto3Finalizacao,
                                  @NotNull final Long codColaborador,
                                  @NotNull final LocalDateTime dataHora,
                                  @NotNull final LocalizacaoSocorroRota localizacao,
                                  @Nullable final String enderecoAutomatico,
                                  @Nullable final String deviceId,
                                  @Nullable final String deviceImei,
                                  final int androidApiVersion,
                                  final long deviceUptimeMillis,
                                  @Nullable final String marcaDevice,
                                  @Nullable final String modeloDevice,
                                  @NotNull final PrologPlatformSocorroRota plataformaOrigem,
                                  @NotNull final String versaoPlataformaOrigem) {
        super(  codUnidadeAbertura,
                StatusSocorroRota.FINALIZADO,
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
        this.codSocorroRota = codSocorroRota;
        this.observacaoFinalizacao = observacaoFinalizacao;
        this.urlFoto1Finalizacao = urlFoto1Finalizacao;
        this.urlFoto2Finalizacao = urlFoto2Finalizacao;
        this.urlFoto3Finalizacao = urlFoto3Finalizacao;
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
    public String getUrlFoto3Finalizacao() {
        return urlFoto3Finalizacao;
    }

}
