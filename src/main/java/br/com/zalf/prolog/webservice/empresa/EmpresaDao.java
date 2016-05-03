package br.com.zalf.prolog.webservice.empresa;

import java.sql.SQLException;
import java.util.List;

import br.com.zalf.prolog.models.Equipe;
import br.com.zalf.prolog.models.Request;
import br.com.zalf.prolog.models.Veiculo;

public interface EmpresaDao {
	
	public List<Equipe> getEquipesByCodUnidade (Request<?> request) throws SQLException;
	
	public boolean updateEquipe (Request<Equipe> request) throws SQLException;
	
	public boolean createEquipe (Request<Equipe> request) throws SQLException;
	
	public List<Veiculo> getVeiculosByCodUnidade(Request<?> request) throws SQLException;
	
	public boolean updateVeiculo (Request<Veiculo> request) throws SQLException;

}
