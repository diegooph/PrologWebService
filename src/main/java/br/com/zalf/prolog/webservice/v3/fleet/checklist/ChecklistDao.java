package br.com.zalf.prolog.webservice.v3.fleet.checklist;

import br.com.zalf.prolog.webservice.v3.fleet.checklist._model.ChecklistEntity;
import br.com.zalf.prolog.webservice.v3.fleet.checklist._model.ChecklistProjection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

/**
 * Created on 2021-03-25
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface ChecklistDao extends JpaRepository<ChecklistEntity, Long> {
    @NotNull
    @Query(value = "select * from func_checklist_get_listagem(" +
            "f_cod_unidades => to_bigint_array(:branchesId), " +
            "f_data_inicial => date(:startDate), " +
            "f_data_final => date(:endDate), " +
            "f_cod_colaborador => to_bigint(:userId), " +
            "f_cod_veiculo => to_bigint(:vehicleId), " +
            "f_cod_tipo_veiculo => to_bigint(:vehicleTypeId), " +
            "f_incluir_respostas => :includeAnswers, " +
            "f_limit => :limit, " +
            "f_offset => :offset);", nativeQuery = true)
    List<ChecklistProjection> getAllChecklists(@NotNull final List<Long> branchesId,
                                               @NotNull final LocalDate startDate,
                                               @NotNull final LocalDate endDate,
                                               @Nullable final Long userId,
                                               @Nullable final Long vehicleId,
                                               @Nullable final Long vehicleTypeId,
                                               boolean includeAnswers,
                                               int limit,
                                               int offset);
}