package br.com.zalf.prolog.webservice.frota.checklist.model.insercao;

import br.com.zalf.prolog.webservice.frota.checklist.model.TipoChecklist;
import br.com.zalf.prolog.webservice.commons.FonteDataHora;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created on 08/11/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ChecklistInsercao {
    private Long codModelo;
    private Long codColaborador;
    private Long codVeiculo;
    private String placaVeiculo;
    private TipoChecklist tipo;
    private long kmColetadoVeiculo;
    private long tempoRealizacaoCheckInMillis;
    private List<ChecklistResposta> respostas;
    private LocalDateTime dataHoraRealizacao;
    private FonteDataHora fonteDataHoraRealizacao;
    /**
     * Versão do aplicativo no momento que o checklist foi realizado.
     */
    private Integer versaoAppMomentoRealizacao;
    /**
     * Versão do aplicativo no momento que o checklist foi sincronizado.
     */
    private Integer versaoAppMomentoSincronizacao;
    /**
     * Identificador único do celular: IMEI ou MEID ou ESN e etc.
     */
    @Nullable
    private String deviceId;
    /**
     * O tempo, em milissegundos, desde que o aparelho foi ligado até a realização do check.
     *
     * @see <a href="Android Docs">https://developer.android.com/reference/android/os/SystemClock.html#elapsedRealtime()</a>
     */
    private long deviceUptimeRealizacaoMillis;

    /**
     * O tempo, em milissegundos, desde que o aparelho foi ligado até a sincronização do check.
     *
     * @see <a href="Android Docs">https://developer.android.com/reference/android/os/SystemClock.html#elapsedRealtime()</a>
     */
    private long deviceUptimeSincronizacaoMillis;

    public ChecklistInsercao() {

    }

    public Long getCodModelo() {
        return codModelo;
    }

    public Long getCodColaborador() {
        return codColaborador;
    }

    public Long getCodVeiculo() {
        return codVeiculo;
    }

    public String getPlacaVeiculo() {
        return placaVeiculo;
    }

    public TipoChecklist getTipo() {
        return tipo;
    }

    public long getKmColetadoVeiculo() {
        return kmColetadoVeiculo;
    }

    public long getTempoRealizacaoCheckInMillis() {
        return tempoRealizacaoCheckInMillis;
    }

    public List<ChecklistResposta> getRespostas() {
        return respostas;
    }

    public LocalDateTime getDataHoraRealizacao() {
        return dataHoraRealizacao;
    }

    public FonteDataHora getFonteDataHoraRealizacao() {
        return fonteDataHoraRealizacao;
    }

    public Integer getVersaoAppMomentoRealizacao() {
        return versaoAppMomentoRealizacao;
    }

    public Integer getVersaoAppMomentoSincronizacao() {
        return versaoAppMomentoSincronizacao;
    }

    @Nullable
    public String getDeviceId() {
        return deviceId;
    }

    public long getDeviceUptimeRealizacaoMillis() {
        return deviceUptimeRealizacaoMillis;
    }

    public long getDeviceUptimeSincronizacaoMillis() {
        return deviceUptimeSincronizacaoMillis;
    }
}