package br.com.zalf.prolog.webservice.dao.interfaces;

import java.sql.SQLException;
import java.util.List;

import br.com.zalf.prolog.models.gsd.Pdv;

public interface PdvDao {
	List<Pdv> insertList(List<Pdv> pdvs) throws SQLException;
	boolean updateList(List<Pdv> pdvs) throws SQLException;
}
