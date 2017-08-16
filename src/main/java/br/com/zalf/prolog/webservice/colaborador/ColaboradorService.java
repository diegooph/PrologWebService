package br.com.zalf.prolog.webservice.colaborador;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.errorhandling.exception.AmazonCredentialsException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * Classe ColaboradorService responsavel por comunicar-se com a interface DAO
 */
public class ColaboradorService {

	private final ColaboradorDao dao = Injection.provideColaboradorDao();
	
	public boolean insert(Colaborador colaborador) {
		try {
			return dao.insert(colaborador);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean update(Long cpfAntigo, Colaborador colaborador) {
		try {
			return dao.update(cpfAntigo, colaborador);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean delete(Long cpf) {
		try {
			return dao.delete(cpf);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public Colaborador getByCod(Long cpf) {
		try {
			return dao.getByCpf(cpf);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Colaborador> getAll(Long codUnidade) {
		try {
			return dao.getAll(codUnidade);
		} catch (SQLException e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

	public List<Colaborador> getMotoristasAndAjudantes(Long codUnidade) {
		try {
			return dao.getMotoristasAndAjudantes(codUnidade);
		}catch (SQLException e){
			e.printStackTrace();
			return null;
		}
	}
	
	public LoginHolder getLoginHolder(Long cpf) {
		try{
			return dao.getLoginHolder(cpf);
		}catch(SQLException | AmazonCredentialsException e){
			e.printStackTrace();
			return null;
		}
		
	}
}
