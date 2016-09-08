package br.com.zalf.prolog.webservice.empresa;

import br.com.zalf.prolog.commons.colaborador.Empresa;
import br.com.zalf.prolog.commons.colaborador.Equipe;
import br.com.zalf.prolog.commons.colaborador.Funcao;
import br.com.zalf.prolog.commons.network.Request;

import java.sql.SQLException;
import java.util.List;

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
