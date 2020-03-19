package br.com.zalf.prolog.webservice.frota.socorrorota._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

/**
 * Este objeto será utilizado para as ações de registro de início de fim de deslocamento de um socorro em atendimento.
 * <p>
 * Como haverá resources específicos, o mesmo objeto poderá ser utilizado para as duas ações.
 * <p>
 * Created on 2020-03-19
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
@Data
public abstract class SocorroRotaAtendimentoDeslocamento {
    /**
     * Código do socorro em rota em atendimento.
     */
    @NotNull
    private final Long codSocorroRota;

    /**
     * Código do colaborador que executou a ação.
     */
    @NotNull
    private final Long codColaborador;

    /**
     * Data e hora da execução da ação.
     */
    @NotNull
    private final LocalDateTime dataHora;

    /**
     * Localidade em que a ação específica do socorro aconteceu.
     */
    @NotNull
    private final LocalizacaoSocorroRota localizacao;

    /**
     * Endereço coletado automaticamente no App com base na localização capturada.
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
     * O tempo, em milissegundos, desde que o aparelho foi ligado até o momento da realização da
     * marcação.
     *
     * @see <a href="Android Docs">https://developer.android.com/reference/android/os/SystemClock.html#elapsedRealtime()</a>
     */
    private final long deviceUptimeMillis;

    /**
     * A versão da API do Android no momento da realização da marcação.
     *
     * @see <a href="Android Docs">https://developer.android.com/reference/android/os/Build.VERSION.html#SDK_INT</a>
     */
    private final int androidApiVersion;


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
     */
    @NotNull
    private final PrologPlatformSocorroRota plataformaOrigem;

    /**
     * A versão da plataforma de origem. Exemplo: v0.0.77 (WEBSITE)
     */
    @NotNull
    private final String versaoPlataformaOrigem;
}
