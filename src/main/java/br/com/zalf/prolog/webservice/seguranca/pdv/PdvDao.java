package br.com.zalf.prolog.webservice.seguranca.pdv;

import java.sql.SQLException;
import java.util.List;

/**
 * Contém os métodos para manipular objetos PDV
 */
public interface PdvDao {

	List<Pdv> insertList(List<Pdv> pdvs) throws SQLException;

	boolean updateList(List<Pdv> pdvs) throws SQLException;
}
