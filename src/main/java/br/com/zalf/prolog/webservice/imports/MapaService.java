package br.com.zalf.prolog.webservice.imports;

import java.sql.SQLException;
import java.util.List;

import br.com.zalf.prolog.models.Colaborador;
import br.com.zalf.prolog.models.imports.MapaImport;

public class MapaService {
	
	private MapaDaoImpl dao = new MapaDaoImpl();
	
	public boolean insertOrUpdate(List<MapaImport> listMapas, Colaborador colaborador) {
		try {
			return dao.insertOrUpdateMapa(listMapas, colaborador);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

}
