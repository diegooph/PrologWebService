package br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model;

import br.com.zalf.prolog.webservice.frota.pneu._model.StatusPneu;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.transicao._model.listagem.TransicaoUnidadeMotivos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Enum que representa as origens e destinos possíveis nas movimentações do Prolog.
 * Para facilitar a passagem dessas constantes para a tabela PNEU, usamos como valor do enum as constantes do
 * enum {@link StatusPneu}.
 *
 * <b>A ordem das constantes nesse enum é importante, não altere!</b>
 * Ele é utilizado para ordenação de objetos no método:
 * {@link TransicaoUnidadeMotivos#compareTo(TransicaoUnidadeMotivos)}
 */
public enum OrigemDestinoEnum implements PermissoesValidatorString {
    ESTOQUE(StatusPneu.ESTOQUE) {
        @NotNull
        @Override
        public String getStringPermissoesValidator() {
            return "ESTOQUE";
        }
    },
    VEICULO(StatusPneu.EM_USO) {
        @NotNull
        @Override
        public String getStringPermissoesValidator() {
            return "VEÍCULO";
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
    public static OrigemDestinoEnum fromString(@Nullable final String text) throws IllegalArgumentException {
        if (text != null) {
            for (final OrigemDestinoEnum origemDestinoEnum : OrigemDestinoEnum.values()) {
                if (text.equalsIgnoreCase(origemDestinoEnum.statusPneu.asString())) {
                    return origemDestinoEnum;
                }
            }
        }
        throw new IllegalArgumentException("Nenhuma origem/destino encontrada para a String: " + text);
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