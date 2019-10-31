package br.com.zalf.prolog.webservice.frota.pneu.servico._model;

import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;

/**
 * Created on 12/5/17
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public enum AgrupamentoQuantidadeServicos {
    POR_PNEU("por-pneu"),
    POR_VEICULO("por-veiculo");

    private final String stringRepresentation;

    AgrupamentoQuantidadeServicos(String stringRepresentation) {
        this.stringRepresentation = stringRepresentation;
    }

    public String asString() {
        return stringRepresentation;
    }

    public static AgrupamentoQuantidadeServicos fromString(@Nonnull final String string) {
        Preconditions.checkNotNull(string, "string cannot be null!");

        for (final AgrupamentoQuantidadeServicos agrupamento : AgrupamentoQuantidadeServicos.values()) {
            if (string.equals(agrupamento.stringRepresentation)) {
                return agrupamento;
            }
        }

        throw new IllegalArgumentException("Nenhum agrupamento encontrado para a string: " + string);
    }
}