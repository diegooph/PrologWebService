package br.com.zalf.prolog.webservice.v3.fleet.vehicle.makemodel;

import br.com.zalf.prolog.webservice.v3.fleet.vehicle.makemodel._model.VehicleModelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created on 2021-06-16
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Repository
public interface VehicleModelDao extends JpaRepository<VehicleModelEntity, Long> {
}
