package br.com.zalf.prolog.webservice.v3.fleet.checklist;

import br.com.zalf.prolog.webservice.v3.fleet.checklist._model.ChecklistAnswerOptionDto;
import br.com.zalf.prolog.webservice.v3.fleet.checklist._model.ChecklistDto;
import br.com.zalf.prolog.webservice.v3.fleet.checklist._model.ChecklistItemDto;
import br.com.zalf.prolog.webservice.v3.fleet.checklist._model.ChecklistProjection;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created on 2021-04-07
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Component
public class ChecklistMapper {
    @NotNull
    public List<ChecklistDto> toDto(@NotNull final List<ChecklistProjection> checklistsProjection,
                                    final boolean includeAnswers) {
        final List<ChecklistDto> checklists = new ArrayList<>();
        checklistsProjection.stream()
                .collect(Collectors.groupingBy(ChecklistProjection::getCodChecklist))
                .forEach((checklistId, checklistProjections) ->
                                 checklists.add(toChecklistDto(checklistProjections, includeAnswers)));
        return checklists;
    }

    @NotNull
    private ChecklistDto toChecklistDto(@NotNull final List<ChecklistProjection> checklistProjections,
                                        final boolean includeAnswers) {
        final ChecklistProjection checklist = checklistProjections.get(0);
        return ChecklistDto.of(checklist.getCodChecklist(),
                               checklist.getCodUnidade(),
                               checklist.getCodModeloChecklist(),
                               checklist.getCodVersaoModelo(),
                               checklist.getCodColaborador(),
                               checklist.getCpfColaborador(),
                               checklist.getNomeColaborador(),
                               checklist.getCodVeiculo(),
                               checklist.getPlacaVeiculo(),
                               checklist.getIdentificadorFrota(),
                               checklist.getKmVeiculoMomentoRealizacao(),
                               checklist.getTipoChecklist(),
                               checklist.getDataHoraRealizacaoUtc(),
                               checklist.getDataHoraRealizacaoTzAplicado(),
                               checklist.getDataHoraImportadoUtc(),
                               checklist.getDataHoraImportadoTzAplicado(),
                               checklist.getDuracaoRealizacaoInMillis(),
                               checklist.getObservacaoChecklist(),
                               checklist.getTotalPerguntasOk(),
                               checklist.getTotalPerguntasNok(),
                               checklist.getTotalAlternativasOk(),
                               checklist.getTotalAlternativasNok(),
                               checklist.getTotalMidiasPerguntasOk(),
                               checklist.getTotalMidiasAlternativasNok(),
                               checklist.getTotalNokBaixa(),
                               checklist.getTotalNokAlta(),
                               checklist.getTotalNokCritica(),
                               checklist.isOffline(),
                               checklist.getDataHoraSincronizacaoUtc(),
                               checklist.getDataHoraSincronizacaoTzAplicado(),
                               checklist.getFonteDataHora(),
                               checklist.getVersaoAppMomentoRealizacao(),
                               checklist.getVersaoAppMomentoSincronizacao(),
                               checklist.getDeviceId(),
                               checklist.getDeviceImei(),
                               checklist.getDeviceUptimeRealizacaoMillis(),
                               checklist.getDeviceUptimeSincronizacaoMillis(),
                               includeAnswers ? toChecklistItemDto(checklistProjections) : null);
    }

    @NotNull
    private List<ChecklistItemDto> toChecklistItemDto(@NotNull final List<ChecklistProjection> checklistProjection) {
        final List<ChecklistItemDto> items = new ArrayList<>();
        checklistProjection.stream()
                .collect(Collectors.groupingBy(ChecklistProjection::getCodPergunta))
                .forEach((itemId, itemsProjections) -> items.add(createChecklistItemDto(itemsProjections)));
        return items;
    }

    @NotNull
    private ChecklistItemDto createChecklistItemDto(@NotNull final List<ChecklistProjection> itemsProjection) {
        final ChecklistProjection itemProjection = itemsProjection.get(0);
        return ChecklistItemDto.of(itemProjection.getCodPergunta(),
                                   itemProjection.getCodContextoPergunta(),
                                   itemProjection.getDescricaoPergunta(),
                                   itemProjection.getOrdemPergunta(),
                                   itemProjection.isPerguntaSingleChoice(),
                                   itemProjection.getAnexoMidiaPerguntaOk(),
                                   toChecklistAnswerOptionDto(itemsProjection));
    }

    @NotNull
    private List<ChecklistAnswerOptionDto> toChecklistAnswerOptionDto(
            @NotNull final List<ChecklistProjection> answerOptionsProjection) {
        final List<ChecklistAnswerOptionDto> answerOptionsDto = new ArrayList<>();
        answerOptionsProjection.forEach(answerOption ->
                                                answerOptionsDto.add(createChecklistAnswerOptionDto(answerOption)));
        return answerOptionsDto;
    }

    @NotNull
    private ChecklistAnswerOptionDto createChecklistAnswerOptionDto(@NotNull final ChecklistProjection answerOption) {
        return ChecklistAnswerOptionDto.of(answerOption.getCodAlternativa(),
                                           answerOption.getCodContextoAlternativa(),
                                           answerOption.getDescricaoAlternativa(),
                                           answerOption.getOrdemAlternativa(),
                                           answerOption.getPrioridadeAlternativa(),
                                           answerOption.getAlternativaTipoOutros(),
                                           answerOption.deveAbrirOrdemServico(),
                                           answerOption.getAnexoMidiaAlternativaNok(),
                                           answerOption.getCodAuxiliarAlternativa(),
                                           answerOption.isAlternativaSelecionada(),
                                           answerOption.getRespostaTipoOutros());
    }
}

