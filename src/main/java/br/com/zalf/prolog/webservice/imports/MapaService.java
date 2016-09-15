package br.com.zalf.prolog.webservice.imports;

import br.com.zalf.prolog.commons.colaborador.Colaborador;
import br.com.zalf.prolog.commons.imports.MapaImport;

import java.sql.SQLException;
import java.util.List;

public class MapaService {
	
	private MapaDao dao = new MapaDaoImpl();
	
	public boolean insertOrUpdate(List<MapaImport> listMapas, Colaborador colaborador) {
		try {
			return dao.insertOrUpdateMapa(listMapas, colaborador);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

}
