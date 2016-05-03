package br.com.zalf.prolog.webservice.imports;

import java.sql.SQLException;
import java.util.List;

import br.com.zalf.prolog.models.Colaborador;

public class TrackingService {
	
private TrackingDaoImpl dao = new TrackingDaoImpl();
	
	public boolean insertOrUpdate(List<Tracking> listTracking, Colaborador colaborador) {
		try {
			return dao.insertOrUpdateTracking(listTracking, colaborador);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

}
