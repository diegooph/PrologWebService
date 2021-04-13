package br.com.zalf.prolog.webservice.v3.frota.checklistordemservico._model;

import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.StatusItemOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.StatusOrdemServico;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public final class ChecklistOrdemServicoMapper {
    @NotNull
    public List<ChecklistOrdemServicoListagemDto> toDto(
            @NotNull final List<ChecklistOrdemServicoProjection> ordensServico,
            final boolean incluirItensOrdemServico) {
        if (ordensServico.isEmpty()) {
            return Collections.emptyList();
        }

        final List<ChecklistOrdemServicoListagemDto> ordensDto = new ArrayList<>();
        ordensServico.stream()
                .collect(Collectors.groupingBy(ChecklistOrdemServicoProjection::getCodigoOs))
                .forEach((codigoOs, checklistOrdemServicoProjections) -> ordensDto.add(
                        createChecklistOrdemServicoListagemDto(
                                checklistOrdemServicoProjections, incluirItensOrdemServico)));
        return ordensDto;
    }

    @NotNull
    private ChecklistOrdemServicoListagemDto createChecklistOrdemServicoListagemDto(
            @NotNull final List<ChecklistOrdemServicoProjection> checklistOrdemServicoProjections,
            final boolean incluirItensOrdemServico) {
        if (checklistOrdemServicoProjections.size() == 0) {
            throw new IllegalStateException("A lista usada neste método não pode ser vazia.");
        }
        return new ChecklistOrdemServicoListagemDto(
                checklistOrdemServicoProjections.get(0).getCodigoOs(),
                checklistOrdemServicoProjections.get(0).getCodigoUnidade(),
                checklistOrdemServicoProjections.get(0).getCodigoChecklist(),
                StatusOrdemServico.fromString(checklistOrdemServicoProjections.get(0).getStatusOs()),
                incluirItensOrdemServico
                        ? createChecklistOrdemServicoItens(checklistOrdemServicoProjections)
                        : null,
                checklistOrdemServicoProjections.get(0).getDataHoraFechamento());
    }

    @NotNull
    private List<ChecklistOrdemServicoItemDto> createChecklistOrdemServicoItens(
            @NotNull final List<ChecklistOrdemServicoProjection> checklistOrdemServicoProjections) {
        return checklistOrdemServicoProjections.stream()
                .map(this::createChecklistOrdemServicoItemDto)
                .collect(Collectors.toList());
    }

    @NotNull
    private ChecklistOrdemServicoItemDto createChecklistOrdemServicoItemDto(
            @NotNull final ChecklistOrdemServicoProjection checklistOrdemServicoProjection) {
        return new ChecklistOrdemServicoItemDto(
                checklistOrdemServicoProjection.getCodigoItemOs(),
                checklistOrdemServicoProjection.getCodigoOs(),
                checklistOrdemServicoProjection.getCodigoUnidade(),
                checklistOrdemServicoProjection.getCpfMecanico(),
                checklistOrdemServicoProjection.getCodigoPerguntaPrimeiroApontamento(),
                checklistOrdemServicoProjection.getCodigoContextoPergunta(),
                checklistOrdemServicoProjection.getCodigoAlternativaPrimeiroApontamento(),
                checklistOrdemServicoProjection.getCodigoContextoAlternativa(),
                checklistOrdemServicoProjection.getCodigoAuxiliarAlternativaPrimeiroApontamento(),
                StatusItemOrdemServico.fromString(checklistOrdemServicoProjection.getStatusItemOs()),
                checklistOrdemServicoProjection.getQuantidadeApontamentos(),
                checklistOrdemServicoProjection.getKm(),
                checklistOrdemServicoProjection.getCodigoAgrupamentoResolucaoEmLote(),
                checklistOrdemServicoProjection.getDataHoraConserto(),
                checklistOrdemServicoProjection.getDataHoraInicioResolucao(),
                checklistOrdemServicoProjection.getDataHoraFimResolucao(),
                checklistOrdemServicoProjection.getTempoRealizacao(),
                checklistOrdemServicoProjection.getFeedbackConserto());
    }
}
