package br.com.zalf.prolog.webservice.frota.pneu.v3._model.dto;

import lombok.Value;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * Created on 2021-03-10
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Value(staticConstructor = "of")
public class PneuCadastro {

    @NotNull(message = "O código da unidade não pode ser nulo.")
    Long codUnidade;

    @NotNull(message = "O código de empresa não pode ser nulo.")
    Long codEmpresa;

    @NotNull(message = "O código do cliente não pode ser nulo.")
    Long codCliente;

    @NotNull(message = "O código de modelo de banda não pode ser nulo.")
    Long codModeloBanda;

    @NotNull(message = "O código de dimensão não pode ser nulo.")
    Long codDimensao;

    @Nullable
    Long vidaAtual;

    @Nullable
    Long vidaTotal;

    @NotNull(message = "A pressão recomendada não pode ser nula.")
    Long pressaoRecomendada;

    @Pattern(regexp = "[0-9]", message = "O DOT só pode conter números")
    @Size(max = 4, message = "O DOT só pode conter 4 caracteres")
    String dot;

    @NotNull(message = "O custo de aquisição não pode ser nulo.")
    Long custoAquisicao;

    boolean pneuUsado;

    @Nullable
    DadosFotoPneu dadosFotoPneu;

    @Value(staticConstructor = "of")
    public static class DadosFotoPneu {
        @Size(max = 3, message = "quantidade de imagens suportadas: 3")
        List<String> urls;
    }
}
