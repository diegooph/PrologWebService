package br.com.zalf.prolog.webservice.v3.fleet.vehicle.diagrama;

import br.com.zalf.prolog.webservice.v3.fleet.vehicle.diagrama._model.DiagramaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiagramaDao extends JpaRepository<DiagramaEntity, Short> {
}
