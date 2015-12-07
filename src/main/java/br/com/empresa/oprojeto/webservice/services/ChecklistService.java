package br.com.empresa.oprojeto.webservice.services;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.empresa.oprojeto.models.checklist.Checklist;
import br.com.empresa.oprojeto.webservice.dao.ChecklistDaoImpl;
import br.com.empresa.oprojeto.webservice.dao.interfaces.BaseDao;

public class ChecklistService {
	private BaseDao<Checklist> dao = new ChecklistDaoImpl();
	
	public boolean save(Checklist checklist) {
		try {
			return dao.save(checklist);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean delete(Long codigo) {
		try {
			return dao.delete(codigo);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public Checklist getByCod(Long codigo) {
		try {
			return dao.getByCod(codigo);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Checklist> getAll() {
		List<Checklist> checklists;
		try {
			checklists = dao.getAll();
			return checklists;
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<Checklist>();
		}
	}
}
