package br.com.zalf.prolog.webservice.v3.fleet.tire;

import br.com.zalf.prolog.webservice.frota.pneu._model.StatusPneu;
import br.com.zalf.prolog.webservice.v3.fleet.tire._model.PneuEntity;
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
public interface TireDao extends JpaRepository<PneuEntity, Long> {
    @NotNull
    @Query("select p from PneuEntity p " +
                   "join fetch p.unidade u " +
                   "join fetch u.group " +
                   "join fetch p.dimensaoPneu " +
                   "join fetch p.modeloPneu mp " +
                   "join fetch mp.marca " +
                   "left join fetch p.modeloBanda mb " +
                   "left join fetch mb.marcaBanda " +
                   "left join fetch p.servicosRealizados psr " +
                   "left join fetch psr.tipoServico " +
                   "left join fetch p.veiculoPneuAplicado " +
                   "left join fetch p.movimentacoesPneu mov " +
                   "left join fetch mov.movimentacaoDestino movD " +
                   "left join fetch movD.recapadora " +
                   // Fizemos JOIN com essas propriedades por mais que não precisava, para não gerar N+1 requests ao BD.
                   "left join fetch mov.movimentacaoOrigem " +
                   "left join fetch mov.movimentacaoProcesso movP " +
                   "left join fetch movP.colaboradorRealizacaoProcesso c " +
                   "where u.id in :branchesId " +
                   "and (:tireStatus is null or p.status = :tireStatus) " +
                   "order by p.codigo asc")
    List<PneuEntity> getAllTires(@NotNull final List<Long> branchesId,
                                 @Nullable final StatusPneu tireStatus,
                                 @NotNull final Pageable pageable);

    @Modifying
    @Query("update PneuEntity p set p.status = :tireStatus where p.codigo = :tireId")
    void updateTireStatus(@NotNull final Long tireId,
                          @NotNull final StatusPneu tireStatus);
}
