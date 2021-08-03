package br.com.zalf.prolog.webservice.v3.fleet.tireservice.movement;

import br.com.zalf.prolog.webservice.v3.fleet.tireservice.movement._model.TireServiceRetreaderEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Created on 2021-06-23
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Repository
public interface TireServiceRetreaderDao extends JpaRepository<TireServiceRetreaderEntity, Long> {
    @NotNull
    @Query(value = "select md.cod_recapadora_destino from movimentacao_destino md " +
            "join movimentacao m on md.cod_movimentacao = m.codigo " +
            "where m.cod_pneu = :tireId " +
            "and md.tipo_destino = 'ANALISE' " +
            "order by m.codigo desc limit 1", nativeQuery = true)
    Long getRetreaderId(@NotNull final Long tireId);
}
