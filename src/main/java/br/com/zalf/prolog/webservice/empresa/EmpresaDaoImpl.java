package br.com.zalf.prolog.webservice.empresa;

import br.com.zalf.prolog.commons.colaborador.*;
import br.com.zalf.prolog.commons.imports.HolderMapaTracking;
import br.com.zalf.prolog.commons.imports.MapaTracking;
import br.com.zalf.prolog.commons.login.Autenticacao;
import br.com.zalf.prolog.commons.network.AbstractResponse;
import br.com.zalf.prolog.commons.network.Request;
import br.com.zalf.prolog.commons.network.Response;
import br.com.zalf.prolog.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.autenticacao.AutenticacaoDao;
import br.com.zalf.prolog.webservice.autenticacao.AutenticacaoDaoImpl;

import javax.ws.rs.core.NoContentException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmpresaDaoImpl extends DatabaseConnection implements EmpresaDao {

	private final String BUSCA_EQUIPES_BY_COD_UNIDADE = "SELECT E.CODIGO, E.NOME "
			+ "FROM EQUIPE E JOIN UNIDADE U ON U.CODIGO = E.COD_UNIDADE "
			+ "WHERE U.CODIGO = ?";

	private final String UPDATE_EQUIPE = "UPDATE EQUIPE SET NOME = (?) "
			+ "FROM TOKEN_AUTENTICACAO TA WHERE CODIGO = ?	"
			+ "AND TA.CPF_COLABORADOR=? "
			+ "AND TA.TOKEN=?";

	private final String BUSCA_FUNCOES_BY_COD_UNIDADE = "SELECT F.CODIGO, F.NOME "
			+ "FROM UNIDADE_FUNCAO UF JOIN FUNCAO F ON F.CODIGO = UF.COD_FUNCAO "
			+ "WHERE UF.COD_UNIDADE = ? "
			+ "ORDER BY F.NOME";

	public static final String BUSCA_EMPRESA_REGIONAL_UNIDADE_EQUIPE_BY_CPF = ""
			+ "select emp.codigo as cod_empresa, emp.nome nome_empresa, "
			+ "reg.codigo as cod_regional, reg.regiao nome_regional, "
			+ "u.codigo as cod_unidade, u.nome as nome_unidade, "
			+ "e.codigo as cod_equipe, e.nome as nome_equipe "
			+ "from colaborador c join unidade u on u.codigo = c.cod_unidade "
			+ "join empresa emp on emp.codigo = u.cod_empresa "
			+ "join regional reg on reg.codigo = u.cod_regional "
			+ "join equipe e on e.codigo = c.cod_equipe where c.cpf = ?";

	public static final String BUSCA_UNIDADE_BY_REGIONAL = " SELECT DISTINCT U.CODIGO, U.NOME "
			+ " FROM UNIDADE U JOIN REGIONAL REG ON REG.CODIGO = U.COD_REGIONAL "
			+ " JOIN EMPRESA E ON U.COD_EMPRESA = E.CODIGO"
			+ " WHERE REG.CODIGO = ? AND E.CODIGO = ? ORDER BY 2 ";

	public static final String BUSCA_EQUIPE_BY_UNIDADE = "SELECT DISTINCT E.NOME "
			+ "FROM EQUIPE E JOIN UNIDADE U ON U.CODIGO = E.COD_UNIDADE "
			+ "WHERE U.CODIGO = ?"
			+ "ORDER BY 1";

	public static final String BUSCA_EMPRESA_REGIONAL_UNIDADE_BY_CPF = "select emp.codigo as cod_empresa, emp.nome as nome_empresa,"
			+ " reg.codigo as cod_regional, reg.regiao nome_regional,"
			+ " u.codigo as cod_unidade, u.nome as nome_unidade "
			+ "from colaborador c join unidade u on u.codigo = c.cod_unidade "
			+ "join empresa emp on emp.codigo = u.cod_empresa "
			+ "join regional reg on reg.codigo = u.cod_regional "
			+ "where c.cpf = ?";

	public static final String BUSCA_CODIGO_PERMISSAO_BY_CPF = "select c.cod_permissao "
			+ "from colaborador c "
			+ "where c.cpf = ?";

	public static final String BUSCA_REGIONAL = "select distinct reg.codigo, reg.regiao, e.codigo as codigo_empresa, e.nome as nome_empresa "
			+ "from unidade u join empresa e on e.codigo = u.cod_empresa "
			+ "join regional reg on reg.codigo = u.cod_regional	"
			+ "where e.codigo in (select c.cod_empresa	"
			+ "from colaborador c	where c.cpf = ?"
			+ " ORDER BY 1)";

	public static final String BUSCA_REGIONAL_BY_CPF = "select distinct reg.codigo, reg.regiao, e.codigo as cod_empresa, e.nome as nome_empresa "
			+ "from regional reg "
			+ "left join unidade u on u.cod_regional = reg.codigo "
			+ "join empresa e on e.codigo = u.cod_empresa join colaborador c on c.cod_unidade = u.codigo and c.cpf=? "
			+ "where reg.codigo in ( "
			+ "select r.codigo "
			+ "from colaborador c join unidade u on u.codigo = c.cod_unidade "
			+ "join regional r on r.codigo = u.cod_regional	"
			+ "where c.cpf=?)";

	public List<Equipe> getEquipesByCodUnidade (Long codUnidade) throws SQLException{
		List<Equipe> listEquipe = new ArrayList<>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(BUSCA_EQUIPES_BY_COD_UNIDADE);
			stmt.setLong(1, codUnidade);
			rSet = stmt.executeQuery();
			while(rSet.next()){
				listEquipe.add(createEquipe(rSet));
			}
		}
		finally {
			closeConnection(conn, stmt, rSet);
		}
		return listEquipe;
	}

	public boolean updateEquipe (Request<Equipe> request) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(UPDATE_EQUIPE);
			stmt.setString(1, request.getObject().getNome());
			stmt.setLong(2, request.getObject().getCodigo());
			stmt.setLong(3, request.getCpf());
			stmt.setString(4, request.getToken());
			int count = stmt.executeUpdate();
			if(count == 0){
				throw new SQLException("Erro ao atualizar a equipe");
			}
		}
		finally {
			closeConnection(conn, stmt, null);
		}
		return true;
	}

	private Equipe createEquipe (ResultSet rset) throws SQLException{
		Equipe equipe = new Equipe();
		equipe.setCodigo(rset.getLong("CODIGO"));
		equipe.setNome(rset.getString("NOME"));
		return equipe;
	}

	public boolean createEquipe(Request<Equipe> request) throws SQLException{
		Autenticacao autenticacao = new Autenticacao("", request.getCpf(),
				request.getToken());
		AutenticacaoDao autenticacaoDao = new AutenticacaoDaoImpl();
		if (autenticacaoDao.verifyIfTokenExists(request.getToken())) {
			Connection conn = null;
			PreparedStatement stmt = null;
			try {
				conn = getConnection();

				stmt = conn.prepareStatement("INSERT INTO EQUIPE "
						+ "(NOME, COD_UNIDADE) VALUES "
						+ "(?,?) ");
				stmt.setString(1, request.getObject().getNome());
				stmt.setLong(2, request.getCodUnidade());
				int count = stmt.executeUpdate();
				if(count == 0){
					throw new SQLException("Erro ao inserir a equipe");
				}
			}
			finally {
				closeConnection(conn, stmt, null);
			}
			return true;
		}
		return false;
	}

	//TODO: Verificar a viabilidade de implementar um método para exclusão de uma equipe, 
	//a equipe está ligada como fk de colaborador e fk de calendário

	public List<Funcao> getFuncoesByCodUnidade (long codUnidade) throws SQLException{
		List<Funcao> listFuncao = new ArrayList<>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(BUSCA_FUNCOES_BY_COD_UNIDADE);
			stmt.setLong(1, codUnidade);
			rSet = stmt.executeQuery();
			while(rSet.next()){
				listFuncao.add(createFuncao(rSet));
			}
		}
		finally {
			closeConnection(conn, stmt, rSet);
		}
		return listFuncao;
	}

	private Funcao createFuncao(ResultSet rSet) throws SQLException{
		Funcao funcao = new Funcao();
		funcao.setCodigo(rSet.getLong("CODIGO"));
		funcao.setNome(rSet.getString("NOME"));
		return funcao;
	}

	public List<Setor> getSetorByCodUnidade(Long codUnidade) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		List<Setor> setores = new ArrayList<>();
		Setor setor = null;
		try{
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT * FROM setor WHERE cod_unidade = ?\n" +
					"ORDER BY nome");
			stmt.setLong(1, codUnidade);
			rSet = stmt.executeQuery();
			while (rSet.next()){
				setor = new Setor();
				setor.setCodigo(rSet.getLong("codigo"));
				setor.setNome(rSet.getString("nome"));
				setores.add(setor);
			}
		}finally {
			closeConnection(conn, stmt, rSet);
		}
		return setores;
	}

	public AbstractResponse insertSetor(String nome, Long codUnidade)throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try{
			conn = getConnection();
			stmt = conn.prepareStatement("INSERT INTO SETOR(cod_unidade, nome) VALUES (?,?) RETURNING CODIGO;");
			stmt.setLong(1, codUnidade);
			stmt.setString(2, nome);
			rSet = stmt.executeQuery();
			if (rSet.next()){
				return ResponseWithCod.Ok("Setor inserido com sucesso", rSet.getLong("codigo"));
			}else{
				return Response.Error("Erro ao inserir o setor");
			}
		}finally {
			closeConnection(conn, stmt, rSet);
		}
	}

	public List<HolderMapaTracking> getResumoAtualizacaoDados(int ano, int mes, Long codUnidade) throws SQLException, NoContentException{
		Connection conn = null;
		PreparedStatement stmt= null;
		ResultSet rSet = null;
		List<HolderMapaTracking> holders = null;
		HolderMapaTracking holder = null;
		List<MapaTracking> mapas = null;
		MapaTracking mapa = null;
		Integer tempMapa = null;
		Integer tempTracking = null;

		try{
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT A.DATA AS DATA, M.MAPA,M.placa, TRACKING.MAPA_TRACKING\n" +
					"FROM MAPA M FULL OUTER JOIN\n" +
					"\t(SELECT DISTINCT DATA AS DATA_TRACKING, MAPA AS MAPA_TRACKINg, código_transportadora as codigo FROM TRACKING) AS TRACKING ON MAPA_TRACKING = M.MAPA\n" +
					"\tJOIN aux_data A ON (A.data = M.data OR A.DATA = tracking.DATA_TRACKING)\n" +
					"\tWHERE (tracking.codigo = ? or m.cod_unidade = ?) and extract(YEAR FROM a.data) = ? and extract(MONTH FROM a.data) = ?\n" +
					"\tORDER BY 1;");
			stmt.setLong(1, codUnidade);
			stmt.setLong(2, codUnidade);
			stmt.setInt(3, ano);
			stmt.setInt(4, mes);
			rSet = stmt.executeQuery();
			while (rSet.next()){
				tempMapa = rSet.getInt("mapa");
				tempTracking = rSet.getInt("mapa_tracking");
				if (tempMapa == 0){
					tempMapa = null;
				}
				if (tempTracking == 0){
					tempTracking = null;
				}

				if (holders == null){// primeira iteração do rSet
					holders = new ArrayList<>();
					holder = new HolderMapaTracking();
					holder.setData(rSet.getDate("DATA"));
					mapas  = new ArrayList<>();
					mapa = new MapaTracking();
					mapa.setMapa(tempMapa);
					mapa.setPlaca(rSet.getString("placa"));
					mapa.setTracking(tempTracking);
					mapas.add(mapa);
				}else{// a partir da primeira linha do rset
					if (rSet.getDate("data").equals(holder.getData())){
						mapa = new MapaTracking();
						mapa.setMapa(tempMapa);
						mapa.setTracking(tempTracking);
						mapa.setPlaca(rSet.getString("placa"));
						mapas.add(mapa);
					}else{// mudou a data, fechar as listas e começar novamente
						holder.setMapas(mapas);
						holders.add(holder);
						holder = new HolderMapaTracking();
						holder.setData(rSet.getDate("data"));
						mapas = new ArrayList<>();
						mapa = new MapaTracking();
						mapa.setMapa(tempMapa);
						mapa.setTracking(tempTracking);
						mapa.setPlaca(rSet.getString("placa"));
						mapas.add(mapa);
					}
				}
			}
			if (holder != null) {
                holder.setMapas(mapas);
                holders.add(holder);
            }
		}finally {
			closeConnection(conn,stmt,rSet);
		}
		if (holder == null){
			throw new NoContentException("Sem dados para retornar");
		}
		return holders;
	}

	/**
	 * Busca dos filtros para os relatórios a partir da permissão cadastrada.
	 */
	public List<Empresa> getFiltros(Long cpf) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;

		List<Empresa> listEmpresa = new ArrayList<>();
		int codPermissao = 0;

		try{
			conn = getConnection();
			stmt = conn.prepareStatement(BUSCA_CODIGO_PERMISSAO_BY_CPF);
			stmt.setLong(1, cpf);
			rSet = stmt.executeQuery();

			while(rSet.next()){ // rset com o código da permissão
				codPermissao = rSet.getInt("COD_PERMISSAO");
			}
			switch (codPermissao) {
				case 0:
					listEmpresa = getPermissao0(cpf);
					break;
				case 1:
					listEmpresa = getPermissao1(cpf);
					break;
				case 2:
					listEmpresa = getPermissao2(cpf);
					break;
				case 3:
					listEmpresa = getPermissao3(cpf);
					break;
				default:
					break;
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return listEmpresa;
	}
	// buscar permisões para colaboradores com permissão = 3 = tudo
	public List<Empresa> getPermissao3(Long cpf) throws SQLException{
		List<Empresa> listEmpresa = new ArrayList<>();
		Empresa empresa = new Empresa();
		List<Regional> listRegional = new ArrayList<>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;

		try{
			conn = getConnection();
			stmt = conn.prepareStatement(BUSCA_REGIONAL);
			stmt.setLong(1, cpf.longValue());
			rSet = stmt.executeQuery();

			while(rSet.next()){ // rset com os codigos e nomes da regionais
				Regional regional = new Regional();
				empresa.setNome(rSet.getString("NOME_EMPRESA"));
				empresa.setCodigo(rSet.getInt("CODIGO_EMPRESA"));
				regional.setCodigo(rSet.getLong("CODIGO"));
				regional.setNome(rSet.getString("REGIAO"));
				setUnidadesByRegional(regional, empresa.getCodigo());
				listRegional.add(regional);
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		empresa.setListRegional(listRegional);
		listEmpresa.add(empresa);
		return listEmpresa;
	}
	// burcar permissoes para colaboradores com permissao = 2 = regional
	public List<Empresa> getPermissao2(Long cpf) throws SQLException{
		List<Empresa> listEmpresa = new ArrayList<>();
		Empresa empresa = new Empresa();
		List<Regional> listRegional = new ArrayList<>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;

		try{
			conn = getConnection();
			stmt = conn.prepareStatement(BUSCA_REGIONAL_BY_CPF);
			stmt.setLong(1, cpf);
			stmt.setLong(2, cpf);
			rSet = stmt.executeQuery();

			while(rSet.next()){ // rset com os codigos e nomes da regionais
				Regional regional = new Regional();
				empresa.setCodigo(rSet.getInt("COD_EMPRESA"));
				empresa.setNome(rSet.getString("NOME_EMPRESA"));
				regional.setCodigo(rSet.getLong("CODIGO"));
				regional.setNome(rSet.getString("REGIAO"));
				setUnidadesByRegional(regional, empresa.getCodigo());
				listRegional.add(regional);
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		empresa.setListRegional(listRegional);
		listEmpresa.add(empresa);
		return listEmpresa;
	}
	// buscar permissoes para colaboradores com permissao = 1 = local, gerente
	public List<Empresa> getPermissao1(Long cpf) throws SQLException{
		List<Empresa> listEmpresa = new ArrayList<>();
		List<Regional> listRegional = new ArrayList<>();
		List<Unidade> listUnidade = new ArrayList<>();
		Empresa empresa = new Empresa();
		Regional regional = new Regional();
		Unidade unidade = new Unidade();

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;

		try{
			conn = getConnection();
			stmt = conn.prepareStatement(BUSCA_EMPRESA_REGIONAL_UNIDADE_BY_CPF);
			stmt.setLong(1, cpf);
			rSet = stmt.executeQuery();

			while(rSet.next()){ // rset com os codigos e nomes da regionais

				empresa.setCodigo(rSet.getInt("COD_EMPRESA"));
				empresa.setNome(rSet.getString("NOME_EMPRESA"));
				regional.setCodigo(rSet.getLong("COD_REGIONAL"));
				regional.setNome(rSet.getString("NOME_REGIONAL"));
				unidade.setCodigo(rSet.getLong("COD_UNIDADE"));
				unidade.setNome(rSet.getString("NOME_UNIDADE"));
				setEquipesByUnidade(unidade);
				listEmpresa.add(empresa);
				listUnidade.add(unidade);
				listRegional.add(regional);
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		empresa.setListRegional(listRegional);
		regional.setListUnidade(listUnidade);
		return listEmpresa;
	}
	// buscar permissoes para colaboradores com permissao = 0 = local, supervisor, busca apenas a sala que o cpf pertence
	public List<Empresa> getPermissao0(Long cpf) throws SQLException{
		List<Empresa> listEmpresa = new ArrayList<>();
		List<Regional> listRegional = new ArrayList<>();
		List<Unidade> listUnidade = new ArrayList<>();
		List<String> listEquipe = new ArrayList<>();
		Empresa empresa = new Empresa();
		Regional regional = new Regional();
		Unidade unidade = new Unidade();
		String equipe = "";

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;

		try{
			conn = getConnection();
			stmt = conn.prepareStatement(BUSCA_EMPRESA_REGIONAL_UNIDADE_EQUIPE_BY_CPF);
			stmt.setLong(1, cpf);
			rSet = stmt.executeQuery();

			while(rSet.next()){ // rset com os codigos e nomes da regionais

				empresa.setCodigo(rSet.getInt("COD_EMPRESA"));
				regional.setCodigo(rSet.getLong("COD_REGIONAL"));
				regional.setNome(rSet.getString("NOME_REGIONAL"));
				unidade.setCodigo(rSet.getLong("COD_UNIDADE"));
				unidade.setNome(rSet.getString("NOME_UNIDADE"));
				equipe = rSet.getString("NOME_EQUIPE");
				empresa.setNome(rSet.getString("NOME_EMPRESA"));
				listEmpresa.add(empresa);
				listUnidade.add(unidade);
				listRegional.add(regional);
				listEquipe.add(equipe);
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		empresa.setListRegional(listRegional);
		regional.setListUnidade(listUnidade);
		unidade.setListEquipe(listEquipe);
		return listEmpresa;
	}

	public void setUnidadesByRegional(Regional regional, int codEmpresa) throws SQLException{

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		List<Unidade> listUnidades = new ArrayList<>();
		try{

			conn = getConnection();
			stmt = conn.prepareStatement(BUSCA_UNIDADE_BY_REGIONAL);
			stmt.setLong(1, regional.getCodigo());
			stmt.setInt(2, codEmpresa);
			rSet = stmt.executeQuery();

			while(rSet.next()){
				Unidade unidade = new Unidade();
				unidade.setCodigo(rSet.getLong("CODIGO"));
				unidade.setNome(rSet.getString("NOME"));
				setEquipesByUnidade(unidade);
				listUnidades.add(unidade);
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		regional.setListUnidade(listUnidades);

	}

	public void setEquipesByUnidade (Unidade unidade) throws SQLException{

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		List<String> listEquipes = new ArrayList<>();
		try{

			conn = getConnection();
			stmt = conn.prepareStatement(BUSCA_EQUIPE_BY_UNIDADE);
			stmt.setLong(1, unidade.getCodigo());
			rSet = stmt.executeQuery();

			while(rSet.next()){
				String equipe = rSet.getString("NOME");
				listEquipes.add(equipe);
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		unidade.setListEquipe(listEquipes);
	}
}
