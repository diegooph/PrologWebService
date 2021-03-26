package br.com.zalf.prolog.webservice.frota.v3.pneuservico;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created on 2021-03-18
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Repository
public interface PneuServicoRealizadoV3Dao
        extends JpaRepository<PneuServicoRealizadoEntity, PneuServicoRealizadoEntity.PK> {
}
