package br.com.zalf.prolog.webservice.v3.fleet.veiculo.tipoveiculo;

import br.com.zalf.prolog.webservice.v3.fleet.veiculo.tipoveiculo._model.TipoVeiculoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoVeiculoDao extends JpaRepository<TipoVeiculoEntity, Long> {
}
