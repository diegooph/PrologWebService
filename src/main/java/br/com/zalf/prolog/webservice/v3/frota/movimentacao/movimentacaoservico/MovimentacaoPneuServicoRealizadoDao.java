package br.com.zalf.prolog.webservice.v3.frota.movimentacao.movimentacaoservico;

import br.com.zalf.prolog.webservice.v3.frota.movimentacao.movimentacaoservico._model.MovimentacaoPneuServicoRealizadoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created on 2021-06-23
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Repository
public interface MovimentacaoPneuServicoRealizadoDao extends JpaRepository<MovimentacaoPneuServicoRealizadoEntity, Long> {
}
