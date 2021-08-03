package br.com.zalf.prolog.webservice.v3.fleet.vehicle.vehicletype;

import br.com.zalf.prolog.webservice.v3.fleet.vehicle.vehicletype._model.VehicleTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleTypeDao extends JpaRepository<VehicleTypeEntity, Long> {
}
