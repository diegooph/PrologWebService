package br.com.zalf.prolog.webservice.v3.fleet.tireservice.movement;

import br.com.zalf.prolog.webservice.v3.fleet.tireservice.movement._model.TireServiceMovementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created on 2021-06-23
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Repository
public interface TireServiceMovementDao extends JpaRepository<TireServiceMovementEntity, Long> {
}
