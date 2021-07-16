package br.com.zalf.prolog.webservice.v3.fleet.vehicle.modelo;

import br.com.zalf.prolog.webservice.v3.fleet.vehicle.modelo._model.ModeloVeiculoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created on 2021-06-16
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Repository
public interface ModeloVeiculoDao extends JpaRepository<ModeloVeiculoEntity, Long> {
}
