package br.com.zalf.prolog.webservice.v3.frota.movimentacao;

import br.com.zalf.prolog.webservice.v3.frota.movimentacao._model.MovimentacaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created on 2021-07-08
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Repository
public interface MovimentacaoDao extends JpaRepository<MovimentacaoEntity, Long> {
}
