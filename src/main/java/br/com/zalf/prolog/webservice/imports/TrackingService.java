package br.com.zalf.prolog.webservice.imports;

import br.com.zalf.prolog.commons.colaborador.Colaborador;
import br.com.zalf.prolog.commons.imports.TrackingImport;

import java.sql.SQLException;
import java.util.List;

public class TrackingService {
	
	private TrackingDao dao = new TrackingDaoImpl();
	
	public boolean insertOrUpdate(List<TrackingImport> listTracking, Colaborador colaborador) {
		try {
			return dao.insertOrUpdateTracking(listTracking, colaborador);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

}
