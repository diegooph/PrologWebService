package br.com.zalf.prolog.webservice.gente.solicitacaoFolga;

import br.com.zalf.prolog.commons.network.AbstractResponse;
import br.com.zalf.prolog.commons.network.Response;
import br.com.zalf.prolog.gente.solicitacao_folga.SolicitacaoFolga;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SolicitacaoFolgaService {

	private SolicitacaoFolgaDao dao = new SolicitacaoFolgaDaoImpl();
	
	public AbstractResponse insert(SolicitacaoFolga solicitacaoFolga) {
		try {
			return dao.insert(solicitacaoFolga);
		} catch (SQLException e) {
			e.printStackTrace();
			return Response.Error("Erro ao inserir a solicitação de folga.");
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
	
	public List<SolicitacaoFolga> getAll(LocalDate dataInicial, LocalDate dataFinal, Long codUnidade,
										 String codEquipe, String status, Long cpfColaborador){
		try{
			return dao.getAll(dataInicial, dataFinal, codUnidade, codEquipe, status, cpfColaborador);
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}
	}
	
	public boolean update(SolicitacaoFolga solicitacaoFolga){
		try{
			return dao.update(solicitacaoFolga);
		}catch(SQLException e){
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean delete(Long codigo){
		try{
			return dao.delete(codigo);
		}catch(SQLException e){
			e.printStackTrace();
			return false;
		}
	}
}
