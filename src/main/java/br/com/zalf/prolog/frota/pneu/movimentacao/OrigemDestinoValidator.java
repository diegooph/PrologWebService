package br.com.zalf.prolog.frota.pneu.movimentacao;

import br.com.zalf.prolog.frota.pneu.movimentacao.destino.Destino;
import br.com.zalf.prolog.frota.pneu.movimentacao.origem.Origem;
import com.sun.istack.internal.NotNull;

/**
 * Responsável por validar se {@link Origem} e {@link Destino} estão corretas utilizando as {@link OrigemDestinoRegras}.
 */
public class OrigemDestinoValidator {

    public OrigemDestinoValidator() {

    }

    public void validate(@NotNull Origem origem, @NotNull Destino destino) throws OrigemDestinoInvalidaException {
        OrigemDestinoRegras[] regras = OrigemDestinoRegras.values();
        for (int i = 0; i < regras.length; i++) {
            OrigemDestinoRegras regra = regras[i];
            if (regra.getOrigem().equals(origem.getTipo())) {
                if (regra.getDestinos() == null || !regra.getDestinos().contains(destino.getTipo())) {
                    // Temos uma Origem válida mas um Destino inválido
                    throw new OrigemDestinoInvalidaException(origem, destino);
                } else {
                    return;
                }
            } else if (i == (regras.length - 1)) {
                // Se entrar aqui então não foi informado uma Origem válida
                throw new OrigemDestinoInvalidaException(origem, destino);
            }
        }
    }
}