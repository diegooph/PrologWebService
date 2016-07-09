package br.com.zalf.prolog.webservice.gente.faleConosco;

import java.util.ArrayList;
import java.util.List;

import br.com.zalf.prolog.models.FaleConosco;

public class FaleConoscoService {
	private FaleConoscoDaoImpl dao = new FaleConoscoDaoImpl();
	
	public boolean insert(FaleConosco faleConosco) {
		try {
			return dao.insert(faleConosco);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
//	public boolean update(FaleConosco faleConosco) {
//		try {
//			return dao.update(faleConosco);
//		} catch (SQLException e) {
//			e.printStackTrace();
//			return false;
//		}
//	}
//	
//	public boolean delete(Long codigo) {
//		try {
//			return dao.delete(codigo);
//		} catch (SQLException e) {
//			e.printStackTrace();
//			return false;
//		}
//	}
	
//	public FaleConosco getByCod(Long codigo, String token) {
//		try {
//			return dao.getByCod(codigo, token);
//		} catch (SQLException e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
//	
//	public List<FaleConosco> getAll() {
//		try {
//			return dao.getAll();
//		} catch (SQLException e) {
//			e.printStackTrace();
//			return new ArrayList<FaleConosco>();
//		}
//	}
	
	public List<FaleConosco> getByColaborador(long cpf) {
		try {
			return dao.getByColaborador(cpf);
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<FaleConosco>();
		}
	}
}
