package br.com.zalf.prolog.webservice.frota.v3.pneu;

import br.com.zalf.prolog.webservice.frota.v3.pneu._model.PneuEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created on 2021-03-12
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Repository
public interface PneuV3Dao extends JpaRepository<PneuEntity, Long> {
}
