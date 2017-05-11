package br.com.zalf.prolog.webservice.frota.pneu.afericao;

import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.Afericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.NovaAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.SelecaoPlacaAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Restricao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe AfericaoService responsavel por comunicar-se com a interface DAO
 */
public class AfericaoService {

	private AfericaoDao afericaoDao = new AfericaoDaoImpl();

	public boolean Insert(Afericao afericao, Long codUnidade){
		try{
			return afericaoDao.insert(afericao, codUnidade);
		}catch(SQLException e){
			e.printStackTrace();
			return false;
		}
	}

	public boolean updateKmAfericao(Afericao afericao) {
		try{
			return afericaoDao.update(afericao);
		}catch (SQLException e){
			e.printStackTrace();
			return false;
		}
	}
	
	public NovaAfericao getNovaAfericao(String placa){
		try{
			return afericaoDao.getNovaAfericao(placa);
		}catch(SQLException e){
			e.printStackTrace();
			return new NovaAfericao();
		}
	}
	
	public Afericao getByCod (Long codAfericao, Long codUnidade){
		try{
			return afericaoDao.getByCod(codAfericao, codUnidade);
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}
	}
	
	public SelecaoPlacaAfericao getSelecaoPlacaAfericao(Long codUnidade){
		try{
			return afericaoDao.getSelecaoPlacaAfericao(codUnidade);
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Afericao> getAfericoesByCodUnidadeByPlaca(List<String> codUnidades, List<String> placas, long limit, long offset){
		try{
			return afericaoDao.getAfericoesByCodUnidadeByPlaca(codUnidades, placas, limit, offset);
		}catch(SQLException e){
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	public Restricao getRestricoesByCodUnidade(Long codUnidade){
		try{
			return afericaoDao.getRestricoesByCodUnidade(codUnidade);
		}catch (SQLException e){
			e.printStackTrace();
			return null;
		}
	}
}
