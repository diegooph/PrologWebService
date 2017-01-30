package br.com.zalf.prolog.webservice.colaborador;

import br.com.zalf.prolog.commons.Report;
import br.com.zalf.prolog.commons.colaborador.Colaborador;
import br.com.zalf.prolog.commons.colaborador.Funcao;
import br.com.zalf.prolog.commons.login.LoginHolder;
import br.com.zalf.prolog.commons.network.Request;
import br.com.zalf.prolog.webservice.CsvWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Classe ColaboradorService responsavel por comunicar-se com a interface DAO
 */
public class ColaboradorService {

	private ColaboradorDaoImpl dao = new ColaboradorDaoImpl();

	void test(Long codUnidade, OutputStream outputStream) {
		try {
			dao.test(codUnidade, outputStream);
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
	}

	Report testReport(Long codUnidade) {
		try {
			return dao.testReport(codUnidade);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
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
			return dao.getByCod(cpf);
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
	
	public Funcao getFuncaoByCod(Long codigo) {
		try {
			return dao.getFuncaoByCod(codigo);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public boolean verifyLogin(long cpf, Date dataNascimento) {
		try {
			return dao.verifyLogin(cpf, dataNascimento);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public LoginHolder getLoginHolder(Long cpf) {
		try{
			return dao.getLoginHolder(cpf);
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}
		
	}
}
