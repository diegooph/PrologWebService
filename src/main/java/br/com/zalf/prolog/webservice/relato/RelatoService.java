package br.com.zalf.prolog.webservice.relato;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import br.com.zalf.prolog.models.Relato;

public class RelatoService {
	private RelatoDaoImpl dao = new RelatoDaoImpl();
	
	public boolean insert(Relato relato) {
		try {
			return dao.insert(relato);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
//	public boolean update(Relato relato) {
//		try {
//			return dao.update(relato);
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
	
//	public Relato getByCod(Long codigo, String token) {
//		try {
//			return dao.getByCod(codigo, token);
//		} catch (SQLException e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
	
//	public List<Relato> getAll() {
//		try {
//			return dao.getAll();
//		} catch (SQLException e) {
//			e.printStackTrace();
//			return new ArrayList<Relato>();
//		}
//	}
	
	public List<Relato> getByColaborador(Long cpf, String token, int limit, long offSet, double latitude, double longitude, boolean isOrderByDate) {
		try {
			return dao.getByColaborador(cpf, token, limit, offSet, latitude, longitude, isOrderByDate);
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<Relato>();
		}
	}
	
	public List<Relato> getAllByUnidade(LocalDate dataInicial, LocalDate dataFinal, String equipe,
			Long codUnidade, Long cpf, String token,long limit, long offset) {
		try {
			return dao.getAllByUnidade(dataInicial, dataFinal, equipe, codUnidade, cpf, token, limit, offset);
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<Relato>();
		}
	}
	
	public List<Relato> getAllExcetoColaborador(Long cpf, String token, int limit, long offSet, double latitude, double longitude, boolean isOrderByDate) {
		try {
			return dao.getAllExcetoColaborador(cpf, token, limit, offSet, latitude, longitude, isOrderByDate);
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<Relato>();
		}
	}
}
