package br.com.zalf.prolog.webservice.imports;

import java.sql.SQLException;
import java.util.List;

import br.com.zalf.prolog.models.Colaborador;
import br.com.zalf.prolog.models.imports.TrackingImport;

public class TrackingService {
	
private TrackingDaoImpl dao = new TrackingDaoImpl();
	
	public boolean insertOrUpdate(List<TrackingImport> listTracking, Colaborador colaborador) {
		try {
			return dao.insertOrUpdateTracking(listTracking, colaborador);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

}
