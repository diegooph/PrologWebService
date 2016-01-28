package br.com.zalf.prolog.webservice.dao.interfaces;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import br.com.zalf.prolog.models.relatorios.ConsolidadoHolder;
import br.com.zalf.prolog.models.relatorios.Empresa;

public interface RelatorioDao {

	
	public List<Empresa> getFiltros(Long cpf, String token) throws SQLException;
	
	public ConsolidadoHolder getRelatorioByPeriodo(LocalDate dataInicial, LocalDate dataFinal, String equipe,
			Long codUnidade, Long cpf, String token) throws SQLException;
		
}
