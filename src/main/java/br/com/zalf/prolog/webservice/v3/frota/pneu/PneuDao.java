package br.com.zalf.prolog.webservice.v3.frota.pneu;

import br.com.zalf.prolog.webservice.frota.pneu._model.StatusPneu;
import br.com.zalf.prolog.webservice.v3.frota.pneu._model.PneuEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created on 2021-03-12
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Repository
public interface PneuDao extends JpaRepository<PneuEntity, Long> {
    @NotNull
    @Query("select p from PneuEntity p " +
                   "join fetch p.dimensaoPneu " +
                   "join fetch p.modeloBanda " +
                   "join fetch p.modeloPneu " +
                   "where p.codUnidade in :codUnidades " +
                   "and (:statusPneu is null or p.status = :statusPneu)")
    List<PneuEntity> getPneusByStatus(@NotNull final List<Long> codUnidades,
                                      @Nullable final StatusPneu statusPneu,
                                      @NotNull final Pageable pageable);
}
