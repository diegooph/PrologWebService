package br.com.zalf.prolog.webservice.frota.socorrorota._model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

/**
 * Created on 2019-12-06
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class SocorroRotaAtendimento extends SocorroRotaAcao {
    @NotNull
    private final Long codSocorroRota;
    @Nullable
    private final String observacaoAtendimento;

    public SocorroRotaAtendimento(@NotNull final Long codUnidadeAbertura,
                                  @NotNull final Long codSocorroRota,
                                  @Nullable final String observacaoAtendimento,
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
                StatusSocorroRota.EM_ATENDIMENTO,
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
        this.observacaoAtendimento = observacaoAtendimento;
    }

    @NotNull
    public Long getCodSocorroRota() {
        return codSocorroRota;
    }

    @Nullable
    public String getObservacaoAtendimento() {
        return observacaoAtendimento;
    }
}
