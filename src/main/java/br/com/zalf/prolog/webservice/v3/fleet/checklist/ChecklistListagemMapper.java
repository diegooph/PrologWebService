package br.com.zalf.prolog.webservice.v3.fleet.checklist;

import br.com.zalf.prolog.webservice.v3.fleet.checklist._model.ChecklistAlternativaDto;
import br.com.zalf.prolog.webservice.v3.fleet.checklist._model.ChecklistListagemDto;
import br.com.zalf.prolog.webservice.v3.fleet.checklist._model.ChecklistPerguntasDto;
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
public class ChecklistListagemMapper {
    @NotNull
    public List<ChecklistListagemDto> toDto(@NotNull final List<ChecklistProjection> checklistsProjection,
                                            final boolean incluirRespostas) {
        final List<ChecklistListagemDto> listagemDtos = new ArrayList<>();
        checklistsProjection.stream()
                .collect(Collectors.groupingBy(ChecklistProjection::getCodChecklist))
                .forEach((codChecklist, checklistProjections) ->
                                 listagemDtos.add(toChecklistListagemDto(checklistProjections, incluirRespostas)));
        return listagemDtos;
    }

    @NotNull
    private ChecklistListagemDto toChecklistListagemDto(@NotNull final List<ChecklistProjection> checklistProjections,
                                                        final boolean incluirRespostas) {
        final ChecklistProjection projection = checklistProjections.get(0);
        return ChecklistListagemDto.of(projection.getCodChecklist(),
                                       projection.getCodUnidade(),
                                       projection.getCodModeloChecklist(),
                                       projection.getCodVersaoModelo(),
                                       projection.getCodColaborador(),
                                       projection.getCpfColaborador(),
                                       projection.getNomeColaborador(),
                                       projection.getCodVeiculo(),
                                       projection.getPlacaVeiculo(),
                                       projection.getIdentificadorFrota(),
                                       projection.getKmVeiculoMomentoRealizacao(),
                                       projection.getTipoChecklist(),
                                       projection.getDataHoraRealizacaoUtc(),
                                       projection.getDataHoraRealizacaoTzAplicado(),
                                       projection.getDataHoraImportadoUtc(),
                                       projection.getDataHoraImportadoTzAplicado(),
                                       projection.getDuracaoRealizacaoInMillis(),
                                       projection.getObservacaoChecklist(),
                                       projection.getTotalPerguntasOk(),
                                       projection.getTotalPerguntasNok(),
                                       projection.getTotalAlternativasOk(),
                                       projection.getTotalAlternativasNok(),
                                       projection.getTotalMidiasPerguntasOk(),
                                       projection.getTotalMidiasAlternativasNok(),
                                       projection.getTotalNokBaixa(),
                                       projection.getTotalNokAlta(),
                                       projection.getTotalNokCritica(),
                                       projection.isOffline(),
                                       projection.getDataHoraSincronizacaoUtc(),
                                       projection.getDataHoraSincronizacaoTzAplicado(),
                                       projection.getFonteDataHora(),
                                       projection.getVersaoAppMomentoRealizacao(),
                                       projection.getVersaoAppMomentoSincronizacao(),
                                       projection.getDeviceId(),
                                       projection.getDeviceImei(),
                                       projection.getDeviceUptimeRealizacaoMillis(),
                                       projection.getDeviceUptimeSincronizacaoMillis(),
                                       incluirRespostas ? toChecklistPerguntaDto(checklistProjections) : null);
    }

    @NotNull
    private List<ChecklistPerguntasDto> toChecklistPerguntaDto(
            @NotNull final List<ChecklistProjection> checklistProjection) {
        final List<ChecklistPerguntasDto> perguntasDto = new ArrayList<>();
        checklistProjection.stream()
                .collect(Collectors.groupingBy(ChecklistProjection::getCodPergunta))
                .forEach((codPergunta, perguntasProjections) ->
                                 perguntasDto.add(createChecklistPerguntasDto(perguntasProjections)));
        return perguntasDto;
    }

    @NotNull
    private ChecklistPerguntasDto createChecklistPerguntasDto(
            @NotNull final List<ChecklistProjection> perguntasProjection) {
        final ChecklistProjection perguntaProjection = perguntasProjection.get(0);
        return ChecklistPerguntasDto.of(perguntaProjection.getCodPergunta(),
                                        perguntaProjection.getCodContextoPergunta(),
                                        perguntaProjection.getDescricaoPergunta(),
                                        perguntaProjection.getOrdemPergunta(),
                                        perguntaProjection.isPerguntaSingleChoice(),
                                        perguntaProjection.getAnexoMidiaPerguntaOk(),
                                        toChecklistAlternativaDto(perguntasProjection));
    }

    @NotNull
    private List<ChecklistAlternativaDto> toChecklistAlternativaDto(
            @NotNull final List<ChecklistProjection> alternativasProjection) {
        final List<ChecklistAlternativaDto> alternativasDto = new ArrayList<>();
        alternativasProjection.forEach(alternativa -> alternativasDto.add(createChecklistAlternativasDto(alternativa)));
        return alternativasDto;
    }

    @NotNull
    private ChecklistAlternativaDto createChecklistAlternativasDto(@NotNull final ChecklistProjection alternativa) {
        return ChecklistAlternativaDto.of(alternativa.getCodAlternativa(),
                                          alternativa.getCodContextoAlternativa(),
                                          alternativa.getDescricaoAlternativa(),
                                          alternativa.getOrdemAlternativa(),
                                          alternativa.getPrioridadeAlternativa(),
                                          alternativa.getAlternativaTipoOutros(),
                                          alternativa.deveAbrirOrdemServico(),
                                          alternativa.getAnexoMidiaAlternativaNok(),
                                          alternativa.getCodAuxiliarAlternativa(),
                                          alternativa.isAlternativaSelecionada(),
                                          alternativa.getRespostaTipoOutros());
    }
}

