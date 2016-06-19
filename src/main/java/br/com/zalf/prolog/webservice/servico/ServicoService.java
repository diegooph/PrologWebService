package br.com.zalf.prolog.webservice.servico;

import java.sql.SQLException;

import br.com.zalf.prolog.models.pneu.servico.PlacaServicoHolder;
import br.com.zalf.prolog.models.pneu.servico.Servico;
import br.com.zalf.prolog.models.pneu.servico.ServicoHolder;

public class ServicoService {
	
	ServicoDaoImpl dao = new ServicoDaoImpl();
	
	public PlacaServicoHolder getConsolidadoListaVeiculos(Long codUnidade){
		try{
			return dao.getPlacasServico(codUnidade);
		}
		catch(SQLException e){
			e.printStackTrace();
			return new PlacaServicoHolder();
		}
	}
	
	public ServicoHolder getServicosByPlaca(String placa){
		try{
			return dao.getServicosByPlaca(placa);
		}catch(SQLException e){
			e.printStackTrace();
			return new ServicoHolder();
		}
	}
	
	public boolean insertManutencao(Servico servico) {
		try{
			return dao.insertManutencao(servico);
		}catch(SQLException e){
			e.printStackTrace();
			return false;
		}
	}


}
