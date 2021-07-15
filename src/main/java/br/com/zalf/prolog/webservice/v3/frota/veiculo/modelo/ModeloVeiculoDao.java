package br.com.zalf.prolog.webservice.v3.frota.veiculo.modelo;

import br.com.zalf.prolog.webservice.v3.frota.veiculo.modelo._model.ModeloVeiculoEntity;
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
