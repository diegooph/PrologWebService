package br.com.zalf.prolog.webservice.v3.fleet.transferenciaveiculo;

import br.com.zalf.prolog.webservice.v3.fleet.transferenciaveiculo._model.TransferenciaVeiculoInformacaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created on 2021-03-28
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface TransferenciaVeiculoInformacoesDao extends JpaRepository<TransferenciaVeiculoInformacaoEntity, Long> {
}
