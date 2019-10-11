package br.com.zalf.prolog.webservice.frota.checklist.model.insercao;

import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.commons.FonteDataHora;
import br.com.zalf.prolog.webservice.commons.gson.Exclude;
import br.com.zalf.prolog.webservice.commons.questoes.Alternativa;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.AlternativaChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.PerguntaRespostaChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.Checklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.TipoChecklist;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    @Nullable
    private Long codVersaoModeloChecklist;

    @NotNull
    private final Long codColaborador;

    @NotNull
    private final String cpfColaborador;

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
    private ChecklistInsercaoMetadata cachedMetadata;

    /**
     * Irá existir temporariamente, pois as integrações ainda usam os objetos antigos. Assim, como já criamos esse
     * objeto no Resource, podemos armazená-lo aqui evitando de realizaar uma nova conversão quando uma integração
     * precisar.
     */
    @Nullable
    private Checklist checklistAntigo;

    public ChecklistInsercao(@NotNull final Long codUnidade,
                             @NotNull final Long codModelo,
                             @NotNull final Long codVersaoModeloChecklist,
                             @NotNull final Long codColaborador,
                             @NotNull final String cpfColaborador,
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
        this.cpfColaborador = cpfColaborador;
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
        if (codVersaoModeloChecklist == null) {
            throw new IllegalStateException("Código da versão do modelo não pode ser null!");
        }

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
    public String getCpfColaborador() {
        return cpfColaborador;
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
        ensureMetadataCreated();
        return cachedMetadata.getQtdPerguntasOk();
    }

    public int getQtdPerguntasNok() {
        ensureMetadataCreated();
        return cachedMetadata.getQtdPerguntasNok();
    }

    public int getQtdAlternativasOk() {
        ensureMetadataCreated();
        return cachedMetadata.getQtdAlternativasOk();
    }

    public int getQtdAlternativasNok() {
        ensureMetadataCreated();
        return cachedMetadata.getQtdAlternativasNok();
    }

    public void setChecklistAntigo(@Nullable final Checklist checklistAntigo) {
        this.checklistAntigo = checklistAntigo;
    }

    @NotNull
    public static ChecklistInsercao createFrom(@NotNull final Checklist antigo,
                                               @NotNull final Integer versaoApp) {
        return new ChecklistInsercao(
                -1L /* antigo não tem unidade */,
                antigo.getCodModelo(),
                antigo.getCodVersaoModeloChecklist(),
                antigo.getColaborador().getCodigo() /* TODO: talvez não tenha */,
                antigo.getColaborador().getCpfAsString() /* antigo não tem codVeiculo */,
                -1L /* antigo não tem codVeiculo */,
                antigo.getPlacaVeiculo(),
                TipoChecklist.fromChar(antigo.getTipo()),
                antigo.getKmAtualVeiculo(),
                antigo.getTempoRealizacaoCheckInMillis(),
                convertRespostas(antigo.getListRespostas()),
                antigo.getData(),
                FonteDataHora.SERVIDOR,
                versaoApp,
                versaoApp,
                null,
                null,
                0,
                0);
    }

    @NotNull
    private static List<ChecklistResposta> convertRespostas(@NotNull final List<PerguntaRespostaChecklist> antigas) {
        final List<ChecklistResposta> respostas = new ArrayList<>();
        antigas
                .forEach(respostaAntiga -> {
                    final ChecklistResposta resposta = new ChecklistResposta();
                    resposta.setCodPergunta(respostaAntiga.getCodigo());
                    final List<ChecklistAlternativaResposta> alternativas = new ArrayList<>();
                    respostaAntiga
                            .getAlternativasResposta()
                            .forEach(alternativaAntiga -> {
                                final ChecklistAlternativaResposta alternativa = new ChecklistAlternativaResposta();
                                alternativa.setCodAlternativa(alternativaAntiga.getCodigo());
                                alternativa.setAlternativaSelecionada(alternativaAntiga.isSelected());
                                alternativa.setTipoOutros(alternativaAntiga.isTipoOutros());
                                alternativa.setRespostaTipoOutros(alternativaAntiga.getRespostaOutros());
                                alternativas.add(alternativa);
                            });
                    resposta.setAlternativasRespostas(alternativas);
                    respostas.add(resposta);
                });
        return respostas;
    }

    @NotNull
    public Checklist getChecklistAntigo() {
        if (checklistAntigo != null) {
            // Já foi convertido e setado no Service.
            return checklistAntigo;
        } else {
            checklistAntigo = new Checklist();
            checklistAntigo.setCodModelo(getCodModelo());
            checklistAntigo.setCodVersaoModeloChecklist(getCodVersaoModeloChecklist());
            final LocalDateTime now = Now.localDateTimeUtc();
            checklistAntigo.setData(now);
            checklistAntigo.setDataHoraImportadoProLog(now);
            checklistAntigo.setPlacaVeiculo(getPlacaVeiculo());
            checklistAntigo.setTipo(getTipo().asChar());
            checklistAntigo.setKmAtualVeiculo(getKmColetadoVeiculo());
            checklistAntigo.setTempoRealizacaoCheckInMillis(getTempoRealizacaoCheckInMillis());
            final Colaborador colaborador = new Colaborador();
            colaborador.setCodigo(getCodColaborador());
            colaborador.setCpf(Long.valueOf(getCpfColaborador()));
            checklistAntigo.setColaborador(colaborador);

            // Conversão das respostas.
            final List<PerguntaRespostaChecklist> respostas = new ArrayList<>();
            getRespostas()
                    .forEach(novaPergunta -> {
                final PerguntaRespostaChecklist resposta = new PerguntaRespostaChecklist();
                resposta.setCodigo(novaPergunta.getCodPergunta());
                final List<AlternativaChecklist> alternativas = new ArrayList<>();
                novaPergunta
                        .getAlternativasRespostas()
                        .forEach(novaAlternativa -> {
                    final AlternativaChecklist alternativa = new AlternativaChecklist();
                    alternativa.setSelected(novaAlternativa.isAlternativaSelecionada());
                    alternativa.setCodigo(novaAlternativa.getCodAlternativa());
                    if (novaAlternativa.isTipoOutros()) {
                        alternativa.setTipo(Alternativa.TIPO_OUTROS);
                        alternativa.setRespostaOutros(novaAlternativa.getRespostaTipoOutros());
                    }
                    alternativas.add(alternativa);
                });
                resposta.setAlternativasResposta(alternativas);
                respostas.add(resposta);
            });
            checklistAntigo.setListRespostas(respostas);

            // Antes de retornar, calcula as quantidades.
            checklistAntigo.calculaQtdOkOrNok();
            return checklistAntigo;
        }
    }

    private void ensureMetadataCreated() {
        //noinspection ConstantConditions
        if (cachedMetadata == null) {
            cachedMetadata = createMetadata();
        }
    }

    @NotNull
    private ChecklistInsercaoMetadata createMetadata() {
        int qtdPerguntasOk = 0;
        int qtdPerguntasNok = 0;
        int qtdAlternativasOk = 0;
        int qtdAlternativasNok = 0;
        boolean perguntaTeveAlternativasNok = false;
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < respostas.size(); i++) {
            final ChecklistResposta checklistResposta = respostas.get(i);
            final List<ChecklistAlternativaResposta> alternativasRespostas = checklistResposta.getAlternativasRespostas();
            //noinspection ForLoopReplaceableByForEach
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