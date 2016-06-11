package br.com.zalf.prolog.webservice.servico;

import java.sql.SQLException;

import br.com.zalf.prolog.models.PlacaServicoHolder;
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

}
