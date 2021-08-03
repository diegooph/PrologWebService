package br.com.zalf.prolog.webservice.v3.fleet.tiremovement;

import br.com.zalf.prolog.webservice.v3.fleet.tiremovement._model.TireMovementDestinationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created on 2021-03-29
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Repository
public interface TireMovementDestinationDao extends JpaRepository<TireMovementDestinationEntity, Long> {

}
