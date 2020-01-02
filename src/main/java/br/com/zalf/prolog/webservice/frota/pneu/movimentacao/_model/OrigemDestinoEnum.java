package br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model;

import br.com.zalf.prolog.webservice.frota.pneu._model.StatusPneu;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Zart on 02/03/17.
 */
public enum OrigemDestinoEnum implements PermissoesValidatorString {
    VEICULO(StatusPneu.EM_USO) {
        @NotNull
        @Override
        public String getStringPermissoesValidator() {
            return "VEÍCULO";
        }
    },
    ESTOQUE(StatusPneu.ESTOQUE) {
        @NotNull
        @Override
        public String getStringPermissoesValidator() {
            return "ESTOQUE";
        }
    },
    DESCARTE(StatusPneu.DESCARTE) {
        @NotNull
        @Override
        public String getStringPermissoesValidator() {
            return "DESCARTE";
        }
    },
    ANALISE(StatusPneu.ANALISE) {
        @NotNull
        @Override
        public String getStringPermissoesValidator() {
            return "ANÁLISE";
        }
    };

    @NotNull
    final StatusPneu statusPneu;

    OrigemDestinoEnum(@NotNull final StatusPneu statusPneu) {
        this.statusPneu = statusPneu;
    }

    @NotNull
    public String asString() {
        return statusPneu.asString();
    }

    @Override
    public String toString() {
        return asString();
    }

    @NotNull
    public StatusPneu toStatusPneu() {
        return statusPneu;
    }
}