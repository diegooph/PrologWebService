package br.com.zalf.prolog.webservice.v3.frota.checklist._model;

import br.com.zalf.prolog.webservice.commons.FonteDataHora;
import br.com.zalf.prolog.webservice.frota.checklist.model.TipoChecklist;
import io.swagger.annotations.ApiModel;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created on 2021-04-07
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@ApiModel(description = "Objeto com as informações de um checklist.")
@Value(staticConstructor = "of")
public class ChecklistListagemDto { //o que recebemos do banco
    @NotNull
    Long codUnidade;
    @NotNull
    Long codChecklist;
    @NotNull
    Long codModeloChecklist;
    @NotNull
    Long codVersaoModelo;
    @NotNull
    Long codColaborador;
    @NotNull
    Long cpfColaborador;
    @NotNull
    String nomeColaborador;
    @NotNull
    Long codVeiculo;
    @NotNull
    String placaVeiculo;
    @Nullable
    String identificadorFrota;
    long kmVeiculoMomentoRealizacao;
    @NotNull
    TipoChecklist tipoChecklist;
    @NotNull
    LocalDateTime dataHoraRealizacaoUtc;
    @NotNull
    LocalDateTime dataHoraRealizacaoTzAplicado;
    @Nullable
    LocalDateTime dataHoraImportadoUtc;
    @Nullable
    LocalDateTime dataHoraImportadoTzAplicado;
    long duracaoRealizacaoInMillis;
    @Nullable
    String observacaoChecklist;
    int totalPerguntasOk;
    int totalPerguntasNok;
    int totalAlternativasOk;
    int totalAlternativasNok;
    int totalImagensPerguntasOk;
    int totalMidiasPerguntasOk;
    int totalMidiasAlternativasNok;
    int totalNokBaixa;
    int totalNokAlta;
    int totalNokCritica;
    boolean isOffline;
    @NotNull
    LocalDateTime dataHoraSincronizacaoUtc;
    @NotNull
    LocalDateTime dataHoraSincronizacaoTzAplicado;
    @NotNull
    FonteDataHora fonteDataHora;
    @Nullable
    Integer versaoAppMomentoRealizacao;
    @Nullable
    Integer versaoAppMomentoSincronizacao;
    @Nullable
    String deviceId;
    @Nullable
    String deviceImei;
    long deviceUptimeRealizacaoMillis;
    long deviceUptimeSincronizacaoMillis;
    boolean temMidiaPerguntaOk;
    boolean temMidiaAlternativaNok;
    @Nullable
    List<ChecklistPerguntasDto> checklistPerguntasDtos;
}