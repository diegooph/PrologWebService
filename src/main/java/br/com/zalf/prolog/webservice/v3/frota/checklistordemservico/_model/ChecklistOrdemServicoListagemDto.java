package br.com.zalf.prolog.webservice.v3.frota.checklistordemservico._model;

import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.StatusOrdemServico;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ChecklistOrdemServicoListagemDto {
    private final long codigoOs;
    private final long codUnidade;
    private final long codChecklistAberturaOs;
    @NotNull
    private final StatusOrdemServico statusOs;
    @NotNull
    private final List<ChecklistOrdemServicoItemDto> itensOs;
    @Nullable
    private LocalDateTime dataHoraFechamentoOs;
}
