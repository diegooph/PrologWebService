package br.com.zalf.prolog.webservice.gente.controlejornada.acompanhamento;

import br.com.zalf.prolog.webservice.commons.FonteDataHora;
import br.com.zalf.prolog.webservice.gente.controlejornada.model.Localizacao;
import br.com.zalf.prolog.webservice.gente.controlejornada.model.TipoInicioFim;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

/**
 * Created on 29/01/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class MarcacaoAcompanhamento {
    @NotNull
    private final Long codigo;
    @NotNull
    private final LocalDateTime dataHoraMarcacao;
    @NotNull
    private final FonteDataHora fonteDataHora;
    @NotNull
    private final TipoInicioFim tipoInicioFim;
    @Nullable
    private final Localizacao localizacaoMarcacao;
    @Nullable
    private final Integer versaoAppMomentoMarcacao;
    @Nullable
    private final Integer versaoAppMomentoSincronizacao;
    private final boolean foiAjustado;
    @Nullable
    private final String deviceImei;
    private final boolean deviceReconhecido;
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

    public MarcacaoAcompanhamento(@NotNull final Long codigo,
                                  @NotNull final LocalDateTime dataHoraMarcacao,
                                  @NotNull final FonteDataHora fonteDataHora,
                                  @NotNull final TipoInicioFim tipoInicioFim,
                                  @Nullable final Localizacao localizacaoMarcacao,
                                  @Nullable final Integer versaoAppMomentoMarcacao,
                                  @Nullable final Integer versaoAppMomentoSincronizacao,
                                  final boolean foiAjustado,
                                  @Nullable final String deviceImei,
                                  final boolean deviceReconhecido,
                                  @Nullable final String marcaDevice,
                                  @Nullable final String modeloDevice) {
        this.codigo = codigo;
        this.dataHoraMarcacao = dataHoraMarcacao;
        this.fonteDataHora = fonteDataHora;
        this.tipoInicioFim = tipoInicioFim;
        this.localizacaoMarcacao = localizacaoMarcacao;
        this.versaoAppMomentoMarcacao = versaoAppMomentoMarcacao;
        this.versaoAppMomentoSincronizacao = versaoAppMomentoSincronizacao;
        this.foiAjustado = foiAjustado;
        this.deviceImei = deviceImei;
        this.deviceReconhecido = deviceReconhecido;
        this.marcaDevice = marcaDevice;
        this.modeloDevice = modeloDevice;
    }

    @NotNull
    public static MarcacaoAcompanhamento createDummy(final boolean tipoInicio) {
        return new MarcacaoAcompanhamento(
                1L,
                LocalDateTime.now(),
                FonteDataHora.REDE_CELULAR,
                tipoInicio ? TipoInicioFim.MARCACAO_INICIO : TipoInicioFim.MARCACAO_FIM,
                new Localizacao("-27.6403877", "-48.6827399"),
                60,
                61,
                true,
                "123456789123456",
                true,
                "ASUS",
                "Zenfone 4");
    }

    @NotNull
    public Long getCodigo() {
        return codigo;
    }

    @NotNull
    public LocalDateTime getDataHoraMarcacao() {
        return dataHoraMarcacao;
    }

    @NotNull
    public FonteDataHora getFonteDataHora() {
        return fonteDataHora;
    }

    @NotNull
    public TipoInicioFim getTipoInicioFim() {
        return tipoInicioFim;
    }

    @Nullable
    public Localizacao getLocalizacaoMarcacao() {
        return localizacaoMarcacao;
    }

    @Nullable
    public Integer getVersaoAppMomentoMarcacao() {
        return versaoAppMomentoMarcacao;
    }

    @Nullable
    public Integer getVersaoAppMomentoSincronizacao() {
        return versaoAppMomentoSincronizacao;
    }

    public boolean isFoiAjustado() {
        return foiAjustado;
    }

    @Nullable
    public String getDeviceImei() { return deviceImei; }

    public boolean isDeviceReconhecido() { return deviceReconhecido; }

    @Nullable
    public String getMarcaDevice() { return marcaDevice; }

    @Nullable
    public String getModeloDevice() { return modeloDevice; }
}