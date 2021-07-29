package br.com.zalf.prolog.webservice.v3.fleet.tire.pneuservico;

import org.jetbrains.annotations.NotNull;
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

    @NotNull
    @Query(value = "select md.cod_recapadora_destino from movimentacao_destino md " +
            "join movimentacao m on md.cod_movimentacao = m.codigo " +
            "where m.cod_pneu = :codPneu " +
            "and md.tipo_destino = :analise " +
            "order by m.codigo desc limit 1", nativeQuery = true)
    Long getCodigoRecapadora(@NotNull final Long codPneu, @NotNull final String analise);
}
