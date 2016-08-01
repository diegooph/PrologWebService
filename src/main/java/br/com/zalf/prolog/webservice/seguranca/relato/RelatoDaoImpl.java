package br.com.zalf.prolog.webservice.seguranca.relato;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import br.com.zalf.prolog.models.Alternativa;
import br.com.zalf.prolog.models.Colaborador;
import br.com.zalf.prolog.models.Relato;
import br.com.zalf.prolog.models.gsd.Pdv;
import br.com.zalf.prolog.models.util.DateUtils;
import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.util.L;

public class RelatoDaoImpl extends DatabaseConnection {

	private static final String TAG = RelatoDaoImpl.class.getSimpleName();


	//	@Override
	public boolean insert(Relato relato) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("INSERT INTO RELATO "
					+ "(DATA_HORA_LOCAL,  DATA_HORA_DATABASE, LATITUDE, LONGITUDE, "
					+ "URL_FOTO_1, URL_FOTO_2, URL_FOTO_3, CPF_COLABORADOR, STATUS, COD_UNIDADE, "
					+ " COD_SETOR, COD_ALTERNATIVA, RESPOSTA_OUTROS, COD_PDV) "
					+ "VALUES (?,?,?,?,?,?,?,?,?,(SELECT COD_UNIDADE FROM COLABORADOR WHERE CPF = ?),"
					+ "(SELECT COD_SETOR FROM COLABORADOR WHERE CPF = ?),?,?,?)");
			stmt.setTimestamp(1, DateUtils.toTimestamp(relato.getDataLocal()));
			stmt.setTimestamp(2, DateUtils.toTimestamp(new Date(System.currentTimeMillis())));
			stmt.setString(3, relato.getLatitude());
			stmt.setString(4, relato.getLongitude());
			stmt.setString(5, relato.getUrlFoto1());
			stmt.setString(6, relato.getUrlFoto2());
			stmt.setString(7, relato.getUrlFoto3());
			stmt.setLong(8, relato.getColaboradorRelato().getCpf());
			stmt.setString(9, Relato.PENDENTE_CLASSIFICACAO);
			stmt.setLong(10, relato.getColaboradorRelato().getCpf());
			stmt.setLong(11, relato.getColaboradorRelato().getCpf());
			stmt.setLong(12, relato.getAlternativa().codigo);

