package br.com.zalf.prolog.webservice.v3.fleet.vehicle.tipoveiculo;

import br.com.zalf.prolog.webservice.v3.fleet.vehicle.tipoveiculo._model.TipoVeiculoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoVeiculoDao extends JpaRepository<TipoVeiculoEntity, Long> {
}
