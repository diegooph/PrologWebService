package br.com.zalf.prolog.webservice.pneu.afericao;

import br.com.zalf.prolog.frota.pneu.afericao.Afericao;
import br.com.zalf.prolog.frota.pneu.afericao.NovaAfericao;

import java.sql.SQLException;

public interface AfericaoDao {
	
	public boolean insert(Afericao afericao, Long codUnidade) throws SQLException;
	
	public NovaAfericao getNovaAfericao(String placa) throws SQLException;

}
