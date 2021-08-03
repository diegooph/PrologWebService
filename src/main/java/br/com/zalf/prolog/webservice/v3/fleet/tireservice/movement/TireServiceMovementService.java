package br.com.zalf.prolog.webservice.v3.fleet.tireservice.movement;

import br.com.zalf.prolog.webservice.frota.pneu.pneutiposervico._model.PneuServicoRealizado;
import br.com.zalf.prolog.webservice.v3.fleet.tiremovement.TireMovementValidator;
import br.com.zalf.prolog.webservice.v3.fleet.tiremovement._model.TireMovementEntity;
import br.com.zalf.prolog.webservice.v3.fleet.tireservice.TireServiceCreator;
import br.com.zalf.prolog.webservice.v3.fleet.tireservice.TireServiceService;
import br.com.zalf.prolog.webservice.v3.fleet.tireservice._model.TireServiceEntity;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created on 2021-06-23
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TireServiceMovementService {
    @NotNull
    private final TireServiceService tireServiceService;
    @NotNull
    private final TireServiceMovementDao tireServiceMovementDao;
    @NotNull
    private final TireServiceRetreaderDao tireServiceRetreaderDao;
    @NotNull
    private final TireMovementValidator tireMovementValidator;

    public void insertTireServiceMovement(@NotNull final TireMovementEntity tireMovementEntity) {
        if (tireMovementEntity.getTireServiceEntities() != null) {
            tireMovementValidator.validateTireServices(tireMovementEntity.getTireEntity().getId(),
                                                       tireMovementEntity.getTireServiceEntities());
            tireMovementEntity.getTireServiceEntities()
                    .forEach(tireService -> insertTireServiceMovement(tireMovementEntity, tireService));
        }
    }

    private void insertTireServiceMovement(@NotNull final TireMovementEntity tireMovementEntity,
                                           @NotNull final TireServiceEntity tireServiceEntity) {
        final TireServiceEntity savedTireService =
                tireServiceService.insertTireService(tireServiceEntity.getTireEntity(),
                                                     tireServiceEntity.getServiceCost(),
                                                     tireServiceEntity.getTireServiceTypeEntity(),
                                                     PneuServicoRealizado.FONTE_MOVIMENTACAO);
        insertTireServiceMovement(tireMovementEntity.getId(), savedTireService.getId());
        final Long retreaderId = tireServiceRetreaderDao.getRetreaderId(tireMovementEntity.getTireEntity().getId());
        insertTireServiceRetreader(tireMovementEntity.getId(), savedTireService.getId(), retreaderId);
    }

    private void insertTireServiceMovement(@NotNull final Long tireMovementId, @NotNull final Long tireServiceId) {
        tireServiceMovementDao.save(
                TireServiceCreator.createTireServiceMovement(tireMovementId,
                                                             tireServiceId,
                                                             PneuServicoRealizado.FONTE_MOVIMENTACAO));
    }

    private void insertTireServiceRetreader(@NotNull final Long tireMovementId,
                                            @NotNull final Long tireServiceId,
                                            @NotNull final Long retreaderId) {
        tireServiceRetreaderDao.save(
                TireServiceCreator.createTireServiceRetreader(tireMovementId, tireServiceId, retreaderId));
    }
}
