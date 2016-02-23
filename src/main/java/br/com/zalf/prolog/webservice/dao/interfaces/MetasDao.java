package br.com.zalf.prolog.webservice.dao.interfaces;

import java.sql.SQLException;
import java.util.List;

import br.com.zalf.prolog.models.Metas;
import br.com.zalf.prolog.models.Request;

public interface MetasDao {
	
	public List<Metas<?>> getByCpf(Long cpf, String token) throws SQLException;
	
	public boolean updateByCod(Request<Metas> request) throws SQLException;

}
