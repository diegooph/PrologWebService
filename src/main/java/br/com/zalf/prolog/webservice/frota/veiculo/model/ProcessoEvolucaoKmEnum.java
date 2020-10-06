package br.com.zalf.prolog.webservice.frota.veiculo.model;

import br.com.zalf.prolog.webservice.frota.veiculo.historico._model.TipoAlteracaoEnum;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum ProcessoEvolucaoKmEnum {
    MOVIMENTACAO("MOVIMENTACAO"),
    AFERICAO("AFERICAO"),
    FECHAMENTO_SERVICO_PNEU("FECHAMENTO_SERVICO_PNEU"),
    CHECKLIST("CHECKLIST"),
    FECHAMENTO_ITEM_CHECKLIST("FECHAMENTO_ITEM_CHECKLIST"),
    TRANSFERENCIA_DE_VEICULOS("TRANSFERENCIA_DE_VEICULOS");

    @NotNull
    private final String stringRepresentation;

    ProcessoEvolucaoKmEnum(@NotNull final String stringRepresentation) {
        this.stringRepresentation = stringRepresentation;
    }

    @NotNull
    public static ProcessoEvolucaoKmEnum fromString(@Nullable final String text) throws IllegalArgumentException {
        if (text != null) {
            for (final ProcessoEvolucaoKmEnum processoEvolucaoKmEnum : ProcessoEvolucaoKmEnum.values()) {
                if (text.equalsIgnoreCase(processoEvolucaoKmEnum.asString())) {
                    return processoEvolucaoKmEnum;
                }
            }
        }
        throw new IllegalArgumentException("Nenhum processo encontrado para a String: " + text);
    }

    @Override
    public String toString() {
        return asString();
    }

    @NotNull
    public String asString() {
        return stringRepresentation;
    }
}
