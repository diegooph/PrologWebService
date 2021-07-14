package br.com.zalf.prolog.webservice.v3.fleet.kmprocessos;

import br.com.zalf.prolog.webservice.v3.fleet.kmprocessos._model.AlteracaoKmProcessoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created on 2021-03-25
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface AlteracaoKmProcessoDao extends JpaRepository<AlteracaoKmProcessoEntity, Long> {
}
