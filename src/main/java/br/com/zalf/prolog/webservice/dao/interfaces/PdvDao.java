package br.com.zalf.prolog.webservice.dao.interfaces;

import java.sql.SQLException;
import java.util.List;

import br.com.empresa.oprojeto.models.gsd.Pdv;

public interface PdvDao {
	boolean insertList(List<Pdv> pdvs, Long codigoGsd) throws SQLException;
	boolean updateList(List<Pdv> pdvs, Long codigoGsd) throws SQLException;
}
