package br.com.zalf.prolog.webservice.integracao.api.controlejornada._model;

import br.com.zalf.prolog.webservice.commons.util.datetime.Now;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

/**
 * Created on 30/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ApiMarcacao {
    @NotNull
    private final Long codUnidade;
    @NotNull
    private final Long codigo;
    @Nullable
    private final Long codMarcacaoVinculada;
    @NotNull
    private final Long codTipoMarcacao;
    @NotNull
    private final String cpfColaboradorMarcacao;
    @NotNull
    private final ApiTipoInicioFim tipoMarcaco;
    @NotNull
    private final LocalDateTime dataHoraMarcacaoUtc;
    @NotNull
    private final ApiFonteDataHora fonteDataHoraMarcacao;
    @Nullable
    private final String justificativaTempoRecomendado;
    @Nullable
    private final String justificativaEstouro;
    @Nullable
    private final ApiCoordenadasMarcacao coordenadasMarcacao;
    @NotNull
    private final LocalDateTime dataHoraSincronizacaoUtc;
    @Nullable
    private final String deviceImei;
    @Nullable
    private final String deviceId;
    @Nullable
    private final String marcaDevice;
    @Nullable
    private final String modeloDevice;
    @Nullable
    private final Integer versaoAppMarcacao;
    @Nullable
    private final Integer versaoAppSincronizacao;
    private final long deviceUptimeMarcacaoMillis;
    private final long deviceUptimeSincronizacaoMillis;
    @Nullable
    private final Integer androidApiVersion;
    private final boolean statusAtivo;

    public ApiMarcacao(@NotNull final Long codUnidade,
                       @NotNull final Long codigo,
                       @Nullable final Long codMarcacaoVinculada,
                       @NotNull final Long codTipoMarcacao,
                       @NotNull final String cpfColaboradorMarcacao,
                       @NotNull final ApiTipoInicioFim tipoMarcaco,
                       @NotNull final LocalDateTime dataHoraMarcacaoUtc,
                       @NotNull final ApiFonteDataHora fonteDataHoraMarcacao,
                       @Nullable final String justificativaTempoRecomendado,
                       @Nullable final String justificativaEstouro,
                       @Nullable final ApiCoordenadasMarcacao coordenadasMarcacao,
                       @NotNull final LocalDateTime dataHoraSincronizacaoUtc,
                       @Nullable final String deviceImei,
                       @Nullable final String deviceId,
                       @Nullable final String marcaDevice,
                       @Nullable final String modeloDevice,
                       @Nullable final Integer versaoAppMarcacao,
                       @Nullable final Integer versaoAppSincronizacao,
                       final long deviceUptimeMarcacaoMillis,
                       final long deviceUptimeSincronizacaoMillis,
                       @Nullable final Integer androidApiVersion,
                       final boolean statusAtivo) {
        this.codUnidade = codUnidade;
        this.codigo = codigo;
        this.codMarcacaoVinculada = codMarcacaoVinculada;
        this.codTipoMarcacao = codTipoMarcacao;
        this.cpfColaboradorMarcacao = cpfColaboradorMarcacao;
        this.tipoMarcaco = tipoMarcaco;
        this.dataHoraMarcacaoUtc = dataHoraMarcacaoUtc;
        this.fonteDataHoraMarcacao = fonteDataHoraMarcacao;
        this.justificativaTempoRecomendado = justificativaTempoRecomendado;
        this.justificativaEstouro = justificativaEstouro;
        this.coordenadasMarcacao = coordenadasMarcacao;
        this.dataHoraSincronizacaoUtc = dataHoraSincronizacaoUtc;
        this.deviceImei = deviceImei;
        this.deviceId = deviceId;
        this.marcaDevice = marcaDevice;
        this.modeloDevice = modeloDevice;
        this.versaoAppMarcacao = versaoAppMarcacao;
        this.versaoAppSincronizacao = versaoAppSincronizacao;
        this.deviceUptimeMarcacaoMillis = deviceUptimeMarcacaoMillis;
        this.deviceUptimeSincronizacaoMillis = deviceUptimeSincronizacaoMillis;
        this.androidApiVersion = androidApiVersion;
        this.statusAtivo = statusAtivo;
    }

    @NotNull
    public static ApiMarcacao getDummy() {
        return new ApiMarcacao(
                5L,
                194753L,
                194738L,
                35L,
                "03383283194",
                ApiTipoInicioFim.MARCACAO_FIM,
                Now.getLocalDateTimeUtc(),
                ApiFonteDataHora.REDE_CELULAR,
                null,
                "Esqueci de finalizar antes",
                new ApiCoordenadasMarcacao("12345", "12345"),
                Now.getLocalDateTimeUtc(),
                "XXXXXXXXX",
                "A23DSJLC43",
                "Samsung",
                "A50",
                78,
                78,
                12345,
                12345,
                23,
                true);
    }

    @NotNull
    public Long getCodUnidade() {
        return codUnidade;
    }

    @NotNull
    public Long getCodigo() {
        return codigo;
    }

    @Nullable
    public Long getCodMarcacaoVinculada() {
        return codMarcacaoVinculada;
    }

    @NotNull
    public Long getCodTipoMarcacao() {
        return codTipoMarcacao;
    }

    @NotNull
    public String getCpfColaboradorMarcacao() {
        return cpfColaboradorMarcacao;
    }

    @NotNull
    public ApiTipoInicioFim getTipoMarcaco() {
        return tipoMarcaco;
    }

    @NotNull
    public LocalDateTime getDataHoraMarcacaoUtc() {
        return dataHoraMarcacaoUtc;
    }

    @NotNull
    public ApiFonteDataHora getFonteDataHoraMarcacao() {
        return fonteDataHoraMarcacao;
    }

    @Nullable
    public String getJustificativaTempoRecomendado() {
        return justificativaTempoRecomendado;
    }

    @Nullable
    public String getJustificativaEstouro() {
        return justificativaEstouro;
    }

    @Nullable
    public ApiCoordenadasMarcacao getCoordenadasMarcacao() {
        return coordenadasMarcacao;
    }

    @NotNull
    public LocalDateTime getDataHoraSincronizacaoUtc() {
        return dataHoraSincronizacaoUtc;
    }

    @Nullable
    public String getDeviceImei() {
        return deviceImei;
    }

    @Nullable
    public String getDeviceId() {
        return deviceId;
    }

    @Nullable
    public String getMarcaDevice() {
        return marcaDevice;
    }

    @Nullable
    public String getModeloDevice() {
        return modeloDevice;
    }

    @Nullable
    public Integer getVersaoAppMarcacao() {
        return versaoAppMarcacao;
    }

    @Nullable
    public Integer getVersaoAppSincronizacao() {
        return versaoAppSincronizacao;
    }

    public long getDeviceUptimeMarcacaoMillis() {
        return deviceUptimeMarcacaoMillis;
    }

    public long getDeviceUptimeSincronizacaoMillis() {
        return deviceUptimeSincronizacaoMillis;
    }

    @Nullable
    public Integer getAndroidApiVersion() {
        return androidApiVersion;
    }

    public boolean isStatusAtivo() {
        return statusAtivo;
    }
}
