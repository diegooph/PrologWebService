package br.com.zalf.prolog.webservice.gente.faleConosco;

import br.com.zalf.prolog.gente.fale_conosco.FaleConosco;

import java.sql.SQLException;
import java.util.List;

public class FaleConoscoService {
	private FaleConoscoDaoImpl dao = new FaleConoscoDaoImpl();

	public boolean insert(FaleConosco faleConosco, Long codUnidade) {
		try {
			return dao.insert(faleConosco, codUnidade);
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
	public boolean insertFeedback(FaleConosco faleConosco, Long codUnidade){
		try{
			return dao.insertFeedback(faleConosco, codUnidade);
		}catch (SQLException e){
			e.printStackTrace();
			return false;
		}
	}

	public List<FaleConosco> getAll(long dataInicial, long dataFinal, int limit, int offset,
									String equipe, Long codUnidade, String status, String categoria){

		try{
			return dao.getAll(dataInicial, dataFinal, limit, offset,equipe, codUnidade, status, categoria);
		}catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}

	public List<FaleConosco> getByColaborador(Long cpf, String status) {
		try {
			return dao.getByColaborador(cpf, status);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
