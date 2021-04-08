package br.com.zalf.prolog.webservice.v3.frota.checklistordemservico._model;

import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.StatusItemOrdemServico;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

@Data
public class ChecklistOrdemServicoItemDto {
    private final long codigoItemOs;
    private final long codigoOs;
    private final long codUnidade;
    @Nullable
    private final Long cpfMecanico;
    private final long codigoPerguntaPrimeiroApontamento;
    private final long codigoContextoPergunta;
    private final long codigoAlternativaPrimeiroApontamento;
    private final long codigoContextoAlternativa;
    @NotNull
    private final StatusItemOrdemServico statusItemOs;
    private final int quantidadeApontamentos;
    @Nullable
    private final Long km;
    @Nullable
    private final Long codigoProcessoFechamentoItensOs;
    @Nullable
    private final LocalDateTime dataHoraConserto;
    @Nullable
    private final LocalDateTime dataHoraInicioResolucao;
    @Nullable
    private final LocalDateTime dataHoraFimResolucao;
    @Nullable
    private final Long tempoRealizacao;
    @Nullable
    private final String feedbackConserto;
}
