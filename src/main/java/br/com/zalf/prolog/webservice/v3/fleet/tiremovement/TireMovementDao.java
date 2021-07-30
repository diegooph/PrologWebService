package br.com.zalf.prolog.webservice.v3.fleet.tiremovement;

import br.com.zalf.prolog.webservice.v3.fleet.tiremovement._model.TireMovementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created on 2021-07-08
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Repository
public interface TireMovementDao extends JpaRepository<TireMovementEntity, Long> {
}
