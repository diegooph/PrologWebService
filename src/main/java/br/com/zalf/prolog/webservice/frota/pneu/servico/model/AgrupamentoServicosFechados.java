package br.com.zalf.prolog.webservice.frota.pneu.servico.model;

import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;

/**
 * Created on 12/5/17
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public enum AgrupamentoServicosFechados {
    POR_PNEU("por-pneu"),
    POR_VEICULO("por-veiculo");

    private final String stringRepresentation;

    AgrupamentoServicosFechados(String stringRepresentation) {
        this.stringRepresentation = stringRepresentation;
    }

    public String asString() {
        return stringRepresentation;
    }

    public static AgrupamentoServicosFechados fromString(@Nonnull final String string) {
        Preconditions.checkNotNull(string, "string cannot be null!");

        for (final AgrupamentoServicosFechados agrupamento : AgrupamentoServicosFechados.values()) {
            if (string.equals(agrupamento.stringRepresentation)) {
                return agrupamento;
            }
        }

        throw new IllegalArgumentException("Nenhum agrupamento encontrado para a string: " + string);
    }
}