package br.com.zalf.prolog.webservice.pneu;

import java.sql.SQLException;
import java.util.List;

import br.com.zalf.prolog.models.pneu.Pneu;

public interface PneuDao {
	
	public List<Pneu> getPneusByPlaca(String placa) throws SQLException;

}
