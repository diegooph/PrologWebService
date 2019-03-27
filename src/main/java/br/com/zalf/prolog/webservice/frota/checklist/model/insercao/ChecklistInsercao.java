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
    private Long codUnidade;
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

    public Long getCodUnidade() {
        return codUnidade;
    }

    public void setCodUnidade(final Long codUnidade) {
        this.codUnidade = codUnidade;
    }

    public Long getCodModelo() {
        return codModelo;
    }

    public void setCodModelo(final Long codModelo) {
        this.codModelo = codModelo;
    }

    public Long getCodColaborador() {
        return codColaborador;
    }

    public void setCodColaborador(final Long codColaborador) {
        this.codColaborador = codColaborador;
    }

    public Long getCodVeiculo() {
        return codVeiculo;
    }

    public void setCodVeiculo(final Long codVeiculo) {
        this.codVeiculo = codVeiculo;
    }

    public String getPlacaVeiculo() {
        return placaVeiculo;
    }

    public void setPlacaVeiculo(final String placaVeiculo) {
        this.placaVeiculo = placaVeiculo;
    }

    public TipoChecklist getTipo() {
        return tipo;
    }

    public void setTipo(final TipoChecklist tipo) {
        this.tipo = tipo;
    }

    public long getKmColetadoVeiculo() {
        return kmColetadoVeiculo;
    }

    public void setKmColetadoVeiculo(final long kmColetadoVeiculo) {
        this.kmColetadoVeiculo = kmColetadoVeiculo;
    }

    public long getTempoRealizacaoCheckInMillis() {
        return tempoRealizacaoCheckInMillis;
    }

    public void setTempoRealizacaoCheckInMillis(final long tempoRealizacaoCheckInMillis) {
        this.tempoRealizacaoCheckInMillis = tempoRealizacaoCheckInMillis;
    }

    public List<ChecklistResposta> getRespostas() {
        return respostas;
    }

    public void setRespostas(final List<ChecklistResposta> respostas) {
        this.respostas = respostas;
    }

    public LocalDateTime getDataHoraRealizacao() {
        return dataHoraRealizacao;
    }

    public void setDataHoraRealizacao(final LocalDateTime dataHoraRealizacao) {
        this.dataHoraRealizacao = dataHoraRealizacao;
    }

    public FonteDataHora getFonteDataHoraRealizacao() {
        return fonteDataHoraRealizacao;
    }

    public void setFonteDataHoraRealizacao(final FonteDataHora fonteDataHoraRealizacao) {
        this.fonteDataHoraRealizacao = fonteDataHoraRealizacao;
    }

    public Integer getVersaoAppMomentoRealizacao() {
        return versaoAppMomentoRealizacao;
    }

    public void setVersaoAppMomentoRealizacao(final Integer versaoAppMomentoRealizacao) {
        this.versaoAppMomentoRealizacao = versaoAppMomentoRealizacao;
    }

    public Integer getVersaoAppMomentoSincronizacao() {
        return versaoAppMomentoSincronizacao;
    }

    public void setVersaoAppMomentoSincronizacao(final Integer versaoAppMomentoSincronizacao) {
        this.versaoAppMomentoSincronizacao = versaoAppMomentoSincronizacao;
    }

    @Nullable
    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(@Nullable final String deviceId) {
        this.deviceId = deviceId;
    }

    public long getDeviceUptimeRealizacaoMillis() {
        return deviceUptimeRealizacaoMillis;
    }

    public void setDeviceUptimeRealizacaoMillis(final long deviceUptimeRealizacaoMillis) {
        this.deviceUptimeRealizacaoMillis = deviceUptimeRealizacaoMillis;
    }

    public long getDeviceUptimeSincronizacaoMillis() {
        return deviceUptimeSincronizacaoMillis;
    }

    public void setDeviceUptimeSincronizacaoMillis(final long deviceUptimeSincronizacaoMillis) {
        this.deviceUptimeSincronizacaoMillis = deviceUptimeSincronizacaoMillis;
    }
}