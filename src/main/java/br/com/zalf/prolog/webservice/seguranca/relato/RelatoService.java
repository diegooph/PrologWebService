package br.com.zalf.prolog.webservice.seguranca.relato;

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
	
	public boolean delete(Long codRelato) {
		try {
			return dao.delete(codRelato);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public Relato getByCod(Long codigo) {
		try {
			return dao.getByCod(codigo);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Relato> getAll(Long codUnidade, int limit, long offset, double latitude, double longitude, boolean isOrderByDate, String status) {
		try {
			return dao.getAll(codUnidade, limit, offset, latitude, longitude, isOrderByDate, status);
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<Relato>();
		}
	}
	
	public List<Relato> getByColaborador(Long cpf, int limit, long offset, double latitude, double longitude, boolean isOrderByDate, String status) {
		try {
			return dao.getByColaborador(cpf, limit, offset, latitude, longitude, isOrderByDate, status);
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<Relato>();
		}
	}
	
	public boolean classificaRelato(Relato relato){
		try{
			return dao.classificaRelato(relato);
		}catch(SQLException e){
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean fechaRelato(Relato relato){
		try{
			return dao.fechaRelato(relato);
		}catch(SQLException e){
			e.printStackTrace();
			return false;
		}
	}

	public List<Relato> getAllByUnidade(LocalDate dataInicial, LocalDate dataFinal, String equipe,
			Long codUnidade,long limit, long offset, String status) {
		try {
			return dao.getAllByUnidade(dataInicial, dataFinal, equipe, codUnidade, limit, offset, status);
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<Relato>();
		}
	}
	
	public List<Relato> getAllExcetoColaborador(Long cpf, int limit, long offset, double latitude, double longitude, boolean isOrderByDate, String status) {
		try {
			return dao.getAllExcetoColaborador(cpf, limit, offset, latitude, longitude, isOrderByDate, status);
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<Relato>();
		}
	}
}
