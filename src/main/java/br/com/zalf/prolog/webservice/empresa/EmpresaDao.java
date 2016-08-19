package br.com.zalf.prolog.webservice.empresa;

import java.sql.SQLException;
import java.util.List;

import br.com.zalf.prolog.models.Equipe;
import br.com.zalf.prolog.models.Funcao;
import br.com.zalf.prolog.models.Request;
import br.com.zalf.prolog.models.relatorios.Empresa;

public interface EmpresaDao {
	
	public List<Equipe> getEquipesByCodUnidade (Long codUnidade) throws SQLException;
	
	public boolean updateEquipe (Request<Equipe> request) throws SQLException;
	
	public boolean createEquipe (Request<Equipe> request) throws SQLException;
	
	public List<Funcao> getFuncoesByCodUnidade (long codUnidade) throws SQLException;

	/**
	 * Busca os itens do Filtro (empresa, unidade, equipe)
	 * @param cpf do solicitante, busca a partir das permissões
	 * @param token para verificar se o solicitante esta devidamente logado
	 * @return list de Empresa, contendo os itens do filtro
	 * @throws SQLException caso não seja possível realizar a busca
	 */
	public List<Empresa> getFiltros(Long cpf) throws SQLException;
	
}
