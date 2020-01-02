package br.com.zalf.prolog.webservice.frota.pneu.transferencia._model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 09/05/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public enum TipoProcessoTransferenciaPneu {
    TRANSFERENCIA_JUNTO_A_VEICULO("TRANSFERENCIA_JUNTO_A_VEICULO"),
    TRANSFERENCIA_APENAS_PNEUS("TRANSFERENCIA_APENAS_PNEUS");

    @NotNull
    private final String tipoTransferenciaPneu;

    TipoProcessoTransferenciaPneu(@NotNull final String tipoTransferenciaPneu) {
        this.tipoTransferenciaPneu = tipoTransferenciaPneu;
    }

    @NotNull
    public String asString() {
        return this.tipoTransferenciaPneu;
    }

    @NotNull
    public TipoProcessoTransferenciaPneu fromString(@NotNull final String tipoTransferenciaPneu) {
        for (final TipoProcessoTransferenciaPneu tipo : TipoProcessoTransferenciaPneu.values()) {
            if (tipo.tipoTransferenciaPneu.equals(tipoTransferenciaPneu)) {
                return tipo;
            }
        }

        throw new IllegalStateException("Nenhum tipo de transferência mapeada para: " + tipoTransferenciaPneu);
    }
}
