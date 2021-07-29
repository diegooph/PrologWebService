package br.com.zalf.prolog.webservice.v3.fleet.checklistworkorder;

import br.com.zalf.prolog.webservice.v3.fleet.checklistworkorder._model.ChecklistWorkOrderEntity;
import br.com.zalf.prolog.webservice.v3.fleet.checklistworkorder._model.ChecklistWorkOrderPk;
import br.com.zalf.prolog.webservice.v3.fleet.checklistworkorder._model.ChecklistWorkOrderProjection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created on 2021-03-26
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface ChecklistWorkOrderDao extends JpaRepository<ChecklistWorkOrderEntity, ChecklistWorkOrderPk> {
    @NotNull
    @Query(value = "select * from func_get_checklist_work_order(" +
            "f_branches_id => to_bigint_array(:branchesId)," +
            "f_vehicle_type_id => to_bigint(:vehicleTypeId)," +
            "f_vehicle_id => to_bigint(:vehicleId)," +
            "f_work_order_status => to_text(:workOrderStatus)," +
            "f_include_work_order_items => :includeWorkOrderItems," +
            "f_limit => :limit," +
            "f_offset => :offset);", nativeQuery = true)
    List<ChecklistWorkOrderProjection> getAllWorkOrders(@NotNull final List<Long> branchesId,
                                                        @Nullable final Long vehicleTypeId,
                                                        @Nullable final String vehicleId,
                                                        @Nullable final String workOrderStatus,
                                                        final boolean includeWorkOrderItems,
                                                        final int limit,
                                                        final int offset);
}
