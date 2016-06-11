package br.com.zalf.prolog.webservice.afericao;

import java.sql.SQLException;

import br.com.zalf.prolog.models.pneu.afericao.NovaAfericao;
import br.com.zalf.prolog.models.servico.Afericao;

public interface AfericaoDao {
	
	public boolean insert(Afericao afericao, Long codUnidade) throws SQLException;
	
	public NovaAfericao getNovaAfericao(String placa) throws SQLException;

}
