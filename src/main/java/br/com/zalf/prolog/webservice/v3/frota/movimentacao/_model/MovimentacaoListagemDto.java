package br.com.zalf.prolog.webservice.v3.frota.movimentacao._model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created on 2021-04-26
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Data
public final class MovimentacaoListagemDto {
    @ApiModelProperty(value = "Código da movimentação.", required = true, example = "12345")
    @NotNull
    private final Long codMovimentacao;
    @ApiModelProperty(value = "Código do pneu movimentado.", required = true, example = "12345")
    @NotNull
    private final Long codPneu;
    @ApiModelProperty(value = "Código de cliente do pneu movimentado. Usualmente se trata do número de fogo do pneu.",
                      required = true,
                      example = "PN0001")
    @NotNull
    private final String codigoClientePneu;
    @ApiModelProperty(value = "Código da dimensão do pneu.", required = true, example = "2")
    @NotNull
    private final Long codDimensaoPneu;
    @ApiModelProperty(value = "Vida do pneu no momento que a movimentação foi realizada. No Prolog, a vida do pneu é " +
            "um número de 1 à 11 onde 1 é usado para o pneu novo, nunca recapado.",
                      example = "1")
    @NotNull
    private final Integer vidaPneuMomentoMovimentacao;
    @ApiModelProperty(value = "Sulco interno no momento da movimentação. Sulco interno é identificado como o sulco " +
            "que fica mais próximo ao centro do veículo.",
                      example = "15.2")
    @Nullable
    private final Double sulcoInternoMomentoMovimentacaoEmMilimetros;
    @ApiModelProperty(value = "Sulco central interno no momento da movimentação. Sulco central interno é identificado" +
            " como o sulco que fica entre o sulco central externo e o interno.",
                      example = "15.2")
    @Nullable
    private final Double sulcoCentralInternoMomentoMovimentacaoEmMilimetros;
    @ApiModelProperty(value = "Sulco central externo no momento da movimentação. Sulco central externo é identificado" +
            " como o sulco que fica entre o sulco externo e o central interno.",
                      example = "15.2")
    @Nullable
    private final Double sulcoCentralExternoMomentoMovimentacaoEmMilimetros;
    @ApiModelProperty(value = "Sulco externo no momento da movimentação. Sulco externo é identificado como o sulco " +
            "que fica mais afastado do centro do veículo.",
                      example = "15.2")
    @Nullable
    private final Double sulcoExternoMomentoMovimentacaoEmMilimetros;
    @ApiModelProperty(value = "Pressão no momento da movimentação.",
                      example = "120.0")
    @Nullable
    private final Double pressaoMomentoMovimentacaoEmPsi;
    @ApiModelProperty(value = "Tipo da origem da movimentação - de onde o pneu saiu. Os tipos de movimentação " +
            "incluem: EM_USO - quando o pneu está em um veículo, ESTOQUE - quando o pneu está no estoque, ANALISE - " +
            "quando o pneu está em conserto/recapagem e DESCARTE - caso o pneu esteja descartado.",
                      required = true,
                      example = "EM_USO")
    @NotNull
    private final String tipoOrigem;
    @ApiModelProperty(value = "Posição onde o pneu está aplicado no veículo. Só será enviado caso o pneu esteja em um" +
            " veículo.",
                      example = "111")
    @Nullable
    private final Long posicaoPneuOrigem;
    @ApiModelProperty(value = "Tipo da origem da movimentação - para onde o pneu foi. Os tipos de movimentação " +
            "incluem: EM_USO - quando o pneu está em um veículo, ESTOQUE - quando o pneu está no estoque, ANALISE - " +
            "quando o pneu está em conserto/recapagem e DESCARTE - caso o pneu esteja descartado.",
                      required = true,
                      example = "EM_USO")
    @NotNull
    private final String tipoDestino;
    @ApiModelProperty(value = "Posição onde o pneu foi aplicado no veículo. Só será enviado caso o pneu esteja em um" +
            " veículo.",
                      example = "111")
    @Nullable
    private final Long posicaoPneuDestino;
    @ApiModelProperty(value = "Observação inserida pelo colaborador para a movimentação.",
                      example = "Rodízio do pneu no veículo")
    @Nullable
    private final String observacaoMovimentacao;
    @ApiModelProperty(value = "Código do motivo do descarte do pneu. Valor presente apenas se tipoDestino = DESCARTE",
                      example = "15")
    @Nullable
    private final Long codMotivoDescarte;
    @ApiModelProperty(value = "Foto capturada no momento do descarte do pneu. Valor presente apenas se tipoDestino = " +
            "DESCARTE. A captura de fotos é opcional.",
                      example = "https://s3.amazonaws.com/descarte/1601906199267.jpeg")
    @Nullable
    private final String urlImagemDescarte1;
    @ApiModelProperty(value = "Foto capturada no momento do descarte do pneu. Valor presente apenas se tipoDestino = " +
            "DESCARTE. A captura de fotos é opcional.",
                      example = "https://s3.amazonaws.com/descarte/1601906199267.jpeg")
    @Nullable
    private final String urlImagemDescarte2;
    @ApiModelProperty(value = "Foto capturada no momento do descarte do pneu. Valor presente apenas se tipoDestino = " +
            "DESCARTE. A captura de fotos é opcional.",
                      example = "https://s3.amazonaws.com/descarte/1601906199267.jpeg")
    @Nullable
    private final String urlImagemDescarte3;
    @ApiModelProperty(value = "Código da recapadora. Valor presente apenas se tipoDestino = ANALISE.",
                      example = "15")
    @Nullable
    private final Long codRecapadora;
    @ApiModelProperty(value = "Nome da recapadora. Valor presente apenas se tipoDestino = ANALISE.",
                      example = "15")
    @Nullable
    private final String nomeRecapadora;
    @ApiModelProperty(value = "Código de coleta. Valor presente apenas se tipoDestino = ANALISE.",
                      example = "15273")
    @Nullable
    private final String codColeta;
    @ApiModelProperty(value = "Serviços realizados no pneu. Valor presente apenas se tipoOrigem = ANALISE e " +
            "tipoOrigem = ANALISE.")
    @Nullable
    private final List<MovimentacaoPneuServicoRealizadoDto> servicosRealizados;
}
