package br.com.zalf.prolog.webservice.pneu.afericao;

import java.sql.SQLException;

import br.com.zalf.prolog.models.pneu.afericao.Afericao;
import br.com.zalf.prolog.models.pneu.afericao.NovaAfericao;

public interface AfericaoDao {
	
	public boolean insert(Afericao afericao, Long codUnidade) throws SQLException;
	
	public NovaAfericao getNovaAfericao(String placa) throws SQLException;

}