			if(relato.getAlternativa().tipo ==  Alternativa.TIPO_OUTROS){
				stmt.setString(13, relato.getAlternativa().respostaOutros);
			}else{
				stmt.setNull(13, java.sql.Types.VARCHAR);
			}
			if (relato.getPdv() != null) {
				stmt.setInt(14, relato.getPdv().getCodigo());
			}
			int count = stmt.executeUpdate();
			if(count == 0){
				throw new SQLException("Erro ao inserir o relato");
			}	
		}
		finally {
			closeConnection(conn, stmt, null);
		}		
		return true;
	}

	//@Override
	public boolean classificaRelato(Relato relato) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("UPDATE RELATO SET CPF_CLASSIFICACAO = ?, "
					+ " DATA_HORA_CLASSIFICACAO = ?, STATUS = ?, COD_ALTERNATIVA = ?, RESPOSTA_OUTROS = ? "
					+ " WHERE CODIGO = ?");

			stmt.setLong(1, relato.getColaboradorClassificacao().getCpf());
			stmt.setTimestamp(2, DateUtils.toTimestamp(new Date(System.currentTimeMillis())));
			stmt.setString(3, relato.getStatus());
			stmt.setLong(4, relato.getAlternativa().codigo);
			if(relato.getAlternativa().tipo == Alternativa.TIPO_OUTROS){
				stmt.setString(5, relato.getAlternativa().respostaOutros);
			}else{
				stmt.setNull(5, java.sql.Types.VARCHAR);
			}
			stmt.setLong(6, relato.getCodigo());
			int count = stmt.executeUpdate();
			if(count == 0){
				throw new SQLException("Erro ao classificar o relato");
			}	
		}
		finally {
			closeConnection(conn, stmt, null);
		}		
		return true;
	}

	public boolean fechaRelato(Relato relato) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("UPDATE RELATO SET CPF_FECHAMENTO = ?, "
					+ " DATA_HORA_FECHAMENTO = ?, STATUS = ?, FEEDBACK_FECHAMENTO = ?  "
					+ " WHERE CODIGO = ?");

			stmt.setLong(1, relato.getColaboradorFechamento().getCpf());
			stmt.setTimestamp(2, DateUtils.toTimestamp(new Date(System.currentTimeMillis())));
			stmt.setString(3, Relato.FECHADO);
			stmt.setString(4, relato.getFeedbackFechamento());
			stmt.setLong(5, relato.getCodigo());
			int count = stmt.executeUpdate();
			if(count == 0){
				throw new SQLException("Erro ao fechar o relato");
			}	
		}
		finally {
			closeConnection(conn, stmt, null);
		}		
		return true;
	}

	//@Override
	public boolean delete(Long codRelato) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("DELETE FROM RELATO WHERE CODIGO = ?");
			stmt.setLong(1, codRelato);
			return (stmt.executeUpdate() > 0);
		} finally {
			closeConnection(conn, stmt, null);
		}
	}

	//	@Override
	public Relato getByCod(Long codRelato) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT *, C2.NOME AS NOME_CLASSIFICACAO, C3.NOME AS NOME_FECHAMENTO, NULL AS DISTANCIA "
					+ "FROM RELATO R JOIN "
					+ "COLABORADOR C ON R.CPF_COLABORADOR = C.CPF  LEFT JOIN "
					+ "COLABORADOR C2 ON R.CPF_CLASSIFICACAO = C2.CPF LEFT JOIN "
					+ "COLABORADOR C3 ON R.CPF_FECHAMENTO = C3.CPF JOIN "
					+ "RELATO_ALTERNATIVA RA ON RA.COD_SETOR = C.COD_SETOR AND RA.CODIGO = R.COD_ALTERNATIVA AND RA.COD_UNIDADE = R.COD_UNIDADE "
					+ "WHERE R.CODIGO = ?");
			stmt.setLong(1, codRelato);
			rSet = stmt.executeQuery();
			if (rSet.next()) {
				Relato relato = createRelato(rSet);
				return relato;
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return null;
	}

	//	@Override
	public List<Relato> getRealizadosByColaborador(Long cpf, int limit, long offset, double latitude, 
			double longitude, boolean isOrderByDate, String status, String campoFiltro) throws SQLException {
		List<Relato> relatos = new ArrayList<>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		String query = "SELECT *, C2.NOME AS NOME_CLASSIFICACAO, C3.NOME AS NOME_FECHAMENTO, "
				+ "ST_Distance(ST_Point(?, ?)::geography,ST_Point(longitude::real, latitude::real)::geography)/1000 as distancia "
				+ " FROM RELATO R JOIN "
				+ "COLABORADOR C ON R.CPF_COLABORADOR = C.CPF JOIN "
				+ "RELATO_ALTERNATIVA RA ON RA.COD_SETOR = C.COD_SETOR AND RA.CODIGO = R.COD_ALTERNATIVA AND RA.COD_UNIDADE = R.COD_UNIDADE LEFT JOIN "
				+ "COLABORADOR C2 ON R.CPF_CLASSIFICACAO = C2.CPF LEFT JOIN "
				+ "COLABORADOR C3 ON R.CPF_FECHAMENTO = C3.CPF "
				+ "WHERE %1s = ? AND R.STATUS LIKE ? "
				+ "ORDER BY %2s "
				+ "LIMIT ? OFFSET ? ";
		try {
			conn = getConnection();
			if(isOrderByDate){
				query = String.format(query, getCampoFiltro(campoFiltro), "DATA_HORA_DATABASE DESC");
			}else{
				query = String.format(query, getCampoFiltro(campoFiltro), "DISTANCIA ASC");
			}
			stmt = conn.prepareStatement(query);
			stmt.setDouble(1, longitude);
			stmt.setDouble(2, latitude);
			stmt.setLong(3, cpf);
			stmt.setString(4, status);
			stmt.setInt(5, limit);
			stmt.setLong(6, offset);
			L.d(TAG, stmt.toString());
			rSet = stmt.executeQuery();
			while (rSet.next()) {
				Relato relato = createRelato(rSet);
				relatos.add(relato);
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return relatos;
	}

	private String getCampoFiltro(String campoFiltro){
		String s = null;
		switch (campoFiltro) {
		case "realizados":
			s = "CPF_COLABORADOR";
			break;
		case "classificados":
			s = "CPF_CLASSIFICACAO";
			break;
		case "fechados":
			s = "CPF_FECHAMENTO";
			break;
		default:
			break;
		}
		return s;
	}

	//	@Override
	public List<Relato> getAllExcetoColaborador(Long cpf, int limit, long offset, double latitude, double longitude, boolean isOrderByDate, String status) throws SQLException {
		List<Relato> relatos = new ArrayList<>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		String query = "SELECT *, C2.NOME AS NOME_CLASSIFICACAO, C3.NOME AS NOME_FECHAMENTO, ST_Distance(ST_Point(?, ?)::geography,ST_Point(longitude::real, latitude::real)::geography)/1000 as distancia "
				+ " FROM RELATO R JOIN "
				+ "COLABORADOR C ON R.CPF_COLABORADOR = C.CPF JOIN "
				+ "RELATO_ALTERNATIVA RA ON RA.COD_SETOR = C.COD_SETOR AND RA.CODIGO = R.COD_ALTERNATIVA AND RA.COD_UNIDADE = R.COD_UNIDADE  LEFT JOIN "
				+ "COLABORADOR C2 ON R.CPF_CLASSIFICACAO = C2.CPF LEFT JOIN "
				+ "COLABORADOR C3 ON R.CPF_FECHAMENTO = C3.CPF "
				+ "WHERE R.CPF_COLABORADOR != ? AND R.STATUS LIKE ? AND R.COD_UNIDADE = c.cod_unidade "
				+ "ORDER BY %s "
				+ "LIMIT ? OFFSET ? ";
		try {

			conn = getConnection();
			if(isOrderByDate){
				query = String.format(query, "DATA_HORA_DATABASE DESC");
			}else{
				query = String.format(query, "DISTANCIA ASC");
			}
			stmt = conn.prepareStatement(query);
			stmt.setDouble(1, longitude);
			stmt.setDouble(2, latitude);
			stmt.setLong(3, cpf);
			stmt.setString(4, status);
			stmt.setInt(5, limit);
			stmt.setLong(6, offset);
			rSet = stmt.executeQuery();
			while (rSet.next()) {
				Relato relato = createRelato(rSet);
				relatos.add(relato);
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return relatos;
	}

	public List<Relato> getAll(Long codUnidade, int limit, long offset, double latitude, double longitude, boolean isOrderByDate, String status) throws SQLException {
		List<Relato> relatos = new ArrayList<>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		String query = "SELECT *, C2.NOME AS NOME_CLASSIFICACAO, C3.NOME AS NOME_FECHAMENTO, ST_Distance(ST_Point(?, ?)::geography,ST_Point(longitude::real, latitude::real)::geography)/1000 as distancia "
				+ " FROM RELATO R JOIN "
				+ "COLABORADOR C ON R.CPF_COLABORADOR = C.CPF JOIN "
				+ "RELATO_ALTERNATIVA RA ON RA.COD_SETOR = C.COD_SETOR AND RA.CODIGO = R.COD_ALTERNATIVA AND RA.COD_UNIDADE = R.COD_UNIDADE LEFT JOIN "
				+ "COLABORADOR C2 ON R.CPF_CLASSIFICACAO = C2.CPF LEFT JOIN "
				+ "COLABORADOR C3 ON R.CPF_FECHAMENTO = C3.CPF "
				+ "WHERE R.COD_UNIDADE = ? AND R.STATUS LIKE ? "
				+ "ORDER BY %s "
				+ "LIMIT ? OFFSET ? ";
		try {

			conn = getConnection();
			if(isOrderByDate){
				query = String.format(query, "DATA_HORA_DATABASE DESC");
			}else{
				query = String.format(query, "DISTANCIA ASC");
			}
			stmt = conn.prepareStatement(query);
			stmt.setDouble(1, longitude);
			stmt.setDouble(2, latitude);
			stmt.setLong(3, codUnidade);
			stmt.setString(4, status);
			stmt.setInt(5, limit);
			stmt.setLong(6, offset);
			rSet = stmt.executeQuery();
			while (rSet.next()) {
				Relato relato = createRelato(rSet);
				relatos.add(relato);
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return relatos;
	}

	//@Override
	public List<Relato> getAllByUnidade(LocalDate dataInicial, LocalDate dataFinal, String equipe,
			Long codUnidade,long limit, long offset, String status) throws SQLException{

		List<Relato> relatos = new ArrayList<>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;

		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT *, NULL AS DISTANCIA, C2.NOME AS NOME_CLASSIFICACAO, C3.NOME AS NOME_FECHAMENTO "
					+ " FROM RELATO R JOIN  "
					+ "COLABORADOR C ON R.CPF_COLABORADOR = C.CPF JOIN "
					+ "EQUIPE E ON E.CODIGO = C.COD_EQUIPE JOIN "
					+ "RELATO_ALTERNATIVA RA ON RA.COD_SETOR = C.COD_SETOR AND RA.CODIGO = R.COD_ALTERNATIVA AND RA.COD_UNIDADE = R.COD_UNIDADE  LEFT JOIN "
					+ "COLABORADOR C2 ON R.CPF_CLASSIFICACAO = C2.CPF LEFT JOIN "
					+ "COLABORADOR C3 ON R.CPF_FECHAMENTO = C3.CPF "
					+ "WHERE R.COD_UNIDADE = ? AND R.STATUS LIKE ? AND E.NOME LIKE ? AND R.DATA_HORA_DATABASE >= ? AND R.DATA_HORA_DATABASE <= ? "
					+ "ORDER BY DATA_HORA_DATABASE DESC "
					+ "LIMIT ? OFFSET ? ");

			stmt.setLong(1, codUnidade);
			stmt.setString(2, status);
			stmt.setString(3, equipe);
			stmt.setDate(4, DateUtils.toSqlDate(dataInicial));
			stmt.setDate(5, DateUtils.toSqlDate(dataFinal));
			stmt.setLong(6, limit);
			stmt.setLong(7, offset);
			rSet = stmt.executeQuery();
			while (rSet.next()) {
				Relato relato = createRelato(rSet);
				relatos.add(relato);
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return relatos;
	}

	private Relato createRelato(ResultSet rSet) throws SQLException{
		Relato relato = new Relato();
		relato.setCodigo(rSet.getLong("CODIGO"));
		// A hora que será mostrada no android deve ser a Data_Hora_Database
		relato.setDataLocal(rSet.getTimestamp("DATA_HORA_LOCAL"));
		relato.setDataDatabase(rSet.getTimestamp("DATA_HORA_DATABASE"));
		relato.setLatitude(rSet.getString("LATITUDE"));
		relato.setLongitude(rSet.getString("LONGITUDE"));
		relato.setUrlFoto1(rSet.getString("URL_FOTO_1"));
		relato.setUrlFoto2(rSet.getString("URL_FOTO_2"));
		relato.setUrlFoto3(rSet.getString("URL_FOTO_3"));
		relato.setColaboradorRelato(createColaborador(rSet.getString("NOME"), rSet.getLong("CPF_COLABORADOR")));
		relato.setColaboradorClassificacao(createColaborador(rSet.getString("NOME_CLASSIFICACAO"), rSet.getLong("CPF_CLASSIFICACAO")));
		relato.setColaboradorFechamento(createColaborador(rSet.getString("NOME_FECHAMENTO"), rSet.getLong("CPF_FECHAMENTO")));
		relato.setDataClassificacao(rSet.getTimestamp("DATA_HORA_CLASSIFICACAO"));
		relato.setDataFechamento(rSet.getTimestamp("DATA_HORA_FECHAMENTO"));
		relato.setFeedbackFechamento(rSet.getString("FEEDBACK_FECHAMENTO"));
		relato.setStatus(rSet.getString("STATUS"));
		Alternativa alternativa = createAlternativa(rSet);
		alternativa.respostaOutros = rSet.getString("RESPOSTA_OUTROS");
		relato.setAlternativa(alternativa);
		relato.setDistanciaColaborador(rSet.getDouble("DISTANCIA"));
		Pdv pdv = new Pdv();
		pdv.setCodigo(rSet.getInt("COD_PDV"));
		relato.setPdv(pdv);
		return relato;
	}

	private Colaborador createColaborador (String nome, Long cpf){
		Colaborador colaborador = new Colaborador();
		colaborador.setCpf(cpf);
		colaborador.setNome(nome);
		return colaborador;
	}

	private Alternativa createAlternativa(ResultSet rSet) throws SQLException{
		Alternativa alternativa = new Alternativa();
		alternativa.codigo = rSet.getLong("COD_ALTERNATIVA");
		alternativa.alternativa = rSet.getString("ALTERNATIVA");
		if(alternativa.alternativa.equals("Outros")){
			alternativa.tipo = alternativa.TIPO_OUTROS;

		}
		return alternativa;
	}

	/**
	 * Busca as alternativas para compor um relato, utilizado quando o usuário loga e quando cria um novo relato. 
	 * Mantém o banco de dados(mobile) atualizado.
	 * @param codUnidade 
	 * @param codSetor cod do setor do colaborador que está realizando o relato, serve para fitlrar as alternativas.
	 * @return lista de Alterniva
	 * @throws SQLException
	 */
	public List<Alternativa> getAlternativas(Long codUnidade, Long codSetor) throws SQLException{
		List<Alternativa> listAlternativas = new ArrayList<>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;

		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT CODIGO, ALTERNATIVA "
					+ "FROM RELATO_ALTERNATIVA "
					+ "WHERE COD_SETOR = ? OR COD_SETOR IS NULL AND COD_UNIDADE = ? AND STATUS_ATIVO = TRUE");
			stmt.setLong(1, codSetor);
			stmt.setLong(2, codUnidade);
			rSet = stmt.executeQuery();
			while (rSet.next()) {
				Alternativa alternativa = new Alternativa();
				alternativa.codigo = rSet.getLong("CODIGO");
				alternativa.alternativa = rSet.getString("ALTERNATIVA");
				if(alternativa.alternativa.equals("Outros")){
					alternativa.tipo = alternativa.TIPO_OUTROS;
				}
				listAlternativas.add(alternativa);
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return listAlternativas;
	}
}
