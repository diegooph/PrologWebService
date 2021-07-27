package br.com.zalf.prolog.webservice.v3.frota.pneu.pneuservico;

import br.com.zalf.prolog.webservice.v3.frota.pneu.pneuservico._model.PneuServicoRealizadoEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Created on 2021-03-18
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Repository
public interface PneuServicoRealizadoDao extends JpaRepository<PneuServicoRealizadoEntity, Long> {

//acho que faz mais sentido ficar aqui do que em PneuDao
    @Query("select md.recapadora.codigo from MovimentacaoDestinoEntity md " +
                   "join MovimentacaoEntity m on md.codMovimentacao = m.codigo " +
                   "where m.pneu.codigo = :codPneu " +
                   "and md.tipoDestino = :analise " +
                   "order by m.codigo desc ")
    Long getCodigoRecapadora(@NotNull final Long codPneu,
                             @NotNull final String analise,
                             @NotNull final Pageable pageable);
}
