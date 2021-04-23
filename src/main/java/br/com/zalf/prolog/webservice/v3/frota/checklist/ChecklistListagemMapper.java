package br.com.zalf.prolog.webservice.v3.frota.checklist;

import br.com.zalf.prolog.webservice.v3.frota.checklist._model.ChecklistListagemDto;
import br.com.zalf.prolog.webservice.v3.frota.checklist._model.ChecklistPerguntasDto;
import br.com.zalf.prolog.webservice.v3.frota.checklist._model.ChecklistProjection;
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
        return ChecklistListagemDto.of(projection.getCodUnidade(),
                                       projection.getCodChecklist(),
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
                                       projection.getTotalImagensPerguntasOk(),
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
                                       projection.temMidiaPerguntaOk(),
                                       projection.temMidiaAlternativaNok(),
                                       incluirRespostas ? toChecklistPerguntaDto(projection) : null);
    }

    @NotNull
    private List<ChecklistPerguntasDto> toChecklistPerguntaDto(@NotNull final ChecklistProjection projection) {
        return null;
    }
}
