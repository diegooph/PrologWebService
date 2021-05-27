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
                   "join fetch p.unidade u " +
                   "join fetch u.grupo " +
                   "join fetch p.dimensaoPneu " +
                   "join fetch p.modeloPneu mp " +
                   "join fetch mp.marca " +
                   "left join fetch p.modeloBanda mb " +
                   "left join fetch mb.marcaBanda " +
                   "left join fetch p.servicosRealizados psr " +
                   "left join fetch psr.tipoServico " +
                   "left join fetch p.veiculoPneuAplicado " +
                   "left join fetch p.movimentacoesPneu mov " +
                   "left join fetch mov.movimentacaoDestino " +
                   "where u.codigo in :codUnidades " +
                   "and (:statusPneu is null or p.status = :statusPneu)")
    List<PneuEntity> getPneusByStatus(@NotNull final List<Long> codUnidades,
                                      @Nullable final StatusPneu statusPneu,
                                      @NotNull final Pageable pageable);
}
