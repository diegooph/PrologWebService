package br.com.zalf.prolog.webservice.pneu.afericao;

import br.com.zalf.prolog.frota.pneu.afericao.Afericao;
import br.com.zalf.prolog.frota.pneu.afericao.NovaAfericao;
import br.com.zalf.prolog.frota.pneu.afericao.SelecaoPlacaAfericao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AfericaoService {

	AfericaoDao afericaoDao = new AfericaoDaoImpl();

	public boolean Insert(Afericao afericao, Long codUnidade){
		try{
			return afericaoDao.insert(afericao, codUnidade);
		}catch(SQLException e){
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
			return new SelecaoPlacaAfericao();
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
}
