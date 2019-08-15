package br.com.zalf.prolog.webservice.frota.checklist.model.insercao;

import br.com.zalf.prolog.webservice.commons.FonteDataHora;
import br.com.zalf.prolog.webservice.commons.gson.Exclude;
import br.com.zalf.prolog.webservice.frota.checklist.model.TipoChecklist;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created on 08/11/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ChecklistInsercao {
    @NotNull
    private final Long codUnidade;

    @NotNull
    private final Long codModelo;

    /**
     * Versão do modelo que o checklist realizado referencia.
     * TODO: Será provisoriamente não-final para funcionar no processo de migração dos apps antigos para a nova estrutura.
     */
    @NotNull
    private Long codVersaoModeloChecklist;

    @NotNull
    private final Long codColaborador;

    @NotNull
    private final Long codVeiculo;

    @NotNull
    private final String placaVeiculo;

    @NotNull
    private final TipoChecklist tipo;

    private final long kmColetadoVeiculo;

    private final long tempoRealizacaoCheckInMillis;

    @NotNull
    private final List<ChecklistResposta> respostas;

    @NotNull
    private final LocalDateTime dataHoraRealizacao;

    @NotNull
    private final FonteDataHora fonteDataHoraRealizacao;

    /**
     * Versão do aplicativo no momento que o checklist foi realizado.
     */
    @NotNull
    private final Integer versaoAppMomentoRealizacao;

    /**
     * Versão do aplicativo no momento que o checklist foi sincronizado.
     */
    @NotNull
    private final Integer versaoAppMomentoSincronizacao;

    /**
     * Identificador único do celular: IMEI ou MEID ou ESN e etc.
     */
    @Nullable
    private final String deviceId;

    /**
     * IMEI do aparelho.
     */
    @Nullable
    private final String deviceImei;

    /**
     * O tempo, em milissegundos, desde que o aparelho foi ligado até a realização do check.
     *
     * @see <a href="Android Docs">https://developer.android.com/reference/android/os/SystemClock.html#elapsedRealtime()</a>
     */
    private final long deviceUptimeRealizacaoMillis;

    /**
     * O tempo, em milissegundos, desde que o aparelho foi ligado até a sincronização do check.
     *
     * @see <a href="Android Docs">https://developer.android.com/reference/android/os/SystemClock.html#elapsedRealtime()</a>
     */
    private final long deviceUptimeSincronizacaoMillis;

    /**
     * Metadados criados no momento de criação desse objeto contendo informações extras sobre o checklist sendo
     * inserido.
     */
    @NotNull
    @Exclude
    private final ChecklistInsercaoMetadata cachedMetadata;

    public ChecklistInsercao(@NotNull final Long codUnidade,
                             @NotNull final Long codModelo,
                             @NotNull final Long codVersaoModeloChecklist,
                             @NotNull final Long codColaborador,
                             @NotNull final Long codVeiculo,
                             @NotNull final String placaVeiculo,
                             @NotNull final TipoChecklist tipo,
                             final long kmColetadoVeiculo,
                             final long tempoRealizacaoCheckInMillis,
                             @NotNull final List<ChecklistResposta> respostas,
                             @NotNull final LocalDateTime dataHoraRealizacao,
                             @NotNull final FonteDataHora fonteDataHoraRealizacao,
                             @NotNull final Integer versaoAppMomentoRealizacao,
                             @NotNull final Integer versaoAppMomentoSincronizacao,
                             @Nullable final String deviceId,
                             @Nullable final String deviceImei,
                             final long deviceUptimeRealizacaoMillis,
                             final long deviceUptimeSincronizacaoMillis) {
        this.codUnidade = codUnidade;
        this.codModelo = codModelo;
        this.codVersaoModeloChecklist = codVersaoModeloChecklist;
        this.codColaborador = codColaborador;
        this.codVeiculo = codVeiculo;
        this.placaVeiculo = placaVeiculo;
        this.tipo = tipo;
        this.kmColetadoVeiculo = kmColetadoVeiculo;
        this.tempoRealizacaoCheckInMillis = tempoRealizacaoCheckInMillis;
        this.respostas = respostas;
        this.dataHoraRealizacao = dataHoraRealizacao;
        this.fonteDataHoraRealizacao = fonteDataHoraRealizacao;
        this.versaoAppMomentoRealizacao = versaoAppMomentoRealizacao;
        this.versaoAppMomentoSincronizacao = versaoAppMomentoSincronizacao;
        this.deviceId = deviceId;
        this.deviceImei = deviceImei;
        this.deviceUptimeRealizacaoMillis = deviceUptimeRealizacaoMillis;
        this.deviceUptimeSincronizacaoMillis = deviceUptimeSincronizacaoMillis;
        this.cachedMetadata = createMetadata();
    }

    @NotNull
    public Long getCodUnidade() {
        return codUnidade;
    }

    @NotNull
    public Long getCodModelo() {
        return codModelo;
    }

    @NotNull
    public Long getCodVersaoModeloChecklist() {
        return codVersaoModeloChecklist;
    }

    public void setCodVersaoModeloChecklist(@NotNull final Long codVersaoModeloChecklist) {
        this.codVersaoModeloChecklist = codVersaoModeloChecklist;
    }

    @NotNull
    public Long getCodColaborador() {
        return codColaborador;
    }

    @NotNull
    public Long getCodVeiculo() {
        return codVeiculo;
    }

    @NotNull
    public String getPlacaVeiculo() {
        return placaVeiculo;
    }

    @NotNull
    public TipoChecklist getTipo() {
        return tipo;
    }

    public long getKmColetadoVeiculo() {
        return kmColetadoVeiculo;
    }

    public long getTempoRealizacaoCheckInMillis() {
        return tempoRealizacaoCheckInMillis;
    }

    @NotNull
    public List<ChecklistResposta> getRespostas() {
        return respostas;
    }

    @NotNull
    public LocalDateTime getDataHoraRealizacao() {
        return dataHoraRealizacao;
    }

    @NotNull
    public FonteDataHora getFonteDataHoraRealizacao() {
        return fonteDataHoraRealizacao;
    }

    @NotNull
    public Integer getVersaoAppMomentoRealizacao() {
        return versaoAppMomentoRealizacao;
    }

    @NotNull
    public Integer getVersaoAppMomentoSincronizacao() {
        return versaoAppMomentoSincronizacao;
    }

    @Nullable
    public String getDeviceId() {
        return deviceId;
    }

    @Nullable
    public String getDeviceImei() {
        return deviceImei;
    }

    public long getDeviceUptimeRealizacaoMillis() {
        return deviceUptimeRealizacaoMillis;
    }

    public long getDeviceUptimeSincronizacaoMillis() {
        return deviceUptimeSincronizacaoMillis;
    }

    public int getQtdPerguntasOk() {
        return cachedMetadata.getQtdPerguntasOk();
    }

    public int getQtdPerguntasNok() {
        return cachedMetadata.getQtdPerguntasNok();
    }

    public int getQtdAlternativasOk() {
        return cachedMetadata.getQtdAlternativasOk();
    }

    public int getQtdAlternativasNok() {
        return cachedMetadata.getQtdAlternativasNok();
    }

    @NotNull
    private ChecklistInsercaoMetadata createMetadata() {
        int qtdPerguntasOk = 0;
        int qtdPerguntasNok = 0;
        int qtdAlternativasOk = 0;
        int qtdAlternativasNok = 0;
        boolean perguntaTeveAlternativasNok = false;
        for (int i = 0; i < respostas.size(); i++) {
            final ChecklistResposta checklistResposta = respostas.get(i);
            final List<ChecklistAlternativaResposta> alternativasRespostas = checklistResposta.getAlternativasRespostas();
            for (int j = 0; j < alternativasRespostas.size(); j++) {
                final ChecklistAlternativaResposta alternativaResposta = alternativasRespostas.get(j);
                if (alternativaResposta.isAlternativaSelecionada()) {
                    qtdAlternativasNok++;
                    perguntaTeveAlternativasNok = true;
                } else {
                    qtdAlternativasOk++;
                }
            }
            if (perguntaTeveAlternativasNok) {
                qtdPerguntasNok++;
                perguntaTeveAlternativasNok = false;
            } else {
                qtdPerguntasOk++;
            }
        }

        return new ChecklistInsercaoMetadata(
                qtdPerguntasOk,
                qtdPerguntasNok,
                qtdAlternativasOk,
                qtdAlternativasNok);
    }

    private static final class ChecklistInsercaoMetadata {
        private final int qtdPerguntasOk;
        private final int qtdPerguntasNok;
        private final int qtdAlternativasOk;
        private final int qtdAlternativasNok;

        ChecklistInsercaoMetadata(final int qtdPerguntasOk,
                                  final int qtdPerguntasNok,
                                  final int qtdAlternativasOk,
                                  final int qtdAlternativasNok) {
            this.qtdPerguntasOk = qtdPerguntasOk;
            this.qtdPerguntasNok = qtdPerguntasNok;
            this.qtdAlternativasOk = qtdAlternativasOk;
            this.qtdAlternativasNok = qtdAlternativasNok;
        }

        int getQtdPerguntasOk() {
            return qtdPerguntasOk;
        }

        int getQtdPerguntasNok() {
            return qtdPerguntasNok;
        }

        int getQtdAlternativasOk() {
            return qtdAlternativasOk;
        }

        int getQtdAlternativasNok() {
            return qtdAlternativasNok;
        }
    }
}