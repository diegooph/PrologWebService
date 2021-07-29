package br.com.zalf.prolog.webservice.v3.fleet.movimentacao.movimentacaoservico.validation;

import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.v3.fleet.tire.pneuservico.PneuServicoRealizadoEntity;
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
        validaTemServicosAplicados(codPneu, servicosRealizados);
        validaUnicoServicoIncrementaVidaAplicado(servicosRealizados);
    }

    private void validaTemServicosAplicados(@NotNull final Long codPneu,
                                            @NotNull final Set<PneuServicoRealizadoEntity> servicosRealizados) {
        if (servicosRealizados.isEmpty()) {
            throw new IllegalStateException(
                    "O pneu " + codPneu + " foi movido dá análise para o estoque e não teve nenhum serviço aplicado!");
        }
    }

    private void validaUnicoServicoIncrementaVidaAplicado(
            @NotNull final Set<PneuServicoRealizadoEntity> servicosRealizados) {
        final long totalServicosIncrementamVida = servicosRealizados.stream()
                .filter(PneuServicoRealizadoEntity::isIncrementaVida)
                .count();
        if (totalServicosIncrementamVida > 1) {
            throw new GenericException("Não é possível realizar dois serviços de troca de banda na mesma movimentação");
        }
    }
}
