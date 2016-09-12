package br.com.zalf.prolog.webservice.metas;

import br.com.zalf.prolog.commons.network.Request;
import br.com.zalf.prolog.entrega.produtividade.Metas;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MetaService {
private MetasDaoImpl dao = new MetasDaoImpl();
	
	public List<Metas<?>> getByCodUnidade(Long codUnidade, Long cpf, String token) {
		try {
			return dao.getByCodUnidade(codUnidade, cpf, token);
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<Metas<?>>();
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
