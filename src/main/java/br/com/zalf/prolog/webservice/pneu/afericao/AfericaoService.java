package br.com.zalf.prolog.webservice.pneu.afericao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.zalf.prolog.models.pneu.afericao.Afericao;
import br.com.zalf.prolog.models.pneu.afericao.NovaAfericao;
import br.com.zalf.prolog.models.pneu.afericao.SelecaoPlacaAfericao;

public class AfericaoService {
	AfericaoDaoImpl afericaoDaoImpl = new AfericaoDaoImpl();

	public boolean Insert(Afericao afericao, Long codUnidade){
		try{
			return afericaoDaoImpl.insert(afericao, codUnidade);
		}catch(SQLException e){
			e.printStackTrace();
			return false;
		}
	}
	
	public NovaAfericao getNovaAfericao(String placa){
		try{
			return afericaoDaoImpl.getNovaAfericao(placa);
		}catch(SQLException e){
			e.printStackTrace();
			return new NovaAfericao();
		}
	}
	
	public SelecaoPlacaAfericao getSelecaoPlacaAfericao(Long codUnidade){
		try{
			return afericaoDaoImpl.getSelecaoPlacaAfericao(codUnidade);
		}catch(SQLException e){
			e.printStackTrace();
			return new SelecaoPlacaAfericao();
		}
	}
	
	public List<Afericao> getAfericoesByCodUnidadeByPlaca(List<String> codUnidades, List<String> placas, long limit, long offset){
		try{
			return afericaoDaoImpl.getAfericoesByCodUnidadeByPlaca(codUnidades, placas, limit, offset);
		}catch(SQLException e){
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
}
