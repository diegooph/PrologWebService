package br.com.zalf.prolog.webservice.gente.treinamento;

import br.com.zalf.prolog.gente.treinamento.Treinamento;
import br.com.zalf.prolog.gente.treinamento.TreinamentoColaborador;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe TreinamentoService responsavel por comunicar-se com a interface DAO
 */
public class TreinamentoService {

	private TreinamentoDao dao = new TreinamentoDaoImpl();
	
	public List<Treinamento> getVistosByColaborador(Long cpf) {
		try {
			return dao.getVistosColaborador(cpf);
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<Treinamento>();
		}
	}
	
	public List<Treinamento> getAll (LocalDate dataInicial, LocalDate dataFinal, String codFuncao,
			Long codUnidade, long limit, long offset) {
		try {
			return dao.getAll(dataInicial, dataFinal, codFuncao, codUnidade, limit, offset);
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
	
	public List<Treinamento> getNaoVistosByColaborador(Long cpf) {
		try {
			return dao.getNaoVistosColaborador(cpf);
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<Treinamento>();
		}
	}
	
	public boolean marcarTreinamentoComoVisto(Long codTreinamento, Long cpf) {
		try {
			return dao.marcarTreinamentoComoVisto(codTreinamento, cpf);
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

	public List<TreinamentoColaborador> getVisualizacoesByTreinamento(Long codTreinamento, Long codUnidade){
		try{
			return dao.getVisualizacoesByTreinamento(codTreinamento, codUnidade);
		}catch (SQLException e){
			e.printStackTrace();
			return null;
		}
	}

}
