package br.com.zalf.prolog.webservice.afericao;

import java.sql.SQLException;

import br.com.zalf.prolog.models.NovaAfericao;
import br.com.zalf.prolog.models.servico.Afericao;

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
}
