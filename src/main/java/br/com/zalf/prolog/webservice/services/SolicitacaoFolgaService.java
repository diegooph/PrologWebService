package br.com.zalf.prolog.webservice.services;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.zalf.prolog.models.SolicitacaoFolga;
import br.com.zalf.prolog.webservice.dao.SolicitacaoFolgaDaoImpl;

public class SolicitacaoFolgaService {
private SolicitacaoFolgaDaoImpl dao = new SolicitacaoFolgaDaoImpl();
	
	public boolean insert(SolicitacaoFolga solicitacaoFolga) {
		try {
			return dao.insert(solicitacaoFolga);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<SolicitacaoFolga> getByColaborador(Long cpf, String token) {
		try {
			return dao.getByColaborador(cpf, token);
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<SolicitacaoFolga>();
		}
	}
}
