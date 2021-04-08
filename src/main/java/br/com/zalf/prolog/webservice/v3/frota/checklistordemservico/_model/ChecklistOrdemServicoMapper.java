package br.com.zalf.prolog.webservice.v3.frota.checklistordemservico._model;

import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.StatusOrdemServico;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public final class ChecklistOrdemServicoMapper {
    @NotNull
    public List<ChecklistOrdemServicoListagemDto> toDto(
            @NotNull final List<ChecklistOrdemServicoProjection> ordensServico) {
        return ordensServico
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @NotNull
    private ChecklistOrdemServicoListagemDto toDto(@NotNull final ChecklistOrdemServicoProjection projection) {
        return new ChecklistOrdemServicoListagemDto(projection.getCodigoOs(),
                projection.getCodigoUnidade(),
                projection.getCodigoChecklist(),
                StatusOrdemServico.fromString(projection.getStatusOs()),
                projection.getDataHoraFechamento());
    }
}
