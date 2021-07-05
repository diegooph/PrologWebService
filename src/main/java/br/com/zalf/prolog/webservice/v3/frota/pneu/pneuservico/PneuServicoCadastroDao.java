package br.com.zalf.prolog.webservice.v3.frota.pneu.pneuservico;

import br.com.zalf.prolog.webservice.v3.frota.pneu.pneuservico._modal.PneuServicoCadastroEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created on 2021-03-18
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Repository
public interface PneuServicoCadastroDao extends JpaRepository<PneuServicoCadastroEntity, Long> {
}
