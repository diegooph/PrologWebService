package br.com.zalf.prolog.webservice.v3.frota.checklist._model;

import br.com.zalf.prolog.webservice.commons.FonteDataHora;
import br.com.zalf.prolog.webservice.frota.checklist.model.PrioridadeAlternativa;
import br.com.zalf.prolog.webservice.frota.checklist.model.TipoChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.AnexoMidiaChecklistEnum;
import io.swagger.annotations.ApiModel;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

/**
 * Created on 2021-04-07
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@ApiModel(description = "Objeto com as informações de um checklist.")
@Value(staticConstructor = "of")
public class ChecklistListagemDto {
    @NotNull
    Long codUnidade;
    @NotNull
    Long codChecklist;
    @NotNull
    Long codModeloChecklist;
    @NotNull
    Long codVersaoModelo;
    @NotNull
    Long getCodColaborador;
    @NotNull
    Long getCpfColaborador;
    @NotNull
    String getNomeColaborador;
    @NotNull
    Long getCodVeiculo;
    @NotNull
    String getPlacaVeiculo;
    @Nullable
    String getIdentificadorFrota;
    long getKmVeiculoMomentoRealizacao;
    @NotNull
    TipoChecklist getTipoChecklist;
    @NotNull
    LocalDateTime getDataHoraRealizacaoUtc;
    @NotNull
    LocalDateTime getDataHoraRealizacaoTzAplicado;
    @Nullable
    LocalDateTime getDataHoraImportadoUtc;
    @Nullable
    LocalDateTime getDataHoraImportadoTzAplicado;
    long getDuracaoRealizacaoInMillis;
    @Nullable
    String getObservacaoChecklist;
    int getTotalPerguntasOk;
    int getTotalPerguntasNok;
    int getTotalAlternativasOk;
    int getTotalAlternativasNok;
    int getTotalImagensPerguntasOk;
    int getTotalMidiasPerguntasOk;
    int getTotalMidiasAlternativasNok;
    int getTotalNokBaixa;
    int getTotalNokAlta;
    int getTotalNokCritica;
    boolean isOffline;
    @NotNull
    LocalDateTime getDataHoraSincronizacaoUtc;
    @NotNull
    LocalDateTime getDataHoraSincronizacaoTzAplicado;
    @NotNull
    FonteDataHora getFonteDataHora;
    @Nullable
    Integer getVersaoAppMomentoRealizacao;
    @Nullable
    Integer getVersaoAppMomentoSincronizacao;
    @Nullable
    String getDeviceId;
    @Nullable
    String getDeviceImei;
    long getDeviceUptimeRealizacaoMillis;
    long getDeviceUptimeSincronizacaoMillis;
    @NotNull
    Long getCodPergunta;
    @NotNull
    Long getCodContextoPergunta;
    @NotNull
    String getDescricaoPergunta;
    int getOrdemPergunta;
    boolean isPerguntaSingleChoice;
    @NotNull
    AnexoMidiaChecklistEnum getAnexoMidiaPerguntaOk;
    @NotNull
    Long getCodAlternativa;
    @NotNull
    Long getCodContextoAlternativa;
    @NotNull
    String getDescricaoAlternativa;
    int getOrdemAlternativa;
    @NotNull
    PrioridadeAlternativa getPrioridadeAlternativa;
    boolean getAlternativaTipoOutros;
    boolean deveAbrirOrdemServico;
    @NotNull
    AnexoMidiaChecklistEnum getAnexoMidiaAlternativaNok;
    @Nullable
    String getCodAuxiliarAlternativa;
    boolean isAlternativaSelecionada;
    @Nullable
    String getRespostaTipoOutros;
    boolean temMidiaPerguntaOk;
    boolean temMidiaAlternativaNok;
}