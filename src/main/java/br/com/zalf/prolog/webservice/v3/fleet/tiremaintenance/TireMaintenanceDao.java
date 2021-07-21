package br.com.zalf.prolog.webservice.v3.fleet.tiremaintenance;

import br.com.zalf.prolog.webservice.v3.fleet.tiremaintenance._model.TireMaintenanceEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created on 2021-03-26
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Repository
public interface TireMaintenanceDao extends JpaRepository<TireMaintenanceEntity, Long> {
    @NotNull
    @Query("select tm from TireMaintenanceEntity tm " +
                   "join fetch tm.tire t " +
                   "join fetch tm.tireInspection ti " +
                   "join fetch ti.veiculo v " +
                   "join fetch ti.valoresAfericao va " +
                   "join fetch va.pneu vapkp " +
                   "left join fetch tm.resolverUser ru " +
                   "left join fetch tm.tireMaintenanceProblem tmp " +
                   "where tm.branchId in :branchesId " +
                   "and (:maintenanceStatus is null " +
                   "or (:maintenanceStatus = true and (tm.isResolvedAutomatically = true or ru is not null)) " +
                   "or (:maintenanceStatus = false and (tm.isResolvedAutomatically = false and ru is null))) " +
                   "and (:vehicleId is null or :vehicleId = v.id) " +
                   "and (:tireId is null or :tireId = t.codigo)")
    List<TireMaintenanceEntity> getAllTireMaintenance(@NotNull final List<Long> branchesId,
                                                      @Nullable final Long vehicleId,
                                                      @Nullable final Long tireId,
                                                      @Nullable final Boolean maintenanceStatus,
                                                      @NotNull final Pageable pageable);
}