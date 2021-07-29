package br.com.zalf.prolog.webservice.v3.fleet.inspection;

import br.com.zalf.prolog.webservice.v3.fleet.inspection._model.InspectionEntity;
import br.com.zalf.prolog.webservice.v3.fleet.inspection._model.TireInspectionProjection;
import br.com.zalf.prolog.webservice.v3.fleet.inspection._model.VehicleInspectionProjection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

/**
 * Created on 2021-03-26
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface InspectionDao extends JpaRepository<InspectionEntity, Long> {
    @NotNull
    @Query(value = "select * from func_afericao_get_afericoes_placas_paginada(" +
            "f_cod_unidades => to_bigint_array(:branchesId), " +
            "f_cod_tipo_veiculo => to_bigint(:vehicleTypeId), " +
            "f_cod_veiculo => to_bigint(:vehicleId), " +
            "f_data_inicial => date(:initialDate), " +
            "f_data_final => date(:finalDate), " +
            "f_limit => :limit, " +
            "f_offset => :offset, " +
            "f_incluir_medidas => :includeMeasures);", nativeQuery = true)
    List<VehicleInspectionProjection> getVehicleInspections(@NotNull final List<Long> branchesId,
                                                            @Nullable final Long vehicleTypeId,
                                                            @Nullable final Long vehicleId,
                                                            @NotNull final LocalDate initialDate,
                                                            @NotNull final LocalDate finalDate,
                                                            final int limit,
                                                            final int offset,
                                                            final boolean includeMeasures);

    @NotNull
    @Query(value = "select * from func_afericao_get_afericoes_avulsas_paginada( " +
            "f_cod_unidades => to_bigint_array(:branchesId), " +
            "f_data_inicial => date(:initialDate), " +
            "f_data_final => date(:finalDate), " +
            "f_limit => :limit, " +
            "f_offset => :offset, " +
            "f_incluir_medidas => :includeMeasures);", nativeQuery = true)
    List<TireInspectionProjection> getTireInspections(@NotNull final List<Long> branchesId,
                                                      @NotNull final LocalDate initialDate,
                                                      @NotNull final LocalDate finalDate,
                                                      final int limit,
                                                      final int offset,
                                                      final boolean includeMeasures);
}
