package br.com.zalf.prolog.webservice.colaborador;

import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.commons.util.DateUtils;
import br.com.zalf.prolog.webservice.empresa.EmpresaDaoImpl;
import br.com.zalf.prolog.webservice.errorhandling.exception.AmazonCredentialsException;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ControleIntervaloDao;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ControleIntervaloDaoImpl;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.EstadoIntervaloSupport;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.IntervaloOfflineSupport;
import br.com.zalf.prolog.webservice.permissao.Visao;
import br.com.zalf.prolog.webservice.permissao.pilares.FuncaoProLog;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilar;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import br.com.zalf.prolog.webservice.seguranca.relato.RelatoDao;
import br.com.zalf.prolog.webservice.seguranca.relato.RelatoDaoImpl;
import com.google.common.base.Preconditions;
import com.sun.istack.internal.NotNull;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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
			stmt.setLong(1, colaborador.getCpf());
			if(colaborador.getMatriculaAmbev() == 0){
				stmt.setNull(2, Types.INTEGER);
			}else{
				stmt.setInt(2, colaborador.getMatriculaAmbev());
			}
			stmt.setInt(3, colaborador.getMatriculaTrans());
			stmt.setDate(4, DateUtils.toSqlDate(colaborador.getDataNascimento()));
			stmt.setDate(5, DateUtils.toSqlDate(colaborador.getDataAdmissao()));
			stmt.setNull(6, Types.DATE);
			stmt.setBoolean(7, colaborador.isAtivo());
			stmt.setString(8, colaborador.getNome());
			stmt.setLong(9, colaborador.getSetor().getCodigo());
			stmt.setLong(10, colaborador.getFuncao().getCodigo());
			stmt.setLong(11, colaborador.getCodUnidade());
			stmt.setLong(12, colaborador.getCodPermissao());
			stmt.setLong(13, colaborador.getCodEmpresa());
			stmt.setLong(14, colaborador.getEquipe().getCodigo());
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
				stmt.setDate(6, DateUtils.toSqlDate(new Date(System.currentTimeMillis())));
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
	public Colaborador getByCpf(Long cpf) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT C.CPF, C.MATRICULA_AMBEV, C.MATRICULA_TRANS, "
					+ "C.DATA_NASCIMENTO, C.DATA_ADMISSAO, C.DATA_DEMISSAO, C.STATUS_ATIVO, "
					+ "C.NOME AS NOME_COLABORADOR, EM.NOME AS NOME_EMPRESA, EM.CODIGO AS COD_EMPRESA, EM.LOGO_THUMBNAIL_URL, "
					+ "R.REGIAO AS NOME_REGIONAL, R.CODIGO AS COD_REGIONAL, U.NOME AS NOME_UNIDADE, U.CODIGO AS COD_UNIDADE, EQ.NOME AS NOME_EQUIPE, EQ.CODIGO AS COD_EQUIPE, "
					+ "S.NOME AS NOME_SETOR, S.CODIGO AS COD_SETOR, "
					+ "C.COD_FUNCAO, F.NOME AS NOME_FUNCAO, C.COD_PERMISSAO AS PERMISSAO "
					+ "FROM COLABORADOR C JOIN FUNCAO F ON C.COD_FUNCAO = F.CODIGO "
					+ " JOIN EQUIPE EQ ON EQ.CODIGO = C.COD_EQUIPE "
					+ " JOIN UNIDADE U ON U.CODIGO = C.COD_UNIDADE "
					+ " JOIN EMPRESA EM ON EM.CODIGO = C.COD_EMPRESA AND EM.CODIGO = U.COD_EMPRESA"
					+ " JOIN REGIONAL R ON R.CODIGO = U.COD_REGIONAL "
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

	@Override
	public Colaborador getByToken(@NotNull String token) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT C.CPF, C.MATRICULA_AMBEV, C.MATRICULA_TRANS, "
					+ "C.DATA_NASCIMENTO, C.DATA_ADMISSAO, C.DATA_DEMISSAO, C.STATUS_ATIVO, "
					+ "C.NOME AS NOME_COLABORADOR, EM.NOME AS NOME_EMPRESA, EM.CODIGO AS COD_EMPRESA, EM.LOGO_THUMBNAIL_URL, "
					+ "R.REGIAO AS NOME_REGIONAL, R.CODIGO AS COD_REGIONAL, U.NOME AS NOME_UNIDADE, U.CODIGO AS COD_UNIDADE, EQ.NOME AS NOME_EQUIPE, EQ.CODIGO AS COD_EQUIPE, "
					+ "S.NOME AS NOME_SETOR, S.CODIGO AS COD_SETOR, "
					+ "C.COD_FUNCAO, F.NOME AS NOME_FUNCAO, C.COD_PERMISSAO AS PERMISSAO "
					+ "FROM COLABORADOR C JOIN FUNCAO F ON C.COD_FUNCAO = F.CODIGO "
					+ " JOIN EQUIPE EQ ON EQ.CODIGO = C.COD_EQUIPE "
					+ " JOIN UNIDADE U ON U.CODIGO = C.COD_UNIDADE "
					+ " JOIN EMPRESA EM ON EM.CODIGO = C.COD_EMPRESA AND EM.CODIGO = U.COD_EMPRESA "
					+ " JOIN REGIONAL R ON R.CODIGO = U.COD_REGIONAL "
					+ " JOIN SETOR S ON S.CODIGO = C.COD_SETOR AND C.COD_UNIDADE = S.COD_UNIDADE "
					+ " JOIN TOKEN_AUTENTICACAO TA ON TA.TOKEN = ? AND TA.CPF_COLABORADOR = C.CPF "
					+ "WHERE C.STATUS_ATIVO = TRUE");
			stmt.setString(1, token);
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
			stmt = conn.prepareStatement("SELECT\n" +
					"  C.CPF,\n" +
					"  C.MATRICULA_AMBEV,\n" +
					"  C.MATRICULA_TRANS,\n" +
					"  C.DATA_NASCIMENTO,\n" +
					"  C.DATA_ADMISSAO,\n" +
					"  C.DATA_DEMISSAO,\n" +
					"  C.STATUS_ATIVO,\n" +
					"  initcap(C.NOME)          AS NOME_COLABORADOR,\n" +
					"  EM.NOME         AS NOME_EMPRESA,\n" +
					"  EM.CODIGO       AS COD_EMPRESA,\n" +
					"  EM.LOGO_THUMBNAIL_URL,\n" +
					"  R.REGIAO        AS NOME_REGIONAL,\n" +
					"  R.CODIGO        AS COD_REGIONAL,\n" +
					"  U.NOME          AS NOME_UNIDADE,\n" +
					"  U.CODIGO        AS COD_UNIDADE,\n" +
					"  EQ.NOME         AS NOME_EQUIPE,\n" +
					"  EQ.CODIGO       AS COD_EQUIPE,\n" +
					"  S.NOME          AS NOME_SETOR,\n" +
					"  S.CODIGO        AS COD_SETOR,\n" +
					"  C.COD_FUNCAO,\n" +
					"  F.NOME          AS NOME_FUNCAO,\n" +
					"  C.COD_PERMISSAO AS PERMISSAO\n" +
					"FROM COLABORADOR C\n" +
					"  JOIN FUNCAO F ON C.COD_FUNCAO = F.CODIGO\n" +
					"  JOIN EQUIPE EQ ON EQ.CODIGO = C.COD_EQUIPE\n" +
					"  JOIN UNIDADE U ON U.CODIGO = C.COD_UNIDADE\n" +
					"  JOIN EMPRESA EM ON EM.CODIGO = C.COD_EMPRESA AND EM.CODIGO = U.COD_EMPRESA\n" +
					"  JOIN REGIONAL R ON R.CODIGO = U.COD_REGIONAL\n" +
					"  JOIN SETOR S ON S.CODIGO = C.COD_SETOR AND C.COD_UNIDADE = S.COD_UNIDADE\n" +
					"WHERE C.COD_UNIDADE = ?\n" +
					"ORDER BY 8");
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
	public List<Colaborador> getMotoristasAndAjudantes(Long codUnidade) throws SQLException {
		List<Colaborador> list = new ArrayList<>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT\n" +
					"  C.CPF,\n" +
					"  C.MATRICULA_AMBEV,\n" +
					"  C.MATRICULA_TRANS,\n" +
					"  C.DATA_NASCIMENTO,\n" +
					"  C.DATA_ADMISSAO,\n" +
					"  C.DATA_DEMISSAO,\n" +
					"  C.STATUS_ATIVO,\n" +
					"  initcap(C.NOME) AS NOME_COLABORADOR,\n" +
					"  EM.NOME         AS NOME_EMPRESA,\n" +
					"  EM.CODIGO       AS COD_EMPRESA,\n" +
					"  EM.LOGO_THUMBNAIL_URL,\n" +
					"  R.REGIAO        AS NOME_REGIONAL,\n" +
					"  R.CODIGO        AS COD_REGIONAL,\n" +
					"  U.NOME          AS NOME_UNIDADE,\n" +
					"  U.CODIGO        AS COD_UNIDADE,\n" +
					"  EQ.NOME         AS NOME_EQUIPE,\n" +
					"  EQ.CODIGO       AS COD_EQUIPE,\n" +
					"  S.NOME          AS NOME_SETOR,\n" +
					"  S.CODIGO        AS COD_SETOR,\n" +
					"  C.COD_FUNCAO,\n" +
					"  F.NOME          AS NOME_FUNCAO,\n" +
					"  C.COD_PERMISSAO AS PERMISSAO\n" +
					"FROM COLABORADOR C\n" +
					"  JOIN FUNCAO F ON C.COD_FUNCAO = F.CODIGO\n" +
					"  JOIN EQUIPE EQ ON EQ.CODIGO = C.COD_EQUIPE\n" +
					"  JOIN UNIDADE U ON U.CODIGO = C.COD_UNIDADE\n" +
					"  JOIN EMPRESA EM ON EM.CODIGO = C.COD_EMPRESA AND EM.CODIGO = U.COD_EMPRESA\n" +
					"  JOIN REGIONAL R ON R.CODIGO = U.COD_REGIONAL\n" +
					"  JOIN SETOR S ON S.CODIGO = C.COD_SETOR AND C.COD_UNIDADE = S.COD_UNIDADE\n" +
					"  JOIN unidade_funcao_produtividade UFP ON UFP.cod_unidade = C.cod_unidade AND\n" +
					"                                           (C.cod_funcao = UFP.cod_funcao_ajudante OR\n" +
					"                                            C.COD_FUNCAO = UFP.cod_funcao_motorista)\n" +
					"WHERE C.COD_UNIDADE = ?\n" +
					"ORDER BY 8");
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
	public LoginHolder getLoginHolder(LoginRequest loginRequest) throws SQLException, AmazonCredentialsException {
		final LoginHolder loginHolder = new LoginHolder();
		loginHolder.setColaborador(getByCpf(loginRequest.getCpf()));

		if (verificaSeFazRelato(loginHolder.getColaborador().getVisao().getPilares())) {
			loginHolder.setAmazonCredentials(getAmazonCredentials());
			final RelatoDao relatoDao = new RelatoDaoImpl();
			loginHolder.setAlternativasRelato(relatoDao.getAlternativas(
					loginHolder.getColaborador().getCodUnidade(),
					loginHolder.getColaborador().getSetor().getCodigo()));
		}

		if (verificaSeFazGsd(loginHolder.getColaborador().getVisao().getPilares())) {
			loginHolder.setAmazonCredentials(getAmazonCredentials());
		}

		final Long codUnidade = getCodUnidadeByCpf(loginRequest.getCpf());
		final ControleIntervaloDao intervaloDao = new ControleIntervaloDaoImpl();
		final Long versaoDadosBanco = intervaloDao.getVersaoDadosIntervaloByUnidade(codUnidade);
		final Long versaoDadosApp = loginRequest.getVersaoDadosIntervalo();
		if (versaoDadosApp == null || versaoDadosApp < versaoDadosBanco) {
			final IntervaloOfflineSupport intervalo = new IntervaloOfflineSupport(EstadoIntervaloSupport.VERSAO_DESATUALIZADA);
			final Optional<List<Colaborador>> optional = getColaboradoresComAcessoFuncaoByUnidade(
					Pilares.Gente.Intervalo.MARCAR_INTERVALO,
					codUnidade);
			optional.ifPresent(intervalo::setColaboradores);
			intervalo.setTiposIntervalo(intervaloDao.getTiposIntervalos(codUnidade, false));
			intervalo.setVersaoDadosIntervalo(versaoDadosBanco);
			loginHolder.setIntervaloOfflineSupport(intervalo);
		} else if (versaoDadosApp.equals(versaoDadosBanco)) {
			// Se a versão está atualizada não precisamos setar mais nada no IntervaloOfflineSupport.
			loginHolder.setIntervaloOfflineSupport(new IntervaloOfflineSupport(EstadoIntervaloSupport.VERSAO_ATUALIZADA));
		} else {
			// Versão dados do app é depois da do banco, isso não deveria acontecer, como proceder?
			// TODO: ??
		}

		return loginHolder;
	}

	@Override
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

	@NotNull
	private Optional<List<Colaborador>> getColaboradoresComAcessoFuncaoByUnidade(final int codFuncaoProLog,
																				 @NotNull final Long codUnidade)
			throws SQLException {

		Preconditions.checkNotNull(codUnidade, "codUnidade não pode ser null!");

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT CPF, NOME, DATA_NASCIMENTO FROM COLABORADOR C JOIN " +
					"CARGO_FUNCAO_PROLOG_V11 CFP ON C.COD_UNIDADE = CFP.COD_UNIDADE " +
					"AND C.COD_FUNCAO = CFP.COD_FUNCAO_COLABORADOR WHERE C.COD_UNIDADE = ? AND COD_FUNCAO_PROLOG = ?;");
			stmt.setLong(1, codUnidade);
			stmt.setInt(2, codFuncaoProLog);
			rSet = stmt.executeQuery();

			if (!rSet.next()) {
				return Optional.empty();
			} else {
				final List<Colaborador> colaboradores = new ArrayList<>();
				do {
					final Colaborador colaborador = new Colaborador();
					colaborador.setCpf(rSet.getLong("CPF"));
					colaborador.setNome(rSet.getString("NOME"));
					colaborador.setDataNascimento(rSet.getDate("DATA_NASCIMENTO"));
					colaboradores.add(colaborador);
				} while (rSet.next());
				return Optional.of(colaboradores);
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
	}

	/**
	 * Esse método não lida com a possibilidade de o código unidade não existir ou de o CPF pelo qual você busca não
	 * estar cadastrado no banco. Tenha certeza de que o {@link Colaborador} do qual vocẽ está utilizando o CPF esteja
	 * cadastrado no banco.
	 *
	 * @param cpf Um CPF.
	 * @return O código da {@link Unidade}.
	 * @throws SQLException Caso aconteça algum erro na requisação ao banco.
	 */
	@NotNull
	private Long getCodUnidadeByCpf(@NotNull final Long cpf) throws SQLException {
		Preconditions.checkNotNull(cpf, "cpf não pode ser null!");

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT COD_UNIDADE FROM COLABORADOR C WHERE C.CPF = ?;");
			stmt.setLong(1, cpf);
			rSet = stmt.executeQuery();
			if (rSet.next()) {
                return rSet.getLong("COD_UNIDADE");
            }
		} finally {
			closeConnection(conn, stmt, rSet);
		}

		throw new IllegalStateException("Unidade não encontrada para o CPF: " + cpf);
	}

	private AmazonCredentials getAmazonCredentials() throws SQLException, AmazonCredentialsException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try{
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT * FROM AMAZON_CREDENTIALS");
			rSet = stmt.executeQuery();
			if(rSet.next()){
				AmazonCredentials amazonCredentials = new AmazonCredentials();
				amazonCredentials.setAccessKeyId(rSet.getString("ACCESS_KEY_ID"));
				amazonCredentials.setSecretAccessKey(rSet.getString("SECRET_KEY"));
				amazonCredentials.setUser(rSet.getString("USER_ID"));
				return amazonCredentials;
			}else{
				throw new AmazonCredentialsException();
			}
		}finally {
			closeConnection(conn, stmt, rSet);
		}
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
			stmt = conn.prepareStatement("SELECT DISTINCT PP.codigo AS COD_PILAR, PP.pilar, FP.codigo AS COD_FUNCAO, FP.funcao FROM cargo_funcao_prolog_v11 CF\n" +
					"JOIN PILAR_PROLOG PP ON PP.codigo = CF.cod_pilar_prolog\n" +
					"JOIN FUNCAO_PROLOG_v11 FP ON FP.cod_pilar = PP.codigo AND FP.codigo = CF.cod_funcao_prolog\n" +
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

	private Cargo createFuncao(ResultSet rSet) throws SQLException {
		Cargo f = new Cargo();
		f.setCodigo(rSet.getLong("CODIGO"));
		f.setNome(rSet.getString("NOME"));
		return f;
	}

	private Colaborador createColaborador(ResultSet rSet) throws SQLException {
		Colaborador c = new Colaborador();
		c.setAtivo(rSet.getBoolean("STATUS_ATIVO"));

		Cargo cargo = new Cargo();
		cargo.setCodigo(rSet.getLong("COD_FUNCAO"));
		cargo.setNome(rSet.getString("NOME_FUNCAO"));
		c.setFuncao(cargo);

		Empresa empresa = new Empresa();
		empresa.setCodigo(rSet.getInt("COD_EMPRESA"));
		empresa.setNome(rSet.getString("NOME_EMPRESA"));
		empresa.setLogoThumbnailUrl(rSet.getString("LOGO_THUMBNAIL_URL"));
		c.setEmpresa(empresa);

		Regional regional = new Regional();
		regional.setCodigo(rSet.getLong("COD_REGIONAL"));
		regional.setNome(rSet.getString("NOME_REGIONAL"));
		c.setRegional(regional);

		Unidade unidade = new Unidade();
		unidade.setCodigo(rSet.getLong("COD_UNIDADE"));
		unidade.setNome(rSet.getString("NOME_UNIDADE"));
		c.setUnidade(unidade);

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

	private boolean verificaSeFazRelato(List<Pilar> pilares) {
		for (Pilar pilar : pilares) {
			if (pilar.codigo == Pilares.SEGURANCA) {
				for (FuncaoProLog funcao : pilar.funcoes) {
					if (funcao.getCodigo() == Pilares.Seguranca.Relato.REALIZAR) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean verificaSeFazGsd(List<Pilar> pilares) {
		for (Pilar pilar : pilares) {
			if (pilar.codigo == Pilares.SEGURANCA) {
				for (FuncaoProLog funcao : pilar.funcoes) {
					if (funcao.getCodigo() == Pilares.Seguranca.GSD) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Deprecated
	private boolean verificaSeMarcaIntervalo(List<Pilar> pilares){
		for (Pilar pilar : pilares) {
			if (pilar.codigo == Pilares.GENTE) {
				for (FuncaoProLog funcao : pilar.funcoes) {
					if (funcao.getCodigo() == Pilares.Gente.Intervalo.MARCAR_INTERVALO) {
						return true;
					}
				}
			}
		}
		return false;
	}
}