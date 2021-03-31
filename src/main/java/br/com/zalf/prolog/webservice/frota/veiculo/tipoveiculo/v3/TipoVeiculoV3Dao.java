package br.com.zalf.prolog.webservice.frota.veiculo.tipoveiculo.v3;

import br.com.zalf.prolog.webservice.frota.veiculo.tipoveiculo.v3._model.TipoVeiculoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoVeiculoV3Dao extends JpaRepository<TipoVeiculoEntity, Long> {
}
