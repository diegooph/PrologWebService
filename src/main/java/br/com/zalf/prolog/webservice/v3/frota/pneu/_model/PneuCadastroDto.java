package br.com.zalf.prolog.webservice.v3.frota.pneu._model;

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
    @NotNull(message = "O código de empresa não pode ser nulo.")
    Long codEmpresaAlocado;
    @NotNull(message = "O código da unidade não pode ser nulo.")
    Long codUnidadeAlocado;
    @NotNull(message = "O código do cliente não pode ser nulo.")
    String codigoCliente;
    @NotNull(message = "O código do modelo do pneu não pode ser nulo.")
    Long codModeloPneu;
    @NotNull(message = "O código de dimensão não pode ser nulo.")
    Long codDimensaoPneu;
    @Min(value = 1, message = "A vida atual deve ser um valor entre 1 e 11.")
    @Max(value = 11, message = "A vida atual deve ser um valor entre 1 e 11.")
    Integer vidaAtualPneu;
    @Min(value = 1, message = "A vida total deve ser um valor entre 1 e 11.")
    @Max(value = 11, message = "A vida total deve ser um valor entre 1 e 11.")
    Integer vidaTotalPneu;
    @PositiveOrZero(message = "A pressão recomendada não pode ser um valor negativo.")
    @NotNull(message = "A pressão recomendada não pode ser nula.")
    Double pressaoRecomendadaPneu;
    @Size(max = 20, message = "O DOT pode conter no máximo 20 caracteres.")
    @Nullable
    String dotPneu;
    @PositiveOrZero(message = "O valor do pneu não pode ser um valor negativo.")
    @NotNull(message = "O valor do pneu não pode ser nulo.")
    BigDecimal valorPneu;
    @NotNull(message = "A flag 'pneuNovoNuncaRodado' é obrigatória.")
    Boolean pneuNovoNuncaRodado;
    @Nullable
    Long codModeloBanda;
    @PositiveOrZero(message = "O valor da banda do pneu não pode ser um valor negativo.")
    @Nullable
    BigDecimal valorBandaPneu;
}
