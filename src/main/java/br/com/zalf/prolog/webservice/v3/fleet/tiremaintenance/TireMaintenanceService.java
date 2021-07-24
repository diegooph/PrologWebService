package br.com.zalf.prolog.webservice.v3.fleet.tiremaintenance;

import br.com.zalf.prolog.webservice.v3.OffsetBasedPageRequest;
import br.com.zalf.prolog.webservice.v3.fleet.kmprocessos._model.EntityKmColetado;
import br.com.zalf.prolog.webservice.v3.fleet.kmprocessos._model.KmProcessoAtualizavel;
import br.com.zalf.prolog.webservice.v3.fleet.tiremaintenance._model.TireMaintenanceEntity;
import br.com.zalf.prolog.webservice.v3.fleet.tiremaintenance._model.TireMaintenanceFilter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created on 2021-03-26
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TireMaintenanceService implements KmProcessoAtualizavel {
    @NotNull
    private final TireMaintenanceDao tireMaintenanceDao;

    @NotNull
    @Override
    public EntityKmColetado getEntityKmColetado(@NotNull final Long entityId,
                                                @NotNull final Long vehicleId) {
        return getById(entityId);
    }

    @Override
    public void updateKmColetadoProcesso(@NotNull final Long processId,
                                         @NotNull final Long vehicleId,
                                         final long newKm) {
        updateVehicleKmAtResolution(processId, newKm);
    }

    @NotNull
    @Transactional
    public List<TireMaintenanceEntity> getAllTireMaintenance(@NotNull final TireMaintenanceFilter filter) {
        return tireMaintenanceDao.getAllTireMaintenance(filter.getBranchesId(),
                                                        filter.getVehicleId(),
                                                        filter.getTireId(),
                                                        filter.getMaintenanceStatusAsBoolean(),
                                                        OffsetBasedPageRequest.of(filter.getLimit(),
                                                                                  filter.getOffset(),
                                                                                  Sort.unsorted()));
    }

    @NotNull
    public TireMaintenanceEntity getById(@NotNull final Long tireMaintenanceId) {
        return tireMaintenanceDao.getOne(tireMaintenanceId);
    }

    @Transactional
    public void updateVehicleKmAtResolution(@NotNull final Long tireMaintenanceId,
                                            final long newKm) {
        final TireMaintenanceEntity entity = getById(tireMaintenanceId)
                .toBuilder()
                .withVehicleKmAtResolution(newKm)
                .build();
        tireMaintenanceDao.save(entity);
    }
}
