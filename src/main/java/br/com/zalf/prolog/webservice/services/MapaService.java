package br.com.zalf.prolog.webservice.services;

import java.sql.SQLException;
import java.util.List;

import br.com.zalf.prolog.models.Colaborador;
import br.com.zalf.prolog.webservice.dao.MapaDaoImpl;
import br.com.zalf.prolog.webservice.imports.Mapa;

public class MapaService {
	
	private MapaDaoImpl dao = new MapaDaoImpl();
	
	public boolean insertOrUpdate(List<Mapa> listMapas, Colaborador colaborador) {
		try {
			return dao.insertOrUpdate(listMapas, colaborador);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

}
