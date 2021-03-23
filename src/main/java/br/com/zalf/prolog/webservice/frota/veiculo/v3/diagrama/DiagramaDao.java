package br.com.zalf.prolog.webservice.frota.veiculo.v3.diagrama;

import br.com.zalf.prolog.webservice.frota.veiculo.v3.diagrama._model.DiagramaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiagramaDao extends JpaRepository<DiagramaEntity, Short> {
}
