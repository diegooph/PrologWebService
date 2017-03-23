package br.com.zalf.prolog.webservice.pneu.servico;

import br.com.zalf.prolog.frota.pneu.servico.PlacaServicoHolder;
import br.com.zalf.prolog.frota.pneu.servico.Servico;
import br.com.zalf.prolog.frota.pneu.servico.ServicoHolder;

import java.sql.SQLException;
import java.util.List;

/**
 * Classe ServicoService responsavel por comunicar-se com a interface DAO
 */
public class ServicoService {
	
	private ServicoDao dao = new ServicoDaoImpl();
	
	public PlacaServicoHolder getConsolidadoListaVeiculos(Long codUnidade){
		try{
			return dao.getPlacasServico(codUnidade);
		}
		catch(SQLException e){
			e.printStackTrace();
			return null;
		}
	}
	
	public ServicoHolder getServicosByPlaca(String placa, Long codUnidade){
		try{
			return dao.getServicosByPlaca(placa, codUnidade);
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Servico> getServicosAbertosByPlaca(String placa, String tipoServico){
		try{
			return dao.getServicosAbertosByPlaca(placa, tipoServico);
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}
	}
	
	public boolean insertManutencao(Servico servico, Long codUnidade, String token) {
		try{
			return dao.insertManutencao(servico, codUnidade, token);
		}catch(SQLException e){
			e.printStackTrace();
			return false;
		}
	}


}
