package br.com.zalf.prolog.webservice.v3.fleet.movimentacao;

import br.com.zalf.prolog.webservice.v3.fleet.movimentacao._model.MovimentacaoOrigemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created on 2021-03-29
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Repository
public interface MovimentacaoOrigemDao extends JpaRepository<MovimentacaoOrigemEntity, Long> {

}
