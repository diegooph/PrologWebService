package br.com.zalf.prolog.webservice.v3.fleet.vehicle.vehiclelayout;

import br.com.zalf.prolog.webservice.v3.fleet.vehicle.vehiclelayout._model.VehicleLayoutEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleLayoutDao extends JpaRepository<VehicleLayoutEntity, Short> {
}
