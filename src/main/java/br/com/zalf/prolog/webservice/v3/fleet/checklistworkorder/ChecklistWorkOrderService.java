package br.com.zalf.prolog.webservice.v3.fleet.checklistworkorder;

import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.StatusOrdemServico;
import br.com.zalf.prolog.webservice.v3.fleet.checklistworkorder._model.ChecklistOrdemServicoItemEntity;
import br.com.zalf.prolog.webservice.v3.fleet.checklistworkorder._model.ChecklistWorkOrderProjection;
import br.com.zalf.prolog.webservice.v3.fleet.kmprocessos._model.EntityKmColetado;
import br.com.zalf.prolog.webservice.v3.fleet.kmprocessos._model.KmProcessoAtualizavel;
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
public class ChecklistWorkOrderService implements KmProcessoAtualizavel {
    @NotNull
    private final ChecklistWorkOrderDao workOrderDao;
    @NotNull
    private final ChecklistWorkOrderItemDao workOrderItemDao;

    @NotNull
    @Override
    public EntityKmColetado getEntityKmColetado(@NotNull final Long entityId,
                                                @NotNull final Long codVeiculo) {
        return getWorkOrderItemById(entityId);
    }

    @Override
    public void updateKmColetadoProcesso(@NotNull final Long codProcesso,
                                         @NotNull final Long codVeiculo,
                                         final long novoKm) {
        updateVehicleKmAtResolution(codProcesso, novoKm);
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
    public ChecklistOrdemServicoItemEntity getWorkOrderItemById(@NotNull final Long workOrderItemId) {
        return workOrderItemDao.getOne(workOrderItemId);
    }

    public void updateWorkOrderItem(@NotNull final ChecklistOrdemServicoItemEntity checklistOrdemServicoItemEntity) {
        workOrderItemDao.save(checklistOrdemServicoItemEntity);
    }

    @Transactional
    public void updateVehicleKmAtResolution(@NotNull final Long workOrderItemId,
                                            final long newKm) {
        final ChecklistOrdemServicoItemEntity entity = getWorkOrderItemById(workOrderItemId)
                .toBuilder()
                .withKmColetadoVeiculoFechamentoItem(newKm)
                .build();
        updateWorkOrderItem(entity);
    }
}
