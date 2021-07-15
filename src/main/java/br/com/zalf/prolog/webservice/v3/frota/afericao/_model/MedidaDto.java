package br.com.zalf.prolog.webservice.v3.frota.afericao._model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 2021-04-12
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Value(staticConstructor = "of")
public class MedidaDto {
    @ApiModelProperty(value = "Código do pneu aferido.", required = true, example = "12345")
    @NotNull
    Long codPneuAfericao;
    @ApiModelProperty(value = "Código de cliente do pneu aferido. Usualmente se trata do número de fogo do pneu.",
                      required = true,
                      example = "PN0001")
    @NotNull
    String codigoClientePneuAfericao;
    @ApiModelProperty(value = "Posição em que o pneu estava aplicado no momento da aferição. Essa informação não será" +
            " enviada no caso de o pneu estar em estoque.",
                      example = "111")
    @NotNull
    Integer posicaoPneuAplicadoMomentoAfericao;
    @ApiModelProperty(value = "Vida do pneu no momento que a aferição foi realizada. No Prolog, a vida do pneu é um " +
            "número de 1 à 11 onde 1 é usado para o pneu novo, nunca recapado.",
                      example = "1")
    @NotNull
    Integer vidaPneuMomentoAfericao;
    @ApiModelProperty(value = "Pressão coletada. Esse valor pode não ser fornecido no caso de uma aferição apenas de " +
            "SULCO. Esse valor é expresso em PSI.",
                      example = "120.0")
    @Nullable
    Double pressaoPneuEmPsi;
    @ApiModelProperty(value = "Sulco interno coletado. Sulco interno é identificado como o sulco que fica mais " +
            "próximo ao centro do veículo. Caso for uma aferição apenas de PRESSAO, esse valor não será informado.",
                      example = "15.2")
    @Nullable
    Double alturaSulcoInternoEmMilimetros;
    @ApiModelProperty(value = "Sulco central interno coletado. Sulco central interno é identificado como o sulco que " +
            "fica entre o sulco central externo e o interno. Caso for uma aferição apenas de PRESSAO, esse valor não " +
            "será informado. Em pneu que têm apenas 3 sulcos, esse valor será igual ao sulco central externo.",
                      example = "15.2")
    @Nullable
    Double alturaSulcoCentralInternoEmMilimetros;
    @ApiModelProperty(value = "Sulco central externo coletado. Sulco central externo é identificado como o sulco que " +
            "fica entre o sulco externo e o central interno. Caso for uma aferição apenas de PRESSAO, esse valor não " +
            "será informado. Em pneu que têm apenas 3 sulcos, esse valor será igual ao sulco central interno.",
                      example = "15.2")
    @Nullable
    Double alturaSulcoCentralExternoEmMilimetros;
    @ApiModelProperty(value = "Sulco externo coletado. Sulco interno é identificado como o sulco que fica mais " +
            "afastado do centro do veículo. Caso for uma aferição apenas de PRESSAO, esse valor não será informado.",
                      example = "15.2")
    @Nullable
    Double alturaSulcoExternoEmMilimetros;
}
