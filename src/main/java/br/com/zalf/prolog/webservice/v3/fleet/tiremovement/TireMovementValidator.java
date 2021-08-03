package br.com.zalf.prolog.webservice.v3.fleet.tiremovement;

import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.v3.fleet.tireservice._model.TireServiceEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Created on 2021-07-26
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Component
public final class TireMovementValidator {
    public void validateTireServices(@NotNull final Long tireId,
                                     @NotNull final Set<TireServiceEntity> tireServiceEntities) {
        validateNotEmptyTireServices(tireId, tireServiceEntities);
        validateUniqueTireServiceIncreaseLifeCycle(tireServiceEntities);
    }

    private void validateNotEmptyTireServices(@NotNull final Long tireId,
                                              @NotNull final Set<TireServiceEntity> tireServiceEntities) {
        if (tireServiceEntities.isEmpty()) {
            throw new IllegalStateException(
                    "O pneu " + tireId + " foi movido dá análise para o estoque e não teve nenhum serviço aplicado!");
        }
    }

    private void validateUniqueTireServiceIncreaseLifeCycle(@NotNull final Set<TireServiceEntity> tireServiceEntities) {
        final long totalTireServicesIncreaseLifeCycle = tireServiceEntities.stream()
                .filter(TireServiceEntity::isIncreaseLifeCycle)
                .count();
        if (totalTireServicesIncreaseLifeCycle > 1) {
            throw new GenericException("Não é possível realizar dois serviços de troca de banda na mesma movimentação");
        }
    }
}
