package br.com.zalf.prolog.webservice.services;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.zalf.prolog.models.Metas;
import br.com.zalf.prolog.models.Request;
import br.com.zalf.prolog.webservice.dao.MetasDaoImpl;

public class MetaService {
private MetasDaoImpl dao = new MetasDaoImpl();
	
	public List<Metas> getByCpf(Long cpf, String token) {
		try {
			return dao.getByCpf(cpf, token);
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<Metas>();
		}
	}
	public boolean updateByCod(Request<Metas> request) {
		try {
			return dao.updateByCod(request);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

}
