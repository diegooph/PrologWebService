package br.com.zalf.prolog.webservice.v3.frota.movimentacao;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created on 2021-03-26
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface MovimentacaoDao extends JpaRepository<MovimentacaoEntity, Long> {

}
