package br.com.zalf.prolog.webservice.v3.frota.transferenciaveiculo;

import br.com.zalf.prolog.webservice.v3.frota.transferenciaveiculo._model.TransferenciaVeiculoProcessoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created on 2021-03-26
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface TransferenciaVeiculoDao extends JpaRepository<TransferenciaVeiculoProcessoEntity, Long> {

}
