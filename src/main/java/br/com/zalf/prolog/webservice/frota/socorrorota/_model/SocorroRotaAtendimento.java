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
    private final boolean deslocamentoIniciado;

    public SocorroRotaAtendimento(@NotNull final Long codUnidadeAbertura,
                                  @NotNull final Long codSocorroRota,
                                  @Nullable final String observacaoAtendimento,
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
                                  @NotNull final String versaoPlataformaOrigem,
                                  final boolean deslocamentoIniciado) {
        super(  codUnidadeAbertura,
                StatusSocorroRota.EM_ATENDIMENTO,
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
        this.observacaoAtendimento = observacaoAtendimento;
        this.deslocamentoIniciado = deslocamentoIniciado;
    }

    @NotNull
    public Long getCodSocorroRota() {
        return codSocorroRota;
    }

    @Nullable
    public String getObservacaoAtendimento() {
        return observacaoAtendimento;
    }

    public boolean isDeslocamentoIniciado() { return deslocamentoIniciado; }
}
