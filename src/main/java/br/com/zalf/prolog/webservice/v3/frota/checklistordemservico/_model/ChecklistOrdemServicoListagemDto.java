package br.com.zalf.prolog.webservice.v3.frota.checklistordemservico._model;

import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.StatusOrdemServico;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ChecklistOrdemServicoListagemDto {
    private final long codigoOs;
    private final long codUnidade;
    private final long codChecklistAberturaOs;
    private final StatusOrdemServico statusOs;
    private final List<ChecklistOrdemServicoItem> itensOs;
    private LocalDateTime dataHoraFechamentoOs;
}
