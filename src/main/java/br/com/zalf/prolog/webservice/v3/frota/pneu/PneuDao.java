package br.com.zalf.prolog.webservice.v3.frota.pneu;

import br.com.zalf.prolog.webservice.frota.pneu._model.StatusPneu;
import br.com.zalf.prolog.webservice.v3.frota.pneu._model.PneuEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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

    @Query("select p from PneuEntity p                          " +
                   "join fetch p.modelo mop                     " +
                   "join fetch mop.marca map                    " +
                   "join fetch p.dimensao d                     " +
                   "join fetch p.unidade u                      " +
                   "join fetch p.empresa e                      " +
                   "left join fetch VeiculoPneuEntity vp        " +
                   "left join fetch vp.primaryKey.veiculo vei   " +
                   "left join fetch vei.veiculoTipo vt          " +
                   "left join fetch p.modeloBanda mob           " +
                   "left join fetch mob.marca mab               " +
                   "where (:statusPneu is null " +
                   "       and p.unidade.codigo in :codUnidades)" +
                   "or (:statusPneu is not null " +
                   "    and p.status = :statusPneu " +
                   "    and p.unidade.codigo in:codUnidades)")
    List<PneuEntity> getListagemPneusByStatus(@NotNull final List<Long> codUnidades,
                                              @Nullable final StatusPneu statusPneu);
}
