package br.com.zalf.prolog.webservice.v3.fleet.checklistworkorder;

import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.StatusItemOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.StatusOrdemServico;
import br.com.zalf.prolog.webservice.v3.fleet.checklistworkorder._model.ChecklistWorkOrderDto;
import br.com.zalf.prolog.webservice.v3.fleet.checklistworkorder._model.ChecklistWorkOrderItemDto;
import br.com.zalf.prolog.webservice.v3.fleet.checklistworkorder._model.ChecklistWorkOrderProjection;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public final class ChecklistWorkOrderMapper {
    @NotNull
    public List<ChecklistWorkOrderDto> toDto(@NotNull final List<ChecklistWorkOrderProjection> workOrdersProjection,
                                             final boolean includeWorkOrderItems) {
        if (workOrdersProjection.isEmpty()) {
            return Collections.emptyList();
        }

        final List<ChecklistWorkOrderDto> workOrdersDto = new ArrayList<>();
        workOrdersProjection.stream()
                .collect(Collectors.groupingBy(ChecklistWorkOrderProjection::getWorkOrderIdProlog))
                .forEach((workOrderId, workOrders) -> workOrdersDto.add(
                        createChecklistWorkOrderDto(workOrders, includeWorkOrderItems)));
        return workOrdersDto;
    }

    @NotNull
    private ChecklistWorkOrderDto createChecklistWorkOrderDto(
            @NotNull final List<ChecklistWorkOrderProjection> workOrdersProjection,
            final boolean includeWorkOrderItems) {
        if (workOrdersProjection.size() == 0) {
            throw new IllegalStateException("A lista usada neste método não pode ser vazia.");
        }
        return new ChecklistWorkOrderDto(
                workOrdersProjection.get(0).getWorkOrderIdProlog(),
                workOrdersProjection.get(0).getWorkOrderId(),
                workOrdersProjection.get(0).getBranchId(),
                workOrdersProjection.get(0).getChecklistId(),
                workOrdersProjection.get(0).getChecklistUserId(),
                workOrdersProjection.get(0).getChecklistUserCpf(),
                workOrdersProjection.get(0).getChecklistUserName(),
                workOrdersProjection.get(0).getVehicleId(),
                workOrdersProjection.get(0).getVehiclePlate(),
                workOrdersProjection.get(0).getFleetId(),
                workOrdersProjection.get(0).getOpenedAtUtc(),
                workOrdersProjection.get(0).getOpenedAtWithTimeZone(),
                StatusOrdemServico.fromString(workOrdersProjection.get(0).getWorkOrderStatus()),
                workOrdersProjection.get(0).getClosedAtUtc(),
                workOrdersProjection.get(0).getClosedAtWithTimeZone(),
                includeWorkOrderItems ? createWorkOrderItems(workOrdersProjection) : null);
    }

    @NotNull
    private List<ChecklistWorkOrderItemDto> createWorkOrderItems(
            @NotNull final List<ChecklistWorkOrderProjection> workOrdersProjection) {
        return workOrdersProjection.stream()
                .map(this::createWorkOrderItemDto)
                .collect(Collectors.toList());
    }

    @NotNull
    private ChecklistWorkOrderItemDto createWorkOrderItemDto(
            @NotNull final ChecklistWorkOrderProjection workOrderProjection) {
        return new ChecklistWorkOrderItemDto(
                workOrderProjection.getWorkOrderItemId(),
                workOrderProjection.getQuestionId(),
                workOrderProjection.getQuestionContextId(),
                workOrderProjection.getOptionId(),
                workOrderProjection.getOptionContextId(),
                workOrderProjection.getOptionAdditionalId(),
                StatusItemOrdemServico.fromString(workOrderProjection.getWorkOrderItemStatus()),
                workOrderProjection.getAmountTimesPointed(),
                workOrderProjection.getResolverUserId(),
                workOrderProjection.getResolverUserCpf(),
                workOrderProjection.getResolverUserName(),
                workOrderProjection.getKm(),
                workOrderProjection.getBatchGroupId(),
                workOrderProjection.getResolvedAtUtc(),
                workOrderProjection.getResolvedAtWithTimeZone(),
                workOrderProjection.getStartedAtUtc(),
                workOrderProjection.getStartedAtWithTimeZone(),
                workOrderProjection.getEndedAtUtc(),
                workOrderProjection.getEndedAtWithTimeZone(),
                workOrderProjection.getResolutionTime(),
                workOrderProjection.getResolutionNotes());
    }
}
