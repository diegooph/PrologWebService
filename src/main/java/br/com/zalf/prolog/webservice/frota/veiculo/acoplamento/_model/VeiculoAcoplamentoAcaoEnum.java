package br.com.zalf.prolog.webservice.frota.veiculo.acoplamento._model;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-11-10
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@AllArgsConstructor
public enum VeiculoAcoplamentoAcaoEnum {
    ACOPLADO("ACOPLADO"),
    DESACOPLADO("DESACOPLADO"),
    PERMANECEU("PERMANECEU");

    @NotNull
    private final String databaseConstant;

    @NotNull
    public static VeiculoAcoplamentoAcaoEnum fromString(@NotNull final String value) {
        final VeiculoAcoplamentoAcaoEnum[] acoes = VeiculoAcoplamentoAcaoEnum.values();
        for (final VeiculoAcoplamentoAcaoEnum acao : acoes) {
            if (acao.databaseConstant.equals(value)) {
                return acao;
            }
        }

        throw new IllegalArgumentException("Nenhum VeiculoAcoplamentoAcaoEnum encontrado com a string: " + value);
    }

    @NotNull
    public String asString() {
        return databaseConstant;
    }

    @Override
    public String toString() {
        return asString();
    }
}
