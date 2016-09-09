package br.com.zalf.prolog.webservice.pneu.pneu;

import br.com.zalf.prolog.frota.pneu.Pneu;

import java.sql.SQLException;
import java.util.List;

public interface PneuDao {
	
	public List<Pneu> getPneusByPlaca(String placa) throws SQLException;

}
