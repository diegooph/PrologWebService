package br.com.zalf.prolog.webservice.v3.fleet.veiculo.diagrama;

import br.com.zalf.prolog.webservice.v3.fleet.veiculo.diagrama._model.DiagramaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiagramaDao extends JpaRepository<DiagramaEntity, Short> {
}
