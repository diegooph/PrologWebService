package br.com.zalf.prolog.webservice.frota.pneu.v3._model.dto;

import lombok.Value;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
    @NotNull
    Integer vidaAtualPneu;
    @NotNull
    Integer vidaTotalPneu;
    @NotNull(message = "A pressão recomendada não pode ser nula.")
    Double pressaoRecomendada;
    @Size(max = 20, message = "O DOT pode conter no máximo 20 caracteres")
    String dotPneu;
    @NotNull(message = "O custo de aquisição não pode ser nulo.")
    BigDecimal valorPneu;
    @NotNull
    Boolean pneuNovoNuncaRodado;
    @Nullable
    Long codModeloBanda;
    @Nullable
    BigDecimal valorBandaPneu;
}
