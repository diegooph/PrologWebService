package br.com.zalf.prolog.webservice.gente.controlejornada.model;

import br.com.zalf.prolog.webservice.commons.FonteDataHora;
import br.com.zalf.prolog.webservice.gente.controlejornada.tipomarcacao.TipoMarcacao;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * Created on 08/03/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class IntervaloMarcacao {
    /**
     * Código desta marcação presente no servidor.
     */
    @NotNull
    private Long codigo;

    /**
     * Código do Tipo da marcação realizada.
     */
    @NotNull
    private Long codTipoIntervalo;

    /**
     * Código da Unidade da Marcação.
     */
    @NotNull
    private Long codUnidade;

    /**
     * CPF do Colaborador que realizou a marcação
     */
    @NotNull
    private Long cpfColaborador;

    /**
     * Data de Nascimento do Colaborador que realizou a marcação.
     */
    @NotNull
    private Date dataNascimentoColaborador;

    /**
     * Data e Hora da marcação realizada.
     */
    @NotNull
    private LocalDateTime dataHoraMaracao;

    /**
     * Constante que representa de onde a {@code dataHoraMaracao} foi obtida.
     */
    @NotNull
    private FonteDataHora fonteDataHora;

    /**
     * Constante que representa se a marcação foi {@link TipoInicioFim#MARCACAO_INICIO}
     * ou {@link TipoInicioFim#MARCACAO_FIM}.
     */
    @NotNull
    private TipoInicioFim tipoMarcacaoIntervalo;

    /**
     * Descrição do motivo de o {@code tempoDecorrido} ser MAIOR que
     * o {@link TipoMarcacao#tempoLimiteEstouro}.
     */
    @Nullable
    private String justificativaEstouro;

    /**
     * Descrição do motivo de o {@code tempoDecorrido} ser MENOR que
     * o {@link TipoMarcacao#tempoRecomendado}.
     */
    @Nullable
    private String justificativaTempoRecomendado;

    /**
     * Localidade em que a marcação foi realizada.
     */
    @Nullable
    private Localizacao localizacaoMarcacao;

    /**
     * Código da marcação vinculada à esta marcação.
     */
    @Nullable
    private Long codMarcacaoVinculada;

    /**
     * Versão do aplicativo no momento que a marcação foi realizada.
     */
    @Nullable
    private Integer versaoAppMomentoMarcacao;

    /**
     * Versão do aplicativo no momento que a marcação foi sincronizada.
     */
    @Nullable
    private Integer versaoAppMomentoSincronizacao;

    /**
     * Identificador único do aparelho. No Android, é equivalente ao Android ID.
     *
     * @see <a href="Android Docs">https://developer.android.com/reference/android/provider/Settings.Secure.html#ANDROID_ID</a>
     */
    @Nullable
    private String deviceId;

    /**
     * IMEI do aparelho.
     */
    @Nullable
    private String deviceImei;

    /**
     * A versão da API do Android no momento da realização da marcação.
     *
     * @see <a href="Android Docs">https://developer.android.com/reference/android/os/Build.VERSION.html#SDK_INT</a>
     */
    private int androidApiVersion;

    /**
     * O tempo, em milissegundos, desde que o aparelho foi ligado até o momento da realização da
     * marcação.
     *
     * @see <a href="Android Docs">https://developer.android.com/reference/android/os/SystemClock.html#elapsedRealtime()</a>
     */
    private long deviceUptimeRealizacaoMarcacaoMillis;

    /**
     * O tempo, em milissegundos, desde que o aparelho foi ligado até o momento da sincronização da
     * marcação.
     *
     * @see <a href="Android Docs">https://developer.android.com/reference/android/os/SystemClock.html#elapsedRealtime()</a>
     */
    private long deviceUptimeSincronizacaoMarcacaoMillis;

    /**
     * A marca do aparelho. Exemplo: Asus, Motorola e etc.
     */
    private String marcaDevice;

    /**
     * O modelo do aparelho. Exemplo: ASUS_Z01KD, Moto G6 e etc.
     */
    private String modeloDevice;

    public IntervaloMarcacao() {

    }

    public boolean isInicio() {
        return tipoMarcacaoIntervalo.equals(TipoInicioFim.MARCACAO_INICIO);
    }

    @NotNull
    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(@NotNull final Long codigo) {
        this.codigo = codigo;
    }

    @NotNull
    public Long getCodTipoIntervalo() {
        return codTipoIntervalo;
    }

    public void setCodTipoIntervalo(@NotNull final Long codTipoIntervalo) {
        this.codTipoIntervalo = codTipoIntervalo;
    }

    @NotNull
    public Long getCodUnidade() {
        return codUnidade;
    }

    public void setCodUnidade(@NotNull final Long codUnidade) {
        this.codUnidade = codUnidade;
    }

    @NotNull
    public Long getCpfColaborador() {
        return cpfColaborador;
    }

    public void setCpfColaborador(@NotNull final Long cpfColaborador) {
        this.cpfColaborador = cpfColaborador;
    }

    @NotNull
    public Date getDataNascimentoColaborador() {
        return dataNascimentoColaborador;
    }

    public void setDataNascimentoColaborador(@NotNull final Date dataNascimentoColaborador) {
        this.dataNascimentoColaborador = dataNascimentoColaborador;
    }

    @NotNull
    public LocalDateTime getDataHoraMaracao() {
        return dataHoraMaracao;
    }

    public void setDataHoraMaracao(@NotNull final LocalDateTime dataHoraMaracao) {
        this.dataHoraMaracao = dataHoraMaracao;
    }

    @NotNull
    public FonteDataHora getFonteDataHora() {
        return fonteDataHora;
    }

    public void setFonteDataHora(@NotNull final FonteDataHora fonteDataHora) {
        this.fonteDataHora = fonteDataHora;
    }

    @NotNull
    public TipoInicioFim getTipoMarcacaoIntervalo() {
        return tipoMarcacaoIntervalo;
    }

    public void setTipoMarcacaoIntervalo(@NotNull final TipoInicioFim tipoMarcacaoIntervalo) {
        this.tipoMarcacaoIntervalo = tipoMarcacaoIntervalo;
    }

    @Nullable
    public String getJustificativaEstouro() {
        return justificativaEstouro;
    }

    public void setJustificativaEstouro(@Nullable final String justificativaEstouro) {
        this.justificativaEstouro = justificativaEstouro;
    }

    @Nullable
    public String getJustificativaTempoRecomendado() {
        return justificativaTempoRecomendado;
    }

    public void setJustificativaTempoRecomendado(@Nullable final String justificativaTempoRecomendado) {
        this.justificativaTempoRecomendado = justificativaTempoRecomendado;
    }

    @Nullable
    public Localizacao getLocalizacaoMarcacao() {
        return localizacaoMarcacao;
    }

    public void setLocalizacaoMarcacao(@Nullable final Localizacao localizacaoMarcacao) {
        this.localizacaoMarcacao = localizacaoMarcacao;
    }

    @Nullable
    public Long getCodMarcacaoVinculada() {
        return codMarcacaoVinculada;
    }

    public void setCodMarcacaoVinculada(@Nullable final Long codMarcacaoVinculada) {
        this.codMarcacaoVinculada = codMarcacaoVinculada;
    }

    @Nullable
    public Integer getVersaoAppMomentoMarcacao() {
        return versaoAppMomentoMarcacao;
    }

    public void setVersaoAppMomentoMarcacao(@Nullable final Integer versaoAppMomentoMarcacao) {
        this.versaoAppMomentoMarcacao = versaoAppMomentoMarcacao;
    }

    @Nullable
    public Integer getVersaoAppMomentoSincronizacao() {
        return versaoAppMomentoSincronizacao;
    }

    public void setVersaoAppMomentoSincronizacao(@Nullable final Integer versaoAppMomentoSincronizacao) {
        this.versaoAppMomentoSincronizacao = versaoAppMomentoSincronizacao;
    }

    @Nullable
    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(@Nullable final String deviceId) {
        this.deviceId = deviceId;
    }

    @Nullable
    public String getDeviceImei() {
        return deviceImei;
    }

    public void setDeviceImei(@Nullable final String deviceImei) {
        this.deviceImei = deviceImei;
    }

    public int getAndroidApiVersion() {
        return androidApiVersion;
    }

    public void setAndroidApiVersion(final int androidApiVersion) {
        this.androidApiVersion = androidApiVersion;
    }

    public long getDeviceUptimeRealizacaoMarcacaoMillis() {
        return deviceUptimeRealizacaoMarcacaoMillis;
    }

    public void setDeviceUptimeRealizacaoMarcacaoMillis(final long deviceUptimeRealizacaoMarcacaoMillis) {
        this.deviceUptimeRealizacaoMarcacaoMillis = deviceUptimeRealizacaoMarcacaoMillis;
    }

    public long getDeviceUptimeSincronizacaoMarcacaoMillis() {
        return deviceUptimeSincronizacaoMarcacaoMillis;
    }

    public void setDeviceUptimeSincronizacaoMarcacaoMillis(final long deviceUptimeSincronizacaoMarcacaoMillis) {
        this.deviceUptimeSincronizacaoMarcacaoMillis = deviceUptimeSincronizacaoMarcacaoMillis;
    }

    public String getMarcaDevice() {
        return marcaDevice;
    }

    public void setMarcaDevice(final String marcaDevice) {
        this.marcaDevice = marcaDevice;
    }

    public String getModeloDevice() {
        return modeloDevice;
    }

    public void setModeloDevice(final String modeloDevice) {
        this.modeloDevice = modeloDevice;
    }
}