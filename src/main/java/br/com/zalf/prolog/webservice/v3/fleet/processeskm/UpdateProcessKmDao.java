package br.com.zalf.prolog.webservice.v3.fleet.processeskm;

import br.com.zalf.prolog.webservice.v3.fleet.processeskm._model.UpdateProcessKmEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created on 2021-03-25
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface UpdateProcessKmDao extends JpaRepository<UpdateProcessKmEntity, Long> {
}
