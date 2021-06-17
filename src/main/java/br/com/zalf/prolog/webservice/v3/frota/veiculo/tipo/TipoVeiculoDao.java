package br.com.zalf.prolog.webservice.v3.frota.veiculo.tipo;

import br.com.zalf.prolog.webservice.v3.frota.veiculo.tipo._model.TipoVeiculoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoVeiculoDao extends JpaRepository<TipoVeiculoEntity, Long> {
}
