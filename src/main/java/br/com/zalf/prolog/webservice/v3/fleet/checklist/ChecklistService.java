package br.com.zalf.prolog.webservice.v3.fleet.checklist;

import br.com.zalf.prolog.webservice.v3.fleet.checklist._model.ChecklistEntity;
import br.com.zalf.prolog.webservice.v3.fleet.checklist._model.ChecklistFilter;
import br.com.zalf.prolog.webservice.v3.fleet.checklist._model.ChecklistProjection;
import br.com.zalf.prolog.webservice.v3.fleet.processeskm._model.KmCollectedEntity;
import br.com.zalf.prolog.webservice.v3.fleet.processeskm._model.ProcessKmUpdatable;
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
public class ChecklistService implements ProcessKmUpdatable {
    @NotNull
    private final ChecklistDao checklistDao;

    @NotNull
    @Override
    public KmCollectedEntity getEntityKmCollected(@NotNull final Long entityId, @NotNull final Long vehicleId) {
        return getById(entityId);
    }

    @Override
    public void updateProcessKmCollected(@NotNull final Long processId,
                                         @NotNull final Long vehicleId,
                                         final long newKm) {
        updateVehicleKmAtChecklist(processId, newKm);
    }

    @NotNull
    public ChecklistEntity getById(@NotNull final Long checklistId) {
        return checklistDao.getOne(checklistId);
    }

    public void update(@NotNull final ChecklistEntity checklistEntity) {
        checklistDao.save(checklistEntity);
    }

    @Transactional
    public void updateVehicleKmAtChecklist(@NotNull final Long checklistId,
                                           final long newKm) {
        final ChecklistEntity entity = getById(checklistId)
                .toBuilder()
                .withVehicleKm(newKm)
                .build();
        update(entity);
    }

    @NotNull
    public List<ChecklistProjection> getAllChecklists(@NotNull final ChecklistFilter checklistFilter) {
        return checklistDao.getAllChecklists(checklistFilter.getBranchesId(),
                                             checklistFilter.getStartDate(),
                                             checklistFilter.getEndDate(),
                                             checklistFilter.getUserId(),
                                             checklistFilter.getVehicleId(),
                                             checklistFilter.getVehicleTypeId(),
                                             checklistFilter.isIncludeAnswers(),
                                             checklistFilter.getLimit(),
                                             checklistFilter.getOffset());
    }
}
