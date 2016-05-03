package br.com.zalf.prolog.webservice.services;

import java.sql.SQLException;
import java.util.List;

import br.com.zalf.prolog.models.Colaborador;
import br.com.zalf.prolog.webservice.dao.TrackingDaoImpl;
import br.com.zalf.prolog.webservice.imports.Tracking;

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
