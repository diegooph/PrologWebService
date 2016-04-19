package br.com.zalf.prolog.webservice.dao.interfaces;

import java.sql.SQLException;
import java.util.List;

import br.com.zalf.prolog.models.Request;
import br.com.zalf.prolog.models.frota.ManutencaoHolder;

public interface FrotaDao {
	
	public List<ManutencaoHolder> getManutencaoHolder(Long codUnidade, int limit, long offset, boolean isAbertos) throws SQLException;
	public boolean consertaItem (Request<?> request) throws SQLException;
	
	

}
