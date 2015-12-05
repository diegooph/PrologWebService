package br.com.empresa.oprojeto.webservice.dao.interfaces;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

import br.com.empresa.oprojeto.webservice.domain.indicadores.Indicador;

public interface IndicadorDao {
	List<Indicador> getByMes (long cpf, Date dataInicial, Date dataFinal) 
			throws SQLException;
}
