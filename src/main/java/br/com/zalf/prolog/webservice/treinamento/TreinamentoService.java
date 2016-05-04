package br.com.zalf.prolog.webservice.treinamento;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.zalf.prolog.models.treinamento.Treinamento;
import br.com.zalf.prolog.models.treinamento.TreinamentoColaborador;

public class TreinamentoService {
	private TreinamentoDao dao = new TreinamentoDaoImpl();
	
	public List<Treinamento> getVistosByColaborador(Long cpf, String token) {
		try {
			return dao.getVistosColaborador(cpf, token);
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<Treinamento>();
		}
	}
	
	public List<Treinamento> getNaoVistosByColaborador(Long cpf, String token) {
		try {
			return dao.getNaoVistosColaborador(cpf, token);
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<Treinamento>();
		}
	}
	
	public boolean marcarTreinamentoComoVisto(TreinamentoColaborador treinamentoColaborador) {
		try {
			return dao.marcarTreinamentoComoVisto(treinamentoColaborador);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean insert(Treinamento treinamento) {
		try {
			return dao.insert(treinamento);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

}
