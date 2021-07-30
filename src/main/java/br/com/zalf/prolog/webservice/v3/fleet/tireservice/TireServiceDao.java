package br.com.zalf.prolog.webservice.v3.fleet.tireservice;

import br.com.zalf.prolog.webservice.v3.fleet.tireservice._model.TireServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created on 2021-03-18
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Repository
public interface TireServiceDao extends JpaRepository<TireServiceEntity, Long> {
}