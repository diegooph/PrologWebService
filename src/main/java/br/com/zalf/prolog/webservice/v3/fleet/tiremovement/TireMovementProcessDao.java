package br.com.zalf.prolog.webservice.v3.fleet.tiremovement;

import br.com.zalf.prolog.webservice.v3.fleet.tiremovement._model.TireMovementProcessEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Created on 2021-03-26
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Repository
public interface TireMovementProcessDao extends JpaRepository<TireMovementProcessEntity, Long> {
    @NotNull
    @Query("select distinct mpe from TireMovementProcessEntity mpe "
                   + "join fetch mpe.movementProcessBy crp "
                   + "join fetch crp.branchEntity u "
                   + "join fetch mpe.tireMovementEntities m "
                   + "join fetch m.tireMovementSourceEntity mo "
                   + "join fetch m.tireMovementDestinationEntity md "
                   + "join fetch m.tireEntity "
                   + "left join fetch m.tireServiceEntities sr "
                   + "left join fetch sr.tireServiceTypeEntity "
                   + "left join fetch mo.vehicleEntity "
                   + "left join fetch md.vehicleEntity "
                   + "left join fetch md.retreaderEntity r "
                   + "where mpe.branchId in :branchesId "
                   + "and tz_date(mpe.movementProcessAt, u.timezone) between :startDate and :endDate "
                   + "and (:userId is null or crp.id = :userId) "
                   + "and (:vehicleId is null or mo.vehicleEntity.id = :vehicleId or md.vehicleEntity.id = :vehicleId) "
                   + "and (:tireId is null or m.tireEntity.id = :tireId) "
                   + "order by mpe.id, m.id")
    List<TireMovementProcessEntity> getAllTireMovements(@NotNull final List<Long> branchesId,
                                                        @NotNull final LocalDate startDate,
                                                        @NotNull final LocalDate endDate,
                                                        @Nullable final Long userId,
                                                        @Nullable final Long vehicleId,
                                                        @Nullable final Long tireId,
                                                        @NotNull final Pageable pageable);
}
