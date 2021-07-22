package br.com.zalf.prolog.webservice.v3.fleet.tire._model;

import br.com.zalf.prolog.webservice.frota.pneu._model.StatusPneu;
import io.swagger.annotations.ApiModelProperty;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;

@Value(staticConstructor = "of")
public class TireDto {
    @ApiModelProperty(value = "Código do pneu.",
                      notes = "Este código não é o número de fogo do pneu",
                      required = true,
                      example = "12345")
    @NotNull
    Long tireId;
    @ApiModelProperty(value = "Código de cliente do pneu. Usualmente se trata do número de fogo do pneu.",
                      required = true,
                      example = "PN0001")
    @NotNull
    String tireClientNumber;
    @ApiModelProperty(value = "Código do grupo da unidade.", required = true, example = "1")
    @NotNull
    Long groupId;
    @ApiModelProperty(value = "Nome do grupo da unidade.", required = true, example = "Sudeste")
    @NotNull
    String groupName;
    @ApiModelProperty(value = "Código da unidade onde o pneu está alocado.", required = true, example = "215")
    @NotNull
    Long branchId;
    @ApiModelProperty(value = "Nome da unidade onde o pneu está alocado.",
                      required = true,
                      example = "Unidade de testes")
    @NotNull
    String branchName;
    @ApiModelProperty(value = "Vida atual do pneu. No Prolog, a vida do pneu é um número de 1 à 11 onde 1 é usado " +
            "para o pneu novo, nunca recapado.",
                      required = true,
                      example = "1")
    @NotNull
    Integer timesRetreaded;
    @ApiModelProperty(value = "Vida total do pneu. Refere-se ao total de vidas que o pneu terá, incluindo a primeira." +
            " No Prolog, a vida do pneu é um número de 1 à 11 onde 1 é usado para o pneu novo, nunca recapado.",
                      required = true,
                      example = "5")
    @NotNull
    Integer maxRetreads;
    @ApiModelProperty(value = "Pressão recomendada para o pneu. Impacta diretamente na funcionalidade de abertura e " +
            "fechamento de serviços de calibragem, do Prolog. Esse valor é expresso em PSI.",
                      required = true,
                      example = "120.0")
    @NotNull
    Double tirePressureRecommendedInPsi;
    @ApiModelProperty(value = "Pressão atual do pneu. Considera o último valor coletado através da aferição. Caso " +
            "pneu nunca tenha sido aferido esse valor não será enviado.",
                      example = "120.0")
    @Nullable
    Double currentTirePressureInPsi;
    @ApiModelProperty(value = "Sulco interno do pneu. Sulco interno é identificado como o sulco que fica mais próximo" +
            " ao centro do veículo. Considera o último valor coletado através da aferição. Caso pneu nunca tenha " +
            "sido aferido esse valor não será enviado.",
                      example = "15.2")
    @Nullable
    Double tireInternalGrooveInMillimeters;
    @ApiModelProperty(value = "Sulco central interno do pneu. Sulco central interno é identificado como o sulco que " +
            "fica entre o sulco central externo e o interno. Considera o último valor coletado através da aferição. " +
            "Caso pneu nunca tenha sido aferido esse valor não será enviado.",
                      example = "15.2")
    @Nullable
    Double tireMiddleInternalGrooveInMillimeters;
    @ApiModelProperty(value = "Sulco central externo do pneu. Sulco central externo é identificado como o sulco que " +
            "fica entre o sulco externo e o central interno. Considera o último valor coletado através da aferição. " +
            "Caso pneu nunca tenha sido aferido esse valor não será enviado.",
                      example = "15.2")
    @Nullable
    Double tireMiddleExternalGrooveInMillimeters;
    @ApiModelProperty(value = "Sulco externo do pneu. Sulco externo é identificado como o sulco que fica mais " +
            "afastado do centro do veículo. Considera o último valor coletado através da aferição. Caso pneu nunca " +
            "tenha sido aferido esse valor não será enviado.",
                      example = "15.2")
    @Nullable
    Double tireExternalGrooveInMillimeters;
    @ApiModelProperty(value = "Dot do pneu.", example = "2035")
    @Nullable
    String tireDot;
    @ApiModelProperty(value = "Código da dimensão do pneu.", required = true, example = "1")
    @NotNull
    Long tireSizeId;
    @ApiModelProperty(value = "Altura do pneu.", required = true, example = "80")
    @NotNull
    Double tireSizeWidth;
    @ApiModelProperty(value = "Largura do pneu.", required = true, example = "195")
    @NotNull
    Double tireSizeAspectRation;
    @ApiModelProperty(value = "Aro do pneu.", required = true, example = "22.5")
    @NotNull
    Double tireSizeDiameter;
    @ApiModelProperty(value = "Código da marca do pneu.", required = true, example = "590")
    @NotNull
    Long tireBrandId;
    @ApiModelProperty(value = "Nome da marca do pneu.", required = true, example = "Continental")
    @NotNull
    String tireBrandName;
    @ApiModelProperty(value = "Código do modelo do pneu.", required = true, example = "137")
    @NotNull
    Long tireModelId;
    @ApiModelProperty(value = "Nome do modelo do pneu.", required = true, example = "HSR")
    @NotNull
    String tireModelName;
    @ApiModelProperty(value = "Quantidade de sulcos que o modelo do pneu possui.", required = true, example = "4")
    @NotNull
    Integer tireModelGrooveQuantity;
    @ApiModelProperty(value = "Altura do sulco do modelo do pneu, quando novo.", required = true, example = "16")
    @NotNull
    Double tireModelGrooveWidthInMillimeters;
    @ApiModelProperty(value = "Valor do pneu, ou, custo de aquisição do pneu.", required = true, example = "1500,00")
    @NotNull
    BigDecimal tirePrice;
    @ApiModelProperty(value = "Código da marca da banda. Esta propriedade não será enviada caso o pneu estiver na " +
            "primeira vida.",
                      example = "6")
    @Nullable
    Long tireTreadBrandId;
    @ApiModelProperty(value = "Nome da marca da banda. Esta propriedade não será enviada caso o pneu estiver na " +
            "primeira vida.",
                      example = "Bandag")
    @Nullable
    String tireTreadBrandName;
    @ApiModelProperty(value = "Código do modelo da banda. Esta propriedade não será enviada caso o pneu estiver na " +
            "primeira vida.",
                      example = "2")
    @Nullable
    Long tireTreadModelId;
    @ApiModelProperty(value = "Nome do modelo da banda. Esta propriedade não será enviada caso o pneu estiver na " +
            "primeira vida.",
                      example = "BTLSA2")
    @Nullable
    String tireTreadModelName;
    @ApiModelProperty(value = "Quantidade de sulcos que o modelo da banda possui. Esta propriedade não será enviada " +
            "caso o pneu estiver na primeira vida.",
                      example = "3")
    @Nullable
    Integer tireTreadModelGrooveQuantity;
    @ApiModelProperty(value = "Altura do sulco do modelo da banda, quando recapado. Esta propriedade não será enviada" +
            " caso o pneu estiver na primeira vida.",
                      example = "14")
    @Nullable
    Double tireTreadModelGrooveWidthInMillimeters;
    @ApiModelProperty(value = "Valor da banda, ou, custo de recapagem do pneu. Esta propriedade não será enviada caso" +
            " o pneu estiver na primeira vida.",
                      example = "1500,00")
    @Nullable
    BigDecimal tireTreadPrice;
    @ApiModelProperty(value = "Flag indicando se o pneu é novo (primeira vida) e nunca foi rodou, não teve " +
            "desgaste de borracha. Essa flag é setada para false quando o pneu é aferido pela primeira vez.",
                      required = true,
                      example = "true")
    boolean isTireNew;
    @ApiModelProperty(value = "Status do pneu. Podendo ser EM_USO, ESTOQUE, DESCARTE ou ANALISE.",
                      required = true,
                      example = "EM_USO")
    @NotNull
    StatusPneu tireStatus;
    @ApiModelProperty(value = "Código do veículo onde o pneu está aplicado. Essa propriedade só será enviado caso o " +
            "status do pneu for EM_USO.",
                      example = "12345")
    @Nullable
    Long tireAppliedInVehicleId;
    @ApiModelProperty(value = "Placa do veículo onde o pneu está aplicado. Essa propriedade só será enviado caso o " +
            "status do pneu for EM_USO.",
                      example = "PRO1102")
    @Nullable
    String tireAppliedInVehiclePlate;
    @ApiModelProperty(value = "Identificador de frota do veículo onde o pneu está aplicado. Essa propriedade só será " +
            "enviado caso o status do pneu for EM_USO.",
                      example = "FROTA01")
    @Nullable
    String tireAppliedInFleetId;
    @ApiModelProperty(value = "Posição onde o pneu está aplicado. Essa propriedade só será enviado caso o status do " +
            "pneu for EM_USO.",
                      example = "111")
    @Nullable
    Integer tireAppliedInPosition;
    @ApiModelProperty(value = "Código da recapadora onde o pneu está. Essa propriedade só será enviado caso o status " +
            "do pneu for ANALISE.",
                      example = "123")
    @Nullable
    Long tireRepairPlaceId;
    @ApiModelProperty(value = "Nome da recapadora onde o pneu está. Essa propriedade só será enviado caso o status " +
            "do pneu for ANALISE.",
                      example = "Recapadora X")
    @Nullable
    String tireRepairPlaceName;
    @ApiModelProperty(value = "Código de coleta. Essa propriedade só será enviado caso o status do pneu for ANALISE.",
                      example = "12345678")
    @Nullable
    String tireRepairAddicionalCode;
    @ApiModelProperty(value = "Código do motivo de descarte do pneu. Essa propriedade só será enviado caso o status " +
            "do pneu for DESCARTE.",
                      example = "12345")
    @Nullable
    Long scrapReasonId;
    @ApiModelProperty(value = "URL da foto 1, capturada no descarte do pneu. Essa propriedade só será enviado caso o " +
            "status do pneu for DESCARTE.",
                      example = "https://localhost/prolog-movimentacao-fotos/descarte/img1.jpeg")
    @Nullable
    String urlScrapImage1;
    @ApiModelProperty(value = "URL da foto 2, capturada no descarte do pneu. Essa propriedade só será enviado caso o " +
            "status do pneu for DESCARTE.",
                      example = "https://localhost/prolog-movimentacao-fotos/descarte/img2.jpeg")
    @Nullable
    String urlScrapImage2;
    @ApiModelProperty(value = "URL da foto 3, capturada no descarte do pneu. Essa propriedade só será enviado caso o " +
            "status do pneu for DESCARTE.",
                      example = "https://localhost/prolog-movimentacao-fotos/descarte/img3.jpeg")
    @Nullable
    String urlScrapImage3;
}
