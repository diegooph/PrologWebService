package br.com.zalf.prolog.webservice.v3.fleet.checklistworkorder;

import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.StatusOrdemServico;
import br.com.zalf.prolog.webservice.v3.fleet.checklistworkorder._model.ChecklistWorkOrderItemEntity;
import br.com.zalf.prolog.webservice.v3.fleet.checklistworkorder._model.ChecklistWorkOrderProjection;
import br.com.zalf.prolog.webservice.v3.fleet.processeskm._model.KmCollectedEntity;
import br.com.zalf.prolog.webservice.v3.fleet.processeskm._model.ProcessKmUpdatable;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
public class ChecklistWorkOrderService implements ProcessKmUpdatable {
    @NotNull
    private final ChecklistWorkOrderDao workOrderDao;
    @NotNull
    private final ChecklistWorkOrderItemDao workOrderItemDao;

    @NotNull
    @Override
    public KmCollectedEntity getEntityKmCollected(@NotNull final Long entityId, @NotNull final Long vehicleId) {
        return getWorkOrderItemById(entityId);
    }

    @Override
    public void updateProcessKmCollected(@NotNull final Long processId,
                                         @NotNull final Long vehicleId,
                                         final long newKm) {
        updateVehicleKmAtResolution(processId, newKm);
    }

    @NotNull
    @Transactional
    public List<ChecklistWorkOrderProjection> getAllWorkOrders(@NotNull final List<Long> branchesId,
                                                               @Nullable final Long vehicleTypeId,
                                                               @Nullable final String vehicleId,
                                                               @Nullable final StatusOrdemServico workOrderStatus,
                                                               final boolean includeWorkOrderItems,
                                                               final int limit,
                                                               final int offset) {
        return workOrderDao.getAllWorkOrders(branchesId,
                                             vehicleTypeId,
                                             vehicleId,
                                             workOrderStatus == null ? null : workOrderStatus.asString(),
                                             includeWorkOrderItems,
                                             limit,
                                             offset);
    }

    @NotNull
    public ChecklistWorkOrderItemEntity getWorkOrderItemById(@NotNull final Long workOrderItemId) {
        return workOrderItemDao.getOne(workOrderItemId);
    }

    public void updateWorkOrderItem(@NotNull final ChecklistWorkOrderItemEntity checklistWorkOrderItemEntity) {
        workOrderItemDao.save(checklistWorkOrderItemEntity);
    }

    @Transactional
    public void updateVehicleKmAtResolution(@NotNull final Long workOrderItemId, final long newKm) {
        final ChecklistWorkOrderItemEntity entity = getWorkOrderItemById(workOrderItemId)
                .toBuilder()
                .withVehicleKmAtResolution(newKm)
                .build();
        updateWorkOrderItem(entity);
    }
}
