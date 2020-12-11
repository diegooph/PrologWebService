package br.com.zalf.prolog.webservice.frota.veiculo.acoplamento.validator;

import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento._model.realizacao.VeiculoAcoplamentoProcessoRealizacao;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public final class VeiculoAcoplamentoDiffChecker {
    @NotNull
    private final VeiculoAcoplamentoProcessoRealizacao processoRealizacao;

    public boolean algoMudou() {
        return processoRealizacao.getTotalAcoesRealizadas() != processoRealizacao.getTotalVeiculosMantidosPosicao();
    }
}
