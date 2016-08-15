package br.com.zalf.prolog.webservice.frota.servico;

import java.sql.SQLException;
import java.util.List;

import br.com.zalf.prolog.models.frota.ItemManutencao;
import br.com.zalf.prolog.models.frota.ManutencaoHolder;
@Deprecated
public interface ServicoDao {
	
	public List<ManutencaoHolder> getManutencaoHolder(Long codUnidade, int limit, long offset, boolean isAbertos) throws SQLException;
	public boolean consertaItem (ItemManutencao itemManutencao) throws SQLException;
	
	

}
