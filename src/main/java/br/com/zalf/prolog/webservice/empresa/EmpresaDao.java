package br.com.zalf.prolog.webservice.empresa;

import java.sql.SQLException;
import java.util.List;

import br.com.zalf.prolog.models.Equipe;
import br.com.zalf.prolog.models.Funcao;
import br.com.zalf.prolog.models.Request;

public interface EmpresaDao {
	
	public List<Equipe> getEquipesByCodUnidade (Long codUnidade) throws SQLException;
	
	public boolean updateEquipe (Request<Equipe> request) throws SQLException;
	
	public boolean createEquipe (Request<Equipe> request) throws SQLException;
	
	public List<Funcao> getFuncoesByCodUnidade (long codUnidade) throws SQLException;
	
}
