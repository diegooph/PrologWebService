package br.com.zalf.prolog.webservice.v3.frota.movimentacao;

import br.com.zalf.prolog.webservice.v3.frota.movimentacao._model.MovimentacaoDestinoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created on 2021-03-29
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface MovimentacaoDestinoDao extends JpaRepository<MovimentacaoDestinoEntity, Long> {

}
