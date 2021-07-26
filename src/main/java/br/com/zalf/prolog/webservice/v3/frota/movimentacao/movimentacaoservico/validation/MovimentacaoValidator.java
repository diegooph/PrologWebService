package br.com.zalf.prolog.webservice.v3.frota.movimentacao.movimentacaoservico.validation;

import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.v3.frota.pneu.pneuservico._model.PneuServicoRealizadoEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Created on 2021-07-26
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Component
public class MovimentacaoValidator {
    public void validaServicosRealizados(@NotNull final Long codPneu,
                                          @NotNull final Set<PneuServicoRealizadoEntity> servicosRealizados) {
        validaIsEmpty(codPneu, servicosRealizados);
        final long totalServicosIncrementamVida = servicosRealizados.stream()
                .filter(PneuServicoRealizadoEntity::isIncrementaVida)
                .count();
        validaTotalServicos(totalServicosIncrementamVida);
    }

    private void validaIsEmpty(final @NotNull Long codPneu,
                               final @NotNull Set<PneuServicoRealizadoEntity> servicosRealizados) {
        if (servicosRealizados.isEmpty()) {
            throw new IllegalStateException(
                    "O pneu " + codPneu + " foi movido dá análise para o estoque e não teve nenhum serviço aplicado!");
        }
    }

    private void validaTotalServicos(final long totalServicosIncrementamVida) {
        if (totalServicosIncrementamVida > 1) {
            throw new GenericException("Não é possível realizar dois serviços de troca de banda na mesma movimentação");
        }
    }

}
