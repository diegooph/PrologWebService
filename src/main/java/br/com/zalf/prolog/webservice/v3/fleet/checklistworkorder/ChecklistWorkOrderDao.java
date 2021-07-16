package br.com.zalf.prolog.webservice.v3.fleet.checklistworkorder;

import br.com.zalf.prolog.webservice.v3.fleet.checklistworkorder._model.ChecklistOrdemServicoEntity;
import br.com.zalf.prolog.webservice.v3.fleet.checklistworkorder._model.ChecklistOrdemServicoPk;
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
public interface ChecklistWorkOrderDao extends JpaRepository<ChecklistOrdemServicoEntity, ChecklistOrdemServicoPk> {
    @NotNull
    @Query(value = "select * from func_checklist_ordem_servico_listagem(" +
            "f_cod_unidades => to_bigint_array(:branchesId)," +
            "f_cod_tipo_veiculo => to_bigint(:vehicleTypeId)," +
            "f_cod_veiculo => to_bigint(:vehicleId)," +
            "f_status_ordem_servico => to_text(:workOrderStatus)," +
            "f_incluir_itens_ordem_servico => :includeWorkOrderItems," +
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
