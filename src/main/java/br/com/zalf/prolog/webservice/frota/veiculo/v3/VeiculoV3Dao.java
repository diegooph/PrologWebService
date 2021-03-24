package br.com.zalf.prolog.webservice.frota.veiculo.v3;

import br.com.zalf.prolog.webservice.frota.veiculo.v3._model.VeiculoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VeiculoV3Dao extends JpaRepository<VeiculoEntity, Long> {
}
