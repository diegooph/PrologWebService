package br.com.zalf.prolog.webservice.v3.fleet.tire;

import br.com.zalf.prolog.webservice.frota.pneu._model.StatusPneu;
import br.com.zalf.prolog.webservice.v3.fleet.tire._model.TireEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created on 2021-03-12
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Repository
public interface TireDao extends JpaRepository<TireEntity, Long> {
    @NotNull
    @Query("select t from TireEntity t " +
                   "join fetch t.branchEntity b " +
                   "join fetch b.group " +
                   "join fetch t.tireSizeEntity " +
                   "join fetch t.tireModelEntity tm " +
                   "join fetch tm.marca " +
                   "left join fetch t.treadModelEntity ttm " +
                   "left join fetch ttm.marcaBanda " +
                   "left join fetch t.servicosRealizados psr " +
                   "left join fetch psr.tipoServico " +
                   "left join fetch t.vehicleApplied " +
                   "left join fetch t.movimentacoesPneu mov " +
                   "left join fetch mov.movimentacaoDestino movD " +
                   "left join fetch movD.recapadora " +
                   // Fizemos JOIN com essas propriedades por mais que não precisava, para não gerar N+1 requests ao BD.
                   "left join fetch mov.movimentacaoOrigem " +
                   "left join fetch mov.movimentacaoProcesso movP " +
                   "left join fetch movP.colaboradorRealizacaoProcesso c " +
                   "where b.id in :branchesId " +
                   "and (:tireStatus is null or t.tireStatus = :tireStatus) " +
                   "order by t.id asc")
    List<TireEntity> getAllTires(@NotNull final List<Long> branchesId,
                                 @Nullable final StatusPneu tireStatus,
                                 @NotNull final Pageable pageable);

    @Modifying
    @Query("update TireEntity t set t.tireStatus = :tireStatus where t.id = :tireId")
    void updateTireStatus(@NotNull final Long tireId,
                          @NotNull final StatusPneu tireStatus);
}
