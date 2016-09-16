package br.com.zalf.prolog.webservice.seguranca.gsd.pdv;

import br.com.zalf.prolog.seguranca.gsd.Pdv;

import java.sql.SQLException;
import java.util.List;

/**
 * Contém os métodos para manipular objetos PDV
 */
public interface PdvDao {

	/**
	 * Insere uma lista de PDV no banco de dados
	 * @param pdvs lista de PDV
	 * @return lista dos PDV inseridos com o código gerado pelo banco de dados
	 * @throws SQLException caso não seja possível realizar o insert
	 */
	List<Pdv> insertList(List<Pdv> pdvs) throws SQLException;

	/**
	 * Operation not supported yet
	 * @param pdvs lista de PDV
	 * @return resultado da requisição
	 * @throws SQLException caso não seja possível realizar o update
	 */
	boolean updateList(List<Pdv> pdvs) throws SQLException;
}
