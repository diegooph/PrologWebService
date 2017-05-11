package br.com.zalf.prolog.webservice.gente.solicitacaoFolga;

import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/**
 * Classe SolicitacaoFolgaService responsavel por comunicar-se com a interface DAO
 */
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

	public List<SolicitacaoFolga> getByColaborador(Long cpf) {
		try {
			return dao.getByColaborador(cpf);
		} catch (SQLException e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}
	
	public List<SolicitacaoFolga> getAll(LocalDate dataInicial, LocalDate dataFinal, Long codUnidade,
										 String codEquipe, String status, Long cpfColaborador){
		try{
			return dao.getAll(dataInicial, dataFinal, codUnidade, codEquipe, status, cpfColaborador);
		}catch(SQLException e){
			e.printStackTrace();
			return Collections.emptyList();
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
