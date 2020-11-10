package br.com.zalf.prolog.webservice.frota.veiculo.acoplamento._model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-11-10
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public enum VeiculoAcoplamentoAcao {
    ACOPLADO("ACOPLADO"),
    DESACOPLADO("DESACOPLADO"),
    PERMANECEU("PERMANECEU");

    @NotNull
    private final String databaseConstant;

    VeiculoAcoplamentoAcao(@NotNull final String databaseConstant) {
        this.databaseConstant = databaseConstant;
    }

    @NotNull
    public static VeiculoAcoplamentoAcao fromString(@NotNull final String value) {
        final VeiculoAcoplamentoAcao[] acoes = VeiculoAcoplamentoAcao.values();
        for (final VeiculoAcoplamentoAcao acao : acoes) {
            if (acao.databaseConstant.equals(value)) {
                return acao;
            }
        }

        throw new IllegalArgumentException("Nenhum VeiculoAcoplamentoAcao encontrado com a string: " + value);
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
