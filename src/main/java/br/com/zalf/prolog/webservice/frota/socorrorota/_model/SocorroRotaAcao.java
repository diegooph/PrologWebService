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
public abstract class SocorroRotaAcao {
    @NotNull
    private final Long codUnidade;
    @NotNull
    private final StatusSocorroRota statusSocorroRota;
    @NotNull
    private final Long codColaborador;
    @NotNull
    private final LocalDateTime dataHora;
    /**
     * Localidade em que a ação específica do socorro aconteceu.
     */
    @NotNull
    private final LocalizacaoSocorroRota localizacao;

    /**
     * Endereço coletado automáticamente no App com base na localização capturada.
     */
    @Nullable
    private final String enderecoAutomatico;

    /**
     * Identificador único do aparelho. No Android, é equivalente ao Android ID.
     *
     * @see <a href="Android Docs">https://developer.android.com/reference/android/provider/Settings.Secure.html#ANDROID_ID</a>
     */
    @Nullable
    private final String deviceId;

    /**
     * IMEI do aparelho.
     */
    @Nullable
    private final String deviceImei;

    /**
     * A versão da API do Android no momento da realização da marcação.
     *
     * @see <a href="Android Docs">https://developer.android.com/reference/android/os/Build.VERSION.html#SDK_INT</a>
     */
    private final int androidApiVersion;

    /**
     * O tempo, em milissegundos, desde que o aparelho foi ligado até o momento da realização da
     * marcação.
     *
     * @see <a href="Android Docs">https://developer.android.com/reference/android/os/SystemClock.html#elapsedRealtime()</a>
     */
    private final long deviceUptimeMillis;

    /**
     * A marca do aparelho. Exemplo: Asus, Motorola e etc.
     */
    @Nullable
    private final String marcaDevice;

    /**
     * O modelo do aparelho. Exemplo: ASUS_Z01KD, Moto G6 e etc.
     */
    @Nullable
    private final String modeloDevice;

    /**
     * A plataforma de origem. Exemplos: ANDROID, WEBSITE
     * */
    @NotNull
    private final PrologPlatform plataformaOrigem;

    /**
     * A versão da plataforma de origem. Exemplo: v0.0.77 (WEBSITE)
     * */
    @NotNull
    private final String versaoPlataformaOrigem;

    protected SocorroRotaAcao(@NotNull final Long codUnidade,
                              @NotNull final StatusSocorroRota statusSocorroRota,
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
                              @NotNull final PrologPlatform plataformaOrigem,
                              @NotNull final String versaoPlataformaOrigem) {
        this.codUnidade = codUnidade;
        this.statusSocorroRota = statusSocorroRota;
        this.codColaborador = codColaborador;
        this.dataHora = dataHora;
        this.localizacao = localizacao;
        this.enderecoAutomatico = enderecoAutomatico;
        this.deviceId = deviceId;
        this.deviceImei = deviceImei;
        this.androidApiVersion = androidApiVersion;
        this.deviceUptimeMillis = deviceUptimeMillis;
        this.marcaDevice = marcaDevice;
        this.modeloDevice = modeloDevice;
        this.plataformaOrigem = plataformaOrigem;
        this.versaoPlataformaOrigem = versaoPlataformaOrigem;
    }
    
    @NotNull
    public Long getCodUnidade() {
        return codUnidade;
    }

    @NotNull
    public StatusSocorroRota getStatusSocorroRota() {
        return statusSocorroRota;
    }

    @NotNull
    public Long getCodColaborador() {
        return codColaborador;
    }

    @NotNull
    public LocalDateTime getDataHora() {
        return dataHora;
    }

    @NotNull
    public LocalizacaoSocorroRota getLocalizacao() {
        return localizacao;
    }

    @Nullable
    public String getEnderecoAutomatico() {
        return enderecoAutomatico;
    }

    @Nullable
    public String getDeviceId() {
        return deviceId;
    }

    @Nullable
    public String getDeviceImei() {
        return deviceImei;
    }

    public int getAndroidApiVersion() {
        return androidApiVersion;
    }

    public long getDeviceUptimeMillis() {
        return deviceUptimeMillis;
    }

    @Nullable
    public String getMarcaDevice() {
        return marcaDevice;
    }

    @Nullable
    public String getModeloDevice() {
        return modeloDevice;
    }

    @NotNull
    public PrologPlatform getPlataformaOrigem() { return plataformaOrigem; }

    @NotNull
    public String getVersaoPlataformaOrigem() { return versaoPlataformaOrigem; }
}
