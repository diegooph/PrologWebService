package br.com.zalf.prolog.webservice.frota.pneu.v3.dao;

import br.com.zalf.prolog.webservice.frota.pneu.v3._model.PneuFotoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created on 2021-03-12
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Repository
public interface PneuFotoV3Dao extends JpaRepository<PneuFotoEntity, Long> {
}
