package br.com.zalf.prolog.webservice.colaborador;

import br.com.zalf.prolog.commons.colaborador.Colaborador;
import br.com.zalf.prolog.commons.colaborador.Equipe;
import br.com.zalf.prolog.commons.colaborador.Funcao;
import br.com.zalf.prolog.commons.colaborador.Setor;
import br.com.zalf.prolog.commons.login.LoginHolder;
import br.com.zalf.prolog.commons.util.DateUtils;
import br.com.zalf.prolog.permissao.Visao;
import br.com.zalf.prolog.permissao.pilares.FuncaoApp;
import br.com.zalf.prolog.permissao.pilares.Pilar;
import br.com.zalf.prolog.permissao.pilares.Pilares;
import br.com.zalf.prolog.permissao.pilares.Seguranca;
import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.empresa.EmpresaDaoImpl;
import br.com.zalf.prolog.webservice.seguranca.relato.RelatoDao;
import br.com.zalf.prolog.webservice.seguranca.relato.RelatoDaoImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Classe ColaboradorDaoImpl, responsavel pela execução da lógica e comunicação com a interface de dados
 */
public class ColaboradorDaoImpl extends DatabaseConnection implements ColaboradorDao {

	@Override
	public boolean insert(Colaborador colaborador) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();

			stmt = conn.prepareStatement("INSERT INTO COLABORADOR "
					+ "(CPF, MATRICULA_AMBEV, MATRICULA_TRANS, DATA_NASCIMENTO, "
					+ "DATA_ADMISSAO, DATA_DEMISSAO, STATUS_ATIVO, NOME, "
					+ "COD_SETOR, COD_FUNCAO, COD_UNIDADE, COD_PERMISSAO, COD_EMPRESA, COD_EQUIPE) VALUES "
					+ "(?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
			setStatementItems(stmt, colaborador);
			int count = stmt.executeUpdate();
			if(count == 0){
				throw new SQLException("Erro ao inserir o colaborador");
			}
		}
		finally {
			closeConnection(conn, stmt, null);
		}
		return true;
	}

	@Override
	public boolean update(Long cpfAntigo, Colaborador colaborador) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("UPDATE COLABORADOR SET "
					+ "CPF = ?, MATRICULA_AMBEV = ?, MATRICULA_TRANS = ?, "
					+ "DATA_NASCIMENTO = ?, DATA_ADMISSAO = ?, DATA_DEMISSAO = ?, "
					+ "STATUS_ATIVO = ?, NOME = ?, COD_SETOR = ?, "
					+ "COD_FUNCAO = ?, COD_UNIDADE = ?, COD_PERMISSAO = ?, "
					+ "COD_EMPRESA = ?, COD_EQUIPE = ? "
					+ "WHERE CPF = ?;");
			stmt.setLong(1, colaborador.getCpf());
			stmt.setInt(2, colaborador.getMatriculaAmbev());
			stmt.setInt(3, colaborador.getMatriculaTrans());
			stmt.setDate(4, DateUtils.toSqlDate(colaborador.getDataNascimento()));
			stmt.setDate(5, DateUtils.toSqlDate(colaborador.getDataAdmissao()));

			// Só vai ter data de demissão quando estiver fazendo um update
			// em um colaborador que já está deletado (inativo). 
			if (colaborador.getDataDemissao() != null)
				stmt.setDate(6, DateUtils.toSqlDate(colaborador.getDataDemissao()));
			else
				stmt.setDate(6, null);
			stmt.setBoolean(7, colaborador.isAtivo());
			stmt.setString(8, colaborador.getNome());
			stmt.setLong(9, colaborador.getSetor().getCodigo());
			stmt.setLong(10, colaborador.getFuncao().getCodigo());
			stmt.setLong(11, colaborador.getCodUnidade());
			stmt.setLong(12, colaborador.getCodPermissao());
			stmt.setLong(13, colaborador.getCodEmpresa());
			stmt.setLong(14, colaborador.getEquipe().getCodigo());
			stmt.setLong(15, cpfAntigo);

			int count = stmt.executeUpdate();

			if(count == 0){
				throw new SQLException("Erro ao atualizar o colaborador");
			}
		}
		finally {
			closeConnection(conn, stmt, null);
		}
		return true;
	}

	/**
	 * Para manter histórico no banco de dados, não é feita exclusão de colaborador,
	 * setamos o status para inativo.
	 */
	@Override
	public boolean delete(Long cpf) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("UPDATE COLABORADOR SET "
					+ "STATUS_ATIVO = FALSE "
					+ "WHERE CPF = ?;");
			stmt.setLong(1, cpf);
			return (stmt.executeUpdate() > 0);
		} finally {
			closeConnection(conn, stmt, null);
		}
	}

	/**
	 * Busca um colaborador por código
	 * @param cpf chave a ser buscada no banco de dados
	 * @return um colaborador
	 * @throws SQLException
	 */
	@Override
	public Colaborador getByCod(Long cpf) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT C.CPF, C.MATRICULA_AMBEV, C.MATRICULA_TRANS, "
					+ "C.DATA_NASCIMENTO, C.DATA_ADMISSAO, C.DATA_DEMISSAO, C.STATUS_ATIVO, "
					+ "C.NOME AS NOME_COLABORADOR, E.NOME AS NOME_EQUIPE, E.CODIGO AS COD_EQUIPE, S.NOME AS NOME_SETOR, S.CODIGO AS COD_SETOR, "
					+ "C.COD_FUNCAO, C.COD_UNIDADE, F.NOME AS NOME_FUNCAO, C.COD_PERMISSAO AS PERMISSAO, C.COD_EMPRESA "
					+ "FROM COLABORADOR C JOIN FUNCAO F ON C.COD_FUNCAO = F.CODIGO "
					+ " JOIN EQUIPE E ON E.CODIGO = C.COD_EQUIPE "
					+ " JOIN SETOR S ON S.CODIGO = C.COD_SETOR AND C.COD_UNIDADE = S.COD_UNIDADE "
					+ "WHERE CPF = ? AND C.STATUS_ATIVO = TRUE");
			stmt.setLong(1, cpf);
			rSet = stmt.executeQuery();
			if (rSet.next()) {
				Colaborador c = createColaborador(rSet);
				c.setVisao(getVisaoByCpf(c.getCpf()));
				return c;
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return null;
	}

	/**
	 * Busca todos os colaboradores de uma unidade
	 */
	@Override
	public List<Colaborador> getAll(Long codUnidade) throws SQLException {
		List<Colaborador> list = new ArrayList<>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT C.CPF, C.MATRICULA_AMBEV, C.MATRICULA_TRANS, "
					+ "C.DATA_NASCIMENTO, C.DATA_ADMISSAO, C.DATA_DEMISSAO, C.STATUS_ATIVO, "
					+ "C.NOME AS NOME_COLABORADOR, E.NOME AS NOME_EQUIPE, E.CODIGO AS COD_EQUIPE, S.NOME AS NOME_SETOR, S.CODIGO AS COD_SETOR, "
					+ "C.COD_FUNCAO, C.COD_UNIDADE, F.NOME AS NOME_FUNCAO, C.COD_PERMISSAO AS PERMISSAO, C.COD_EMPRESA "
					+ " FROM COLABORADOR C JOIN FUNCAO F ON F.CODIGO = C.cod_funcao "
					+ " JOIN EQUIPE E ON E.CODIGO = C.COD_EQUIPE "
					+ " JOIN SETOR S ON S.CODIGO = C.COD_SETOR AND C.COD_UNIDADE = S.COD_UNIDADE "
					+ "WHERE C.COD_UNIDADE = ? ORDER BY C.NOME; ");
			stmt.setLong(1, codUnidade);
			rSet = stmt.executeQuery();
			while (rSet.next()) {
				Colaborador c = createColaborador(rSet);
				list.add(c);
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return list;
	}

	@Override
	public List<Colaborador> getAtivosByUnidade(Long codUnidade, String token, Long cpf) throws SQLException {
		List<Colaborador> list = new ArrayList<>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT C.CPF, C.MATRICULA_AMBEV, C.MATRICULA_TRANS, "
					+ "C.DATA_NASCIMENTO, C.DATA_ADMISSAO, C.DATA_DEMISSAO, C.STATUS_ATIVO, "
					+ "C.NOME AS NOME_COLABORADOR, E.NOME AS NOME_EQUIPE, E.CODIGO AS COD_EQUIPE, S.NOME AS NOME_SETOR, S.CODIGO AS COD_SETOR, "
					+ "C.COD_FUNCAO, C.COD_UNIDADE, F.NOME AS NOME_FUNCAO, C.COD_PERMISSAO AS PERMISSAO, C.COD_EMPRESA "
					+ "FROM COLABORADOR C JOIN TOKEN_AUTENTICACAO TA "
					+ "ON ? = TA.CPF_COLABORADOR AND ? = TA.TOKEN JOIN FUNCAO F ON F.CODIGO = C.COD_UNIDADE "
					+ " JOIN EQUIPE E ON E.CODIGO = C.COD_EQUIPE "
					+ " JOIN SETOR S ON S.CODIGO = C.COD_SETOR AND C.COD_UNIDADE = S.COD_UNIDADE "
					+ "WHERE C.COD_UNIDADE = ? AND C.STATUS_ATIVO = TRUE ORDER BY C.NOME; ");
			stmt.setLong(1, cpf);
			stmt.setString(2, token);
			stmt.setLong(3, codUnidade);
			rSet = stmt.executeQuery();
			while (rSet.next()) {
				Colaborador c = createColaborador(rSet);
				list.add(c);
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return list;
	}

	@Override
	public boolean verifyLogin(long cpf, Date dataNascimento) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT EXISTS(SELECT C.NOME FROM "
					+ "COLABORADOR C WHERE C.CPF = ? AND DATA_NASCIMENTO = ? "
					+ "AND C.STATUS_ATIVO = TRUE)");
			stmt.setLong(1, cpf);
			stmt.setDate(2, DateUtils.toSqlDate(dataNascimento));
			rSet = stmt.executeQuery();
			if (rSet.next()) {
				return rSet.getBoolean("EXISTS");
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return false;
	}

	@Override
	public Funcao getFuncaoByCod(Long codigo) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT * FROM FUNCAO F JOIN "
					+ "COLABORADOR C ON F.CODIGO = C.COD_FUNCAO "
					+ "WHERE C.CPF = ?");
			stmt.setLong(1, codigo);
			rSet = stmt.executeQuery();
			if (rSet.next()) {
				Funcao f = createFuncao(rSet);
				return f;
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return null;
	}

	@Override
	public LoginHolder getLoginHolder(Long cpf) throws SQLException {
		LoginHolder loginHolder = new LoginHolder();
		loginHolder.colaborador = getByCod(cpf);

		if (verificaSeFazRelato(loginHolder.colaborador.getVisao().getPilares())) {
			RelatoDao relatoDao = new RelatoDaoImpl();
			loginHolder.alternativasRelato = relatoDao.getAlternativas(
					loginHolder.colaborador.getCodUnidade(),
					loginHolder.colaborador.getSetor().getCodigo());
		}

		return loginHolder;
	}

	private Visao getVisaoByCpf(Long cpf)throws SQLException {
		Visao visao = new Visao();
		List<Pilar> pilares = new ArrayList<>();
		ResultSet rSet = null;
		Connection conn = null;
		PreparedStatement stmt = null;
		EmpresaDaoImpl empresaDao = new EmpresaDaoImpl();

		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT DISTINCT PP.codigo AS COD_PILAR, PP.pilar, FP.codigo AS COD_FUNCAO, FP.funcao FROM cargo_funcao_prolog CF\n" +
					"JOIN PILAR_PROLOG PP ON PP.codigo = CF.cod_pilar_prolog\n" +
					"JOIN FUNCAO_PROLOG FP ON FP.cod_pilar = PP.codigo AND FP.codigo = CF.cod_funcao_prolog\n" +
					"JOIN colaborador C ON C.cod_unidade = CF.cod_unidade AND CF.cod_funcao_colaborador = C.cod_funcao\n" +
					"WHERE C.CPF = ?\n" +
					"ORDER BY PP.pilar, FP.funcao");
			stmt.setLong(1, cpf);
			rSet = stmt.executeQuery();
			pilares = empresaDao.createPilares(rSet);
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		visao.setPilares(pilares);
		return visao;
	}


	private Funcao createFuncao(ResultSet rSet) throws SQLException {
		Funcao f = new Funcao();
		f.setCodigo(rSet.getLong("CODIGO"));
		f.setNome(rSet.getString("NOME"));
		return f;
	}

	private Colaborador createColaborador(ResultSet rSet) throws SQLException {
		Colaborador c = new Colaborador();
		c.setAtivo(rSet.getBoolean("STATUS_ATIVO"));

		Funcao funcao = new Funcao();
		funcao.setCodigo(rSet.getLong("COD_FUNCAO"));
		funcao.setNome(rSet.getString("NOME_FUNCAO"));
		c.setFuncao(funcao);

		Equipe equipe = new Equipe();
		equipe.setCodigo(rSet.getLong("COD_EQUIPE"));
		equipe.setNome(rSet.getString("NOME_EQUIPE"));
		c.setEquipe(equipe);

		Setor setor = new Setor();
		setor.setCodigo(rSet.getLong("COD_SETOR"));
		setor.setNome(rSet.getString("NOME_SETOR"));
		c.setSetor(setor);

		c.setCpf(rSet.getLong("CPF"));
		c.setDataNascimento(rSet.getDate("DATA_NASCIMENTO"));
		c.setCodUnidade(rSet.getLong("COD_UNIDADE"));
		c.setNome(rSet.getString("NOME_COLABORADOR"));
		c.setMatriculaAmbev(rSet.getInt("MATRICULA_AMBEV"));
		c.setMatriculaTrans(rSet.getInt("MATRICULA_TRANS"));
		c.setDataAdmissao(rSet.getDate("DATA_ADMISSAO"));
		c.setDataDemissao(rSet.getDate("DATA_DEMISSAO"));
		c.setCodPermissao(rSet.getLong("PERMISSAO"));
		c.setCodEmpresa(rSet.getLong("COD_EMPRESA"));
		return c;
	}

	private void setStatementItems(PreparedStatement stmt, Colaborador c) throws SQLException {
		stmt.setLong(1, c.getCpf());
		stmt.setInt(2, c.getMatriculaAmbev());
		stmt.setInt(3, c.getMatriculaTrans());
		stmt.setDate(4, DateUtils.toSqlDate(c.getDataNascimento()));
		stmt.setDate(5, DateUtils.toSqlDate(c.getDataAdmissao()));
		stmt.setDate(6, DateUtils.toSqlDate(c.getDataDemissao()));
		stmt.setBoolean(7, c.isAtivo());
		stmt.setString(8, c.getNome());
		stmt.setLong(9, c.getSetor().getCodigo());
		stmt.setLong(10, c.getFuncao().getCodigo());
		stmt.setLong(11, c.getCodUnidade());
		stmt.setLong(12, c.getCodPermissao());
		stmt.setLong(13, c.getCodEmpresa());
		stmt.setLong(14, c.getEquipe().getCodigo());
	}

	private boolean verificaSeFazRelato(List<Pilar> pilares) {
		for (Pilar pilar : pilares) {
			if (pilar.codigo == Pilares.SEGURANCA) {
				for (FuncaoApp funcao : pilar.funcoes) {
					if (funcao.getCodigo() == Seguranca.Relato.NOVO_RELATO) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean verifyIfCpfExists(Long cpf, Long codUnidade) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try{
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT EXISTS(SELECT C.NOME FROM "
					+ "COLABORADOR C WHERE C.CPF = ? AND C.cod_unidade = ?)");
			stmt.setLong(1, cpf);
			stmt.setLong(2, codUnidade);
			rSet = stmt.executeQuery();
			if(rSet.next()){
				return rSet.getBoolean("EXISTS");
			}
		}finally {
			closeConnection(conn, stmt, rSet);
		}
		return false;
	}
}