package br.com.zalf.prolog.webservice.v3.fleet.inspection;

import br.com.zalf.prolog.webservice.v3.fleet.inspection._model.*;
import br.com.zalf.prolog.webservice.v3.fleet.kmprocessos._model.EntityKmColetado;
import br.com.zalf.prolog.webservice.v3.fleet.kmprocessos._model.KmProcessoAtualizavel;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
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
public class InspectionService implements KmProcessoAtualizavel {
    @NotNull
    private final InspectionDao inspectionDao;

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
        updateVehicleKmAtInspection(processId, newKm);
    }

    @NotNull
    public List<VehicleInspectionProjection> getVehicleInspections(@NotNull final VehicleInspectionFilter filter) {
        return inspectionDao.getVehicleInspections(filter.getBranchesId(),
                                                   filter.getVehicleTypeId(),
                                                   filter.getVehicleId(),
                                                   filter.getInitialDate(),
                                                   filter.getFinalDate(),
                                                   filter.getLimit(),
                                                   filter.getOffset(),
                                                   filter.isIncludeMeasures());
    }

    @NotNull
    public List<TireInspectionProjection> getTireInspections(@NotNull final TireInspectionFilter filter) {
        return inspectionDao.getTireInspections(filter.getBranchesId(),
                                                filter.getInitialDate(),
                                                filter.getFinalDate(),
                                                filter.getLimit(),
                                                filter.getOffset(),
                                                filter.isIncludeMeasures());
    }

    @Transactional
    public void updateVehicleKmAtInspection(@NotNull final Long inspectionId,
                                            final long newKm) {
        final InspectionEntity entity = getById(inspectionId)
                .toBuilder()
                .withVehicleKm(newKm)
                .build();
        inspectionDao.save(entity);
    }

    @NotNull
    public InspectionEntity getById(@NotNull final Long inspectionId) {
        return inspectionDao.getOne(inspectionId);
    }
}
