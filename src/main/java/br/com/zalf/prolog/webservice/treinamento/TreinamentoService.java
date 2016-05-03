package br.com.zalf.prolog.webservice.services;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.zalf.prolog.models.treinamento.Treinamento;
import br.com.zalf.prolog.models.treinamento.TreinamentoColaborador;
import br.com.zalf.prolog.webservice.dao.TreinamentoDaoImpl;

public class TreinamentoService {
	private TreinamentoDaoImpl dao = new TreinamentoDaoImpl();
	
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

}
