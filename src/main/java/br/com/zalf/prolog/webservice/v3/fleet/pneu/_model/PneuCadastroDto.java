package br.com.zalf.prolog.webservice.v3.fleet.pneu._model;

import br.com.zalf.prolog.webservice.v3.validation.IdBranch;
import br.com.zalf.prolog.webservice.v3.validation.IdCompany;
import io.swagger.annotations.ApiModelProperty;
import lombok.Value;

import javax.annotation.Nullable;
import javax.validation.constraints.*;
import java.math.BigDecimal;

/**
 * Created on 2021-03-10
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Value(staticConstructor = "of")
public class PneuCadastroDto {
    @IdCompany
    @ApiModelProperty(value = "Código da empresa onde o pneu será cadastrado.", required = true, example = "10")
    @NotNull(message = "O código de empresa não pode ser nulo.")
    Long codEmpresaAlocado;
    @IdBranch
    @ApiModelProperty(value = "Código da unidade onde o pneu será cadastrado.", required = true, example = "215")
    @NotNull(message = "O código da unidade não pode ser nulo.")
    Long codUnidadeAlocado;
    @ApiModelProperty(value = "Código de cliente do pneu. Usualmente se trata do número de fogo do pneu.",
                      required = true,
                      example = "PN0001")
    @NotNull(message = "O código do cliente não pode ser nulo.")
    String codigoCliente;
    @ApiModelProperty(value = "Código do modelo do pneu.", required = true, example = "590")
    @NotNull(message = "O código do modelo do pneu não pode ser nulo.")
    Long codModeloPneu;
    @ApiModelProperty(value = "Código da dimensão do pneu.", required = true, example = "1")
    @NotNull(message = "O código de dimensão não pode ser nulo.")
    Long codDimensaoPneu;
    @ApiModelProperty(value = "Vida atual do pneu. No Prolog, a vida do pneu é um número de 1 à 11 onde 1 é usado " +
            "para o pneu novo, nunca recapado.",
                      required = true,
                      example = "1")
    @Min(value = 1, message = "A vida atual deve ser um valor entre 1 e 11.")
    @Max(value = 11, message = "A vida atual deve ser um valor entre 1 e 11.")
    Integer vidaAtualPneu;
    @ApiModelProperty(value = "Vida total do pneu. Refere-se ao total de vidas que o pneu terá, incluindo a primeira." +
            " No Prolog, a vida do pneu é um número de 1 à 11 onde 1 é usado para o pneu novo, nunca recapado.",
                      required = true,
                      example = "5")
    @Min(value = 1, message = "A vida total deve ser um valor entre 1 e 11.")
    @Max(value = 11, message = "A vida total deve ser um valor entre 1 e 11.")
    Integer vidaTotalPneu;
    @ApiModelProperty(value = "Pressão recomendada para o pneu. Impacta diretamente na funcionalidade de abertura e " +
            "fechamento de serviços de calibragem, do Prolog. Esse valor é expresso em PSI.",
                      required = true,
                      example = "120.0")
    @PositiveOrZero(message = "A pressão recomendada não pode ser um valor negativo.")
    @NotNull(message = "A pressão recomendada não pode ser nula.")
    Double pressaoRecomendadaPneu;
    @ApiModelProperty(value = "Dot do pneu.", example = "2035")
    @Size(max = 20, message = "O DOT pode conter no máximo 20 caracteres.")
    @Nullable
    String dotPneu;
    @ApiModelProperty(value = "Valor do pneu, ou, custo de aquisição do pneu.", required = true, example = "1500,00")
    @PositiveOrZero(message = "O valor do pneu não pode ser um valor negativo.")
    @NotNull(message = "O valor do pneu não pode ser nulo.")
    BigDecimal valorPneu;
    @ApiModelProperty(value = "Flag indicando se o pneu é novo (primeira vida) e nunca foi rodou, não teve " +
            "desgaste de borracha.",
                      required = true,
                      example = "true")
    @NotNull(message = "A flag 'pneuNovoNuncaRodado' é obrigatória.")
    Boolean pneuNovoNuncaRodado;
    @ApiModelProperty(value = "Código do modelo de banda. Esse campo é obrigatório caso vidaAtualPneu > 1.",
                      example = "1540")
    @Nullable
    Long codModeloBanda;
    @ApiModelProperty(value = "Valor da banda aplicada. Esse campo é obrigatório caso vidaAtualPneu > 1.",
                      example = "550,00")
    @PositiveOrZero(message = "O valor da banda do pneu não pode ser um valor negativo.")
    @Nullable
    BigDecimal valorBandaPneu;
}
