package br.com.zalf.prolog.webservice.seguranca.gsd;

import br.com.zalf.prolog.webservice.commons.questoes.Pergunta;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe GsdService responsavel por comunicar-se com a interface DAO
 */
public class GsdService {

	private GsdDao dao = new GsdDaoImpl();
	
	public boolean insert(Gsd gsd) {
		try {
			return dao.insert(gsd);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<Gsd> getByColaborador(Long cpf, String token) {
		try {
			return dao.getByColaborador(cpf, token);
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
	
	public List<Gsd> getByAvaliador(Long cpf, String token) {
		try {
			return dao.getByAvaliador(cpf, token);
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<Gsd>();
		}
	}
	
	public List<Gsd> getAllExcetoAvaliador(Long cpf, String token) {
		try {
			return dao.getAllExcetoAvaliador(cpf, token);
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<Gsd>();
		}
	}
	
	public List<Gsd> getAll(LocalDate dataInicial, LocalDate dataFinal, String equipe,
			Long codUnidade, long limit, long offset){
		try {
			return dao.getAll(dataInicial, dataFinal, equipe, codUnidade, limit, offset);
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<Gsd>();
		}
	}

	public List<Pergunta> getPerguntas() {
		try {
			return dao.getPerguntas();
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<Pergunta>();
		}
	}
	
}
