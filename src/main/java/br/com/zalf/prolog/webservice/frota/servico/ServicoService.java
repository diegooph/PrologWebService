package br.com.zalf.prolog.webservice.frota.servico;

import java.sql.SQLException;
import java.util.List;

import br.com.zalf.prolog.models.frota.ItemManutencao;
import br.com.zalf.prolog.models.frota.ManutencaoHolder;
@Deprecated
public class ServicoService {
	private ServicoDaoImpl dao = new ServicoDaoImpl();

	public List<ManutencaoHolder> getManutencaoHolder(Long codUnidade, int limit, long offset, boolean isAbertos) {
		
		try {
			return dao.getManutencaoHolder(codUnidade, limit, offset, isAbertos);					
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public boolean consertaItem (ItemManutencao itemManutencao){
		try{
			return dao.consertaItem(itemManutencao);
		} catch(SQLException e){
			e.printStackTrace();
			return false;
		}
	}

}
