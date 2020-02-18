package br.com.zalf.prolog.webservice.frota.socorrorota._model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

/**
 * Created on 2019-12-06
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class SocorroRotaInvalidacao extends SocorroRotaAcao {
    @NotNull
    private final Long codSocorroRota;
    @NotNull
    private final String motivoInvalidacao;
    @Nullable
    private final String urlFoto1Invalidacao;
    @Nullable
    private final String urlFoto2Invalidacao;
    @Nullable
    private final String urlFoto3Invalidacao;

    public SocorroRotaInvalidacao(@NotNull final Long codUnidadeInvalidacao,
                                  @NotNull final Long codSocorroRota,
                                  @NotNull final String motivoInvalidacao,
                                  @Nullable final String urlFoto1Invalidacao,
                                  @Nullable final String urlFoto2Invalidacao,
                                  @Nullable final String urlFoto3Invalidacao,
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
        super(  codUnidadeInvalidacao,
                StatusSocorroRota.INVALIDO,
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
        this.motivoInvalidacao = motivoInvalidacao;
        this.urlFoto1Invalidacao = urlFoto1Invalidacao;
        this.urlFoto2Invalidacao = urlFoto2Invalidacao;
        this.urlFoto3Invalidacao = urlFoto3Invalidacao;
    }

    @NotNull
    public Long getCodSocorroRota() {
        return codSocorroRota;
    }

    @NotNull
    public String getMotivoInvalidacao() {
        return motivoInvalidacao;
    }

    @Nullable
    public String getUrlFoto1() {
        return urlFoto1Invalidacao;
    }

    @Nullable
    public String getUrlFoto2() {
        return urlFoto2Invalidacao;
    }

    @Nullable
    public String getUrlFoto3() {
        return urlFoto3Invalidacao;
    }
}
