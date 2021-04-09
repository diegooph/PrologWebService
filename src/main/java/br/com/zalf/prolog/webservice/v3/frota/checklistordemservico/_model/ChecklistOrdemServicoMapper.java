package br.com.zalf.prolog.webservice.v3.frota.checklistordemservico._model;

import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.StatusItemOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.StatusOrdemServico;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public final class ChecklistOrdemServicoMapper {
    @NotNull
    public List<ChecklistOrdemServicoListagemDto> toDto(
            @NotNull final List<ChecklistOrdemServicoProjection> ordensServico) {
        if (ordensServico.isEmpty()) {
            return Collections.emptyList();
        }

        final List<ChecklistOrdemServicoListagemDto> ordensDto = new ArrayList<>();
        long osAntiga = 0;
        for (final ChecklistOrdemServicoProjection osProjection : ordensServico) {
            if (osAntiga != osProjection.getCodigoOs()) {
                ordensDto.add(new ChecklistOrdemServicoListagemDto(osProjection.getCodigoOs(),
                        osProjection.getCodigoUnidade(),
                        osProjection.getCodigoChecklist(),
                        StatusOrdemServico.fromString(osProjection.getStatusOs()),
                        new ArrayList<>(),
                        osProjection.getDataHoraFechamento()));
            }
            ordensDto.get(ordensDto.size() - 1).getItensOs().add(new ChecklistOrdemServicoItemDto(osProjection.getCodigoItemOs(),
                    osProjection.getCodigoOs(),
                    osProjection.getCodigoUnidade(),
                    osProjection.getCpfMecanico(),
                    osProjection.getCodigoPerguntaPrimeiroApontamento(),
                    osProjection.getCodigoContextoPergunta(),
                    osProjection.getCodigoAlternativaPrimeiroApontamento(),
                    osProjection.getCodigoContextoAlternativa(),
                    StatusItemOrdemServico.fromString(osProjection.getStatusItemOs()),
                    osProjection.getQuantidadeApontamentos(),
                    osProjection.getKm(),
                    osProjection.getCodigoAgrupamentoResolucaoEmLote(),
                    osProjection.getDataHoraConserto(),
                    osProjection.getDataHoraInicioResolucao(),
                    osProjection.getDataHoraFimResolucao(),
                    osProjection.getTempoRealizacao(),
                    osProjection.getFeedbackConserto()));
            osAntiga = osProjection.getCodigoOs();
        }
        return ordensDto;
    }
}
