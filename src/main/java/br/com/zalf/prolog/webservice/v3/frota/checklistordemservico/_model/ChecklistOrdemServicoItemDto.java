package br.com.zalf.prolog.webservice.v3.frota.checklistordemservico._model;

import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.StatusItemOrdemServico;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

@Data
public class ChecklistOrdemServicoItemDto {
    @ApiModelProperty(
            value = "Código do item de ordem de serviço.",
            example = "2")
    private final long codigoItemOs;
    @ApiModelProperty(
            value = "Código da ordem de serviço.",
            example = "23")
    private final long codigoOs;
    @ApiModelProperty(
            value = "Código da unidade a que o item da ordem de serviço pertence.",
            example = "215")
    private final long codUnidade;
    @ApiModelProperty(
            value = "Cpf do mecânico que fechou o item de ordem de serviço.",
            example = "97599336087")
    @Nullable
    private final Long cpfMecanico;
    @ApiModelProperty(
            value = "Código da pergunta que abriu a ordem de serviço.",
            example = "3")
    private final long codigoPerguntaPrimeiroApontamento;
    @ApiModelProperty(
            value = "Código que muda apenas qunado o contexto ou significado da pergunta é altrado.",
            example = "5")
    private final long codigoContextoPergunta;
    @ApiModelProperty(
            value = "Código da alternativa que abriu a ordem de serviço.",
            example = "7")
    private final long codigoAlternativaPrimeiroApontamento;
    @ApiModelProperty(
            value = "Código que muda apenas qunado o contexto ou significado da alternativa é alterado.",
            example = "9")
    private final long codigoContextoAlternativa;
    @ApiModelProperty(
            value = "Código auxiliar da alternativa que abriu a ordem de serviço.",
            example = "AA:BB")
    @Nullable
    private final String codAuxiliarAlternativaPrimeiroApontamento;
    @ApiModelProperty(
            value = "Status do item da ordem de serviço: aberto ou fechado.",
            example = "F")
    @NotNull
    private final StatusItemOrdemServico statusItemOs;
    @ApiModelProperty(
            value = "Quantidade de vezes que esse item de ordem de serviço já foi apontado.",
            example = "45")
    private final int quantidadeApontamentos;
    @ApiModelProperty(
            value = "Quilometragem do veículo quando o item de ordem de serviço foi criado.",
            example = "53246")
    @Nullable
    private final Long km;
    @ApiModelProperty(
            value = "Código do processo de fechamento de itens de ordem de serviço, " +
                    "quando vários itens são fechados em lote.",
            example = "84")
    @Nullable
    private final Long codigoAgrupamentoFechamentoEmLote;
    @ApiModelProperty(
            value = "A data e a hora em que foi apontado que o conserto necessário no item da " +
                    "ordem de serviço foi realizado, em utc.",
            example = "2019-08-18T13:47:00")
    @Nullable
    private final LocalDateTime dataHoraConsertoUtc;
    @ApiModelProperty(
            value = "A data e a hora em que foi apontado que o conserto necessário no item da " +
                    "ordem de serviço foi realizado, com timezone do mecânico aplicado.",
            example = "2019-08-18T10:47:00")
    @Nullable
    private final LocalDateTime dataHoraConsertoTimezoneAplicado;
    @ApiModelProperty(
            value = "A data e a hora em que foi iniciado o conserto necessário no item da ordem de serviço, em utc.",
            example = "2019-08-18T13:47:00")
    @Nullable
    private final LocalDateTime dataHoraInicioResolucaoUtc;
    @ApiModelProperty(
            value = "A data e a hora em que foi iniciado o conserto necessário no item da ordem de serviço, " +
                    "com timezone do mecânico aplicado.",
            example = "2019-08-18T10:47:00")
    @Nullable
    private final LocalDateTime dataHoraInicioResolucaoTimezoneAplicado;
    @ApiModelProperty(
            value = "A data e a hora em que foi finalizado o conserto necessário no item da ordem de serviço, em utc.",
            example = "2019-08-18T13:47:00")
    @Nullable
    private final LocalDateTime dataHoraFimResolucaoUtc;
    @ApiModelProperty(
            value = "A data e a hora em que foi finalizado o conserto necessário no item da ordem de serviço, " +
                    "com timezone do mecânico aplicado.",
            example = "2019-08-18T10:47:00")
    @Nullable
    private final LocalDateTime dataHoraFimResolucaoTimezoneAplicado;
    @ApiModelProperty(
            value = "O tempo total para realizar o conserto necessário, em milissegundos.",
            example = "100000")
    @Nullable
    private final Long tempoRealizacao;
    @ApiModelProperty(
            value = "Uma observação referente o conserto..",
            example = "O pneu só irá conseguir rodar mais 10000 KM.")
    @Nullable
    private final String feedbackConserto;
}
