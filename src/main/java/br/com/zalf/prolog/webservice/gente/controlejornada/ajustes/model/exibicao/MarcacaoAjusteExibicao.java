package br.com.zalf.prolog.webservice.gente.controlejornada.ajustes.model.exibicao;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Representa uma marcação para exibição na tela de ajustes.
 *
 * Created on 04/09/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class MarcacaoAjusteExibicao {
    /**
     * Código único da marcação.
     */
    private Long codMarcacao;

    /**
     * Data e hora de realização da marcação.
     */
    private LocalDateTime dataHoraMarcacao;

    /**
     * Indica se esta marcação está ativa.
     * <code>true</code> se estiver ativa, <code>false</code> caso contrário.
     */
    private boolean isAtiva;

    /**
     * Identifica se esta marcação já foi ajustada.
     *
     * <code>true</code> se esta marcação já foi ajustada, <code>false</code> caso contrário.
     */
    private boolean jaFoiAjustada;

    @Nullable
    private String deviceImei;
    private boolean deviceReconhecido;

    /**
     * A marca do aparelho. Exemplo: Asus, Motorola e etc.
     */
    @Nullable
    private String marcaDevice;

    /**
     * O modelo do aparelho. Exemplo: ASUS_Z01KD, Moto G6 e etc.
     */
    @Nullable
    private String modeloDevice;

    public MarcacaoAjusteExibicao() {

    }

    @NotNull
    public static MarcacaoAjusteExibicao createDummyInicio() {
        final MarcacaoAjusteExibicao intervalo = new MarcacaoAjusteExibicao();
        intervalo.setCodMarcacao(10101L);
        intervalo.setJaFoiAjustada(true);
        intervalo.setAtiva(true);
        intervalo.setDataHoraMarcacao(LocalDateTime.now());
        intervalo.setDeviceImei("123456789123456");
        intervalo.setDeviceReconhecido(true);
        intervalo.setMarcaDevice("ASUS");
        intervalo.setModeloDevice("Zenfone");
        return intervalo;
    }

    @NotNull
    public static MarcacaoAjusteExibicao createDummyFim() {
        final MarcacaoAjusteExibicao intervalo = new MarcacaoAjusteExibicao();
        intervalo.setCodMarcacao(10101L);
        intervalo.setJaFoiAjustada(false);
        intervalo.setAtiva(true);
        intervalo.setDataHoraMarcacao(LocalDateTime.now().plus(30, ChronoUnit.MINUTES));
        intervalo.setDeviceImei("123456789123456");
        intervalo.setDeviceReconhecido(true);
        intervalo.setMarcaDevice("ASUS");
        intervalo.setModeloDevice("Zenfone");
        return intervalo;
    }

    public Long getCodMarcacao() {
        return codMarcacao;
    }

    public void setCodMarcacao(final Long codMarcacao) {
        this.codMarcacao = codMarcacao;
    }

    public LocalDateTime getDataHoraMarcacao() {
        return dataHoraMarcacao;
    }

    public void setDataHoraMarcacao(final LocalDateTime dataHoraMarcacao) {
        this.dataHoraMarcacao = dataHoraMarcacao;
    }

    public boolean isAtiva() {
        return isAtiva;
    }

    public void setAtiva(final boolean ativa) {
        isAtiva = ativa;
    }

    public boolean isJaFoiAjustada() {
        return jaFoiAjustada;
    }

    public void setJaFoiAjustada(final boolean jaFoiAjustada) {
        this.jaFoiAjustada = jaFoiAjustada;
    }

    @Nullable
    public String getDeviceImei() { return deviceImei; }

    public void setDeviceImei(@Nullable String deviceImei) { this.deviceImei = deviceImei; }

    public boolean isDeviceReconhecido() { return deviceReconhecido; }

    public void setDeviceReconhecido(boolean deviceReconhecido) { this.deviceReconhecido = deviceReconhecido; }

    @Nullable
    public String getMarcaDevice() { return marcaDevice; }

    public void setMarcaDevice(@Nullable String marcaDevice) { this.marcaDevice = marcaDevice; }

    @Nullable
    public String getModeloDevice() { return modeloDevice; }

    public void setModeloDevice(@Nullable String modeloDevice) { this.modeloDevice = modeloDevice; }
}
