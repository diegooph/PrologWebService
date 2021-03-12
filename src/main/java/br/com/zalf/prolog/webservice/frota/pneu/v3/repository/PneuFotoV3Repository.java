package br.com.zalf.prolog.webservice.frota.pneu.v3.repository;

import br.com.zalf.prolog.webservice.frota.pneu.v3._model.PneuFotoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created on 2021-03-12
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Repository
public interface PneuFotoV3Repository extends JpaRepository<PneuFotoEntity, Long> {
}
